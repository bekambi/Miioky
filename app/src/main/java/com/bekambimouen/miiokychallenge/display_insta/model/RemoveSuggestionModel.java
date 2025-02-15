package com.bekambimouen.miiokychallenge.display_insta.model;

public class RemoveSuggestionModel {
    private String user_id;

    public RemoveSuggestionModel() {
    }

    public RemoveSuggestionModel(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
