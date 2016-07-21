package com.j13.zed.api;

import com.j13.zed.util.http.NewApiRequestBase;
import com.michael.corelib.internet.core.annotations.HttpMethod;
import com.michael.corelib.internet.core.annotations.RestMethodUrl;
import com.michael.corelib.internet.core.annotations.StringRawResponse;

/**
 * Created by sunbo on 16/5/22.
 */
@RestMethodUrl("dz.list")
@HttpMethod("POST")
@StringRawResponse
public class DZListRequest extends NewApiRequestBase<String> {




}
