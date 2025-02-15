package com.bekambimouen.miiokychallenge.friends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.friends.adapter.FriendConfirmationAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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

public class SeeAllFriendsRequest extends AppCompatActivity {

    // widgets
    private RelativeLayout main, relLayout_view_overlay;
    private RecyclerView recyclerView_confirmation;
    private ProgressBar progressBar;

    // vars
    private SeeAllFriendsRequest context;
    private FriendConfirmationAdapter adapter_confirmation;
    private ArrayList<User> userList_confirmation, pagination;
    private ArrayList<String> following_userIDList_confirmation;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_see_all_friends_request);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        init();
        getData_confirmation();
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
        progressBar = findViewById(R.id.progressBar);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        recyclerView_confirmation = findViewById(R.id.recyclerView_confirmation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView_confirmation.setLayoutManager(layoutManager);
        recyclerView_confirmation.setItemAnimator(new DefaultItemAnimator());

        arrowBack.setOnClickListener(view -> {
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void clearAll_confirmation(){
        if(userList_confirmation != null){
            userList_confirmation.clear();
        }
        if(following_userIDList_confirmation != null){
            following_userIDList_confirmation.clear();
        }

        if(adapter_confirmation != null){
            adapter_confirmation = null;
        }

        if(recyclerView_confirmation != null){
            handler.post(() -> recyclerView_confirmation.setAdapter(null));
        }

        userList_confirmation = new ArrayList<>();
        following_userIDList_confirmation = new ArrayList<>();
    }

    void getData_confirmation() {
        clearAll_confirmation();
        Query query1 = myRef.child(context.getString(R.string.dbname_invitation_friend_follower))
                .child(user_id);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    following_userIDList_confirmation.add(keyID);
                }

                for(int i = 0; i < following_userIDList_confirmation.size(); i++){
                    final int count = i;

                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(following_userIDList_confirmation.get(i));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                userList_confirmation.add(user);
                            }

                            if(count >= following_userIDList_confirmation.size() -1){

                                userList_confirmation.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                        .compareTo(String.valueOf(o1.getDate_created())));

                                adapter_confirmation = new FriendConfirmationAdapter(context, userList_confirmation, null, progressBar, relLayout_view_overlay);
                                recyclerView_confirmation.setAdapter(adapter_confirmation);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, main);
    }
}