package com.bekambimouen.miiokychallenge.danikula_cache.sourcestorage;

import com.bekambimouen.miiokychallenge.danikula_cache.SourceInfo;

/**
 * Storage for {@link SourceInfo}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface SourceInfoStorage {

    SourceInfo get(String url);

    void put(String url, SourceInfo sourceInfo);

    void release();
}
