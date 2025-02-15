package com.bekambimouen.miiokychallenge.display_insta.suggestion_groups.adapter.viewholder;

import android.app.Dialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.BottomSheetPublicGroupDiscover;
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

public class PublicGroupDisplayViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PublicGroupDisplayViewHolder";

    // widgets
    private final ImageView profile_photo, close;
    private final TextView group_name, nber_of_members;
    private final TextView bouton_quitter, bouton_rejoindre;
    private final RelativeLayout relLayout_member_button;

    // vars
    private final AppCompatActivity context;
    private BottomSheetPublicGroupDiscover bottomSheetPublicGroupDiscover;
    private String portee;
    private int t = 0;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public PublicGroupDisplayViewHolder(AppCompatActivity context, View itemView) {
        super(itemView);
        this.context = context;

        profile_photo = itemView.findViewById(R.id.profile_photo);
        close = itemView.findViewById(R.id.close);
        group_name = itemView.findViewById(R.id.group_name);
        nber_of_members = itemView.findViewById(R.id.nber_of_members);
        bouton_quitter = itemView.findViewById(R.id.bouton_quitter);
        bouton_rejoindre = itemView.findViewById(R.id.bouton_rejoindre);
        relLayout_member_button = itemView.findViewById(R.id.relLayout_member_button);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public void bind(UserGroups user_groups) {
        group_name.setText(user_groups.getGroup_name());

        Glide.with(context.getApplicationContext())
                .load(user_groups.getProfile_photo())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(profile_photo);

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                    t++;
                }

                portee = context.getString(R.string.title_public);

                nber_of_members.setText(Html.fromHtml(context.getString(R.string.group)+" "+
                        context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                        "<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));
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

        close.setOnClickListener(view -> {
            // show dialog box
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = v.findViewById(R.id.dialog_title);
            TextView dialog_text = v.findViewById(R.id.dialog_text);
            TextView negativeButton = v.findViewById(R.id.tv_no);
            TextView positiveButton = v.findViewById(R.id.tv_yes);
            builder.setView(v);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.cancel));
            positiveButton.setText(context.getString(R.string.hide));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.hide_suggestion)));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_will_definitely_remove_this_suggestion_for_you)));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            positiveButton.setOnClickListener(view3 -> {
                itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 0;
                params.width = 0;
                itemView.setLayoutParams(params);

                String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                HashMap<String, Object> map_account = new HashMap<>();
                map_account.put("user_id", user_groups.getGroup_id());
                assert newKey != null;
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_remove_Suggestion))
                        .child(user_id)
                        .child(newKey)
                        .setValue(map_account);

                d.dismiss();
            });
        });

        isFollowing(user_groups);

        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(),
                user_id, "", user_groups.getGroup_id());

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

        bouton_quitter.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .child(user_groups.getGroup_id())
                    .removeValue();

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id())
                    .child(user_id)
                    .removeValue();
            setUnfollowing();
        });
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        bouton_rejoindre.setVisibility(View.GONE);
        relLayout_member_button.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        bouton_rejoindre.setVisibility(View.VISIBLE);
        relLayout_member_button.setVisibility(View.GONE);
    }

    private void isFollowing(UserGroups user_groups){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        Query query = myRef.child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());
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

