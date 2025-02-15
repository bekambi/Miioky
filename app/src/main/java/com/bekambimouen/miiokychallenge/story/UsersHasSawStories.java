package com.bekambimouen.miiokychallenge.story;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.story.adapter.SawStoriesAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UsersHasSawStories extends AppCompatActivity {
    // widgets
    private RelativeLayout parent;

    // vars
    private SawStoriesAdapter usersAdapter;
    private ArrayList<User> usersList;
    private UsersHasSawStories context;
    private List<String> idList;
    private String id;

    // firebase
    private FirebaseDatabase database;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_users_has_saw_stories);
        context = this;

        database=FirebaseDatabase.getInstance();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        parent = findViewById(R.id.parent);
        TextView tv_views = findViewById(R.id.tv_views);
        TextView number1 = findViewById(R.id.number_views);
        // widgets
        RecyclerView recyclerView = findViewById(R.id.FollowFollowing_recyclerView);

        context.getWindow().setExitTransition(new Slide(Gravity.END));
        context.getWindow().setEnterTransition(new Slide(Gravity.START));
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String number = intent.getStringExtra("number");

        tv_views.setText(title);
        assert number != null;
        int value = Integer.parseInt(number) - 1;
        number1.setText(String.valueOf(value));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersList = new ArrayList<>();
        usersAdapter = new SawStoriesAdapter(this, usersList);
        recyclerView.setAdapter(usersAdapter);

        idList = new ArrayList<>();

        switch (Objects.requireNonNull(title)){
            case ("Followers"):
                getFollowers();
                break;
            case ("Following"):
                getFollowings();
                break;
            case "Vues":
                getViews();
                break;

        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void getFollowings() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_following))
                .child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_followers))
                .child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    for(String id : idList){
                        if (!user.getUser_id().equals(user_id)) {
                            if(user.getUser_id().equals(id)){
                                usersList.add(user);
                            }
                        }
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getViews(){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_story))
                .child(id)
                .child(Objects.requireNonNull(getIntent().getStringExtra(getString(R.string.field_storyid))))
                .child("views");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}