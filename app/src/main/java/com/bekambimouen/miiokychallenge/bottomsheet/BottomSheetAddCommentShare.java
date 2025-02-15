package com.bekambimouen.miiokychallenge.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.comment_share.ViewResponseCommentShare;
import com.bekambimouen.miiokychallenge.comment_share.publication.CommentPublicationShare;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_double.bottomsheet_adapter.CommentShareOnMiiokyImageDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_double.bottomsheet_adapter.CommentShareOnMiiokyVideoDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_une.bottomsheet_adapter.CommentShareOnMiiokyImageUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.img_and_vid_une.bottomsheet_adapter.CommentShareOnMiiokyVideoUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.recycler_img.bottomsheet_adapter.CommentShareOnMiiokyRecyclerBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.resp_img_and_vid.bottomsheet_adapter.CommentShareOnMiiokyRespImgDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_on_miioky.resp_img_and_vid.bottomsheet_adapter.CommentShareOnMiiokyRespVidDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_double.bottomsheet_adapter.CommentShareSingleTopImageDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_double.bottomsheet_adapter.CommentShareSingleTopVideoDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_une.bottomsheet_adapter.CommentShareSingleTopImageUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_une.bottomsheet_adapter.CommentShareSingleTopVideoUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.recycler_img.bottomsheet_adapter.CommentShareSingleTopRecyclerBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.resp_img_and_vid.bottomsheet_adapter.CommentShareSingleTopRespImgDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment_share.single_top.resp_img_and_vid.bottomsheet_adapter.CommentShareSingleTopRespVidDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Comment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

public class BottomSheetAddCommentShare extends BottomSheetDialogFragment implements OnLoadMoreItemsListener {
    private static final String TAG = "BottomSheetAddCommentShare";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView number_of_comments;
    private View line_view;
    private RelativeLayout relLayout_first_comment;
    private ProgressBar loading_progressBar;

    // vars
    private final AppCompatActivity context;

    // single top
    private CommentShareSingleTopImageUneBottomSheetAdapter imageUneSingleTop_adapter;
    private CommentShareSingleTopVideoUneBottomSheetAdapter videoUneSingleTop_adapter;
    private CommentShareSingleTopRecyclerBottomSheetAdapter recyclerSingleTop_adapter;
    private CommentShareSingleTopImageDoubleBottomSheetAdapter imageDoubleSingleTop_adapter;
    private CommentShareSingleTopVideoDoubleBottomSheetAdapter videoDoubleSingleTop_adapter;
    private CommentShareSingleTopRespImgDoubleBottomSheetAdapter response_imgSingleTop_adapter;
    private CommentShareSingleTopRespVidDoubleBottomSheetAdapter response_vidSingleTop_adapter;

    // single on Miioky
    private CommentShareOnMiiokyImageUneBottomSheetAdapter imageUneOnMiioky_adapter;
    private CommentShareOnMiiokyVideoUneBottomSheetAdapter videoUneOnMiioky_adapter;
    private CommentShareOnMiiokyRecyclerBottomSheetAdapter recyclerOnMiioky_adapter;
    private CommentShareOnMiiokyImageDoubleBottomSheetAdapter imageDoubleOnMiioky_adapter;
    private CommentShareOnMiiokyVideoDoubleBottomSheetAdapter videoDoubleOnMiioky_adapter;
    private CommentShareOnMiiokyRespImgDoubleBottomSheetAdapter response_imgOnMiioky_adapter;
    private CommentShareOnMiiokyRespVidDoubleBottomSheetAdapter response_vidOnMiioky_adapter;

    private LinearLayoutManager layoutManager;

    private int resultsCount = 0, comments_count;
    private final Handler handler;
    private boolean isPosting = false;

    private BattleModel battleModel;

    private String photo_id;

    // single top
    public BattleModel comment_image_une_single_top, comment_recycler_single_top, comment_image_double_single_top,
            comment_reponse_image_double_single_top;
    private final String image_une_single_top, video_une_single_top, image_double_single_top, video_double_single_top,
            response_img_double_single_top, response_vid_double_single_top;

    // single on miioky
    public BattleModel comment_image_une_on_miioky, comment_recycler_on_miioky, comment_image_double_on_miioky,
            comment_reponse_image_double_on_miioky;
    private final String image_une_on_miioky, video_une_on_miioky, image_double_on_miioky, video_double_on_miioky,
            response_img_double_on_miioky, response_vid_double_on_miioky;

    private ArrayList<Comment> commentsList, pagination;
    private Date date;
    private ItemImageUneClickListener itemImageUneClickListener;
    private final RelativeLayout relLayout_view_overlay;

    // notification comment data ___________________________________________________________________
    private String the_user_who_posted_id, post_id, admin_master, group_id, category_of_post, post_type = "",
            post_id_field;
    private long time;
    private Comment commentModel;
    private String recyclerview_photo_id_upload, recyclerview_fieldLike_upload;
    // _____________________________________________________________________________________________

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public BottomSheetAddCommentShare(AppCompatActivity context,
                                      BattleModel comment_image_une_single_top, BattleModel comment_recycler_single_top,
                                      BattleModel comment_image_double_single_top, BattleModel comment_reponse_image_double_single_top,
                                      BattleModel comment_image_une_on_miioky, BattleModel comment_recycler_on_miioky,
                                      BattleModel comment_image_double_on_miioky, BattleModel comment_reponse_image_double_on_miioky,
                                      String image_une_single_top, String video_une_single_top, String image_double_single_top, String video_double_single_top,
                                      String response_img_double_single_top, String response_vid_double_single_top,
                                      String image_une_on_miioky, String video_une_on_miioky, String image_double_on_miioky, String video_double_on_miioky,
                                      String response_img_double_on_miioky, String response_vid_double_on_miioky, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.comment_image_une_single_top = comment_image_une_single_top;
        this.comment_recycler_single_top = comment_recycler_single_top;
        this.comment_image_double_single_top = comment_image_double_single_top;
        this.comment_reponse_image_double_single_top = comment_reponse_image_double_single_top;
        this.comment_image_une_on_miioky = comment_image_une_on_miioky;
        this.comment_recycler_on_miioky = comment_recycler_on_miioky;
        this.comment_image_double_on_miioky = comment_image_double_on_miioky;
        this.comment_reponse_image_double_on_miioky = comment_reponse_image_double_on_miioky;
        this.image_une_single_top = image_une_single_top;
        this.video_une_single_top = video_une_single_top;
        this.image_double_single_top = image_double_single_top;
        this.video_double_single_top = video_double_single_top;
        this.response_img_double_single_top = response_img_double_single_top;
        this.response_vid_double_single_top = response_vid_double_single_top;
        this.image_une_on_miioky = image_une_on_miioky;
        this.video_une_on_miioky = video_une_on_miioky;
        this.image_double_on_miioky = image_double_on_miioky;
        this.video_double_on_miioky = video_double_on_miioky;
        this.response_img_double_on_miioky = response_img_double_on_miioky;
        this.response_vid_double_on_miioky = response_vid_double_on_miioky;
        this.relLayout_view_overlay = relLayout_view_overlay;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        this.date = new Date();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // to expand completely layout
        requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
            coordinatorLayout.getParent().requestLayout();
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onStart()
    {
        requireNonNull(requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet_comment, null);

        new Handler().postDelayed(() -> {
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
            init(view);

            setComments();

        }, 1000);

        dialog.setContentView(view);
    }

    private void init(View v) {
        relLayout_first_comment = v.findViewById(R.id.relLayout_first_comment);
        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        number_of_comments = v.findViewById(R.id.number_of_comments);
        line_view = v.findViewById(R.id.line_view);
        mComment = v.findViewById(R.id.EditText_commentaire);
        mComment.requestFocus();
        showKeyboard();
        loading_progressBar = v.findViewById(R.id.loading_progressBar);
        ImageView checkMark = v.findViewById(R.id.posterComment);
        RelativeLayout icon_photo = v.findViewById(R.id.icon_photo);

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
                    icon_photo.setVisibility(View.GONE);
                } else {
                    icon_photo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        icon_photo.setOnClickListener(view -> {
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, CommentPublicationShare.class);
            intent.putExtra("userid", battleModel.getUser_id());
            intent.putExtra("photo_id", photo_id);
            intent.putExtra("view_comment", "view_comment");
            intent.putExtra("from_bottom_sheet", "from_bottom_sheet");
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
            if (comment_recycler_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_recycler_single_top(requireNonNull(mComment.getText()).toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_une_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_une_single_top(requireNonNull(requireNonNull(mComment.getText())).toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_double_single_top != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_double_single_top(requireNonNull(mComment.getText()).toString());

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
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewResponseCommentShare.class);
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

            recyclerSingleTop_adapter = new CommentShareSingleTopRecyclerBottomSheetAdapter(context, pagination, battleModel,
                    itemImageUneClickListener, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            recyclerView.setAdapter(recyclerSingleTop_adapter);

            if (recyclerSingleTop_adapter.getItemCount() == 0) {
                line_view.setVisibility(View.GONE);
                relLayout_first_comment.setVisibility(View.VISIBLE);
            }

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
                imageDoubleSingleTop_adapter = new CommentShareSingleTopImageDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageDoubleSingleTop_adapter);

                if (imageDoubleSingleTop_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_double_single_top != null) {
                videoDoubleSingleTop_adapter = new CommentShareSingleTopVideoDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoDoubleSingleTop_adapter);

                if (videoDoubleSingleTop_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
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
                response_imgSingleTop_adapter = new CommentShareSingleTopRespImgDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_imgSingleTop_adapter);

                if (response_imgSingleTop_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (response_vid_double_single_top != null) {
                response_vidSingleTop_adapter = new CommentShareSingleTopRespVidDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_vidSingleTop_adapter);

                if (response_vidSingleTop_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
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
                imageUneSingleTop_adapter = new CommentShareSingleTopImageUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageUneSingleTop_adapter);

                if (imageUneSingleTop_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_une_single_top != null) {
                videoUneSingleTop_adapter = new CommentShareSingleTopVideoUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoUneSingleTop_adapter);

                if (videoUneSingleTop_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
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

            recyclerOnMiioky_adapter = new CommentShareOnMiiokyRecyclerBottomSheetAdapter(context, pagination, battleModel,
                    itemImageUneClickListener, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            recyclerView.setAdapter(recyclerOnMiioky_adapter);

            if (recyclerOnMiioky_adapter.getItemCount() == 0) {
                line_view.setVisibility(View.GONE);
                relLayout_first_comment.setVisibility(View.VISIBLE);
            }

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
                imageDoubleOnMiioky_adapter = new CommentShareOnMiiokyImageDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageDoubleOnMiioky_adapter);

                if (imageDoubleOnMiioky_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_double_on_miioky != null) {
                videoDoubleOnMiioky_adapter = new CommentShareOnMiiokyVideoDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoDoubleOnMiioky_adapter);

                if (videoDoubleOnMiioky_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
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
                response_imgOnMiioky_adapter = new CommentShareOnMiiokyRespImgDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_imgOnMiioky_adapter);

                if (response_imgOnMiioky_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (response_vid_double_on_miioky != null) {
                response_vidOnMiioky_adapter = new CommentShareOnMiiokyRespVidDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_vidOnMiioky_adapter);

                if (response_vidOnMiioky_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
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
                imageUneOnMiioky_adapter = new CommentShareOnMiiokyImageUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageUneOnMiioky_adapter);

                if (imageUneOnMiioky_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_une_on_miioky != null) {
                videoUneOnMiioky_adapter = new CommentShareOnMiiokyVideoUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoUneOnMiioky_adapter);

                if (videoUneOnMiioky_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
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

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (response_vid_double_single_top != null) {
            response_vidSingleTop_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_double_single_top != null) {
            videoDoubleSingleTop_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_une_single_top != null) {
            videoUneSingleTop_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (response_vid_double_on_miioky != null) {
            response_vidOnMiioky_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_double_on_miioky != null) {
            videoDoubleOnMiioky_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_une_on_miioky != null) {
            videoUneOnMiioky_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

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
                    LinearSmoothScroller(context.getApplicationContext()) {
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

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
    public void onResume() {
        super.onResume();
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

    private void setComments() {
        comments_count = 0;
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_battle_media_share))
                            .child(photo_id)
                            .child(context.getString(R.string.field_comment_share))
                            .child(keyID)
                            .child(context.getString(R.string.field_comments));

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: data: "+data);
                                comments_count++;
                            }

                            if (comments_count >= 1) {
                                number_of_comments.setVisibility(View.VISIBLE);
                                if (comments_count == 1)
                                    number_of_comments.setText(Html.fromHtml(" <font color=red>"+ comments_count +"</font> "
                                            +context.getString(R.string.comment_single)));
                                else
                                    number_of_comments.setText(Html.fromHtml(" <font color=red>"+ comments_count +"</font> "
                                            +context.getString(R.string.comments)));
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

    /**
     * Prevent BottomSheetDialogFragment covering navigation bar
     */
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}

