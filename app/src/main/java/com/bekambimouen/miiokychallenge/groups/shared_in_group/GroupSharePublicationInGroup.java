package com.bekambimouen.miiokychallenge.groups.shared_in_group;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.shared_in_group.adapter.ShareInGroupAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupSharePublicationInGroup extends AppCompatActivity {
    private static final String TAG = "GroupSharePublicationInGroup";
    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText edit_search;
    private ImageView erase;
    private ProgressBar loading_progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private GroupSharePublicationInGroup context;
    private ShareInGroupAdapter adapter;
    private BattleModel model;
    private String value;
    private ArrayList<UserGroups> list;
    public ArrayList<String> followingUserList;
    public ArrayList<String> group_id_List;
    public ArrayList<String> followingUserIDList;
    public ArrayList<String> userIDList;
    private Handler handler;

    // share on group
    private boolean group_share_single_bottom_circle_image;
    private boolean group_share_single_bottom_image_double;
    private boolean group_share_single_bottom_image_une;
    private boolean group_share_single_bottom_recycler;
    private boolean group_share_single_bottom_response_image_double;
    private boolean group_share_single_bottom_response_video_double;
    private boolean group_share_single_bottom_video_double;
    private boolean group_share_single_bottom_video_une;

    private boolean group_share_top_bottom_circle_image;
    private boolean group_share_top_bottom_image_double;
    private boolean group_share_top_bottom_image_une;
    private boolean group_share_top_bottom_recycler;
    private boolean group_share_top_bottom_response_image_double;
    private boolean group_share_top_bottom_response_video_double;
    private boolean group_share_top_bottom_video_double;
    private boolean group_share_top_bottom_video_une;

    private boolean group_share_single_top_circle_image;
    private boolean group_share_single_top_image_double;
    private boolean group_share_single_top_image_une;
    private boolean group_share_single_top_recycler;
    private boolean group_share_single_top_response_image_double;
    private boolean group_share_single_top_response_video_double;
    private boolean group_share_single_top_video_double;
    private boolean group_share_single_top_video_une;

    private boolean group_circle_image;
    private boolean group_image_double;
    private boolean group_image_une;
    private boolean group_recycler;
    private boolean group_response_image_double;
    private boolean group_response_video_double;
    private boolean group_video_double;
    private boolean group_video_une;

    // share on Miioky
    private boolean user_profile_shared;
    private boolean recyclerItem_shared;
    private boolean imageUne_shared;
    private boolean videoUne_shared;
    private boolean imageDouble_shared;
    private boolean videoDouble_shared;
    private boolean reponseImageDouble_shared;
    private boolean reponseVideoDouble_shared;

    private boolean circle_image;
    private boolean image_double;
    private boolean image_une;
    private boolean recycler;
    private boolean response_image_double;
    private boolean response_video_double;
    private boolean video_double;
    private boolean video_une;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_group_share_publication_in_group);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            model = gson.fromJson(getIntent().getStringExtra("model"), BattleModel.class);
            value = getIntent().getStringExtra("value");

            group_share_single_bottom_circle_image = getIntent().getBooleanExtra("group_share_single_bottom_circle_image", false);
            group_share_single_bottom_image_double = getIntent().getBooleanExtra("group_share_single_bottom_image_double", false);
            group_share_single_bottom_image_une = getIntent().getBooleanExtra("group_share_single_bottom_image_une", false);
            group_share_single_bottom_recycler = getIntent().getBooleanExtra("group_share_single_bottom_recycler", false);
            group_share_single_bottom_response_image_double = getIntent().getBooleanExtra("group_share_single_bottom_response_image_double", false);
            group_share_single_bottom_response_video_double = getIntent().getBooleanExtra("group_share_single_bottom_response_video_double", false);
            group_share_single_bottom_video_double = getIntent().getBooleanExtra("group_share_single_bottom_video_double", false);
            group_share_single_bottom_video_une = getIntent().getBooleanExtra("group_share_single_bottom_video_une", false);

            group_share_single_top_circle_image = getIntent().getBooleanExtra("group_share_single_top_circle_image", false);
            group_share_single_top_image_double = getIntent().getBooleanExtra("group_share_single_top_image_double", false);
            group_share_single_top_image_une = getIntent().getBooleanExtra("group_share_single_top_image_une", false);
            group_share_single_top_recycler = getIntent().getBooleanExtra("group_share_single_top_recycler", false);
            group_share_single_top_response_image_double = getIntent().getBooleanExtra("group_share_single_top_response_image_double", false);
            group_share_single_top_response_video_double = getIntent().getBooleanExtra("group_share_single_top_response_video_double", false);
            group_share_single_top_video_double = getIntent().getBooleanExtra("group_share_single_top_video_double", false);
            group_share_single_top_video_une = getIntent().getBooleanExtra("group_share_single_top_video_une", false);

            group_share_top_bottom_circle_image = getIntent().getBooleanExtra("group_share_top_bottom_circle_image", false);
            group_share_top_bottom_image_double = getIntent().getBooleanExtra("group_share_top_bottom_image_double", false);
            group_share_top_bottom_image_une = getIntent().getBooleanExtra("group_share_top_bottom_image_une", false);
            group_share_top_bottom_recycler = getIntent().getBooleanExtra("group_share_top_bottom_recycler", false);
            group_share_top_bottom_response_image_double = getIntent().getBooleanExtra("group_share_top_bottom_response_image_double", false);
            group_share_top_bottom_response_video_double = getIntent().getBooleanExtra("group_share_top_bottom_response_video_double", false);
            group_share_top_bottom_video_double = getIntent().getBooleanExtra("group_share_top_bottom_video_double", false);
            group_share_top_bottom_video_une = getIntent().getBooleanExtra("group_share_top_bottom_video_une", false);

            user_profile_shared = getIntent().getBooleanExtra("user_profile_shared", false);
            recyclerItem_shared = getIntent().getBooleanExtra("recyclerItem_shared", false);
            imageUne_shared = getIntent().getBooleanExtra("imageUne_shared", false);
            videoUne_shared = getIntent().getBooleanExtra("videoUne_shared", false);
            imageDouble_shared = getIntent().getBooleanExtra("imageDouble_shared", false);
            videoDouble_shared = getIntent().getBooleanExtra("videoDouble_shared", false);
            reponseImageDouble_shared = getIntent().getBooleanExtra("reponseImageDouble_shared", false);
            reponseVideoDouble_shared = getIntent().getBooleanExtra("reponseVideoDouble_shared", false);

            group_circle_image = getIntent().getBooleanExtra("group_circle_image", false);
            group_image_double = getIntent().getBooleanExtra("group_image_double", false);
            group_image_une = getIntent().getBooleanExtra("group_image_une", false);
            group_recycler = getIntent().getBooleanExtra("group_recycler", false);
            group_response_image_double = getIntent().getBooleanExtra("group_response_image_double", false);
            group_response_video_double = getIntent().getBooleanExtra("group_response_video_double", false);
            group_video_double = getIntent().getBooleanExtra("group_video_double", false);
            group_video_une = getIntent().getBooleanExtra("group_video_une", false);

            circle_image = getIntent().getBooleanExtra("circle_image", false);
            image_double = getIntent().getBooleanExtra("image_double", false);
            image_une = getIntent().getBooleanExtra("image_une", false);
            recycler = getIntent().getBooleanExtra("recycler", false);
            response_image_double = getIntent().getBooleanExtra("response_image_double", false);
            response_video_double = getIntent().getBooleanExtra("response_video_double", false);
            video_double = getIntent().getBooleanExtra("video_double", false);
            video_une = getIntent().getBooleanExtra("video_une", false);

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getGroupFollowing();

        fetchUsers();
        adapter = new ShareInGroupAdapter(context, list, model, value,
                group_share_single_bottom_circle_image, group_share_single_bottom_image_double,
                group_share_single_bottom_image_une, group_share_single_bottom_recycler, group_share_single_bottom_response_image_double,
                group_share_single_bottom_response_video_double, group_share_single_bottom_video_double, group_share_single_bottom_video_une,
                group_share_single_top_circle_image, group_share_single_top_image_double,
                group_share_single_top_image_une, group_share_single_top_recycler, group_share_single_top_response_image_double,
                group_share_single_top_response_video_double, group_share_single_top_video_double, group_share_single_top_video_une,
                group_share_top_bottom_circle_image, group_share_top_bottom_image_double,
                group_share_top_bottom_image_une, group_share_top_bottom_recycler, group_share_top_bottom_response_image_double,
                group_share_top_bottom_response_video_double, group_share_top_bottom_video_double, group_share_top_bottom_video_une,
                user_profile_shared, imageDouble_shared, imageUne_shared, recyclerItem_shared, reponseImageDouble_shared,
                reponseVideoDouble_shared, videoDouble_shared, videoUne_shared,

                group_circle_image, group_image_double, group_image_une, group_recycler, group_response_image_double,
                group_response_video_double, group_video_double, group_video_une, circle_image, image_double, image_une,
                recycler, response_image_double, response_video_double, video_double, video_une,
                loading_progressBar, relLayout_view_overlay);
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        edit_search = findViewById(R.id.edit_search);
        erase = findViewById(R.id.erase);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int size = charSequence.length();
                if (size != 0)
                    erase.setVisibility(View.VISIBLE);
                else
                    erase.setVisibility(View.GONE);
                // filter recycler view when text is changed
                adapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        erase.setOnClickListener(view -> edit_search.setText(""));

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

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(list != null){
            list.clear();
        }
        if(followingUserList != null){
            followingUserList.clear();
        }
        if(group_id_List != null){
            group_id_List.clear();
        }
        if(followingUserIDList != null){
            followingUserIDList.clear();
        }
        if(userIDList != null){
            userIDList.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list = new ArrayList<>();
        followingUserList = new ArrayList<>();
        followingUserIDList = new ArrayList<>();
        group_id_List = new ArrayList<>();
        userIDList = new ArrayList<>();
    }

    /**
     // * RÃ©cupÃ©rer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     // */
    private void getGroupFollowing() {
        clearAll();

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_following))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // first add current user id
                userIDList.add(user_id);

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    GroupFollowersFollowing following = new GroupFollowersFollowing();

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    following.setAdmin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString());
                    String admin_id = following.getAdmin_master();
                    if (!userIDList.contains(admin_id))
                        userIDList.add(admin_id);
                }
                getUserIdList(userIDList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query myQuery1 = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_id);

        myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get all current user groups
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String groupID = ds.getKey();
                    group_id_List.add(groupID);
                }

                // add all other following groups
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String followingId = ds.getKey();
                            group_id_List.add(followingId);
                        }

                        getGroups(group_id_List);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get list of all followings
    private void getUserIdList(ArrayList<String> following_usersIDList) {
        followingUserIDList.addAll(following_usersIDList);
    }

    private void getGroups(ArrayList<String> group_id_List) {
        if (!followingUserIDList.isEmpty()) {
            for(int i = 0; i < followingUserIDList.size(); i++){
                final int count_user_list = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(followingUserIDList.get(i))
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(followingUserIDList.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            for (String groupKey : group_id_List) {
                                if (user_groups.getGroup_id().equals(groupKey))
                                    list.add(user_groups);
                            }
                        }

                        if(count_user_list >= followingUserIDList.size() -1){
                            list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                    .compareTo(String.valueOf(o1.getDate_created())));

                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    /**
     * fetches json by making http calls
     */
    private void fetchUsers() {
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(URL,
                response -> {
                    if (response == null) {
                        Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<UserGroups> items = new Gson().fromJson(response.toString(), new TypeToken<List<User>>() {
                    }.getType());

                    // adding contacts to contacts list
                    list.clear();
                    list.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.e(TAG, "Error: " + error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
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