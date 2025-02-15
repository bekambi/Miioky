package com.bekambimouen.miiokychallenge.comment_fun;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.comment.publication.CommentPublication;
import com.bekambimouen.miiokychallenge.comment_fun.comment_response.adapter.CommentResponseFunAdapter;
import com.bekambimouen.miiokychallenge.fun.FunSuggestions;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
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

public class ViewResponseFunComment extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "ViewResponseFunComment";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView answering_username;
    private RelativeLayout parent, see_the_post, relLayout_view_overlay;
    private ProgressBar loading_progressBar;

    // vars
    private AppCompatActivity context;
    private CommentResponseFunAdapter adapter;
    private ChildItemClickListener childItemClickListener;
    private BattleModel_fun comment_video_fun, category_from_notification;
    private Comment commentModel;
    private String comment_key;
    private String comment;
    private String mUsername;
    private String userID;
    private String media_comment_url;
    private String media_comment_thumbnail;
    private String userName;
    private String edittext_clear_focus;
    private String from_notification_comment;
    private int resultsCount = 0;
    private long time;
    private Date date;
    private boolean isInResponseTo = false;
    private boolean isPosting = false;
    private ArrayList<CommentResponse> commentsResponseList, pagination;
    private Handler handler;
    // notification comment data ___________________________________________________________________
    private String post_id, post_id_field, admin_master, group_id, category_of_post, post_type = "",
            recyclerview_photo_id_upload, recyclerview_fieldLike_upload, the_user_who_posted_id;

    // firebase
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_view_response_fun_comment);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            comment_video_fun= gson.fromJson(getIntent().getStringExtra("comment_video_fun"), BattleModel_fun.class);
            commentModel = gson.fromJson(getIntent().getStringExtra("commentModel"), Comment.class);

            // from notification adapter
            category_from_notification = gson.fromJson(getIntent().getStringExtra("category_from_notification"), BattleModel_fun.class);
            from_notification_comment = getIntent().getStringExtra("from_notification_comment");
            String from_notification = getIntent().getStringExtra("from_notification");
            if (from_notification != null) {
                comment_key = getIntent().getStringExtra("author_commentKey");
                comment = getIntent().getStringExtra("author_comment");
                userID = getIntent().getStringExtra("author_id");
                media_comment_thumbnail = getIntent().getStringExtra("author_thumbnail");
                media_comment_url = getIntent().getStringExtra("author_url");
                time = getIntent().getLongExtra("author_comment_date_create", 0);

            } else {
                comment_key = getIntent().getStringExtra("comment_key");
                comment = getIntent().getStringExtra("comment");
                userID = getIntent().getStringExtra("userID");
                media_comment_url = getIntent().getStringExtra("media_comment_url");
                media_comment_thumbnail = getIntent().getStringExtra("media_comment_thumbnail");
                time= getIntent().getLongExtra("time", 0);
                userName = getIntent().getStringExtra("userName");
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
            group_id = getIntent().getStringExtra("group_id");

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        date = new Date();
        init(comment_video_fun);

        getResponseComments(comment_video_fun);

        // see the post of comment
        if (from_notification_comment != null) {
            see_the_post.setVisibility(View.VISIBLE);

            see_the_post.setOnClickListener(view1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, FunSuggestions.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(comment_video_fun);
                intent.putExtra("from_notification_comment", myGson);
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

    private void init(BattleModel_fun model) {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout icone_photo = findViewById(R.id.icone_photo);
        see_the_post = findViewById(R.id.see_the_post);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        mComment = findViewById(R.id.EditText_commentaire);
        answering_username = findViewById(R.id.answering_username);
        if (from_notification_comment == null && edittext_clear_focus == null)
            mComment.requestFocus();
        ImageView checkMark = findViewById(R.id.posterComment);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

                    answering_username.setText(Html.fromHtml(context.getString(R.string.answer_to)+" "+user.getUsername()));
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
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, CommentPublication.class);
            intent.putExtra("isInResponseTo", isInResponseTo);
            intent.putExtra("comment_key", comment_key);
            intent.putExtra("mUsername", mUsername);
            intent.putExtra("userid", model.getUser_id());
            intent.putExtra("photo_id", model.getPhoto_id());
            intent.putExtra("fun_comment", "fun_comment");
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
                if (isInResponseTo) {
                    Log.d(TAG, "onClick: attempting to submit new response comment.");
                    addNewChildResponseComment_video_fun(requireNonNull(mComment.getText()).toString(), comment_key, mUsername);

                    mComment.setText("");
                    mComment.setHint(context.getString(R.string.add_comment));
                    closeKeyboard();

                    isInResponseTo = false;

                } else {
                    Log.d(TAG, "onClick: attempting to submit new response comment.");
                    addNewResponseComment_video_fun(mComment.getText().toString(), comment_key);

                    mComment.setText("");
                    closeKeyboard();
                }
            }
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

    // get comment _____________________________________________________________________________

    private void clearAll() {
        if (commentsResponseList != null)
            commentsResponseList.clear();
        if (pagination != null)
            pagination.clear();

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsResponseList = new ArrayList<>();
        pagination = new ArrayList<>();
    }
    private void getResponseComments(BattleModel_fun model) {
        clearAll();
        try {
            Query query1 = myRef.child(context.getString(R.string.dbname_videos))
                    .child(model.getPhoto_id())
                    .child(context.getString(R.string.field_comments))
                    .child(comment_key)
                    .child(context.getString(R.string.field_comments));

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                    int iterations = commentsResponseList.size();
                    if(iterations > 20){
                        iterations = 20;
                    }

                    resultsCount = 20;
                    for(int i = 0; i < iterations; i++){
                        pagination.add(commentsResponseList.get(i));
                    }

                    getAdapter(model);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG, "getResponseComments: "+e.getMessage());
        }
    }

    private void getAdapter(BattleModel_fun model) {
        adapter = new CommentResponseFunAdapter(context, pagination, model, comment_key,
                childItemClickListener, commentModel, userID, comment, media_comment_url,
                media_comment_thumbnail, time, mComment, loading_progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);
    }

    private void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(commentsResponseList.size() > resultsCount && !commentsResponseList.isEmpty()){

                int iterations;
                if(commentsResponseList.size() > (resultsCount + 20)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = commentsResponseList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(commentsResponseList.get(i));
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

    // add response fun comment ___________________________________________________________________________________
    private void addNewResponseComment_video_fun(String newComment, String commentKey){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                false, newComment, commentID, "", "", date.getTime(),
                user_id);

        adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_videos))
                    .child(comment_video_fun.getPhoto_id())
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_fun))
                    .child(comment_video_fun.getUser_id())
                    .child(comment_video_fun.getPhoto_id())
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

    private void addNewChildResponseComment_video_fun(String newComment, String commentKey, String username){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                true, newComment, commentID, "", username, date.getTime(),
                user_id);

        adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_videos))
                    .child(comment_video_fun.getPhoto_id())
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_fun))
                    .child(comment_video_fun.getUser_id())
                    .child(comment_video_fun.getPhoto_id())
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
                false, true,
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
            map_number.put("user_id", userID);

            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                    .child(userID)
                    .child(new_key)
                    .setValue(map_number);
        }
    }

    public void showKeyboard() {
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
            if (!commentsResponseList.isEmpty())
                commentsResponseList.clear();
            adapter.notifyDataSetChanged();

            mComment.setHint(context.getString(R.string.add_comment));
            getResponseComments(comment_video_fun);
            recyclerView.scrollToPosition(pagination.size()-1);
        }
    }
}