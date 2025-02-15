package com.bekambimouen.miiokychallenge.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetUpdateProfile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetVisitProfile;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile.SeeUserAllProfileOnMiioky;
import com.bekambimouen.miiokychallenge.editprofile.ChooseProfilePhoto;
import com.bekambimouen.miiokychallenge.editprofile.EditProfile;
import com.bekambimouen.miiokychallenge.followersfollowings.NbFollowers;
import com.bekambimouen.miiokychallenge.followersfollowings.NbFollowings;
import com.bekambimouen.miiokychallenge.friends.SeeAllMyFriends;
import com.bekambimouen.miiokychallenge.friends.adapter.FriendGridProfileAdapter;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.fun.FunActivity;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.profile.adapter.CompletProfileAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileCollegeAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileEstablishmentAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileFragmentAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileWorkPlaceAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.SuggestionProfileAdapter;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.adapter.SpotlightStoryAdapter;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bekambimouen.miiokychallenge.profile.model.CompletProfilModel;
import com.bekambimouen.miiokychallenge.suggestions.SeeAllSuggestions;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";

    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //widgets
    private RecyclerView recyclerView_spotlight;
    private RecyclerView recyclerView_school, recyclerView_establishment, recyclerView_workplace;
    private CircleImageView user_profile_photo;
    private ImageView profile_photo_blur;
    private TextView tvPosts, town_name, marital_status;
    private TextView tvPosts_fun;
    private TextView full_name;
    private TextView username;
    private TextView bio;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private TextView text1;
    private TextView tv_all_friends;
    private RelativeLayout parent, relLayoutAbonne, relLayoutAbonnement;
    private RelativeLayout relLayout_suggestions, relLayout_about_me, relLayout_marital_status;
    private RelativeLayout relLayout_visit_profil, relLayout_no_connexion, relLayout_view_overlay;

    // suggestions
    private SuggestionProfileAdapter suggestionAdapter;
    private RecyclerView recyclerview_suggestions;
    private ArrayList<User> list, other_part_list, finalUserList, finalList, all_user_list, tamponList;
    private ArrayList<String> following_userIDList, removed_suggestions_list, total_user_id_list;
    // suggestion about school and work place
    private String current_user_hometown, current_user_live_town;
    private ArrayList<SchoolAttended> current_user_schools_list;
    private ArrayList<FrequentedEstablishment> current_user_establishments_list;
    private ArrayList<WorkAt> current_user_workplaces_list;
    private ArrayList<SchoolAttended> schools_suggestion_list;
    private ArrayList<FrequentedEstablishment> establishments_suggestion_list;
    private ArrayList<WorkAt> workplaces_suggestion_list;
    // suggestion about common friends
    private List<String> current_user_friends_suggestion_list, friend_user_friends_list;
    // suggestion about 2 common groups i follow
    private ArrayList<String> group_id_list, members_of_groups_list, filter_members_of_groups_list;
    private int common_groups_members_count = 0;

    // friendship
    private RecyclerView recyclerView_friends;
    private TextView number_of_friends;
    private LinearLayout linLayout_friends;

    // vars
    private Profile context;
    public ArrayList<FriendsFollowerFollowing> friends_List, friends_final_List;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;
    private List<SpolightCover> storyList;
    private String from_publication;
    private String from_fun_publication;
    private String from_profile_fun;
    private String from_view_saved_delete;
    private String from_view_post_delete;
    private double mFollowersCount = 0;
    private double mFollowingCount = 0;
    private double mPostsCount = 0;
    private double mPostsCount_fun = 0;
    private int mNumberCompleteProfilCount, nber_of_friends = 0;
    private String from_camera_video, from_fun;
    private Handler handler;

    //firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_profile);
        context = this;

        try {
            from_fun = getIntent().getStringExtra("from_fun");
            from_camera_video = getIntent().getStringExtra("from_camera_video");
            from_publication = getIntent().getStringExtra("from_publication");
            from_fun_publication = getIntent().getStringExtra("from_fun_publication");
            from_profile_fun = getIntent().getStringExtra("from_profile_fun");
            from_view_saved_delete = getIntent().getStringExtra("from_view_saved_delete");
            from_view_post_delete = getIntent().getStringExtra("from_view_post_delete");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        init();
        dataDownloaded();
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

    private void init_friends() {
        // verify is users are friends
        Query myQuery = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_friend_follower))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    nber_of_friends++;

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    FriendsFollowerFollowing follower = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                    friends_List.add(follower);
                }

                number_of_friends.setText(Html.fromHtml(nber_of_friends +" "+context.getString(R.string.friends)));

                friends_List.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                // to show only six items
                if (friends_List.size() >= 6) {
                    for (int i = 0; i <= 6; i++) {
                        friends_final_List.add(friends_List.get(i));
                    }

                } else {
                    friends_final_List.addAll(friends_List);
                }

                FriendGridProfileAdapter adapter = new FriendGridProfileAdapter(context, friends_final_List, user_id, relLayout_view_overlay);
                recyclerView_friends.setAdapter(adapter);

                if (adapter.getItemCount() <= 0)
                    linLayout_friends.setVisibility(View.GONE);
                else
                    linLayout_friends.setVisibility(View.VISIBLE);

                if (adapter.getItemCount() >= 1)
                    tv_all_friends.setVisibility(View.VISIBLE);
                else
                    tv_all_friends.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv_all_friends.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SeeAllMyFriends.class);
            intent.putExtra("userID", user_id);
            startActivity(intent);
        });
    }

    private void init_spotlight() {
        Query query = myRef
                .child(context.getString(R.string.dbname_story_spotlight_cover))
                .child(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    SpolightCover story = new SpolightCover();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                    assert objectMap != null;
                    story.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
                    story.setMediaUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_mediaUrl))).toString());
                    story.setStoryid(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_storyid))).toString());
                    story.setTitle(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_title))).toString());

                    storyList.add(story);
                }

                Collections.reverse(storyList);
                SpotlightStoryAdapter adapter = new SpotlightStoryAdapter(context, storyList, relLayout_view_overlay);
                recyclerView_spotlight.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        // int friends _____________________________________________________________________________
        tv_all_friends = findViewById(R.id.tv_all_friends);
        number_of_friends = findViewById(R.id.number_of_friends);
        linLayout_friends = findViewById(R.id.linLayout_friends);
        recyclerView_friends = findViewById(R.id.recyclerView_friends);
        recyclerView_friends.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView_friends.addItemDecoration(new GridSpacingItemDecoration(3, 15, false));

        // init spotlight __________________________________________________________________________
        parent = findViewById(R.id.parent);
        recyclerView_spotlight = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerView_spotlight.setLayoutManager(layoutManager);
        recyclerView_school = findViewById(R.id.recyclerView_school);
        recyclerView_school.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_establishment = findViewById(R.id.recyclerView_establishment);
        recyclerView_establishment.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_workplace = findViewById(R.id.recyclerView_workplace);
        recyclerView_workplace.setLayoutManager(new LinearLayoutManager(context));

        recyclerview_suggestions = findViewById(R.id.recyclerview_suggestions);
        recyclerview_suggestions.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        manager.setInitialPrefetchItemCount(10);
        recyclerview_suggestions.setLayoutManager(manager);
        recyclerview_suggestions.setItemAnimator(new DefaultItemAnimator());

        RecyclerView complete_recyclerview = findViewById(R.id.complete_recyclerview);
        LinearLayoutManager manager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        complete_recyclerview.setLayoutManager(manager1);
        complete_recyclerview.setAdapter(new CompletProfileAdapter(context, relLayout_view_overlay));

        relLayoutAbonne = findViewById(R.id.relLayoutAbonne);
        relLayoutAbonnement = findViewById(R.id.relLayoutAbonnement);
        relLayout_suggestions = findViewById(R.id.relLayout_suggestions);
        full_name = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        user_profile_photo = findViewById(R.id.user_profile_photo);
        profile_photo_blur = findViewById(R.id.profile_photo_blur);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        tvPosts = findViewById(R.id.tvPosts);
        tvPosts_fun = findViewById(R.id.tvPosts_fun);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        text1 = findViewById(R.id.text1);
        TextView tv_voir_tout = findViewById(R.id.tv_voir_tout);
        TextView tv_EditProfile = findViewById(R.id.tv_EditProfile);
        town_name = findViewById(R.id.town_name);
        marital_status = findViewById(R.id.marital_status);
        relLayout_about_me = findViewById(R.id.relLayout_about_me);
        relLayout_marital_status = findViewById(R.id.relLayout_marital_status);
        relLayout_no_connexion = findViewById(R.id.relLayout_no_connexion);
        relLayout_visit_profil = findViewById(R.id.relLayout_visit_profil);
        AppCompatButton btn_retry = findViewById(R.id.btn_retry);

        numberCompleteProfileDone();

        tv_EditProfile.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, EditProfile.class);
            startActivity(intent);
        });

        tv_voir_tout.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SeeAllSuggestions.class);
            context.startActivity(intent);
        });

        // active connexion
        btn_retry.setOnClickListener(view1 -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                relLayout_no_connexion.setVisibility(View.VISIBLE);

            } else {
                relLayout_no_connexion.setVisibility(View.GONE);
                super.onResume();
                dataDownloaded();
            }
        });

        relLayout_visit_profil.setOnClickListener(view -> {
            Util.preventTwoClick(relLayout_visit_profil);
            BottomSheetVisitProfile bottomSheet = new BottomSheetVisitProfile(context, user_id, true);
            bottomSheet.show(context.getSupportFragmentManager(), TAG);
        });

        arrowBack.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (from_fun_publication != null) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, FunActivity.class);
                    intent.putExtra("from_fun_publication", "from_fun_publication");
                    startActivity(intent);

                } else if (from_profile_fun != null) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    startActivity(new Intent(context, MainActivity.class));

                } else if (from_view_saved_delete != null) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    startActivity(new Intent(context, MainActivity.class));

                } else if (from_publication != null) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    startActivity(new Intent(context, MainActivity.class));

                } else if (from_view_post_delete != null) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    startActivity(new Intent(context, MainActivity.class));

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    // download data
    private void dataDownloaded() {
        friends_List = new ArrayList<>();
        friends_final_List = new ArrayList<>();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
        storyList = new ArrayList<>();

        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByKey()
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()) {
                        SchoolAttended school = new SchoolAttended();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        school.setSchoolKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_schoolKey))).toString());
                        school.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());
                        school.setUser_school_attended_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended_header))).toString());

                        schools_list.add(school);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        establishment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        establishment.setEstablishmentKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_establishmentKey))).toString());
                        establishment.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());
                        establishment.setUser_establishment_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment_header))).toString());

                        establishments_list.add(establishment);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()) {
                        WorkAt workAt = new WorkAt();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        workAt.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        workAt.setWorkAtKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_workAtKey))).toString());
                        workAt.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());
                        workAt.setUser_work_at_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at_header))).toString());

                        workplaces_list.add(workAt);
                    }

                    relLayoutAbonne.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, NbFollowers.class);
                        Gson gson = new Gson();
                        String myGSon = gson.toJson(user);
                        intent.putExtra("user", myGSon);
                        startActivity(intent);
                    });

                    relLayoutAbonnement.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, NbFollowings.class);
                        Gson gson = new Gson();
                        String myGSon = gson.toJson(user);
                        intent.putExtra("user", myGSon);
                        startActivity(intent);
                    });
                }

                ProfileCollegeAdapter adapter_school = new ProfileCollegeAdapter(context, schools_list);
                recyclerView_school.setAdapter(adapter_school);
                if (adapter_school.getItemCount() == 0)
                    recyclerView_school.setVisibility(View.GONE);

                ProfileEstablishmentAdapter adapter_establishment = new ProfileEstablishmentAdapter(context, establishments_list);
                recyclerView_establishment.setAdapter(adapter_establishment);
                if (adapter_establishment.getItemCount() == 0)
                    recyclerView_establishment.setVisibility(View.GONE);

                ProfileWorkPlaceAdapter adapter_workplace = new ProfileWorkPlaceAdapter(context, workplaces_list);
                recyclerView_workplace.setAdapter(adapter_workplace);
                if (adapter_workplace.getItemCount() == 0)
                    recyclerView_workplace.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        init_spotlight();
        init_friends();
        getPhoneContactsSuggestions();
        setProfileWidgets();
        getFollowersCount();
        getFollowingCount();
        getPostsCount();
        getPostsFunCount();
        configureViewPagerAndTabs();
    }

    private void clearAll(){
        if(following_userIDList != null){
            following_userIDList.clear();
        }
        if(finalList != null){
            finalList.clear();
        }
        if (schools_suggestion_list != null) {
            schools_suggestion_list.clear();
        }
        if (establishments_suggestion_list != null) {
            establishments_suggestion_list.clear();
        }
        if (workplaces_suggestion_list != null) {
            workplaces_suggestion_list.clear();
        }
        if (members_of_groups_list != null) {
            members_of_groups_list.clear();
        }
        if (friend_user_friends_list != null) {
            friend_user_friends_list.clear();
        }
        if (all_user_list != null) {
            all_user_list.clear();
        }
        if(list != null){
            list.clear();
        }
        if (other_part_list != null) {
            other_part_list.clear();
        }
        if (group_id_list != null) {
            group_id_list.clear();
        }
        if (finalUserList != null) {
            finalUserList.clear();
        }
        if (current_user_friends_suggestion_list != null) {
            current_user_friends_suggestion_list.clear();
        }
        if (current_user_schools_list != null) {
            current_user_schools_list.clear();
        }
        if (current_user_establishments_list != null) {
            current_user_establishments_list.clear();
        }
        if (current_user_workplaces_list != null) {
            current_user_workplaces_list.clear();
        }

        if(suggestionAdapter != null){
            suggestionAdapter = null;
        }

        if(recyclerview_suggestions != null){
            handler.post(() -> recyclerview_suggestions.setAdapter(null));
        }

        tamponList = new ArrayList<>();
        finalList = new ArrayList<>();
        all_user_list = new ArrayList<>();
        following_userIDList = new ArrayList<>();
        removed_suggestions_list = new ArrayList<>();
        total_user_id_list = new ArrayList<>();
        schools_suggestion_list = new ArrayList<>();
        establishments_suggestion_list = new ArrayList<>();
        workplaces_suggestion_list = new ArrayList<>();
        current_user_friends_suggestion_list = new ArrayList<>();

        list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        finalUserList = new ArrayList<>();
        // suggestion about school and work place
        current_user_schools_list = new ArrayList<>();
        current_user_establishments_list = new ArrayList<>();
        current_user_workplaces_list = new ArrayList<>();
        friend_user_friends_list = new ArrayList<>();
        members_of_groups_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        filter_members_of_groups_list = new ArrayList<>();
    }

    // suggestions
    void getPhoneContactsSuggestions() {
        clearAll();
        // to remove suggestion close by current user
        Query query4 = myRef.child(context.getString(R.string.dbname_remove_Suggestion))
                .child(user_id);
        query4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    RemoveSuggestionModel suggestionModel = Util_RemoveSuggestionModel.getRemoveSuggestionModel(context, objectMap);
                    removed_suggestions_list.add(suggestionModel.getUser_id());
                }

                // to remove suggestion when i already follow
                Query query3 = myRef.child(context.getString(R.string.dbname_following))
                        .child(user_id);

                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String keyID = ds.getKey();
                            following_userIDList.add(keyID);
                        }

                        // to remove suggestion when they are already friend
                        Query query1 = myRef.child(context.getString(R.string.dbname_friend_following))
                                .child(user_id);

                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    String keyID = ds.getKey();
                                    following_userIDList.add(keyID);
                                }

                                users_from_the_same_establishment_or_same_town();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the current user school and work place
    private void users_from_the_same_establishment_or_same_town() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByKey()
                .equalTo(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        current_user_hometown = Normalizer.normalize(user.getHometown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                        current_user_live_town = Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");

                        SchoolAttended school = new SchoolAttended();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                        current_user_schools_list.add(school);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                        current_user_establishments_list.add(establishment);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()) {
                        WorkAt workAt = new WorkAt();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                        current_user_workplaces_list.add(workAt);
                    }
                }

                // list of all user
                Query query = myRef
                        .child(context.getString(R.string.dbname_users));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            // get all the user to get common friends
                            all_user_list.add(user);

                            // get user's schools
                            if (!user.getUser_id().equals(user_id)) {

                                // get user's hometown/livetown
                                if (Normalizer.normalize(user.getHometown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                        .equalsIgnoreCase(current_user_hometown) ||
                                        Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                .equalsIgnoreCase(current_user_live_town))
                                    if (!total_user_id_list.contains(user.getUser_id())) {
                                        list.add(user);
                                        total_user_id_list.add(user.getUser_id());
                                    }

                                for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()) {
                                    SchoolAttended school = new SchoolAttended();
                                    Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                    assert objectHashMap != null;
                                    school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                    school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                                    schools_suggestion_list.add(school);
                                }
                                // get user's establishments
                                for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
                                    FrequentedEstablishment establishment = new FrequentedEstablishment();
                                    Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                    assert objectHashMap != null;
                                    establishment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                    establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                                    establishments_suggestion_list.add(establishment);
                                }
                                // get user's workplace
                                for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()) {
                                    WorkAt workAt = new WorkAt();
                                    Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                    assert objectHashMap != null;
                                    workAt.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                    workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                                    workplaces_suggestion_list.add(workAt);
                                }

                                // verify if we are in the same school
                                for (int j = 0; j < current_user_schools_list.size(); j++) {

                                    for (int i = 0; i < schools_suggestion_list.size(); i++) {
                                        if (schools_suggestion_list.get(i).getUser_school_attended().equals(current_user_schools_list.get(j).getUser_school_attended())) {
                                            if (!total_user_id_list.contains(schools_suggestion_list.get(i).getUser_id())) {
                                                list.add(user);
                                                total_user_id_list.add(schools_suggestion_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }

                                // verify if we are in the same establishment
                                for (int j = 0; j < current_user_establishments_list.size(); j++) {

                                    for (int i = 0; i < establishments_suggestion_list.size(); i++) {
                                        if (establishments_suggestion_list.get(i).getUser_establishment().equals(current_user_establishments_list.get(j).getUser_establishment())) {
                                            if (!total_user_id_list.contains(establishments_suggestion_list.get(i).getUser_id())) {
                                                list.add(user);
                                                total_user_id_list.add(establishments_suggestion_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }

                                // verify if we are in the same workplace
                                for (int j = 0; j < current_user_workplaces_list.size(); j++) {

                                    for (int i = 0; i < workplaces_suggestion_list.size(); i++) {
                                        if (workplaces_suggestion_list.get(i).getUser_work_at().equals(current_user_workplaces_list.get(j).getUser_work_at())) {
                                            if (!total_user_id_list.contains(workplaces_suggestion_list.get(i).getUser_id())) {
                                                list.add(user);
                                                total_user_id_list.add(workplaces_suggestion_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // get common friends
                        getCommonFriends(all_user_list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the common friends ___________________________________________________
    private void getCommonFriends(ArrayList<User> all_user_list) {
        Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String keyID = dataSnapshot.getKey();

                    current_user_friends_suggestion_list.add(keyID);
                }

                // get the friend user friend list
                for (int i = 0; i < all_user_list.size(); i++) {
                    final int count = i;
                    Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                            .child(all_user_list.get(i).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String keyID = dataSnapshot.getKey();

                                friend_user_friends_list.add(keyID);
                            }

                            // get the common friends
                            if (count >= all_user_list.size() - 1) {
                                for (String ID: current_user_friends_suggestion_list) {
                                    for (int j = 0; j < friend_user_friends_list.size(); j++) {
                                        if (ID.equals(friend_user_friends_list.get(j))) {

                                            if (!total_user_id_list.contains(friend_user_friends_list.get(j))) {
                                                if (!friend_user_friends_list.get(j).equals(user_id)) {
                                                    Query query = myRef
                                                            .child(context.getString(R.string.dbname_users))
                                                            .orderByChild(context.getString(R.string.field_user_id))
                                                            .equalTo(friend_user_friends_list.get(j));

                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot ds : snapshot.getChildren()){
                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                assert objectMap != null;
                                                                User user = Util_User.getUser(context, objectMap, ds);

                                                                list.add(user);
                                                                total_user_id_list.add(user.getUser_id());
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                // get the common 2 group user has with another member
                                getCommonGroups();
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

    // get the common groups ___________________________________________________
    private void getCommonGroups() {
        // get the groups user follow
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);
                    group_id_list.add(following.getGroup_id());
                }

                if (!group_id_list.isEmpty()) {
                    //get all member of each group i follow
                    for (int i = 0; i < group_id_list.size(); i++) {
                        final int count = i;
                        Query myQuery = myRef
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(group_id_list.get(i));
                        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    String ID = ds.getKey();
                                    assert ID != null;
                                    if (!ID.equals(user_id))
                                        members_of_groups_list.add(ds.getKey());
                                }

                                if (count >= group_id_list.size() - 1) {

                                    if (!members_of_groups_list.isEmpty()) {
                                        for (int i = 0; i < members_of_groups_list.size(); i++) {
                                            final int count = i;
                                            // verification if this member is in same another group
                                            if (filter_members_of_groups_list.contains(members_of_groups_list.get(i))) {
                                                common_groups_members_count++;
                                                // verification if user_id is already saved
                                                if (!total_user_id_list.contains(members_of_groups_list.get(i))) {
                                                    total_user_id_list.add(members_of_groups_list.get(i));

                                                    // list of all user
                                                    Query query = myRef
                                                            .child(context.getString(R.string.dbname_users))
                                                            .orderByChild(context.getString(R.string.field_user_id))
                                                            .equalTo(members_of_groups_list.get(i));
                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                assert objectMap != null;
                                                                User user = Util_User.getUser(context, objectMap, ds);

                                                                if (!user.getUser_id().equals(user_id))
                                                                    list.add(user);
                                                            }
                                                            // _____________________________________________________________
                                                            if (count >= members_of_groups_list.size() - 1) {
                                                                getAllOtherUsers();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                } else {
                                                    // the list already contain user Id
                                                    if (count >= members_of_groups_list.size() - 1) {
                                                        getAllOtherUsers();
                                                    }
                                                }
                                            } else {
                                                filter_members_of_groups_list.add(members_of_groups_list.get(i));
                                            }
                                        }
                                        // _____________________________________________________________
                                        if (common_groups_members_count == 0) {
                                            getAllOtherUsers();
                                        }

                                    } else {
                                        getAllOtherUsers();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } else {
                    getAllOtherUsers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // add other part of the users
    private void getAllOtherUsers() {
        for (int i = 0; i < all_user_list.size(); i++) {
            final  int count = i;

            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(all_user_list.get(i).getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        if (!total_user_id_list.contains(user.getUser_id()) && !user.getUser_id().equals(user_id)) {
                            other_part_list.add(user);
                            total_user_id_list.add(user.getUser_id());
                        }

                        if (count >= all_user_list.size() - 1) {
                            displayTheList();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void displayTheList() {
        //sort for newest to oldest
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        finalUserList.addAll(list);

        //sort for newest to oldest to other part of list
        other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        finalUserList.addAll(other_part_list);

        // remove the users that i already follow
        if (!following_userIDList.isEmpty()) {
            for (int i = 0; i < finalUserList.size(); i++) {
                if (!following_userIDList.contains(finalUserList.get(i).getUser_id())) {
                    // liste des utilistauers auxquels je ne suis pas abonnÃ©
                    tamponList.add(finalUserList.get(i));
                }
            }
        } else {
            tamponList.addAll(finalUserList);
        }

        // remove the delete suggestions and this user his suggestion
        for (int i = 0; i < tamponList.size(); i++) {
            if (!removed_suggestions_list.contains(tamponList.get(i).getUser_id())
                    && !tamponList.get(i).getUser_id().equals(user_id))
                finalList.add(tamponList.get(i));
        }

        suggestionAdapter = new SuggestionProfileAdapter(context, finalList, relLayout_view_overlay);
        recyclerview_suggestions.setAdapter(suggestionAdapter);

        if (suggestionAdapter.getItemCount() == 0) {
            relLayout_suggestions.setVisibility(View.GONE);
        }
    }
    // end of friends suggestions __________________________________________________________________

    private void numberCompleteProfileDone() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        assert user != null;
        Query query = myRef.child(getString(R.string.dbname_complet_profile))
                .child(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String profilUrl = Objects.requireNonNull(snapshot.getValue(CompletProfilModel.class)).getProfileUrl();
                    String bio = Objects.requireNonNull(snapshot.getValue(CompletProfilModel.class)).getBio();
                    String username = Objects.requireNonNull(snapshot.getValue(CompletProfilModel.class)).getUsername();

                    mNumberCompleteProfilCount = 0;

                    if (!TextUtils.isEmpty(profilUrl)) {
                        mNumberCompleteProfilCount++;
                    }

                    if (!TextUtils.isEmpty(bio)) {
                        mNumberCompleteProfilCount++;
                    }

                    if (!TextUtils.isEmpty(username)) {
                        mNumberCompleteProfilCount++;
                    }

                    text1.setText(String.valueOf(mNumberCompleteProfilCount));
                } catch (NullPointerException e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configureViewPagerAndTabs() {
        TabLayout tabLayout_profile = findViewById(R.id.tabLayout);
        ViewPager2 viewPager_main = findViewById(R.id.viewPager);
        ProfileFragmentAdapter adapter = new ProfileFragmentAdapter(context);
        viewPager_main.setAdapter(adapter);
        viewPager_main.setFocusable(true);
        viewPager_main.requestFocus();

        // from_fun_activity is when user cliked on profil or username
        if (from_camera_video != null || from_fun != null || from_profile_fun != null) {
            viewPager_main.setCurrentItem(1);
            tvPosts.setVisibility(View.GONE);
            tvPosts_fun.setVisibility(View.VISIBLE);
        }

        String[] titles = {"Battle", "Videos"};

        new TabLayoutMediator(tabLayout_profile, viewPager_main,
                (tabLayout, position) -> tabLayout.setText(titles[position])).attach();

        // to view battle and fun post number
        tabLayout_profile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    tvPosts.setVisibility(View.GONE);
                    tvPosts_fun.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    tvPosts.setVisibility(View.VISIBLE);
                    tvPosts_fun.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getPostsCount(){
        mPostsCount = 0;
        Query query = myRef.child(getString(R.string.dbname_battle))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) singleSnapshot.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, singleSnapshot);

                    if (!model.isSuppressed())
                        mPostsCount++;
                }

                double count;
                if (mPostsCount >= 1000) {
                    count = mPostsCount/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    tvPosts.setText(tv_count);

                } else {
                    tvPosts.setText(String.valueOf((int) mPostsCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostsFunCount(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_fun))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found fun post:" + singleSnapshot.getValue());
                    mPostsCount_fun++;
                }

                double count;
                if (mPostsCount_fun >= 1000) {
                    count = mPostsCount_fun/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    tvPosts_fun.setText(tv_count);

                } else {
                    tvPosts_fun.setText(String.valueOf((int) mPostsCount_fun));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersCount(){
        mFollowersCount = 0;

        Query query = myRef.child(getString(R.string.dbname_followers))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }

                double count;
                if (mFollowersCount >= 1000) {
                    count = mFollowersCount/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    tvFollowers.setText(tv_count);

                } else {
                    tvFollowers.setText(String.valueOf((int) mFollowersCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount(){
        mFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }

                double count;
                if (mFollowingCount >= 1000) {
                    count = mFollowingCount/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    tvFollowing.setText(tv_count);

                } else {
                    tvFollowing.setText(String.valueOf((int) mFollowingCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(user_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo_blur);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    String user_bio = user.getBio();
                    if (TextUtils.isEmpty(user_bio))
                        bio.setVisibility(View.GONE);
                    bio.setText(user_bio);

                    username.setText(user.getUsername());
                    full_name.setText(user.getFullName());
                    town_name.setText(Html.fromHtml(context.getString(R.string.live_in)+" <b>"+user.getTown_name()+"</b>"));
                    String status = user.getMarital_status();
                    if (TextUtils.isEmpty(status)) {
                        relLayout_marital_status.setVisibility(View.GONE);
                    }
                    marital_status.setText(status);

                    profile_photo_blur.setOnClickListener(view -> {
                        if (!user.getProfileUrl().isEmpty()) {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, SeeUserAllProfileOnMiioky.class);
                            intent.putExtra("userID", user.getUser_id());
                            startActivity(intent);
                        }
                    });

                    relLayout_about_me.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, SeeSectionAboutMe.class);
                        intent.putExtra("userID", user.getUser_id());
                        startActivity(intent);
                    });

                    user_profile_photo.setOnClickListener(view -> {
                        if (allPermissionsGrented()) {
                            int REQUEST_CODE_CAMERA = 101;
                            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

                        } else {
                            if (user.getProfileUrl().isEmpty()) {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ChooseProfilePhoto.class);
                                context.startActivity(intent);
                            } else {
                                BottomSheetUpdateProfile bottomSheet = new BottomSheetUpdateProfile(context, user_id, user.getProfileUrl());
                                bottomSheet.show(context.getSupportFragmentManager(), TAG);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError  error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (allPermissionsGrented()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ChooseProfilePhoto.class);
            context.startActivity(intent);
        }
    }

    private boolean allPermissionsGrented() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public void internetIsConnected() {
        // internet control
        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

        if (!isConnected) {
            relLayout_no_connexion.setVisibility(View.VISIBLE);

        } else {
            relLayout_no_connexion.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        internetIsConnected();
    }
}