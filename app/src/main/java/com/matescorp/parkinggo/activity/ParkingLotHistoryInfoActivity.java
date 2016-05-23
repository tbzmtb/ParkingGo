package com.matescorp.parkinggo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.adapter.ParkingLotCurentInfoListViewAdapter;
import com.matescorp.parkinggo.adapter.ParkingLotHistoryInfoListViewAdapter;
import com.matescorp.parkinggo.asynctask.GetSingleParkingLotDataTask;
import com.matescorp.parkinggo.data.ParkingInfoData;
import com.matescorp.parkinggo.data.ParkingLotInfoData;
import com.matescorp.parkinggo.data.ParkingServerData;
import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.DataPreference;
import com.matescorp.parkinggo.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tbzm on 16. 5. 12.
 */
public class ParkingLotHistoryInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getName();
    private ImageButton mBtnBack;
    private TextView mLotNameText;
    private TextView mLotBatteryText;
    private Button mBtnLotHistory;
    private ParkingLotInfoData mLotData;
    private ListView mLotInfoListView;
    private ParkingLotHistoryInfoListViewAdapter mListViewAdapter;
    private ArrayList<ParkingServerData> mParkingServerData = new ArrayList<>();
    private ArrayList<ParkingInfoData> mParkingInfoData = new ArrayList<>();
    private ParkingLotHandler mHandler;
    private int fee_per_minute = 100;
    private ParkingTimerHandler mTimerHandler;
    private IntentFilter mFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_lot_history);
        mLotData = getIntent().getExtras().getParcelable(Config.LOT_DATA_INTENT_KEY);
        mFilter = new IntentFilter(Config.GCM_SEND_KEY);
        DataPreference.PREF = PreferenceManager.getDefaultSharedPreferences(this);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mLotNameText = (TextView) findViewById(R.id.lot_name_text);
        mLotNameText.setText(mLotData.getLotName());
        mLotBatteryText = (TextView) findViewById(R.id.lot_battery_text);
        setBatterText(mLotData.getBattery());

        mBtnLotHistory = (Button) findViewById(R.id.btn_lot_history);
        mBtnLotHistory.setOnClickListener(this);
        mLotInfoListView = (ListView) findViewById(R.id.lot_info_list_view);
        mListViewAdapter = new ParkingLotHistoryInfoListViewAdapter(this, mParkingInfoData);
        mLotInfoListView.setAdapter(mListViewAdapter);
        mHandler = new ParkingLotHandler();
        mTimerHandler = new ParkingTimerHandler();
        mLotInfoListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void scrollMyListViewToBottom() {
        mLotInfoListView.post(new Runnable() {
            @Override
            public void run() {
                mLotInfoListView.setSelection(mListViewAdapter.getCount() - 1);
            }
        });

    }

    public void setBatterText(String gage) {
        if (gage != null) {
            int value = Integer.parseInt(gage);

            if (value < 20) {
                mLotBatteryText.setTextColor(Color.parseColor("#ff0000"));
            } else {
                mLotBatteryText.setTextColor(Color.parseColor("#828282"));
            }
            mLotBatteryText.setText(gage + "%");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
        new GetSingleParkingLotDataTask(ParkingLotHistoryInfoActivity.this, mHandler, mLotData.getSensorId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Config.GCM_SEND_KEY)) {
                new GetSingleParkingLotDataTask(ParkingLotHistoryInfoActivity.this, mHandler, mLotData.getSensorId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }
    };

    private class ParkingLotHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Config.PARKING_SINGLE_LOT_DATA_HANDLER) {
                JSONArray json;
                String result = msg.obj.toString();
                if (result == null) {
                    return;
                }
                result = result.trim();
                Log.i(TAG, "httpTask result =" + result);

                mParkingServerData.clear();
                if (result.trim().equals("") || result.trim().equals("[]") || result.trim().contains("null")) {
                    Log.i(TAG, "not data in server");
                } else {
                    try {
                        json = new JSONArray(result);
                        if (json.length() > 0) {
                            for (int i = 0; i < json.length(); i++) {
                                ParkingServerData data = new ParkingServerData();
                                data.setSensorId(json.getJSONObject(i).getString(Config.SENSOR_ID));
                                data.setParkingDate(json.getJSONObject(i).getString(Config.DATE_KEY));
                                data.setParkingState(json.getJSONObject(i).getString(Config.STATE_KEY));
                                data.setBattery(json.getJSONObject(i).getString(Config.BATTERY_KEY));
                                data.setGwid(json.getJSONObject(i).getString(Config.GWIDX_KEY));
                                mParkingServerData.add(data);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                setInfoData();
                setNotifyDataChanged();
                scrollMyListViewToBottom();
            }
        }
    }

    private void setNotifyDataChanged() {
        mListViewAdapter.notifyDataSetChanged();

    }

    private class ParkingTimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Config.PARKING_SINGLE_LOT_TIMER_HANDLER) {
                setInfoData();
                setNotifyDataChanged();
            }
        }
    }

    Runnable timer = new Runnable() {
        @Override
        public void run() {
            mTimerHandler.sendEmptyMessage(Config.PARKING_SINGLE_LOT_TIMER_HANDLER);
        }
    };

    public void setInfoData() {
        mParkingInfoData.clear();
        if (mParkingServerData.size() == 0) {
            return;
        }

        for (int i = 0; i < mParkingServerData.size(); i++) {
            ParkingInfoData infoData = new ParkingInfoData();


            if (mParkingServerData.get(i).getParkingState().equals(Config.PARKING_STATE_PARKED)) {
                infoData.setStartTitle(getResources().getString(R.string.parking_start));
                infoData.setStartDate(milliSecond2Date(mParkingServerData.get(i).getParkingDate()));
                infoData.setStartDay(milliSecond2Day(mParkingServerData.get(i).getParkingDate()));
                infoData.setStartTime(Config.milliSecond2Time(time2MilliSecond(mParkingServerData.get(i).getParkingDate())));

                int nextItem = i + 1;

                if (nextItem < mParkingServerData.size()) {
                    if (mParkingServerData.get(nextItem).getParkingState().equals(Config.PARKING_STATE_AVAILABLE_PARKING)) {
                        long endTime = time2MilliSecond(mParkingServerData.get(nextItem).getParkingDate());
                        infoData.setEndTitle(getResources().getString(R.string.parking_end));
                        infoData.setEndDate(milliSecond2Date(mParkingServerData.get(nextItem).getParkingDate()));
                        infoData.setEndDay(milliSecond2Day(mParkingServerData.get(nextItem).getParkingDate()));
                        infoData.setEndTime(Config.milliSecond2Time(time2MilliSecond(mParkingServerData.get(nextItem).getParkingDate())));

                        infoData.setTotalParkingTime(getTotalParkingTime(mParkingServerData.get(i).getParkingDate(), endTime));
                        infoData.setTotalParkingMinute(getTotalParkingMinute(mParkingServerData.get(i).getParkingDate(), endTime));
                        infoData.setTotalParkingSecond(getTotalParkingSecond(mParkingServerData.get(i).getParkingDate(), endTime));
                        infoData.setParkingCurrentFee(getCurrentFee(mParkingServerData.get(i).getParkingDate(), endTime));
                        mParkingInfoData.add(infoData);
                    }

                }

            }

        }
    }


    public synchronized long time2MilliSecond(String textTime) {
        Logger.d(TAG, "textTime = " + textTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(textTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        }
        return date.getTime();
    }

    public synchronized String milliSecond2Date(String time) {
        long milli = time2MilliSecond(time);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milli);
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;
        return Arrays.asList(getResources().getStringArray(R.array.day_of_week)).get(day);
    }

    public synchronized String milliSecond2Day(String time) {
        Logger.d(TAG, "milliSecond2Day call time ==" + time);
        long milli = time2MilliSecond(time);
        Logger.d(TAG, "milli ==" + milli);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milli);
        int month = c.get(Calendar.MONTH) + 1;
        if (month < 10) {
            return "0" + month + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        }
        return month + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
    }

    public synchronized String getCurrentFee(String startTime, long endTime) {
        long gab = getTimeGab(startTime, endTime);
        int hour = (int) (gab / 3600);
        int minutes = (int) (gab % 3600) / 60;
        int fee = (minutes * fee_per_minute) + ((hour * 60) * fee_per_minute);
        return String.valueOf(fee);
    }

    public synchronized String getTotalParkingTime(String startTime, long endTime) {
        long gab = getTimeGab(startTime, endTime);
        int hour = (int) (gab / 3600);
        return String.valueOf(hour);
    }

    public synchronized String getTotalParkingMinute(String startTime, long endTime) {
        long gab = getTimeGab(startTime, endTime);
        int minutes = (int) (gab % 3600) / 60;
        if (minutes < 10) {
            return "0" + minutes;
        }
        return String.valueOf(minutes);
    }

    public synchronized String getTotalParkingSecond(String startTime, long endTime) {
        long gab = getTimeGab(startTime, endTime);
        int second = (int) (gab % 60);
        if (second < 10) {
            return "0" + second;
        }
        return String.valueOf(second);
    }

    public long getTimeGab(String startTime, long endTime) {
        long startMilli = time2MilliSecond(startTime);
        startMilli = startMilli / 1000;
        long start = startMilli % 60;
        startMilli = startMilli - start;
        endTime = endTime / 1000;
        return endTime - startMilli;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_lot_history:
                Intent intent = new Intent(ParkingLotHistoryInfoActivity.this, ParkingLotHistoryInfoActivity.class);
                intent.putExtra(Config.LOT_DATA_INTENT_KEY, mLotData);
                startActivity(intent);
                break;
        }
    }
}