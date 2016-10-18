package com.j13.zed.user;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.j13.zed.activity.PhoneNumLoginActivity;
import com.j13.zed.api.InternetUtil;
import com.j13.zed.api.user.PhoneLoginRequest;
import com.j13.zed.api.user.PhoneLoginResponse;
import com.j13.zed.model.ShareHelper;
import com.j13.zed.user.event.LoginResultEvent;
import com.j13.zed.user.event.WxCodeEvent;
import com.j13.zed.user.event.XmUserInfoEvent;
import com.j13.zed.util.Constants;
import com.michael.corelib.coreutils.CustomThreadPool;
import com.michael.corelib.internet.core.NetWorkException;
import com.michael.corelib.internet.core.util.JsonUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by lz on 16/4/7.
 */
public class OauthLoginManager {
    private static final String TAG = OauthLoginManager.class.getSimpleName();
    private static final long XM_APPID = 2882303761517136438L;
    private static final String XM_REDIRECTURI = "http://www.kuaipan.cn";
    private static final int[] XM_SCOPE = new int[]{
            XiaomiOAuthConstants.SCOPE_PROFILE,
            XiaomiOAuthConstants.SCOPE_OPEN_ID
    };

    public static final int PHONE_CODE_ERR = 9006;
    public static final int ACCOUNT_HAS_USED_ERR = 9003;
    public static final int ACCOUNT_INVALID_ERR = 9011;


    public static final int PASSWORD_IS_NOT_RIGHT = 1001;
    public static final int RESULT_OK = 0;
    public static final String BUNDLE_KEY_OPENIDINFO = "bundle_key_openid_info";
    public static final String BUNDLE_KEY_TOKENINFO = "bundle_key_token_info";
    public static final String XM_LOGIN_TYPE = "xiaomi";
    public static final String XM_BIND_TYPE = "xiaomi";
    public static final String WX_LOGIN_TYPE = "weichat";
    public static final String WX_BIND_TYPE = "weichat";
    public static final String PHONE_LOGIN_TYPE = "phone";

    private static final String WB_BIND_TYPE = "weibo";
    private static final String WB_LOGIN_TYPE = "weibo";
    public static final String WB_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    private static final String QQ_SCOPE = "all";
    private static final String QQ_BIND_TYPE = "qq";
    private static final String QQ_LOGIN_TYPE = "qq";

    private Context mAppContext;
    private String mOperation;

    private volatile static OauthLoginManager instance;

    private OauthLoginManager(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public static OauthLoginManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OauthLoginManager.class) {
                if (instance == null) {
                    instance = new OauthLoginManager(context);
                }
            }
        }

        return instance;
    }

    public void login(Activity activity, int type, String operation) {
        switch (type) {
            case ThirdPartyType.WECHAT:
                wxLogin(activity, operation);
                break;
            case ThirdPartyType.PHONE:
                pnLogin(activity);
                break;
            default:
                break;
        }
    }

//    /**
//     * 获取小米登陆信息
//     * @param activity
//     * @param accessToken
//     * @param macKey
//     * @param macAlgorithm
//     * @param operation
//     */
//    public void getXmUserInfoAndReg(Activity activity, final String accessToken, String macKey, String macAlgorithm, final String operation) {
//        final XiaomiOAuthFuture<String> future = new XiaomiOAuthorize().callOpenApi(activity,
//                XM_APPID,
//                XiaomiOAuthConstants.OPEN_API_PATH_PROFILE,
//                accessToken,
//                macKey,
//                macAlgorithm);
//
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                Exception exception = null;
//                int resultEvent = XmUserInfoEvent.RESULT_ERROR;
//
//                try {
//                    String result = future.getResult();
//
//                    XmUserInfo userInfo = null;
//                    if (!TextUtils.isEmpty(result)) {
//                        userInfo = JsonUtils.parse(result, XmUserInfo.class);
//                        if (userInfo != null && userInfo.result != null && userInfo.result.toLowerCase().trim().equals("ok")) {
//                            String appId = String.valueOf(XM_APPID);
//                            String openId = userInfo.data != null ? String.valueOf(userInfo.data.openId) : "";
//                            if (ThirdPartyType.BIND_OPERATION.equals(operation)) {
//                                bindThirdParty(appId, openId, accessToken, XM_BIND_TYPE);
//                            } else {
//                                openIdLogin(appId, openId, accessToken, XM_LOGIN_TYPE);
//                            }
//                            resultEvent = XmUserInfoEvent.RESULT_OK;
//                        }
//                    }
//                } catch (IOException e1) {
//                    // error
//                    exception = e1;
//                } catch (OperationCanceledException e1) {
//                    // error
//                    exception = e1;
//                } catch (XMAuthericationException e1) {
//                    // error
//                    exception = e1;
//                }
//
//                XmUserInfoEvent event = new XmUserInfoEvent();
//                event.result = resultEvent;
//                EventBus.getDefault().post(event);
//            }
//        });
//    }

    //    private void bindThirdParty(final String appId, final String openId, final String accessToken, final String type) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                BindThirdPartyRequest request = new BindThirdPartyRequest();
//                request.appId = appId;
//                request.openId = openId;
//                request.accessToken = accessToken;
//                request.type = type;
//
//                BindThirdPartyResponse bindThirdPartyResponse = null;
//                try {
//                    bindThirdPartyResponse = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "bindThirdParty response=" + bindThirdPartyResponse);
//                    if (bindThirdPartyResponse != null && bindThirdPartyResponse.bindResult) {
//                        User user = UserContext.getInstance(mAppContext).getCurrentUser();
//                        if (user != null) {
//                            /*String currType = user.getCurrentAccountType();
//                            int bindTypeInt = 0, currTypeInt = 0;
//                            if(QQ_BIND_TYPE.equals(type) || WB_BIND_TYPE.equals(type) || WX_BIND_TYPE.equals(type)){
//                                bindTypeInt = 3;
//                            }else if(XM_BIND_TYPE.equals(type)){
//                                bindTypeInt = 2;
//                            }else {
//                                bindTypeInt = 1;
//                            }
//                            if(QQ_LOGIN_TYPE.equals(currType) || WB_LOGIN_TYPE.equals(currType) || WX_LOGIN_TYPE.equals(currType)){
//                                currTypeInt = 3;
//                            }else if(XM_LOGIN_TYPE.equals(currType)){
//                                currTypeInt = 2;
//                            }else if(PHONE_LOGIN_TYPE.equals(currType)){
//                                currTypeInt = 1;
//                            }else {
//                                currTypeInt = 3;
//                            }
//
//                            if(bindTypeInt > currTypeInt){   //如果绑定的账号优先级高于现有账号类型，才更新用户账号信息*/
//                            user.setExistAccountExceptMobile("1");
//                            user.setHeadIconUrl(bindThirdPartyResponse.headIconUrl);
//                            user.setUserName(bindThirdPartyResponse.userName);
//                            user.setSex(bindThirdPartyResponse.sex);
//                            UserContext.getInstance(mAppContext).updateUserInfo(user);
//                            bindThirdPartyResponse.resultCode = BindResultEvent.RESULT_OK;
//                            /*}*/
//                        }
//                    } else {
//                        bindThirdPartyResponse = new BindThirdPartyResponse();
//                        bindThirdPartyResponse.resultCode = BindResultEvent.RESULT_ERROR;
//                        bindThirdPartyResponse.bindResult = false;
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                    if (e.getErrorCode() == ACCOUNT_HAS_USED_ERR || e.getErrorCode() == ACCOUNT_INVALID_ERR) {
//                        bindThirdPartyResponse = new BindThirdPartyResponse();
//                        bindThirdPartyResponse.resultCode = ACCOUNT_HAS_USED_ERR;
//                        bindThirdPartyResponse.bindResult = false;
//                    }
//                }
//
//                handleBindResult(bindThirdPartyResponse);
//            }
//        });
//    }
//
//    private void handleBindResult(BindThirdPartyResponse response) {
//        BindResultEvent event = new BindResultEvent();
//        int result = BindResultEvent.RESULT_OK;
//        if (response == null) {
//            result = BindResultEvent.RESULT_ERROR;
//        } else if (!response.bindResult) {
//            result = response.resultCode;
//        }else {
//            event.data = response.userName;
//        }
//        event.resultCode = result;
//
//        EventBus.getDefault().post(event);
//    }
//
//    private void openIdLogin(final String appId, final String openId, final String accessToken, final String type) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                ThirdPartyLoginRequest request = new ThirdPartyLoginRequest();
//                request.appId = appId;
//                request.openId = openId;
//                request.accessToken = accessToken;
//                request.type = type;
//
//                LoginResponse loginResponse = null;
//                try {
//                    String response = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "openIdLogin type=" + type + "   response=" + response);
//                    if (!TextUtils.isEmpty(response)) {
//                        loginResponse = JsonUtils.parse(response, LoginResponse.class);
//                        if (loginResponse != null) {
//                            loginResponse.loginType = type;
//                        }
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//
//                handleLoginResult(loginResponse);
//            }
//        });
//    }
//
    private void handleLoginResult(PhoneLoginResponse loginResponse) {
        if (loginResponse != null && loginResponse.getUserId() > 0) {
            User user = new User();
            user.setUserId(loginResponse.getUserId());
            user.setUserName(loginResponse.getUserName());

            //保存登录信息
            UserContext.getInstance(mAppContext).saveUserInfo(user);
//            UserContext.getInstance(mAppContext).backupLoginTicket(user.getT());
//            handleLoginSuccess(loginResponse.loginType, loginResponse.isNewUser);
            //获取用户详细信息
//            getUserDetailInfo(loginResponse.userId, loginResponse.loginType, loginResponse.isNewUser);
        } else {
//            handleLoginFailed(loginResponse);
            UserContext.getInstance(mAppContext).cleanLoginInfo();
        }
    }

    //
//    /**
//     * 登录成功请求用户详细信息
//     */
//    private void getUserDetailInfo(final long userId, final String loginType, final boolean isNewUser){
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//
//                GetUserInfoRequest request = new GetUserInfoRequest();
//                request.userId = userId;
//
//                GetUserInfoResponse response = null;
//                int resultCode = UserInfoEvent.RESULT_ERROR;
//
//                try {
//                    response = InternetUtil.request(mAppContext, request);
//                    if (response != null) {
//                        resultCode = UserInfoEvent.RESULT_OK;
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//                // 保存自己的用户信息
//                UserContext userContext = UserContext.getInstance(mAppContext);
//                if (resultCode == UserInfoEvent.RESULT_OK && userId == userContext.getLoginUid()) {
//                    User user = userContext.getLoginUser();
//                    if (user != null) {
////                        user.setUserName(response.userName);
////                        user.setHeadIconUrl(response.headIconUrl);
////                        user.setSex(response.sex);
//                        user.setDesc(response.desc);
//                        user.setGold(response.point);
//                        user.setUploadVideoCount(response.videoCount);
//                        user.setLikedVideoCount(response.likedVideoCount);
//                        user.setFollowedTagCount(response.followedTagCount);
//                        user.setUserFollowCount(response.userFollowCount);
//                        user.setUserFansCount(response.userFollowMeCount);
//                        user.setMessageCount(response.messageCount);
//                        user.setNewMessageCount(response.newMessageCount);
//
//                        userContext.saveUserInfo(user);
//
//                        GoldChangeEvent event = new GoldChangeEvent();
//                        EventBus.getDefault().post(event);
//                    }
//                }
//                handleLoginSuccess(loginType, isNewUser);
//            }
//        });
//    }
//
    private void handleLoginSuccess(String loginType) {
        LoginResultEvent event = new LoginResultEvent();
        event.loginType = loginType;
        event.resultCode = LoginResultEvent.RESULT_OK;
        EventBus.getDefault().post(event);
    }


    private void handleLoginFailed(PhoneLoginResponse loginResponse) {
        LoginResultEvent event = new LoginResultEvent();
        event.resultCode = loginResponse.getCode();
        EventBus.getDefault().post(event);
    }

    //
//    private void xmLogin(Activity activity, final String operation) {
//        final XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
//                .setAppId(XM_APPID)
//                .setRedirectUrl(XM_REDIRECTURI)
//                .setScope(XM_SCOPE)
//                .startGetAccessToken(activity);
//
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//
//                XiaomiOAuthResults results = null;
//                Exception exception = null;
//
//                try {
//                    results = future.getResult();
//
////                    if (results.hasError()) {
////                        int errorCode = results.getErrorCode();
////                        String errorMessage = results.getErrorMessage();
////                    } else {
////                        String accessToken = results.getAccessToken();
////                        String macKey = results.getMacKey();
////                        String macAlgorithm = results.getMacAlgorithm();
////                    }
//                } catch (IOException e1) {
//                    // error
//                    exception = e1;
//                } catch (OperationCanceledException e1) {
//                    // user cancel
//                    exception = e1;
//                } catch (XMAuthericationException e1) {
//                    // error
//                    exception = e1;
//                }
//
//                //post
//                XmOauthEvent xiaomiOauthEvent = new XmOauthEvent();
//                xiaomiOauthEvent.results = results;
//                xiaomiOauthEvent.exception = exception;
//                xiaomiOauthEvent.operation = operation;
//                EventBus.getDefault().post(xiaomiOauthEvent);
//
//            }
//        });
//    }
//
    private void wxLogin(final Activity activity, final String operation) {
        mOperation = operation;
        IWXAPI api = WXAPIFactory.createWXAPI(activity, ShareHelper.WX_APP_ID, false);
        api.registerApp(ShareHelper.WX_APP_ID);

        if (!api.isWXAppInstalled()) {
            WxCodeEvent event = new WxCodeEvent();
            event.resultCode = Constants.WX_NOT_INSTALLED;
            EventBus.getDefault().post(event);
            //ToastManager.show(activity, R.string.weixin_not_installed);
            return;
        }

        // 唤起微信登录授权
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_login";
        api.sendReq(req);

    }

    //
//    public void wxOpenIdLoginOrBind(String openId, String accessToken) {
//        DebugLog.d(TAG, "openid_info =" + openId + "  token=" + accessToken);
//        if (!TextUtils.isEmpty(openId) && !TextUtils.isEmpty(accessToken)) {
//            if (ThirdPartyType.BIND_OPERATION.equals(mOperation)) {
//                bindThirdParty(ShareHelper.WX_APP_ID, openId, accessToken, WX_BIND_TYPE);
//            } else if (ThirdPartyType.LOGIN_OPERATION.equals(mOperation)) {
//                openIdLogin(ShareHelper.WX_APP_ID, openId, accessToken, WX_LOGIN_TYPE);
//            }
//        }
//    }
//
//    public void qqOpenIdLoginOrBind(String openId, String accessToken) {
//        DebugLog.d(TAG, "openid_info =" + openId + "  token=" + accessToken);
//        if (!TextUtils.isEmpty(openId) && !TextUtils.isEmpty(accessToken)) {
//            if (ThirdPartyType.BIND_OPERATION.equals(mOperation)) {
//                bindThirdParty(ShareHelper.QQ_APP_ID, openId, accessToken, QQ_BIND_TYPE);
//            } else if (ThirdPartyType.LOGIN_OPERATION.equals(mOperation)) {
//                openIdLogin(ShareHelper.QQ_APP_ID, openId, accessToken, QQ_LOGIN_TYPE);
//            }
//        }
//    }
//
//    private SsoHandler mSsoHandler;
//
//    private void weiboLogin(final Activity activity, final String operation) {
//        mOperation = operation;
//        AuthInfo authInfo = new AuthInfo(activity, ShareHelper.WEIBO_APP_ID, WB_REDIRECT_URL, WB_SCOPE);;
//        mSsoHandler = new SsoHandler(activity, authInfo);
//
//        mSsoHandler.authorize(new AuthListener());
//    }
//
//    public void weiboLoginCallback(int requestCode, int resultCode, Intent data){
//        if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }
//    /**
//     * 微博认证授权回调类。
//     * 1. SSO 授权时，需要在 {@link Activity#onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
//     *    该回调才会被执行。
//     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
//     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
//     */
//    class AuthListener implements WeiboAuthListener {
//
//        @Override
//        public void onComplete(Bundle values) {
//            // 从 Bundle 中解析 Token
//            Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//            if (mAccessToken.isSessionValid()) {
//                // 显示 Token
////                updateTokenView(false);
//
//                // 保存 Token 到 SharedPreferences
//                AccessTokenKeeper.writeAccessToken(mAppContext, mAccessToken);
////                Toast.makeText(WBAuthActivity.this,
////                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
//                WeiboAuthEvent weiboAuthEvent = new WeiboAuthEvent();
//                weiboAuthEvent.resultCode = WeiboAuthEvent.AUTH_SUCCESS;
//                weiboAuthEvent.accessToken = mAccessToken;
//                EventBus.getDefault().post(weiboAuthEvent);
//            } else {
//                // 以下几种情况，您会收到 Code：
//                // 1. 当您未在平台上注册的应用程序的包名与签名时；
//                // 2. 当您注册的应用程序包名与签名不正确时；
//                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                String code = values.getString("code");
////                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
////                if (!TextUtils.isEmpty(code)) {
////                    message = message + "\nObtained the code: " + code;
////                }
////                Toast.makeText(WBAuthActivity.this, message, Toast.LENGTH_LONG).show();
//                WeiboAuthEvent weiboAuthEvent = new WeiboAuthEvent();
//                weiboAuthEvent.resultCode = WeiboAuthEvent.AUTH_FAILED;
//                weiboAuthEvent.errorCode = code;
//                EventBus.getDefault().post(weiboAuthEvent);
//            }
//        }
//
//        @Override
//        public void onCancel() {
////            Toast.makeText(WBAuthActivity.this,
////                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
//            WeiboAuthEvent weiboAuthEvent = new WeiboAuthEvent();
//            weiboAuthEvent.resultCode = WeiboAuthEvent.USER_CANCEL;
//            EventBus.getDefault().post(weiboAuthEvent);
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
////            Toast.makeText(WBAuthActivity.this,
////                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
//            WeiboAuthEvent weiboAuthEvent = new WeiboAuthEvent();
//            weiboAuthEvent.resultCode = WeiboAuthEvent.WEIBO_EXCEPTION;
//            weiboAuthEvent.errorCode = e.getMessage();
//            EventBus.getDefault().post(weiboAuthEvent);
//        }
//    }
//
//    public void wbLoginOrBind(Oauth2AccessToken accessToken) {
//        String openId = accessToken.getUid();
//        String token = accessToken.getToken();
//        DebugLog.d(TAG, "openid_info =" + openId + "  token=" + accessToken);
//        if (!TextUtils.isEmpty(openId) && !TextUtils.isEmpty(token)) {
//            if (ThirdPartyType.BIND_OPERATION.equals(mOperation)) {
//                bindThirdParty(ShareHelper.WEIBO_APP_ID, openId, token, WB_BIND_TYPE);
//            } else if (ThirdPartyType.LOGIN_OPERATION.equals(mOperation)) {
//                openIdLogin(ShareHelper.WEIBO_APP_ID, openId, token, WB_LOGIN_TYPE);
//            }
//        }
//    }
//    private QQAuthListener qqAuthListener;
//    /**
//     * QQ登录模块
//     */
//    public void qqLogin(Activity activity, String operation){
//        mOperation = operation;
//        Tencent tencent = Tencent.createInstance(ShareHelper.QQ_APP_ID, mAppContext);
//        qqAuthListener = new QQAuthListener();
//        tencent.login(activity, QQ_SCOPE, qqAuthListener);
//    }
//
//    private class QQAuthListener implements IUiListener {
//
//        @Override
//        public void onComplete(Object object) {
//            QQAuthEvent qqAuthEvent = new QQAuthEvent();
//            qqAuthEvent.resultCode = QQAuthEvent.AUTH_SUCCESS;
//            qqAuthEvent.object = object;
//            EventBus.getDefault().post(qqAuthEvent);
//        }
//
//        @Override
//        public void onError(UiError uiError) {
//            QQAuthEvent qqAuthEvent = new QQAuthEvent();
//            qqAuthEvent.resultCode = QQAuthEvent.QQ_EXCEPTION;
//            qqAuthEvent.errorCode = uiError.errorDetail;
//            EventBus.getDefault().post(qqAuthEvent);
//        }
//
//        @Override
//        public void onCancel() {
//            QQAuthEvent qqAuthEvent = new QQAuthEvent();
//            qqAuthEvent.resultCode = QQAuthEvent.USER_CANCEL;
//            EventBus.getDefault().post(qqAuthEvent);
//        }
//    }
//
//    public void qqLoginCallback(int requestCode, int resultCode, Intent data){
//        if(qqAuthListener != null){
//            Tencent.onActivityResultData(requestCode, resultCode, data, qqAuthListener);
//        }
//    }
//
//    /**
//     * 获取QQ的授权信息
//     *
//     * @param access_token
//     */
//    public void getQQUserInfo(final String access_token, final String openid) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                QQOauthRequest request = new QQOauthRequest();
//                request.oauth_consumer_key = ShareHelper.QQ_APP_ID;
//                request.openid = openid;
//                request.access_token = access_token;
//
//                QQOauthResponse qqOauthResponse = null;
//                try {
//                    qqOauthResponse = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "getAccessTokenAndOpenId response=" + qqOauthResponse);
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//
//                QQInfoEvent event = new QQInfoEvent();
//                if (qqOauthResponse != null && !TextUtils.isEmpty(qqOauthResponse.nickname)) {
////                    event.code = WxOauthEvent.RESULT_OK;
////                    event.openid = qqOauthResponse.openid;
////                    event.accessToken = qqOauthResponse.access_token;
//                    event.response = qqOauthResponse;
//                } else {
////                    event.code = WxOauthEvent.RESULT_ERROR;
//                }
//
//                EventBus.getDefault().post(event);
//            }
//        });
//
//    }
//
//    /**
//     * 获取微信的授权信息
//     *
//     * @param code
//     */
//    public void getWxAccessTokenAndOpenId(final String code) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                WxOauthRequest request = new WxOauthRequest();
//                request.appid = ShareHelper.WX_APP_ID;
//                request.secret = ShareHelper.WX_APP_SECRET;
//                request.grant_type = "authorization_code";
//                request.code = code;
//
//                WxOauthResponse wxOauthResponse = null;
//                try {
//                    wxOauthResponse = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "getAccessTokenAndOpenId response=" + wxOauthResponse);
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//
//                WxOauthEvent event = new WxOauthEvent();
//                if (wxOauthResponse != null && !TextUtils.isEmpty(wxOauthResponse.openid)) {
//                    event.code = WxOauthEvent.RESULT_OK;
//                    event.openid = wxOauthResponse.openid;
//                    event.accessToken = wxOauthResponse.access_token;
//                } else {
//                    event.code = WxOauthEvent.RESULT_ERROR;
//                }
//
//                EventBus.getDefault().post(event);
//            }
//        });
//
//    }
//
//    /**
//     * 获取微博的授权信息
//     *
//     * @param code
//     */
//    public void getWbAccessTokenAndOpenId(final String code) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                WxOauthRequest request = new WxOauthRequest();
//                request.appid = ShareHelper.WX_APP_ID;
//                request.secret = ShareHelper.WX_APP_SECRET;
//                request.grant_type = "authorization_code";
//                request.code = code;
//
//                WxOauthResponse wxOauthResponse = null;
//                try {
//                    wxOauthResponse = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "getAccessTokenAndOpenId response=" + wxOauthResponse);
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//
//                WxOauthEvent event = new WxOauthEvent();
//                if (wxOauthResponse != null && !TextUtils.isEmpty(wxOauthResponse.openid)) {
//                    event.code = WxOauthEvent.RESULT_OK;
//                    event.openid = wxOauthResponse.openid;
//                    event.accessToken = wxOauthResponse.access_token;
//                } else {
//                    event.code = WxOauthEvent.RESULT_ERROR;
//                }
//
//                EventBus.getDefault().post(event);
//            }
//        });
//
//    }
//
//
    private void pnLogin(Activity activity) {
        Intent to = new Intent(activity, PhoneNumLoginActivity.class);
        activity.startActivity(to);
        //AnalyticsAgent.trackEvent(new LoginPhonePageData("login_page"));
    }
//
//    /**
//     * 获取手机验证码
//     *
//     * @param phoneNum
//     */
//    public void getPhoneCode(final String phoneNum, final String type) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                CommonResultEvent event = new CommonResultEvent();
//                event.resultCode = CommonResultEvent.RESULT_ERROR;
//
//                GetPhoneCodeRequest request = new GetPhoneCodeRequest();
//                request.mobile = phoneNum;
//                request.type = type;
//                try {
//                    CommonResultResponse response = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "getPhoneCode response=" + response);
//                    if (response != null && response.result == 0) {
//                        event.resultCode = CommonResultEvent.RESULT_OK;
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//
//                EventBus.getDefault().post(event);
//            }
//        });
//    }

    /**
     * 手机号登录
     *
     * @param phoneNum
     */
    public void loginByPhone(final String phoneNum, final String password) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                PhoneLoginRequest request = new PhoneLoginRequest();
                request.mobile = phoneNum;
                request.password = password;

                PhoneLoginResponse loginResponse = null;
                try {
                    String response = InternetUtil.request(mAppContext, request);
                    if (!TextUtils.isEmpty(response)) {
                        loginResponse = JSON.parseObject(response, PhoneLoginResponse.class);

                        if (loginResponse.getCode() != RESULT_OK)
                            handleLoginFailed(loginResponse);
                        else {
                            handleLoginSuccess(PHONE_LOGIN_TYPE);
                            handleLoginResult(loginResponse);
                        }
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

            }
        });
    }
//
//    /**
//     * 绑定手机号
//     *
//     * @param phoneNum
//     * @param phoneCode
//     */
//    public void bindByPhone(final String phoneNum, final String phoneCode) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                BindPhoneRequest request = new BindPhoneRequest();
//                request.mobile = phoneNum;
//                request.mobileCode = phoneCode;
//                int resultCode = BindPhoneResultEvent.RESULT_ERROR;
//
//                try {
//                    BindPhoneResponse response = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "bindByPhone response=" + response);
//                    if (response != null && response.result == 0) {
//                        resultCode = BindPhoneResultEvent.RESULT_OK;
//                        User user = UserContext.getInstance(mAppContext).getCurrentUser();
//                        if (user != null) {
//                            user.setPhoneNum(phoneNum);
//                            user.setExistAccountExceptMobile("1");
//                            UserContext.getInstance(mAppContext).updateUserInfo(user);
//                        }
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                    resultCode = e.getErrorCode();
//                }
//
//                BindPhoneResultEvent event = new BindPhoneResultEvent();
//                event.resultCode = resultCode;
//                event.phoneNum = phoneNum;
//                EventBus.getDefault().post(event);
//            }
//        });
//    }
//    /**
//     * 解绑账号
//     */
//    public void unBindAccount(final String source){
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                BindEvent bindEvent = new BindEvent();
//                bindEvent.type = TYPE_UNBIND;
//                bindEvent.result = false;
//
//                UnBindRequest request = new UnBindRequest();
//                User user = UserContext.getInstance(mAppContext).getCurrentUser();
////                if (user == null) {
////                    return;
////                }
//                if (TYPE_PHONE.equals(source)) { //解绑手机号
//                    //String openId = userInfo.data != null ? String.valueOf(userInfo.data.openId) : "";
//                    //request.openId = user.getPhoneNum();
//                    request.userAccountType = "mobile";
//                    bindEvent.source = TYPE_PHONE;
//                } else if (TYPE_WECHAT.equals(source)) { //解绑第三方账号
//                    //unBindThirdParty();
//                    //String openId = userInfo.data != null ? String.valueOf(userInfo.data.openId) : "";
//                    //request.openId = openId;
//                    request.userAccountType = "weichat";
//                    bindEvent.source = TYPE_WECHAT;
//                } else if (TYPE_QQ.equals(source)) { //解绑第三方账号
//                    //unBindThirdParty();
//                    //String openId = userInfo.data != null ? String.valueOf(userInfo.data.openId) : "";
//                    //request.openId = openId;
//                    request.userAccountType = "qq";
//                    bindEvent.source = TYPE_QQ;
//                } else if (TYPE_WEIBO.equals(source)) { //解绑第三方账号
//                    //unBindThirdParty();
//                    //String openId = userInfo.data != null ? String.valueOf(userInfo.data.openId) : "";
//                    //request.openId = openId;
//                    request.userAccountType = "weibo";
//                    bindEvent.source = TYPE_WEIBO;
//                } else {
//                    //String openId = userInfo.data != null ? String.valueOf(userInfo.data.openId) : "";
//                    //request.openId = openId;
//                    request.userAccountType = "xiaomi";
//                    bindEvent.source = TYPE_XIAOMI;
//                }
//                int resultCode = BindPhoneResultEvent.RESULT_ERROR;
//                try {
//                    UnBindResponse response = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "unBindPhone response=" + response);
//                    if (response != null && response.result == 0) {
//                        resultCode = BindEvent.RESULT_OK;
//                        bindEvent.result = true;
//                        //User user = UserContext.getInstance(mAppContext).getCurrentUser();
//                        if (user != null) {
//                            //user.setPhoneNum(phoneNum);
//                            //user.setExistAccountExceptMobile("1");
//                            if (TYPE_PHONE.equals(source)) {
//                                user.setPhoneNum("");
//                            } else {
//
//                            }
//                            UserContext.getInstance(mAppContext).updateUserInfo(user);
//                        }
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                    resultCode = e.getErrorCode();
//                    bindEvent.result = false;
//                }
//                bindEvent.resultCode = resultCode;
//                EventBus.getDefault().post(bindEvent);
//            }
//        });
//    }
//
//    public void getBindAccountList(){
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                BindAccountListEvent event = new BindAccountListEvent();
//                GetOAuthAccountListRequest request = new GetOAuthAccountListRequest();
//                int resultCode = BindPhoneResultEvent.RESULT_ERROR;
//                try {
//                    GetOAuthAccountListResponse response = InternetUtil.request(mAppContext, request);
//                    DebugLog.d(TAG, "GetOAuthAccountListResponse response=" + response);
//                    if (response != null && response.oauthAccountList != null) {
//                        resultCode = BindAccountListEvent.RESULT_OK;
//                        event.oauthAccountList = response.oauthAccountList;
//                        User user = UserContext.getInstance(mAppContext).getCurrentUser();
//                        if (user != null) {
////                            String account = "";
//                            JSONArray jsonArray = new JSONArray();
//                            for (int i = 0; i < response.oauthAccountList.size(); i++) {
//                                JSONObject jsonObject = new JSONObject();
//                                try {
//                                    jsonObject.put("type", response.oauthAccountList.get(i).getType());
//                                    jsonObject.put("userName", response.oauthAccountList.get(i).getUserName());
//                                    jsonObject.put("headIconUrl", response.oauthAccountList.get(i).getHeadIconUrl());
//                                    jsonArray.put(jsonObject);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                //account += response.oauthAccountList.get(i) + "#";
//                            }
////                            account = account.substring(0, account.length() - 1);
//                            user.setUserBindAccount(jsonArray.toString());
//                            UserContext.getInstance(mAppContext).updateUserInfo(user);
//                        }
//                    }
//
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                    resultCode = e.getErrorCode();
//                }
//                event.resultCode = resultCode;
//                EventBus.getDefault().post(event);
//            }
//        });
//    }
//    /**
//     * 解绑手机号
//     */
//    private void unBindAccount(){
//        UnBindRequest request = new UnBindRequest();
////        request.mobile = phoneNum;
////        request.mobileCode = phoneCode;
////        int resultCode = BindPhoneResultEvent.RESULT_ERROR;
////
////        try {
////            BindPhoneResponse response = InternetUtil.request(mAppContext, request);
////            DebugLog.d(TAG, "bindByPhone response=" + response);
////            if (response != null && response.result == 0) {
////                resultCode = BindPhoneResultEvent.RESULT_OK;
////                User user = UserContext.getInstance(mAppContext).getCurrentUser();
////                if (user != null) {
////                    user.setPhoneNum(phoneNum);
////                    user.setExistAccountExceptMobile("1");
////                    UserContext.getInstance(mAppContext).updateUserInfo(user);
////                }
////            }
////        } catch (NetWorkException e) {
////            e.printStackTrace();
////            resultCode = e.getErrorCode();
////        }
//
////        BindPhoneResultEvent event = new BindPhoneResultEvent();
////        event.resultCode = resultCode;
////        EventBus.getDefault().post(event);
//
//        BindEvent bindEvent = new BindEvent();
//        bindEvent.type = TYPE_UNBIND;
//        bindEvent.source = TYPE_PHONE;
//        bindEvent.result = true;
////        EventBus.getDefault().post(bindEvent);
//    }

    /**
     * 解绑第三方账号
     */
    private void unBindThirdParty() {

    }
}
