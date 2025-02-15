package com.bekambimouen.miiokychallenge.market_place.recycler_full_image;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.recycler_full_image.adapter.MarketRecyclerFullImageAdapter;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MarketRecyclerFullImage extends AppCompatActivity {
    private static final String TAG = "MarketRecyclerFullImage";

    // widgets
    private RelativeLayout parent;

    // vars
    private MarketRecyclerFullImage context;
    private MarketModel market_model;
    private int position;


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
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // adjustpan
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_market_recycler_full_image);
        context = this;
        getBlackTheme();

        List<String> url_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
            position = getIntent().getIntExtra("position", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        ImageView back = findViewById(R.id.back);
        PhotoView one_image = findViewById(R.id.one_image);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(position);


        if (market_model.isImageUne()) {
            recyclerView.setVisibility(View.GONE);

            Glide.with(this.getApplicationContext())
                    .load(market_model.getUrli())
                    .into(one_image);

        } else {
            one_image.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(market_model.getUrli()))
                url_list.add(market_model.getUrli());

            if (!TextUtils.isEmpty(market_model.getUrlii()))
                url_list.add(market_model.getUrlii());

            if (!TextUtils.isEmpty(market_model.getUrliii()))
                url_list.add(market_model.getUrliii());

            if (!TextUtils.isEmpty(market_model.getUrliv()))
                url_list.add(market_model.getUrliv());

            if (!TextUtils.isEmpty(market_model.getUrlv()))
                url_list.add(market_model.getUrlv());

            if (!TextUtils.isEmpty(market_model.getUrlvi()))
                url_list.add(market_model.getUrlvi());

            if (!TextUtils.isEmpty(market_model.getUrlvii()))
                url_list.add(market_model.getUrlvii());

            if (!TextUtils.isEmpty(market_model.getUrlviii()))
                url_list.add(market_model.getUrlviii());

            if (!TextUtils.isEmpty(market_model.getUrlix()))
                url_list.add(market_model.getUrlix());

            if (!TextUtils.isEmpty(market_model.getUrlx()))
                url_list.add(market_model.getUrlx());

            if (!TextUtils.isEmpty(market_model.getUrlxi()))
                url_list.add(market_model.getUrlxi());

            if (!TextUtils.isEmpty(market_model.getUrlxii()))
                url_list.add(market_model.getUrlxii());

            if (!TextUtils.isEmpty(market_model.getUrlxiii()))
                url_list.add(market_model.getUrlxiii());

            if (!TextUtils.isEmpty(market_model.getUrlxiv()))
                url_list.add(market_model.getUrlxiv());

            if (!TextUtils.isEmpty(market_model.getUrlxv()))
                url_list.add(market_model.getUrlxv());

            if (!TextUtils.isEmpty(market_model.getUrlxvi()))
                url_list.add(market_model.getUrlxvi());

            if (!TextUtils.isEmpty(market_model.getUrlxvii()))
                url_list.add(market_model.getUrlxvii());

            MarketRecyclerFullImageAdapter adapter = new MarketRecyclerFullImageAdapter(this, url_list);
            recyclerView.setAdapter(adapter);
        }

        back.setOnClickListener(view -> {
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

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}