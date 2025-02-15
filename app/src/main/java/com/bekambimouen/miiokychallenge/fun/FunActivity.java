package com.bekambimouen.miiokychallenge.fun;

import static com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFun.preDownloadVideo;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.fun.adapter.MainFunAdapter;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.fun.publication.CameraVideoFun;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFun;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel_fun;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class FunActivity extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "FunActivity";
    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private Container recyclerView;
    private RelativeLayout parent, relLayout2, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private FunActivity context;
    private MainFunAdapter adapter;
    private ArrayList<BattleModel_fun> mVideos, pagination;
    private ArrayList<String> mFollowing;
    private String from_fun_publication, from_suggestion_fun;
    private Handler handler;
    private Runnable preDownloadRunnable;
    private int resultsCount = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

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
        // to disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_fun);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            from_fun_publication = getIntent().getStringExtra("from_fun_publication");
            from_suggestion_fun = getIntent().getStringExtra("from_suggestion_fun");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        new Thread(this::getFriendsFollowing).start();
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
        RelativeLayout backArrow = findViewById(R.id.backArrow);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        ImageView camera_id_fun = findViewById(R.id.camera_id_fun);

        recyclerView = findViewById(R.id.container);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
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

        relLayout2 = findViewById(R.id.relLayout2);

        camera_id_fun.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent i = new Intent(this, CameraVideoFun.class);
                startActivity(i);
            }
        });

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
                context.startActivity(intent);
            }
        });

        backArrow.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (from_fun_publication != null)
                    startActivity(new Intent(context, MainActivity.class));
                else if (from_suggestion_fun != null)
                    startActivity(new Intent(context, MainActivity.class));
                else {
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

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(mFollowing != null){
            mFollowing.clear();
        }

        if(mVideos != null){
            mVideos.clear();
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

        mFollowing = new ArrayList<>();
        mVideos = new ArrayList<>();
        pagination = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    /**
     //     * Récupérer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     //     */
    private void getFriendsFollowing() {
        Log.d(TAG, "getFriendFollowing: searching for following");
        clearAll();

        //also add your own id to the list
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_friend_following))
                    .child(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        FriendsFollowerFollowing following = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                        String followingId = Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString();

                        // verify if user is unsubscribe from his friend
                        if (!following.isUnSubscribe()) {
                            mFollowing.add(followingId);
                        }
                    }

                    getFollowing();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        //also add your own id to the list
        mFollowing.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    mFollowing.add(Objects.requireNonNull(ds.child(getString(R.string.field_user_id)).getValue()).toString());
                }

                getVideos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getVideos(){
        Log.d(TAG, "getPhotos: getting list of photos");

        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_fun))
                    .child(mFollowing.get(i)) // user_id
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds :  dataSnapshot.getChildren()){
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, ds);

                        if (!model.isSuppressed())
                            mVideos.add(model);
                    }

                    if(count >= mFollowing.size() - 1){
                        //display the photos
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

    @SuppressLint("NotifyDataSetChanged")
    private void displayVideos(){
        //sort for newest to oldest
        mVideos.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        int iterations = mVideos.size();

        if(iterations > 1){
            iterations = 1;
        }

        resultsCount = 0;
        for(int i = 0; i < iterations; i++){
            pagination.add(mVideos.get(i));
            resultsCount++;
        }

        adapter = new MainFunAdapter(this, pagination, recyclerView, progressBar,
                this, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);

        if (mVideos.size() >= 2) {
            // first download the second video _____________________________________________________
            BattleModel_fun nextVideoItem = mVideos.get(1);

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
                                if (nextVideoPosition < mVideos.size()) {
                                    BattleModel_fun nextVideoItem = mVideos.get(nextVideoPosition);

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

        context.runOnUiThread(() -> {
            if (adapter.getItemCount() == 0) {
                progressBar.setVisibility(View.GONE);
                relLayout2.setVisibility(View.VISIBLE);
            } else {
                relLayout2.setVisibility(View.GONE);
            }
        });
    }

    public void displayMore() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(mVideos.size() > resultsCount && !mVideos.isEmpty()){

                int iterations;
                if(mVideos.size() > (resultsCount + 1)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 1;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mVideos.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(mVideos.get(i));
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
        displayMore();
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