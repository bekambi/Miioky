package com.bekambimouen.miiokychallenge.groups.parameters;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.parameters.adapter.GroupFollowingAdapter;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupsFollowing extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "GroupsFollowing";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private RecyclerView recyclerView;
    private ImageView erase;
    private ProgressBar loading_progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private GroupsFollowing context;
    private GroupFollowingAdapter adapter;
    private ArrayList<UserGroups> list, pagination;
    private ArrayList<String> group_id_List;
    private ArrayList<String> followingUserIDList;
    private ArrayList<String> userIDList;
    private Handler handler;
    private int resultsCount = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_groups_following);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        init();
        getGroupFollowing();

        fetchUsers();
        adapter = new GroupFollowingAdapter(context, pagination, loading_progressBar, relLayout_view_overlay);
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
        MyEditText edit_search = findViewById(R.id.edit_search);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        erase = findViewById(R.id.erase);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                adapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        erase.setOnClickListener(view -> Objects.requireNonNull(edit_search.getText()).clear());

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
        if(pagination != null){
            pagination.clear();
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
        pagination = new ArrayList<>();
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
                .child(context.getString(R.string.dbname_user_group));

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    userIDList.add(dataSnapshot.getKey());
                }
                getUserIdList(userIDList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query myQuery1 = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_id);

        myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get all current user groups
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String groupID = ds.getKey();
                    group_id_List.add(groupID);
                }

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

                            String followingId = ds.getKey();
                            if (!following.isBanFromGroup())
                                group_id_List.add(followingId);
                        }

                        getGroups(group_id_List);
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
                    @SuppressLint("NotifyDataSetChanged")
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

                            int iterations = list.size();

                            if(iterations > 20){
                                iterations = 20;
                            }

                            resultsCount = 20;
                            for(int i = 0; i < iterations; i++){
                                pagination.add(list.get(i));
                            }

                            adapter.notifyDataSetChanged();
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

    private void displayMorePhotos() {
        android.util.Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(list.size() > resultsCount && !list.isEmpty()){

                int iterations;
                if(list.size() > (resultsCount + 20)){
                    android.util.Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
                }else{
                    android.util.Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = list.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(list.get(i));
                }

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            android.util.Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            android.util.Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }

    @Override
    public void onLoadMoreItems() {
        loading_progressBar.setVisibility(View.VISIBLE);
        displayMorePhotos();
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

                    List<UserGroups> items = new Gson().fromJson(response.toString(), new TypeToken<List<UserGroups>>() {
                    }.getType());

                    // adding contacts to contacts list
                    list.clear();
                    list.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
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