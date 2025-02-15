package com.bekambimouen.miiokychallenge.groups.explorer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.adapter.YourGroupsAdapter;
import com.bekambimouen.miiokychallenge.groups.explorer.GroupExplorer;
import com.bekambimouen.miiokychallenge.groups.explorer.Utils.AddNestedScrollView;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupYourGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupYourGroupsFragment extends Fragment {
    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_view_overlay;

    // vars
    private GroupExplorer context;
    private YourGroupsAdapter adapter;
    private ArrayList<UserGroups> list;
    public ArrayList<String> followingUserList;
    public ArrayList<String> group_id_List;
    public ArrayList<String> followingUserIDList;
    public ArrayList<String> userIDList;
    private Handler handler;
    private boolean isFirstLoaded = true;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public GroupYourGroupsFragment() {
        // Required empty public constructor
    }

    public static GroupYourGroupsFragment newInstance() {
        GroupYourGroupsFragment fragment = new GroupYourGroupsFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_your_groups, container, false);
        context = (GroupExplorer) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        handler = new Handler(Looper.getMainLooper());

        relLayout_view_overlay = context.getRelLayout_view_overlay();

        init(view);

        return view;
    }

    private void init(View v) {
        recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(list != null){
            list.clear();
        }
        if(followingUserList != null){
            followingUserList.clear();
        }
        if(group_id_List != null){
            group_id_List.clear();
        }

        if(followingUserIDList != null){
            followingUserIDList.clear();
        }
        if(userIDList != null){
            userIDList.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list = new ArrayList<>();
        followingUserList = new ArrayList<>();
        followingUserIDList = new ArrayList<>();
        group_id_List = new ArrayList<>();
        userIDList = new ArrayList<>();
    }

    /**
     // * RÃ©cupÃ©rer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     // */
    private void getGroupFollowing() {
        clearAll();

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // first add current user id
                userIDList.add(user_id);

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    // get following admin master
                    String admin_id = following.getAdmin_master();
                    if (!userIDList.contains(admin_id)) {
                        userIDList.add(admin_id);
                    }
                }
                getUserIdList(userIDList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // add all other following groups
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    String followingId = following.getGroup_id();

                    // get the list of group where member has been banned
                    if (!following.isBanFromGroup() && !following.isAdminInGroup() && !following.isModerator())
                        group_id_List.add(followingId);
                }

                getGroups(group_id_List);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get list of all followings
    private void getUserIdList(ArrayList<String> following_usersIDList) {
        followingUserIDList.addAll(following_usersIDList);
    }

    private void getGroups(ArrayList<String> group_id_List) {
        if (!followingUserIDList.isEmpty()) {
            for(int i = 0; i < followingUserIDList.size(); i++){
                final int count_user_list = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(followingUserIDList.get(i))
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(followingUserIDList.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            for (String groupKey : group_id_List) {
                                if (user_groups.getGroup_id().equals(groupKey))
                                    if (!user_groups.isSuppressed())
                                        list.add(user_groups);
                            }
                        }

                        if(count_user_list >= followingUserIDList.size() -1){

                            list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                    .compareTo(String.valueOf(o1.getDate_created())));

                            list.add(0, AddNestedScrollView.getUserGroups(true));

                            adapter = new YourGroupsAdapter(context, list, user_id, relLayout_view_overlay);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
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
            getGroupFollowing();
        }
    }
}