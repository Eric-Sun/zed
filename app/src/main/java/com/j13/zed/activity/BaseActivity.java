package com.j13.zed.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.j13.zed.R;
import com.j13.zed.view.ImmersedSystemBar;
import com.j13.zed.view.dialog.AlertDialog;
import com.j13.zed.view.dialog.ProgressDialog;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends AppCompatActivity {

    protected ActionBar mActionBar;
    protected ImmersedSystemBar mSystemBar;
    protected ProgressDialog mProgressDialog;
    protected boolean mResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemBar();
        initContentView();
        initActionBar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initContentView() {
        int contentViewId = geContentViewId();
        boolean haveContent = contentViewId != 0;
        boolean withToolbar = isDisplayWithToolbar();

        if (withToolbar) {
            setContentView(R.layout.activity_with_toolbar);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (haveContent) {
                FrameLayout containerView = (FrameLayout) findViewById(R.id.content_container);
                getLayoutInflater().inflate(contentViewId, containerView);
            }

            return;
        }

        if (haveContent) {
            setContentView(contentViewId);
        }
    }

    protected abstract int geContentViewId();

    protected boolean isDisplayWithToolbar() {
        return true;
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar == null) {
            return;
        }

//        mActionBar.setTitle(getTitle());
        mActionBar.setDisplayHomeAsUpEnabled(isHomeAsUpEnabled());
    }

    protected boolean isHomeAsUpEnabled() {
        return true;
    }

    private void initSystemBar() {
        if (isChangeStatusBar()) {
            mSystemBar = new ImmersedSystemBar(this);
            mSystemBar.setStateBarColor(getResources().getColor(R.color.colorAccent));
        }
    }
    protected boolean isChangeStatusBar() {
        return true;
    }

    public void showStatusBar() {
        if (mSystemBar != null) {
            mSystemBar.showSystemBar();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Show the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void hideStatusBar() {
        if (mSystemBar != null) {
            mSystemBar.hideSystemBar();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;

        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 封装打开Activity
     *
     * @param pClazz
     */
    public void openActivity(Class pClazz) {
        Intent it = new Intent(this, pClazz);
        startActivity(it);
    }

    /**
     * 封装Toast
     *
     * @param msg 内容
     */
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int id) {
        showToast(getString(id));
    }

    /**
     * 封装弹窗
     *
     * @param title
     * @param msg
     */
    public void showDialog(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg).show();
    }

    public void showProgressDialog(CharSequence title, CharSequence message) {
        dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(this, title, message);
        mProgressDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
