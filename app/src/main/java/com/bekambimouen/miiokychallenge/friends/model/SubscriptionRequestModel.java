package com.bekambimouen.miiokychallenge.friends.model;

public class SubscriptionRequestModel {
    private String user_id;
    private long date_created;

    public SubscriptionRequestModel() {
    }

    public SubscriptionRequestModel(String user_id, long date_created) {
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}

