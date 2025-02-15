package com.bekambimouen.miiokychallenge.groups.discover;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.groups.GroupAbout;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.VerifyRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.AcceptPrivateRules;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupMembershipModel;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupPrivateViewProfileDiscover extends AppCompatActivity {
    private static final String TAG = "GroupPrivateViewProfileDiscover";

    // widgets
    private ImageView profile_photo;
    private TextView group_name, private_public, total_members, group_msg;
    private RelativeLayout relLayout_go_about_discover_class, relLayout_view_overlay;
    private LinearLayout linLayout_admin_group_rules;

    // not yet member
    private RecyclerView recyclerView;
    private TextView date_group_creation;
    private RelativeLayout relLayout_button_cancel, relLayout_button_join, relLayout_buttons_join_cancel_bottom,
            relLayout_button_cancel_bottom, relLayout_buttons_join_bottom;

    // theme
    private RelativeLayout parent, arrowBack;
    private Toolbar toolBar;
    private TextView toolBar_group_name;
    private ImageView close;

    // vars
    private GroupPrivateViewProfileDiscover context;
    private UserGroups user_groups;
    private List<Rule> rules_list;
    private List<String> admins_id_list;
    private int t = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_private_view_profile_discover);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        rules_list = new ArrayList<>();
        admins_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void theme() {
        String theme = user_groups.getGroup_theme();

        if (theme.equals(context.getString(R.string.theme_normal))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);

            // changer a couleur des icons en noir
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            parent.setBackgroundResource(R.drawable.toolbar_blue_grey_50);
            toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
            toolBar_group_name.setTextColor(context.getColor(R.color.black));
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            relLayout_button_join.setBackgroundResource(R.drawable.toolbar_blue_background);
            close.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_blue))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.blue_600));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_blue_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            relLayout_button_join.setBackgroundResource(R.drawable.toolbar_blue_background);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_brown))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.background_brown));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_brown_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            relLayout_button_join.setBackgroundResource(R.drawable.toolbar_brown_background);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_pink))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.pink));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_pink_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            relLayout_button_join.setBackgroundResource(R.drawable.toolbar_pink_background);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_green))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.vertWatsApp));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_green_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            relLayout_button_join.setBackgroundResource(R.drawable.toolbar_green_background);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_black))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.black));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_black_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            relLayout_button_join.setBackgroundResource(R.drawable.toolbar_black_background);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        arrowBack = findViewById(R.id.arrowBack);
        toolBar = findViewById(R.id.toolBar);
        toolBar_group_name = findViewById(R.id.toolBar_group_name);
        close = findViewById(R.id.close);

        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        ImageView profile_photo_toolbar = findViewById(R.id.profile_photo_toolbar);
        relLayout_button_cancel = findViewById(R.id.relLayout_button_cancel);
        relLayout_button_join = findViewById(R.id.relLayout_button_join);
        relLayout_buttons_join_cancel_bottom = findViewById(R.id.relLayout_buttons_join_cancel_bottom);
        relLayout_button_cancel_bottom = findViewById(R.id.relLayout_button_cancel_bottom);
        relLayout_buttons_join_bottom = findViewById(R.id.relLayout_buttons_join_bottom);
        linLayout_admin_group_rules = findViewById(R.id.linLayout_admin_group_rules);
        total_members = findViewById(R.id.total_members);
        group_msg = findViewById(R.id.group_msg);
        relLayout_go_about_discover_class = findViewById(R.id.relLayout_go_about_class);
        profile_photo = findViewById(R.id.profile_photo);
        group_name = findViewById(R.id.group_name);
        private_public = findViewById(R.id.private_public);

        // not yet member
        date_group_creation = findViewById(R.id.date_group_creation);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v1, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                String TAG = "nested_sync";
                if (scrollY > oldScrollY) {
                    Log.i(TAG, "SCROLL DOWN");
                    if (!(View.VISIBLE == relLayout_buttons_join_cancel_bottom.getVisibility()))
                        new Handler().postDelayed(() -> relLayout_buttons_join_cancel_bottom.setVisibility(View.VISIBLE), 1000);
                }

                if (scrollY < oldScrollY) {
                    Log.i(TAG, "SCROLL UP");
                }

                if (scrollY == 0) {
                    Log.i(TAG, "TOP SCROLL");
                    if (View.VISIBLE == relLayout_buttons_join_cancel_bottom.getVisibility())
                        relLayout_buttons_join_cancel_bottom.setVisibility(View.GONE);
                }

                if (scrollY == (v1.getChildAt(0).getMeasuredHeight() - v1.getMeasuredHeight())) {
                    Log.i(TAG, "BOTTOM SCROLL");
                }
            });
        }

        getTotalMembers();
        getUserData();
        getGroupRules();

        toolBar_group_name.setText(user_groups.getGroup_name());

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(user_groups.getProfile_photo())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(profile_photo_toolbar);

        arrowBack.setOnClickListener(view -> finish());
    }

    // get rules
    private void getGroupRules() {
        if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one()))
            rules_list.add(new Rule(user_groups.getRule_title_one(), user_groups.getRule_detail_one()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_two()) && !TextUtils.isEmpty(user_groups.getRule_detail_two()))
            rules_list.add(new Rule(user_groups.getRule_title_two(), user_groups.getRule_detail_two()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_three()) && !TextUtils.isEmpty(user_groups.getRule_detail_three()))
            rules_list.add(new Rule(user_groups.getRule_title_three(), user_groups.getRule_detail_three()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_four()) && !TextUtils.isEmpty(user_groups.getRule_detail_four()))
            rules_list.add(new Rule(user_groups.getRule_title_four(), user_groups.getRule_detail_four()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_five()) && !TextUtils.isEmpty(user_groups.getRule_detail_five()))
            rules_list.add(new Rule(user_groups.getRule_title_five(), user_groups.getRule_detail_five()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_six()) && !TextUtils.isEmpty(user_groups.getRule_detail_six()))
            rules_list.add(new Rule(user_groups.getRule_title_six(), user_groups.getRule_detail_six()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_seven()) && !TextUtils.isEmpty(user_groups.getRule_detail_seven()))
            rules_list.add(new Rule(user_groups.getRule_title_seven(), user_groups.getRule_detail_seven()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_eight()) && !TextUtils.isEmpty(user_groups.getRule_detail_eight()))
            rules_list.add(new Rule(user_groups.getRule_title_eight(), user_groups.getRule_detail_eight()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_nine()) && !TextUtils.isEmpty(user_groups.getRule_detail_nine()))
            rules_list.add(new Rule(user_groups.getRule_title_nine(), user_groups.getRule_detail_nine()));
        if (!TextUtils.isEmpty(user_groups.getRule_title_ten()) && !TextUtils.isEmpty(user_groups.getRule_detail_ten()))
            rules_list.add(new Rule(user_groups.getRule_title_ten(), user_groups.getRule_detail_ten()));

        VerifyRulesAdapter adapter = new VerifyRulesAdapter(context, rules_list);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0)
            linLayout_admin_group_rules.setVisibility(View.GONE);
    }

    private void getTotalMembers() {
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

                total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get the group message
        if (!TextUtils.isEmpty(user_groups.getGroup_message()))
            group_msg.setVisibility(View.VISIBLE);
        group_msg.setText(user_groups.getGroup_message());
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

                    long date = user_groups.getDate_created();
                    date_group_creation.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                            +context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date)));

                    if (user_groups.isGroup_public())
                        private_public.setText(context.getString(R.string.title_public));
                    else
                        private_public.setText(context.getString(R.string.title_private));

                    group_name.setText(Html.fromHtml("<b>" + user_groups.getGroup_name() + "</b> >"));

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    profile_photo.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                        intent.putExtra("img_url", user_groups.getFull_photo());
                        context.startActivity(intent);
                    });

                    relLayout_go_about_discover_class.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupAbout.class);
                        intent.putExtra("user_groups", myGson);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

        // bottom button
        relLayout_buttons_join_bottom.setOnClickListener(view -> {
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
            }
        });

        relLayout_button_cancel_bottom.setOnClickListener(v -> {
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
        relLayout_buttons_join_bottom.setVisibility(View.GONE);
        relLayout_button_cancel.setVisibility(View.VISIBLE);
        relLayout_button_cancel_bottom.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        relLayout_button_join.setVisibility(View.VISIBLE);
        relLayout_buttons_join_bottom.setVisibility(View.VISIBLE);
        relLayout_button_cancel.setVisibility(View.GONE);
        relLayout_button_cancel_bottom.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
        theme();
    }
}