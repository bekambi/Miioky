package com.bekambimouen.miiokychallenge.groups.manage_member.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.limit_member_actions.LimitMemberAction;
import com.bekambimouen.miiokychallenge.groups.manage_member.suspend.GroupAddRulesWithExample;
import com.bekambimouen.miiokychallenge.groups.manage_member.suspend.GroupSuspendMember;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.GroupCreateNewGroup;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_NotificationModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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

public class BottomSheetManageMemberInGroup extends BottomSheetDialogFragment {
    // vars
    private final AppCompatActivity context;
    private final UserGroups user_groups;
    private boolean isPostActivityLimitation= false, isCommentActivityLimitation = false;

    private String the_user_id;
    private String admin_or_moderator_id;

    // firebase
    private final DatabaseReference myRef;
    private final String current_admin_in_group_id;

    public BottomSheetManageMemberInGroup(AppCompatActivity context, UserGroups user_groups) {
        this.context = context;
        this.user_groups = user_groups;

        myRef = FirebaseDatabase.getInstance().getReference();
        current_admin_in_group_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // to expand completely layout
        requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheetInternal != null;
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onStart()
    {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
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
                .inflate(R.layout.layout_bottomsheet_manage_members_in_group, null);

        try {
            assert this.getArguments() != null;
            admin_or_moderator_id = this.getArguments().getString("admin_or_moderator_id");
            the_user_id = this.getArguments().getString("the_user_id");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        RelativeLayout close = view.findViewById(R.id.close);
        close.setOnClickListener(view1 -> dialog.dismiss());

        LinearLayout linLayout_suspend = view.findViewById(R.id.linLayout_suspend);
        LinearLayout linLayout_activate_approbation = view.findViewById(R.id.linLayout_activate_approbation);
        LinearLayout linLayout_limit_member_action = view.findViewById(R.id.linLayout_limit_member_action);
        LinearLayout linLayout_banish = view.findViewById(R.id.linLayout_banish);
        LinearLayout linLayout_add_rules_in_the_group = view.findViewById(R.id.linLayout_add_rules_in_the_group);
        LinearLayout linLayout_invite_like_admin = view.findViewById(R.id.linLayout_invite_like_admin);
        LinearLayout linLayout_delete_from_admin = view.findViewById(R.id.linLayout_delete_from_admin);
        LinearLayout linLayout_add_like_moderator = view.findViewById(R.id.linLayout_add_like_moderator);
        LinearLayout linLayout_send_a_message = view.findViewById(R.id.linLayout_send_a_message);
        LinearLayout linLayout_change_group_profile_photo = view.findViewById(R.id.linLayout_change_group_profile_photo);
        LinearLayout linLayout_see_profile = view.findViewById(R.id.linLayout_see_profile);

        // add rules in the group
        Query query_rules = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query_rules.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    // hide the view if the group already have rules
                    if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
                        linLayout_add_rules_in_the_group.setVisibility(View.GONE);
                    }
                }

                linLayout_add_rules_in_the_group.setOnClickListener(view1 -> {
                    dismiss();
                    Gson gson = new Gson();
                    String myGson = gson.toJson(user_groups);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupAddRulesWithExample.class);
                    intent.putExtra("user_groups", myGson);
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // suspend member
        ImageView icon_suspended = view.findViewById(R.id.icon_suspended);
        TextView tv_suspend = view.findViewById(R.id.tv_suspend);
        // approval post
        ImageView icon_approval = view.findViewById(R.id.icon_approval);
        TextView tv_activate_approbation = view.findViewById(R.id.tv_activate_approbation);
        // limit activity
        ImageView icon_limitation = view.findViewById(R.id.icon_limitation);
        TextView tv_limitation_activity_title = view.findViewById(R.id.tv_limitation_activity_title);
        TextView tv_limit_member_action = view.findViewById(R.id.tv_limit_member_action);
        // invite like admin
        ImageView icon_admin = view.findViewById(R.id.icon_admin);
        TextView tv_invite_like_admin = view.findViewById(R.id.tv_invite_like_admin);
        // add moderator
        ImageView icon_moderator = view.findViewById(R.id.icon_moderator);
        TextView tv_add_like_moderator = view.findViewById(R.id.tv_add_like_moderator);
        // delet from admin
        TextView tv_delete_from_admin = view.findViewById(R.id.tv_delete_from_admin);

        TextView username = view.findViewById(R.id.username);

        Date date = new Date();
        // when the current user is the admin master
        if (user_groups.getAdmin_master().equals(current_admin_in_group_id)) {

            if (admin_or_moderator_id.equals(user_groups.getAdmin_master())) {
                linLayout_suspend.setVisibility(View.GONE);
                linLayout_activate_approbation.setVisibility(View.GONE);
                linLayout_limit_member_action.setVisibility(View.GONE);
                linLayout_banish.setVisibility(View.GONE);
                linLayout_invite_like_admin.setVisibility(View.GONE);
                linLayout_add_like_moderator.setVisibility(View.GONE);
                linLayout_send_a_message.setVisibility(View.GONE);

            } else if (admin_or_moderator_id.equals(the_user_id)) {
                linLayout_suspend.setVisibility(View.GONE);
                linLayout_activate_approbation.setVisibility(View.GONE);
                linLayout_limit_member_action.setVisibility(View.GONE);
                linLayout_banish.setVisibility(View.GONE);
                linLayout_invite_like_admin.setVisibility(View.GONE);
                linLayout_delete_from_admin.setVisibility(View.VISIBLE);
                linLayout_add_like_moderator.setVisibility(View.GONE);

                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(the_user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            if (following.isAdminInGroup()) {
                                linLayout_delete_from_admin.setOnClickListener(view1 -> {
                                    dismiss();

                                    // go to user group profile
                                    Query query1 = myRef
                                            .child(context.getString(R.string.dbname_users))
                                            .orderByChild(context.getString(R.string.field_user_id))
                                            .equalTo(the_user_id);
                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                assert objectMap != null;
                                                User user = Util_User.getUser(context, objectMap, ds);

                                                String user_name = user.getUsername();

                                                // show dialog box
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                                TextView dialog_title = v.findViewById(R.id.dialog_title);
                                                TextView dialog_text = v.findViewById(R.id.dialog_text);
                                                TextView negativeButton = v.findViewById(R.id.tv_no);
                                                TextView positiveButton = v.findViewById(R.id.tv_yes);
                                                builder.setView(v);

                                                Dialog d = builder.create();
                                                d.show();

                                                negativeButton.setText(context.getString(R.string.cancel));
                                                positiveButton.setText(context.getString(R.string.delete_admin));

                                                dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_admin)));
                                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_remove_member)
                                                        +" <b>"+user_name+"</b> "+context.getString(R.string.from_admins)));

                                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                                positiveButton.setOnClickListener(view3 -> {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("adminInGroup", false);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_following))
                                                            .child(the_user_id)
                                                            .child(user_groups.getGroup_id())
                                                            .updateChildren(map);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_followers))
                                                            .child(user_groups.getGroup_id())
                                                            .child(the_user_id)
                                                            .updateChildren(map);

                                                    // send notification
                                                    String new_key = myRef.push().getKey();
                                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                            true, false, false, false, false,
                                                            false,false, false, false,
                                                            false, false, false, false, false, false,
                                                            false,
                                                            true, false, false, false, false,
                                                            false, true,
                                                            false, false, false, false, false,
                                                            false, false,
                                                            false, "", false, false, false, false,
                                                            true,false, false,
                                                            the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                            current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                            .child(the_user_id)
                                                            .child(new_key)
                                                            .setValue(map_notification);

                                                    // add badge number
                                                    HashMap<String, Object> map_number = new HashMap<>();
                                                    map_number.put("user_id", the_user_id);

                                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                            .child(the_user_id)
                                                            .child(new_key)
                                                            .setValue(map_number);

                                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                                    d.dismiss();
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                });

                            } else if (following.isModerator()) {
                                linLayout_suspend.setVisibility(View.GONE);
                                linLayout_activate_approbation.setVisibility(View.GONE);
                                linLayout_limit_member_action.setVisibility(View.GONE);
                                linLayout_banish.setVisibility(View.GONE);
                                linLayout_invite_like_admin.setVisibility(View.GONE);
                                linLayout_add_like_moderator.setVisibility(View.GONE);
                                linLayout_send_a_message.setVisibility(View.GONE);

                                linLayout_delete_from_admin.setVisibility(View.VISIBLE);
                                tv_delete_from_admin.setText(context.getString(R.string.delete_like_moderator));

                                linLayout_delete_from_admin.setOnClickListener(view1 -> {
                                    dismiss();

                                    // go to user group profile
                                    Query query1 = myRef
                                            .child(context.getString(R.string.dbname_users))
                                            .orderByChild(context.getString(R.string.field_user_id))
                                            .equalTo(the_user_id);
                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                assert objectMap != null;
                                                User user = Util_User.getUser(context, objectMap, ds);

                                                String user_name = user.getUsername();

                                                // show dialog box
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                                TextView dialog_title = v.findViewById(R.id.dialog_title);
                                                TextView dialog_text = v.findViewById(R.id.dialog_text);
                                                TextView negativeButton = v.findViewById(R.id.tv_no);
                                                TextView positiveButton = v.findViewById(R.id.tv_yes);
                                                builder.setView(v);

                                                Dialog d = builder.create();
                                                d.show();

                                                negativeButton.setText(context.getString(R.string.cancel));
                                                positiveButton.setText(context.getString(R.string.delete));

                                                dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_moderator)));
                                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_remove_member)
                                                        +" <b>"+user_name+"</b> "+context.getString(R.string.from_moderators)));

                                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                                positiveButton.setOnClickListener(view3 -> {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("moderator", false);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_following))
                                                            .child(the_user_id)
                                                            .child(user_groups.getGroup_id())
                                                            .updateChildren(map);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_followers))
                                                            .child(user_groups.getGroup_id())
                                                            .child(the_user_id)
                                                            .updateChildren(map);

                                                    // send notification
                                                    String new_key = myRef.push().getKey();
                                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                            true, false, false, false, false,
                                                            false,false, false, false,
                                                            false, false, false, false, false, false,
                                                            false,
                                                            false, false, false, false, false,
                                                            false, false,
                                                            true, false, false, false, false,
                                                            false, true,
                                                            false, "", false,  false, false, false,
                                                            true,false, false,
                                                            the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                            current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                            .child(the_user_id)
                                                            .child(new_key)
                                                            .setValue(map_notification);

                                                    // add badge number
                                                    HashMap<String, Object> map_number = new HashMap<>();
                                                    map_number.put("user_id", the_user_id);

                                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                            .child(the_user_id)
                                                            .child(new_key)
                                                            .setValue(map_number);

                                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                                    d.dismiss();
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else if (admin_or_moderator_id.equals("member")) {
                linLayout_suspend.setVisibility(View.VISIBLE);
                linLayout_activate_approbation.setVisibility(View.VISIBLE);
                linLayout_limit_member_action.setVisibility(View.VISIBLE);
                linLayout_banish.setVisibility(View.VISIBLE);
                linLayout_invite_like_admin.setVisibility(View.VISIBLE);
                linLayout_delete_from_admin.setVisibility(View.GONE);
                linLayout_add_like_moderator.setVisibility(View.VISIBLE);
            }

        } // others admins
        else if (admin_or_moderator_id.equals(the_user_id)) {

            if (admin_or_moderator_id.equals(user_groups.getAdmin_master())) {
                linLayout_suspend.setVisibility(View.GONE);
                linLayout_activate_approbation.setVisibility(View.GONE);
                linLayout_limit_member_action.setVisibility(View.GONE);
                linLayout_banish.setVisibility(View.GONE);
                linLayout_invite_like_admin.setVisibility(View.GONE);
                linLayout_add_like_moderator.setVisibility(View.GONE);

            } else if (admin_or_moderator_id.equals(the_user_id)) {

                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(current_admin_in_group_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            if (following.isAdminInGroup()) {
                                linLayout_suspend.setVisibility(View.GONE);
                                linLayout_activate_approbation.setVisibility(View.GONE);
                                linLayout_limit_member_action.setVisibility(View.GONE);
                                linLayout_banish.setVisibility(View.GONE);
                                linLayout_invite_like_admin.setVisibility(View.GONE);
                                linLayout_add_like_moderator.setVisibility(View.GONE);
                                linLayout_send_a_message.setVisibility(View.GONE);

                                linLayout_delete_from_admin.setVisibility(View.VISIBLE);
                                tv_delete_from_admin.setText(context.getString(R.string.remove_yourself_from_admin));

                                Query query = myRef.child(context.getString(R.string.dbname_notification))
                                        .child(current_admin_in_group_id)
                                        .orderByChild(context.getString(R.string.field_user_id))
                                        .equalTo(current_admin_in_group_id);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()) {

                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                            assert objectMap != null;
                                            NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                                            linLayout_delete_from_admin.setOnClickListener(view1 -> {
                                                dismiss();
                                                // show dialog box
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                                TextView dialog_title = v.findViewById(R.id.dialog_title);
                                                TextView dialog_text = v.findViewById(R.id.dialog_text);
                                                TextView negativeButton = v.findViewById(R.id.tv_no);
                                                TextView positiveButton = v.findViewById(R.id.tv_yes);
                                                builder.setView(v);

                                                Dialog d = builder.create();
                                                d.show();

                                                negativeButton.setText(context.getString(R.string.cancel));
                                                positiveButton.setText(context.getString(R.string.delete_admin));

                                                dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_admin)));
                                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_remove_yourself_from_admins)));

                                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                                positiveButton.setOnClickListener(view3 -> {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("adminInGroup", false);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_following))
                                                            .child(the_user_id)
                                                            .child(user_groups.getGroup_id())
                                                            .updateChildren(map);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_followers))
                                                            .child(user_groups.getGroup_id())
                                                            .child(the_user_id)
                                                            .updateChildren(map);

                                                    // send notification
                                                    String new_key = myRef.push().getKey();
                                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                            true, false, false, false, false,
                                                            false,false, false, false,
                                                            false, false, false, false, false, false,
                                                            false,
                                                            true, false, false, false, false,
                                                            true, false,
                                                            false, false, false, false, false,
                                                            false, false,
                                                            false, "", false,  false, false, false,
                                                            true,false, false,
                                                            notification.getAdmin_in_group(), new_key, the_user_id, user_groups.getAdmin_master(),
                                                            notification.getAdmin_in_group(), user_groups.getGroup_id(), date.getTime(),
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
                                                            .child(notification.getAdmin_in_group())
                                                            .child(new_key)
                                                            .setValue(map_notification);

                                                    // add badge number
                                                    HashMap<String, Object> map_number = new HashMap<>();
                                                    map_number.put("user_id", notification.getAdmin_in_group());

                                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                            .child(notification.getAdmin_in_group())
                                                            .child(new_key)
                                                            .setValue(map_number);

                                                    // clear all back stack fragments
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    context.startActivity(intent);

                                                    d.dismiss();
                                                });
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else if (following.isModerator()) {
                                linLayout_suspend.setVisibility(View.GONE);
                                linLayout_activate_approbation.setVisibility(View.GONE);
                                linLayout_limit_member_action.setVisibility(View.GONE);
                                linLayout_banish.setVisibility(View.GONE);
                                linLayout_invite_like_admin.setVisibility(View.GONE);
                                linLayout_add_like_moderator.setVisibility(View.GONE);
                                linLayout_send_a_message.setVisibility(View.GONE);

                                linLayout_delete_from_admin.setVisibility(View.VISIBLE);
                                tv_delete_from_admin.setText(context.getString(R.string.remove_yourself_like_moderator));

                                Query query = myRef.child(context.getString(R.string.dbname_notification))
                                        .child(current_admin_in_group_id)
                                        .orderByChild(context.getString(R.string.field_user_id))
                                        .equalTo(current_admin_in_group_id);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()) {

                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                            assert objectMap != null;
                                            NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                                            linLayout_delete_from_admin.setOnClickListener(view1 -> {
                                                dismiss();
                                                // show dialog box
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                                TextView dialog_title = v.findViewById(R.id.dialog_title);
                                                TextView dialog_text = v.findViewById(R.id.dialog_text);
                                                TextView negativeButton = v.findViewById(R.id.tv_no);
                                                TextView positiveButton = v.findViewById(R.id.tv_yes);
                                                builder.setView(v);

                                                Dialog d = builder.create();
                                                d.show();

                                                negativeButton.setText(context.getString(R.string.cancel));
                                                positiveButton.setText(context.getString(R.string.delete));

                                                dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_moderator)));
                                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_remove_yourself_from_moderators)));

                                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                                positiveButton.setOnClickListener(view3 -> {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("moderator", false);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_following))
                                                            .child(the_user_id)
                                                            .child(user_groups.getGroup_id())
                                                            .updateChildren(map);

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(context.getString(R.string.dbname_group_followers))
                                                            .child(user_groups.getGroup_id())
                                                            .child(the_user_id)
                                                            .updateChildren(map);

                                                    // send notification
                                                    String new_key = myRef.push().getKey();
                                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                            true, false, false, false, false,
                                                            false,false, false, false,
                                                            false, false, false, false, false, false,
                                                            false,
                                                            false, false, false, false, false,
                                                            false, false,
                                                            true, false, false, false, false,
                                                            true, false,
                                                            false, "", false,  false, false, false,
                                                            true,false, false,
                                                            notification.getAdmin_in_group(), new_key, the_user_id, user_groups.getAdmin_master(),
                                                            notification.getAdmin_in_group(), user_groups.getGroup_id(), date.getTime(),
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
                                                            .child(notification.getAdmin_in_group())
                                                            .child(new_key)
                                                            .setValue(map_notification);

                                                    // add badge number
                                                    HashMap<String, Object> map_number = new HashMap<>();
                                                    map_number.put("user_id", notification.getAdmin_in_group());

                                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                            .child(notification.getAdmin_in_group())
                                                            .child(new_key)
                                                            .setValue(map_number);

                                                    // clear all back stack fragments
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    context.startActivity(intent);

                                                    d.dismiss();
                                                });
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else if (admin_or_moderator_id.equals("member")) {
                linLayout_suspend.setVisibility(View.VISIBLE);
                linLayout_activate_approbation.setVisibility(View.VISIBLE);
                linLayout_limit_member_action.setVisibility(View.VISIBLE);
                linLayout_banish.setVisibility(View.VISIBLE);
                linLayout_invite_like_admin.setVisibility(View.VISIBLE);
                linLayout_delete_from_admin.setVisibility(View.GONE);
                linLayout_add_like_moderator.setVisibility(View.VISIBLE);
            }
        }

        // change group profile photo
        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    // to know if the member is suspended
                    Query query = myRef
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(user_groups.getGroup_id())
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(the_user_id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                // verify is current user is suspended _____________________________________________________
                                if (follower.isSuspended()) {
                                    linLayout_invite_like_admin.setVisibility(View.GONE);
                                    linLayout_add_like_moderator.setVisibility(View.GONE);

                                    tv_suspend.setText(context.getString(R.string.cancel_suspension));
                                    icon_suspended.setImageResource(R.drawable.ic_baseline_close_24);

                                    linLayout_suspend.setOnClickListener(view1 -> {
                                        //  cancel suspension
                                        dismiss();

                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                                        InsetDrawable inset = new InsetDrawable(back, 50);
                                        d.getWindow().setBackgroundDrawable(inset);
                                        d.show();

                                        positiveButton.setText(context.getString(R.string.cancel));
                                        negativeButton.setText(context.getString(R.string.give_up));

                                        dialog_title.setText(context.getString(R.string.cancel_suspension));
                                        dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you__want_to_cancel_suspension)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("suspended", false);
                                            map.put("suspension_duration", "");
                                            map.put("sanction_admin_comment", "");
                                            map.put("date_admin_applied_sanction_in_group", 0);

                                            // rules
                                            map.put("rule_title_one", "");
                                            map.put("rule_detail_one", "");
                                            map.put("rule_title_two", "");
                                            map.put("rule_detail_two", "");
                                            map.put("rule_title_three", "");
                                            map.put("rule_detail_three", "");
                                            map.put("rule_title_four", "");
                                            map.put("rule_detail_four", "");
                                            map.put("rule_title_five", "");
                                            map.put("rule_detail_five", "");
                                            map.put("rule_title_six", "");
                                            map.put("rule_detail_six", "");
                                            map.put("rule_title_seven", "");
                                            map.put("rule_detail_seven", "");
                                            map.put("rule_title_eight", "");
                                            map.put("rule_detail_eight", "");
                                            map.put("rule_title_nine", "");
                                            map.put("rule_detail_nine", "");
                                            map.put("rule_title_ten", "");
                                            map.put("rule_detail_ten", "");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, true, false, true,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });

                                } else {
                                    linLayout_suspend.setOnClickListener(view1 -> {
                                        dismiss();
                                        // suspend member
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupSuspendMember.class);
                                        intent.putExtra("user_groups", myGson);
                                        intent.putExtra("the_user_id", the_user_id);
                                        startActivity(intent);
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    linLayout_change_group_profile_photo.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupCreateNewGroup.class);
                        intent.putExtra("user_groups", myGson);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // go to user group profile
        Query query1 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(the_user_id);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String userID = user.getUser_id();
                    String user_name = user.getUsername();

                    username.setText(user_name);

                    // to know if the member need admin post approval
                    Query query = myRef
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(user_groups.getGroup_id())
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(the_user_id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                // hide item limit member activity when he's already suspended
                                if (follower.isSuspended()) {
                                    linLayout_activate_approbation.setVisibility(View.GONE);
                                    linLayout_limit_member_action.setVisibility(View.GONE);
                                }

                                // hide item limit member activity when he's already suspended
                                if (follower.isPublicationApprobation()) {
                                    linLayout_suspend.setVisibility(View.GONE);
                                    linLayout_limit_member_action.setVisibility(View.GONE);
                                }

                                // hide item suspend member when his activity in group already limited
                                if (follower.isActivityLimitation()) {
                                    linLayout_suspend.setVisibility(View.GONE);
                                    linLayout_activate_approbation.setVisibility(View.GONE);
                                }

                                // verification of post approval
                                if (follower.isPublicationApprobation()) {
                                    tv_activate_approbation.setText(context.getString(R.string.disable_post_approval));
                                    icon_approval.setImageResource(R.drawable.ic_baseline_close_24);

                                    linLayout_activate_approbation.setOnClickListener(view1 -> {
                                        dismiss();

                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        negativeButton.setText(context.getString(R.string.cancel));
                                        positiveButton.setText(context.getString(R.string.ok));

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.dialog_disable_post_approval_title)+" "+user_name));
                                        dialog_text.setText(Html.fromHtml(user_name+" "+context.getString(R.string.dialog_member_will_be_able_to_freely_make_a_post_in_group)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("publicationApprobation", false);
                                            map.put("date_admin_applied_sanction_in_group", 0);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, true, false, false,
                                                    true,false, false, true,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false,  false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });

                                } else {
                                    linLayout_activate_approbation.setOnClickListener(view1 -> {
                                        dismiss();

                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        positiveButton.setText(context.getString(R.string.activate));

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.dialog_enable_post_approval_title)+" "+user_name));
                                        dialog_text.setText(Html.fromHtml(context.getString(R.string.dialog_all_group_post)
                                                +" "+user_name+" "+context.getString(R.string.dialog_they_will_be_checked)
                                                +" "+user_name+" "+context.getString(R.string.dialog_member_will_receive_notification)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("publicationApprobation", true);
                                            map.put("date_admin_applied_sanction_in_group", date.getTime());

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    true,true, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false,  false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });
                                }

                                // limit member activity in group
                                if (follower.isActivityLimitation()) {
                                    tv_limitation_activity_title.setText(context.getString(R.string.cancel_limit_activity));
                                    icon_limitation.setImageResource(R.drawable.ic_baseline_close_24);
                                    // text below title
                                    tv_limit_member_action.setVisibility(View.GONE);

                                    linLayout_limit_member_action.setOnClickListener(view12 -> {
                                        dismiss();
                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        negativeButton.setText(context.getString(R.string.cancel));
                                        positiveButton.setText(context.getString(R.string.ok));

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.dialog_cancel_limit_activity_title)+" "+user_name));
                                        dialog_text.setText(Html.fromHtml(user_name+" "+context.getString(R.string.dialog_member_will_be_able_to_freely_make_a_post)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {HashMap<String, Object> map = new HashMap<>();
                                            map.put("activityLimitation", false);
                                            map.put("postsActivityLimited", false);
                                            map.put("commentsActivityLimited", false);

                                            map.put("number_of_posts_today", 0);
                                            map.put("number_of_comments_today", 0);
                                            map.put("date_last_post_or_last_comment", 0);
                                            map.put("date_admin_applied_sanction_in_group", 0);
                                            map.put("number_posts_per_day", "");
                                            map.put("number_posts_per_day_expiration", "");
                                            map.put("number_comments_per_day", "");
                                            map.put("number_comments_per_day_expiration", "");
                                            map.put("sanction_admin_comment", "");

                                            // rules
                                            map.put("rule_title_one", "");
                                            map.put("rule_detail_one", "");
                                            map.put("rule_title_two", "");
                                            map.put("rule_detail_two", "");
                                            map.put("rule_title_three", "");
                                            map.put("rule_detail_three", "");
                                            map.put("rule_title_four", "");
                                            map.put("rule_detail_four", "");
                                            map.put("rule_title_five", "");
                                            map.put("rule_detail_five", "");
                                            map.put("rule_title_six", "");
                                            map.put("rule_detail_six", "");
                                            map.put("rule_title_seven", "");
                                            map.put("rule_detail_seven", "");
                                            map.put("rule_title_eight", "");
                                            map.put("rule_detail_eight", "");
                                            map.put("rule_title_nine", "");
                                            map.put("rule_detail_nine", "");
                                            map.put("rule_title_ten", "");
                                            map.put("rule_detail_ten", "");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);


                                            // get the notification limit activity status
                                            Query query2 = myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(current_admin_in_group_id)
                                                    .orderByChild(context.getString(R.string.field_user_id))
                                                    .equalTo(current_admin_in_group_id);
                                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                    for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {

                                                        Map<Object, Object> objectMap1 = (HashMap<Object, Object>) dataSnapshot1.getValue();
                                                        assert objectMap1 != null;
                                                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap1, dataSnapshot1);

                                                        if (notification.isPostsActivityLimitation()) {
                                                            isPostActivityLimitation = true;
                                                        }
                                                        if (notification.isCommentsActivityLimitation()) {
                                                            isCommentActivityLimitation = true;
                                                        }
                                                    }

                                                    // send notification like this because the two values are independent
                                                    String new_key = myRef.push().getKey();
                                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                            true, false, false, false, false,
                                                            false,false, false, false,
                                                            true, isPostActivityLimitation, isCommentActivityLimitation, false, false, true,
                                                            false,
                                                            false, false, false, false, false,
                                                            false, false,
                                                            false, false, false, false, false,
                                                            false, false,
                                                            false, "", false,  false, false, false,
                                                            true,false, false,
                                                            the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                            current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                            .child(the_user_id)
                                                            .child(new_key)
                                                            .setValue(map_notification);

                                                    // add badge number
                                                    HashMap<String, Object> map_number = new HashMap<>();
                                                    map_number.put("user_id", the_user_id);

                                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                            .child(the_user_id)
                                                            .child(new_key)
                                                            .setValue(map_number);

                                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            d.dismiss();
                                        });
                                    });

                                } else {
                                    linLayout_limit_member_action.setOnClickListener(view -> {
                                        dismiss();
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, LimitMemberAction.class);
                                        intent.putExtra("user_groups", myGson);
                                        intent.putExtra("the_user_id", the_user_id);
                                        context.startActivity(intent);
                                    });
                                }

                                // banish member from group
                                linLayout_banish.setOnClickListener(view1 -> {
                                    dismiss();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_banishmen_in_group, null);

                                    CircleImageView profile_photo = v.findViewById(R.id.profile_photo);
                                    TextView username = v.findViewById(R.id.username);
                                    TextView txt_explanation = v.findViewById(R.id.txt_explanation);
                                    TextView button_banish = v.findViewById(R.id.button_banish);

                                    builder.setView(v);
                                    Dialog custom_dialog = builder.create();
                                    custom_dialog.show();

                                    if (!context.isFinishing()) {
                                        Glide.with(context.getApplicationContext())
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(profile_photo);
                                    }

                                    String user_name = user.getUsername();
                                    username.setText(user_name);
                                    txt_explanation.setText(Html.fromHtml(context.getString(R.string.do_you_want_to_banish)
                                            +" <b>"+user_name+"</b> "+context.getString(R.string.from_group)
                                            +" <b>"+user_groups.getGroup_name()+"</b> ? "+context.getString(R.string.this_action_is_irreversible)
                                            +" <b>"+user_name+"</b> "+context.getString(R.string.he_will_be_permanently_excluded_from_group)));

                                    // go to user group profile
                                    profile_photo.setOnTouchListener((view5, motionEvent) -> {
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupProfileMember.class);
                                        intent.putExtra("user_groups", myGson);
                                        intent.putExtra("userID", userID);
                                        intent.putExtra("group_id", user_groups.getGroup_id());
                                        context.startActivity(intent);
                                        return false;
                                    });

                                    button_banish.setOnClickListener(view2 -> {
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("banFromGroup", true);
                                        map.put("date_admin_applied_sanction_in_group", date.getTime());

                                        FirebaseDatabase.getInstance().getReference()
                                                .child(context.getString(R.string.dbname_group_following))
                                                .child(the_user_id)
                                                .child(user_groups.getGroup_id())
                                                .updateChildren(map);

                                        FirebaseDatabase.getInstance().getReference()
                                                .child(context.getString(R.string.dbname_group_followers))
                                                .child(user_groups.getGroup_id())
                                                .child(the_user_id)
                                                .updateChildren(map);

                                        // send notification
                                        String new_key = myRef.push().getKey();
                                        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                true, false, false, false, false,
                                                false,false, false, false,
                                                false, false, false, false, false, false,
                                                true,
                                                false, false, false, false, false,
                                                false, false,
                                                false, false, false, false, false,
                                                false, false,
                                                false, "", false, false, false, false,
                                                true,false, false,
                                                the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                .child(the_user_id)
                                                .child(new_key)
                                                .setValue(map_notification);

                                        // add badge number
                                        HashMap<String, Object> map_number = new HashMap<>();
                                        map_number.put("user_id", the_user_id);

                                        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                .child(the_user_id)
                                                .child(new_key)
                                                .setValue(map_number);

                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        custom_dialog.dismiss();
                                    });
                                });

                                if (follower.isAdminInvitation()) {
                                    // hide views
                                    linLayout_suspend.setVisibility(View.GONE);
                                    linLayout_activate_approbation.setVisibility(View.GONE);
                                    linLayout_limit_member_action.setVisibility(View.GONE);
                                    linLayout_banish.setVisibility(View.GONE);
                                    linLayout_add_like_moderator.setVisibility(View.GONE);

                                    tv_invite_like_admin.setText(context.getString(R.string.cancel_invitation_to_become_an_admin));
                                    icon_admin.setImageResource(R.drawable.ic_baseline_close_24);

                                    linLayout_invite_like_admin.setOnClickListener(view1 -> {
                                        dismiss();

                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        negativeButton.setText(context.getString(R.string.cancel));
                                        positiveButton.setText(context.getString(R.string.ok));

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.cancel_invitation)));
                                        dialog_text.setText(Html.fromHtml(context.getString(R.string.cancel_invitation_of)+" "+user_name+" "+context.getString(R.string.to_become_an_admin)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("adminInvitation", false);
                                            map.put("date_admin_applied_sanction_in_group", 0);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    true, false, true, false, false,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
                                                    false, false,
                                                    false, false,false, false, false, false, false, false,
                                                    false, "", "", "", false, "", "", "", false,
                                                    "", "", "", "", "", 0,
                                                    "", "", 0, "", "", "",
                                                    false, false, false, false,
                                                    false, false, false,
                                                    false, false);

                                            assert new_key != null;
                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });

                                } else {
                                    linLayout_invite_like_admin.setOnClickListener(view1 -> {
                                        dismiss();
                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        negativeButton.setText(context.getString(R.string.cancel));
                                        positiveButton.setText(context.getString(R.string.send_invitation));

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.invite)+" "+user_name+" "+context.getString(R.string.to_become_an_admin)));
                                        dialog_text.setText(Html.fromHtml(user_name+" "+context.getString(R.string.he_will_be_able_to_modify_the_group_parameters)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("adminInvitation", true);
                                            map.put("date_admin_applied_sanction_in_group", date.getTime());

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    true, true, false, false, false,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });
                                }

                                if (follower.isModeratorAdding()) {
                                    // hide views
                                    linLayout_suspend.setVisibility(View.GONE);
                                    linLayout_activate_approbation.setVisibility(View.GONE);
                                    linLayout_limit_member_action.setVisibility(View.GONE);
                                    linLayout_banish.setVisibility(View.GONE);
                                    linLayout_invite_like_admin.setVisibility(View.GONE);

                                    tv_add_like_moderator.setText(context.getString(R.string.remove_ass_moderator));
                                    icon_moderator.setImageResource(R.drawable.ic_baseline_close_24);

                                    linLayout_add_like_moderator.setOnClickListener(view1 -> {
                                        dismiss();

                                        // show dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.remove_ass_moderator)));
                                        dialog_text.setText(Html.fromHtml(context.getString(R.string.remove) + " "
                                                + user_name + " " + context.getString(R.string.ass_moderator)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("moderatorAdding", false);
                                            map.put("date_admin_applied_sanction_in_group", 0);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification to member
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false, false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    true, false,
                                                    true, false, true, false, false,
                                                    true, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });

                                } else {
                                    linLayout_add_like_moderator.setOnClickListener(view1 -> {
                                        // show dialog box
                                        dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        negativeButton.setText(context.getString(R.string.cancel));
                                        positiveButton.setText(context.getString(R.string.appoint_moderator));

                                        dialog_title.setText(Html.fromHtml(context.getString(R.string.appoint_group_moderator)));
                                        dialog_text.setText(Html.fromHtml(context.getString(R.string.moderators_are_like_admins)
                                                +" "+user_name+" "+context.getString(R.string.he_will_be_able_to_manage_members)));

                                        negativeButton.setOnClickListener(view2 -> d.dismiss());

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("moderatorAdding", true);
                                            map.put("date_admin_applied_sanction_in_group", date.getTime());

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(the_user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(the_user_id)
                                                    .updateChildren(map);

                                            // send notification
                                            String new_key = myRef.push().getKey();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    true, true, false, false, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                                                    current_admin_in_group_id, user_groups.getGroup_id(), date.getTime(),
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
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", the_user_id);

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(the_user_id)
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            d.dismiss();
                                        });
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    linLayout_send_a_message.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    });

                    linLayout_see_profile.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("user_groups", myGson);
                        intent.putExtra("userID", userID);
                        intent.putExtra("group_id", user_groups.getGroup_id());
                        context.startActivity(intent);
                    });
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

