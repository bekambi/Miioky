package com.bekambimouen.miiokychallenge.child_recyclerview.comment;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.child_recyclerview.RecyclerViewActivity;
import com.bekambimouen.miiokychallenge.child_recyclerview.comment.comment_response.adapter.CommentResponseRecyclerChildAdapter;
import com.bekambimouen.miiokychallenge.comment.publication.CommentPublication;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_CommentResponse;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChildRecyclerResponseComment extends AppCompatActivity {
    private static final String TAG = "ChildRecyclerResponseComment";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView answering_username;
    private RelativeLayout parent, see_the_post, relLayout_view_overlay;

    // vars
    private ChildRecyclerResponseComment context;
    private CommentResponseRecyclerChildAdapter recyclerView_child_adapter;
    private BattleModel battleModel, comment_recycler_child;
    private ChildItemClickListener childItemClickListener;
    private Comment commentModel;
    private String photo_id;
    private String comment_key;
    private String mUsername;
    private String userID;
    private String userName;
    private String recyclerview_photo_id, recyclerview_fieldLike;
    private String edittext_clear_focus;
    private LinearLayoutManager layoutManager;
    private long time;
    private Date date;
    private boolean isInResponseTo = false;
    private boolean isPosting = false;
    private int list_size = 0;
    private Handler handler;
    private ArrayList<CommentResponse> commentsResponseList;

    private String from_notification_comment;
    private String comment, url, thumbnail;
    // notification comment data ___________________________________________________________________
    private String post_id, post_id_field, admin_master, category_of_post, post_type = "",
            recyclerview_photo_id_upload, recyclerview_fieldLike_upload, the_user_who_posted_id;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_child_recycler_response_comment);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            comment_recycler_child = gson.fromJson(getIntent().getStringExtra("comment_recycler_child"), BattleModel.class);
            commentModel = gson.fromJson(getIntent().getStringExtra("commentModel"), Comment.class);

            // from notification adapter
            from_notification_comment = getIntent().getStringExtra("from_notification_comment");
            if (from_notification_comment != null) {
                comment_key = getIntent().getStringExtra("author_commentKey");
                comment = getIntent().getStringExtra("author_comment");
                userID = getIntent().getStringExtra("author_id");
                time = getIntent().getLongExtra("author_comment_date_create", 0);
                recyclerview_photo_id = getIntent().getStringExtra("recyclerview_photo_id");
                recyclerview_fieldLike = getIntent().getStringExtra("recyclerview_fieldLike");
                url = getIntent().getStringExtra("author_url");
                thumbnail = getIntent().getStringExtra("author_thumbnail");

            } else {
                comment_key = getIntent().getStringExtra("comment_key");
                userID = getIntent().getStringExtra("userID");
                time = getIntent().getLongExtra("time", 0);
                userName = getIntent().getStringExtra("userName");
                recyclerview_photo_id = getIntent().getStringExtra("recyclerview_photo_id");
                recyclerview_fieldLike = getIntent().getStringExtra("recyclerview_fieldLike");
                edittext_clear_focus = getIntent().getStringExtra("edittext_clear_focus");
            }

            // notification comment from viewHolder
            post_id_field = getIntent().getStringExtra("post_id_field");
            category_of_post = getIntent().getStringExtra("category_of_post");
            post_type = getIntent().getStringExtra("post_type");
            post_id = getIntent().getStringExtra("post_id");
            recyclerview_photo_id_upload = getIntent().getStringExtra("recyclerview_photo_id_upload");
            recyclerview_fieldLike_upload = getIntent().getStringExtra("recyclerview_fieldLike_upload");
            admin_master = getIntent().getStringExtra("admin_master");
            the_user_who_posted_id = getIntent().getStringExtra("the_user_who_posted_id");

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (comment_recycler_child != null) {
            battleModel = comment_recycler_child;
            photo_id = comment_recycler_child.getPhotoi_id();

        }

        date = new Date();
        init(battleModel, photo_id);
        getChildRecyclerviewResponseComments(recyclerview_photo_id, comment_key);

        // see the post of comment
        if (from_notification_comment != null) {
            see_the_post.setVisibility(View.VISIBLE);

            see_the_post.setOnClickListener(view1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, RecyclerViewActivity.class);
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

    private void init(BattleModel model, String photo_id) {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        see_the_post = findViewById(R.id.see_the_post);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout icone_photo = findViewById(R.id.icone_photo);
        mComment = findViewById(R.id.EditText_commentaire);
        answering_username = findViewById(R.id.answering_username);
        if (from_notification_comment == null && edittext_clear_focus == null)
            mComment.requestFocus();
        ImageView checkMark = findViewById(R.id.posterComment);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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

        childItemClickListener = (childPosition, username) -> {
            isInResponseTo = true;
            mUsername = username;
            mComment.setHint(getString(R.string.in_response_to)+username);
            mComment.requestFocus();
            showKeyboard();
        };

        // answer user from view comment
        if (userName != null) {
            isInResponseTo = true;
            mUsername = userName;
            mComment.setHint(getString(R.string.in_response_to)+mUsername);
        }

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String username = user.getUsername();
                    answering_username.setText(Html.fromHtml(context.getString(R.string.answer_to)+" "+username));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

        icone_photo.setOnClickListener(v -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            isPosting = true;
            Intent intent= new Intent(context, CommentPublication.class);
            intent.putExtra("isInResponseTo", isInResponseTo);
            intent.putExtra("comment_key", comment_key);
            intent.putExtra("mUsername", mUsername);
            intent.putExtra("userid", model.getUser_id());
            intent.putExtra("photo_id", photo_id);
            intent.putExtra("recyclerview_photo_id", recyclerview_photo_id);
            intent.putExtra("view_response_comment", "view_response_comment");
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
            intent.putExtra("userID", userID);

            Gson gson = new Gson();
            String myGson;
            if (commentModel == null) {
                String commentID = myRef.push().getKey();

                Comment comment = new Comment();
                comment.setSuppressed("no");
                comment.setThumbnail("");
                comment.setUrl("");
                comment.setComment("");
                comment.setCommentKey(commentID);
                comment.setDate_created(date.getTime());
                comment.setUser_id(user_id);

                myGson = gson.toJson(comment);
            } else
                myGson = gson.toJson(commentModel);

            intent.putExtra("commentModel", myGson);
            startActivity(intent);
        });

        checkMark.setOnClickListener(view -> {
            String newComment = requireNonNull(mComment.getText()).toString().trim();
            if (!TextUtils.isEmpty(newComment)) {
                if (model != null) {
                    if (isInResponseTo) {
                        Log.d(TAG, "onClick: attempting to submit new response comment.");
                        addNewChildResponseComment_recycler_child(mComment.getText().toString(), comment_key, mUsername);

                        mComment.getText().clear();
                        mComment.setHint(context.getString(R.string.add_comment));
                        closeKeyboard();

                        isInResponseTo = false;

                        RecyclerView.SmoothScroller smoothScroller = new
                                LinearSmoothScroller(getApplicationContext()) {
                                    @Override protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_START;
                                    }
                                };

                        smoothScroller.setTargetPosition(0);
                        layoutManager.startSmoothScroll(smoothScroller);

                    } else {
                        Log.d(TAG, "onClick: attempting to submit new response comment.");
                        addNewResponseComment_recycler_child(mComment.getText().toString(), comment_key);

                        mComment.getText().clear();
                        closeKeyboard();

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

            } else
                Toast.makeText(context, context.getString(R.string.you_must_write_comment), Toast.LENGTH_SHORT).show();
        });

        arrowBack.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    private void clearAll() {
        if (commentsResponseList != null)
            commentsResponseList.clear();

        if (recyclerView_child_adapter != null)
            recyclerView_child_adapter = null;

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsResponseList = new ArrayList<>();
    }

    // get recyclerview child comment ______________________________________________________________
    private void getChildRecyclerviewResponseComments(String photo_id, String commentKey) {
        clearAll();
        try {
            Query query = myRef.child(context.getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    commentsResponseList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        CommentResponse commentResponse = new CommentResponse();
                        Map<Object, Object> hashMap = (HashMap<Object, Object>) ds.getValue();

                        assert hashMap != null;
                        commentResponse.setSuppressed(requireNonNull(hashMap.get(getString(R.string.field_suppressed))).toString());
                        commentResponse.setUserAnswer(Boolean.parseBoolean(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_userAnswer))).toString()));
                        commentResponse.setUser_id(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_user_id))).toString());
                        commentResponse.setComment(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_comment))).toString());
                        commentResponse.setCommentParentKey(comment_key);
                        commentResponse.setCommentKey(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_commentKey))).toString());
                        commentResponse.setUrl(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_url))).toString());
                        commentResponse.setThumbnail(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_thumbnail))).toString());
                        try {
                            commentResponse.setAnsweringUsername(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_answeringUsername))).toString());
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                        commentResponse.setDate_created(Long.parseLong(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_date_created))).toString()));

                        List<Like> likeResponseList = new ArrayList<>();
                        for (DataSnapshot data : ds
                                .child(context.getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(Objects.requireNonNull(data.getValue(Like.class)).getUser_id());
                            likeResponseList.add(like);
                        }
                        commentResponse.setLikes(likeResponseList);
                        commentsResponseList.add(commentResponse);
                    }

                    list_size = commentsResponseList.size();
                    recyclerView_child_adapter = new CommentResponseRecyclerChildAdapter(context, commentsResponseList, battleModel,
                            recyclerview_photo_id, recyclerview_fieldLike, comment_key, commentModel, userID, childItemClickListener, mComment,
                            comment, time, url, thumbnail, relLayout_view_overlay);
                    recyclerView.setAdapter(recyclerView_child_adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (NullPointerException e) {
            Log.d(TAG, "getChildRecyclerviewResponseComments: "+e.getMessage());
        }

    }

    // recyclerView child _________________________________________________________________________
    private void addNewResponseComment_recycler_child(String newComment, String commentKey){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                false, newComment, commentID, "", "", date.getTime(),
                user_id);

        recyclerView_child_adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            // send notification
            sendNotification(newComment, commentKey, "");

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    private void addNewChildResponseComment_recycler_child(String newComment, String commentKey, String username){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                true, newComment, commentID, "", username, date.getTime(),
                user_id);

        recyclerView_child_adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_child_comments))
                    .child(recyclerview_photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            // send notification
            sendNotification(newComment, commentKey, username);

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    // send notification to owner post
    @TargetApi(Build.VERSION_CODES.O)
    private void sendNotification(String comment, String commentKey, String answeringUserName) {
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
                userID, new_key, user_id, "",
                "", "", date.getTime(),
                true, false,
                true, false, false, false, true, false, false, false,
                false, post_id_field, category_of_post, post_type, true, post_id, "", "", true,
                comment, commentKey, answeringUserName, recyclerview_photo_id_upload, recyclerview_fieldLike_upload, time,
                commentModel.getComment(), commentModel.getCommentKey(), commentModel.getDate_created(), commentModel.getThumbnail(),
                commentModel.getUrl(), commentModel.getUser_id(),
                false, false, false, false,
                false, false, false,
                false, false);

        if (!userID.equals(user_id)) {
            myRef.child(context.getString(R.string.dbname_notification))
                    .child(userID)
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

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            if (!commentsResponseList.isEmpty()) {
                recyclerView_child_adapter.notifyDataSetChanged();
            }

            mComment.setHint(context.getString(R.string.add_comment));
            getChildRecyclerviewResponseComments(recyclerview_photo_id, comment_key);

            recyclerView.scrollToPosition(list_size - 1);
        }
    }
}