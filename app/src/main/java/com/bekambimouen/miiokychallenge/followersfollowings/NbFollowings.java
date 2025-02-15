package com.bekambimouen.miiokychallenge.followersfollowings;

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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.followersfollowings.adapter.FollowersAdapter;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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

public class NbFollowings extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "NbFollowings";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private MyEditText edit_search;
    private ImageView erase;
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_empty_list, relLayout_view_overlay;

    // vars
    private NbFollowings context;
    private FollowersAdapter adapter;
    private User user;
    private ArrayList<String> following_userIDList, friends_userIDList, final_userIDList;
    private ArrayList<User> userList, pagination;
    private Handler handler;
    private int resultsCount = 0;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_nb_followings);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: NullPointerException: "+e.getMessage());
        }

        init();
        getData();
        fetchUsers();

        adapter = new FollowersAdapter(context, pagination, this, relLayout_view_overlay);
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
        relLayout_empty_list = findViewById(R.id.relLayout_empty_list);
        ImageView arrowBack = findViewById(R.id.arrowBack);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        erase = findViewById(R.id.erase);
        edit_search = findViewById(R.id.edit_search);

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

        erase.setOnClickListener(view -> {
            Objects.requireNonNull(edit_search.getText()).clear();
            edit_search.requestFocus();
            //showKeyboard();
        });

        arrowBack.setOnClickListener(view -> {
            // hide keyboard if is visible
            closeKeyboard();
            finish();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(userList != null){
            userList.clear();
        }
        if(following_userIDList != null){
            following_userIDList.clear();
        }
        if(friends_userIDList != null){
            friends_userIDList.clear();
        }
        if(final_userIDList != null){
            final_userIDList.clear();
        }

        if(pagination != null){
            pagination.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        following_userIDList = new ArrayList<>();
        friends_userIDList = new ArrayList<>();
        final_userIDList = new ArrayList<>();
        userList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getData() {
        clearAll();
        Query query = myRef
                .child(context.getString(R.string.dbname_following))
                .child(user.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String userID = ds.getKey();
                    following_userIDList.add(userID);
                }

                // to remove suggestion when they are already friend
                Query query1 = myRef
                        .child(context.getString(R.string.dbname_friend_following))
                        .child(user.getUser_id());
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String keyID = ds.getKey();
                            friends_userIDList.add(keyID);
                        }

                        if (!following_userIDList.isEmpty() && !friends_userIDList.isEmpty()) {
                            for (String friendID: friends_userIDList) {
                                for (int i = 0; i < following_userIDList.size(); i++) {
                                    if (!following_userIDList.get(i).equals(friendID))
                                        if (!final_userIDList.contains(following_userIDList.get(i)))
                                            final_userIDList.add(following_userIDList.get(i));
                                }
                            }
                        } else if (!following_userIDList.isEmpty()) {
                            final_userIDList.addAll(following_userIDList);

                        } else if (!friends_userIDList.isEmpty()) {
                            final_userIDList.addAll(following_userIDList);
                        }

                        for (int i = 0; i < final_userIDList.size(); i++) {
                            final int count = i;
                            Query query2 = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(final_userIDList.get(i));
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dts: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dts.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, dts);

                                        userList.add(user);
                                    }
                                    if(count >= final_userIDList.size() -1){

                                        int iterations = userList.size();

                                        if(iterations > 10){
                                            iterations = 10;
                                        }

                                        resultsCount = 10;
                                        for(int i = 0; i < iterations; i++){
                                            pagination.add(userList.get(i));
                                        }

                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        // the list is empty
                        if (final_userIDList.isEmpty())
                            relLayout_empty_list.setVisibility(View.VISIBLE);
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

    public void displayMore() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(userList.size() > resultsCount && !userList.isEmpty()){

                int iterations;
                if(userList.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = userList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(userList.get(i));
                }

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }

    @Override
    public void onLoadMoreItems() {
        displayMore();
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

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}