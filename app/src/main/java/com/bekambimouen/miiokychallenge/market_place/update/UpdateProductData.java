package com.bekambimouen.miiokychallenge.market_place.update;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.NumberTextWatcher;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.HRArrayAdapter;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UpdateProductData extends AppCompatActivity {
    private static final String TAG = "UpdateProductData";
    // widgets _________________________________________
    private MyEditText edit_product_name, edit_old_price, edit_price, edit_description;
    private AutoCompleteTextView edit_neighborhood;
    private RelativeLayout parent, relLayout_neighborhood_section, relLayout_neighborhood, relLayout_product,
            relLayout_price;

    // vars
    private UpdateProductData context;
    private MarketModel market_model;
    private List<String> suggestion_list;
    private boolean isFirstTime_stroke_product_name = false, isFirstTime_stroke_price = false,
            isFirstTime_stroke_neighborhood = false;

    //firebase
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_update_product_data);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        suggestion_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_update = findViewById(R.id.relLayout_update);
        relLayout_neighborhood = findViewById(R.id.relLayout_neighborhood);
        relLayout_neighborhood_section = findViewById(R.id.relLayout_neighborhood_section);
        relLayout_product = findViewById(R.id.relLayout_product);
        relLayout_price = findViewById(R.id.relLayout_price);
        TextView country_devise1 = findViewById(R.id.country_devise1);
        TextView country_devise2 = findViewById(R.id.country_devise2);
        TextView tv_msg = findViewById(R.id.tv_msg);
        edit_neighborhood = findViewById(R.id.edit_neighborhood);
        edit_product_name = findViewById(R.id.edit_product_name);
        edit_old_price = findViewById(R.id.edit_old_price);
        edit_price = findViewById(R.id.edit_price);
        edit_description = findViewById(R.id.edit_description);

        edit_product_name.setText(market_model.getProduct_name());
        edit_price.setText(market_model.getPrice());
        edit_old_price.setText(market_model.getOld_price());
        edit_description.setText(market_model.getProduct_description());

        if (market_model.isLocation())
            tv_msg.setText(context.getString(R.string.a_detail_description_can_help_you_rent_faster));

        // neighborhood name
        if (market_model.isStore() || market_model.isBakery() || market_model.isRestaurant())
            relLayout_neighborhood_section.setVisibility(View.GONE);
        else
            edit_neighborhood.setText(market_model.getNeighborhood_name());

        // autoCompleteTextView neighborhood
        final HRArrayAdapter<String> autoComplete = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        suggestion_list.clear();
                        autoComplete.clear();
                        edit_neighborhood.setAdapter(null);
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

        edit_neighborhood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTime_stroke_neighborhood) {
                    isFirstTime_stroke_neighborhood = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_product_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTime_stroke_product_name) {
                    isFirstTime_stroke_product_name = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_product.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTime_stroke_price) {
                    isFirstTime_stroke_price = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_price.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // country devise
        country_devise1.setText(GetCountryDevise());
        country_devise2.setText(GetCountryDevise());

        // publish
        relLayout_update.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                closeKeyboard();

                String neighborhood_name = Objects.requireNonNull(edit_neighborhood.getText().toString().trim());
                String product_name = Objects.requireNonNull(requireNonNull(edit_product_name.getText()).toString().trim());
                String price = Objects.requireNonNull(requireNonNull(edit_price.getText()).toString());

                if (TextUtils.isEmpty(neighborhood_name)){
                    isFirstTime_stroke_neighborhood = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                if (TextUtils.isEmpty(product_name)){
                    isFirstTime_stroke_product_name = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_product.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                if (TextUtils.isEmpty(price)){
                    isFirstTime_stroke_price = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_price.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }
                // download data to firebase
                setUpdate();
            }
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

    // get country devise money
    private String GetCountryDevise() {
        String CountryID;
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = Currency.getInstance(new Locale("en", g[0])).getSymbol().toLowerCase();

                // format text inside editText _____________________________________________________
                // old price
                Locale locale = new Locale("en", g[0]);
                int numDecs = 2; // Let's use 2 decimals
                TextWatcher tw1 = new NumberTextWatcher(edit_old_price, locale, numDecs);
                edit_old_price.addTextChangedListener(tw1);

                // current price
                TextWatcher tw = new NumberTextWatcher(edit_price, locale, numDecs);
                edit_price.addTextChangedListener(tw);
                // _________________________________________________________________________________
                break;
            }
        }
        return CountryZipCode;
    }

    private void setUpdate() {
        String neighborhood_name = Objects.requireNonNull(edit_neighborhood.getText().toString().trim());
        String product_name = requireNonNull(edit_product_name.getText()).toString().trim();
        String price = requireNonNull(edit_price.getText()).toString();
        String old_price = requireNonNull(edit_old_price.getText()).toString();
        String product_description = requireNonNull(edit_description.getText()).toString();

        HashMap<String, Object> map = new HashMap<>();

        if (!market_model.isStore() || !market_model.isBakery() || !market_model.isRestaurant()) {
            if (!neighborhood_name.equals(market_model.getNeighborhood_name()))
                map.put("neighborhood_name", neighborhood_name);
        }
        if (!product_name.equals(market_model.getProduct_name()))
            map.put("product_name", product_name);
        if (!price.equals(market_model.getPrice()))
            map.put("price", price);
        if (!old_price.equals(market_model.getOld_price()))
            map.put("old_price", old_price);
        if (!product_description.equals(market_model.getProduct_description()))
            map.put("product_description", product_description);

        if (!map.isEmpty()) {
            map.put("devise", GetCountryDevise());

            myRef.child(getString(R.string.dbname_market))
                    .child(market_model.getStore_id())
                    .child(market_model.getPhotoi_id())
                    .updateChildren(map);

            myRef.child(getString(R.string.dbname_market_media))
                    .child(market_model.getPhotoi_id())
                    .updateChildren(map).addOnSuccessListener(unused -> finish());
        }
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
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}