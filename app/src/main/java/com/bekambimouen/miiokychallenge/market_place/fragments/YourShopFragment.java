package com.bekambimouen.miiokychallenge.market_place.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.adapter.YourStoresAdapter;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.model.StoreFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_StoreFollowersFollowing;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YourShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourShopFragment extends Fragment {
    // widgets
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_view_overlay;

    // vars
    private MainActivity context;
    private YourStoresAdapter adapter;
    private ArrayList<MarketModel> list;
    public ArrayList<String> followingUserList;
    public ArrayList<String> store_id_List;
    public ArrayList<String> followingUserIDList;
    public ArrayList<String> userIDList;
    private Handler handler;
    private boolean isFirstLoaded = true;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public YourShopFragment() {
        // Required empty public constructor
    }

    public static YourShopFragment newInstance() {
        YourShopFragment fragment = new YourShopFragment();
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
        View view = inflater.inflate(R.layout.fragment_your_shop, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        handler = new Handler(Looper.getMainLooper());
        init(view);

        return view;
    }

    private void init(View v) {
        progressBar = v.findViewById(R.id.progressBar);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);

        recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(list != null){
            list.clear();
        }
        if(followingUserList != null){
            followingUserList.clear();
        }
        if(store_id_List != null){
            store_id_List.clear();
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
        followingUserList = new ArrayList<>();
        followingUserIDList = new ArrayList<>();
        store_id_List = new ArrayList<>();
        userIDList = new ArrayList<>();
    }

    /**
     // * Récupérer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     // */
    private void getStoresFollowing() {
        clearAll();

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_store_following))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // first add current user id
                userIDList.add(user_id);

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    StoreFollowersFollowing following = Util_StoreFollowersFollowing.getStoreFollowersFollowing(context, objectMap);

                    // get store's owner
                    String owner = following.getStore_owner();
                    if (!userIDList.contains(owner)) {
                        userIDList.add(owner);
                    }
                }
                getUserIdList(userIDList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // add all other following stores
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_store_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    StoreFollowersFollowing following = Util_StoreFollowersFollowing.getStoreFollowersFollowing(context, objectMap);

                    String followingId = following.getStore_id();

                    store_id_List.add(followingId);
                }

                getStores(store_id_List);
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

    private void getStores(ArrayList<String> store_id_List) {
        if (!followingUserIDList.isEmpty()) {
            for(int i = 0; i < followingUserIDList.size(); i++){
                final int count_user_list = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_stores))
                        .child(followingUserIDList.get(i))
                        .orderByChild(context.getString(R.string.field_store_owner))
                        .equalTo(followingUserIDList.get(i));

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                            for (String groupKey : store_id_List) {
                                if (market_model.getStore_id().equals(groupKey))
                                    if (!market_model.isSuppressed())
                                        list.add(market_model);
                            }
                        }

                        if(count_user_list >= followingUserIDList.size() -1){

                            list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                    .compareTo(String.valueOf(o1.getDate_created())));

                            adapter = new YourStoresAdapter(context, list, progressBar, relLayout_view_overlay);
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

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            getStoresFollowing();
        }
    }
}