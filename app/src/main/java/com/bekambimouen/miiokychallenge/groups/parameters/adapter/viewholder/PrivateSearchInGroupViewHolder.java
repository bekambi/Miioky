package com.bekambimouen.miiokychallenge.groups.parameters.adapter.viewholder;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.AcceptPrivateRules;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupMembershipModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrivateSearchInGroupViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "MyViewHolder";
    // widgets
    private final ImageView profile_photo;
    private final TextView group_name, total_members, group_msg, button_cancel, button_join, bouton_voir;
    public final RelativeLayout relLayout_group_name;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_view_overlay;
    private String portee;
    private final ArrayList<String> admins_id_list;
    private int n = 0, t = 0;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public PrivateSearchInGroupViewHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        group_name = itemView.findViewById(R.id.group_name);
        total_members = itemView.findViewById(R.id.total_members);
        group_msg = itemView.findViewById(R.id.group_msg);
        button_cancel = itemView.findViewById(R.id.button_cancel);
        button_join = itemView.findViewById(R.id.button_join);
        bouton_voir = itemView.findViewById(R.id.bouton_voir);
        profile_photo = itemView.findViewById(R.id.profile_photo);
        relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        admins_id_list = new ArrayList<>();
    }

    public void bind(UserGroups user_groups) {
        t = 0;
        group_name.setText(user_groups.getGroup_name());

        Query query = myRef
                .child(context.getString(R.string.dbname_group))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                n = 0;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    n++;
                }

                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id());
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        t = 0;
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                            t++;
                        }

                        portee = context.getString(R.string.title_private);

                        total_members.setText(Html.fromHtml(context.getString(R.string.group)+" "
                                +context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                                "<b>"+ (t+1)+"</b> "+context.getString(R.string.members)
                                +" <b>" +n+"</b> "+context.getString(R.string.publications)));

                        group_msg.setText(user_groups.getGroup_message());

                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (user_groups.getAdmin_master().equals(user_id)) {
            button_join.setVisibility(View.GONE);
            button_cancel.setVisibility(View.GONE);
            bouton_voir.setVisibility(View.VISIBLE);

        } else {
            Query query2 = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+ds.getKey());
                        isAlreadyFollowing(user_groups);
                    }
                    if (!snapshot.exists()) {
                        isFollowing(user_groups);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        HashMap<Object, Object> map = Utils_Map_GroupMembershipModel.setGroupMembershipModel(user_id, user_groups.getAdmin_master(), user_groups.getGroup_id());

        // top button
        button_join.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                Intent intent = new Intent(context, AcceptPrivateRules.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);

            } else {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_Membership_request_following))
                        .child(user_id)
                        .child(user_groups.getGroup_id())
                        .setValue(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_Membership_request_follower))
                        .child(user_groups.getGroup_id())
                        .child(user_id)
                        .setValue(map);

                // send notification to all admins
                Query query1 = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id());

                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // first add admin aster id
                        admins_id_list.add(user_groups.getAdmin_master());

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            if (follower.isAdminInGroup() || follower.isModerator())
                                admins_id_list.add(follower.getUser_id());
                        }

                        // send notification
                        for (int i = 0; i < admins_id_list.size(); i++) {
                            Date date = new Date();
                            String new_key = myRef.push().getKey();
                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                    false, false, false, false, false,
                                    false,false, false, false,
                                    false, false, false, false, false, false,
                                    false,
                                    false, false, false, false, false,
                                    false, true,
                                    false, false, false, false, false,
                                    false, false,
                                    false, "", false, false, false, false,
                                    true,false, false,
                                    admins_id_list.get(i), new_key, user_id, user_groups.getAdmin_master(),
                                    "", user_groups.getGroup_id(), date.getTime(),
                                    false, false,
                                    false, false, false, false, false, false, false, false,
                                    false, "", "", "", false, "", "", "", false,
                                    "", "", "", "", "", 0,
                                    "", "", 0, "", "", "",
                                    true, true, false, false,
                                    false, false, false,
                                    false, false);

                            assert new_key != null;
                            myRef.child(context.getString(R.string.dbname_notification))
                                    .child(admins_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map_notification);

                            // add badge number
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", admins_id_list.get(i));

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(admins_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map_number);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        button_cancel.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_following))
                    .child(user_id)
                    .child(user_groups.getGroup_id())
                    .removeValue();

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_follower))
                    .child(user_groups.getGroup_id())
                    .child(user_id)
                    .removeValue();
            setUnfollowing();
        });


        // if current user is admin master
        if (user_groups.getAdmin_master().equals(user_id)) {
            Gson gson = new Gson();
            String myJson = gson.toJson(user_groups);

            bouton_voir.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewAdmin.class);
                intent.putExtra("user_groups", myJson);
                context.startActivity(intent);
            });

        } else {
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        Gson gson = new Gson();
                        String myJson = gson.toJson(user_groups);

                        // if user already following the group
                        bouton_voir.setOnClickListener(v -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (follower.isModerator() || follower.isAdminInGroup()) {
                                intent = new Intent(context, GroupViewAdmin.class);
                            } else {
                                intent = new Intent(context, GroupViewProfile.class);
                            }
                            intent.putExtra("user_groups", myJson);
                            context.startActivity(intent);
                        });
                    }

                    // if user not following the group
                    if (!snapshot.exists()) {
                        bouton_voir.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user_groups);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                            intent.putExtra("user_groups", myGson);
                            context.startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_join.setVisibility(View.GONE);
        bouton_voir.setVisibility(View.GONE);
        button_cancel.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_join.setVisibility(View.VISIBLE);
        bouton_voir.setVisibility(View.GONE);
        button_cancel.setVisibility(View.GONE);
    }

    private void isFollowing(UserGroups userGroups){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_group_Membership_request_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(userGroups.getGroup_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing();
                }

                if (!snapshot.exists())
                    setUnfollowing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAlreadyFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_join.setVisibility(View.GONE);
        button_cancel.setVisibility(View.GONE);
        bouton_voir.setVisibility(View.VISIBLE);
    }

    private void setAlreadyUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_join.setVisibility(View.VISIBLE);
        button_cancel.setVisibility(View.GONE);
        bouton_voir.setVisibility(View.GONE);
    }

    private void isAlreadyFollowing(UserGroups userGroups){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(userGroups.getGroup_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setAlreadyFollowing();
                }
                if (!snapshot.exists()) {
                    setAlreadyUnfollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

