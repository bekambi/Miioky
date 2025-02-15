package com.bekambimouen.miiokychallenge.groups;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.adapter.ContributorsAdapter;
import com.bekambimouen.miiokychallenge.groups.adapter.SameTownAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.adapter.AdminAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupMembersLists extends AppCompatActivity {
    private static final String TAG = "GroupMembersLists";
    // widgets
    private RecyclerView recyclerView_administrators, recyclerView_contributors,
            recyclerView_live_in_same_town;
    private RelativeLayout parent, relLayout_view_overlay;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_textview, live_in_same_town;
    private Toolbar toolBar;

    // vars
    private GroupMembersLists context;
    private UserGroups user_groups;
    private ArrayList<String> admin_id_list;
    private ArrayList<String> same_town_list, group_user_followersLiveSameTownList;
    private ArrayList<GroupFollowersFollowing> contributor_list;
    private String admin, contributor, same_town, currentUserTown;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_members_lists);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        admin_id_list = new ArrayList<>();
        contributor_list = new ArrayList<>();
        group_user_followersLiveSameTownList = new ArrayList<>();
        same_town_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
            admin = getIntent().getStringExtra("admin");
            contributor = getIntent().getStringExtra("contributor");
            same_town = getIntent().getStringExtra("same_town");
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

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        toolBar = findViewById(R.id.toolBar);
        arrowBack = findViewById(R.id.arrowBack);
        toolBar_textview = findViewById(R.id.toolBar_textview);
        close = findViewById(R.id.close);
        live_in_same_town = findViewById(R.id.live_in_same_town);

        // administrators
        recyclerView_administrators = findViewById(R.id.recyclerView_administrators);
        LinearLayoutManager layoutManager_administrators = new LinearLayoutManager(context);
        layoutManager_administrators.setInitialPrefetchItemCount(10);
        recyclerView_administrators.setLayoutManager(layoutManager_administrators);
        recyclerView_administrators.setItemAnimator(new DefaultItemAnimator());

        // contributors
        recyclerView_contributors = findViewById(R.id.recyclerView_contributors);
        LinearLayoutManager layoutManager_contributors = new LinearLayoutManager(context);
        layoutManager_contributors.setInitialPrefetchItemCount(10);
        recyclerView_contributors.setLayoutManager(layoutManager_contributors);
        recyclerView_contributors.setItemAnimator(new DefaultItemAnimator());

        // members in same live town
        recyclerView_live_in_same_town = findViewById(R.id.recyclerView_live_in_same_town);
        LinearLayoutManager layoutManager_live_in_same_town = new LinearLayoutManager(context);
        layoutManager_live_in_same_town.setInitialPrefetchItemCount(10);
        recyclerView_live_in_same_town.setLayoutManager(layoutManager_live_in_same_town);
        recyclerView_live_in_same_town.setItemAnimator(new DefaultItemAnimator());

        if (admin != null) {
            toolBar_textview.setVisibility(View.VISIBLE);
            toolBar_textview.setText(context.getString(R.string.administrator));
            recyclerView_administrators.setVisibility(View.VISIBLE);
        }

        if (contributor != null) {
            toolBar_textview.setVisibility(View.VISIBLE);
            toolBar_textview.setText(context.getString(R.string.contributor_in_group));
            recyclerView_contributors.setVisibility(View.VISIBLE);
        }

        if (same_town != null) {
            live_in_same_town.setVisibility(View.VISIBLE);
            recyclerView_live_in_same_town.setVisibility(View.VISIBLE);
        }

        if (admin != null)
            getAdministrators();
        if (contributor != null)
            getContributors();
        if (same_town != null)
            getMembersLiveInSameTown();

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    // get user town
                    getCurrentUserTown(user.getTown_name());
                }
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
            live_in_same_town.setTextColor(context.getColor(R.color.black));
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
            live_in_same_town.setTextColor(context.getColor(R.color.white));
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
            live_in_same_town.setTextColor(context.getColor(R.color.white));
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
            live_in_same_town.setTextColor(context.getColor(R.color.white));
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
            live_in_same_town.setTextColor(context.getColor(R.color.white));
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
            live_in_same_town.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void getAdministrators() {
        // first add admin master
        admin_id_list.add(user_groups.getAdmin_master());

        // to know if current user is admin or moderator
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (follower.isAdminInGroup() || follower.isModerator())
                        admin_id_list.add(follower.getUser_id());
                }

                Collections.reverse(admin_id_list);

                AdminAdapter adminAdapter = new AdminAdapter(context, admin_id_list, user_groups, relLayout_view_overlay);
                recyclerView_administrators.setAdapter(adminAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getContributors() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (!follower.getUser_id().equals(user_id)) {
                        if (!follower.getPost_points().equals(context.getString(R.string.zero))
                                || !follower.getComment_points().equals(context.getString(R.string.zero))
                                || !follower.getShare_points().equals(context.getString(R.string.zero))
                                || !follower.getLike_points().equals(context.getString(R.string.zero))) {

                            contributor_list.add(follower);
                        }
                    }
                }

                Collections.reverse(contributor_list);

                ContributorsAdapter contributorsAdapter = new ContributorsAdapter(context, contributor_list, user_groups, relLayout_view_overlay);
                recyclerView_contributors.setAdapter(contributorsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentUserTown(String town) {
        currentUserTown = town;
        live_in_same_town.setText(Html.fromHtml(context.getString(R.string.members_live_in_same_town)+" "+town));
    }

    private void getMembersLiveInSameTown() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    group_user_followersLiveSameTownList.add(follower.getUser_id());
                }

                for(int i = 0; i < group_user_followersLiveSameTownList.size(); i++){
                    final int count = i;

                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(group_user_followersLiveSameTownList.get(i));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                if (!user.getUser_id().equals(user_id))
                                    if (user.getTown_name().equals(currentUserTown))
                                        same_town_list.add(user.getUser_id());
                            }

                            if(count >= group_user_followersLiveSameTownList.size() -1){

                                Collections.reverse(same_town_list);

                                SameTownAdapter sameTownAdapter = new SameTownAdapter(context, same_town_list, user_groups, relLayout_view_overlay);
                                recyclerView_live_in_same_town.setAdapter(sameTownAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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