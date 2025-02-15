package com.bekambimouen.miiokychallenge.model;

public class StringAboutTimeModel {
    private String user_id;
    private long timeStamp;

    public StringAboutTimeModel() {
    }

    public StringAboutTimeModel(String user_id, long timeStamp) {
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
