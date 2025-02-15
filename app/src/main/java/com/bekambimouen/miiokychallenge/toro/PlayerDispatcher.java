package com.bekambimouen.miiokychallenge.toro;

public interface PlayerDispatcher {

    int DELAY_INFINITE = -1;

    int DELAY_NONE = 0;

    /**
     * Return the number of milliseconds that a call to {@link ToroPlayer#play()} should be delayed.
     * Returning {@link #DELAY_INFINITE} will not start the playback, while returning {@link
     * #DELAY_NONE} will start it immediately.
     *
     * @param player the player that is about to play.
     * @return number of milliseconds to delay the play, or one of {@link #DELAY_INFINITE} or
     * {@link #DELAY_NONE}. No other negative number should be used.
     */
    int getDelayToPlay(ToroPlayer player);

    PlayerDispatcher DEFAULT = new PlayerDispatcher() {
        @Override public int getDelayToPlay(ToroPlayer player) {
            return DELAY_NONE;
        }
    };
}
