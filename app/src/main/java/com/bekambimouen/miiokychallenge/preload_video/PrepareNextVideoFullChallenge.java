package com.bekambimouen.miiokychallenge.preload_video;

import android.content.Context;
import android.util.Log;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.model.ChallengeModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PrepareNextVideoFullChallenge {
    private static final String TAG = "PrepareNextVideo";
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds

    public interface PreloadListener {
        void onPreloadProgress(ChallengeModel videoItem, int progress);
        void onPreloadComplete(ChallengeModel videoItem);
        void onPreloadError(ChallengeModel videoItem, Exception e);
    }

    public static void preDownloadVideo(Context context, ChallengeModel videoItem, PreloadListener listener) {
        HttpProxyCacheServer proxy = App.getProxy(context);
        String proxyUrl = proxy.getProxyUrl(videoItem.getUrl());
        android.util.Log.d(TAG, "preDownloadVideo: proxyUrl = " + proxyUrl);

        new Thread(() -> {
            URLConnection connection = null; // Use URLConnection instead of HttpURLConnection
            try {
                URL url = new URL(proxyUrl);

                if (proxyUrl.startsWith("file://")) {
                    Log.d(TAG, "preDownloadVideo: Handling file:// URL for " + videoItem.getUrl());
                    // Handle file:// URL (e.g., copy the fileto a different location)
                    // In this case, we consider the preload complete since it's already cached
                    if (listener != null) {
                        listener.onPreloadComplete(videoItem);
                    }
                    return; // Exit the method
                } else if (proxyUrl.startsWith("http://") || proxyUrl.startsWith("https://")) {
                    Log.d(TAG, "preDownloadVideo: Handling http:// URL for " + videoItem.getUrl());
                    connection = url.openConnection();
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);
                    connection.connect();

                    // Simulate progress (replace with actual progress tracking if possible)
                    for (int i = 1; i <= 100; i++) {
                        if (listener != null) {
                            listener.onPreloadProgress(videoItem, i);
                        }
                        Thread.sleep(50); // Simulate some work
                    }

                    if (listener != null) {
                        listener.onPreloadComplete(videoItem);
                    }
                    Log.d(TAG, "preDownloadVideo: Preload complete for " + videoItem.getUrl());
                } else {
                    Log.d(TAG, "preDownloadVideo: Unsupported URL scheme: " + proxyUrl);
                    if (listener != null) {
                        listener.onPreloadError(videoItem, new Exception("Unsupported URL scheme: " + proxyUrl));
                    }
                }
            } catch (IOException | InterruptedException e) {
                Log.d(TAG, "preDownloadVideo: Error preloading " + videoItem.getUrl(), e);
                if (listener != null) {
                    listener.onPreloadError(videoItem, e);
                }
            } finally {
                if (connection instanceof HttpURLConnection) {
                    ((HttpURLConnection) connection).disconnect();
                }
            }
        }).start();
    }
}
