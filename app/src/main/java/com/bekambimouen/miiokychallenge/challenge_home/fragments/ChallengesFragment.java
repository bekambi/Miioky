package com.bekambimouen.miiokychallenge.challenge_home.fragments;

import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.ViewMyChallenges;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.challenge_home.adapter.HomeChallengesAdapter;
import com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.MyChallenges;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.BackPressedListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.register.RegisterLogin;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.blongho.country_data.World;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BackPressedListener {
    private static final String TAG = "ChallengesFragment";

    // Camera Permissions
    private final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int REQUEST_CODE_CAMERA = 102;

    // creating object of BackPressedListener interface
    public static BackPressedListener backpressedlistener;

    // widgets
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_welcome, relLayout_board, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private HomeActivity context;
    private HomeChallengesAdapter adapter;
    private ArrayList<BattleModel> list, other_part_list, final_tampon_list, final_list;
    private ArrayList<String> admin_list, group_id_list, other_part_group_id_list, banned_member_list, tampon_list, filter_list;

    private ArrayList<User> all_user_list, other_part_all_user_list,
            users_in_same_school_list, users_in_same_estabishments_list, users_in_same_workplace_list,
            users_in_same_town_list, common_friends_list;
    public ArrayList<String> test_videos_photo_id_list, admin_master_list,
            filter_group_id_list, user_group_id_following_list, total_user_id_list;

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
    private List<String> friend_user_friends_list, response_id_list, removed_suggestions_list;

    private Dialog dialog;
    private Handler handler;
    private RelativeLayout relLayout_app_name;
    private String from_main_fragment;

    // pagination constants
    int counter = 10;
    private boolean isFirstLoaded = true;

    // firebase
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String user_id;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    public static ChallengesFragment newInstance() {
        ChallengesFragment fragment = new ChallengesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);
        context = (HomeActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            relLayout_app_name = context.getRelLayout_app_name();
            relLayout_view_overlay = context.getRelLayout_view_overlay();
            relLayout_welcome = context.getRelLayout_view_welcome();
            from_main_fragment = context.getFrom_main_fragment();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: error: "+e.getMessage());
        }

        setupFirebaseAuth();

        if (user != null) {
            // countries flags
            World.init(context);
            handler = new Handler(Looper.getMainLooper());

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            init(view);
        }

        // actualize page
        if (user != null) {
            relLayout_app_name.setOnClickListener(view1 -> {
                swipeRefreshLayout.setRefreshing(true);
                getPhoneContacts();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1500);
            });
        }

        return view;
    }

    private void init(View v) {
        progressBar = v.findViewById(R.id.progressBar);
        relLayout_board = v.findViewById(R.id.relLayout_board);
        relLayout_board.setVisibility(View.VISIBLE);
        RelativeLayout relLayout_challenges = v.findViewById(R.id.relLayout_challenges);
        RelativeLayout relLayout_create = v.findViewById(R.id.relLayout_create);
        RelativeLayout relLayout_discover = v.findViewById(R.id.relLayout_discover);
        LinearLayout linLayout_challenges = v.findViewById(R.id.linLayout_challenges);
        LinearLayout linLayout_create = v.findViewById(R.id.linLayout_create);
        LinearLayout linLayout_discover = v.findViewById(R.id.linLayout_discover);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // challenge click
        relLayout_challenges.setOnClickListener(view -> challengeClick());
        linLayout_challenges.setOnClickListener(view -> challengeClick());
        // create click
        relLayout_create.setOnClickListener(view -> createClick());
        linLayout_create.setOnClickListener(view -> createClick());
        // discover click
        relLayout_discover.setOnClickListener(view -> discoverClick());
        linLayout_discover.setOnClickListener(view -> discoverClick());

        if (from_main_fragment != null) {
            relLayout_welcome.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            relLayout_welcome.setVisibility(View.VISIBLE);
        }
    }

    // discover click
    private void discoverClick() {
        if (relLayout_view_overlay != null)
            relLayout_view_overlay.setVisibility(View.VISIBLE);
        startActivity(new Intent(new Intent(context, GridCategories.class)));
    }

    // challenge click
    private void createClick() {
        // check permission
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, context.getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();

        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.startActivity(new Intent(context, ViewMyChallenges.class));
        }
    }

    // challenge click
    private void challengeClick() {
        // check permission
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, context.getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();

        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.startActivity(new Intent(context, MyChallenges.class));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
        if(other_part_all_user_list != null){
            other_part_all_user_list.clear();
        }
        if(total_user_id_list != null){
            total_user_id_list.clear();
        }
        if (test_videos_photo_id_list != null) {
            test_videos_photo_id_list.clear();
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

        if (group_id_list != null) {
            group_id_list.clear();
        }
        if (other_part_group_id_list != null) {
            other_part_group_id_list.clear();
        }
        if (banned_member_list != null) {
            banned_member_list.clear();
        }
        if (admin_list != null) {
            admin_list.clear();
        }
        if (tampon_list != null) {
            tampon_list.clear();
        }
        if (filter_list != null) {
            filter_list.clear();
        }
        if(user_group_id_following_list != null){
            user_group_id_following_list.clear();
        }
        if(admin_master_list != null){
            admin_master_list.clear();
        }
        if(filter_group_id_list != null){
            filter_group_id_list.clear();
        }
        if(list != null){
            list.clear();
        }
        if(other_part_list != null){
            other_part_list.clear();
        }
        if(final_tampon_list != null){
            final_tampon_list.clear();
        }
        if(final_list != null){
            final_list.clear();
        }
        if(response_id_list != null){
            response_id_list.clear();
        }
        if(removed_suggestions_list != null){
            removed_suggestions_list.clear();
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
        other_part_all_user_list = new ArrayList<>();
        total_user_id_list = new ArrayList<>();
        test_videos_photo_id_list = new ArrayList<>();
        // suggestion about school and work place
        current_user_schools_list = new ArrayList<>();
        current_user_establishments_list = new ArrayList<>();
        current_user_workplaces_list = new ArrayList<>();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
        current_user_friends_list = new ArrayList<>();
        friend_user_friends_list = new ArrayList<>();

        user_group_id_following_list = new ArrayList<>();
        filter_group_id_list = new ArrayList<>();
        admin_master_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        other_part_group_id_list = new ArrayList<>();
        banned_member_list = new ArrayList<>();
        admin_list = new ArrayList<>();
        list = new ArrayList<>();
        filter_list = new ArrayList<>();
        tampon_list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        final_tampon_list = new ArrayList<>();
        final_list = new ArrayList<>();
        response_id_list = new ArrayList<>();
        removed_suggestions_list = new ArrayList<>();
    }

    // get the challenges created by my phone contacts
    private void getPhoneContacts() {
        clearAll();
        // to remove suggestion close by current user
        Query query = myRef.child(context.getString(R.string.dbname_remove_Suggestion))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    RemoveSuggestionModel suggestionModel = Util_RemoveSuggestionModel.getRemoveSuggestionModel(context, objectMap);
                    removed_suggestions_list.add(suggestionModel.getUser_id());
                }

                group_users_from_the_same_establishment_or_same_town();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the current user school and work place
    private void group_users_from_the_same_establishment_or_same_town() {
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

                            if (user.getScope().equals(context.getString(R.string.title_public))) {
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
                        group_getGroupsCreatedByUsersInTheSameSchool();
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
    private void group_getGroupsCreatedByUsersInTheSameSchool() {
        if (!users_in_same_school_list.isEmpty()) {
            for (int i = 0; i < users_in_same_school_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_school_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_school_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    group_id_list.add(user_groups.getGroup_id());
                                filter_group_id_list.add(user_groups.getGroup_id());
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
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_estabishments_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_estabishments_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    group_id_list.add(user_groups.getGroup_id());

                                filter_group_id_list.add(user_groups.getGroup_id());
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
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_workplace_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_workplace_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    group_id_list.add(user_groups.getGroup_id());
                                filter_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= users_in_same_workplace_list.size() - 1) {
                            // get common friends
                            group_getGroupsCreatedByUsersInTheSameTown();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common friends
            group_getGroupsCreatedByUsersInTheSameTown();
        }
    }

    // get the groups created by the in the same town
    private void group_getGroupsCreatedByUsersInTheSameTown() {
        if (!users_in_same_town_list.isEmpty()) {
            for (int i = 0; i < users_in_same_town_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_town_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_town_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    group_id_list.add(user_groups.getGroup_id());
                                filter_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= users_in_same_town_list.size() - 1) {
                            // get common friends
                            group_getCommonFriends(all_user_list);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common friends
            group_getCommonFriends(all_user_list);
        }
    }

    // get the common friends ___________________________________________________
    private void group_getCommonFriends(ArrayList<User> all_user_list) {
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
                                group_getGroupsCreatedByCommonFriends();
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
    private void group_getGroupsCreatedByCommonFriends() {
        if (!common_friends_list.isEmpty()) {
            for (int i = 0; i < common_friends_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(common_friends_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(common_friends_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    group_id_list.add(user_groups.getGroup_id());
                                filter_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the groups created by common knowledge
                            getChallengesGroupsCreatedByMyknowledge();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by common knowledge
            getChallengesGroupsCreatedByMyknowledge();
        }
    }

    // get the groups created by common knowledge
    private void getChallengesGroupsCreatedByMyknowledge() {
        if (!group_id_list.isEmpty()) {
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

                        // get the list of group where member has been banned
                        if (following.isBanFromGroup())
                            banned_member_list.add(following.getGroup_id());
                    }

                    if (!banned_member_list.isEmpty()) {
                        for (String group_banned_id: banned_member_list) {

                            for (int i = 0; i < group_id_list.size(); i++) {
                                if (!group_id_list.get(i).equals(group_banned_id)) {
                                    tampon_list.add(group_id_list.get(i));
                                }
                            }
                        }
                        group_id_list.clear();
                        group_id_list.addAll(tampon_list);
                    }
                    // find the posts ______________________________________________________________
                    for(int i = 0; i < group_id_list.size(); i++){
                        final int count = i;
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group))
                                .child(group_id_list.get(i)) // group_id
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(group_id_list.get(i));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                    if (!model.isSuppressed()) {
                                        if (!removed_suggestions_list.contains(model.getGroup_id())) {
                                            if (model.isGroup_imageDouble() || model.isGroup_videoDouble()
                                                    || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble()) {

                                                if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                                    list.add(model);
                                                    filter_list.add(model.getPhoto_id_un());
                                                }
                                                if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                                    list.add(model);
                                                    filter_list.add(model.getReponse_photoID());
                                                }
                                            }
                                        }
                                    }
                                }

                                if(count >= group_id_list.size() -1){
                                    // add the other part of list
                                    group_getGroupAdminMasterID();
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
            // add the other part of list
            group_getGroupAdminMasterID();
        }
    }

    // add the other part of list
    private void group_getGroupAdminMasterID() {
        Log.d(TAG, "getFollowing: searching for following");

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group));

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get all current user groups
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String admin_id = ds.getKey();

                    admin_list.add(admin_id);
                }

                getGroupIDList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupIDList() {
        if (!admin_list.isEmpty()) {
            for(int i = 0; i < admin_list.size(); i++){
                final int count = i;
                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(admin_list.get(i));

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    other_part_group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    other_part_group_id_list.add(user_groups.getGroup_id());
                                filter_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if(count >= admin_list.size() -1) {
                            if (!banned_member_list.isEmpty()) {
                                for (String group_banned_id: banned_member_list) {

                                    for (int i = 0; i < other_part_group_id_list.size(); i++) {
                                        if (!other_part_group_id_list.get(i).equals(group_banned_id)) {
                                            tampon_list.add(other_part_group_id_list.get(i));
                                        }
                                    }
                                }
                                other_part_group_id_list.clear();
                                other_part_group_id_list.addAll(tampon_list);
                            }

                            getOtherPartsGroupPosts();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            getOtherPartsGroupPosts();
        }
    }

    private void getOtherPartsGroupPosts() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        if (!other_part_group_id_list.isEmpty()) {
            for(int i = 0; i < other_part_group_id_list.size(); i++){
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group))
                        .child(other_part_group_id_list.get(i)) // group_id
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(other_part_group_id_list.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            if (!model.isSuppressed()) {
                                if (model.isGroup_imageDouble() || model.isGroup_videoDouble()
                                        || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble()) {

                                    if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                        other_part_list.add(model);
                                        filter_list.add(model.getPhoto_id_un());
                                    }
                                    if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                        other_part_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }
                                }
                            }
                        }

                        if(count >= other_part_group_id_list.size() -1){
                            users_from_the_same_establishment_or_same_town();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            users_from_the_same_establishment_or_same_town();
        }
    }
    // Miioky ______________________________________________________________________________________
    // get the current user school and work place
    private void users_from_the_same_establishment_or_same_town() {
        // list of all user
        Query query = myRef
                .child(context.getString(R.string.dbname_users));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    user = Util_User.getUser(context, objectMap, ds);

                    // get all the user to get common friends
                    // remove private count
                    if (user.getScope().equals(context.getString(R.string.title_public))) {
                        if (user.getScope().equals(context.getString(R.string.title_public)))
                            other_part_all_user_list.add(user);

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
                        }
                    }
                }
                // get the challenges created on Miioky by user in same schools
                getChallengesCreatedByUsersInTheSameSchool();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the challenges created on Miioky by user in same schools
    private void getChallengesCreatedByUsersInTheSameSchool() {
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
                            if (!model.isSuppressed()) {
                                if (!removed_suggestions_list.contains(model.getUser_id())) {
                                    if (model.isImageDouble() || model.isVideoDouble()
                                            || model.isReponseImageDouble() || model.isReponseVideoDouble()) {
                                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                            list.add(model);
                                            filter_list.add(model.getPhoto_id_un());
                                        }
                                        if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                            list.add(model);
                                            filter_list.add(model.getReponse_photoID());
                                        }
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_school_list.size() - 1) {
                            // get the groups created by user in same establishments
                            getChallengesCreatedByUsersInTheSameEstablishments();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same establishments
            getChallengesCreatedByUsersInTheSameEstablishments();
        }
    }

    // get the challenges created by the in the same school
    private void getChallengesCreatedByUsersInTheSameEstablishments() {
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
                            if (!model.isSuppressed()) {
                                if (!removed_suggestions_list.contains(model.getUser_id())) {
                                    if (model.isImageDouble() || model.isVideoDouble()
                                            || model.isReponseImageDouble() || model.isReponseVideoDouble()) {
                                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                            list.add(model);
                                            filter_list.add(model.getPhoto_id_un());
                                        }
                                        if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                            list.add(model);
                                            filter_list.add(model.getReponse_photoID());
                                        }
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_estabishments_list.size() - 1) {
                            // get the challenges created by user in same workplaces
                            getChallengesCreatedByUsersInTheSameWorkPlaces();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the challenges created by user in same workplaces
            getChallengesCreatedByUsersInTheSameWorkPlaces();
        }
    }

    // get the challenges created by user in same workplaces
    private void getChallengesCreatedByUsersInTheSameWorkPlaces() {
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
                            if (!model.isSuppressed()) {
                                if (!removed_suggestions_list.contains(model.getUser_id())) {
                                    if (model.isImageDouble() || model.isVideoDouble()
                                            || model.isReponseImageDouble() || model.isReponseVideoDouble()) {
                                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                            list.add(model);
                                            filter_list.add(model.getPhoto_id_un());
                                        }
                                        if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                            list.add(model);
                                            filter_list.add(model.getReponse_photoID());
                                        }
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_workplace_list.size() - 1) {
                            // get common town challenges friends
                            getChallengesCreatedByUsersInTheSameTown();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common town challenges friends
            getChallengesCreatedByUsersInTheSameTown();
        }
    }

    // get the groups created by the in the same town
    private void getChallengesCreatedByUsersInTheSameTown() {
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
                            if (!model.isSuppressed()) {
                                if (!removed_suggestions_list.contains(model.getUser_id())) {
                                    if (model.isImageDouble() || model.isVideoDouble()
                                            || model.isReponseImageDouble() || model.isReponseVideoDouble()) {
                                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                            list.add(model);
                                            filter_list.add(model.getPhoto_id_un());
                                        }
                                        if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                            list.add(model);
                                            filter_list.add(model.getReponse_photoID());
                                        }
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_town_list.size() - 1) {
                            // get challenges created by common friends
                            getChallengesCreatedByCommonFriends();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get challenges created by common friends
            getChallengesCreatedByCommonFriends();
        }
    }

    // get challenges created by common friends
    private void getChallengesCreatedByCommonFriends() {
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
                            if (!model.isSuppressed()) {
                                if (!removed_suggestions_list.contains(model.getUser_id())) {
                                    if (model.isImageDouble() || model.isVideoDouble()
                                            || model.isReponseImageDouble() || model.isReponseVideoDouble()) {
                                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                            list.add(model);
                                            filter_list.add(model.getPhoto_id_un());
                                        }
                                        if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                            list.add(model);
                                            filter_list.add(model.getReponse_photoID());
                                        }
                                    }
                                }
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the list
                            getAllOtherChallenges();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the other challenges
            getAllOtherChallenges();
        }
    }

    // add other part of the groups
    private void getAllOtherChallenges() {
        for(int i = 0; i < other_part_all_user_list.size(); i++){
            final int count = i;
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle))
                    .child(other_part_all_user_list.get(i).getUser_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(other_part_all_user_list.get(i).getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                        // get all posts
                        if (!model.isSuppressed()) {
                            if (!removed_suggestions_list.contains(model.getUser_id())) {
                                if (model.isImageDouble() || model.isVideoDouble()
                                        || model.isReponseImageDouble() || model.isReponseVideoDouble()) {
                                    if (!TextUtils.isEmpty(model.getPhoto_id_un()) && !filter_list.contains(model.getPhoto_id_un())) {
                                        other_part_list.add(model);
                                        filter_list.add(model.getPhoto_id_un());
                                    }
                                    if (!TextUtils.isEmpty(model.getReponse_photoID()) && !filter_list.contains(model.getReponse_photoID())) {
                                        other_part_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }
                                }
                            }
                        }
                    }

                    if (count >= other_part_all_user_list.size() - 1) {
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
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        final_tampon_list.addAll(list);

        //sort for newest to oldest to other part of list
        other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        final_tampon_list.addAll(other_part_list);

        for(int i = 0; i < final_tampon_list.size(); i++){
            if ((final_tampon_list.get(i).isReponseImageDouble() || final_tampon_list.get(i).isReponseVideoDouble())) {
                // to prevent duplicate of response challenge
                if (!response_id_list.contains(final_tampon_list.get(i).getReponse_photoID())) {
                    final_list.add(final_tampon_list.get(i));
                    response_id_list.add(final_tampon_list.get(i).getReponse_photoID());
                }
            }

            // remove response challenge here because it had already be added
            if (!(final_tampon_list.get(i).isReponseImageDouble() || final_tampon_list.get(i).isReponseVideoDouble()))
                final_list.add(final_tampon_list.get(i));
        }
        adapter = new HomeChallengesAdapter(context, relLayout_welcome, progressBar, relLayout_view_overlay);
        // to prevent crash when we return to the app
        try {
            adapter.setDefaultRecyclerView(context, R.id.recyclerView);
        } catch (NullPointerException e) {
            Log.d(TAG, "displayTheList: error: "+e.getMessage());
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

        // hide/show board
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dx > 0) {
                    System.out.println("Scrolled Right");
                } else if (dx < 0) {
                    System.out.println("Scrolled Left");
                } else {
                    System.out.println("No Horizontal Scrolled");
                }

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");

                    if (relLayout_board.getVisibility() == View.VISIBLE) {
                        relLayout_board.setVisibility(View.GONE);
                        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, relLayout_board.getHeight());
                        animate.setDuration(700);
                        relLayout_board.startAnimation(animate);
                    }

                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    if (relLayout_board.getVisibility() == View.GONE) {
                        // visibility of view
                        relLayout_board.setVisibility(View.VISIBLE);
                        TranslateAnimation animate = new TranslateAnimation(0, 0, relLayout_board.getHeight(), 0);

                        // duration of animation
                        animate.setDuration(700);
                        animate.setFillAfter(true);
                        relLayout_board.startAnimation(animate);
                    }

                } else {
                    System.out.println("No Vertical Scrolled");
                }
            }
        });
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
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getPhoneContacts();
    }

    @Override
    public void onBackPressed() {
        if (user != null) {
            if (adapter != null) {
                if (adapter.getCurrentPage() != 1) {
                    swipeRefreshLayout.setRefreshing(true);
                    getPhoneContacts();
                    new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1500);
                } else {
                    closeApp();
                    context.overridePendingTransition(0, 0);
                }
            } else {
                closeApp();
                context.overridePendingTransition(0, 0);
            }
        } else {
            context.finishAffinity();
        }
    }

    private void closeApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_get_out, null);
        TextView ok = view1.findViewById(R.id.ok);
        TextView cancel = view1.findViewById(R.id.cancel);

        builder.setView(view1);
        dialog = builder.create();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        requireNonNull(dialog.getWindow()).setBackgroundDrawable(inset);
        dialog.show();

        ok.setOnClickListener(view -> {
            dialog.dismiss();
            context.finishAffinity();
        });

        cancel.setOnClickListener(view -> dialog.dismiss());
    }

    /**
     * checks to see if the @param 'user' is logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
        if(user == null){
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, RegisterLogin.class);
            startActivity(intent);
            context.finish();
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

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!allPermissionsGranted()) {
            // Permissions not granted
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, context.getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user != null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
                relLayout_view_overlay.setVisibility(View.GONE);

            // passing context of fragment to backpressedlistener
            backpressedlistener=this;
            // make app name enable true
            relLayout_app_name.setEnabled(true);
            // to prevent data to upload to another fragment
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    handler.postDelayed(() -> {
                        // to prevent data to upload to another fragment
                        if (isFirstLoaded) {
                            isFirstLoaded = false;
                            if (user != null)
                                getPhoneContacts();
                        }
                    }, 500);
                }
            });
        }
    }

    @Override
    public void onPause() {
        // passing null value to backpressedlistener
        backpressedlistener=null;
        super.onPause();

        if (dialog != null)
            dialog.dismiss();

        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (user != null)
            if (mAuthListener != null)
                mAuth.removeAuthStateListener(mAuthListener);
    }
}