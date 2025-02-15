package com.bekambimouen.miiokychallenge.challenge_home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.fragments.ChallengesFragment;
import com.bekambimouen.miiokychallenge.challenge_home.fragments.adapter.HomeViewPagerAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.register.RegisterLogin;
import com.bekambimouen.miiokychallenge.register.RegisterUserName;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    // update application manager ___________________________
    private AppUpdateManager appUpdateManager;
    private ActivityResultLauncher<IntentSenderRequest> updateActivityResultLauncher;
    private InstallStateUpdatedListener installStateUpdatedListener;
    // ______________________________________________________

    // widgets
    private RelativeLayout main;
    private TabLayout tabLayout_profile;
    private ViewPager2 viewPager;
    private RelativeLayout relLayout_app_name, relLayout_welcome, relLayout_view_overlay;

    // vars
    private HomeActivity context;
    private String from_main_fragment;
    private int n = 0;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_home);
        context = this;

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            from_main_fragment = getIntent().getStringExtra("from_main_fragment");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        // update application __________________________________
        if (user != null) {
            updateActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {
                            Log.e("MainActivity", "Update flow failed! Result code: " + result.getResultCode());
                            // If the update flow fails or is cancelled, you might want to retry or inform the user
                        }
                    }
            );

            installStateUpdatedListener = state -> {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    // After the update is downloaded, show a notification to restart the app
                    popupSnackbarForCompleteUpdate();
                }
            };
        }
        // _____________________________________________________

        main = findViewById(R.id.main);

        configureViewPagerAndTabs();
        init();
        setupFirebaseAuth();
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

    public RelativeLayout getRelLayout_view_welcome() {
        return relLayout_welcome;
    }

    public String getFrom_main_fragment() {
        return from_main_fragment;
    }

    private void init() {
        relLayout_app_name = findViewById(R.id.relLayout_app_name);
        relLayout_welcome = findViewById(R.id.relLayout_welcome);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout relLayout_go_home = findViewById(R.id.relLayout_go_home);

        relLayout_go_home.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("from_home_activity", "from_home_activity");
            context.startActivity(intent);
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager != null) {
                    if (viewPager.getCurrentItem() == 0) {
                        try {
                            ChallengesFragment.backpressedlistener.onBackPressed();
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBackPressed: "+e.getMessage());
                        }
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                } else {
                    finish();
                }
            }
        });
    }

    public RelativeLayout getRelLayout_app_name() {
        return relLayout_app_name;
    }

    private void configureViewPagerAndTabs() {
        tabLayout_profile = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new HomeViewPagerAdapter(this));

        // disable viewpager swiping
        viewPager.setUserInputEnabled(false);

        String[] titles = {context.getString(R.string.challenge), context.getString(R.string.invitation)};

        new TabLayoutMediator(tabLayout_profile, viewPager,
                (tabLayout, position) -> tabLayout.setText(titles[position])).attach();
    }

    // badge notification number
    private void getBadgeNotification(TabLayout tabLayout, ViewPager2 viewPager, FirebaseUser user) {
        // challenge invitation badge
        Query query =
                myRef.child(context.getString(R.string.dbname_badge_challenge_invitation_number))
                        .child(user.getUid())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    n++;
                }

                if (n > 0)
                    Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge().setNumber(n);
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

                // remove market badge
                if (position == 1) {
                    Objects.requireNonNull(tabLayout.getTabAt(1)).removeBadge();
                    Query query2 =
                            myRef.child(context.getString(R.string.dbname_badge_challenge_invitation_number))
                                    .child(user.getUid())
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user.getUid());
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                myRef.child(context.getString(R.string.dbname_badge_challenge_invitation_number))
                                        .child(user.getUid())
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

    /**
     * checks to see if the @param 'user' is logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(context, RegisterLogin.class);
            startActivity(intent);
            finish();

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user_model = Util_User.getUser(context, objectMap, ds);
                        String username = user_model.getUsername();

                        // in case user disconnected and reconnected
                        if (TextUtils.isEmpty(username)) {
                            Intent intent = new Intent(context, RegisterUserName.class);
                            intent.putExtra("register_name" , "register_name");
                            startActivity(intent);
                            finish();

                        } else {
                            getBadgeNotification(tabLayout_profile, viewPager, user);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //check if the user is logged in
            checkCurrentUser(user);
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // update application __
        if (user != null) {
            checkForUpdates();
        }
        // _____________________
        mAuth.addAuthStateListener(mAuthListener);
    }

    // update application _____________________________________________
    private void checkForUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(installStateUpdatedListener);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Check if immediate update is allowed
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    // Check if flexible update is allowed
                    startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE);
                }
            }
        });

    }

    private void startUpdate(AppUpdateInfo appUpdateInfo, int updateType) {
        // Create AppUpdateOptions
        AppUpdateOptions appUpdateOptions = AppUpdateOptions.newBuilder(updateType).build();

        // Start the update flow
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateActivityResultLauncher,
                    appUpdateOptions
            );
        } catch (Exception e) {
            Log.d(TAG, "startUpdate: "+e.getMessage());
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        // TODO: Show a snackbar or a dialog to inform the user that the update is ready to be installed
        // When the user clicks on the action, call completeUpdate()
        appUpdateManager.completeUpdate();
    }
    // ________________________________________________________________

    @Override
    protected void onResume() {
        super.onResume();
        // update application _____________________________________
        if (user != null) {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {
                        // If the update is downloaded but not installed, notify the user to complete the update.
                        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                            popupSnackbarForCompleteUpdate();
                        }
                    });
        }
        // ________________________________________________________
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, main);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // update application __________
        if (user != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
        // _____________________________
    }
}