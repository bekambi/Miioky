package com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile.adapter.SeeUserAllProfileAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
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

public class SeeUserAllProfileOnMiioky extends AppCompatActivity {
    private static final String TAG = "SeelUserAllProfileOnMiioky";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private SeeUserAllProfileOnMiioky context;
    private SeeUserAllProfileAdapter adapter;
    private List<BattleModel> list;

    private String userID;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private String user_id;
    private FirebaseDatabase database;

    private void getBlackTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);

        //change bottom bar calor
        updateNavigationBarBlackColor(window);
    }

    // set bottom navigation bar color
    protected void updateNavigationBarBlackColor(Window window) {
        if (window == null) {
            return;
        }

        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setNavigationBarColor(getColor(R.color.black));//black or white
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // changer a couleur des icons en noir
        getBlackTheme();
        // adjustpan
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_see_user_all_profile_on_miioky);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            userID = getIntent().getStringExtra("userID");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getProfile();
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
        AppCompatButton btn_retry = findViewById(R.id.btn_retry);
        RelativeLayout relLayout_no_connxion = findViewById(R.id.relLayout_no_connxion);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        btn_retry.setOnClickListener(view1 -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                relLayout_no_connxion.setVisibility(View.VISIBLE);

            } else {
                relLayout_no_connxion.setVisibility(View.GONE);
            }
        });

        arrowBack.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    private void clearAll(){
        if(list != null){
            list.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list = new ArrayList<>();
    }

    private void getProfile() {
        clearAll();
        Query query = myRef
                .child(context.getString(R.string.dbname_battle))
                .child(userID) // user_id
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    if (model.isUser_profile())
                        list.add(model);
                }

                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                adapter = new SeeUserAllProfileAdapter(context, list, relLayout_view_overlay);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
        // verify if user is online
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.field_onLine), getString(R.string.in_line));
        database.getReference().child(getString(R.string.dbname_presence)).child(user_id).setValue(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Date date=new Date();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.field_onLine), getString(R.string.field_offLine));
        hashMap.put("timeStamp", date.getTime());
        database.getReference().child(getString(R.string.dbname_presence)).child(user_id).setValue(hashMap);
    }
}