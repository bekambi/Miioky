package com.bekambimouen.miiokychallenge.groups.comment;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.comment.comment_response.adapter.GroupCommentResponseImageDoubleAdapter;
import com.bekambimouen.miiokychallenge.groups.comment.comment_response.adapter.GroupCommentResponseImageUneAdapter;
import com.bekambimouen.miiokychallenge.groups.comment.comment_response.adapter.GroupCommentResponseRecyclerAdapter;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview.comment.comment_response.adapter.GroupCommentResponseRecyclerChildAdapter;
import com.bekambimouen.miiokychallenge.groups.comment.comment_response.adapter.GroupCommentResponseResponseAdapter;
import com.bekambimouen.miiokychallenge.groups.comment.publication.GroupCommentPublication;
import com.bekambimouen.miiokychallenge.groups.explorer.GroupExplorer;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_CommentResponse;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupResponseComment extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "GroupResponseComment";

    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView answering_username;
    private ProgressBar loading_progressBar;
    private RelativeLayout parent, see_the_post, relLayout_view_overlay;

    // vars
    private GroupResponseComment context;
    private GroupCommentResponseImageUneAdapter image_une_adapter;
    private GroupCommentResponseRecyclerAdapter recyclerView_adapter;
    private GroupCommentResponseRecyclerChildAdapter recyclerView_child_adapter;
    private GroupCommentResponseImageDoubleAdapter image_double_adapter;
    private GroupCommentResponseResponseAdapter response_image_double_adapter;
    private ChildItemClickListener childItemClickListener;
    private BattleModel comment_image_une, comment_recycler,
            comment_image_double, comment_reponse_image_double;
    private BattleModel battleModel;
    private Comment commentModel;
    private String photo_id;
    private String comment_key;
    private String comment;
    private String mUsername;
    private String userID;
    private String media_comment_url;
    private String media_comment_thumbnail;
    private String userName;
    private String recyclerview_fieldLike;
    private String edittext_clear_focus;
    private LinearLayoutManager layoutManager;
    private long time;
    private Date date;
    private boolean isInResponseTo = false;
    private boolean isPosting = false;
    private int resultsCount = 0, list_size = 0;
    private Handler handler;

    private String from_notification;
    // notification comment data ___________________________________________________________________
    private String post_id, post_id_field, admin_master, group_id, category_of_post, post_type = "",
            recyclerview_photo_id_upload, recyclerview_fieldLike_upload, the_user_who_posted_id;
    // _____________________________________________________________________________________________

    private ArrayList<CommentResponse> commentsResponseList, pagination;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_response_comment);
        context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            comment_image_une = gson.fromJson(getIntent().getStringExtra("comment_image_une"), BattleModel.class);
            comment_recycler = gson.fromJson(getIntent().getStringExtra("comment_recycler"), BattleModel.class);
            comment_image_double = gson.fromJson(getIntent().getStringExtra("comment_image_double"), BattleModel.class);
            comment_reponse_image_double = gson.fromJson(getIntent().getStringExtra("comment_reponse_image_double"), BattleModel.class);
            commentModel = gson.fromJson(getIntent().getStringExtra("commentModel"), Comment.class);

            // from notification adapter
            from_notification = getIntent().getStringExtra("from_notification");
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
                recyclerview_fieldLike = getIntent().getStringExtra("recyclerview_fieldLike");
                edittext_clear_focus = getIntent().getStringExtra("edittext_clear_focus");
            }

            // notification comment
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

        if (comment_image_une != null) {
            battleModel = comment_image_une;
            photo_id = comment_image_une.getPhoto_id();

        } else if (comment_recycler != null) {
            battleModel = comment_recycler;
            photo_id = comment_recycler.getPhotoi_id();

        } else if (comment_image_double != null) {
            battleModel = comment_image_double;
            photo_id = comment_image_double.getPhoto_id_un();

        } else if (comment_reponse_image_double != null) {
            battleModel = comment_reponse_image_double;
            photo_id = comment_reponse_image_double.getReponse_photoID();
        }

        date = new Date();
        init();

        getResponseComments(photo_id);

        // see the post of comment
        if (from_notification != null) {
            see_the_post.setVisibility(View.VISIBLE);

            see_the_post.setOnClickListener(view1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupExplorer.class);
                Gson gson = new Gson();

                String myGson = null;
                if (comment_image_double != null)
                    myGson = gson.toJson(comment_image_double);
                if (comment_image_une != null)
                    myGson = gson.toJson(comment_image_une);
                if (comment_recycler != null)
                    myGson = gson.toJson(comment_recycler);
                if (comment_reponse_image_double != null)
                    myGson = gson.toJson(comment_reponse_image_double);

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

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        see_the_post = findViewById(R.id.see_the_post);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout icone_photo = findViewById(R.id.icone_photo);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        mComment = findViewById(R.id.EditText_commentaire);
        answering_username = findViewById(R.id.answering_username);
        if (from_notification == null && edittext_clear_focus == null) {
            mComment.requestFocus();
            showKeyboard();
        }
        // widgets
        ImageView posterComment = findViewById(R.id.posterComment);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
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

        icone_photo.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            isPosting = true;
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, GroupCommentPublication.class);
            intent.putExtra("isInResponseTo", isInResponseTo);
            intent.putExtra("comment_key", comment_key);
            intent.putExtra("mUsername", mUsername);
            intent.putExtra("group_id", battleModel.getGroup_id());
            intent.putExtra("photo_id", photo_id);
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

        posterComment.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                // to know if the member activity has limited
                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(battleModel.getGroup_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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

    // add comment to database
    private void downloadComment() {
        String newComment = requireNonNull(mComment.getText()).toString().trim();
        if (!TextUtils.isEmpty(newComment)) {
            if (battleModel != null) {
                if (comment_reponse_image_double != null) {
                    if (isInResponseTo) {
                        Log.d(TAG, "onClick: attempting to submit new response comment.");
                        addNewChildResponseComment_CommentImgDouble(battleModel, photo_id, requireNonNull(mComment.getText()).toString(), comment_key, mUsername);

                        mComment.getText().clear();
                        mComment.setHint(context.getString(R.string.add_comment));
                        closeKeyboard();

                        isInResponseTo = false;

                        RecyclerView.SmoothScroller smoothScroller = new
                                LinearSmoothScroller(context.getApplicationContext()) {
                                    @Override protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_START;
                                    }
                                };

                        smoothScroller.setTargetPosition(0);
                        layoutManager.startSmoothScroll(smoothScroller);

                    } else {
                        Log.d(TAG, "onClick: attempting to submit new response comment.");
                        addNewResponseComment_CommentImgDouble(battleModel, photo_id, requireNonNull(mComment.getText()).toString(), comment_key);

                        mComment.getText().clear();
                        closeKeyboard();

                        RecyclerView.SmoothScroller smoothScroller = new
                                LinearSmoothScroller(context.getApplicationContext()) {
                                    @Override protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_START;
                                    }
                                };

                        smoothScroller.setTargetPosition(0);
                        layoutManager.startSmoothScroll(smoothScroller);
                    }

                } else {
                    if (isInResponseTo) {
                        Log.d(TAG, "onClick: attempting to submit new response comment.");
                        addNewChildResponseComment(battleModel, photo_id, requireNonNull(mComment.getText()).toString(), comment_key, mUsername);

                        mComment.getText().clear();
                        mComment.setHint(context.getString(R.string.add_comment));
                        closeKeyboard();

                        isInResponseTo = false;

                        RecyclerView.SmoothScroller smoothScroller = new
                                LinearSmoothScroller(context.getApplicationContext()) {
                                    @Override protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_START;
                                    }
                                };

                        smoothScroller.setTargetPosition(0);
                        layoutManager.startSmoothScroll(smoothScroller);

                    } else {
                        Log.d(TAG, "onClick: attempting to submit new response comment.");
                        addNewResponseComment(battleModel, photo_id, requireNonNull(mComment.getText()).toString(), comment_key);

                        mComment.getText().clear();
                        closeKeyboard();

                        RecyclerView.SmoothScroller smoothScroller = new
                                LinearSmoothScroller(context.getApplicationContext()) {
                                    @Override protected int getVerticalSnapPreference() {
                                        return LinearSmoothScroller.SNAP_TO_START;
                                    }
                                };

                        smoothScroller.setTargetPosition(0);
                        layoutManager.startSmoothScroll(smoothScroller);
                    }
                }
            }

        } else
            Toast.makeText(context, context.getString(R.string.you_must_write_comment), Toast.LENGTH_SHORT).show();
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
                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_have_reached_the_post_quota)
                        +" "+follower.getNumber_posts_per_day()+" "+context.getString(R.string.publications)
                        +" "+context.getString(R.string.per_day)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());
                positiveButton.setOnClickListener(view3 -> d.dismiss());
            }
        }
    }

    // get comment _____________________________________________________________________________
    private void getResponseComments(String photo_id) {
        clearAll();
        try {
            Query query1 = myRef.child(context.getString(R.string.dbname_group_media))
                    .child(photo_id)
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

                    if (comment_image_une != null) {
                        int iterations = commentsResponseList.size();

                        if(iterations > 20){
                            iterations = 20;
                        }

                        resultsCount = 20;
                        for(int i = 0; i < iterations; i++){
                            pagination.add(commentsResponseList.get(i));
                        }

                        list_size = commentsResponseList.size();

                        getImageUneAdapter();

                    } else if (comment_recycler != null) {
                        int iterations = commentsResponseList.size();

                        if(iterations > 20){
                            iterations = 20;
                        }

                        resultsCount = 20;
                        for(int i = 0; i < iterations; i++){
                            pagination.add(commentsResponseList.get(i));
                        }

                        list_size = commentsResponseList.size();

                        getRecyclerViewAdapter();

                    } else if (comment_image_double != null) {
                        int iterations = commentsResponseList.size();

                        if(iterations > 20){
                            iterations = 20;
                        }

                        resultsCount = 20;
                        for(int i = 0; i < iterations; i++){
                            pagination.add(commentsResponseList.get(i));
                        }

                        list_size = commentsResponseList.size();

                        getImageDoubleAdapter();

                    } else if (comment_reponse_image_double != null) {
                        int iterations = commentsResponseList.size();

                        if(iterations > 20){
                            iterations = 20;
                        }

                        resultsCount = 20;
                        for(int i = 0; i < iterations; i++){
                            pagination.add(commentsResponseList.get(i));
                        }

                        list_size = commentsResponseList.size();

                        getResponseImageDoubleAdapter();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG, "getResponseComments: "+e.getMessage());
        }
    }

    private void getImageUneAdapter() {
        image_une_adapter = new GroupCommentResponseImageUneAdapter(context, pagination, battleModel,
                comment_key, childItemClickListener, null, comment_reponse_image_double,
                commentModel, userID, comment, media_comment_url,
                media_comment_thumbnail, null, recyclerview_fieldLike, photo_id, time,
                mComment, loading_progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(image_une_adapter);
    }

    private void getRecyclerViewAdapter() {
        recyclerView_adapter = new GroupCommentResponseRecyclerAdapter(context, pagination, battleModel,
                comment_key, childItemClickListener, null, comment_reponse_image_double,
                commentModel, userID, comment, media_comment_url,
                media_comment_thumbnail, null, recyclerview_fieldLike, photo_id, time,
                mComment, loading_progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(recyclerView_adapter);
    }

    private void getImageDoubleAdapter() {
        image_double_adapter = new GroupCommentResponseImageDoubleAdapter(context, pagination, battleModel,
                comment_key, childItemClickListener, null, comment_reponse_image_double,
                commentModel, userID, comment, media_comment_url,
                media_comment_thumbnail, null, recyclerview_fieldLike, photo_id, time,
                mComment, loading_progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(image_double_adapter);
    }

    private void getResponseImageDoubleAdapter() {
        response_image_double_adapter = new GroupCommentResponseResponseAdapter(context, pagination, battleModel,
                comment_key, childItemClickListener, null, comment_reponse_image_double,
                commentModel, userID, comment, media_comment_url,
                media_comment_thumbnail, null, recyclerview_fieldLike, photo_id, time,
                mComment, loading_progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(response_image_double_adapter);
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

    private void clearAll() {
        if (commentsResponseList != null)
            commentsResponseList.clear();
        if (pagination != null)
            pagination.clear();

        if (image_une_adapter != null)
            image_une_adapter = null;

        if (recyclerView_adapter != null)
            recyclerView_adapter = null;

        if (recyclerView_child_adapter != null)
            recyclerView_child_adapter = null;

        if (image_double_adapter != null)
            image_double_adapter = null;

        if (response_image_double_adapter != null)
            response_image_double_adapter = null;

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsResponseList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    // add new comment response image une _________________________________________________________
    private void addNewResponseComment(BattleModel model, String photo_id, String newComment, String commentKey){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                false, newComment, commentID, "", "", date.getTime(),
                user_id);

        if (comment_image_une != null)
            image_une_adapter.addComment(comment);
        else if (comment_recycler != null)
            recyclerView_adapter.addComment(comment);
        else if (comment_image_double != null)
            image_double_adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);


            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(photo_id)
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

    private void addNewChildResponseComment(BattleModel model, String photo_id, String newComment, String commentKey, String username){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                true, newComment, commentID, "", username, date.getTime(),
                user_id);

        if (comment_image_une != null)
            image_une_adapter.addComment(comment);
        else if (comment_recycler != null)
            recyclerView_adapter.addComment(comment);
        else if (comment_image_double != null)
            image_double_adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(photo_id)
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

    // response image double _______________________________________________________________________
    private void addNewResponseComment_CommentImgDouble(BattleModel model, String photo_id, String newComment, String commentKey){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                false, newComment, commentID, "", "", date.getTime(),
                user_id);

        response_image_double_adapter.addComment(comment);

        try {
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(photo_id)
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

    private void addNewChildResponseComment_CommentImgDouble(BattleModel model, String photo_id, String newComment, String commentKey, String username){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                true, newComment, commentID, "", username, date.getTime(),
                user_id);

        response_image_double_adapter.addComment(comment);

        try {
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(photo_id)
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

    // add post point and send notification to owner post
    @TargetApi(Build.VERSION_CODES.O)
    private void sendNotification(String comment, String commentKey, String answeringUserName) {
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
                userID, new_key, user_id, admin_master,
                "", group_id, date.getTime(),
                false, false,
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

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                commentsResponseList.clear();
                if (comment_image_une != null)
                    image_une_adapter.notifyDataSetChanged();

                else if (comment_image_double != null)
                    image_double_adapter.notifyDataSetChanged();

                else if (comment_reponse_image_double != null)
                    response_image_double_adapter.notifyDataSetChanged();

                else if (comment_recycler != null)
                    recyclerView_adapter.notifyDataSetChanged();
            }

            mComment.setHint(context.getString(R.string.add_comment));
            getResponseComments(photo_id);

            recyclerView.scrollToPosition(list_size - 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeKeyboard();
    }
}