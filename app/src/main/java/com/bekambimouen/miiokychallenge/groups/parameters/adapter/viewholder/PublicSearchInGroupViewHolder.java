package com.bekambimouen.miiokychallenge.groups.parameters.adapter.viewholder;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PublicSearchInGroupViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "MyViewHolder";
    // widgets
    private final ImageView profile_photo;
    public final TextView group_name, total_members, group_msg, bouton_voir, bouton_rejoindre;
    public final RelativeLayout relLayout_group_name;

    // vars
    private final AppCompatActivity context;
    private String portee;
    private int n = 0, t = 0;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public PublicSearchInGroupViewHolder(AppCompatActivity context, @NonNull View itemView) {
        super(itemView);
        this.context = context;

        group_name = itemView.findViewById(R.id.group_name);
        total_members = itemView.findViewById(R.id.total_members);
        group_msg = itemView.findViewById(R.id.group_msg);
        bouton_voir = itemView.findViewById(R.id.bouton_voir);
        bouton_rejoindre = itemView.findViewById(R.id.bouton_rejoindre);
        profile_photo = itemView.findViewById(R.id.profile_photo);
        relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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

                        portee = context.getString(R.string.title_public);

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
            bouton_rejoindre.setVisibility(View.GONE);
            bouton_voir.setVisibility(View.VISIBLE);

        } else {
            isFollowing(user_groups);
        }

        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(), user_id, "", user_groups.getGroup_id());

        bouton_rejoindre.setOnClickListener(view -> {
            // hide the button in notification "invite user to be member of group"
            Query query1 = myRef.child(context.getString(R.string.dbname_notification))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
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

        Query query = myRef.child(context.getString(R.string.dbname_group_following))
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
                if (!snapshot.exists()) {
                    setUnfollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

