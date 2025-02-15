package com.bekambimouen.miiokychallenge.challenges_view_all;

import static com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoChallenge.preDownloadVideo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.ViewInviteVideoAdapter;
import com.bekambimouen.miiokychallenge.fun.FunSuggestions;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoChallenge;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
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

public class ViewChallengeVideos extends AppCompatActivity {
    private static final String TAG = "ViewChallengeVideos";

    // widgets
    private Container recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private ViewChallengeVideos context;
    private ViewInviteVideoAdapter adapter;
    private ModelInvite modelInvite;
    private List<ModelInvite> videos_list;
    private String userID, from_view_my_challenge, category_status;
    private Handler handler;
    private Runnable preDownloadRunnable;

    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;

    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
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
            window.setNavigationBarColor(context.getColor(R.color.black));//black or white
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_challenge_videos);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            modelInvite = gson.fromJson(getIntent().getStringExtra("modelInvite"), ModelInvite.class);
            userID = getIntent().getStringExtra("userID");
            category_status = getIntent().getStringExtra("category_status");
            from_view_my_challenge = getIntent().getStringExtra("from_view_my_challenge");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init();
        new Thread(() -> {
            if (from_view_my_challenge != null)
                getMyVideos();
            else
                getAllVideos();
        }).start();
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
        progressBar = findViewById(R.id.progressBar);
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        TextView category = findViewById(R.id.category);
        RelativeLayout relLayout_icon_home = findViewById(R.id.relLayout_icon_home);
        RelativeLayout relLayout_img_play_red = findViewById(R.id.relLayout_img_play_red);
        RelativeLayout relLayout_menu = findViewById(R.id.relLayout_menu);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String Category = null;
        if (category_status != null) {
            if (category_status.equals(getString(R.string.love_choice)))
                Category = getString(R.string.love);
            else if (category_status.equals(getString(R.string.beauty_choice)))
                Category = getString(R.string.beauty);
            else if (category_status.equals(getString(R.string.acapella_choice)))
                Category = getString(R.string.acapella);
            else if (category_status.equals(getString(R.string.dance_choice)))
                Category = getString(R.string.dance);
            else if (category_status.equals(getString(R.string.comedy_choice)))
                Category = getString(R.string.comedy);
            else if (category_status.equals(getString(R.string.swag_choice)))
                Category = getString(R.string.swag);
            else if (category_status.equals(getString(R.string.decoration_choice)))
                Category = getString(R.string.decoration);
            else if (category_status.equals(getString(R.string.couple_choice)))
                Category = getString(R.string.couple);
            else if (category_status.equals(getString(R.string.cinema_choice)))
                Category = getString(R.string.cinema);
            else if (category_status.equals(getString(R.string.emotions_choice)))
                Category = getString(R.string.emotions);
            else if (category_status.equals(getString(R.string.art_choice)))
                Category = getString(R.string.art);
            else if (category_status.equals(getString(R.string.sport_choice)))
                Category = getString(R.string.sport);
            else if (category_status.equals(getString(R.string.games_choice)))
                Category = getString(R.string.games);
            else if (category_status.equals(getString(R.string.vehicle_choice)))
                Category = getString(R.string.vehicle);
            else if (category_status.equals(getString(R.string.accessories_choice)))
                Category = getString(R.string.accessories);
            else if (category_status.equals(getString(R.string.kitchen_choice)))
                Category = getString(R.string.kitchen);
            else if (category_status.equals(getString(R.string.interior_decoration_choice)))
                Category = getString(R.string.interior_decoration);
            else if (category_status.equals(getString(R.string.accommodation_choice)))
                Category = getString(R.string.accommodation);
            else if (category_status.equals(getString(R.string.restoration_choice)))
                Category = getString(R.string.restoration);
            else if (category_status.equals(getString(R.string.journey_choice)))
                Category = getString(R.string.journey);
            else if (category_status.equals(getString(R.string.animals_choice)))
                Category = getString(R.string.animals);
            else if (category_status.equals(getString(R.string.entertainment_choice)))
                Category = getString(R.string.entertainment);
            else if (category_status.equals(getString(R.string.event_choice)))
                Category = getString(R.string.event);

        } else
            Category = getString(R.string.watch);

        category.setText(Category);

        relLayout_icon_home.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("from_challenge_video_home", "from_challenge_video_home");
            startActivity(intent);
        });

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

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(videos_list != null){
            videos_list.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        videos_list = new ArrayList<>();
    }

    private void getAllVideos() {
        clearAll();

        Query query = myRef.child(getString(R.string.dbname_invite_battle_media));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                    if (model.isVideo() || model.isGroup_video())
                        if (!model.isSuppressed()) {
                            if (category_status != null) {
                                if (category_status.equals(getString(R.string.love_choice))) {
                                    if (model.getInvite_category_status().equals(getString(R.string.love_choice)) && !model.isGroup_private()) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.beauty_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.beauty_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.acapella_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.acapella_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.dance_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.dance_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.comedy_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.comedy_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.swag_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.swag_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.decoration_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.decoration_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.couple_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.couple_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.cinema_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.cinema_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.emotions_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.emotions_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.art_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.art_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.sport_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.sport_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.games_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.games_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.vehicle_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.vehicle_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.accessories_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.accessories_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.kitchen_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.kitchen_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.interior_decoration_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.interior_decoration_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.accommodation_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.accommodation_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.restoration_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.restoration_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.journey_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.journey_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.animals_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.animals_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.entertainment_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.entertainment_choice))) {
                                        videos_list.add(model);
                                    }
                                } else if (category_status.equals(getString(R.string.event_choice)) && !model.isGroup_private()) {
                                    if (model.getInvite_category_status().equals(getString(R.string.event_choice))) {
                                        videos_list.add(model);
                                    }
                                }

                            } else {
                                videos_list.add(model);
                            }
                        }
                }

                //sort for newest to oldest
                videos_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                // to start adapter to some position
                for (int i = 0; i < videos_list.size(); i++) {
                    if (!modelInvite.getInvite_photoID().isEmpty()) {
                        if (videos_list.get(i).getInvite_photoID().equals(modelInvite.getInvite_photoID())) {
                            videos_list.remove(i);
                        }
                    }
                }

                videos_list.add(0, modelInvite);

                adapter = new ViewInviteVideoAdapter(context, recyclerView, progressBar, relLayout_view_overlay);
                adapter.setDefaultRecyclerView(context, R.id.recyclerView);

                if (videos_list.size() >= 2) {
                    // first download the second video _____________________________________________________
                    ModelInvite nextVideoItem = videos_list.get(1);

                    // Create a PreloadListener
                    PrepareNextVideoChallenge.PreloadListener preloadListener = new PrepareNextVideoChallenge.PreloadListener() {
                        @Override
                        public void onPreloadProgress(ModelInvite videoItem, int progress) {
                            // Handle progress updates (e.g., update a progress bar)
                            Log.d("Preload", "Progress for " + videoItem.getInvite_url() + ": " + progress + "%");
                        }

                        @Override
                        public void onPreloadComplete(ModelInvite videoItem) {
                            // Handle completion (e.g., update UI)
                            Log.d("Preload", "Complete for " + videoItem.getInvite_url());
                        }

                        @Override
                        public void onPreloadError(ModelInvite videoItem, Exception e) {
                            // Handle errors (e.g., show an error message)
                            Log.e("Preload", "Error for " + videoItem.getInvite_url(), e);
                        }
                    };

                    // Call preDownloadVideo with the listener
                    preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
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
                                        if (nextVideoPosition < videos_list.size()) {
                                            ModelInvite nextVideoItem = videos_list.get(nextVideoPosition);

                                            // Create a PreloadListener
                                            PrepareNextVideoChallenge.PreloadListener preloadListener = new PrepareNextVideoChallenge.PreloadListener() {
                                                @Override
                                                public void onPreloadProgress(ModelInvite videoItem, int progress) {
                                                    // Handle progress updates (e.g., update a progress bar)
                                                    Log.d("Preload", "Progress for " + videoItem.getInvite_url() + ": " + progress + "%");
                                                }

                                                @Override
                                                public void onPreloadComplete(ModelInvite videoItem) {
                                                    // Handle completion (e.g., update UI)
                                                    Log.d("Preload", "Complete for " + videoItem.getInvite_url());
                                                }

                                                @Override
                                                public void onPreloadError(ModelInvite videoItem, Exception e) {
                                                    // Handle errors (e.g., show an error message)
                                                    Log.e("Preload", "Error for " + videoItem.getInvite_url(), e);
                                                }
                                            };

                                            // Call preDownloadVideo with the listener
                                            preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
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
                        //context.runOnUiThread(() -> Toast.makeText(context, context.getString(R.string.you_watched_everything), Toast.LENGTH_SHORT).show());
                    }
                });


                getNewItems(adapter.getStartPage());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void getMyVideos() {
        clearAll();

        Query query = myRef.child(getString(R.string.dbname_invite_battle))
                .child(userID)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                    if (model.isVideo() || model.isGroup_video())
                        if (!model.isSuppressed())
                            videos_list.add(model);
                }

                //sort for newest to oldest
                videos_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                // to start adapter to some position
                for (int i = 0; i < videos_list.size(); i++) {
                    if (!modelInvite.getInvite_photoID().isEmpty()) {
                        if (videos_list.get(i).getInvite_photoID().equals(modelInvite.getInvite_photoID())) {
                            videos_list.remove(i);
                        }
                    }
                }

                videos_list.add(0, modelInvite);

                adapter = new ViewInviteVideoAdapter(context, recyclerView, progressBar, relLayout_view_overlay);
                adapter.setDefaultRecyclerView(context, R.id.recyclerView);

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
                        //Toast.makeText(context, context.getString(R.string.you_watched_everything), Toast.LENGTH_SHORT).show();
                    }
                });


                getNewItems(adapter.getStartPage());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    public void onGetDate(List<ModelInvite> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        new Handler().postDelayed(() -> {
            List<ModelInvite> models = new ArrayList<>();
            int start = page * counter - counter;
            int end = page * counter;
            for (int i = start; i < end; i++) {
                if (i < videos_list.size()) {
                    models.add(videos_list.get(i));
                }
            }
            onGetDate(models);
        }, 1000);
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