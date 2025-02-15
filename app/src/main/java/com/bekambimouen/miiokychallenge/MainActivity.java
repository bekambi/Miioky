package com.bekambimouen.miiokychallenge;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.display_insta.fragments.MainFragment;
import com.bekambimouen.miiokychallenge.display_insta.fragments.ParametersFragment;
import com.bekambimouen.miiokychallenge.fun.FunActivity;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.fragments.MarketExplorerFragment;
import com.bekambimouen.miiokychallenge.search.ExploreGridView;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.interfaces.RequestPermissionResultListener;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // widgets
    private RelativeLayout parent, relLayout_app_name, relLayout_view_overlay;
    private ViewPager2 viewPager;

    // vars
    private MainActivity context;
    private InformationEvent event;
    private ArrayList<String> groupIdList;
    private String from_home_activity, from_market, from_view_video,
            from_challenge_video_home, from_publish;
    private int f = 0, m = 0, n = 0, c = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_main);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        event = new InformationEvent(true);

        groupIdList = new ArrayList<>();

        try {
            from_home_activity = getIntent().getStringExtra("from_home_activity");
            from_market = getIntent().getStringExtra("from_market");
            from_view_video = getIntent().getStringExtra("from_view_video");
            from_challenge_video_home = getIntent().getStringExtra("from_challenge_video_home");
            from_publish = getIntent().getStringExtra("from_publish");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: error: "+e.getMessage());
        }

        init();
        configureViewPagerAndTabs();
        // verify is user is suspended
        getFollowingGroupId();
    }

    public RelativeLayout getRelLayout_view_overlay() {
        return relLayout_view_overlay;
    }

    public String getFrom_home_value() {
        return from_home_activity;
    }

    // coming from challenge video
    public String getFrom_view_video() {
        return from_view_video;
    }

    // coming from challenge video
    public String getFrom_challenge_video_home() {
        return from_challenge_video_home;
    }

    // coming from publication Market
    public String getFrom_publish() {
        return from_publish;
    }

    // coming from challenge video
    public ViewPager2 getViewPager() {
        return viewPager;
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout_app_name = findViewById(R.id.relLayout_app_name);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_search = findViewById(R.id.relLayout_search);
        RelativeLayout relLayout_img_play = findViewById(R.id.relLayout_img_play);

        relLayout_img_play.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, FunActivity.class);
            startActivity(intent);
        });

        relLayout_search.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ExploreGridView.class);
            startActivity(intent);

        });

        arrowBack.setOnClickListener(view -> {
            if (from_home_activity != null) {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
            else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                startActivity(new Intent(context, HomeActivity.class));
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() == 0) {
                    try {
                        MainFragment.backpressedlistener.onBackPressed();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onBackPressed: error"+e.getMessage());
                    }

                } else if (viewPager.getCurrentItem() == 3) {
                    try {
                        MarketExplorerFragment.backpressedlistener.onBackPressed();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onBackPressed: error"+e.getMessage());
                    }

                } else {
                    viewPager.setCurrentItem(0);
                }
            }
        });
    }

    private void getFollowingGroupId() {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String groupID = dataSnapshot.getKey();

                    groupIdList.add(groupID);
                }

                getMemberSuspensionStateInGroup();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMemberSuspensionStateInGroup() {
        if (!groupIdList.isEmpty()) {
            for(int i = 0; i < groupIdList.size(); i++){
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(groupIdList.get(i))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);

                String group_id = groupIdList.get(i);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            String admin_master = follower.getAdmin_master();
                            String suspension_duration = follower.getSuspension_duration();
                            long sanction_date = follower.getDate_admin_applied_sanction_in_group();
                            long currentTime = System.currentTimeMillis();

                            // action here are the automatic action
                            if (follower.isSuspended()) {

                                switch (suspension_duration) {
                                    case "12":
                                        if (currentTime > (sanction_date + 12*3600000L)) {
                                            getSuspensionExpiration(admin_master, group_id);
                                        }

                                        break;
                                    case "24":
                                        if (currentTime > (sanction_date + 86400000L)) {
                                            getSuspensionExpiration(admin_master, group_id);
                                        }

                                        break;
                                    case "3":
                                        if (currentTime > (sanction_date + 3 * 86400000L)) {
                                            getSuspensionExpiration(admin_master, group_id);
                                        }

                                        break;
                                    case "7":
                                        if (currentTime > (sanction_date + 7 * 86400000L)) {
                                            getSuspensionExpiration(admin_master, group_id);
                                        }

                                        break;
                                    case "14":
                                        if (currentTime > (sanction_date + 14 * 86400000L)) {
                                            getSuspensionExpiration(admin_master, group_id);
                                        }

                                        break;
                                    case "28":
                                        if ((sanction_date + 28 * 86400000L) > currentTime) {
                                            getSuspensionExpiration(admin_master, group_id);
                                        }

                                        break;
                                }
                            }

                            if (follower.isPublicationApprobation()) {
                                // disable post approval after 30 days
                                if (currentTime > (sanction_date + (30*86400000L))) {
                                    getPostApprovalExpiration(admin_master, group_id);
                                }

                            }
                            // activity limited
                            if (follower.isActivityLimitation()) {

                                if (follower.isPostsActivityLimited() || follower.isCommentsActivityLimited()) {
                                    switch (follower.getNumber_posts_per_day_expiration()) {
                                        case "12":
                                            // stop the activity limit
                                            if (currentTime > (sanction_date + 12*3600000L)) {
                                                getActivityLimitExpiration(admin_master, group_id);
                                            }
                                            // initialize number of posts and number of comments per day
                                            else if (TimeUtils.isYesterday(follower.getDate_last_post_or_last_comment())){
                                                if (follower.getNumber_of_posts_today() != 0 || follower.getNumber_of_comments_today() != 0) {
                                                    initializeNumberOfPostsAndNumberOfCommentPerDay(group_id);
                                                }
                                            }
                                            break;
                                        case "24":
                                            if (currentTime > (sanction_date + 86400000L)) {
                                                getActivityLimitExpiration(admin_master, group_id);
                                            }
                                            // initialize number of posts and number of comments per day
                                            else if (TimeUtils.isYesterday(follower.getDate_last_post_or_last_comment())){
                                                if (follower.getNumber_of_posts_today() != 0 || follower.getNumber_of_comments_today() != 0) {
                                                    initializeNumberOfPostsAndNumberOfCommentPerDay(group_id);
                                                }
                                            }
                                            break;
                                        case "3":
                                            if (currentTime > (sanction_date + 3 * 86400000L)) {
                                                getActivityLimitExpiration(admin_master, group_id);
                                            }
                                            // initialize number of posts and number of comments per day
                                            else if (TimeUtils.isYesterday(follower.getDate_last_post_or_last_comment())){
                                                if (follower.getNumber_of_posts_today() != 0 || follower.getNumber_of_comments_today() != 0) {
                                                    initializeNumberOfPostsAndNumberOfCommentPerDay(group_id);
                                                }
                                            }
                                            break;
                                        case "7":
                                            if (currentTime > (sanction_date + 7 * 86400000L)) {
                                                getActivityLimitExpiration(admin_master, group_id);
                                            }
                                            // initialize number of posts and number of comments per day
                                            else if (TimeUtils.isYesterday(follower.getDate_last_post_or_last_comment())){
                                                if (follower.getNumber_of_posts_today() != 0 || follower.getNumber_of_comments_today() != 0) {
                                                    initializeNumberOfPostsAndNumberOfCommentPerDay(group_id);
                                                }
                                            }
                                            break;
                                        case "14":
                                            if (currentTime > (sanction_date + 14 * 86400000L)) {
                                                getActivityLimitExpiration(admin_master, group_id);
                                            }
                                            // initialize number of posts and number of comments per day
                                            else if (TimeUtils.isYesterday(follower.getDate_last_post_or_last_comment())){
                                                if (follower.getNumber_of_posts_today() != 0 || follower.getNumber_of_comments_today() != 0) {
                                                    initializeNumberOfPostsAndNumberOfCommentPerDay(group_id);
                                                }
                                            }
                                            break;
                                        case "28":
                                            if (currentTime > (sanction_date + 28 * 86400000L)) {
                                                getActivityLimitExpiration(admin_master, group_id);
                                            }
                                            // initialize number of posts and number of comments per day
                                            else if (TimeUtils.isYesterday(follower.getDate_last_post_or_last_comment())){
                                                if (follower.getNumber_of_posts_today() != 0 || follower.getNumber_of_comments_today() != 0) {
                                                    initializeNumberOfPostsAndNumberOfCommentPerDay(group_id);
                                                }
                                            }
                                            break;
                                    }
                                }
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

    //  cancel suspension
    private void getSuspensionExpiration(String admin_master, String group_id) {
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
                .child(user_id)
                .child(group_id)
                .updateChildren(map);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(group_id)
                .child(user_id)
                .updateChildren(map);

        // send notification
        String new_key = myRef.push().getKey();
        Date date1 = new Date();
        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                true, false, true, false, false,
                false,false, false, false,
                false, false, false, false, false, false,
                false,
                false, false, false, false, false,
                false, false,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true, false, false,
                user_id, new_key, user_id, admin_master, "", group_id, date1.getTime(),
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
                .child(user_id)
                .child(new_key)
                .setValue(map_notification);
    }

    // disable post approval after 30 days
    private void getPostApprovalExpiration(String admin_master, String group_id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("publicationApprobation", false);
        map.put("date_admin_applied_sanction_in_group", 0);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .child(group_id)
                .updateChildren(map);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(group_id)
                .child(user_id)
                .updateChildren(map);

        // send notification
        String new_key = myRef.push().getKey();
        Date date1 = new Date();
        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                true, false, true, false, false,
                true,false, false, false,
                false, false, false, false, false, false,
                false,
                false, false, false, false, false,
                false, false,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true,false, false,
                user_id, new_key, user_id, admin_master, "", group_id, date1.getTime(),
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
                .child(user_id)
                .child(new_key)
                .setValue(map_notification);
    }

    // get post activity limit
    private void getActivityLimitExpiration(String admin_master, String group_id) {
        HashMap<String, Object> map = new HashMap<>();
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
                .child(user_id)
                .child(group_id)
                .updateChildren(map);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(group_id)
                .child(user_id)
                .updateChildren(map);

        // send notification
        String new_key = myRef.push().getKey();
        Date date1 = new Date();
        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                true, false, false, false, false,
                false,false, false, false,
                true, false, false, false, false, false,
                false,
                false, false, false, false, false,
                false, false,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true,false, false,
                user_id, new_key, user_id, admin_master, "", group_id, date1.getTime(),
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
                .child(user_id)
                .child(new_key)
                .setValue(map_notification);
    }

    // initialize number of posts and number of comments per day in limit activity group
    private void initializeNumberOfPostsAndNumberOfCommentPerDay(String group_id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("number_of_posts_today", 0);
        map.put("number_of_comments_today", 0);
        map.put("date_last_post_or_last_comment", 0);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .child(group_id)
                .updateChildren(map);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(group_id)
                .child(user_id)
                .updateChildren(map);
    }

    private void configureViewPagerAndTabs() {
        TabLayout tabLayout_profile = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(new MainViewPagerAdapter(this));

        // disable viewpager swiping
        viewPager.setUserInputEnabled(false);

        int[] tabsIcons = {
                R.drawable.blue_home,
                R.drawable.icons_follow_empty,
                R.drawable.icons8_chat_empty,
                R.drawable.icons_shop_empty,
                R.drawable.icons_notification,
                R.drawable.menu_icon};

        new TabLayoutMediator(tabLayout_profile, viewPager,
                (tabLayout, position) -> tabLayout.setIcon(tabsIcons[position])).attach();

        // add badge to tab
        getBadgeNotification(tabLayout_profile, viewPager);

        // selected first tab by default
        if (from_market != null) {
            new Handler().postDelayed(() -> {
                relLayout_app_name.setVisibility(View.GONE);
                viewPager.setCurrentItem(3);
                EventBus.getDefault().post(event);
                Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                TabLayout.Tab tab = tabLayout_profile.getTabAt(3);
                assert tab != null;
                tab.setIcon(R.drawable.icons_shop_blue);
                // color
                int tabIconColor = ContextCompat.getColor(context, R.color.shinning_blue);
                Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                // tab indicator color
                tabLayout_profile.setSelectedTabIndicatorColor(getColor(R.color.shinning_blue));
            }, 3000);

        } else if (from_view_video != null) {
            new Handler().postDelayed(() -> {
                relLayout_app_name.setVisibility(View.GONE);
                viewPager.setCurrentItem(5);
                Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                TabLayout.Tab tab = tabLayout_profile.getTabAt(5);
                assert tab != null;
                tab.setIcon(R.drawable.icons_menu_blue);
                // color
                int tabIconColor = ContextCompat.getColor(context, R.color.shinning_blue);
                Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                // tab indicator color
                tabLayout_profile.setSelectedTabIndicatorColor(getColor(R.color.shinning_blue));
            }, 3000);

        } else
            viewPager.setCurrentItem(0);

        // icon color
        TabLayout.Tab tab = tabLayout_profile.getTabAt(0);
        int tabIconColor = ContextCompat.getColor(context, R.color.shinning_blue);
        assert tab != null;
        Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        // tab indicator color
        tabLayout_profile.setSelectedTabIndicatorColor(getColor(R.color.shinning_blue));

        tabLayout_profile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    relLayout_app_name.setVisibility(View.VISIBLE);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.blue_home);
                    // icon color
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(1)).setIcon(R.drawable.icons_follow_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(2)).setIcon(R.drawable.icons8_chat_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(3)).setIcon(R.drawable.icons_shop_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(4)).setIcon(R.drawable.icons_notification);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(5)).setIcon(R.drawable.menu_icon);

                } else  if (tab.getPosition() == 1) {
                    relLayout_app_name.setVisibility(View.GONE);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(1)).setIcon(R.drawable.icons_follow_blue);
                    // icon color
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(2)).setIcon(R.drawable.icons8_chat_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(3)).setIcon(R.drawable.icons_shop_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(4)).setIcon(R.drawable.icons_notification);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(5)).setIcon(R.drawable.menu_icon);

                } else  if (tab.getPosition() == 2) {
                    relLayout_app_name.setVisibility(View.GONE);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(1)).setIcon(R.drawable.icons_follow_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(2)).setIcon(R.drawable.icons8_chat_full);
                    // icon color
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(3)).setIcon(R.drawable.icons_shop_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(4)).setIcon(R.drawable.icons_notification);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(5)).setIcon(R.drawable.menu_icon);

                } else  if (tab.getPosition() == 3) {
                    relLayout_app_name.setVisibility(View.GONE);
                    EventBus.getDefault().post(event);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(1)).setIcon(R.drawable.icons_follow_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(2)).setIcon(R.drawable.icons8_chat_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(3)).setIcon(R.drawable.icons_shop_blue);
                    // icon color
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(4)).setIcon(R.drawable.icons_notification);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(5)).setIcon(R.drawable.menu_icon);

                } else  if (tab.getPosition() == 4) {
                    relLayout_app_name.setVisibility(View.GONE);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(1)).setIcon(R.drawable.icons_follow_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(2)).setIcon(R.drawable.icons8_chat_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(3)).setIcon(R.drawable.icons_shop_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(4)).setIcon(R.drawable.icons_notification_blue);
                    // icon color
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(5)).setIcon(R.drawable.menu_icon);

                } else  if (tab.getPosition() == 5) {
                    relLayout_app_name.setVisibility(View.GONE);

                    Objects.requireNonNull(tabLayout_profile.getTabAt(0)).setIcon(R.drawable.icons_home_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(1)).setIcon(R.drawable.icons_follow_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(2)).setIcon(R.drawable.icons8_chat_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(3)).setIcon(R.drawable.icons_shop_empty);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(4)).setIcon(R.drawable.icons_notification);
                    Objects.requireNonNull(tabLayout_profile.getTabAt(5)).setIcon(R.drawable.icons_menu_blue);
                    // icon color
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // badge notification number
    private void getBadgeNotification(TabLayout tabLayout, ViewPager2 viewPager) {
        // follow badge
        Query query1 =
                myRef.child(context.getString(R.string.dbname_badge_follow_number))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    f++;
                }

                if (f > 0)
                    Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().setNumber(f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // chat badge
        Query query2 =
                myRef.child(context.getString(R.string.dbname_badge_message_number))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    c++;
                }

                if (c > 0)
                    Objects.requireNonNull(tabLayout.getTabAt(2)).getOrCreateBadge().setNumber(c);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // market badge
        Query query3 =
                myRef.child(context.getString(R.string.dbname_badge_market_number))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);
        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    m++;
                }

                if (m > 0)
                    Objects.requireNonNull(tabLayout.getTabAt(3)).getOrCreateBadge().setNumber(m);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // notification badge
        Query query4 =
                myRef.child(context.getString(R.string.dbname_badge_notification_number))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);
        query4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    n++;
                }

                if (n > 0)
                    Objects.requireNonNull(tabLayout.getTabAt(4)).getOrCreateBadge().setNumber(n);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // to remove the badge on tab selected
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // remove follow badge
                if (position == 1) {
                    Objects.requireNonNull(tabLayout.getTabAt(1)).removeBadge();
                    Query query1 =
                            myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                    .child(user_id)
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user_id);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                        .child(user_id)
                                        .removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                // remove chat badge
                if (position == 2) {
                    Objects.requireNonNull(tabLayout.getTabAt(2)).removeBadge();
                    Query query2 =
                            myRef.child(context.getString(R.string.dbname_badge_message_number))
                                    .child(user_id)
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user_id);
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                myRef.child(context.getString(R.string.dbname_badge_message_number))
                                        .child(user_id)
                                        .removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                // remove market badge
                if (position == 3) {
                    Objects.requireNonNull(tabLayout.getTabAt(3)).removeBadge();
                    Query query2 =
                            myRef.child(context.getString(R.string.dbname_badge_market_number))
                                    .child(user_id)
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user_id);
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                myRef.child(context.getString(R.string.dbname_badge_market_number))
                                        .child(user_id)
                                        .removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                // remove notification badge
                if (position == 4) {
                    Objects.requireNonNull(tabLayout.getTabAt(4)).removeBadge();
                    Query query3 =
                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(user_id)
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user_id);
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                        .child(user_id)
                                        .removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // verify internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.viewPager);

        if (fragment instanceof ParametersFragment) {
            ((RequestPermissionResultListener) fragment).onRequestPermission(requestCode, permissions, grantResults);
        }
    }
}