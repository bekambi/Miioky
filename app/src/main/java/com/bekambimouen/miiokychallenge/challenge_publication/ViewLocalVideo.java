package com.bekambimouen.miiokychallenge.challenge_publication;



import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;

public class ViewLocalVideo extends AppCompatActivity {
    private static final String TAG = "ViewLocalVideo";

    // widgets
    private ImageView img_play, img_pause;
    private VideoView videoView;
    private RelativeLayout parent;

    // vars
    private ViewLocalVideo context;
    private String video_url;

    private void getBlackTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
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
            window.setNavigationBarColor(getColor(R.color.black));//black or white
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // changer a couleur des icons en noir
        getBlackTheme();
        setContentView(R.layout.activity_view_local_video);
        context = this;

        try {
            video_url = getIntent().getStringExtra("video_url");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        parent = findViewById(R.id.parent);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        img_play = findViewById(R.id.img_play);
        img_pause = findViewById(R.id.img_pause);
        videoView = findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);

        videoView.setVideoURI(Uri.parse(video_url));
        videoView.requestFocus();
        videoView.setZOrderOnTop(true);
        videoView.start();

        videoView.setOnPreparedListener(mp -> mp.setLooping(true));

        img_play.setOnClickListener(view -> {
            img_pause.setVisibility(View.VISIBLE);
            img_play.setVisibility(View.GONE);
            videoView.start();
        });

        img_pause.setOnClickListener(view -> {
            img_pause.setVisibility(View.GONE);
            img_play.setVisibility(View.VISIBLE);
            videoView.pause();
        });

        arrowBack.setOnClickListener(view -> {
            videoView.stopPlayback();
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                videoView.stopPlayback();
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}