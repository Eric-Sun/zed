package com.j13.zed.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;


import com.j13.zed.helper.FileIconHelper;
import com.j13.zed.util.AppUtils;
import com.j13.zed.util.BitmapUtils;
import com.j13.zed.util.DebugLog;
import com.j13.zed.util.NetworkUtils;
import com.j13.zed.util.ToastManager;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;

import com.j13.zed.R;
import com.squareup.picasso.Picasso;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aaronliu on 15-12-21.
 */
public class ShareHelper {

    private static final String TAG = "ShareHelper";

    private static final String SEND_QQ_TMP_NAME = "send_qq_tmp";
    private static final String SEND_WX_TMP_NAME = "send_wx_tmp";

    public static int[] SHARE_TITLES = new int[]{
            R.string.wechat_platform, R.string.wxcircle_platform,
            R.string.qq_platform, R.string.qzone_platform ,
            R.string.weibo_platform
    };

    public static int[] SHARE_ICONS = new int[]{
            R.drawable.icon_wechat, R.drawable.icon_wxcircle,
            R.drawable.icon_qq, R.drawable.icon_qzone ,
            R.drawable.icon_weibo
    };

    public static int[] SHARE_WHITE_ICONS = new int[]{
            /*R.drawable.icon_wechat_white, R.drawable.icon_wxcircle_white,
            R.drawable.icon_qq_white, R.drawable.icon_qzone_white,
            R.drawable.icon_weibo_white*/
            R.drawable.icon_wechat_share, R.drawable.icon_wxcircle_share,
            R.drawable.icon_qq_share, R.drawable.icon_qzone_share,
            R.drawable.icon_weibo_share
    };

    public static final int WEIXIN = 0;
    public static final int WEIXIN_CIRCLE = 1;
    public static final int QQ = 2;
    public static final int QZONE = 3;
    public static final int WEIBO = 4;

    private static final int THUMB_SIZE = 90;
    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    public static final String QQ_APP_ID = "1101020666";
    public static final String WX_APP_ID = "wxcd9ea2e725cb2147";
    public static final String WX_APP_SECRET = "792bda7e4a96005bedb52eebde692f0f";

    public static final String WEIBO_APP_ID = "1449369659";

    private IWXAPI wxApi;
    private Tencent mTencent;
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI mWeiboShareAPI;

    private final Activity mActivity;
    private final Context mAppContext;

    private Picasso mPicasso;

    private String[] mShareTitle;

    public ShareHelper(Activity activity) {
        mActivity = activity;
        mAppContext = activity.getApplicationContext();
        mPicasso = FileIconHelper.getInstance(mAppContext).getPicasso();
        initShareTitle();
    }

    private void initShareTitle(){
        mShareTitle = new String[]{
                "\uD83D\uDD25" + mActivity.getString(R.string.share_title_01) + "\uD83D\uDC40",
                "\uD83D\uDC49" + mActivity.getString(R.string.share_title_02),
                "\uD83D\uDEAB" + mActivity.getString(R.string.share_title_03) + "\uD83D\uDEAB",
                "㊙️" + mActivity.getString(R.string.share_title_04) + "㊙️",
                mActivity.getString(R.string.share_title_05) + "\uD83D\uDC4D" + "\uD83D\uDC4D" + mActivity.getString(R.string.share_title_05_tile),
                mActivity.getString(R.string.share_title_06) + "\uD83D\uDCA5" + mActivity.getString(R.string.share_title_06_a) + "\uD83D\uDCA5" + mActivity.getString(R.string.share_title_06_b),
                "\uD83C\uDF81" + mActivity.getString(R.string.share_title_07),
                mActivity.getString(R.string.share_title_08) + "\uD83D\uDCAF" + mActivity.getString(R.string.share_title_08_a),
                "\uD83D\uDCE2" + mActivity.getString(R.string.share_title_09)
        };
    }

    /**
     * 配置所有分享平台
     */
    public void configAllPlatforms() {
        // 添加微信、微信朋友圈平台
        addWXPlatform();

        // 添加QQ、QZone平台
        addQQPlatform();

        // 添加微博平台
//        addWeiboPlatform();
    }

    public void addWeiboPlatform() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mAppContext, WEIBO_APP_ID);
        mWeiboShareAPI.registerApp();
    }

    /**
     * 添加QQ、QZone平台
     */
    public void addQQPlatform() {
        mTencent = Tencent.createInstance(QQ_APP_ID, mAppContext);
    }

    /**
     * 添加微信、微信朋友圈平台
     */
    public void addWXPlatform() {
        wxApi = WXAPIFactory.createWXAPI(mAppContext, WX_APP_ID);
        wxApi.registerApp(WX_APP_ID);
    }

    /**
     * 检测微博是否安装
     */
    public boolean isWeiboInstalled(){
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
//            ToastManager.show(mActivity, R.string.weibo_not_installed);
            return false;
        }
        return true;
    }

    private boolean isQQAppInstalled() {
        if (!AppUtils.hasInstalledApp(mAppContext, QQ_PACKAGE_NAME)) {
            ToastManager.show(mActivity, R.string.qq_not_installed);
            return false;
        }
        return true;
    }

    private boolean isWXAppInstalled() {
        if (!wxApi.isWXAppInstalled()) {
            ToastManager.show(mActivity, R.string.weixin_not_installed);
            return false;
        }
        return true;
    }

//    public void shareVideo(ShortVideo video, int titleId, boolean isPlayShare, String pageName, String tag) {
//        String shareUrl = ShortVideoManager.getShareVideoUrl(video.videoId);
//        ShareContent content = new ShareContent();
//        content.setTargetUrl(shareUrl);
//        content.setThumb(video.thumbUrl);
//        String formatPlayNum;
//        if (video.playNum <= 0) {
//            formatPlayNum = mAppContext.getResources().getQuantityString(R.plurals.video_play_num,
//                    (int) video.playNum, "0");
//        } else {
//            formatPlayNum = mAppContext.getResources().getQuantityString(R.plurals.video_play_num,
//                    (int) video.playNum, VideoFormatter.getFormatCount(mAppContext, video.playNum));
//        }
//        if(video.title == null || TextUtils.isEmpty(video.title)){
//            content.setTitle(formatPlayNum);
//        }else {
//            content.setTitle(video.title + "  |  " + formatPlayNum);
//        }
////        content.setTitle(video.title);
//        content.setPlayCount("  |  " + formatPlayNum);
//        String name = "";
//        switch (titleId) {
//            case R.string.wechat_platform:
//                name = HubbleConstant.APP_WECHAT;
//                shareVideo(content, ShareHelper.WEIXIN);
//                break;
//            case R.string.wxcircle_platform:
//                name = HubbleConstant.APP_MOMENTS;
//                shareVideo(content, ShareHelper.WEIXIN_CIRCLE);
//                break;
//            case R.string.qq_platform:
//                name = HubbleConstant.APP_QQ;
//                shareVideo(content, ShareHelper.QQ);
//                break;
//            case R.string.qzone_platform:
////            case R.string.weibo_platform:
//                name = HubbleConstant.APP_QZONE;
//                shareVideo(content, ShareHelper.QZONE);
////                name = HubbleConstant.APP_WEIBO;
////                shareVideo(content, ShareHelper.WEIBO);
//                break;
//            case R.string.weibo_platform:
//                name = HubbleConstant.APP_WEIBO;
//                shareVideo(content, ShareHelper.WEIBO);
//                break;
//        }
//
//        if (!isPlayShare) {
//            ShareClick shareClick = ShareClick.buildVideo(mActivity, pageName, ShareClick.TYPE_PAGE, name,
//                    tag, video.gcid, video.videoId);
//            Hubble.onEvent(mActivity, shareClick);
//
//            ShareSuccess.storeVideoCache(pageName, ShareSuccess.TYPE_PAGE, name, tag, video.gcid, video.videoId, video.isRecommend);
//        } else {
//            ShareClick shareClick = ShareClick.buildVideo(mActivity, pageName, ShareClick.TYPE_PLAY_FINISHED, name,
//                    tag, video.gcid, video.videoId);
//            Hubble.onEvent(mActivity, shareClick);
//
//            ShareSuccess.storeVideoCache(pageName, ShareSuccess.TYPE_PLAY_FINISHED, name, tag, video.gcid, video.videoId, video.isRecommend);
//        }
//
//        ShortVideoManager.getInstance(mActivity).shareVideo(video, pageName, tag, VideoShareRequest.SHARE_TYPE_CLICK, name);
//    }


//    /**
//     * 分享视频
//     *
//     * @param content  分享内容
//     * @param platform 指定分享平台
//     */
//    public void shareVideo(ShareContent content, int platform) {
//        switch (platform) {
//            case WEIXIN:
//                shareVideoToWX(content, false);
//                break;
//            case WEIXIN_CIRCLE:
//                shareVideoToWX(content, true);
//                break;
//            case QQ:
//                shareVideoToQQ(content);
//                break;
//            case QZONE:
//                shareVideoToQzone(content);
//                break;
//            case WEIBO:
//                shareVideoToWeibo(content);
//                break;
//            default:
//                break;
//        }
//    }

//    public static ShareContent tempWBShareContent;

    /**
     * 分享视频到微博
     * @param content
     */
//    public void shareVideoToWeibo(final ShareContent content) {
//        if(!NetworkUtils.hasInternet(mActivity)){
//            return;
//        }
//        addWeiboPlatform();
//        if (mWeiboShareAPI == null){
//            return;
//        }
//        if (content == null ) {
//            return;
//        }
////        tempWBShareContent = content;
//        /*if(!isWeiboInstalled()){
//            shareVideoToWeiboIfNotInstalled(content);
//            return;
//        }*/
////        Intent it = new Intent(mActivity, WeiboShareActivity.class);
////        mActivity.startActivity(it);
//        new AsyncTask<Void, Void, WeiboMultiMessage>(){
//            @Override
//            protected WeiboMultiMessage doInBackground(Void... params) {
//                return buildWBShareMessage(content);
//            }
//
//            @Override
//            protected void onPostExecute(WeiboMultiMessage weiboMessage) {
//                // 2. 初始化从第三方到微博的消息请求
//                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//                // 用transaction唯一标识一个请求
//                request.transaction = String.valueOf(System.currentTimeMillis());
//                request.multiMessage = weiboMessage;
//                // 3. 发送请求消息到微博，唤起微博分享界面
////                mWeiboShareAPI.sendRequest(mActivity, request);
//
//                /*AuthInfo authInfo = new AuthInfo(mActivity, WEIBO_APP_ID, OauthLoginManager.WB_REDIRECT_URL, OauthLoginManager.WB_SCOPE);
//                Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mAppContext);
//                String token = "";
//                if (accessToken != null) {
//                    token = accessToken.getToken();
//                }
//                mWeiboShareAPI.sendRequest(mActivity, request, authInfo, token, new WeiboAuthListener() {
//
//                    @Override
//                    public void onWeiboException(WeiboException arg0) {
//                    }
//
//                    @Override
//                    public void onComplete(Bundle bundle) {   //经测试该回调只会在网页分享的时候调动
//                        Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                        AccessTokenKeeper.writeAccessToken(mAppContext, newToken);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                    }
//                });*/
//                sRequest = request;
//                shareWeiboVideo(request);
//            }
//        }.execute();
//    }
    public static SendMultiMessageToWeiboRequest sRequest;
//    public void shareWeiboVideo(SendMultiMessageToWeiboRequest request){
//        AuthInfo authInfo = new AuthInfo(mActivity, WEIBO_APP_ID, OauthLoginManager.WB_REDIRECT_URL, OauthLoginManager.WB_SCOPE);
//        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mAppContext);
//        String token = "";
//        if (accessToken != null) {
//            token = accessToken.getToken();
//        }
//        mWeiboShareAPI.sendRequest(mActivity, request, authInfo, token, new WeiboAuthListener() {
//
//            @Override
//            public void onWeiboException(WeiboException arg0) {
//            }
//
//            @Override
//            public void onComplete(Bundle bundle) {   //经测试该回调只会在网页分享的时候调动
//                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
//                AccessTokenKeeper.writeAccessToken(mAppContext, newToken);
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });
//    }

//    public void shareVideoToWeiboIfNotInstalled(ShareContent content){
//        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(mActivity);
//        String title = mShareTitle[new Random().nextInt(9)] + content.getPlayCount() + mActivity.getString(R.string.share_from_youliao);
//        String shareContent = title + content.getTitle() + content.getTargetUrl();
//        mTempShareContent = shareContent;
//        mTempShareThumb = content.getThumb();
//        if(!accessToken.isSessionValid()){  //need to login to get AccessToken
//            ToastManager.show(mActivity, R.string.please_login_weibo_first);
////            Toast.makeText(mActivity, mActivity.getString(R.string.please_login_weibo_first), Toast.LENGTH_SHORT).show();
//            Intent it = new Intent(mActivity, WeiboShareLoginActivity.class);
//            mActivity.startActivity(it);
//        }else {
//            shareToWeiboByWeb(mActivity, accessToken);
//        }
//    }
    private static String mTempShareContent;
    private static String mTempShareThumb;
//    public static void shareToWeiboByWeb(final Activity activity, Oauth2AccessToken accessToken){
////        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
//        StatusesAPI statusesAPI = new StatusesAPI(activity, WEIBO_APP_ID, accessToken);
//        statusesAPI.uploadUrlText(mTempShareContent, mTempShareThumb, "", "14.5", "23.0", new RequestListener() {
//            @Override
//            public void onComplete(String s) {
//                DebugLog.d("DebugLog", s);
//                if(s != null){
//                    ToastManager.show(activity, R.string.weibo_share_success);
////                    Toast.makeText(activity, activity.getString(R.string.weibo_share_success), Toast.LENGTH_SHORT).show();
//                    ShareSuccess shareSuccess = ShareSuccess.buildFromCache(activity);
//                    if (shareSuccess != null) {
//                        Hubble.onEvent(activity, shareSuccess);
//                        SettingManager.setCachedShareSuccess("");
//
//                        if (shareSuccess.isShareVideo()) {
//                            long videoId = 0;
//                            try {
//                                videoId = Long.parseLong(shareSuccess.getVideoId());
//                            } catch (NumberFormatException e) {
//                                e.printStackTrace();
//                            }
//                            ShortVideo video = new ShortVideo();
//                            video.videoId = videoId;
//                            video.gcid = shareSuccess.getGcid();
//                            video.isRecommend = shareSuccess.isRecommend();
//                            ShortVideoManager.getInstance(activity.getApplicationContext()).shareVideo(
//                                    video, shareSuccess.getPageName(), shareSuccess.getTag(),
//                                    VideoShareRequest.SHARE_TYPE_SUCCESS, shareSuccess.getAppName());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onWeiboException(WeiboException e) {
//                DebugLog.d("DebugLog", e.getMessage());
//                Toast.makeText(activity, activity.getString(R.string.weibo_share_failed), Toast.LENGTH_SHORT).show();
//            }
//        });

//            // 2. 调用接口发送微博
//            WeiboParameters params = new WeiboParameters();
//            params.put("access_token", accessToken.getToken());
//            params.put("status",       "通过API发送微博-upload");
//            params.put("visible",      "0");
//            params.put("list_id",      "");
//            params.put("pic",          bitmap);
//            params.put("lat",          "14.5");
//            params.put("long",         "23.0");
//            params.put("annotations", "");
//
//            AsyncWeiboRunner.requestAsync(
//                    "https://api.weibo.com/2/statuses/upload.json",
//                    params,
//                    "POST",
//                    mListener);
//    }

    private WeiboMultiMessage buildWBShareMessage(ShareContent content){
        String title = mShareTitle[new Random().nextInt(9)]/* + content.getPlayCount()*/ + mActivity.getString(R.string.share_from_youliao);
        Bitmap bitmap = null;
        try {
            InputStream is = mPicasso.load(content.getThumb()).getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            if(bitmap == null){
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.share_default);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.share_default);
        }
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        //分享的文本内容
        weiboMessage.textObject = getWeiboShareTextObj(content, title);
        //分享的图片
        weiboMessage.imageObject = getWeiboShareImageObj(content, bitmap);
        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        weiboMessage.mediaObject = getWeiboShareVideoObj(content, content.getTitle() , bitmap);

        return weiboMessage;
    }

//    static final int[] SHARE_RANDOM_TITLE = {
//            R.string.share_title_01,
//            R.string.share_title_02,
//            R.string.share_title_03,
//            R.string.share_title_04,
//            R.string.share_title_05,
//            R.string.share_title_06,
//            R.string.share_title_07,
//            R.string.share_title_08,
//            R.string.share_title_09
//    };
//
//    static final String[] EMOJI = {
//            "\uD83D\uDD25","\uD83D\uDC40",
//            "\uD83D\uDC49",
//            "\uD83D\uDEAB",
//            "㊙️",
//            "\uD83D\uDC4D",
//            "\uD83D\uDCA5",
//            "\uD83C\uDF81",
//            "\uD83C\uDF81",
//            "\uD83D\uDCE2"
//    };

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getWeiboShareTextObj(ShareContent content, String title) {
        TextObject textObject = new TextObject();
//        textObject.text = mShareTitle[new Random().nextInt(9)] + content.getPlayCount();
        if(isWeiboInstalled()){
            textObject.text = title;
        }else {
            textObject.text = title + content.getTitle();
        }

        boolean isCorrect = textObject.checkArgs();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getWeiboShareImageObj(ShareContent content, Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        boolean isCorrect = imageObject.checkArgs();
        return imageObject;
    }

    /**
     * 创建微博分享多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getWeiboShareVideoObj(ShareContent content, String title, Bitmap bitmap) {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
//        videoObject.title = mShareTitle[new Random().nextInt(9)] + content.getPlayCount();
        videoObject.title = title;
        videoObject.description = content.getTitle();
//        Bitmap bitmap;
//        try {
//            InputStream is = mPicasso.load(content.getThumb()).getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
//        } catch (Exception e) {
//            bitmap = BitmapFactory.decodeResource(mAppContext.getResources(), R.drawable.icon_weibo);
//            e.printStackTrace();
//        }
//        // 设置 Bitmap 类型的图片到视频对象里  设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//        ByteArrayOutputStream os = null;
//        try {
//            os = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
//            System.out.println("kkkkkkk    size  "+ os.toByteArray().length );
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.e("Weibo.BaseMediaObject", "put thumb failed");
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        int bitmapSize = getBitmapSize(bitmap);
        Log.e("bitmapSize", "" + bitmapSize);
        if(bitmapSize > 32768){
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = ((float) 320) / bitmapWidth;
            float scaleHeight = ((float) 200) / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmap.getHeight(), matrix, true);
            videoObject.setThumbImage(resizeBitmap);
        }else {
            videoObject.setThumbImage(bitmap);
        }
        videoObject.actionUrl = content.getTargetUrl();
        videoObject.dataUrl = content.getTargetUrl();
        videoObject.dataHdUrl = content.getTargetUrl();
        videoObject.duration = 10;
        videoObject.defaultText = content.getTitle();
        boolean isCorrect = videoObject.checkArgs();
        return videoObject;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public void handleWeiboCallback(Intent intent, IWeiboHandler.Response activity){
        mWeiboShareAPI.handleWeiboResponse(intent, activity);
    }

    /**
     * QQ分享视频
     *
     * @param content
     */
//    private void shareVideoToQQ(ShareContent content) {
//        if (mTencent == null || content == null || !isQQAppInstalled()) {
//            return;
//        }
//
//        Bundle bundle = new Bundle();
//        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        String title = mShareTitle[new Random().nextInt(9)] /*+ content.getPlayCount()*/;
//        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
//        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, content.getThumb());
//        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, content.getTargetUrl());
//        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, content.getTitle());
//
//        mTencent.shareToQQ(mActivity, bundle, mTencentIUiListener);
//    }


    /**
     * QQ空间分享视频
     *
     * @param content
     */
//    private void shareVideoToQzone(ShareContent content) {
//        if (mTencent == null || content == null || !isQQAppInstalled()) {
//            return;
//        }
//
//        Bundle bundle = new Bundle();
//        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//        String title = mShareTitle[new Random().nextInt(9)]/* + content.getPlayCount()*/;
//        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
//        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, content.getTargetUrl());
//        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content.getTitle());
//        ArrayList<String> imageUrls = new ArrayList<String>(1);
//        imageUrls.add(content.getThumb());
//        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//
//        mTencent.shareToQzone(mActivity, bundle, mTencentIUiListener);
//    }


//    private final IUiListener mTencentIUiListener = new IUiListener() {
//        @Override
//        public void onComplete(Object o) {
//            DebugLog.d(TAG, " onComplete result=" + o);
//            ShareSuccess shareSuccess = ShareSuccess.buildFromCache(mActivity);
//            if (shareSuccess != null) {
//                Hubble.onEvent(mAppContext, shareSuccess);
//                SettingManager.setCachedShareSuccess("");
//
//                if (shareSuccess.isShareVideo()) {
//                    long videoId = 0;
//                    try {
//                        videoId = Long.parseLong(shareSuccess.getVideoId());
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//                    ShortVideo video = new ShortVideo();
//                    video.videoId = videoId;
//                    video.gcid = shareSuccess.getGcid();
//                    video.isRecommend = shareSuccess.isRecommend();
//                    ShortVideoManager.getInstance(mAppContext).shareVideo(
//                            video, shareSuccess.getPageName(), shareSuccess.getTag(),
//                            VideoShareRequest.SHARE_TYPE_SUCCESS, shareSuccess.getAppName());
//                }
//            }
//        }
//
//        @Override
//        public void onError(UiError error) {
//            if (error != null) {
//                DebugLog.w("ShareHelper", error.errorDetail);
//            }
//        }
//
//        @Override
//        public void onCancel() {
//
//        }
//    };

//    public void onTencentActivityResult(int requestCode, int resultCode, Intent data) {
//        DebugLog.d(TAG, " onTencentActivityResult data=" + data);
//        Tencent.onActivityResultData(requestCode, resultCode, data, mTencentIUiListener);
//    }

    /**
     * 微信、朋友圈分享视频
     *
     * @param content
     * @param isToCircle
     * @return
     */
    private void shareVideoToWX(final ShareContent content, final boolean isToCircle) {
        if (wxApi == null || content == null || !isWXAppInstalled()) {
            return;
        }

        new AsyncTask<Void, Void, WXMediaMessage>() {
            @Override
            protected WXMediaMessage doInBackground(Void... params) {
                return buildWXVideoParams(content);
            }

            @Override
            protected void onPostExecute(WXMediaMessage result) {
                if (result == null) {
                    return;
                }

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());
                req.message = result;
                req.scene = isToCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                wxApi.sendReq(req);
            }
        }.execute();
    }

    private WXMediaMessage buildWXVideoParams(ShareContent content) {
//        String title = content.getTitle();
        String title = mShareTitle[new Random().nextInt(9)]/* + content.getPlayCount()*/;
        String imageUrl = content.getThumb();
        String targetUrl = content.getTargetUrl();

        WXMediaMessage msg = new WXMediaMessage();
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = targetUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            msg.thumbData = getByteArray(imageUrl, THUMB_SIZE, THUMB_SIZE);
        }
        msg.title = title;
        msg.mediaObject = video;
        msg.description = content.getTitle();
        return msg;
    }

    public byte[] getByteArray(String path, int width, int height) {
        InputStream is = null;

        try {
            if (path.startsWith("http") || path.startsWith("https")) {
                is = mPicasso.load(path).getInputStream();
            } else {
                is = mPicasso.load(Uri.fromFile(new File(path))).getInputStream();
            }
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (bitmap != null) {
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    byte[] thumbData = BitmapUtils.bitmapToByteArray(thumbBmp);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    if (thumbBmp != null && !thumbBmp.isRecycled()) {
                        thumbBmp.recycle();
                    }
                    return thumbData;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取分享动画索引值
     *
     * @return
     */
    public static int getShareAnimatorPosition() {
        //30%概率开始动画
        Random start = new Random();
        int v = start.nextInt(100);
        if (v < 70)
            return -1;

        Random r = new Random();
        int i = r.nextInt(100);
        if (i < 35) {
            return 0; //微信：35%概率
        } else if (i < 50) {
            return 1; //朋友圈：15%概率
        } else if (i < 85) {
            return 2; //QQ：35%概率
        } else {
            return 3; //QQ空间：15%概率
        }
    }


    /**
     * 启动分享图标动画
     *
     * @param icon
     * @param x
     * @param y
     */
//    public static void startShareIconAnimator(final View icon, final int x, final int y) {
//        if (icon != null) {
//            icon.setVisibility(View.VISIBLE);
//            icon.bringToFront();
//
//            ViewAnimator.animate(icon)
//                    .translationX(x, x)
//                    .translationY(-20f, y)
//                    .interpolator(new BounceInterpolator())
//                    .duration(1000)
//                    .thenAnimate(icon)
//                    .scale(1f, 2.0f, 1f)
//                    .duration(500)
//                    .onStop(new AnimationListener.Stop() {
//                        @Override
//                        public void onStop() {
//                            icon.postDelayed(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    resetShareIconAnimator(icon);
//                                }
//                            }, 2000);
//                        }
//                    })
//                    .start();
//        }
//    }

    /**
     * 重置分享图标动画
     *
     * @param icon
     */
//    public static void resetShareIconAnimator(View icon) {
//        icon.setVisibility(View.GONE);
//        ViewAnimator.animate(icon)
//                .translationX(0f)
//                .translationY(-20f)
//                .start();
//    }

}