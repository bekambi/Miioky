package com.bekambimouen.miiokychallenge.profile;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileCollegeAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileEstablishmentAdapter;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileWorkPlaceAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SeeSectionAboutMe extends AppCompatActivity {
    private static final String TAG = "SeeSectionAboutMe";

    // widgets
    private RecyclerView recyclerView_school, recyclerView_establishment, recyclerView_workplace;
    private LinearLayout parent;

    // vars
    private SeeSectionAboutMe context;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;

    //firebase
    private DatabaseReference myRef;
    private String userID;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_see_section_about_me);
        context = this;

        try {
            userID = getIntent().getStringExtra("userID");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        myRef = FirebaseDatabase.getInstance().getReference();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
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
        RelativeLayout relLayout_school_to_display = findViewById(R.id.relLayout_school_to_display);
        RelativeLayout relLayout_workplace_to_display = findViewById(R.id.relLayout_workplace_to_display);
        LinearLayout linLayout_marital_status = findViewById(R.id.linLayout_marital_status);
        LinearLayout linLayout_gender = findViewById(R.id.linLayout_gender);
        TextView live_country = findViewById(R.id.live_country);
        TextView native_country = findViewById(R.id.native_country);
        TextView town_name = findViewById(R.id.town_name);
        TextView hometown_name = findViewById(R.id.hometown_name);
        TextView marital_status = findViewById(R.id.marital_status);
        TextView marital_status_empty = findViewById(R.id.marital_status_empty);
        TextView gender = findViewById(R.id.gender);
        recyclerView_school = findViewById(R.id.recyclerView_school);
        recyclerView_school.setNestedScrollingEnabled(false);
        recyclerView_school.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_establishment = findViewById(R.id.recyclerView_establishment);
        recyclerView_establishment.setNestedScrollingEnabled(false);
        recyclerView_establishment.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_workplace = findViewById(R.id.recyclerView_workplace);
        recyclerView_workplace.setNestedScrollingEnabled(false);
        recyclerView_workplace.setLayoutManager(new LinearLayoutManager(context));

        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByKey()
                .equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    live_country.setText(GetCountryName());
                    native_country.setText(user.getNative_country_name());
                    town_name.setText(user.getTown_name());
                    hometown_name.setText(user.getHometown_name());

                    String Gender = user.getGender();
                    if (!TextUtils.isEmpty(Gender))
                        linLayout_gender.setVisibility(View.VISIBLE);
                    gender.setText(Gender);

                    String status = user.getMarital_status();
                    if (TextUtils.isEmpty(status)) {
                        linLayout_marital_status.setVisibility(View.GONE);
                        marital_status_empty.setVisibility(View.VISIBLE);
                    }
                    marital_status.setText(status);

                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()){
                        SchoolAttended school = new SchoolAttended();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        school.setSchoolKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_schoolKey))).toString());
                        school.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());
                        school.setUser_school_attended_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended_header))).toString());

                        schools_list.add(school);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()){
                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        establishment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        establishment.setEstablishmentKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_establishmentKey))).toString());
                        establishment.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());
                        establishment.setUser_establishment_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment_header))).toString());

                        establishments_list.add(establishment);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()){
                        WorkAt workAt = new WorkAt();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        workAt.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        workAt.setWorkAtKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_workAtKey))).toString());
                        workAt.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());
                        workAt.setUser_work_at_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at_header))).toString());

                        workplaces_list.add(workAt);
                    }
                }

                ProfileCollegeAdapter adapter_school = new ProfileCollegeAdapter(context, schools_list);
                recyclerView_school.setAdapter(adapter_school);
                if (adapter_school.getItemCount() == 0)
                    recyclerView_school.setVisibility(View.GONE);

                ProfileEstablishmentAdapter adapter_establishment = new ProfileEstablishmentAdapter(context, establishments_list);
                recyclerView_establishment.setAdapter(adapter_establishment);
                if (adapter_establishment.getItemCount() == 0)
                    recyclerView_establishment.setVisibility(View.GONE);

                if (adapter_school.getItemCount() == 0 && adapter_establishment.getItemCount() == 0)
                    relLayout_school_to_display.setVisibility(View.VISIBLE);

                ProfileWorkPlaceAdapter adapter_workplace = new ProfileWorkPlaceAdapter(context, workplaces_list);
                recyclerView_workplace.setAdapter(adapter_workplace);
                if (adapter_workplace.getItemCount() == 0) {
                    recyclerView_workplace.setVisibility(View.GONE);
                    relLayout_workplace_to_display.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

    // country name corresponding to country name
    public String GetCountryName(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return CountryZipCode;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}