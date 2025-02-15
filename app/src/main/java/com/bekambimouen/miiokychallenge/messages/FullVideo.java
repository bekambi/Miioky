package com.bekambimouen.miiokychallenge.messages;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.messages.adapter.FullVideoAdapter;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FullVideo extends AppCompatActivity {
    private static final String TAG = "FullPhotosVideos";

    // widgets
    private RelativeLayout parent;

    // vars
    private FullVideo context;
    private ArrayList<String> listUrl;
    private String url;
    private String from_bottom_sheet;

    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(0);

        //change bottom bar calor
        updateNavigationBarBlackColor(window);
    }

    // set bottom navigation bar color
    protected void updateNavigationBarBlackColor(Window window) {
        if (window == null) {
            return;
        }

        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setNavigationBarColor(context.getColor(R.color.black));//black or white
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);
        context = this;
        getBlackTheme();

        listUrl = new ArrayList<>();

        try {
            from_bottom_sheet = getIntent().getStringExtra("from_bottom_sheet");
            url = getIntent().getStringExtra("video_url");
            listUrl.add(url);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        closeKeyboard();
        if (url != null)
            init();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        parent = findViewById(R.id.parent);
        ImageView arrowBack = findViewById(R.id.arrowBack);

        // widgets
        Container recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FullVideoAdapter adapter = new FullVideoAdapter(context, listUrl);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        arrowBack.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    private void closeKeyboard(){
        if (from_bottom_sheet != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } else {
            View view = context.getCurrentFocus();
            if(view != null){
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    /**
     * Returns a string representing the number of days ago the post was made
     */
    @SuppressLint("SetTextI18n")
    public void getTimestampDifference(long date, TextView tvDate){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        String tps;
        long time = (System.currentTimeMillis() - date);
        if (time >= 2*3600*24000) {
            tps = date_passe;
        } else if (time >= 24*3600000) {
            tps = context.getString(R.string.yesterday);
        } else if (time >= 2*3600000) {
            tps = ((int)(time/(3600000)))+" "+context.getString(R.string.hours_ago);
        } else if (time >= 3600000) {
            tps = context.getString(R.string.an_hour_ago);
        } else if (time >= 120000) {
            tps = (int)(time/60000) + " "+context.getString(R.string.minutes_ago);
        } else {
            tps = context.getString(R.string.just_now);
        }
        tvDate.setText(tps);
    }
}