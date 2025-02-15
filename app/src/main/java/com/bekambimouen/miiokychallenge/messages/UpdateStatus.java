package com.bekambimouen.miiokychallenge.messages;

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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateStatus extends AppCompatActivity {
    private static final String TAG = "UpdateStatus";

    // widgets
    private RelativeLayout parent, relLayout1;
    private MyEditText edit_status;
    private ImageView done1;

    // vars
    private UpdateStatus context;
    private boolean isFirsTimeStroke_status = false;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseMethods mFirebaseMethods;
    private UserSettings mUserSettings;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_update_status);
        context = this;

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(this);
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        init();
        setupFirebaseAuth();
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
        relLayout1 = findViewById(R.id.relLayout1);
        edit_status = findViewById(R.id.edit_status);
        edit_status.requestFocus();

        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        ImageView done = findViewById(R.id.done);
        done1 = findViewById(R.id.done1);

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

                    edit_status.setText(user.getStatus());
                    edit_status.setSelection(Objects.requireNonNull(edit_status.getText()).length());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit_status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirsTimeStroke_status) {
                    isFirsTimeStroke_status = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout1.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
                    done1.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        done.setOnClickListener(view -> {
            Log.d(TAG, "onClick: attempting to save changes.");
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                closeKeyboard();
                saveProfileSettings();
            }
        });

        // keyboard done button
        edit_status.setOnEditorActionListener((v, actionId, event) -> {
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

    private void saveProfileSettings() {
        String status = Objects.requireNonNull(edit_status.getText()).toString();
        if (!TextUtils.isEmpty(status)) {
            if (!mUserSettings.getSettings().getStatus().equals(status)) {
                closeKeyboard();
                //update status
                mFirebaseMethods.updateUserAccountSettings(null, null, status, null);
                done1.setVisibility(View.VISIBLE);
                relLayout1.requestFocus();

            } else {
                isFirsTimeStroke_status = true;
                GradientDrawable drawable = (GradientDrawable)relLayout1.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                relLayout1.requestFocus();
            }

        } else {
            isFirsTimeStroke_status = true;
            GradientDrawable drawable = (GradientDrawable)relLayout1.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            relLayout1.requestFocus();
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setProfileWidgets(UserSettings userSettings) {
        mUserSettings = userSettings;

        UserAccountSettings settings = userSettings.getSettings();
        edit_status.setHint(settings.getStatus());

    }
    /*
        _________________________________________ Firebase _________________________________________
     */
    /**
     * setupt the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };

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
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}