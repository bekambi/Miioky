package com.bekambimouen.miiokychallenge.full_img_and_vid_simple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class SimpleFullProfilPhoto extends AppCompatActivity {
    private static final String TAG = "SimpleFullProfilPhoto";

    // vars
    private SimpleFullProfilPhoto context;
    private String img_url;
    private RelativeLayout parent;
    private String from_bottom_sheet;

    private void getBlackTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_simple_full_profil_photo);
        context = this;
        getBlackTheme();

        try {
            from_bottom_sheet = getIntent().getStringExtra("from_bottom_sheet");
            img_url = getIntent().getStringExtra("img_url");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        closeKeyboard();

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

    private void init() {
        ImageView back = findViewById(R.id.back);
        parent = findViewById(R.id.parent);
        // pour agrandir et rÃ©duire l'image (import graddle)
        PhotoView photoView = findViewById(R.id.iv_photo);

        if (img_url != null) {
            Glide.with(context.getApplicationContext())
                    .load(img_url)
                    .placeholder(R.color.black)
                    .into(photoView);
        }

        back.setOnClickListener(v -> finish());
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
    public void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

}