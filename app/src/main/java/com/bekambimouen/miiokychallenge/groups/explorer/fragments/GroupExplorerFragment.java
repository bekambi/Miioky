package com.bekambimouen.miiokychallenge.groups.explorer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.adapter.AddNestedScrollView;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.groups.explorer.GroupExplorer;
import com.bekambimouen.miiokychallenge.groups.explorer.adapter.GroupExplorerAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.auth.FirebaseAuth;
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
 * Use the {@link GroupExplorerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupExplorerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "GroupExplorerFragment";

    // widgets
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_view_overlay;

    // vars
    private GroupExplorer context;
    private GroupExplorerAdapter adapter;
    private ArrayList<BattleModel> final_list, final_other_part_list, total_list;
    private ArrayList<String> admin_list, list, other_part_list, banned_member_list, tampon_list,
            total_user_id_list, filter_group_id_list, removed_suggestions_list;

    private ArrayList<User> all_user_list,
            users_in_same_school_list, users_in_same_estabishments_list, users_in_same_workplace_list,
            users_in_same_town_list, common_friends_list;

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
    private Handler handler;
    private boolean isFirstLoaded = true;

    // pagination constants
    int counter = 10;

    private BattleModel from_notification_comment;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public GroupExplorerFragment() {
        // Required empty public constructor
    }

    public static GroupExplorerFragment newInstance() {
        GroupExplorerFragment fragment = new GroupExplorerFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_explorer, container, false);
        context = (GroupExplorer) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            from_notification_comment = context.getFrom_notification_comment();
            relLayout_view_overlay = context.getRelLayout_view_overlay();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: error: "+e.getMessage());
        }

        init(view);

        return view;
    }

    private void init(View v) {
        progressBar = v.findViewById(R.id.progressBar);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);

        recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        if (banned_member_list != null) {
            banned_member_list.clear();
        }
        if (admin_list != null) {
            admin_list.clear();
        }
        if (tampon_list != null) {
            tampon_list.clear();
        }
        if(filter_group_id_list != null){
            filter_group_id_list.clear();
        }
        if(removed_suggestions_list != null){
            removed_suggestions_list.clear();
        }
        if(list != null){
            list.clear();
        }
        if(other_part_list != null){
            other_part_list.clear();
        }
        if(final_list != null){
            final_list.clear();
        }
        if(final_other_part_list != null){
            final_other_part_list.clear();
        }
        if(total_list != null){
            total_list.clear();
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
        filter_group_id_list = new ArrayList<>();
        banned_member_list = new ArrayList<>();
        admin_list = new ArrayList<>();
        list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        final_list = new ArrayList<>();
        final_other_part_list = new ArrayList<>();
        tampon_list = new ArrayList<>();
        total_list = new ArrayList<>();
        removed_suggestions_list = new ArrayList<>();
    }

    // get the groups created by my phone contacts
    private void getPhoneContactsGroups() {
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

                users_from_the_same_establishment_or_same_town();
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
                                                total_user_id_list.add(user.getUser_id());
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
                            if (!user_groups.isSuppressed()) {
                                if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                    if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                        list.add(user_groups.getGroup_id());
                                    if (user_groups.isGroup_public())
                                        list.add(user_groups.getGroup_id());
                                    filter_group_id_list.add(user_groups.getGroup_id());
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
                                                total_user_id_list.add(all_user_list.get(n).getUser_id());
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
                            if (!user_groups.isSuppressed()) {
                                if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                    if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                        list.add(user_groups.getGroup_id());
                                    if (user_groups.isGroup_public())
                                        list.add(user_groups.getGroup_id());
                                    filter_group_id_list.add(user_groups.getGroup_id());
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
                                                total_user_id_list.add(all_user_list.get(n).getUser_id());
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
                            if (!user_groups.isSuppressed()) {
                                if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                    if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                        list.add(user_groups.getGroup_id());
                                    if (user_groups.isGroup_public())
                                        list.add(user_groups.getGroup_id());
                                    filter_group_id_list.add(user_groups.getGroup_id());
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
                            if (!user_groups.isSuppressed()) {
                                if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                    if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                        list.add(user_groups.getGroup_id());
                                    if (user_groups.isGroup_public())
                                        list.add(user_groups.getGroup_id());
                                    filter_group_id_list.add(user_groups.getGroup_id());
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
                            if (!user_groups.isSuppressed()) {
                                if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                    if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                        list.add(user_groups.getGroup_id());
                                    if (user_groups.isGroup_public())
                                        list.add(user_groups.getGroup_id());
                                    filter_group_id_list.add(user_groups.getGroup_id());
                                }
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the banned list
                            getTheFirstListGroupsWhereUserBanned();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // add the other part of list
            getTheFirstListGroupsWhereUserBanned();
        }
    }

    // get the first list where current user banned
    private void getTheFirstListGroupsWhereUserBanned() {
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

                        for (int i = 0; i < list.size(); i++) {
                            if (!list.get(i).equals(group_banned_id)) {
                                tampon_list.add(list.get(i));
                            }
                        }
                    }
                    list.clear();
                    list.addAll(tampon_list);
                    tampon_list.clear();
                }

                getFirstGroupPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the first group post
    private void getFirstGroupPosts() {
        if (!list.isEmpty()) {
            for(int i = 0; i < list.size(); i++){
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_media))
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(list.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            if (!removed_suggestions_list.contains(model.getGroup_id())) {
                                if (!model.isSuppressed())
                                    final_list.add(model);
                            }
                        }

                        if(count >= list.size() -1){
                            getGroupAdminMasterID();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            getGroupAdminMasterID();
        }
    }

    private void getGroupAdminMasterID() {
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

                            // get all groups
                            if (!user_groups.isSuppressed()) {
                                if (!filter_group_id_list.contains(user_groups.getGroup_id())) {
                                    if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                        other_part_list.add(user_groups.getGroup_id());
                                    if (user_groups.isGroup_public())
                                        other_part_list.add(user_groups.getGroup_id());
                                    filter_group_id_list.add(user_groups.getGroup_id());
                                }
                            }
                        }

                        if(count >= admin_list.size() -1) {
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

                                            for (int i = 0; i < other_part_list.size(); i++) {
                                                if (!other_part_list.get(i).equals(group_banned_id)) {
                                                    tampon_list.add(other_part_list.get(i));
                                                }
                                            }
                                        }
                                        other_part_list.clear();
                                        other_part_list.addAll(tampon_list);
                                    }

                                    getOtherGroupsPosts();
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
    }

    private void getOtherGroupsPosts() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        // when coming from notification
        if (from_notification_comment != null) {
            clearAll();

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_media))
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(from_notification_comment.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                        // to see the post from the notification comment
                        if (!TextUtils.isEmpty(model.getPhoto_id_un()) &&
                                model.getPhoto_id_un().equals(from_notification_comment.getPhoto_id_un())) {
                            final_other_part_list.add(model);
                        }
                        if (!TextUtils.isEmpty(model.getPhoto_id()) &&
                                model.getPhoto_id().equals(from_notification_comment.getPhoto_id())) {
                            final_other_part_list.add(model);
                        }
                        if (!TextUtils.isEmpty(model.getPhotoi_id()) &&
                                model.getPhotoi_id().equals(from_notification_comment.getPhotoi_id())) {
                            final_other_part_list.add(model);
                        }
                        if (!TextUtils.isEmpty(model.getReponse_photoID()) &&
                                model.getReponse_photoID().equals(from_notification_comment.getReponse_photoID())) {
                            final_other_part_list.add(model);
                        }
                    }

                    displayPhotos();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            if (!other_part_list.isEmpty()) {
                for(int i = 0; i < other_part_list.size(); i++){
                    final int count = i;
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_media))
                            .orderByChild(context.getString(R.string.field_group_id))
                            .equalTo(other_part_list.get(i));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                if (!removed_suggestions_list.contains(model.getGroup_id())) {
                                    if (!model.isSuppressed())
                                        final_other_part_list.add(model);
                                }
                            }

                            if(count >= other_part_list.size() -1){

                                displayPhotos();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            } else {
                displayPhotos();
            }
        }
    }

    private void displayPhotos() {
        if (from_notification_comment != null) {
            // to walk we must keep getOtherGroupsPosts() in resume
            total_list.add(from_notification_comment);

        } else {
            //sort for newest to oldest
            final_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                    .compareTo(String.valueOf(o1.getDate_created())));
            total_list.addAll(final_list);

            //sort for newest to oldest to other part of list
            final_other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                    .compareTo(String.valueOf(o1.getDate_created())));
            total_list.addAll(final_other_part_list);

            // add the header
            total_list.add(0, AddNestedScrollView.getBattleModel(true,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));
        }

        adapter = new GroupExplorerAdapter(context, progressBar, relLayout_view_overlay);
        // to prevent crash when we return to the app
        try {
            adapter.setDefaultRecyclerView(context, R.id.recyclerView);
        } catch (NullPointerException e) {
            Log.d(TAG, "displayPhotos: error: "+e.getMessage());
        }

        adapter.setOnPaginationListener(new PaginatedAdapter.OnPaginationListener() {
            @Override
            public void onCurrentPage(int page) {

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
            if (i < total_list.size()) {
                models.add(total_list.get(i));
            }
        }
        onGetDate(models);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        // publications
        new Thread(this::getPhoneContactsGroups).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;

            if (from_notification_comment != null) {
                getOtherGroupsPosts();
            } else {
                new Thread(this::getPhoneContactsGroups).start();
            }
        }
    }
}