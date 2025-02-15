package com.bekambimouen.miiokychallenge.toro.media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author eneim | 6/5/17.
 *
 * A definition of DRM media type.
 */

public interface DrmMedia {

    // DRM Scheme
    @NonNull String getType();

    @Nullable String getLicenseUrl();

    @Nullable String[] getKeyRequestPropertiesArray();

    boolean multiSession();
}
