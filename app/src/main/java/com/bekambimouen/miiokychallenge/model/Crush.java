package com.bekambimouen.miiokychallenge.model;

import androidx.annotation.NonNull;

public class Crush {

    private String user_id;

    public Crush() { }

    public Crush(String user_id) {
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
        return "Crush{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
