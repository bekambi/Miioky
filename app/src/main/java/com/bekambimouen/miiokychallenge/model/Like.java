package com.bekambimouen.miiokychallenge.model;

import androidx.annotation.NonNull;

public class Like {

    private String user_id;

    public Like() { }

    public Like(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Like{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
