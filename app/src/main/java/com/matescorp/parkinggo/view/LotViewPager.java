package com.matescorp.parkinggo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by tbzm on 16. 4. 26.
 */
public class LotViewPager extends ViewPager {
    private boolean swipeable = true;

    public LotViewPager(Context context) {
        super(context);
    }

    public LotViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return (this.swipeable) ? super.onInterceptTouchEvent(event) : false;
    }

}
