package com.bekambimouen.miiokychallenge.toro;

import static com.bekambimouen.miiokychallenge.toro.ToroUtil.visibleAreaOffset;
import static com.bekambimouen.miiokychallenge.toro.annotations.Sorted.Order.ASCENDING;

import androidx.annotation.NonNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.bekambimouen.miiokychallenge.toro.annotations.Sorted;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

/**
 * @author eneim | 6/2/17.
 *
 * PlayerSelector is a convenient class to help selecting the players to start Media
 * playback.
 *
 * On specific event of RecyclerView, such as Child view attached/detached, scroll, the
 * Collection of players those are available for a playback will change. PlayerSelector is
 * responded to select a specific number of players from that updated Collection to start a
 * new playback or pause an old playback if the corresponding Player is not selected
 * anymore.
 *
 * Client should implement a custom PlayerSelecter and set it to the Container for expected
 * behaviour. By default, Toro comes with linear selection implementation (the Selector
 * that will iterate over the Collection and select the players from top to bottom until a
 * certain condition is fullfilled, for example the maximum of player count is reached).
 *
 * Custom Selector can have more complicated selecting logics, for example: among 2n + 1
 * playable widgets, select n players in the middles ...
 */

@SuppressWarnings("unused") //
public interface PlayerSelector {

    String TAG = "ToroLib:Selector";

    /**
     * Select a collection of {@link ToroPlayer}s to start a playback (if there is non-playing) item.
     * Playing item are also selected.
     *
     * @param container current {@link Container} that holds the players.
     * @param items a mutable collection of candidate {@link ToroPlayer}s, which are the players
     * those can start a playback. Items are sorted in order obtained from
     * {@link ToroPlayer#getPlayerOrder()}.
     * @return the collection of {@link ToroPlayer}s to start a playback. An on-going playback can be
     * selected, but it will keep playing.
     */
    @NonNull Collection<ToroPlayer> select(@NonNull Container container,
                                           @Sorted(order = ASCENDING) @NonNull List<ToroPlayer> items);

    /**
     * The 'reverse' selector of this selector, which can help to select the reversed collection of
     * that expected by this selector.
     * For example: this selector will select the first playable {@link ToroPlayer} from top, so the
     * 'reverse' selector will select the last playable {@link ToroPlayer} from top.
     *
     * @return The PlayerSelector that has opposite selecting logic. If there is no special one,
     * return "this".
     */
    @NonNull PlayerSelector reverse();

    PlayerSelector DEFAULT = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull Container container, //
                                                                @Sorted(order = ASCENDING) @NonNull List<ToroPlayer> items) {
            int count = items.size();
            return count > 0 ? singletonList(items.get(0)) : Collections.<ToroPlayer>emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return DEFAULT_REVERSE;
        }
    };

    PlayerSelector DEFAULT_REVERSE = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull Container container, //
                                                                @Sorted(order = ASCENDING) @NonNull List<ToroPlayer> items) {
            int count = items.size();
            return count > 0 ? singletonList(items.get(count - 1)) : Collections.<ToroPlayer>emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return DEFAULT;
        }
    };

    @SuppressWarnings("unused") PlayerSelector BY_AREA = new PlayerSelector() {

        NavigableMap<Float, ToroPlayer> areas = new TreeMap<>(new Comparator<Float>() {
            @Override public int compare(Float o1, Float o2) {
                return Float.compare(o2, o1); // reverse order, from high to low.
            }
        });

        @NonNull @Override public Collection<ToroPlayer> select(@NonNull final Container container,
                                                                @Sorted(order = ASCENDING) @NonNull List<ToroPlayer> items) {
            areas.clear();
            int count = items.size();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    ToroPlayer item = items.get(i);
                    if (!areas.containsValue(item)) areas.put(visibleAreaOffset(item, container), item);
                }

                count = areas.size();
            }

            return count > 0 ? singletonList(areas.firstEntry().getValue())
                    : Collections.<ToroPlayer>emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return this;
        }
    };

    @SuppressWarnings("unused") PlayerSelector NONE = new PlayerSelector() {
        @NonNull @Override public Collection<ToroPlayer> select(@NonNull Container container, //
                                                                @Sorted(order = ASCENDING) @NonNull List<ToroPlayer> items) {
            return emptyList();
        }

        @NonNull @Override public PlayerSelector reverse() {
            return this;
        }
    };
}
