package com.bekambimouen.miiokychallenge.register;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.yesterselga.countrypicker.CountryPicker;
import com.yesterselga.countrypicker.Theme;

import java.util.Objects;

public class RegisterTownAndQuarter extends AppCompatActivity {
    private static final String TAG = "RegisterTownAndQuarter";

    // widgets
    private MyEditText edit_town_name, edit_neighborhood_name, edit_hometown_name;
    private TextView txt_native_country_name;
    private CountryPicker picker;
    private RelativeLayout main, relLayout_town_name, relLayout_neighborhood_name, relLayout_hometown_name,
            relLayout_native_country_name;

    // vars
    private RegisterTownAndQuarter context;
    private String full_name;
    private String phoneNumber;
    private boolean isEmptyPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_register_town_and_quarter);
        context = this;

        try {
            full_name = getIntent().getStringExtra("full_name");
            phoneNumber = getIntent().getStringExtra("phoneNumber");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        main = findViewById(R.id.main);
        relLayout_town_name = findViewById(R.id.relLayout_town_name);
        relLayout_neighborhood_name = findViewById(R.id.relLayout_neighborhood_name);
        relLayout_native_country_name = findViewById(R.id.relLayout_native_country_name);
        relLayout_hometown_name = findViewById(R.id.relLayout_hometown_name);
        edit_town_name = findViewById(R.id.edit_town_name);
        edit_neighborhood_name = findViewById(R.id.edit_neighborhood_name);
        txt_native_country_name = findViewById(R.id.txt_native_country_name);
        ImageView countryIcon = findViewById(R.id.countryIcon);
        edit_hometown_name = findViewById(R.id.edit_hometown_name);
        RelativeLayout relLayout_next = findViewById(R.id.relLayout_next);

        // country picker
        picker = CountryPicker.newInstance(context.getString(R.string.select_country), Theme.LIGHT);  // Set Dialog Title and Theme
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {

            txt_native_country_name.setText(name);
            countryIcon.setImageResource(flagDrawableResID);

            picker.dismiss();

        });

        txt_native_country_name.setOnClickListener(view -> {
            if (picker == null || !picker.isAdded()) {
                assert picker != null;
                picker.show(context.getSupportFragmentManager(), "COUNTRY_PICKER");
            }

            if (isEmptyPhoneNumber) {
                isEmptyPhoneNumber = false;

                GradientDrawable drawable = (GradientDrawable) relLayout_native_country_name.getBackground();
                drawable.mutate();
                drawable.setStroke(1, context.getColor(R.color.grey));
            }
        });

        edit_town_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyPhoneNumber) {
                    isEmptyPhoneNumber = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_town_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_neighborhood_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyPhoneNumber) {
                    isEmptyPhoneNumber = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_hometown_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyPhoneNumber) {
                    isEmptyPhoneNumber = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_hometown_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        relLayout_next.setOnClickListener(view -> getUserLocation());

        // keyboard done button
        edit_hometown_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                getUserLocation();
            }
            return false;
        });

        arrowBack.setOnClickListener(view1 -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void getUserLocation() {
        closeKeyboard();
        String town_name = Objects.requireNonNull(edit_town_name.getText()).toString().trim();
        String neighborhood_name = Objects.requireNonNull(edit_neighborhood_name.getText()).toString().trim();
        String hometown_name = Objects.requireNonNull(edit_hometown_name.getText()).toString().trim();
        String native_country_name = Objects.requireNonNull(txt_native_country_name.getText()).toString().trim();

        if (TextUtils.isEmpty(town_name) || town_name.length() < 3) {
            isEmptyPhoneNumber = true;
            GradientDrawable drawable = (GradientDrawable)relLayout_town_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_town_name.requestFocus();

        } else if (TextUtils.isEmpty(neighborhood_name) || neighborhood_name.length() < 3) {
            isEmptyPhoneNumber = true;
            GradientDrawable drawable = (GradientDrawable)relLayout_neighborhood_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_neighborhood_name.requestFocus();

        } else if (native_country_name.equals(context.getString(R.string.your_native_country))) {
            isEmptyPhoneNumber = true;
            GradientDrawable drawable = (GradientDrawable)relLayout_native_country_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_neighborhood_name.requestFocus();

        } else if (TextUtils.isEmpty(hometown_name) || hometown_name.length() < 3) {
            isEmptyPhoneNumber = true;
            GradientDrawable drawable = (GradientDrawable)relLayout_hometown_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_hometown_name.requestFocus();

        } else {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, RegisterBirthDay.class);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("full_name", full_name);
            intent.putExtra("town_name", town_name);
            intent.putExtra("neighborhood_name", neighborhood_name);
            intent.putExtra("native_country_name", native_country_name);
            intent.putExtra("hometown_name", hometown_name);
            startActivity(intent);
        }
    }

    private void closeKeyboard() {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckInternetStatus.internetIsConnected(context, main);
    }
}