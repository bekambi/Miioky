package com.bekambimouen.miiokychallenge.market_place.search;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.NumberTextWatcher;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.HRArrayAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MarketSearchStore extends AppCompatActivity {
    // widgets
    private AutoCompleteTextView edit_search, edit_neighborhood, edit_price_min, edit_price_max;
    private ImageView erase, search_id_white;
    private RelativeLayout parent, relLayout_price_max, relLayout_view_overlay;

    // vars
    private MarketSearchStore context;
    private List<String> suggestion_list, suggestion_search_list;
    private boolean isFilterVisible = false, isFirstTime_max_price = false;

    // firebase
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_market_search_store);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        suggestion_list = new ArrayList<>();
        suggestion_search_list = new ArrayList<>();

        // to show keyboard
        InputMethodManager imgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        init();
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
        RelativeLayout relLayout_filter = findViewById(R.id.relLayout_filter);
        relLayout_price_max = findViewById(R.id.relLayout_price_max);
        LinearLayout linLayout_filter = findViewById(R.id.linLayout_filter);
        erase = findViewById(R.id.erase);
        search_id_white = findViewById(R.id.search_id_white);
        search_id_white.setEnabled(false);
        TextView neighborhood = findViewById(R.id.neighborhood);
        TextView min = findViewById(R.id.min_price);
        TextView max = findViewById(R.id.max_price);
        edit_neighborhood = findViewById(R.id.edit_neighborhood);
        edit_search = findViewById(R.id.edit_search);
        edit_search.requestFocus();
        edit_price_min = findViewById(R.id.edit_price_min);
        edit_price_max = findViewById(R.id.edit_price_max);

        // show/hide filter
        relLayout_filter.setOnClickListener(view -> {
            isFilterVisible = !isFilterVisible;

            if (isFilterVisible)
                linLayout_filter.setVisibility(View.VISIBLE);
            else {
                edit_neighborhood.getText().clear();
                Objects.requireNonNull(edit_price_min.getText()).clear();
                Objects.requireNonNull(edit_price_max.getText()).clear();
                linLayout_filter.setVisibility(View.GONE);
            }
        });

        // price format
        formatPrice();

        edit_neighborhood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int txt_length = charSequence.length();

                if (txt_length == 0)
                    neighborhood.setTextColor(context.getColor(R.color.caption_color));
                else
                    neighborhood.setTextColor(context.getColor(R.color.shinning_blue));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_price_min.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int txt_length = charSequence.length();

                if (txt_length == 0)
                    min.setTextColor(context.getColor(R.color.caption_color));
                else
                    min.setTextColor(context.getColor(R.color.shinning_blue));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_price_max.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int txt_length = charSequence.length();

                if (txt_length == 0)
                    max.setTextColor(context.getColor(R.color.caption_color));
                else
                    max.setTextColor(context.getColor(R.color.shinning_blue));

                if (isFirstTime_max_price) {
                    isFirstTime_max_price = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_price_max.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int size = charSequence.length();
                if (size != 0) {
                    erase.setVisibility(View.VISIBLE);
                    search_id_white.setEnabled(true);
                }
                else {
                    erase.setVisibility(View.GONE);
                    search_id_white.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        erase.setOnClickListener(view -> {
            edit_search.getText().clear();
            edit_search.requestFocus();
            showKeyboard();
        });

        // autoCompleteTextView search bar
        final HRArrayAdapter<String> autoComplete_search = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_user_stores_media))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        suggestion_search_list.clear();
                        autoComplete_search.clear();
                        edit_search.setAdapter(null);

                        // get the name of store
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            // get the suggestion by childing the key of the string you want to get.
                            String store_name = ds.child(context.getString(R.string.field_store_name)).getValue(String.class);

                            // add the retrieving String to the list
                            if (!suggestion_search_list.contains(store_name))
                                suggestion_search_list.add(store_name);
                        }

                        // get the product informations
                        myRef.child(context.getString(R.string.dbname_market_media))
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                            // get the suggestion by childing the key of the string you want to get.
                                            String product_name = dataSnapshot.child(context.getString(R.string.field_product_name)).getValue(String.class);
                                            String category = dataSnapshot.child(context.getString(R.string.field_product_category)).getValue(String.class);
                                            String location_category = dataSnapshot.child(context.getString(R.string.field_location_category)).getValue(String.class);

                                            if (!suggestion_search_list.contains(product_name))
                                                suggestion_search_list.add(product_name);

                                            if (!suggestion_search_list.contains(category))
                                                suggestion_search_list.add(category);

                                            if (!suggestion_search_list.contains(location_category))
                                                suggestion_search_list.add(location_category);
                                        }

                                        for (int i = 0; i < suggestion_search_list.size(); i++) {
                                            autoComplete_search.add(suggestion_search_list.get(i));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        edit_search.setAdapter(autoComplete_search);
                        // number of typing charSequence before suggestion appear
                        edit_search.setThreshold(1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // autoCompleteTextView neighborhood
        final HRArrayAdapter<String> autoComplete = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_market_media))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        suggestion_list.clear();
                        autoComplete.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            // get the suggestion by childing the key of the string you want to get.
                            String suggestion_neighborhood = dataSnapshot.child(context.getString(R.string.field_neighborhood_name)).getValue(String.class);
                            // add the retrieving String to the list
                            if (!suggestion_list.contains(suggestion_neighborhood))
                                suggestion_list.add(suggestion_neighborhood);
                        }

                        for (int i = 0; i < suggestion_list.size(); i++) {
                            autoComplete.add(suggestion_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_neighborhood.setAdapter(autoComplete);
        // number of typing charSequence before suggestion appear
        edit_neighborhood.setThreshold(1);

        // apply listener on autocomplete item
        /*edit_search.setOnItemClickListener((adapterView, view, i, l) -> {
            // search
            performSearch();
        });*/


        // search clicking on keyboard entry
        edit_search.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // to get ation seach only on keyboard
        edit_neighborhood.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        search_id_white.setOnClickListener(view -> {
            // search
            performSearch();
        });

        edit_price_min.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        edit_price_max.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        arrowBack.setOnClickListener(view -> {
            closeKeyboard();
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeKeyboard();
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    private void performSearch() {
        String search_item = edit_search.getText().toString().trim();
        String neighborhood_name = edit_neighborhood.getText().toString().trim();
        String minPrice = Objects.requireNonNull(edit_price_min.getText()).toString().replace(",", "").trim();
        String maxPrice = Objects.requireNonNull(edit_price_max.getText()).toString().replace(",", "").trim();

        // case where string is empty
        if (TextUtils.isEmpty(minPrice))
            minPrice = "0";

        if (TextUtils.isEmpty(maxPrice))
            maxPrice = "0";

        // convert to int
        int min_price = Integer.parseInt(minPrice);
        int max_price = Integer.parseInt(maxPrice);

        if (min_price != 0 && min_price >= max_price){
            isFirstTime_max_price = true;

            GradientDrawable drawable = (GradientDrawable) relLayout_price_max.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            return;
        }

        if (!TextUtils.isEmpty(search_item)) {
            closeKeyboard();
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, MarketResultSearch.class);
            intent.putExtra("search_item", search_item);
            intent.putExtra("neighborhood_name", neighborhood_name);
            intent.putExtra("min_price", min_price);
            intent.putExtra("max_price", max_price);
            startActivity(intent);
        }
    }

    // get country devise money
    private void formatPrice() {
        String CountryID;

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {

                // format text inside editText _____________________________________________________
                // old price
                Locale locale1 = new Locale("en", g[0]); // For example Argentina
                int numDecs1 = 2; // Let's use 2 decimals
                TextWatcher tw1 = new NumberTextWatcher(edit_price_min, locale1, numDecs1);
                edit_price_min.addTextChangedListener(tw1);

                // current price
                Locale locale = new Locale("en", g[0]); // For example Argentina
                int numDecs = 2; // Let's use 2 decimals
                TextWatcher tw = new NumberTextWatcher(edit_price_max, locale, numDecs);
                edit_price_max.addTextChangedListener(tw);
                // _________________________________________________________________________________
                break;
            }
        }
    }

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
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