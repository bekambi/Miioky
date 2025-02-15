package com.bekambimouen.miiokychallenge.toro.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is to mark deprecated objects to be removed from library from a certain version,
 * specific by {@link #version()}. This is to help quickly navigate through them, and to make it clear.
 *
 * @author eneim (2018/05/01).
 */
@Retention(RetentionPolicy.SOURCE)  //
public @interface RemoveIn {

    String version();
}
