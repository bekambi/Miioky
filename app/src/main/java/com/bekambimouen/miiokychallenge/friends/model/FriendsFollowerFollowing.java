package com.bekambimouen.miiokychallenge.friends.model;

public class FriendsFollowerFollowing {
    private String user_id;
    private boolean unSubscribe;
    private long date_created;

    public FriendsFollowerFollowing() {
    }

    public FriendsFollowerFollowing(String user_id, boolean unSubscribe, long date_created) {
        this.user_id = user_id;
        this.unSubscribe = unSubscribe;
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isUnSubscribe() {
        return unSubscribe;
    }

    public void setUnSubscribe(boolean unSubscribe) {
        this.unSubscribe = unSubscribe;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}

