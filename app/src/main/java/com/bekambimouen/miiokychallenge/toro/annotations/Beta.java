package com.bekambimouen.miiokychallenge.toro.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicate that the feature is still in Beta testing and may not ready for production.
 *
 * @author eneim (2018/02/27).
 */

@Retention(RetentionPolicy.SOURCE)  //
public @interface Beta {
}
