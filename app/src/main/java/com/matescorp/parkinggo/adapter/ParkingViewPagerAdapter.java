package com.matescorp.parkinggo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.matescorp.parkinggo.activity.MainActivity;
import com.matescorp.parkinggo.data.ParkingFloorInfoData;
import com.matescorp.parkinggo.util.Logger;
import com.matescorp.parkinggo.view.LotViewFragment;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;
import java.util.WeakHashMap;

public abstract class ParkingViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> titles;
    private ArrayList<ParkingFloorInfoData> lotData;

    private final String TAG = getClass().getName();

    public ParkingViewPagerAdapter(FragmentManager fm, ArrayList<String> titles, ArrayList<ParkingFloorInfoData> lotData) {
        super(fm);
        this.titles = titles;
        this.lotData = lotData;
    }

    @Override
    public Fragment getItem(int position) {
        return LotViewFragment.newInstance(position, titles, lotData);
    }

    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (Integer position : MainActivity.INSTANCE.mFragments.keySet()) {
            if (position != null && MainActivity.INSTANCE.mFragments.get(position) != null && position.intValue() < getCount()) {
                updateFragmentItem(position, MainActivity.INSTANCE.mFragments.get(position));
            } else {
                Logger.i(TAG, "notifyDataSetChanged error");
            }
        }

    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof Fragment) {
            int position = findFragmentPositionHashMap((Fragment) object);
            if (position >= 0) {
                return (position >= getCount() ? POSITION_NONE : position);
            }
        }

        return super.getItemPosition(object);
    }

    protected int findFragmentPositionHashMap(Fragment object) {
        for (Integer position : MainActivity.INSTANCE.mFragments.keySet()) {
            if (position != null &&
                    MainActivity.INSTANCE.mFragments.get(position) != null &&
                    MainActivity.INSTANCE.mFragments.get(position) == object) {
                return position;
            }
        }

        return -1;
    }

//    public abstract Fragment getFragmentItem(int position);

    public abstract void updateFragmentItem(int position, Fragment fragment);

}