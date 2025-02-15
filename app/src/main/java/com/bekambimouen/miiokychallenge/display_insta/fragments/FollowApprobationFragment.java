package com.bekambimouen.miiokychallenge.display_insta.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.followersfollowings.NbFollowers;
import com.bekambimouen.miiokychallenge.friends.SeeAllFollowingsRequest;
import com.bekambimouen.miiokychallenge.friends.adapter.SubscriptionRequestAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.search.user_common_friends.adapter.AllUserFriendsAdapter;
import com.bekambimouen.miiokychallenge.suggestions.SeeAllSuggestions;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowApprobationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowApprobationFragment extends Fragment {
    // widgets
    private TextView number_of_subscription_request, subscription_request;
    private RecyclerView recyclerView_subscription_request;
    private RelativeLayout relLayout_subscription_request, relLayout_view_overlay;
    // suggestion
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView see_all;

    // vars
    private MainActivity context;
    private SubscriptionRequestAdapter adapter_subscription_request;
    private ArrayList<User> userList_subscription_request, pagination;
    private ArrayList<String> following_userIDList_subscription_request;
    private Handler handler;
    private int t = 0, n = 0;
    private boolean isFirstLoaded = true;

    // vars suggestions
    private AllUserFriendsAdapter adapter;
    private ArrayList<User> list, other_part_list, finalUserList, finalList,
            all_user_list, tamponList;
    private ArrayList<String> following_userIDList, removed_suggestions_list, total_user_id_list;
    // suggestion about school and work place
    private String current_user_hometown, current_user_live_town;
    private ArrayList<SchoolAttended> current_user_schools_list;
    private ArrayList<FrequentedEstablishment> current_user_establishments_list;
    private ArrayList<WorkAt> current_user_workplaces_list;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;
    // suggestion abut common friends
    private List<String> current_user_friends_list, friend_user_friends_list;
    // suggestion abut 2 common groups i follow
    private ArrayList<String> group_id_list, members_of_groups_list, filter_members_of_groups_list;
    private int common_groups_members_count = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public FollowApprobationFragment() {
        // Required empty public constructor
    }

    public static FollowApprobationFragment newInstance() {
        FollowApprobationFragment fragment = new FollowApprobationFragment();
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
        View view = inflater.inflate(R.layout.fragment_follow_approbation, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        handler = new Handler(Looper.getMainLooper());
        init(view);

        return view;
    }

    private void init(View v) {
        RelativeLayout relLayout_suggestion = v.findViewById(R.id.relLayout_suggestion);
        RelativeLayout relLayout_your_followers = v.findViewById(R.id.relLayout_your_followers);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);
        relLayout_subscription_request = v.findViewById(R.id.relLayout_subscription_request);
        number_of_subscription_request = v.findViewById(R.id.number_of_subscription_request);
        TextView tv_voir_tout = v.findViewById(R.id.tv_voir_tout);
        subscription_request = v.findViewById(R.id.subscription_request);
        recyclerView_subscription_request = v.findViewById(R.id.recyclerView_subscription_request);
        recyclerView_subscription_request.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView_subscription_request.setLayoutManager(layoutManager);
        recyclerView_subscription_request.setItemAnimator(new DefaultItemAnimator());
        // suggestion
        progressBar = v.findViewById(R.id.progressBar);
        see_all = v.findViewById(R.id.see_all);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tv_voir_tout.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SeeAllFollowingsRequest.class);
            context.startActivity(intent);
        });

        relLayout_suggestion.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SeeAllSuggestions.class);
            context.startActivity(intent);
        });

        see_all.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SeeAllSuggestions.class);
            context.startActivity(intent);
        });

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    relLayout_your_followers.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        Intent intent = new Intent(context, NbFollowers.class);
                        intent.putExtra("user", myGson);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearAll_confirmation(){
        if(userList_subscription_request != null){
            userList_subscription_request.clear();
        }
        if(following_userIDList_subscription_request != null){
            following_userIDList_subscription_request.clear();
        }

        if(adapter_subscription_request != null){
            adapter_subscription_request = null;
        }

        if(recyclerView_subscription_request != null){
            handler.post(() -> recyclerView_subscription_request.setAdapter(null));
        }

        userList_subscription_request = new ArrayList<>();
        following_userIDList_subscription_request = new ArrayList<>();
    }

    void getData_subscription_request() {
        clearAll_confirmation();
        Query query1 = myRef.child(context.getString(R.string.dbname_subscription_request_follower))
                .child(user_id);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    t++;
                    following_userIDList_subscription_request.add(keyID);
                }

                number_of_subscription_request.setText(Html.fromHtml(context.getString(R.string.request)+
                        " <font color=red>"+t+"</font>"));

                for(int i = 0; i < following_userIDList_subscription_request.size(); i++){
                    final int count = i;

                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(following_userIDList_subscription_request.get(i));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                n++;
                                if (n <= 5)
                                    userList_subscription_request.add(user);
                            }

                            if(count >= following_userIDList_subscription_request.size() -1){

                                userList_subscription_request.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                        .compareTo(String.valueOf(o1.getDate_created())));

                                adapter_subscription_request = new SubscriptionRequestAdapter(context, userList_subscription_request,
                                        relLayout_subscription_request, null, relLayout_view_overlay);
                                recyclerView_subscription_request.setAdapter(adapter_subscription_request);

                                if (adapter_subscription_request.getItemCount() >= 1) {
                                    relLayout_subscription_request.setVisibility(View.VISIBLE);
                                    subscription_request.setVisibility(View.VISIBLE);
                                }
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

    private void clearAll(){
        if(following_userIDList != null){
            following_userIDList.clear();
        }
        if(finalList != null){
            finalList.clear();
        }
        if (members_of_groups_list != null) {
            members_of_groups_list.clear();
        }
        if (group_id_list != null) {
            group_id_list.clear();
        }
        if (friend_user_friends_list != null) {
            friend_user_friends_list.clear();
        }
        if (all_user_list != null) {
            all_user_list.clear();
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
        if(list != null){
            list.clear();
        }
        if (other_part_list != null) {
            other_part_list.clear();
        }
        if (finalUserList != null) {
            finalUserList.clear();
        }
        if(pagination != null){
            pagination.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        tamponList = new ArrayList<>();
        finalList = new ArrayList<>();
        all_user_list = new ArrayList<>();
        following_userIDList = new ArrayList<>();
        removed_suggestions_list = new ArrayList<>();
        total_user_id_list = new ArrayList<>();

        list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        finalUserList = new ArrayList<>();
        // suggestion about school and work place
        current_user_schools_list = new ArrayList<>();
        current_user_establishments_list = new ArrayList<>();
        current_user_workplaces_list = new ArrayList<>();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
        current_user_friends_list = new ArrayList<>();
        friend_user_friends_list = new ArrayList<>();
        members_of_groups_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        filter_members_of_groups_list = new ArrayList<>();
        pagination = new ArrayList<>();
    }

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
                                                list.add(user);
                                                total_user_id_list.add(user.getUser_id());
                                            }
                                        }
                                    }
                                }

                                // verify if we are in the same establishment
                                for (int j = 0; j < current_user_establishments_list.size(); j++) {

                                    for (int i = 0; i < establishments_list.size(); i++) {
                                        if (establishments_list.get(i).getUser_establishment().equals(current_user_establishments_list.get(j).getUser_establishment())) {
                                            if (!total_user_id_list.contains(establishments_list.get(i).getUser_id())) {
                                                list.add(user);
                                                total_user_id_list.add(user.getUser_id());
                                            }
                                        }
                                    }
                                }

                                // verify if we are in the same workplace
                                for (int j = 0; j < current_user_workplaces_list.size(); j++) {

                                    for (int i = 0; i < workplaces_list.size(); i++) {
                                        if (workplaces_list.get(i).getUser_work_at().equals(current_user_workplaces_list.get(j).getUser_work_at())) {
                                            if (!total_user_id_list.contains(workplaces_list.get(i).getUser_id())) {
                                                list.add(user);
                                                total_user_id_list.add(user.getUser_id());
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
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String keyID = dataSnapshot.getKey();

                                friend_user_friends_list.add(keyID);
                            }

                            // get the common friends
                            if (count >= all_user_list.size() - 1) {
                                for (String ID: current_user_friends_list) {
                                    for (int j = 0; j < friend_user_friends_list.size(); j++) {
                                        if (ID.equals(friend_user_friends_list.get(j))) {

                                            if (!total_user_id_list.contains(all_user_list.get(finalI).getUser_id())) {
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
                                                                    if (!total_user_id_list.contains(user.getUser_id())) {
                                                                        list.add(user);
                                                                        total_user_id_list.add(user.getUser_id());
                                                                    }
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
                    // liste des utilistauers auxquels je ne suis pas abonné
                    tamponList.add(finalUserList.get(i));
                }
            }
        } else {
            tamponList.addAll(finalUserList);
        }

        // to start adapter to some position
        for (int i = 0; i < tamponList.size(); i++) {
            if (!removed_suggestions_list.contains(tamponList.get(i).getUser_id()))
                finalList.add(tamponList.get(i));
        }

        int iterations = finalList.size();

        if(iterations > 15){
            iterations = 15;
        }

        for(int i = 0; i < iterations; i++){
            pagination.add(finalList.get(i));
        }

        adapter = new AllUserFriendsAdapter(context, pagination, progressBar, null, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() >= 15) {
            see_all.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            getData_subscription_request();
            getPhoneContactsSuggestions();
        }
    }
}