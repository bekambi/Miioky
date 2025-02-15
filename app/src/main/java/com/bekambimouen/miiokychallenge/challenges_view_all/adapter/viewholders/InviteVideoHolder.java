package com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders;

import static java.util.Objects.requireNonNull;

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
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
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
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.io.File;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bekambimouen.miiokychallenge.toro.ToroPlayer;
import com.bekambimouen.miiokychallenge.toro.ToroUtil;
import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

public class InviteVideoHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, Player.Listener,
        CacheListener {
    private static final String TAG = "VideoViewHolder";

    // widgets
    private final View view_online;
    private final ImageView thumbnail;
    public final ImageView menu_item;
    private final CircleImageView profile;
    private final TextView username;
    private final TextView tps_publication;
    private final ViewMoreTextView caption;
    private final TextView translate_comment;
    private final TextView category;
    private final RelativeLayout relLayout_category;
    private final RelativeLayout relLayout_go_user_profile;
    public final RelativeLayout relLayout_follow;
    private final ProgressBar progressBar;

    private final TextView downloaded_count;
    private final TextView share_count;
    private final RelativeLayout relLayout_count_actions;
    private final RelativeLayout relLayout_download;
    private final RelativeLayout relLayout_share;
    private final LinearLayout linLayout_share;


    // vars
    private final AppCompatActivity context;
    private final View parent;
    private BottomSheetSharePublication bottomSheetShare;
    private ModelInvite video;
    private final RelativeLayout relLayout_view_overlay;
    private int saved_count = 0;
    private int shared_count = 0;

    public final RelativeLayout rel_vol;
    private final GestureDetector detector_video;
    private final RelativeLayout relLayout_img_play;
    private boolean isPlaying = true;
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
    public InviteVideoHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        parent = itemView;
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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
        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        category = itemView.findViewById(R.id.category);
        relLayout_category = itemView.findViewById(R.id.relLayout_category);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        username = itemView.findViewById(R.id.username);
        profile = itemView.findViewById(R.id.profile_photo);
        menu_item = itemView.findViewById(R.id.menu_item);
        relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);
        relLayout_follow = itemView.findViewById(R.id.relLayout_follow);

        downloaded_count = itemView.findViewById(R.id.downloaded_count);
        share_count = itemView.findViewById(R.id.share_count);
        relLayout_count_actions = itemView.findViewById(R.id.relLayout_count_actions);
        relLayout_download = itemView.findViewById(R.id.relLayout_download);
        relLayout_share = itemView.findViewById(R.id.relLayout_share);
        linLayout_share = itemView.findViewById(R.id.linLayout_share);

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

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    }

    public void bindVideo(ModelInvite model) {
        video = model;

        caption.setVisibility(View.GONE);
        translate_comment.setVisibility(View.GONE);

        // verify if user is online
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getInvite_userID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            assert status != null;
                            if(!status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getInvite_userID().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        setSavedVideosInGallery();
        setSharedVideo();

        //set the time it was posted
        long tv_date = video.getDate_created();
        long time = (System.currentTimeMillis() - tv_date);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(TimeUtils.getTime(context, tv_date));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

        // category
        String set_category = Util_InviteChallengeCategory.getInviteChallengeCategory(context, model);
        category.setText(Html.fromHtml("#"+set_category));

        String invite_category_status = model.getInvite_category_status();
        relLayout_category.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);

            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("battle_model", myGson);
            intent.putExtra("category_status", invite_category_status);
            context.startActivity(intent);
        });

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, null, model.getInvite_userID(), model.getInvite_url(),
                model.getInvite_photoID(), "from_video", "", true);
        linLayout_share.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "bindVideo: "+e.getMessage());
            }
        });

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(video.getThumbnail_invite())
                .placeholder(R.drawable.black_person)
                .into(thumbnail);

        HttpProxyCacheServer proxy = App.getProxy(context);
        String proxyUrl = proxy.getProxyUrl(video.getInvite_url());

        Uri uri = Uri.parse(proxyUrl);
        if (uri != null) {
            mediaUri = uri;
        }

        //get the profile image and username
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(video.getInvite_userID());

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
                            .into(profile);

                    username.setText(user.getUsername());

                    String description = video.getInvite_caption();

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

                    if (!TextUtils.isEmpty(description)) {
                        caption.setCharText(description);
                        caption.setVisibility(View.VISIBLE);
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

                    String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    relLayout_go_user_profile.setOnClickListener(v -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);

                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(user_id)) {
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
            args.putParcelable("model_invite", model);
            args.putString("miioky", "miioky");
            bottomSheet.setArguments(args);
            bottomSheet.show(context.getSupportFragmentManager(),
                    "TAG");
        });

        // following _______________________________________________________________________________
        Query myQuery = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getInvite_userID());

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
                            .equalTo(model.getInvite_userID());

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

    // count number of video downloaded
    public void setSavedVideosInGallery() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(video.getInvite_photoID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    saved_count++;
                }

                if (saved_count >= 1) {
                    relLayout_count_actions.setVisibility(View.VISIBLE);
                    relLayout_download.setVisibility(View.VISIBLE);

                    downloaded_count.setText(String.valueOf(saved_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // share video coubt
    private void setSharedVideo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_share_video))
                .child(video.getInvite_photoID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shared_count++;
                }

                if (shared_count >= 1) {
                    relLayout_count_actions.setVisibility(View.VISIBLE);
                    relLayout_share.setVisibility(View.VISIBLE);

                    share_count.setText(String.valueOf(shared_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
