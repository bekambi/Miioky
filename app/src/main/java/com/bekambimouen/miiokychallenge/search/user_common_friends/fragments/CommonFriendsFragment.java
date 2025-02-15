package com.bekambimouen.miiokychallenge.search.user_common_friends.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.friends.adapter.SeeAllMyFriendsAdapter;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.interfaces.RemoveItemListener;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.user_common_friends.SeeUserFriends;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommonFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommonFriendsFragment extends Fragment implements OnLoadMoreItemsListener {
    private static final String TAG = "CommonFriendsFragment";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText edit_search;
    private ImageView erase;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_no_suggestions, relLayout_view_overlay;

    // vars
    private SeeUserFriends context;
    private SeeAllMyFriendsAdapter adapter;
    private Handler handler;
    private String userID;
    private boolean isFirstLoaded = true;

    // vars friends
    private List<String> current_user_friends_list;
    private List<String> friend_user_friends_list;
    private ArrayList<User> friends_profile_list, pagination;
    private List<String> common_friends_id_list;
    private int resultsCount = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public CommonFriendsFragment() {
        // Required empty public constructor
    }

    public static CommonFriendsFragment newInstance() {
        CommonFriendsFragment fragment = new CommonFriendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_common_friends, container, false);
        context = (SeeUserFriends) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            userID = context.getUserID();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: "+e.getMessage());
        }

        relLayout_view_overlay = view.findViewById(R.id.relLayout_view_overlay);
        progressBar = view.findViewById(R.id.progressBar);
        relLayout_no_suggestions = view.findViewById(R.id.relLayout_no_suggestions);
        erase = view.findViewById(R.id.erase);
        edit_search = view.findViewById(R.id.edit_search);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        erase.setOnClickListener(view1 -> {
            Objects.requireNonNull(edit_search.getText()).clear();
            edit_search.requestFocus();
        });

        return view;
    }

    private void clearAll(){
        if(current_user_friends_list != null){
            current_user_friends_list.clear();
        }
        if(friend_user_friends_list != null){
            friend_user_friends_list.clear();
        }
        if(common_friends_id_list != null){
            common_friends_id_list.clear();
        }
        if(friends_profile_list != null){
            friends_profile_list.clear();
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

        current_user_friends_list = new ArrayList<>();
        friend_user_friends_list = new ArrayList<>();
        common_friends_id_list = new ArrayList<>();
        friends_profile_list = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    void getData() {
        clearAll();
        // get the common friends
        Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String keyID = dataSnapshot.getKey();

                    current_user_friends_list.add(keyID);
                }

                // get the friend user friend list
                Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                        .child(userID);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            String keyID = dataSnapshot.getKey();

                            friend_user_friends_list.add(keyID);
                        }

                        for (String ID: current_user_friends_list) {
                            for (int i = 0; i < friend_user_friends_list.size(); i++) {
                                if (ID.equals(friend_user_friends_list.get(i))) {
                                    common_friends_id_list.add(ID);
                                }
                            }
                        }

                        if (!common_friends_id_list.isEmpty()) {
                            for(int i = 0; i < common_friends_id_list.size(); i++){
                                final int count = i;

                                // get all comon friends
                                Query query = myRef.child(context.getString(R.string.dbname_users))
                                        .orderByChild(context.getString(R.string.field_user_id))
                                        .equalTo(common_friends_id_list.get(i));

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                            assert objectMap != null;
                                            User user = Util_User.getUser(context, objectMap, ds);
                                            // get the friends profile
                                            friends_profile_list.add(user);
                                        }

                                        if(count >= common_friends_id_list.size() -1){
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
                            relLayout_no_suggestions.setVisibility(View.VISIBLE);
                        }
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

    private void display() {
        int iterations = friends_profile_list.size();

        if(iterations > 1){
            iterations = 1;
        }

        resultsCount = 1;
        for(int i = 0; i < iterations; i++){
            pagination.add(friends_profile_list.get(i));
        }

        //get user follwing
        adapter = new SeeAllMyFriendsAdapter(context, pagination, progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);

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
    }

    public void displayMore() {
        android.util.Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(friends_profile_list.size() > resultsCount && !friends_profile_list.isEmpty()){

                int iterations;
                if(friends_profile_list.size() > (resultsCount + 10)){
                    android.util.Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    android.util.Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = friends_profile_list.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(friends_profile_list.get(i));
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
                    friends_profile_list.clear();
                    friends_profile_list.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.d(TAG, "fetchUsers: "+ error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            getData();
            fetchUsers();
        }
    }
}