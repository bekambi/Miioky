package com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Crushers;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.bottomsheet.GroupBottomSheetAddCommentShare;
import com.bekambimouen.miiokychallenge.groups.full_image_share.GroupImageUneFullImageShare;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_NotificationModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bumptech.glide.Glide;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.github.kshitij_jain.instalike.InstaLikeView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSharedTopBottomCircleImageDisplay extends RecyclerView.ViewHolder {
    private static final String TAG = "GroupSharedTopBottomImageUneDisplay";

    // wifgets
    private final ImageView conner_big_star_icon;
    private final InstaLikeView insta_star_view;
    private final SmallBangView like_star;
    private final ImageView unlike_star_image;
    private final SmallBangView like_heart;
    private final ImageView image;
    private final InstaLikeView mInstaLikeView;
    private final CircleImageView circle_img;
    private final TextView nber_of_crush;
    private final TextView crush_msg;
    private final LinearLayout linLayout_share_only;
    private final LinearLayout linLayout_possibility_action;

    private final TextView number_of_likes;
    private final TextView number_of_comments;
    private final TextView number_of_share;
    private final LinearLayout linLayout_like;
    private final LinearLayout linLayout_comment;
    private final LinearLayout linLayout_share;
    private final LinearLayout linLayout_count_like;

    // top
    private final CardView sharing_cardView;
    private final ImageView sharing_group_profile_photo;
    private final CircleImageView sharing_profile_photo;
    private final View sharing_view_online;
    private final ImageView menu_item;
    public final ImageView delete_suggestion;
    private final TextView sharing_group_name;
    private final TextView sharing_username;
    private final ViewMoreTextView sharing_caption;
    private final TextView sharing_translate_comment;
    private final TextView sharing_tps_publication;
    private final TextView sharing_join_group;
    private final RelativeLayout sharing_relLayout_username;
    private final RelativeLayout sharing_relLayout_group_name;

    // group
    private final CardView cardView;
    private final ImageView group_profile_photo;
    private final CircleImageView profile_photo;
    private final View view_online;
    private final TextView username;
    private final TextView group_name;
    private final TextView tps_publication;
    private final TextView join_group;
    private final RelativeLayout relLayout_username;
    private final RelativeLayout relLayout_group_name;

    // vars
    private final AppCompatActivity context;
    private final GestureDetector mDetectorLike;
    private BottomSheetSharePublication bottomSheetShare;
    private BattleModel mPhoto, mModel;
    private final RelativeLayout relLayout_view_overlay;
    private MyEditText mComment;
    private String from_full_image;
    private StringBuilder mUsers;
    private StringBuilder updateLikeUsers;
    private StringBuilder mUsersCrush;
    private StringBuilder updateCrushUsers;
    private User mCurrentUser;
    private final ArrayList<String> liker_list, crusher_list;
    private boolean mLikedByCurrentUser;
    private boolean mCrushedByCurrentUser;
    private final String user_id;
    private int crush_count = 0;
    private int likes_count = 0;
    private int comments_count = 0;
    private int shares_count = 0;

    // firebase
    private final DatabaseReference myRef;
    private final FirebaseDatabase database;

    @SuppressLint("ClickableViewAccessibility")
    public GroupSharedTopBottomCircleImageDisplay(AppCompatActivity context, BattleModel mModel,
                                                  MyEditText mComment, String from_full_image,
                                                  RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        if (mModel != null) {
            this.mModel = mModel;
            this.mPhoto = mModel;
            this.mComment = mComment;
            this.from_full_image = from_full_image;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        database=FirebaseDatabase.getInstance();

        liker_list = new ArrayList<>();
        crusher_list = new ArrayList<>();

        LinearLayout parent = itemView.findViewById(R.id.parent);
        circle_img = itemView.findViewById(R.id.circle_img);

        image = itemView.findViewById(R.id.image);
        like_heart = itemView.findViewById(R.id.like_heart);
        mInstaLikeView = itemView.findViewById(R.id.insta_like_view);

        number_of_likes = itemView.findViewById(R.id.number_of_likes);
        number_of_comments = itemView.findViewById(R.id.number_of_comments);
        number_of_share = itemView.findViewById(R.id.number_of_share);
        linLayout_count_like = itemView.findViewById(R.id.linLayout_count_like);
        linLayout_like = itemView.findViewById(R.id.linLayout_like);
        linLayout_comment = itemView.findViewById(R.id.linLayout_comment);
        linLayout_share = itemView.findViewById(R.id.linLayout_share);

        // crush
        conner_big_star_icon = itemView.findViewById(R.id.conner_big_star_icon);
        insta_star_view = itemView.findViewById(R.id.insta_star_view);
        like_star = itemView.findViewById(R.id.like_star);
        unlike_star_image = itemView.findViewById(R.id.unlike_star_image);

        nber_of_crush = itemView.findViewById(R.id.nber_of_crush);
        crush_msg = itemView.findViewById(R.id.crush_msg);

        // top
        sharing_cardView = itemView.findViewById(R.id.sharing_cardView);
        sharing_group_profile_photo = itemView.findViewById(R.id.sharing_group_profile_photo);
        sharing_profile_photo = itemView.findViewById(R.id.sharing_profile_photo);
        sharing_view_online = itemView.findViewById(R.id.sharing_view_online);
        menu_item = itemView.findViewById(R.id.menu_item);
        delete_suggestion = itemView.findViewById(R.id.delete_suggestion);
        sharing_group_name = itemView.findViewById(R.id.sharing_group_name);
        sharing_username = itemView.findViewById(R.id.sharing_username);
        sharing_caption = itemView.findViewById(R.id.sharing_caption);
        sharing_translate_comment = itemView.findViewById(R.id.sharing_translate_comment);
        sharing_tps_publication = itemView.findViewById(R.id.sharing_tps_publication);
        sharing_relLayout_username = itemView.findViewById(R.id.sharing_relLayout_username);
        sharing_relLayout_group_name = itemView.findViewById(R.id.sharing_relLayout_group_name);

        sharing_join_group = itemView.findViewById(R.id.sharing_join_group);
        join_group = itemView.findViewById(R.id.join_group);

        // group
        cardView = itemView.findViewById(R.id.cardView);
        group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
        profile_photo = itemView.findViewById(R.id.profile_photo);
        view_online = itemView.findViewById(R.id.view_online);
        username = itemView.findViewById(R.id.username);
        group_name = itemView.findViewById(R.id.group_name);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        relLayout_username = itemView.findViewById(R.id.relLayout_username);
        relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);

        linLayout_share_only = itemView.findViewById(R.id.linLayout_share_only);
        linLayout_possibility_action = itemView.findViewById(R.id.linLayout_possibility_action);

        // commenatires
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // for double tap to like
        mDetectorLike = new GestureDetector(context, new GestureListenerLike());
        circle_img.setOnTouchListener((view, motionEvent) -> mDetectorLike.onTouchEvent(motionEvent));

        // for comments
        if (mModel != null) {
            init(this.mModel);
            parent.setPadding(0,0,0,100);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 500);
            circle_img.setLayoutParams(layoutParams);
        }
    }

    public void init(BattleModel model) {
        mPhoto = model;

        sharing_caption.setCharText("");
        sharing_caption.setVisibility(View.GONE);
        sharing_translate_comment.setVisibility(View.GONE);
        nber_of_crush.setText("0");
        number_of_likes.setText("0");
        number_of_comments.setText("0");
        number_of_share.setText("0");

        if(liker_list != null){
            liker_list.clear();
        }

        if(crusher_list != null){
            crusher_list.clear();
        }

        // verify if user is online /___top
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getUser_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            if(status != null && !status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    sharing_view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        sharing_view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // verify if user is online /___bottom
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getUser_id_sharing())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            if(status != null && !status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id_sharing().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        setLikes();
        setComments();
        setShare();
        crushCount();
        getCurrentUser();
        getLikesString();
        updateLike();
        getCrushString();
        updateCrush();

        // verify is current user is suspended _____________________________________________________
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(model.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (follower.isSuspended()) {
                        linLayout_possibility_action.setVisibility(View.GONE);
                        linLayout_share_only.setVisibility(View.VISIBLE);

                    } else {
                        linLayout_possibility_action.setVisibility(View.VISIBLE);
                        linLayout_share_only.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // _________________________________________________________________________________________

        //set the time it was posted
        long date_shared = model.getDate_shared();
        long time = (System.currentTimeMillis() - date_shared);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(TimeUtils.getTime(context, date_shared));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date_shared)));

        long date_created = model.getDate_created();
        long time2 = (System.currentTimeMillis() - date_created);
        if (time2 >= 2*3600*24000254025L)
            sharing_tps_publication.setText(TimeUtils.getTime(context, date_created));
        else
            sharing_tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date_created)));

        // use if url is empty here
        if (!model.getGroup_user_background_profile_url().isEmpty()) {
            // to user background prodile
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getGroup_user_background_profile_url())
                    .into(circle_img);

            Glide.with(context.getApplicationContext())
                    .load(model.getGroup_user_background_full_profile_url())
                    .preload();

        } else {
            // to group profile
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getGroup_profile_photo())
                    .into(circle_img);

            Glide.with(context.getApplicationContext())
                    .load(model.getGroup_full_profile_photo())
                    .preload();
        }

        // sharing caption _______________________________________
        String sharing_description = model.getSharing_caption();

        if (!TextUtils.isEmpty(sharing_description)) {
            sharing_caption.setCharText(sharing_description);
            sharing_caption.setVisibility(View.VISIBLE);
        }

        // Get the current language in device
        String language1 = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(sharing_description)
                .addOnSuccessListener(
                        languageCode -> {
                            assert languageCode != null;
                            if (languageCode.equals("und")) {
                                Log.i(TAG, "Can't identify language.");
                            } else {
                                Log.i(TAG, "Language: " + languageCode);
                                if (!languageCode.equals(language1))
                                    sharing_translate_comment.setVisibility(View.VISIBLE);
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldn’t be loaded or other internal error.
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        sharing_translate_comment.setOnClickListener(view -> {
            sharing_translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language1,         //language to
                    sharing_description);           //Query Text

            translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                @Override
                public void onSuccess(String translatedText) {
                    Log.d(TAG, "onSuccess: "+translatedText);
                    sharing_caption.setCharText(translatedText);
                }

                @Override
                public void onFailure(String ErrorText) {
                    Log.d(TAG, "onFailure: "+ErrorText);
                }
            });
        });

        //get the profile image and username /___top
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(mPhoto.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(mPhoto.getGroup_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    sharing_group_name.setText(user_groups.getGroup_name());

                    Glide.with(context.getApplicationContext())
                            .load(user_groups.getProfile_photo())
                            .into(sharing_group_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user_groups.getFull_photo())
                            .preload();

                    Query query2 = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(mPhoto.getUser_id());

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found user: "+user.getUsername());

                                String userID = user.getUser_id();

                                String userName = user.getUsername();
                                sharing_username.setText(userName);

                                Glide.with(context.getApplicationContext())
                                        .load(user.getProfileUrl())
                                        .into(sharing_profile_photo);

                                Glide.with(context.getApplicationContext())
                                        .load(user.getFull_profileUrl())
                                        .preload();

                                sharing_profile_photo.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (model.getUser_id().equals(user_id) && user_groups.getAdmin_master().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                sharing_relLayout_username.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (model.getUser_id().equals(user_id) && user_groups.getAdmin_master().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                // to know if current user is admin
                                if (user_groups.getAdmin_master().equals(user_id)) {
                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);

                                    sharing_cardView.setOnClickListener(v -> {
                                        Log.d(TAG, "onClick: navigating to profile of: " +
                                                user.getUsername());

                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewAdmin.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                    sharing_relLayout_group_name.setOnClickListener(v -> {
                                        Log.d(TAG, "onClick: navigating to profile of: " +
                                                user.getUsername());

                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewAdmin.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                } else {
                                    Query query = myRef
                                            .child(context.getString(R.string.dbname_group_following))
                                            .child(user_id)
                                            .orderByChild(context.getString(R.string.field_group_id))
                                            .equalTo(user_groups.getGroup_id());

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                                                assert objectMap != null;
                                                GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                                Gson gson = new Gson();
                                                String myJson = gson.toJson(user_groups);

                                                sharing_cardView.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent;
                                                    if (follower.isModerator()|| follower.isAdminInGroup()) {
                                                        intent = new Intent(context, GroupViewAdmin.class);
                                                    } else {
                                                        intent = new Intent(context, GroupViewProfile.class);
                                                    }
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });

                                                sharing_relLayout_group_name.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent;
                                                    if (follower.isModerator() || follower.isAdminInGroup()) {
                                                        intent = new Intent(context, GroupViewAdmin.class);
                                                    } else {
                                                        intent = new Intent(context, GroupViewProfile.class);
                                                    }
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });
                                            }

                                            if (!snapshot.exists()) {
                                                Gson gson = new Gson();
                                                String myJson = gson.toJson(user_groups);

                                                sharing_cardView.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupViewProfile.class);
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });

                                                sharing_relLayout_group_name.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupViewProfile.class);
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // menu_item
                    BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                    menu_item.setOnClickListener(view -> {
                        if (bottomSheet.isAdded())
                            return;
                        Gson gson = new Gson();
                        String myJson = gson.toJson(user_groups);

                        Bundle args = new Bundle();
                        args.putParcelable("battle_model", mPhoto);
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
        // _________________________________________________________________
        //get the profile image and username /___bottom
        Query myQuery1 = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(mPhoto.getSharing_admin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(mPhoto.getShared_group_id());

        myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    group_name.setText(user_groups.getGroup_name());

                    Glide.with(context.getApplicationContext())
                            .load(user_groups.getProfile_photo())
                            .into(group_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user_groups.getFull_photo())
                            .preload();

                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(model.getUser_id_sharing());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Glide.with(context.getApplicationContext())
                                        .load(user.getProfileUrl())
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(profile_photo);

                                Glide.with(context.getApplicationContext())
                                        .load(user.getFull_profileUrl())
                                        .preload();

                                String userID = user.getUser_id();
                                String userName = user.getUsername();
                                username.setText(userName);

                                profile_photo.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (model.getUser_id_sharing().equals(user_id) && user_groups.getAdmin_master().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                relLayout_username.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (model.getUser_id_sharing().equals(user_id) && user_groups.getAdmin_master().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                // to know if current user is admin
                                if (user_groups.getAdmin_master().equals(user_id)) {
                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);

                                    cardView.setOnClickListener(v -> {
                                        Log.d(TAG, "onClick: navigating to profile of: " +
                                                user.getUsername());

                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewAdmin.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                    relLayout_group_name.setOnClickListener(v -> {
                                        Log.d(TAG, "onClick: navigating to profile of: " +
                                                user.getUsername());

                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewAdmin.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                } else {
                                    Query query = myRef
                                            .child(context.getString(R.string.dbname_group_following))
                                            .child(user_id)
                                            .orderByChild(context.getString(R.string.field_group_id))
                                            .equalTo(user_groups.getGroup_id());

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                                                assert objectMap != null;
                                                GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                                Gson gson = new Gson();
                                                String myJson = gson.toJson(user_groups);

                                                cardView.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent;
                                                    if (follower.isModerator()|| follower.isAdminInGroup()) {
                                                        intent = new Intent(context, GroupViewAdmin.class);
                                                    } else {
                                                        intent = new Intent(context, GroupViewProfile.class);
                                                    }
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });

                                                relLayout_group_name.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent;
                                                    if (follower.isModerator() || follower.isAdminInGroup()) {
                                                        intent = new Intent(context, GroupViewAdmin.class);
                                                    } else {
                                                        intent = new Intent(context, GroupViewProfile.class);
                                                    }
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });
                                            }

                                            if (!snapshot.exists()) {
                                                Gson gson = new Gson();
                                                String myJson = gson.toJson(user_groups);

                                                cardView.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupViewProfile.class);
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });

                                                relLayout_group_name.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());

                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupViewProfile.class);
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
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

        if (mModel != null) {
            linLayout_comment.setOnClickListener(view -> {
                // to show keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                mComment.requestFocus();
            });
        } else {
            GroupBottomSheetAddCommentShare sheetFragmentShare = new GroupBottomSheetAddCommentShare(context,
                    null, null, null, null, model,
                    null, null, null, null, null, null,
                    null, null, null, "circle_image_top_bottom", null,
                    null, null, null, null, null, relLayout_view_overlay);
            linLayout_comment.setOnClickListener(view -> {
                if (sheetFragmentShare.isAdded())
                    return;
                sheetFragmentShare.show(context.getSupportFragmentManager(), "TAG");
            });
        }

        // share
        if (!mPhoto.getGroup_user_background_profile_url().isEmpty())
            bottomSheetShare = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getGroup_user_background_full_profile_url(),
                    model.getPhoto_id(), "", "", true);
        else
            bottomSheetShare = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getGroup_full_profile_photo(),
                    model.getPhoto_id(), "", "", true);
        linLayout_share.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare.setArguments(bundle);
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        // when current user is suspended
        linLayout_share_only.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(linLayout_share_only);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare.setArguments(bundle);
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        linLayout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                    image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
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

        like_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_star.isSelected()) {
                    like_star.setSelected(false);
                    conner_big_star_icon.setVisibility(View.GONE);
                    unlike_star_image.setImageResource(R.drawable.unlike_star);
                    like_star.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeCrush();
                        }
                    });

                } else {
                    like_star.setSelected(true);
                    unlike_star_image.setImageResource(R.drawable.big_star_icon);
                    insta_star_view.start();
                    conner_big_star_icon.setVisibility(View.VISIBLE);
                    like_star.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mCrushedByCurrentUser) {
                                addNewCrush();
                            }
                        }
                    });
                }
            }
        });

        // top
        getJoin_group(model.getAdmin_master(), model.getGroup_id(), sharing_join_group);

        // bottom
        getJoin_group(model.getSharing_admin_master(), model.getShared_group_id(), join_group);
    }

    private final class GestureListenerLike extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            if (mModel != null) {
                if (from_full_image != null) {
                    context.finish();
                } else  {
                    Intent intent = new Intent(context, GroupImageUneFullImageShare.class);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(mPhoto);
                    intent.putExtra("battleModel_image_une", myGson);
                    if (!mPhoto.getGroup_user_background_profile_url().isEmpty())
                        intent.putExtra("from_group_background_profile", "from_group_background_profile");
                    else
                        intent.putExtra("from_group_cover", "from_group_cover");
                    intent.putExtra("circle_image_top_bottom", "circle_image_top_bottom");
                    intent.putExtra("from_comment", "from_comment");
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, GroupImageUneFullImageShare.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                intent.putExtra("battleModel_image_une", myGson);
                if (!mPhoto.getGroup_user_background_profile_url().isEmpty())
                    intent.putExtra("from_group_background_profile", "from_group_background_profile");
                else
                    intent.putExtra("from_group_cover", "from_group_cover");
                intent.putExtra("circle_image_top_bottom", "circle_image_top_bottom");
                context.startActivity(intent);
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Log.d(TAG, "onDoubleTap: single tap detected.");
            if (like_heart.isSelected()) {
                like_heart.setSelected(false);
                image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
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
                // screen like animation
                mInstaLikeView.start();
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
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    }

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
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
                        if (str.equals("0")) {
                            linLayout_count_like.setVisibility(View.INVISIBLE);
                            number_of_likes.setVisibility(View.GONE);
                        }
                        number_of_likes.setText(str);

                        // remove like points
                        removeLikePoints();

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_group_media_share))
                                .child(mPhoto.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(mPhoto.getGroup_id())
                                .child(mPhoto.getPhoto_id())
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
        if (!str.equals("0")) {
            number_of_likes.setVisibility(View.VISIBLE);
            linLayout_count_like.setVisibility(View.VISIBLE);
        }
        if (linLayout_count_like.getVisibility() != View.VISIBLE)
            linLayout_count_like.setVisibility(View.VISIBLE);

        number_of_likes.setText(str);

        // add like points
        addLikePoints();

        // add new like
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_likes_share))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_group))
                .child(mPhoto.getGroup_id())
                .child(mPhoto.getPhoto_id())
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
                .child(mPhoto.getPhoto_id())
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
                .child(mPhoto.getPhoto_id())
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
                image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    private void removeCrush() {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_crush_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mCrushedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update crush count
                        int count = Integer.parseInt(nber_of_crush.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nber_of_crush.setVisibility(View.GONE);
                        nber_of_crush.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_group_media_share))
                                .child(mPhoto.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(mPhoto.getGroup_id())
                                .child(mPhoto.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share))
                                .child(keyID)
                                .removeValue();

                        getCrushString();
                        updateCrush();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewCrush(){
        Log.d(TAG, "addNewCrush: adding new crush");
        // update crush count
        nber_of_crush.setVisibility(View.VISIBLE);
        int count = Integer.parseInt(nber_of_crush.getText().toString());
        String str = String.valueOf(count+1);
        nber_of_crush.setText(str);

        String newCrushID = myRef.push().getKey();
        Crush crush = new Crush();
        crush.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newCrushID != null;
        myRef.child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_crush_share))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_group))
                .child(mPhoto.getGroup_id())
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_crush_share))
                .child(newCrushID)
                .setValue(crush);

        // affichage à l'écran
        getCrushString();
        updateCrush();
    }

    private void getCrushString(){
        Log.d(TAG, "getCrushString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_crush_share));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersCrush = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsersCrush.append(user.getUsername());
                                mUsersCrush.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mCrushedByCurrentUser = mUsersCrush.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser = false;
                    setupCrushString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCrush(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_crush_share));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateCrushUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found crush: " +user.getUsername());

                                updateCrushUsers.append(user.getUsername());
                                updateCrushUsers.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mCrushedByCurrentUser = updateCrushUsers.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser = false;
                    setupCrushString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupCrushString() {
        if (mCrushedByCurrentUser) {
            if (!like_star.isSelected()) {
                like_star.setSelected(true);
                conner_big_star_icon.setVisibility(View.VISIBLE);
                unlike_star_image.setImageResource(R.drawable.big_star_icon);
            }

        } else {
            if (like_star.isSelected()) {
                like_star.setSelected(false);
                conner_big_star_icon.setVisibility(View.GONE);
                unlike_star_image.setImageResource(R.drawable.unlike_star);
            }
        }
    }

    private void getCurrentUser(){
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

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

    public void crushCount() {
        crush_count = 0;
        nber_of_crush.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_crush_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    crush_count++;
                }

                if (crush_count >= 1) {
                    nber_of_crush.setVisibility(View.VISIBLE);
                    nber_of_crush.setText(String.valueOf(crush_count));

                    if (crush_count >= 2)
                        crush_msg.setText(context.getString(R.string.several_crush));

                    nber_of_crush.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list);
                        context.startActivity(intent);
                    });

                    crush_msg.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLikes() {
        likes_count = 0;
        linLayout_count_like.setVisibility(View.INVISIBLE);
        number_of_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    linLayout_count_like.setVisibility(View.VISIBLE);
                    number_of_likes.setVisibility(View.VISIBLE);

                    double count;
                    if (likes_count >= 1000) {
                        count = (float)likes_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";

                        number_of_likes.setText(tv_count);

                    } else {
                        number_of_likes.setText(String.valueOf(likes_count));
                    }

                    linLayout_count_like.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Likers.class);
                        intent.putStringArrayListExtra("liker_list", liker_list);
                        context.startActivity(intent);
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setComments() {
        comments_count = 0;
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(mPhoto.getPhoto_id())
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
                            .child(mPhoto.getPhoto_id())
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
        shares_count = 0;
        number_of_share.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(mPhoto.getPhoto_id());

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

    // add post points
    private void addLikePoints() {
        if (mPhoto.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(mPhoto.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();
                        int number_of_like_points = Integer.parseInt(user_groups.getLike_points());
                        map.put("like_points", number_of_like_points+1);

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
                    .equalTo(mPhoto.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();

                        int number_of_like_points = Integer.parseInt(following.getLike_points());
                        map.put("like_points", number_of_like_points+1);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(mPhoto.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(mPhoto.getGroup_id())
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

    // remove post points
    private void removeLikePoints() {
        if (mPhoto.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(mPhoto.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();
                        int number_of_like_points = Integer.parseInt(user_groups.getLike_points());
                        map.put("like_points", number_of_like_points-1);

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
                    .equalTo(mPhoto.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();

                        int number_of_like_points = Integer.parseInt(following.getLike_points());
                        map.put("like_points", number_of_like_points-1);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(mPhoto.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(mPhoto.getGroup_id())
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

    // join group __________________________________________________________________________________
    private void getJoin_group(String admin_master, String group_id, TextView join_group) {
        isJoinedGroup(admin_master, group_id, join_group);

        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(admin_master,
                user_id, "", group_id);

        join_group.setOnClickListener(view -> {
            // hide the button in notification "invite user to be member of group"
            Query query2 = myRef.child(context.getString(R.string.dbname_notification))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                        if (notification.isInvitation_in_group()) {
                            // show or hide dialog box
                            if (notification.isInvite_to_join_the_group()
                                    && !notification.isAccepted_invitation_to_join_the_group()
                                    && !notification.isRefuse_invitation_to_join_the_group()) {

                                HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(admin_master,
                                        user_id, notification.getAdding_by(), group_id);

                                // follow the group
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_following))
                                        .child(user_id)
                                        .child(group_id)
                                        .setValue(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_followers))
                                        .child(group_id)
                                        .child(user_id)
                                        .setValue(map);

                                setJoinedGroup(join_group);

                                // update current user notification
                                HashMap<String, Object> user_notification = new HashMap<>();
                                user_notification.put("invite_to_join_the_group", true);
                                user_notification.put("accepted_invitation_to_join_the_group", true);
                                user_notification.put("hide_buttons", true);

                                myRef.child(context.getString(R.string.dbname_notification))
                                        .child(user_id)
                                        .child(notification.getNotification_id())
                                        .updateChildren(user_notification);

                                // send notification to member who invited
                                Date date = new Date();
                                String new_key = myRef.push().getKey();
                                HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                        true, false, false, false, false,
                                        false,false, false, false,
                                        false, false, false, false, false, false,
                                        false,
                                        false, false, false, false, false,
                                        false, false,
                                        false, false, false, false, false,
                                        false, false,
                                        true, "", false, true,
                                        false, false,
                                        true,false, false,
                                        notification.getAdding_by(), new_key, user_id, admin_master,
                                        "", group_id, date.getTime(),
                                        false, false,
                                        false, false, false, false, false, false, false, false,
                                        false, "", "", "", false, "", "", "", false,
                                        "", "", "", "", "", 0,
                                        "", "", 0, "", "", "",
                                        false, false, false, false,
                                        false, false, false,
                                        false, false);

                                assert new_key != null;
                                myRef.child(context.getString(R.string.dbname_notification))
                                        .child(notification.getAdding_by())
                                        .child(new_key)
                                        .setValue(map_notification);

                                // remove person who has been invited in the list
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                        .child(group_id)
                                        .child(user_id)
                                        .removeValue();
                            }
                        } else {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user_id)
                                    .child(group_id)
                                    .setValue(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_followers))
                                    .child(group_id)
                                    .child(user_id)
                                    .setValue(map);
                            setJoinedGroup(join_group);
                        }
                    }

                    if (!snapshot.exists()) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(group_id)
                                .setValue(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(group_id)
                                .child(user_id)
                                .setValue(map);
                        setJoinedGroup(join_group);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void setJoinedGroup(TextView join_group){
        Log.d(TAG, "setFollowing: updating UI for following this group");
        join_group.setVisibility(View.GONE);
        if (join_group == this.join_group) {
            menu_item.setVisibility(View.VISIBLE);
            delete_suggestion.setVisibility(View.GONE);
        }
    }

    private void setUnFollowing(String admin_master, TextView join_group){
        Log.d(TAG, "setUnFollowing: updating UI for following this user");

        if (admin_master.equals(user_id)) {
            join_group.setVisibility(View.GONE);
            if (join_group == this.join_group) {
                menu_item.setVisibility(View.VISIBLE);
                delete_suggestion.setVisibility(View.GONE);
            }
        }
        else {
            join_group.setVisibility(View.VISIBLE);
            if (join_group == this.join_group) {
                menu_item.setVisibility(View.GONE);
                delete_suggestion.setVisibility(View.VISIBLE);
            }
        }
    }

    private void isJoinedGroup(String admin_master, String group_id, TextView join_group){
        Log.d(TAG, "isFollowing: checking if following this group.");
        setUnFollowing(admin_master, join_group);

        Query query = myRef.child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(group_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setJoinedGroup(join_group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

