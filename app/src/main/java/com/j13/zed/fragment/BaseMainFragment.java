package com.j13.zed.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.util.DebugLog;

public abstract class BaseMainFragment extends Fragment implements ILazyFragment {

    private static final String LOG_TAG = "BaseMainFragment";

    protected boolean mIsUserVisible = false;

    protected boolean mIsHidden = false;
    protected boolean mIsResume = false;

    private boolean mIsFirstVisible = true;
    private boolean mIsFirstInvisible = true;

    protected View mRootView;

    @Override
    public void onResume() {
        super.onResume();
        mIsResume = true;

        if (!mIsHidden) {
            visible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsResume = false;

        invisible();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mIsHidden = hidden;

        if (!mIsHidden && mIsResume) {
            visible();
        } else if (mIsHidden) {
            invisible();
        }
    }

    private void visible() {
        if (!mIsUserVisible) {
            mIsUserVisible = true;

            onUserVisible(mIsFirstVisible);
            mIsFirstVisible = false;
        }
    }

    private void invisible() {
        if (mIsUserVisible) {
            mIsUserVisible = false;

            onUserInvisible(mIsFirstInvisible);
            mIsFirstInvisible = false;
        }
    }

    @Override
    public void onUserVisible(boolean first) {
        DebugLog.d(LOG_TAG, getClass().getSimpleName() + " onUserVisible " + (first ? " first" : ""));
    }

    @Override
    public void onUserInvisible(boolean first) {
        DebugLog.d(LOG_TAG, getClass().getSimpleName() + " onUserInvisible " + (first ? " first" : ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int contentViewId = geContentViewId();
        boolean haveContent = contentViewId != 0;
        boolean withToolbar = isDisplayWithToolbar();
        if (withToolbar) {
            mRootView = inflater.inflate(R.layout.fragment_with_toolbar, container, false);

            Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
            TextView titleView = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (getToolbarTitleId() > 0)
                titleView.setText(getToolbarTitleId());

            if (haveContent) {
                FrameLayout containerView = (FrameLayout) mRootView.findViewById(R.id.content_container);
                inflater.inflate(contentViewId, containerView);
            }
        } else if (haveContent) {
            mRootView = inflater.inflate(contentViewId, container, false);
        }
        setHasOptionsMenu(true);
        initView(inflater, mRootView, savedInstanceState);
        return mRootView;
    }

    protected abstract int geContentViewId();

    protected boolean isDisplayWithToolbar() {
        return true;
    }

    protected int getToolbarTitleId() {
        return 0;
    }

    protected abstract void initView(LayoutInflater inflater, View rootView, Bundle savedInstanceState);
}
