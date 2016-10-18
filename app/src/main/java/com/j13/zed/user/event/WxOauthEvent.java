package com.j13.zed.user.event;

/**
 * Created by lz on 16/4/27.
 */
public class WxOauthEvent {

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;

    public int code;

    public String openid;

    public String accessToken;

}
