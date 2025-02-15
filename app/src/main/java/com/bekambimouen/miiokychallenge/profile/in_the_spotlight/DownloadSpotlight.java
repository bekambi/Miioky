package com.bekambimouen.miiokychallenge.profile.in_the_spotlight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;
import com.bekambimouen.miiokychallenge.publication_insta.adapter.SelectedImageAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DownloadSpotlight extends AppCompatActivity {
    private static final String TAG = "DownloadSpotlight";

    // widgets
    private RelativeLayout parent, relLayout1;
    private RelativeLayout relLayout2;
    private EditText add_title;
    // constants
    private DownloadSpotlight context;

    // vars
    private ArrayList<Story_spotlight> selectedImageList;
    private ArrayList<String> selectedList;
    private ArrayList<String> displaySelectedList;
    private boolean isScreenEnabled = true;
    private boolean isRelayout2Visible = false;
    private String storyID;

    private TextView tv_end;
    private ProgressBar progressBar;

    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_download_spotlight);
        context = this;
        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        selectedList = new ArrayList<>();
        // to avoid duplicate item in list
        displaySelectedList = new ArrayList<>();

        try {
            selectedImageList = getIntent().getParcelableArrayListExtra("spotlight");

            for (int i = 0; i < Objects.requireNonNull(selectedImageList).size(); i++) {
                selectedList.add(selectedImageList.get(i).getMediaUrl());
                displaySelectedList.add(selectedImageList.get(i).getMediaUrl());


            }
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        try {
            storyID = getIntent().getStringExtra("storyID");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        selectedViews();
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
        parent = findViewById(R.id.parent);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);
        progressBar = findViewById(R.id.progressBar);
        CircleImageView cover_photo = findViewById(R.id.cover_photo);
        add_title = findViewById(R.id.add_title);
        tv_end = findViewById(R.id.tv_end);
        ImageView arrowBack = findViewById(R.id.arrowBack);

        if (storyID != null) {
            add_title.setVisibility(View.GONE);
            tv_end.setTextColor(getColor(R.color.blue_500));
            tv_end.setEnabled(true);
        }

        if (selectedImageList != null) {
            if (!isFinishing()) {
                Glide.with(context)
                        .load(selectedList.get(0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE )
                        .skipMemoryCache(true)
                        .into(cover_photo);
            }
        }

        add_title.requestFocus();
        add_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    tv_end.setTextColor(getColor(R.color.blue_500));
                    tv_end.setEnabled(true);
                } else {
                    tv_end.setTextColor(getColor(R.color.black_semi_transparent));
                    tv_end.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tv_end.setOnClickListener(view -> {
            closeKeyboard();
            isScreenEnabled = false;
            progressBar.setVisibility(View.VISIBLE);
            if (storyID != null) {
                reference = FirebaseDatabase.getInstance()
                        .getReference(getString(R.string.dbname_story_spotlight))
                        .child(user_id)
                        .child(storyID);

                for (int i = 0; i < selectedList.size(); i++) {
                    addToExistantStory(i, selectedList.get(i));
                }

            } else {
                String newPhotoKey = myRef.child(getString(R.string.dbname_story_spotlight)).push().getKey();
                assert newPhotoKey != null;
                reference = FirebaseDatabase.getInstance()
                        .getReference(getString(R.string.dbname_story_spotlight))
                        .child(user_id)
                        .child(newPhotoKey);

                String title = add_title.getText().toString();
                for (int i = 0; i < selectedList.size(); i++) {

                    addPhotosToDatabase(i, title, selectedList.get(i), newPhotoKey);
                }
            }
        });

        arrowBack.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isRelayout2Visible) {
                    isRelayout2Visible = false;
                    relLayout1.setVisibility(View.VISIBLE);
                    relLayout2.setVisibility(View.GONE);
                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectedViews() {
        RecyclerView recyclerView_selected_view = findViewById(R.id.recyclerView_selected_view);
        recyclerView_selected_view.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView_selected_view.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView_selected_view.setNestedScrollingEnabled(false);
        recyclerView_selected_view.setItemAnimator(null);
        recyclerView_selected_view.setHasFixedSize(true);

        SelectedImageAdapter selectedImageAdapter = new SelectedImageAdapter(context, displaySelectedList);
        recyclerView_selected_view.setAdapter(selectedImageAdapter);
        selectedImageAdapter.notifyDataSetChanged();

    }

    private void addToExistantStory(int i, String url) {
        String newPhotoKey = myRef.child(getString(R.string.dbname_story_spotlight)).push().getKey();

        Story_spotlight spotlight = new Story_spotlight();
        spotlight.setMediaUrl(url);
        spotlight.setStoryid(newPhotoKey);
        spotlight.setUser_id(selectedImageList.get(i).getUser_id());
        spotlight.setCaption(selectedImageList.get(i).getCaption());
        // suppress
        spotlight.setSuppressed(false);

        assert newPhotoKey != null;
        reference.child(newPhotoKey)
                .setValue(spotlight)
                .addOnCompleteListener(task -> {
                    if ((i + 1) == selectedList.size()) {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Profile.class);
                        startActivity(intent);
                    }
                });
    }

    private void addPhotosToDatabase(int i, String title, String url, String keyID) {
        // cover______________________________________________________________________
        if (i == 0) {
            SpolightCover spolightCover = new SpolightCover();
            spolightCover.setUser_id(user_id);
            spolightCover.setMediaUrl(url);
            spolightCover.setStoryid(keyID);
            spolightCover.setTitle(title);

            DatabaseReference myRef = FirebaseDatabase.getInstance()
                    .getReference(context.getString(R.string.dbname_story_spotlight_cover))
                    .child(user_id);

            myRef.child(keyID)
                    .setValue(spolightCover);
        }
        // ___________________________________________________________________________

        String newPhotoKey = myRef.child(getString(R.string.dbname_story_spotlight)).push().getKey();

        Story_spotlight spotlight = new Story_spotlight();
        spotlight.setMediaUrl(url);
        spotlight.setStoryid(newPhotoKey);
        spotlight.setUser_id(selectedImageList.get(i).getUser_id());
        spotlight.setCaption(selectedImageList.get(i).getCaption());
        // suppress
        spotlight.setSuppressed(false);

        assert newPhotoKey != null;
        reference.child(newPhotoKey)
                .setValue(spotlight)
                .addOnCompleteListener(task -> {
                    if ((i + 1) == selectedList.size()) {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Profile.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        isScreenEnabled = true;
    }
}