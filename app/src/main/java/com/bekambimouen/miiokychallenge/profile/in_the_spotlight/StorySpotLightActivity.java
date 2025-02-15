package com.bekambimouen.miiokychallenge.profile.in_the_spotlight;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aghajari.zoomhelper.ZoomHelper;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;
import com.bekambimouen.miiokychallenge.story.UsersHasSawStories;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StorySpotLightActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    private static final String TAG = "StorySpotLightActivity";
    private int counter = 0;
    private long pressTime = 0L;
    long limit = 500L;

    private RelativeLayout parent, relLayout_view_overlay;
    private StoriesProgressView storiesProgressView;
    private ProgressBar mProgressBar;
    private ImageView image, story_photo;
    private TextView story_username, caption;

    private TextView seen_number;

    // vars
    private StorySpotLightActivity context;
    private List<String> images, captionList;
    private List<String> storyids;
    private String user_id, storyID;
    private String viewcount;
    private CustomShareProgressView mProgresDialog;

    private void showLoading(){
        if (mProgresDialog == null)
            mProgresDialog = new CustomShareProgressView(context);
        mProgresDialog.show();
    }

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    // firebase
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_spot_light);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.ActivityStory_image);
        ZoomHelper.Companion.addZoomableView(image);
        ZoomHelper zoomHelper1 = ZoomHelper.Companion.getInstance();
        story_photo = findViewById(R.id.profile_photo);
        story_username = findViewById(R.id.ActivityStory_story_username);
        caption = findViewById(R.id.caption);
        mProgressBar = findViewById(R.id.progressBar);


        LinearLayout linearlayout_views = findViewById(R.id.linearlayout_views);
        linearlayout_views.setVisibility(View.INVISIBLE);
        RelativeLayout r_seen = findViewById(R.id.ActivityStory_r_seen);
        seen_number = findViewById(R.id.ActivityStory_seen_number);
        RelativeLayout relLayout2 = findViewById(R.id.relLayout2);
        RelativeLayout relLayout3 = findViewById(R.id.relLayout3);
        RelativeLayout story_delete = findViewById(R.id.ActivityStory_story_delete);

        user_id = getIntent().getStringExtra("user_id");
        storyID = getIntent().getStringExtra("storyID");

        if (user_id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            linearlayout_views.setVisibility(View.VISIBLE);
        }

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        getStories(user_id);
        userInfo(user_id);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(this, UsersHasSawStories.class);
            intent.putExtra("id", user_id);
            intent.putExtra("storyid", storyids.get(counter));
            intent.putExtra("title", getString(R.string.views));
            intent.putExtra("number",viewcount);
            startActivity(intent);
        });

        story_delete.setOnClickListener(view -> {
            storiesProgressView.pause();
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = v.findViewById(R.id.dialog_title);
            TextView dialog_text = v.findViewById(R.id.dialog_text);
            TextView negativeButton = v.findViewById(R.id.tv_no);
            TextView positiveButton = v.findViewById(R.id.tv_yes);
            builder.setView(v);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.no));
            positiveButton.setText(context.getString(R.string.ok));

            dialog_title.setVisibility(View.GONE);
            dialog_text.setText(Html.fromHtml(context.getString(R.string.are_u_sure_u_want_to_delete)));

            negativeButton.setOnClickListener(view1 -> {
                d.dismiss();
                storiesProgressView.resume();
            });
            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {
                    d.dismiss();
                    showLoading();

                    myRef.child(getString(R.string.dbname_story_spotlight))
                            .child(user_id)
                            .child(storyID)
                            .child(storyids.get(counter))
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, getString(R.string.delete), Toast.LENGTH_SHORT).show();
                                    context.runOnUiThread(() -> mProgresDialog.dismiss());

                                    // to remove the cover when list is empty
                                    if (images.isEmpty()) {
                                        myRef.child(getString(R.string.dbname_story_spotlight_cover))
                                                .child(user_id)
                                                .child(storyID)
                                                .removeValue();
                                    }

                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                }
                            });
                }
            });
        });

        relLayout2.setOnClickListener(view -> {
            storiesProgressView.pause();
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, StatusGallery.class);
            intent.putExtra("storyID", storyID);
            startActivity(intent);
        });

        relLayout3.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                try {
                    // prevent double click
                    Util.preventTwoClick(view);

                    storiesProgressView.pause();
                    // share
                    BottomSheetSharePublication bottomSheetSharePublication = new BottomSheetSharePublication(context, null, user_id, images.get(counter),
                            storyids.get(counter), "from_full_image", "", false);


                    Bundle bundle = new Bundle();
                    bundle.putString("item_view", "image_une");
                    bottomSheetSharePublication.setArguments(bundle);
                    bottomSheetSharePublication.show(context.getSupportFragmentManager(), "TAG");

                } catch (IllegalStateException e) {
                    Log.d(TAG, "onCreate: "+e.getMessage());
                }
            }
        });

        zoomHelper1.addOnZoomStateChangedListener((zoomHelper, view, b) -> {
            if (!b) {
                storiesProgressView.resume();
            }
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

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onNext() {
        mProgressBar.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext())
                .load(images.get(++counter)).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(context, "Failed to load media...", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        caption.setText(captionList.get(counter));
                        storiesProgressView.setStoryDuration(5000);
                        storiesProgressView.startStories(counter);
                        return false;
                    }
                }).into(image);

        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }

    @Override
    public void onPrev() {

        if ((counter - 1) < 0) return;
        mProgressBar.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext()).load(images.get(--counter)).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(context, "Failed to load media...", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        caption.setText(captionList.get(counter));
                        storiesProgressView.setStoryDuration(5000);
                        storiesProgressView.startStories(counter);
                        return false;
                    }
                }).placeholder(R.color.colorPrimaryDark)
                .into(image);

        seenNumber(storyids.get(counter));

    }

    @Override
    public void onComplete() {
        context.getWindow().setExitTransition(new Slide(Gravity.END));
        context.getWindow().setEnterTransition(new Slide(Gravity.START));
        finish();
    }
    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void getStories(String userid){
        // getPicture
        images = new ArrayList<>();
        captionList = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_story_spotlight))
                .child(userid)
                .child(storyID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                captionList.clear();
                storyids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story_spotlight story = snapshot.getValue(Story_spotlight.class);

                    assert story != null;
                    images.add(story.getMediaUrl());
                    storyids.add(story.getStoryid());
                    captionList.add(story.getCaption());
                }

                try {
                    Glide.with(getApplicationContext()).load(images.get(counter)).addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Toast.makeText(context, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    storiesProgressView.setStoriesCount(images.size()); // <- set stories
                                    storiesProgressView.setStoryDuration(5000L); // <- set a story duration
                                    storiesProgressView.setStoriesListener(context); // <- set listener
                                    storiesProgressView.startStories(counter); // <- start progress

                                    caption.setText(captionList.get(counter));
                                    return false;
                                }
                            }).placeholder(R.color.colorPrimaryDark)
                            .into(image);

                    addView(storyids.get(counter));
                    seenNumber(storyids.get(counter));

                } catch (IndexOutOfBoundsException e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo(String userid){
        Query query = FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Glide.with(getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(story_photo);
                    story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addView(String storyid){
        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_story_spotlight))
                .child(user_id)
                .child(storyID)
                .child(storyid)
                .child(getString(R.string.field_views))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(true);
    }

    private void seenNumber(String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_story_spotlight))
                .child(user_id)
                .child(storyID)
                .child(storyid)
                .child(getString(R.string.field_views));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int value = ((int) dataSnapshot.getChildrenCount() - 1);
                seen_number.setText(""+value);
                viewcount = ""+dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    // zoom image in recyclerview
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return ZoomHelper.Companion.getInstance().dispatchTouchEvent(ev, this) || super.dispatchTouchEvent(ev);
    }
}