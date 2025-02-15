package com.bekambimouen.miiokychallenge.groups.invite_user_in_group.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.GroupInviteFromSameTown;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.adapter.InviteMembersSuggestionAdapter;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InvitationSuggestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitationSuggestionsFragment extends Fragment implements OnLoadMoreItemsListener {
    private static final String TAG = "InvitationSuggestionsFragment";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private TextView empty_list;
    private RecyclerView recyclerView;
    private MyEditText edit_search;
    private ImageView erase;
    private ProgressBar loading_progressBar;
    private RelativeLayout relLayout_empty_list, relLayout_view_overlay;

    // vars
    private GroupInviteFromSameTown context;
    private InviteMembersSuggestionAdapter adapter;
    private UserGroups user_groups;
    private ArrayList<User> userList, pagination;
    private ArrayList<String> followers_id_List, member_list, tampon_list;
    private Handler handler;
    private int resultsCount = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public InvitationSuggestionsFragment() {
        // Required empty public constructor
    }

    public static InvitationSuggestionsFragment newInstance() {
        InvitationSuggestionsFragment fragment = new InvitationSuggestionsFragment();
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
        View view = inflater.inflate(R.layout.fragment_invitation_suggestions, container, false);
        context = (GroupInviteFromSameTown) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            user_groups = context.getUserGroups();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: "+e.getMessage());
        }

        init(view);
        getFriendsFollower();
        fetchUsers();

        adapter = new InviteMembersSuggestionAdapter(context, pagination, "", "",
                user_groups, loading_progressBar, relLayout_view_overlay);

        return view;
    }

    private void init(View v) {
        loading_progressBar = v.findViewById(R.id.loading_progressBar);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);
        empty_list = v.findViewById(R.id.empty_list);
        relLayout_empty_list = v.findViewById(R.id.relLayout_empty_list);
        erase = v.findViewById(R.id.erase);
        edit_search = v.findViewById(R.id.edit_search);
        recyclerView = v.findViewById(R.id.recyclerView);
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(pagination != null){
            pagination.clear();
        }
        if(member_list != null){
            member_list.clear();
        }
        if(tampon_list != null){
            tampon_list.clear();
        }
        if(userList != null){
            userList.clear();
        }
        if(followers_id_List != null){
            followers_id_List.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        pagination = new ArrayList<>();
        member_list = new ArrayList<>();
        tampon_list = new ArrayList<>();
        userList = new ArrayList<>();
        followers_id_List = new ArrayList<>();
    }

    private void getFriendsFollower() {
        clearAll();
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_friend_follower))
                .child(user_id);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    FriendsFollowerFollowing follower = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                    followers_id_List.add(follower.getUser_id());
                }

                getFollowers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        // get the list of my followers on miioky
        Query query = myRef
                .child(context.getString(R.string.dbname_followers))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String userID = ds.getKey();
                    followers_id_List.add(userID);
                }

                Query query = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            String keyID = ds.getKey();

                            // get the list of members in group
                            member_list.add(keyID);
                        }

                        for (int i = 0; i < followers_id_List.size(); i++) {
                            if (!member_list.contains(followers_id_List.get(i)))
                                tampon_list.add(followers_id_List.get(i));
                        }

                        if (!tampon_list.isEmpty()) {
                            for (int i = 0; i < tampon_list.size(); i++) {
                                final int count = i;
                                Query query1 = myRef
                                        .child(context.getString(R.string.dbname_users))
                                        .orderByKey()
                                        .equalTo(tampon_list.get(i));
                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                            assert objectMap != null;
                                            User user = Util_User.getUser(context, objectMap, ds);

                                            // remove the admin master
                                            if (!user.getUser_id().equals(user_groups.getAdmin_master()))
                                                userList.add(user);
                                        }

                                        if (count >= tampon_list.size()-1) {
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

                                            if (adapter.getItemCount() == 0) {
                                                relLayout_empty_list.setVisibility(View.VISIBLE);
                                                empty_list.setText(context.getString(R.string.the_list_is_empty));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        } else {
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

                            if (adapter.getItemCount() == 0) {
                                relLayout_empty_list.setVisibility(View.VISIBLE);
                                empty_list.setText(context.getString(R.string.the_list_is_empty));
                            }
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

    private void displayMorePhotos() {
        android.util.Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(userList.size() > resultsCount && !userList.isEmpty()){

                int iterations;
                if(userList.size() > (resultsCount + 3)){
                    android.util.Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 3;
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

                    List<User> items = new Gson().fromJson(response.toString(), new TypeToken<List<User>>() {
                    }.getType());

                    // adding contacts to contacts list
                    userList.clear();
                    userList.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.e(TAG, "Error: " + error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);
    }
}