package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model;

public class SpolightCover {
    private String mediaUrl,storyid,user_id;
    private String title;

    public SpolightCover() {
    }

    public SpolightCover(String mediaUrl, String storyid, String user_id, String title) {
        this.mediaUrl = mediaUrl;
        this.storyid = storyid;
        this.user_id = user_id;
        this.title = title;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

