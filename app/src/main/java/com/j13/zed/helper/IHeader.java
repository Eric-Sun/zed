package com.j13.zed.helper;

import android.app.Activity;
import android.view.View;

public interface IHeader {

    public void onCreate(Activity activity);

    public void onResume();

    public void onPause();

    public void onDestroy();

    public void onLoadData();

    public View getView();

}
