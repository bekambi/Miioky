package com.bekambimouen.miiokychallenge.groups.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.comment_share.GroupResponseCommentShare;
import com.bekambimouen.miiokychallenge.groups.comment_share.publication.GroupCommentSharePublication;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.img_and_vid_double.bottomsheet_adapter.GroupCommentShareSingleBottomImageDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.img_and_vid_double.bottomsheet_adapter.GroupCommentShareSingleBottomVideoDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.img_and_vid_une.bottomsheet_adapter.GroupCommentShareSingleBottomImageUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.img_and_vid_une.bottomsheet_adapter.GroupCommentShareSingleBottomVideoUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.recycler_img.bottomsheet_adapter.GroupCommentShareSingleBottomRecyclerBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.resp_img_and_vid.bottomsheet_adapter.GroupCommentShareSingleBottomRespImgDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.single_bottom.resp_img_and_vid.bottomsheet_adapter.GroupCommentShareSingleBottomRespVidDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.circle_image.bottomsheet_adapter.GroupCommentShareTopBottomCircleImageBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.img_and_vid_double.bottomsheet_adapter.GroupCommentShareTopBottomImageDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.img_and_vid_double.bottomsheet_adapter.GroupCommentShareTopBottomVideoDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.img_and_vid_une.bottomsheet_adapter.GroupCommentShareTopBottomImageUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.img_and_vid_une.bottomsheet_adapter.GroupCommentShareTopBottomVideoUneBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.recycler_img.bottomsheet_adapter.GroupCommentShareTopBottomRecyclerBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.resp_img_and_vid.bottomsheet_adapter.GroupCommentShareTopBottomRespImgDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.resp_img_and_vid.bottomsheet_adapter.GroupCommentShareTopBottomRespVidDoubleBottomSheetAdapter;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Comment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupBottomSheetAddCommentShare extends BottomSheetDialogFragment implements OnLoadMoreItemsListener {
    private static final String TAG = "GroupBottomSheetAddCommentShare";

    // widgets
    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView number_of_comments;
    private View line_view;
    private RelativeLayout relLayout_first_comment;
    private ProgressBar loading_progressBar;

    // vars
    private final AppCompatActivity context;

    // single bottom
    private GroupCommentShareSingleBottomImageUneBottomSheetAdapter imageUneSingleBottom_adapter;
    private GroupCommentShareSingleBottomVideoUneBottomSheetAdapter videoUneSingleBottom_adapter;
    private GroupCommentShareSingleBottomRecyclerBottomSheetAdapter recyclerSingleBottom_adapter;
    private GroupCommentShareSingleBottomImageDoubleBottomSheetAdapter imageDoubleSingleBottom_adapter;
    private GroupCommentShareSingleBottomVideoDoubleBottomSheetAdapter videoDoubleSingleBottom_adapter;
    private GroupCommentShareSingleBottomRespImgDoubleBottomSheetAdapter response_imgSingleBottom_adapter;
    private GroupCommentShareSingleBottomRespVidDoubleBottomSheetAdapter response_vidSingleBottom_adapter;

    // top bottom
    private GroupCommentShareTopBottomCircleImageBottomSheetAdapter circleImageTopBottom_adapter;
    private GroupCommentShareTopBottomImageUneBottomSheetAdapter imageUneTopBottom_adapter;
    private GroupCommentShareTopBottomVideoUneBottomSheetAdapter videoUneTopBottom_adapter;
    private GroupCommentShareTopBottomRecyclerBottomSheetAdapter recyclerTopBottom_adapter;
    private GroupCommentShareTopBottomImageDoubleBottomSheetAdapter imageDoubleTopBottom_adapter;
    private GroupCommentShareTopBottomVideoDoubleBottomSheetAdapter videoDoubleTopBottom_adapter;
    private GroupCommentShareTopBottomRespImgDoubleBottomSheetAdapter response_imgTopBottom_adapter;
    private GroupCommentShareTopBottomRespVidDoubleBottomSheetAdapter response_vidTopBottom_adapter;

    private LinearLayoutManager layoutManager;

    private int resultsCount = 0, comments_count;
    private final Handler handler;
    private boolean isPosting = false;

    private BattleModel battleModel;

    private String photo_id;

    // single bottom
    public BattleModel comment_image_une_single_bottom, comment_recycler_single_bottom, comment_image_double_single_bottom,
            comment_reponse_image_double_single_bottom;
    private final String image_une_single_bottom, video_une_single_bottom, image_double_single_bottom, video_double_single_bottom,
            response_img_double_single_bottom, response_vid_double_single_bottom;
    // top bottom
    public BattleModel comment_image_une_top_bottom, comment_recycler_top_bottom, comment_image_double_top_bottom,
            comment_reponse_image_double_top_bottom;
    private final String circle_image_top_bottom, image_une_top_bottom, video_une_top_bottom, image_double_top_bottom, video_double_top_bottom,
            response_img_double_top_bottom, response_vid_double_top_bottom;
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

    public GroupBottomSheetAddCommentShare(AppCompatActivity context,
                                           BattleModel comment_image_une_single_bottom, BattleModel comment_recycler_single_bottom,
                                           BattleModel comment_image_double_single_bottom, BattleModel comment_reponse_image_double_single_bottom,
                                           BattleModel comment_image_une_top_bottom, BattleModel comment_recycler_top_bottom,
                                           BattleModel comment_image_double_top_bottom, BattleModel comment_reponse_image_double_top_bottom,
                                           String image_une_single_bottom, String video_une_single_bottom, String image_double_single_bottom,
                                           String video_double_single_bottom, String response_img_double_single_bottom, String response_vid_double_single_bottom,
                                           String circle_image_top_bottom, String image_une_top_bottom, String video_une_top_bottom,
                                           String image_double_top_bottom, String video_double_top_bottom, String response_img_double_top_bottom,
                                           String response_vid_double_top_bottom, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.comment_image_une_single_bottom = comment_image_une_single_bottom;
        this.comment_recycler_single_bottom = comment_recycler_single_bottom;
        this.comment_image_double_single_bottom = comment_image_double_single_bottom;
        this.comment_reponse_image_double_single_bottom = comment_reponse_image_double_single_bottom;
        this.comment_image_une_top_bottom = comment_image_une_top_bottom;
        this.comment_recycler_top_bottom = comment_recycler_top_bottom;
        this.comment_image_double_top_bottom = comment_image_double_top_bottom;
        this.comment_reponse_image_double_top_bottom = comment_reponse_image_double_top_bottom;
        this.image_une_single_bottom = image_une_single_bottom;
        this.video_une_single_bottom = video_une_single_bottom;
        this.image_double_single_bottom = image_double_single_bottom;
        this.video_double_single_bottom = video_double_single_bottom;
        this.response_img_double_single_bottom = response_img_double_single_bottom;
        this.response_vid_double_single_bottom = response_vid_double_single_bottom;
        this.circle_image_top_bottom = circle_image_top_bottom;
        this.image_une_top_bottom = image_une_top_bottom;
        this.video_une_top_bottom = video_une_top_bottom;
        this.image_double_top_bottom = image_double_top_bottom;
        this.video_double_top_bottom = video_double_top_bottom;
        this.response_img_double_top_bottom = response_img_double_top_bottom;
        this.response_vid_double_top_bottom = response_vid_double_top_bottom;
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
            if (comment_recycler_single_bottom != null) {
                battleModel = comment_recycler_single_bottom;
                photo_id = comment_recycler_single_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_recycler_single_bottom.getUser_id();
                post_id = comment_recycler_single_bottom.getPhotoi_id();
                post_id_field = context.getString(R.string.field_photoi_id);
                category_of_post = "comment_recycler_single_bottom";
                post_type = "";
                admin_master = comment_recycler_single_bottom.getAdmin_master();
                group_id = comment_recycler_single_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_image_une_single_bottom != null) {
                battleModel = comment_image_une_single_bottom;
                photo_id = comment_image_une_single_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_image_une_single_bottom.getUser_id();
                post_id = comment_image_une_single_bottom.getPhoto_id();
                post_id_field = context.getString(R.string.field_photo_id);
                category_of_post = "comment_image_une_single_bottom";
                if (image_une_single_bottom != null)
                    post_type = "image_une_single_bottom";
                else
                    post_type = "video_une_single_bottom";
                admin_master = comment_image_une_single_bottom.getAdmin_master();
                group_id = comment_image_une_single_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_image_double_single_bottom != null){
                battleModel = comment_image_double_single_bottom;
                photo_id = comment_image_double_single_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_image_double_single_bottom.getUser_id();
                post_id = comment_image_double_single_bottom.getPhoto_id_un();
                post_id_field = context.getString(R.string.field_photo_id_un);
                category_of_post = "comment_image_double_single_bottom";
                if (image_double_single_bottom != null)
                    post_type = "image_double_single_bottom";
                else
                    post_type = "video_double_single_bottom";
                admin_master = comment_image_double_single_bottom.getAdmin_master();
                group_id = comment_image_double_single_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_reponse_image_double_single_bottom != null) {
                battleModel = comment_reponse_image_double_single_bottom;
                photo_id = comment_reponse_image_double_single_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_reponse_image_double_single_bottom.getUser_id();
                post_id = comment_reponse_image_double_single_bottom.getReponse_photoID();
                post_id_field = context.getString(R.string.field_reponse_photoID);
                category_of_post = "comment_reponse_image_double_single_bottom";
                if (response_img_double_single_bottom != null)
                    post_type = "response_img_double_single_bottom";
                else
                    post_type = "response_vid_double_single_bottom";
                admin_master = comment_reponse_image_double_single_bottom.getAdmin_master();
                group_id = comment_reponse_image_double_single_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_recycler_top_bottom != null) {
                battleModel = comment_recycler_top_bottom;
                photo_id = comment_recycler_top_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_recycler_top_bottom.getUser_id();
                post_id = comment_recycler_top_bottom.getPhotoi_id();
                post_id_field = context.getString(R.string.field_photoi_id);
                category_of_post = "comment_recycler_top_bottom";
                post_type = "";
                admin_master = comment_recycler_top_bottom.getAdmin_master();
                group_id = comment_recycler_top_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_image_une_top_bottom != null) {
                battleModel = comment_image_une_top_bottom;
                photo_id = comment_image_une_top_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_image_une_top_bottom.getUser_id();
                post_id = comment_image_une_top_bottom.getPhoto_id();
                post_id_field = context.getString(R.string.field_photo_id);
                category_of_post = "comment_image_une_top_bottom";
                if (image_une_top_bottom != null)
                    post_type = "image_une_top_bottom";
                else if (circle_image_top_bottom != null)
                    post_type = "circle_image_top_bottom";
                else
                    post_type = "video_une_top_bottom";
                admin_master = comment_image_une_top_bottom.getAdmin_master();
                group_id = comment_image_une_top_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_image_double_top_bottom != null){
                battleModel = comment_image_double_top_bottom;
                photo_id = comment_image_double_top_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_image_double_top_bottom.getUser_id();
                post_id = comment_image_double_top_bottom.getPhoto_id_un();
                post_id_field = context.getString(R.string.field_photo_id_un);
                category_of_post = "comment_image_double_top_bottom";
                if (image_double_top_bottom != null)
                    post_type = "image_double_top_bottom";
                else
                    post_type = "video_double_top_bottom";
                admin_master = comment_image_double_top_bottom.getAdmin_master();
                group_id = comment_image_double_top_bottom.getGroup_id();
                recyclerview_photo_id_upload = "";
                recyclerview_fieldLike_upload = "";

            } else if (comment_reponse_image_double_top_bottom != null) {
                battleModel = comment_reponse_image_double_top_bottom;
                photo_id = comment_reponse_image_double_top_bottom.getPhoto_id();
                // comment notification
                the_user_who_posted_id = comment_reponse_image_double_top_bottom.getUser_id();
                post_id = comment_reponse_image_double_top_bottom.getReponse_photoID();
                post_id_field = context.getString(R.string.field_reponse_photoID);
                category_of_post = "comment_reponse_image_double_top_bottom";
                if (response_img_double_top_bottom != null)
                    post_type = "response_img_double_top_bottom";
                else
                    post_type = "response_vid_double_top_bottom";
                admin_master = comment_reponse_image_double_top_bottom.getAdmin_master();
                group_id = comment_reponse_image_double_top_bottom.getGroup_id();
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
        // widgets
        ImageView posterComment = v.findViewById(R.id.posterComment);
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
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, GroupCommentSharePublication.class);
            intent.putExtra("group_id", battleModel.getGroup_id());
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

        posterComment.setOnClickListener(view -> {
            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

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
            // for ccomment notification
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
            Intent intent = new Intent(context, GroupResponseCommentShare.class);

            intent.putExtra("comment_key", commentKey);
            intent.putExtra("comment", comment);
            intent.putExtra("userID", user_id);
            intent.putExtra("media_comment_url", url);
            intent.putExtra("media_comment_thumbnail", thumbnail);
            intent.putExtra("time", time);
            intent.putExtra("commentModel", myGson2);

            if (comment_image_une_single_bottom != null || comment_image_une_top_bottom != null)
                intent.putExtra("comment_image_une", myGson);
            else if (comment_recycler_single_bottom != null || comment_recycler_top_bottom != null)
                intent.putExtra("comment_recycler", myGson);
            else if (comment_image_double_single_bottom != null || comment_image_double_top_bottom != null)
                intent.putExtra("comment_image_double", myGson);
            else if (comment_reponse_image_double_single_bottom != null || comment_reponse_image_double_top_bottom != null)
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

    // add comment to database
    private void downloadComment() {
        if (comment_recycler_single_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_recycler_single_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }

        } else if (comment_image_une_single_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_image_une_single_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }

        } else if (comment_image_double_single_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_image_double_single_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        } else if (comment_reponse_image_double_single_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_response_image_double_single_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }

        } else
        if (comment_recycler_top_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_recycler_top_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }

        } else if (comment_image_une_top_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_image_une_top_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }

        } else if (comment_image_double_top_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_image_double_top_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        } else if (comment_reponse_image_double_top_bottom != null) {
            if(!requireNonNull(mComment.getText()).toString().isEmpty()){
                Log.d(TAG, "onClick: attempting to submit new comment.");
                addNewComment_response_image_double_top_bottom(mComment.getText().toString());

                mComment.getText().clear();
                closeKeyboard();

            } else{
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }

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
                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_have_reached_the_post_quota)
                        +" "+follower.getNumber_posts_per_day()+" "+context.getString(R.string.publications)
                        +" "+context.getString(R.string.per_day)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());
                positiveButton.setOnClickListener(view3 -> d.dismiss());
            }
        }
    }

    private void setupWidgets(BattleModel battleModel){
        if (comment_recycler_single_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            recyclerSingleBottom_adapter = new GroupCommentShareSingleBottomRecyclerBottomSheetAdapter(context, pagination, battleModel,
                    itemImageUneClickListener, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            recyclerView.setAdapter(recyclerSingleBottom_adapter);

            if (recyclerSingleBottom_adapter.getItemCount() == 0) {
                line_view.setVisibility(View.GONE);
                relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_image_double_single_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (image_double_single_bottom != null) {
                imageDoubleSingleBottom_adapter = new GroupCommentShareSingleBottomImageDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageDoubleSingleBottom_adapter);

                if (imageDoubleSingleBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_double_single_bottom != null) {
                videoDoubleSingleBottom_adapter = new GroupCommentShareSingleBottomVideoDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoDoubleSingleBottom_adapter);

                if (videoDoubleSingleBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
            }

        } else if (comment_reponse_image_double_single_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (response_img_double_single_bottom != null) {
                response_imgSingleBottom_adapter = new GroupCommentShareSingleBottomRespImgDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_imgSingleBottom_adapter);

                if (response_imgSingleBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (response_vid_double_single_bottom != null) {
                response_vidSingleBottom_adapter = new GroupCommentShareSingleBottomRespVidDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_vidSingleBottom_adapter);

                if (response_vidSingleBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            }

        } else if (comment_image_une_single_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }
            if (image_une_single_bottom != null) {
                imageUneSingleBottom_adapter = new GroupCommentShareSingleBottomImageUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageUneSingleBottom_adapter);

                if (imageUneSingleBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_une_single_bottom != null) {
                videoUneSingleBottom_adapter = new GroupCommentShareSingleBottomVideoUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoUneSingleBottom_adapter);

                if (videoUneSingleBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
            }
        } else
        if (comment_recycler_top_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            recyclerTopBottom_adapter = new GroupCommentShareTopBottomRecyclerBottomSheetAdapter(context, pagination, battleModel,
                    itemImageUneClickListener, loading_progressBar,
                    post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                    recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
            recyclerView.setAdapter(recyclerTopBottom_adapter);

            if (recyclerTopBottom_adapter.getItemCount() == 0) {
                line_view.setVisibility(View.GONE);
                relLayout_first_comment.setVisibility(View.VISIBLE);
            }

        } else if (comment_image_double_top_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (image_double_top_bottom != null) {
                imageDoubleTopBottom_adapter = new GroupCommentShareTopBottomImageDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageDoubleTopBottom_adapter);

                if (imageDoubleTopBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_double_top_bottom != null) {
                videoDoubleTopBottom_adapter = new GroupCommentShareTopBottomVideoDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoDoubleTopBottom_adapter);

                if (videoDoubleTopBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
            }

        } else if (comment_reponse_image_double_top_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }

            if (response_img_double_top_bottom != null) {
                response_imgTopBottom_adapter = new GroupCommentShareTopBottomRespImgDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_imgTopBottom_adapter);

                if (response_imgTopBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (response_vid_double_top_bottom != null) {
                response_vidTopBottom_adapter = new GroupCommentShareTopBottomRespVidDoubleBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(response_vidTopBottom_adapter);

                if (response_vidTopBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }
            }

        } else if (comment_image_une_top_bottom != null) {
            Collections.reverse(commentsList);

            int iterations = commentsList.size();

            if(iterations > 20){
                iterations = 20;
            }

            resultsCount = 20;
            for(int i = 0; i < iterations; i++){
                pagination.add(commentsList.get(i));
            }
            if (circle_image_top_bottom != null) {
                circleImageTopBottom_adapter = new GroupCommentShareTopBottomCircleImageBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(circleImageTopBottom_adapter);

                if (circleImageTopBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (image_une_top_bottom != null) {
                imageUneTopBottom_adapter = new GroupCommentShareTopBottomImageUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(imageUneTopBottom_adapter);

                if (imageUneTopBottom_adapter.getItemCount() == 0) {
                    line_view.setVisibility(View.GONE);
                    relLayout_first_comment.setVisibility(View.VISIBLE);
                }

            } else if (video_une_top_bottom != null) {
                videoUneTopBottom_adapter = new GroupCommentShareTopBottomVideoUneBottomSheetAdapter(context, pagination, battleModel,
                        itemImageUneClickListener, loading_progressBar,
                        post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                        recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, this, relLayout_view_overlay);
                recyclerView.setAdapter(videoUneTopBottom_adapter);

                if (videoUneTopBottom_adapter.getItemCount() == 0) {
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

    // single bottom ____________________________
    private void addNewComment_response_image_double_single_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (response_img_double_single_bottom != null) {
            response_imgSingleBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (response_vid_double_single_bottom != null) {
            response_vidSingleBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            assert commentID != null;

            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    private void addNewComment_image_double_single_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_double_single_bottom != null) {
            imageDoubleSingleBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_double_single_bottom != null) {
            videoDoubleSingleBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    private void addNewComment_image_une_single_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_une_single_bottom != null) {
            imageUneSingleBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_une_single_bottom != null) {
            videoUneSingleBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    private void addNewComment_recycler_single_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        recyclerSingleBottom_adapter.addComment(comment);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    // top bottom _______________________________________
    private void addNewComment_response_image_double_top_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (response_img_double_top_bottom != null) {
            response_imgTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (response_vid_double_top_bottom != null) {
            response_vidTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            assert commentID != null;

            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    private void addNewComment_image_double_top_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (image_double_top_bottom != null) {
            imageDoubleTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_double_top_bottom != null) {
            videoDoubleTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    private void addNewComment_image_une_top_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        if (circle_image_top_bottom != null) {
            circleImageTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (image_une_top_bottom != null) {
            imageUneTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);

        } else if (video_une_top_bottom != null) {
            videoUneTopBottom_adapter.addComment(comment);

            if (relLayout_first_comment.getVisibility() == View.VISIBLE)
                relLayout_first_comment.setVisibility(View.GONE);
        }

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    private void addNewComment_recycler_top_bottom(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        recyclerTopBottom_adapter.addComment(comment);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(getString(R.string.field_comment_share))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(battleModel.getGroup_id())
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

    // add post point and send notification to owner post
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

    /**
     * Setup the firebase auth object
     */
    private void clearAll() {
        if (commentsList != null)
            commentsList.clear();
        if (pagination != null)
            pagination.clear();

        if (imageUneSingleBottom_adapter != null)
            imageUneSingleBottom_adapter = null;

        if (videoUneSingleBottom_adapter != null)
            videoUneSingleBottom_adapter = null;

        if (imageDoubleSingleBottom_adapter != null)
            imageDoubleSingleBottom_adapter = null;

        if (videoDoubleSingleBottom_adapter != null)
            videoDoubleSingleBottom_adapter = null;

        if (response_imgSingleBottom_adapter != null)
            response_imgSingleBottom_adapter = null;

        if (response_vidSingleBottom_adapter != null)
            response_vidSingleBottom_adapter = null;

        if (recyclerSingleBottom_adapter != null)
            recyclerSingleBottom_adapter = null;

        if (circle_image_top_bottom != null)
            circleImageTopBottom_adapter = null;

        if (imageUneTopBottom_adapter != null)
            imageUneTopBottom_adapter = null;

        if (videoUneTopBottom_adapter != null)
            videoUneTopBottom_adapter = null;

        if (imageDoubleTopBottom_adapter != null)
            imageDoubleTopBottom_adapter = null;

        if (videoDoubleTopBottom_adapter != null)
            videoDoubleTopBottom_adapter = null;

        if (response_imgTopBottom_adapter != null)
            response_imgTopBottom_adapter = null;

        if (response_vidTopBottom_adapter != null)
            response_vidTopBottom_adapter = null;

        if (recyclerTopBottom_adapter != null)
            recyclerTopBottom_adapter = null;

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
        if (comment_image_une_single_bottom != null || comment_image_une_top_bottom != null) {
            query = myRef
                    .child(getString(R.string.dbname_group_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_image_double_single_bottom != null || comment_image_double_top_bottom != null) {
            query = myRef
                    .child(getString(R.string.dbname_group_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_recycler_single_bottom != null || comment_recycler_top_bottom != null) {
            query = myRef
                    .child(getString(R.string.dbname_group_media_share))
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());

        } else if (comment_reponse_image_double_single_bottom != null || comment_reponse_image_double_top_bottom != null) {
            query = myRef
                    .child(context.getString(R.string.dbname_group_media_share))
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
                    if (comment_image_une_single_bottom != null || comment_image_une_top_bottom != null) {
                        photo.setCaption(requireNonNull(objectMap.get(context.getString(R.string.field_caption))).toString());
                        photo.setTags(requireNonNull(objectMap.get(context.getString(R.string.field_tags))).toString());
                        photo.setPhoto_id(requireNonNull(objectMap.get(context.getString(R.string.field_photo_id))).toString());
                    }
                    if (comment_recycler_single_bottom != null || comment_recycler_top_bottom != null) {
                        photo.setCaption(requireNonNull(objectMap.get(context.getString(R.string.field_caption))).toString());
                        photo.setTags(requireNonNull(objectMap.get(context.getString(R.string.field_tags))).toString());
                        photo.setPhotoi_id(requireNonNull(objectMap.get(context.getString(R.string.field_photoi_id))).toString());
                    }
                    if (comment_image_double_single_bottom != null || comment_image_double_top_bottom != null) {
                        photo.setCaption(requireNonNull(objectMap.get(context.getString(R.string.field_captionUn))).toString());
                        photo.setTags(requireNonNull(objectMap.get(context.getString(R.string.field_tagsUn))).toString());
                        photo.setPhoto_id(requireNonNull(objectMap.get(context.getString(R.string.field_photo_id_un))).toString());
                    }
                    if (comment_reponse_image_double_single_bottom != null || comment_reponse_image_double_top_bottom != null) {
                        photo.setReponse_caption(requireNonNull(objectMap.get(context.getString(R.string.field_reponse_caption))).toString());
                        photo.setReponse_tag(requireNonNull(objectMap.get(context.getString(R.string.field_reponse_tag))).toString());
                        photo.setReponse_photoID(requireNonNull(objectMap.get(context.getString(R.string.field_reponse_photoID))).toString());

                        photo.setInvite_caption(requireNonNull(objectMap.get(context.getString(R.string.field_invite_caption))).toString());
                        photo.setInvite_tag(requireNonNull(objectMap.get(context.getString(R.string.field_invite_tag))).toString());
                        photo.setInvite_photoID(requireNonNull(objectMap.get(context.getString(R.string.field_invite_photoID))).toString());
                    }
                    photo.setUser_id(requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
                    photo.setDate_created(Long.parseLong(requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

                    for (DataSnapshot dSnapshot : singleSnapshot.child(context.getString(R.string.field_comment_share)).getChildren()){
                        Comment comment = new Comment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        comment.setSuppressed(requireNonNull(objectHashMap.get(getString(R.string.field_suppressed))).toString());
                        comment.setUser_id(requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        comment.setComment(requireNonNull(objectHashMap.get(context.getString(R.string.field_comment))).toString());
                        comment.setCommentKey(requireNonNull(objectHashMap.get(context.getString(R.string.field_commentKey))).toString());
                        comment.setDate_created(Long.parseLong(requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        comment.setUrl(requireNonNull(objectHashMap.get(context.getString(R.string.field_url))).toString());
                        comment.setThumbnail(requireNonNull(objectHashMap.get(context.getString(R.string.field_thumbnail))).toString());

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
                if (comment_image_une_single_bottom != null) {

                    if (image_une_single_bottom != null)
                        imageUneSingleBottom_adapter.notifyDataSetChanged();
                    else if (video_une_single_bottom != null)
                        videoUneSingleBottom_adapter.notifyDataSetChanged();

                } else if (comment_image_double_single_bottom != null) {
                    if (image_double_single_bottom != null)
                        imageDoubleSingleBottom_adapter.notifyDataSetChanged();
                    else if (video_double_single_bottom != null)
                        videoDoubleSingleBottom_adapter.notifyDataSetChanged();

                } else if (comment_reponse_image_double_single_bottom != null) {
                    if (response_img_double_single_bottom != null)
                        response_imgSingleBottom_adapter.notifyDataSetChanged();
                    else if (response_vid_double_single_bottom != null)
                        response_vidSingleBottom_adapter.notifyDataSetChanged();

                } else if (comment_recycler_single_bottom != null) {
                    recyclerSingleBottom_adapter.notifyDataSetChanged();

                } else if (comment_image_une_top_bottom != null) {
                    if (circle_image_top_bottom != null)
                        circleImageTopBottom_adapter.notifyDataSetChanged();
                    else if (image_une_top_bottom != null)
                        imageUneTopBottom_adapter.notifyDataSetChanged();
                    else if (video_une_top_bottom != null)
                        videoUneTopBottom_adapter.notifyDataSetChanged();

                } else if (comment_image_double_top_bottom != null) {
                    if (image_double_top_bottom != null)
                        imageDoubleTopBottom_adapter.notifyDataSetChanged();
                    else if (video_double_top_bottom != null)
                        videoDoubleTopBottom_adapter.notifyDataSetChanged();

                } else if (comment_reponse_image_double_top_bottom != null) {
                    if (response_img_double_top_bottom != null)
                        response_imgTopBottom_adapter.notifyDataSetChanged();
                    else if (response_vid_double_top_bottom != null)
                        response_vidTopBottom_adapter.notifyDataSetChanged();

                } else if (comment_recycler_top_bottom != null) {
                    recyclerTopBottom_adapter.notifyDataSetChanged();

                }
            }

            if (battleModel != null) {
                getResponseComment(battleModel);

                RecyclerView.SmoothScroller smoothScroller = new
                        LinearSmoothScroller(context.getApplicationContext()) {
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

    private void setComments() {
        comments_count = 0;
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
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
                            .child(context.getString(R.string.dbname_group_media_share))
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

