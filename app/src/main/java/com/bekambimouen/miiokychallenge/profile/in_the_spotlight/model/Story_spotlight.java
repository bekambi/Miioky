package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Story_spotlight implements Parcelable {

    boolean isSelected;
    private String mediaUrl,storyid,user_id;
    private String caption;
    // suppressed the post
    private boolean suppressed;

    public Story_spotlight() {
    }

    public Story_spotlight(String mediaUrl, String storyid, String user_id,
                           String caption, boolean suppressed) {
        this.mediaUrl = mediaUrl;
        this.storyid = storyid;
        this.user_id = user_id;
        this.caption = caption;
        this.suppressed = suppressed;
    }

    protected Story_spotlight(Parcel in) {
        suppressed = in.readByte() != 0;
        isSelected = in.readByte() != 0;
        mediaUrl = in.readString();
        storyid = in.readString();
        user_id = in.readString();
        caption = in.readString();
    }

    public static final Creator<Story_spotlight> CREATOR = new Creator<Story_spotlight>() {
        @Override
        public Story_spotlight createFromParcel(Parcel in) {
            return new Story_spotlight(in);
        }

        @Override
        public Story_spotlight[] newArray(int size) {
            return new Story_spotlight[size];
        }
    };

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (suppressed ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeString(mediaUrl);
        parcel.writeString(storyid);
        parcel.writeString(user_id);
        parcel.writeString(caption);
    }
}

