package com.j13.zed.user;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.j13.zed.api.InternetUtil;
import com.j13.zed.api.user.GetUserInfoRequest;
import com.j13.zed.api.user.GetUserInfoResponse;
import com.j13.zed.event.UserInfoEvent;
import com.j13.zed.util.thread.CustomThreadPool;
import com.michael.corelib.internet.core.NetWorkException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lz on 16/5/11.
 */
public class UserInfoManager {

    private static final String TAG = "UserInfoManager";

    public static final String TYPE_USER_FOLLOW = "follow";
    public static final String TYPE_USER_FANS = "fans";
    public static final String TYPE_TOPIC = "topic";

    private WeakReference<Context> mRef;
    private volatile static UserInfoManager instance;

    private UserInfoManager(Context context) {
        mRef = new WeakReference<Context>(context.getApplicationContext());
    }

    public static UserInfoManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserInfoManager.class) {
                if (instance == null) {
                    instance = new UserInfoManager(context);
                }
            }
        }

        return instance;
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(final long userId, final String from) {
        if (mRef == null || mRef.get() == null) {
            return;
        }
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {

                GetUserInfoRequest request = new GetUserInfoRequest();
                request.setIgnoreResponse(true);
                request.userId = userId;

                GetUserInfoResponse response = null;
                int resultCode = UserInfoEvent.RESULT_ERROR;

                try {
                    response = InternetUtil.request(mRef.get(), request);
                    if (response != null) {
                        resultCode = UserInfoEvent.RESULT_OK;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                // 保存自己的用户信息
                if (resultCode == UserInfoEvent.RESULT_OK && response != null
                        && userId == UserContext.getInstance(mRef.get()).getLoginUid()) {
                    saveMineUserInfo(response);
                }

                UserInfoEvent event = new UserInfoEvent();
                event.from = from;
                event.resultCode = resultCode;
                if (response != null && resultCode == UserInfoEvent.RESULT_OK) {
                    event.messageCount = response.messageCount;
                    event.newMessageCount = response.newMessageCount;
                    event.followedTagCount = response.followedTagCount;
                    event.videoCount = response.videoCount;
                    event.userName = response.userName;
                    event.headIconUrl = response.headIconUrl;
                    event.sex = response.sex;
                    event.info = response.desc;
                    event.likedVideoCount = response.likedVideoCount;
                    event.userFollowCount = response.userFollowCount;
                    event.userFollowMeCount = response.userFollowMeCount;
                    event.isFollowed = response.isFollowed;
                    event.userId = userId;
                    event.point = response.point;
                    event.isSpecial = response.isSpecial;
                }
                EventBus.getDefault().post(event);
            }
        });
    }

    /**
     * 保存个人用户信息
     */
    public void saveMineUserInfo(GetUserInfoResponse response) {
        Context context = mRef.get();
        if (response == null || context == null)
            return;

        UserContext userContext = UserContext.getInstance(context);
        User user = userContext.getLoginUser();
        if (user != null) {
            user.setUserName(response.userName);
            user.setHeadIconUrl(response.headIconUrl);
            user.setSex(response.sex);
            user.setDesc(response.desc);
            userContext.saveUserInfo(user);
        }
    }

}
