package com.j13.zed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.j13.zed.R;
import com.j13.zed.user.OauthLoginManager;
import com.j13.zed.user.ThirdPartyType;
import com.j13.zed.user.UserContext;
import com.j13.zed.user.event.LoginResultEvent;
import com.j13.zed.user.event.LoginSuccessEvent;
import com.j13.zed.util.AppUtils;
import com.j13.zed.util.Constants;
import com.j13.zed.util.NetworkUtils;
import com.j13.zed.util.ToastManager;
import com.j13.zed.view.dialog.ProgressDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by lz on 16/4/7.
 */
public class PhoneNumLoginActivity extends BaseActivity implements Constants {

    private static final String TAG = "PhoneNumLoginActivity";

    public static final String KEY_TYPE = "key_type";
    public static final int WAIT_NUM = 60;

    private EditText mPhoneNumEt;
    private EditText passwordEt;
    private Button mLoginBtn;
    private TextView mBindTipTv;
    private TextView mBindSkipTv;
    private ProgressDialog mWaitDialog;

    private String mPhoneNum;
    private String password;
    private HashMap<String, Integer> mPhoneNumRecord = new HashMap<String, Integer>();
    private HashMap<String, Integer> mPhoneCodeRecord = new HashMap<String, Integer>();

    private int mCountNum = WAIT_NUM;
    private boolean mIsSendingCode = false;
    private boolean mIsBind = false;
    private String mTopicOrMine;

    private String mBindPhone;

    @Override
    protected int geContentViewId() {
        return R.layout.activity_phone_num_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (intent != null && ThirdPartyType.BIND_OPERATION.equals(getIntent().getStringExtra(KEY_TYPE))) {
            mIsBind = true;
        }

//        if (intent != null) {
//            mTopicOrMine = intent.getStringExtra(TopicVideoActivity.EXTRA_TOPIC);
//            mBindPhone = intent.getStringExtra(BIND_SOURCE);
//        }

        initView();
        initData();
    }

    private void initView() {
        if (mIsBind) {
            setTitle(R.string.phone_bind_title);
        }

        mPhoneNumEt = (EditText) findViewById(R.id.phone_num_et);
        passwordEt = (EditText) findViewById(R.id.password);
        mLoginBtn = (Button) findViewById(R.id.phone_login_btn);
        setLoginBtnEnable(false);
        mBindTipTv = (TextView) findViewById(R.id.phone_bind_tip);
        mBindSkipTv = (TextView) findViewById(R.id.phone_bind_skip);

        if (mIsBind) {
            mBindTipTv.setVisibility(View.VISIBLE);
            mBindSkipTv.setVisibility(View.VISIBLE);
        } else {
            mBindTipTv.setVisibility(View.GONE);
            mBindSkipTv.setVisibility(View.GONE);
        }
        if (mBindPhone != null) {
            mBindSkipTv.setVisibility(View.GONE);
            mLoginBtn.setText(R.string.bind);
        }
        initListener();
    }

//    private void setCodeBtnEnable(boolean isEnable) {
//        if (isEnable) {
//            mGetCodeBtn.setEnabled(true);
//            mGetCodeBtn.setTextColor(getResources().getColor(R.color.text_color_primary));
//        } else {
//            mGetCodeBtn.setEnabled(false);
//            mGetCodeBtn.setTextColor(getResources().getColor(R.color.phone_code_btn_disable_text));
//        }
//    }

    private void setLoginBtnEnable(boolean isEnable) {
        if (isEnable) {
            mLoginBtn.setEnabled(true);
            mLoginBtn.setTextColor(getResources().getColor(R.color.text_color_primary));
        } else {
            mLoginBtn.setEnabled(false);
            mLoginBtn.setTextColor(getResources().getColor(R.color.phone_code_btn_disable_text));
        }
    }

    private void initListener() {
        mPhoneNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoneNum = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidPhone(mPhoneNum)) {
                    setLoginBtnEnable(true);
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnalyticsAgent.trackEvent(new LoginPhoneClickData("login"));
                if (!NetworkUtils.hasInternet(PhoneNumLoginActivity.this)) {
                    ToastManager.show(PhoneNumLoginActivity.this, R.string.user_no_net_video_tip);
                    return;
                }

                if (!isValidPhone(mPhoneNum)) {
                    ToastManager.show(PhoneNumLoginActivity.this, R.string.phone_code_err);
                    return;
                }


                showWaitDialog(getResources().getString(R.string.loading));
//                if (mIsBind) {
//                    OauthLoginManager.getInstance(PhoneNumLoginActivity.this).bindByPhone(mPhoneNum, mPhoneCode);
//                } else {
                OauthLoginManager.getInstance(PhoneNumLoginActivity.this).loginByPhone(mPhoneNum, password);
//                }

            }
        });

        mBindSkipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnalyticsAgent.trackEvent(new LoginPhoneClickData("skip"));
                long userId = UserContext.getInstance(PhoneNumLoginActivity.this).getLoginUid();
                finish();
            }
        });

    }

    private boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean isValidPhoneCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]{6}$");
        Matcher matcher = pattern.matcher(code);
        return matcher.matches();
    }

    private void initData() {

    }

    private void showWaitDialog(String msg) {
        if (!isFinishing()) {
            if (mWaitDialog == null) {
                mWaitDialog = AppUtils.getWaitDialog(this, msg);
                mWaitDialog.show();
            } else {
                mWaitDialog.setMessage(msg);
                mWaitDialog.show();
            }
        }
    }

    private void hideWaitDialog() {
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    public void onEventMainThread(LoginResultEvent event) {
        hideWaitDialog();
        if (event.resultCode != OauthLoginManager.RESULT_OK) {
            ToastManager.show(this, R.string.login_failed_tip);
            return;
        }

        ToastManager.show(this, R.string.login_success_tip);

        if (OauthLoginManager.PHONE_LOGIN_TYPE.equals(event.loginType)) {
            if (UserContext.getInstance(this).isLogin()) {
//                if (UserContext.getInstance(this).isExistOtherAccount()) {
                LoginSuccessEvent successEvent = new LoginSuccessEvent();
                EventBus.getDefault().post(successEvent);
                setResult(RESULT_OK);
                finish();
//                } else {
//                    Intent it = new Intent(this, LoginAccountBindOpenIdActivity.class);
//                    it.putExtra(TopicVideoActivity.EXTRA_TOPIC, mTopicOrMine);
//                    startActivity(it);
//                }
            }
        }

    }

//    public void onEventMainThread(BindPhoneResultEvent event) {
//        DebugLog.d(TAG, "onEventMainThread BindPhoneResultEvent=" + event);
//        BindEvent bindEvent = new BindEvent();
//        bindEvent.type = TYPE_BIND;
//        bindEvent.source = TYPE_PHONE;
//        if (event.resultCode == BindPhoneResultEvent.RESULT_ERROR) {
//            ToastManager.show(this, R.string.bind_phone_failed_tip);
//            bindEvent.result = false;
//            EventBus.getDefault().post(bindEvent);
//            hideWaitDialog();
//            return;
//        } else if (event.resultCode == OauthLoginManager.PHONE_CODE_ERR) {
//            ToastManager.show(this, R.string.phone_code_err);
//            bindEvent.result = false;
//            EventBus.getDefault().post(bindEvent);
//            hideWaitDialog();
//            return;
//        } else if (event.resultCode == OauthLoginManager.ACCOUNT_HAS_USED_ERR) {
//            ToastManager.show(this, R.string.phone_has_used_tip);
//            bindEvent.result = false;
//            EventBus.getDefault().post(bindEvent);
//            hideWaitDialog();
//            return;
//        }
//
//        if (event.resultCode != BindPhoneResultEvent.RESULT_OK) {
//            ToastManager.show(this, R.string.bind_phone_failed_tip);
//            bindEvent.result = false;
//            EventBus.getDefault().post(bindEvent);
//            hideWaitDialog();
//            return;
//        }
//
//        ToastManager.show(this, R.string.bind_phone_success_tip);
//        bindEvent.result = true;
//        bindEvent.data = event.phoneNum;
//        EventBus.getDefault().post(bindEvent);
//        hideWaitDialog();
//        finish();
//    }

//    public void onEventMainThread(CommonResultEvent event) {
//        DebugLog.d(TAG, "onEventMainThread CommonResultEvent=" + event);
//
//        if (event.resultCode == CommonResultEvent.RESULT_OK) {
////            setCodeBtnEnable(false);
////            mIsSendingCode = true;
////            mHandler.sendEmptyMessage(0);
//
//            Integer count = mPhoneNumRecord.get(mPhoneNum);
//            if (count == null) {
//                mPhoneNumRecord.put(mPhoneNum, 1);
//            } else {
//                mPhoneNumRecord.put(mPhoneNum, ++count);
//            }
//        } else {
//            ToastManager.show(this, R.string.get_verification_code_fail);
//            mIsSendingCode = false;
//            mCountNum = WAIT_NUM;
//            mGetCodeBtn.setText(getResources().getString(R.string.phone_send_code_again));
//            setCodeBtnEnable(true);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    /**
     * 若用户按返回则表明，用户取消登录
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        LoginResultEvent event = new LoginResultEvent();
//        event.resultCode = LoginResultEvent.USER_CANCEL;
//        EventBus.getDefault().post(event);
    }


}
