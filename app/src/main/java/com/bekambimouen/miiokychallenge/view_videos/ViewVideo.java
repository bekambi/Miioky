package com.bekambimouen.miiokychallenge.view_videos;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.fun.FunSuggestions;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.view_videos.adapter.ViewVideoUneShareAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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

public class ViewVideo extends AppCompatActivity {
    private static final String TAG = "ViewVideo";

    // widgets
    private Container recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private ViewVideo context;
    private ViewVideoUneShareAdapter adapter;
    private BattleModel video;
    private ArrayList<BattleModel> list;
    private ArrayList<String> admin_list, group_list, user_list;
    private String from_home;
    private Handler handler;
    private Runnable preDownloadRunnable;

    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;

    private void getBlackTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_view_video);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            video = gson.fromJson(getIntent().getStringExtra("video"), BattleModel.class);
            from_home = getIntent().getStringExtra("from_home");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getGroup();
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
        RelativeLayout relLayout_img_play_red = findViewById(R.id.relLayout_img_play_red);
        RelativeLayout relLayout_icon_home = findViewById(R.id.relLayout_icon_home);
        RelativeLayout relLayout_menu = findViewById(R.id.relLayout_menu);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        relLayout_img_play_red.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            startActivity(new Intent(context, FunSuggestions.class));
        });

        relLayout_menu.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("from_view_video", "from_view_video");
            startActivity(intent);
        });

        relLayout_icon_home.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            if (from_home != null)
                finish();
            else
                startActivity(new Intent(context, MainActivity.class));
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
        if(admin_list != null){
            admin_list.clear();
        }
        if(group_list != null){
            group_list.clear();
        }

        if(user_list != null){
            user_list.clear();
        }

        if(list != null){
            list.clear();
        }
        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        admin_list = new ArrayList<>();
        group_list = new ArrayList<>();
        user_list = new ArrayList<>();
        list = new ArrayList<>();
    }

    private void getGroup() {
        Log.d(TAG, "getFollowing: searching for following");
        clearAll();

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group));

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get all current user groups
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String admin_id = ds.getKey();

                    admin_list.add(admin_id);
                }

                getGroupList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupList() {
        if (!admin_list.isEmpty()) {
            for(int i = 0; i < admin_list.size(); i++){
                final int count = i;
                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(admin_list.get(i));

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            String groupID = dataSnapshot.getKey();

                            group_list.add(groupID);
                        }

                        if(count >= admin_list.size() -1)
                            getGroupVideos();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else
            getMiiokyVodeos();
    }

    private void getGroupVideos() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        if (!group_list.isEmpty()) {
            for(int i = 0; i < group_list.size(); i++){
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_group))
                        .child(group_list.get(i)) // group_id
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(group_list.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            if (model.isVideoUne() || model.isVideoUne_shared() || model.isGroup_videoUne()
                                    || model.isGroup_share_single_bottom_video_une() || model.isGroup_share_single_top_video_une()
                                    || model.isGroup_share_top_bottom_video_une())
                            {
                                list.add(model);
                            }
                        }

                        if(count >= group_list.size() -1){

                            getMiiokyVodeos();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void getMiiokyVodeos() {
        Log.d(TAG, "getFollowing: searching for following");

        Query query = myRef
                .child(context.getString(R.string.dbname_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    if (user.getScope().equals(context.getString(R.string.title_public)))
                        user_list.add(user.getUser_id());
                }

                getVideos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getVideos() {
        Log.d(TAG, "getPhotos: getting list of photos");
        for(int i = 0; i < user_list.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_battle))
                    .child(user_list.get(i)) // user_id
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(user_list.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                        if (model.isVideoUne() || model.isVideoUne_shared() || model.isGroup_videoUne()
                                || model.isGroup_share_single_bottom_video_une() || model.isGroup_share_single_top_video_une()
                                || model.isGroup_share_top_bottom_video_une())
                        {
                            list.add(model);
                        }
                    }

                    if(count >= user_list.size() -1){
                        //display our photos
                        displayVideos();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });
        }
    }

    private void displayVideos() {
        //sort for newest to oldest
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        // to start adapter to some position
        for (int i = 0; i < list.size(); i++) {
            if (!video.getPhoto_id().isEmpty()) {
                if (list.get(i).getPhoto_id().equals(video.getPhoto_id())) {
                    list.remove(i);
                }
            }
        }

        list.add(0, video);

        adapter = new ViewVideoUneShareAdapter(context, recyclerView, progressBar, relLayout_view_overlay);
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
            if (i < list.size()) {
                models.add(list.get(i));
            }
        }
        onGetDate(models);
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