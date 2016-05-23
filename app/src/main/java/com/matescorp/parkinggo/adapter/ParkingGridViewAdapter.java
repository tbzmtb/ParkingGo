package com.matescorp.parkinggo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.activity.ParkingLotCurrentInfoActivity;
import com.matescorp.parkinggo.data.ParkingFloorInfoData;
import com.matescorp.parkinggo.data.ParkingLotInfoData;
import com.matescorp.parkinggo.util.Config;
import com.matescorp.parkinggo.util.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tbzm on 16. 4. 28.
 */
public class ParkingGridViewAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = "ParkingGridViewAdapter";
    private LayoutInflater mInflater;
    private ArrayList<ParkingLotInfoData> mLotData;
    private ParkingFloorInfoData mFloorData;
    private Context mContext;
    private int WARNNING_BATTER_VALUE = 20;
    private int CHECK_OUT_TIME = 20000;

    public ParkingGridViewAdapter(Context context, ParkingFloorInfoData floorData) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        mFloorData = floorData;
        mLotData = floorData.getLotDataObject();
    }

    private static class ViewHolder {
        public Button mBtnLot;
        public TextView mBatteryWarnning;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.parking_lot, null);
            holder = new ViewHolder();
            holder.mBtnLot = (Button) convertView.findViewById(R.id.btn_lot);
            holder.mBtnLot.setOnClickListener(this);
            holder.mBatteryWarnning = (TextView) convertView.findViewById(R.id.battert_warning_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Logger.d(TAG, "getView call " + position);
        ParkingLotInfoData lotData = mLotData.get(position);
        holder.mBtnLot.setTag(lotData);
        if (lotData.getPositionX() == null) {
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
            holder.mBtnLot.setText(lotData.getLotName());
            if (lotData.getParkingState().equals(Config.PARKING_STATE_AVAILABLE_PARKING)) {
                int gab = getOutTimeGab(lotData.getParkingDate());
                if (gab < CHECK_OUT_TIME && gab > 0) {
                    setBlinkeView(convertView, gab);
                } else {
                    convertView.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            } else if (lotData.getParkingState().equals(Config.PARKING_STATE_PARKED)) {
                convertView.setBackgroundColor(Color.parseColor("#ff7f66"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ff0000"));
            }
            String battery = lotData.getBattery();
            if (battery != null) {
                if (Integer.parseInt(lotData.getBattery()) <= WARNNING_BATTER_VALUE) {
                    holder.mBatteryWarnning.setVisibility(View.VISIBLE);
                } else {
                    holder.mBatteryWarnning.setVisibility(View.GONE);
                }
            } else {
                holder.mBatteryWarnning.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private int getOutTimeGab(String date) {
        Logger.d(TAG, "getOutTimeGab call");
        long time = time2MilliSecond(date);
        long current = System.currentTimeMillis();
        Logger.d(TAG, "current = " + current + " time = " + time);
        // 아래 서버 시간이 실제보다 빠르므로 임의로 현재시간을 10초를 더해서 늦춰준다
        int gab = (int) (current + 1000 - time);
        Logger.d(TAG, "gab =" + gab);
        return gab;
    }

    private void setBlinkeView(View view, int gabTime) {
        Logger.d(TAG, "setBlinkeView call");
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        int count = (CHECK_OUT_TIME - gabTime) / 1000;

        anim.setRepeatCount(count);
        view.startAnimation(anim);
    }

    public synchronized long time2MilliSecond(String textTime) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lot:
                ParkingLotInfoData lotData = (ParkingLotInfoData) v.getTag();
                Intent intent = new Intent(mContext, ParkingLotCurrentInfoActivity.class);
                intent.putExtra(Config.LOT_DATA_INTENT_KEY, lotData);
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mLotData.get(position);
    }

    @Override
    public int getCount() {
        return Integer.parseInt(mFloorData.getMaxPositionX()) * Integer.parseInt(mFloorData.getMaxPositionY());
    }
}
