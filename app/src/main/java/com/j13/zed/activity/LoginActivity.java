package com.j13.zed.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.user.OauthLoginManager;
import com.j13.zed.user.ThirdPartyType;
import com.j13.zed.util.DebugLog;
import com.j13.zed.util.ToastManager;
import com.j13.zed.view.TextImgCenterBtn;
import com.j13.zed.view.dialog.ProgressDialog;
import com.tencent.mm.sdk.modelbase.BaseResp;
import de.greenrobot.event.EventBus;

/**
 * 登陆界面
 *
 * @author Melody 2016.05.31
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String ACTION = "action";
    public static final int ACTION_SEND_COMMENT = 1;
    public static final int ACTION_FOLLOW_TAG = 2;

    private LinearLayout mLoginContainerLayout;
    private LinearLayout mLoginLoadingLayout;

    /**
     * 微信登陆按钮
     */
    private TextImgCenterBtn mWechatLoginBtn;
    /**
     * 手机号登陆按钮
     */
    private TextImgCenterBtn mPhoneLoginBtn;
    /**
     * 小米账号登陆按钮
     */
    private TextView mXiaomiLoginTv;

    private ProgressDialog mWaitDialog;

    private String mTopicOrMine;


    @Override
    protected int geContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isDisplayWithToolbar() {
        return false;
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra(EXTRA_TOPIC, CLAIM_TAG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        EventBus.getDefault().register(this);

//        if (getIntent() != null) {
//            mTopicOrMine = getIntent().getStringExtra(TopicVideoActivity.EXTRA_TOPIC);
//        }

        findViewById();
        initView();
        setListener();

        if(mSystemBar != null){
            mSystemBar.hideSystemBar();
        }else {
            hideStatusBar();
        }
    }

    /**
     * 加载视图组件
     */
    public void findViewById() {
//        mWechatLoginBtn = (TextImgCenterBtn) findViewById(R.id.btn_wechat_login);
        mPhoneLoginBtn = (TextImgCenterBtn) findViewById(R.id.btn_phone_login);
        //mXiaomiLoginBtn = (TextImgCenterBtn) findViewById(R.id.btn_xiaomi_login);
        //mBrowsingAroundBtn = (TextView) findViewById(R.id.tv_browsing_around);
//        mXiaomiLoginTv = (TextView) findViewById(R.id.tv_xiaomi_login);

        mLoginContainerLayout = (LinearLayout) findViewById(R.id.ll_login_container);
        mLoginLoadingLayout = (LinearLayout) findViewById(R.id.ll_login_loading);
    }

    /**
     * 设置相关内容
     */
    public void initView() {
        //mXiaomiLoginBtn.setBtnContent(R.drawable.xm_icon, R.string.xiaomi_btn_str);
//        mWechatLoginBtn.setBtnContent(R.drawable.wechat_login, R.string.wechat_login);
        mPhoneLoginBtn.setBtnContent(R.drawable.phone_login, R.string.phone_login);

//        mWechatLoginBtn.setBackgroundResource(R.drawable.selector_login_wechat);
//        mPhoneLoginBtn.setBackgroundResource(R.drawable.selector_login_wechat);

//        mWechatLoginBtn.setTextColor(getResources().getColor(R.color.text_color_primary));
        mPhoneLoginBtn.setTextColor(getResources().getColor(R.color.text_color_primary));
        //如果从闪屏页过来，则显示随便看看按钮
//        if (SPLASH_TAG.equals(mTopicOrMine)) {
//            mBrowsingAroundBtn.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 设置监听器
     */
    public void setListener() {
//        //微信登陆监听器
//        mWechatLoginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OauthLoginManager.getInstance(LoginActivity.this).login(LoginActivity.this, ThirdPartyType.WECHAT, ThirdPartyType.LOGIN_OPERATION, "");
//                LoginClick loginClick = LoginClick.build("wechat");
//                Hubble.onEvent(LoginActivity.this, loginClick);
//                mLoginContainerLayout.setVisibility(View.GONE);
//            }
//        });
        //手机号登陆监听器
        mPhoneLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OauthLoginManager.getInstance(LoginActivity.this).login(LoginActivity.this, ThirdPartyType.PHONE, ThirdPartyType.LOGIN_OPERATION);
                mLoginContainerLayout.setVisibility(View.GONE);
            }
        });
//        //小米登陆监听器
//        mXiaomiLoginTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //showWaitDialog(getResources().getString(R.string.loading));
//                OauthLoginManager.getInstance(LoginActivity.this).login(LoginActivity.this, ThirdPartyType.XIAOMI, ThirdPartyType.LOGIN_OPERATION, "");
//                LoginClick loginClick = LoginClick.build("xiaomi");
//                Hubble.onEvent(LoginActivity.this, loginClick);
//                mLoginContainerLayout.setVisibility(View.GONE);
//            }
//        });
        //随便看看监听器
//        mBrowsingAroundBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openActivity(MainActivity.class);
//                finish();
//                Hubble.onEvent(LoginActivity.this, new LoginOtherLook());
//            }
//        });
    }

//    /**
//     * 登陆响应事件
//     *
//     * @param event
//     */
//    public void onEventMainThread(LoginResultEvent event) {
//        DebugLog.d(TAG, "onEventMainThread LoginResultEvent=" + event);
//
//        //hideWaitDialog();
//        mLoginLoadingLayout.setVisibility(View.GONE);
//        if (event.resultCode == LoginResultEvent.RESULT_ERROR || event.resultCode == OauthLoginManager.PHONE_CODE_ERR
//                || event.resultCode == OauthLoginManager.ACCOUNT_HAS_USED_ERR) {
//            if (!OauthLoginManager.PHONE_LOGIN_TYPE.equals(event.loginType)) {
//                ToastManager.show(this, R.string.login_failed_tip);
//                finish();
//            }
//            return;
//        }
//
//        ToastManager.show(this, R.string.login_success_tip);
//
//        if (getIntent().getIntExtra(ACTION, 0) == ACTION_SEND_COMMENT || getIntent().getIntExtra(ACTION, 0) == ACTION_FOLLOW_TAG) {
//            setResult(RESULT_OK);
//        }  else if (UPLOAD_TAG.equals(mTopicOrMine)) {
//            UploadActivity.startUploadActivity(this, mTopicOrMine);
//        }
//
////        if (MINE_TAG.equals(mTopicOrMine)) {
////            RegisterSuccessEvent registerSuccessEvent = new RegisterSuccessEvent();
////            EventBus.getDefault().post(registerSuccessEvent);
////        }else if (SPLASH_TAG.equals(mTopicOrMine)) {
////
////            openActivity(MainActivity.class);
////        } else if (!MINE_TAG.equals(mTopicOrMine)) {
////            UploadActivity.startUploadActivity(this, mTopicOrMine);
////        }
//        //新用户发送注册成功事件
//        if(event.isNewUser){
////        if(UserContext.getInstance(this).getLoginUser().isNewUser()){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    RegisterSuccessEvent registerSuccessEvent = new RegisterSuccessEvent();
//                    registerSuccessEvent.comeFrom = mTopicOrMine;
//                    EventBus.getDefault().post(registerSuccessEvent);
//                }
//            }, 300);
//        }
//        finish();
//    }



    @Override
    protected void onPause() {
        super.onPause();
        //hideWaitDialog();
        mLoginLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
//        if (SPLASH_TAG.equals(mTopicOrMine)) {
//            openActivity(MainActivity.class);
//            finish();
//        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (SPLASH_TAG.equals(mTopicOrMine)) {
            if(item.getItemId() == android.R.id.home) {
                openActivity(MainActivity.class);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/
}
