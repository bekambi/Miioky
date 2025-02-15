package com.bekambimouen.miiokychallenge.preload_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PreloadMyChallengeImages {
    public static void getPreloadMyChallengeImages(AppCompatActivity context, ModelInvite model) {

        if (model.isImage()) {
            Glide.with(context).load(model.getInvite_url()).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model);

        } else if (model.isVideo()) {
            Glide.with(context).load(model.getThumbnail_invite()).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model);

        } else if (model.isGroup_image()) {
            Glide.with(context).load(model.getInvite_url()).preload();

            //get the user profile image
            getUserProfilePhoto(context, model);
            //get the group profile image
            getGroupProfilePhoto(context, model);

        } else if (model.isGroup_video()) {
            Glide.with(context).load(model.getThumbnail_invite()).preload();

            //get the user profile image
            getUserProfilePhoto(context, model);
            //get the group profile image
            getGroupProfilePhoto(context, model);

        }
    }

    // preload profile photo
    private static void getUserProfilePhoto(AppCompatActivity context, ModelInvite model) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getInvite_userID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Glide.with(context).load(user.getProfileUrl()).preload();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // preload group profile photo
    private static void getGroupProfilePhoto(AppCompatActivity context, ModelInvite model) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_user_group))
                .child(model.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(model.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    Glide.with(context).load(user_groups.getProfile_photo()).preload();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
