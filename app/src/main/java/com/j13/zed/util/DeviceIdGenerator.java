package com.j13.zed.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 数据统计获取device id
 */
public class DeviceIdGenerator {

    private static final String PREF_NAME = "xl_device_id";
    private static final String KEY_DEVICE_ID = "device_id1";
    private static final String DEFAULT_DEVICE_ID = "0000000000";

    public static String getDeviceId(Context context) {
        // 从本地获取device id
        String deviceId = getDeviceIdFromSP(context, KEY_DEVICE_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        String suffix;
        deviceId = DeviceUtils.getIMEI(context);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceUtils.getMacAddress(context);
            if (TextUtils.isEmpty(deviceId)) {
                // 使用默认值，添加后缀00
                deviceId = DEFAULT_DEVICE_ID;
                suffix = "00";
            } else {
                // 使用MAC地址值，添加后缀01
                suffix = "01";
            }
        } else {
            // 使用IMEI值，添加后缀10
            suffix = "10";
        }

        // md5
        deviceId = md5(deviceId);

        // 添加后缀
        deviceId = deviceId + suffix;
        
        // 保存device id到本地
        if (!"00".equals(suffix)) {
            saveDeviceIdToSP(context, KEY_DEVICE_ID, deviceId);
        }

        return deviceId;
    }

    private static String getDeviceIdFromSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    private static void saveDeviceIdToSP(Context context, String key, String deviceId) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, deviceId);
        editor.commit();
    }

    private static String md5(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] encryption = md.digest(origin.getBytes("UTF-8"));

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    result.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    result.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return origin;
    }

}
