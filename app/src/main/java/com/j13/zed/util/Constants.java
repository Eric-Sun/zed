package com.j13.zed.util;

/**
 * 常量类
 * Created by Melody on 2016/5/31.
 */
public interface Constants {

    int NUM_PAGES = 3;
    int TAB_FOLLOW = 0;
    int TAB_HOT = 1;
    int TAB_NEW = 2;

    String KEY_ENTER_COUNT = "key_enter_count";
    String KEY_ENTER_TIME = "key_enter_time";
    String KEY_CHECK_TIME = "key_check_time";
    String KEY_NEVER_SHOW = "key_never_show";
    int DEFAULT_ENTER_COUNT = 2;
    int DEFAULT_ENTER_TIME = 3;
    int DEFAULT_CHECK_TIME = 2;

    /**
     * R.id.tab_homepage、R.id.tab_claim、R.id.tab_discover、R.id.tab_mine
     */
    String EXTRA_MAIN_TAB_ID = "main_tab";
    /**
     * TAB_FOLLOW、TAB_HOT、TAB_NEW
     */
    String EXTRA_HOMEPAGE_TAB = "homepage_tab";
    String EXTRA_REFRESH = "refresh";

    String EXTRA_MISSION = "mission";
    String EXTRA_TOPIC = "topic_for_login";
    String EXTRA_TAG = "tag";
    String EXTRA_POS = "pos";
    String EXTRA_TYPE = "type";

    String EXTRA_CATEGORY = "category";
    String EXTRA_USER_ID = "userId";
    String EXTRA_VIDEO_TAG = "video_tag";
    String EXTRA_ALBUM_ID = "albumId";
    String EXTRA_FROM_PUSH = "from_push";
    String EXTRA_USER_NAME = "userName";
    String EXTRA_USER_DESC = "userDesc";

    String EXTRA_TOPIC_COUNT = "topic_count";
    String EXTRA_VIDEO_COUNT = "video_count";
    String EXTRA_USER_COUNT = "user_count";

    String MINE_TAG = "MineUserCenterFragment-Login-Btn";
    String SPLASH_TAG = "SplashActivity";
    String CLAIM_TAG = "ClaimActivity";
    String UPLOAD_TAG = "UploadActivity";

    // growingIO
    String GROWINGIO_APPID = "a904ffb8bb146d8d";
    String GROWINGIO_SCHEME = "growing.e764000c4ed5a3ce";

    String KEY_CATEGORY = "category";
    String KEY_ALBUM_ID = "albumId";

    String TYPE_PHONE = "phone";
    String TYPE_WECHAT = "wechat";
    String TYPE_XIAOMI = "xiaomi";
    String TYPE_QQ = "qq";
    String TYPE_WEIBO = "weibo";

    String TYPE_BIND = "bind";
    String TYPE_UNBIND = "unbind";

    String BIND_TAG = "account_bind";
    String BIND_SOURCE = "bind_source";

    int WX_NOT_INSTALLED = -6;

    String LOG_FILE_NAME = "debuglog";
    String LOG_TAG = "YouLiao";
}
