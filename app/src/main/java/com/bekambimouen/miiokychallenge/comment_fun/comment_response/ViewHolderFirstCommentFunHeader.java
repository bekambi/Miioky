package com.bekambimouen.miiokychallenge.comment_fun.comment_response;

import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.FullVideo;
import com.bekambimouen.miiokychallenge.model.Comment;
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

public class ViewHolderFirstCommentFunHeader extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderFirstCommentFunHeader";

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
    private final BattleModel_fun model;
    private final RelativeLayout relLayout_view_overlay;
    private final Comment commentModel;
    private final String userID;
    private final String comment;
    private final String media_comment_url;
    private final String media_comment_thumbnail;
    private final String comment_key;
    private final long time;
    private final MyEditText mComment;
    private final ArrayList<String> liker_list;

    private double likes_count = 0;
    private boolean mLikedByCurrentUser;
    private User mCurrentUser;
    private StringBuilder users;

    // firebase
    private final DatabaseReference myRef;

    public ViewHolderFirstCommentFunHeader(AppCompatActivity context, BattleModel_fun model,
                                           Comment commentModel, String userID, String comment, String media_comment_url,
                                           String media_comment_thumbnail, String comment_key,
                                           long time, MyEditText mComment, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.model = model;
        this.commentModel = commentModel;
        this.userID = userID;
        this.comment = comment;
        this.media_comment_url = media_comment_url;
        this.media_comment_thumbnail = media_comment_thumbnail;
        this.comment_key = comment_key;
        this.time = time;
        this.mComment = mComment;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        liker_list = new ArrayList<>();

        cardView = itemView.findViewById(R.id.cardView);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
        likes = itemView.findViewById(R.id.likes);

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
        // show first comment
        if (!TextUtils.isEmpty(comment))
            user_comment.setVisibility(View.VISIBLE);
        user_comment.setCharText(comment);

        if(liker_list != null){
            liker_list.clear();
        }

        getCurrentUser();
        setLikes();
        getLikesString();
        updateLikesString();

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

                    Glide.with(((Activity) context))
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
                            removeLike(model);
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
                                addNewLike(model);
                            }
                        }
                    });
                }
            }
        });
    }

    private void removeLike(BattleModel_fun model) {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_videos))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
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
                        myRef.child(context.getString(R.string.dbname_videos))
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_comments))
                                .child(comment_key)
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_fun))
                                .child(model.getUser_id())
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_comments))
                                .child(comment_key)
                                .child(context.getString(R.string.field_likes))
                                .child(newLikeID)
                                .removeValue();

                        getLikesString();
                        updateLikesString();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike(BattleModel_fun model){
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
        myRef.child(context.getString(R.string.dbname_videos))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_fun))
                .child(model.getUser_id())
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comments))
                .child(comment_key)
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString();
        updateLikesString();
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_videos))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comments))
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

    private void updateLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_videos))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comments))
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

    private void setLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_videos))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_comments))
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
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

