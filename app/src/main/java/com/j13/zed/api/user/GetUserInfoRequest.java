package com.j13.zed.api.user;

import com.j13.zed.api.ZedRequestBase;
import com.michael.corelib.internet.core.annotations.OptionalParam;
import com.michael.corelib.internet.core.annotations.RestMethodUrl;

/**
 * Created by lz on 16/5/11.
 */

@RestMethodUrl("user.getRa2UserInfo")
public class GetUserInfoRequest extends ZedRequestBase<GetUserInfoResponse> {

    @OptionalParam("userId")
    public long userId;

}
