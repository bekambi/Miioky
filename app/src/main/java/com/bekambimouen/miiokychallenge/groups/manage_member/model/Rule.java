package com.bekambimouen.miiokychallenge.groups.manage_member.model;

public class Rule {
    private String title;
    private String details;

    public Rule() {
    }

    public Rule(String title, String details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

