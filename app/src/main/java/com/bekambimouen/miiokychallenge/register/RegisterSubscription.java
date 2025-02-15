package com.bekambimouen.miiokychallenge.register;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Spanned;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.text.HtmlCompat;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class RegisterSubscription extends AppCompatActivity {
    private static final String TAG = "RegisterSubscription";

    // widgets
    private RelativeLayout main;
    private ProgressBar progressBar;
    private TextView tv_pleaseWait;
    private AppCompatRadioButton radio_public, radio_private;

    // vars
    private RegisterSubscription context;
    private String user_id;
    private String scope;
    private String birth_day, phoneNumber, full_name, town_name, neighborhood_name, native_country_name,
            hometown_name;

    private boolean isTermsOfUseVisible = false;

    // firebase
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_register_subscription);context = this;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        try {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            full_name = getIntent().getStringExtra("full_name");
            birth_day = getIntent().getStringExtra("birth_day");
            town_name = getIntent().getStringExtra("town_name");
            neighborhood_name = getIntent().getStringExtra("neighborhood_name");
            native_country_name = getIntent().getStringExtra("native_country_name");
            hometown_name = getIntent().getStringExtra("hometown_name");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        main = findViewById(R.id.main);
        progressBar = findViewById(R.id.progressBar);
        tv_pleaseWait = findViewById(R.id.pleaseWait);
        radio_public = findViewById(R.id.radio_public);
        radio_private = findViewById(R.id.radio_private);
        TextView tv_subscribe_ass = findViewById(R.id.tv_subscribe_ass);
        TextView btn_terms_of_use = findViewById(R.id.btn_terms_of_use);
        Button btn_next = findViewById(R.id.btn_next);
        RelativeLayout relLayout_term_of_use = findViewById(R.id.relLayout_term_of_use);
        RelativeLayout arrowBack_terms_of_use = findViewById(R.id.arrowBack_terms_of_use);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);

        String msg = tv_subscribe_ass.getText().toString();
        String username = full_name.toLowerCase().replace(" ", ".");
        Spanned spanned = HtmlCompat.fromHtml(msg+" "+username, HtmlCompat.FROM_HTML_MODE_LEGACY);
        tv_subscribe_ass.setText(spanned);

        btn_terms_of_use.setOnClickListener(view -> {
            isTermsOfUseVisible = true;
            relLayout_term_of_use.setVisibility(View.VISIBLE);
        });

        arrowBack_terms_of_use.setOnClickListener(view -> {
            isTermsOfUseVisible = false;
            relLayout_term_of_use.setVisibility(View.GONE);
        });

        radio_public.setOnClickListener(view1 -> {
            radio_private.setChecked(false);
            scope = context.getString(R.string.title_public);
        });

        radio_private.setOnClickListener(view1 -> {
            radio_public.setChecked(false);
            scope = context.getString(R.string.title_private);
        });

        btn_next.setOnClickListener(view1 -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

            if (isConnected) {
                if (user_id != null) {
                    if (TextUtils.isEmpty(scope))
                        scope = context.getString(R.string.title_public);

                    btn_next.setEnabled(false);
                    closeKeyboard();
                    progressBar.setVisibility(View.VISIBLE);
                    tv_pleaseWait.setVisibility(View.VISIBLE);

                    Date date=new Date();
                    HashMap<String, Object> map_complet = new HashMap<>();
                    map_complet.put("username", username);
                    map_complet.put("bio", "");
                    map_complet.put("profileUrl", "");

                    myRef.child(getString(R.string.dbname_complet_profile))
                            .child(user_id)
                            .setValue(map_complet);

                    HashMap<String, Object> map_account = new HashMap<>();
                    map_account.put("user_id", user_id);
                    map_account.put("username", username);
                    map_account.put("fullName", full_name);
                    map_account.put("profileUrl", "");
                    map_account.put("full_profileUrl", "");
                    map_account.put("photo_id", "");
                    map_account.put("bio", "");
                    map_account.put("status", context.getString(R.string.hi_follow_me_on_miioky));

                    myRef.child(getString(R.string.dbname_user_account_settings))
                            .child(user_id)
                            .updateChildren(map_account);


                    HashMap<String, Object> map_users = new HashMap<>();
                    map_users.put("phoneNumber", phoneNumber);
                    map_users.put("user_id", user_id);
                    map_users.put("username", username);
                    map_users.put("fullName", full_name);
                    map_users.put("birthDay", birth_day);
                    map_users.put("profileUrl", "");
                    map_users.put("full_profileUrl", "");
                    map_users.put("photo_id", "");
                    map_users.put("bio", "");
                    map_users.put("status", context.getString(R.string.hi_follow_me_on_miioky));
                    map_users.put("marital_status", "");
                    map_users.put("gender", "");
                    map_users.put("native_country_name", native_country_name);
                    map_users.put("hometown_name", hometown_name);
                    map_users.put("live_country_name", GetCountryName());
                    map_users.put("town_name", town_name);
                    map_users.put("neighborhood_name", neighborhood_name);
                    map_users.put("scope", scope);
                    map_users.put("date_created", date.getTime());
                    map_users.put("views", 0);
                    map_users.put("verified", false);
                    map_users.put("isSelected", false);
                    map_users.put("dontSuggestAnymore", "");

                    myRef.child(getString(R.string.dbname_users))
                            .child(user_id)
                            .updateChildren(map_users)
                            .addOnSuccessListener(unused -> {
                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(context, HomeActivity.class);
                                startActivity(intent);
                                context.finish();

                            }).addOnFailureListener(e -> Toast.makeText(context, "error: "+e, Toast.LENGTH_SHORT).show());

                }

            } else {
                closeKeyboard();
            }
        });

        arrowBack.setOnClickListener(view1 -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isTermsOfUseVisible) {
                    isTermsOfUseVisible = false;
                    relLayout_term_of_use.setVisibility(View.GONE);

                } else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    // country name corresponding to country name
    public String GetCountryName(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(",");
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return CountryZipCode;
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