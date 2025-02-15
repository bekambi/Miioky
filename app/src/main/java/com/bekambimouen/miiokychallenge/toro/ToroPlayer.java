package com.bekambimouen.miiokychallenge.toro;

import android.view.View;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Definition of a Player used in Toro. Besides common playback command ({@link #play()}, {@link
 * #pause()}, etc), it provides the library necessary information about the playback and
 * components.
 *
 * @author eneim | 5/31/17.
 */

public interface ToroPlayer {

    @NonNull View getPlayerView();

    @NonNull
    PlaybackInfo getCurrentPlaybackInfo();

    void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo);

    /**
     * Start playback or resume from a pausing state.
     */
    void play();

    /**
     * Pause current playback.
     */
    void pause();

    boolean isPlaying();

    /**
     * Tear down all the setup. This should release all player instances.
     */
    void release();

    boolean wantsToPlay();

    /**
     * @return prefer playback order in list. Can be customized.
     */
    int getPlayerOrder();

    /**
     * A convenient callback to help {@link ToroPlayer} to listen to different playback states.
     */
    interface EventListener {

        void onFirstFrameRendered();

        void onBuffering(); // ExoPlayer state: 2

        void onPlaying(); // ExoPlayer state: 3, play flag: true

        void onPaused();  // ExoPlayer state: 3, play flag: false

        void onCompleted(); // ExoPlayer state: 4
    }

    interface OnVolumeChangeListener {

        void onVolumeChanged(@NonNull VolumeInfo volumeInfo);
    }

    interface OnErrorListener {

        void onError(Exception error);
    }

    class EventListeners extends CopyOnWriteArraySet<EventListener> implements EventListener {

        @Override public void onFirstFrameRendered() {
            for (EventListener listener : this) {
                listener.onFirstFrameRendered();
            }
        }

        @Override public void onBuffering() {
            for (EventListener listener : this) {
                listener.onBuffering();
            }
        }

        @Override public void onPlaying() {
            for (EventListener listener : this) {
                listener.onPlaying();
            }
        }

        @Override public void onPaused() {
            for (EventListener listener : this) {
                listener.onPaused();
            }
        }

        @Override public void onCompleted() {
            for (EventListener listener : this) {
                listener.onCompleted();
            }
        }
    }

    class ErrorListeners extends CopyOnWriteArraySet<OnErrorListener>
            implements ToroPlayer.OnErrorListener {

        @Override public void onError(Exception error) {
            for (ToroPlayer.OnErrorListener listener : this) {
                listener.onError(error);
            }
        }
    }

    class VolumeChangeListeners extends CopyOnWriteArraySet<ToroPlayer.OnVolumeChangeListener>
            implements ToroPlayer.OnVolumeChangeListener {

        @Override public void onVolumeChanged(@NonNull VolumeInfo volumeInfo) {
            for (ToroPlayer.OnVolumeChangeListener listener : this) {
                listener.onVolumeChanged(volumeInfo);
            }
        }
    }

    // Adapt from ExoPlayer.
    @Retention(RetentionPolicy.SOURCE)  //
    @IntDef({ State.STATE_IDLE, State.STATE_BUFFERING, State.STATE_READY, State.STATE_END })  //
    @interface State {
        int STATE_IDLE = 1;
        int STATE_BUFFERING = 2;
        int STATE_READY = 3;
        int STATE_END = 4;
    }
}
