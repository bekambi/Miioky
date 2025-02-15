package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.model.StoreFollowersFollowing;

import java.util.Map;
import java.util.Objects;

public class Util_StoreFollowersFollowing {
    public static StoreFollowersFollowing getStoreFollowersFollowing(AppCompatActivity context, Map<Object, Object> objectMap) {
        StoreFollowersFollowing storeModel = new StoreFollowersFollowing();

        storeModel.setStore_owner(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_owner))).toString());
        storeModel.setStore_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_id))).toString());
        storeModel.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        storeModel.setDate_following(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_following))).toString()));

        return storeModel;
    }
}

