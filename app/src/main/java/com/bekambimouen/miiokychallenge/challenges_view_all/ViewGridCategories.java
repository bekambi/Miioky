package com.bekambimouen.miiokychallenge.challenges_view_all;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.ViewGridCategoriesAdapter;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
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

public class ViewGridCategories extends AppCompatActivity {
    private static final String TAG = "ViewGridCategories";

    // widgets
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private ViewGridCategories context;
    private ViewGridCategoriesAdapter adapter;
    public ArrayList<String> user_videos_id_following_list, test_videos_photo_id_list;
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
    // suggestion abut common friends
    private List<String> current_user_friends_list;
    private List<String> friend_user_friends_list, filter_challenge_photo_id_list;
    private ArrayList<ModelInvite> challengeList, other_part_list, list, final_list;


    private ModelInvite model_invite;
    private String category_status;
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
        // to disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_view_grid_categories);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            model_invite = gson.fromJson(getIntent().getStringExtra("model_invite"), ModelInvite.class);
            category_status = getIntent().getStringExtra("category_status");
            position = getIntent().getIntExtra("position", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init();
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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        TextView category = findViewById(R.id.category);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String Category = null;
        if (category_status != null) {
            if (category_status.equals(getString(R.string.love_choice)))
                Category = getString(R.string.love);
            else if (category_status.equals(getString(R.string.beauty_choice)))
                Category = getString(R.string.beauty);
            else if (category_status.equals(getString(R.string.acapella_choice)))
                Category = getString(R.string.acapella);
            else if (category_status.equals(getString(R.string.dance_choice)))
                Category = getString(R.string.dance);
            else if (category_status.equals(getString(R.string.comedy_choice)))
                Category = getString(R.string.comedy);
            else if (category_status.equals(getString(R.string.swag_choice)))
                Category = getString(R.string.swag);
            else if (category_status.equals(getString(R.string.decoration_choice)))
                Category = getString(R.string.decoration);
            else if (category_status.equals(getString(R.string.couple_choice)))
                Category = getString(R.string.couple);
            else if (category_status.equals(getString(R.string.cinema_choice)))
                Category = getString(R.string.cinema);
            else if (category_status.equals(getString(R.string.emotions_choice)))
                Category = getString(R.string.emotions);
            else if (category_status.equals(getString(R.string.art_choice)))
                Category = getString(R.string.art);
            else if (category_status.equals(getString(R.string.sport_choice)))
                Category = getString(R.string.sport);
            else if (category_status.equals(getString(R.string.games_choice)))
                Category = getString(R.string.games);
            else if (category_status.equals(getString(R.string.vehicle_choice)))
                Category = getString(R.string.vehicle);
            else if (category_status.equals(getString(R.string.accessories_choice)))
                Category = getString(R.string.accessories);
            else if (category_status.equals(getString(R.string.kitchen_choice)))
                Category = getString(R.string.kitchen);
            else if (category_status.equals(getString(R.string.interior_decoration_choice)))
                Category = getString(R.string.interior_decoration);
            else if (category_status.equals(getString(R.string.accommodation_choice)))
                Category = getString(R.string.accommodation);
            else if (category_status.equals(getString(R.string.restoration_choice)))
                Category = getString(R.string.restoration);
            else if (category_status.equals(getString(R.string.journey_choice)))
                Category = getString(R.string.journey);
            else if (category_status.equals(getString(R.string.animals_choice)))
                Category = getString(R.string.animals);
            else if (category_status.equals(getString(R.string.entertainment_choice)))
                Category = getString(R.string.entertainment);
            else if (category_status.equals(getString(R.string.event_choice)))
                Category = getString(R.string.event);

        } else
            Category = getString(R.string.watch);

        category.setText(Category);

        users_from_the_same_establishment_or_same_town();

        arrowBack.setOnClickListener(view -> finish());
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
        if (filter_challenge_photo_id_list != null) {
            filter_challenge_photo_id_list.clear();
        }
        if(user_videos_id_following_list != null){
            user_videos_id_following_list.clear();
        }
        if(test_videos_photo_id_list != null){
            test_videos_photo_id_list.clear();
        }
        if(admin_master_list != null){
            admin_master_list.clear();
        }
        if(other_part_list != null){
            other_part_list.clear();
        }
        if(list != null){
            list.clear();
        }
        if(final_list != null){
            final_list.clear();
        }
        if(challengeList != null){
            challengeList.clear();
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
        user_videos_id_following_list = new ArrayList<>();
        filter_challenge_photo_id_list = new ArrayList<>();
        test_videos_photo_id_list = new ArrayList<>();
        admin_master_list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        list = new ArrayList<>();
        final_list = new ArrayList<>();
        challengeList = new ArrayList<>();
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
                                        // remove private count
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
                                                // remove private count
                                                users_in_same_school_list.add(user);
                                                total_user_id_list.add(schools_list.get(i).getUser_id());
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
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .orderByChild(context.getString(R.string.field_invite_userID))
                        .equalTo(users_in_same_school_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            if (!model.isSuppressed()) {
                                if (!filter_challenge_photo_id_list.contains(model.getInvite_photoID())) {
                                    if (category_status != null) {
                                        if (category_status.equals(getString(R.string.love_choice))) {
                                            if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                                challengeList.add(model);
                                            }
                                        }

                                    } else {
                                        challengeList.add(model);
                                    }
                                    filter_challenge_photo_id_list.add(model.getInvite_photoID());
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
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .orderByChild(context.getString(R.string.field_invite_userID))
                        .equalTo(users_in_same_estabishments_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            if (!model.isSuppressed()) {
                                if (!filter_challenge_photo_id_list.contains(model.getInvite_photoID())) {
                                    if (category_status != null) {
                                        if (category_status.equals(getString(R.string.love_choice))) {
                                            if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                                challengeList.add(model);
                                            }
                                        }

                                    } else {
                                        challengeList.add(model);
                                    }
                                    filter_challenge_photo_id_list.add(model.getInvite_photoID());
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
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .orderByChild(context.getString(R.string.field_invite_userID))
                        .equalTo(users_in_same_workplace_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            if (!model.isSuppressed()) {
                                if (!filter_challenge_photo_id_list.contains(model.getInvite_photoID())) {
                                    if (category_status != null) {
                                        if (category_status.equals(getString(R.string.love_choice))) {
                                            if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                                challengeList.add(model);
                                            }
                                        }

                                    } else {
                                        challengeList.add(model);
                                    }
                                    filter_challenge_photo_id_list.add(model.getInvite_photoID());
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
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .orderByChild(context.getString(R.string.field_invite_userID))
                        .equalTo(users_in_same_town_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            if (!model.isSuppressed()) {
                                if (!filter_challenge_photo_id_list.contains(model.getInvite_photoID())) {
                                    if (category_status != null) {
                                        if (category_status.equals(getString(R.string.love_choice))) {
                                            if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                                challengeList.add(model);
                                            }
                                        }

                                    } else {
                                        challengeList.add(model);
                                    }
                                    filter_challenge_photo_id_list.add(model.getInvite_photoID());
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
                                    for (int i = 0; i < friend_user_friends_list.size(); i++) {
                                        if (ID.equals(friend_user_friends_list.get(i))) {

                                            if (!total_user_id_list.contains(friend_user_friends_list.get(i))) {
                                                Query query = myRef
                                                        .child(context.getString(R.string.dbname_users))
                                                        .orderByChild(context.getString(R.string.field_user_id))
                                                        .equalTo(friend_user_friends_list.get(i));

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
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .orderByChild(context.getString(R.string.field_invite_userID))
                        .equalTo(common_friends_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            if (!model.isSuppressed()) {
                                if (!filter_challenge_photo_id_list.contains(model.getInvite_photoID())) {
                                    if (category_status != null) {
                                        if (category_status.equals(getString(R.string.love_choice))) {
                                            if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                                challengeList.add(model);
                                            }
                                        } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                            if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                                challengeList.add(model);
                                            }
                                        }

                                    } else {
                                        challengeList.add(model);
                                    }
                                    filter_challenge_photo_id_list.add(model.getInvite_photoID());
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
        for(int i = 0; i < all_user_list.size(); i++){
            final int count = i;
            Query query = myRef
                    .child(context.getString(R.string.dbname_invite_battle_media));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                        if (!model.isSuppressed()) {
                            if (!filter_challenge_photo_id_list.contains(model.getInvite_photoID())) {
                                if (category_status != null) {
                                    if (category_status.equals(getString(R.string.love_choice))) {
                                        if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                            other_part_list.add(model);
                                        }
                                    } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                        if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                            other_part_list.add(model);
                                        }
                                    }

                                } else {
                                    other_part_list.add(model);
                                }
                                filter_challenge_photo_id_list.add(model.getInvite_photoID());
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

    private void displayTheList() {
        //sort for newest to oldest
        challengeList.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        list.addAll(challengeList);

        //sort for newest to oldest to other part of list
        other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        list.addAll(other_part_list);

        for (int i = 0; i < list.size(); i++) {
            if (i > position) {
                final_list.add(list.get(i));
            }
        }

        final_list.add(0, model_invite);

        adapter = new ViewGridCategoriesAdapter(context, progressBar, relLayout_view_overlay);
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

    public void onGetDate(List<ModelInvite> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        List<ModelInvite> models = new ArrayList<>();
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