package com.bekambimouen.miiokychallenge.groups.photos_videos_only;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.adapter.ViewVideoAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupViewOnlyVideos extends AppCompatActivity {
    private static final String TAG = "GroupViewOnlyVideos";

    //widgets
    private ProgressBar progressBar;
    private Container recyclerView;
    private LinearLayout parent;

    // vars
    private GroupViewOnlyVideos context;
    private ViewVideoAdapter adapter;
    private UserGroups user_groups;
    private BattleModel battle_model;
    private List<BattleModel> list, final_list;
    private int position;
    private Runnable preDownloadRunnable;
    private Handler handler;

    // pagination constants
    int counter = 10;

    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_group_view_only_videos);
        context = this;
        getBlackTheme();

        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
            battle_model = gson.fromJson(getIntent().getStringExtra("battle_model"), BattleModel.class);
            position = getIntent().getIntExtra("position", 0);
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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

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
        if(final_list != null){
            final_list.clear();
        }
        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list = new ArrayList<>();
        final_list = new ArrayList<>();
    }

    private void getUserBattlePosts() {
        clearAll();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(getString(R.string.dbname_group_media))
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    if ((model.isGroup_videoUne() || model.isGroup_videoDouble()
                            || model.isGroup_response_videoDouble()) && model.isGroup())
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
        //sort for newest to oldest
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        for (int i = 0; i < list.size(); i++) {
            if (i > position) {
                final_list.add(list.get(i));
            }
        }

        final_list.add(0, battle_model);

        adapter = new ViewVideoAdapter(context, recyclerView, progressBar);
        adapter.setDefaultRecyclerView(context, R.id.recyclerView);

        if (list.size() >= 2) {
            // first download the second video _____________________________________________________
            BattleModel nextVideoItem = list.get(1);

            // Create a PreloadListener
            PrepareNextVideo.PreloadListener preloadListener = new PrepareNextVideo.PreloadListener() {
                @Override
                public void onPreloadProgress(BattleModel videoItem, int progress) {
                    // Handle progress updates (e.g., update a progress bar)
                    Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                }

                @Override
                public void onPreloadComplete(BattleModel videoItem) {
                    // Handle completion (e.g., update UI)
                    Log.d("Preload", "Complete for " + videoItem.getUrl());
                }

                @Override
                public void onPreloadError(BattleModel videoItem, Exception e) {
                    // Handle errors (e.g., show an error message)
                    Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                }
            };

            // Call preDownloadVideo with the listener
            PrepareNextVideo.preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
            // _____________________________________________________________________________________
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    // Remove any pending pre-download tasks
                    handler.removeCallbacks(preDownloadRunnable);

                    // Schedule a new pre-download task with a delay
                    preDownloadRunnable = () -> {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if (layoutManager != null) {
                            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                            // Pre-download the next two videos
                            for (int i = 1; i <= 3; i++) {
                                int nextVideoPosition = firstVisibleItemPosition + i;
                                if (nextVideoPosition < list.size()) {
                                    BattleModel nextVideoItem = list.get(nextVideoPosition);

                                    // Create a PreloadListener
                                    PrepareNextVideo.PreloadListener preloadListener = new PrepareNextVideo.PreloadListener() {
                                        @Override
                                        public void onPreloadProgress(BattleModel videoItem, int progress) {
                                            // Handle progress updates (e.g., update a progress bar)
                                            Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                                        }

                                        @Override
                                        public void onPreloadComplete(BattleModel videoItem) {
                                            // Handle completion (e.g., update UI)
                                            Log.d("Preload", "Complete for " + videoItem.getUrl());
                                        }

                                        @Override
                                        public void onPreloadError(BattleModel videoItem, Exception e) {
                                            // Handle errors (e.g., show an error message)
                                            Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                                        }
                                    };

                                    // Call preDownloadVideo with the listener
                                    PrepareNextVideo.preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
                                }
                            }
                        }
                    };
                    handler.postDelayed(preDownloadRunnable, 200);
                }
            });
        }

        adapter.setOnPaginationListener(new PaginatedAdapter.OnPaginationListener() {
            @Override
            public void onCurrentPage(int page) {
                //Toast.makeText(context, "Page " + page + " loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNextPage(int page) {
                getNewItems(page);
            }

            @Override
            public void onFinish() {

            }
        });

        getNewItems(adapter.getStartPage());
    }

    public void onGetDate(List<BattleModel> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        List<BattleModel> models = new ArrayList<>();
        int start = page * counter - counter;
        int end = page * counter;
        for (int i = start; i < end; i++) {
            if (i < final_list.size()) {
                models.add(final_list.get(i));
            }
        }
        onGetDate(models);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}