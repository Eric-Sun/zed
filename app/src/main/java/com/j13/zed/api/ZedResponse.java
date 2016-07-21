package com.j13.zed.api;

import com.michael.corelib.internet.core.ResponseBase;
import com.michael.corelib.internet.core.json.JsonProperty;

/**
 * Created by sunbo on 16/5/22.
 */
public class ZedResponse extends ResponseBase {

    @JsonProperty("code")
    public int code;

    @JsonProperty("data")
    public DZDataResponse data;

    @JsonProperty("uid")
    public long uid;
}
