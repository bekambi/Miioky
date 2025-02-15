package com.bekambimouen.miiokychallenge.groups.discover.bottomsheet;

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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupMembershipModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.csguys.viewmoretextview.ViewMoreTextView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetPrivateGroupDiscover extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetGroupDiscover";

    // widgets
    private TextView group_name;
    private TextView private_public;
    private TextView publications_today;
    private TextView publications_last_month;
    private TextView total_members;
    private TextView total_members2;
    private TextView total_members_last_sevens_days;
    private TextView admin_master_username;
    private ImageView profile_photo;
    private CircleImageView admin_master_profile_photo;
    private RelativeLayout relLayout_go_about_class, relLayout_button_cancel, relLayout_button_join;

    private CircleImageView member_i, member_ii, member_iii, member_iv, member_v, member_vi, member_vii,
            member_viii, member_ix;
    private RelativeLayout relLayout_profile_members, relLayout_member_i, relLayout_member_ii,
            relLayout_member_iii, relLayout_member_iv, relLayout_member_v, relLayout_member_vi,
            relLayout_member_vii, relLayout_member_viii, relLayout_member_ix, relLayout_member_x;

    // vars
    private final AppCompatActivity context;
    private final UserGroups user_groups;
    private ArrayList<String> membersList, admins_id_list;
    private int n, m, t, k;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public BottomSheetPrivateGroupDiscover(AppCompatActivity context, UserGroups userGroups) {
        this.context = context;
        this.user_groups = userGroups;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            setupFullHeight(bottomSheetDialog);
        });

        return dialog;
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // expand BottomSheetDialog on full screen
    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheet != null;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight - 100;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        behavior.setFitToContents(true);
        behavior.setHideable(true);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_group_bottomsheet_private_discover, null);

        View iv_view = view.findViewById(R.id.iv_view);
        profile_photo = view.findViewById(R.id.profile_photo);
        admin_master_profile_photo = view.findViewById(R.id.admin_master_profile_photo);
        group_name = view.findViewById(R.id.group_name);
        private_public = view.findViewById(R.id.private_public);
        total_members = view.findViewById(R.id.total_members);
        total_members2 = view.findViewById(R.id.total_members2);
        relLayout_button_cancel = view.findViewById(R.id.relLayout_button_cancel);
        relLayout_button_join = view.findViewById(R.id.relLayout_button_join);
        ViewMoreTextView group_msg = view.findViewById(R.id.group_msg);
        admin_master_username = view.findViewById(R.id.admin_master_username);
        publications_today = view.findViewById(R.id.publications_today);
        publications_last_month = view.findViewById(R.id.publications_last_month);
        total_members_last_sevens_days = view.findViewById(R.id.total_members_last_sevens_days);
        TextView date_group_creation2 = view.findViewById(R.id.date_group_creation2);
        relLayout_go_about_class = view.findViewById(R.id.relLayout_go_about_class);
        relLayout_profile_members = view.findViewById(R.id.relLayout_profile_members);
        relLayout_member_i = view.findViewById(R.id.relLayout_member_i);
        relLayout_member_ii = view.findViewById(R.id.relLayout_member_ii);
        relLayout_member_iii = view.findViewById(R.id.relLayout_member_iii);
        relLayout_member_iv = view.findViewById(R.id.relLayout_member_iv);
        relLayout_member_v = view.findViewById(R.id.relLayout_member_v);
        relLayout_member_vi = view.findViewById(R.id.relLayout_member_vi);
        relLayout_member_vii = view.findViewById(R.id.relLayout_member_vii);
        relLayout_member_viii = view.findViewById(R.id.relLayout_member_viii);
        relLayout_member_ix = view.findViewById(R.id.relLayout_member_ix);
        relLayout_member_x = view.findViewById(R.id.relLayout_member_x);
        member_i = view.findViewById(R.id.member_i);
        member_ii = view.findViewById(R.id.member_ii);
        member_iii = view.findViewById(R.id.member_iii);
        member_iv = view.findViewById(R.id.member_iv);
        member_v = view.findViewById(R.id.member_v);
        member_vi = view.findViewById(R.id.member_vi);
        member_vii = view.findViewById(R.id.member_vii);
        member_viii = view.findViewById(R.id.member_viii);
        member_ix = view.findViewById(R.id.member_ix);

        dialog.setContentView(view);

        iv_view.setOnClickListener(view1 -> dialog.dismiss());

        membersList = new ArrayList<>();
        admins_id_list = new ArrayList<>();

        getUserData();
        getNewPublicatiosNumbers();
        getNewMembersNumbers();

        date_group_creation2.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                +context.getString(R.string.there_is)+" "+TimeUtils.getTime(context, user_groups.getDate_created())));

        // get the group message
        if (!TextUtils.isEmpty(user_groups.getGroup_message()))
            group_msg.setVisibility(View.VISIBLE);
        group_msg.setCharText(user_groups.getGroup_message());

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_groups.getAdmin_master());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    admin_master_username.setText(Html.fromHtml("<b>"+user.getUsername()+"</b> "+context.getString(R.string.created_the_group2)));

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user.getProfileUrl())
                                .into(admin_master_profile_photo);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData() {
        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    group_name.setText(Html.fromHtml("<b>" + user_groups.getGroup_name() + "</b> >"));

                    private_public.setText(context.getString(R.string.title_private));

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    profile_photo.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                        intent.putExtra("user_groups", myGson);
                        context.startActivity(intent);
                    });

                    relLayout_go_about_class.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
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

        // get out or join the group
        isFollowing(user_groups);

        HashMap<Object, Object> map = Utils_Map_GroupMembershipModel.setGroupMembershipModel(user_id, user_groups.getAdmin_master(), user_groups.getGroup_id());

        relLayout_button_join.setOnClickListener(view -> {
            dismiss();
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

            // send notification yo all admins
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
        });

        relLayout_button_cancel.setOnClickListener(v -> {
            Gson gson = new Gson();
            String myJson = gson.toJson(user_groups);
            Intent intent = new Intent(context, GroupViewProfile.class);
            intent.putExtra("user_groups", myJson);
            context.startActivity(intent);
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

    private void getNewPublicatiosNumbers() {
        n = 0;
        m = 0;
        Log.d(TAG, "getPhotos: getting list of photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_group))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    BattleModel model = new BattleModel();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                    assert objectMap != null;
                    model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

                    long date_create = model.getDate_created();
                    // 1 day later
                    if (TimeUtils.isDateToday(date_create))
                        n++;

                    // 1 month later
                    if ((model.getDate_created() + 86400000L*28) > System.currentTimeMillis())
                        m++;
                }

                publications_today.setText(Html.fromHtml(n +" "+context.getString(R.string.publications_today)));
                publications_last_month.setText(Html.fromHtml(m +" "+context.getString(R.string.publications_last_month)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNewMembersNumbers() {
        t = 0;
        k = 0;
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    GroupFollowersFollowing followersFollowing = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    t++;

                    if ((followersFollowing.getDate_following() + 86400000L*7) > System.currentTimeMillis())
                        k++;

                    String member = followersFollowing.getUser_id();
                    membersList.add(member);
                }

                if (t == 0)
                    total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.member_minus)));
                else
                    total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));

                total_members2.setText(Html.fromHtml((t+1)+" "+context.getString(R.string.total_members)));
                total_members_last_sevens_days.setText(Html.fromHtml("+"+ k +" "+context.getString(R.string.total_members_last_sevens_days)));

                if (!membersList.isEmpty()) {
                    relLayout_profile_members.setVisibility(View.VISIBLE);

                    if (membersList.size() == 1) {
                        relLayout_member_i.setVisibility(View.VISIBLE);

                        Query query = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 2) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 3) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 4) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 5) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 6) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 7) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 8) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);
                        relLayout_member_viii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_viii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(7));

                        query_viii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_viii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() >= 9) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);
                        relLayout_member_viii.setVisibility(View.VISIBLE);
                        relLayout_member_ix.setVisibility(View.VISIBLE);
                        relLayout_member_x.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_viii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(7));

                        query_viii.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_viii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ix = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(8));

                        query_ix.addValueEventListener(new ValueEventListener() {
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
                                                .into(member_ix);
                                    }
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
    }

    /**
     * Prevent BottomSheetDialogFragment covering navigation bar
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

