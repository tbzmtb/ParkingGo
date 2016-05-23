package com.matescorp.parkinggo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.data.ParkingInfoData;

import java.util.ArrayList;

/**
 * Created by tbzm on 16. 5. 9.
 */
public class ParkingLotCurentInfoListViewAdapter extends BaseAdapter {

    private static final String TAG = "ParkingLotCurentInfoListViewAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ParkingInfoData> minfoData;

    public ParkingLotCurentInfoListViewAdapter(Context context, ArrayList<ParkingInfoData> infoData) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        minfoData = infoData;
    }

    private static class InfoViewHolder {
        public TextView mLeftTitleText;
        public TextView mRightTitleText;
        public TextView mLeftDateText;
        public TextView mRightDateText;
        public TextView mLeftDayText;
        public TextView mRightDayText;
        public TextView mLeftTimeText;
        public TextView mRightTimeText;
        public TextView mTotalParkingTime;
        public TextView mTotalParkingMinute;
        public TextView mTotalParkingSecond;
        public TextView mParkingFee;
        public TextView mParkingTotalFee;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_lot_current_info_row, null);
            holder = new InfoViewHolder();
            holder.mLeftTitleText = (TextView) convertView.findViewById(R.id.left_start_title_text);
            holder.mRightTitleText = (TextView) convertView.findViewById(R.id.right_start_title_text);
            holder.mLeftDateText = (TextView) convertView.findViewById(R.id.left_start_date_text);
            holder.mRightDateText = (TextView) convertView.findViewById(R.id.right_start_date_text);
            holder.mLeftDayText = (TextView) convertView.findViewById(R.id.left_start_day_text);
            holder.mRightDayText = (TextView) convertView.findViewById(R.id.right_start_day_text);
            holder.mLeftTimeText = (TextView) convertView.findViewById(R.id.left_start_time_text);
            holder.mRightTimeText = (TextView) convertView.findViewById(R.id.right_start_time_text);
            holder.mTotalParkingTime = (TextView) convertView.findViewById(R.id.parking_total_time);
            holder.mTotalParkingMinute = (TextView)convertView.findViewById(R.id.parking_total_minute);
            holder.mTotalParkingSecond = (TextView)convertView.findViewById(R.id.parking_total_second);
            holder.mParkingFee = (TextView) convertView.findViewById(R.id.parking_fee);
            holder.mParkingTotalFee = (TextView) convertView.findViewById(R.id.parking_total_fee);
            convertView.setTag(holder);
        } else {
            holder = (InfoViewHolder) convertView.getTag();
        }
        ParkingInfoData data = minfoData.get(position);
        holder.mLeftTitleText.setText(data.getStartTitle());
        holder.mRightTitleText.setText(data.getEndTitle());
        holder.mLeftDateText.setText(data.getStartDate());
        holder.mRightDateText.setText(data.getEndtDate());
        holder.mLeftDayText.setText(data.getStartDay());
        holder.mRightDayText.setText(data.getEndDay());
        holder.mLeftTimeText.setText(data.getStartTime());
        holder.mRightTimeText.setText(data.getEndTime());
        holder.mTotalParkingTime.setText(data.getTotalParkingTime());
        holder.mTotalParkingMinute.setText(data.getTotalParkingMinute());
        holder.mTotalParkingSecond.setText(data.getTotalParkingSecond());
        holder.mParkingFee.setText(data.getParkingCurrentFee());
        holder.mParkingTotalFee.setText(data.getParkingTotalFee());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return minfoData.get(position);
    }

    @Override
    public int getCount() {
        return minfoData.size();
    }
}
