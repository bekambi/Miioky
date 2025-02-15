package com.bekambimouen.miiokychallenge.challenge_home.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.ViewMyChallenges;
import com.bekambimouen.miiokychallenge.challenge.adapter.ViewOthersUsersChallengesAdapter;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 * Use the {@link ChallengeInvitationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeInvitationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ChallengeInvitationFragment";

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
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relLayout, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private HomeActivity context;
    private ViewOthersUsersChallengesAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<ModelInvite> challengesList, finalList;
    private ArrayList<String> responded_photoID_List, following_friend_and_user_id_list, groupFollowingIdList
            , group_photo_id_list, miioky_photo_id_list;
    private RelativeLayout relLayout_app_name;
    private Handler handler;
    private boolean isFirstLoaded = true, loading = false;

    private DatabaseReference myRef;
    private String user_id;

    public ChallengeInvitationFragment() {
        // Required empty public constructor
    }

    public static ChallengeInvitationFragment newInstance() {
        ChallengeInvitationFragment fragment = new ChallengeInvitationFragment();
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
        View view = inflater.inflate(R.layout.fragment_challenge_invitation, container, false);
        context = (HomeActivity) getActivity();

        // firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            relLayout_app_name = context.getRelLayout_app_name();
            relLayout_view_overlay = context.getRelLayout_view_overlay();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: error: "+e.getMessage());
        }

        if (user != null) {
            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            handler = new Handler(Looper.getMainLooper());

            init(view);
        }

        return view;
    }

    private void init(View v) {
        TextView send_challenge = v.findViewById(R.id.send_challenge);
        relLayout = v.findViewById(R.id.relLayout);
        progressBar = v.findViewById(R.id.progressBar);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);

        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(challengesList != null){
            challengesList.clear();
        }
        if(following_friend_and_user_id_list != null){
            following_friend_and_user_id_list.clear();
        }
        if(groupFollowingIdList != null){
            groupFollowingIdList.clear();
        }
        if(finalList != null){
            finalList.clear();
        }
        if(responded_photoID_List != null){
            responded_photoID_List.clear();
        }
        if(group_photo_id_list != null){
            group_photo_id_list.clear();
        }
        if(miioky_photo_id_list != null){
            miioky_photo_id_list.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        challengesList = new ArrayList<>();
        finalList = new ArrayList<>();
        following_friend_and_user_id_list = new ArrayList<>();
        groupFollowingIdList = new ArrayList<>();
        responded_photoID_List = new ArrayList<>();
        group_photo_id_list = new ArrayList<>();
        miioky_photo_id_list = new ArrayList<>();
    }

    private void getGroupFollowing(){
        Log.d(TAG, "getFollowing: searching for following");
        clearAll();

        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found group Following Invite: " +
                            singleSnapshot.child("user_id").getValue());

                    groupFollowingIdList.add(Objects.requireNonNull(singleSnapshot.child(context.getString(R.string.field_group_id)).getValue()).toString());
                }

                Query query1 = myRef
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            Log.d(TAG, "onDataChange: found group Following Invite: " +
                                    singleSnapshot.child("user_id").getValue());

                            groupFollowingIdList.add(Objects.requireNonNull(singleSnapshot.child(context.getString(R.string.field_group_id)).getValue()).toString());
                        }

                        readGroupInvitationChallenge();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readGroupInvitationChallenge() {
        Query query = myRef
                .child(context.getString(R.string.dbname_already_response_challenge))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    responded_photoID_List.add(ds.getKey());
                }

                Query query5 = myRef
                        .child(context.getString(R.string.dbname_group_invitation_challenge));

                query5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long currentTime = System.currentTimeMillis();
                        challengesList.clear();

                        for (String group_id: groupFollowingIdList) {
                            ModelInvite modelInvite = null;

                            for (DataSnapshot dataSnapshot: snapshot.child(group_id).getChildren()) {
                                ModelInvite modelInvite_verification = dataSnapshot.getValue(ModelInvite.class);

                                assert modelInvite_verification != null;
                                if (!modelInvite_verification.getInvite_userID().equals(user_id)) {
                                    if (!responded_photoID_List.contains(modelInvite_verification.getInvite_photoID()))
                                        modelInvite = dataSnapshot.getValue(ModelInvite.class);

                                    // to prevent nullPointerException
                                    if (modelInvite != null) {
                                        if (currentTime > modelInvite.getTimestart() && currentTime < modelInvite.getTimeend()) {

                                            if (!modelInvite.isSuppressed()) {
                                                // get challenge if user have been indexed
                                                if (modelInvite.getIndex_i().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_ii().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_iii().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_iv().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_v().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_vi().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_vii().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_viii().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_ix().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                                if (modelInvite.getIndex_x().equals(user_id)
                                                        && !group_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                    challengesList.add(modelInvite);
                                                    group_photo_id_list.add(modelInvite.getInvite_photoID());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        finalList.addAll(challengesList);
                        // get miioky challenge following
                        getFriendsFollowing();
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

    // get friend following
    private void getFriendsFollowing() {
        Log.d(TAG, "getFollowing: searching for following");
        //also add your own id to the list
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_friend_following))
                    .child(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        FriendsFollowerFollowing following = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                        String followingId = Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString();

                        // verify if user is unsubscribe from his friend
                        if (!following.isUnSubscribe()) {
                            following_friend_and_user_id_list.add(followingId);
                        }
                    }

                    getSimpleFollowing();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // get user following
    private void getSimpleFollowing() {
        Query query = myRef
                .child(context.getString(R.string.dbname_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String followingId = Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString();

                    following_friend_and_user_id_list.add(followingId);
                }

                readInvitation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readInvitation(){
        Query query = myRef
                .child(context.getString(R.string.dbname_already_response_challenge))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    responded_photoID_List.add(ds.getKey());
                }

                Query query5 = myRef
                        .child(context.getString(R.string.dbname_invite_battle));

                query5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long currentTime = System.currentTimeMillis();
                        challengesList.clear();

                        for (String userID: following_friend_and_user_id_list) {
                            ModelInvite modelInvite = null;

                            for (DataSnapshot dataSnapshot: snapshot.child(userID).getChildren()) {
                                ModelInvite modelInvite_verification = dataSnapshot.getValue(ModelInvite.class);
                                assert modelInvite_verification != null;
                                if (!responded_photoID_List.contains(modelInvite_verification.getInvite_photoID()))
                                    modelInvite = dataSnapshot.getValue(ModelInvite.class);

                                try {
                                    assert modelInvite != null;
                                    if (currentTime > modelInvite.getTimestart() && currentTime < modelInvite.getTimeend()) {

                                        if (!modelInvite.isSuppressed()) {
                                            // get challenge if user have been indexed
                                            if (modelInvite.getIndex_i().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_ii().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_iii().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_iv().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_v().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_vi().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_vii().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_viii().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_ix().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                            if (modelInvite.getIndex_x().equals(user_id)
                                                    && !miioky_photo_id_list.contains(modelInvite.getInvite_photoID())) {
                                                challengesList.add(modelInvite);
                                                miioky_photo_id_list.add(modelInvite.getInvite_photoID());
                                            }
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onDataChange: error: "+e.getMessage());
                                }
                            }
                        }

                        finalList.addAll(challengesList);

                        finalList.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                .compareTo(String.valueOf(o1.getDate_created())));

                        adapter = new ViewOthersUsersChallengesAdapter(context, finalList, progressBar, relLayout_view_overlay);
                        recyclerView.setAdapter(adapter);

                        context.runOnUiThread(() -> {
                            if (adapter.getItemCount() == 0) {
                                relLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else {
                                relLayout.setVisibility(View.GONE);
                            }
                        });

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // hide/show board
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);

                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        if (dy > 0 && !loading) {
                                            loading = true;
                                            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                                            int preloadCount =5; // Number of elements to preload

                                            // Calculate the starting index for preloading
                                            int startIndex = lastVisiblePosition + 1;

                                            // Ensure startIndex is within bounds and adjust preloadCount if necessary
                                            if (startIndex >= finalList.size()) {
                                                startIndex = finalList.size();
                                                preloadCount = 0; // Nothing to preload
                                            } else if (startIndex + preloadCount > finalList.size()) {
                                                preloadCount = finalList.size() - startIndex; // Adjust preloadCount
                                            }

                                            // Preload images
                                            for(int i = startIndex; i < startIndex + preloadCount; i++) {
                                                PreloadMyChallengeImages.getPreloadMyChallengeImages(context, finalList.get(i));
                                            }

                                            loading = false;
                                        }
                                    }
                                });
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        new Thread(this::getGroupFollowing).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // make app name enable false
        relLayout_app_name.setEnabled(false);
        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            new Thread(this::getGroupFollowing).start();
        }
    }
}