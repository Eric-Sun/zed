package com.j13.zed.api;

import android.os.Bundle;

import com.j13.zed.ZedApplication;
import com.j13.zed.util.DeviceIdGenerator;
import com.michael.corelib.internet.core.NetWorkException;
import com.michael.corelib.internet.core.RequestBase;
import com.michael.corelib.internet.core.RequestEntity;
import com.michael.corelib.internet.core.util.InternetStringUtils;

import java.net.URLEncoder;
import java.util.TreeMap;
import java.util.Vector;

public class ZedRequestBase<T> extends RequestBase<T> {

    private String mVersion;

    @Override
    protected Bundle getParams() throws NetWorkException {
        Bundle params = super.getParams();

        Class<?> c = this.getClass();

//        String method = params.getString(ApiConstant.KEY_METHOD);
//        if (TextUtils.isEmpty(method)) {
//            throw new RuntimeException("Method Name MUST NOT be NULL");
//        }

//        if (!method.startsWith("http://") && !method.startsWith("https://")) {
//            method = ApiConstant.getBaseMcpApiUrl() + "api/" + method.replace('.', '/');
//        }

//        if (c.isAnnotationPresent(UseHttps.class) && !method.startsWith("https://")) {
//            method = method.replace("http", "https");
//            method = method.replaceAll(":(\\d+)/", "/");
//        }

        String httpMethod = params.getString(ApiConstant.KEY_HTTP_METHOD);
        String act = params.getString(ApiConstant.KEY_METHOD);
        params.remove(ApiConstant.KEY_HTTP_METHOD);
        params.remove(ApiConstant.KEY_METHOD);

//        params.putString("appId", ApiConstant.APP_ID);
//        if (TextUtils.isEmpty(mVersion)) {
//            mVersion = "1.0";
//        }
//        params.putString("v", mVersion);
//        params.putString("callId", String.valueOf(System.currentTimeMillis()));
//        params.putString("appVersion", CommonParam.RA2_VERSION);
//        params.putString("gz", "1");
        params.putString("act", act);
        params.putString("deviceId", DeviceIdGenerator.getDeviceId(ZedApplication.getInstance()));
//        params.putString("sig", getSig(params, ApiConstant.SECRET_KEY));

        params.putString(ApiConstant.KEY_METHOD, ApiConstant.JAX_RELEASE_URL);
        params.putString(ApiConstant.KEY_HTTP_METHOD, httpMethod);

        return params;
    }

    private String getSig(Bundle params, String secretKey) {
        if (params.size() == 0) {
            return "";
        }

        TreeMap<String, String> sortParams = new TreeMap<String, String>();
        for (String key : params.keySet()) {
            sortParams.put(key, params.getString(key));
        }

        Vector<String> vecSig = new Vector<String>();
        for (String key : sortParams.keySet()) {
            String value = sortParams.get(key);
            vecSig.add(key + "=" + value);
        }

        String[] nameValuePairs = new String[vecSig.size()];
        vecSig.toArray(nameValuePairs);

        for (int i = 0; i < nameValuePairs.length; i++) {
            for (int j = nameValuePairs.length - 1; j > i; j--) {
                if (nameValuePairs[j].compareTo(nameValuePairs[j - 1]) < 0) {
                    String temp = nameValuePairs[j];
                    nameValuePairs[j] = nameValuePairs[j - 1];
                    nameValuePairs[j - 1] = temp;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameValuePairs.length; i++) {
            sb.append(nameValuePairs[i]);
        }
        sb.append(secretKey);

        return InternetStringUtils.MD5Encode(sb.toString());
    }

    @Override
    public RequestEntity getRequestEntity() throws NetWorkException {
        RequestEntity entity = super.getRequestEntity();
        entity.setContentType("application/x-www-form-urlencoded");
        return entity;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }
}
