package com.j13.zed;

import android.Manifest;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Config;

import com.j13.zed.user.UserContext;
import com.j13.zed.util.AppUtils;
import com.j13.zed.util.Constants;
import com.j13.zed.util.DebugLog;
import com.michael.corelib.config.CoreConfig;
import com.orhanobut.logger.Logger;
import com.xunlei.analytics.HubbleAgent;

import java.util.HashMap;

public class ZedApplication extends Application {

    //for Hubble Analytics
    private static final String CHANNEL_ID = "short_video";

    private static ZedApplication sApplication;

    public static ZedApplication getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
//        mPermissionChecker = new PermissionHelper(this);
        // init logger
        Logger.init(Constants.LOG_TAG).hideThreadInfo().methodCount(0);

//        CoreConfig.init(this, Config.DEBUG, Config.ROOT_PATH, Constants.LOG_FILE_NAME);
//
        if (!AppUtils.isService(sApplication)) {
//            PushConstants.init();
//            RequestErrorHandler.getInstance().init(this);
            UserContext.getInstance(this).initLogin();
//            VideoItemManager.getInstance(this).initLoad();

//            if(!PermissionHelper.lackPermission(this, Manifest.permission.READ_PHONE_STATE)){  //不缺少READ_PHONE_STATE权限，才执行
//                initHubble();
//                initConfig();
//                checkCloudConfig();
//            }
//            if(!PermissionHelper.lackPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){  //不缺少WRITE_EXTERNAL_STORAGE权限，才执行
//                initMiPush();
//                initVideoManager();
//            }
        }

//        //init Hubble Agent
//        HubbleAgent.init(sApplication, APP_ID, SECRET_KEY, getChannelId());
//        HubbleAgent.setDebugMode(Config.DEBUG);
//        HubbleAgent.setEventReportSwitch(true);
//        HubbleAgent.setReportEventServerMode(Config.HUBBLE_MODE);

//        PermissionHelper checker = new PermissionHelper(this);
    }

    private String getChannelId() {
        String channel = CHANNEL_ID;
        try {
            ApplicationInfo appInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        DebugLog.i("ShortVideo", "App Channel:" + channel);
        return channel;
    }


//    static final String[] PERMISSIONS = new String[]{
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
////            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE
//    };

//    private PermissionHelper mPermissionChecker;

    public void initData() {
    }


}
