package com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSeeUserChallenges;
import com.bekambimouen.miiokychallenge.challenge.ViewMyChallenges;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_home.adapter.MyChallengesAdapter;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.auth.FirebaseAuth;
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

public class MyChallenges extends AppCompatActivity {
    private static final String TAG = "MyChallenges";

    // Camera Permissions
    private final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // widgets
    private TextView nothing_here_for_the_moment;
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_not_yet_challenge;
    private RelativeLayout relLayout_view_overlay;
    private RelativeLayout main;
    private ProgressBar progressBar;

    // vars
    private MyChallenges context;
    private MyChallengesAdapter adapter;
    private ArrayList<ModelInvite> challengesList;
    private ArrayList<String> groupIdList;
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
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_my_challenges);
        context = this;

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
        main = findViewById(R.id.main);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_accepted_challeneges = findViewById(R.id.relLayout_accepted_challeneges);
        TextView send_challenge = findViewById(R.id.send_challenge);
        nothing_here_for_the_moment = findViewById(R.id.nothing_here_for_the_moment);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout_not_yet_challenge = findViewById(R.id.relLayout_not_yet_challenge);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getGroupFollowing();

        relLayout_accepted_challeneges.setOnClickListener(view -> {
            BottomSheetSeeUserChallenges bottomSheet = new BottomSheetSeeUserChallenges(context, user_id);
            bottomSheet.show(context.getSupportFragmentManager(), TAG);
        });

        if (user_id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
            send_challenge.setVisibility(View.VISIBLE);

        send_challenge.setOnClickListener(view -> {
            // check permission
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, ViewMyChallenges.class));
            }
        });

        arrowBack.setOnClickListener(view -> finish());
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
        challengesList.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        adapter = new MyChallengesAdapter(context, from_view_profile_user_id, progressBar, relLayout_view_overlay);
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

        if (adapter.getItemCount() == 0) {
            relLayout_not_yet_challenge.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (from_view_profile_user_id != null)
                nothing_here_for_the_moment.setText(context.getString(R.string.nothing_here_yet));

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
            if (i < challengesList.size()) {
                models.add(challengesList.get(i));
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
        CheckInternetStatus.internetIsConnected(context, main);
    }
}