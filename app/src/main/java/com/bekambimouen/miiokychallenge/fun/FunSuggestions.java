package com.bekambimouen.miiokychallenge.fun;

import static com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFun.preDownloadVideo;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.fun.adapter.SuggestionsFunAdapter;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.fun.publication.CameraVideoFun;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFun;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel_fun;
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

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FunSuggestions extends AppCompatActivity implements CacheListener {
    private static final String TAG = "FunSuggestions";
    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private Container recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private FunSuggestions context;
    private SuggestionsFunAdapter adapter;
    private ArrayList<BattleModel_fun> list, other_part_list, list_of_videos;
    public ArrayList<String> user_videos_id_following_list, filter_videos_photo_id_list;
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
    private List<String> friend_user_friends_list;

    private ArrayList<BattleModel_fun> myList;
    private BattleModel_fun myVideo, from_notification_comment;
    private Handler handler;
    private Runnable preDownloadRunnable;
    private int position;
    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;
    private String user_id;


    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_fun_suggestions);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            myVideo = gson.fromJson(getIntent().getStringExtra("myVideo"), BattleModel_fun.class);
            position = getIntent().getIntExtra("position", 0);
            from_notification_comment = gson.fromJson(getIntent().getStringExtra("from_notification_comment"), BattleModel_fun.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init();
        if (from_notification_comment != null) {
            getAllOtherVideos();
        } else {
            new Thread(this::users_from_the_same_establishment_or_same_town).start();
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
        progressBar = findViewById(R.id.progressBar);
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout backArrow = findViewById(R.id.backArrow);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        recyclerView = findViewById(R.id.container);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(recyclerView);

        relLayout_publish.setOnClickListener(view -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 102;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CameraVideoFun.class);
                context.startActivity(intent);
            }
        });

        backArrow.setOnClickListener(view -> {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent i = new Intent(context, CameraVideoFun.class);
            startActivity(i);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
        if(list_of_videos != null){
            list_of_videos.clear();
        }
        if(user_videos_id_following_list != null){
            user_videos_id_following_list.clear();
        }
        if(filter_videos_photo_id_list != null){
            filter_videos_photo_id_list.clear();
        }
        if(admin_master_list != null){
            admin_master_list.clear();
        }
        if(myList != null){
            myList.clear();
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
        list_of_videos = new ArrayList<>();
        user_videos_id_following_list = new ArrayList<>();
        filter_videos_photo_id_list = new ArrayList<>();
        admin_master_list = new ArrayList<>();
        myList = new ArrayList<>();
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
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        User user = null;
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            user = Util_User.getUser(context, objectMap, ds);

                            // get all the user to get common friends
                            // remove private count
                            if (user.getScope().equals(context.getString(R.string.title_public)))
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
                                        if (user.getScope().equals(context.getString(R.string.title_public))) {
                                            users_in_same_town_list.add(user);
                                            total_user_id_list.add(user.getUser_id());
                                        }
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
                            }
                        }

                        // verify if we are in the same school
                        for (int j = 0; j < current_user_schools_list.size(); j++) {

                            for (int i = 0; i < schools_list.size(); i++) {
                                if (schools_list.get(i).getUser_school_attended().equals(current_user_schools_list.get(j).getUser_school_attended())) {
                                    if (!total_user_id_list.contains(schools_list.get(i).getUser_id())) {
                                        // remove private count
                                        assert user != null;
                                        if (user.getScope().equals(context.getString(R.string.title_public))) {
                                            users_in_same_school_list.add(user);
                                            total_user_id_list.add(schools_list.get(i).getUser_id());
                                        }
                                    }
                                }
                            }
                        }

                        // get the groups created by user in same schools
                        getGroupsCreatedByUsersInTheSameSchool(user);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    // get the groups created by the in the same school
    private void getGroupsCreatedByUsersInTheSameSchool(User user) {
        if (!users_in_same_school_list.isEmpty()) {
            for (int i = 0; i < users_in_same_school_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_videos))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_school_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                            // get all groups
                            if (!filter_videos_photo_id_list.contains(model.getPhoto_id())) {
                                if (!model.isSuppressed())
                                    list.add(model);
                                filter_videos_photo_id_list.add(model.getPhoto_id());
                            }
                        }

                        if (count >= users_in_same_school_list.size() - 1) {
                            // verify if we are in the same establishment
                            for (int j = 0; j < current_user_establishments_list.size(); j++) {

                                for (int i = 0; i < establishments_list.size(); i++) {
                                    if (establishments_list.get(i).getUser_establishment().equals(current_user_establishments_list.get(j).getUser_establishment())) {
                                        if (!total_user_id_list.contains(establishments_list.get(i).getUser_id())) {
                                            // remove private count
                                            if (user.getScope().equals(context.getString(R.string.title_public))) {
                                                users_in_same_estabishments_list.add(user);
                                                total_user_id_list.add(establishments_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }
                            }

                            // get the groups created by user in same establishments
                            getGroupsCreatedByUsersInTheSameEstablishments(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same establishments
            getGroupsCreatedByUsersInTheSameEstablishments(user);
        }
    }

    // get the groups created by the in the same school
    private void getGroupsCreatedByUsersInTheSameEstablishments(User user) {
        if (!users_in_same_estabishments_list.isEmpty()) {
            for (int i = 0; i < users_in_same_estabishments_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_videos))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_estabishments_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                            // get all groups
                            if (!filter_videos_photo_id_list.contains(model.getPhoto_id())) {
                                if (!model.isSuppressed())
                                    list.add(model);
                                filter_videos_photo_id_list.add(model.getPhoto_id());
                            }
                        }

                        if (count >= users_in_same_estabishments_list.size() - 1) {
                            // verify if we are in the same workplace
                            for (int j = 0; j < current_user_workplaces_list.size(); j++) {

                                for (int i = 0; i < workplaces_list.size(); i++) {
                                    if (workplaces_list.get(i).getUser_work_at().equals(current_user_workplaces_list.get(j).getUser_work_at())) {
                                        if (!total_user_id_list.contains(workplaces_list.get(i).getUser_id())) {
                                            // remove private count
                                            if (user.getScope().equals(context.getString(R.string.title_public))) {
                                                users_in_same_workplace_list.add(user);
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
                    public void onCancelled(@NonNull  DatabaseError error) {

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
                        .child(context.getString(R.string.dbname_videos))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_workplace_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                            // get all groups
                            if (!filter_videos_photo_id_list.contains(model.getPhoto_id())) {
                                if (!model.isSuppressed())
                                    list.add(model);
                                filter_videos_photo_id_list.add(model.getPhoto_id());
                            }
                        }

                        if (count >= users_in_same_workplace_list.size() - 1) {
                            // get common friends
                            getGroupsCreatedByUsersInTheSameTown();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

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
                        .child(context.getString(R.string.dbname_videos))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_town_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                            // get all groups
                            if (!filter_videos_photo_id_list.contains(model.getPhoto_id())) {
                                if (!model.isSuppressed())
                                    list.add(model);
                                filter_videos_photo_id_list.add(model.getPhoto_id());
                            }
                        }

                        if (count >= users_in_same_town_list.size() - 1) {
                            // get common friends
                            getCommonFriends(all_user_list);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

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
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String keyID = dataSnapshot.getKey();

                    current_user_friends_list.add(keyID);
                }

                // get the friend user friend list
                for (int i = 0; i < all_user_list.size(); i++) {
                    final int count = i;
                    Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                            .child(all_user_list.get(i).getUser_id());

                    int finalI = i;
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    // get groups created by common friends
    private void getGroupsCreatedByCommonFriends() {
        if (!common_friends_list.isEmpty()) {
            for (int i = 0; i < common_friends_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_videos))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(common_friends_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                            // get all groups
                            if (!filter_videos_photo_id_list.contains(model.getPhoto_id())) {
                                if (!model.isSuppressed())
                                    list.add(model);
                                filter_videos_photo_id_list.add(model.getPhoto_id());
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the list
                            getAllOtherVideos();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

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
                    .child(context.getString(R.string.dbname_videos))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(from_notification_comment.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                        if (model.getPhoto_id().equals(from_notification_comment.getPhoto_id()))
                            if (!model.isSuppressed())
                                other_part_list.add(model);
                    }

                    // get the list
                    displayTheList();
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });

        } else {
            for(int i = 0; i < all_user_list.size(); i++){
                final int count = i;
                Query query = myRef
                        .child(context.getString(R.string.dbname_videos))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(all_user_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, dataSnapshot);

                            // get all groups
                            if (!filter_videos_photo_id_list.contains(model.getPhoto_id())) {
                                if (!model.isSuppressed())
                                    other_part_list.add(model);
                                filter_videos_photo_id_list.add(model.getPhoto_id());
                            }
                        }

                        if (count >= all_user_list.size() - 1) {
                            // get the list
                            displayTheList();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
            }
        }
    }

    private void displayTheList() {
        if (from_notification_comment != null) {
            myList.addAll(other_part_list);

        } else {
            list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                    .compareTo(String.valueOf(o1.getDate_created())));
            list_of_videos.addAll(list);

            other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                    .compareTo(String.valueOf(o1.getDate_created())));
            list_of_videos.addAll(other_part_list);

            if (position == 0) {
                myList.addAll(list_of_videos);

            } else {
                for (int i = 0; i < list_of_videos.size(); i++) {
                    if (i > position) {
                        myList.add(list_of_videos.get(i));
                    }
                }

                if (myVideo != null) {
                    myList.add(0, myVideo);
                }
            }
        }

        adapter = new SuggestionsFunAdapter(this, recyclerView, progressBar, relLayout_view_overlay);
        adapter.setDefaultRecyclerView(context, R.id.container);

        if (myList.size() >= 2) {
            // first download the second video _____________________________________________________
            BattleModel_fun nextVideoItem = myList.get(1);

            // Create a PreloadListener
            PrepareNextVideoFun.PreloadListener preloadListener = new PrepareNextVideoFun.PreloadListener() {
                @Override
                public void onPreloadProgress(BattleModel_fun videoItem, int progress) {
                    // Handle progress updates (e.g., update a progress bar)
                    Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                }

                @Override
                public void onPreloadComplete(BattleModel_fun videoItem) {
                    // Handle completion (e.g., update UI)
                    Log.d("Preload", "Complete for " + videoItem.getUrl());
                }

                @Override
                public void onPreloadError(BattleModel_fun videoItem, Exception e) {
                    // Handle errors (e.g., show an error message)
                    Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                }
            };

            // Call preDownloadVideo with the listener
            preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
            // _____________________________________________________________________________________
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    // Remove any pending pre-download tasks
                    handler.removeCallbacks(preDownloadRunnable);

                    // Schedule a new pre-download task with a delay
                    preDownloadRunnable = () -> {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if (layoutManager != null) {
                            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                            // Pre-download the next two videos
                            for (int i = 1; i <= 3; i++) {
                                int nextVideoPosition = firstVisibleItemPosition + i;
                                if (nextVideoPosition < myList.size()) {
                                    BattleModel_fun nextVideoItem = myList.get(nextVideoPosition);

                                    // Create a PreloadListener
                                    PrepareNextVideoFun.PreloadListener preloadListener = new PrepareNextVideoFun.PreloadListener() {
                                        @Override
                                        public void onPreloadProgress(BattleModel_fun videoItem, int progress) {
                                            // Handle progress updates (e.g., update a progress bar)
                                            Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                                        }

                                        @Override
                                        public void onPreloadComplete(BattleModel_fun videoItem) {
                                            // Handle completion (e.g., update UI)
                                            Log.d("Preload", "Complete for " + videoItem.getUrl());
                                        }

                                        @Override
                                        public void onPreloadError(BattleModel_fun videoItem, Exception e) {
                                            // Handle errors (e.g., show an error message)
                                            Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                                        }
                                    };

                                    // Call preDownloadVideo with the listener
                                    preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
                                }
                            }
                        }
                    };
                    handler.postDelayed(preDownloadRunnable, 200);
                }
            });
        }

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


    public void onGetDate(List<BattleModel_fun> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        List<BattleModel_fun> models = new ArrayList<>();
        int start = page * counter - counter;
        int end = page * counter;
        for (int i = start; i < end; i++) {
            if (i < myList.size()) {
                models.add(myList.get(i));
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

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

    }
}