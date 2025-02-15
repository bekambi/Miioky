package com.bekambimouen.miiokychallenge.profile;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CompletBioAndUsername extends AppCompatActivity {
    private static final String TAG = "CompletBioAndUsername";

    // widgets
    private RelativeLayout parent, relLayout1, relLayout2;
    private MyEditText edit_username, edit_bio;

    // vars
    private CompletBioAndUsername context;
    private UserSettings mUserSettings;
    private String bio, username;
    private boolean isFirsTimeStroke_bio = false, isFirsTimeStroke_username = false;

    // firebase
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_complet_bio_and_username);
        context = this;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(this);

        setupFirebaseAuth();

        try {
            bio = getIntent().getStringExtra("bio");
            username = getIntent().getStringExtra("username");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);
        edit_username = findViewById(R.id.edit_username);
        edit_bio = findViewById(R.id.edit_bio);
        TextView nber_char_sequence = findViewById(R.id.nber_char_sequence);
        LinearLayout linLayout_modify_bio = findViewById(R.id.linLayout_modify_bio);
        LinearLayout linLayout_modify_name = findViewById(R.id.linLayout_modify_name);
        TextView title = findViewById(R.id.title);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout done = findViewById(R.id.done);

        if (bio != null) {
            title.setText(getString(R.string.bio));
            linLayout_modify_bio.setVisibility(View.VISIBLE);

        } else if (username != null) {
            title.setText(getString(R.string.username));
            linLayout_modify_name.setVisibility(View.VISIBLE);
        }

        edit_bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_sequence.setText(String.valueOf(char_length));

                if (isFirsTimeStroke_bio) {
                    isFirsTimeStroke_bio = false;
                    GradientDrawable drawable = (GradientDrawable) relLayout1.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirsTimeStroke_username) {
                    isFirsTimeStroke_username = false;
                    GradientDrawable drawable = (GradientDrawable) relLayout2.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        done.setOnClickListener(view1 -> {
            Log.d(TAG, "onClick: attempting to save changes.");
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);
            } else {
                saveProfileSettings();
            }
        });

        // keyboard done button
        edit_username.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.d(TAG, "onClick: attempting to save changes.");
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);
                } else {
                    saveProfileSettings();
                }
            }
            return false;
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

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    /**
     * retrieve the data contained in the widgets and submit it to the database
     * Before doing so it check to make sure the username chosen is unique
     */
    private void saveProfileSettings(){
        if (bio != null) {
            final String bio = Objects.requireNonNull(edit_bio.getText()).toString();

            if (!TextUtils.isEmpty(bio) && bio.length() > 10) {
                if (!mUserSettings.getSettings().getBio().equals(bio)) {
                    closeKeyboard();
                    //update bio
                    mFirebaseMethods.updateUserAccountSettings(null, null, null, bio);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();

                } else {
                    isFirsTimeStroke_bio = true;
                    GradientDrawable drawable = (GradientDrawable)relLayout1.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    relLayout1.requestFocus();
                    edit_bio.setSelection(edit_bio.getText().length());
                }

            } else {
                isFirsTimeStroke_bio = true;
                GradientDrawable drawable = (GradientDrawable)relLayout1.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                relLayout1.requestFocus();
                edit_bio.setSelection(edit_bio.getText().length());
            }

        } else {
            String regexStr = "^(?=[a-zA-ZÀ-ÿ'0-9._ ]{3,20}$)(?!.*[_. ]{2})[^_. ].*[^_. ]$";
            final String username = Objects.requireNonNull(edit_username.getText()).toString().trim().toLowerCase().replace(" ", ".");

            //case1: if the user made a change to their username
            if (!TextUtils.isEmpty(username) && username.matches(regexStr)) {
                closeKeyboard();
                checkIfUsernameExists(username);

            } else {
                isFirsTimeStroke_username = true;
                GradientDrawable drawable = (GradientDrawable)relLayout2.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                relLayout2.requestFocus();
                edit_username.setSelection(edit_username.getText().length());
            }
        }
    }
    /**
     * check is @param username already exists in the database
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        isFirsTimeStroke_username = true;
                        GradientDrawable drawable = (GradientDrawable)relLayout2.getBackground();
                        drawable.mutate();
                        drawable.setStroke(3, Color.RED);
                        relLayout2.requestFocus();
                        edit_username.setSelection(Objects.requireNonNull(edit_username.getText()).length());

                        Toast.makeText(context, getString(R.string.name_already_exists), Toast.LENGTH_SHORT).show();
                    }
                }

                if(!dataSnapshot.exists()){
                    //update username
                    mFirebaseMethods.updateUserAccountSettings(username, null, null, null);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: "
                + userSettings.toString());
        mUserSettings = userSettings;

        UserAccountSettings settings = userSettings.getSettings();
        edit_username.setText(settings.getUsername());
        edit_bio.setText(settings.getBio());
    }
    /*
        _________________________________________ Firebase _________________________________________
     */
    /**
     * setupt the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}