package com.bekambimouen.miiokychallenge.child_recyclerview_share.comment;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.child_recyclerview_share.RecyclerViewActivityShare;
import com.bekambimouen.miiokychallenge.comment_share.publication.CommentPublicationShare;
import com.bekambimouen.miiokychallenge.child_recyclerview_share.comment.child_viewholder.adapter.CommentShareRecyclerChildAdapter;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildRecyclerCommentShare extends AppCompatActivity {
    private static final String TAG = "ChildRecyclerCommentShare";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private RelativeLayout parent, relLayout_first_comment, see_the_post, relLayout_view_overlay;
    private ProgressBar loading_progressBar;

    // vars
    private ChildRecyclerCommentShare context;
    private BattleModel comment_recycler_child, battleModel;
    private CommentShareRecyclerChildAdapter recycler_child_adapter;
    private LinearLayoutManager layoutManager;
    private boolean isPosting = false;

    private String photo_id, recyclerview_photo_id, recyclerview_fieldLike;
    private ArrayList<Comment> commentsList;
    private Date date;
    private ItemImageUneClickListener itemImageUneClickListener;
    private boolean isResponse = false, isInResponseTo = false;
    private Handler handler;

    private String from_notification_comment;
    // notification comment data ___________________________________________________________________
    private String the_user_who_posted_id, post_id, admin_master, group_id, category_of_post, post_type = "",
            post_id_field;
    private long time;
    private Comment commentModel;
    private String recyclerview_photo_id_upload, recyclerview_fieldLike_upload;
    // _____________________________________________________________________________________________

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_child_recycler_comment_share);
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
            photo_id = comment_recycler_child.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_recycler_child.getUser_id();
            post_id = comment_recycler_child.getPhotoi_id();
            post_id_field = context.getString(R.string.field_photoi_id);
            category_of_post = "comment_recycler_child";
            post_type = "";
            admin_master = "";
            group_id = "";
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
                Intent intent = new Intent(context, RecyclerViewActivityShare.class);
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
        relLayout_first_comment = findViewById(R.id.relLayout_first_comment);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        mComment = findViewById(R.id.EditText_commentaire);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        ImageView checkMark = findViewById(R.id.posterComment);
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

        icone_photo.setOnClickListener(v -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, CommentPublicationShare.class);
            intent.putExtra("userid", battleModel.getUser_id());
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

        checkMark.setOnClickListener(view -> {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_recycler_child(mComment.getText().toString());

                mComment.setText("");
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        });

        itemImageUneClickListener = (commentKey, comment, user_id, url, thumbnail, commentModel, recyclerview_photo_id, recyclerview_fieldLike, time) -> {
            // for comment notification
            this.commentModel = commentModel;
            recyclerview_photo_id_upload = recyclerview_photo_id;
            recyclerview_fieldLike_upload = recyclerview_fieldLike;
            this.time = time;
            // __________________________________________________________________
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ChildRecyclerResponseCommentShare.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(battleModel);;
            String myGson2 = gson.toJson(commentModel);

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
            Gson gson3 = new Gson();
            String myGson3 = gson3.toJson(commentModel);
            intent.putExtra("commentModel", myGson3);
            startActivity(intent);
        };

        arrowBack.setOnClickListener(view -> {
            mComment.setHint(getString(R.string.add_comment));
            if (isResponse) {
                isResponse = false;
            } else if (isInResponseTo) {
                isInResponseTo = false;
            } else {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mComment.setHint(getString(R.string.add_comment));
                if (isResponse) {
                    isResponse = false;
                } else if (isInResponseTo) {
                    isInResponseTo = false;
                } else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    private void setupWidgets(BattleModel battleModel){
        Collections.reverse(commentsList);

        recycler_child_adapter = new CommentShareRecyclerChildAdapter(this, commentsList,
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
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_child_comments_share))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into pub_battle node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_child_comments_share))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
                        @Override protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };

            smoothScroller.setTargetPosition(0);
            layoutManager.startSmoothScroll(smoothScroller);

            // send notification
            sendNotification();

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    // send notification to owner post
    private void sendNotification() {
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
                false,false, true,
                the_user_who_posted_id, new_key, user_id, "",
                "", "", date.getTime(),
                true, false,
                true, true, false, false, true, false, false, false,
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

    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Setup the firebase auth object
     */
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
                .child(getString(R.string.dbname_battle_media_share))
                .child(comment_recycler_child.getPhoto_id())
                .child(getString(R.string.field_child_comments_share))
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

            if (battleModel != null) {
                if (comment_recycler_child != null)
                    setupFirebaseAuth_recycler_child_item();

                RecyclerView.SmoothScroller smoothScroller = new
                        LinearSmoothScroller(getApplicationContext()) {
                            @Override protected int getVerticalSnapPreference() {
                                return LinearSmoothScroller.SNAP_TO_START;
                            }
                        };

                smoothScroller.setTargetPosition(0);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        }
    }
}