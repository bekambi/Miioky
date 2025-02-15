package com.bekambimouen.miiokychallenge.profile.in_the_spotlight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.adapter.ImageStatusAdapter;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;
import com.bekambimouen.miiokychallenge.publication_insta.adapter.SelectedImageAdapter;
import com.bekambimouen.miiokychallenge.story.model.Story;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_Story;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_Story_spotlight;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusGallery extends AppCompatActivity {
    private static final String TAG = "StatusGallery";
    // status gallery _______________________________________________________________________
    // constants
    private StatusGallery context;

    // widgets
    private ProgressBar main_progressBar;
    private RelativeLayout parent, relLayout2, relLayout_status_gallery;
    private TextView number;
    private TextView selections;
    private TextView tv_Next;

    // vars
    private ImageStatusAdapter adapter;
    private RecyclerView recyclerView;
    private List<Story_spotlight> imageList;
    private ArrayList<Story_spotlight> selectedImageList;
    private ArrayList<String> selectedImageUrlList;
    private String storyID;
    private boolean isSelectedEmpty = true, isDownloadVisible = false;
    private int nber = 0;

    // download spotlight _______________________________________________________________________
    // widgets
    private CircleImageView cover_photo;
    private EditText add_title;

    // vars
    private SelectedImageAdapter selectedImageAdapter;
    private boolean isScreenEnabled = true;

    private TextView tv_end;
    private ProgressBar progressBar;

    //firebase
    private DatabaseReference myRef;
    private DatabaseReference reference;
    private String user_id;
    private RelativeLayout relLayout_download_spotlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_status_gallery);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        closeKeyboard();

        imageList = new ArrayList<>();
        selectedImageList = new ArrayList<>();
        selectedImageUrlList = new ArrayList<>();

        try {
            storyID = getIntent().getStringExtra("storyID");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init_status_gallery();
        getAllStories();
        init_download_spotlight();
        add_title.clearFocus();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void getAllStories() {
        imageList.clear();
        Query query = myRef.child(getString(R.string.dbname_story))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    Story story = Util_Story.getStory(context, objectMap);

                    Story_spotlight story_spotlight = Util_Story_spotlight.getStory_spotlight(context, objectMap);
                    if (!story.isSuppressed())
                        imageList.add(story_spotlight);
                }

                Collections.reverse(imageList);
                adapter = new ImageStatusAdapter(context, imageList, main_progressBar);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener((position, v) -> {
                    try {
                        if (!imageList.get(position).isSelected()) {
                            selectImage(position);
                        } else {
                            unSelectImage(position);
                        }
                    } catch (ArrayIndexOutOfBoundsException ed) {
                        Log.d(TAG, "onDataChange: error: "+ed.getMessage());
                    }
                });

                // if list is empty
                if (adapter.getItemCount() == 0) {
                    main_progressBar.setVisibility(View.GONE);
                    tv_Next.setVisibility(View.GONE);
                    relLayout2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init_status_gallery() {
        parent = findViewById(R.id.parent);
        main_progressBar = findViewById(R.id.main_progressBar);
        LinearLayout relLayout_cover = findViewById(R.id.relLayout_cover);
        if (storyID != null)
            relLayout_cover.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.pub_RecyclerView_photo);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        relLayout2 = findViewById(R.id.relLayout2);
        relLayout_status_gallery = findViewById(R.id.relLayout_status_gallery);
        number = findViewById(R.id.number);
        selections = findViewById(R.id.selections);
        tv_Next = findViewById(R.id.tv_Next);
        RelativeLayout iv_close = findViewById(R.id.iv_close);

        iv_close.setOnClickListener(view -> {
            if (!isSelectedEmpty) {
                for (int i = 0; i < imageList.size(); i++) {
                    try {
                        if (imageList.get(i).isSelected()) {
                            imageList.get(i).setSelected(false);
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "init_status_gallery: error: "+e.getMessage());
                    }
                }
                selectedImageList.clear();
                nber = 0;
                tv_Next.setTextColor(getColor(R.color.black_semi_transparent));
                tv_Next.setEnabled(false);
                adapter.notifyDataSetChanged();
                isSelectedEmpty = true;

            } else {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        tv_Next.setOnClickListener(view -> {
            isDownloadVisible = true;
            relLayout_status_gallery.setVisibility(View.GONE);
            relLayout_download_spotlight.setVisibility(View.VISIBLE);
            selectedViews();

            Glide.with(context.getApplicationContext())
                    .load(selectedImageList.get(0).getMediaUrl())
                    .into(cover_photo);
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDownloadVisible) {
                    isDownloadVisible = false;
                    relLayout_download_spotlight.setVisibility(View.GONE);
                    relLayout_status_gallery.setVisibility(View.VISIBLE);
                    selectedImageUrlList.clear();
                    selectedImageAdapter.notifyDataSetChanged();

                } else if (!isSelectedEmpty) {
                    for (int i = 0; i < imageList.size(); i++) {
                        try {
                            if (imageList.get(i).isSelected()) {
                                imageList.get(i).setSelected(false);
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBackPressed: error: "+e.getMessage());;
                        }
                    }
                    selectedImageList.clear();
                    nber = 0;
                    tv_Next.setTextColor(getColor(R.color.black_semi_transparent));
                    tv_Next.setEnabled(false);
                    adapter.notifyDataSetChanged();
                    isSelectedEmpty = true;

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    public void selectImage(int position) {
        // Check before add new item in ArrayList;
        if (!selectedImageList.contains(imageList.get(position))) {
            selectedImageList.add(0, imageList.get(position));
            nber++;
            number.setText(String.valueOf(nber));
            isSelectedEmpty = false;

            tv_Next.setEnabled(true);
            if (nber >= 2)
                selections.setText(getString(R.string.selections));

            imageList.get(position).setSelected(true);
            tv_Next.setTextColor(getColor(R.color.blue_500));

            adapter.notifyDataSetChanged();
        }
    }

    // Remove image from selectedImageList
    @SuppressLint("NotifyDataSetChanged")
    public void unSelectImage(int position) {
        for (int i = 0; i < selectedImageList.size(); i++) {
            if (imageList.get(position).getMediaUrl() != null) {
                if (selectedImageList.get(i).equals(imageList.get(position))) {
                    imageList.get(position).setSelected(false);
                    selectedImageList.remove(i);
                    nber--;
                    number.setText(String.valueOf(nber));

                    if (nber < 2)
                        selections.setText(getString(R.string.selection));

                    adapter.notifyDataSetChanged();
                }
                if (selectedImageList.size() <= 0) {
                    tv_Next.setTextColor(getColor(R.color.black_semi_transparent));
                    tv_Next.setEnabled(false);
                }
            }
        }
    }

    // download spotlight _______________________________________________________________
    @SuppressLint("NotifyDataSetChanged")
    private void init_download_spotlight() {
        relLayout_download_spotlight = findViewById(R.id.relLayout_download_spotlight);
        progressBar = findViewById(R.id.progressBar);
        cover_photo = findViewById(R.id.cover_photo);
        add_title = findViewById(R.id.add_title);
        tv_end = findViewById(R.id.tv_end);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);

        if (storyID != null) {
            add_title.setVisibility(View.GONE);
            tv_end.setTextColor(getColor(R.color.blue_500));
            tv_end.setEnabled(true);
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

                for (int i = 0; i < selectedImageList.size(); i++) {
                    addToExistantStory(i, selectedImageList.get(i).getMediaUrl());
                }

            } else {
                String newPhotoKey = myRef.child(getString(R.string.dbname_story_spotlight)).push().getKey();
                assert newPhotoKey != null;
                reference = FirebaseDatabase.getInstance()
                        .getReference(getString(R.string.dbname_story_spotlight))
                        .child(user_id)
                        .child(newPhotoKey);

                String title = add_title.getText().toString();
                for (int i = 0; i < selectedImageList.size(); i++) {

                    addPhotosToDatabase(i, title, selectedImageList.get(i).getMediaUrl(), newPhotoKey);
                }
            }
        });

        arrowBack.setOnClickListener(view -> {
            if (isDownloadVisible) {
                isDownloadVisible = false;
                relLayout_download_spotlight.setVisibility(View.GONE);
                relLayout_status_gallery.setVisibility(View.VISIBLE);
                selectedImageUrlList.clear();
                selectedImageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void selectedViews() {
        RecyclerView recyclerView_selected_view = findViewById(R.id.recyclerView_selected_view);
        recyclerView_selected_view.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView_selected_view.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView_selected_view.setNestedScrollingEnabled(false);

        for (int i = 0; i < selectedImageList.size(); i++) {
            selectedImageUrlList.add(selectedImageList.get(i).getMediaUrl());
        }

        selectedImageAdapter = new SelectedImageAdapter(context, selectedImageUrlList);
        recyclerView_selected_view.setAdapter(selectedImageAdapter);
    }

    // add story to existant story
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
                    if ((i + 1) == selectedImageList.size()) {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
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

            myRef.child(context.getString(R.string.dbname_story_spotlight_cover))
                    .child(user_id)
                    .child(keyID)
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
                    if ((i + 1) == selectedImageList.size()) {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
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
}