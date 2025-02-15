package com.bekambimouen.miiokychallenge.profile;

import static com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFun.preDownloadVideo;
import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.fun.publication.CameraVideoFun;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFun;
import com.bekambimouen.miiokychallenge.profile.adapter.ViewPostFunAdapter;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel_fun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewPostsFun extends AppCompatActivity implements OnLoadMoreItemsListener, CacheListener {
    private static final String TAG = "ViewPostsFun";
    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private Container recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private ViewPostsFun context;
    private ViewPostFunAdapter adapter;
    private User from_view_profile;
    private ArrayList<BattleModel_fun> list, pagination;
    private int position;
    private int resultsCount = 0;
    private String user_id;
    private String from_fun_publication;
    private Handler handler;
    private Runnable preDownloadRunnable;


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
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_view_posts_fun);
        context = this;
        getBlackTheme();

        handler = new Handler(Looper.getMainLooper());

        try {
            from_fun_publication = getIntent().getStringExtra("from_fun_publication");
            Gson gson = new Gson();
            from_view_profile = gson.fromJson(getIntent().getStringExtra("from_view_battle_fun"), User.class);
            position = getIntent().getIntExtra("position", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (from_view_profile != null)
            user_id = from_view_profile.getUser_id();
        else
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        initViews();
        getUserFunPosts();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout backArrow = findViewById(R.id.backArrow);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(recyclerView);

        requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(position);

        relLayout_publish.setOnClickListener(view -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 102;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CameraVideoFun.class);
                intent.putExtra("from_fun_profile", "data");
                context.startActivity(intent);
            }
        });

        backArrow.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            if (from_fun_publication != null) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("from_profile_fun", "from_profile_fun");
                startActivity(intent);

            } else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (from_fun_publication != null) {
                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra("from_profile_fun", "data");
                    startActivity(intent);
                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent i = new Intent(context, CameraVideoFun.class);
            startActivity(i);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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

    private void getUserFunPosts() {
        clearAll();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_fun))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, ds);

                    if (!model.isSuppressed())
                        list.add(model);
                }

                display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void display() {
        //sort for newest to oldest
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

        adapter = new ViewPostFunAdapter(context, pagination, this, recyclerView, progressBar, relLayout_view_overlay);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        if (list.size() >= 2) {
            // first download the second video _____________________________________________________
            BattleModel_fun nextVideoItem = list.get(1);

            // Create a PreloadListener
            PrepareNextVideoFun.PreloadListener preloadListener = new PrepareNextVideoFun.PreloadListener() {
                @Override
                public void onPreloadProgress(BattleModel_fun videoItem, int progress) {
                    // Handle progress updates (e.g., update a progress bar)
                    Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                }

                @Override
                public void onPreloadComplete(BattleModel_fun videoItem) {
                    // Handle completion (e.g., update UI)
                    Log.d("Preload", "Complete for " + videoItem.getUrl());
                }

                @Override
                public void onPreloadError(BattleModel_fun videoItem, Exception e) {
                    // Handle errors (e.g., show an error message)
                    Log.e("Preload", "Error for " + videoItem.getUrl(), e);
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
                                if (nextVideoPosition < list.size()) {
                                    BattleModel_fun nextVideoItem = list.get(nextVideoPosition);

                                    // Create a PreloadListener
                                    PrepareNextVideoFun.PreloadListener preloadListener = new PrepareNextVideoFun.PreloadListener() {
                                        @Override
                                        public void onPreloadProgress(BattleModel_fun videoItem, int progress) {
                                            // Handle progress updates (e.g., update a progress bar)
                                            Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                                        }

                                        @Override
                                        public void onPreloadComplete(BattleModel_fun videoItem) {
                                            // Handle completion (e.g., update UI)
                                            Log.d("Preload", "Complete for " + videoItem.getUrl());
                                        }

                                        @Override
                                        public void onPreloadError(BattleModel_fun videoItem, Exception e) {
                                            // Handle errors (e.g., show an error message)
                                            Log.e("Preload", "Error for " + videoItem.getUrl(), e);
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
    }

    public void displayMoreVidos() {
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
        displayMoreVidos();
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

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