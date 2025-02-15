package com.bekambimouen.miiokychallenge.fun.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;

import java.util.List;

public class BattleModel_fun implements Parcelable {
    private String user_id;
    private String caption;
    private String thumbnail;
    private String url;
    private String photo_id;
    private String tags;
    private long date_created;
    private List<Like> likes;
    private List<Crush> crush;
    private List<Comment> comments;
    // suppressed the post
    private boolean suppressed;
    private long views;

    public BattleModel_fun() {
    }

    public BattleModel_fun(String url, String thumbnail, String caption, long date_created, String photo_id,
                           String user_id, String tags, List<Like> likes, List<Crush> crush, List<Comment> comments,
                           boolean suppressed, long views) {
        this.url = url;
        this.thumbnail = thumbnail;
        this.caption = caption;
        this.date_created = date_created;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
        this.likes = likes;
        this.crush = crush;
        this.comments = comments;
        this.suppressed = suppressed;
        this.views = views;
    }

    protected BattleModel_fun(Parcel in) {
        suppressed = in.readByte() != 0;
        url = in.readString();
        thumbnail = in.readString();
        caption = in.readString();
        date_created = in.readLong();
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
        views = in.readLong();
    }

    public static final Creator<BattleModel_fun> CREATOR = new Creator<BattleModel_fun>() {
        @Override
        public BattleModel_fun createFromParcel(Parcel in) {
            return new BattleModel_fun(in);
        }

        @Override
        public BattleModel_fun[] newArray(int size) {
            return new BattleModel_fun[size];
        }
    };

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Crush> getCrush() {
        return crush;
    }

    public void setCrush(List<Crush> crush) {
        this.crush = crush;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (suppressed ? 1 : 0));
        parcel.writeString(thumbnail);
        parcel.writeString(url);
        parcel.writeString(caption);
        parcel.writeLong(date_created);
        parcel.writeString(photo_id);
        parcel.writeString(user_id);
        parcel.writeString(tags);
        parcel.writeLong(views);
    }
}

