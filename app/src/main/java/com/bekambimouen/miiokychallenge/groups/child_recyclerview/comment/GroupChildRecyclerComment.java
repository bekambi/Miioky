package com.bekambimouen.miiokychallenge.groups.child_recyclerview.comment;

import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview.GroupViewRecycler;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview.comment.recycler_child_img.adapter.GroupCommentRecyclerChildAdapter;
import com.bekambimouen.miiokychallenge.groups.comment.publication.GroupCommentPublication;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Comment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChildRecyclerComment extends AppCompatActivity {
    private static final String TAG = "GroupChildRecyclerComment";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private RelativeLayout parent, relLayout_first_comment, see_the_post, relLayout_view_overlay;
    private ProgressBar loading_progressBar;

    // vars
    private GroupChildRecyclerComment context;
    private GroupCommentRecyclerChildAdapter recycler_child_adapter;
    private BattleModel battleModel, comment_recycler_child;
    private LinearLayoutManager layoutManager;
    private Date date;
    private ItemImageUneClickListener itemImageUneClickListener;
    private String photo_id, recyclerview_photo_id, recyclerview_fieldLike;
    private ArrayList<Comment> commentsList;
    private boolean isResponse = false, isInResponseTo = false;

    private Handler handler;
    private boolean isPosting = false;

    private String from_notification_comment;
    // notification comment data ___________________________________________________________________
    private String the_user_who_posted_id, post_id, admin_master, group_id, category_of_post, post_type = "",
            post_id_field, recyclerview_photo_id_upload, recyclerview_fieldLike_upload;
    private long time;
    private Comment commentModel;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_child_recycler_comment);
        context = this;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            comment_recycler_child = gson.fromJson(getIntent().getStringExtra("comment_recycler_child"),
                    BattleModel.class);
            recyclerview_photo_id = getIntent().getStringExtra("recyclerview_photo_id");
            recyclerview_fieldLike = getIntent().getStringExtra("recyclerview_fieldLike");

            // from notificationAdapter
            from_notification_comment = getIntent().getStringExtra("from_notification_comment");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (comment_recycler_child != null) {
            battleModel = comment_recycler_child;
            photo_id = comment_recycler_child.getPhotoi_id();
            // comment notification
            the_user_who_posted_id = comment_recycler_child.getUser_id();
            post_id = comment_recycler_child.getPhotoi_id();
            post_id_field = context.getString(R.string.field_photoi_id);
            category_of_post = "comment_recycler_child";
            post_type = "";
            admin_master = comment_recycler_child.getAdmin_master();
            group_id = comment_recycler_child.getGroup_id();
            recyclerview_photo_id_upload = recyclerview_photo_id;
            recyclerview_fieldLike_upload = recyclerview_fieldLike;
            setupFirebaseAuth_recycler_child_item();

        }

        date = new Date();
        init();

        // see the post of comment
        if (from_notification_comment != null) {
            see_the_post.setVisibility(View.VISIBLE);

            see_the_post.setOnClickListener(view1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                Gson gson = new Gson();

                String myGson = gson.toJson(comment_recycler_child);
                intent.putExtra("battleModel", myGson);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_i)))
                    intent.putExtra("position", 0);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_ii)))
                    intent.putExtra("position", 1);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_iii)))
                    intent.putExtra("position", 2);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_iv)))
                    intent.putExtra("position", 3);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_v)))
                    intent.putExtra("position", 4);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_vi)))
                    intent.putExtra("position", 5);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_vii)))
                    intent.putExtra("position", 6);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_viii)))
                    intent.putExtra("position", 7);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_ix)))
                    intent.putExtra("position", 8);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_x)))
                    intent.putExtra("position", 9);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xi)))
                    intent.putExtra("position", 10);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xii)))
                    intent.putExtra("position", 11);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xiii)))
                    intent.putExtra("position", 12);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xiv)))
                    intent.putExtra("position", 13);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xv)))
                    intent.putExtra("position", 14);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xvi)))
                    intent.putExtra("position", 15);
                if (recyclerview_fieldLike.equals(context.getString(R.string.field_likes_xvii)))
                    intent.putExtra("position", 16);
                startActivity(intent);
            });
        }
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
        see_the_post = findViewById(R.id.see_the_post);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        mComment = findViewById(R.id.EditText_commentaire);
        relLayout_first_comment = findViewById(R.id.relLayout_first_comment);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        ImageView posterComment = findViewById(R.id.posterComment);
        RelativeLayout icone_photo = findViewById(R.id.icone_photo);

        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = requireNonNull(mComment.getText()).toString();
                if (text.startsWith(" ")) {
                    mComment.setText(text.trim());
                }

                if (count != 0 || s.length() != 0) {
                    icone_photo.setVisibility(View.GONE);
                } else {
                    icone_photo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        icone_photo.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, GroupCommentPublication.class);
            intent.putExtra("group_id", battleModel.getGroup_id());
            intent.putExtra("photo_id", photo_id);
            intent.putExtra("recyclerview_photo_id", recyclerview_photo_id);
            intent.putExtra("view_comment", "view_comment");
            // send notification
            intent.putExtra("the_user_who_posted_id", the_user_who_posted_id);
            intent.putExtra("admin_master", admin_master);
            intent.putExtra("post_id_field", post_id_field);
            intent.putExtra("category_of_post", category_of_post);
            intent.putExtra("post_type", post_type);
            intent.putExtra("post_id", post_id);
            intent.putExtra("recyclerview_photo_id_upload", recyclerview_photo_id_upload);
            intent.putExtra("recyclerview_fieldLike_upload", recyclerview_fieldLike_upload);
            intent.putExtra("time", time);

            Gson gson = new Gson();
            String myGson;
            if (commentModel == null) {
                String commentID = myRef.push().getKey();

                Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                        "", commentID, date.getTime(), user_id);

                myGson = gson.toJson(comment);
            } else
                myGson = gson.toJson(commentModel);

            intent.putExtra("commentModel", myGson);
            startActivity(intent);
        });

        arrowBack.setOnClickListener(view -> finish());

        posterComment.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                // to know if the member activity has limited
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(battleModel.getGroup_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            long sanction_date = follower.getDate_admin_applied_sanction_in_group();
                            long currentTime = System.currentTimeMillis();

                            // limited posts activity
                            if (follower.isActivityLimitation()) {
                                if (follower.isCommentsActivityLimited()) {
                                    switch (follower.getNumber_comments_per_day_expiration()) {
                                        case "12":
                                            // check that the date of the sanctions has not expired
                                            if ((sanction_date + 12*3600000L) > currentTime) {
                                                getLimitedComments(follower);
                                            }
                                            break;
                                        case "24":
                                            if ((sanction_date + 86400000L) > currentTime) {
                                                getLimitedComments(follower);
                                            }
                                            break;
                                        case "3":
                                            if ((sanction_date + 3 * 86400000L) > currentTime) {
                                                getLimitedComments(follower);
                                            }
                                            break;
                                        case "7":
                                            if ((sanction_date + 7 * 86400000L) > currentTime) {
                                                getLimitedComments(follower);
                                            }
                                            break;
                                        case "14":
                                            if ((sanction_date + 14 * 86400000L) > currentTime) {
                                                getLimitedComments(follower);
                                            }
                                            break;
                                        case "28":
                                            if ((sanction_date + 28 * 86400000L) > currentTime) {
                                                getLimitedComments(follower);
                                            }
                                            break;
                                    }
                                }

                            } else {
                                // add comment to database
                                downloadComment();
                            }
                        }

                        if (!snapshot.exists()) {
                            // add comment to database
                            downloadComment();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // detect if user is admin master
        if (battleModel.getAdmin_master().equals(user_id)) {
            posterComment.setOnClickListener(view -> {
                // add comment to database
                downloadComment();
            });
        }

        itemImageUneClickListener = (commentKey, comment, user_id, url, thumbnail, commentModel, recyclerview_photo_id, recyclerview_fieldLike, time) -> {
            // for comment notification
            this.commentModel = commentModel;
            recyclerview_photo_id_upload = recyclerview_photo_id;
            recyclerview_fieldLike_upload = recyclerview_fieldLike;
            this.time = time;
            // __________________________________________________________________
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(battleModel);

            String myGson2 = gson.toJson(commentModel);

            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupChildRecyclerResponseComment.class);
            intent.putExtra("comment_key", commentKey);
            intent.putExtra("comment", comment);
            intent.putExtra("userID", user_id);
            intent.putExtra("media_comment_url", url);
            intent.putExtra("media_comment_thumbnail", thumbnail);
            intent.putExtra("time", time);
            intent.putExtra("commentModel", myGson2);
            intent.putExtra("comment_recycler_child", myGson);
            intent.putExtra("recyclerview_photo_id", recyclerview_photo_id);
            intent.putExtra("recyclerview_fieldLike", recyclerview_fieldLike);

            // notification data
            intent.putExtra("post_id_field", post_id_field);
            intent.putExtra("category_of_post", category_of_post);
            intent.putExtra("post_type", post_type);
            intent.putExtra("post_id", post_id);
            intent.putExtra("recyclerview_photo_id_upload", recyclerview_photo_id_upload);
            intent.putExtra("recyclerview_fieldLike_upload", recyclerview_fieldLike_upload);
            intent.putExtra("admin_master", admin_master);
            intent.putExtra("the_user_who_posted_id", the_user_who_posted_id);
            intent.putExtra("group_id", group_id);
            startActivity(intent);
        };
    }

    // add comment to database
    private void downloadComment() {
        if(!requireNonNull(mComment.getText()).toString().isEmpty()){
            Log.d(TAG, "onClick: attempting to submit new comment.");
            addNewComment_recycler_child(mComment.getText().toString());

            mComment.getText().clear();
            closeKeyboard();

        } else{
            Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
        }
    }

    // limit the post
    @TargetApi(Build.VERSION_CODES.O)
    private void getLimitedComments(GroupFollowersFollowing follower) {
        // get the day of today
        LocalDate date = LocalDate.now();
        long time = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        // it's today
        if (TimeUtils.isDateToday(time)) {
            // we check that the number os publications is lower than the desire limit
            if (follower.getNumber_of_comments_today() < Integer.parseInt(follower.getNumber_comments_per_day())) {
                // increment number of comments today
                HashMap<String, Object> map = new HashMap<>();
                Date date1 = new Date();
                int number_of_comments = follower.getNumber_of_comments_today();
                map.put("number_of_comments_today", number_of_comments+1);
                map.put("date_last_post_or_last_comment", date1.getTime());

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .child(battleModel.getGroup_id())
                        .updateChildren(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(battleModel.getGroup_id())
                        .child(user_id)
                        .updateChildren(map);

                // add comments to firebase
                downloadComment();

            } else {
                // show dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = v.findViewById(R.id.dialog_title);
                TextView dialog_text = v.findViewById(R.id.dialog_text);
                TextView negativeButton = v.findViewById(R.id.tv_no);
                TextView positiveButton = v.findViewById(R.id.tv_yes);
                builder.setView(v);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.cancel));
                negativeButton.setVisibility(View.GONE);
                positiveButton.setText(context.getString(R.string.ok));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_have_reached_the_comment_quota)
                        +" "+follower.getNumber_comments_per_day()+" "+context.getString(R.string.comments)
                        +" "+context.getString(R.string.per_day)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());
                positiveButton.setOnClickListener(view3 -> d.dismiss());
            }
        }
    }

    private void setupWidgets(BattleModel battleModel){
        Collections.reverse(commentsList);

        recycler_child_adapter = new GroupCommentRecyclerChildAdapter(context, commentsList,
                recyclerview_photo_id, recyclerview_fieldLike, battleModel, itemImageUneClickListener, loading_progressBar,
                post_id_field, category_of_post, post_type, post_id, admin_master, the_user_who_posted_id, group_id, relLayout_view_overlay);
        recyclerView.setAdapter(recycler_child_adapter);

        if (recycler_child_adapter.getItemCount() == 0)
            relLayout_first_comment.setVisibility(View.VISIBLE);
    }

    private void addNewComment_recycler_child(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        recycler_child_adapter.addComment(comment);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media))
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into pub_battle node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(context) {
                        @Override protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };

            smoothScroller.setTargetPosition(1);
            layoutManager.startSmoothScroll(smoothScroller);

            // send notification
            sendNotification();

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // add post point and send notification to post owner
    @TargetApi(Build.VERSION_CODES.O)
    private void sendNotification() {
        // add comment points
        addCommentPoints();

        // send notification to owner post
        Date date = new Date();
        String new_key = myRef.push().getKey();
        assert new_key != null;
        // send notification to the post owner
        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                false, false, false, false, false,
                false, false, false, false,
                false, false, false, false, false, false,
                false,
                false, false, false, false, false,
                false, false,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true,false, false,
                the_user_who_posted_id, new_key, user_id, admin_master,
                "", group_id, date.getTime(),
                false, false,
                true, false, false, false, true, false, false, false,
                true, post_id_field, category_of_post, post_type, false, post_id, "", "", false,
                "", "", "", recyclerview_photo_id_upload, recyclerview_fieldLike_upload, time,
                "", "", 0, "", "", "",
                false, false, false, false,
                false, false, false,
                false, false);

        if (!the_user_who_posted_id.equals(user_id)) {
            myRef.child(context.getString(R.string.dbname_notification))
                    .child(the_user_who_posted_id)
                    .child(new_key)
                    .setValue(map_notification);

            // add badge number
            HashMap<String, Object> map_number = new HashMap<>();
            map_number.put("user_id", the_user_who_posted_id);

            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                    .child(the_user_who_posted_id)
                    .child(new_key)
                    .setValue(map_number);
        }
    }

    // add post points
    private void addCommentPoints() {
        if (battleModel.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();
                        int number_of_comment_points = Integer.parseInt(user_groups.getComment_points());
                        map.put("comment_points", number_of_comment_points+5);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_user_group))
                                .child(user_groups.getAdmin_master())
                                .child(user_groups.getGroup_id())
                                .updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();
                        int number_of_comment_points = Integer.parseInt(following.getComment_points());
                        map.put("comment_points", number_of_comment_points+5);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(group_id)
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(group_id)
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

    private void clearAll() {
        if (commentsList != null)
            commentsList.clear();

        if (recycler_child_adapter != null)
            recycler_child_adapter = null;

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsList = new ArrayList<>();
    }

    private void setupFirebaseAuth_recycler_child_item(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        clearAll();

        Query query = myRef
                .child(getString(R.string.dbname_group_media))
                .child(comment_recycler_child.getPhotoi_id())
                .child(getString(R.string.field_child_comments))
                .child(recyclerview_photo_id)
                .child(getString(R.string.field_comments));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BattleModel photo = new BattleModel();
                for (DataSnapshot dSnapshot : snapshot.getChildren()){
                    Comment comment = new Comment();
                    Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                    assert objectHashMap != null;
                    comment.setSuppressed(requireNonNull(objectHashMap.get(getString(R.string.field_suppressed))).toString());
                    comment.setUser_id(requireNonNull(objectHashMap.get(getString(R.string.field_user_id))).toString());
                    comment.setComment(requireNonNull(objectHashMap.get(getString(R.string.field_comment))).toString());
                    comment.setCommentKey(requireNonNull(objectHashMap.get(getString(R.string.field_commentKey))).toString());
                    comment.setDate_created(Long.parseLong(requireNonNull(objectHashMap.get(getString(R.string.field_date_created))).toString()));
                    comment.setUrl(requireNonNull(objectHashMap.get(getString(R.string.field_url))).toString());
                    comment.setThumbnail(requireNonNull(objectHashMap.get(getString(R.string.field_thumbnail))).toString());

                    List<Like> likeList = new ArrayList<>();
                    for (DataSnapshot data : dSnapshot
                            .child(getString(R.string.field_likes)).getChildren()) {
                        Like like = new Like();
                        like.setUser_id(requireNonNull(data.getValue(Like.class)).getUser_id());
                        likeList.add(like);
                    }
                    comment.setLikes(likeList);
                    commentsList.add(comment);
                }

                photo.setComments(commentsList);
                setupWidgets(battleModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        mComment.setHint(getString(R.string.add_comment));
        if (isResponse) {
            isResponse = false;
        } else if (isInResponseTo) {
            isInResponseTo = false;
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);

        if (isPosting) {
            isPosting = false;

            // to avoid crash when user post and repost photo
            if (!commentsList.isEmpty()) {
                commentsList.clear();
                if (comment_recycler_child != null)
                    recycler_child_adapter.notifyDataSetChanged();
            }

            setupFirebaseAuth_recycler_child_item();

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(context) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };

            smoothScroller.setTargetPosition(0);
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }
}