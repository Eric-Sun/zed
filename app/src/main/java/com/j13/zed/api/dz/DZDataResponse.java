package com.j13.zed.api.dz;

import com.j13.zed.api.ZedResponse;
import com.michael.corelib.internet.core.json.JsonProperty;

/**
 * Created by sunbo on 16/5/22.
 */
public class DZDataResponse extends ZedResponse {

    @JsonProperty("data")
    public DZResponse[] data;

}
