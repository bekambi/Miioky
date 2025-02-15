package com.bekambimouen.miiokychallenge.groups.explorer;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.parameters.GroupParameters;
import com.bekambimouen.miiokychallenge.groups.explorer.viewpager.GroupExplorerViewPagerAdapter;
import com.bekambimouen.miiokychallenge.groups.parameters.GroupSearchInGroup;
import com.bekambimouen.miiokychallenge.groups.publication.GroupCreateNewGroup;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.google.gson.Gson;

public class GroupExplorer extends AppCompatActivity {
    private static final String TAG = "GroupExplorer";

    // widgets
    private ViewPager2 viewPager;
    private TextView tv_recommendations, tv_see_your_groups, tv_discover;
    private RelativeLayout parent, relLayout_recommendations, relLayout_see_your_groups,
            relLayout_discover, relLayout_view_overlay;
    private HorizontalScrollView horizontal_scrollview;

    // vars
    private GroupExplorer context;
    private String from_create_new_group, from_main_suggestion_group_see_more;
    private boolean isRecommendations = false, isSee_your_groups = false, isDiscover = false;
    private BattleModel from_notification_comment;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_explorer);
        context = this;

        try {
            from_create_new_group = getIntent().getStringExtra("from_create_new_group");
            from_main_suggestion_group_see_more = getIntent().getStringExtra("from_main_suggestion_group_see_more");

            // from notificationAdapter
            Gson gson = new Gson();
            from_notification_comment = gson.fromJson(getIntent().getStringExtra("from_notification_comment"), BattleModel.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        configureViewPager();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    public RelativeLayout getRelLayout_view_overlay() {
        return relLayout_view_overlay;
    }

    // coming from notification and send to  groupExplorer fragment
    public BattleModel getFrom_notification_comment() {
        return from_notification_comment;
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_add = findViewById(R.id.relLayout_add);
        RelativeLayout relLayout_parameters = findViewById(R.id.relLayout_parameters);
        RelativeLayout relLayout_search = findViewById(R.id.relLayout_search);
        tv_recommendations = findViewById(R.id.tv_recommendations);
        tv_see_your_groups = findViewById(R.id.tv_see_your_groups);
        tv_discover = findViewById(R.id.tv_discover);
        relLayout_recommendations = findViewById(R.id.relLayout_recommendations);
        relLayout_see_your_groups = findViewById(R.id.relLayout_see_your_groups);
        relLayout_discover = findViewById(R.id.relLayout_discover);
        horizontal_scrollview = findViewById(R.id.horizontal_scrollview);

        relLayout_add.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            context.startActivity(new Intent(context, GroupCreateNewGroup.class));
        });
        relLayout_parameters.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            startActivity(new Intent(context, GroupParameters.class));
        });
        relLayout_search.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            startActivity(new Intent(context, GroupSearchInGroup.class));
        });

        arrowBack.setOnClickListener(view -> {
            if (viewPager.getCurrentItem() == 0) {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                if (from_create_new_group != null) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    startActivity(new Intent(context, MainActivity.class));
                }
                else
                    super.onBackPressed();

            } else {
                if (!isRecommendations) {
                    horizontal_scrollview.smoothScrollTo(relLayout_recommendations.getLeft(), 0);

                    viewPager.setCurrentItem(0, true);

                    isRecommendations = true;
                    tv_recommendations.setTextColor(context.getColor(R.color.shinning_blue));
                    relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                    isSee_your_groups = false;
                    tv_see_your_groups.setTextColor(context.getColor(R.color.black));
                    relLayout_see_your_groups.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                    isDiscover = false;
                    tv_discover.setTextColor(context.getColor(R.color.black));
                    relLayout_discover.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
                }
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() == 0) {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    if (from_create_new_group != null) {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        startActivity(new Intent(context, MainActivity.class));
                    }
                    else
                        finish();

                } else {
                    if (!isRecommendations) {
                        horizontal_scrollview.smoothScrollTo(relLayout_recommendations.getLeft(), 0);

                        viewPager.setCurrentItem(0, true);

                        isRecommendations = true;
                        tv_recommendations.setTextColor(context.getColor(R.color.shinning_blue));
                        relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                        isSee_your_groups = false;
                        tv_see_your_groups.setTextColor(context.getColor(R.color.black));
                        relLayout_see_your_groups.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                        isDiscover = false;
                        tv_discover.setTextColor(context.getColor(R.color.black));
                        relLayout_discover.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
                    }
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int x, y;
        x = tv_discover.getTop();
        y = tv_discover.getLeft();

        horizontal_scrollview.scrollTo(x, y);
    }

    private void configureViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GroupExplorerViewPagerAdapter(this));

        // disable viewpager swiping
        viewPager.setUserInputEnabled(false);

        // coming from main suggestion group see more
        if (from_main_suggestion_group_see_more != null) {
            viewPager.setCurrentItem(2);

            // make discover button selected by default
            isRecommendations = false;
            tv_recommendations.setTextColor(context.getColor(R.color.black));
            relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

            isSee_your_groups = false;
            tv_see_your_groups.setTextColor(context.getColor(R.color.black));
            relLayout_see_your_groups.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

            isDiscover = true;
            tv_discover.setTextColor(context.getColor(R.color.shinning_blue));
            relLayout_discover.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

            horizontal_scrollview.smoothScrollTo(relLayout_discover.getLeft(), 0);
        }

        relLayout_recommendations.setOnClickListener(view -> {
            if (!isRecommendations) {
                horizontal_scrollview.smoothScrollTo(relLayout_recommendations.getLeft(), 0);

                viewPager.setCurrentItem(0, true);

                isRecommendations = true;
                tv_recommendations.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                isSee_your_groups = false;
                tv_see_your_groups.setTextColor(context.getColor(R.color.black));
                relLayout_see_your_groups.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isDiscover = false;
                tv_discover.setTextColor(context.getColor(R.color.black));
                relLayout_discover.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });

        relLayout_see_your_groups.setOnClickListener(view -> {
            if (!isSee_your_groups) {
                horizontal_scrollview.smoothScrollTo(relLayout_see_your_groups.getLeft(), 0);

                viewPager.setCurrentItem(1, true);

                isRecommendations = false;
                tv_recommendations.setTextColor(context.getColor(R.color.black));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSee_your_groups = true;
                tv_see_your_groups.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_see_your_groups.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                isDiscover = false;
                tv_discover.setTextColor(context.getColor(R.color.black));
                relLayout_discover.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });

        relLayout_discover.setOnClickListener(view -> {
            if (!isDiscover) {
                horizontal_scrollview.smoothScrollTo(relLayout_discover.getLeft(), 0);

                viewPager.setCurrentItem(2, true);

                isRecommendations = false;
                tv_recommendations.setTextColor(context.getColor(R.color.black));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSee_your_groups = false;
                tv_see_your_groups.setTextColor(context.getColor(R.color.black));
                relLayout_see_your_groups.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isDiscover = true;
                tv_discover.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_discover.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));
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
    }
}