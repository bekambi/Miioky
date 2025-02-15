package com.bekambimouen.miiokychallenge.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoItem {
    private Bitmap videoThumb;
    private String videoDuration;
    private Uri videoUri;

    String video;
    boolean isSelected;

    public VideoItem(){}

    public VideoItem(Bitmap videoThumb, String videoDuration, Uri videoUri) {
        this.videoThumb = videoThumb;
        this.videoDuration = videoDuration;
        this.videoUri = videoUri;
    }

    public Bitmap getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(Bitmap videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
