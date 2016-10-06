package com.j13.zed.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.j13.zed.api.ApiConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class DeviceUtils {

    private static String MAC_AES = "";
    private static String IMEI_AES = "";
    private static String VERSION_NAME = "";
    public static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00";

    public static String getIMEI(Context context) {
        String imei = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            imei = telephonyManager.getDeviceId();
        }
        return imei != null ? imei : "";
    }

    public static String getMacAddress(Context context) {
        String mac = null;
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    mac = "";
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(Integer.toHexString(b & 0xFF) + ":");
                    }

                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    mac = sb.toString();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return DEFAULT_MAC_ADDRESS.equals(mac) ? "" : mac;
    }

    public static String getPhoneNumber(Context context) {
        String phone = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            phone = telephonyManager.getLine1Number();
        }
        return phone != null ? phone : "";
    }

    public static String getSimSerialNumber(Context context) {
        String simNumber = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            simNumber = telephonyManager.getSimSerialNumber();
        }
        return simNumber != null ? simNumber : "";
    }

    public static String getSimOperator(Context context) {
        String simOperator = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            simOperator = telephonyManager.getSimOperator();
        }
        return simOperator != null ? simOperator : "";
    }

    public static String getNetworkOperator(Context context) {
        String networkOperator = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            networkOperator = telephonyManager.getNetworkOperator();
        }
        return networkOperator != null ? networkOperator : "";
    }

    public static String getNetworkType(Context context) {
        if (!hasInternet(context)) {
            return "nonet";
        }
        if (isWifiConnected(context)) {
            return "wifi";
        }

        TelephonyManager telManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        int netType = telManager.getNetworkType();
        switch (netType) {
            case 1:
                //return "GPRS";
                return "2G";
            case 2:
                //return "EDGE";
                return "2G";
            case 4:
                //return "CDMA-IS95A/B";
                return "2G";
            case 5:
                //return "1xRTT";
                return "2G";
            case 6:
                //return "EvDo-rev.0";
                return "3G";
            case 7:
                //return "EvDo-rev.A";
                return "3G";
            case 12:
                //return "EvDo-rev.B";
                return "3G";
            case 8:
                //return "HSDPA";
                return "3G";
            case 3:
                //return "UMTS";
                return "3G";
            case 13:
                //return "LTE";
                return "4G";
            case 11:
                return "other";
            case 9:
                //return "HSUPA";
                return "3G";
            case 10:
                //return "HSPA";
                return "3G";
            case 15:
                //return "HSPAP";
                return "3G";
            default:
                return "other";
        }
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && networkInfo.isConnected());
    }

    public static String getPhoneIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String result = tm.getSubscriberId();
        return result == null ? "" : result;
    }

    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId != null ? androidId : "";
    }

    public static String getPhoneIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getVersionName(Context context) {
        if (TextUtils.isEmpty(VERSION_NAME)) {
            if (context != null) {
                try {
                    PackageManager packageManager = context.getPackageManager();
                    PackageInfo packInfo = packageManager.getPackageInfo(
                            context.getPackageName(), 0);
                    VERSION_NAME = packInfo.versionName;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return VERSION_NAME;
    }

    public static String getScreenSize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return String.format("%d*%d", metrics.widthPixels, metrics.heightPixels);
    }

    public static String getScreenDensity(Context context) {
        return String.valueOf(context.getResources().getDisplayMetrics().density);
    }

    public static String getScreenWidth(Context context) {
        return String.valueOf(context.getResources().getDisplayMetrics().widthPixels);
    }

    public static String getScreenHeight(Context context) {
        return String.valueOf(context.getResources().getDisplayMetrics().heightPixels);
    }

    public static String getMACAES(Context context) {
        if (TextUtils.isEmpty(MAC_AES)) {
            String mac = getMacAddress(context);
            if (TextUtils.isEmpty(mac))
                MAC_AES = "";
            else {
                String mac_fix = mac.replace(":", "").toUpperCase();
                MAC_AES = AESUtils.aesEncode(mac_fix, ApiConstant.SECRET_KEY);
            }
        }
        return MAC_AES;
    }

    public static String getIMEIAES(Context context) {
        if (TextUtils.isEmpty(IMEI_AES)) {
            String imei = getIMEI(context);
            if (TextUtils.isEmpty(imei))
                IMEI_AES = "";
            else
                IMEI_AES = AESUtils.aesEncode(imei, ApiConstant.SECRET_KEY);
        }
        return IMEI_AES;
    }

    public static String calcGcid(String path) {
        File file = new File(path);
        long fileSize = file.length();
        long blockSize = calcBlockSize(fileSize);

        byte[] buffer = new byte[8192];
        int len;
        int total = 0;
        MessageDigest gcidDigest;
        MessageDigest bcidDigest = null;
        InputStream is = null;
        try {
            gcidDigest = MessageDigest.getInstance("SHA-1");
            is = new FileInputStream(file);
            while ((len = is.read(buffer)) != -1) {
                if (bcidDigest == null) {
                    bcidDigest = MessageDigest.getInstance("SHA-1");
                }
                bcidDigest.update(buffer, 0, len);
                total += len;
                if (total >= blockSize) {
                    gcidDigest.update(bcidDigest.digest());
                    bcidDigest = null;
                    total = 0;
                }
            }
            if (total > 0) {
                gcidDigest.update(bcidDigest.digest());
            }

            return byteArrayToHexString(gcidDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static long calcBlockSize(long fileSize) {
        if (fileSize >= 0 && fileSize <= (128 << 20)) {
            return 256 << 10;
        } else if (fileSize > (128 << 20) && fileSize <= (256 << 20)) {
            return 512 << 10;
        } else if (fileSize > (256 << 20) && fileSize <= (512 << 20)) {
            return 1024 << 10;
        } else {
            return 2048 << 10;
        }
    }

    public static String joinString(List<String> list, char separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String sha1Hex(String origin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(origin.getBytes("UTF-8"));
            return byteArrayToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private final static String[] hexDigits = {
            "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n >>> 4 & 0xf;
        int d2 = n & 0xf;
        return hexDigits[d1] + hexDigits[d2];
    }
}
