package com.bekambimouen.miiokychallenge.friends;

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
import android.widget.ProgressBar;
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
import com.bekambimouen.miiokychallenge.friends.adapter.SeeAllMyFriendsAdapter;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SeeAllMyFriends extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "SeeAllMyFriends";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText edit_search;
    private ImageView erase;
    private ProgressBar progressBar;
    private RelativeLayout parent, relLayout_no_friends, relLayout_view_overlay;

    // vars
    private SeeAllMyFriends context;
    private SeeAllMyFriendsAdapter adapter;
    private ArrayList<User> userList, pagination;
    private ArrayList<String> following_userIDList;
    private Handler handler;
    private String userID;
    private int resultsCount = 0;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_see_all_my_friends);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(Looper.getMainLooper());

        try {
            userID = getIntent().getStringExtra("userID");
        } catch (NullPointerException e) {
            android.util.Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout_no_friends = findViewById(R.id.relLayout_no_friends);
        progressBar = findViewById(R.id.progressBar);
        RelativeLayout relLayout_arrowback = findViewById(R.id.relLayout_arrowback);
        erase = findViewById(R.id.erase);
        edit_search = findViewById(R.id.edit_search);
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

        getData();

        erase.setOnClickListener(view1 -> {
            Objects.requireNonNull(edit_search.getText()).clear();
            edit_search.requestFocus();
            //showKeyboard();
        });

        fetchUsers();

        relLayout_arrowback.setOnClickListener(view1 -> {
            closeKeyboard();
            finish();
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

    private void clearAll(){
        if(userList != null){
            userList.clear();
        }
        if(following_userIDList != null){
            following_userIDList.clear();
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

        userList = new ArrayList<>();
        following_userIDList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    void getData() {
        clearAll();
        Query query1 = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(userID);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    following_userIDList.add(keyID);
                }

                if (!following_userIDList.isEmpty()) {
                    for(int i = 0; i < following_userIDList.size(); i++){
                        final int count = i;
                        Query query = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(following_userIDList.get(i));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        userList.add(user);
                                }

                                if(count >= following_userIDList.size() -1){
                                    display();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    relLayout_no_friends.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void display() {
        userList.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        int iterations = userList.size();

        if(iterations > 1){
            iterations = 1;
        }

        resultsCount = 1;
        for(int i = 0; i < iterations; i++){
            pagination.add(userList.get(i));
        }


        adapter = new SeeAllMyFriendsAdapter(context, pagination, progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);
    }

    public void displayMore() {
        android.util.Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(userList.size() > resultsCount && !userList.isEmpty()){

                int iterations;
                if(userList.size() > (resultsCount + 10)){
                    android.util.Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    android.util.Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = userList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(userList.get(i));
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

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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