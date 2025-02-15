package com.bekambimouen.miiokychallenge.groups.full_image_share;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.groups.comment_share.GroupCommentShare;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GroupImageUneFullImageShare extends AppCompatActivity {
    private static final String TAG = "GroupImageUneFullImageShare";
    // widgets
    private TextView username;
    private TextView the_user;
    private TextView tps_publication;
    private TextView translate_comment;
    private ViewMoreTextView caption;
    private PhotoView photoView;
    private ImageView menu_item;
    private SmallBangView like_heart;
    private ImageView image;
    private RelativeLayout parent, relLayout_username, relLayout_view_overlay;

    private TextView number_of_likes;
    private TextView number_of_comments;
    private TextView number_of_share;
    private RelativeLayout relLayout_write_to;
    private LinearLayout linLayout_all_moving_views;
    private LinearLayout linLayout_like;
    private LinearLayout linLayout_comment;
    private LinearLayout linLayout_share;

    // vars
    private GroupImageUneFullImageShare context;
    private BattleModel model;
    private BottomSheetSharePublication bottomSheetShare;
    private User mCurrentUser;
    private String user_id;
    private String description, from_comment, from_group_cover, from_group_background_profile;
    private StringBuilder mUsers;
    private StringBuilder updateLikeUsers;
    private boolean isFrom_approval_post;
    private boolean mLikedByCurrentUser;
    private boolean isShown = true;
    private int likes_count;
    private int comments_count;
    private int shares_count;
    // single bottom
    private String circle_image_single_bottom, image_une_single_bottom;
    // single top
    private String circle_image_single_top, image_une_single_top;
    // top bottom
    private String circle_image_top_bottom, image_une_top_bottom;

    // firebase
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_group_image_une_full_image_share);
        context = this;
        getBlackTheme();

        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        database=FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        try {
            Gson gson = new Gson();
            model = gson.fromJson(getIntent().getStringExtra("battleModel_image_une"), BattleModel.class);
            from_comment = getIntent().getStringExtra("from_comment");
            from_group_cover = getIntent().getStringExtra("from_group_cover");
            from_group_background_profile = getIntent().getStringExtra("from_group_background_profile");

            // show publication
            // single bottom
            circle_image_single_bottom = getIntent().getStringExtra("circle_image_single_bottom");
            image_une_single_bottom = getIntent().getStringExtra("image_une_single_bottom");
            // single top
            circle_image_single_top = getIntent().getStringExtra("circle_image_single_top");
            image_une_single_top = getIntent().getStringExtra("image_une_single_top");
            // top bottom
            circle_image_top_bottom = getIntent().getStringExtra("circle_image_top_bottom");
            image_une_top_bottom = getIntent().getStringExtra("image_une_top_bottom");

            // data from approval post
            isFrom_approval_post = getIntent().getBooleanExtra("isFrom_approval_post", false);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        // get the current user
        getCurrentUser();

        getLikesString();
        updateLike();
        setLikes();
        setComments();
        setShare();

        init();
        fullImage();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        ImageView back = findViewById(R.id.back);
        menu_item = findViewById(R.id.menu_item);

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        username = findViewById(R.id.username);
        the_user = findViewById(R.id.the_user);
        tps_publication = findViewById(R.id.tps_publication);
        caption = findViewById(R.id.caption);
        translate_comment = findViewById(R.id.translate_comment);
        relLayout_username = findViewById(R.id.relLayout_username);

        image = findViewById(R.id.image);
        like_heart = findViewById(R.id.like_heart);
        number_of_likes = findViewById(R.id.number_of_likes);
        number_of_comments = findViewById(R.id.number_of_comments);
        number_of_share = findViewById(R.id.number_of_share);
        linLayout_all_moving_views = findViewById(R.id.linLayout_all_moving_views);
        relLayout_write_to = findViewById(R.id.relLayout_write_to);
        linLayout_like = findViewById(R.id.linLayout_like);
        linLayout_comment = findViewById(R.id.linLayout_comment);
        linLayout_share = findViewById(R.id.linLayout_share);
        LinearLayout linLayout_like_and_comment = findViewById(R.id.linLayout_like_and_comment);

        // hide view when is from approval post
        if (isFrom_approval_post)
            linLayout_like_and_comment.setVisibility(View.GONE);

        // pour agrandir et réduire l'image (import graddle)
        photoView = findViewById(R.id.iv_photo);

        back.setOnClickListener(v -> {
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

    private void fullImage() {
        if (model.getUser_id().equals(user_id))
            relLayout_write_to.setVisibility(View.GONE);

        long tv_date = model.getDate_created();
        tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

        if (!isFinishing()) {
            if (from_group_cover != null) {
                Glide.with(context)
                        .load(model.getGroup_full_profile_photo())
                        .placeholder(R.color.black)
                        .into(photoView);

            } else if (from_group_background_profile != null) {
                Glide.with(context)
                        .load(model.getGroup_user_background_full_profile_url())
                        .placeholder(R.color.black)
                        .into(photoView);

            } else if (!model.getUser_profile_photo().isEmpty()) {
                Glide.with(context)
                        .load(model.getUser_full_profile_photo())
                        .placeholder(R.color.black)
                        .into(photoView);

            } else {
                Glide.with(context)
                        .load(model.getUrl())
                        .placeholder(R.color.black)
                        .into(photoView);
            }
        }

        photoView.setOnClickListener(view -> {
            if (isShown) {
                linLayout_all_moving_views.setVisibility(View.GONE);
                menu_item.setVisibility(View.GONE);
                isShown = false;
            } else {
                linLayout_all_moving_views.setVisibility(View.VISIBLE);
                menu_item.setVisibility(View.VISIBLE);
                isShown = true;
            }
        });

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String name = user.getUsername();

                    username.setText(name);
                    the_user.setText(name);

                    relLayout_write_to.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query myQuery1 = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(model.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(model.getShared_group_id());

        myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    relLayout_username.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("user_groups", myGson);
                        intent.putExtra("userID", model.getUser_id());
                        intent.putExtra("group_id", model.getGroup_id());
                        context.startActivity(intent);
                    });

                    // menu_item
                    BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                    menu_item.setOnClickListener(view -> {
                        if (bottomSheet.isAdded())
                            return;
                        Gson gson = new Gson();
                        String myJson = gson.toJson(user_groups);

                        Bundle args = new Bundle();
                        args.putParcelable("battle_model", model);
                        args.putString("user_groups", myJson);
                        args.putString("group", "group");
                        bottomSheet.setArguments(args);
                        bottomSheet.show(context.getSupportFragmentManager(),
                                "TAG");
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        description = model.getCaption();
        if (!TextUtils.isEmpty(description)) {
            caption.setVisibility(View.VISIBLE);
            caption.setCharText(description);
        }

        // Get the current language in device
        String language = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(description)
                .addOnSuccessListener(
                        languageCode -> {
                            assert languageCode != null;
                            if (languageCode.equals("und")) {
                                Log.i(TAG, "Can't identify language.");
                            } else {
                                Log.i(TAG, "Language: " + languageCode);
                                if (!languageCode.equals(language))
                                    translate_comment.setVisibility(View.VISIBLE);
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldn’t be loaded or other internal error.
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        translate_comment.setOnClickListener(view -> {
            translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language,         //language to
                    description);           //Query Text

            translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                @Override
                public void onSuccess(String translatedText) {
                    Log.d(TAG, "onSuccess: "+translatedText);
                    caption.setCharText(translatedText);
                }

                @Override
                public void onFailure(String ErrorText) {
                    Log.d(TAG, "onFailure: "+ErrorText);
                }
            });
        });

        // like
        linLayout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                    image.setImageResource(R.drawable.ic_heart_white);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeLike();
                        }
                    });

                } else {
                    like_heart.setSelected(true);
                    image.setImageResource(R.drawable.ic_heart_red);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mLikedByCurrentUser) {
                                addNewLike();
                            }
                        }
                    });
                }
            }
        });

        linLayout_comment.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            if (from_comment != null) {
                finish();

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGSon = gson.toJson(model);
                Intent intent = new Intent(context, GroupCommentShare.class);
                intent.putExtra("comment_image_une_top_bottom", myGSon);
                // to show image une publication
                intent.putExtra("image_une_top_bottom", "image_une_top_bottom");
                intent.putExtra("from_full_image", "from_full_image");
                if (from_group_cover != null) {
                    intent.putExtra("from_group_cover", "from_group_cover");
                    intent.putExtra("circle_image_top_bottom", "circle_image_top_bottom");
                }
                else if (from_group_background_profile != null) {
                    intent.putExtra("from_group_background_profile", "from_group_background_profile");
                    intent.putExtra("circle_image_top_bottom", "circle_image_top_bottom");
                }

                // top bottom
                if (image_une_top_bottom != null || circle_image_top_bottom!= null) {
                    intent.putExtra("comment_image_une_top_bottom", myGSon);
                    // to show image une publication
                    intent.putExtra("image_une_top_bottom", "image_une_top_bottom");
                } else
                    // single bottom
                    if (image_une_single_bottom != null || circle_image_single_bottom!= null) {
                        intent.putExtra("comment_image_une_single_bottom", myGSon);
                        // to show image une publication
                        intent.putExtra("image_une_single_bottom", "image_une_single_bottom");
                    } else
                        // single top
                        if (image_une_single_top != null || circle_image_single_top!= null) {
                            intent.putExtra("comment_image_une_single_top", myGSon);
                            // to show image une publication
                            intent.putExtra("image_une_single_top", "image_une_single_top");
                        }

                context.startActivity(intent);
            }
        });

        if (from_group_cover != null)
            bottomSheetShare = new BottomSheetSharePublication(context, null, model.getUser_id(), model.getGroup_full_profile_photo(),
                    model.getPhoto_id(), "from_full_image", "", true);
        else if (from_group_background_profile != null)
            bottomSheetShare = new BottomSheetSharePublication(context, null, model.getUser_id(), model.getGroup_user_background_full_profile_url(),
                    model.getPhoto_id(), "from_full_image", "", true);
        else
            bottomSheetShare = new BottomSheetSharePublication(context, null, model.getUser_id(), model.getUrl(),
                    model.getPhoto_id(), "from_full_image", "", true);
        linLayout_share.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putBoolean("group_share_single_bottom_circle_image", false);
                bundle.putBoolean("group_share_single_bottom_image_double", false);
                bundle.putBoolean("group_share_single_bottom_image_une", false);
                bundle.putBoolean("group_share_single_bottom_recycler", false);
                bundle.putBoolean("group_share_single_bottom_response_image_double", false);
                bundle.putBoolean("group_share_single_bottom_response_video_double", false);
                bundle.putBoolean("group_share_single_bottom_video_double", false);
                bundle.putBoolean("group_share_single_bottom_video_une", false);

                bundle.putBoolean("group_share_single_top_circle_image", false);
                bundle.putBoolean("group_share_single_top_image_double", false);
                bundle.putBoolean("group_share_single_top_image_une", false);
                bundle.putBoolean("group_share_single_top_recycler", false);
                bundle.putBoolean("group_share_single_top_response_image_double", false);
                bundle.putBoolean("group_share_single_top_response_video_double", false);
                bundle.putBoolean("group_share_single_top_video_double", false);
                bundle.putBoolean("group_share_single_top_video_une", false);

                bundle.putBoolean("group_share_top_bottom_circle_image", false);
                bundle.putBoolean("group_share_top_bottom_image_double", false);
                bundle.putBoolean("group_share_top_bottom_image_une", false);
                bundle.putBoolean("group_share_top_bottom_recycler", false);
                bundle.putBoolean("group_share_top_bottom_response_image_double", false);
                bundle.putBoolean("group_share_top_bottom_response_video_double", false);
                bundle.putBoolean("group_share_top_bottom_video_double", false);
                bundle.putBoolean("group_share_top_bottom_video_une", false);

                bundle.putBoolean("user_profile_shared", false);
                bundle.putBoolean("recyclerItem_shared", false);
                bundle.putBoolean("imageUne_shared", false);
                bundle.putBoolean("videoUne_shared", false);
                bundle.putBoolean("imageDouble_shared", false);
                bundle.putBoolean("videoDouble_shared", false);
                bundle.putBoolean("reponseImageDouble_shared", false);
                bundle.putBoolean("reponseVideoDouble_shared", false);
                bundle.putBoolean("user_profile", false);

                bundle.putBoolean("group_circle_image", false);
                bundle.putBoolean("group_image_double", false);
                bundle.putBoolean("group_image_une", true);
                bundle.putBoolean("group_recycler", false);
                bundle.putBoolean("group_response_image_double", false);
                bundle.putBoolean("group_response_video_double", false);
                bundle.putBoolean("group_video_double", false);
                bundle.putBoolean("group_video_une", false);

                bundle.putBoolean("circle_image", false);
                bundle.putBoolean("image_double", false);
                bundle.putBoolean("image_une", false);
                bundle.putBoolean("recycler", false);
                bundle.putBoolean("response_image_double", false);
                bundle.putBoolean("response_video_double", false);
                bundle.putBoolean("video_double", false);
                bundle.putBoolean("video_une", false);
                bottomSheetShare.setArguments(bundle);
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "fullImage: "+e.getMessage());
            }
        });
    }

    private void getCurrentUser(){
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    mCurrentUser = Util_User.getUser(context, objectMap, ds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mLikedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update like count
                        int count = Integer.parseInt(number_of_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            number_of_likes.setVisibility(View.GONE);
                        number_of_likes.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_group_media_share))
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(model.getGroup_id())
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share))
                                .child(keyID)
                                .removeValue();

                        getLikesString();
                        updateLike();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(number_of_likes.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            number_of_likes.setVisibility(View.VISIBLE);
        number_of_likes.setText(str);

        // add new like
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_group_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_likes_share))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_group))
                .child(model.getGroup_id())
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_likes_share))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString();
        updateLike();
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsers.append(user.getUsername());
                                mUsers.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikeString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupLikeString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLike(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateLikeUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                updateLikeUsers.append(user.getUsername());
                                updateLikeUsers.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = updateLikeUsers.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikeString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupLikeString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupLikeString() {
        if (mLikedByCurrentUser) {
            if (!like_heart.isSelected()) {
                like_heart.setSelected(true);
                image.setImageResource(R.drawable.ic_heart_red);
            }

        } else {
            if (like_heart.isSelected()) {
                like_heart.setSelected(false);
                image.setImageResource(R.drawable.ic_heart_white);
            }
        }
    }

    private void setLikes() {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    number_of_likes.setVisibility(View.VISIBLE);

                    double count;
                    if (likes_count >= 1000) {
                        count = (float)likes_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";

                        number_of_likes.setText(tv_count);

                    } else {
                        number_of_likes.setText(String.valueOf(likes_count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setComments() {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(model.getPhoto_id())
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
                            .child(model.getPhoto_id())
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

                                double count;
                                if (comments_count >= 1000) {
                                    count = (float)comments_count/1000;

                                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                                    number_of_comments.setText(tv_count);

                                } else {
                                    number_of_comments.setText(String.valueOf(comments_count));
                                }
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

    private void setShare() {
        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(model.getPhoto_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shares_count++;
                }

                if (shares_count >= 1) {
                    number_of_share.setVisibility(View.VISIBLE);

                    double count;
                    if (shares_count >= 1000) {
                        count = (float)shares_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        number_of_share.setText(tv_count);

                    } else {
                        number_of_share.setText(String.valueOf(shares_count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}