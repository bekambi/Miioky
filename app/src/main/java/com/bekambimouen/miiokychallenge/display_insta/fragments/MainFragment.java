package com.bekambimouen.miiokychallenge.display_insta.fragments;

import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.ViewMyChallenges;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.MyChallenges;
import com.bekambimouen.miiokychallenge.display_insta.Utils_list_with_nested_recyclerview;
import com.bekambimouen.miiokychallenge.display_insta.adapter.MainAdapter;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.BackPressedListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.publication_insta.PubCommentText;
import com.bekambimouen.miiokychallenge.publication_insta.PubInstagPhoto;
import com.bekambimouen.miiokychallenge.suggestions.SeeAllSuggestions;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.blongho.country_data.World;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BackPressedListener {
    private static final String TAG = "MainFragment";

    // creating object of Backpressedlistener interface
    public static BackPressedListener backpressedlistener;

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
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_board, relLayout_view_overlay;

    // vars
    private MainActivity context;
    private MainAdapter adapter;
    private ArrayList<BattleModel> list, final_list;
    private ArrayList<String> mFollowing, friendFollowing, groupFollowing;
    private String from_home_activity, from_view_video, from_challenge_video_home;
    private String user_id, from_suggestion;
    private Handler handler;
    private Dialog d;
    private boolean isFirstLoaded = true;

    // _____________________________________________________________________________________________
    private ArrayList<BattleModel> suggestion_list, other_part_list, total_list;
    private ArrayList<String> admin_list, group_id_list, other_part_group_id_list, banned_member_list,
            tampon_suggestion_list, filter_list;

    private ArrayList<User> all_user_list, other_part_all_user_list,
            users_in_same_school_list, users_in_same_estabishments_list, users_in_same_workplace_list,
            users_in_same_town_list, common_friends_list;
    public ArrayList<String> test_videos_photo_id_list, admin_master_list,
            test_group_id_list, user_group_id_following_list, total_user_id_list;
    public ArrayList<Long> filter_date_created_list;

    // suggestion about school and work place
    private String current_user_hometown, current_user_live_town;
    private ArrayList<SchoolAttended> current_user_schools_list;
    private ArrayList<FrequentedEstablishment> current_user_establishments_list;
    private ArrayList<WorkAt> current_user_workplaces_list;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;
    // suggestion abut common friends
    private List<String> current_user_friends_list;
    private List<String> friend_user_friends_list, removed_suggestions_list;

    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        context = (MainActivity) getActivity();

        // countries flags
        World.init(context);
        handler = new Handler(Looper.getMainLooper());

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        if (context != null) {
            from_suggestion = context.getIntent().getStringExtra("from_suggestion");
            from_home_activity = context.getFrom_home_value();
            from_view_video = context.getFrom_view_video();
            from_challenge_video_home = context.getFrom_challenge_video_home();
            relLayout_view_overlay = context.getRelLayout_view_overlay();
        }

        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void init(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        relLayout_board = view.findViewById(R.id.relLayout_board);
        RelativeLayout relLayout_challenges = view.findViewById(R.id.relLayout_challenges);
        RelativeLayout relLayout_publisher = view.findViewById(R.id.relLayout_publisher);
        RelativeLayout relLayout_thought = view.findViewById(R.id.relLayout_thought);
        LinearLayout linLayout_challenges = view.findViewById(R.id.linLayout_challenges);
        LinearLayout linLayout_publisher = view.findViewById(R.id.linLayout_publisher);
        LinearLayout linLayout_thought = view.findViewById(R.id.linLayout_thought);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_600, R.color.red_600, R.color.vertWatsApp);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // publish in Miioky
        relLayout_publisher.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, PubInstagPhoto.class));
            }
        });
        linLayout_publisher.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, PubInstagPhoto.class));
            }
        });

        // my thought
        relLayout_thought.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, PubCommentText.class));
            }
        });
        linLayout_thought.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, PubCommentText.class));
            }
        });

        // challenges
        relLayout_challenges.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);

            } else {
                challengesClick(relLayout_challenges);
            }
        });
        linLayout_challenges.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);

            } else {
                challengesClick(linLayout_challenges);
            }
        });
    }

    // challenges click
    private void challengesClick(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(context, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_home_challenge);
        //adding click listener
        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_see_my_challenges) {
                popup.dismiss();
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, MyChallenges.class));

            } else if (menuItem.getItemId() == R.id.menu_create_a_challenge) {
                popup.dismiss();
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, ViewMyChallenges.class));
            }
            return false;
        });

        //displaying the popup
        popup.show();
    }

    private void clearAll(){
        if(friendFollowing != null){
            friendFollowing.clear();
        }
        if(mFollowing != null){
            mFollowing.clear();
        }
        if(list != null){
            list.clear();
        }
        if(final_list != null){
            final_list.clear();
        }

        if(users_in_same_school_list != null){
            users_in_same_school_list.clear();
        }
        if(users_in_same_estabishments_list != null){
            users_in_same_estabishments_list.clear();
        }
        if(users_in_same_workplace_list != null){
            users_in_same_workplace_list.clear();
        }
        if(users_in_same_town_list != null){
            users_in_same_town_list.clear();
        }
        if(common_friends_list != null){
            common_friends_list.clear();
        }
        if(all_user_list != null){
            all_user_list.clear();
        }
        if(other_part_all_user_list != null){
            other_part_all_user_list.clear();
        }
        if(total_user_id_list != null){
            total_user_id_list.clear();
        }
        if (test_videos_photo_id_list != null) {
            test_videos_photo_id_list.clear();
        }
        if(current_user_schools_list != null){
            current_user_schools_list.clear();
        }
        if(current_user_establishments_list != null){
            current_user_establishments_list.clear();
        }
        if(current_user_workplaces_list != null){
            current_user_workplaces_list.clear();
        }
        if(schools_list != null){
            schools_list.clear();
        }
        if(establishments_list != null){
            establishments_list.clear();
        }
        if(workplaces_list != null){
            workplaces_list.clear();
        }
        if(current_user_friends_list != null){
            current_user_friends_list.clear();
        }
        if(friend_user_friends_list != null){
            friend_user_friends_list.clear();
        }

        if (group_id_list != null) {
            group_id_list.clear();
        }
        if (other_part_group_id_list != null) {
            other_part_group_id_list.clear();
        }
        if (banned_member_list != null) {
            banned_member_list.clear();
        }
        if (admin_list != null) {
            admin_list.clear();
        }
        if (tampon_suggestion_list != null) {
            tampon_suggestion_list.clear();
        }
        if (filter_list != null) {
            filter_list.clear();
        }
        if (filter_date_created_list != null) {
            filter_date_created_list.clear();
        }
        if(user_group_id_following_list != null){
            user_group_id_following_list.clear();
        }
        if(admin_master_list != null){
            admin_master_list.clear();
        }
        if(test_group_id_list != null){
            test_group_id_list.clear();
        }
        if(suggestion_list != null){
            suggestion_list.clear();
        }
        if(other_part_list != null){
            other_part_list.clear();
        }
        if(total_list != null){
            total_list.clear();
        }
        if(removed_suggestions_list != null){
            removed_suggestions_list.clear();
        }
        // suggestion ________________________________________
        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }
        friendFollowing = new ArrayList<>();
        mFollowing = new ArrayList<>();
        groupFollowing = new ArrayList<>();
        list = new ArrayList<>();
        final_list = new ArrayList<>();
        // suggestion _____________________________________
        users_in_same_school_list = new ArrayList<>();
        users_in_same_estabishments_list = new ArrayList<>();
        users_in_same_workplace_list = new ArrayList<>();
        users_in_same_town_list = new ArrayList<>();
        common_friends_list = new ArrayList<>();
        all_user_list = new ArrayList<>();
        other_part_all_user_list = new ArrayList<>();
        total_user_id_list = new ArrayList<>();
        test_videos_photo_id_list = new ArrayList<>();
        // suggestion about school and work place
        current_user_schools_list = new ArrayList<>();
        current_user_establishments_list = new ArrayList<>();
        current_user_workplaces_list = new ArrayList<>();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
        current_user_friends_list = new ArrayList<>();
        friend_user_friends_list = new ArrayList<>();
        user_group_id_following_list = new ArrayList<>();
        test_group_id_list = new ArrayList<>();
        admin_master_list = new ArrayList<>();
        group_id_list = new ArrayList<>();
        other_part_group_id_list = new ArrayList<>();
        banned_member_list = new ArrayList<>();
        admin_list = new ArrayList<>();
        suggestion_list = new ArrayList<>();
        other_part_list = new ArrayList<>();
        filter_list = new ArrayList<>();
        filter_date_created_list = new ArrayList<>();
        tampon_suggestion_list = new ArrayList<>();
        total_list = new ArrayList<>();
        removed_suggestions_list = new ArrayList<>();
    }

    /**
     //     * Récupérer tous les identifiants d'utilisateur que l'utilisateur actuel suit
     //     */
    private void getGroupFollowing() {
        Log.d(TAG, "getFollowing: searching for following");
        clearAll();

        //also add your own id to the list
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id);

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get all current user groups
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        UserGroups userGroups = Util_UserGroups.getUserGroups(context, objectMap);

                        String groupID = ds.getKey();

                        if (!userGroups.isGroup_paused())
                            groupFollowing.add(groupID);
                    }

                    // get all user groups
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                String followingId = ds.getKey();

                                // get the list of group where member has been banned
                                if (!following.isBanFromGroup())
                                    if (!following.isGroup_paused())
                                        groupFollowing.add(followingId);
                            }

                            getGroupPosts();
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
    }

    private void getGroupPosts() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        if (!groupFollowing.isEmpty()) {
            for(int i = 0; i < groupFollowing.size(); i++){
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group))
                        .child(groupFollowing.get(i)) // group_id
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(groupFollowing.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long currentTime = System.currentTimeMillis();

                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            // to prevent duplicate challenge
                            if (currentTime > model.getDate_created() && currentTime  <= model.getDate_created()+ 5*86400000L) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }

                        }

                        if(count >= groupFollowing.size() -1){

                            getFriendsFollowing();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else
            getFriendsFollowing();
    }

    private void getFriendsFollowing() {
        Log.d(TAG, "getFriendFollowing: searching for following");

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
                            friendFollowing.add(followingId);
                        }
                    }

                    getFriendsPosts();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getFriendsPosts() {
        Log.d(TAG, "getFriendsPhotos: getting list of photos");
        if (!friendFollowing.isEmpty()) {
            for(int i = 0; i < friendFollowing.size(); i++){
                final int count = i;
                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(friendFollowing.get(i)) // user_id
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(friendFollowing.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long currentTime = System.currentTimeMillis();

                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            // to prevent duplicate challenge
                            if (currentTime > model.getDate_created() && currentTime  <= model.getDate_created()+ 5*86400000L) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if(count >= friendFollowing.size() -1){
                            //get user follwing
                            getFollowing();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: query cancelled.");
                    }
                });
            }

        } else
            getFollowing();
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        //also add your own id to the list
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mFollowing.add(user_id);
            Query query = myRef
                    .child(context.getString(R.string.dbname_following))
                    .child(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String followingId = Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString();
                        mFollowing.add(followingId);
                    }

                    getPhotos();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting list of photos");
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_battle))
                    .child(mFollowing.get(i)) // user_id
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long currentTime = System.currentTimeMillis();

                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                        // to prevent duplicate challenge
                        if (currentTime > model.getDate_created() && currentTime  <= model.getDate_created()+ 5*86400000L) {
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }
                    }

                    if(count >= mFollowing.size() -1){
                        //add the suggestion posts
                        getPhoneContactsPosts();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });
        }
    }
    // get current post (list)______________________________________________________________________
    // get the challenges created by my phone contacts
    private void getPhoneContactsPosts() {
        // to remove suggestion close by current user
        Query query = myRef.child(context.getString(R.string.dbname_remove_Suggestion))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    RemoveSuggestionModel suggestionModel = Util_RemoveSuggestionModel.getRemoveSuggestionModel(context, objectMap);
                    removed_suggestions_list.add(suggestionModel.getUser_id());
                }

                group_users_from_the_same_establishment_or_same_town();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the current user school and work place
    private void group_users_from_the_same_establishment_or_same_town() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByKey()
                .equalTo(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        current_user_hometown = Normalizer.normalize(user.getHometown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                        current_user_live_town = Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");

                        SchoolAttended school = new SchoolAttended();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                        current_user_schools_list.add(school);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                        current_user_establishments_list.add(establishment);
                    }
                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()) {
                        WorkAt workAt = new WorkAt();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                        current_user_workplaces_list.add(workAt);
                    }
                }

                // list of all user
                Query query = myRef
                        .child(context.getString(R.string.dbname_users));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            if (user.getScope().equals(context.getString(R.string.title_public))) {
                                // get all the user to get common friends
                                all_user_list.add(user);

                                // get user's schools
                                if (!user.getUser_id().equals(user_id)) {

                                    // get user's hometown/livetown
                                    if (Normalizer.normalize(user.getHometown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(current_user_hometown) ||
                                            Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                                    .equalsIgnoreCase(current_user_live_town))
                                        if (!total_user_id_list.contains(user.getUser_id())) {
                                            users_in_same_town_list.add(user);
                                            total_user_id_list.add(user.getUser_id());
                                        }

                                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_school)).getChildren()) {
                                        SchoolAttended school = new SchoolAttended();
                                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                        assert objectHashMap != null;
                                        school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());

                                        schools_list.add(school);
                                    }
                                    // get user's establishments
                                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_establishments)).getChildren()) {
                                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                        assert objectHashMap != null;
                                        establishment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());

                                        establishments_list.add(establishment);
                                    }
                                    // get user's workplace
                                    for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_workAts)).getChildren()) {
                                        WorkAt workAt = new WorkAt();
                                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                                        assert objectHashMap != null;
                                        workAt.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());

                                        workplaces_list.add(workAt);
                                    }

                                    // verify if we are in the same school
                                    for (int j = 0; j < current_user_schools_list.size(); j++) {
                                        for (int i = 0; i < schools_list.size(); i++) {
                                            if (schools_list.get(i).getUser_school_attended().equals(current_user_schools_list.get(j).getUser_school_attended())) {
                                                if (!total_user_id_list.contains(schools_list.get(i).getUser_id())) {
                                                    users_in_same_school_list.add(user);
                                                    total_user_id_list.add(schools_list.get(i).getUser_id());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // get the groups created by user in same schools
                        group_getGroupsCreatedByUsersInTheSameSchool();
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

    // get the groups created by the in the same school
    private void group_getGroupsCreatedByUsersInTheSameSchool() {
        if (!users_in_same_school_list.isEmpty()) {
            for (int i = 0; i < users_in_same_school_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_school_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_school_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!test_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                test_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= users_in_same_school_list.size() - 1) {
                            // verify if we are in the same establishment
                            for (int n = 0; n < all_user_list.size(); n++) {
                                for (int j = 0; j < current_user_establishments_list.size(); j++) {
                                    for (int i = 0; i < establishments_list.size(); i++) {
                                        if (establishments_list.get(i).getUser_establishment().equals(current_user_establishments_list.get(j).getUser_establishment())) {
                                            if (!total_user_id_list.contains(establishments_list.get(i).getUser_id())) {
                                                users_in_same_estabishments_list.add(all_user_list.get(n));
                                                total_user_id_list.add(establishments_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }
                            }

                            // get the groups created by user in same establishments
                            getGroupsCreatedByUsersInTheSameEstablishments();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same establishments
            getGroupsCreatedByUsersInTheSameEstablishments();
        }
    }

    // get the groups created by the in the same school
    private void getGroupsCreatedByUsersInTheSameEstablishments() {
        if (!users_in_same_estabishments_list.isEmpty()) {
            for (int i = 0; i < users_in_same_estabishments_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_estabishments_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_estabishments_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!test_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());

                                test_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= users_in_same_estabishments_list.size() - 1) {
                            // verify if we are in the same workplace
                            for (int n = 0; n < all_user_list.size(); n++) {
                                for (int j = 0; j < current_user_workplaces_list.size(); j++) {

                                    for (int i = 0; i < workplaces_list.size(); i++) {
                                        if (workplaces_list.get(i).getUser_work_at().equals(current_user_workplaces_list.get(j).getUser_work_at())) {
                                            if (!total_user_id_list.contains(workplaces_list.get(i).getUser_id())) {
                                                users_in_same_workplace_list.add(all_user_list.get(n));
                                                total_user_id_list.add(workplaces_list.get(i).getUser_id());
                                            }
                                        }
                                    }
                                }
                            }

                            // get the groups created by user in same workplaces
                            getGroupsCreatedByUsersInTheSameWorkPlaces();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same workplaces
            getGroupsCreatedByUsersInTheSameWorkPlaces();
        }
    }

    // get groups created by users in same workplaces
    private void getGroupsCreatedByUsersInTheSameWorkPlaces() {
        if (!users_in_same_workplace_list.isEmpty()) {
            for (int i = 0; i < users_in_same_workplace_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_workplace_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_workplace_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!test_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                test_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= users_in_same_workplace_list.size() - 1) {
                            // get common friends
                            group_getGroupsCreatedByUsersInTheSameTown();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common friends
            group_getGroupsCreatedByUsersInTheSameTown();
        }
    }

    // get the groups created by the in the same town
    private void group_getGroupsCreatedByUsersInTheSameTown() {
        if (!users_in_same_town_list.isEmpty()) {
            for (int i = 0; i < users_in_same_town_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(users_in_same_town_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(users_in_same_town_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!test_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                test_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= users_in_same_town_list.size() - 1) {
                            // get common friends
                            group_getCommonFriends(all_user_list);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common friends
            group_getCommonFriends(all_user_list);
        }
    }

    // get the common friends ___________________________________________________
    private void group_getCommonFriends(ArrayList<User> all_user_list) {
        Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String keyID = dataSnapshot.getKey();

                    current_user_friends_list.add(keyID);
                }

                // get the friend user friend list
                for (int i = 0; i < all_user_list.size(); i++) {
                    final int count = i;
                    Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                            .child(all_user_list.get(i).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String keyID = dataSnapshot.getKey();

                                friend_user_friends_list.add(keyID);
                            }

                            if (count >= all_user_list.size() - 1) {
                                // get common friends
                                for (String ID: current_user_friends_list) {
                                    for (int i = 0; i < friend_user_friends_list.size(); i++) {
                                        if (ID.equals(friend_user_friends_list.get(i))) {

                                            if (!total_user_id_list.contains(friend_user_friends_list.get(i))) {
                                                Query query = myRef
                                                        .child(context.getString(R.string.dbname_users))
                                                        .orderByChild(context.getString(R.string.field_user_id))
                                                        .equalTo(friend_user_friends_list.get(i));

                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot ds : snapshot.getChildren()){
                                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                            assert objectMap != null;
                                                            User user = Util_User.getUser(context, objectMap, ds);

                                                            common_friends_list.add(user);
                                                            total_user_id_list.add(user.getUser_id());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                // Group created by common friends
                                group_getGroupsCreatedByCommonFriends();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get groups created by common friends
    private void group_getGroupsCreatedByCommonFriends() {
        if (!common_friends_list.isEmpty()) {
            for (int i = 0; i < common_friends_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(common_friends_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_admin_master))
                        .equalTo(common_friends_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get all groups
                            if (!test_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    if (!user_groups.isGroup_paused())
                                        group_id_list.add(user_groups.getGroup_id());
                                test_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the groups created by common knowledge
                            getTheGroupsCreatedByMyknowledge();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by common knowledge
            getTheGroupsCreatedByMyknowledge();
        }
    }

    // get the groups created by common knowledge
    private void getTheGroupsCreatedByMyknowledge() {
        if (!group_id_list.isEmpty()) {
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id);

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        // get the list of group where member has been banned
                        if (following.isBanFromGroup())
                            banned_member_list.add(following.getGroup_id());
                    }

                    if (!banned_member_list.isEmpty()) {
                        for (String group_banned_id: banned_member_list) {

                            for (int i = 0; i < group_id_list.size(); i++) {
                                if (!group_id_list.get(i).equals(group_banned_id)) {
                                    tampon_suggestion_list.add(group_id_list.get(i));
                                }
                            }
                        }
                        group_id_list.clear();
                        group_id_list.addAll(tampon_suggestion_list);
                    }
                    // find the posts ______________________________________________________________
                    for(int i = 0; i < group_id_list.size(); i++){
                        final int count = i;
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group))
                                .child(group_id_list.get(i)) // group_id
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(group_id_list.get(i));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                    // get all posts
                                    if (!removed_suggestions_list.contains(model.getGroup_id())) {
                                        if (!TextUtils.isEmpty(model.getReponse_photoID())
                                                &&
                                                (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                        || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                            if (!filter_list.contains(model.getReponse_photoID())) {
                                                if (!model.isSuppressed())
                                                    suggestion_list.add(model);
                                                filter_list.add(model.getReponse_photoID());
                                            }

                                        } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                                &&
                                                !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                        || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                            if (!filter_list.contains(model.getPost_identity())) {
                                                if (!model.isSuppressed())
                                                    suggestion_list.add(model);
                                                filter_list.add(model.getPost_identity());
                                            }

                                        } else {
                                            if (!filter_list.contains(model.getPost_identity())) {
                                                if (!model.isSuppressed())
                                                    suggestion_list.add(model);
                                                filter_list.add(model.getPost_identity());
                                            }
                                        }
                                    }
                                }

                                if(count >= group_id_list.size() -1){
                                    // add the other part of list
                                    group_getGroupAdminMasterID();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            // add the other part of list
            group_getGroupAdminMasterID();
        }
    }

    // add the other part of list
    private void group_getGroupAdminMasterID() {
        Log.d(TAG, "getFollowing: searching for following");

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

                getGroupIDList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupIDList() {
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
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            if (!test_group_id_list.contains(user_groups.getGroup_id())) {
                                if (user_groups.isGroup_private() && user_groups.getAdmin_master().equals(user_id))
                                    if (!user_groups.isGroup_paused())
                                        other_part_group_id_list.add(user_groups.getGroup_id());
                                if (user_groups.isGroup_public())
                                    if (!user_groups.isGroup_paused())
                                        other_part_group_id_list.add(user_groups.getGroup_id());
                                test_group_id_list.add(user_groups.getGroup_id());
                            }
                        }

                        if(count >= admin_list.size() -1) {
                            if (!banned_member_list.isEmpty()) {
                                for (String group_banned_id: banned_member_list) {

                                    for (int i = 0; i < other_part_group_id_list.size(); i++) {
                                        if (!other_part_group_id_list.get(i).equals(group_banned_id)) {
                                            tampon_suggestion_list.add(other_part_group_id_list.get(i));
                                        }
                                    }
                                }
                                other_part_group_id_list.clear();
                                other_part_group_id_list.addAll(tampon_suggestion_list);
                            }

                            getOtherPartsGroupPosts();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            getOtherPartsGroupPosts();
        }
    }

    private void getOtherPartsGroupPosts() {
        Log.d(TAG, "getPhotos: getting group list of photos");
        if (!other_part_group_id_list.isEmpty()) {
            for(int i = 0; i < other_part_group_id_list.size(); i++){
                final int count = i;
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group))
                        .child(other_part_group_id_list.get(i)) // group_id
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(other_part_group_id_list.get(i));

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                            // get all posts
                            if (!removed_suggestions_list.contains(model.getGroup_id())) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            other_part_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            other_part_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            other_part_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if(count >= other_part_group_id_list.size() -1){
                            users_from_the_same_establishment_or_same_town();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            users_from_the_same_establishment_or_same_town();
        }

    }
    // Miioky ______________________________________________________________________________________
    // get the current user school and work place
    private void users_from_the_same_establishment_or_same_town() {
        // list of all user
        Query query = myRef
                .child(context.getString(R.string.dbname_users));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    user = Util_User.getUser(context, objectMap, ds);

                    if (user.getScope().equals(context.getString(R.string.title_public))) {
                        // get all the user to get common friends
                        // remove private count
                        other_part_all_user_list.add(user);

                        // get user's schools
                        if (!user.getUser_id().equals(user_id)) {

                            // get user's hometown/livetown
                            if (Normalizer.normalize(user.getHometown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                    .equalsIgnoreCase(current_user_hometown) ||
                                    Normalizer.normalize(user.getTown_name(), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                                            .equalsIgnoreCase(current_user_live_town))
                                if (!total_user_id_list.contains(user.getUser_id())) {
                                    users_in_same_town_list.add(user);
                                    total_user_id_list.add(user.getUser_id());
                                }
                        }
                    }
                }
                // get the challenges created on Miioky by user in same schools
                getPostsCreatedByUsersInTheSameSchool();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the challenges created on Miioky by user in same schools
    private void getPostsCreatedByUsersInTheSameSchool() {
        if (!users_in_same_school_list.isEmpty()) {
            for (int i = 0; i < users_in_same_school_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_school_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_school_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!removed_suggestions_list.contains(model.getUser_id())) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_school_list.size() - 1) {
                            // get the groups created by user in same establishments
                            getPostsCreatedByUsersInTheSameEstablishments();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the groups created by user in same establishments
            getPostsCreatedByUsersInTheSameEstablishments();
        }
    }

    // get the challenges created by the in the same school
    private void getPostsCreatedByUsersInTheSameEstablishments() {
        if (!users_in_same_estabishments_list.isEmpty()) {
            for (int i = 0; i < users_in_same_estabishments_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_estabishments_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_estabishments_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!removed_suggestions_list.contains(model.getUser_id())) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_estabishments_list.size() - 1) {
                            // get the challenges created by user in same workplaces
                            getPostsCreatedByUsersInTheSameWorkPlaces();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the challenges created by user in same workplaces
            getPostsCreatedByUsersInTheSameWorkPlaces();
        }
    }

    // get the challenges created by user in same workplaces
    private void getPostsCreatedByUsersInTheSameWorkPlaces() {
        if (!users_in_same_workplace_list.isEmpty()) {
            for (int i = 0; i < users_in_same_workplace_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_workplace_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_workplace_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!removed_suggestions_list.contains(model.getUser_id())) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_workplace_list.size() - 1) {
                            // get common town challenges friends
                            getPostsCreatedByUsersInTheSameTown();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get common town challenges friends
            getPostsCreatedByUsersInTheSameTown();
        }
    }

    // get the groups created by the in the same town
    private void getPostsCreatedByUsersInTheSameTown() {
        if (!users_in_same_town_list.isEmpty()) {
            for (int i = 0; i < users_in_same_town_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(users_in_same_town_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(users_in_same_town_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!removed_suggestions_list.contains(model.getUser_id())) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if (count >= users_in_same_town_list.size() - 1) {
                            // get challenges created by common friends
                            getPostsCreatedByCommonFriends();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get challenges created by common friends
            getPostsCreatedByCommonFriends();
        }
    }

    // get challenges created by common friends
    private void getPostsCreatedByCommonFriends() {
        if (!common_friends_list.isEmpty()) {
            for (int i = 0; i < common_friends_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(common_friends_list.get(i).getUser_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(common_friends_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                            // get all posts
                            if (!removed_suggestions_list.contains(model.getUser_id())) {
                                if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                    if (!filter_list.contains(model.getReponse_photoID())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getReponse_photoID());
                                    }

                                } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                        &&
                                        !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                                || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }

                                } else {
                                    if (!filter_list.contains(model.getPost_identity())) {
                                        if (!model.isSuppressed())
                                            suggestion_list.add(model);
                                        filter_list.add(model.getPost_identity());
                                    }
                                }
                            }
                        }

                        if (count >= common_friends_list.size() - 1) {
                            // get the other posts
                            getAllOtherPosts();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            // get the other posts
            getAllOtherPosts();
        }
    }

    // add other part of the groups
    private void getAllOtherPosts() {
        for(int i = 0; i < other_part_all_user_list.size(); i++){
            final int count = i;
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle))
                    .child(other_part_all_user_list.get(i).getUser_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(other_part_all_user_list.get(i).getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                        // get all posts
                        if (!removed_suggestions_list.contains(model.getUser_id())) {
                            if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    (model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {

                                if (!filter_list.contains(model.getReponse_photoID())) {
                                    if (!model.isSuppressed())
                                        other_part_list.add(model);
                                    filter_list.add(model.getReponse_photoID());
                                }

                            } else if (!TextUtils.isEmpty(model.getReponse_photoID())
                                    &&
                                    !(model.isReponseImageDouble() || model.isReponseVideoDouble()
                                            || model.isGroup_response_imageDouble() || model.isGroup_response_videoDouble())) {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        other_part_list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }

                            } else {
                                if (!filter_list.contains(model.getPost_identity())) {
                                    if (!model.isSuppressed())
                                        other_part_list.add(model);
                                    filter_list.add(model.getPost_identity());
                                }
                            }
                        }
                    }

                    if (count >= other_part_all_user_list.size() - 1) {
                        // get the list
                        displayTheList();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void displayTheList() {
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        total_list.addAll(list);

        suggestion_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        total_list.addAll(suggestion_list);

        other_part_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));
        total_list.addAll(other_part_list);

        // add nested recyclerview to the list
        final_list = Utils_list_with_nested_recyclerview.getMainList(total_list);

        adapter = new MainAdapter(context, progressBar, relLayout_view_overlay);

        // to prevent crash when we return to the app
        try {
            adapter.setDefaultRecyclerView(context, R.id.recyclerView);
        } catch (NullPointerException e) {
            Log.d(TAG, "displayTheList: error: "+e.getMessage());
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
        if (list.isEmpty() && from_suggestion == null) {
            // verify if user has followers or friends to display the dialog invitation to follow
            Query query = myRef
                    .child(context.getString(R.string.dbname_following))
                    .child(user_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Query query1 = myRef
                                .child(context.getString(R.string.dbname_friend_following))
                                .child(user_id);
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    // show dialog box
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                    TextView dialog_title = v.findViewById(R.id.dialog_title);
                                    TextView dialog_text = v.findViewById(R.id.dialog_text);
                                    TextView negativeButton = v.findViewById(R.id.tv_no);
                                    TextView positiveButton = v.findViewById(R.id.tv_yes);
                                    builder.setView(v);

                                    d = builder.create();
                                    d.show();

                                    negativeButton.setText(context.getString(R.string.cancel));
                                    positiveButton.setText(context.getString(R.string.follow));

                                    dialog_title.setVisibility(View.GONE);
                                    dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_is_more_interesting)));

                                    negativeButton.setOnClickListener(view -> d.dismiss());

                                    positiveButton.setOnClickListener(view2 -> {
                                        d.dismiss();
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, SeeAllSuggestions.class);
                                        intent.putExtra("follow", "follow");
                                        context.startActivity(intent);
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // hide/show board
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

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
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getGroupFollowing();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // passing context of fragment to backPressedListener
        backpressedlistener=this;
        // to prevent data to upload to another fragment
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // to prevent data to upload to another fragment
                if (isFirstLoaded) {
                    isFirstLoaded = false;
                    getGroupFollowing();
                }
            }
        });
    }

    @Override
    public void onPause() {
        // passing null value to backpressedlistener
        backpressedlistener=null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (adapter != null) {
            if (adapter.getCurrentPage() != 1) {
                swipeRefreshLayout.setRefreshing(true);
                getGroupFollowing();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1500);
            } else {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                if (from_home_activity != null || from_view_video != null || from_challenge_video_home != null)
                    context.finish();
                else {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra("from_main_fragment", "from_main_fragment");
                    startActivity(intent);
                }
            }

        } else {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            context.finish();
        }
    }

}