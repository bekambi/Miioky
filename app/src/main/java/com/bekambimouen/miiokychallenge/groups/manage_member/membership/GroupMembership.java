package com.bekambimouen.miiokychallenge.groups.manage_member.membership;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.adapter.GroupMembershipAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupMembershipModel;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupMembershipModel;
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

public class GroupMembership extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "GroupMembership";

    // widgets
    private RecyclerView recyclerView;
    private TextView number_of_demand;
    private LinearLayout linLayout_number_of_membership, linLayout_buttons;
    private RelativeLayout parent, relLayout_button_join, relLayout_button_cancel;
    private RelativeLayout relLayout_no_membership_request, relLayout_view_overlay;
    private ProgressBar progressBar, loading_progressBar;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_textview;
    private Toolbar toolBar;

    // vars
    private GroupMembership context;
    private GroupMembershipAdapter adapter;
    private UserGroups user_groups;
    private ArrayList<GroupMembershipModel> list, pagination;
    private int resultsCount = 0,  n = 0, m = 0, t = 0;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private GroupMembershipModel membershipModel;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_membership);
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

        init();
        theme();
        getMemberShipRequestsCount();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
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

            toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
            toolBar_textview.setTextColor(context.getColor(R.color.black));
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            close.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_blue))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.blue_600));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
            toolBar_textview.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_brown))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.background_brown));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
            toolBar_textview.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_pink))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.pink));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
            toolBar_textview.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_green))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.vertWatsApp));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
            toolBar_textview.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_black))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.black));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
            toolBar_textview.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        progressBar = findViewById(R.id.progressBar);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        toolBar = findViewById(R.id.toolBar);
        arrowBack = findViewById(R.id.arrowBack);
        toolBar_textview = findViewById(R.id.toolBar_textview);
        close = findViewById(R.id.close);

        linLayout_buttons = findViewById(R.id.linLayout_buttons);
        linLayout_number_of_membership = findViewById(R.id.linLayout_number_of_membership);
        relLayout_button_join = findViewById(R.id.relLayout_button_join);
        relLayout_button_cancel = findViewById(R.id.relLayout_button_cancel);
        relLayout_no_membership_request = findViewById(R.id.relLayout_no_membership_request);
        number_of_demand = findViewById(R.id.number_of_demand);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        toolBar_textview.setText(context.getString(R.string.membership_request));

        getAllMemberShip();

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

    // get all the membership requests
    private void getAllMemberShip() {
        clearAll();
        Query query = myRef
                .child(getString(R.string.dbname_group_Membership_request_follower))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    membershipModel = Util_GroupMembershipModel.getGroupMembershipModel(context, objectMap);
                    list.add(membershipModel);
                }

                if (!snapshot.exists()) {
                    linLayout_number_of_membership.setVisibility(View.GONE);
                    linLayout_buttons.setVisibility(View.GONE);
                } else {
                    linLayout_number_of_membership.setVisibility(View.VISIBLE);
                    linLayout_buttons.setVisibility(View.VISIBLE);
                }

                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                int iterations = list.size();

                if(iterations > 20){
                    iterations = 20;
                }

                resultsCount = 20;
                for(int i = 0; i < iterations; i++){
                    pagination.add(list.get(i));
                }

                adapter = new GroupMembershipAdapter(context, pagination, GroupMembership.this, progressBar,
                        loading_progressBar, relLayout_view_overlay);
                recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    relLayout_no_membership_request.setVisibility(View.VISIBLE);
                }
                ;
                // approve or cancel all membership
                relLayout_button_join.setOnClickListener(view -> {
                    for (int i = 0; i < list.size(); i++) {
                        approveAllMembership(list.get(i).getUser_id(), membershipModel, list.size());
                    }
                });

                // refuse all membership
                relLayout_button_cancel.setOnClickListener(view -> {
                    for (int i = 0; i < list.size(); i++) {
                        refuseAllMembership(list.get(i).getUser_id(), membershipModel, list.size());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get membership request number
    private void getMemberShipRequestsCount() {
        Log.d(TAG, "getPhotos: getting list of posts");
        Query query = myRef
                .child(getString(R.string.dbname_group_Membership_request_follower))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                    n++;
                }

                if (n == 1)
                    number_of_demand.setText(Html.fromHtml(" <b>"+n +"</b> "+context.getString(R.string.asked)));
                else
                    number_of_demand.setText(Html.fromHtml(" <b>"+n +"</b> "+context.getString(R.string.askeds)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // approve all membership
    private void approveAllMembership(String userID, GroupMembershipModel model, int size) {
        m++;
        // create the count to the user who asked_______________________________________________
        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(model.getAdmin_master(),
                userID, user_id, model.getGroup_id());

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_following))
                .child(userID)
                .child(model.getGroup_id())
                .setValue(map);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(model.getGroup_id())
                .child(userID)
                .setValue(map);
        // _____________________________________________________________________________________

        // remove membership request
        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_Membership_request_following))
                .child(userID)
                .child(model.getGroup_id())
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_Membership_request_follower))
                .child(model.getGroup_id())
                .child(userID)
                .removeValue();

        // send notification
        Date date = new Date();
        String new_key = myRef.push().getKey();
        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                false, false, false, false, false,
                false,false, false, false,
                false, false, false, false, false, false,
                false,
                false, false, false, false, false,
                false, true,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true,false, false,
                userID, new_key, userID, model.getAdmin_master(),
                "", model.getGroup_id(), date.getTime(),
                false, false,
                false, false, false, false, false, false, false, false,
                false, "", "", "", false, "", "", "", false,
                "", "", "", "", "", 0,
                "", "", 0, "", "", "",
                true, false, true, false,
                false, false, false,
                false, false);

        assert new_key != null;
        myRef.child(context.getString(R.string.dbname_notification))
                .child(userID)
                .child(new_key)
                .setValue(map_notification);

        // add badge number
        HashMap<String, Object> map_number = new HashMap<>();
        map_number.put("user_id", userID);

        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                .child(userID)
                .child(new_key)
                .setValue(map_number);

        if (m == size) {
            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // refuse all membership
    private void refuseAllMembership(String userID, GroupMembershipModel model, int size) {
        t++;

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_Membership_request_following))
                .child(userID)
                .child(model.getGroup_id())
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_Membership_request_follower))
                .child(model.getGroup_id())
                .child(userID)
                .removeValue();

        // send notification
        Date date = new Date();
        String new_key = myRef.push().getKey();
        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                false, false, false, false, false,
                false,false, false, false,
                false, false, false, false, false, false,
                false,
                false, false, false, false, false,
                false, true,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true,false, false,
                userID, new_key, userID, model.getAdmin_master(),
                "", model.getGroup_id(), date.getTime(),
                false, false,
                false, false, false, false, false, false, false, false,
                false, "", "", "", false, "", "", "", false,
                "", "", "", "", "", 0,
                "", "", 0, "", "", "",
                true, false, false, true,
                false, false, false,
                false, false);

        assert new_key != null;
        myRef.child(context.getString(R.string.dbname_notification))
                .child(userID)
                .child(new_key)
                .setValue(map_notification);

        // add badge number
        HashMap<String, Object> map_number = new HashMap<>();
        map_number.put("user_id", userID);

        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                .child(userID)
                .child(new_key)
                .setValue(map_number);

        if (t == size) {
            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(list.size() > resultsCount && !list.isEmpty()){

                int iterations;
                if(list.size() > (resultsCount + 20)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
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
        loading_progressBar.setVisibility(View.VISIBLE);
        displayMorePhotos();
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