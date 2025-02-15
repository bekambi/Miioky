package com.bekambimouen.miiokychallenge.model;

import androidx.annotation.NonNull;

import java.util.List;

public class CommentResponse {
    private boolean userAnswer;
    private String url;
    private String thumbnail;
    private String comment;
    private String user_id;
    private String commentParentKey;
    private String commentKey;
    private String answeringUsername;
    private List<Like> likes;
    private long date_created;
    // suppressed the comment
    private String suppressed;

    public CommentResponse() { }

    public CommentResponse(boolean userAnswer, String url, String thumbnail, String comment, String user_id,
                           String commentParentKey, String commentKey, String answeringUsername, List<Like> likes,
                           long date_created, String suppressed) {
        this.userAnswer = userAnswer;
        this.url = url;
        this.thumbnail = thumbnail;
        this.comment = comment;
        this.user_id = user_id;
        this.commentParentKey = commentParentKey;
        this.commentKey = commentKey;
        this.answeringUsername = answeringUsername;
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

    public boolean isUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(boolean userAnswer) {
        this.userAnswer = userAnswer;
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

    public String getCommentParentKey() {
        return commentParentKey;
    }

    public void setCommentParentKey(String commentParentKey) {
        this.commentParentKey = commentParentKey;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    public String getAnsweringUsername() {
        return answeringUsername;
    }

    public void setAnsweringUsername(String answeringUsername) {
        this.answeringUsername = answeringUsername;
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

