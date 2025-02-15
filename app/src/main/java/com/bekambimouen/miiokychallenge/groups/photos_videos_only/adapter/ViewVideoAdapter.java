package com.bekambimouen.miiokychallenge.groups.photos_videos_only.adapter;

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
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.toro.ToroPlayer;
import com.bekambimouen.miiokychallenge.toro.ToroUtil;
import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bumptech.glide.Glide;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

public class ViewVideoAdapter extends PaginatedAdapter<BattleModel, ViewVideoAdapter.Video> {
    private static final String TAG = "FullChallengeAdapter";

    private final AppCompatActivity context;
    private final RecyclerView recyclerView;
    private final ProgressBar progress_bar;

    private boolean isMute = false;

    public ViewVideoAdapter(AppCompatActivity context, RecyclerView recyclerView, ProgressBar progress_bar) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.progress_bar = progress_bar;
    }


    @NonNull
    @Override
    public Video onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_video, parent, false);
        return new Video(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Video holder, int position) {
        BattleModel model = getItem(position);
        holder.bindVideo(model);

        holder.rel_vol.setOnClickListener(view -> {
            if (holder.player != null) {
                if (holder.player.getVolume() == 0) {
                    isMute = false;
                    holder.player.setVolume(1);
                    holder.img_vol.setImageResource(R.drawable.ic_unmute);

                } else {
                    isMute = true;
                    holder.player.setVolume(0);
                    holder.img_vol.setImageResource(R.drawable.ic_mute);
                }
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (holder.player != null) {
                        if (isMute) {
                            try {
                                holder.player.setVolume(0);
                                holder.img_vol.setImageResource(R.drawable.ic_mute);
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                            }

                        } else {
                            try {
                                holder.player.setVolume(1);
                                holder.img_vol.setImageResource(R.drawable.ic_unmute);
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onScrollStateChanged: "+e);
                            }
                        }
                    }
                }
            }
        });

        // hide progressbar
        if (progress_bar != null && progress_bar.getVisibility() == View.VISIBLE)
            progress_bar.setVisibility(View.GONE);
    }

    public class Video extends RecyclerView.ViewHolder  implements ToroPlayer, ToroPlayer.EventListener,
            Player.Listener, CacheListener {

        private final RelativeLayout rel_vol, relLayout_img_play;
        private final ProgressBar progressBar;
        private final GestureDetector detector_video;

        // vars
        private final View parent;
        private boolean isPlaying = true;
        private final ImageView thumbnail, img_vol;
        public Player player;
        private final PlayerView playerView;
        private Uri mediaUri;

        private final TextView positionTextView;
        private final TextView durationTextView;
        private final StringBuilder formatBuilder;
        private final Formatter formatter;

        @SuppressLint("ClickableViewAccessibility")
        public Video(@NonNull View itemView) {
            super(itemView);
            this.parent = itemView;

            progressBar = itemView.findViewById(R.id.progressBar);
            playerView = itemView.findViewById(R.id.playerView);
            positionTextView = playerView.findViewById(R.id.exo_position);
            durationTextView = playerView.findViewById(R.id.exo_duration);
            formatBuilder = new StringBuilder();
            formatter = new Formatter(formatBuilder, Locale.getDefault());

            thumbnail = itemView.findViewById(R.id.thumbnail);
            rel_vol = itemView.findViewById(R.id.rel_vol);
            relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
            img_vol = itemView.findViewById(R.id.img_vol);

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

        void bindVideo(BattleModel model) {
            HttpProxyCacheServer proxy = App.getProxy(context);
            String proxyUrl = proxy.getProxyUrl(model.getUrl());

            thumbnail.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(proxyUrl);
            if (uri != null) {
                mediaUri = uri;
            }

            if (!context.isFinishing()) {
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(model.getThumbnail())
                        .placeholder(R.color.black_semi_transparen3)
                        .into(thumbnail);
            }
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
    }
}

