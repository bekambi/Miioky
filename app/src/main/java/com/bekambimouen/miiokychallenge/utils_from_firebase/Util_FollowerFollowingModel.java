package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.followersfollowings.model.FollowerFollowingModel;

import java.util.Map;
import java.util.Objects;

public class Util_FollowerFollowingModel {
    public static FollowerFollowingModel getUtil_FollowerFollowingModel(AppCompatActivity context, Map<Object, Object> objectMap) {
        FollowerFollowingModel followingModel = new FollowerFollowingModel();

        followingModel.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        //       followingModel.setTo_block(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_to_block))).toString()));
        followingModel.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

        return followingModel;
    }
}

