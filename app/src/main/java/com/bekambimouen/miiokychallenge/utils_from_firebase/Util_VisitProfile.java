package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.VisitProfileModel;
import com.google.firebase.database.DataSnapshot;

import java.util.Map;
import java.util.Objects;

public class Util_VisitProfile {
    public static VisitProfileModel getUser(AppCompatActivity context, Map<Object, Object> objectMap) {
        VisitProfileModel user = new VisitProfileModel();

        user.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        user.setFullName(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_fullName))).toString());
        user.setUsername(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_username))).toString());
        user.setDate_last_profile_visit(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_last_profile_visit))).toString()));
        user.setViews(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_views))).toString()));

        return user;
    }
}
