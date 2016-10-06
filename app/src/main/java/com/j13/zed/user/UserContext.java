package com.j13.zed.user;

import android.content.Context;
import android.text.TextUtils;

import com.j13.zed.util.ApplicationConfig;
import com.j13.zed.util.StringUtils;

import java.util.Properties;

/**
 * Created by lz on 16/4/7.
 */
public class UserContext {

    private static final String KEY_UID = "user.uid";
    private static final String KEY_NAME = "user.name";
    private static final String KEY_ICON_URL = "user.headIconUrl";
    private static final String KEY_SEX = "user.sex";
    private static final String KEY_T = "user.t";
    private static final String KEY_SECRET = "user.userSecretKey";
    private static final String KEY_PHONE = "user.phoneNum";
    private static final String KEY_EXCEPT_MOBILE = "user.existAccountExceptMobile";
    private static final String KEY_GOLD = "user.gold";
    private static final String KEY_LIKE = "user.like";
    private static final String KEY_UPLOAD = "user.upload";
    private static final String KEY_TOPIC = "user.topic";
    private static final String KEY_FOLLOW = "user.follow";
    private static final String KEY_FANS = "user.fans";
    private static final String KEY_MSG = "user.msg";
    private static final String KEY_NEW_MSG = "user.newMsg";

    private volatile static UserContext instance;

    private Context mAppContext;

    private long loginUid;
    private boolean login;
    private User currentUser;

    private UserContext(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public static UserContext getInstance(Context context) {
        if (instance == null) {
            synchronized (UserContext.class) {
                if (instance == null) {
                    instance = new UserContext(context);
                }
            }
        }

        return instance;
    }

    public void initLogin() {
        User user = getLoginUser();
        if (null != user && user.getUserId() > 0) {
            login = true;
            loginUid = user.getUserId();
            currentUser = user;
        } else {
            this.cleanLoginInfo();
        }
    }

    public long getLoginUid() {
        return loginUid;
    }

    public boolean isLogin() {
        return login;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isExistPhoneAccount() {
        if (currentUser != null) {
            return !TextUtils.isEmpty(currentUser.getPhoneNum());
        } else {
            return false;
        }
    }

    /**
     * 用户注销
     */
    public void logout() {
        cleanLoginInfo();
        this.login = false;
        this.loginUid = 0;
    }

    /**
     * 保存登录信息
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final User user) {
        this.loginUid = user.getUserId();
        this.login = true;
        currentUser = user;
        setProperties(new Properties() {
            {
                setProperty(KEY_UID, StringUtils.trimNull(String.valueOf(user.getUserId())));
                setProperty(KEY_NAME, StringUtils.trimNull(user.getUserName()));
                setProperty(KEY_ICON_URL, StringUtils.trimNull(user.getHeadIconUrl()));// 用户头像-文件名
                setProperty(KEY_SEX, StringUtils.trimNull(user.getSex()));
                setProperty(KEY_T, StringUtils.trimNull(user.getT()));
                setProperty(KEY_PHONE, StringUtils.trimNull(user.getPhoneNum()));
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final User user) {
        if (currentUser != null) {
            currentUser.setUserName(user.getUserName());
            currentUser.setHeadIconUrl(user.getHeadIconUrl());
            currentUser.setSex(user.getSex());
            currentUser.setPhoneNum(user.getPhoneNum());
        }
        setProperties(new Properties() {
            {
                setProperty(KEY_NAME, StringUtils.trimNull(user.getUserName()));
                setProperty(KEY_ICON_URL, StringUtils.trimNull(user.getHeadIconUrl()));// 用户头像-文件名
                setProperty(KEY_SEX, StringUtils.trimNull(user.getSex()));
                setProperty(KEY_PHONE, StringUtils.trimNull(user.getPhoneNum()));
            }
        });
    }


    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public User getLoginUser() {
        User user = new User();
        user.setUserId(StringUtils.toLong(getProperty(KEY_UID), 0));
        user.setUserName(getProperty(KEY_NAME));
        user.setHeadIconUrl(getProperty(KEY_ICON_URL));
        user.setSex(getProperty(KEY_SEX));
        user.setT(getProperty(KEY_T));
        user.setPhoneNum(getProperty(KEY_PHONE));
        return user;
    }

    public String getProperty(String key) {
        String res = ApplicationConfig.getInstance(mAppContext).get(key);
        return res;
    }

    public void setProperties(Properties ps) {
        ApplicationConfig.getInstance(mAppContext).set(ps);
    }

    public Properties getProperties() {
        return ApplicationConfig.getInstance(mAppContext).get();
    }

    public void setProperty(String key, String value) {
        ApplicationConfig.getInstance(mAppContext).set(key, value);
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        currentUser = null;
        removeProperty(KEY_UID, KEY_NAME, KEY_ICON_URL, KEY_SEX,
                KEY_T, KEY_SECRET, KEY_PHONE, KEY_EXCEPT_MOBILE,
                KEY_GOLD, KEY_UPLOAD, KEY_LIKE, KEY_TOPIC, KEY_FOLLOW,
                KEY_FANS, KEY_MSG, KEY_NEW_MSG);
    }

    public void removeProperty(String... key) {
        ApplicationConfig.getInstance(mAppContext).remove(key);
    }


}
