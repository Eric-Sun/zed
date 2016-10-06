package com.j13.zed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.util.Constants;
import com.j13.zed.util.NetworkUtils;

/**
 * Created by chengzhe on 16/7/15.
 */
public class UserDescEditActivity extends BaseActivity {

    private static final int MAX_NUM = 140;

    private String mDesc;
    private EditText mEditDesc;
    private TextView mNumView;

    @Override
    protected int geContentViewId() {
        return R.layout.activity_user_desc_edit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mDesc = intent.getStringExtra(Constants.EXTRA_USER_DESC);

        int num = TextUtils.isEmpty(mDesc) ? 0 : mDesc.length();
        mNumView = (TextView) findViewById(R.id.desc_num);
        mNumView.setText(getString(R.string.user_sign_limit, MAX_NUM - num));

        mEditDesc = (EditText) findViewById(R.id.edit_desc);
        mEditDesc.setText(mDesc);
        mEditDesc.selectAll();
        mEditDesc.requestFocus();

        mEditDesc.addTextChangedListener(new TextWatcher() {

            CharSequence text;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int restNum = MAX_NUM - s.length();
                mNumView.setText(getString(R.string.user_sign_limit, restNum));
            }
        });
    }

    private boolean checkDesc(String desc) {
        boolean result = true;
        if (desc.length() > 140 || TextUtils.equals(mDesc, desc)) {
            result = false;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        String desc = mEditDesc.getText().toString();
        if (!NetworkUtils.hasInternet(this)) {
            showToast(R.string.network_not_available);
        } else {
            if (checkDesc(desc)) {
//                UserInfoManager.getInstance(this).updateUserDesc(desc);
            }
        }
        super.onBackPressed();
    }
}
