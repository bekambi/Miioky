package com.bekambimouen.miiokychallenge.groups.invite_user_in_group;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.fragments.adapter.InvitationViewPagerAdapter;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupInviteFromSameTown extends AppCompatActivity {
    private static final String TAG = "GroupInviteFromSameTown";
    // widgets
    private TextView tv_suggestions, tv_town, tv_village;
    private RelativeLayout relLayout_suggestions, relLayout_town, relLayout_village;
    private LinearLayout parent;
    private HorizontalScrollView scrollView;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_textview;
    private Toolbar toolBar;

    // vars
    private GroupInviteFromSameTown context;
    private UserGroups user_groups;
    private ViewPager2 viewPager;
    private boolean isSuggestions = true, isSameTown = false, isHomeTown = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_invite_from_same_town);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        theme();
        getTowns();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    public UserGroups getUserGroups() {
        return user_groups;
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
            toolBar_textview.setTextColor(context.getColor(R.color.black));
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
            toolBar_textview.setTextColor(context.getColor(R.color.white));
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
            toolBar_textview.setTextColor(context.getColor(R.color.white));
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
            toolBar_textview.setTextColor(context.getColor(R.color.white));
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
            toolBar_textview.setTextColor(context.getColor(R.color.white));
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
            toolBar_textview.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void init() {
        parent = findViewById(R.id.parent);
        toolBar = findViewById(R.id.toolBar);
        scrollView = findViewById(R.id.scrollView);
        arrowBack = findViewById(R.id.arrowBack);
        toolBar_textview = findViewById(R.id.toolBar_textview);
        close = findViewById(R.id.close);

        tv_suggestions = findViewById(R.id.tv_suggestions);
        tv_town = findViewById(R.id.tv_town);
        tv_village = findViewById(R.id.tv_village);

        relLayout_suggestions = findViewById(R.id.relLayout_suggestions);
        relLayout_village = findViewById(R.id.relLayout_village);
        relLayout_town = findViewById(R.id.relLayout_town);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new InvitationViewPagerAdapter(this));

        // disable viewpager swiping
        viewPager.setUserInputEnabled(false);

        relLayout_suggestions.setOnClickListener(view -> {
            if (!isSuggestions) {
                scrollView.smoothScrollTo(relLayout_suggestions.getLeft(), 0);

                viewPager.setCurrentItem(0, true);

                isSuggestions = true;
                tv_suggestions.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_suggestions.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview_blue));

                isSameTown = false;
                tv_town.setTextColor(context.getColor(R.color.black));
                relLayout_town.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isHomeTown = false;
                tv_village.setTextColor(context.getColor(R.color.black));
                relLayout_village.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });
        relLayout_town.setOnClickListener(view -> {
            if (!isSameTown) {
                scrollView.smoothScrollTo(relLayout_town.getLeft(), 0);

                viewPager.setCurrentItem(1, true);

                isSuggestions = false;
                tv_suggestions.setTextColor(context.getColor(R.color.black));
                relLayout_suggestions.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSameTown = true;
                tv_town.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_town.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview_blue));

                isHomeTown = false;
                tv_village.setTextColor(context.getColor(R.color.black));
                relLayout_village.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

            }
        });

        relLayout_village.setOnClickListener(view -> {
            if (!isHomeTown) {
                scrollView.smoothScrollTo(relLayout_town.getLeft(), 0);

                viewPager.setCurrentItem(2, true);

                isSuggestions = false;
                tv_suggestions.setTextColor(context.getColor(R.color.black));
                relLayout_suggestions.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSameTown = false;
                tv_town.setTextColor(context.getColor(R.color.black));
                relLayout_town.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isHomeTown = true;
                tv_village.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_village.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview_blue));
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

    private void getTowns() {
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    tv_town.setText(user.getTown_name());
                    tv_village.setText(user.getHometown_name());

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
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}