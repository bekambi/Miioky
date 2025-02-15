package com.bekambimouen.miiokychallenge.groups.manage_member.group_where_user_is_member;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.adapter.YourGroupsAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupWhereUserIsMember extends AppCompatActivity {
    private static final String TAG = "GroupWhereUserIsMember";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private GroupWhereUserIsMember context;
    private YourGroupsAdapter adapter;
    private ArrayList<UserGroups> list;
    public ArrayList<String> followingUserList;
    public ArrayList<String> group_id_List;
    public ArrayList<String> followingUserIDList;
    public ArrayList<String> userIDList;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_where_user_is_member);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(Looper.getMainLooper());

        try {
            userID = getIntent().getStringExtra("userID");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getGroupFollowing();
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
                .child(userID);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // first add current user id
                userIDList.add(userID);

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
                .child(userID);

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

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            for (String groupKey : group_id_List) {
                                if (user_groups.getGroup_id().equals(groupKey))
                                    list.add(user_groups);
                            }
                        }

                        if(count_user_list >= followingUserIDList.size() -1){

                            list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                    .compareTo(String.valueOf(o1.getDate_created())));

                            adapter = new YourGroupsAdapter(context, list, userID, relLayout_view_overlay);
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
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}