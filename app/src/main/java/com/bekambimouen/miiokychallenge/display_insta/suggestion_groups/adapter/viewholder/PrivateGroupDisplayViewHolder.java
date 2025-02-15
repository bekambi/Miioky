package com.bekambimouen.miiokychallenge.display_insta.suggestion_groups.adapter.viewholder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
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
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.BottomSheetPrivateGroupDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.BottomSheetPrivateGroupDiscoverWithRules;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.AcceptPrivateRules;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupMembershipModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class PrivateGroupDisplayViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PrivateGroupDisplayViewHolder";

    // widgets
    private final ImageView profile_photo, close;
    private final TextView group_name, nber_of_members;
    private final RelativeLayout relLayout_button_cancel, relLayout_button_join;

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

    public PrivateGroupDisplayViewHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        profile_photo = itemView.findViewById(R.id.profile_photo);
        close = itemView.findViewById(R.id.close);
        group_name = itemView.findViewById(R.id.group_name);
        nber_of_members = itemView.findViewById(R.id.nber_of_members);
        relLayout_button_cancel = itemView.findViewById(R.id.relLayout_button_cancel);
        relLayout_button_join = itemView.findViewById(R.id.relLayout_button_join);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        admins_id_list = new ArrayList<>();
    }

    public void bind(UserGroups user_groups) {
        group_name.setText(user_groups.getGroup_name());

        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .asDrawable().sizeMultiplier(0.1f);

        if (!context.isFinishing()) {
            Glide.with(context)
                    .load(user_groups.getProfile_photo())
                    .thumbnail(requestBuilder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);
        }

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

                portee = context.getString(R.string.title_private);

                nber_of_members.setText(Html.fromHtml(context.getString(R.string.group)+" "+
                        context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                        "<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));
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

        HashMap<Object, Object> map = Utils_Map_GroupMembershipModel.setGroupMembershipModel(user_id, user_groups.getAdmin_master(), user_groups.getGroup_id());

        // top button
        relLayout_button_join.setOnClickListener(view -> {
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
                setFollowing();

                // send notification to all admins
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
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

        relLayout_button_cancel.setOnClickListener(v -> {
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
        relLayout_button_join.setVisibility(View.GONE);
        relLayout_button_cancel.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        relLayout_button_join.setVisibility(View.VISIBLE);
        relLayout_button_cancel.setVisibility(View.GONE);
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

