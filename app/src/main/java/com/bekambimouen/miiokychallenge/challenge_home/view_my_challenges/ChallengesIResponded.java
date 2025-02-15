package com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.adapter.ChallengesIRespondedAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChallengesIResponded extends AppCompatActivity {
    private static final String TAG = "ChallengesIResponded";

    // widgets
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout parent, relLayout_view_overlay, relLayout_not_yet_challenge;

    // vars
    private ChallengesIResponded context;
    private ChallengesIRespondedAdapter adapter;
    private ArrayList<BattleModel> list;
    private ArrayList<String> groupFollowing, filter_list;
    private String from_view_profile_user_id;
    private Handler handler;

    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges_iresponded);
        context = this;

        // countries flags
        World.init(context);
        handler = new Handler(Looper.getMainLooper());

        myRef = FirebaseDatabase.getInstance().getReference();

        try {
            from_view_profile_user_id = context.getIntent().getStringExtra("from_view_profile_user_id");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (from_view_profile_user_id != null)
            user_id = from_view_profile_user_id;
        else
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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
        relLayout_not_yet_challenge = findViewById(R.id.relLayout_not_yet_challenge);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrowBack.setOnClickListener(view -> finish());
    }

    private void clearAll(){
        if(list != null){
            list.clear();
        }
        if (filter_list != null) {
            filter_list.clear();
        }

        // suggestion ________________________________________
        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }
        groupFollowing = new ArrayList<>();
        list = new ArrayList<>();

        filter_list = new ArrayList<>();
    }

    /**
     //     * Récupérer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     //     */
    private void getGroupFollowing() {
        Log.d(TAG, "getFollowing: searching for following");
        clearAll();

        //also add your own id to the list
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id);

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get all current user groups
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        UserGroups userGroups = Util_UserGroups.getUserGroups(context, objectMap);

                        String groupID = ds.getKey();

                        if (!userGroups.isGroup_paused())
                            groupFollowing.add(groupID);
                    }

                    // get all user groups
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

                                String followingId = ds.getKey();

                                // get the list of group where member has been banned
                                if (!following.isBanFromGroup())
                                    if (!following.isGroup_paused())
                                        groupFollowing.add(followingId);
                            }

                            getGroupPosts();
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
    }

    private void getGroupPosts() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        if (!groupFollowing.isEmpty()) {
            for(int i = 0; i < groupFollowing.size(); i++){
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group))
                        .child(groupFollowing.get(i)) // group_id
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(groupFollowing.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            // to prevent duplicate challenge
                            if (model.getReponse_user_id().equals(user_id)) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                }
                            }
                        }

                        if(count >= groupFollowing.size() -1){

                            getPhotos();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else
            getPhotos();
    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting list of photos");
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_battle))
                .child(user_id) // user_id
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    // to prevent duplicate challenge
                    if (model.getReponse_user_id().equals(user_id)) {
                        if (!TextUtils.isEmpty(model.getReponse_photoID())
                                &&
                                (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                        || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                            if (!filter_list.contains(model.getReponse_photoID())) {
                                if (!model.isSuppressed())
                                    list.add(model);
                                filter_list.add(model.getReponse_photoID());
                            }
                        }
                    }
                }

                //add the suggestion posts
                displayTheList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void displayTheList() {
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        adapter = new ChallengesIRespondedAdapter(context, progressBar, relLayout_view_overlay);

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

        if (adapter.getItemCount() == 0) {
            relLayout_not_yet_challenge.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        } else {
            relLayout_not_yet_challenge.setVisibility(View.GONE);
        }
    }

    public void onGetDate(List<BattleModel> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        List<BattleModel> models = new ArrayList<>();
        int start = page * counter - counter;
        int end = page * counter;
        for (int i = start; i < end; i++) {
            if (i < list.size()) {
                models.add(list.get(i));
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