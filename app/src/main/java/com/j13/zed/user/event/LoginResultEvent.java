package com.j13.zed.user.event;

/**
 * Created by lz on 16/4/11.
 */
public class LoginResultEvent {

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;
    public static final int USER_CANCEL = 2;

    public String loginType;
    /**标识新用户*/
    public boolean isNewUser;

    public int resultCode;
}
