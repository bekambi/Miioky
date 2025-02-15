package com.bekambimouen.miiokychallenge.market_place.model;

public class StoreFollowersFollowing {
    private String store_owner;
    private String user_id;
    private String store_id;
    private long date_following;

    public StoreFollowersFollowing() {
    }

    public StoreFollowersFollowing(String store_owner, String user_id, String store_id, long date_following) {
        this.store_owner = store_owner;
        this.user_id = user_id;
        this.store_id = store_id;
        this.date_following = date_following;
    }

    public String getStore_owner() {
        return store_owner;
    }

    public void setStore_owner(String store_owner) {
        this.store_owner = store_owner;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public long getDate_following() {
        return date_following;
    }

    public void setDate_following(long date_following) {
        this.date_following = date_following;
    }
}

