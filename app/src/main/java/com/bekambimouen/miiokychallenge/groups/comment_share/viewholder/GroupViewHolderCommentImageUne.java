package com.bekambimouen.miiokychallenge.groups.comment_share.viewholder;

import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullImage;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.groups.comment_share.GroupResponseCommentShare;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.FullVideo;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupViewHolderCommentImageUne extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderCommentImageUne";

    // widgets
    public final RelativeLayout parent;
    private final RelativeLayout deleted_comment;
    public final ImageView menu_item;
    private final SmallBangView like_heart;
    private final ImageView image;
    private final CircleImageView comment_profile_image;
    private final TextView comment_time_posted;
    private final TextView comment_likes;
    private final RelativeLayout comment_reply;
    private final TextView comment_username;
    private final ViewMoreTextView commentaire;
    private final RelativeLayout likes;

    // first comment
    private final RelativeLayout relLayout_first_comment;
    private final SmallBangView like_heart_first_comment;
    private final ImageView image_first_comment;
    private final CircleImageView comment_profile_image_first_comment;
    private final TextView comment_time_posted_first_comment;
    private final TextView comment_likes_first_comment;
    private final RelativeLayout comment_reply_first_comment;
    private final TextView comment_username_first_comment;
    private final ViewMoreTextView commentaire_first_comment;

    private final CardView cardView;
    private final ImageView thumbnail;
    private final RelativeLayout relLayout_img_play;

    private final CardView cardView_first_comment;
    private final ImageView thumbnail_first_comment;
    private final RelativeLayout relLayout_img_play_first_comment;

    private final RelativeLayout see_comments;
    private final TextView nber_of_comment_comments;

    // vars
    private final AppCompatActivity context;
    private final BattleModel battleModel;
    private final RelativeLayout relLayout_view_overlay;
    private final String from_bottom_sheet;
    private final ArrayList<String> liker_list;
    private final ItemImageUneClickListener itemImageUneClickListener;
    private Comment mComment;
    private String username;
    double likes_count;
    double likes_count_first_comment;
    boolean mLikedByCurrentUser;
    boolean mLikedByCurrentUser_first_comment;
    private User mCurrentUser;
    private String parent_key, child_comment_key;
    private StringBuilder users;
    private StringBuilder users_first_comment;
    private final ArrayList<CommentResponse> commentsResponseList;

    // for notification
    private final String post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
            recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupViewHolderCommentImageUne(AppCompatActivity context, BattleModel battleModel, ItemImageUneClickListener itemImageUneClickListener,
                                          String post_id_field, String category_of_post, String post_type,
                                          String post_id, String recyclerview_photo_id_upload,
                                          String recyclerview_fieldLike_upload, String admin_master, String the_user_who_posted_id,
                                          String group_id, String from_bottom_sheet, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.battleModel = battleModel;
        this.itemImageUneClickListener = itemImageUneClickListener;
        this.post_id_field = post_id_field;
        this.category_of_post = category_of_post;
        this.post_type = post_type;
        this.post_id = post_id;
        this.recyclerview_photo_id_upload = recyclerview_photo_id_upload;
        this.recyclerview_fieldLike_upload = recyclerview_fieldLike_upload;
        this.admin_master = admin_master;
        this.the_user_who_posted_id = the_user_who_posted_id;
        this.group_id = group_id;
        this.from_bottom_sheet = from_bottom_sheet;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        liker_list = new ArrayList<>();

        likes = itemView.findViewById(R.id.likes);
        comment_profile_image = itemView.findViewById(R.id.comment_profile_image);
        comment_username = itemView.findViewById(R.id.comment_username);
        commentaire = itemView.findViewById(R.id.comment);
        comment_time_posted = itemView.findViewById(R.id.comment_time_posted);
        comment_likes = itemView.findViewById(R.id.comment_likes);
        comment_reply = itemView.findViewById(R.id.comment_reply);

        image = itemView.findViewById(R.id.image);
        like_heart = itemView.findViewById(R.id.like_heart);
        parent = itemView.findViewById(R.id.parent);
        menu_item = itemView.findViewById(R.id.menu_item);
        deleted_comment = itemView.findViewById(R.id.deleted_comment);

        // first comment
        relLayout_first_comment = itemView.findViewById(R.id.relLayout_first_comment);
        comment_profile_image_first_comment = itemView.findViewById(R.id.comment_profile_image_first_comment);
        comment_username_first_comment = itemView.findViewById(R.id.comment_username_first_comment);
        commentaire_first_comment = itemView.findViewById(R.id.comment_first_comment);
        comment_time_posted_first_comment = itemView.findViewById(R.id.comment_time_posted_first_comment);
        comment_likes_first_comment = itemView.findViewById(R.id.comment_likes_first_comment);
        comment_reply_first_comment = itemView.findViewById(R.id.comment_reply_first_comment);

        image_first_comment = itemView.findViewById(R.id.image_first_comment);
        like_heart_first_comment = itemView.findViewById(R.id.like_heart_first_comment);

        cardView = itemView.findViewById(R.id.cardView);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);

        cardView_first_comment = itemView.findViewById(R.id.cardView_first_comment);
        thumbnail_first_comment = itemView.findViewById(R.id.thumbnail_first_comment);
        relLayout_img_play_first_comment = itemView.findViewById(R.id.relLayout_img_play_first_comment);

        see_comments = itemView.findViewById(R.id.see_comments);
        nber_of_comment_comments = itemView.findViewById(R.id.nber_of_comment_comments);

        commentsResponseList = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    public void bindView(Comment comment) {
        mComment = comment;

        if(liker_list != null){
            liker_list.clear();
        }

        if (comment.getSuppressed().equals("yes")) {
            parent.setVisibility(View.GONE);
            deleted_comment.setVisibility(View.VISIBLE);
        } else {
            parent.setVisibility(View.VISIBLE);
            deleted_comment.setVisibility(View.GONE);
        }

        getCurrentUser();
        setLikes(comment);
        getLikesString(comment);
        updateLikesString(comment);

        getResponseComments();

        //set the username and profile image
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(comment.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentaire.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                relLayout_img_play.setVisibility(View.GONE);

                for ( DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    username = user.getUsername();
                    comment_username.setText(username);

                    if (!TextUtils.isEmpty(comment.getComment()))
                        commentaire.setVisibility(View.VISIBLE);
                    commentaire.setCharText(Html.fromHtml(comment.getComment()));

                    commentaire.setMovementMethod(LinkMovementMethod.getInstance());

                    if (!comment.getUrl().isEmpty()) {
                        cardView.setVisibility(View.VISIBLE);
                        if (mComment.getThumbnail().isEmpty()) {
                            if (!context.isFinishing()) {
                                Glide.with(context.getApplicationContext())
                                        .asBitmap()
                                        .load(comment.getUrl())
                                        .into(thumbnail);

                            }
                        } else {
                            relLayout_img_play.setVisibility(View.VISIBLE);
                            if (!context.isFinishing()) {
                                Glide.with(context.getApplicationContext())
                                        .asBitmap()
                                        .load(comment.getThumbnail())
                                        .into(thumbnail);
                            }
                        }
                    }

                    Glide.with(((Activity) context))
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(comment_profile_image);

                    thumbnail.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (mComment.getThumbnail().isEmpty()) {
                            intent = new Intent(context, SimpleFullImage.class);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(comment);
                            intent.putExtra("comment", myGson);

                        } else {
                            intent = new Intent(context, FullVideo.class);
                            intent.putExtra("video_url", comment.getUrl());
                        }
                        intent.putExtra("from_bottom_sheet", from_bottom_sheet);
                        context.startActivity(intent);
                    });

                    comment_reply.setOnClickListener(view -> itemImageUneClickListener.onClick(comment.getCommentKey(),
                            comment.getComment(), comment.getUser_id(), comment.getUrl(), comment.getThumbnail(),
                            comment, "", "", comment.getDate_created()));

                    see_comments.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Gson gson = new Gson();
                        String myGson = gson.toJson(battleModel);
                        String myGson2 = gson.toJson(mComment);
                        Intent intent = new Intent(context, GroupResponseCommentShare.class);

                        intent.putExtra("comment_key", comment.getCommentKey());
                        intent.putExtra("comment", comment.getComment());
                        intent.putExtra("userID", comment.getUser_id());
                        intent.putExtra("media_comment_url", comment.getUrl());
                        intent.putExtra("media_comment_thumbnail", comment.getThumbnail());
                        intent.putExtra("time", comment.getDate_created());
                        intent.putExtra("comment_image_une", myGson);
                        intent.putExtra("commentModel", myGson2);
                        intent.putExtra("edittext_clear_focus", "edittext_clear_focus");

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
                        context.startActivity(intent);
                    });

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

        //set the timestamp difference
        long tv_date = comment.getDate_created();
        getTimestampDifference(tv_date, comment_time_posted);

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
    }

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(mComment.getCommentKey())
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
                                .child(battleModel.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(mComment.getCommentKey())
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(battleModel.getGroup_id())
                                .child(battleModel.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(mComment.getCommentKey())
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        getLikesString(mComment);
                        updateLikesString(mComment);

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
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(mComment.getCommentKey())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_group))
                .child(battleModel.getGroup_id())
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(mComment.getCommentKey())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString(mComment);
        updateLikesString(mComment);
    }

    private void getLikesString(Comment comment){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment.getCommentKey())
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

    private void updateLikesString(Comment comment){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment.getCommentKey())
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

    // pour compter le nombre de likes
    public void setLikes(Comment mComment) {
        likes_count = 0;
        comment_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(mComment.getCommentKey())
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
                    comment_likes.setVisibility(View.VISIBLE);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // get First comment
    private void getResponseComments() {
        try {
            Query query1 = myRef.child(context.getString(R.string.dbname_group_media_share))
                    .child(battleModel.getPhoto_id())
                    .child(context.getString(R.string.field_comment_share))
                    .child(mComment.getCommentKey())
                    .child(context.getString(R.string.field_comments));

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    commentsResponseList.clear();
                    relLayout_first_comment.setVisibility(View.GONE);
                    commentaire_first_comment.setVisibility(View.GONE);
                    cardView_first_comment.setVisibility(View.GONE);
                    see_comments.setVisibility(View.GONE);
                    relLayout_img_play_first_comment.setVisibility(View.GONE);

                    for (DataSnapshot ds: snapshot.getChildren()) {
                        CommentResponse commentResponse = new CommentResponse();
                        Map<Object, Object> hashMap = (HashMap<Object, Object>) ds.getValue();

                        assert hashMap != null;
                        commentResponse.setUserAnswer(Boolean.parseBoolean(requireNonNull(hashMap.get(context.getString(R.string.field_userAnswer))).toString()));
                        commentResponse.setUser_id(requireNonNull(hashMap.get(context.getString(R.string.field_user_id))).toString());
                        commentResponse.setComment(requireNonNull(hashMap.get(context.getString(R.string.field_comment))).toString());
                        commentResponse.setCommentParentKey(mComment.getCommentKey());
                        commentResponse.setCommentKey(requireNonNull(hashMap.get(context.getString(R.string.field_commentKey))).toString());
                        commentResponse.setUrl(requireNonNull(hashMap.get(context.getString(R.string.field_url))).toString());
                        commentResponse.setThumbnail(requireNonNull(hashMap.get(context.getString(R.string.field_thumbnail))).toString());
                        try {
                            commentResponse.setAnsweringUsername(requireNonNull(hashMap.get(context.getString(R.string.field_answeringUsername))).toString());
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                        commentResponse.setDate_created(Long.parseLong(requireNonNull(hashMap.get(context.getString(R.string.field_date_created))).toString()));

                        List<Like> likeResponseList = new ArrayList<>();
                        for (DataSnapshot data : ds
                                .child(context.getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(requireNonNull(data.getValue(Like.class)).getUser_id());
                            likeResponseList.add(like);
                        }
                        commentResponse.setLikes(likeResponseList);
                        commentsResponseList.add(commentResponse);

                        if (!commentsResponseList.get(0).getUrl().isEmpty()) {
                            cardView_first_comment.setVisibility(View.VISIBLE);
                            if (commentsResponseList.get(0).getThumbnail().isEmpty()) {
                                if (!context.isFinishing()) {
                                    Glide.with(context.getApplicationContext())
                                            .asBitmap()
                                            .load(commentsResponseList.get(0).getUrl())
                                            .into(thumbnail_first_comment);

                                }
                            } else {
                                relLayout_img_play_first_comment.setVisibility(View.VISIBLE);
                                if (!context.isFinishing()) {
                                    Glide.with(context.getApplicationContext())
                                            .asBitmap()
                                            .load(commentsResponseList.get(0).getThumbnail())
                                            .into(thumbnail_first_comment);
                                }
                            }
                        }
                    }

                    thumbnail_first_comment.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (commentsResponseList.get(0).getThumbnail().isEmpty()) {
                            intent = new Intent(context, SimpleFullImage.class);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(commentsResponseList.get(0));
                            intent.putExtra("comment_response", myGson);

                        } else {
                            intent = new Intent(context, FullVideo.class);
                            intent.putExtra("video_url", commentsResponseList.get(0).getUrl());
                        }
                        intent.putExtra("from_bottom_sheet", from_bottom_sheet);
                        context.startActivity(intent);
                    });

                    // get the first comment username
                    if (!commentsResponseList.isEmpty()) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(commentsResponseList.get(0).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    String userName = user.getUsername();

                                    comment_reply_first_comment.setOnClickListener(view -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(battleModel);
                                        String myGson2 = gson.toJson(mComment);
                                        Intent intent = new Intent(context, GroupResponseCommentShare.class);

                                        intent.putExtra("comment_key", mComment.getCommentKey());
                                        intent.putExtra("comment", mComment.getComment());
                                        intent.putExtra("userID", mComment.getUser_id());
                                        intent.putExtra("media_comment_url", mComment.getUrl());
                                        intent.putExtra("media_comment_thumbnail", mComment.getThumbnail());
                                        intent.putExtra("time", mComment.getDate_created());
                                        intent.putExtra("comment_image_une", myGson);
                                        intent.putExtra("commentModel", myGson2);
                                        intent.putExtra("userName", userName);

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
                                        context.startActivity(intent);
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (commentsResponseList.size() == 1) {
                        parent_key = commentsResponseList.get(0).getCommentParentKey();
                        child_comment_key = commentsResponseList.get(0).getCommentKey();

                        relLayout_first_comment.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(commentsResponseList.get(0).getComment()))
                            commentaire_first_comment.setVisibility(View.VISIBLE);
                        commentaire_first_comment.setCharText(commentsResponseList.get(0).getComment());
                        getTimestampDifference(commentsResponseList.get(0).getDate_created(), comment_time_posted_first_comment);

                        getComment_image_une(commentsResponseList.get(0).getUser_id(),
                                commentsResponseList.get(0).getCommentParentKey(), commentsResponseList.get(0).getCommentKey());

                        setLikes_first_comment(parent_key, child_comment_key);
                        getLikesString_first_comment(parent_key, child_comment_key);
                        updateLikesString_first_comment(parent_key, child_comment_key);

                    } else if (commentsResponseList.size() == 2) {
                        parent_key = commentsResponseList.get(0).getCommentParentKey();
                        child_comment_key = commentsResponseList.get(0).getCommentKey();

                        relLayout_first_comment.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(commentsResponseList.get(0).getComment()))
                            commentaire_first_comment.setVisibility(View.VISIBLE);
                        commentaire_first_comment.setCharText(commentsResponseList.get(0).getComment());
                        getTimestampDifference(commentsResponseList.get(0).getDate_created(), comment_time_posted_first_comment);
                        nber_of_comment_comments.setText(String.valueOf(commentsResponseList.size()));
                        see_comments.setVisibility(View.VISIBLE);

                        getComment_image_une(commentsResponseList.get(0).getUser_id(),
                                commentsResponseList.get(0).getCommentParentKey(), commentsResponseList.get(0).getCommentKey());

                        setLikes_first_comment(parent_key, child_comment_key);
                        getLikesString_first_comment(parent_key, child_comment_key);
                        updateLikesString_first_comment(parent_key, child_comment_key);

                    } else if (commentsResponseList.size() > 2) {
                        parent_key = commentsResponseList.get(0).getCommentParentKey();
                        child_comment_key = commentsResponseList.get(0).getCommentKey();

                        relLayout_first_comment.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(commentsResponseList.get(0).getComment()))
                            commentaire_first_comment.setVisibility(View.VISIBLE);
                        commentaire_first_comment.setCharText(commentsResponseList.get(0).getComment());
                        getTimestampDifference(commentsResponseList.get(0).getDate_created(), comment_time_posted_first_comment);
                        nber_of_comment_comments.setText(String.valueOf(commentsResponseList.size()));
                        see_comments.setVisibility(View.VISIBLE);

                        getComment_image_une(commentsResponseList.get(0).getUser_id(),
                                commentsResponseList.get(0).getCommentParentKey(), commentsResponseList.get(0).getCommentKey());

                        setLikes_first_comment(parent_key, child_comment_key);
                        getLikesString_first_comment(parent_key, child_comment_key);
                        updateLikesString_first_comment(parent_key, child_comment_key);
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

    private void getComment_image_une(String user_id, String parent_key, String child_comment_key) {
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    comment_username_first_comment.setText(user.getUsername());

                    Glide.with(((Activity) context))
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(comment_profile_image_first_comment);

                    comment_profile_image_first_comment.setOnClickListener(v -> {
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

                    comment_username_first_comment.setOnClickListener(v -> {
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

        like_heart_first_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart_first_comment.isSelected()) {
                    like_heart_first_comment.setSelected(false);
                    image_first_comment.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    like_heart_first_comment.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeLike_first_comment(parent_key, child_comment_key);
                        }
                    });

                } else {
                    like_heart_first_comment.setSelected(true);
                    image_first_comment.setImageResource(R.drawable.ic_heart_red);
                    like_heart_first_comment.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mLikedByCurrentUser_first_comment) {
                                addNewLike_first_comment(parent_key, child_comment_key);
                            }
                        }
                    });
                }
            }
        });
    }

    private void removeLike_first_comment(String parent_key, String child_comment_key) {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(parent_key)
                .child(context.getString(R.string.field_comments))
                .child(child_comment_key)
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    //case1: Then user already liked the photo
                    String newLikeID = ds.getKey();
                    if (mLikedByCurrentUser_first_comment &&
                            requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                            .getUid())) {

                        // update like count
                        int count = Integer.parseInt(comment_likes_first_comment.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            comment_likes_first_comment.setVisibility(View.GONE);
                        comment_likes_first_comment.setText(str);

                        // remove like point
                        removeLikePoints();

                        // remove like
                        assert newLikeID != null;
                        myRef.child(context.getString(R.string.dbname_group_media_share))
                                .child(battleModel.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(parent_key)
                                .child(context.getString(R.string.field_comments))
                                .child(child_comment_key)
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(battleModel.getGroup_id())
                                .child(battleModel.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(parent_key)
                                .child(context.getString(R.string.field_comments))
                                .child(child_comment_key)
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        getLikesString_first_comment(parent_key, child_comment_key);
                        updateLikesString_first_comment(parent_key, child_comment_key);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike_first_comment(String parent_key, String child_comment_key){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(comment_likes_first_comment.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            comment_likes_first_comment.setVisibility(View.VISIBLE);
        comment_likes_first_comment.setText(str);

        // add like point
        addLikePoints();

        // add new like
        Like like = new Like();
        like.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        String newLikeID = myRef.push().getKey();

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(parent_key)
                .child(context.getString(R.string.field_comments))
                .child(child_comment_key)
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_group))
                .child(battleModel.getGroup_id())
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(parent_key)
                .child(context.getString(R.string.field_comments))
                .child(child_comment_key)
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString_first_comment(parent_key, child_comment_key);
        updateLikesString_first_comment(parent_key, child_comment_key);
    }

    private void getLikesString_first_comment(String parent_key, String child_comment_key){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(parent_key)
                .child(context.getString(R.string.field_comments))
                .child(child_comment_key)
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users_first_comment = new StringBuilder();
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

                                users_first_comment.append(user.getFullName());
                                users_first_comment.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser_first_comment = users_first_comment.toString().contains(mCurrentUser.getFullName());

                            setupWidgets_first_comment();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser_first_comment = false;
                    setupWidgets_first_comment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLikesString_first_comment(String parent_key, String child_comment_key){
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(parent_key)
                .child(context.getString(R.string.field_comments))
                .child(child_comment_key)
                .child(context.getString(R.string.field_likes));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users_first_comment = new StringBuilder();
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

                                users_first_comment.append(user.getFullName());
                                users_first_comment.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser_first_comment = users_first_comment.toString().contains(mCurrentUser.getFullName());

                            setupWidgets_first_comment();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser_first_comment = false;
                    setupWidgets_first_comment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupWidgets_first_comment() {
        if (mLikedByCurrentUser_first_comment) {
            if (!like_heart_first_comment.isSelected()) {
                like_heart_first_comment.setSelected(true);
                image_first_comment.setImageResource(R.drawable.ic_heart_red);
            }

        } else {
            if (like_heart_first_comment.isSelected()) {
                like_heart_first_comment.setSelected(false);
                image_first_comment.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    private void setLikes_first_comment(String parent_key, String child_comment_key) {
        likes_count_first_comment = 0;
        comment_likes_first_comment.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(parent_key)
                .child(context.getString(R.string.field_comments))
                .child(child_comment_key)
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: like: "+ds.getValue(Like.class));
                    likes_count_first_comment++;
                }

                if (likes_count_first_comment >= 1)
                    comment_likes_first_comment.setVisibility(View.VISIBLE);

                double count;
                if (likes_count_first_comment >= 1000) {
                    count = likes_count_first_comment/1000;

                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                    comment_likes_first_comment.setText(tv_count);

                } else {
                    comment_likes_first_comment.setText(String.valueOf((int) likes_count_first_comment));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // add post points
    private void addLikePoints() {
        if (battleModel.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(battleModel.getGroup_id());

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
                    .equalTo(battleModel.getGroup_id());

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
                                .child(battleModel.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(battleModel.getGroup_id())
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
        if (battleModel.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(battleModel.getGroup_id());

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
                    .equalTo(battleModel.getGroup_id());

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
                                .child(battleModel.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(battleModel.getGroup_id())
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

