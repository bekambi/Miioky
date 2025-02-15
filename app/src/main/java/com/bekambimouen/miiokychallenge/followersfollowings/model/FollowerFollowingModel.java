package com.bekambimouen.miiokychallenge.followersfollowings.model;

public class FollowerFollowingModel {
    private String user_id;
    private boolean to_block;
    private long date_created;

    public FollowerFollowingModel() {
    }

    public FollowerFollowingModel(String user_id, boolean to_block, long date_created) {
        this.user_id = user_id;
        this.to_block = to_block;
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isTo_block() {
        return to_block;
    }

    public void setTo_block(boolean to_block) {
        this.to_block = to_block;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}

