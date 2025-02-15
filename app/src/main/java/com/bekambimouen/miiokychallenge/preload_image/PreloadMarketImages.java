package com.bekambimouen.miiokychallenge.preload_image;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bumptech.glide.Glide;

public class PreloadMarketImages {
    public static void getPreloadMarketImages(AppCompatActivity context, MarketModel model) {

        if (model.isStore() || model.isRestaurant() || model.isBakery()) {
            Glide.with(context).load(model.getProfile_photo()).preload();

        } else if (model.isSell() || model.isLocation()) {
            Glide.with(context).load(model.getMain_photo()).preload();

        }
    }

    public static void getPreloadStoreImages(AppCompatActivity context, MarketModel model) {

        if (model.isStore() || model.isRestaurant() || model.isBakery()) {
            Glide.with(context).load(model.getProfile_photo()).preload();

        }
    }
}
