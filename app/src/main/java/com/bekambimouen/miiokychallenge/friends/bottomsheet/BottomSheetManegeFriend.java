package com.bekambimouen.miiokychallenge.friends.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.search.user_common_friends.SeeUserFriends;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetManegeFriend extends BottomSheetDialogFragment {

    // vars
    private final AppCompatActivity context;
    private final String userID;
    private final LinearLayout linLayout_not_yet_friend;
    private final LinearLayout linLayout_already_friend;
    private final RelativeLayout relLayout_unfriend;
    private final RelativeLayout relLayout_follow;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public BottomSheetManegeFriend(AppCompatActivity context, String userID, LinearLayout linLayout_not_yet_friend,
                                   LinearLayout linLayout_already_friend, RelativeLayout relLayout_unfriend, RelativeLayout relLayout_follow) {
        this.context = context;
        this.userID = userID;
        this.linLayout_not_yet_friend = linLayout_not_yet_friend;
        this.linLayout_already_friend = linLayout_already_friend;
        this.relLayout_unfriend = relLayout_unfriend;
        this.relLayout_follow = relLayout_follow;
    }

    @Override
    public void onStart()
    {
        requireNonNull(requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // to expand completely layout
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
            coordinatorLayout.getParent().requestLayout();
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottomsheet_manege_friend, null);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        CircleImageView profile_photo = view.findViewById(R.id.profile_photo);
        TextView username = view.findViewById(R.id.username);
        TextView full_name = view.findViewById(R.id.full_name);
        TextView friends_from = view.findViewById(R.id.friends_from);
        RelativeLayout relLayout_go_user_profile = view.findViewById(R.id.relLayout_go_user_profile);
        TextView see_user_friends = view.findViewById(R.id.see_user_friends);
        LinearLayout linLayout_see_user_friends = view.findViewById(R.id.linLayout_see_user_friends);
        TextView send_a_message_to = view.findViewById(R.id.send_a_message_to);
        LinearLayout linLayout_send_a_message_to = view.findViewById(R.id.linLayout_send_a_message_to);
        TextView unsubscribe_from = view.findViewById(R.id.unsubscribe_from);
        LinearLayout linLayout_unsubscribe_from = view.findViewById(R.id.linLayout_unsubscribe_from);
        TextView resubscribe_to = view.findViewById(R.id.resubscribe_to);
        LinearLayout linLayout_resubscribe_to = view.findViewById(R.id.linLayout_resubscribe_to);
        TextView remove_from_friends = view.findViewById(R.id.remove_from_friends);
        TextView remove_him_from_your_friends = view.findViewById(R.id.remove_him_from_your_friends);
        LinearLayout linLayout_remove_from_friends = view.findViewById(R.id.linLayout_remove_from_friends);

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    if (!context.isFinishing()) {
                        Glide.with(context.getApplicationContext())
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    username.setText(user.getUsername());
                    full_name.setText(user.getFullName());
                    see_user_friends.setText(context.getString(R.string.see_all_his_friends));
                    send_a_message_to.setText(Html.fromHtml(context.getString(R.string.send_a_message_to)
                            +" "+user.getUsername()));
                    unsubscribe_from.setText(Html.fromHtml(context.getString(R.string.unsubscribe_from)
                            +" "+user.getUsername()));
                    resubscribe_to.setText(Html.fromHtml(context.getString(R.string.resubscribe_to)
                            +" "+user.getUsername()));
                    remove_from_friends.setText(Html.fromHtml(context.getString(R.string.remove)
                            +" "+user.getUsername()+" "+context.getString(R.string.from_friends)));
                    remove_him_from_your_friends.setText(Html.fromHtml(context.getString(R.string.remove)
                            +" "+user.getUsername()+" "+context.getString(R.string.from_your_friends)));

                    relLayout_go_user_profile.setOnClickListener(view1 -> {
                        dismiss();
                        Intent intent = new Intent(context, ViewProfile.class);
                        Gson gson = new Gson();
                        String myJson = gson.toJson(user);
                        intent.putExtra("user", myJson);
                        context.startActivity(intent);
                    });

                    linLayout_see_user_friends.setOnClickListener(view1 -> {
                        dismiss();
                        Intent intent = new Intent(context, SeeUserFriends.class);
                        intent.putExtra("userID", user.getUser_id());
                        startActivity(intent);
                    });

                    linLayout_send_a_message_to.setOnClickListener(view1 -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        Intent intent = new Intent(context, MessageActivity.class);

                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    });

                    // unsubscribe to friend
                    linLayout_unsubscribe_from.setOnClickListener(view1 -> {
                        dismiss();
                        // show dialog box
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                        TextView negativeButton = v.findViewById(R.id.tv_no);
                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                        builder.setView(v);

                        Dialog d = builder.create();
                        d.show();

                        negativeButton.setText(context.getString(R.string.cancel));
                        positiveButton.setText(context.getString(R.string.unsubscribe));

                        dialog_title.setText(Html.fromHtml(context.getString(R.string.unsubscribe)));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_really_want_to_unsubscribe_in)+" "
                                +user.getUsername()+" ?"));

                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                        positiveButton.setOnClickListener(view2 -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("unSubscribe", true);

                            // current user
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .updateChildren(map).addOnSuccessListener(unused -> {
                                        d.dismiss();

                                        linLayout_resubscribe_to.setVisibility(View.VISIBLE);
                                        linLayout_unsubscribe_from.setVisibility(View.GONE);
                                    });
                        });
                    });

                    // resubscribe to friend
                    linLayout_resubscribe_to.setOnClickListener(view1 -> {
                        dismiss();
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
                        positiveButton.setText(context.getString(R.string.resubscribe));

                        dialog_title.setText(Html.fromHtml(context.getString(R.string.resubscribe)));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_subscribe_to)+" "
                                +user.getUsername()));

                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                        positiveButton.setOnClickListener(view2 -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("unSubscribe", false);

                            // current user
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .updateChildren(map).addOnSuccessListener(unused -> {
                                        d.dismiss();

                                        linLayout_resubscribe_to.setVisibility(View.GONE);
                                        linLayout_unsubscribe_from.setVisibility(View.VISIBLE);
                                    });
                        });
                    });

                    // remove user from your friend
                    linLayout_remove_from_friends.setOnClickListener(view1 -> {
                        dismiss();
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
                        positiveButton.setText(context.getString(R.string.remove));

                        dialog_title.setText(Html.fromHtml(context.getString(R.string.remove_from_friends)));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_remove)+" "
                                +user.getUsername()+" "+(context.getString(R.string.from_your_friends))));

                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                        positiveButton.setOnClickListener(view2 -> {
                            if (linLayout_already_friend != null)
                                linLayout_already_friend.setVisibility(View.GONE);
                            if (linLayout_not_yet_friend != null)
                                linLayout_not_yet_friend.setVisibility(View.VISIBLE);

                            if (relLayout_unfriend != null)
                                relLayout_unfriend.setVisibility(View.GONE);
                            if (relLayout_follow != null)
                                relLayout_follow.setVisibility(View.VISIBLE);
                            // current user
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .removeValue();

                            // the one who invites
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_following))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_friend_follower))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .removeValue().addOnSuccessListener(unused -> {
                                        d.dismiss();

                                        // send notification to unfriend
                                        Date date = new Date();
                                        String new_key = myRef.push().getKey();
                                        assert new_key != null;
                                        // send notification to the post owner
                                        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                false, false, false, false, false,
                                                false, false, false, false,
                                                false, false, false, false, false, false,
                                                false,
                                                false, false, false, false, false,
                                                false, false,
                                                false, false, false, false, false,
                                                false, false,
                                                false, "", false, false, false, false,
                                                false,false, true,
                                                userID, new_key, user_id, "",
                                                "", "", date.getTime(),
                                                false, false,
                                                false, false, false, false, false, false, false, false,
                                                false, "", "", "", false, "", "", "", false,
                                                "", "", "", "", "", 0,
                                                "", "", 0, "", "", "",
                                                false, false, false, false,
                                                true, false, true,
                                                false, false);

                                        myRef.child(context.getString(R.string.dbname_notification))
                                                .child(userID)
                                                .child(new_key)
                                                .setValue(map_notification);

                                        // add badge number
                                        HashMap<String, Object> map_number = new HashMap<>();
                                        map_number.put("user_id", userID);

                                        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                .child(userID)
                                                .child(new_key)
                                                .setValue(map_number);
                                    });
                        });
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query1 = myRef.child(context.getString(R.string.dbname_friend_follower))
                .child(userID)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    FriendsFollowerFollowing follower = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                    if (follower.isUnSubscribe()) {
                        linLayout_unsubscribe_from.setVisibility(View.GONE);
                        linLayout_resubscribe_to.setVisibility(View.VISIBLE);
                    }
                    else {
                        linLayout_resubscribe_to.setVisibility(View.GONE);
                        linLayout_unsubscribe_from.setVisibility(View.VISIBLE);
                    }

                    long tv_date = follower.getDate_created();
                    friends_from.setText(Html.fromHtml(context.getString(R.string.friends_from)+" "+ TimeUtils.getTime(context, tv_date)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialog.setContentView(view);
    }

    /**
     * Prevent BottomSheetDialogFragment covering navigation bar
     * @param dialog
     */
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}

