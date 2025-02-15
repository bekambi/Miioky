package com.bekambimouen.miiokychallenge.groups.photos_videos_only;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.adapter.GridPhotosAdapter;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.Utils.SpannedGridLayoutManager;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupOnlyPhotoGradle extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "GroupOnlyPhotoGradle";

    //widgets
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_none_post, relLayout_view_overlay;

    // vars
    private GroupOnlyPhotoGradle context;
    private GridPhotosAdapter adapter;
    private UserGroups user_groups;
    private ArrayList<BattleModel> list, pagination;
    private Handler handler;
    private int resultsCount = 0;

    // firebase
    private FirebaseDatabase database;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_only_photo_gradle);
        context = this;

        database=FirebaseDatabase.getInstance();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_none_post = findViewById(R.id.relLayout_none_post);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(getString(R.string.dbname_group_media))
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    if ((model.isGridRecyclerImage() || model.isGroup_imageUne() ||
                            model.isGroup_cover_profile_photo()
                            || model.isGroup_cover_background_profile_photo() ||
                            model.isGroup_imageDouble() || model.isGroup_response_imageDouble()) && model.isGroup())
                    {
                        list.add(model);
                    }
                }

                displayPhotos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayPhotos() {
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        int iterations = list.size();

        if(iterations > 10){
            iterations = 10;
        }

        resultsCount = 10;
        for(int i = 0; i < iterations; i++){
            pagination.add(list.get(i));
        }

        adapter = new GridPhotosAdapter(context, pagination, user_groups, progressBar, relLayout_view_overlay);
        // the gridView
        SpannedGridLayoutManager manager = new SpannedGridLayoutManager(
                position -> {
                    // Conditions for 2x2 items
                    if (position % 6 == 0 || position % 6 == 4) {
                        return new SpannedGridLayoutManager.SpanInfo(2, 2);
                    } else {
                        return new SpannedGridLayoutManager.SpanInfo(1, 1);
                    }
                },
                3, // number of columns
                1f // how big is default item
        );
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() <= 1) {
            progressBar.setVisibility(View.GONE);
            relLayout_none_post.setVisibility(View.VISIBLE);
        }
    }

    public void displayMoreImages() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(list.size() > resultsCount && !list.isEmpty()){

                int iterations;
                if(list.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = list.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(list.get(i));
                }

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }

    @Override
    public void onLoadMoreItems() {
        displayMoreImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}