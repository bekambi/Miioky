package com.bekambimouen.miiokychallenge.preload_image;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bumptech.glide.Glide;

public class PreloadSuggestionVideos {
    public static void getPreloadSuggestionVideos(AppCompatActivity context, BattleModel_fun model) {
        Glide.with(context).load(model.getThumbnail()).preload();
    }
}
