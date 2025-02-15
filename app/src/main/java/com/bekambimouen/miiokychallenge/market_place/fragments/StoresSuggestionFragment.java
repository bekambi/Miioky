package com.bekambimouen.miiokychallenge.market_place.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.adapter.StoreSuggestionAdapter;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMarketImages;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoresSuggestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoresSuggestionFragment extends Fragment implements OnLoadMoreItemsListener {
    private static final String TAG = "GroupDiscoverFragment";

    // widgets
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_no_suggestions, relLayout_view_overlay;

    // vars
    private MainActivity context;
    private StoreSuggestionAdapter adapter;
    private GridLayoutManager layoutManager;
    private ArrayList<MarketModel> list_of_stores, final_list_of_stores, pagination;
    private ArrayList<String> following_store_id_list;
    public ArrayList<String> user_store_id_following_list;
    public ArrayList<String> owner_list, removed_suggestions_list;
    private Handler handler;
    private int resultsCount = 0;
    private boolean isFirstLoaded = true, loading = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;
    private String countryName, townName;

    public StoresSuggestionFragment() {
        // Required empty public constructor
    }

    public static StoresSuggestionFragment newInstance() {
        StoresSuggestionFragment fragment = new StoresSuggestionFragment();
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
        View view = inflater.inflate(R.layout.fragment_stores_suggestion, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());
        removed_suggestions_list = new ArrayList<>();

        init(view);

        return view;
    }

    private void init(View v) {
        progressBar = v.findViewById(R.id.progressBar);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);
        relLayout_no_suggestions = v.findViewById(R.id.relLayout_no_suggestions);

        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager=new GridLayoutManager(context,2);

        int spanCount = 2; // 3 columns
        int spacing = 10; // 50px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(list_of_stores != null){
            list_of_stores.clear();
        }
        if(final_list_of_stores != null){
            final_list_of_stores.clear();
        }
        if(pagination != null){
            pagination.clear();
        }
        if(following_store_id_list != null){
            following_store_id_list.clear();
        }
        if(user_store_id_following_list != null){
            user_store_id_following_list.clear();
        }
        if(owner_list != null){
            owner_list.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list_of_stores = new ArrayList<>();
        final_list_of_stores = new ArrayList<>();
        pagination = new ArrayList<>();
        following_store_id_list = new ArrayList<>();
        user_store_id_following_list = new ArrayList<>();
        owner_list = new ArrayList<>();
    }

    // stores that i follow
    private void getStoreFollowing() {
        clearAll();
        Query query1 = myRef.child(context.getString(R.string.dbname_store_following))
                .child(user_id);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String group_id = ds.getKey();
                    following_store_id_list.add(group_id);
                }

                // store id that i follow
                getStoreID(following_store_id_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // all stores
        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String userID = ds.getKey();

                    assert userID != null;
                    if (!userID.equals(user_id))
                        owner_list.add(userID);
                }

                getUserStores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get all stores
    private void getUserStores() {
        // displaying store in function of user's town
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    countryName = Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                    townName = Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                }

                if (!owner_list.isEmpty()) {
                    for(int i = 0; i < owner_list.size(); i++){
                        final int count = i;

                        Query query = myRef
                                .child(context.getString(R.string.dbname_user_stores))
                                .child(owner_list.get(i))
                                .orderByChild(context.getString(R.string.field_store_owner))
                                .equalTo(owner_list.get(i));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                    assert objectMap != null;
                                    MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                                    // get all groups
                                    if ((Normalizer.normalize(market_model.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(countryName)) &&
                                            (Normalizer.normalize(market_model.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                    .equalsIgnoreCase(townName)))
                                        if (!market_model.isSuppressed())
                                            list_of_stores.add(market_model);
                                }

                                if(count >= owner_list.size() -1){
                                    displaySuggestions();
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

    // get list of all followings
    private void getStoreID(ArrayList<String> following_store_id_list) {
        user_store_id_following_list.addAll(following_store_id_list);
    }

    private void displaySuggestions() {
        // to remove suggestion close by current user
        Query query = myRef.child(context.getString(R.string.dbname_remove_Suggestion))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    RemoveSuggestionModel suggestionModel = Util_RemoveSuggestionModel.getRemoveSuggestionModel(context, objectMap);
                    removed_suggestions_list.add(suggestionModel.getUser_id());
                }

                if (!user_store_id_following_list.isEmpty()) {
                    for (int i = 0; i < list_of_stores.size(); i++) {
                        // remove the store that current user is following
                        if (!user_store_id_following_list.contains(list_of_stores.get(i).getStore_id())) {
                            if (!removed_suggestions_list.contains(list_of_stores.get(i).getStore_id()))
                                final_list_of_stores.add(list_of_stores.get(i));
                        }
                    }
                } else {
                    // this is the place of tampon_list_of_stores
                    for (int i = 0; i < list_of_stores.size(); i++) {
                        if (!removed_suggestions_list.contains(list_of_stores.get(i).getStore_id()))
                            final_list_of_stores.add(list_of_stores.get(i));
                    }
                }

                final_list_of_stores.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                final_list_of_stores.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                disPlay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void disPlay() {
        int iterations = final_list_of_stores.size();

        if(iterations > 5){
            iterations = 5;
        }

        resultsCount = 0;
        for(int i = 0; i < iterations; i++){
            pagination.add(final_list_of_stores.get(i));
            resultsCount++;
        }

        adapter = new StoreSuggestionAdapter(context, pagination, progressBar, relLayout_view_overlay);
        layoutManager.setInitialPrefetchItemCount(20);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreItemsListener(this);

        if (adapter.getItemCount() == 0) {
            progressBar.setVisibility(View.GONE);
            relLayout_no_suggestions.setVisibility(View.VISIBLE);
        }

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
                            if (startIndex >= final_list_of_stores.size()) {
                                startIndex = final_list_of_stores.size();
                                preloadCount = 0; // Nothing to preload
                            } else if (startIndex + preloadCount > final_list_of_stores.size()) {
                                preloadCount = final_list_of_stores.size() - startIndex; // Adjust preloadCount
                            }

                            // Preload images
                            for(int i = startIndex; i < startIndex + preloadCount; i++) {
                                PreloadMarketImages.getPreloadStoreImages(context, final_list_of_stores.get(i));
                            }

                            loading = false;
                        }
                    }
                });
            }
        });
    }

    public void displayMore() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(final_list_of_stores.size() > resultsCount && !final_list_of_stores.isEmpty()){

                int iterations;
                if(final_list_of_stores.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = final_list_of_stores.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(final_list_of_stores.get(i));
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

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            getStoreFollowing();
        }
    }
}