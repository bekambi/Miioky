package com.bekambimouen.miiokychallenge.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Comment {
    private String url;
    private String thumbnail;
    private String comment;
    private String user_id;
    private String commentKey;
    private List<Like> likes;
    private long date_created;
    // suppressed the comment
    private String suppressed;

    public Comment() { }

    public Comment(String url, String thumbnail, String comment, String user_id, String commentKey,
                   List<Like> likes, long date_created, String suppressed) {
        this.url = url;
        this.thumbnail = thumbnail;
        this.comment = comment;
        this.user_id = user_id;
        this.commentKey = commentKey;
        this.likes = likes;
        this.date_created = date_created;
        this.suppressed = suppressed;
    }

    public String getSuppressed() {
        return suppressed;
    }

    public void setSuppressed(String suppressed) {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    @NonNull
    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", likes=" + likes +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}
