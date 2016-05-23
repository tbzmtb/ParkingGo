package com.matescorp.parkinggo.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.matescorp.parkinggo.R;
import com.matescorp.parkinggo.activity.MainActivity;
import com.matescorp.parkinggo.adapter.ParkingGridViewAdapter;
import com.matescorp.parkinggo.data.ParkingLotInfoData;
import com.matescorp.parkinggo.data.ParkingFloorInfoData;
import com.matescorp.parkinggo.util.Config;

import java.util.ArrayList;

public class LotViewFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String LOT_DATA_KEY = "lotData_key";
    private static final String TITLE_DATA_ARRAY = "title_data_array";
    private final String TAG = getClass().getName();
    private ArrayList<ParkingFloorInfoData> mFloorData;
    private ArrayList<String> mTitles;
    private int position;
    private ParkingGridViewAdapter mLotAdapter;
    private TextView mTotalParkingNum;
    private TextView mParkedNum;
    private TextView mAvailableParkingNum;
    private TextView mCheckParkingNum;
    private TextView mBookedParkingNum;
    public static LotViewFragment newInstance(int position, ArrayList<String> titles, ArrayList<ParkingFloorInfoData> lotData) {
        LotViewFragment f = new LotViewFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putStringArrayList(TITLE_DATA_ARRAY, titles);
        b.putParcelableArrayList(LOT_DATA_KEY, lotData);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        position = getArguments().getInt(ARG_POSITION);
        if (MainActivity.INSTANCE.mFragments.containsKey(position)) {
            MainActivity.INSTANCE.mFragments.remove(position);
        }
        MainActivity.INSTANCE.mFragments.put(position, this);
        mFloorData = getArguments().getParcelableArrayList(LOT_DATA_KEY);
        mTitles = getArguments().getStringArrayList(TITLE_DATA_ARRAY);
        View rootView = inflater.inflate(R.layout.page, container, false);
        mFloorData = getArguments().getParcelableArrayList(LOT_DATA_KEY);
        String title = mTitles.get(position);
        Log.d(TAG, "title == " + title);
        ParkingFloorInfoData floorData = mFloorData.get(position);
        setParkingLot(rootView, floorData);
        return rootView;
    }

    private void setParkingLot(View parent, ParkingFloorInfoData data) {
        GridView gridView = (GridView) parent.findViewById(R.id.grid_view);
        mTotalParkingNum = (TextView) parent.findViewById(R.id.total_parking_num);
        mParkedNum = (TextView) parent.findViewById(R.id.parked_num);
        mAvailableParkingNum = (TextView) parent.findViewById(R.id.available_parking_num);
        mCheckParkingNum = (TextView)parent.findViewById(R.id.need_check_parking_num);
        mBookedParkingNum = (TextView)parent.findViewById(R.id.booked_parking_num);
        int numberOfColumns = Integer.parseInt(data.getMaxPositionX());
        int sizeOfWidthPerColumn = (int) getActivity().getResources().getDimension(R.dimen.parking_lot_width);
        int sizeOfWidthSpacing = (int) getActivity().getResources().getDimension(R.dimen.grid_view_horizontal_spacing);
        int sizeOfWidthPadding = (int) getActivity().getResources().getDimension(R.dimen.grid_view_padding);
        gridView.setNumColumns(numberOfColumns);
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.width = numberOfColumns * sizeOfWidthPerColumn + numberOfColumns - 1 * sizeOfWidthSpacing + sizeOfWidthPadding * 2;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        gridView.setLayoutParams(layoutParams);
        mLotAdapter = new ParkingGridViewAdapter(getActivity(), data);
        gridView.setAdapter(mLotAdapter);
    }

    public String getTotalParkingNum(ParkingFloorInfoData data) {
        if (data == null) {
            return "";
        }
        int count = 0;
        for (int i = 0; i < data.getLotDataObject().size(); i++) {
            if (data.getLotDataObject().get(i).getSensorId() != null) {
                count++;
            }
        }
        return String.valueOf(count);
    }

    public void setTotalParkingNum(ParkingFloorInfoData data) {
        if (mTotalParkingNum != null) {
            mTotalParkingNum.setText(getTotalParkingNum(data));
        }
    }

    public String getParkedNum(ParkingFloorInfoData data) {
        if (data == null) {
            return "";
        }
        if (data.getLotDataObject() == null) {
            return "";
        }
        int count = 0;
        for (int i = 0; i < data.getLotDataObject().size(); i++) {
            ParkingLotInfoData infoData = data.getLotDataObject().get(i);
            if (infoData.getParkingState() != null) {
                if (infoData.getParkingState().equals(Config.PARKING_STATE_PARKED)) {
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }


    public void setParkedNum(ParkingFloorInfoData data) {
        if (mParkedNum != null) {
            mParkedNum.setText(getParkedNum(data));
        }
    }

    public String getAvailableParkingNum(ParkingFloorInfoData data){
        if (data == null) {
            return "";
        }
        if (data.getLotDataObject() == null) {
            return "";
        }
        int count = 0;
        for (int i = 0; i < data.getLotDataObject().size(); i++) {
            ParkingLotInfoData infoData = data.getLotDataObject().get(i);
            if (infoData.getParkingState() != null) {
                if (infoData.getParkingState().equals(Config.PARKING_STATE_AVAILABLE_PARKING)) {
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

    public void setAvailableParkingNum(ParkingFloorInfoData data){
        if (mAvailableParkingNum != null) {
            mAvailableParkingNum.setText(getAvailableParkingNum(data));
        }
    }

    public String getCheckParkingNum(ParkingFloorInfoData data){
        if (data == null) {
            return "";
        }
        if (data.getLotDataObject() == null) {
            return "";
        }
        int count = 0;
        for (int i = 0; i < data.getLotDataObject().size(); i++) {
            ParkingLotInfoData infoData = data.getLotDataObject().get(i);
            if (infoData.getParkingState() != null) {
                if (infoData.getParkingState().equals(Config.PARKING_STATE_OFF)) {
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

    public void setCheckParkingNum(ParkingFloorInfoData data){
        if (mCheckParkingNum != null) {
            mCheckParkingNum.setText(getCheckParkingNum(data));
        }
    }

    public String getBookParkingNum(ParkingFloorInfoData data){
        if (data == null) {
            return "";
        }
        if (data.getLotDataObject() == null) {
            return "";
        }
        int count = 0;
        for (int i = 0; i < data.getLotDataObject().size(); i++) {
            ParkingLotInfoData infoData = data.getLotDataObject().get(i);
            if (infoData.getParkingState() != null) {
                if (infoData.getParkingState().equals(Config.PARKING_STATE_BOOKED)) {
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

    public void setBookedParkingNum(ParkingFloorInfoData data){
        if (mBookedParkingNum != null) {
            mBookedParkingNum.setText(getBookParkingNum(data));
        }
    }


    public void notifyDataChange2Adapter(ParkingFloorInfoData data) {
        if (mLotAdapter != null) {
            mLotAdapter.notifyDataSetChanged();
            setTotalParkingNum(data);
            setParkedNum(data);
            setAvailableParkingNum(data);
            setCheckParkingNum(data);
            setBookedParkingNum(data);
        } else {
            Log.d(TAG, "mLotAdapter == null");
        }
    }
}