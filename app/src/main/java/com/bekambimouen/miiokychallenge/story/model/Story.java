package com.bekambimouen.miiokychallenge.story.model;

public class Story {

    private String user_id;
    private String mediaUrl;
    private String storyid;
    private long timestart,timeend;
    private String caption;
    // suppressed the post
    private boolean suppressed;

    public Story() {
    }

    public Story(String mediaUrl, String storyid, String user_id, long timestart, long timeend,
                 String caption, boolean suppressed) {
        this.mediaUrl = mediaUrl;
        this.storyid = storyid;
        this.user_id = user_id;
        this.timestart = timestart;
        this.timeend = timeend;
        this.caption = caption;
        this.suppressed = suppressed;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
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

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

