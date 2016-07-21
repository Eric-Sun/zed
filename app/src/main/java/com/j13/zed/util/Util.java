
package com.j13.zed.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Util {
    //constants from android.content.MiuiIntent
    public static final String ACTION_GARBAGE_CLEANUP = "miui.intent.action.GARBAGE_CLEANUP";

    private static String ANDROID_SECURE = "/mnt/sdcard/.android_secure";

    private static final String LOG_TAG = "Util";

    public static final String EXTERNAL_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String THUMBNAIL_PATH = EXTERNAL_DIR_PATH + "/DCIM/.thumbnails";

    private static final int MAX_FILENAME_LENGTH = 50;

    public static final String EXTRA_STARTING_WINDOW_LABEL = ":miui:starting_window_label";

    private static String sIMEI;

    public static final String EXTRA_ENTER_CLEAN_WAY = "enter_homepage_way";
    public static final String ENTER_CLEAN_HOMEPAGE = "00003";
    public static final String ENTER_CLEAN_OPERATION = "00022";
    public static final String ENTER_CLEAN_HINT = "00019";

    public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";

    public static final String ACTION_PICK_FOLDER = "miui.intent.action.PICK_FOLDER";
    public static final String ACTION_PICK_MULTIPLE = "miui.intent.action.PICK_MULTIPLE";
    public static final String ACTION_PICK_MULTIPLE_NO_FOLDER = "miui.intent.action.PICK_MULTIPLE_NO_FOLDER";

    /**
     * MiDrive fragment argument to determine add to MiDriveActivity or FileExplorerActivity
     */
    public static final String ARGUMENT_SEND_TO_MIDRIVE = "send_to_midrive";

    /**
     * The extra to specify the tab index, see:
     * {@link Util#CATEGORY_TAB_INDEX},
     * {@link Util#SDCARD_TAB_INDEX},
     * {@link Util#MIDRIVE_TAB_INDEX}
     */
    public static final String EXTRA_TAB_INDEX_TYPE = "explorer_tab";

    /**
     * The path of the folder, include: sdcard, kuaipan
     */
    public static final String EXTRA_PATH_TYPE = "explorer_path";

    public static final String EXTRA_DIRECTORY = "current_directory";

    public static final String EXTRA_INTERNAL_FORWARD = "internal_forward";

    /**
     * 文件管理器Sdcard路径
     */
    public static final String FILE_EXPLORER_PATH = Environment.getExternalStorageDirectory() + "/FileExplorer";

    public static final String DOWNLOAD_PATH = "Download";

    public static final String WECHAT_VIDEO = "android.intent.action.WECHAT_VIDEO";

    public static final String MIDROP_PACKAGE_NAME = "com.xiaomi.midrop";

    /*
     * Return true if the filename length > 255 bytes
     */
    public static boolean isFileNameTooLong(CharSequence filename) {
        if (null != filename && filename.length() > MAX_FILENAME_LENGTH) {
            return true;
        }
        return false;
    }

    public static boolean isSDCardReady() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // if path1 contains path2
    /*public static boolean containsPath(String path1, String path2) {
        String path = path2;
        while (path != null) {
            if (path.equalsIgnoreCase(path1))
                return true;

            if (path.equals(GlobalConsts.ROOT_PATH))
                break;
            path = new File(path).getParent();
        }

        return false;
    }*/

    public static String makePath(String path1, String path2) {
        if (path1.endsWith(File.separator) && path2.startsWith(File.separator))
            return path1 + path2.substring(1);
        if (path1.endsWith(File.separator) || path2.startsWith(File.separator)) {
            return path1 + path2;
        }
        return path1 + File.separator + path2;
    }

    public static String getSdDirectory() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static boolean isNormalFile(String fullName) {
        return !fullName.equals(ANDROID_SECURE);
    }

    /*public static FileInfo getFileInfo(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null : Util
                .getFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH));
    }*/


    /*public static FileInfo getFileInfo(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File lFile = new File(filePath);
        if (!lFile.exists())
            return null;

        FileInfo lFileInfo = new FileInfo();
        lFileInfo.canRead = lFile.canRead();
        lFileInfo.canWrite = lFile.canWrite();
        lFileInfo.isHidden = !shouldShowSystemFile(lFile);
        lFileInfo.fileName = Util.getNameFromFilepath(filePath);
        lFileInfo.modifiedDate = lFile.lastModified();
        lFileInfo.isDirectory = lFile.isDirectory();
        lFileInfo.filePath = filePath;
        lFileInfo.fileSize = lFile.length();
        return lFileInfo;
    }*/

    /*public static FileInfo getFileInfo(File f, FilenameFilter filter, boolean isInPrivateFolder) {
        FileInfo lFileInfo = new FileInfo();
        lFileInfo.canRead = f.canRead();
        lFileInfo.canWrite = f.canWrite();
        lFileInfo.isHidden = isInPrivateFolder ? !shouldShowFile(f, true) : !shouldShowSystemFile(f);
        lFileInfo.fileName = f.getName();
        lFileInfo.modifiedDate = f.lastModified();
        lFileInfo.isDirectory = f.isDirectory();
        lFileInfo.filePath = f.getAbsolutePath();
        if (lFileInfo.isDirectory && !lFileInfo.isHidden) {
            int lCount = 0;
            File[] files = f.listFiles(filter);
            if (files != null) {
                for (File file : files) {
                    Boolean isHidden = isInPrivateFolder ? !shouldShowFile(file, true) : !shouldShowSystemFile(file);
                    if (!isHidden) {
                        lCount++;
                    }
                }
            }
            lFileInfo.count = lCount;
        } else {
            lFileInfo.fileSize = f.length();
        }
        return lFileInfo;
    }

    public static FileInfo getInvalidFileInfo() {
        FileInfo lFileInfo = new FileInfo();
        lFileInfo.canRead = false;
        lFileInfo.canWrite = false;
        lFileInfo.isHidden = false;
        lFileInfo.fileName = "";
        lFileInfo.modifiedDate = 0;
        lFileInfo.isDirectory = false;
        lFileInfo.filePath = "";
        lFileInfo.fileSize = 0;
        return lFileInfo;
    }

    public static FileInfo getFileInfo(SmbFile f) throws SmbException {
        FileInfo lFileInfo = new FileInfo();
        String fileName = f.getName();
        if (fileName.endsWith("/")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        lFileInfo.fileName = fileName;
        lFileInfo.modifiedDate = f.lastModified();
        lFileInfo.isDirectory = f.isDirectory();
        lFileInfo.filePath = f.getPath();
        if (!lFileInfo.isDirectory) {
            lFileInfo.fileSize = f.length();
        } else {
            try {
                lFileInfo.count = new SmbFile(f.getPath() + "/").list().length;
            } catch (MalformedURLException e) {
            } catch (SmbAuthException e) {
            } catch (SmbException e) {
                e.printStackTrace();
            }
        }

        lFileInfo.fileType = FileInfo.FILE_IN_SMB;

        try {
            lFileInfo.canRead = f.canRead();
            lFileInfo.canWrite = f.canWrite();
            lFileInfo.isHidden = f.isHidden();
        } catch (SmbAuthException e) {
        }
        return lFileInfo;
    }*/

    public static Drawable getApkIcon(Context context, String path) {
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
        return null;
    }

    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return filename;
    }

    public static String getPathFromFilepath(String filepath) {
        String path = filepath;
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        int pos = path.lastIndexOf('/');
        if (pos != -1) {
            return path.substring(0, pos);
        }
        return "";
    }

    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    public static String removeTrailingSeparatorFromPath(String path) {
        if (path.charAt(path.length() - 1) == File.separatorChar) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    /*public static String copyFile(File file, File destFile) {
        if (!file.exists() || file.isDirectory()) {
            Log.v(LOG_TAG, "copyFile: file not exist or is directory, " + file.getAbsolutePath());
            return null;
        }

        try {
            FileInputStream fi = null;
            FileOutputStream fo = null;
            try {
                fi = new FileInputStream(file);
                File destPlace = new File(destFile.getParent());
                if (!destPlace.exists()) {
                    if (!destPlace.mkdirs())
                        return null;
                }

                if (!destFile.createNewFile())
                    return null;

                fo = new FileOutputStream(destFile);
                int count = 10240;
                byte[] buffer = new byte[count];
                int read;
                while ((read = fi.read(buffer, 0, count)) != -1) {
                    if (ProgressManager.getInstance().isCancelled()) {
                        destFile.delete();
                        return "";
                    }
                    fo.write(buffer, 0, read);
                    ProgressManager.getInstance().increaseTotalSizeBy(read);
                }
                return destFile.getPath();
            } finally {
                if (fi != null)
                    fi.close();
                if (fo != null)
                    fo.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "copyFile: file not found, " + file.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "copyFile: " + e.toString());
        }

        return null;
    }*/

    // return new file path if successful, or return null
    /*public static String copyFile(String src, File destFile) {
        return copyFile(new File(src), destFile);
    }*/

    // does not include sd card folder
    /*private static String[] SysFileDirs = new String[]{
            "/miren_browser/imagecaches", DirOperationUtil.PRIVATE_FOLDER_PATH
    };*/

    /*public static boolean shouldShowFile(String path, boolean isInPrivateFolder) {
        return shouldShowFile(new File(path), isInPrivateFolder);
    }*/

    /*public static boolean shouldShowSystemFile(File file) {
        return !isSystemFile(file) && shouldShowFile(file, false);
    }*/

    /*public static boolean isSystemFile(File file) {
        for (String s : SysFileDirs) {
            if (file.getPath().contains(s))
                return true;
        }
        return false;
    }*/

    /*public static boolean shouldShowFile(File file, boolean isInPrivateFolder) {
        if ((isInPrivateFolder &&
                (file.getName().startsWith(FileOperationUtil.HEADER_PREFIX)
                        || file.getName().startsWith(FileOperationUtil.LOCK_PREFIX)
                        || file.getName().startsWith(FileOperationUtil.THUMB_PREFIX)
                        || file.getName().startsWith(FileConstant.FILE_NOMEDIA)))) {
            return false;
        }
        boolean show = ShowHiddenFileInstance.instance().getShowDotAndHiddenFiles();
        return show || !(file.isHidden() || file.getName().startsWith("."));
    }*/

    /*public static boolean shouldShowSMBFile(SmbFile file) throws SmbException {
        boolean show = ShowHiddenFileInstance.instance().getShowDotAndHiddenFiles();
        if (show)
            return true;

        if (file.isHidden())
            return false;

        if (file.getName().startsWith("."))
            return false;

        String sdFolder = getSdDirectory();
        for (String s : SysFileDirs) {
            if (file.getPath().startsWith(makePath(sdFolder, s)))
                return false;
        }
        return true;
    }

    public static ArrayList<FavoriteItem> getDefaultFavorites(Context context) {
        ArrayList<FavoriteItem> list = new ArrayList<FavoriteItem>();
        list.add(new FavoriteItem(context.getString(R.string.favorite_photo), makePath(getSdDirectory(), "DCIM/Camera")));
        list.add(new FavoriteItem(context.getString(R.string.favorite_screen_cap), makePath(getSdDirectory(), "MIUI/screen_cap")));
        list.add(new FavoriteItem(context.getString(R.string.favorite_ringtone), makePath(getSdDirectory(), "MIUI/ringtone")));
        return list;
    }*/

    public static boolean setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    public static boolean setText(View view, int id, int text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    // comma separated number
    public static String convertNumber(long number) {
        return String.format("%,d", number);
    }

   /* public static void showNotification(Context context, Intent intent, String title, String body, int drawableId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(drawableId, body, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        if (intent == null) {
            // FIXEME: category tab is disabled
            intent = new Intent(context, FileFragment.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notification.setLatestEventInfo(context, title, body, contentIntent);
        manager.notify(drawableId, notification);
    }*/

    public static String formatDateString(Context context, long time) {
        DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat
                .getTimeFormat(context);
        Date date = new Date(time);
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        {
            add("text/plain");
            add("application/pdf");
            add("application/msword");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            add("application/vnd.ms-powerpoint");
            add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            add("application/vnd.ms-excel");
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            add("application/wps");
            add("application/wpt");
            add("application/et");
            add("application/ett");
            add("application/dps");
            add("application/dpt");
        }
    };

    public static int getDisplayWidth(Context context) {
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
    }

    public static final String sZipFileMimeType = "application/zip";

    public static final int NAVIGATION_TAB_INDEX = 0;
    public static final int CATEGORY_TAB_INDEX = 1;
    public static final int SDCARD_TAB_INDEX = 2;
    public static final int UNKNOWN_PATH_TAB_INDEX = -1;
    public static final int MIDRIVE_TAB_INDEX = 3;
    public static final int FTP_TAB_INDEX = 4;
    public static final int FILE_CLEAN_TAB_INDEX = 5;
    public static final int MI_ROUTER_TAB_INDEX = 6;
    public static final int USB_TAB_INDEX = 7;
    public static final int APP_TAG_INDEX = 8;
    public static final int SEARCH_TAG_INDEX = 9;
    public static final int PRIVATE_FOLDER_INDEX = 10;
    public static final int FAVORITE_TAB_INDEX = 11;

    public static final int sFragmentBaseTabIndex[] = new int[]{
            NAVIGATION_TAB_INDEX, CATEGORY_TAB_INDEX, SDCARD_TAB_INDEX
    };

    public static MenuItem makeMenuItemVisible(Menu menu, int itemId, boolean visible) {
        MenuItem item = menu.findItem(itemId);
        if (item != null) {
            item.setVisible(visible);
        }
        return item;
    }

    /*public static int getFileSortMethod(Context context, int tabIndex) {
        return FileSortHelper.getSortMethodFromPreference(context, tabIndex);
    }*/

    public static boolean isInSameVolume(String pathA, String pathB) {
        if (TextUtils.isEmpty(pathA) || TextUtils.isEmpty(pathB)) {
            return false;
        }

        String[] strs = pathA.split(File.separator);
        if (strs != null && strs.length > 2) {
            StringBuilder sa = new StringBuilder(pathB).append(File.separator);
            StringBuilder sb = new StringBuilder(File.separator).append(strs[1])
                    .append(File.separator).append(strs[2]).append(File.separator);
            return sa.toString().startsWith(sb.toString());
        }
        return false;
    }

    /*public static boolean isInVolume(Context context, String path) {
        ArrayList<StorageInfo> volumes = StorageHelper.getInstance(context).getMountVolumeList();
        for (StorageInfo v : volumes) {
            if (path.startsWith(v.getPath())) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Check port number pattern: 1. digits only; 2. more or equal than 0 and less than 65535.
     * See {@link java.net.InetSocketAddress#InetSocketAddress(java.net.InetAddress, int)}
     */
    public static boolean checkPortNumPattern(String portNum) {
        if (TextUtils.isEmpty(portNum) || !TextUtils.isDigitsOnly(portNum)) {
            return false;
        }

        int port = 0;
        try {
            port = Integer.parseInt(portNum);
        } catch (Exception e) {
            port = 0;
        }
        return (port >= 1024 && port <= 65535);
    }

    /**
     * Check password pattern: 4~16 chars, contains digits, alphabet letters, case sensitive
     */
    public static boolean checkUserNamePasswordPattern(String string) {
        if (TextUtils.isEmpty(string) || string.length() < 4 || string.length() > 16) {
            return false;
        }
        return string.matches("[a-zA-Z0-9]+");
    }

    /**
     * Copied from Android Formatter, make MB as the minimal unit
     */
    public static String formatFileSize(long number) {
        float result = number;
        String suffix = "MB";
        result /= (1024 * 1024);
        if (result > 900) {
            suffix = "GB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "TB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "PB";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.0f", result);
        }
        return value + " " + suffix;
    }

    /*public static String getReadablePath(Context context, String path) {
        StorageInfo storageInfo = getMountedStorageBySubPath(context, path);
        if (storageInfo == null || storageInfo.getPath() == null) {
            return path;
        }
        return path.replace(storageInfo.getPath(), StorageHelper.getInstance(context).getVolumeDescription(storageInfo));
    }*/

    public static String getRelativePathAtVolume(String volume, String path) {
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(volume)) {
            if (path.indexOf(volume) >= 0) {
                return path.substring(volume.length());
            }
        }
        return path;
    }

    /*public static String getRelativePath(Context context, String path) {
        String volumePath = Util.getStoragePathBySubPath(context, path);
        if (volumePath != null) {
            String relativePath = getRelativePathAtVolume(volumePath, path);
            if (relativePath != null && relativePath.startsWith("/")) {
                return relativePath.substring(1);
            }
            return relativePath;
        }
        return "";
    }*/

    /*public static boolean isDownloadPath(Context context, String path) {
        return DOWNLOAD_PATH.equalsIgnoreCase(getRelativePath(context, path));
    }*/

    /*public static boolean isInUsbVolume(Context context, String path) {
        StorageInfo storageInfo = getMountedStorageBySubPath(context, path);
        return storageInfo != null && StorageHelper.getInstance(context).isUsbVolume(storageInfo);
    }

    public static boolean isInInternalVolume(Context context, String path) {
        StorageInfo storageInfo = getMountedStorageBySubPath(context, path);
        return storageInfo != null && storageInfo.isPrimary();
    }

    public static boolean isInVisibleVolume(Context context, String path) {
        StorageInfo storageInfo = getMountedStorageBySubPath(context, path);
        return storageInfo != null && storageInfo.isVisible();
    }
*/

    /*public static void showSpaceNotEnoughDialog(final Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.no_enough_storage_title)
                .setMessage(R.string.no_enough_storage_indication)
                .setNegativeButton(R.string.confirm_know, null)
                .setPositiveButton(R.string.confirm_clean, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enterClean(context, ENTER_CLEAN_OPERATION);
                    }
                })
                .create();
        dialog.show();
    }

    public static boolean isSpaceNotEnough(Context context) {
        StorageHelper storageHelper = StorageHelper.getInstance(context);
        StorageHelper.SDCardInfo info = storageHelper.getStorageInfoForVolume(storageHelper.getPrimaryStorageVolume());
        if (info != null) {
            return info.free < (2 * 1024 * 1024 * 1024l) && (info.free * 10 < info.total);
        }
        return false;
    }*/

    public static void enterClean(Context context, String way) {
        try {
            Intent intent = new Intent(ACTION_GARBAGE_CLEANUP);
            intent.putExtra(EXTRA_ENTER_CLEAN_WAY, way);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scroll to sdcard tab if necessary
     */
   /* public static void scrollToSdcardTab(Activity activity, String path) {
        Intent intent = new Intent();
        String action = activity.getIntent().getAction();
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
        intent.setClass(activity, FileActivity.class);
        if (path != null && new File(path).exists()) {
            intent.setData(Uri.fromFile(new File(path)));
        } else {
            intent.putExtra(FileActivity.EXTRA_DEVICE_INDEX, Util.SDCARD_TAB_INDEX);
        }
        intent.putExtra(FileActivity.EXTRA_INNER_CALL, true);
        activity.startActivityForResult(intent, FileActivity.REQUEST_PICK_FILES);
    }*/

    /*public static void pickFolder(Activity activity, int titleRes, int btnNameRes, boolean showRouter, boolean showMiDrive) {
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Util.ACTION_PICK_FOLDER);
        intent.putExtra(FileActivity.EXTRA_INNER_CALL, true);
        intent.putExtra(FileActivity.EXTRA_TITLE, activity.getString(titleRes));
        intent.putExtra(FileActivity.EXTRA_PICK_BUTTON_NAME, activity.getString(btnNameRes));
        intent.putExtra(FileActivity.EXTRA_PICK_FROM_ROUTER, showRouter);
        intent.putExtra(FileActivity.EXTRA_PICK_FROM_MI_DRIVE, showMiDrive);
        activity.startActivityForResult(intent, FileActivity.REQUEST_PICK_FOLDER);
    }*/

    public static final String ACTION_START_KUAIPAN = "miui.intent.action.START_KUAIPAN";

    /**
     * Create kuaipan short cut
     */
    /*public static void createKuaipanShortCut(Context context) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.kuaipan_label));
        final Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.kuaipan_icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        Intent i = new Intent(ACTION_START_KUAIPAN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.setComponent(new ComponentName(context, FileExplorerTabActivity.class));
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
        context.sendBroadcast(shortcutIntent);
    }*/

    public static String getIMEI(Context context) {
        if (sIMEI == null) {
            String deviceId = null;
           /* try {
                deviceId = CloudManager.getDeviceId(context);
            } catch (IllegalDeviceException e) {
                e.printStackTrace();
            }*/

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = "";
            }
            sIMEI = deviceId;
        }

        return sIMEI;
    }

    /*public static View getBackView(ActionBar actionBar) {
        if (actionBar != null) {
            View customView = actionBar.getCustomView();
            if (customView != null) {
                if (Build.IS_TABLET) {
                    return customView.findViewById(R.id.title);
                } else {
                    return customView.findViewById(R.id.title);
                }
            }
        }
        return null;
    }*/

   /* public static void setHomeClickListener(View.OnClickListener listener, ActionBar actionBar) {
        View home = Util.getBackView(actionBar);
        if (home != null) {
            home.setOnClickListener(listener);
            home.setBackgroundColor(0xff0000);
        }
    }*/

    public static interface OnDoubleTapListener {
        public boolean onDoubleTap();
    }

    public static void setOnDoubleTapListener(Context context, View view, final OnDoubleTapListener listener) {
        final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return listener.onDoubleTap();
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    /*public static File getDestFile(Context context, String path, String fileName, boolean isDir, boolean checkInDB) {

        String destPath = Util.makePath(path, fileName);
        File destFile = new File(destPath);
        int i = 1;

        if (isDir) {
            while (destFile.exists()) {
                destPath = Util.makePath(path, fileName + " " + i++);
                destFile = new File(destPath);
            }
        } else {
            while (destFile.exists() || (checkInDB && PrivateDBHelper.isDisplayPathExist(destPath))) {
                if (fileName.endsWith(FileOperationUtil.POSTFIX_NEW) || fileName.endsWith(FileOperationUtil.POSTFIX)) {
                    String realName = EncryptUtil.getRealName(fileName);
                    String destName = Util.getNameFromFilename(realName) + " " + i++;
                    String ext = FileUtils.getFileExt(realName);
                    if (!TextUtils.isEmpty(ext)) {
                        destName += ("." + ext);
                    }
                    destPath = Util.makePath(path, EncryptUtil.getEncryptedName(context, destName));
                    destFile = new File(destPath);
                } else {
                    String destName = Util.getNameFromFilename(fileName) + " " + i++;
                    String ext = FileUtils.getFileExt(fileName);
                    if (!TextUtils.isEmpty(ext)) {
                        destName += ("." + ext);
                    }
                    destPath = Util.makePath(path, destName);
                    destFile = new File(destPath);
                }
            }
        }
        return destFile;
    }*/

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }

        // The directory is now empty so now it can be smoked
        return dir.delete();
    }

    public static boolean mkdirs(File dir) {
        if (dir.exists() && !dir.isDirectory()) {
            dir.delete();
        }
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    public static boolean addNoMedia(File dir) {
        File file = new File(dir, ".nomedia");
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }
    }

    /*public static boolean isSupportMiDrive() {
        return !MidConfig.isInternalBuild();
    }

    public static boolean isCtaBuild() {
        return miui.os.Build.IS_CTA_BUILD;
    }
*/
    public static void textEmphasize(TextView tv, String original, String interest, int color) {
        String orig_lower = original.toLowerCase();
        String interest_lower = interest.toLowerCase();
        if (!orig_lower.contains(interest_lower) || TextUtils.isEmpty(interest)) {
            tv.setText(original);
            return;
        }

        //user lower case to get pos
        List<Pos> poslist = new ArrayList<Pos>();
        int idx;
        int start = 0;
        int interest_len = interest_lower.length();
        do {
            idx = orig_lower.indexOf(interest_lower, start);
            if (idx == -1)
                break;
            poslist.add(new Pos(idx, idx + interest_len));
            start = idx + interest_len;
        } while (start <= orig_lower.length());

        SpannableStringBuilder style = new SpannableStringBuilder(original);
        for (Pos pos : poslist) {
            if (pos.getStart() != pos.getEnd())
                style.setSpan(new ForegroundColorSpan(color), pos.getStart(), pos.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(style);
//        Pattern pattern = Pattern.compile(interest, Pattern.CASE_INSENSITIVE);
//        String[] splits = original.split(pattern.pattern());
//        SpannableStringBuilder builder = new SpannableStringBuilder();
////        SpannableString text = new SpannableString(interest);
////        text.setSpan(new ForegroundColorSpan(Color.RED), 0, interest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        for (int i=0; i!= splits.length; ++i) {
//            builder.append(splits[i]);
//            if(i != splits.length -1) {
//                SpannableString text = new SpannableString(interest);
//                text.setSpan(new ForegroundColorSpan(Color.RED), 0, interest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.append(text);
//            }
//        }
//        if(orig_lower.endsWith(interest_lower)) {
//            SpannableString text = new SpannableString(interest);
//            text.setSpan(new ForegroundColorSpan(Color.RED), 0, interest.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.append(text);
//        }

//        int start = orig_lower.indexOf(interest_lower);
//        int end = orig_lower.indexOf(interest_lower) + interest_lower.length();

//        SpannableStringBuilder style=new SpannableStringBuilder(original);
//        style.setSpan(new ForegroundColorSpan(Color.RED),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        tv.setText(builder);
    }

    private static class Pos {
        public int start;
        public int end;

        private Pos(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    /*public static HashSet<String> getFileInfoFavListInLowerCase(Context context, List<FileInfo> fileInfos) {
        HashSet<String> favList = new HashSet<String>();
        List<String> pathList = new ArrayList<String>();
        List<FileInfo> list = new ArrayList<FileInfo>(fileInfos);
        int index = 0;
        for (FileInfo info : list) {
            pathList.add(info.filePath.toLowerCase());
            index++;
            if (index > 200) {
                index = 0;
                favList.addAll(FavoriteDatabaseHelper.getInstance(context).getFavListInLowerCase(pathList));
                pathList.clear();
            }
        }
        favList.addAll(FavoriteDatabaseHelper.getInstance(context).getFavListInLowerCase(pathList));
        return favList;
    }

    public static HashSet<String> getFavListInLowerCase(Context context, List<FileWithExt> fileWithExts) {
        HashSet<String> favList = new HashSet<String>();
        List<String> pathList = new ArrayList<String>();
        List<FileWithExt> list = new ArrayList<FileWithExt>(fileWithExts);
        int index = 0;
        for (FileWithExt info : list) {
            pathList.add(info.filePath.toLowerCase());
            index++;
            if (index > 200) {
                index = 0;
                favList.addAll(FavoriteDatabaseHelper.getInstance(context).getFavListInLowerCase(pathList));
                pathList.clear();
            }
        }
        favList.addAll(FavoriteDatabaseHelper.getInstance(context).getFavListInLowerCase(pathList));
        return favList;
    }

    public static HashSet<String> getFavInListInLowerCase(Context context, ArrayList<FavoriteItem> defaultFavs) {
        HashSet<String> favList = new HashSet<String>();
        List<String> pathList = new ArrayList<String>();
        List<FavoriteItem> list = new ArrayList<FavoriteItem>(defaultFavs);
        int index = 0;
        for (FavoriteItem info : list) {
            pathList.add(info.location.toLowerCase());
            index++;
            if (index > 200) {
                index = 0;
                favList.addAll(FavoriteDatabaseHelper.getInstance(context).getFavListInLowerCase(pathList));
                pathList.clear();
            }
        }
        favList.addAll(FavoriteDatabaseHelper.getInstance(context).getFavListInLowerCase(pathList));
        return favList;
    }

    public static void removeFavList(Context context, ArrayList<FileInfo> favoriteItems, boolean isDelete) {
        StringBuilder sbFile = new StringBuilder("(");
        StringBuilder sbDir = new StringBuilder("(");
        int indexFile = 0;
        int indexDir = 0;
        for (FileInfo file : favoriteItems) {
            file.isFav = false;

            if (isDelete && file.isDirectory) {
                if (sbDir.length() != 1) {
                    sbDir.append(" or ");
                }
                indexDir++;
                String dirPath = file.filePath.replace("'", "''");
                if (!dirPath.endsWith("/")) {
                    dirPath = dirPath + "/";
                }
                sbDir.append("lower(").append(FavoriteDatabaseHelper.FIELD_LOCATION).append(") like '").append(dirPath).append("%'");
                if (indexDir > 200) {
                    indexDir = 0;
                    sbDir.append(")");
                    FavoriteDatabaseHelper.getInstance(context).deleteDirList(sbDir.toString());
                    sbDir = new StringBuilder("(");
                }
            }
            if (sbFile.length() != 1) {
                sbFile.append(",");
            }
            indexFile++;
            String filePath = file.filePath.replace("'", "''");
            sbFile.append("'").append(filePath).append("'");
            if (indexFile > 200) {
                indexFile = 0;
                sbFile.append(")");
                FavoriteDatabaseHelper.getInstance(context).deleteFileList(sbFile.toString());
                sbFile = new StringBuilder("(");
            }
        }
        sbFile.append(")");
        sbDir.append(")");
        FavoriteDatabaseHelper.getInstance(context).deleteFileList(sbFile.toString());
        FavoriteDatabaseHelper.getInstance(context).deleteDirList(sbDir.toString());

    }

    public static void removeFavList(Context context, List<FileWithExt> fileWithExtList, boolean isDelete) {
        StringBuilder sbFile = new StringBuilder("(");
        StringBuilder sbDir = new StringBuilder("(");
        int indexFile = 0;
        int indexDir = 0;
        for (FileWithExt file : fileWithExtList) {
            if (isDelete && new File(file.getFilePath()).isDirectory()) {
                if (sbDir.length() != 1) {
                    sbDir.append(" or ");
                }
                indexDir++;
                String dirPath = file.getFilePath().replace("'", "''");
                if (!dirPath.endsWith("/")) {
                    dirPath = dirPath + "/";
                }
                sbDir.append("lower(").append(FavoriteDatabaseHelper.FIELD_LOCATION).append(") like '").append(dirPath).append("%'");
                if (indexDir > 200) {
                    indexDir = 0;
                    sbDir.append(")");
                    FavoriteDatabaseHelper.getInstance(context).deleteDirList(sbDir.toString());
                    sbDir = new StringBuilder("(");
                }
            }
            if (sbFile.length() != 1) {
                sbFile.append(",");
            }
            indexFile++;
            String filePath = file.getFilePath().replace("'", "''");
            sbFile.append("'").append(filePath).append("'");
            if (indexFile > 200) {
                indexFile = 0;
                sbFile.append(")");
                FavoriteDatabaseHelper.getInstance(context).deleteFileList(sbFile.toString());
                sbFile = new StringBuilder("(");
            }
        }
        sbFile.append(")");
        sbDir.append(")");
        FavoriteDatabaseHelper.getInstance(context).deleteFileList(sbFile.toString());
        FavoriteDatabaseHelper.getInstance(context).deleteDirList(sbDir.toString());

    }

    public static void removeFavList(Context context, ArrayList<FavoriteItem> favoriteItems) {
        StringBuilder sbFile = new StringBuilder("(");
        int indexFile = 0;
        for (FavoriteItem file : favoriteItems) {
            if (sbFile.length() != 1) {
                sbFile.append(",");
            }
            indexFile++;
            String filePath = file.location.replace("'", "''");
            sbFile.append("'").append(filePath).append("'");
            if (indexFile > 200) {
                indexFile = 0;
                sbFile.append(")");
                FavoriteDatabaseHelper.getInstance(context).deleteFileList(sbFile.toString());
                sbFile = new StringBuilder("(");
            }
        }
        sbFile.append(")");
        FavoriteDatabaseHelper.getInstance(context).deleteFileList(sbFile.toString());
    }*/

    /**
     * 获取应用元数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getMetadata(Context context, String key) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
        String value = appInfo == null ? "" : (appInfo.metaData == null ? ""
                : String.valueOf(appInfo.metaData.get(key)));
        return value;
    }

    public static boolean isSupportMiDrop(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(MIDROP_PACKAGE_NAME);
        intent.setType("*/*");
        return IntentBuilder.isIntentResolvable(context, intent);
    }

    public static int getFlvDuration(String path) {
        File file = new File(path);
        //return FLVReader.getDuration(file);
        return 3000;
    }

}
