package com.bekambimouen.miiokychallenge.toro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

import java.util.LinkedHashMap;

/**
 * {@link CacheManager} is a helper interface used by {@link Container} to manage the
 * {@link PlaybackInfo} of {@link ToroPlayer}s. For each {@link ToroPlayer},
 * {@link CacheManager} will ask for a unique key for its {@link PlaybackInfo} cache.
 * {@link Container} uses a {@link LinkedHashMap} to implement the caching mechanism, so
 * {@link CacheManager} must provide keys which are uniquely distinguished by
 * {@link Object#equals(Object)}.
 *
 * @author eneim (7/5/17).
 */
public interface CacheManager {

    /**
     * Get the unique key for the {@link ToroPlayer} of a specific order. Note that this key must
     * also be managed by {@link RecyclerView.Adapter} so that it prevents the uniqueness at data
     * change events.
     *
     * @param order order of the {@link ToroPlayer}.
     * @return the unique key of the {@link ToroPlayer}.
     */
    @Nullable Object getKeyForOrder(int order);

    /**
     * Get the order of a specific key value. Returning a {@code null} order value here will tell
     * {@link Container} to ignore this key's cache order.
     *
     * @param key the key value to lookup.
     * @return the order of the {@link ToroPlayer} whose unique key value is key.
     */
    @Nullable Integer getOrderForKey(@NonNull Object key);

    /**
     * A built-in {@link CacheManager} that use the order as the unique key. Note that this is not
     * data-changes-proof. Which means that after data change events, the map may need to be
     * updated.
     */
    CacheManager DEFAULT = new CacheManager() {
        @Override public Object getKeyForOrder(int order) {
            return order;
        }

        @Override public Integer getOrderForKey(@NonNull Object key) {
            return key instanceof Integer ? (Integer) key : null;
        }
    };
}
