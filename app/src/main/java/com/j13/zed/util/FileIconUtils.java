package com.j13.zed.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.j13.zed.R;

import java.util.HashMap;


public class FileIconUtils {

    private static final String TYPE_APK = "apk";
    private static final String LOG_TAG = "FileIconHelper";
    private static HashMap<String, Integer> sFileExtToIcons = new HashMap<String, Integer>();

    static {
        /*addItem(new String[]{
                "mp3"
        }, R.drawable.file_icon_mp3);
        addItem(new String[]{
                "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[]{
                "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[]{
                "mid"
        }, R.drawable.file_icon_mid);*/
        addItem(new String[]{
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3g2", "3gpp2", "asf",
                "flv", "mkv", "vob", "ts", "f4v", "rm", "mov", "rmvb"
        }, R.drawable.file_icon_video);
        /*addItem(new String[]{
                "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
        }, R.drawable.file_icon_picture);*/
        /*addItem(new String[]{
                "txt", "log", "ini", "lrc"
        }, R.drawable.file_icon_txt);
        addItem(new String[]{
                "doc", "docx"
        }, R.drawable.file_icon_doc);
        addItem(new String[]{
                "ppt", "pptx"
        }, R.drawable.file_icon_ppt);
        addItem(new String[]{
                "xls", "xlsx"
        }, R.drawable.file_icon_xls);
        addItem(new String[]{
                "wps"
        }, R.drawable.file_icon_wps);
        addItem(new String[]{
                "pps"
        }, R.drawable.file_icon_pps);
        addItem(new String[]{
                "et"
        }, R.drawable.file_icon_et);
        addItem(new String[]{
                "wpt"
        }, R.drawable.file_icon_wpt);
        addItem(new String[]{
                "ett"
        }, R.drawable.file_icon_ett);
        addItem(new String[]{
                "dps"
        }, R.drawable.file_icon_dps);
        addItem(new String[]{
                "dpt"
        }, R.drawable.file_icon_dpt);
        addItem(new String[]{
                "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[]{
                "zip"
        }, R.drawable.file_icon_zip);
        addItem(new String[]{
                "mtz"
        }, R.drawable.file_icon_theme);
        addItem(new String[]{
                "rar"
        }, R.drawable.file_icon_rar);
        addItem(new String[]{
                "apk"
        }, R.drawable.file_icon_apk);
        addItem(new String[]{
                "amr"
        }, R.drawable.file_icon_amr);
        addItem(new String[]{
                "vcf"
        }, R.drawable.file_icon_vcf);*/
        /*addItem(new String[]{
                "flac"
        }, R.drawable.file_icon_flac);
        addItem(new String[]{
                "aac"
        }, R.drawable.file_icon_aac);
        addItem(new String[]{
                "ape"
        }, R.drawable.file_icon_ape);
        addItem(new String[]{
                "m4a"
        }, R.drawable.file_icon_m4a);
        addItem(new String[]{
                "ogg"
        }, R.drawable.file_icon_ogg);
        addItem(new String[]{
                "audio"
        }, R.drawable.file_icon_audio);
        addItem(new String[]{
                "html"
        }, R.drawable.file_icon_html);
        addItem(new String[]{
                "xml"
        }, R.drawable.file_icon_xml);*/
        addItem(new String[]{
                "3gpp"
        }, R.drawable.file_icon_3gpp);
    }

    /**
     * Only for extends, please don't instantiate this class.
     *
     * @throws InstantiationException always when this class is instantiated.
     */
    protected FileIconUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                sFileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }

    private static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    private static Drawable getApkIcon(Context context, String path) {
        // TODO use IconCustomizer instead of icon from APK.
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            if (appInfo != null) {
                try {
                    appInfo.publicSourceDir = path;
                    return pm.getApplicationIcon(appInfo);
                } catch (OutOfMemoryError e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        }

        return context.getResources().getDrawable(R.drawable.file_icon_default);
    }

    /**
     * Get the default file icon drawable id from the extension of the file.
     *
     * @param ext The extension of the file
     * @return The resource drawable id of the extension.
     */
    public static int getFileIconId(String ext) {
        Integer i = sFileExtToIcons.get(ext.toLowerCase());
        return i == null ? R.drawable.file_icon_default : i;
    }

    /**
     * Get the default file icon of the file. If it is an apk, will resolve the package information
     * to get the icon of the apk package.
     *
     * @param context
     * @param fileFullPath Full path of the file.
     * @return A drawable of the file.
     */
    public static Drawable getFileIcon(Context context, String fileFullPath) {
        String ext = getExtFromFilename(fileFullPath);
        return ext.equals(TYPE_APK) ?
                getApkIcon(context, fileFullPath) :
                context.getResources().getDrawable(getFileIconId(ext));
    }

}
