package com.bekambimouen.miiokychallenge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.bekambimouen.miiokychallenge.Utils.CacheUtils;
import com.android.volley.toolbox.Volley;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.blongho.country_data.World;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = App.class.getSimpleName();

    // offline/online control
    private FirebaseUser user;
    private FirebaseDatabase database;

    // mise en mémoire tampon
    private static HttpProxyCacheServer proxy;

    // for search bar
    private RequestQueue mRequestQueue;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        // to disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(this);
        mInstance = this;
        // api key:
        proxy = new HttpProxyCacheServer(this);

        FirebaseApp.initializeApp(this);
        // persistance
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        database.getReference().keepSynced(true);

        // Initializes the library and loads all countries flag
        World.init(getApplicationContext());

        // offline/online control
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // for search bar
    public static synchronized App getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return proxy == null ? (proxy = app.newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(CacheUtils.getVideoCacheDir(this))
                .maxCacheFilesCount(40)
                .maxCacheSize(1024 * 1024 * 1024)
                .build();
    }

    // offline/online control
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        // new activity created; force its orientation to portrait
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (user != null) {
            try {
                // Set user status to online
                String currentId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.field_onLine), getString(R.string.in_line));
                database.getReference().child(getString(R.string.dbname_presence)).child(currentId).setValue(hashMap);
            } catch (NullPointerException e) {
                Log.d(TAG, "onActivityResumed: "+e.getMessage());
            }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (user != null) {
            // verify if user is online
            try {
                String currentId= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                Date date=new Date();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.field_onLine), getString(R.string.field_offLine));
                hashMap.put("timeStamp", date.getTime());

                database.getReference().child(getString(R.string.dbname_presence)).child(currentId).setValue(hashMap);
            } catch (NullPointerException e) {
                Log.d(TAG, "onActivityPaused: "+e.getMessage());
            }
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
