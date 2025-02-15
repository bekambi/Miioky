package com.bekambimouen.miiokychallenge.groups.invite_user_in_group;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.groups.explorer.GroupExplorer;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.adapter.InviteMembersSuggestionAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InvitePersonsAfterGroupCreated extends AppCompatActivity {
    private static final String TAG = "InvitePersonsAfterGroupCreated";
    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private RecyclerView recyclerView_invite_members;
    private ImageView erase;
    private MyEditText edit_search;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private InvitePersonsAfterGroupCreated context;
    private InviteMembersSuggestionAdapter adapter_invite_members;
    private ArrayList<String> user_idList;
    private ArrayList<User> userList;
    private String theme, newGroupKey;
    private Handler handler;

    // firebase
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_invite_persons_after_group_created);
        context = this;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            theme = getIntent().getStringExtra("theme");
            newGroupKey = getIntent().getStringExtra("newGroupKey");

            if (theme.equals(context.getString(R.string.theme_normal))) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.WHITE);

                // changer a couleur des icons en noir
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            } else if (theme.equals(context.getString(R.string.theme_blue))) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getColor(R.color.blue_600));

                // changer a couleur des icons en blanc
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(0);

            } else if (theme.equals(context.getString(R.string.theme_brown))) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getColor(R.color.background_brown));

                // changer a couleur des icons en blanc
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(0);

            } else if (theme.equals(context.getString(R.string.theme_pink))) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getColor(R.color.pink));

                // changer a couleur des icons en blanc
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(0);

            } else if (theme.equals(context.getString(R.string.theme_green))) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getColor(R.color.vertWatsApp));

                // changer a couleur des icons en blanc
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(0);

            } else if (theme.equals(context.getString(R.string.theme_black))) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getColor(R.color.black));

                // changer a couleur des icons en blanc
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(0);

            }

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getFriends();
        fetchUsers();
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
        RelativeLayout relLayout_finish_normal = findViewById(R.id.relLayout_finish_normal);
        RelativeLayout relLayout_finish_blue = findViewById(R.id.relLayout_finish_blue);
        RelativeLayout relLayout_finish_brown = findViewById(R.id.relLayout_finish_brown);
        RelativeLayout relLayout_finish_pink = findViewById(R.id.relLayout_finish_pink);
        RelativeLayout relLayout_finish_green = findViewById(R.id.relLayout_finish_green);
        RelativeLayout relLayout_finish_black = findViewById(R.id.relLayout_finish_black);

        Toolbar toolBar_invite_normal = findViewById(R.id.toolBar_invite_normal);
        Toolbar toolBar_invite_blue = findViewById(R.id.toolBar_invite_blue);
        Toolbar toolBar_invite_brown = findViewById(R.id.toolBar_invite_brown);
        Toolbar toolBar_invite_pink = findViewById(R.id.toolBar_invite_pink);
        Toolbar toolBar_invite_green = findViewById(R.id.toolBar_invite_green);
        Toolbar toolBar_invite_black = findViewById(R.id.toolBar_invite_black);

        recyclerView_invite_members = findViewById(R.id.recyclerView_invite_members);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView_invite_members.setLayoutManager(layoutManager);
        recyclerView_invite_members.setItemAnimator(new DefaultItemAnimator());

        erase = findViewById(R.id.erase);
        edit_search = findViewById(R.id.edit_search);

        if (theme.equals(context.getString(R.string.theme_normal))) {
            toolBar_invite_normal.setVisibility(View.VISIBLE);
            toolBar_invite_blue.setVisibility(View.GONE);
            toolBar_invite_brown.setVisibility(View.GONE);
            toolBar_invite_pink.setVisibility(View.GONE);
            toolBar_invite_green.setVisibility(View.GONE);
            toolBar_invite_black.setVisibility(View.GONE);

        } else if (theme.equals(context.getString(R.string.theme_blue))) {
            toolBar_invite_normal.setVisibility(View.GONE);
            toolBar_invite_blue.setVisibility(View.VISIBLE);
            toolBar_invite_brown.setVisibility(View.GONE);
            toolBar_invite_pink.setVisibility(View.GONE);
            toolBar_invite_green.setVisibility(View.GONE);
            toolBar_invite_black.setVisibility(View.GONE);

        } else if (theme.equals(context.getString(R.string.theme_brown))) {
            toolBar_invite_normal.setVisibility(View.GONE);
            toolBar_invite_blue.setVisibility(View.GONE);
            toolBar_invite_brown.setVisibility(View.VISIBLE);
            toolBar_invite_pink.setVisibility(View.GONE);
            toolBar_invite_green.setVisibility(View.GONE);
            toolBar_invite_black.setVisibility(View.GONE);

        } else if (theme.equals(context.getString(R.string.theme_pink))) {
            toolBar_invite_normal.setVisibility(View.GONE);
            toolBar_invite_blue.setVisibility(View.GONE);
            toolBar_invite_brown.setVisibility(View.GONE);
            toolBar_invite_pink.setVisibility(View.VISIBLE);
            toolBar_invite_green.setVisibility(View.GONE);
            toolBar_invite_black.setVisibility(View.GONE);

        } else if (theme.equals(context.getString(R.string.theme_green))) {
            toolBar_invite_normal.setVisibility(View.GONE);
            toolBar_invite_blue.setVisibility(View.GONE);
            toolBar_invite_brown.setVisibility(View.GONE);
            toolBar_invite_pink.setVisibility(View.GONE);
            toolBar_invite_green.setVisibility(View.VISIBLE);
            toolBar_invite_black.setVisibility(View.GONE);

        } else if (theme.equals(context.getString(R.string.theme_black))) {
            toolBar_invite_normal.setVisibility(View.GONE);
            toolBar_invite_blue.setVisibility(View.GONE);
            toolBar_invite_brown.setVisibility(View.GONE);
            toolBar_invite_pink.setVisibility(View.GONE);
            toolBar_invite_green.setVisibility(View.GONE);
            toolBar_invite_black.setVisibility(View.VISIBLE);

        }

        relLayout_finish_normal.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupExplorer.class);
            intent.putExtra("from_create_new_group", "from_create_new_group");
            startActivity(intent);
            finish();
        });

        relLayout_finish_blue.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupExplorer.class);
            intent.putExtra("from_create_new_group", "from_create_new_group");
            startActivity(intent);
            finish();
        });

        relLayout_finish_brown.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupExplorer.class);
            intent.putExtra("from_create_new_group", "from_create_new_group");
            startActivity(intent);
            finish();
        });

        relLayout_finish_pink.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupExplorer.class);
            intent.putExtra("from_create_new_group", "from_create_new_group");
            startActivity(intent);
            finish();
        });

        relLayout_finish_green.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupExplorer.class);
            intent.putExtra("from_create_new_group", "from_create_new_group");
            startActivity(intent);
            finish();
        });

        relLayout_finish_black.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupExplorer.class);
            intent.putExtra("from_create_new_group", "from_create_new_group");
            startActivity(intent);
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

    private void clearAll(){
        if(user_idList != null){
            user_idList.clear();
        }
        if(userList != null){
            userList.clear();
        }

        if(adapter_invite_members != null){
            adapter_invite_members = null;
        }

        if(recyclerView_invite_members != null){
            handler.post(() -> recyclerView_invite_members.setAdapter(null));
        }

        user_idList = new ArrayList<>();
        userList = new ArrayList<>();
    }

    private void getFriends() {
        clearAll();
        // verify is users are friends
        Query myQuery = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_friend_follower))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    FriendsFollowerFollowing follower = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                    user_idList.add(follower.getUser_id());
                }
                getFollowers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_followers))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String userID = ds.getKey();
                    user_idList.add(userID);
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsers() {
        for (int i = 0; i < user_idList.size(); i++) {
            final int count = i;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query1 = ref
                    .child(context.getString(R.string.dbname_users))
                    .orderByKey()
                    .equalTo(user_idList.get(i));
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dts: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dts.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, dts);

                        userList.add(user);
                    }


                    if(count >= user_idList.size() -1){
                        Collections.reverse(userList);

                        adapter_invite_members = new InviteMembersSuggestionAdapter(context, userList, newGroupKey, theme, null,
                                null, relLayout_view_overlay);
                        recyclerView_invite_members.setAdapter(adapter_invite_members);

                        edit_search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                int size = charSequence.length();
                                if (size != 0)
                                    erase.setVisibility(View.VISIBLE);
                                else
                                    erase.setVisibility(View.GONE);
                                // filter recycler view when text is changed
                                adapter_invite_members.getFilter().filter(charSequence.toString().toLowerCase());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        erase.setOnClickListener(view -> {
                            Objects.requireNonNull(edit_search.getText()).clear();
                            edit_search.requestFocus();
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**
     * fetches json by making http calls
     */
    private void fetchUsers() {
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(URL,
                response -> {
                    if (response == null) {
                        Toast.makeText(context, "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<User> items = new Gson().fromJson(response.toString(), new TypeToken<List<User>>() {
                    }.getType());

                    // adding contacts to contacts list
                    userList.clear();
                    userList.addAll(items);

                    // refreshing recycler view
                    adapter_invite_members.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.d(TAG, "fetchUsers: "+ error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
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