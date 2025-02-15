package com.bekambimouen.miiokychallenge.groups.discover.adapter.viewholder;

import android.content.Intent;
import android.text.Html;
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
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.BottomSheetPublicGroupDiscover;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_NotificationModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PublicGroupViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PublicGroupViewHolder";

    private final ImageView profile_photo;
    private final TextView group_name, nber_of_members;
    private final TextView bouton_voir, bouton_rejoindre;
    public final RelativeLayout close;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_view_overlay;
    private BottomSheetPublicGroupDiscover bottomSheetPublicGroupDiscover;
    private String portee;
    private int t = 0;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public PublicGroupViewHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        profile_photo = itemView.findViewById(R.id.profile_photo);
        close = itemView.findViewById(R.id.close);
        nber_of_members = itemView.findViewById(R.id.nber_of_members);
        group_name = itemView.findViewById(R.id.group_name);
        bouton_voir = itemView.findViewById(R.id.bouton_voir);
        bouton_rejoindre = itemView.findViewById(R.id.bouton_rejoindre);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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

                portee = context.getString(R.string.title_public);

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

        bottomSheetPublicGroupDiscover = new BottomSheetPublicGroupDiscover(context, user_groups);
        profile_photo.setOnClickListener(view -> {
            if (bottomSheetPublicGroupDiscover.isAdded())
                return;
            bottomSheetPublicGroupDiscover.show(context.getSupportFragmentManager(), "TAG");
        });

        group_name.setOnClickListener(view -> {
            if (bottomSheetPublicGroupDiscover.isAdded())
                return;
            bottomSheetPublicGroupDiscover.show(context.getSupportFragmentManager(), "TAG");
        });

        nber_of_members.setOnClickListener(view -> {
            if (bottomSheetPublicGroupDiscover.isAdded())
                return;
            bottomSheetPublicGroupDiscover.show(context.getSupportFragmentManager(), "TAG");
        });

        isFollowing(user_groups);

        // get the following model data
        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(), user_id, "", user_groups.getGroup_id());

        bouton_rejoindre.setOnClickListener(view -> {
            // hide the button in notification "invite user to be member of group"
            Query query = myRef.child(context.getString(R.string.dbname_notification))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                        if (notification.isInvitation_in_group()) {
                            // show or hide dialog box
                            if (notification.isInvite_to_join_the_group()
                                    && !notification.isAccepted_invitation_to_join_the_group()
                                    && !notification.isRefuse_invitation_to_join_the_group()) {

                                HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(),
                                        user_id, notification.getAdding_by(), user_groups.getGroup_id());

                                // follow the group
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_following))
                                        .child(user_id)
                                        .child(user_groups.getGroup_id())
                                        .setValue(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_followers))
                                        .child(user_groups.getGroup_id())
                                        .child(user_id)
                                        .setValue(map);

                                setFollowing();

                                // update current user notification
                                HashMap<String, Object> user_notification = new HashMap<>();
                                user_notification.put("invite_to_join_the_group", true);
                                user_notification.put("accepted_invitation_to_join_the_group", true);
                                user_notification.put("hide_buttons", true);

                                myRef.child(context.getString(R.string.dbname_notification))
                                        .child(user_id)
                                        .child(notification.getNotification_id())
                                        .updateChildren(user_notification);

                                // send notification to member who invited
                                Date date = new Date();
                                String new_key = myRef.push().getKey();
                                HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                        true, false, false, false, false,
                                        false,false, false, false,
                                        false, false, false, false, false, false,
                                        false,
                                        false, false, false, false, false,
                                        false, false,
                                        false, false, false, false, false,
                                        false, false,
                                        true, "", false, true,
                                        false, false,
                                        true,false, false,
                                        notification.getAdding_by(), new_key, user_id, user_groups.getAdmin_master(),
                                        "", user_groups.getGroup_id(), date.getTime(),
                                        false, false,
                                        false, false, false, false, false, false, false, false,
                                        false, "", "", "", false, "", "", "", false,
                                        "", "", "", "", "", 0,
                                        "", "", 0, "", "", "",
                                        false, false, false, false,
                                        false, false, false,
                                        false, false);

                                assert new_key != null;
                                myRef.child(context.getString(R.string.dbname_notification))
                                        .child(notification.getAdding_by())
                                        .child(new_key)
                                        .setValue(map_notification);

                                // add badge number
                                HashMap<String, Object> map_number = new HashMap<>();
                                map_number.put("user_id", notification.getAdding_by());

                                myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                        .child(notification.getAdding_by())
                                        .child(new_key)
                                        .setValue(map_number);

                                // remove person who has been invited in the list
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                        .child(user_groups.getGroup_id())
                                        .child(user_id)
                                        .removeValue();
                            }
                        } else {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user_id)
                                    .child(user_groups.getGroup_id())
                                    .setValue(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_followers))
                                    .child(user_groups.getGroup_id())
                                    .child(user_id)
                                    .setValue(map);
                            setFollowing();
                        }
                    }

                    if (!snapshot.exists()) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(user_groups.getGroup_id())
                                .setValue(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(user_groups.getGroup_id())
                                .child(user_id)
                                .setValue(map);
                        setFollowing();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        bouton_voir.setOnClickListener(v -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myJson = gson.toJson(user_groups);

            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupViewProfile.class);
            intent.putExtra("user_groups", myJson);
            context.startActivity(intent);
        });
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        bouton_rejoindre.setVisibility(View.GONE);
        bouton_voir.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        bouton_rejoindre.setVisibility(View.VISIBLE);
        bouton_voir.setVisibility(View.GONE);
    }

    private void isFollowing(UserGroups userGroups){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(context.getString(R.string.dbname_group_following))
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

