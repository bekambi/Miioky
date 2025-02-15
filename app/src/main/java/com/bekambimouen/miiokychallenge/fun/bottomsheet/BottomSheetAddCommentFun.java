package com.bekambimouen.miiokychallenge.fun.bottomsheet;

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
import com.bekambimouen.miiokychallenge.comment.publication.CommentPublication;
import com.bekambimouen.miiokychallenge.comment_fun.ViewResponseFunComment;
import com.bekambimouen.miiokychallenge.comment_fun.adapter.CommentFunAdapter;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
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

public class BottomSheetAddCommentFun extends BottomSheetDialogFragment  implements OnLoadMoreItemsListener {
    private static final String TAG = "BottomSheetAddCommentFun";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView number_of_comments;
    private View line_view;
    private RelativeLayout relLayout_first_comment;
    private ProgressBar loading_progressBar;

    // vars
    private final AppCompatActivity context;
    private CommentFunAdapter fun_adapter;
    private final BattleModel_fun comment_video_fun;
    private BattleModel_fun battleModel_fun;
    private final RelativeLayout relLayout_view_overlay;
    private ArrayList<Comment> commentsList, pagination;
    private LinearLayoutManager layoutManager;
    private Date date;
    private ItemImageUneClickListener itemImageUneClickListener;
    private String photo_id;
    private boolean isPosting = false;

    private int resultsCount = 0, comments_count;
    private final Handler handler;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    // notification comment data ___________________________________________________________________
    private String the_user_who_posted_id, post_id, admin_master, group_id, category_of_post, post_type = "",
            post_id_field, recyclerview_photo_id_upload, recyclerview_fieldLike_upload;
    private long time;
    private Comment commentModel;

    public BottomSheetAddCommentFun(AppCompatActivity context, BattleModel_fun comment_video_fun, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.comment_video_fun = comment_video_fun;
        this.relLayout_view_overlay = relLayout_view_overlay;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());
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
            if (comment_video_fun != null) {
                battleModel_fun = comment_video_fun;
                photo_id = comment_video_fun.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_video_fun.getUser_id();
                post_id = comment_video_fun.getPhoto_id();
                post_id_field = context.getString(R.string.field_photo_id);
                category_of_post = "comment_video_fun";
                post_type = "";
                admin_master = "";
                group_id = "";
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";
                setupFirebaseAuth_video_fun();
            }

            date = new Date();
            init(view);
            setComments();
        }, 300);

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
            intent.putExtra("userid", battleModel_fun.getUser_id());
            intent.putExtra("photo_id", photo_id);
            intent.putExtra("fun_comment", "fun_comment");
            intent.putExtra("view_comment", "view_comment");
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
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_video_fun(mComment.getText().toString());

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
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewResponseFunComment.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(battleModel_fun);
            String myGson2 = gson.toJson(commentModel);

            intent.putExtra("comment_key", commentKey);
            intent.putExtra("comment", comment);
            intent.putExtra("userID", user_id);
            intent.putExtra("media_comment_url", url);
            intent.putExtra("media_comment_thumbnail", thumbnail);
            intent.putExtra("time", time);
            intent.putExtra("commentModel", myGson2);
            intent.putExtra("comment_video_fun", myGson);

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

    private void setupFunWidgets(BattleModel_fun battleModel_fun) {
        Collections.reverse(commentsList);

        int iterations = commentsList.size();

        if(iterations > 20){
            iterations = 20;
        }

        resultsCount = 20;
        for(int i = 0; i < iterations; i++){
            pagination.add(commentsList.get(i));
        }

        fun_adapter = new CommentFunAdapter(context, pagination, battleModel_fun,
                itemImageUneClickListener, loading_progressBar,
                post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
        try {
            recyclerView.setAdapter(fun_adapter);
        } catch (NullPointerException e) {
            Log.d(TAG, "setupFunWidgets: "+e.getMessage());
        }

        if (fun_adapter.getItemCount() == 0)
            relLayout_first_comment.setVisibility(View.VISIBLE);
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

    @SuppressLint("NotifyDataSetChanged")
    private void addNewComment_video_fun(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = new Comment();
        comment.setSuppressed("no");
        comment.setThumbnail("");
        comment.setUrl("");
        comment.setComment(newComment);
        comment.setCommentKey(commentID);
        comment.setDate_created(date.getTime());
        comment.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        fun_adapter.addComment(comment);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_videos))
                    .child(comment_video_fun.getPhoto_id())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_fun))
                    .child(comment_video_fun.getUser_id()) //should be mphoto.getUser_id()
                    .child(comment_video_fun.getPhoto_id())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(context.getApplicationContext()) {
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
                false, true,
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
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void setupFirebaseAuth_video_fun(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        clearAll();

        Query query = myRef
                .child(getString(R.string.dbname_videos))
                .orderByChild(getString(R.string.field_photo_id))
                .equalTo(comment_video_fun.getPhoto_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for ( DataSnapshot singleSnapshot :  snapshot.getChildren()){

                    BattleModel_fun video = new BattleModel_fun();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    assert objectMap != null;
                    video.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));
                    video.setCaption(requireNonNull(objectMap.get(getString(R.string.field_caption))).toString());
                    video.setTags(requireNonNull(objectMap.get(getString(R.string.field_tags))).toString());
                    video.setPhoto_id(requireNonNull(objectMap.get(getString(R.string.field_photo_id))).toString());
                    video.setUser_id(requireNonNull(objectMap.get(getString(R.string.field_user_id))).toString());
                    video.setDate_created(Long.parseLong(requireNonNull(objectMap.get(getString(R.string.field_date_created))).toString()));

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

                    video.setComments(commentsList);
                    setupFunWidgets(comment_video_fun);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setComments() {
        comments_count = 0;
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
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
                            .child(context.getString(R.string.dbname_videos))
                            .child(photo_id)
                            .child(context.getString(R.string.field_comments))
                            .child(keyID)
                            .child(context.getString(R.string.field_comments));

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+data.getKey());
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (isPosting) {
            isPosting = false;

            // to avoid crash when user post and repost photo
            if (!commentsList.isEmpty())
                commentsList.clear();
            fun_adapter.notifyDataSetChanged();

            setupFirebaseAuth_video_fun();

            recyclerView.scrollToPosition(pagination.size()-1);
        }
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

