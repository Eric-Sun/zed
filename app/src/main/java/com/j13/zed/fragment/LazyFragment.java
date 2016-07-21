package com.j13.zed.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class LazyFragment extends Fragment {

    public static final String TAG = "LazyFragment";

    private boolean mIsPrepared = false;
    private boolean mIsResume = false;
    protected boolean mIsUserVisible = false;
    private boolean mIsFirstVisible = true;
    private boolean mIsFirstInvisible = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareVisible();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsResume = true;
        if (getUserVisibleHint()) {
            prepareVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsResume = false;
        if (getUserVisibleHint()) {
            invisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            prepareVisible();
        } else {
            invisible();
        }
    }

    public void prepareVisible() {
        if (mIsPrepared) {
            visible();
        } else {
            mIsPrepared = true;
        }
    }

    private void visible() {
        if (!mIsUserVisible && mIsResume) {
            mIsUserVisible = true;
            if (mIsFirstVisible) {
                onUserVisible(true);
                mIsFirstVisible = false;
            } else {
                onUserVisible(false);
            }
        }
    }

    private void invisible() {
        if (mIsUserVisible) {
            mIsUserVisible = false;
            if (mIsFirstInvisible) {
                onUserInvisible(true);
                mIsFirstInvisible = false;
            } else {
                onUserInvisible(false);
            }
        }
    }

    /**
     * fragment可见（切换回来或者onResume）
     */
    public void onUserVisible(boolean first) {

    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public void onUserInvisible(boolean first) {

    }
}
