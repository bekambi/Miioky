package com.bekambimouen.miiokychallenge.model;

import androidx.annotation.NonNull;

public class UserSettings {
    private UserAccountSettings settings;

    public UserSettings() {
    }

    public UserSettings(UserAccountSettings settings) {
        this.settings = settings;
    }

    public UserAccountSettings getSettings() {
        return settings;
    }

    public void setSettings(UserAccountSettings settings) {
        this.settings = settings;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserSettings{" +
                ", settings=" + settings +
                '}';
    }
}
