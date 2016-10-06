package com.j13.zed.event;

/**
 * Created by lz on 16/5/11.
 */
public class UserInfoEvent {

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;

    public static final String FROM_MINE_USER_CENTER = "mine";
    public static final String FROM_OTHER_USER_CENTER = "other";

    public long userId;

    public int resultCode;

    public String userName;

    public String headIconUrl;

    public String sex;

    public String info;

    public long messageCount;

    public long newMessageCount;

    public long followedTagCount;

    public long videoCount;

    public long likedVideoCount;

    public long userFollowCount;

    public long userFollowMeCount;

    public String from;

    public boolean isFollowed;

    public long point;

    public boolean isSpecial;

}
