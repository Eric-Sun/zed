package com.j13.zed.api.user;

/**
 * Created by sunbo on 16/10/15.
 */
public class UserLoginResponse {
    private int id;
    private String nickName;
    private String img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
