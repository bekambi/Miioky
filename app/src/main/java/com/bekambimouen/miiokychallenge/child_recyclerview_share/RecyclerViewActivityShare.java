package com.bekambimouen.miiokychallenge.child_recyclerview_share;

import static java.util.Objects.requireNonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.child_recyclerview_share.adapter.RecyclerViewShareAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RecyclerViewActivityShare extends AppCompatActivity {
    private static final String TAG = "RecyclerViewActivityShare";
    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private RecyclerViewActivityShare context;
    private BattleModel battleModel;
    private ArrayList<String> listUrl;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_recycler_view_share);
        context = this;

        listUrl = new ArrayList<>();

        try {
            Gson gson = new Gson();
            battleModel = gson.fromJson(getIntent().getStringExtra("battleModel"), BattleModel.class);
            position = getIntent().getIntExtra("position", 0);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getData();
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
        progressBar = findViewById(R.id.progressBar);
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(position);
    }

    private void getData() {
        if (battleModel != null) {
            // add url to the arrayList
            if (!battleModel.getUrli().isEmpty())
                listUrl.add(battleModel.getUrli());

            if (!battleModel.getUrlii().isEmpty())
                listUrl.add(battleModel.getUrlii());

            if (!battleModel.getUrliii().isEmpty())
                listUrl.add(battleModel.getUrliii());

            if (!battleModel.getUrliv().isEmpty())
                listUrl.add(battleModel.getUrliv());

            if (!battleModel.getUrlv().isEmpty())
                listUrl.add(battleModel.getUrlv());

            if (!battleModel.getUrlvi().isEmpty())
                listUrl.add(battleModel.getUrlvi());

            if (!battleModel.getUrlvii().isEmpty())
                listUrl.add(battleModel.getUrlvii());

            if (!battleModel.getUrlviii().isEmpty())
                listUrl.add(battleModel.getUrlviii());

            if (!battleModel.getUrlix().isEmpty())
                listUrl.add(battleModel.getUrlix());

            if (!battleModel.getUrlx().isEmpty())
                listUrl.add(battleModel.getUrlx());

            if (!battleModel.getUrlxi().isEmpty())
                listUrl.add(battleModel.getUrlxi());

            if (!battleModel.getUrlxii().isEmpty())
                listUrl.add(battleModel.getUrlxii());

            if (!battleModel.getUrlxiii().isEmpty())
                listUrl.add(battleModel.getUrlxiii());

            if (!battleModel.getUrlxiv().isEmpty())
                listUrl.add(battleModel.getUrlxiv());

            if (!battleModel.getUrlxv().isEmpty())
                listUrl.add(battleModel.getUrlxv());

            if (!battleModel.getUrlxvi().isEmpty())
                listUrl.add(battleModel.getUrlxvi());

            if (!battleModel.getUrlxvii().isEmpty())
                listUrl.add(battleModel.getUrlxvii());

            RecyclerViewShareAdapter adapter = new RecyclerViewShareAdapter(context, listUrl, battleModel,
                    progressBar, relLayout_view_overlay);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}