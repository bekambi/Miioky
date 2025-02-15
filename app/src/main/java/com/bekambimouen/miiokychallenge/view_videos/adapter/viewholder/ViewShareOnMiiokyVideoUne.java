package com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder;

import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.comment_share.ViewCommentShare;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Crushers;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.toro.ToroPlayer;
import com.bekambimouen.miiokychallenge.toro.ToroUtil;
import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewShareOnMiiokyVideoUne extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener,
        Player.Listener, CacheListener {
    private static final String TAG = "VideoViewHolder";

    // widgets
    private final ImageView conner_big_star_icon;
    private final InstaLikeView insta_star_view;
    private final SmallBangView like_star;
    private final ImageView unlike_star_image;
    private final SmallBangView like_heart;
    private final InstaLikeView mInstaLikeView;
    private final ImageView image;
    private final ImageView thumbnail;
    public final ImageView menu_item;
    private final TextView nber_of_crush;
    private final TextView crush_msg;
    private final ProgressBar progressBar;

    private final TextView number_of_likes;
    private final TextView number_of_comments;
    private final TextView number_of_share;
    private final LinearLayout linLayout_like;
    private final LinearLayout linLayout_comment;
    private final LinearLayout linLayout_share;

    // top single
    private final CircleImageView sharing_profile_photo;
    private final View sharing_view_online;
    private final TextView sharing_username;
    private final TextView sharing_tps_publication;
    private final ViewMoreTextView sharing_caption;
    private final TextView sharing_translate_comment;
    private final RelativeLayout sharing_relLayout_username, relLayout_follow;

    // bottom single
    private final CircleImageView profile_photo_shared;
    private final View view_online_shared;
    private final TextView username_shared;
    private final TextView tps_publication;
    private final ViewMoreTextView caption;
    private final TextView translate_comment;
    private final RelativeLayout relLayout_username_shared, relLayout_follow_shared;

    // vars
    private final AppCompatActivity context;
    private final View parent;
    private BottomSheetSharePublication bottomSheetShare;
    private BattleModel video;
    private final RelativeLayout relLayout_view_overlay;
    private User mCurrentUser;
    private final ArrayList<String> liker_list, crusher_list;
    private boolean mLikedByCurrentUser;
    private boolean mCrushedByCurrentUser;
    private StringBuilder mUsers;
    private StringBuilder updateLikeUsers;
    private StringBuilder mUsersCrush;
    private StringBuilder updateCrushUsers;
    private boolean isPlaying = true;
    private int crush_count = 0;
    private int likes_count = 0;
    private int comments_count = 0;
    private int shares_count = 0;

    public final RelativeLayout rel_vol;
    private final GestureDetector detector_video;
    private final RelativeLayout relLayout_img_play;
    public final ImageView img_vol;
    public Player player;
    private final PlayerView playerView;
    private Uri mediaUri;

    private final TextView positionTextView;
    private final TextView durationTextView;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;

    // firebase
    private final DatabaseReference myRef;
    private final FirebaseDatabase database;
    private final String user_id;

    @SuppressLint("ClickableViewAccessibility")
    public ViewShareOnMiiokyVideoUne(AppCompatActivity context, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.parent = itemView;
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        liker_list = new ArrayList<>();
        crusher_list = new ArrayList<>();

        playerView = itemView.findViewById(R.id.playerView);
        positionTextView = playerView.findViewById(R.id.exo_position);
        durationTextView = playerView.findViewById(R.id.exo_duration);
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        thumbnail = itemView.findViewById(R.id.thumbnail);
        rel_vol = itemView.findViewById(R.id.rel_vol);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
        img_vol = itemView.findViewById(R.id.img_vol);

        image = itemView.findViewById(R.id.image);
        like_heart = itemView.findViewById(R.id.like_heart);
        mInstaLikeView = itemView.findViewById(R.id.insta_like_view);
        progressBar = itemView.findViewById(R.id.progressBar);
        menu_item = itemView.findViewById(R.id.menu_item);

        number_of_likes = itemView.findViewById(R.id.number_of_likes);
        number_of_comments = itemView.findViewById(R.id.number_of_comments);
        number_of_share = itemView.findViewById(R.id.number_of_share);
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

        // single top
        sharing_profile_photo = itemView.findViewById(R.id.sharing_profile_photo);
        sharing_view_online = itemView.findViewById(R.id.sharing_view_online);
        sharing_username = itemView.findViewById(R.id.sharing_username);
        sharing_caption = itemView.findViewById(R.id.sharing_caption);
        sharing_translate_comment = itemView.findViewById(R.id.sharing_translate_comment);
        sharing_tps_publication = itemView.findViewById(R.id.sharing_tps_publication);
        sharing_relLayout_username = itemView.findViewById(R.id.sharing_relLayout_username);
        relLayout_follow = itemView.findViewById(R.id.relLayout_follow);

        // single bottom
        profile_photo_shared = itemView.findViewById(R.id.profile_photo_shared);
        view_online_shared = itemView.findViewById(R.id.view_online_shared);
        username_shared = itemView.findViewById(R.id.username_shared);
        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        relLayout_username_shared = itemView.findViewById(R.id.relLayout_username_shared);
        relLayout_follow_shared = itemView.findViewById(R.id.relLayout_follow_shared);

        // Play/Pause video
        detector_video = new GestureDetector(context, new GestureListenerPausePlay());
        playerView.setOnTouchListener((view, motionEvent) -> detector_video.onTouchEvent(motionEvent));
    }

    private final class GestureListenerPausePlay extends GestureDetector.SimpleOnGestureListener {
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
            if (isPlaying)  {
                isPlaying = false;
                if (player != null && player.isPlaying()) {
                    player.pause();
                }
                thumbnail.setVisibility(View.GONE);
                relLayout_img_play.setVisibility(View.VISIBLE);
            } else {
                isPlaying = true;
                if (player != null) {
                    player.play();
                }
                relLayout_img_play.setVisibility(View.GONE);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Log.d(TAG, "onDoubleTap: single tap detected.");
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

    public void bindVideo(BattleModel model) {
        video = model;
        if (model.getUser_id().equals(user_id))
            relLayout_follow.setVisibility(View.GONE);
        if (model.getUser_id_sharing().equals(user_id))
            relLayout_follow_shared.setVisibility(View.GONE);

        sharing_caption.setCharText("");
        caption.setCharText("");
        nber_of_crush.setText("0");
        number_of_likes.setText("0");
        number_of_comments.setText("0");
        number_of_share.setText("0");
        translate_comment.setVisibility(View.GONE);
        sharing_translate_comment.setVisibility(View.GONE);

        if(liker_list != null){
            liker_list.clear();
        }

        if(crusher_list != null){
            crusher_list.clear();
        }

        // verify if user is online /___top. Inverser les ID
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getUser_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            assert status != null;
                            if(!status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    sharing_view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id().equals(user_id))
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

                            assert status != null;
                            if(!status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online_shared.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id_sharing().equals(user_id))
                                        view_online_shared.setVisibility(View.VISIBLE);
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

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(model.getThumbnail())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(thumbnail);

        HttpProxyCacheServer proxy = App.getProxy(context);
        String proxyUrl = proxy.getProxyUrl(video.getUrl());

        // Check if the video is already cached
        Uri uri = Uri.parse(proxyUrl);
        if (uri != null) {
            mediaUri = uri;
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
                            // Model couldnâ€™t be loaded or other internal error.
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

        // caption __________________________________________________________________
        String description = model.getCaption();

        if (!TextUtils.isEmpty(description)) {
            caption.setCharText(description);
            caption.setVisibility(View.VISIBLE);
        }

        // Get the current language in device
        String language2 = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier2 =
                LanguageIdentification.getClient();
        languageIdentifier2.identifyLanguage(description)
                .addOnSuccessListener(
                        languageCode -> {
                            assert languageCode != null;
                            if (languageCode.equals("und")) {
                                Log.i(TAG, "Can't identify language.");
                            } else {
                                Log.i(TAG, "Language: " + languageCode);
                                if (!languageCode.equals(language2))
                                    translate_comment.setVisibility(View.VISIBLE);
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldnâ€™t be loaded or other internal error.
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        translate_comment.setOnClickListener(view -> {
            translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language2,         //language to
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

        //get the profile image and username /___top
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Log.d(TAG, "onDataChange: found user: "+user.getUsername());

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(sharing_profile_photo);

                    sharing_username.setText(user.getUsername());

                    sharing_profile_photo.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

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

                    sharing_relLayout_username.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

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

            }
        });
        // _________________________________________________________________
        //get the profile image and username /___bottom
        Query query1 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id_sharing());

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Log.d(TAG, "onDataChange: found user: "+user.getUsername());

                    if (!context.isFinishing()) {
                        Glide.with(context.getApplicationContext())
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo_shared);
                    }

                    username_shared.setText(user.getUsername());

                    profile_photo_shared.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

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

                    relLayout_username_shared.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

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

            }
        });

        // menu_item
        BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
        menu_item.setOnClickListener(view -> {
            if (bottomSheet.isAdded())
                return;

            Bundle args = new Bundle();
            args.putParcelable("battle_model", model);
            args.putString("miioky", "miioky");
            bottomSheet.setArguments(args);
            bottomSheet.show(context.getSupportFragmentManager(),
                    "TAG");
        });

        linLayout_comment.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewCommentShare.class);
            intent.putExtra("comment_image_une_on_miioky", myGson);
            intent.putExtra("video_une_on_miioky", "video_une_on_miioky");
            intent.putExtra("from_full_video", "from_full_video");
            context.startActivity(intent);
        });

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getUrl(),
                model.getPhoto_id(), "from_video", "", true);
        linLayout_share.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                // put video on pause
                if (isPlaying)  {
                    player.pause();
                    thumbnail.setVisibility(View.GONE);
                    relLayout_img_play.setVisibility(View.VISIBLE);
                    isPlaying = false;
                }

                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare.setArguments(bundle);
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "bindVideo: "+e.getMessage());
            }
        });

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
        getFollow(model.getUser_id(), relLayout_follow);
        // bottom
        getFollow(model.getUser_id_sharing(), relLayout_follow_shared);
    }

    // follow ______________________________________________________________________________________
    private void getFollow(String userID, RelativeLayout relLayout_follow) {
        Query myQuery = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());

                    relLayout_follow.setVisibility(View.GONE);
                }

                if (!snapshot.exists()) {
                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(userID);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                isFollowing(user, relLayout_follow);

                                HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
                                HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

                                relLayout_follow.setOnClickListener(view -> {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_following))
                                            .child(user_id)
                                            .child(user.getUser_id())
                                            .setValue(map_other_user);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_followers))
                                            .child(user.getUser_id())
                                            .child(user_id)
                                            .setValue(map_current_user);
                                    setFollowing(relLayout_follow);
                                });
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

    private void setFollowing(RelativeLayout relLayout_follow){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_follow.setVisibility(View.GONE);
    }

    private void isFollowing(User user, RelativeLayout relLayout_follow){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing(relLayout_follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return this.playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        if (player != null) {
            PlaybackInfo playbackInfo = new PlaybackInfo();
            playbackInfo.setResumePosition(player.getCurrentPosition());
            playbackInfo.setVolumeInfo(new VolumeInfo(player.getVolume() == 0f, player.getVolume()));
            return playbackInfo;
        } else {
            return new PlaybackInfo();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void initialize(@NonNull Container container, @NonNull  PlaybackInfo playbackInfo) {
        if (player == null) {
            player = new ExoPlayer.Builder(context).build();

        }
        player.addListener(this);
        if (mediaUri != null) {
            player.clearMediaItems();
            MediaItem mediaItem = MediaItem.fromUri(mediaUri);
            player.setMediaItem(mediaItem);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    // Update the duration when the player is ready
                    long duration = player.getDuration();
                    durationTextView.setText(androidx.media3.common.util.Util.getStringForTime(formatBuilder, formatter, duration));
                }
            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
                updatePosition();
            }

            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {

                updatePosition();
            }
        });

        player.prepare();

        if (playerView != null) {
            playerView.setPlayer(player);
        }

        parent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull  View view) {

            }

            @Override
            public void onViewDetachedFromWindow(@NonNull  View view) {
                thumbnail.setVisibility(View.VISIBLE);
            }
        });
    }

    @UnstableApi
    private void updatePosition() {
        long position = player.getCurrentPosition();
        positionTextView.setText(androidx.media3.common.util.Util.getStringForTime(formatBuilder, formatter, position));
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            onBuffering();
        } else if (playbackState == Player.STATE_READY) {
            if (player.getPlayWhenReady()) {
                onPlaying();
            } else {
                onPaused();
            }
        } else if (playbackState == Player.STATE_ENDED) {
            onCompleted();
        }
    }

    @Override
    public void play() {
        if (player != null && !player.isPlaying()) {
            player.play();
        }
    }

    @Override
    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        relLayout_img_play.setVisibility(View.GONE);
        return player != null && player.isPlaying();
    }

    @Override
    public void release() {
        if (player != null) {
            player.removeListener(this);
            player.release();
            player = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.25;
    }

    @Override
    public int getPlayerOrder() {
        return getLayoutPosition();
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

    }

    @Override public void onFirstFrameRendered() {
    }

    @Override public void onBuffering() {
        thumbnail.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void onPlaying() {
        progressBar.setVisibility(View.GONE);
        thumbnail.setVisibility(View.GONE);
    }

    @Override public void onPaused() {
        // pour que la vignette n'apparaisse pas losqu'on met la video en pause
        thumbnail.setVisibility(View.VISIBLE);
    }

    @Override public void onCompleted() {
    }

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
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
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(video.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(video.getUser_id())
                                .child(video.getPhoto_id())
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

        if (number_of_likes.getVisibility() != View.VISIBLE)
            number_of_likes.setVisibility(View.VISIBLE);
        number_of_likes.setText(str);

        // add new like
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes_share))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(video.getUser_id())
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes_share))
                .child(newLikeID)
                .setValue(like);

        // affichage Ã  l'Ã©cran
        getLikesString();
        updateLike();
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on rÃ©cupÃ¨re l'identifiant du likeur et le like
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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

    private void removeCrush() {
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
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
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(video.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(video.getUser_id())
                                .child(video.getPhoto_id())
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
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush_share))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(video.getUser_id())
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush_share))
                .child(newCrushID)
                .setValue(crush);

        // affichage Ã  l'Ã©cran
        getCrushString();
        updateCrush();
    }

    private void getCrushString(){
        Log.d(TAG, "getCrushString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush_share));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersCrush = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on rÃ©cupÃ¨re l'identifiant du likeur et le like
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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
        number_of_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    number_of_likes.setVisibility(View.VISIBLE);
                    number_of_likes.setText(String.valueOf(likes_count));

                    number_of_likes.setOnClickListener(view -> {
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
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_comment_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_battle_media_share))
                            .child(video.getPhoto_id())
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
                                number_of_comments.setText(String.valueOf(comments_count));

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
                .child(video.getPhoto_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shares_count++;
                }

                if (shares_count >= 1) {
                    number_of_share.setVisibility(View.VISIBLE);
                    number_of_share.setText(String.valueOf(shares_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

