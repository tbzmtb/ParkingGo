package com.matescorp.parkinggo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.asynctask.GetTotalParkingLotDataTask;

import com.matescorp.parkinggo.data.ParkingFloorInfoData;
import com.matescorp.parkinggo.data.ParkingLotInfoData;
import com.matescorp.parkinggo.data.ParkingServerData;
import com.matescorp.parkinggo.provider.ParkingProvider;
import com.matescorp.parkinggo.provider.ParkingSQLiteHelper;
import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;
import com.matescorp.parkinggo.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tbzm on 16. 5. 11.
 */
public class TotalHistoryActivity extends Activity implements View.OnClickListener {
    private final String TAG = getClass().getName();
    private ImageButton mBtnBack;
    private ListView mTotalList;
    private ListViewAdapter mAdapter;
    private TotalDataHandler mTotalDataHandler;
    private ArrayList<ParkingServerData> mFloorTotalDataList = new ArrayList<>();
    private ArrayList<ParkingFloorInfoData> mFloorInfoDataList = new ArrayList<>();
    private IntentFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "TotalHistoryActivity onCreate call");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.total_history_activity);

        mTotalDataHandler = new TotalDataHandler();
        mFilter = new IntentFilter(Config.GCM_SEND_KEY);
        DataPreference.PREF = PreferenceManager.getDefaultSharedPreferences(this);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mAdapter = new ListViewAdapter(TotalHistoryActivity.this, mFloorTotalDataList);
        mTotalList = (ListView) findViewById(R.id.history_list);
        mTotalList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mFloorInfoDataList.clear();
        mFloorInfoDataList = getIntent().getParcelableArrayListExtra(Config.LOT_TOTAL_DATA_INTENT_KEY);
        mTotalList.setAdapter(mAdapter);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private class ViewHolder {

        public TextView mTotal_date;
        public TextView mParking_message;
        public TextView lot_number;
        public TextView header_layout_text;
        public RelativeLayout mHeaderLayout;
        public ImageView date_line;

    }

    private void scrollMyListViewToBottom() {
        mTotalList.post(new Runnable() {
            @Override
            public void run() {
                mTotalList.setSelection(mAdapter.getCount() - 1);
            }
        });

    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        ArrayList<ParkingServerData> mFloorTotalDataList = new ArrayList<>();

        public ListViewAdapter(Context context, ArrayList<ParkingServerData> floorTotalDataList) {
            super();
            mContext = context;
            mFloorTotalDataList = floorTotalDataList;
        }

        @Override
        public int getCount() {
            return mFloorTotalDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFloorTotalDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.total_history_list_row, null);
                holder.mParking_message = (TextView) convertView.findViewById(R.id.mParking_message);
                holder.mTotal_date = (TextView) convertView.findViewById(R.id.mTotal_date);
                holder.lot_number = (TextView) convertView.findViewById(R.id.lot_number);
                holder.header_layout_text = (TextView) convertView.findViewById(R.id.header_layout_text);
                holder.mHeaderLayout = (RelativeLayout) convertView.findViewById(R.id.header_layout);
                holder.date_line = (ImageView) convertView.findViewById(R.id.date_line);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ParkingServerData data = mFloorTotalDataList.get(position);
            if (data.getParkingState().equals(Config.PARKING_STATE_PARKED)) {
                holder.mParking_message.setText(getString(R.string.in_the_lot));
            } else if (data.getParkingState().equals(Config.PARKING_STATE_AVAILABLE_PARKING)) {
                holder.mParking_message.setText(getString(R.string.out_of_the_lot));
//            } else if (data.getParkingState().equals(Config.PARKING_STATE_BOOKED)) {
//                holder.mParking_message.setText(getString(R.string.is_booked));
            } else {
                holder.mParking_message.setText(getString(R.string.check_is_needed));
            }

            holder.mTotal_date.setText(Config.milliSecond2Time(data.getParkingDate()));
            holder.lot_number.setText(data.getLotName());
            holder.header_layout_text.setText(Config.milliSecond2HeaderTime(data.getParkingDate()));
            int pre = position - 1;
            if (pre < 0) {
                holder.mHeaderLayout.setVisibility(View.VISIBLE);
            } else {
                ParkingServerData preData = (ParkingServerData) getItem(position - 1);
                if (changeDate(preData, data)) {
                    holder.mHeaderLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.mHeaderLayout.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Config.GCM_SEND_KEY)) {
                new GetTotalParkingLotDataTask(TotalHistoryActivity.this, mTotalDataHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                scrollMyListViewToBottom();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
        new GetTotalParkingLotDataTask(TotalHistoryActivity.this, mTotalDataHandler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public class TotalDataHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Config.PARKING_TOTAL_LOT_DATA_HANDLER) {
                JSONArray json;
                String result = msg.obj.toString();
                result = result.trim();
                Log.i(TAG, "result = " + result);
                mFloorTotalDataList.clear();
                if (result.trim().equals("") || result.trim().equals("[]") || result.trim().contains("null")) {
                    Log.i(TAG, "not data in server");
                } else {
                    try {
                        json = new JSONArray(result);
                        setTotalSensorData2Database(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                mFloorTotalDataList.addAll(getTotalSensorDataFromDatabase());
                mAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
        }

        private ArrayList<ParkingServerData> getTotalSensorDataFromDatabase() {
            ContentResolver resolver = getContentResolver();
            Cursor c = resolver.query(ParkingProvider.PARKING_TABLE_URI, new String[]{ParkingSQLiteHelper.COL_SENSOR_ID, ParkingSQLiteHelper.COL_GWIDX,
                            ParkingSQLiteHelper.COL_DATE, ParkingSQLiteHelper.COL_STATE, ParkingSQLiteHelper.COL_BATTERY, ParkingSQLiteHelper.COL_LOT_NAME}, null,
                    null, null);
            ArrayList<ParkingServerData> result = new ArrayList<>();
            if (c != null && c.moveToFirst()) {
                try {
                    do {
                        ParkingServerData data = new ParkingServerData();
                        data.setSensorId(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_SENSOR_ID)));
                        data.setGwid(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_GWIDX)));
                        data.setParkingDate(time2MilliSecond(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_DATE))));
                        data.setParkingState(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_STATE)));
                        data.setBattery(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_BATTERY)));
                        data.setLotName(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_LOT_NAME)));
                        result.add(data);

                    } while (c.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    c.close();
                }
            }
            return result;
        }

        private void setTotalSensorData2Database(JSONArray json) {
            ContentResolver resolver = getContentResolver();
            for (int j = 0; j < json.length(); j++) {
                try {
                    String date = json.getJSONObject(j).getString(Config.DATE_KEY);
                    Cursor c = resolver.query(ParkingProvider.PARKING_TABLE_URI, new String[]{ParkingSQLiteHelper.COL_DATE}, ParkingSQLiteHelper.COL_DATE + " =? ",
                            new String[]{date}, null);
                    ArrayList<String> result = new ArrayList<>();
                    if (c != null && c.moveToFirst()) {
                        try {
                            do {
                                result.add(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_DATE)));
                            } while (c.moveToNext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            c.close();

                        }
                    }
                    if (result.size() <= 0) {
                        ContentValues values = new ContentValues();
                        values.put(ParkingSQLiteHelper.COL_SENSOR_ID, json.getJSONObject(j).getString(Config.SENSOR_ID));
                        values.put(ParkingSQLiteHelper.COL_GWIDX, json.getJSONObject(j).getString(Config.GWIDX_KEY));
                        values.put(ParkingSQLiteHelper.COL_DATE, json.getJSONObject(j).getString(Config.DATE_KEY));
                        values.put(ParkingSQLiteHelper.COL_STATE, json.getJSONObject(j).getString(Config.STATE_KEY));
                        values.put(ParkingSQLiteHelper.COL_BATTERY, json.getJSONObject(j).getString(Config.BATTERY_KEY));
                        ParkingLotInfoData lot = getLotInfoData(json.getJSONObject(j).getString(Config.SENSOR_ID));
                        if (lot != null) {
                            values.put(ParkingSQLiteHelper.COL_LOT_NAME, lot.getLotName());
                        } else {
                            values.put(ParkingSQLiteHelper.COL_LOT_NAME, "");
                        }
                        Uri uri = resolver.insert(ParkingProvider.PARKING_TABLE_URI, values);
                        Logger.d(TAG, "uri insert success = " + json.getJSONObject(j).getString(Config.DATE_KEY));
                    } else {
                        Logger.d(TAG, "uri aready exist = " + json.getJSONObject(j).getString(Config.DATE_KEY));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private ArrayList<String> getTotalSensorDateFromDatabse() {
            Logger.d(TAG, "setTotalSensorData2Databse call ");
            ContentResolver resolver = getContentResolver();
            Cursor c = resolver.query(ParkingProvider.PARKING_TABLE_URI, new String[]{ParkingSQLiteHelper.COL_DATE}, null,
                    null, null);
            ArrayList<String> date = new ArrayList<String>();
            if (c != null && c.moveToFirst()) {
                try {
                    do {
                        date.add(c.getString(c.getColumnIndex(ParkingSQLiteHelper.COL_DATE)));
                    } while (c.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    c.close();

                }
            }
            return date;
        }

        public ParkingLotInfoData getLotInfoData(String sensorId) {
            if (MainActivity.mFloorInfoDataList != null) {
                for (int i = 0; i < MainActivity.mFloorInfoDataList.size(); i++) {
                    ArrayList<ParkingLotInfoData> lotData = MainActivity.mFloorInfoDataList.get(i).getLotDataObject();
                    for (int j = 0; j < lotData.size(); j++) {
                        if (lotData.get(j).getSensorId() != null) {
                            if (lotData.get(j).getSensorId().equals(sensorId)) {
                                return lotData.get(j);
                            }
                        }

                    }

                }
            }
            return null;
        }

        public String time2MilliSecond(String time) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = formatter.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date == null) {
                return null;
            }
            return String.valueOf(date.getTime());
        }
    }

    public boolean changeDate(ParkingServerData preData, ParkingServerData curData) {
        if (preData == null || curData == null) {
            return false;
        }
        String preD = preData.getParkingDate();
        String curD = curData.getParkingDate();
        String preString = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(preD)));
        String curString = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(curD)));

        if (preString.equals(curString)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }
}
