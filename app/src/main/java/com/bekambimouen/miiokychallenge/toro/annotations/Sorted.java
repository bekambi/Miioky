package com.bekambimouen.miiokychallenge.toro.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author eneim (2018/02/07).
 *
 *         Annotate that a list of items are sorted in specific {@link Order}.
 */

@Retention(RetentionPolicy.SOURCE)  //
public @interface Sorted {

    Order order() default Order.ASCENDING;

    enum Order {
        ASCENDING, DESCENDING, UNSORTED
    }
}
