package com.bekambimouen.miiokychallenge.market_place.comment.store_comment.coment_response;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullImage;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.messages.FullVideo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreViewHolderResponse extends RecyclerView.ViewHolder {
    private static final String TAG = "MarketViewHolderResponse";

    // widgets
    private final SmallBangView like_heart;
    private final ImageView image;
    private final CircleImageView comment_profile_image;
    private final TextView comment_time_posted;
    private final TextView comment_likes;
    private final RelativeLayout comment_reply;
    private final TextView comment_username;
    private final ViewMoreTextView commentaire;
    private final RelativeLayout likes;

    private final CardView cardView;
    private final ImageView thumbnail;
    private final RelativeLayout relLayout_img_play;

    // vars
    private final AppCompatActivity context;
    private final MarketModel marketModel;
    private final RelativeLayout relLayout_view_overlay;
    private final String comment_key;
    private final ChildItemClickListener mChildItemClickListener;
    private CommentResponse mComment;
    double likes_count = 0;
    boolean mLikedByCurrentUser;
    private User mCurrentUser;
    private StringBuilder users;
    private final ArrayList<String> liker_list;

    // firebase
    private final DatabaseReference myRef;

    public StoreViewHolderResponse(AppCompatActivity context, MarketModel marketModel, String comment_key,
                                   ChildItemClickListener mChildItemClickListener, RelativeLayout relLayout_view_overlay,
                                   @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.marketModel = marketModel;
        this.comment_key = comment_key;
        this.mChildItemClickListener = mChildItemClickListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();

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

        liker_list = new ArrayList<>();

        image = itemView.findViewById(R.id.image);
        like_heart = itemView.findViewById(R.id.like_heart);
    }

    @SuppressLint("SetTextI18n")
    public void bindView(CommentResponse comment) {
        mComment = comment;

        getCurrentUser(comment);
        getLikesString(comment);
        updateLikesString(comment);
        setLikes(comment);

        if(liker_list != null){
            liker_list.clear();
        }

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

                for ( DataSnapshot ds:  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    // permet d'aligner deux ou ++ texte
                    comment_username.setText(user.getUsername());

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

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(comment_profile_image);

                    thumbnail.setOnClickListener(view -> {
                        closeKeyboard();
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

                    comment_reply.setOnClickListener(view -> mChildItemClickListener.onClick(getLayoutPosition(), user.getUsername()));

                    comment_profile_image.setOnClickListener(v -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        closeKeyboard();
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
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
                        closeKeyboard();
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
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
        comment_time_posted.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

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
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(marketModel.getStore_id())
                .child(context.getString(R.string.field_comments))
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
                        myRef.child(context.getString(R.string.dbname_user_stores_media))
                                .child(marketModel.getStore_id())
                                .child(context.getString(R.string.field_comments))
                                .child(comment_key)
                                .child(context.getString(R.string.field_comments))
                                .child(mComment.getCommentKey())
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_user_stores))
                                .child(marketModel.getStore_owner())
                                .child(marketModel.getStore_id())
                                .child(context.getString(R.string.field_comments))
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
        myRef.child(context.getString(R.string.dbname_user_stores_media))
                .child(marketModel.getStore_id())
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
                .child(comment.getCommentKey())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_user_stores))
                .child(marketModel.getStore_owner())
                .child(marketModel.getStore_id())
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(context.getString(R.string.field_comments))
                .child(comment.getCommentKey())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage Ã  l'Ã©cran
        getLikesString(comment);
        updateLikesString(comment);
    }

    private void getLikesString(CommentResponse comment){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(marketModel.getStore_id())
                .child(context.getString(R.string.field_comments))
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(marketModel.getStore_id())
                .child(context.getString(R.string.field_comments))
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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
    private void setLikes(CommentResponse mComment) {
        likes_count = 0;
        comment_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(marketModel.getStore_id())
                .child(context.getString(R.string.field_comments))
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

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

