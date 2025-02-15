package com.bekambimouen.miiokychallenge.search;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetVisitProfile;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile.SeeUserAllProfileOnMiioky;
import com.bekambimouen.miiokychallenge.followersfollowings.NbFollowers;
import com.bekambimouen.miiokychallenge.followersfollowings.NbFollowings;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.friends.adapter.FriendGridProfileAdapter;
import com.bekambimouen.miiokychallenge.friends.bottomsheet.BottomSheetManegeFriend;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.friends.utils_map.Utils_Map_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.friends.utils_map.Utils_Map_SubscriptionRequestModel;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.profile.SeeSectionAboutMe;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileCollegeAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileEstablishmentAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileWorkPlaceAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.SuggestionProfileAdapter;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bekambimouen.miiokychallenge.search.adapter.SpotlightStorySearchAdapter;
import com.bekambimouen.miiokychallenge.search.adapter.ViewProfileFragmentAdapter;
import com.bekambimouen.miiokychallenge.search.user_common_friends.SeeUserFriends;
import com.bekambimouen.miiokychallenge.suggestions.SeeAllSuggestions;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.views_count.VisitProfileManager;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
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

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";

    //widgets
    private LinearLayout linLayout_details, linLayout_friends;
    private RelativeLayout parent, relLayoutAbonnement, relLayoutAbonne, relLayout_spotlight;
    private RelativeLayout relLayout_suggestions, relLayout_marital_status;
    private TextView mPosts, mPostsFun, username, fullName, bio,
            button_follow, button_unfollow, button_sent, tvFollowers, tvFollowing, town_name, marital_status;
    private CircleImageView user_profile_photo;
    private ImageView profile_photo_blur;
    private RecyclerView recyclerView_spotlight;
    private RecyclerView recyclerView_school, recyclerView_establishment, recyclerView_workplace;
    private RelativeLayout relLayout_visit_profil, relLayout_no_connexion, relLayout_view_overlay;

    // suggestions
    private SuggestionProfileAdapter adapter;
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
    // suggestion abut common friends
    private List<String> current_user_friends_suggestion_list, friend_user_friends_list;
    // suggestion abut 2 common groups i follow
    private ArrayList<String> group_id_list, members_of_groups_list, filter_members_of_groups_list;
    private int common_groups_members_count = 0;

    // friendship
    private RecyclerView recyclerView_friends;
    private TextView tv_all_friends, number_of_friends, see_about_me;
    private LinearLayout linLayout_not_yet_friend, linLayout_already_friend;
    private RelativeLayout relLayout_invite_button, relLayout_cancel_invite_button, relLayout_unfriend,
            relLayout_about_me;
    // vars
    private List<SpolightCover> storyList;
    private ArrayList<FriendsFollowerFollowing> profile_user_friends_List, friends_final_List, tampon_list, tampon_list2, final_list;
    private ArrayList<String> current_user_friends_list;
    private int nber_of_friends = 0;
    // number of followings
    private ArrayList<User> following_userList;
    private ArrayList<String> following_userID_List, friends_following_userIDList, final_following_userIDList;
    // number of followers
    private ArrayList<User> followers_userList;
    private ArrayList<String> followers_userIDList, friends_followers_userIDList, final_followers_userIDList;

    //vars
    private ViewProfile context;
    public User user;
    private String from_fun;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;
    private Handler handler;
    private double mPostsCount = 0;
    private double mPostsCount_fun = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_view_profile);
        context = this;

        try{
            Gson gson = new Gson();
            user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
            from_fun = getIntent().getStringExtra("from_fun");
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: "  + e.getMessage() );
        }

        // here intent_fun can't be null
        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        // number of followings
        following_userID_List = new ArrayList<>();
        friends_following_userIDList = new ArrayList<>();
        final_following_userIDList = new ArrayList<>();
        following_userList = new ArrayList<>();
        // number of followers
        followers_userIDList = new ArrayList<>();
        friends_followers_userIDList = new ArrayList<>();
        final_followers_userIDList = new ArrayList<>();
        followers_userList = new ArrayList<>();

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

    // appelé dans BattleViewProfileFragment
    public User getUser() {
        return user;
    }

    public RelativeLayout getRelLayout_view_overlay() {
        return relLayout_view_overlay;
    }

    private void init_friends() {
        // verify is users are friends
        Query myQuery = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_friend_follower))
                .child(user.getUser_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    nber_of_friends++;

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    FriendsFollowerFollowing follower = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                    profile_user_friends_List.add(follower);
                }

                number_of_friends.setText(Html.fromHtml(nber_of_friends +" "+context.getString(R.string.friends)));


                // to first get the common friends ___________________________________________________
                Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                        .child(user_id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            String keyID = dataSnapshot.getKey();
                            assert keyID != null;
                            if (!keyID.equals(user.getUser_id()))
                                current_user_friends_list.add(keyID);
                        }

                        // to first show the common friends
                        for (String ID: current_user_friends_list) {
                            for (int i = 0; i < profile_user_friends_List.size(); i++) {
                                if (ID.equals(profile_user_friends_List.get(i).getUser_id())) {
                                    tampon_list.add(profile_user_friends_List.get(i));
                                }
                            }
                        }

                        tampon_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                .compareTo(String.valueOf(o1.getDate_created())));

                        final_list.addAll(tampon_list);

                        for (int i = 0; i < profile_user_friends_List.size(); i++) {
                            // remove the common friends from the list
                            if (!final_list.contains(profile_user_friends_List.get(i)))
                                tampon_list2.add(profile_user_friends_List.get(i));
                        }

                        tampon_list2.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                .compareTo(String.valueOf(o1.getDate_created())));

                        final_list.addAll(tampon_list2);

                        // to show only six items
                        if (final_list.size() >= 6) {
                            for (int i = 0; i <= 6; i++) {
                                friends_final_List.add(final_list.get(i));
                            }

                        } else {
                            friends_final_List.addAll(final_list);
                        }

                        FriendGridProfileAdapter adapter = new FriendGridProfileAdapter(context, friends_final_List, user.getUser_id(), relLayout_view_overlay);
                        recyclerView_friends.setAdapter(adapter);

                        if (adapter.getItemCount() == 0) {
                            linLayout_friends.setVisibility(View.GONE);
                            tv_all_friends.setVisibility(View.GONE);
                        }
                        if (adapter.getItemCount() >= 1)
                            linLayout_friends.setVisibility(View.VISIBLE);
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

        tv_all_friends.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SeeUserFriends.class);
            intent.putExtra("userID", user.getUser_id());
            startActivity(intent);
        });
    }

    private void init_spotlight(User user) {
        Query query = myRef
                .child(getString(R.string.dbname_story_spotlight_cover))
                .child(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    SpolightCover story = new SpolightCover();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                    assert objectMap != null;
                    story.setUser_id(Objects.requireNonNull(objectMap.get(getString(R.string.field_user_id))).toString());
                    story.setMediaUrl(Objects.requireNonNull(objectMap.get(getString(R.string.field_mediaUrl))).toString());
                    story.setStoryid(Objects.requireNonNull(objectMap.get(getString(R.string.field_storyid))).toString());
                    story.setTitle(Objects.requireNonNull(objectMap.get(getString(R.string.field_title))).toString());

                    storyList.add(story);
                }

                Collections.reverse(storyList);
                SpotlightStorySearchAdapter adapter = new SpotlightStorySearchAdapter(context, storyList, relLayout_view_overlay);
                recyclerView_spotlight.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (adapter.getItemCount() == 0) {
                    relLayout_spotlight.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        parent = findViewById(R.id.parent);
        recyclerview_suggestions = findViewById(R.id.recyclerview_suggestions);
        recyclerview_suggestions.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        manager.setInitialPrefetchItemCount(10);
        recyclerview_suggestions.setLayoutManager(manager);
        recyclerview_suggestions.setItemAnimator(new DefaultItemAnimator());

        username = findViewById(R.id.username);
        fullName = findViewById(R.id.full_name);
        bio = findViewById(R.id.bio);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        linLayout_details = findViewById(R.id.linLayout_details);
        linLayout_friends = findViewById(R.id.linLayout_friends);
        user_profile_photo = findViewById(R.id.user_profile_photo);
        profile_photo_blur = findViewById(R.id.profile_photo_blur);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayoutAbonne = findViewById(R.id.relLayoutAbonne);
        relLayoutAbonnement = findViewById(R.id.relLayoutAbonnement);
        RelativeLayout relLayoutDiscussion = findViewById(R.id.relLayoutDiscussion);
        RelativeLayout relLayout_discussion_friend = findViewById(R.id.relLayout_discussion_friend);
        relLayout_suggestions = findViewById(R.id.relLayout_suggestions);
        mPosts = findViewById(R.id.tvPosts);
        mPostsFun = findViewById(R.id.tvPosts_fun);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        button_follow = findViewById(R.id.button_follow);
        button_unfollow = findViewById(R.id.button_unfollow);
        button_sent = findViewById(R.id.button_sent);
        TextView tv_voir_tout = findViewById(R.id.tv_voir_tout);
        town_name = findViewById(R.id.town_name);
        marital_status = findViewById(R.id.marital_status);
        relLayout_about_me = findViewById(R.id.relLayout_about_me);
        relLayout_marital_status = findViewById(R.id.relLayout_marital_status);
        relLayout_no_connexion = findViewById(R.id.relLayout_no_connexion);
        relLayout_visit_profil = findViewById(R.id.relLayout_visit_profil);
        AppCompatButton btn_retry = findViewById(R.id.btn_retry);

        // friends list ____________________________________________________________________________
        tv_all_friends = findViewById(R.id.tv_all_friends);
        number_of_friends = findViewById(R.id.number_of_friends);
        see_about_me = findViewById(R.id.see_about_me);
        relLayout_invite_button = findViewById(R.id.relLayout_invite_button);
        relLayout_cancel_invite_button = findViewById(R.id.relLayout_cancel_invite_button);
        relLayout_unfriend = findViewById(R.id.relLayout_unfriend);
        linLayout_not_yet_friend = findViewById(R.id.linLayout_not_yet_friend);
        linLayout_already_friend = findViewById(R.id.linLayout_already_friend);

        recyclerView_friends = findViewById(R.id.recyclerView_friends);
        recyclerView_friends.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView_friends.addItemDecoration(new GridSpacingItemDecoration(3, 15, false));
        // sptLight and others _____________________________________________________________________
        relLayout_spotlight = findViewById(R.id.relLayout_spotlight);
        recyclerView_spotlight = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerView_spotlight.setLayoutManager(layoutManager);
        recyclerView_school = findViewById(R.id.recyclerView_school);
        recyclerView_school.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_establishment = findViewById(R.id.recyclerView_establishment);
        recyclerView_establishment.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_workplace = findViewById(R.id.recyclerView_workplace);
        recyclerView_workplace.setLayoutManager(new LinearLayoutManager(context));

        relLayoutDiscussion.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("to_chat", myGson);
            context.startActivity(intent);
        });

        relLayout_discussion_friend.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, MessageActivity.class);

            intent.putExtra("to_chat", myGson);
            context.startActivity(intent);
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
            BottomSheetVisitProfile bottomSheet = new BottomSheetVisitProfile(context, user.getUser_id(), false);
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
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    // download data
    private void dataDownloaded() {
        storyList = new ArrayList<>();
        profile_user_friends_List = new ArrayList<>();
        friends_final_List = new ArrayList<>();
        current_user_friends_list = new ArrayList<>();
        tampon_list = new ArrayList<>();
        tampon_list2 = new ArrayList<>();
        final_list = new ArrayList<>();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();

        getUserDetails();
        getPhoneContactsSuggestions();

        if (user != null) {
            follow_unfollow(user);

            setProfileWidgets();
            init_spotlight(user);
            init_friends();
            getFollowingCount(user);
            getFollowersCount(user);
            getPostsCount(user);
            getPostsFunCount(user);

            getFollowers(user);
        }
        configureViewPagerAndTabs();
    }

    // the user's details
    private void getUserDetails() {
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    see_about_me.setText(Html.fromHtml(context.getString(R.string.see_the_section_about)+" "+user.getUsername()));

                    // user's informations
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()){
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
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()){
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
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()){
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

                    // user friendship
                    isFriendFollowing(user);

                    HashMap<Object, Object> map_current_user = Utils_Map_FriendsFollowerFollowing.setFriendsFollowerFollowingModel(user_id);
                    HashMap<Object, Object> map_other_user = Utils_Map_FriendsFollowerFollowing.setFriendsFollowerFollowingModel(user.getUser_id());

                    relLayout_invite_button.setOnClickListener(view -> {
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_invitation_friend_following))
                                .child(user_id)
                                .child(user.getUser_id())
                                .setValue(map_other_user);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_invitation_friend_follower))
                                .child(user.getUser_id())
                                .child(user_id)
                                .setValue(map_current_user);
                        setFriendFollowing();

                        // add badge number
                        String key = myRef.push().getKey();
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", user.getUser_id());

                        assert key != null;
                        myRef.child(context.getString(R.string.dbname_badge_friend_confirmation_number))
                                .child(user.getUser_id())
                                .child(key)
                                .setValue(map_number);

                        // for the global
                        myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                .child(user.getUser_id())
                                .child(key)
                                .setValue(map_number);
                    });

                    relLayout_cancel_invite_button.setOnClickListener(view -> {
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_invitation_friend_following))
                                .child(user_id)
                                .child(user.getUser_id())
                                .removeValue();

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_invitation_friend_follower))
                                .child(user.getUser_id())
                                .child(user_id)
                                .removeValue();
                        setFriendUnfollowing();
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
        if (finalUserList != null) {
            finalUserList.clear();
        }
        if (group_id_list != null) {
            group_id_list.clear();
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

        if(adapter != null){
            adapter = null;
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
                    // liste des utilisateurs auxquels je ne suis pas abonné
                    tamponList.add(finalUserList.get(i));
                }
            }
        } else {
            tamponList.addAll(finalUserList);
        }

        // remove the delete suggestions and this user his suggestion
        for (int i = 0; i < tamponList.size(); i++) {
            if (!removed_suggestions_list.contains(tamponList.get(i).getUser_id())
                    && !tamponList.get(i).getUser_id().equals(user.getUser_id()))
                finalList.add(tamponList.get(i));
        }

        adapter = new SuggestionProfileAdapter(context, finalList, relLayout_view_overlay);
        recyclerview_suggestions.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            relLayout_suggestions.setVisibility(View.GONE);
        }
    }

    // end of suggestions list _____________________________________________________________________
    private void follow_unfollow(User user) {
        // follow unfollow person ______________________________________________________________
        // public account
        if (user.getScope().equals(context.getString(R.string.title_public))) {
            isFollowing_public_account(user);

            HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
            HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

            button_follow.setOnClickListener(view -> {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_following))
                        .child(user_id)
                        .child(user.getUser_id())
                        .setValue(map_other_user);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_followers))
                        .child(user.getUser_id())
                        .child(user_id)
                        .setValue(map_current_user);
                setFollowing_public_account();
            });

            button_unfollow.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_following))
                        .child(user_id)
                        .child(user.getUser_id())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_followers))
                        .child(user.getUser_id())
                        .child(user_id)
                        .removeValue();
                setUnfollowing_public_account();
            });
        }
        // private account
        if (user.getScope().equals(context.getString(R.string.title_private))) {
            // verify if user is already following
            Query query1 = myRef.child(context.getString(R.string.dbname_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUser_id());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+ds.getValue());

                        isFollowing_public_account(user);

                        button_follow.setOnClickListener(view -> {
                            HashMap<Object, Object> map_current_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user_id);
                            HashMap<Object, Object> map_other_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user.getUser_id());

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .setValue(map_other_user);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .setValue(map_current_user);
                            setFollowing_subscription_request();

                            // add badge number
                            String key = myRef.push().getKey();
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", user.getUser_id());

                            assert key != null;
                            myRef.child(context.getString(R.string.dbname_badge_follow_approbation_number))
                                    .child(user.getUser_id())
                                    .child(key)
                                    .setValue(map_number);

                            // for the global
                            myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                    .child(user.getUser_id())
                                    .child(key)
                                    .setValue(map_number);
                        });

                        button_unfollow.setOnClickListener(v -> {
                            // show dialog box
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                            TextView dialog_title = view.findViewById(R.id.dialog_title);
                            TextView dialog_text = view.findViewById(R.id.dialog_text);
                            TextView negativeButton = view.findViewById(R.id.tv_no);
                            TextView positiveButton = view.findViewById(R.id.tv_yes);
                            builder.setView(view);

                            Dialog d = builder.create();
                            d.show();

                            negativeButton.setText(context.getString(R.string.cancel));
                            positiveButton.setText(context.getString(R.string.unsubscribe));

                            dialog_title.setVisibility(View.GONE);
                            dialog_text.setText(Html.fromHtml(context.getString(R.string.would_you_like_to_stop_following)+" "
                                    +user.getUsername()+" ?"));

                            negativeButton.setOnClickListener(view2 -> d.dismiss());

                            positiveButton.setOnClickListener(view1 -> {
                                d.dismiss();
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_following))
                                        .child(user_id)
                                        .child(user.getUser_id())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_followers))
                                        .child(user.getUser_id())
                                        .child(user_id)
                                        .removeValue();
                                setUnfollowing_subscription_request();
                            });
                        });
                    }

                    if (!snapshot.exists()) {
                        isFollowing_subscription_request(user);

                        HashMap<Object, Object> map_current_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user_id);
                        HashMap<Object, Object> map_other_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user.getUser_id());

                        button_follow.setOnClickListener(view -> {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .setValue(map_other_user);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .setValue(map_current_user);
                            setFollowing_subscription_request();

                            // add badge number
                            String key = myRef.push().getKey();
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", user.getUser_id());

                            assert key != null;
                            myRef.child(context.getString(R.string.dbname_badge_follow_approbation_number))
                                    .child(user.getUser_id())
                                    .child(key)
                                    .setValue(map_number);

                            // for the global
                            myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                    .child(user.getUser_id())
                                    .child(key)
                                    .setValue(map_number);
                        });

                        button_sent.setOnClickListener(v -> {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .removeValue();
                            setUnfollowing_subscription_request();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // follow unfollow person ______________________________________________________________________
    // public
    private void setFollowing_public_account(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_follow.setVisibility(View.GONE);
        button_unfollow.setVisibility(View.VISIBLE);
        button_sent.setVisibility(View.GONE);
    }

    private void setUnfollowing_public_account(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_follow.setVisibility(View.VISIBLE);
        button_unfollow.setVisibility(View.GONE);
        button_sent.setVisibility(View.GONE);
    }

    private void isFollowing_public_account(User user){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing_public_account();
                }

                if (!snapshot.exists()) {
                    setUnfollowing_public_account();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // private
    private void setFollowing_subscription_request(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_follow.setVisibility(View.GONE);
        button_unfollow.setVisibility(View.GONE);
        button_sent.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing_subscription_request(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_follow.setVisibility(View.VISIBLE);
        button_unfollow.setVisibility(View.GONE);
        button_sent.setVisibility(View.GONE);
    }

    private void isFollowing_subscription_request(User user){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_subscription_request_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing_subscription_request();
                }

                if (!snapshot.exists()) {
                    setUnfollowing_subscription_request();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // friendship __________________________________________________________________________________
    private void setFriendFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_invite_button.setVisibility(View.GONE);
        relLayout_cancel_invite_button.setVisibility(View.VISIBLE);
    }

    private void setFriendUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        relLayout_invite_button.setVisibility(View.VISIBLE);
        relLayout_cancel_invite_button.setVisibility(View.GONE);
    }

    private void isFriendFollowing(User user){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_invitation_friend_following))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFriendFollowing();
                }

                if (!snapshot.exists()) {
                    setFriendUnfollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersCount(User user){
        Query query = myRef.child(getString(R.string.dbname_followers))
                .child(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower:" + ds.getValue());
                    String userID = ds.getKey();
                    followers_userIDList.add(userID);
                }

                // to remove suggestion when they are already friend
                Query query1 = myRef
                        .child(context.getString(R.string.dbname_friend_following))
                        .child(user.getUser_id());
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String keyID = ds.getKey();
                            friends_followers_userIDList.add(keyID);
                        }

                        if (!followers_userIDList.isEmpty() && !friends_followers_userIDList.isEmpty()) {
                            for (String friendID: friends_followers_userIDList) {
                                for (int i = 0; i < followers_userIDList.size(); i++) {
                                    if (!followers_userIDList.get(i).equals(friendID))
                                        if (!final_followers_userIDList.contains(followers_userIDList.get(i)))
                                            final_followers_userIDList.add(followers_userIDList.get(i));
                                }
                            }
                        } else if (!followers_userIDList.isEmpty()) {
                            final_followers_userIDList.addAll(followers_userIDList);

                        } else if (!friends_followers_userIDList.isEmpty()) {
                            final_followers_userIDList.addAll(followers_userIDList);
                        }

                        for (int i = 0; i < final_followers_userIDList.size(); i++) {
                            final int count = i;
                            Query query2 = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(final_followers_userIDList.get(i));
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dts: snapshot.getChildren()) {

                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dts.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, dts);

                                        followers_userList.add(user);
                                    }

                                    if(count >= final_followers_userIDList.size() -1){
                                        tvFollowers.setText(String.valueOf(followers_userList.size()));
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount(User user){
        Query query = myRef.child(getString(R.string.dbname_following))
                .child(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + ds.getValue());
                    String userID = ds.getKey();
                    following_userID_List.add(userID);
                }

                // to remove suggestion when they are already friend
                Query query1 = myRef
                        .child(context.getString(R.string.dbname_friend_following))
                        .child(user.getUser_id());
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String keyID = ds.getKey();
                            friends_following_userIDList.add(keyID);
                        }

                        if (!following_userID_List.isEmpty() && !friends_following_userIDList.isEmpty()) {
                            for (String friendID: friends_following_userIDList) {
                                for (int i = 0; i < following_userID_List.size(); i++) {
                                    if (!following_userID_List.get(i).equals(friendID))
                                        if (!final_following_userIDList.contains(following_userID_List.get(i)))
                                            final_following_userIDList.add(following_userID_List.get(i));
                                }
                            }
                        } else if (!following_userID_List.isEmpty()) {
                            final_following_userIDList.addAll(following_userID_List);

                        } else if (!friends_following_userIDList.isEmpty()) {
                            final_following_userIDList.addAll(following_userID_List);
                        }

                        for (int i = 0; i < final_following_userIDList.size(); i++) {
                            final int count = i;
                            Query query2 = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(final_following_userIDList.get(i));
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dts: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dts.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, dts);

                                        following_userList.add(user);
                                    }
                                    if(count >= final_following_userIDList.size() -1){
                                        tvFollowing.setText(String.valueOf(following_userList.size()));
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostsCount(User user){
        mPostsCount = 0;

        Query query = myRef.child(getString(R.string.dbname_battle))
                .child(user.getUser_id());
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
                    mPosts.setText(tv_count);

                } else {
                    mPosts.setText(String.valueOf((int) mPostsCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostsFunCount(User user){
        mPostsCount_fun = 0;

        Query query = myRef.child(getString(R.string.dbname_fun))
                .child(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    mPostsCount_fun++;
                }

                double count;
                if (mPostsCount_fun >= 1000) {
                    count = mPostsCount_fun/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    mPostsFun.setText(tv_count);

                } else {
                    mPostsFun.setText(String.valueOf((int) mPostsCount_fun));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(User user) {
        // verify if account is private _______________________________________
        if (user.getScope().equals(context.getString(R.string.title_private))) {
            relLayout_visit_profil.setVisibility(View.GONE);
            // verify is users are friends
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_friend_follower))
                    .child(user.getUser_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());

                        // users are  friend
                        relLayout_visit_profil.setVisibility(View.VISIBLE);
                        linLayout_already_friend.setVisibility(View.VISIBLE);
                        // users are not yet friend
                        linLayout_not_yet_friend.setVisibility(View.GONE);

                        // show details
                        linLayout_details.setVisibility(View.VISIBLE);
                        linLayout_friends.setVisibility(View.VISIBLE);

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

                    if (!snapshot.exists()) {
                        // verify if user is follower
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_followers))
                                .child(user.getUser_id())
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(user_id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
                                    // users are not yet friend
                                    linLayout_not_yet_friend.setVisibility(View.VISIBLE);

                                    // show details
                                    linLayout_details.setVisibility(View.VISIBLE);
                                    linLayout_friends.setVisibility(View.VISIBLE);
                                    relLayout_visit_profil.setVisibility(View.VISIBLE);

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

                                if (!snapshot.exists()) {
                                    relLayoutAbonne.setOnClickListener(view -> {});
                                    relLayoutAbonnement.setOnClickListener(view -> {});

                                    // show details
                                    linLayout_details.setVisibility(View.GONE);
                                    linLayout_friends.setVisibility(View.GONE);

                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
                                    relLayout_visit_profil.setVisibility(View.GONE);
                                    // users are not yet friend
                                    linLayout_not_yet_friend.setVisibility(View.VISIBLE);
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

        } else {
            // verify is users are friends
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_friend_follower))
                    .child(user.getUser_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());

                        // users are  friend
                        linLayout_already_friend.setVisibility(View.VISIBLE);
                        // users are not yet friend
                        linLayout_not_yet_friend.setVisibility(View.GONE);

                        // show details
                        linLayout_details.setVisibility(View.VISIBLE);
                        linLayout_friends.setVisibility(View.VISIBLE);

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

                    if (!snapshot.exists()) {
                        // verify if user is follower
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_followers))
                                .child(user.getUser_id())
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(user_id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    String ID = dataSnapshot.getKey();

                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
                                    // users are not yet friend
                                    linLayout_not_yet_friend.setVisibility(View.VISIBLE);

                                    // show details
                                    linLayout_details.setVisibility(View.VISIBLE);
                                    linLayout_friends.setVisibility(View.VISIBLE);

                                    assert ID != null;
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

                                if (!snapshot.exists()) {
                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
                                    // users are not yet friend
                                    linLayout_not_yet_friend.setVisibility(View.VISIBLE);
                                    linLayout_details.setVisibility(View.VISIBLE);
                                    linLayout_friends.setVisibility(View.VISIBLE);

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
    }

    private void configureViewPagerAndTabs() {
        TabLayout tabLayout_profile = findViewById(R.id.tabLayout);
        ViewPager2 viewPager_main = findViewById(R.id.viewPager);
        viewPager_main.setAdapter(new ViewProfileFragmentAdapter(context));
        viewPager_main.setFocusable(true);
        viewPager_main.requestFocus();

        if (from_fun != null) {
            viewPager_main.setCurrentItem(1);
            mPosts.setVisibility(View.GONE);
            mPostsFun.setVisibility(View.VISIBLE);
        }

        String[] titles = {"Battle", "Videos"};

        new TabLayoutMediator(tabLayout_profile, viewPager_main,
                (tabLayout, position) -> tabLayout.setText(titles[position])).attach();

        tabLayout_profile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    mPosts.setVisibility(View.GONE);
                    mPostsFun.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    mPostsFun.setVisibility(View.GONE);
                    mPosts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setProfileWidgets() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    String user_bio = user.getBio();
                    if (TextUtils.isEmpty(user_bio))
                        bio.setVisibility(View.GONE);
                    bio.setText(user_bio);

                    username.setText(user.getUsername());
                    fullName.setText(user.getFullName());
                    town_name.setText(Html.fromHtml(context.getString(R.string.live_in)+" <b>"+user.getTown_name()+"</b>"));
                    String status = user.getMarital_status();
                    if (TextUtils.isEmpty(status)) {
                        relLayout_marital_status.setVisibility(View.GONE);
                    }
                    marital_status.setText(status);

                    relLayout_about_me.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, SeeSectionAboutMe.class);
                        intent.putExtra("userID", user.getUser_id());
                        startActivity(intent);
                    });

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

                    user_profile_photo.setOnClickListener(view -> {
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

                    // unfriend
                    relLayout_unfriend.setOnClickListener(view -> {
                        BottomSheetManegeFriend bottomSheetManegeFriend = new BottomSheetManegeFriend(context, user.getUser_id(),
                                linLayout_not_yet_friend, linLayout_already_friend, null, null);
                        if (bottomSheetManegeFriend.isAdded())
                            return;
                        bottomSheetManegeFriend.show(context.getSupportFragmentManager(), "TAG");
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

        internetIsConnected();

        // set the current user profile visit
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User current_user = Util_User.getUser(context, objectMap, dataSnapshot);

                    VisitProfileManager visitProfileManager = new VisitProfileManager(context);
                    visitProfileManager.incrementViewCount(user, current_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}