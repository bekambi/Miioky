package com.bekambimouen.miiokychallenge.fun.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.fun.adapter.viewholder.FunProfileViewHolder;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bumptech.glide.RequestManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FunPlayerRecyclerView extends RecyclerView implements CacheListener {

    private static final String TAG = "VideoPlayerRecyclerView";

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

    }

    private enum VolumeState {ON, OFF};

    // ui
    private ImageView thumbnail, volumeControl;
    private RelativeLayout relLayout_img_play;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private PlayerView playerView;
    private ExoPlayer player;

    // vars
    private Context context;
    private ArrayList<BattleModel_fun> mediaObjects = new ArrayList<>();
    private long currentPlaybackPosition = 0;
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;
    private boolean isPlaying = true, isMute = false;

    // controlling playback state
    private VolumeState volumeState;

    public FunPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FunPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void init(Context context){
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        playerView = new PlayerView(this.context);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this.context).build();
        TrackSelector trackSelector = new DefaultTrackSelector(this.context);

        // 2. Create the player
        player = new ExoPlayer.Builder(this.context)
                .setBandwidthMeter(bandwidthMeter)
                .setTrackSelector(trackSelector)
                .build();

        // Bind the player to the view.
        playerView.setUseController(false);
        playerView.setPlayer(player);
        setVolumeControl(VolumeState.ON);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: called.");
                    if (relLayout_img_play.getVisibility() == View.VISIBLE)
                        relLayout_img_play.setVisibility(GONE);

                    if(thumbnail != null){ // show the old thumbnail
                        progressBar.setVisibility(VISIBLE);
                        //thumbnail.setVisibility(VISIBLE);
                    }

                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    playVideo(!recyclerView.canScrollVertically(1));

                    if (isMute) {
                        try {
                            volumeControl.setImageResource(R.drawable.ic_mute);
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                        }
                    } else {
                        volumeControl.setImageResource(R.drawable.ic_unmute);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }

            }
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        thumbnail.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(VISIBLE);

                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        player.seekTo(0);
                        break;
                    case Player.STATE_IDLE:
                        //thumbnail.setVisibility(VISIBLE);
                        progressBar.setVisibility(VISIBLE);
                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        //thumbnail.setVisibility(GONE);
                        progressBar.setVisibility(GONE);
                        if(!isVideoViewAdded){
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @SuppressLint("WrongViewCast")
    public void playVideo(boolean isEndOfList) {
        int targetPosition;

        if(!isEndOfList){
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }

            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            }
            else {
                targetPosition = startPosition;
            }
        }
        else{
            targetPosition = mediaObjects.size() - 1;
        }

        Log.d(TAG, "playVideo: target position: " + targetPosition);

        // video is already playing so return
        if (targetPosition == playPosition) {
            return;
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition;
        if (playerView == null) {
            return;
        }

        // remove any old surface views from previously playing videos
        playerView.setVisibility(INVISIBLE);
        removeVideoView(playerView);

        int currentPosition = targetPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        FunProfileViewHolder holder = (FunProfileViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        /*thumbnail = holder.thumbnail;
        relLayout_img_play = holder.relLayout_img_play;
        progressBar = holder.progressBar;
        volumeControl = holder.img_vol;
        viewHolderParent = holder.itemView;
        requestManager = holder.requestManager;
        frameLayout = holder.itemView.findViewById(R.id.media_container);*/

        playerView.setPlayer(player);

        viewHolderParent.setOnClickListener(videoViewClickListener);
        volumeControl.setOnClickListener(volumeClickListener);

        HttpProxyCacheServer proxy = App.getProxy(context);
        proxy.registerCacheListener(this, mediaObjects.get(targetPosition).getUrl());
        String proxyUrl = proxy.getProxyUrl(mediaObjects.get(targetPosition).getUrl());

        if (proxyUrl != null) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(proxyUrl));

            boolean shouldResume = !player.isPlaying() && currentPlaybackPosition > 0; // Check if resuming

            if (!shouldResume) { // New video orstarting from beginning
                player.setMediaItem(mediaItem);
                player.prepare();
                currentPlaybackPosition = 0; // Reset position for new video
            } else { // Resuming paused video
                player.seekTo(currentPlaybackPosition); // Seek to stored position
            }

            player.setPlayWhenReady(true);
            isPlaying = true;
        }
    }

    private final OnClickListener videoViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isPlaying)  {
                player.setPlayWhenReady(false);
                thumbnail.setVisibility(GONE);
                relLayout_img_play.setVisibility(VISIBLE);
                isPlaying = false;
            } else {
                player.setPlayWhenReady(true);
                relLayout_img_play.setVisibility(GONE);
                isPlaying = true;
            }
        }
    };

    private final OnClickListener volumeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleVolume();
        }
    };

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     */
    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }


    // Remove the old player
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }

    }

    private void addVideoView(){
        if (!isVideoViewAdded && frameLayout != null) {
            frameLayout.addView(playerView);
            isVideoViewAdded = true;
            playerView.setVisibility(VISIBLE);
            playerView.setAlpha(1);
            thumbnail.setVisibility(GONE);
            playerView.requestFocus();
        }
    }

    private void resetVideoView(){
        if(isVideoViewAdded){
            removeVideoView(playerView);
            playPosition = -1;
            playerView.setVisibility(INVISIBLE);
            thumbnail.setVisibility(VISIBLE);
        }
    }

    public void pausePlayer() {
        if (player != null && player.isPlaying()) {
            currentPlaybackPosition = player.getCurrentPosition(); // Store current position
            player.pause();
        }
    }

    public void resumePlayer() {
        if (player != null && !player.isPlaying()) {
            player.seekTo(currentPlaybackPosition); // Seek to stored position
            player.play();
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }

        viewHolderParent = null;
    }

    private void toggleVolume() {
        if (player != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);

            } else if(volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
            }
        }
    }

    private void setVolumeControl(VolumeState state){
        volumeState = state;
        if(state == VolumeState.OFF){
            isMute = true;
            player.setVolume(0f);
            animateVolumeControl();
        }
        else if(state == VolumeState.ON){
            isMute = false;
            player.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl(){
        if(volumeControl != null){
            volumeControl.bringToFront();
            if(volumeState == VolumeState.OFF){
                requestManager.load(R.drawable.ic_mute)
                        .into(volumeControl);
            }
            else if(volumeState == VolumeState.ON){
                requestManager.load(R.drawable.ic_unmute)
                        .into(volumeControl);
            }
        }
    }

    public void setMediaObjects(ArrayList<BattleModel_fun> mediaObjects){
        this.mediaObjects = mediaObjects;
    }
}