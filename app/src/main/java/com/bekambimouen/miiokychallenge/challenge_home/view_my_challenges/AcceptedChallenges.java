package com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_home.adapter.MyChallengesAdapter;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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

public class AcceptedChallenges extends AppCompatActivity {
    private static final String TAG = "ChallengesResponded";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_not_yet_challenge, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private AcceptedChallenges context;
    private MyChallengesAdapter adapter;
    private ArrayList<String> response_invite_photo_id_List, groupIdList;
    private ArrayList<ModelInvite> challengesList, final_list;
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
        setContentView(R.layout.activity_accepted_challenges);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        try {
            user_id = getIntent().getStringExtra("user_id");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        handler = new Handler(Looper.getMainLooper());

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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout_not_yet_challenge = findViewById(R.id.relLayout_not_yet_challenge);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getRespondedChallenges();

        arrowBack.setOnClickListener(view -> finish());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll() {
        if (final_list != null) {
            final_list.clear();
        }
        if (response_invite_photo_id_List != null) {
            response_invite_photo_id_List.clear();
        }
        if (groupIdList != null) {
            groupIdList.clear();
        }
        if (challengesList != null) {
            challengesList.clear();
        }

        if (adapter != null) {
            adapter = null;
        }

        if (recyclerView != null) {
            handler.post(() -> recyclerView.setAdapter(null));
        }

        response_invite_photo_id_List = new ArrayList<>();
        groupIdList = new ArrayList<>();
        challengesList = new ArrayList<>();
        final_list = new ArrayList<>();
    }

    private void getRespondedChallenges() {
        clearAll();
        Query query = myRef
                .child(context.getString(R.string.dbname_group_response_challenge));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    response_invite_photo_id_List.add(ds.getKey());
                }

                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_battle_response_challenge));
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            response_invite_photo_id_List.add(ds.getKey());
                        }

                        getGroupFollowing();
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

    private void getGroupFollowing() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get all user's groups
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    UserGroups userGroups = Util_UserGroups.getUserGroups(context, objectMap);
                    String groupID = ds.getKey();

                    if (!userGroups.isSuppressed()) {
                        groupIdList.add(groupID);
                    }
                }

                // get all user's groups following
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String followingId = ds.getKey();
                            groupIdList.add(followingId);
                        }

                        getGroupInvitationChallenge();
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

    private void getGroupInvitationChallenge() {
        Log.d(TAG, "getPhotos: getting list of photos");
        if (!groupIdList.isEmpty()) {
            for (int i = 0; i < groupIdList.size(); i++) {
                final int count_id = i;
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_invitation_challenge))
                        .child(groupIdList.get(i))
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(groupIdList.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot singleSnapshot :  snapshot.getChildren()){
                            Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            if (model.getInvite_userID().equals(user_id)) {
                                if (!model.isSuppressed())
                                    challengesList.add(model);
                            }
                        }

                        if (count_id >= groupIdList.size() -1) {

                            getInvitationChallenge();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            getInvitationChallenge();
        }
    }

    private void getInvitationChallenge(){
        Log.d(TAG, "getPhotos: getting list of photos");
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_invite_battle))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot :  snapshot.getChildren()){
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    assert objectMap != null;
                    ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                    if (!model.isSuppressed())
                        challengesList.add(model);
                }

                // keep only the responded challenges
                for (int j = 0; j < response_invite_photo_id_List.size(); j++) {
                    for (int i = 0; i < challengesList.size(); i++) {
                        if (challengesList.get(i).getInvite_photoID().equals(response_invite_photo_id_List.get(j))) {
                            final_list.add(challengesList.get(i));
                        }
                    }
                }

                displayPhotos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayPhotos(){
        //sort for newest to oldest
        final_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        adapter = new MyChallengesAdapter(context, null, progressBar, relLayout_view_overlay);
        adapter.setDefaultRecyclerView(context, R.id.recyclerView);

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

        if (adapter.getItemCount() == 0) {
            relLayout_not_yet_challenge.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        } else {
            relLayout_not_yet_challenge.setVisibility(View.GONE);
        }
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

    public void internetIsConnected() {
        // internet control
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(this, getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // verify internet connection
        internetIsConnected();
    }
}