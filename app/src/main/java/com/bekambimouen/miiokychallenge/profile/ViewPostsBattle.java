package com.bekambimouen.miiokychallenge.profile;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.adapter.ViewPostBattleAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewPostsBattle extends AppCompatActivity {
    private static final String TAG = "ViewPostsBattle";

    // widgets
    private ProgressBar progressBar;
    private ProgressBar progressBar1;
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private ViewPostsBattle context;
    private ViewPostBattleAdapter adapter;
    private User mUser_main, user_from_my_challenge;
    private User mUser;
    private ArrayList<BattleModel> list, pagination;
    private List<String> preload_url_list;
    private int position;
    private int resultsCount = 0;
    private boolean loading = false;
    private String user_id;
    private Handler handler;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_view_posts_battle);
        context = this;

        handler = new Handler(Looper.getMainLooper());
        preload_url_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            mUser_main = gson.fromJson(getIntent().getStringExtra("user"), User.class);
            mUser = gson.fromJson(getIntent().getStringExtra("from_view_battle_profile"), User.class);
            user_from_my_challenge = gson.fromJson(getIntent().getStringExtra("user_from_my_challenge"), User.class);
            position = getIntent().getIntExtra("position", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (mUser != null)
            user_id = mUser.getUser_id();
        else if (mUser_main != null)
            user_id = mUser_main.getUser_id();
        else if (user_from_my_challenge != null)
            user_id = user_from_my_challenge.getUser_id();
        else
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        init();
        getUserBattlePosts();
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
        progressBar = findViewById(R.id.progressBar);
        progressBar1 = findViewById(R.id.progressBar1);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

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

    private void clearAll(){
        if(list != null){
            list.clear();
        }
        if(pagination != null){
            pagination.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getUserBattlePosts() {
        clearAll();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_battle))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);
                    if (!model.isSuppressed())
                        list.add(model);
                }

                //sort for newest to oldest
                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                // preload image
                for(int i = 0; i < list.size(); i++){
                    // get the 20 first url to preload
                    if (!TextUtils.isEmpty(list.get(i).getUrl())){
                        preload_url_list.add(list.get(i).getUrl());

                    } else if (!TextUtils.isEmpty(list.get(i).getUrlUn())) {
                        preload_url_list.add(list.get(i).getUrlUn());
                        preload_url_list.add(list.get(i).getUrlDeux());

                    } else if (!TextUtils.isEmpty(list.get(i).getReponse_url())) {
                        preload_url_list.add(list.get(i).getReponse_url());
                        preload_url_list.add(list.get(i).getInvite_url());

                    } else if (!TextUtils.isEmpty(list.get(i).getUrli())) {
                        preload_url_list.add(list.get(i).getUrli());
                        preload_url_list.add(list.get(i).getUrlii());
                        if (!TextUtils.isEmpty(list.get(i).getUrliii()))
                            preload_url_list.add(list.get(i).getUrliii());
                        if (!TextUtils.isEmpty(list.get(i).getUrliv()))
                            preload_url_list.add(list.get(i).getUrliv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlv()))
                            preload_url_list.add(list.get(i).getUrlv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlvi()))
                            preload_url_list.add(list.get(i).getUrlvi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlvii()))
                            preload_url_list.add(list.get(i).getUrlvii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlviii()))
                            preload_url_list.add(list.get(i).getUrlviii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlix()))
                            preload_url_list.add(list.get(i).getUrlix());
                        if (!TextUtils.isEmpty(list.get(i).getUrlx()))
                            preload_url_list.add(list.get(i).getUrlx());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxi()))
                            preload_url_list.add(list.get(i).getUrlxi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxii()))
                            preload_url_list.add(list.get(i).getUrlxii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxiii()))
                            preload_url_list.add(list.get(i).getUrlxiii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxiv()))
                            preload_url_list.add(list.get(i).getUrlxiv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxv()))
                            preload_url_list.add(list.get(i).getUrlxv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxvi()))
                            preload_url_list.add(list.get(i).getUrlxvi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxvii()))
                            preload_url_list.add(list.get(i).getUrlxvii());

                    } else if (!TextUtils.isEmpty(list.get(i).getUser_profile_photo())) {
                        preload_url_list.add(list.get(i).getUser_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_profile_photo())) {
                        preload_url_list.add(list.get(i).getGroup_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_full_profile_photo())) {
                        preload_url_list.add(list.get(i).getGroup_full_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_user_background_profile_url())) {
                        preload_url_list.add(list.get(i).getGroup_user_background_profile_url());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_user_background_full_profile_url())) {
                        preload_url_list.add(list.get(i).getGroup_user_background_full_profile_url());
                    }
                }

                // preload image
                preloadAllImage(preload_url_list);

                adapter = new ViewPostBattleAdapter(context, list, progressBar, relLayout_view_overlay);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setInitialPrefetchItemCount(10);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(position);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public List<BattleModel> displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(list.size() > resultsCount && !list.isEmpty()){

                int iterations;
                if(list.size() > (resultsCount + 20)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    loading = true;
                    progressBar1.setVisibility(View.VISIBLE);
                    iterations = 20;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    loading = false;
                    iterations = list.size() - resultsCount;

                    progressBar1.setVisibility(View.GONE);
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    // get the 20 first url to preload
                    if (!TextUtils.isEmpty(list.get(i).getUrl())){
                        preload_url_list.add(list.get(i).getUrl());

                    } else if (!TextUtils.isEmpty(list.get(i).getUrlUn())) {
                        preload_url_list.add(list.get(i).getUrlUn());
                        preload_url_list.add(list.get(i).getUrlDeux());

                    } else if (!TextUtils.isEmpty(list.get(i).getReponse_url())) {
                        preload_url_list.add(list.get(i).getReponse_url());
                        preload_url_list.add(list.get(i).getInvite_url());

                    } else if (!TextUtils.isEmpty(list.get(i).getUrli())) {
                        preload_url_list.add(list.get(i).getUrli());
                        preload_url_list.add(list.get(i).getUrlii());
                        if (!TextUtils.isEmpty(list.get(i).getUrliii()))
                            preload_url_list.add(list.get(i).getUrliii());
                        if (!TextUtils.isEmpty(list.get(i).getUrliv()))
                            preload_url_list.add(list.get(i).getUrliv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlv()))
                            preload_url_list.add(list.get(i).getUrlv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlvi()))
                            preload_url_list.add(list.get(i).getUrlvi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlvii()))
                            preload_url_list.add(list.get(i).getUrlvii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlviii()))
                            preload_url_list.add(list.get(i).getUrlviii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlix()))
                            preload_url_list.add(list.get(i).getUrlix());
                        if (!TextUtils.isEmpty(list.get(i).getUrlx()))
                            preload_url_list.add(list.get(i).getUrlx());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxi()))
                            preload_url_list.add(list.get(i).getUrlxi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxii()))
                            preload_url_list.add(list.get(i).getUrlxii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxiii()))
                            preload_url_list.add(list.get(i).getUrlxiii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxiv()))
                            preload_url_list.add(list.get(i).getUrlxiv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxv()))
                            preload_url_list.add(list.get(i).getUrlxv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxvi()))
                            preload_url_list.add(list.get(i).getUrlxvi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxvii()))
                            preload_url_list.add(list.get(i).getUrlxvii());

                    } else if (!TextUtils.isEmpty(list.get(i).getUser_profile_photo())) {
                        preload_url_list.add(list.get(i).getUser_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_profile_photo())) {
                        preload_url_list.add(list.get(i).getGroup_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_full_profile_photo())) {
                        preload_url_list.add(list.get(i).getGroup_full_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_user_background_profile_url())) {
                        preload_url_list.add(list.get(i).getGroup_user_background_profile_url());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_user_background_full_profile_url())) {
                        preload_url_list.add(list.get(i).getGroup_user_background_full_profile_url());
                    }
                    pagination.add(list.get(i));
                }

                // preload image
                preloadAllImage(preload_url_list);

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }

        return pagination;
    }

    private void preloadAllImage(List<String> list) {
        for (String url: list) {
            preloadImages(url);
        }
    }

    private void preloadImages(String url) {
        Glide.with(context.getApplicationContext())
                .downloadOnly()
                .load(url)
                .submit(500, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}