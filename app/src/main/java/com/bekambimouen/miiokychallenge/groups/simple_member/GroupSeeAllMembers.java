package com.bekambimouen.miiokychallenge.groups.simple_member;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.PreloadLinearLayoutManager;
import com.bekambimouen.miiokychallenge.groups.GroupMembersLists;
import com.bekambimouen.miiokychallenge.groups.adapter.ContributorsAdapter;
import com.bekambimouen.miiokychallenge.groups.adapter.SameTownAdapter;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.GroupInviteNewMembers;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.adapter.AdminAdapter;
import com.bekambimouen.miiokychallenge.groups.simple_member.adapter.GoMembersProfileAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSeeAllMembers extends AppCompatActivity {
    private static final String TAG = "GroupSeeAllMembers";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private CircleImageView profile_photo;
    private TextView username, full_name, live_in_same_town;
    private LinearLayout linLayout_members, linLayout_recyclerview, linLayout_contributors,
            linLayout_live_in_same_town, linLayout_all_members;
    private RelativeLayout parent, relLayout_admin_button, relLayout_contributor_button,
            relLayout_same_town_button, relLayout_view_overlay;
    private RecyclerView recyclerView_search;
    private MyEditText edit_search;
    private ImageView erase;
    // administrators
    private RecyclerView recyclerView_administrators, recyclerView_contributors,
            recyclerView_live_in_same_town, recyclerView_all_members;

    // theme
    private RelativeLayout arrowBack, relLayout_invite;
    private ImageView close, group_icon;
    private TextView toolBar_textview;
    private Toolbar toolBar;

    // vars
    private GroupSeeAllMembers context;
    private AdminAdapter adminAdapter;
    private ContributorsAdapter contributorsAdapter;
    private SameTownAdapter sameTownAdapter;
    private UserGroups user_groups;
    private GoMembersProfileAdapter adapter;
    private ArrayList<String> user_idList, admin_id_list, final_admin_id_list;
    private ArrayList<String> group_user_followersLiveSameTownList, list, user_SameTownList, final_user_SameTownList,
            all_members_list;
    private ArrayList<GroupFollowersFollowing> contributors_list, final_contributors_list;
    private ArrayList<User> userList;
    private String currentUserTown;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_see_all_members);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userList = new ArrayList<>();
        user_idList = new ArrayList<>();
        admin_id_list = new ArrayList<>();
        final_admin_id_list = new ArrayList<>();
        contributors_list = new ArrayList<>();
        final_contributors_list = new ArrayList<>();
        group_user_followersLiveSameTownList = new ArrayList<>();
        all_members_list = new ArrayList<>();
        list = new ArrayList<>();
        user_SameTownList = new ArrayList<>();
        final_user_SameTownList = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        theme();
        getData();

        fetchUsers();

        adapter = new GoMembersProfileAdapter(context, userList, user_groups, relLayout_view_overlay);

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();

                if (char_length == 0) {
                    linLayout_recyclerview.setVisibility(View.GONE);
                    linLayout_members.setVisibility(View.VISIBLE);

                } else {
                    linLayout_members.setVisibility(View.GONE);
                    linLayout_recyclerview.setVisibility(View.VISIBLE);

                }

                if (char_length != 0)
                    erase.setVisibility(View.VISIBLE);
                else
                    erase.setVisibility(View.GONE);
                // filter recycler view when text is changed
                adapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        erase.setOnClickListener(view1 -> {
            Objects.requireNonNull(edit_search.getText()).clear();
            edit_search.requestFocus();
        });
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
        erase = findViewById(R.id.erase);
        group_icon = findViewById(R.id.group_icon);

        relLayout_invite = findViewById(R.id.relLayout_invite);
        RelativeLayout relLayout_user = findViewById(R.id.relLayout_user);
        relLayout_admin_button = findViewById(R.id.relLayout_admin_button);
        relLayout_contributor_button = findViewById(R.id.relLayout_contributor_button);
        relLayout_same_town_button = findViewById(R.id.relLayout_same_town_button);
        linLayout_members = findViewById(R.id.linLayout_members);
        linLayout_contributors = findViewById(R.id.linLayout_contributors);
        linLayout_live_in_same_town = findViewById(R.id.linLayout_live_in_same_town);
        linLayout_all_members = findViewById(R.id.linLayout_all_members);
        linLayout_recyclerview = findViewById(R.id.linLayout_recyclerview);
        profile_photo = findViewById(R.id.profile_photo);
        username = findViewById(R.id.username);
        full_name = findViewById(R.id.full_name);
        live_in_same_town = findViewById(R.id.live_in_same_town);
        edit_search = findViewById(R.id.edit_search);
        recyclerView_search = findViewById(R.id.recyclerView_search);
        LinearLayoutManager layoutManager_search = new LinearLayoutManager(context);
        layoutManager_search.setInitialPrefetchItemCount(10);
        recyclerView_search.setLayoutManager(layoutManager_search);
        recyclerView_search.setItemAnimator(new DefaultItemAnimator());

        // administrators
        recyclerView_administrators = findViewById(R.id.recyclerView_administrators);
        LinearLayoutManager layoutManager_administrator = new LinearLayoutManager(context);
        layoutManager_administrator.setInitialPrefetchItemCount(10);
        recyclerView_administrators.setLayoutManager(layoutManager_administrator);
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

        // all members
        recyclerView_all_members = findViewById(R.id.recyclerView_all_members);
        LinearLayoutManager layoutManager_all_members = new LinearLayoutManager(context);
        layoutManager_all_members.setInitialPrefetchItemCount(10);
        recyclerView_all_members.setLayoutManager(layoutManager_all_members);
        recyclerView_all_members.setItemAnimator(new DefaultItemAnimator());

        getAdministrators();
        getContributors();
        // ____________________________________________________________________________________

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

                    String profileUrl = user.getProfileUrl();
                    String userName = user.getUsername();
                    String fullName = user.getFullName();
                    String userID = user.getUser_id();
                    String town_name = user.getTown_name();

                    // get user town
                    getCurrentUserTown(town_name);

                    username.setText(userName);
                    full_name.setText(fullName);

                    if (!context.isFinishing()) {
                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(profileUrl)
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    relLayout_user.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
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

        relLayout_invite.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupInviteNewMembers.class);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
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
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            relLayout_invite.setBackgroundResource(R.drawable.selector_circle);
            group_icon.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
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
            relLayout_invite.setBackgroundResource(R.drawable.selector_circle2);
            group_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
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
            relLayout_invite.setBackgroundResource(R.drawable.selector_circle2);
            group_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
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
            relLayout_invite.setBackgroundResource(R.drawable.selector_circle2);
            group_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
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
            relLayout_invite.setBackgroundResource(R.drawable.selector_circle2);
            group_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
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
            relLayout_invite.setBackgroundResource(R.drawable.selector_circle2);
            group_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    // administrator or moderator in group
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

                for (int i = 0; i < admin_id_list.size(); i++) {
                    if (i < 3)
                        final_admin_id_list.add(admin_id_list.get(i));
                }

                Collections.reverse(final_admin_id_list);

                adminAdapter = new AdminAdapter(context, final_admin_id_list, user_groups, relLayout_view_overlay);
                recyclerView_administrators.setAdapter(adminAdapter);

                // go list class
                relLayout_admin_button.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(user_groups);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupMembersLists.class);
                    intent.putExtra("user_groups", myGson);
                    intent.putExtra("admin", "admin");
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // contributor in group
    private void getContributors() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contributors_list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (!follower.getUser_id().equals(user_id)) {
                        if (!follower.getPost_points().equals(context.getString(R.string.zero))
                                || !follower.getComment_points().equals(context.getString(R.string.zero))
                                || !follower.getShare_points().equals(context.getString(R.string.zero))
                                || !follower.getLike_points().equals(context.getString(R.string.zero))) {

                            contributors_list.add(follower);
                        }

                        list.add(follower.getUser_id());
                    }
                }

                getGroupFollowersList(list);

                for (int i = 0; i < contributors_list.size(); i++) {
                    if (i < 3)
                        final_contributors_list.add(contributors_list.get(i));
                }

                contributorsAdapter = new ContributorsAdapter(context, final_contributors_list, user_groups, relLayout_view_overlay);
                recyclerView_contributors.setAdapter(contributorsAdapter);

                if (contributorsAdapter.getItemCount() != 0)
                    linLayout_contributors.setVisibility(View.VISIBLE);

                relLayout_contributor_button.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(user_groups);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupMembersLists.class);
                    intent.putExtra("user_groups", myGson);
                    intent.putExtra("contributor", "contributor");
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupFollowersList(ArrayList<String> list) {
        group_user_followersLiveSameTownList.addAll(list);
        getMembersLiveInSameTown();
        getAllMembers();
    }

    private void getCurrentUserTown(String town) {
        currentUserTown = town;
        live_in_same_town.setText(Html.fromHtml(context.getString(R.string.members_live_in_same_town)+" "+town));
    }

    // member in same town
    private void getMembersLiveInSameTown() {
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

                        if (user.getTown_name().equals(currentUserTown))
                            user_SameTownList.add(user.getUser_id());
                    }

                    if(count >= group_user_followersLiveSameTownList.size() -1){

                        for (int i = 0; i < user_SameTownList.size(); i++) {
                            if (i < 3)
                                final_user_SameTownList.add(user_SameTownList.get(i));
                        }
                        Collections.reverse(final_user_SameTownList);

                        sameTownAdapter = new SameTownAdapter(context, final_user_SameTownList, user_groups, relLayout_view_overlay);
                        recyclerView_live_in_same_town.setAdapter(sameTownAdapter);

                        if (sameTownAdapter.getItemCount() != 0)
                            linLayout_live_in_same_town.setVisibility(View.VISIBLE);

                        relLayout_same_town_button.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user_groups);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupMembersLists.class);
                            intent.putExtra("user_groups", myGson);
                            intent.putExtra("same_town", "same_town");
                            startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // all member in group
    private void getAllMembers() {
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

                        all_members_list.add(user.getUser_id());
                    }

                    if(count >= group_user_followersLiveSameTownList.size() -1){
                        Collections.reverse(all_members_list);

                        sameTownAdapter = new SameTownAdapter(context, all_members_list, user_groups, relLayout_view_overlay);
                        recyclerView_all_members.setAdapter(sameTownAdapter);

                        if (sameTownAdapter.getItemCount() != 0)
                            linLayout_all_members.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // to search
    private void getData() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    String user_id = follower.getUser_id();
                    user_idList.add(user_id);
                }

                for (int i = 0; i < user_idList.size(); i++) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query1 = ref
                            .child(context.getString(R.string.dbname_users))
                            .orderByKey()
                            .equalTo(user_idList.get(i));
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                userList.add(user);
                            }

                            if (userList.size() == user_idList.size()) {
                                recyclerView_search.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
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

    /**
     * fetches json by making http calls
     */
    private void fetchUsers() {
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(URL,
                response -> {
                    if (response == null) {
                        Toast.makeText(context, "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<User> items = new Gson().fromJson(response.toString(), new TypeToken<List<User>>() {
                    }.getType());

                    // adding contacts to contacts list
                    userList.clear();
                    userList.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.d(TAG, "fetchUsers: "+ error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
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