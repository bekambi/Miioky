package com.bekambimouen.miiokychallenge.full_challenge_video.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.interfaces.HelperPlayerViewListener;
import com.bekambimouen.miiokychallenge.model.ChallengeModel;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import com.bekambimouen.miiokychallenge.toro.ToroPlayer;
import com.bekambimouen.miiokychallenge.toro.ToroUtil;
import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

public class FullChallengeVideoAdapter extends RecyclerView.Adapter<FullChallengeVideoAdapter.MyViewHolder> {
    private static final String TAG = "FullChallengeVideoAdapter";

    private final AppCompatActivity context;
    private final List<ChallengeModel> list;
    private final HelperPlayerViewListener playerViewListener;
    private final ProgressBar progressBar;

    public FullChallengeVideoAdapter(AppCompatActivity context, List<ChallengeModel> list,
                                     HelperPlayerViewListener playerViewListener, ProgressBar progressBar) {
        this.context = context;
        this.list = list;
        this.playerViewListener = playerViewListener;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_full_challenge, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {
        ChallengeModel model = list.get(position);
        holder.bindVideo(model);

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        ChallengeModel model = list.get(position);
        return model.getDate_created();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, Player.Listener,
            CacheListener {

        private final RelativeLayout relLayout_img_play;

        // vars
        private final GestureDetector detector_video;
        private final View parent;
        private boolean isPlaying = true;
        private final ProgressBar progressBar;
        private final ImageView thumbnail;
        public Player player;
        private final PlayerView playerView;
        private Uri mediaUri;

        private final TextView positionTextView;
        private final TextView durationTextView;
        private final StringBuilder formatBuilder;
        private final Formatter formatter;

        @SuppressLint("ClickableViewAccessibility")
        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);
            parent = itemView;
            playerView = itemView.findViewById(R.id.playerView);
            positionTextView = playerView.findViewById(R.id.exo_position);
            durationTextView = playerView.findViewById(R.id.exo_duration);
            formatBuilder = new StringBuilder();
            formatter = new Formatter(formatBuilder, Locale.getDefault());

            thumbnail = itemView.findViewById(R.id.thumbnail);
            relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
            progressBar = itemView.findViewById(R.id.progressBar);
            // Play/Pause video
            detector_video = new GestureDetector(context, new GestureListenerPausePlay());
            playerView.setOnTouchListener((view, motionEvent) -> detector_video.onTouchEvent(motionEvent));
        }

        public class GestureListenerPausePlay extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onDown(@NonNull  MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(@NonNull  MotionEvent e) {
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
                return false;
            }
        }

        void bindVideo(ChallengeModel model) {
            HttpProxyCacheServer proxy = App.getProxy(context);
            String proxyUrl = proxy.getProxyUrl(model.getUrl(), true);

            Uri item = Uri.parse(proxyUrl);
            if (item != null) {
                mediaUri = item;
            }

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getThumbnail())
                    .placeholder(R.color.colorPrimaryDark)
                    .into(thumbnail);
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
                    durationTextView.setText(Util.getStringForTime(formatBuilder, formatter, duration));
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
            playerViewListener.onAddEventListener(player);

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
            positionTextView.setText(Util.getStringForTime(formatBuilder, formatter, position));
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
    }
}
