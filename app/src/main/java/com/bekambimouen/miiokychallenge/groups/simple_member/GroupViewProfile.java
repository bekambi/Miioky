package com.bekambimouen.miiokychallenge.groups.simple_member;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.adapter.AddNestedScrollView;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.GroupPubChallengePhoto;
import com.bekambimouen.miiokychallenge.groups.publication.GroupPubChallengeVideo;
import com.bekambimouen.miiokychallenge.groups.publication.GroupPubCommentText;
import com.bekambimouen.miiokychallenge.groups.publication.GroupPublicationPhoto;
import com.bekambimouen.miiokychallenge.groups.simple_member.adapter.GroupViewProfileAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupViewProfile extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "GroupViewProfileFragment";

    // Camera Permissions
    private final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // widgets
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView profile_photo_toolbar;
    private RelativeLayout relLayout_board, relLayout_challenges, relLayout_publisher, relLayout_thought,
            relLayout_view_overlay;
    private LinearLayout linLayout_challenges, linLayout_publisher, linLayout_thought;
    private ProgressBar progressBar;

    // theme
    private RelativeLayout parent, arrowBack;
    private Toolbar toolBar;
    private TextView toolBar_group_name;
    private ImageView close;

    // vars
    private GroupViewProfile context;
    private GroupViewProfileAdapter adapter;
    private ArrayList<BattleModel> list;
    private UserGroups user_groups;
    private Handler handler;

    // pagination constants
    int counter = 10;

    private String group_id, admin_master;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view_profile);
        // to disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (user_groups != null) {
            admin_master = user_groups.getAdmin_master();
            group_id = user_groups.getGroup_id();
        }

        init();
        theme();
        getUserData();
        getPhotos();
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
        parent = findViewById(R.id.parent);
        arrowBack = findViewById(R.id.arrowBack);
        toolBar = findViewById(R.id.toolBar);
        toolBar_group_name = findViewById(R.id.toolBar_group_name);
        close = findViewById(R.id.close);

        profile_photo_toolbar = findViewById(R.id.profile_photo_toolbar);
        relLayout_board = findViewById(R.id.relLayout_board);
        relLayout_challenges = findViewById(R.id.relLayout_challenges);
        relLayout_publisher = findViewById(R.id.relLayout_publisher);
        relLayout_thought = findViewById(R.id.relLayout_thought);
        linLayout_challenges = findViewById(R.id.linLayout_challenges);
        linLayout_publisher = findViewById(R.id.linLayout_publisher);
        linLayout_thought = findViewById(R.id.linLayout_thought);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

    private void theme() {
        String theme = user_groups.getGroup_theme();

        if (theme.equals(context.getString(R.string.theme_normal))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);

            // changer a couleur des icons en noir
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            parent.setBackgroundResource(R.drawable.toolbar_blue_grey_50);
            toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
            toolBar_group_name.setTextColor(context.getColor(R.color.black));
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            close.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_blue))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.blue_600));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_blue_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_brown))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.background_brown));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_brown_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_pink))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.pink));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_pink_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_green))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.vertWatsApp));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_green_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_black))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.black));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            parent.setBackgroundResource(R.drawable.toolbar_black_clear_background);
            toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void getUserData() {
        toolBar_group_name.setText(user_groups.getGroup_name());

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(user_groups.getProfile_photo())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(profile_photo_toolbar);

        // to know if current user is admin
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (follower.isSuspended())
                        relLayout_board.setVisibility(View.GONE);

                    // challenges click
                    relLayout_challenges.setOnClickListener(view1 -> challengesClick(relLayout_challenges, user_groups));
                    linLayout_challenges.setOnClickListener(view1 -> challengesClick(linLayout_challenges, user_groups));

                    // publisher click
                    relLayout_publisher.setOnClickListener(view -> publisherClick(user_groups));
                    linLayout_publisher.setOnClickListener(view -> publisherClick(user_groups));

                    // thought click
                    relLayout_thought.setOnClickListener(view -> thoughtClick(user_groups));
                    linLayout_thought.setOnClickListener(view -> thoughtClick(user_groups));
                }

                // user is not the follower
                if (!snapshot.exists()) {
                    // challenges click
                    relLayout_challenges.setOnClickListener(view1 -> challengesClick(relLayout_challenges, user_groups));
                    linLayout_challenges.setOnClickListener(view1 -> challengesClick(linLayout_challenges, user_groups));

                    // publisher click
                    relLayout_publisher.setOnClickListener(view -> publisherClick(user_groups));
                    linLayout_publisher.setOnClickListener(view -> publisherClick(user_groups));

                    // thought click
                    relLayout_thought.setOnClickListener(view -> thoughtClick(user_groups));
                    linLayout_thought.setOnClickListener(view -> thoughtClick(user_groups));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // publisher click
    private void publisherClick(UserGroups user_groups) {
        if (!allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupPublicationPhoto.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
        }
    }

    // thought click
    private void thoughtClick(UserGroups user_groups) {
        if (relLayout_view_overlay != null)
            relLayout_view_overlay.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        String myGson = gson.toJson(user_groups);
        context.getWindow().setExitTransition(new Slide(Gravity.END));
        context.getWindow().setEnterTransition(new Slide(Gravity.START));
        Intent intent = new Intent(context, GroupPubCommentText.class);
        intent.putExtra("user_groups", myGson);
        startActivity(intent);
    }

    // challenges click
    private void challengesClick(View view, UserGroups user_groups) {
        if (!allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

        } else {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, view);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_group_publication);
            //adding click listener
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_pub_photo) {
                    popup.dismiss();
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupPubChallengePhoto.class);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(user_groups);
                    intent.putExtra("user_groups", myGson);
                    startActivity(intent);

                } else if (menuItem.getItemId() == R.id.menu_pub_video) {
                    popup.dismiss();
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupPubChallengeVideo.class);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(user_groups);
                    intent.putExtra("user_groups", myGson);
                    startActivity(intent);
                }
                return false;
            });

            //displaying the popup
            popup.show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting list of photos");
        clearAll();
        Query query = myRef
                .child(context.getString(R.string.dbname_group))
                .child(group_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    if (!model.isSuppressed())
                        list.add(model);
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
        // add the header
        list.add(0, AddNestedScrollView.getBattleModel(true,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false));

        adapter = new GroupViewProfileAdapter(context, list, admin_master, group_id, user_groups, progressBar, relLayout_view_overlay);
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

            }
        });


        getNewItems(adapter.getStartPage());

        // hide/show board
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        System.out.println("The RecyclerView is not scrolling");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        System.out.println("Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        System.out.println("Scroll Settling");
                        break;

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dx > 0) {
                    System.out.println("Scrolled Right");
                } else if (dx < 0) {
                    System.out.println("Scrolled Left");
                } else {
                    System.out.println("No Horizontal Scrolled");
                }

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");

                    if (relLayout_board.getVisibility() == View.VISIBLE) {
                        relLayout_board.setVisibility(View.GONE);
                        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, relLayout_board.getHeight());
                        animate.setDuration(700);
                        relLayout_board.startAnimation(animate);
                    }

                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    if (relLayout_board.getVisibility() == View.GONE) {
                        // visibility of view
                        relLayout_board.setVisibility(View.VISIBLE);
                        TranslateAnimation animate = new TranslateAnimation(0, 0, relLayout_board.getHeight(), 0);

                        // duration of animation
                        animate.setDuration(700);
                        animate.setFillAfter(true);
                        relLayout_board.startAnimation(animate);
                    }

                } else {
                    System.out.println("No Vertical Scrolled");
                }
            }
        });
    }

    public void onGetDate(List<BattleModel> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        new Handler().postDelayed(() -> {
            List<BattleModel> models = new ArrayList<>();
            int start = page * counter - counter;
            int end = page * counter;
            for (int i = start; i < end; i++) {
                if (i < list.size()) {
                    models.add(list.get(i));
                }
            }
            onGetDate(models);
        }, 1000);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
        theme();

        // to know if current user is still member of group and to prevent single field adding in firebase
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // get the user's last visit in this group _____________________________________
                    Date date = new Date();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("lastSeenInGroup", date.getTime());

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id)
                            .child(user_groups.getGroup_id())
                            .updateChildren(map);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(user_groups.getGroup_id())
                            .child(user_id)
                            .updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}