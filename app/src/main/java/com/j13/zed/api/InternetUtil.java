package com.j13.zed.api;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.michael.corelib.internet.core.BeanRequestFactory;
import com.michael.corelib.internet.core.NetWorkException;
import com.michael.corelib.internet.core.RequestBase;
import com.michael.corelib.internet.core.ResponseBase;
import com.michael.corelib.internet.core.util.JsonUtils;

public class InternetUtil {

    public static final String ACTION_API_SERVER_ERROR = "com.plugin.internet.error.server";
    public static final String ACTION_API_LOCAL_ERROR = "com.plugin.internet.error.local";

    public static final String EXTRA_ERROR_CODE = "code";
    public static final String EXTRA_ERROR_MSG = "msg";

    // 系统错误返回值的最小值
    public static final int SYSTEM_ERROR_CODE_START = 1;
    // 系统错误返回值的最大值
    public static final int SYSTEM_ERROR_CODE_END = 999;

    private static int sCurVersionCode = 0;

    /**
     * 同步接口 发送REST请求
     *
     * @param <T>
     * @param request REST请求
     * @return REST返回
     */
    public static <T> T requestInternal(Context context, RequestBase<T> request) throws NetWorkException {
        try {
            if (context != null && BeanRequestFactory.createBeanRequestInterface(context.getApplicationContext()) != null) {
                T response = BeanRequestFactory.createBeanRequestInterface(context.getApplicationContext()).request(request);

//                if (response != null && (response instanceof ResponseBase || response instanceof String)) {
//                    ServerErrorResponse errorResponse = null;
//                    if (response instanceof ResponseBase) {
////                        errorResponse = parseError(((ResponseBase) response).getContent());
//                    } else if (response instanceof String) {
//                        errorResponse = parseError((String) response);
//                    }
//                    if (errorResponse != null && errorResponse.errorCode >= SYSTEM_ERROR_CODE_START
//                            && errorResponse.errorCode <= SYSTEM_ERROR_CODE_END) {
////                        if (!request.isIgnoreResponse()) {
////                            postServerErrorMsg(context, errorResponse);
////                        }
//                        return null;
//                    }
//                }
                return response;
            }
        } catch (NetWorkException exception) {
            exception.printStackTrace();
//            if (!request.isIgnoreResponse()) {
//                sendRequestErrorLocal(context, exception);
//            }
            throw exception;
        }
        return null;
    }

    public static <T> T request(Context context, RequestBase<T> request) throws NetWorkException {
        // do some ugly things here.
//        synchronized (InternetUtil.class) {
//            int curr = getVersionCode(context);
//            int saved = SettingManager.getSavedVersionCode();
//            if (!SettingManager.getDeviceSigned() || saved < curr) {
//                int retry = 1;
//                // do we need retry?
//                while (retry > 0) {
//            AppInitRequest appInitRequest = AppInitRequest.build(context);
//            AppInitResponse appInitResponse = request(context, appInitRequest);
//                    if (appInitResponse != null && appInitResponse.result == 0) {
//                        SettingManager.setDeviceSigned(true);
//                        SettingManager.setSavedVersionCode(curr);
//                        break;
//                    }
//                    retry--;
//                }
//            }
        return requestInternal(context, request);
//        }
    }

    //thread-safe

    private static int getVersionCode(Context context) {
        if (sCurVersionCode == 0) {
            synchronized (InternetUtil.class) {
                if (sCurVersionCode == 0) {
                    try {
                        PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        sCurVersionCode = info.versionCode;
                    } catch (Exception e) {
                        sCurVersionCode = 0;
                    }
                }
            }
        }
        return sCurVersionCode;
    }

    private static void sendRequestErrorLocal(Context context, NetWorkException exception) {
        Intent i = new Intent();
        i.putExtra(EXTRA_ERROR_CODE, exception.getErrorCode());
        i.putExtra(EXTRA_ERROR_MSG, exception.getDeveloperExceptionMsg());
        i.setAction(ACTION_API_LOCAL_ERROR);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    private static void postServerErrorMsg(Context context, ServerErrorResponse response) {
        Intent i = new Intent();
        i.putExtra(EXTRA_ERROR_CODE, response.errorCode);
        i.putExtra(EXTRA_ERROR_MSG, response.errorMsg);
        i.setAction(ACTION_API_SERVER_ERROR);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    /**
     * Check whether the response is a failure response
     *
     * @param response The json response
     * @return Return a RRFailureResponse object is the response is a failure
     * response, or return null if request succeeds
     */
    private static ServerErrorResponse parseError(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        return JsonUtils.parse(response, ServerErrorResponse.class);
    }

}
