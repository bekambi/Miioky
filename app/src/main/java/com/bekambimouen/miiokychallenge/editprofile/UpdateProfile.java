package com.bekambimouen.miiokychallenge.editprofile;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.HRArrayAdapter;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UpdateProfile extends AppCompatActivity {
    private static final String TAG = "UpdateProfile";

    // widgets
    private AutoCompleteTextView edit_town_name, edit_neighborhood_name, edit_add_college, edit_school_name,
            edit_add_establishment, edit_add_workplace, edit_establishment_name, edit_workplace_name;
    private MyEditText edit_username, edit_name, edit_bio,
            edit_add_college_header, edit_school_name_header,
            edit_add_establishment_header, edit_establishment_name_header,
            edit_add_workplace_header, edit_workplace_name_header;
    private RelativeLayout main, relLayout_username, relLayout_name, relLayout_bio, relLayout_town_name,
            relLayout_neighborhood_name, relLayout_button, relLayout_college, relLayout_establishment,
            relLayout_workplace, relLayout_school_name, relLayout_establishment_name, relLayout_workplace_name;
    private RadioButton radio_single, radio_married, radio_in_a_relationship_with, radio_other,
            radio_man, radio_women, radio_binary_name, radio_other_gender, radio_divorced, radio_separated;

    // vars
    private UpdateProfile context;
    private List<String> town_list, neighborhood_list, add_school_list, school_name_list, add_establishment_list, establishment_name_list,
            add_workplace_list, workplace_name_list;
    private String update_username, update_fullName, update_bio, update_town_name, update_neighborhood_name,
            update_relationship_status, update_gender, relationship, gender, add_school, add_establishment,
            add_workplace, update_school_name, schoolKey, school_name, school_name_header, update_establishment_name, establishmentKey,
            establishment_name, establishment_name_header, update_workplace_name, workplaceKey, workplace_name, workplace_name_header;

    private boolean isFirstTimeStroke_edit_town_name = false, isFirstTimeStroke_edit_neighborhood_name = false,
            isFirstTimeStroke_edit_add_college = false, isFirstTimeStroke_edit_school_name = false,
            isFirstTimeStroke_edit_add_establishment = false, isFirstTimeStroke_edit_add_workplace = false,
            isFirstTimeStroke_edit_establishment_name = false, isFirstTimeStroke_edit_workplace_name = false,
            isFirstTimeStroke_edit_username = false,
            isFirstTimeStroke_edit_name = false, isFirstTimeStroke_edit_bio = false;

    private int k = 0;

    // firebase
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_update_profile);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseMethods = new FirebaseMethods(this);
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        town_list = new ArrayList<>();
        neighborhood_list = new ArrayList<>();
        add_school_list = new ArrayList<>();
        school_name_list = new ArrayList<>();
        add_establishment_list = new ArrayList<>();
        establishment_name_list = new ArrayList<>();
        add_workplace_list = new ArrayList<>();
        workplace_name_list = new ArrayList<>();

        try {
            update_username = getIntent().getStringExtra("update_username");
            update_fullName = getIntent().getStringExtra("update_fullName");
            update_bio = getIntent().getStringExtra("update_bio");
            update_town_name = getIntent().getStringExtra("update_town_name");
            update_neighborhood_name = getIntent().getStringExtra("update_neighborhood_name");
            update_relationship_status = getIntent().getStringExtra("update_relationship_status");
            update_gender = getIntent().getStringExtra("update_gender");
            add_school = getIntent().getStringExtra("add_school");
            update_school_name = getIntent().getStringExtra("update_school_name");
            schoolKey = getIntent().getStringExtra("schoolKey");
            school_name = getIntent().getStringExtra("school_name");
            school_name_header = getIntent().getStringExtra("school_name_header");
            add_establishment = getIntent().getStringExtra("add_establishment");
            add_workplace = getIntent().getStringExtra("add_workplace");
            update_establishment_name = getIntent().getStringExtra("update_establishment_name");
            establishmentKey = getIntent().getStringExtra("establishmentKey");
            establishment_name = getIntent().getStringExtra("establishment_name");
            establishment_name_header = getIntent().getStringExtra("establishment_name_header");
            update_workplace_name = getIntent().getStringExtra("update_workplace_name");
            workplaceKey = getIntent().getStringExtra("workplaceKey");
            workplace_name = getIntent().getStringExtra("workplace_name");
            workplace_name_header = getIntent().getStringExtra("workplace_name_header");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        init();
        setProfileWidgets();
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
        main = findViewById(R.id.main);
        RelativeLayout backArrow = findViewById(R.id.backArrow);
        RelativeLayout relLayout_update_username = findViewById(R.id.relLayout_update_username);
        relLayout_username = findViewById(R.id.relLayout_username);
        RelativeLayout relLayout_update_name = findViewById(R.id.relLayout_update_name);
        relLayout_name = findViewById(R.id.relLayout_name);
        RelativeLayout relLayout_update_bio = findViewById(R.id.relLayout_update_bio);
        RelativeLayout relLayout_update_town_name = findViewById(R.id.relLayout_update_town_name);
        RelativeLayout relLayout_update_neighborhood_name = findViewById(R.id.relLayout_update_neighborhood_name);
        RelativeLayout relLayout_update_school_name = findViewById(R.id.relLayout_update_school_name);
        RelativeLayout relLayout_update_marital_status = findViewById(R.id.relLayout_update_marital_status);
        RelativeLayout relLayout_update_genter = findViewById(R.id.relLayout_update_genter);
        RelativeLayout relLayout_update_establishment_name = findViewById(R.id.relLayout_update_establishment_name);
        RelativeLayout relLayout_update_workplace_name = findViewById(R.id.relLayout_update_workplace_name);
        RelativeLayout relLayout_add_college = findViewById(R.id.relLayout_add_college);
        RelativeLayout relLayout_add_establishment = findViewById(R.id.relLayout_add_establishment);
        RelativeLayout relLayout_add_workplace = findViewById(R.id.relLayout_add_workplace);
        relLayout_bio = findViewById(R.id.relLayout_bio);
        relLayout_town_name = findViewById(R.id.relLayout_town_name);
        relLayout_neighborhood_name = findViewById(R.id.relLayout_neighborhood_name);
        relLayout_college = findViewById(R.id.relLayout_college);
        relLayout_establishment = findViewById(R.id.relLayout_establishment);
        relLayout_workplace = findViewById(R.id.relLayout_workplace);
        relLayout_school_name = findViewById(R.id.relLayout_school_name);
        relLayout_establishment_name = findViewById(R.id.relLayout_establishment_name);
        relLayout_workplace_name = findViewById(R.id.relLayout_workplace_name);
        relLayout_button = findViewById(R.id.relLayout_button);
        TextView button_text = findViewById(R.id.button_text);
        edit_username = findViewById(R.id.edit_username);
        edit_name = findViewById(R.id.edit_name);
        edit_bio = findViewById(R.id.edit_bio);
        edit_town_name = findViewById(R.id.edit_town_name);
        edit_neighborhood_name = findViewById(R.id.edit_neighborhood_name);
        edit_add_college = findViewById(R.id.edit_add_college);
        edit_add_college_header = findViewById(R.id.edit_add_college_header);
        edit_add_establishment = findViewById(R.id.edit_add_establishment);
        edit_add_establishment_header = findViewById(R.id.edit_add_establishment_header);
        edit_add_workplace = findViewById(R.id.edit_add_workplace);
        edit_add_workplace_header = findViewById(R.id.edit_add_workplace_header);
        edit_school_name = findViewById(R.id.edit_school_name);
        edit_school_name_header = findViewById(R.id.edit_school_name_header);
        edit_establishment_name = findViewById(R.id.edit_establishment_name);
        edit_establishment_name_header = findViewById(R.id.edit_establishment_name_header);
        edit_workplace_name = findViewById(R.id.edit_workplace_name);
        edit_workplace_name_header = findViewById(R.id.edit_workplace_name_header);
        TextView nber_char_sequence = findViewById(R.id.nber_char_sequence);
        TextView txt_update = findViewById(R.id.txt_update);
        RadioGroup radioGroup1 = findViewById(R.id.radioGroup);
        RadioGroup radioGroup_genter = findViewById(R.id.radioGroup_genter);
        radio_single = findViewById(R.id.radio_single);
        radio_married = findViewById(R.id.radio_married);
        radio_in_a_relationship_with = findViewById(R.id.radio_in_a_relationship_with);
        radio_other = findViewById(R.id.radio_other);
        radio_man = findViewById(R.id.radio_man);
        radio_women = findViewById(R.id.radio_women);
        radio_binary_name = findViewById(R.id.radio_binary_name);
        radio_other_gender = findViewById(R.id.radio_other_gender);
        radio_divorced = findViewById(R.id.radio_divorced);
        radio_separated = findViewById(R.id.radio_separated);

        // username
        if (update_username != null) {
            relLayout_update_username.setVisibility(View.VISIBLE);
            edit_username.requestFocus();
            showKeyboard();

            edit_username.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_username) {
                        isFirstTimeStroke_edit_username = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_username.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // user full name
        if (update_fullName != null) {
            relLayout_update_name.setVisibility(View.VISIBLE);
            edit_name.requestFocus();
            showKeyboard();

            edit_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_name) {
                        isFirstTimeStroke_edit_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // user bio
        if (update_bio != null) {
            relLayout_update_bio.setVisibility(View.VISIBLE);
            edit_bio.requestFocus();
            showKeyboard();

            edit_bio.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int char_length = charSequence.length();
                    nber_char_sequence.setText(String.valueOf(char_length));

                    if (isFirstTimeStroke_edit_bio) {
                        isFirstTimeStroke_edit_bio = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_bio.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // user town name
        if (update_town_name != null) {
            relLayout_update_town_name.setVisibility(View.VISIBLE);
            edit_town_name.requestFocus();
            showKeyboard();

            edit_town_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (isFirstTimeStroke_edit_town_name) {
                        isFirstTimeStroke_edit_town_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_town_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

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

        // user neighborhood name
        if (update_neighborhood_name != null) {
            relLayout_update_neighborhood_name.setVisibility(View.VISIBLE);
            edit_neighborhood_name.requestFocus();
            showKeyboard();

            edit_neighborhood_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_neighborhood_name) {
                        isFirstTimeStroke_edit_neighborhood_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // autoCompleteTextView neighborhood
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

        // marital status
        if (update_relationship_status != null) {
            relLayout_update_marital_status.setVisibility(View.VISIBLE);

            radioGroup1.setOnCheckedChangeListener((radioGroup, i) -> {
                int radioBtnID = radioGroup.getCheckedRadioButtonId();

                if(radioBtnID == R.id.radio_single){
                    relationship = context.getString(R.string.single);
                } else if(radioBtnID == R.id.radio_married){
                    relationship = context.getString(R.string.married);
                } else if(radioBtnID == R.id.radio_divorced){
                    relationship = context.getString(R.string.divorced);
                } else if(radioBtnID == R.id.radio_in_a_relationship_with){
                    relationship = context.getString(R.string.in_a_relationship_with);
                } else if(radioBtnID == R.id.radio_separated){
                    relationship = context.getString(R.string.separated);
                } else if(radioBtnID == R.id.radio_other){
                    relationship = context.getString(R.string.other);
                }
            });
        }

        // gender
        if (update_gender != null) {
            relLayout_update_genter.setVisibility(View.VISIBLE);

            radioGroup_genter.setOnCheckedChangeListener((radioGroup, i) -> {
                int radioBtnID = radioGroup.getCheckedRadioButtonId();

                if(radioBtnID == R.id.radio_man){
                    gender = context.getString(R.string.man);
                } else if(radioBtnID == R.id.radio_women){
                    gender = context.getString(R.string.women);
                } else if(radioBtnID == R.id.radio_binary_name){
                    gender = context.getString(R.string.binary_name);
                } else if(radioBtnID == R.id.radio_other_gender){
                    gender = context.getString(R.string.other);
                }
            });
        }

        // add college
        if (add_school != null) {
            txt_update.setText(context.getString(R.string.add));
            button_text.setText(context.getString(R.string.add));
            relLayout_add_college.setVisibility(View.VISIBLE);
            edit_add_college_header.setText(context.getString(R.string.studied_at));
            edit_add_college.requestFocus();
            showKeyboard();

            edit_add_college.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_add_college) {
                        isFirstTimeStroke_edit_add_college = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_college.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // user school name
        if (update_school_name != null) {
            relLayout_update_school_name.setVisibility(View.VISIBLE);
            edit_school_name.requestFocus();
            showKeyboard();

            edit_school_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_school_name) {
                        isFirstTimeStroke_edit_school_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_school_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // autoCompleteTextView add school
        final HRArrayAdapter<String> autoComplete_add_school = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        add_school_list.clear();
                        autoComplete_add_school.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_school)).getChildren()){
                                SchoolAttended school = new SchoolAttended();
                                Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                assert objectHashMap != null;
                                school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                                // get the suggestion by childing the key of the string you want to get.
                                String schoolName = school.getUser_school_attended();

                                // add the retrieving String to the list
                                if (!add_school_list.contains(schoolName))
                                    add_school_list.add(schoolName);

                            }
                        }

                        for (int i = 0; i < add_school_list.size(); i++) {
                            autoComplete_add_school.add(add_school_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_add_college.setAdapter(autoComplete_add_school);
        edit_add_college.setThreshold(1);

        // autoCompleteTextView school name
        final HRArrayAdapter<String> autoComplete_school_name = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        school_name_list.clear();
                        autoComplete_school_name.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_school)).getChildren()){
                                SchoolAttended school = new SchoolAttended();
                                Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                assert objectHashMap != null;
                                school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                                // get the suggestion by childing the key of the string you want to get.
                                String schoolName = school.getUser_school_attended();

                                // add the retrieving String to the list
                                if (!school_name_list.contains(schoolName))
                                    school_name_list.add(schoolName);

                            }
                        }

                        for (int i = 0; i < school_name_list.size(); i++) {
                            autoComplete_school_name.add(school_name_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_school_name.setAdapter(autoComplete_school_name);
        // number of typing charSequence before suggestion appear
        edit_school_name.setThreshold(1);

        // add establisment
        if (add_establishment != null) {
            txt_update.setText(context.getString(R.string.add));
            button_text.setText(context.getString(R.string.add));
            relLayout_add_establishment.setVisibility(View.VISIBLE);
            edit_add_establishment_header.setText(context.getString(R.string.studied_at));
            edit_add_establishment.requestFocus();
            showKeyboard();

            edit_add_establishment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_add_establishment) {
                        isFirstTimeStroke_edit_add_establishment = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_establishment.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // user establishment name
        if (update_establishment_name != null) {
            relLayout_update_establishment_name.setVisibility(View.VISIBLE);
            edit_establishment_name.requestFocus();
            showKeyboard();

            edit_establishment_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_establishment_name) {
                        isFirstTimeStroke_edit_establishment_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_establishment_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // autoCompleteTextView add establishment
        final HRArrayAdapter<String> autoComplete_add_establishment = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        add_establishment_list.clear();
                        autoComplete_add_establishment.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_establishments)).getChildren()){
                                FrequentedEstablishment establishment = new FrequentedEstablishment();
                                Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                assert objectHashMap != null;
                                establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                                // get the suggestion by childing the key of the string you want to get.
                                String establishmentName = establishment.getUser_establishment();

                                // add the retrieving String to the list
                                if (!add_establishment_list.contains(establishmentName))
                                    add_establishment_list.add(establishmentName);

                            }
                        }

                        for (int i = 0; i < add_establishment_list.size(); i++) {
                            autoComplete_add_establishment.add(add_establishment_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_add_establishment.setAdapter(autoComplete_add_establishment);
        edit_add_establishment.setThreshold(1);

        // autoCompleteTextView school name
        final HRArrayAdapter<String> autoComplete_establishment_name = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        establishment_name_list.clear();
                        autoComplete_establishment_name.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_establishments)).getChildren()){
                                FrequentedEstablishment establishment = new FrequentedEstablishment();
                                Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                assert objectHashMap != null;
                                establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                                // get the suggestion by childing the key of the string you want to get.
                                String establishmentName = establishment.getUser_establishment();

                                // add the retrieving String to the list
                                if (!establishment_name_list.contains(establishmentName))
                                    establishment_name_list.add(establishmentName);

                            }
                        }

                        for (int i = 0; i < establishment_name_list.size(); i++) {
                            autoComplete_establishment_name.add(establishment_name_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_establishment_name.setAdapter(autoComplete_establishment_name);
        // number of typing charSequence before suggestion appear
        edit_establishment_name.setThreshold(1);

        // add workplace
        if (add_workplace != null) {
            txt_update.setText(context.getString(R.string.add));
            button_text.setText(context.getString(R.string.add));
            relLayout_add_workplace.setVisibility(View.VISIBLE);
            edit_add_workplace_header.setText(context.getString(R.string.work_at));
            edit_add_workplace.requestFocus();
            showKeyboard();

            edit_add_workplace.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_add_workplace) {
                        isFirstTimeStroke_edit_add_workplace = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_workplace.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // user workplace name
        if (update_workplace_name != null) {
            relLayout_update_workplace_name.setVisibility(View.VISIBLE);
            edit_workplace_name.requestFocus();
            showKeyboard();

            edit_workplace_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (isFirstTimeStroke_edit_workplace_name) {
                        isFirstTimeStroke_edit_workplace_name = false;
                        GradientDrawable drawable = (GradientDrawable) relLayout_workplace_name.getBackground();
                        drawable.mutate();
                        drawable.setStroke(1, context.getColor(R.color.grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        // autoCompleteTextView add workplace
        final HRArrayAdapter<String> autoComplete_add_workplace = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        add_workplace_list.clear();
                        autoComplete_add_workplace.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_workAts)).getChildren()){
                                WorkAt workplace = new WorkAt();
                                Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                assert objectHashMap != null;
                                workplace.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                                // get the suggestion by childing the key of the string you want to get.
                                String workplaceName = workplace.getUser_work_at();

                                // add the retrieving String to the list
                                if (!add_workplace_list.contains(workplaceName))
                                    add_workplace_list.add(workplaceName);

                            }
                        }

                        for (int i = 0; i < add_workplace_list.size(); i++) {
                            autoComplete_add_workplace.add(add_workplace_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_add_workplace.setAdapter(autoComplete_add_workplace);
        edit_add_workplace.setThreshold(1);

        // autoCompleteTextView workplace name
        final HRArrayAdapter<String> autoComplete_workplace_name = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        workplace_name_list.clear();
                        autoComplete_workplace_name.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_workAts)).getChildren()){
                                WorkAt workplace = new WorkAt();
                                Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                assert objectHashMap != null;
                                workplace.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                                // get the suggestion by childing the key of the string you want to get.
                                String workplaceName = workplace.getUser_work_at();

                                // add the retrieving String to the list
                                if (!workplace_name_list.contains(workplaceName))
                                    workplace_name_list.add(workplaceName);

                            }
                        }

                        for (int i = 0; i < workplace_name_list.size(); i++) {
                            autoComplete_workplace_name.add(workplace_name_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_workplace_name.setAdapter(autoComplete_workplace_name);
        edit_workplace_name.setThreshold(1);

        // click on button
        relLayout_button.setOnClickListener(view -> {
            keyboardActionDone();
        });

        // active done button on keyboard
        edit_username.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
                return true;
            }
            return false;
        });

        edit_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_bio.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_town_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_neighborhood_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_add_college.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_add_establishment.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_add_workplace.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_school_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_establishment_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        edit_workplace_name.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
                keyboardActionDone();
            }
            return false;
        });

        backArrow.setOnClickListener(view -> {
            closeKeyboard();
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeKeyboard();
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));

                finish();
            }
        });
    }

    /**
     * retrieve the data contained in the widgets and submit it to the database
     * Before doing so it check to make sure the username chosen is unique
     */
    private void keyboardActionDone() {
        Log.d(TAG, "onClick: attempting to save changes.");
        // internet control
        boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

        if (!isConnected) {
            CheckInternetStatus.internetIsConnected(context, main);

        } else {
            saveProfileSettings();
        }
    }
    private void saveProfileSettings(){
        String regexStr = "^(?=[a-zA-ZÀ-ÿ'0-9._ ]{3,20}$)(?!.*[_. ]{2})[^_. ].*[^_. ]$";
        final String username = Objects.requireNonNull(edit_username.getText()).toString().trim().toLowerCase().replace(" ", ".");
        final String fullname = Objects.requireNonNull(edit_name.getText()).toString().trim();
        final String bio = Objects.requireNonNull(edit_bio.getText()).toString();
        final String townName = Objects.requireNonNull(edit_town_name.getText()).toString().trim();
        final String neighborhoodName = Objects.requireNonNull(edit_neighborhood_name.getText()).toString().trim();
        final String addCollege = Objects.requireNonNull(edit_add_college.getText()).toString().trim();
        final String addCollege_header = Objects.requireNonNull(edit_add_college_header.getText()).toString().trim();
        final String schoolName = Objects.requireNonNull(edit_school_name.getText()).toString().trim();
        final String schoolName_header = Objects.requireNonNull(edit_school_name_header.getText()).toString().trim();
        final String addEstablishment = Objects.requireNonNull(edit_add_establishment.getText()).toString().trim();
        final String addEstablishment_header = Objects.requireNonNull(edit_add_establishment_header.getText()).toString().trim();
        final String establishmentName = Objects.requireNonNull(edit_establishment_name.getText()).toString().trim();
        final String establishmentName_header = Objects.requireNonNull(edit_establishment_name_header.getText()).toString().trim();
        final String addWorkplace = Objects.requireNonNull(edit_add_workplace.getText()).toString().trim();
        final String addWorkplace_header = Objects.requireNonNull(edit_add_workplace_header.getText()).toString().trim();
        final String workplaceName = Objects.requireNonNull(edit_workplace_name.getText()).toString().trim();
        final String workplaceName_header = Objects.requireNonNull(edit_workplace_name_header.getText()).toString().trim();

        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    k++;
                    if (k == 1) {
                        if (update_username != null) {
                            //case1: if the user made a change to their username
                            if (!TextUtils.isEmpty(username) && username.matches(regexStr)) {
                                closeKeyboard();
                                checkIfUsernameExists(username);
                            } else {
                                isFirstTimeStroke_edit_username = true;
                                GradientDrawable drawable = (GradientDrawable)relLayout_username.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                edit_username.requestFocus();
                                k = 0;
                                return;
                            }
                        }

                        if (update_fullName != null) {
                            // change the rest of the settings that do not require uniqueness
                            if (!TextUtils.isEmpty(fullname) && fullname.matches(regexStr)) {
                                if (!user.getFullName().equals(fullname)) {
                                    relLayout_button.setEnabled(false);
                                    closeKeyboard();
                                    //update fullname
                                    mFirebaseMethods.updateUserAccountSettings(null, fullname, null, null);

                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));

                                    finish();
                                }
                            } else {
                                isFirstTimeStroke_edit_name = true;
                                GradientDrawable drawable = (GradientDrawable)relLayout_name.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                edit_name.requestFocus();
                                k = 0;
                                return;
                            }
                        }

                        // town name
                        if (update_town_name != null) {
                            //case1: if the user made a change to their username
                            if (!TextUtils.isEmpty(townName) && townName.length() > 3) {
                                if (!user.getTown_name().equals(townName)) {
                                    relLayout_button.setEnabled(false);
                                    closeKeyboard();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("town_name", townName);

                                    myRef.child(context.getString(R.string.dbname_users))
                                            .child(user_id)
                                            .updateChildren(map).addOnSuccessListener(unused -> {
                                                getWindow().setExitTransition(new Slide(Gravity.END));
                                                getWindow().setEnterTransition(new Slide(Gravity.START));

                                                finish();
                                            });
                                }
                            } else {
                                isFirstTimeStroke_edit_town_name = true;
                                GradientDrawable drawable = (GradientDrawable)relLayout_town_name.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                edit_town_name.requestFocus();
                                k = 0;
                                return;
                            }
                        }

                        // neighborhood name
                        if (update_neighborhood_name != null) {
                            //case1: if the user made a change to their username
                            if (!TextUtils.isEmpty(neighborhoodName) && neighborhoodName.length() > 3) {
                                if (!user.getNeighborhood_name().equals(neighborhoodName)) {
                                    relLayout_button.setEnabled(false);
                                    closeKeyboard();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("neighborhood_name", neighborhoodName);

                                    myRef.child(context.getString(R.string.dbname_users))
                                            .child(user_id)
                                            .updateChildren(map).addOnSuccessListener(unused -> {
                                                getWindow().setExitTransition(new Slide(Gravity.END));
                                                getWindow().setEnterTransition(new Slide(Gravity.START));

                                                finish();
                                            });
                                }
                            } else {
                                isFirstTimeStroke_edit_neighborhood_name = true;
                                GradientDrawable drawable = (GradientDrawable)relLayout_neighborhood_name.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                edit_neighborhood_name.requestFocus();
                                k = 0;
                                return;
                            }
                        }

                        if (update_bio != null) {
                            if (bio.length() > 3) {
                                if (!user.getBio().equals(bio)) {
                                    relLayout_button.setEnabled(false);
                                    closeKeyboard();
                                    //update bio
                                    mFirebaseMethods.updateUserAccountSettings(null, null, null, bio);

                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));

                                    finish();
                                }
                            } else {
                                isFirstTimeStroke_edit_bio = true;
                                GradientDrawable drawable = (GradientDrawable)relLayout_bio.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                edit_bio.requestFocus();
                                k = 0;
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // update school name
        if (update_school_name != null) {
            // school name
            if (!TextUtils.isEmpty(schoolName) && !school_name.equals(schoolName)) {
                relLayout_button.setEnabled(false);
                closeKeyboard();

                HashMap<String, Object> map = new HashMap<>();
                map.put("user_school_attended", schoolName);
                map.put("user_school_attended_header", schoolName_header);

                myRef.child(context.getString(R.string.dbname_users))
                        .child(user_id)
                        .child(context.getString(R.string.field_school))
                        .child(schoolKey)
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            finish();
                        });

            } else {
                isFirstTimeStroke_edit_school_name = true;
                GradientDrawable drawable = (GradientDrawable)relLayout_school_name.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_school_name.requestFocus();
            }
        }

        // update establishment name
        if (update_establishment_name != null) {
            // establishment name
            if (!TextUtils.isEmpty(establishmentName) && !establishment_name.equals(establishmentName)) {
                relLayout_button.setEnabled(false);
                closeKeyboard();

                HashMap<String, Object> map = new HashMap<>();
                map.put("user_establishment", establishmentName);
                map.put("user_establishment_header", establishmentName_header);

                myRef.child(context.getString(R.string.dbname_users))
                        .child(user_id)
                        .child(context.getString(R.string.field_establishments))
                        .child(establishmentKey)
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            finish();
                        });

            } else {
                isFirstTimeStroke_edit_establishment_name = true;
                GradientDrawable drawable = (GradientDrawable)relLayout_establishment_name.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_establishment_name.requestFocus();
            }
        }

        // update workplace name
        if (update_workplace_name != null) {
            // workplace name
            if (!TextUtils.isEmpty(workplaceName) && !workplace_name.equals(workplaceName)) {
                relLayout_button.setEnabled(false);
                closeKeyboard();

                HashMap<String, Object> map = new HashMap<>();
                map.put("user_work_at", workplaceName);
                map.put("user_work_at_header", workplaceName_header);

                myRef.child(context.getString(R.string.dbname_users))
                        .child(user_id)
                        .child(context.getString(R.string.field_workAts))
                        .child(workplaceKey)
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            finish();
                        });

            } else {
                isFirstTimeStroke_edit_workplace_name = true;
                GradientDrawable drawable = (GradientDrawable)relLayout_workplace_name.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_workplace_name.requestFocus();
            }
        }

        if (update_relationship_status != null && !TextUtils.isEmpty(relationship)) {
            relLayout_button.setEnabled(false);
            HashMap<String, Object> map = new HashMap<>();
            map.put("marital_status", relationship);

            myRef.child(context.getString(R.string.dbname_users))
                    .child(user_id)
                    .updateChildren(map).addOnSuccessListener(unused -> {
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));

                        finish();
                    });
        }

        if (update_gender != null && !TextUtils.isEmpty(gender)) {
            relLayout_button.setEnabled(false);
            HashMap<String, Object> map = new HashMap<>();
            map.put("gender", gender);

            myRef.child(context.getString(R.string.dbname_users))
                    .child(user_id)
                    .updateChildren(map).addOnSuccessListener(unused -> {
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));

                        finish();
                    });
        }

        // add collège
        if (add_school != null) {
            if (!TextUtils.isEmpty(addCollege) && addCollege.length() > 3) {
                relLayout_button.setEnabled(false);
                closeKeyboard();

                String newCollegeKey = myRef.push().getKey();
                Date date = new Date();

                HashMap<String, Object> map = new HashMap<>();
                map.put("user_school_attended_header", addCollege_header);
                map.put("user_school_attended", addCollege);
                map.put("user_id", user_id);
                map.put("schoolKey", newCollegeKey);
                map.put("date_created", date.getTime());

                assert newCollegeKey != null;
                myRef.child(getString(R.string.dbname_users))
                        .child(user_id)
                        .child(context.getString(R.string.field_school))
                        .child(newCollegeKey)
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            finish();
                        });

            } else {
                isFirstTimeStroke_edit_add_college = true;
                GradientDrawable drawable = (GradientDrawable)relLayout_college.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_add_college.requestFocus();
            }
        }

        // add establishment
        if (add_establishment != null) {
            if (!TextUtils.isEmpty(addEstablishment) && addEstablishment.length() > 3) {
                relLayout_button.setEnabled(false);
                closeKeyboard();

                String newEstablishmentKey = myRef.push().getKey();
                Date date = new Date();

                HashMap<String, Object> map = new HashMap<>();
                map.put("user_establishment", addEstablishment);
                map.put("user_establishment_header", addEstablishment_header);
                map.put("user_id", user_id);
                map.put("establishmentKey", newEstablishmentKey);
                map.put("date_created", date.getTime());

                assert newEstablishmentKey != null;
                myRef.child(getString(R.string.dbname_users))
                        .child(user_id)
                        .child(context.getString(R.string.field_establishments))
                        .child(newEstablishmentKey)
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            finish();
                        });

            } else {
                isFirstTimeStroke_edit_add_establishment = true;
                GradientDrawable drawable = (GradientDrawable)relLayout_establishment.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_add_establishment.requestFocus();
            }
        }

        // add workplace
        if (add_workplace != null) {
            if (!TextUtils.isEmpty(addWorkplace) && addWorkplace.length() > 3) {
                relLayout_button.setEnabled(false);
                closeKeyboard();

                String newWorkplaceKey = myRef.push().getKey();
                Date date = new Date();

                HashMap<String, Object> map = new HashMap<>();
                map.put("user_work_at_header", addWorkplace_header);
                map.put("user_work_at", addWorkplace);
                map.put("user_id", user_id);
                map.put("workAtKey", newWorkplaceKey);
                map.put("date_created", date.getTime());

                assert newWorkplaceKey != null;
                myRef.child(getString(R.string.dbname_users))
                        .child(user_id)
                        .child(context.getString(R.string.field_workAts))
                        .child(newWorkplaceKey)
                        .updateChildren(map).addOnSuccessListener(unused -> {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            finish();
                        });

            } else {
                isFirstTimeStroke_edit_add_workplace = true;
                GradientDrawable drawable = (GradientDrawable)relLayout_workplace.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_add_workplace.requestFocus();
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
                        isFirstTimeStroke_edit_username = true;
                        GradientDrawable drawable = (GradientDrawable)relLayout_username.getBackground();
                        drawable.mutate();
                        drawable.setStroke(3, Color.RED);
                        relLayout_username.requestFocus();

                        Toast.makeText(context, getString(R.string.name_already_exists), Toast.LENGTH_SHORT).show();
                        k = 0;
                    }
                }

                if(!dataSnapshot.exists()){
                    //add the username
                    relLayout_button.setEnabled(false);

                    //update username
                    mFirebaseMethods.updateUserAccountSettings(username, null, null, null);

                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    if (update_bio != null) {
                        edit_bio.setText(user.getBio());
                        edit_bio.setSelection(Objects.requireNonNull(edit_bio.getText()).length());
                    }

                    if (update_username != null) {
                        edit_username.setText(user.getUsername());
                        edit_username.setSelection(Objects.requireNonNull(edit_username.getText()).length());
                    }

                    if (update_fullName != null) {
                        edit_name.setText(user.getFullName());
                        edit_name.setSelection(Objects.requireNonNull(edit_name.getText()).length());
                    }

                    if (update_town_name != null) {
                        edit_town_name.setText(user.getTown_name());
                        edit_town_name.setSelection(Objects.requireNonNull(edit_town_name.getText()).length());
                    }

                    if (update_neighborhood_name != null) {
                        edit_neighborhood_name.setText(user.getNeighborhood_name());
                        edit_neighborhood_name.setSelection(Objects.requireNonNull(edit_neighborhood_name.getText()).length());
                    }

                    if (update_relationship_status != null) {
                        String status = user.getMarital_status();
                        if (!TextUtils.isEmpty(status)) {
                            if (status.equals(context.getString(R.string.single)))
                                radio_single.setChecked(true);
                            if (status.equals(context.getString(R.string.married)))
                                radio_married.setChecked(true);
                            if (status.equals(context.getString(R.string.divorced)))
                                radio_divorced.setChecked(true);
                            if (status.equals(context.getString(R.string.in_a_relationship_with)))
                                radio_in_a_relationship_with.setChecked(true);
                            if (status.equals(context.getString(R.string.separated)))
                                radio_separated.setChecked(true);
                            if (status.equals(context.getString(R.string.other)))
                                radio_other.setChecked(true);
                        }
                    }

                    if (update_gender != null) {
                        String status = user.getGender();
                        if (!TextUtils.isEmpty(status)) {
                            if (status.equals(context.getString(R.string.man)))
                                radio_man.setChecked(true);
                            if (status.equals(context.getString(R.string.women)))
                                radio_women.setChecked(true);
                            if (status.equals(context.getString(R.string.binary_name)))
                                radio_binary_name.setChecked(true);
                            if (status.equals(context.getString(R.string.other)))
                                radio_other_gender.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // update school name
        if (update_school_name != null) {
            edit_school_name_header.setText(school_name_header);
            edit_school_name.setText(school_name);
            edit_school_name.setSelection(Objects.requireNonNull(edit_school_name.getText()).length());
        }

        // update establishment name
        if (update_establishment_name != null) {
            edit_establishment_name_header.setText(establishment_name_header);
            edit_establishment_name.setText(establishment_name);
            edit_establishment_name.setSelection(Objects.requireNonNull(edit_establishment_name.getText()).length());
        }

        // update workplace name
        if (update_workplace_name != null) {
            edit_workplace_name_header.setText(workplace_name_header);
            edit_workplace_name.setText(workplace_name);
            edit_workplace_name.setSelection(Objects.requireNonNull(edit_workplace_name.getText()).length());
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
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, main);
    }
}