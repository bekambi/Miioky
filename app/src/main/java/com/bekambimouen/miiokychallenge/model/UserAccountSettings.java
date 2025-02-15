package com.bekambimouen.miiokychallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccountSettings implements Parcelable {
    private String user_id;
    private String username;
    private String fullName;
    private String profileUrl;
    private String full_profileUrl;
    private String photo_id;
    private String bio;
    private String status;

    public UserAccountSettings() {
    }

    public UserAccountSettings(String user_id, String username, String fullName, String profileUrl,
                               String full_profileUrl, String photo_id, String bio, String status) {
        this.user_id = user_id;
        this.username = username;
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.full_profileUrl = full_profileUrl;
        this.photo_id = photo_id;
        this.bio = bio;
        this.status = status;
    }

    protected UserAccountSettings(Parcel in) {
        user_id = in.readString();
        username = in.readString();
        fullName = in.readString();
        profileUrl = in.readString();
        full_profileUrl = in.readString();
        photo_id = in.readString();
        bio = in.readString();
        status = in.readString();
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFull_profileUrl() {
        return full_profileUrl;
    }

    public void setFull_profileUrl(String full_profileUrl) {
        this.full_profileUrl = full_profileUrl;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(username);
        parcel.writeString(fullName);
        parcel.writeString(profileUrl);
        parcel.writeString(full_profileUrl);
        parcel.writeString(photo_id);
        parcel.writeString(bio);
        parcel.writeString(status);
    }
}
