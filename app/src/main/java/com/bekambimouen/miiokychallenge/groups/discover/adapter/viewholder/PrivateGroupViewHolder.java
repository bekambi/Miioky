package com.bekambimouen.miiokychallenge.groups.discover.adapter.viewholder;

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
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.BottomSheetPrivateGroupDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.BottomSheetPrivateGroupDiscoverWithRules;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.AcceptPrivateRules;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
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

public class PrivateGroupViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PrivateGroupViewHolder";

    private final ImageView profile_photo;
    private final TextView group_name, nber_of_members;
    private final TextView button_cancel, button_join;
    public final RelativeLayout close;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_view_overlay;
    private BottomSheetPrivateGroupDiscoverWithRules bottomSheetPrivateGroupDiscoverWithRules;
    private BottomSheetPrivateGroupDiscover bottomSheetPrivateGroupDiscover;
    private final ArrayList<String> admins_id_list;
    private String portee;
    private int t = 0;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public PrivateGroupViewHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        profile_photo = itemView.findViewById(R.id.profile_photo);
        close = itemView.findViewById(R.id.close);
        nber_of_members = itemView.findViewById(R.id.nber_of_members);
        group_name = itemView.findViewById(R.id.group_name);
        button_cancel = itemView.findViewById(R.id.button_cancel);
        button_join = itemView.findViewById(R.id.button_join);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        admins_id_list = new ArrayList<>();
    }

    public void bind(UserGroups user_groups) {
        group_name.setText(user_groups.getGroup_name());

        if (!context.isFinishing()) {
            Glide.with(context)
                    .asBitmap()
                    .load(user_groups.getProfile_photo())
                    .into(profile_photo);
        }

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                    t++;
                }

                portee = context.getString(R.string.title_private);

                if (t == 0) {
                    nber_of_members.setText(Html.fromHtml(context.getString(R.string.group)+" "+
                            context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                            "<b>"+ (t+1)+"</b> "+context.getString(R.string.member_minus)));

                } else {
                    nber_of_members.setText(Html.fromHtml(context.getString(R.string.group)+" "+
                            context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                            "<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
            bottomSheetPrivateGroupDiscoverWithRules = new BottomSheetPrivateGroupDiscoverWithRules(context, user_groups);
            profile_photo.setOnClickListener(view -> {
                if (bottomSheetPrivateGroupDiscoverWithRules.isAdded())
                    return;
                bottomSheetPrivateGroupDiscoverWithRules.show(context.getSupportFragmentManager(), "TAG");
            });

            group_name.setOnClickListener(view -> {
                if (bottomSheetPrivateGroupDiscoverWithRules.isAdded())
                    return;
                bottomSheetPrivateGroupDiscoverWithRules.show(context.getSupportFragmentManager(), "TAG");
            });

            nber_of_members.setOnClickListener(view -> {
                if (bottomSheetPrivateGroupDiscoverWithRules.isAdded())
                    return;
                bottomSheetPrivateGroupDiscoverWithRules.show(context.getSupportFragmentManager(), "TAG");
            });

        } else {
            bottomSheetPrivateGroupDiscover = new BottomSheetPrivateGroupDiscover(context, user_groups);
            profile_photo.setOnClickListener(view -> {
                if (bottomSheetPrivateGroupDiscover.isAdded())
                    return;
                bottomSheetPrivateGroupDiscover.show(context.getSupportFragmentManager(), "TAG");
            });

            group_name.setOnClickListener(view -> {
                if (bottomSheetPrivateGroupDiscover.isAdded())
                    return;
                bottomSheetPrivateGroupDiscover.show(context.getSupportFragmentManager(), "TAG");
            });

            nber_of_members.setOnClickListener(view -> {
                if (bottomSheetPrivateGroupDiscover.isAdded())
                    return;
                bottomSheetPrivateGroupDiscover.show(context.getSupportFragmentManager(), "TAG");
            });
        }

        isFollowing(user_groups);

        HashMap<Object, Object> map = Utils_Map_GroupMembershipModel.setGroupMembershipModel(user_id, user_groups.getAdmin_master(), user_groups.getGroup_id());

        // top button
        button_join.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
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
                setFollowing();FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_Membership_request_following))
                        .child(user_id)
                        .child(user_groups.getGroup_id())
                        .setValue(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_Membership_request_follower))
                        .child(user_groups.getGroup_id())
                        .child(user_id)
                        .setValue(map);
                setFollowing();

                // send notification yo all admins
                Query query1 = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id());

                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
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
                        }
                    }

                    @Override
                    public void onCancelled(@org.jetbrains.annotations.NotNull DatabaseError error) {

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
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_join.setVisibility(View.GONE);
        button_cancel.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_join.setVisibility(View.VISIBLE);
        button_cancel.setVisibility(View.GONE);
    }

    private void isFollowing(UserGroups userGroups){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(context.getString(R.string.dbname_group_Membership_request_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(userGroups.getGroup_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(@org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }
}

