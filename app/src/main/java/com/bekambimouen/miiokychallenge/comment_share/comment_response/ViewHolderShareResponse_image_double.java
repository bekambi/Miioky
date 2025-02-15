package com.bekambimouen.miiokychallenge.comment_share.comment_response;

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
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.FullVideo;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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

public class ViewHolderShareResponse_image_double extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderShareResponse_image_double";

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

    private final CardView cardView;
    private final ImageView thumbnail;
    private final RelativeLayout relLayout_img_play;
    private final RelativeLayout likes;

    // vars
    private CommentResponse mComment;
    private final ArrayList<String> liker_list;
    double likes_count = 0;
    boolean mLikedByCurrentUser;
    private User mCurrentUser;
    private StringBuilder users;
    private String username;

    // vars
    private final AppCompatActivity context;
    private final BattleModel model;
    private final RelativeLayout relLayout_view_overlay;
    private final String comment_key;
    private final ChildItemClickListener mChildItemClickListener;

    // firebase
    private final DatabaseReference myRef;

    public ViewHolderShareResponse_image_double(AppCompatActivity context, BattleModel model, String comment_key,
                                                ChildItemClickListener mChildItemClickListener,
                                                RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.model = model;
        this.comment_key = comment_key;
        this.mChildItemClickListener = mChildItemClickListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        liker_list = new ArrayList<>();

        comment_profile_image = itemView.findViewById(R.id.comment_profile_image);
        comment_username = itemView.findViewById(R.id.comment_username);
        commentaire = itemView.findViewById(R.id.comment);

        comment_time_posted = itemView.findViewById(R.id.comment_time_posted);
        comment_likes = itemView.findViewById(R.id.comment_likes);
        comment_reply = itemView.findViewById(R.id.comment_reply);

        cardView = itemView.findViewById(R.id.cardView);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
        likes = itemView.findViewById(R.id.likes);

        image = itemView.findViewById(R.id.image);
        like_heart = itemView.findViewById(R.id.like_heart);
        parent = itemView.findViewById(R.id.parent);
        menu_item = itemView.findViewById(R.id.menu_item);
        deleted_comment = itemView.findViewById(R.id.deleted_comment);
    }

    @SuppressLint("SetTextI18n")
    public void bindView(CommentResponse comment) {
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

        getCurrentUser(comment);
        getLikesString(comment);
        updateLikesString(comment);
        setLikes(comment);

        //set the username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(comment.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentaire.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                relLayout_img_play.setVisibility(View.GONE);

                for ( DataSnapshot ds:  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user_reponse = Util_User.getUser(context, objectMap, ds);

                    username = user_reponse.getUsername();
                    comment_username.setText(username);

                    if (!TextUtils.isEmpty(comment.getComment()))
                        commentaire.setVisibility(View.VISIBLE);
                    if (comment.isUserAnswer()) {
                        commentaire.setCharText(Html.fromHtml(context.getString(R.string.in_response_to)
                                +"<b>" + comment.getAnsweringUsername() + "</b>  "+comment.getComment()));
                    } else {
                        commentaire.setCharText(Html.fromHtml(comment.getComment()));
                    }

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
                            .load(user_reponse.getProfileUrl())
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
                            intent.putExtra("comment_response", myGson);

                        } else {
                            intent = new Intent(context, FullVideo.class);
                            intent.putExtra("video_url", comment.getUrl());
                        }
                        context.startActivity(intent);
                    });

                    comment_reply.setOnClickListener(view -> mChildItemClickListener.onClick(getLayoutPosition(), username));

                    comment_profile_image.setOnClickListener(v -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user_reponse.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            intent = new Intent(context, Profile.class);

                        } else {
                            intent = new Intent(context, ViewProfile.class);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user_reponse);
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
                        if (user_reponse.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            intent = new Intent(context, Profile.class);

                        } else {
                            intent = new Intent(context, ViewProfile.class);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user_reponse);
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
                                addNewLike(comment);
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
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
                .child(mComment.getCommentKey())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    //case1: Then user already liked the photo
                    String newLikeID = ds.getKey();
                    if (mLikedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                            .getUid())) {

                        // update like count
                        int count = Integer.parseInt(comment_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            comment_likes.setVisibility(View.GONE);
                        comment_likes.setText(str);

                        // remove like
                        assert newLikeID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(comment_key)
                                .child(context.getString(R.string.field_comments))
                                .child(mComment.getCommentKey())
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(model.getUser_id())
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(comment_key)
                                .child(context.getString(R.string.field_comments))
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

    public void addNewLike(CommentResponse comment){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(comment_likes.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            comment_likes.setVisibility(View.VISIBLE);
        comment_likes.setText(str);

        // add new like
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        String newLikeID = myRef.push().getKey();

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
                .child(comment.getCommentKey())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(model.getUser_id())
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
                .child(comment.getCommentKey())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString(comment);
        updateLikesString(comment);
    }

    private void getLikesString(CommentResponse comment){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
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
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

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

    private void updateLikesString(CommentResponse comment){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
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
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

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

    private void getCurrentUser(CommentResponse comment){
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

                try {
                    getLikesString(comment);
                    updateLikesString(comment);
                } catch (NullPointerException e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
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
    public void setLikes(CommentResponse mComment) {
        likes_count = 0;
        comment_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comment_share))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
                .child(mComment.getCommentKey())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: like: "+ds.getValue(Like.class));
                    // add user id to the list
                    liker_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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

