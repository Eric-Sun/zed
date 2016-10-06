package com.j13.zed.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.j13.zed.R;
import com.j13.zed.activity.LoginActivity;
import com.j13.zed.activity.PhoneNumLoginActivity;
import com.j13.zed.user.ThirdPartyType;
import com.j13.zed.user.UserContext;
import com.j13.zed.view.dialog.AlertDialog;
import com.j13.zed.view.dialog.ProgressDialog;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by lz on 15/9/15.
 */
public class AppUtils implements Constants{

    private static final Set<String> RTL;

    static {
        Set<String> lang = new HashSet<String>();
        lang.add("ar"); // Arabic
        lang.add("dv"); // Divehi
        lang.add("fa"); // Persian (Farsi)
        lang.add("ha"); // Hausa
        lang.add("he"); // Hebrew
        lang.add("iw"); // Hebrew (old code)
        lang.add("ji"); // Yiddish (old code)
        lang.add("ps"); // Pashto, Pushto
        lang.add("ur"); // Urdu
        lang.add("yi"); // Yiddish
        RTL = Collections.unmodifiableSet(lang);
    }


    public static boolean hasInstalledApp(Context context, String packageName) {
        if (context == null) {
            return false;
        }

        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            //判断是否获取到了对应的包名信息
            if (pInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isRTL(Locale locale) {
        if (locale == null)
            return false;

        return RTL.contains(locale.getLanguage());
    }


    /**
     * 获取一个耗时等待对话框
     *
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog getWaitDialog(Context context, String message) {
        ProgressDialog waitDialog = new ProgressDialog(context);
        if (!TextUtils.isEmpty(message)) {
            waitDialog.setMessage(message);
        }
        return waitDialog;
    }

    /**
     * 登录对话框
     *
     * @param context
     */
    public static void showLoginDialog(final Context context, final String topic) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.user_login_dialog_title)
                .setMessage(R.string.user_login_dialog_tip)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent to = new Intent(context, LoginActivity.class);
                        to.putExtra(EXTRA_TOPIC, topic);
                        context.startActivity(to);
                        //AnalyticsAgent.trackEvent(new LoginTipsData("pre_upload", "login"));
                        //AnalyticsAgent.trackEvent(new LoginPageData("pre_upload"));
                    }
                })
                .setNegativeButton(R.string.user_login_dialog_cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //AnalyticsAgent.trackEvent(new LoginTipsData("pre_upload", "cancel"));
                    }
                }).create();
        dialog.show();
    }

    /**
     * 绑定手机对话框
     *
     * @param activity
     */
    public static void showBindPhoneDialog(final Activity activity) {
        final long userId = UserContext.getInstance(activity).getLoginUid();
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.phone_bind_title)
                .setMessage(R.string.bind_phone_dialog_tip)
                .setPositiveButton(R.string.bind_phone_dialog_bind, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent to = new Intent(activity, PhoneNumLoginActivity.class);
                        to.putExtra(PhoneNumLoginActivity.KEY_TYPE, ThirdPartyType.BIND_OPERATION);
                        to.putExtra(BIND_SOURCE, TYPE_PHONE);
                        activity.startActivity(to);
                        activity.setResult(Activity.RESULT_OK);
                        activity.finish();
                    }
                })
                .setNegativeButton(R.string.bind_phone_dialog_cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.setResult(Activity.RESULT_OK);
                        activity.finish();
                    }
                }).create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });
        dialog.show();
    }

    /**
     * 解绑对话框
     *
     * @param activity
     */
    public static void showUnbindAccountDialog(final Activity activity, final String source) {
        if(!UserContext.getInstance(activity).isLogin()){
            Toast.makeText(activity, R.string.unlogin, Toast.LENGTH_SHORT).show();
            return;
        }
        String unBindStr = "";
        if(TYPE_PHONE.equals(source)){
            unBindStr = String.format(activity.getString(R.string.unbind_account), activity.getString(R.string.phone_num));
        }else if(TYPE_WECHAT.equals(source)){
            unBindStr = String.format(activity.getString(R.string.unbind_account), activity.getString(R.string.wechat_btn_str));
        }else if(TYPE_QQ.equals(source)){
            unBindStr = String.format(activity.getString(R.string.unbind_account), activity.getString(R.string.qq_platform));
        }else if(TYPE_WEIBO.equals(source)){
            unBindStr = String.format(activity.getString(R.string.unbind_account), activity.getString(R.string.weibo_platform));
        }else {
            unBindStr = String.format(activity.getString(R.string.unbind_account), activity.getString(R.string.xiaomi_btn_str));
        }
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.tip)
                .setMessage(unBindStr)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //解绑账号
//                        OauthLoginManager.getInstance(activity).unBindAccount(source);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //activity.setResult(Activity.RESULT_OK);
                        //activity.finish();
                    }
                }).create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //activity.setResult(Activity.RESULT_OK);
                //activity.finish();
            }
        });
        dialog.show();
    }


//    /**
//     * 流量使用提醒对话框
//     *
//     * @param activity
//     * @param id
//     */
//    public static void showDataConsumptionDialog(final Activity activity, final Long id) {
//        final VideoUploadManager manager = VideoUploadManager.getInstance(activity);
//        String consumption = manager.getUploadMissionDataConsumption(id);
//        AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setTitle(R.string.video_data_consumption_dialog_title)
//                .setMessage(String.format(activity.getResources().getString(R.string.video_data_consumption_dialog_message), consumption))
//                .setPositiveButton(R.string.video_data_consumption_confirm, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        manager.setAllowDataConsumption(id, true);
//                        manager.requestRetry(id);
//                        manager.statistic_traffic(id, "upload");
//                    }
//                })
//                .setNegativeButton(R.string.video_data_consumption_cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        manager.setAllowDataConsumption(id, false);
//                        manager.statistic_traffic(id, "cancel");
//                    }
//                }).create();
//        dialog.show();
//    }

    /**
     * 重试上传视频对话框
     *
     * @param activity
     */
//    public static void showRetryDialog(final Activity activity, final Long id) {
//        final VideoUploadManager manager = VideoUploadManager.getInstance(activity);
//        AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setTitle(R.string.video_upload_retry_title)
//                .setMessage(R.string.video_upload_retry_message)
//                .setPositiveButton(R.string.video_upload_retry_confirm, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        manager.statistic_retry(id, "upload");
//                        if (!NetworkUtils.hasInternet(activity.getApplicationContext())) {
//                            Toast.makeText(activity.getApplicationContext(), R.string.no_network_tip, Toast.LENGTH_SHORT).show();
//                            manager.setAllowDataConsumption(id, false);
//                            manager.requestRetry(id);
//                            return;
//                        }
//                        if (NetworkUtils.isMobileNetwork(activity.getApplicationContext())) {
//                            showDataConsumptionDialog(activity, id);
//                        } else {
//                            manager.setAllowDataConsumption(id, false);
//                            manager.requestRetry(id);
//                        }
//                    }
//                })
//                .setNegativeButton(R.string.video_data_consumption_cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                manager.statistic_retry(id, "cancel");
//                            }
//                        }
//                ).create();
//        dialog.show();
//    }


//    enum HammerState {
//        HAMMER,
//        FETCH
//    }
//    private static HammerState hammerState = HammerState.HAMMER;
//    public static boolean isRegisterPointGet = false;
//    /**
//     * 显示砸金蛋对话框
//     */
//    public static void showHammerEggDialog(final Context context){
//        if (isRegisterPointGet){
//            return;
//        }
//        isRegisterPointGet = true;
//        final AlertDialog dialog = new AlertDialog.Builder(context).create();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setContentView(R.layout.dialog_open_treasure_box);
//        window.setBackgroundDrawableResource(android.R.color.transparent);
//        //砸金蛋提示
//        final TextView hammerHint = (TextView) window.findViewById(R.id.tv_dialog_hammer_egg_hint);
//        //砸金蛋分数
//        final TextView hammerMark = (TextView) window.findViewById(R.id.tv_dialog_hammer_egg_point);
//        //金蛋图片
//        final ImageView goldEgg = (ImageView) window.findViewById(R.id.iv_dialog_hammer_egg_egg);
//
//        final WebView webView = (WebView) window.findViewById(R.id.wv_dialog_hammer_egg);
//        webView.setWebViewClient(new WebViewClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setUseWideViewPort(true);// 这个很关键
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        webView.setHorizontalScrollBarEnabled(false);//水平不显示
//        webView.setVerticalScrollBarEnabled(false); //垂直不显示
//        webView.setOnTouchListener(new View.OnTouchListener() { //禁用webView的滑动事件
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        return true;
//                }
//                return false;
//            }
//        });
//        webView.setOnLongClickListener(new View.OnLongClickListener() { //屏蔽长按事件，避免出现系统的复制框
//            @Override
//            public boolean onLongClick(View v) {
//                return true;
//            }
//        });
//        webView.loadUrl("file:///android_asset/index.html");
//        //关闭按钮事件
//        final ImageView closeBtn = (ImageView) window.findViewById(R.id.iv_dialog_hammer_egg_close);
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        //砸金蛋按钮事件
//        final Button hammerBtn = (Button) window.findViewById(R.id.btn_dialog_hammer_egg_hammer);
//        hammerBtn.setOnClickListener(new View.OnClickListener() {
//            int point = 0;
//            @Override
//            public void onClick(View v) {
//                if (hammerState == HammerState.HAMMER){ //执行砸金蛋业务逻辑
//                    int[] points = new int[]{15, 16, 17, 18, 19, 20};
//                    point = points[new Random().nextInt(6)];
//
//                    hammerHint.setText(String.format(context.getString(R.string.egg_hammered_hint), point));
//                    goldEgg.setVisibility(View.GONE);
//                    hammerMark.setVisibility(View.VISIBLE);
//                    hammerMark.setText(String.format(context.getString(R.string.hammer_egg_mark), point));
//                    hammerBtn.setText(R.string.fetch_mark);
//
//                    webView.loadUrl("JavaScript:hammerEgg(" + point + ")");
//
//                    hammerState = HammerState.FETCH;
//                }else {   //执行领取积分业务逻辑
//                    Toast.makeText(context, R.string.fetch_mark_success, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    hammerState = HammerState.HAMMER;
//                    PointManager pointManager = PointManager.getInstance(context.getApplicationContext());
//                    long sum = pointManager.getPoint();
//                    sum += point;
//                    pointManager.savePoint(sum);
//                }
//            }
//        });
//    }


    public static void showSoftInput(final Activity activity, final boolean show) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                View focus = activity.getCurrentFocus();
                if (focus != null) {
                    if (!show) {
                        inManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } else
                        inManager.showSoftInput(focus, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
    }

    public static boolean isIntentResolvable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    public static void viewUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (isIntentResolvable(context, intent)) {
            context.startActivity(intent);
        }
    }

    public static boolean isService(Context context) {
        String proName = getCurProcessName(context);
        if (proName != null) {
            ApplicationInfo appInfo = context.getApplicationInfo();
            if (appInfo != null) {
                return !proName.equals(appInfo.processName);
            }
        }
        return false;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess == null) {
                continue;
            }
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 应用是否在前台
     */
    public static boolean isAppRunningForeground(Context context) {
        String packageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity != null && topActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }



}
