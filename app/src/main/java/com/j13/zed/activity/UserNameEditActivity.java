package com.j13.zed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.j13.zed.R;
import com.j13.zed.user.UserInfoManager;
import com.j13.zed.util.Constants;
import com.j13.zed.util.NetworkUtils;

/**
 * Created by chengzhe on 16/7/15.
 */
public class UserNameEditActivity extends BaseActivity {

    private String mName;
    private EditText mEditName;

    @Override
    protected int geContentViewId() {
        return R.layout.activity_user_name_edit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mName = intent.getStringExtra(Constants.EXTRA_USER_NAME);

        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditName.setText(mName);
        mEditName.selectAll();
        mEditName.requestFocus();
    }

    private boolean checkName(String name) {
        boolean result = true;
        if (TextUtils.isEmpty(name) || name.length() > 20 || name.length() < 4 || TextUtils.equals(mName, name)) {
            result = false;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        String name = mEditName.getText().toString();
        if (!NetworkUtils.hasInternet(this)) {
            showToast(R.string.network_not_available);
        } else {
            if (checkName(name)) {
//                UserInfoManager.getInstance(this).updateUserName(name);
            }
        }
        super.onBackPressed();
    }
}
