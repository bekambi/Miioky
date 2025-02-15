package com.bekambimouen.miiokychallenge.fun.adapter.viewholder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetShare;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Crushers;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.fun.bottomsheet.BottomSheetAddCommentFun;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.toro.ToroPlayer;
import com.bekambimouen.miiokychallenge.toro.ToroUtil;
import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.github.kshitij_jain.instalike.InstaLikeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FunProfileViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener,
        Player.Listener, CacheListener {
    private static final String TAG = "FunProfileViewHolder";

    // widgets
    private final ImageView conner_big_star_icon;
    private final InstaLikeView insta_star_view;
    private final SmallBangView like_star;
    private final ImageView unlike_star_image;
    private final SmallBangView like_heart;
    private final ImageView image;
    private final InstaLikeView mInstaLikeView;
    public final CircleImageView profil_image;
    private final ImageView share;
    private final ImageView ivEnveloppe;
    public final ImageView menu_item;
    private final View view_online;
    private final ImageView iv_download;
    private final TextView nber_of_crush;
    private final TextView crush_msg;
    private final TextView nb_likes;
    private final TextView nb_commentaires;
    private final TextView tps_publication;
    public final TextView username;
    private final TextView tv_download;
    private final TextView tv_share;
    private final ViewMoreTextView caption;
    private final TextView translate_comment;
    public final RelativeLayout relLayout_profil;

    // follow unfollow
    public final View point;
    public final TextView button_follow;
    public final TextView button_unfollow;
    public final RelativeLayout relLayout_follow;
    // vars
    private final AppCompatActivity context;
    private final View parent;
    private BattleModel_fun video;
    private final RelativeLayout relLayout_view_overlay;
    private StringBuilder mUsers;
    private StringBuilder mUsersCrush;
    private StringBuilder updateCrushUsers;
    private User mCurrentUser;
    private final ArrayList<String> liker_list, crusher_list;
    private boolean mLikedByCurrentUser;
    private boolean mCrushedByCurrentUser;
    private boolean isPlaying = true;
    private double comments_count;
    private int crush_count;
    private double likes_count;
    private double saved_count;
    private double shared_count;

    private final DatabaseReference myRef;
    private final String user_id;

    private CustomShareProgressView progresDialog;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.show();
    }

    // fun videos
    public final RelativeLayout rel_vol, relLayout_img_play;
    private final GestureDetector detector_video;
    public final ProgressBar progressBar;
    private final ImageView thumbnail;
    public final ImageView img_vol;
    public Player player;
    public final PlayerView playerView;
    private Uri mediaUri;

    private final TextView positionTextView;
    private final TextView durationTextView;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;

    // firebase
    private final FirebaseDatabase database;

    @SuppressLint("ClickableViewAccessibility")
    public FunProfileViewHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, @NonNull  View itemView) {
        super(itemView);
        this.parent = itemView;
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        database=FirebaseDatabase.getInstance();

        playerView = itemView.findViewById(R.id.playerView);
        positionTextView = playerView.findViewById(R.id.exo_position);
        durationTextView = playerView.findViewById(R.id.exo_duration);
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        thumbnail = itemView.findViewById(R.id.thumbnail);
        rel_vol = itemView.findViewById(R.id.rel_vol);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
        img_vol = itemView.findViewById(R.id.img_vol);
        progressBar = itemView.findViewById(R.id.progressBar);

        view_online = itemView.findViewById(R.id.view_online);
        relLayout_profil = itemView.findViewById(R.id.relLayout_profil);
        profil_image = itemView.findViewById(R.id.profile_photo);
        image = itemView.findViewById(R.id.image);
        like_heart = itemView.findViewById(R.id.like_heart);
        mInstaLikeView = itemView.findViewById(R.id.insta_like_view);
        nb_likes = itemView.findViewById(R.id.nb_likes);
        share = itemView.findViewById(R.id.iv_share);
        ivEnveloppe = itemView.findViewById(R.id.message_video);
        nb_commentaires = itemView.findViewById(R.id.nb_commentaires);
        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        username = itemView.findViewById(R.id.username);
        menu_item = itemView.findViewById(R.id.menu_item);
        iv_download = itemView.findViewById(R.id.iv_download);
        tv_share = itemView.findViewById(R.id.tv_share);
        tv_download = itemView.findViewById(R.id.tv_download);
        // crush
        conner_big_star_icon = itemView.findViewById(R.id.conner_big_star_icon);
        insta_star_view = itemView.findViewById(R.id.insta_star_view);
        like_star = itemView.findViewById(R.id.like_star);
        unlike_star_image = itemView.findViewById(R.id.unlike_star_image);

        nber_of_crush = itemView.findViewById(R.id.nber_of_crush);
        crush_msg = itemView.findViewById(R.id.crush_msg);
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // follow
        point = itemView.findViewById(R.id.point);
        button_follow = itemView.findViewById(R.id.button_follow);
        button_unfollow = itemView.findViewById(R.id.button_unfollow);
        relLayout_follow = itemView.findViewById(R.id.relLayout_follow);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        liker_list = new ArrayList<>();
        crusher_list = new ArrayList<>();

        // Play/Pause video
        detector_video = new GestureDetector(context, new GestureListenerPausePlay());
        playerView.setOnTouchListener((view, motionEvent) -> detector_video.onTouchEvent(motionEvent));
    }

    private final class GestureListenerPausePlay extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull  MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull  MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull  MotionEvent e) {
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
        public boolean onDoubleTap(@NonNull  MotionEvent e) {
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
        public void onLongPress(@NonNull  MotionEvent e) {
            super.onLongPress(e);
        }
    }

    public void bindVideo(BattleModel_fun model) {
        video = model;

        caption.setCharText("");
        caption.setVisibility(View.GONE);
        translate_comment.setVisibility(View.GONE);
        nber_of_crush.setText("0");
        nb_likes.setText("0");
        nb_commentaires.setText("0");
        tv_share.setText("0");

        if(liker_list != null){
            liker_list.clear();
        }

        if(crusher_list != null){
            crusher_list.clear();
        }

        translate_comment.setVisibility(View.GONE);

        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getUser_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            assert status != null;
                            if(!status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

        setComments();
        crushCount();
        setLikes(model);
        setSavedVideosInGallery();
        setSharedVideo();
        getCurrentUser();
        getLikesString();
        updatedLikes();
        getCrushString();
        updateCrush();

        HttpProxyCacheServer proxy = App.getProxy(context);
        proxy.registerCacheListener(this, model.getUrl());
        String proxyUrl = proxy.getProxyUrl(model.getUrl());

        // Check if the video is already cached
        Uri uri = Uri.parse(proxyUrl);
        if (uri != null) {
            mediaUri = uri;
        }

        long interval = getLayoutPosition() * 1000L;
        RequestOptions options = new RequestOptions().frame(interval);

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(model.getThumbnail())
                .apply(options)
                .placeholder(R.color.black_semi_transparen3)
                .into(thumbnail);

        init(model);
    }

    void init(BattleModel_fun model) {
        long tv_date = model.getDate_created();
        getTimestampDifference(tv_date, tps_publication);

        String description = model.getCaption();

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
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        caption.setCharText("");
        caption.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(description)) {
            caption.setVisibility(View.VISIBLE);
            caption.setCharText(description);
        }

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

        BottomSheetAddCommentFun mSheetFragment = new BottomSheetAddCommentFun(context, video, relLayout_view_overlay);
        ivEnveloppe.setOnClickListener(view -> {
            if (mSheetFragment.isAdded())
                return;
            if (isPlaying)  {
                player.pause();
                thumbnail.setVisibility(View.GONE);
                relLayout_img_play.setVisibility(View.VISIBLE);
                isPlaying = false;
            }
            mSheetFragment.show(context.getSupportFragmentManager(), "TAG");
        });

        // share
        BottomSheetShare bottomSheetShare = new BottomSheetShare(context, video.getUrl(), video.getPhoto_id());
        share.setOnClickListener(view -> {
            if (bottomSheetShare.isAdded())
                return;
            // put video on pause
            if (isPlaying)  {
                player.pause();
                thumbnail.setVisibility(View.GONE);
                relLayout_img_play.setVisibility(View.VISIBLE);
                isPlaying = false;
            }

            try {
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");
            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        like_heart.setOnClickListener(new View.OnClickListener() {
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
                    unlike_star_image.setImageResource(R.drawable.unlike_star_white);
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

        // to save in gallery
        iv_download.setOnClickListener(view -> {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

            } else {
                addNewSaveVideo();
            }
        });

        // following _______________________________________________________________________________
        Query myQuery = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    relLayout_follow.setVisibility(View.GONE);
                }

                if (!snapshot.exists()) {
                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(model.getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing(relLayout_follow);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    // save video in gallery
    private void addNewSaveVideo() {
        showLoading();
        DownloadVideoFile videoFile = new DownloadVideoFile();
        videoFile.execute(video.getUrl());

        HashMap<String, Object> map = new HashMap<>();
        map.put(context.getString(R.string.field_user_id), user_id);

        String newKey = myRef.push().getKey();
        assert newKey != null;
        myRef.child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(video.getPhoto_id())
                .child(newKey)
                .setValue(map);
    }

    // count number of video downloaded
    public void setSavedVideosInGallery() {
        saved_count = 0;
        tv_download.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(video.getPhoto_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    saved_count++;
                }


                if (saved_count >= 1) {
                    tv_download.setVisibility(View.VISIBLE);

                    double count;
                    if (saved_count >= 1000) {
                        count = saved_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        tv_download.setText(tv_count);

                    } else {
                        tv_download.setText(String.valueOf((int) saved_count));
                    }
                    // download count
                    int downloadCount = Integer.parseInt(tv_download.getText().toString());
                    String downloadStr = String.valueOf(downloadCount);
                    tv_download.setText(downloadStr);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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
    public void initialize(@NonNull  Container container, @NonNull  PlaybackInfo playbackInfo) {
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
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mLikedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {

                        // update like count
                        int count = Integer.parseInt(nb_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nb_likes.setVisibility(View.INVISIBLE);
                        nb_likes.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_videos))
                                .child(video.getPhoto_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_fun))
                                .child(video.getUser_id())
                                .child(video.getPhoto_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        getLikesString();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    public void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(nb_likes.getText().toString());
        String str = String.valueOf(count+1);

        if (!str.equals("0"))
            nb_likes.setVisibility(View.VISIBLE);
        nb_likes.setText(str);

        // add new like
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_fun))
                .child(video.getUser_id())
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString();
    }

    public void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsers.append(user.getUsername());
                                mUsers.append(",");

                            }

                            try {
                                mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ",");
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }

                            setupLikesString();
                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupLikesString();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void updatedLikes(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found crush: " +user.getUsername());

                                mUsers.append(user.getUsername());
                                mUsers.append(",");
                            }

                            mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ",");

                            setupLikesString();
                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupLikesString();
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void getCurrentUser(){
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    mCurrentUser = Util_User.getUser(context, objectMap, ds);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupLikesString() {
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
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                        myRef.child(context.getString(R.string.dbname_videos))
                                .child(video.getPhoto_id())
                                .child(context.getString(R.string.field_crush))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_fun))
                                .child(video.getUser_id())
                                .child(video.getPhoto_id())
                                .child(context.getString(R.string.field_crush))
                                .child(keyID)
                                .removeValue();

                        getCrushString();
                        updateCrush();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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
        myRef.child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_fun))
                .child(video.getUser_id())
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush))
                .child(newCrushID)
                .setValue(crush);

        // affichage à l'écran
        getCrushString();
        updateCrush();
    }

    private void getCrushString(){
        Log.d(TAG, "getCrushString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
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
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser = false;
                    setupCrushString();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void updateCrush(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                updateCrushUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(user_id);

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
                unlike_star_image.setImageResource(R.drawable.unlike_star_white);
            }
        }
    }

    public void crushCount() {
        crush_count = 0;
        nber_of_crush.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_crush));

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

    public void setComments() {
        comments_count = 0;
        nb_commentaires.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
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
                            .child(video.getPhoto_id())
                            .child(context.getString(R.string.field_comments))
                            .child(keyID)
                            .child(context.getString(R.string.field_comments));

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+ds.getKey());
                                comments_count++;
                            }

                            if (comments_count >= 1) {
                                nb_commentaires.setVisibility(View.VISIBLE);

                                double count;
                                if (comments_count >= 1000) {
                                    count = comments_count/1000;

                                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                                    nb_commentaires.setText(tv_count);

                                } else {
                                    nb_commentaires.setText(String.valueOf((int) comments_count));
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

    public void setLikes(BattleModel_fun video) {
        likes_count = 0;
        nb_likes.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_videos))
                .child(video.getPhoto_id())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    nb_likes.setVisibility(View.VISIBLE);

                    double count;
                    if (likes_count >= 1000) {
                        count = likes_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        nb_likes.setText(tv_count);

                    } else {
                        nb_likes.setText(String.valueOf((int) likes_count));
                    }

                    nb_likes.setOnClickListener(view -> {
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

    // share video coubt
    private void setSharedVideo() {
        shared_count = 0;
        tv_share.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(video.getPhoto_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shared_count++;
                }

                if (shared_count >= 1) {
                    tv_share.setVisibility(View.VISIBLE);

                    double count;
                    if (shared_count >= 1000) {
                        count = shared_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        tv_share.setText(tv_count);

                    } else {
                        tv_share.setText(String.valueOf((int) shared_count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // download video from URL
    @SuppressLint("StaticFieldLeak")
    class DownloadVideoFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... videoURL) {
            int count;
            try {
                URL url = new URL(videoURL[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), video.getDate_created()+".mp4");
                MediaScannerConnection.scanFile(context, new String[] { file.getPath() }, new String[] { "video/mp4" }, null);
                OutputStream output = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    output = Files.newOutputStream(file.toPath());
                }

                byte[] data = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    assert output != null;
                    output.write(data, 0, count);
                }

                assert output != null;
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                progresDialog.dismiss();
                context.runOnUiThread(() -> Toast.makeText(context, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progresDialog.dismiss();
            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     */
    @SuppressLint("SetTextI18n")
    public void getTimestampDifference(long date, TextView tvDate){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        String tps;
        long time = (System.currentTimeMillis() - date);
        if (time >= 2*3600*24000) {
            tps = date_passe;
        } else if (isYesterday(date)) {
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

    public boolean isYesterday(long milliSeconds) {
        Calendar toCheck = Calendar.getInstance();
        toCheck.setTimeInMillis(milliSeconds); // your date

        Calendar yesterday = Calendar.getInstance(); // today
        yesterday.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        return yesterday.get(Calendar.YEAR) == toCheck.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == toCheck.get(Calendar.DAY_OF_YEAR);
    }
}
