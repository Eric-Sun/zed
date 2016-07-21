package com.j13.zed.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by chengzhe on 16/6/1.
 */
public class ScrollControlViewPager extends ViewPager {

    private boolean isScrollEnabled = true;

    public ScrollControlViewPager(Context context) {
        super(context);
    }

    public ScrollControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isScrollEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isScrollEnabled && super.onInterceptTouchEvent(event);
    }

    public void setScrollEnabled(boolean isScrollEnabled) {
        this.isScrollEnabled = isScrollEnabled;
    }
}
