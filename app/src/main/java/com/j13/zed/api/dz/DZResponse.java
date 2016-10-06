package com.j13.zed.api.dz;

import com.michael.corelib.internet.core.ResponseBase;
import com.michael.corelib.internet.core.json.JsonProperty;

/**
 * Created by sunbo on 16/5/22.
 */
public class DZResponse extends ResponseBase {

    @JsonProperty("content")
    public String content;

    @JsonProperty("userId")
    public long userId;

    @JsonProperty("id")
    public long id;

    @JsonProperty("userName")
    public String userName;

    @JsonProperty("img")
    public String img;
}
