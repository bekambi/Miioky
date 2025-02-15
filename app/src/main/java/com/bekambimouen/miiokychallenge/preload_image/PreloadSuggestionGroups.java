package com.bekambimouen.miiokychallenge.preload_image;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bumptech.glide.Glide;

public class PreloadSuggestionGroups {
    public static void getPreloadSuggestionGroups(AppCompatActivity context, UserGroups groups) {
        Glide.with(context).load(groups.getProfile_photo()).preload();
    }
}
