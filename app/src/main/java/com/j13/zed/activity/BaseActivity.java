package com.j13.zed.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //5.0以上Actionbar去掉阴影
        if(Build.VERSION.SDK_INT>=21 && getSupportActionBar() != null){
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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

}
