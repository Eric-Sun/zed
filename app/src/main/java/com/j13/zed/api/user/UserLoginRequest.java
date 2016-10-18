package com.j13.zed.api.user;

import com.j13.zed.api.ZedRequestBase;
import com.michael.corelib.internet.core.annotations.HttpMethod;
import com.michael.corelib.internet.core.annotations.RequiredParam;
import com.michael.corelib.internet.core.annotations.RestMethodUrl;
import com.michael.corelib.internet.core.annotations.StringRawResponse;

/**
 * Created by sunbo on 16/10/15.
 */
@RestMethodUrl("user.login")
@HttpMethod("POST")
@StringRawResponse
public class UserLoginRequest extends ZedRequestBase<UserLoginResponse> {

    @RequiredParam("mobile")
    public String mobile;

    @RequiredParam("password")
    public String password;
}
