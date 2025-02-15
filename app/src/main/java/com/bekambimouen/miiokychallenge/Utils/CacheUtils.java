package com.bekambimouen.miiokychallenge.Utils;

import static com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory;

import android.content.Context;

import java.io.File;

// danikula cache video
public class CacheUtils {

    public static File getVideoCacheDir(Context context) {
        File cacheDir = getCacheDirectory(context, true);
        File file = new File(cacheDir, "video-cache");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
