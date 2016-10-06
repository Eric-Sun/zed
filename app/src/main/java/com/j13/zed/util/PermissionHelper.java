package com.j13.zed.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.j13.zed.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限工具类
 * Created by Melody on 2016/7/29.
 */
public class PermissionHelper {
    /**权限授予标识*/
    public static final int PERMISSIONS_GRANTED = 1;
    /**权限拒绝标识*/
    public static final int PERMISSIONS_DENIED = 0;
    /**权限授权请求码*/
    public static final int PERMISSION_REQUEST_CODE = 0;
    /**报名scheme*/
    private static final String PACKAGE_URL_SCHEME = "package:";

    private Context mContext;
    public CheckCallback mCheckCallback;
//    private static PermissionHelper sPermissionChecker;

    public PermissionHelper(Context context) {
        mContext = context.getApplicationContext();
    }

//    public static PermissionHelper getInstance(Context context){
//        if(sPermissionChecker == null){
//            synchronized (PermissionHelper.class){
//                if(sPermissionChecker == null) {
//                    sPermissionChecker = new PermissionHelper(context);
//                }
//            }
//        }
//        return sPermissionChecker;
//    }

    /**
     * 判断缺少的权限
     * @param permissions 待检查的权限，可以为数组或单个权限
     * @return 缺少的权限数组，null如果不缺少
     */
    public static String[] lackPermissions(Context context, String... permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (lackPermission(context, permission)) {
                list.add(permission);
            }
        }
        if(list.size() == 0){
            return null;
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 判断是否缺少权限
     * @param permission 待检查的权限
     * @return true 缺少权限， false 不缺少
     */
    public static boolean lackPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     * @param permissions 待检查的权限
     */
    public static void requestPermissions(AppCompatActivity activity, String ...permissions){
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
    }

    public void checkPermission(String permission){
        if(lackPermission(mContext, permission)){
            mCheckCallback.onPermissionDenied(permission);
        }else {
            mCheckCallback.onPermissionGranted();
        }
    }

    public void setPermissionCheckCallback(CheckCallback checkCallback) {
        this.mCheckCallback = checkCallback;
    }

    /**
     * Context作为参数，检查单一权限的回调
     */
    public interface CheckCallback{
        void onPermissionGranted();
        void onPermissionDenied(String lackedPermission);
    }
    /**
     * Context作为参数，检查一组权限的回调
     */
    public interface CheckCallback2{
        void onPermissionGranted();
        void onPermissionDenied(String[] lackedPermissions);
    }
    /**
     * Activity作为参数，检查单一权限的回调，可以检测是否设置不再提醒
     */
    public interface CheckTipCallback{
        void onPermissionGranted();  //授权回调
        void onPermissionDenied(String lackedPermission); //未授权回调, lackedPermissions为缺少的权限
        void onUserOnceDenied(String lackedPermission); //用户设置不再提醒回调, lackedPermissions为缺少的权限
    }
    /**
     * Activity作为参数，检查一组权限的回调, 可以检测是否设置不再提醒
     */
    public interface CheckTipCallback2{
        void onPermissionGranted();
        void onPermissionDenied(String[] lackedPermissions);
        void onUserOnceDenied(String[] lackedPermissions);
    }

//    public interface OnReadPhoneStateCheckListener{
//        void onPermissionGranted();
//        void onPermissionDenied();
//    }
//
//    public interface OnReadExternalStorageCheckListener{
//        void onPermissionGranted();
//        void onPermissionDenied();
//    }

    public static void doWithPermissionChecked(Context context, String permission, CheckCallback checkCallback){
        if(lackPermission(context, permission)){
            checkCallback.onPermissionDenied(permission);
        }else {
            checkCallback.onPermissionGranted();
        }
    }

    public static void doWithPermissionChecked(Context context, String[] permissions, CheckCallback2 checkCallback){
        String[] lackPermissions = lackPermissions(context, permissions);
        if(lackPermissions != null){
            checkCallback.onPermissionDenied(lackPermissions);
        }else {
            checkCallback.onPermissionGranted();
        }
    }

    /**
     * 权限检查
     * @param activity 上下文
     * @param permission 带检查的权限
     * @param checkCallback 检查回调
     */
    public static void doWithPermissionChecked(AppCompatActivity activity, String permission, CheckTipCallback checkCallback){
        if(lackPermission(activity, permission)){
            if(shouldShowPermissionTip(activity, permission)){
                checkCallback.onUserOnceDenied(permission);
            }else {
                checkCallback.onPermissionDenied(permission);
            }
        }else {
            checkCallback.onPermissionGranted();
        }
    }

    /**
     * 权限检查
     * @param activity 上下文
     * @param permissions 待检查的权限数组
     * @param checkCallback 检查回调
     */
    public static void doWithPermissionChecked(AppCompatActivity activity, String[] permissions, CheckTipCallback2 checkCallback){
        String[] lackedPermissions = lackPermissions(activity, permissions);
        if(lackedPermissions != null){
            for(String permission : lackedPermissions){
                if(shouldShowPermissionTip(activity, permission)){ //如果用户设置了不再提醒
                    checkCallback.onUserOnceDenied(lackedPermissions);
                    return;
                }
            }
            checkCallback.onPermissionDenied(lackedPermissions);
        }else {
            checkCallback.onPermissionGranted();
        }
    }

    /**
     * 检测用户是否设置不再提醒
     */
    private static boolean shouldShowPermissionTip(AppCompatActivity activity, String permission){
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 显示设置权限对话框
     * @param context 上下文
     * @param title dialog标题
     * @param message dialog内容
     * @param dialogCallback dialog回调
     */
    public static void showPermSetDialog(final Context context, int title, int message, final PermSetDialogCallback dialogCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(title));
        builder.setMessage(context.getString(message));

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialogCallback.onDeny(dialog, which);
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCallback.onAgree(dialog, which);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogCallback.onCancel(dialog);
            }
        });

        builder.show();
    }

    /**
     * 权限设置对话框回调接口
     */
    public interface PermSetDialogCallback{
        void onCancel(DialogInterface dialog);
        void onAgree(DialogInterface dialog, int which);
        void onDeny(DialogInterface dialog, int which);
    }

    /**
     * 当用户选择不再提示时，引导用户去设置页面设置
     */
    public static void startAppSettings(AppCompatActivity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + activity.getPackageName()));
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }
}
