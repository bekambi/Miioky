package com.bekambimouen.miiokychallenge.profile.model;

public class CompletProfilModel {
    private String user_id;
    private String username;
    private String profileUrl;
    private String bio;

    public CompletProfilModel() {
    }

    public CompletProfilModel(String user_id, String username, String profileUrl, String bio) {
        this.user_id = user_id;
        this.username = username;
        this.profileUrl = profileUrl;
        this.bio = bio;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

