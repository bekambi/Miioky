package com.bekambimouen.miiokychallenge.market_place.search;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.HRArrayAdapter;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.search.adapter.ResultSearchAdapter;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarketResultSearch extends AppCompatActivity {
    private static final String TAG = "MarketResultSearch";

    // widgets
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private AutoCompleteTextView edit_town_name, edit_neighborhood_name;
    private RelativeLayout parent, relLayout_town_name, relLayout_neighborhood_name, relLayout_view_overlay;
    private LinearLayout relLayout_no_result;

    // vars
    private MarketResultSearch context;
    private ResultSearchAdapter adapter;
    private ArrayList<MarketModel> list;
    private ArrayList<String> owner_store_list, store_id_list;
    private List<String> town_list, neighborhood_list;
    private String search_item_from, search_item, neighborhood_name;
    private int min_price, max_price;

    private Handler handler;
    private Dialog d;
    private boolean isFirstTime_stroke_town_name = false, isFirstTime_stroke_neighborhood_name = false;
    private String countryName, townName;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_market_result_search);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        town_list = new ArrayList<>();
        neighborhood_list = new ArrayList<>();

        try {
            search_item_from = getIntent().getStringExtra("search_item");
            search_item = Normalizer.normalize(search_item_from, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
            neighborhood_name = Normalizer.normalize(getIntent().getStringExtra("neighborhood_name"), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
            min_price = getIntent().getIntExtra("min_price", 0);
            max_price = getIntent().getIntExtra("max_price", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getStoreOwnerID();
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
        relLayout_no_result = findViewById(R.id.relLayout_no_result);
        TextView modify_the_parameters = findViewById(R.id.modify_the_parameters);
        progressBar = findViewById(R.id.progressBar);
        TextView toolBar_product_name = findViewById(R.id.toolBar_product_name);

        toolBar_product_name.setText(search_item_from);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 10, false));

        modify_the_parameters.setOnClickListener(view -> {
            Util.preventTwoClick(modify_the_parameters);
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_market_result_search_modify_the_parameters, null);

            edit_town_name = v.findViewById(R.id.edit_town_name);
            edit_neighborhood_name = v.findViewById(R.id.edit_neighborhood_name);
            relLayout_town_name = v.findViewById(R.id.relLayout_town_name);
            RelativeLayout relLayout_update_town_name = v.findViewById(R.id.relLayout_update_town_name);
            relLayout_neighborhood_name = v.findViewById(R.id.relLayout_neighborhood_name);
            TextView update = v.findViewById(R.id.update);
            TextView cancel = v.findViewById(R.id.cancel);
            builder.setView(v);

            d = builder.create();
            d.show();
            relLayout_update_town_name.setVisibility(View.VISIBLE);
            edit_town_name.requestFocus();

            // hide keyboard when dialog box is dismiss
            d.setOnDismissListener(dialogInterface -> {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            });

            cancel.setOnClickListener(view1 -> d.dismiss());

            if (d.isShowing())
                showKeyboard();
            setProfileWidgets();

            edit_town_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (isFirstTime_stroke_town_name) {
                        isFirstTime_stroke_town_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_town_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            // autoCompleteTextView search bar
            final HRArrayAdapter<String> autoComplete_town = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

            myRef.child(context.getString(R.string.dbname_users))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                            town_list.clear();
                            autoComplete_town.clear();
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                // get the suggestion by childing the key of the string you want to get.
                                String towName = dataSnapshot.child(context.getString(R.string.field_town_name)).getValue(String.class);

                                // add the retrieving String to the list
                                if (!town_list.contains(towName))
                                    town_list.add(towName);
                            }

                            for (int i = 0; i < town_list.size(); i++) {
                                autoComplete_town.add(town_list.get(i));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            edit_town_name.setAdapter(autoComplete_town);
            // number of typing charSequence before suggestion appear
            edit_town_name.setThreshold(1);

            // user neighborhood name ____________________________________________________________
            edit_neighborhood_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (isFirstTime_stroke_neighborhood_name) {
                        isFirstTime_stroke_neighborhood_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            // autoCompleteTextView to update neighborhood
            final HRArrayAdapter<String> autoComplete_neighborhood = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

            myRef.child(context.getString(R.string.dbname_users))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                            neighborhood_list.clear();
                            autoComplete_neighborhood.clear();
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                // get the suggestion by childing the key of the string you want to get.
                                String neighborhoodName = dataSnapshot.child(context.getString(R.string.field_neighborhood_name)).getValue(String.class);

                                // add the retrieving String to the list
                                if (!neighborhood_list.contains(neighborhoodName))
                                    neighborhood_list.add(neighborhoodName);
                            }

                            for (int i = 0; i < neighborhood_list.size(); i++) {
                                autoComplete_neighborhood.add(neighborhood_list.get(i));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            edit_neighborhood_name.setAdapter(autoComplete_neighborhood);
            // number of typing charSequence before suggestion appear
            edit_neighborhood_name.setThreshold(1);

            update.setOnClickListener(view1 -> {
                Log.d(TAG, "onClick: attempting to save changes.");
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {
                    String town = edit_town_name.getText().toString().trim();
                    String neighborhood = edit_neighborhood_name.getText().toString().trim();

                    saveProfileSettings(town, neighborhood);
                }
            });
        });

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
        if (owner_store_list != null) {
            owner_store_list.clear();
        }

        if (store_id_list != null) {
            store_id_list.clear();
        }

        if(list != null){
            list.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        owner_store_list = new ArrayList<>();
        store_id_list = new ArrayList<>();
        list = new ArrayList<>();
    }

    /**
     //     * RÃ©cupÃ©rer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     //     */

    private void getStoreOwnerID() {
        Log.d(TAG, "getFollowing: searching for following");
        clearAll();

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_stores));

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get all current store owner
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String owner_id = ds.getKey();

                    owner_store_list.add(owner_id);
                }

                // hide progressbar
                if (!snapshot.exists())
                    progressBar.setVisibility(View.GONE);

                getStoreIDList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoreIDList() {
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
                                .child(owner_store_list.get(i));

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
                                            store_id_list.add(storeID);
                                }

                                if(count >= owner_store_list.size() -1) {
                                    // to get the store
                                    for(int j = 0; j < owner_store_list.size(); j++){
                                        final int j_count = j;

                                        if (!store_id_list.isEmpty()) {
                                            for(int i = 0; i < store_id_list.size(); i++){
                                                final int count = i;
                                                Query myQuery = myRef
                                                        .child(context.getString(R.string.dbname_user_stores))
                                                        .child(owner_store_list.get(j))
                                                        .orderByChild(context.getString(R.string.field_store_id))
                                                        .equalTo(store_id_list.get(i));
                                                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                            assert objectMap != null;
                                                            MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                                            if (Normalizer.normalize(market_model.getStore_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                    .equalsIgnoreCase(search_item))
                                                                if ((Normalizer.normalize(market_model.getLive_country_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                        .equalsIgnoreCase(countryName)) &&
                                                                        (Normalizer.normalize(market_model.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                                        .equalsIgnoreCase(townName)))
                                                                    if (!market_model.isSuppressed())
                                                                        list.add(market_model);
                                                        }

                                                        if(count >= store_id_list.size() -1) {

                                                            if(j_count >= owner_store_list.size() -1) {
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
                                                                                store_id_list.add(user.getUser_id());
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
                                        } else {Query query = myRef
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
                                                            store_id_list.add(user.getUser_id());
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

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } else {
                    // to get the to_rent_out or selled object
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
                                    store_id_list.add(user.getUser_id());
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

        if (!store_id_list.isEmpty()) {
            for(int i = 0; i < store_id_list.size(); i++){
                final int count = i;
                Query query = myRef
                        .child(getString(R.string.dbname_market))
                        .child(store_id_list.get(i)) // store_id
                        .orderByChild(context.getString(R.string.field_store_id))
                        .equalTo(store_id_list.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                            if (
                                    Normalizer.normalize(market_model.getStore_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(search_item)
                                            || Normalizer.normalize(market_model.getProduct_category(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(search_item)
                                            || Normalizer.normalize(market_model.getLocation_category(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(search_item)
                                            || Normalizer.normalize(market_model.getProduct_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(search_item)
                            ) {

                                // verify if neighborhood is not empty
                                if (!TextUtils.isEmpty(neighborhood_name)) {
                                    if (Normalizer.normalize(market_model.getNeighborhood_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(neighborhood_name)) {
                                        if (max_price != 0) {
                                            if (min_price < Integer.parseInt(market_model.getPrice().replace(",", ""))
                                                    &&
                                                    Integer.parseInt(market_model.getPrice().replace(",", "")) <=  max_price) {
                                                if (!market_model.isSuppressed())
                                                    list.add(market_model);
                                            }

                                        } else {
                                            if (!market_model.isSuppressed())
                                                list.add(market_model);
                                        }
                                    }

                                } else {
                                    if (max_price != 0) {
                                        if (min_price < Integer.parseInt(market_model.getPrice().replace(",", ""))
                                                &&
                                                Integer.parseInt(market_model.getPrice().replace(",", "")) <=  max_price) {
                                            if (!market_model.isSuppressed())
                                                list.add(market_model);
                                        }

                                    } else {
                                        if (!market_model.isSuppressed())
                                            list.add(market_model);
                                    }
                                }

                            }
                        }

                        if(count >= store_id_list.size() -1){

                            displayPhotos();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else
            displayPhotos();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayPhotos() {
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        adapter = new ResultSearchAdapter(context, list, progressBar, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            progressBar.setVisibility(View.GONE);
            relLayout_no_result.setVisibility(View.VISIBLE);
        }
    }

    private void setProfileWidgets() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    edit_town_name.setText(user.getTown_name());
                    edit_town_name.setSelection(Objects.requireNonNull(edit_town_name.getText()).length());
                    edit_neighborhood_name.setText(user.getNeighborhood_name());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveProfileSettings(String townName, String neighborhood_name) {
        if (TextUtils.isEmpty(townName)) {
            isFirstTime_stroke_town_name = true;
            GradientDrawable drawable = (GradientDrawable)relLayout_town_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_town_name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(neighborhood_name)) {
            isFirstTime_stroke_neighborhood_name = true;
            GradientDrawable drawable = (GradientDrawable)relLayout_neighborhood_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_neighborhood_name.requestFocus();
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("town_name", townName);
        map.put("neighborhood_name", neighborhood_name);

        myRef.child(context.getString(R.string.dbname_users))
                .child(user_id)
                .updateChildren(map).addOnSuccessListener(unused -> {
                    closeKeyboard();
                    d.dismiss();
                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (d != null)
            d.dismiss();
    }
}