package com.j13.zed.api.dz;

import com.j13.zed.api.ZedRequestBase;
import com.michael.corelib.internet.core.annotations.HttpMethod;
import com.michael.corelib.internet.core.annotations.RestMethodUrl;
import com.michael.corelib.internet.core.annotations.StringRawResponse;

/**
 * Created by sunbo on 16/5/22.
 */
@RestMethodUrl("dz.list")
@HttpMethod("POST")
@StringRawResponse
public class DZListRequest extends ZedRequestBase<String> {




}
