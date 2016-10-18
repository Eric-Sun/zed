package com.j13.zed.api.user;

/**
 * Created by sunbo on 16/10/17.
 */
public class PhoneLoginResponse {

    private long userId;
    private int code;
    private String userName;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
