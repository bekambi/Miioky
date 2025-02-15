package com.bekambimouen.miiokychallenge.model;

public class VisitProfileModel {
    private String user_id;
    private String fullName;
    private String username;
    private long views;
    private long date_last_profile_visit;

    public VisitProfileModel() {
    }

    public VisitProfileModel(String user_id, String fullName, String username, long views, long date_last_profile_visit) {
        this.user_id = user_id;
        this.fullName = fullName;
        this.username = username;
        this.views = views;
        this.date_last_profile_visit = date_last_profile_visit;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public long getDate_last_profile_visit() {
        return date_last_profile_visit;
    }

    public void setDate_last_profile_visit(long date_last_profile_visit) {
        this.date_last_profile_visit = date_last_profile_visit;
    }
}
