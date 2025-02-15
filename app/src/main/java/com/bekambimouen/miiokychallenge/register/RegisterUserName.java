package com.bekambimouen.miiokychallenge.register;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterUserName extends AppCompatActivity {
    private static final String TAG = "RegisterUserName";

    // widgets
    private LinearLayout main;
    private RelativeLayout rel_name, rel_phone;
    private TextView country_code;
    private EditText edit_name, edit_phone_number;

    // vars
    private RegisterUserName context;
    private String register_name;
    private boolean isEmptyPhoneNumber;

    // firebase
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_register_user_name);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        try {
            // coming from Display_insta
            register_name = getIntent().getStringExtra("register_name");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        // first load phone number in case where user leave the app here
        setupFirebaseAuth();

        main = findViewById(R.id.main);
        rel_name = findViewById(R.id.rel_name);
        rel_phone = findViewById(R.id.rel_phone);
        edit_name = findViewById(R.id.edit_name);
        edit_phone_number = findViewById(R.id.edit_phone_number);
        country_code = findViewById(R.id.tv_country_code);
        TextView complet_profil = findViewById(R.id.complet_profil);
        if (register_name != null)
            complet_profil.setVisibility(View.VISIBLE);
        Button btn_name = findViewById(R.id.btn_next);

        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyPhoneNumber) {
                    isEmptyPhoneNumber = false;

                    GradientDrawable drawable = (GradientDrawable) rel_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_name.setOnClickListener(view1 -> getUserName());

        // keyboard done button
        edit_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                getUserName();
            }
            return false;
        });

        // the country phone code
        Spanned spanned = HtmlCompat.fromHtml("+"+GetCountryZipCode(), HtmlCompat.FROM_HTML_MODE_LEGACY);
        country_code.setText(spanned);

        edit_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyPhoneNumber) {
                    isEmptyPhoneNumber = false;

                    GradientDrawable drawable = (GradientDrawable) rel_phone.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // active keyboard done button
        edit_phone_number.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                getUserName();
            }
            return false;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (register_name != null) {
                    context.finishAffinity();
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

    private void getUserName() {
        boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

        if (!isConnected) {
            CheckInternetStatus.internetIsConnected(context, main);;
        } else {
            // name
            String regexStr_name = "^(?=[a-zA-ZÀ-ÿ'0-9._ ]{3,20}$)(?!.*[_. ]{2})[^_. ].*[^_. ]$";
            String full_name = edit_name.getText().toString().trim();

            // phone number
            String regexStr_phone = "^[0-9]{7,20}$";
            String phone_number = edit_phone_number.getText().toString().trim().replace(" ", "");
            String code = country_code.getText().toString();
            String phoneNumber = code+phone_number;

            if (TextUtils.isEmpty(full_name) || full_name.length() < 3)  {
                isEmptyPhoneNumber = true;
                GradientDrawable drawable = (GradientDrawable)rel_name.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                rel_name.requestFocus();

            } else if (TextUtils.isEmpty(phone_number)){
                isEmptyPhoneNumber = true;
                GradientDrawable drawable = (GradientDrawable)rel_phone.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_phone_number.requestFocus();

            } else if (!TextUtils.isEmpty(phone_number)) {
                // formatted phone number
                //String formattedPhone = PhoneNumberUtils.formatNumber(phone_number, Locale.getDefault().getCountry());

                if (phone_number.matches(regexStr_phone)) {
                    closeKeyboard();
                    StringBuilder formatted_phone = new StringBuilder();
                    for (int i = 0; i < phone_number.length(); i++) {
                        if (i % 3 == 0 && i != 0) {
                            formatted_phone.append(" ");
                        }
                        formatted_phone.append(phone_number.charAt(i));
                    }

                    edit_phone_number.setText(formatted_phone);
                    edit_phone_number.clearFocus();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_verification_phone_item, null);
                    TextView tv_phone_number = view1.findViewById(R.id.tv_phone_number);
                    TextView tv_update = view1.findViewById(R.id.tv_update);
                    TextView tv_yes = view1.findViewById(R.id.tv_yes);
                    builder.setView(view1);

                    Dialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();

                    String number = code+" "+formatted_phone;
                    tv_phone_number.setText(number);

                    tv_update.setOnClickListener(view2 -> {
                        dialog.dismiss();
                        edit_phone_number.requestFocus();
                        edit_phone_number.setSelection(edit_phone_number.length());
                    });
                    tv_yes.setOnClickListener(view2 -> {
                        dialog.dismiss();

                        if ((!TextUtils.isEmpty(full_name) && full_name.matches(regexStr_name))) {

                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            Intent intent = new Intent(context, RegisterTownAndQuarter.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("full_name", full_name);
                            startActivity(intent);

                        }
                    });

                } else {
                    isEmptyPhoneNumber = true;
                    GradientDrawable drawable = (GradientDrawable)rel_phone.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    edit_phone_number.requestFocus();
                    Toast.makeText(context, getString(R.string.this_number_is_not_valid),
                            Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    // country name corresponding to country code
    public String GetCountryZipCode(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (String s : rl) {
            String[] g = s.split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    /**
     * checks to see if the @param 'user' is logged in
     */
    private void checkCurrentUser(FirebaseUser user){
        if(user != null){
            Query query = myRef.child(getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user2 = Util_User.getUser(context, objectMap, ds);
                        String fullName = user2.getFullName();

                        // in case user disconnected and reconnected
                        if ((!TextUtils.isEmpty(fullName) || fullName != null) ) {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //check if the user is logged in
            checkCurrentUser(user);
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
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