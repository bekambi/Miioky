package com.bekambimouen.miiokychallenge.groups.comment_share.comment_response;

import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullImage;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.FullVideo;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupViewHolderFirstCommentShareHeader extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderFirstCommentHeader";

    // widgets
    private final SmallBangView like_heart;
    private final ImageView image;
    private final CircleImageView comment_profile_image;
    private final TextView comment_username;
    private final TextView comment_time_posted;
    private final TextView comment_likes;
    private final ViewMoreTextView user_comment;
    private final RelativeLayout likes;

    private final CardView cardView;
    private final ImageView thumbnail;
    private final RelativeLayout relLayout_img_play;

    // vars
    private final AppCompatActivity context;
    private final BattleModel model;
    private final RelativeLayout relLayout_view_overlay;
    private final BattleModel comment_recycler_child;
    private final BattleModel comment_reponse_image_double;
    private final Comment commentModel;
    private final ArrayList<String> liker_list;
    private final String userID;
    private final String comment;
    private final String media_comment_url;
    private final String media_comment_thumbnail;
    private final String comment_key;
    private final String recyclerview_photo_id;
    private final String recyclerview_fieldLike;
    private final String photo_id;
    private final long time;
    private final MyEditText mComment;

    private double likes_count = 0;
    private boolean mLikedByCurrentUser;
    private User mCurrentUser;
    private StringBuilder users;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupViewHolderFirstCommentShareHeader(AppCompatActivity context, BattleModel model, BattleModel comment_recycler_child,
                                                  BattleModel comment_reponse_image_double,
                                                  Comment commentModel, String userID, String comment, String media_comment_url, String media_comment_thumbnail,
                                                  String comment_key, String recyclerview_photo_id, String recyclerview_fieldLike, String photo_id,
                                                  long time, MyEditText mComment, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.model = model;
        this.comment_recycler_child = comment_recycler_child;
        this.comment_reponse_image_double = comment_reponse_image_double;
        this.commentModel = commentModel;
        this.userID = userID;
        this.comment = comment;
        this.media_comment_url = media_comment_url;
        this.media_comment_thumbnail = media_comment_thumbnail;
        this.comment_key = comment_key;
        this.recyclerview_photo_id = recyclerview_photo_id;
        this.recyclerview_fieldLike = recyclerview_fieldLike;
        this.photo_id = photo_id;
        this.time = time;
        this.mComment = mComment;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        liker_list = new ArrayList<>();

        likes = itemView.findViewById(R.id.likes);
        cardView = itemView.findViewById(R.id.cardView);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);

        like_heart = itemView.findViewById(R.id.like_heart);
        image = itemView.findViewById(R.id.image);
        comment_profile_image = itemView.findViewById(R.id.comment_profile_image);
        comment_username = itemView.findViewById(R.id.comment_username);
        comment_time_posted = itemView.findViewById(R.id.comment_time_posted);
        RelativeLayout comment_reply = itemView.findViewById(R.id.comment_reply);
        comment_likes = itemView.findViewById(R.id.comment_likes);
        user_comment = itemView.findViewById(R.id.comment);

        getComment();

        // for answer user Republier comment
        comment_reply.setOnClickListener(view -> {
            showKeyboard();
            this.mComment.requestFocus();
        });
    }

    private void getComment() {

        if(liker_list != null){
            liker_list.clear();
        }

        user_comment.setVisibility(View.GONE);
        if (comment_recycler_child != null) {
            getCurrentUser();
            setLikes_recyclerview_child(photo_id);
            getLikesString_recyclerview_child(photo_id);
            updateLikesString_recyclerview_child(photo_id);

        } else {
            getCurrentUser();
            setLikes(photo_id);
            getLikesString(photo_id);
            updateLikesString(photo_id);
        }

        // show first comment
        if (!TextUtils.isEmpty(comment))
            user_comment.setVisibility(View.VISIBLE);
        user_comment.setCharText(comment);

        getTimestampDifference(time, comment_time_posted);

        if (!media_comment_url.isEmpty()) {
            cardView.setVisibility(View.VISIBLE);
            if (media_comment_thumbnail.isEmpty()) {
                relLayout_img_play.setVisibility(View.GONE);

                if (!context.isFinishing()) {
                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(media_comment_url)
                            .into(thumbnail);

                }
            } else {
                if (!context.isFinishing()) {
                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(media_comment_thumbnail)
                            .into(thumbnail);
                }
            }
        }

        thumbnail.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent;
            if (media_comment_thumbnail.isEmpty()) {
                intent = new Intent(context, SimpleFullImage.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(commentModel);
                intent.putExtra("comment", myGson);

            } else {
                intent = new Intent(context, FullVideo.class);
                intent.putExtra("video_url", media_comment_url);
            }
            context.startActivity(intent);
        });

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

                    comment_username.setText(user.getUsername());

                    Glide.with((context.getApplicationContext()))
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(comment_profile_image);

                    comment_profile_image.setOnClickListener(v -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            intent = new Intent(context, Profile.class);

                        } else {
                            intent = new Intent(context, ViewProfile.class);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user);
                            intent.putExtra("user", myJson);
                        }
                        context.startActivity(intent);
                    });

                    comment_username.setOnClickListener(v -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            intent = new Intent(context, Profile.class);

                        } else {
                            intent = new Intent(context, ViewProfile.class);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user);
                            intent.putExtra("user", myJson);
                        }
                        context.startActivity(intent);
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

        like_heart.setOnClickListener(new View.OnClickListener() {
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
                            if (comment_recycler_child != null)
                                removeLike_recyclerview_child(comment_recycler_child, photo_id);
                            else
                                removeLike(model, photo_id);
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
                                if (comment_recycler_child != null)
                                    addNewLike_recyclerview_child(comment_recycler_child, photo_id);
                                else
                                    addNewLike(model, photo_id);
                            }
                        }
                    });
                }
            }
        });
    }

    private void removeLike(BattleModel model, String photo_id) {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    //case1: Then user already liked the photo
                    String newLikeID = ds.getKey();
                    if (mLikedByCurrentUser &&
                            requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                            .getUid())) {

                        // update like count
                        int count = Integer.parseInt(comment_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            comment_likes.setVisibility(View.GONE);
                        comment_likes.setText(str);

                        // remove like point
                        removeLikePoints();

                        // remove like
                        assert newLikeID != null;
                        myRef.child(context.getString(R.string.dbname_group_media_share))
                                .child(photo_id)
                                .child(context.getString(R.string.field_comment_share))
                                .child(comment_key)
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        if (comment_reponse_image_double != null) {
                            myRef.child(context.getString(R.string.dbname_group))
                                    .child(model.getGroup_id())
                                    .child(photo_id)
                                    .child(context.getString(R.string.field_comment_share))
                                    .child(comment_key)
                                    .child(context.getString(R.string.field_likes))
                                    .child(newLikeID)
                                    .removeValue();

                        } else {
                            myRef.child(context.getString(R.string.dbname_group))
                                    .child(model.getGroup_id())
                                    .child(photo_id)
                                    .child(context.getString(R.string.field_comment_share))
                                    .child(comment_key)
                                    .child(context.getString(R.string.field_likes))
                                    .child(newLikeID)
                                    .removeValue();
                        }

                        getLikesString(photo_id);
                        updateLikesString(photo_id);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike(BattleModel model, String photo_id){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(comment_likes.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            comment_likes.setVisibility(View.VISIBLE);
        comment_likes.setText(str);

        // add like point
        addLikePoints();

        // add new like
        Like like = new Like();
        like.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        String newLikeID = myRef.push().getKey();

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);


        if (comment_reponse_image_double != null) {
            myRef.child(context.getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(photo_id)
                    .child(context.getString(R.string.field_comment_share))
                    .child(comment_key)
                    .child(context.getString(R.string.field_likes))
                    .child(newLikeID)
                    .setValue(like);

        } else {
            myRef.child(context.getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(photo_id)
                    .child(context.getString(R.string.field_comment_share))
                    .child(comment_key)
                    .child(context.getString(R.string.field_likes))
                    .child(newLikeID)
                    .setValue(like);
        }

        // affichage à l'écran
        getLikesString(photo_id);
        updateLikesString(photo_id);
    }

    private void getLikesString(String photo_id){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = new StringBuilder();
                for (DataSnapshot ds: snapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getFullName());

                                users.append(user.getFullName());
                                users.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = users.toString().contains(mCurrentUser.getFullName());

                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLikesString(String photo_id){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_likes));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = new StringBuilder();
                for (DataSnapshot ds: snapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getFullName());

                                users.append(user.getFullName());
                                users.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = users.toString().contains(mCurrentUser.getFullName());

                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // recyclerView child like _____________________________________________________________________
    private void removeLike_recyclerview_child(BattleModel model, String photo_id) {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_child_comments_share))
                .child(recyclerview_photo_id)
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(recyclerview_fieldLike);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    //case1: Then user already liked the photo
                    String newLikeID = ds.getKey();
                    if (mLikedByCurrentUser &&
                            requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                            .getUid())) {

                        // update like count
                        int count = Integer.parseInt(comment_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            comment_likes.setVisibility(View.GONE);
                        comment_likes.setText(str);

                        // remove like point
                        removeLikePoints();

                        // remove like
                        assert newLikeID != null;
                        myRef.child(context.getString(R.string.dbname_group_media_share))
                                .child(photo_id)
                                .child(context.getString(R.string.field_child_comments_share))
                                .child(recyclerview_photo_id)
                                .child(context.getString(R.string.field_comments))
                                .child(comment_key)
                                .child(recyclerview_fieldLike)
                                .child(newLikeID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(model.getGroup_id())
                                .child(photo_id)
                                .child(context.getString(R.string.field_child_comments_share))
                                .child(recyclerview_photo_id)
                                .child(context.getString(R.string.field_comments))
                                .child(comment_key)
                                .child(recyclerview_fieldLike)
                                .child(newLikeID)
                                .removeValue();

                        getLikesString_recyclerview_child(photo_id);
                        updateLikesString_recyclerview_child(photo_id);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike_recyclerview_child(BattleModel model, String photo_id){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(comment_likes.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            comment_likes.setVisibility(View.VISIBLE);
        comment_likes.setText(str);

        // add like point
        addLikePoints();

        // add new like
        Like like = new Like();
        like.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        String newLikeID = myRef.push().getKey();

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_child_comments_share))
                .child(recyclerview_photo_id)
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(recyclerview_fieldLike)
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_group))
                .child(model.getGroup_id())
                .child(photo_id)
                .child(context.getString(R.string.field_child_comments_share))
                .child(recyclerview_photo_id)
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(recyclerview_fieldLike)
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString_recyclerview_child(photo_id);
        updateLikesString_recyclerview_child(photo_id);
    }

    private void getLikesString_recyclerview_child(String photo_id){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_child_comments_share))
                .child(recyclerview_photo_id)
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(recyclerview_fieldLike);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = new StringBuilder();
                for (DataSnapshot ds: snapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getFullName());

                                users.append(user.getFullName());
                                users.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = users.toString().contains(mCurrentUser.getFullName());

                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLikesString_recyclerview_child(String photo_id){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_child_comments_share))
                .child(recyclerview_photo_id)
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(recyclerview_fieldLike);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = new StringBuilder();
                for (DataSnapshot ds: snapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getFullName());

                                users.append(user.getFullName());
                                users.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = users.toString().contains(mCurrentUser.getFullName());

                            setupWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setLikes_recyclerview_child(String photo_id) {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_child_comments_share))
                .child(recyclerview_photo_id)
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(recyclerview_fieldLike);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(ds.child(context.getString(R.string.field_user_id)).getValue().toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    likes.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Likers.class);
                        intent.putStringArrayListExtra("liker_list", liker_list);
                        context.startActivity(intent);
                    });
                }

                double count;
                if (likes_count >= 1000) {
                    count = likes_count/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    comment_likes.setText(tv_count);

                } else {
                    comment_likes.setText(String.valueOf((int) likes_count));
                }

                if (comment_likes.getText().toString().equals("0"))
                    comment_likes.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getCurrentUser(){
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

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

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupWidgets() {
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

    private void setLikes(String photo_id) {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    likes.setOnClickListener(view -> {
                        Intent intent = new Intent(context, Likers.class);
                        intent.putStringArrayListExtra("liker_list", liker_list);
                        context.startActivity(intent);
                    });
                }

                double count;
                if (likes_count >= 1000) {
                    count = likes_count/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    comment_likes.setText(tv_count);

                } else {
                    comment_likes.setText(String.valueOf((int) likes_count));
                }

                if (comment_likes.getText().toString().equals("0"))
                    comment_likes.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // add post points
    private void addLikePoints() {
        if (model.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model.getGroup_id());

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
                    .equalTo(model.getGroup_id());

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
                                .child(model.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(model.getGroup_id())
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
        if (model.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model.getGroup_id());

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
                    .equalTo(model.getGroup_id());

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
                                .child(model.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(model.getGroup_id())
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

    /**
     * Returns a string representing the number of days ago the post was made
     */
    @SuppressLint("SetTextI18n")
    private void getTimestampDifference(long date, TextView tvDate){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        String tps;
        long time = (System.currentTimeMillis() - date);
        if (time >= 2*3600*24000) {
            tps = date_passe;
        } else if (time >= 24*3600000) {
            tps = context.getString(R.string.yesterday);
        } else if (time >= 2*3600000) {
            tps = ((int)(time/(3600000)))+" "+context.getString(R.string.hours_ago);
        } else if (time >= 3600000) {
            tps = context.getString(R.string.an_hour_ago);
        } else if (time >= 120000) {
            tps = (int)(time/60000) + " "+context.getString(R.string.minutes_ago);
        } else {
            tps = context.getString(R.string.just_now);
        }
        tvDate.setText(tps);
    }
}

