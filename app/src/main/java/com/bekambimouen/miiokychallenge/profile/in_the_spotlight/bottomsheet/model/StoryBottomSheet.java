package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.bottomsheet.model;

import androidx.annotation.NonNull;

public class StoryBottomSheet {
    private String title;

    public StoryBottomSheet() {
    }

    public StoryBottomSheet(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "ItemBottomSheet{" +
                ", title='" + title + '\'' +
                '}';
    }
}

