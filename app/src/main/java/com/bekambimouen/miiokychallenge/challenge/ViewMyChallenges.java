package com.bekambimouen.miiokychallenge.challenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge.adapter.ViewMyChallengesAdapter;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_publication.PubChallengePhoto;
import com.bekambimouen.miiokychallenge.challenge_publication.PubChallengeVideo;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.GroupPubChallengePhoto;
import com.bekambimouen.miiokychallenge.groups.publication.GroupPubChallengeVideo;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewMyChallenges extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "VueImagesUserBattle";

    // widgets
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout parent, relLayout_not_yet_challenge, add_photo, add_video, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private ViewMyChallenges context;
    private ViewMyChallengesAdapter adapter;
    private ArrayList<ModelInvite> challengesList;
    private ArrayList<String> groupIdList;
    private Handler handler;
    private String from_group_challenge;

    private UserGroups user_groups;

    // verify if user has posted a new challenge
    private boolean isHasPosted = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_view_my_challenges);
        context = ViewMyChallenges.this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
            from_group_challenge = getIntent().getStringExtra("from_group_challenge");
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
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        add_photo = findViewById(R.id.add_photo);
        add_video = findViewById(R.id.add_video);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_not_yet_challenge = findViewById(R.id.relLayout_not_yet_challenge);
        LinearLayout linLayout_camera_photo = findViewById(R.id.linLayout_camera_photo);
        LinearLayout linLayout_camera_video = findViewById(R.id.linLayout_camera_video);
        progressBar = findViewById(R.id.progressBar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getGroupFollowing();

        if (from_group_challenge != null) {
            addChallengeInGroup();

        } else {
            add_photo.setOnClickListener(view -> {
                // prevent double clik
                Util.preventTwoClick(view);
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubChallengePhoto.class);
                intent.putExtra("my_challenge_photo", "data");
                mLauncher.launch(intent);
            });

            add_video.setOnClickListener(view -> {
                // prevent double clik
                Util.preventTwoClick(view);
                // interface to change ViewMyChallenge state
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubChallengeVideo.class);
                intent.putExtra("my_challenge_photo", "data");
                mLauncher.launch(intent);
            });

            linLayout_camera_photo.setOnClickListener(view -> {
                // prevent double clik
                Util.preventTwoClick(view);
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubChallengePhoto.class);
                intent.putExtra("my_challenge_photo", "data");
                mLauncher.launch(intent);
            });

            linLayout_camera_video.setOnClickListener(view -> {
                // prevent double clik
                Util.preventTwoClick(view);
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubChallengeVideo.class);
                intent.putExtra("my_challenge_photo", "data");
                mLauncher.launch(intent);
            });
        }

        arrowBack.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    isHasPosted = true;
                }
            }
    );

    private void addChallengeInGroup() {
        add_photo.setOnClickListener(view -> {
            // prevent double clik
            Util.preventTwoClick(view);
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupPubChallengePhoto.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
            finish();
        });

        add_video.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupPubChallengeVideo.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(challengesList != null){
            challengesList.clear();
        }
        if(groupIdList != null){
            groupIdList.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }
        challengesList = new ArrayList<>();
        groupIdList = new ArrayList<>();
    }

    private void getGroupFollowing() {
        clearAll();
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get all user's groups
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String groupID = ds.getKey();
                    groupIdList.add(groupID);
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

                            long timecurrent = System.currentTimeMillis();

                            if (timecurrent > model.getTimestart() && timecurrent < model.getTimeend()) {
                                if (model.getInvite_userID().equals(user_id)) {
                                    if (!model.isSuppressed())
                                        challengesList.add(model);
                                }
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
                    long timecurrent = System.currentTimeMillis();

                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    assert objectMap != null;
                    ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                    if (timecurrent > model.getTimestart() && timecurrent < model.getTimeend()) {
                        if (!model.isSuppressed())
                            challengesList.add(model);
                    }
                }

                displayPhotos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayPhotos(){
        //sort for newest to oldest
        challengesList.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        adapter = new ViewMyChallengesAdapter(context, challengesList, progressBar, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            add_photo.setVisibility(View.GONE);
            add_video.setVisibility(View.GONE);
            relLayout_not_yet_challenge.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        } else {
            relLayout_not_yet_challenge.setVisibility(View.GONE);
            add_photo.setVisibility(View.VISIBLE);
            add_video.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);

        if (isHasPosted) {
            isHasPosted = false;

            if (View.VISIBLE == relLayout_not_yet_challenge.getVisibility())
                relLayout_not_yet_challenge.setVisibility(View.GONE);

            new Handler().postDelayed(() -> {
                //context.recreate();
                finish();
                startActivity(getIntent());
            }, 500);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getGroupFollowing();
    }
}