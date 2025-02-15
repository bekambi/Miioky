package com.bekambimouen.miiokychallenge.search;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.search.adapter.ViewExploreAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewExplore extends AppCompatActivity {
    private static final String TAG = "ViewExplore";

    // widgets
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private ViewExplore context;
    private ViewExploreAdapter adapter;
    public ArrayList<String> user_videos_id_following_list, filter_list;
    public ArrayList<String> admin_master_list;

    private ArrayList<User> all_user_list,
            users_in_same_school_list, users_in_same_estabishments_list, users_in_same_workplace_list,
            users_in_same_town_list, common_friends_list;
    private ArrayList<String> total_user_id_list;

    // suggestion about school and work place
    private String current_user_hometown, current_user_live_town;
    private ArrayList<SchoolAttended> current_user_schools_list;
    private ArrayList<FrequentedEstablishment> current_user_establishments_list;
    private ArrayList<WorkAt> current_user_workplaces_list;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;
    // suggestion about common friends
    private List<String> current_user_friends_list;
    private List<String> friend_user_friends_list;
    private ArrayList<BattleModel> list, other_part_list, all_list, final_list;

    private BattleModel from_notification_comment, battle_model;
    private int position;
    private Handler handler;

    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_view_explore);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            from_notification_comment = gson.fromJson(getIntent().getStringExtra("from_notification_comment"), BattleModel.class);
            battle_model = gson.fromJson(getIntent().getStringExtra("battle_model"), BattleModel.class);
            position = getIntent().getIntExtra("position", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "run: error: "+e.getMessage());
        }

        init();
        if (from_notification_comment != null) {
            getAllOtherVideos();
        } else {
            users_from_the_same_establishment_or_same_town();
        }
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
        progressBar = findViewById(R.id.progressBar);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

    private void clearAll(){
        if(users_in_same_school_list != null){
            users_in_same_school_list.clear();
        }
        if(users_in_same_estabishments_list != null){
            users_in_same_estabishments_list.clear();
        }
        if(users_in_same_workplace_list != null){
            users_in_same_workplace_list.clear();
        }
        if(users_in_same_town_list != null){
            users_in_same_town_list.clear();
        }
        if(common_friends_list != null){
            common_friends_list.clear();
        }
        if(all_user_list != null){
            all_user_list.clear();
        }
        if(total_user_id_list != null){
            total_user_id_list.clear();
        }
        if(current_user_schools_list != null){
            current_user_schools_list.clear();
        }
        if(current_user_establishments_list != null){
            current_user_establishments_list.clear();
        }
        if(current_user_workplaces_list != null){
            current_user_workplaces_list.clear();
        }
        if(schools_list != null){
            schools_list.clear();
        }
        if(establishments_list != null){
            establishments_list.clear();
        }
        if(workplaces_list != null){
            workplaces_list.clear();
        }
        if(current_user_friends_list != null){
            current_user_friends_list.clear();
        }
        if(friend_user_friends_list != null){
            friend_user_friends_list.clear();
        }
        if(list != null){
            list.clear();
        }
        if(other_part_list != null){
            other_part_list.clear();
        }
        if(all_list != null){
            all_list.clear();
        }
        if(user_videos_id_following_list != null){
            user_videos_id_following_list.clear();
        }
        if(filter_list != null){
            filter_list.clear();
        }
        if(admin_master_list != null){
            admin_master_list.clear();
        }
        if(final_list != null){
            final_list.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        users_in_same_school_list = new ArrayList<>();
        users_in_same_estabishments_list = new ArrayList<>();
        users_in_same_workplace_list = new ArrayList<>();
        users_in_same_town_list = new ArrayList<>();
        common_friends_list = new ArrayList<>();
        all_user_list = new ArrayList<>();
        total_user_id_list = new ArrayList<>();

        // suggestion about school and work place
        current_user_schools_list = new ArrayList<>();
        current_user_establishments_list = new ArrayList<>();
        current_user_workplaces_list = new ArrayList<>();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
        current_user_friends_list = new ArrayList<>();
        friend_user_friends_list = new ArrayList<>();
        list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        all_list = new ArrayList<>();
        user_videos_id_following_list = new ArrayList<>();
        filter_list = new ArrayList<>();
        admin_master_list = new ArrayList<>();
        final_list = new ArrayList<>();
    }

    // get the current user school and work place
    private void users_from_the_same_establishment_or_same_town() {
        clearAll();
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

                            // remove private count
                            if (user.getScope().equals(context.getString(R.string.title_public))) {
                                all_user_list.add(user);

                                // get user's schools
                                if (!user.getUser_id().equals(user_id)) {

                                    // get user's hometown/livetown
                                    if (Normalizer.normalize(user.getHometown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(current_user_hometown) ||
                                            Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                    .equalsIgnoreCase(current_user_live_town))
                                        if (!total_user_id_list.contains(user.getUser_id())) {
                                            users_in_same_town_list.add(user);
                                            total_user_id_list.add(user.getUser_id());
                                        }

                                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()) {
                                        SchoolAttended school = new SchoolAttended();
                                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                        assert objectHashMap != null;
                                        school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                                        schools_list.add(school);
                                    }
                                    // get user's establishments
                                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
                                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                        assert objectHashMap != null;
                                        establishment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                                        establishments_list.add(establishment);
                                    }
                                    // get user's workplace
                                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()) {
                                        WorkAt workAt = new WorkAt();
                                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                        assert objectHashMap != null;
                                        workAt.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                                        workplaces_list.add(workAt);
                                    }

                                    // verify if we are in the same school
                                    for (int j = 0; j < current_user_schools_list.size(); j++) {
                                        for (int i = 0; i < schools_list.size(); i++) {
                                            if (schools_list.get(i).getUser_school_attended().equals(current_user_schools_list.get(j).getUser_school_attended())) {
                                                if (!total_user_id_list.contains(schools_list.get(i).getUser_id())) {
                                                    users_in_same_school_list.add(user);
                                                    total_user_id_list.add(schools_list.get(i).getUser_id());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // get the groups created by user in same schools
                        getGroupsCreatedByUsersInTheSameSchool();
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

    // get the groups created by the in the same school
    private void getGroupsCreatedByUsersInTheSameSchool() {
        if (!users_in_same_school_list.isEmpty()) {
            for (int i = 0; i < users_in_same_school_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_school_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_school_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }

                        if (count >= users_in_same_school_list.size() - 1) {
                            // verify if we are in the same establishment
                            for (int n = 0; n < all_user_list.size(); n++) {
                                for (int j = 0; j < current_user_establishments_list.size(); j++) {
                                    for (int i = 0; i < establishments_list.size(); i++) {
                                        if (establishments_list.get(i).getUser_establishment().equals(current_user_establishments_list.get(j).getUser_establishment())) {
                                            if (!total_user_id_list.contains(establishments_list.get(i).getUser_id())) {
                                                users_in_same_estabishments_list.add(all_user_list.get(n));
                                                total_user_id_list.add(establishments_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }
                            }

                            // get the groups created by user in same establishments
                            getGroupsCreatedByUsersInTheSameEstablishments();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same establishments
            getGroupsCreatedByUsersInTheSameEstablishments();
        }
    }

    // get the groups created by the in the same school
    private void getGroupsCreatedByUsersInTheSameEstablishments() {
        if (!users_in_same_estabishments_list.isEmpty()) {
            for (int i = 0; i < users_in_same_estabishments_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_estabishments_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_estabishments_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }

                        if (count >= users_in_same_estabishments_list.size() - 1) {
                            // verify if we are in the same workplace
                            for (int n = 0; n < all_user_list.size(); n++) {
                                for (int j = 0; j < current_user_workplaces_list.size(); j++) {

                                    for (int i = 0; i < workplaces_list.size(); i++) {
                                        if (workplaces_list.get(i).getUser_work_at().equals(current_user_workplaces_list.get(j).getUser_work_at())) {
                                            if (!total_user_id_list.contains(workplaces_list.get(i).getUser_id())) {
                                                users_in_same_workplace_list.add(all_user_list.get(n));
                                                total_user_id_list.add(workplaces_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }
                            }

                            // get the groups created by user in same workplaces
                            getGroupsCreatedByUsersInTheSameWorkPlaces();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same workplaces
            getGroupsCreatedByUsersInTheSameWorkPlaces();
        }
    }

    // get groups created by users in same workplaces
    private void getGroupsCreatedByUsersInTheSameWorkPlaces() {
        if (!users_in_same_workplace_list.isEmpty()) {
            for (int i = 0; i < users_in_same_workplace_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_workplace_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_workplace_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }

                        if (count >= users_in_same_workplace_list.size() - 1) {
                            // get common friends
                            getGroupsCreatedByUsersInTheSameTown();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common friends
            getGroupsCreatedByUsersInTheSameTown();
        }
    }

    // get the groups created by the in the same town
    private void getGroupsCreatedByUsersInTheSameTown() {
        if (!users_in_same_town_list.isEmpty()) {
            for (int i = 0; i < users_in_same_town_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_town_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_town_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }

                        if (count >= users_in_same_town_list.size() - 1) {
                            // get common friends
                            getCommonFriends(all_user_list);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common friends
            getCommonFriends(all_user_list);
        }
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

                    current_user_friends_list.add(keyID);
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

                            if (count >= all_user_list.size() - 1) {
                                // get common friends
                                for (String ID: current_user_friends_list) {
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

                                                                common_friends_list.add(user);
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
                                // Group created by common friends
                                getGroupsCreatedByCommonFriends();
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

    // get groups created by common friends
    private void getGroupsCreatedByCommonFriends() {
        if (!common_friends_list.isEmpty()) {
            for (int i = 0; i < common_friends_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(common_friends_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(common_friends_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the list
                            getAllOtherVideos();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the list
            getAllOtherVideos();
        }
    }

    // add other part of the groups
    private void getAllOtherVideos() {
        // when coming from notification
        if (from_notification_comment != null) {
            clearAll();

            Query query = myRef
                    .child(context.getString(R.string.dbname_battle))
                    .child(from_notification_comment.getUser_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(from_notification_comment.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                        // to see the post from the notification comment
                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) &&
                                model.getPhoto_id_un().equals(from_notification_comment.getPhoto_id_un()))
                            if ( !filter_list.contains(model.getReponse_photoID())) {
                                if (!model.isSuppressed())
                                    other_part_list.add(model);
                                filter_list.add(model.getReponse_photoID());
                            }
                        if (!TextUtils.isEmpty(model.getPhoto_id()) &&
                                model.getPhoto_id().equals(from_notification_comment.getPhoto_id()))
                            if ( !filter_list.contains(model.getReponse_photoID())) {
                                if (!model.isSuppressed())
                                    other_part_list.add(model);
                                filter_list.add(model.getReponse_photoID());
                            }
                        if (!TextUtils.isEmpty(model.getPhotoi_id()) &&
                                model.getPhotoi_id().equals(from_notification_comment.getPhotoi_id()))
                            if ( !filter_list.contains(model.getReponse_photoID())) {
                                if (!model.isSuppressed())
                                    other_part_list.add(model);
                                filter_list.add(model.getReponse_photoID());
                            }
                        if (!TextUtils.isEmpty(model.getReponse_photoID()) &&
                                model.getReponse_photoID().equals(from_notification_comment.getReponse_photoID()))
                            if ( !filter_list.contains(model.getReponse_photoID())) {
                                if (!model.isSuppressed())
                                    other_part_list.add(model);
                                filter_list.add(model.getReponse_photoID());
                            }
                    }

                    // get the list
                    displayTheList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            for(int i = 0; i < all_user_list.size(); i++){
                final int count = i;
                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(all_user_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(all_user_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        other_part_list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        other_part_list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        other_part_list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }

                        if (count >= all_user_list.size() - 1) {
                            // get the list
                            displayTheList();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void displayTheList() {
        if (from_notification_comment != null) {
            final_list.addAll(other_part_list);

        } else {

            //sort for newest to oldest
            list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                    .compareTo(String.valueOf(o1.getDate_created())));
            all_list.addAll(list);

            //sort for newest to oldest to other part of list
            other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                    .compareTo(String.valueOf(o1.getDate_created())));
            all_list.addAll(other_part_list);

            for (int i = 0; i < all_list.size(); i++) {
                if (i > position) {
                    final_list.add(all_list.get(i));
                }
            }

            final_list.add(0, battle_model);
        }

        adapter = new ViewExploreAdapter(context, progressBar, relLayout_view_overlay);
        adapter.setDefaultRecyclerView(context, R.id.recyclerView);

        adapter.setOnPaginationListener(new PaginatedAdapter.OnPaginationListener() {
            @Override
            public void onCurrentPage(int page) {
                //Toast.makeText(context, "Page " + page + " loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNextPage(int page) {
                getNewItems(page);
            }

            @Override
            public void onFinish() {

            }
        });

        getNewItems(adapter.getStartPage());
    }

    public void onGetDate(List<BattleModel> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        List<BattleModel> models = new ArrayList<>();
        int start = page * counter - counter;
        int end = page * counter;
        for (int i = start; i < end; i++) {
            if (i < final_list.size()) {
                models.add(final_list.get(i));
            }
        }
        onGetDate(models);
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