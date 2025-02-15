package com.bekambimouen.miiokychallenge.market_place.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bekambimouen.miiokychallenge.InformationEvent;
import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.adapter.MarketExplorerAdapter;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMarketImages;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketRecommendationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketRecommendationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnLoadMoreItemsListener {
    private static final String TAG = "GroupExplorerFragment";

    // widgets
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView be_the_first_to_sell;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_first_seller, relLayout_view_overlay;

    // vars
    private MainActivity context;
    private MarketExplorerAdapter adapter;
    private GridLayoutManager layoutManager;
    private ArrayList<MarketModel> following_list, suggestions_list, final_list, resultat_list, pagination;
    private ArrayList<String> owner_store_list, following_stores_id_list, suggestion_stores_id_list, stores_id_list,
            user_id_list, filter_id_list;
    private Handler handler;
    private String countryName, townName, from_publish;
    private int resultsCount = 0;
    private boolean loading = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public MarketRecommendationsFragment() {
        // Required empty public constructor
    }

    public static MarketRecommendationsFragment newInstance() {
        MarketRecommendationsFragment fragment = new MarketRecommendationsFragment();
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
        View view = inflater.inflate(R.layout.fragment_market_recommendations, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            from_publish = context.getFrom_publish();
        } catch (NumberFormatException e) {
            Log.d(TAG, "onCreateView: "+e.getMessage());
        }

        init(view);

        return view;
    }
    private void init(View v) {
        progressBar = v.findViewById(R.id.progressBar);
        be_the_first_to_sell = v.findViewById(R.id.be_the_first_to_sell);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);
        relLayout_first_seller = v.findViewById(R.id.relLayout_first_seller);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);

        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(context, 2);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 5, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if (owner_store_list != null) {
            owner_store_list.clear();
        }

        if (stores_id_list != null) {
            stores_id_list.clear();
        }

        if (filter_id_list != null) {
            filter_id_list.clear();
        }

        if (following_stores_id_list != null) {
            following_stores_id_list.clear();
        }

        if (suggestion_stores_id_list != null) {
            suggestion_stores_id_list.clear();
        }

        if (user_id_list != null) {
            user_id_list.clear();
        }

        if(following_list != null){
            following_list.clear();
        }

        if(suggestions_list != null){
            suggestions_list.clear();
        }

        if(final_list != null){
            final_list.clear();
        }

        if(resultat_list != null){
            resultat_list.clear();
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

        owner_store_list = new ArrayList<>();
        stores_id_list = new ArrayList<>();
        following_stores_id_list = new ArrayList<>();
        suggestion_stores_id_list = new ArrayList<>();
        user_id_list = new ArrayList<>();
        following_list = new ArrayList<>();
        suggestions_list = new ArrayList<>();
        final_list = new ArrayList<>();
        filter_id_list = new ArrayList<>();
        resultat_list = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getUserFollowingStoreID() {
        clearAll();

        // get all store the current user follow
        Query query = myRef
                .child(context.getString(R.string.dbname_store_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String followingId = ds.getKey();

                    following_stores_id_list.add(followingId);
                }

                getStoreOwnerID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoreOwnerID() {
        // get all current store owner
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_stores));

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String owner_id = dataSnapshot.getKey();

                    owner_store_list.add(owner_id);
                }

                getStoreIDList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoreIDList() {
        // displaying store in function of user's town
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    countryName = Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                    townName = Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                }

                if (!owner_store_list.isEmpty()) {

                    for(int i = 0; i < owner_store_list.size(); i++){
                        final int count = i;
                        Query myQuery = myRef
                                .child(context.getString(R.string.dbname_user_stores))
                                .child(owner_store_list.get(i))
                                .orderByChild(context.getString(R.string.field_store_owner))
                                .equalTo(owner_store_list.get(i));

                        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                    assert objectMap != null;
                                    MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                                    // get the store ID
                                    String storeID = dataSnapshot.getKey();

                                    if ((Normalizer.normalize(market_model.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(countryName)) &&
                                            (Normalizer.normalize(market_model.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                    .equalsIgnoreCase(townName)))
                                        if (!market_model.isSuppressed())
                                            suggestion_stores_id_list.add(storeID);
                                }

                                if(count >= owner_store_list.size() -1) {

                                    if (!following_stores_id_list.isEmpty()) {
                                        for(int j = 0; j < following_stores_id_list.size(); j++){
                                            for(int i = 0; i < suggestion_stores_id_list.size(); i++){

                                                // here we remove the stores followed by user
                                                if (!suggestion_stores_id_list.get(i).equals(following_stores_id_list.get(j)))
                                                    stores_id_list.add(suggestion_stores_id_list.get(i));
                                            }
                                        }
                                    } else {
                                        stores_id_list.addAll(suggestion_stores_id_list);
                                    }

                                    // get the store
                                    if (!stores_id_list.isEmpty() || !following_stores_id_list.isEmpty()) {
                                        // first get the following stores
                                        if (!following_stores_id_list.isEmpty()) {
                                            for(int j = 0; j < owner_store_list.size(); j++){
                                                final int count_j = j;

                                                for(int i = 0; i < following_stores_id_list.size(); i++){
                                                    final int count = i;
                                                    Query myQuery = myRef
                                                            .child(context.getString(R.string.dbname_user_stores))
                                                            .child(owner_store_list.get(j))
                                                            .orderByChild(context.getString(R.string.field_store_id))
                                                            .equalTo(following_stores_id_list.get(i));
                                                    myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                assert objectMap != null;
                                                                MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                                                if (!market_model.isSuppressed())
                                                                    following_list.add(market_model);
                                                            }
                                                            if(count >= following_stores_id_list.size() -1) {

                                                                if(count_j >= owner_store_list.size() -1) {
                                                                    // add the following stores list to the final list
                                                                    following_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                                                            .compareTo(String.valueOf(o1.getDate_created())));

                                                                    final_list.addAll(following_list);

                                                                    // add the suggestions stores
                                                                    for(int j = 0; j < owner_store_list.size(); j++){
                                                                        final int count_j = j;

                                                                        if (!stores_id_list.isEmpty()) {
                                                                            for(int i = 0; i < stores_id_list.size(); i++){
                                                                                final int count = i;
                                                                                Query myQuery = myRef
                                                                                        .child(context.getString(R.string.dbname_user_stores))
                                                                                        .child(owner_store_list.get(j))
                                                                                        .orderByChild(context.getString(R.string.field_store_id))
                                                                                        .equalTo(stores_id_list.get(i));
                                                                                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                                            assert objectMap != null;
                                                                                            MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                                                                            if (!market_model.isSuppressed())
                                                                                                suggestions_list.add(market_model);
                                                                                        }

                                                                                        if(count >= stores_id_list.size() -1) {

                                                                                            if(count_j >= owner_store_list.size() -1) {
                                                                                                // to get the sell and to_rent_out object
                                                                                                Query query = myRef
                                                                                                        .child(context.getString(R.string.dbname_users));
                                                                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                                                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                                                            assert objectMap != null;
                                                                                                            User user = Util_User.getUser(context, objectMap, ds);

                                                                                                            // to get the location object
                                                                                                            if ((Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                                                    .equalsIgnoreCase(countryName)) &&
                                                                                                                    (Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                                                            .equalsIgnoreCase(townName)))
                                                                                                                user_id_list.add(user.getUser_id());
                                                                                                        }

                                                                                                        getMarketPosts();
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });
                                                                            }

                                                                        } else if (!following_stores_id_list.isEmpty()) {
                                                                            for(int i = 0; i < following_stores_id_list.size(); i++){
                                                                                final int count = i;
                                                                                Query myQuery = myRef
                                                                                        .child(context.getString(R.string.dbname_user_stores))
                                                                                        .child(owner_store_list.get(j))
                                                                                        .orderByChild(context.getString(R.string.field_store_id))
                                                                                        .equalTo(following_stores_id_list.get(i));
                                                                                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                                            assert objectMap != null;
                                                                                            MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                                                                            if (!market_model.isSuppressed())
                                                                                                suggestions_list.add(market_model);
                                                                                        }

                                                                                        if(count >= following_stores_id_list.size() -1) {

                                                                                            if(count_j >= owner_store_list.size() -1) {
                                                                                                // to get the sell and to_rent_out object
                                                                                                Query query = myRef
                                                                                                        .child(context.getString(R.string.dbname_users));
                                                                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                                                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                                                            assert objectMap != null;
                                                                                                            User user = Util_User.getUser(context, objectMap, ds);

                                                                                                            // to get the location object
                                                                                                            if ((Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                                                    .equalsIgnoreCase(countryName)) &&
                                                                                                                    (Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                                                            .equalsIgnoreCase(townName)))
                                                                                                                user_id_list.add(user.getUser_id());
                                                                                                        }

                                                                                                        getMarketPosts();
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        } else {

                                            for(int j = 0; j < owner_store_list.size(); j++){
                                                final int count_j = j;

                                                for(int i = 0; i < stores_id_list.size(); i++){
                                                    final int count = i;
                                                    Query myQuery = myRef
                                                            .child(context.getString(R.string.dbname_user_stores))
                                                            .child(owner_store_list.get(j))
                                                            .orderByChild(context.getString(R.string.field_store_id))
                                                            .equalTo(stores_id_list.get(i));
                                                    myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                assert objectMap != null;
                                                                MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                                                if (!market_model.isSuppressed())
                                                                    final_list.add(market_model);
                                                            }

                                                            if(count >= stores_id_list.size() -1) {

                                                                if(count_j >= owner_store_list.size() -1) {
                                                                    // to get the sell and to_rent_out object
                                                                    Query query = myRef
                                                                            .child(context.getString(R.string.dbname_users));
                                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                                assert objectMap != null;
                                                                                User user = Util_User.getUser(context, objectMap, ds);

                                                                                // to get the location object
                                                                                if ((Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                        .equalsIgnoreCase(countryName)) &&
                                                                                        (Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                                .equalsIgnoreCase(townName)))
                                                                                    user_id_list.add(user.getUser_id());
                                                                            }

                                                                            getMarketPosts();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                    } else {
                                        // add the suggestions stores if user not following any store
                                        for(int j = 0; j < owner_store_list.size(); j++){
                                            final int count_j = j;

                                            if (!suggestion_stores_id_list.isEmpty()) {
                                                for(int i = 0; i < suggestion_stores_id_list.size(); i++){
                                                    final int count = i;
                                                    Query myQuery = myRef
                                                            .child(context.getString(R.string.dbname_user_stores))
                                                            .child(owner_store_list.get(j))
                                                            .orderByChild(context.getString(R.string.field_store_id))
                                                            .equalTo(suggestion_stores_id_list.get(i));
                                                    myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                assert objectMap != null;
                                                                MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                                                if (!market_model.isSuppressed())
                                                                    suggestions_list.add(market_model);
                                                            }

                                                            if(count >= suggestion_stores_id_list.size() -1) {

                                                                if(count_j >= owner_store_list.size() -1) {
                                                                    // to get the sell and to_rent_out object
                                                                    Query query = myRef
                                                                            .child(context.getString(R.string.dbname_users));
                                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            for (DataSnapshot ds: snapshot.getChildren()) {
                                                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                                                assert objectMap != null;
                                                                                User user = Util_User.getUser(context, objectMap, ds);

                                                                                // to get the location object
                                                                                if ((Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                        .equalsIgnoreCase(countryName)) &&
                                                                                        (Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                                                .equalsIgnoreCase(townName)))
                                                                                    user_id_list.add(user.getUser_id());
                                                                            }

                                                                            getMarketPosts();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                            } else {
                                                Query query = myRef
                                                        .child(context.getString(R.string.dbname_users));
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                            assert objectMap != null;
                                                            User user = Util_User.getUser(context, objectMap, ds);

                                                            // to get the location object
                                                            if ((Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                    .equalsIgnoreCase(countryName)) &&
                                                                    (Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                            .equalsIgnoreCase(townName)))
                                                                user_id_list.add(user.getUser_id());

                                                        }

                                                        getMarketPosts();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } else {
                    // to get the to_rent_out object
                    Query query = myRef
                            .child(context.getString(R.string.dbname_users));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                // to get the location object
                                if ((Normalizer.normalize(user.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                        .equalsIgnoreCase(countryName)) &&
                                        (Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                .equalsIgnoreCase(townName)))
                                    user_id_list.add(user.getUser_id());
                            }

                            getMarketPosts();
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

    private void getMarketPosts() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        for(int i = 0; i < user_id_list.size(); i++){
            final int count = i;
            Query query = myRef
                    .child(context.getString(R.string.dbname_market))
                    .child(user_id_list.get(i)) // user_owner_id
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(user_id_list.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        if (!market_model.isSuppressed())
                            suggestions_list.add(market_model);
                    }

                    if(count >= user_id_list.size() -1){
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void displayPhotos() {
        // add the suggestions_list stores list to the final list
        suggestions_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        final_list.addAll(suggestions_list);

        // to prevent repetition -- if we use store id, user_id will be select one time
        for (int i = 0; i < final_list.size(); i++) {
            // to add store in the list
            if (!TextUtils.isEmpty(final_list.get(i).getStore_profile_photo_id())
                    && (final_list.get(i).isStore() || final_list.get(i).isRestaurant() || final_list.get(i).isBakery())) {
                if (!filter_id_list.contains(final_list.get(i).getStore_profile_photo_id())) {
                    resultat_list.add(final_list.get(i));
                    filter_id_list.add(final_list.get(i).getStore_profile_photo_id());
                }
            }

            // to add product in the list
            if (!TextUtils.isEmpty(final_list.get(i).getPhotoi_id()) && (final_list.get(i).isSell() || final_list.get(i).isLocation())) {
                if (!filter_id_list.contains(final_list.get(i).getPhotoi_id())) {
                    resultat_list.add(final_list.get(i));
                    filter_id_list.add(final_list.get(i).getPhotoi_id());
                }
            }
        }

        int iterations = resultat_list.size();

        if(iterations > 5){
            iterations = 5;
        }

        resultsCount = 0;
        for(int i = 0; i < iterations; i++){
            pagination.add(resultat_list.get(i));
            resultsCount++;
        }

        adapter = new MarketExplorerAdapter(context, pagination, progressBar, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreItemsListener(this);

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
                            if (startIndex >= resultat_list.size()) {
                                startIndex = resultat_list.size();
                                preloadCount = 0; // Nothing to preload
                            } else if (startIndex + preloadCount > resultat_list.size()) {
                                preloadCount = resultat_list.size() - startIndex; // Adjust preloadCount
                            }

                            // Preload images
                            for(int i = startIndex; i < startIndex + preloadCount; i++) {
                                PreloadMarketImages.getPreloadMarketImages(context, resultat_list.get(i));
                            }

                            loading = false;
                        }
                    }
                });
            }
        });

        if (adapter.getItemCount() == 0) {
            progressBar.setVisibility(View.GONE);
            relLayout_first_seller.setVisibility(View.VISIBLE);

            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByKey()
                    .equalTo(user_id);
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        be_the_first_to_sell.setText(Html.fromHtml(context.getString(R.string.be_the_first_to_sell)+" <b>"
                                +user.getTown_name()+"</b>"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            progressBar.setVisibility(View.GONE);
            relLayout_first_seller.setVisibility(View.GONE);
        }
    }

    public void displayMore() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(resultat_list.size() > resultsCount && !resultat_list.isEmpty()){

                int iterations;
                if(resultat_list.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = resultat_list.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(resultat_list.get(i));
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
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        // publications
        getUserFollowingStoreID();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(InformationEvent event) {
        boolean isFirstLoaded = event.getInfo();

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            getUserFollowingStoreID();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        if (from_publish != null) {
            getUserFollowingStoreID();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}