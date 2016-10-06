package com.j13.zed.api;

import com.michael.corelib.internet.core.ResponseBase;
import com.michael.corelib.internet.core.json.JsonProperty;

/**
 * 服务错误
 */
public class ServerErrorResponse extends ResponseBase {

    @JsonProperty("code")
    public int errorCode;

    @JsonProperty("data")
    public String errorMsg;

}
