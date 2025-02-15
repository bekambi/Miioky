package com.bekambimouen.miiokychallenge.comment_share;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.bekambimouen.miiokychallenge.comment_share.publication.CommentPublicationShare;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_double.CommentShareOnMiiokyImageDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_double.CommentShareOnMiiokyVideoDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_une.CommentShareOnMiiokyImageUneAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_une.CommentShareOnMiiokyVideoUneAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.recycler_img.CommentShareOnMiiokyRecyclerAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.resp_img_and_vid.CommentShareOnMiiokyRespImgDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.resp_img_and_vid.CommentShareOnMiiokyRespVidDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_double.CommentShareSingleTopImageDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_double.CommentShareSingleTopVideoDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_une.CommentShareSingleTopImageUneAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_une.CommentShareSingleTopVideoUneAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.recycler_img.CommentShareSingleTopRecyclerAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.resp_img_and_vid.CommentShareSingleTopRespImgDoubleAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.resp_img_and_vid.CommentShareSingleTopRespVidDoubleAdapter;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.search.ViewExplore;
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

public class ViewCommentShare extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "ViewCommentShare";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private RelativeLayout parent, relLayout_first_comment, see_the_post, relLayout_view_overlay;
    private ProgressBar loading_progressBar;

    // vars
    private ViewCommentShare context;

    // single top
    private CommentShareSingleTopImageUneAdapter imageUneSingleTop_adapter;
    private CommentShareSingleTopVideoUneAdapter videoUneSingleTop_adapter;
    private CommentShareSingleTopRecyclerAdapter recyclerSingleTop_adapter;
    private CommentShareSingleTopImageDoubleAdapter imageDoubleSingleTop_adapter;
    private CommentShareSingleTopVideoDoubleAdapter videoDoubleSingleTop_adapter;
    private CommentShareSingleTopRespImgDoubleAdapter response_imgSingleTop_adapter;
    private CommentShareSingleTopRespVidDoubleAdapter response_vidSingleTop_adapter;

    // single on Miioky
    private CommentShareOnMiiokyImageUneAdapter imageUneOnMiioky_adapter;
    private CommentShareOnMiiokyVideoUneAdapter videoUneOnMiioky_adapter;
    private CommentShareOnMiiokyRecyclerAdapter recyclerOnMiioky_adapter;
    private CommentShareOnMiiokyImageDoubleAdapter imageDoubleOnMiioky_adapter;
    private CommentShareOnMiiokyVideoDoubleAdapter videoDoubleOnMiioky_adapter;
    private CommentShareOnMiiokyRespImgDoubleAdapter response_imgOnMiioky_adapter;
    private CommentShareOnMiiokyRespVidDoubleAdapter response_vidOnMiioky_adapter;

    private LinearLayoutManager layoutManager;

    private int resultsCount = 0;
    private Handler handler;
    private boolean isPosting = false;

    private BattleModel battleModel;

    private String photo_id;
    private String from_full_image, from_full_video, from_view_recyclerview;

    // single top
    public BattleModel comment_image_une_single_top, comment_recycler_single_top, comment_image_double_single_top,
            comment_reponse_image_double_single_top;
    private String image_une_single_top, video_une_single_top, image_double_single_top, video_double_single_top,
            response_img_double_single_top, response_vid_double_single_top;

    // single on miioky
    public BattleModel comment_image_une_on_miioky, comment_recycler_on_miioky, comment_image_double_on_miioky,
            comment_reponse_image_double_on_miioky;
    private String image_une_on_miioky, video_une_on_miioky, image_double_on_miioky, video_double_on_miioky,
            response_img_double_on_miioky, response_vid_double_on_miioky;

    private String from_notification_comment;
    private ArrayList<Comment> commentsList, pagination;
    private Date date;
    private ItemImageUneClickListener itemImageUneClickListener;
    private boolean isResponse = false, isInResponseTo = false;

    // notification comment data ___________________________________________________________________
    private String the_user_who_posted_id, post_id, admin_master, group_id, category_of_post, post_type = "",
            post_id_field;
    private long time;
    private Comment commentModel;
    private String recyclerview_photo_id_upload, recyclerview_fieldLike_upload;
    // _____________________________________________________________________________________________

    // firebase
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_view_comment_share);
        context = this;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        handler = new Handler(Looper.getMainLooper());

        try{
            from_full_image = getIntent().getStringExtra("from_full_image");
            from_full_video = getIntent().getStringExtra("from_full_video");
            from_view_recyclerview = getIntent().getStringExtra("from_view_recyclerview");

            Gson gson = new Gson();
            // single top
            // if is from notification category of post is here
            comment_recycler_single_top = gson.fromJson(getIntent().getStringExtra("comment_recycler_single_top"),
                    BattleModel.class);
            comment_image_une_single_top = gson.fromJson(getIntent().getStringExtra("comment_image_une_single_top"),
                    BattleModel.class);
            comment_image_double_single_top = gson.fromJson(getIntent().getStringExtra("comment_image_double_single_top"),
                    BattleModel.class);
            comment_reponse_image_double_single_top = gson.fromJson(getIntent()
                    .getStringExtra("comment_reponse_image_double_single_top"), BattleModel.class);

            // single on Miioky
            // if is from notification category of post is here
            comment_recycler_on_miioky = gson.fromJson(getIntent().getStringExtra("comment_recycler_on_miioky"),
                    BattleModel.class);
            comment_image_une_on_miioky = gson.fromJson(getIntent().getStringExtra("comment_image_une_on_miioky"),
                    BattleModel.class);
            comment_image_double_on_miioky = gson.fromJson(getIntent().getStringExtra("comment_image_double_on_miioky"),
                    BattleModel.class);
            comment_reponse_image_double_on_miioky = gson.fromJson(getIntent()
                    .getStringExtra("comment_reponse_image_double_on_miioky"), BattleModel.class);

            // show publication
            // single top
            image_une_single_top = getIntent().getStringExtra("image_une_single_top");
            video_une_single_top = getIntent().getStringExtra("video_une_single_top");
            image_double_single_top = getIntent().getStringExtra("image_double_single_top");
            video_double_single_top = getIntent().getStringExtra("video_double_single_top");
            response_img_double_single_top = getIntent().getStringExtra("response_img_double_single_top");
            response_vid_double_single_top = getIntent().getStringExtra("response_vid_double_single_top");

            // single on Miioky
            image_une_on_miioky = getIntent().getStringExtra("image_une_on_miioky");
            video_une_on_miioky = getIntent().getStringExtra("video_une_on_miioky");
            image_double_on_miioky = getIntent().getStringExtra("image_double_on_miioky");
            video_double_on_miioky = getIntent().getStringExtra("video_double_on_miioky");
            response_img_double_on_miioky = getIntent().getStringExtra("response_img_double_on_miioky");
            response_vid_double_on_miioky = getIntent().getStringExtra("response_vid_double_on_miioky");

            // form notificationAdapter
            from_notification_comment = getIntent().getStringExtra("from_notification_comment");
            category_of_post = getIntent().getStringExtra("category_of_post");

        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        if (comment_recycler_single_top != null) {
            battleModel = comment_recycler_single_top;
            photo_id = comment_recycler_single_top.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_recycler_single_top.getUser_id();
            post_id = comment_recycler_single_top.getPhotoi_id();
            post_id_field = context.getString(R.string.field_photoi_id);
            category_of_post = "comment_recycler_single_top";
            post_type = "";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else if (comment_image_une_single_top != null) {
            battleModel = comment_image_une_single_top;
            photo_id = comment_image_une_single_top.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_image_une_single_top.getUser_id();
            post_id = comment_image_une_single_top.getPhoto_id();
            post_id_field = context.getString(R.string.field_photo_id);
            category_of_post = "comment_image_une_single_top";
            if (image_une_single_top != null)
                post_type = "image_une_single_top";
            else
                post_type = "video_une_single_top";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else if (comment_image_double_single_top != null){
            battleModel = comment_image_double_single_top;
            photo_id = comment_image_double_single_top.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_image_double_single_top.getUser_id();
            post_id = comment_image_double_single_top.getPhoto_id_un();
            post_id_field = context.getString(R.string.field_photo_id_un);
            category_of_post = "comment_image_double_single_top";
            if (image_double_single_top != null)
                post_type = "image_double_single_top";
            else
                post_type = "video_double_single_top";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else if (comment_reponse_image_double_single_top != null) {
            battleModel = comment_reponse_image_double_single_top;
            photo_id = comment_reponse_image_double_single_top.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_reponse_image_double_single_top.getUser_id();
            post_id = comment_reponse_image_double_single_top.getReponse_photoID();
            post_id_field = context.getString(R.string.field_reponse_photoID);
            category_of_post = "comment_reponse_image_double_single_top";
            if (response_img_double_single_top != null)
                post_type = "response_img_double_single_top";
            else
                post_type = "response_vid_double_single_top";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else
        if (comment_recycler_on_miioky != null) {
            battleModel = comment_recycler_on_miioky;
            photo_id = comment_recycler_on_miioky.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_recycler_on_miioky.getUser_id();
            post_id = comment_recycler_on_miioky.getPhotoi_id();
            post_id_field = context.getString(R.string.field_photoi_id);
            category_of_post = "comment_recycler_on_miioky";
            post_type = "";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else if (comment_image_une_on_miioky != null) {
            battleModel = comment_image_une_on_miioky;
            photo_id = comment_image_une_on_miioky.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_image_une_on_miioky.getUser_id();
            post_id = comment_image_une_on_miioky.getPhoto_id();
            post_id_field = context.getString(R.string.field_photo_id);
            category_of_post = "comment_image_une_on_miioky";
            if (image_une_on_miioky != null)
                post_type = "image_une_on_miioky";
            else
                post_type = "video_une_on_miioky";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else if (comment_image_double_on_miioky != null){
            battleModel = comment_image_double_on_miioky;
            photo_id = comment_image_double_on_miioky.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_image_double_on_miioky.getUser_id();
            post_id = comment_image_double_on_miioky.getPhoto_id_un();
            post_id_field = context.getString(R.string.field_photo_id_un);
            category_of_post = "comment_image_double_on_miioky";
            if (image_double_on_miioky != null)
                post_type = "image_double_on_miioky";
            else
                post_type = "video_double_on_miioky";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";

        } else if (comment_reponse_image_double_on_miioky != null) {
            battleModel = comment_reponse_image_double_on_miioky;
            photo_id = comment_reponse_image_double_on_miioky.getPhoto_id();
            // comment notification
            the_user_who_posted_id = comment_reponse_image_double_on_miioky.getUser_id();
            post_id = comment_reponse_image_double_on_miioky.getReponse_photoID();
            post_id_field = context.getString(R.string.field_reponse_photoID);
            category_of_post = "comment_reponse_image_double_on_miioky";
            if (response_img_double_on_miioky != null)
                post_type = "response_img_double_on_miioky";
            else
                post_type = "response_vid_double_on_miioky";
            admin_master = "";
            group_id = "";
            recyclerview_photo_id_upload = "";
            recyclerview_fieldLike_upload = "";
        }

        getResponseComment(battleModel);
        date = new Date();
        init();

        // see the post of comment
        if (from_notification_comment != null) {
            see_the_post.setVisibility(View.VISIBLE);

            see_the_post.setOnClickListener(view1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();

                String myGson = null;
                if (comment_image_double_on_miioky != null)
                    myGson = gson.toJson(comment_image_double_on_miioky);
                if (comment_image_double_single_top != null)
                    myGson = gson.toJson(comment_image_double_single_top);
                if (comment_image_une_on_miioky != null)
                    myGson = gson.toJson(comment_image_une_on_miioky);
                if (comment_image_une_single_top != null)
                    myGson = gson.toJson(comment_image_une_single_top);
                if (comment_recycler_on_miioky != null)
                    myGson = gson.toJson(comment_recycler_on_miioky);
                if (comment_recycler_single_top != null)
                    myGson = gson.toJson(comment_recycler_single_top);
                if (comment_reponse_image_double_on_miioky != null)
                    myGson = gson.toJson(comment_reponse_image_double_on_miioky);
                if (comment_reponse_image_double_single_top != null)
                    myGson = gson.toJson(comment_reponse_image_double_single_top);

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
        relLayout_first_comment = findViewById(R.id.relLayout_first_comment);
        see_the_post = findViewById(R.id.see_the_post);
        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mComment = findViewById(R.id.EditText_commentaire);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        RelativeLayout relLayout_arrowBack = findViewById(R.id.relLayout_arrowBack);
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
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            isPosting = true;
            Intent intent= new Intent(context, CommentPublicationShare.class);
            intent.putExtra("userid", battleModel.getUser_id());
            intent.putExtra("photo_id", photo_id);
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

        relLayout_arrowBack.setOnClickListener(view -> {
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

        checkMark.setOnClickListener(v -> {
            if (comment_recycler_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_recycler_single_top(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_une_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_une_single_top(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_double_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_double_single_top(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }
            } else if (comment_reponse_image_double_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_response_image_double_single_top(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else

            if (comment_recycler_on_miioky != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_recycler_on_miioky(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_une_on_miioky != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_une_on_miioky(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_double_on_miioky != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_double_on_miioky(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }
            } else if (comment_reponse_image_double_on_miioky != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_response_image_double_on_miioky(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

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
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewResponseCommentShare.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(battleModel);
            String myGson2 = gson.toJson(commentModel);

            intent.putExtra("comment_key", commentKey);
            intent.putExtra("comment", comment);
            intent.putExtra("userID", user_id);
            intent.putExtra("media_comment_url", url);
            intent.putExtra("media_comment_thumbnail", thumbnail);
            intent.putExtra("time", time);
            intent.putExtra("commentModel", myGson2);

            if (comment_image_une_single_top != null || comment_image_une_on_miioky != null)
                intent.putExtra("comment_image_une", myGson);
            else if (comment_recycler_single_top != null || comment_recycler_on_miioky != null)
                intent.putExtra("comment_recycler", myGson);
            else if (comment_image_double_single_top != null || comment_image_double_on_miioky != null)
                intent.putExtra("comment_image_double", myGson);
            else if (comment_reponse_image_double_single_top != null || comment_reponse_image_double_on_miioky != null)
                intent.putExtra("comment_reponse_image_double", myGson);

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
    }

    private void setupWidgets(BattleModel battleModel){
        if (comment_recycler_single_top != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            recyclerSingleTop_adapter = new CommentShareSingleTopRecyclerAdapter(this, pagination, battleModel,
                    itemImageUneClickListener, mComment, from_view_recyclerview, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            try {
                recyclerView.setAdapter(recyclerSingleTop_adapter);
            } catch (NullPointerException e) {
                Log.d(TAG, "setupWidgets: "+e.getMessage());
            }

            if (recyclerSingleTop_adapter.getItemCount() == 1)
                relLayout_first_comment.setVisibility(View.VISIBLE);

        } else if (comment_image_double_single_top != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (image_double_single_top != null) {
                imageDoubleSingleTop_adapter = new CommentShareSingleTopImageDoubleAdapter(this, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_image, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                try {
                    recyclerView.setAdapter(imageDoubleSingleTop_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (imageDoubleSingleTop_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

            } else if (video_double_single_top != null) {
                videoDoubleSingleTop_adapter = new CommentShareSingleTopVideoDoubleAdapter(this, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_video, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);

                try {
                    recyclerView.setAdapter(videoDoubleSingleTop_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (videoDoubleSingleTop_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_reponse_image_double_single_top != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (response_img_double_single_top != null) {
                response_imgSingleTop_adapter = new CommentShareSingleTopRespImgDoubleAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_image, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                try {
                    recyclerView.setAdapter(response_imgSingleTop_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (response_imgSingleTop_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

            } else if (response_vid_double_single_top != null) {
                response_vidSingleTop_adapter = new CommentShareSingleTopRespVidDoubleAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_video, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                try {
                    recyclerView.setAdapter(response_vidSingleTop_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (response_vidSingleTop_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_image_une_single_top != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }
            if (image_une_single_top != null) {
                imageUneSingleTop_adapter = new CommentShareSingleTopImageUneAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_image, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);

                try {
                    recyclerView.setAdapter(imageUneSingleTop_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (imageUneSingleTop_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

            } else if (video_une_single_top != null) {
                videoUneSingleTop_adapter = new CommentShareSingleTopVideoUneAdapter(this, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_video, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);

                try {
                    recyclerView.setAdapter(videoUneSingleTop_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (videoUneSingleTop_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);
            }
        } else
        if (comment_recycler_on_miioky != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            recyclerOnMiioky_adapter = new CommentShareOnMiiokyRecyclerAdapter(this, pagination, battleModel,
                    itemImageUneClickListener, mComment, from_view_recyclerview, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            try {
                recyclerView.setAdapter(recyclerOnMiioky_adapter);
            } catch (NullPointerException e) {
                Log.d(TAG, "setupWidgets: "+e.getMessage());
            }

            if (recyclerOnMiioky_adapter.getItemCount() == 1)
                relLayout_first_comment.setVisibility(View.VISIBLE);

        } else if (comment_image_double_on_miioky != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (image_double_on_miioky != null) {
                imageDoubleOnMiioky_adapter = new CommentShareOnMiiokyImageDoubleAdapter(this, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_image, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                try {
                    recyclerView.setAdapter(imageDoubleOnMiioky_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (imageDoubleOnMiioky_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

            } else if (video_double_on_miioky != null) {
                videoDoubleOnMiioky_adapter = new CommentShareOnMiiokyVideoDoubleAdapter(this, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_video, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);

                try {
                    recyclerView.setAdapter(videoDoubleOnMiioky_adapter);

                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (videoDoubleOnMiioky_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_reponse_image_double_on_miioky != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (response_img_double_on_miioky != null) {
                response_imgOnMiioky_adapter = new CommentShareOnMiiokyRespImgDoubleAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_image, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                try {
                    recyclerView.setAdapter(response_imgOnMiioky_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (response_imgOnMiioky_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

            } else if (response_vid_double_on_miioky != null) {
                response_vidOnMiioky_adapter = new CommentShareOnMiiokyRespVidDoubleAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_video, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                try {
                    recyclerView.setAdapter(response_vidOnMiioky_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (response_vidOnMiioky_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_image_une_on_miioky != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }
            if (image_une_on_miioky != null) {
                imageUneOnMiioky_adapter = new CommentShareOnMiiokyImageUneAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_image, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);

                try {
                    recyclerView.setAdapter(imageUneOnMiioky_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (imageUneOnMiioky_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

            } else if (video_une_on_miioky != null) {
                videoUneOnMiioky_adapter = new CommentShareOnMiiokyVideoUneAdapter(this, pagination, battleModel,
                        itemImageUneClickListener, mComment, from_full_video, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);

                try {
                    recyclerView.setAdapter(videoUneOnMiioky_adapter);
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupWidgets: "+e.getMessage());
                }

                if (videoUneOnMiioky_adapter.getItemCount() == 1)
                    relLayout_first_comment.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(commentsList.size() > resultsCount && !commentsList.isEmpty()){

                int iterations;
                if(commentsList.size() > (resultsCount + 20)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = commentsList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(commentsList.get(i));
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

    // single top ________________________________________
    private void addNewComment_response_image_double_single_top(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (response_img_double_single_top != null) {
            response_imgSingleTop_adapter.addComment(comment);

        } else if (response_vid_double_single_top != null) {
            response_vidSingleTop_adapter.addComment(comment);
        }

        try {
            assert commentID != null;

            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    private void addNewComment_image_double_single_top(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_double_single_top != null) {
            imageDoubleSingleTop_adapter.addComment(comment);

        } else if (video_double_single_top != null) {
            videoDoubleSingleTop_adapter.addComment(comment);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    private void addNewComment_image_une_single_top(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_une_single_top != null) {
            imageUneSingleTop_adapter.addComment(comment);

        } else if (video_une_single_top != null) {
            videoUneSingleTop_adapter.addComment(comment);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    private void addNewComment_recycler_single_top(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        recyclerSingleTop_adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    // single on Miioky ________________________________________
    private void addNewComment_response_image_double_on_miioky(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (response_img_double_on_miioky != null) {
            response_imgOnMiioky_adapter.addComment(comment);

        } else if (response_vid_double_on_miioky != null) {
            response_vidOnMiioky_adapter.addComment(comment);
        }

        try {
            assert commentID != null;

            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    private void addNewComment_image_double_on_miioky(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_double_on_miioky != null) {
            imageDoubleOnMiioky_adapter.addComment(comment);

        } else if (video_double_on_miioky != null) {
            videoDoubleOnMiioky_adapter.addComment(comment);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    private void addNewComment_image_une_on_miioky(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_une_on_miioky != null) {
            imageUneOnMiioky_adapter.addComment(comment);

        } else if (video_une_on_miioky != null) {
            videoUneOnMiioky_adapter.addComment(comment);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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

    private void addNewComment_recycler_on_miioky(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        recyclerOnMiioky_adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(getApplicationContext()) {
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
        if (pagination != null)
            pagination.clear();

        if (imageUneSingleTop_adapter != null)
            imageUneSingleTop_adapter = null;

        if (videoUneSingleTop_adapter != null)
            videoUneSingleTop_adapter = null;

        if (imageDoubleSingleTop_adapter != null)
            imageDoubleSingleTop_adapter = null;

        if (videoDoubleSingleTop_adapter != null)
            videoDoubleSingleTop_adapter = null;

        if (response_imgSingleTop_adapter != null)
            response_imgSingleTop_adapter = null;

        if (response_vidSingleTop_adapter != null)
            response_vidSingleTop_adapter = null;

        if (recyclerSingleTop_adapter != null)
            recyclerSingleTop_adapter = null;

        if (imageUneOnMiioky_adapter != null)
            imageUneOnMiioky_adapter = null;

        if (videoUneOnMiioky_adapter != null)
            videoUneOnMiioky_adapter = null;

        if (imageDoubleOnMiioky_adapter != null)
            imageDoubleOnMiioky_adapter = null;

        if (videoDoubleOnMiioky_adapter != null)
            videoDoubleOnMiioky_adapter = null;

        if (response_imgOnMiioky_adapter != null)
            response_imgOnMiioky_adapter = null;

        if (response_vidOnMiioky_adapter != null)
            response_vidOnMiioky_adapter = null;

        if (recyclerOnMiioky_adapter != null)
            recyclerOnMiioky_adapter = null;

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getResponseComment(BattleModel battleModel) {
        Log.d(TAG, "onChildAdded: child added.");
        clearAll();

        Query query = null;
        if (comment_image_une_single_top != null || comment_image_une_on_miioky != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_image_double_single_top != null || comment_image_double_on_miioky != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_recycler_single_top != null || comment_recycler_on_miioky != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_reponse_image_double_single_top != null || comment_reponse_image_double_on_miioky != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        }

        assert query != null;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot singleSnapshot :  snapshot.getChildren()){

                    BattleModel photo = new BattleModel();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    assert objectMap != null;
                    if (comment_image_une_single_top != null || comment_image_une_on_miioky != null) {
                        photo.setCaption(requireNonNull(objectMap.get(getString(R.string.field_caption))).toString());
                        photo.setTags(requireNonNull(objectMap.get(getString(R.string.field_tags))).toString());
                        photo.setPhoto_id(requireNonNull(objectMap.get(getString(R.string.field_photo_id))).toString());
                    }
                    if (comment_recycler_single_top != null || comment_recycler_on_miioky != null) {
                        photo.setCaption(requireNonNull(objectMap.get(getString(R.string.field_caption))).toString());
                        photo.setTags(requireNonNull(objectMap.get(getString(R.string.field_tags))).toString());
                        photo.setPhotoi_id(requireNonNull(objectMap.get(getString(R.string.field_photoi_id))).toString());
                    }
                    if (comment_image_double_single_top != null || comment_image_double_on_miioky != null) {
                        photo.setCaption(requireNonNull(objectMap.get(getString(R.string.field_captionUn))).toString());
                        photo.setTags(requireNonNull(objectMap.get(getString(R.string.field_tagsUn))).toString());
                        photo.setPhoto_id(requireNonNull(objectMap.get(getString(R.string.field_photo_id_un))).toString());
                    }
                    if (comment_reponse_image_double_single_top != null || comment_reponse_image_double_on_miioky != null) {
                        photo.setReponse_caption(requireNonNull(objectMap.get(getString(R.string.field_reponse_caption))).toString());
                        photo.setReponse_tag(requireNonNull(objectMap.get(getString(R.string.field_reponse_tag))).toString());
                        photo.setReponse_photoID(requireNonNull(objectMap.get(getString(R.string.field_reponse_photoID))).toString());

                        photo.setInvite_caption(requireNonNull(objectMap.get(getString(R.string.field_invite_caption))).toString());
                        photo.setInvite_tag(requireNonNull(objectMap.get(getString(R.string.field_invite_tag))).toString());
                        photo.setInvite_photoID(requireNonNull(objectMap.get(getString(R.string.field_invite_photoID))).toString());
                    }
                    photo.setUser_id(requireNonNull(objectMap.get(getString(R.string.field_user_id))).toString());
                    photo.setDate_created(Long.parseLong(requireNonNull(objectMap.get(getString(R.string.field_date_created))).toString()));

                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comment_share)).getChildren()){
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
                if (comment_image_une_single_top != null) {
                    if (image_une_single_top != null)
                        imageUneSingleTop_adapter.notifyDataSetChanged();
                    else if (video_une_single_top != null)
                        videoUneSingleTop_adapter.notifyDataSetChanged();

                } else if (comment_image_double_single_top != null) {
                    if (image_double_single_top != null)
                        imageDoubleSingleTop_adapter.notifyDataSetChanged();
                    else if (video_double_single_top != null)
                        videoDoubleSingleTop_adapter.notifyDataSetChanged();

                } else if (comment_reponse_image_double_single_top != null) {
                    if (response_img_double_single_top != null)
                        response_imgSingleTop_adapter.notifyDataSetChanged();
                    else if (response_vid_double_single_top != null)
                        response_vidSingleTop_adapter.notifyDataSetChanged();

                } else if (comment_recycler_single_top != null) {
                    recyclerSingleTop_adapter.notifyDataSetChanged();

                }

                else if (comment_image_une_on_miioky != null) {
                    if (image_une_on_miioky != null)
                        imageUneOnMiioky_adapter.notifyDataSetChanged();
                    else if (video_une_on_miioky != null)
                        videoUneOnMiioky_adapter.notifyDataSetChanged();

                } else if (comment_image_double_on_miioky != null) {
                    if (image_double_on_miioky != null)
                        imageDoubleOnMiioky_adapter.notifyDataSetChanged();
                    else if (video_double_on_miioky != null)
                        videoDoubleOnMiioky_adapter.notifyDataSetChanged();

                } else if (comment_reponse_image_double_on_miioky != null) {
                    if (response_img_double_on_miioky != null)
                        response_imgOnMiioky_adapter.notifyDataSetChanged();
                    else if (response_vid_double_on_miioky != null)
                        response_vidOnMiioky_adapter.notifyDataSetChanged();

                } else if (comment_recycler_on_miioky != null) {
                    recyclerOnMiioky_adapter.notifyDataSetChanged();

                }
            }

            if (battleModel != null) {
                getResponseComment(battleModel);

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