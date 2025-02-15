package com.bekambimouen.miiokychallenge.groups.bottomsheet.model;

import androidx.annotation.NonNull;

public class BottomSheetPubModel {
    private int imageId;
    private String title;

    public BottomSheetPubModel() {
    }

    public BottomSheetPubModel(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
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
                "imageId=" + imageId +
                ", title='" + title + '\'' +
                '}';
    }
}

