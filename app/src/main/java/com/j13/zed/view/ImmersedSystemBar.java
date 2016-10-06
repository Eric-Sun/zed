package com.j13.zed.view;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import com.j13.zed.R;

public class ImmersedSystemBar {

    private static final String TAG = "ImmersedSystemBar";

    private Activity activity;
    private boolean showActionBar = false;
    private View stateBarView;
    private View contentView;

    private final static String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    public ImmersedSystemBar(Activity activity) {
        this.activity = activity;
    }

    //获取状态栏高度
    private int getStatusBarHeight(Resources res) {
        int statusBarHeight = 0;
        int resourceId = res.getIdentifier(STATUS_BAR_HEIGHT_RES_NAME, "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void setShowActionBar(boolean show) {
        showActionBar = show;
    }

    public void showSystemBar() {
        if (contentView != null) {
            int actionBarHeight = 0;
            if (showActionBar) {
                actionBarHeight = getActionBarHeight(activity.getResources());
            }
            int top = getStatusBarHeight(activity.getResources()) + actionBarHeight;
            contentView.setPadding(0, top, 0, 0);
        }
        if (stateBarView != null) {
            stateBarView.setVisibility(View.VISIBLE);
        }
    }

    public void hideSystemBar() {
        if (contentView != null) {
            contentView.setPadding(0, 0, 0, 0);
        }
        if (stateBarView != null) {
            stateBarView.setVisibility(View.GONE);
        }
    }

    private int getActionBarHeight(Resources res) {
        TypedValue value = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);
        return TypedValue.complexToDimensionPixelSize(value.data, res.getDisplayMetrics());
    }

    //添加顶部状态栏
    private View addStateBar(Activity activity, ViewGroup rootView, int statusBarHeight) {
        //创建新的View,并添加到rootView顶部)
        View statusBarView = rootView.findViewById(R.id.status_bar_view);
        if (statusBarView == null) {
            statusBarView = new View(activity);
            statusBarView.setId(R.id.status_bar_view);
            rootView.addView(statusBarView);
            View contentView = rootView.findViewById(android.R.id.content);
            this.contentView = contentView;
            int actionBarHeight = 0;
            if (showActionBar) {
                actionBarHeight = getActionBarHeight(activity.getResources());
            }
            contentView.setPadding(0, statusBarHeight + actionBarHeight, 0, 0);
        }
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setVisibility(View.VISIBLE);
        this.stateBarView = statusBarView;
        return statusBarView;
    }

    /**
     * 设置状态栏颜色
     *
     * @param ColorId
     */
    public void setStateBarColor(int ColorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();
            //activity的顶级布局
            ViewGroup rootView = (ViewGroup) window.getDecorView();

            //透明化状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            Resources res = activity.getResources();
            //获取状态栏目的高度
            int statusBarHeight = getStatusBarHeight(res);

            View stateBarView = addStateBar(activity, rootView, statusBarHeight);
            stateBarView.setBackgroundColor(ColorId);
        }
    }
}
