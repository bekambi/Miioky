package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;

import java.util.Map;
import java.util.Objects;

public class Util_FriendsFollowerFollowing {
    public static FriendsFollowerFollowing getFriendsFollowerFollowing(AppCompatActivity context, Map<Object, Object> objectMap) {
        FriendsFollowerFollowing followerFollowing = new FriendsFollowerFollowing();

        followerFollowing.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        followerFollowing.setUnSubscribe(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_unSubscribe))).toString()));
        followerFollowing.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

        return followerFollowing;
    }
}

