package com.j13.zed.api.dz;

import com.michael.corelib.internet.core.ResponseBase;
import com.michael.corelib.internet.core.json.JsonProperty;

/**
 * Created by sunbo on 16/5/22.
 */
public class DZResponse  {

    public String content;
    public long userId;
    public long id;

    public String userName;

    public String img;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
