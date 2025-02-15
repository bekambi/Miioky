package com.bekambimouen.miiokychallenge.story;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.bottomsheet.BottomSheetAddSpotlight;
import com.bekambimouen.miiokychallenge.story.model.Story;
import com.bekambimouen.miiokychallenge.story.publication.PublicationStories;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    private static final String TAG = "StoryActivity";

    private int counter = 0;
    private long pressTime = 0L;
    long limit = 500L;

    private StoriesProgressView storiesProgressView;
    private ProgressBar mProgressBar;
    private ImageView image, story_photo;
    private TextView story_username, caption, tps_publication;
    private RelativeLayout parent;

    private TextView seen_number;

    // vars
    private StoryActivity context;
    private BottomSheetAddSpotlight bottomSheetAddSpotlight;
    private List<String> images, captionList;
    private List<Long> time_list;
    private List<String> storyids;
    private String userid;
    private String viewcount;

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

    //firebase
    private FirebaseDatabase database;

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
        // inside in create method below line is use to make a full screen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story);
        context = this;
        getBlackTheme();
        database=FirebaseDatabase.getInstance();

        parent = findViewById(R.id.parent);
        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.ActivityStory_image);
        ZoomHelper.Companion.addZoomableView(image);
        ZoomHelper zoomHelper1 = ZoomHelper.Companion.getInstance();
        //zoomHelper1.setLayoutTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        story_photo = findViewById(R.id.profile_photo);
        story_username = findViewById(R.id.ActivityStory_story_username);
        caption = findViewById(R.id.caption);
        tps_publication = findViewById(R.id.tps_publication);
        mProgressBar = findViewById(R.id.progressBar);

        LinearLayout linearlayout_views = findViewById(R.id.linearlayout_views);
        linearlayout_views.setVisibility(View.GONE);
        RelativeLayout relLayout_add_story = findViewById(R.id.relLayout_add_story);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        RelativeLayout r_seen = findViewById(R.id.ActivityStory_r_seen);
        seen_number = findViewById(R.id.ActivityStory_seen_number);
        RelativeLayout relLayout_spotlight = findViewById(R.id.relLayout_spotlight);


        userid = getIntent().getStringExtra("userid");

        assert userid != null;
        if (userid.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            linearlayout_views.setVisibility(View.VISIBLE);
        }

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        getStories(userid);
        userInfo(userid);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(StoryActivity.this, UsersHasSawStories.class);
            intent.putExtra("id", userid);
            intent.putExtra("storyid", storyids.get(counter));
            intent.putExtra("title", getString(R.string.views));
            intent.putExtra("number",viewcount);
            startActivity(intent);
        });

        menu_item.setOnClickListener(view -> {
            storiesProgressView.pause();
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.more_story);
            //adding click listener
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_delete) {
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

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)+" ?"));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.are_u_sure_u_want_to_delete)));

                    negativeButton.setOnClickListener(view2 -> {
                        d.dismiss();
                        storiesProgressView.resume();
                    });

                    positiveButton.setOnClickListener(view2 -> {
                        // internet control
                        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                        if (!isConnected) {
                            CheckInternetStatus.internetIsConnected(context, parent);

                        } else {
                            d.dismiss();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", true);

                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                    .getReference(getString(R.string.dbname_story))
                                    .child(userid)
                                    .child(storyids.get(counter));

                            reference.updateChildren(map).addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    d.dismiss();
                                    storiesProgressView.resume();
                                }
                            });
                        }
                    });

                } else {
                    if (menuItem.getItemId() == R.id.menu_share) {
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
                                BottomSheetSharePublication bottomSheetSharePublication = new BottomSheetSharePublication(context, null, userid, images.get(counter),
                                        storyids.get(counter), "from_full_image", "", false);


                                Bundle bundle = new Bundle();
                                bundle.putString("item_view", "image_une");
                                bottomSheetSharePublication.setArguments(bundle);
                                bottomSheetSharePublication.show(context.getSupportFragmentManager(), "TAG");

                            } catch (IllegalStateException e) {
                                Log.d(TAG, "onCreate: "+e.getMessage());
                            }
                        }
                    }
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });

        relLayout_spotlight.setOnClickListener(view -> {
            storiesProgressView.pause();
            bottomSheetAddSpotlight.show(getSupportFragmentManager(), "TAG");
        });

        relLayout_add_story.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, PublicationStories.class);
            context.startActivity(intent);
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

                        // it's today. show the label "today"
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        if (TimeUtils.isDateToday(time_list.get(counter))) {
                            tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                                    +sdf.format(time_list.get(counter))));
                        } else {
                            // it's not today. shows the week of day label
                            tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                                    +sdf.format(time_list.get(counter))));
                        }

                        bottomSheetAddSpotlight = new BottomSheetAddSpotlight(context, storiesProgressView, userid,
                                storyids.get(counter), images.get(counter), captionList.get(counter));
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

                        // it's today. show the label "today"
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        if (TimeUtils.isDateToday(time_list.get(counter))) {
                            tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                                    +sdf.format(time_list.get(counter))));
                        } else {
                            // it's not today. shows the week of day label
                            tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                                    +sdf.format(time_list.get(counter))));
                        }

                        bottomSheetAddSpotlight = new BottomSheetAddSpotlight(context, storiesProgressView, userid,
                                storyids.get(counter), images.get(counter), captionList.get(counter));
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
        time_list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_story))
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                captionList.clear();
                storyids.clear();
                time_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    long timecurrent = System.currentTimeMillis();
                    assert story != null;
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        if (!story.isSuppressed()) {
                            images.add(story.getMediaUrl());
                            storyids.add(story.getStoryid());
                            captionList.add(story.getCaption());
                            time_list.add(story.getTimestart());
                        }
                    }
                }

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
                                storiesProgressView.setStoriesListener(StoryActivity.this); // <- set listener
                                storiesProgressView.startStories(counter); // <- start progress

                                caption.setText(captionList.get(counter));
                                // it's today. show the label "today"
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                if (TimeUtils.isDateToday(time_list.get(counter))) {
                                    tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                                            +sdf.format(time_list.get(counter))));
                                } else {
                                    // it's not today. shows the week of day label
                                    tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                                            +sdf.format(time_list.get(counter))));
                                }

                                bottomSheetAddSpotlight = new BottomSheetAddSpotlight(context, storiesProgressView, userid,
                                        storyids.get(counter), images.get(counter), captionList.get(counter));
                                return false;
                            }
                        }).placeholder(R.color.colorPrimaryDark)
                        .into(image);

                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    //
    private void addView(String storyid){
        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_story))
                .child(userid)
                .child(storyid)
                .child(getString(R.string.field_views))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(true);
    }

    private void seenNumber(String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_story))
                .child(userid)
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
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    // zoom image in recyclerview
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return ZoomHelper.Companion.getInstance().dispatchTouchEvent(ev, this) || super.dispatchTouchEvent(ev);
    }
}