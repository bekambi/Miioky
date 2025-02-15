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
import com.bekambimouen.miiokychallenge.comment.ViewResponseComment;
import com.bekambimouen.miiokychallenge.comment.adapter.img_and_vid_double.adapter.bottomsheet_adapter.CommentImageDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.img_and_vid_double.adapter.bottomsheet_adapter.CommentVideoDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.img_and_vid_une.adapter.bottomsheet_adapter.CommentCommentTextBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.img_and_vid_une.adapter.bottomsheet_adapter.CommentImageUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.img_and_vid_une.adapter.bottomsheet_adapter.CommentVideoUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.recycler_img.adapter.bottomsheet_adapter.CommentRecyclerBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.resp_img_and_vid.adapter.bottomsheet_adapter.CommentRespImgDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.adapter.resp_img_and_vid.adapter.bottomsheet_adapter.CommentRespVidDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.comment.publication.CommentPublication;
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
import java.util.Objects;

public class BottomSheetAddComment extends BottomSheetDialogFragment implements OnLoadMoreItemsListener {
    private static final String TAG = "BottomSheetAddComment";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView number_of_comments;
    private View line_view;
    private RelativeLayout relLayout_first_comment;
    private ProgressBar loading_progressBar;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_view_overlay;
    private CommentCommentTextBottomSheetAdapter commentTextAdapter;
    private CommentImageUneBottomSheetAdapter imageUne_adapter;
    private CommentVideoUneBottomSheetAdapter videoUne_adapter;
    private CommentRecyclerBottomSheetAdapter recycler_adapter;
    private CommentImageDoubleBottomSheetAdapter imageDouble_adapter;
    private CommentVideoDoubleBottomSheetAdapter videoDouble_adapter;
    private CommentRespImgDoubleBottomSheetAdapter response_img_adapter;
    private CommentRespVidDoubleBottomSheetAdapter response_vid_adapter;
    private LinearLayoutManager layoutManager;
    private String photo_id;

    private int resultsCount = 0, comments_count;
    private final Handler handler;
    private boolean isPosting = false;

    public BattleModel comment_image_une, comment_recycler, comment_image_double,
            comment_reponse_image_double, battleModel;
    private final String comment_text;
    private final String image_une;
    private final String video_une;
    private final String image_double;
    private final String video_double;
    private final String response_img_double;
    private final String response_vid_double;
    private ArrayList<Comment> commentsList, pagination;
    private Date date;
    private ItemImageUneClickListener itemImageUneClickListener;

    // notification comment data ___________________________________________________________________
    private String the_user_who_posted_id, post_id, admin_master, group_id, category_of_post, post_type = "",
            post_id_field, recyclerview_photo_id_upload, recyclerview_fieldLike_upload;
    private long time;
    private Comment commentModel;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public BottomSheetAddComment(AppCompatActivity context, BattleModel comment_image_une, BattleModel comment_recycler,
                                 BattleModel comment_image_double, BattleModel comment_reponse_image_double, String comment_text,
                                 String image_une, String video_une, String image_double, String video_double,
                                 String response_img_double, String response_vid_double, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.comment_image_une = comment_image_une;
        this.comment_recycler = comment_recycler;
        this.comment_image_double = comment_image_double;
        this.comment_reponse_image_double = comment_reponse_image_double;
        this.comment_text = comment_text;
        this.image_une = image_une;
        this.video_une = video_une;
        this.image_double = image_double;
        this.video_double = video_double;
        this.response_img_double = response_img_double;
        this.response_vid_double = response_vid_double;
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
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
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
            if (comment_recycler != null) {
                battleModel = comment_recycler;
                photo_id = comment_recycler.getPhotoi_id();
                // comment notification
                the_user_who_posted_id = comment_recycler.getUser_id();
                post_id = comment_recycler.getPhotoi_id();
                post_id_field = context.getString(R.string.field_photoi_id);
                category_of_post = "comment_recycler";
                post_type = "";
                admin_master = "";
                group_id = "";
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_image_une != null) {
                battleModel = comment_image_une;
                photo_id = comment_image_une.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_image_une.getUser_id();
                post_id = comment_image_une.getPhoto_id();
                post_id_field = context.getString(R.string.field_photo_id);
                category_of_post = "comment_image_une";
                if (image_une != null) {
                    post_type = "image_une";
                } else if (video_une != null)
                    post_type = "video_une";
                else
                    post_type = "comment_text";
                admin_master = "";
                group_id = "";
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_image_double != null){
                battleModel = comment_image_double;
                photo_id = comment_image_double.getPhoto_id_un();
                // comment notification
                the_user_who_posted_id = comment_image_double.getUser_id();
                post_id = comment_image_double.getPhoto_id_un();
                post_id_field = context.getString(R.string.field_photo_id_un);
                category_of_post = "comment_image_double";
                if (image_double != null)
                    post_type = "image_double";
                else
                    post_type = "video_double";
                admin_master = "";
                group_id = "";
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_reponse_image_double != null) {
                battleModel = comment_reponse_image_double;
                photo_id = comment_reponse_image_double.getReponse_photoID();
                // comment notification
                the_user_who_posted_id = comment_reponse_image_double.getUser_id();
                post_id = comment_reponse_image_double.getReponse_photoID();
                post_id_field = context.getString(R.string.field_reponse_photoID);
                category_of_post = "comment_reponse_image_double";
                if (response_img_double != null)
                    post_type = "response_img_double";
                else
                    post_type = "response_vid_double";
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
        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        number_of_comments = v.findViewById(R.id.number_of_comments);
        line_view = v.findViewById(R.id.line_view);
        mComment = v.findViewById(R.id.EditText_commentaire);
        mComment.requestFocus();
        showKeyboard();
        relLayout_first_comment = v.findViewById(R.id.relLayout_first_comment);
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
            Intent intent= new Intent(context, CommentPublication.class);
            intent.putExtra("userid", battleModel.getUser_id());
            intent.putExtra("photo_id", photo_id);
            intent.putExtra("view_comment", "view_comment");
            intent.putExtra("from_bottom_sheet", "from_bottom_sheet");
            // send notification
            intent.putExtra("the_user_who_posted_id", the_user_who_posted_id);
            intent.putExtra("admin_master", "");
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
            if (comment_recycler != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_recycler(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_une != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_une(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }

            } else if (comment_image_double != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_image_double(mComment.getText().toString());

                    mComment.getText().clear();
                    closeKeyboard();

                } else{
                    Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }
            } else if (comment_reponse_image_double != null) {
                if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment_response_image_double(mComment.getText().toString());

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
            Intent intent = new Intent(context, ViewResponseComment.class);
            Gson gson = new Gson();
            String myGson = null;
            if (comment_image_une != null)
                myGson = gson.toJson(battleModel);
            else if (comment_recycler != null)
                myGson = gson.toJson(battleModel);
            else if (comment_image_double != null)
                myGson = gson.toJson(battleModel);
            else if (comment_reponse_image_double != null)
                myGson = gson.toJson(battleModel);

            String myGson2 = gson.toJson(commentModel);

            intent.putExtra("comment_key", commentKey);
            intent.putExtra("comment", comment);
            intent.putExtra("userID", user_id);
            intent.putExtra("media_comment_url", url);
            intent.putExtra("media_comment_thumbnail", thumbnail);
            intent.putExtra("time", time);
            intent.putExtra("commentModel", myGson2);

            if (comment_image_une != null)
                intent.putExtra("comment_image_une", myGson);
            else if (comment_recycler != null)
                intent.putExtra("comment_recycler", myGson);
            else if (comment_image_double != null)
                intent.putExtra("comment_image_double", myGson);
            else if (comment_reponse_image_double != null)
                intent.putExtra("comment_reponse_image_double", myGson);

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

    private void setupWidgets(BattleModel battleModel){
        if (comment_recycler != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            recycler_adapter = new CommentRecyclerBottomSheetAdapter(context, pagination, battleModel,
                    itemImageUneClickListener, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            recyclerView.setAdapter(recycler_adapter);

            if (recycler_adapter.getItemCount() == 0) {
                line_view.setVisibility(View.GONE);
                relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_image_double != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (image_double != null) {
                imageDouble_adapter = new CommentImageDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageDouble_adapter);

                if (imageDouble_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_double != null) {
                videoDouble_adapter = new CommentVideoDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoDouble_adapter);

                if (videoDouble_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
            }

        } else if (comment_reponse_image_double != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (response_img_double != null) {
                response_img_adapter = new CommentRespImgDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_img_adapter);

                if (response_img_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (response_vid_double != null) {
                response_vid_adapter = new CommentRespVidDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_vid_adapter);

                if (response_vid_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            }

        } else if (comment_image_une != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }
            if (comment_text != null) {
                commentTextAdapter = new CommentCommentTextBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(commentTextAdapter);

                if (commentTextAdapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (image_une != null) {
                imageUne_adapter = new CommentImageUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageUne_adapter);

                if (imageUne_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_une != null) {
                videoUne_adapter = new CommentVideoUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoUne_adapter);

                if (videoUne_adapter.getItemCount() == 0) {
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

    private void addNewComment_response_image_double(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (response_img_double != null) {
            response_img_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (response_vid_double != null) {
            response_vid_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getReponse_user_id())
                    .child(battleModel.getReponse_photoID())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_battle_media))
                    .child(battleModel.getReponse_photoID())
                    .child(getString(R.string.field_comments))
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

    private void addNewComment_image_double(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_double != null) {
            imageDouble_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_double != null) {
            videoDouble_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhoto_id_un())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id()) //should be mphoto.getUser_id()
                    .child(battleModel.getPhoto_id_un())
                    .child(getString(R.string.field_comments))
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

    private void addNewComment_image_une(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (comment_text != null) {
            commentTextAdapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (image_une != null) {
            imageUne_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_une != null) {
            videoUne_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id()) //should be mphoto.getUser_id()
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comments))
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

    private void addNewComment_recycler(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        recycler_adapter.addComment(comment);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .child(battleModel.getPhotoi_id())
                    .child(getString(R.string.field_comments))
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

        if (commentTextAdapter != null)
            commentTextAdapter = null;

        if (imageUne_adapter != null)
            imageUne_adapter = null;

        if (videoUne_adapter != null)
            videoUne_adapter = null;

        if (imageDouble_adapter != null)
            imageDouble_adapter = null;

        if (videoDouble_adapter != null)
            videoDouble_adapter = null;

        if (response_img_adapter != null)
            response_img_adapter = null;

        if (response_vid_adapter != null)
            response_vid_adapter = null;

        if (recycler_adapter != null)
            recycler_adapter = null;

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
        if (comment_image_une != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_image_double != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media))
                    .orderByChild(context.getString(R.string.field_photo_id_un))
                    .equalTo(battleModel.getPhoto_id_un());

        } else if (comment_recycler != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media))
                    .orderByChild(context.getString(R.string.field_photoi_id))
                    .equalTo(battleModel.getPhotoi_id());

        } else if (comment_reponse_image_double != null) {
            query = myRef
                    .child(getString(R.string.dbname_battle_media))
                    .orderByChild(context.getString(R.string.field_reponse_photoID))
                    .equalTo(battleModel.getReponse_photoID());

        }

        assert query != null;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot singleSnapshot :  snapshot.getChildren()){

                    BattleModel photo = new BattleModel();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    assert objectMap != null;
                    if (comment_image_une != null) {
                        photo.setCaption(requireNonNull(objectMap.get(getString(R.string.field_caption))).toString());
                        photo.setTags(requireNonNull(objectMap.get(getString(R.string.field_tags))).toString());
                        photo.setPhoto_id(requireNonNull(objectMap.get(getString(R.string.field_photo_id))).toString());
                    }
                    if (comment_recycler != null) {
                        photo.setCaption(requireNonNull(objectMap.get(getString(R.string.field_caption))).toString());
                        photo.setTags(requireNonNull(objectMap.get(getString(R.string.field_tags))).toString());
                        photo.setPhotoi_id(requireNonNull(objectMap.get(getString(R.string.field_photoi_id))).toString());
                    }
                    if (comment_image_double != null) {
                        photo.setCaption(requireNonNull(objectMap.get(getString(R.string.field_captionUn))).toString());
                        photo.setTags(requireNonNull(objectMap.get(getString(R.string.field_tagsUn))).toString());
                        photo.setPhoto_id(requireNonNull(objectMap.get(getString(R.string.field_photo_id_un))).toString());
                    }
                    if (comment_reponse_image_double != null) {
                        photo.setReponse_caption(requireNonNull(objectMap.get(getString(R.string.field_reponse_caption))).toString());
                        photo.setReponse_tag(requireNonNull(objectMap.get(getString(R.string.field_reponse_tag))).toString());
                        photo.setReponse_photoID(requireNonNull(objectMap.get(getString(R.string.field_reponse_photoID))).toString());

                        photo.setInvite_caption(requireNonNull(objectMap.get(getString(R.string.field_invite_caption))).toString());
                        photo.setInvite_tag(requireNonNull(objectMap.get(getString(R.string.field_invite_tag))).toString());
                        photo.setInvite_photoID(requireNonNull(objectMap.get(getString(R.string.field_invite_photoID))).toString());
                    }
                    photo.setUser_id(requireNonNull(objectMap.get(getString(R.string.field_user_id))).toString());
                    photo.setDate_created(Long.parseLong(requireNonNull(objectMap.get(getString(R.string.field_date_created))).toString()));

                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()){
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
                if (comment_image_une != null) {
                    if (comment_text != null)
                        commentTextAdapter.notifyDataSetChanged();
                    if (image_une != null)
                        imageUne_adapter.notifyDataSetChanged();
                    if (video_une != null)
                        videoUne_adapter.notifyDataSetChanged();

                } else if (comment_image_double != null) {
                    if (image_double != null)
                        imageDouble_adapter.notifyDataSetChanged();
                    else if (video_double != null)
                        videoDouble_adapter.notifyDataSetChanged();

                } else if (comment_reponse_image_double != null) {
                    if (response_img_double != null)
                        response_img_adapter.notifyDataSetChanged();
                    else if (response_vid_double != null)
                        response_vid_adapter.notifyDataSetChanged();

                } else if (comment_recycler != null) {
                    recycler_adapter.notifyDataSetChanged();

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
                .child(context.getString(R.string.dbname_battle_media))
                .child(photo_id)
                .child(context.getString(R.string.field_comments));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_battle_media))
                            .child(photo_id)
                            .child(context.getString(R.string.field_comments))
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

