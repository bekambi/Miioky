package com.bekambimouen.miiokychallenge.groups.manage_member.limit_member_actions;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.VerifyRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LimitActivityExplanation extends AppCompatActivity {
    private static final String TAG = "LimitActivityExplanation";
    // widgets
    private LinearLayout parent;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_name, tv_non_respected_rules;
    private Toolbar toolBar;

    // vars
    private LimitActivityExplanation context;
    private UserGroups user_groups;
    private List<Rule> rules_list;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_limit_explanation);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        rules_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        theme();
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

            toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
            toolBar_name.setTextColor(context.getColor(R.color.black));
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            close.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_blue))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.blue_600));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
            toolBar_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_brown))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.background_brown));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
            toolBar_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_pink))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.pink));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
            toolBar_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_green))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.vertWatsApp));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
            toolBar_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_black))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.black));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
            toolBar_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void init() {
        parent = findViewById(R.id.parent);
        tv_non_respected_rules = findViewById(R.id.tv_non_respected_rules);
        toolBar = findViewById(R.id.toolBar);
        arrowBack = findViewById(R.id.arrowBack);
        close = findViewById(R.id.close);
        toolBar_name = findViewById(R.id.toolBar_name);
        TextView admin_notation = findViewById(R.id.admin_notation);
        LinearLayout linLayout_posts_frequency = findViewById(R.id.linLayout_posts_frequency);
        LinearLayout linLayout_comments_frequency = findViewById(R.id.linLayout_comments_frequency);
        TextView posts_limited_date = findViewById(R.id.posts_limited_date);
        TextView comments_limited_date = findViewById(R.id.comments_limited_date);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (!TextUtils.isEmpty(follower.getRule_title_one()) && !TextUtils.isEmpty(follower.getRule_detail_one()))
                        rules_list.add(new Rule(follower.getRule_title_one(), follower.getRule_detail_one()));
                    if (!TextUtils.isEmpty(follower.getRule_title_two()) && !TextUtils.isEmpty(follower.getRule_detail_two()))
                        rules_list.add(new Rule(follower.getRule_title_two(), follower.getRule_detail_two()));
                    if (!TextUtils.isEmpty(follower.getRule_title_three()) && !TextUtils.isEmpty(follower.getRule_detail_three()))
                        rules_list.add(new Rule(follower.getRule_title_three(), follower.getRule_detail_three()));
                    if (!TextUtils.isEmpty(follower.getRule_title_four()) && !TextUtils.isEmpty(follower.getRule_detail_four()))
                        rules_list.add(new Rule(follower.getRule_title_four(), follower.getRule_detail_four()));
                    if (!TextUtils.isEmpty(follower.getRule_title_five()) && !TextUtils.isEmpty(follower.getRule_detail_five()))
                        rules_list.add(new Rule(follower.getRule_title_five(), follower.getRule_detail_five()));
                    if (!TextUtils.isEmpty(follower.getRule_title_six()) && !TextUtils.isEmpty(follower.getRule_detail_six()))
                        rules_list.add(new Rule(follower.getRule_title_six(), follower.getRule_detail_six()));
                    if (!TextUtils.isEmpty(follower.getRule_title_seven()) && !TextUtils.isEmpty(follower.getRule_detail_seven()))
                        rules_list.add(new Rule(follower.getRule_title_seven(), follower.getRule_detail_seven()));
                    if (!TextUtils.isEmpty(follower.getRule_title_eight()) && !TextUtils.isEmpty(follower.getRule_detail_eight()))
                        rules_list.add(new Rule(follower.getRule_title_eight(), follower.getRule_detail_eight()));
                    if (!TextUtils.isEmpty(follower.getRule_title_nine()) && !TextUtils.isEmpty(follower.getRule_detail_nine()))
                        rules_list.add(new Rule(follower.getRule_title_nine(), follower.getRule_detail_nine()));
                    if (!TextUtils.isEmpty(follower.getRule_title_ten()) && !TextUtils.isEmpty(follower.getRule_detail_ten()))
                        rules_list.add(new Rule(follower.getRule_title_ten(), follower.getRule_detail_ten()));

                    admin_notation.setText(follower.getSanction_admin_comment());

                    String time_posts_expiration = follower.getNumber_posts_per_day_expiration();
                    String time_comments_expiration = follower.getNumber_comments_per_day_expiration();

                    long tv_date = follower.getDate_admin_applied_sanction_in_group();
                    String timestamp = TimeUtils.getTime(context, tv_date);

                    if (follower.isPostsActivityLimited()) {
                        linLayout_posts_frequency.setVisibility(View.VISIBLE);
                        switch (time_posts_expiration) {
                            case "12":
                                posts_limited_date.setText(Html.fromHtml(context.getString(R.string.your_post_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.suspension_duration)
                                        +" "+"12"+ context.getString(R.string.letter_h)+". "+context.getString(R.string.for_max_posts)
                                        +" "+follower.getNumber_posts_per_day() +" "+context.getString(R.string.per_day)));

                                break;
                            case "24":
                                posts_limited_date.setText(Html.fromHtml(context.getString(R.string.your_post_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.suspension_duration)
                                        +" "+"24"+ context.getString(R.string.letter_h)+". "+context.getString(R.string.for_max_posts)
                                        +" "+follower.getNumber_posts_per_day() +" "+context.getString(R.string.per_day)));

                                break;
                            case "3":
                                posts_limited_date.setText(Html.fromHtml(context.getString(R.string.your_post_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.suspension_duration)
                                        +" "+"3 "+ context.getString(R.string.days)+". "+context.getString(R.string.for_max_posts)
                                        +" "+follower.getNumber_posts_per_day() +" "+context.getString(R.string.per_day)));

                                break;
                            case "7":
                                posts_limited_date.setText(Html.fromHtml(context.getString(R.string.your_post_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.suspension_duration)
                                        +" "+"7 "+ context.getString(R.string.days)+". "+context.getString(R.string.for_max_posts)
                                        +" "+follower.getNumber_posts_per_day() +" "+context.getString(R.string.per_day)));

                                break;
                            case "14":
                                posts_limited_date.setText(Html.fromHtml(context.getString(R.string.your_post_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.suspension_duration)
                                        +" "+"14 "+ context.getString(R.string.days)+". "+context.getString(R.string.for_max_posts)
                                        +" "+follower.getNumber_posts_per_day() +" "+context.getString(R.string.per_day)));

                                break;
                            case "28":
                                posts_limited_date.setText(Html.fromHtml(context.getString(R.string.your_post_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.suspension_duration)
                                        +" "+"28 "+ context.getString(R.string.days)+". "+context.getString(R.string.for_max_posts)
                                        +" "+follower.getNumber_posts_per_day() +" "+context.getString(R.string.per_day)));

                                break;
                        }
                    }

                    if (follower.isCommentsActivityLimited()) {
                        linLayout_comments_frequency.setVisibility(View.VISIBLE);
                        switch (time_comments_expiration) {
                            case "12":
                                comments_limited_date.setText(Html.fromHtml(context.getString(R.string.your_comment_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.for_max_comments)+" "+follower.getNumber_comments_per_day()
                                        +" "+context.getString(R.string.per_day)+" "+context.getString(R.string.suspension_duration)
                                        +" "+"12" + context.getString(R.string.letter_h)));

                                break;
                            case "24":
                                comments_limited_date.setText(Html.fromHtml(context.getString(R.string.your_comment_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.for_max_comments)+" "+follower.getNumber_comments_per_day()
                                        +" "+context.getString(R.string.per_day)+" "+context.getString(R.string.suspension_duration)
                                        +" "+"24" + context.getString(R.string.letter_h)));

                                break;
                            case "3":
                                comments_limited_date.setText(Html.fromHtml(context.getString(R.string.your_comment_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.for_max_comments)+" "+follower.getNumber_comments_per_day()
                                        +" "+context.getString(R.string.per_day)+" "+context.getString(R.string.suspension_duration)
                                        +" "+"3" + context.getString(R.string.days)));

                                break;
                            case "7":
                                comments_limited_date.setText(Html.fromHtml(context.getString(R.string.your_comment_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.for_max_comments)+" "+follower.getNumber_comments_per_day()
                                        +" "+context.getString(R.string.per_day)+" "+context.getString(R.string.suspension_duration)
                                        +" "+"7" + context.getString(R.string.days)));

                                break;
                            case "14":
                                comments_limited_date.setText(Html.fromHtml(context.getString(R.string.your_comment_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.for_max_comments)+" "+follower.getNumber_comments_per_day()
                                        +" "+context.getString(R.string.per_day)+" "+context.getString(R.string.suspension_duration)
                                        +" "+"14" + context.getString(R.string.days)));

                                break;
                            case "28":
                                comments_limited_date.setText(Html.fromHtml(context.getString(R.string.your_comment_activity_has_been_limited_in_this_group)
                                        +" "+timestamp+" "+context.getString(R.string.for_max_comments)+" "+follower.getNumber_comments_per_day()
                                        +" "+context.getString(R.string.per_day)+" "+context.getString(R.string.suspension_duration)
                                        +" "+"28" + context.getString(R.string.days)));

                                break;
                        }
                    }
                }

                VerifyRulesAdapter verifyRulesAdapter = new VerifyRulesAdapter(context, rules_list);
                recyclerView.setAdapter(verifyRulesAdapter);

                if (verifyRulesAdapter.getItemCount() == 0)
                    tv_non_respected_rules.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        arrowBack.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
        theme();
    }
}