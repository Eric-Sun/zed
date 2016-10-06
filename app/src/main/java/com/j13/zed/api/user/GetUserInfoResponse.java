package com.j13.zed.api.user;

import com.michael.corelib.internet.core.ResponseBase;
import com.michael.corelib.internet.core.json.JsonProperty;

/**
 * Created by lz on 16/5/11.
 */
public class GetUserInfoResponse extends ResponseBase {

    @JsonProperty("userName")
    public String userName;

    @JsonProperty("headIconUrl")
    public String headIconUrl;

    @JsonProperty("sex")
    public String sex;

    @JsonProperty("desc")
    public String desc;

    @JsonProperty("messageCount")
    public long messageCount;

    @JsonProperty("newMessageCount")
    public long newMessageCount;

    @JsonProperty("followedTagCount")
    public long followedTagCount;

    @JsonProperty("videoCount")
    public long videoCount;

    @JsonProperty("userPrasieCount")
    public long likedVideoCount;

    @JsonProperty("userFollowCount")
    public long userFollowCount;

    @JsonProperty("userFansCount")
    public long userFollowMeCount;

    @JsonProperty("isFollowed")
    public boolean isFollowed;

    @JsonProperty("point")
    public long point;

    @JsonProperty("isSpecial")
    public boolean isSpecial;

}
