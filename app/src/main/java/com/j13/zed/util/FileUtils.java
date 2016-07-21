package com.j13.zed.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FileUtils {

    private static final String DEFAULT_LANGUAGE = Locale.CHINA.toString().toLowerCase();

    public static String getFileExt(String filename) {
        String name = filename;
        int pos = filename.lastIndexOf('/');
        if (pos != -1) {
            name = filename.substring(pos + 1);
        }
        int dotPosition = name.lastIndexOf('.');
        if (dotPosition > 0) {
            return name.substring(dotPosition + 1);
        }
        return "";
    }

    /**
     * 根据语言环境解析name
     *
     * @param names zh_cn=xx&zh_tw=xx&=xx
     * @return
     */
    public static String getNameByLocale(String names) {
        if (names == null) {
            return "";
        }

        String[] nameArray = names.split("&");
        Map<String, String> langNameMap = new HashMap<String, String>();
        String defaultName = "";
        for (String entry : nameArray) {
            String[] pair = entry.split("=");
            if (pair.length == 2) {
                if (TextUtils.isEmpty(pair[0])) {
                    defaultName = pair[1];
                } else {
                    langNameMap.put(pair[0], pair[1]);
                }
            }
        }

        String name;
        String currentLanguage = Locale.getDefault().toString().toLowerCase();
        if (langNameMap.containsKey(currentLanguage)) {
            name = langNameMap.get(currentLanguage);
        } else {
            name = defaultName;
        }

        if (TextUtils.isEmpty(name) && langNameMap.containsKey(DEFAULT_LANGUAGE)) {
            name = langNameMap.get(DEFAULT_LANGUAGE);
        }
        return name;
    }

    public static String getAppName(Context context, String packName, String savedName) {
        return  getNameByLocale(savedName);
    }

    public static String getFormattedVideoLength(long time) {
//        long time = getVideoLength(context, path);
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}
