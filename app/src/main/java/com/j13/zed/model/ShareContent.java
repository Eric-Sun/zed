package com.j13.zed.model;

/**
 * Created by chengzhe on 15/12/29.
 */
public class ShareContent {
    private String mTitle;
    private String mThumb;
    private String mTargetUrl;
    private String mMediaUrl;
    private String mPlayCount;

    public String getPlayCount() {
        return mPlayCount;
    }

    public void setPlayCount(String mPlayCount) {
        this.mPlayCount = mPlayCount;
    }

    public ShareContent() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getThumb() {
        return mThumb;
    }

    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    public String getTargetUrl() {
        return mTargetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        mTargetUrl = targetUrl;
    }

    public String getMediaUrl() {
        return mMediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        mMediaUrl = mediaUrl;
    }

}
