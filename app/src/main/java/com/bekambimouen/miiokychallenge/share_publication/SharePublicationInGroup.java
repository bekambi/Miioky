package com.bekambimouen.miiokychallenge.share_publication;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.ShareUtils;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SharePublicationInGroup extends AppCompatActivity {
    private static final String TAG = "SharePublicationInGroup";
    // widgets
    private ProgressBar progressBar;
    private FrameLayout frame_layout;
    private LinearLayout parent;

    // vars
    private SharePublicationInGroup context;
    private BattleModel model;
    private UserGroups userGroup;
    private ArrayList<String> listUrl, members_id_list;
    private String value;
    private boolean isScreenEnabled = true;

    // share
    // group
    private boolean group_share_single_bottom_circle_image;
    private boolean group_share_single_bottom_image_double;
    private boolean group_share_single_bottom_image_une;
    private boolean group_share_single_bottom_recycler;
    private boolean group_share_single_bottom_response_image_double;
    private boolean group_share_single_bottom_response_video_double;
    private boolean group_share_single_bottom_video_double;
    private boolean group_share_single_bottom_video_une;

    private boolean group_share_top_bottom_circle_image;
    private boolean group_share_top_bottom_image_double;
    private boolean group_share_top_bottom_image_une;
    private boolean group_share_top_bottom_recycler;
    private boolean group_share_top_bottom_response_image_double;
    private boolean group_share_top_bottom_response_video_double;
    private boolean group_share_top_bottom_video_double;
    private boolean group_share_top_bottom_video_une;

    private boolean group_share_single_top_circle_image;
    private boolean group_share_single_top_image_double;
    private boolean group_share_single_top_image_une;
    private boolean group_share_single_top_recycler;
    private boolean group_share_single_top_response_image_double;
    private boolean group_share_single_top_response_video_double;
    private boolean group_share_single_top_video_double;
    private boolean group_share_single_top_video_une;

    private boolean group_circle_image;
    private boolean group_image_double;
    private boolean group_image_une;
    private boolean group_recycler;
    private boolean group_response_image_double;
    private boolean group_response_video_double;
    private boolean group_video_double;
    private boolean group_video_une;

    // miioky
    private boolean user_profile_shared;
    private boolean recyclerItem_shared;
    private boolean imageUne_shared;
    private boolean videoUne_shared;
    private boolean imageDouble_shared;
    private boolean videoDouble_shared;
    private boolean reponseImageDouble_shared;
    private boolean reponseVideoDouble_shared;

    private boolean circle_image;
    private boolean image_double;
    private boolean image_une;
    private boolean recycler;
    private boolean response_image_double;
    private boolean response_video_double;
    private boolean video_double;
    private boolean video_une;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_share_publication_in_group);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        listUrl = new ArrayList<>();
        members_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            model = gson.fromJson(getIntent().getStringExtra("model"), BattleModel.class);
            userGroup = gson.fromJson(getIntent().getStringExtra("userGroup"), UserGroups.class);
            value = getIntent().getStringExtra("value");

            group_share_single_bottom_circle_image = getIntent().getBooleanExtra("group_share_single_bottom_circle_image", false);
            group_share_single_bottom_image_double = getIntent().getBooleanExtra("group_share_single_bottom_image_double", false);
            group_share_single_bottom_image_une = getIntent().getBooleanExtra("group_share_single_bottom_image_une", false);
            group_share_single_bottom_recycler = getIntent().getBooleanExtra("group_share_single_bottom_recycler", false);
            group_share_single_bottom_response_image_double = getIntent().getBooleanExtra("group_share_single_bottom_response_image_double", false);
            group_share_single_bottom_response_video_double = getIntent().getBooleanExtra("group_share_single_bottom_response_video_double", false);
            group_share_single_bottom_video_double = getIntent().getBooleanExtra("group_share_single_bottom_video_double", false);
            group_share_single_bottom_video_une = getIntent().getBooleanExtra("group_share_single_bottom_video_une", false);

            group_share_single_top_circle_image = getIntent().getBooleanExtra("group_share_single_top_circle_image", false);
            group_share_single_top_image_double = getIntent().getBooleanExtra("group_share_single_top_image_double", false);
            group_share_single_top_image_une = getIntent().getBooleanExtra("group_share_single_top_image_une", false);
            group_share_single_top_recycler = getIntent().getBooleanExtra("group_share_single_top_recycler", false);
            group_share_single_top_response_image_double = getIntent().getBooleanExtra("group_share_single_top_response_image_double", false);
            group_share_single_top_response_video_double = getIntent().getBooleanExtra("group_share_single_top_response_video_double", false);
            group_share_single_top_video_double = getIntent().getBooleanExtra("group_share_single_top_video_double", false);
            group_share_single_top_video_une = getIntent().getBooleanExtra("group_share_single_top_video_une", false);

            group_share_top_bottom_circle_image = getIntent().getBooleanExtra("group_share_top_bottom_circle_image", false);
            group_share_top_bottom_image_double = getIntent().getBooleanExtra("group_share_top_bottom_image_double", false);
            group_share_top_bottom_image_une = getIntent().getBooleanExtra("group_share_top_bottom_image_une", false);
            group_share_top_bottom_recycler = getIntent().getBooleanExtra("group_share_top_bottom_recycler", false);
            group_share_top_bottom_response_image_double = getIntent().getBooleanExtra("group_share_top_bottom_response_image_double", false);
            group_share_top_bottom_response_video_double = getIntent().getBooleanExtra("group_share_top_bottom_response_video_double", false);
            group_share_top_bottom_video_double = getIntent().getBooleanExtra("group_share_top_bottom_video_double", false);
            group_share_top_bottom_video_une = getIntent().getBooleanExtra("group_share_top_bottom_video_une", false);

            user_profile_shared = getIntent().getBooleanExtra("user_profile_shared", false);
            recyclerItem_shared = getIntent().getBooleanExtra("recyclerItem_shared", false);
            imageUne_shared = getIntent().getBooleanExtra("imageUne_shared", false);
            videoUne_shared = getIntent().getBooleanExtra("videoUne_shared", false);
            imageDouble_shared = getIntent().getBooleanExtra("imageDouble_shared", false);
            videoDouble_shared = getIntent().getBooleanExtra("videoDouble_shared", false);
            reponseImageDouble_shared = getIntent().getBooleanExtra("reponseImageDouble_shared", false);
            reponseVideoDouble_shared = getIntent().getBooleanExtra("reponseVideoDouble_shared", false);

            group_circle_image = getIntent().getBooleanExtra("group_circle_image", false);
            group_image_double = getIntent().getBooleanExtra("group_image_double", false);
            group_image_une = getIntent().getBooleanExtra("group_image_une", false);
            group_recycler = getIntent().getBooleanExtra("group_recycler", false);
            group_response_image_double = getIntent().getBooleanExtra("group_response_image_double", false);
            group_response_video_double = getIntent().getBooleanExtra("group_response_video_double", false);
            group_video_double = getIntent().getBooleanExtra("group_video_double", false);
            group_video_une = getIntent().getBooleanExtra("group_video_une", false);

            circle_image = getIntent().getBooleanExtra("circle_image", false);
            image_double = getIntent().getBooleanExtra("image_double", false);
            image_une = getIntent().getBooleanExtra("image_une", false);
            recycler = getIntent().getBooleanExtra("recycler", false);
            response_image_double = getIntent().getBooleanExtra("response_image_double", false);
            response_video_double = getIntent().getBooleanExtra("response_video_double", false);
            video_double = getIntent().getBooleanExtra("video_double", false);
            video_une = getIntent().getBooleanExtra("video_une", false);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

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
        Toolbar toolBar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        TextView toolBar_group_name = findViewById(R.id.toolBar_group_name);
        RelativeLayout relLayout_arrowBack = findViewById(R.id.relLayout_arrowBack);
        ImageView arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        TextView tv_publish = findViewById(R.id.tv_publish);
        ImageView thumbnail_un = findViewById(R.id.thumbnail_un);
        ImageView thumbnail_deux = findViewById(R.id.thumbnail_deux);
        ImageView thumbnail = findViewById(R.id.thumbnail);
        CircleImageView circle_img = findViewById(R.id.circle_img);
        RelativeLayout relLayout_img_play_un = findViewById(R.id.relLayout_img_play_un);
        RelativeLayout relLayout_img_play_deux = findViewById(R.id.relLayout_img_play_deux);
        RelativeLayout relLayout_img_play = findViewById(R.id.relLayout_img_play);
        RelativeLayout relLayout_img_double = findViewById(R.id.relLayout_img_double);
        RelativeLayout relLayout_img_une = findViewById(R.id.relLayout_img_une);
        RelativeLayout relLayout_recycler = findViewById(R.id.relLayout_recycler);
        RelativeLayout relLayout_cover_img = findViewById(R.id.relLayout_cover_img);
        frame_layout = findViewById(R.id.frame_layout);
        MyEditText edit_caption = findViewById(R.id.edit_caption);

        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(userGroup.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(userGroup.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    String theme = user_groups.getGroup_theme();
                    toolBar_group_name.setText(Html.fromHtml(context.getString(R.string.share_in)+" "+user_groups.getGroup_name()));

                    if (theme.equals(context.getString(R.string.theme_normal))) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(Color.WHITE);

                        // changer a couleur des icons en noir
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                        toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
                        toolBar_group_name.setTextColor(getColor(R.color.black));
                        relLayout_arrowBack.setBackgroundResource(R.drawable.selector_circle);
                        arrowBack.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                        relLayout_publish.setBackgroundResource(R.drawable.selector_2);
                        tv_publish.setTextColor(getColor(R.color.black));

                    } else if (theme.equals(context.getString(R.string.theme_blue))) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getColor(R.color.blue_600));

                        // changer a couleur des icons en blanc
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                        toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
                        toolBar_group_name.setTextColor(getColor(R.color.white));
                        relLayout_arrowBack.setBackgroundResource(R.drawable.selector_circle2);
                        arrowBack.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        relLayout_publish.setBackgroundResource(R.drawable.selector_1);
                        tv_publish.setTextColor(getColor(R.color.white));

                    } else if (theme.equals(context.getString(R.string.theme_brown))) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getColor(R.color.background_brown));

                        // changer a couleur des icons en blanc
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                        toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
                        toolBar_group_name.setTextColor(getColor(R.color.white));
                        relLayout_arrowBack.setBackgroundResource(R.drawable.selector_circle2);
                        arrowBack.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        relLayout_publish.setBackgroundResource(R.drawable.selector_1);
                        tv_publish.setTextColor(getColor(R.color.white));

                    } else if (theme.equals(context.getString(R.string.theme_pink))) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getColor(R.color.pink));

                        // changer a couleur des icons en blanc
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                        toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
                        toolBar_group_name.setTextColor(getColor(R.color.white));
                        relLayout_arrowBack.setBackgroundResource(R.drawable.selector_circle2);
                        arrowBack.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        relLayout_publish.setBackgroundResource(R.drawable.selector_1);
                        tv_publish.setTextColor(getColor(R.color.white));

                    } else if (theme.equals(context.getString(R.string.theme_green))) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getColor(R.color.vertWatsApp));

                        // changer a couleur des icons en blanc
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                        toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
                        toolBar_group_name.setTextColor(getColor(R.color.white));
                        relLayout_arrowBack.setBackgroundResource(R.drawable.selector_circle2);
                        arrowBack.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        relLayout_publish.setBackgroundResource(R.drawable.selector_1);
                        tv_publish.setTextColor(getColor(R.color.white));

                    } else if (theme.equals(context.getString(R.string.theme_black))) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getColor(R.color.black));

                        // changer a couleur des icons en blanc
                        View decor = getWindow().getDecorView();
                        decor.setSystemUiVisibility(0);

                        toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
                        toolBar_group_name.setTextColor(getColor(R.color.white));
                        relLayout_arrowBack.setBackgroundResource(R.drawable.selector_circle2);
                        arrowBack.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        relLayout_publish.setBackgroundResource(R.drawable.selector_1);
                        tv_publish.setTextColor(getColor(R.color.white));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (value.equals("recycler")) {
            relLayout_recycler.setVisibility(View.VISIBLE);

            listUrl.clear();
            // add url to the arrayList
            if (!model.getUrli().isEmpty())
                listUrl.add(model.getUrli());

            if (!model.getUrlii().isEmpty())
                listUrl.add(model.getUrlii());

            if (!model.getUrliii().isEmpty())
                listUrl.add(model.getUrliii());

            if (!model.getUrliv().isEmpty())
                listUrl.add(model.getUrliv());

            if (!model.getUrlv().isEmpty())
                listUrl.add(model.getUrlv());

            if (!model.getUrlvi().isEmpty())
                listUrl.add(model.getUrlvi());

            if (!model.getUrlvii().isEmpty())
                listUrl.add(model.getUrlvii());

            if (!model.getUrlviii().isEmpty())
                listUrl.add(model.getUrlviii());

            if (!model.getUrlix().isEmpty())
                listUrl.add(model.getUrlix());

            if (!model.getUrlx().isEmpty())
                listUrl.add(model.getUrlx());

            if (!model.getUrlxi().isEmpty())
                listUrl.add(model.getUrlxi());

            if (!model.getUrlxii().isEmpty())
                listUrl.add(model.getUrlxii());

            if (!model.getUrlxiii().isEmpty())
                listUrl.add(model.getUrlxiii());

            if (!model.getUrlxiv().isEmpty())
                listUrl.add(model.getUrlxiv());

            if (!model.getUrlxv().isEmpty())
                listUrl.add(model.getUrlxv());

            if (!model.getUrlxvi().isEmpty())
                listUrl.add(model.getUrlxvi());

            if (!model.getUrlxvii().isEmpty())
                listUrl.add(model.getUrlxvii());

            gridImages(listUrl);
        }

        if (value.equals("video_une")) {
            relLayout_img_une.setVisibility(View.VISIBLE);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail())
                    .into(thumbnail);
        }

        if (value.equals("image_une")) {
            if (!model.getUrl().isEmpty()) {
                relLayout_img_une.setVisibility(View.VISIBLE);
                relLayout_img_play.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext())
                        .load(model.getUrl())
                        .centerCrop()
                        .into(thumbnail);

            } else if (!model.getUser_profile_photo().isEmpty()) {
                relLayout_cover_img.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(model.getUser_profile_photo())
                        .centerCrop()
                        .into(circle_img);
            } else if (!model.getGroup_user_background_full_profile_url().isEmpty()) {
                relLayout_cover_img.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(model.getGroup_user_background_full_profile_url())
                        .centerCrop()
                        .into(circle_img);
            } else if (!model.getGroup_full_profile_photo().isEmpty()) {
                relLayout_cover_img.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(model.getGroup_full_profile_photo())
                        .centerCrop()
                        .into(circle_img);
            }
        }

        if (value.equals("video_double")) {
            relLayout_img_double.setVisibility(View.VISIBLE);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_un())
                    .into(thumbnail_un);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_deux())
                    .into(thumbnail_deux);
        }

        if (value.equals("image_double")) {
            relLayout_img_double.setVisibility(View.VISIBLE);
            relLayout_img_play_un.setVisibility(View.GONE);
            relLayout_img_play_deux.setVisibility(View.GONE);

            Glide.with(context.getApplicationContext())
                    .load(model.getUrlUn())
                    .into(thumbnail_un);

            Glide.with(context.getApplicationContext())
                    .load(model.getUrlDeux())
                    .into(thumbnail_deux);
        }

        if (value.equals("response_video_double")) {
            relLayout_img_double.setVisibility(View.VISIBLE);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_response())
                    .into(thumbnail_un);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_invite())
                    .into(thumbnail_deux);
        }

        if (value.equals("response_image_double")) {
            relLayout_img_double.setVisibility(View.VISIBLE);
            relLayout_img_play_un.setVisibility(View.GONE);
            relLayout_img_play_deux.setVisibility(View.GONE);

            Glide.with(context.getApplicationContext())
                    .load(model.getReponse_url())
                    .into(thumbnail_un);

            Glide.with(context.getApplicationContext())
                    .load(model.getInvite_url())
                    .into(thumbnail_deux);
        }

        // to know if the member admin post approval
        Query query3 = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(userGroup.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    GroupFollowersFollowing follower = new GroupFollowersFollowing();

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    follower.setPublicationApprobation(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_publicationApprobation))).toString())));

                    if (follower.isPublicationApprobation()) {
                        relLayout_publish.setOnClickListener(view -> {
                            // show dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                            TextView dialog_title = v.findViewById(R.id.dialog_title);
                            TextView dialog_text = v.findViewById(R.id.dialog_text);
                            TextView negativeButton = v.findViewById(R.id.tv_no);
                            TextView positiveButton = v.findViewById(R.id.tv_yes);
                            builder.setView(v);

                            Dialog d = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                            InsetDrawable inset = new InsetDrawable(back, 50);
                            Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(inset);
                            d.show();

                            negativeButton.setText(context.getString(R.string.cancel));
                            positiveButton.setText(context.getString(R.string.ok));

                            dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                            dialog_text.setText(Html.fromHtml(context.getString(R.string.posts_will_be_submitted_to_admin_before_display)));

                            negativeButton.setOnClickListener(view2 -> d.dismiss());

                            positiveButton.setOnClickListener(view1 -> {
                                // internet control
                                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                                if (!isConnected) {
                                    CheckInternetStatus.internetIsConnected(context, parent);

                                } else {
                                    closeKeyboard();
                                    isScreenEnabled = false;
                                    edit_caption.clearFocus();
                                    String caption = Objects.requireNonNull(edit_caption.getText()).toString();
                                    progressBar.setVisibility(View.VISIBLE);
                                    downloadData(caption);
                                }
                                d.dismiss();
                            });
                        });

                    } else {
                        // upload data on firebase
                        relLayout_publish.setOnClickListener(view -> {
                            // internet control
                            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                            if (!isConnected) {
                                CheckInternetStatus.internetIsConnected(context, parent);

                            } else {
                                closeKeyboard();
                                isScreenEnabled = false;
                                edit_caption.clearFocus();
                                String caption = Objects.requireNonNull(edit_caption.getText()).toString();
                                progressBar.setVisibility(View.VISIBLE);
                                downloadData(caption);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (userGroup.getAdmin_master().equals(user_id)) {
            // upload data on firebase
            relLayout_publish.setOnClickListener(view -> {
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {
                    closeKeyboard();
                    isScreenEnabled = false;
                    edit_caption.clearFocus();
                    String caption = Objects.requireNonNull(edit_caption.getText()).toString();
                    progressBar.setVisibility(View.VISIBLE);
                    downloadData(caption);
                }
            });
        }

        relLayout_arrowBack.setOnClickListener(view -> {
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

    private void gridImages(ArrayList<String> urlList) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int sst=width-20;
        Log.d("Width is",String.valueOf(sst));
        Log.d("Width is",String.valueOf(width));
        int i;
        i = urlList.size();
        frame_layout.removeAllViews();

        if(i==2)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 800));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlii())
                    .centerCrop()
                    .into(imageView2);

            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 800));
            imageView2.setX((float) width /2);
            imageView2.setPadding(7,0,0,0);
            frame_layout.addView(imageView1);
            frame_layout.addView(imageView2);
        }
        if(i==3)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 600));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlii())
                    .centerCrop()
                    .into(imageView2);

            imageView2.setX((float) width /2);
            imageView2.setPadding(7,0,0,0);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 300));

            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliii())
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setY(300);
            imageView3.setPadding(7,7,0,0);
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2, 300));
            frame_layout.addView(imageView1);
            frame_layout.addView(imageView2);
            frame_layout.addView(imageView3);
        }
        if(i==4)
        {
            //x=0,y=0
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .centerCrop()
                    .into(imageView1);

            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2,600));
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            //x=200,y==0
            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlii())
                    .centerCrop()
                    .into(imageView2);

            imageView2.setX((float) width/2);
            imageView2.setPadding(7,7,0,0);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliii())
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setY(200);
            imageView3.setPadding(7,7,0,0);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView4= new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliv())
                    .centerCrop()
                    .into(imageView4);

            imageView4.setX((float) width/2);
            imageView4.setY(400);
            imageView4.setPadding(7,7,0,0);
            imageView4.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

            frame_layout.addView(imageView1);
            frame_layout.addView(imageView2);
            frame_layout.addView(imageView3);
            frame_layout.addView(imageView4);
        }
        if(i==5)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 300));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlii())
                    .centerCrop()
                    .into(imageView2);

            imageView2.setY(300);
            imageView2.setPadding(0,7,0,0);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 300));

            //x=200,y==0
            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliii())
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setPadding(7,0,0,0);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView4 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliv())
                    .centerCrop()
                    .into(imageView4);

            imageView4.setX((float) width/2);
            imageView4.setY(200);
            imageView4.setPadding(7,7,0,0);
            imageView4.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView5= new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlv())
                    .centerCrop()
                    .into(imageView5);

            imageView5.setX((float) width/2);
            imageView5.setY(400);
            imageView5.setPadding(7,7,0,0);
            imageView5.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView5.setScaleType(ImageView.ScaleType.FIT_XY);

            frame_layout.addView(imageView1);
            frame_layout.addView(imageView2);
            frame_layout.addView(imageView3);
            frame_layout.addView(imageView4);
            frame_layout.addView(imageView5);
        }
        if(i>5)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 300));

            ImageView imageView2 = new ImageView(context);
            if (!context.isFinishing()) {
                Glide.with(context.getApplicationContext())
                        .load(model.getUrlii())
                        .centerCrop()
                        .into(imageView2);
            }

            imageView2.setY(300);
            imageView2.setPadding(0,7,0,0);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 300));

            //x=200,y==0
            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliii())
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setPadding(7,0,0,0);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView4 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrliv())
                    .centerCrop()
                    .into(imageView4);

            imageView4.setX((float) width/2);
            imageView4.setY(200);
            imageView4.setPadding(7,7,0,0);
            imageView4.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView5= new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlv())
                    .centerCrop()
                    .into(imageView5);

            imageView5.setX((float) width/2);
            imageView5.setY(400);
            imageView5.setPadding(7,7,0,0);
            imageView5.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            imageView5.setScaleType(ImageView.ScaleType.FIT_XY);

            TextView textView=new TextView(context);
            String n = "+" + (i - 5);
            textView.setText(n);
            textView.setX((float) width/2);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setY(400);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            textView.setLayoutParams(new FrameLayout.LayoutParams(width/2,200));
            frame_layout.addView(imageView1);
            frame_layout.addView(imageView2);
            frame_layout.addView(imageView3);
            frame_layout.addView(imageView4);
            frame_layout.addView(imageView5);
            frame_layout.addView(textView);
        }
    }

    private void downloadData(String caption) {
        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        Date date=new Date();

        // juste l'apparition du recyclerview
        BattleModel battleModel = new BattleModel();

        battleModel.setSuppressed(false);
        battleModel.setRecycler_story(false);
        battleModel.setFriends_suggestion_one(false);
        battleModel.setFriends_suggestion_two(false);
        battleModel.setFriends_suggestion_three(false);
        battleModel.setFriends_suggestion_four(false);
        battleModel.setFriends_suggestion_five(false);
        battleModel.setGroups_suggestion_one(false);
        battleModel.setGroups_suggestion_two(false);
        battleModel.setGroups_suggestion_three(false);
        battleModel.setGroups_suggestion_four(false);
        battleModel.setGroups_suggestion_five(false);
        battleModel.setVideos_suggestion_one(false);
        battleModel.setVideos_suggestion_two(false);
        battleModel.setVideos_suggestion_three(false);
        battleModel.setVideos_suggestion_four(false);
        battleModel.setVideos_suggestion_five(false);
        battleModel.setEveryone_can_answer_this_challenge(false);

        battleModel.setRecyclerItem(recycler);
        battleModel.setImageUne(image_une);
        battleModel.setVideoUne(video_une);
        battleModel.setImageDouble(image_double);
        battleModel.setVideoDouble(video_double);
        battleModel.setReponseImageDouble(response_image_double);
        battleModel.setReponseVideoDouble(response_video_double);
        // pour le grid profile
        battleModel.setGridRecyclerImage(model.isGridRecyclerImage());

        battleModel.setReponse_deja(model.isReponse_deja());
        battleModel.setDetails(model.getDetails());

        battleModel.setCaption(model.getCaption());
        battleModel.setUrl(model.getUrl());
        battleModel.setPhoto_id(newPhotoKey);
        battleModel.setTags(model.getTags());

        battleModel.setCaptionUn(model.getCaptionUn());
        battleModel.setTagsUn(model.getTagsUn());
        battleModel.setTagsDeux(model.getTagsDeux());
        battleModel.setUrlUn(model.getUrlUn());
        battleModel.setUrlDeux(model.getUrlDeux());
        battleModel.setPhoto_id_un(model.getPhoto_id_un());
        battleModel.setPhoto_id_deux(model.getPhoto_id_deux());

        battleModel.setUrli(model.getUrli());
        battleModel.setUrlii(model.getUrlii());
        battleModel.setUrliii(model.getUrliii());
        battleModel.setUrliv(model.getUrliv());
        battleModel.setUrlv(model.getUrlv());
        battleModel.setUrlvi(model.getUrlvi());
        battleModel.setUrlvii(model.getUrlvii());
        battleModel.setUrlviii(model.getUrlviii());
        battleModel.setUrlix(model.getUrlix());
        battleModel.setUrlx(model.getUrlx());
        battleModel.setUrlxi(model.getUrlxi());
        battleModel.setUrlxii(model.getUrlxii());
        battleModel.setUrlxiii(model.getUrlxiii());
        battleModel.setUrlxiv(model.getUrlxiv());
        battleModel.setUrlxv(model.getUrlxv());
        battleModel.setUrlxvi(model.getUrlxvi());
        battleModel.setUrlxvii(model.getUrlxvii());


        battleModel.setPhotoi_id(model.getPhotoi_id());
        battleModel.setPhotoii_id(model.getPhotoii_id());
        battleModel.setPhotoiii_id(model.getPhotoiii_id());
        battleModel.setPhotoiv_id(model.getPhotoiv_id());
        battleModel.setPhotov_id(model.getPhotov_id());
        battleModel.setPhotovi_id(model.getPhotovi_id());
        battleModel.setPhotovii_id(model.getPhotovii_id());
        battleModel.setPhotoviii_id(model.getPhotoviii_id());
        battleModel.setPhotoix_id(model.getPhotoix_id());
        battleModel.setPhotox_id(model.getPhotox_id());
        battleModel.setPhotoxi_id(model.getPhotoxi_id());
        battleModel.setPhotoxii_id(model.getPhotoxii_id());
        battleModel.setPhotoxiii_id(model.getPhotoxiii_id());
        battleModel.setPhotoxiv_id(model.getPhotoxiv_id());
        battleModel.setPhotoxv_id(model.getPhotoxv_id());
        battleModel.setPhotoxvi_id(model.getPhotoxvi_id());
        battleModel.setPhotoxvii_id(model.getPhotoxvii_id());

        battleModel.setCaption_i(model.getCaption_i());
        battleModel.setCaption_ii(model.getCaption_ii());
        battleModel.setCaption_iii(model.getCaption_iii());
        battleModel.setCaption_iv(model.getCaption_iv());
        battleModel.setCaption_v(model.getCaption_v());
        battleModel.setCaption_vi(model.getCaption_vi());
        battleModel.setCaption_vii(model.getCaption_vii());
        battleModel.setCaption_viii(model.getCaption_viii());
        battleModel.setCaption_ix(model.getCaption_ix());
        battleModel.setCaption_x(model.getCaption_x());
        battleModel.setCaption_xi(model.getCaption_xi());
        battleModel.setCaption_xii(model.getCaption_xii());
        battleModel.setCaption_xiii(model.getCaption_xiii());
        battleModel.setCaption_xiv(model.getCaption_xiv());
        battleModel.setCaption_xv(model.getCaption_xv());
        battleModel.setCaption_xvi(model.getCaption_xvi());
        battleModel.setCaption_xvii(model.getCaption_xvii());

        battleModel.setThumbnail_un(model.getThumbnail_un());
        battleModel.setThumbnail_deux(model.getThumbnail_deux());
        battleModel.setThumbnail(model.getThumbnail());
        battleModel.setThumbnail_invite(model.getThumbnail_invite());
        battleModel.setThumbnail_response(model.getThumbnail_response());

        // challenge
        battleModel.setInvite_url(model.getInvite_url());
        battleModel.setInvite_photoID(model.getInvite_photoID());
        battleModel.setInvite_username(model.getInvite_username());
        battleModel.setInvite_userID(model.getInvite_userID());
        battleModel.setInvite_caption(model.getInvite_caption());
        battleModel.setInvite_tag(model.getInvite_tag());
        battleModel.setInvite_category(model.getInvite_category());
        battleModel.setInvite_profile_photo(model.getInvite_profile_photo());

        battleModel.setReponse_profile_photo(model.getReponse_profile_photo());
        battleModel.setReponse_username(model.getReponse_username());
        battleModel.setReponse_user_id(model.getReponse_user_id());
        battleModel.setReponse_url(model.getReponse_url());
        battleModel.setReponse_photoID(model.getReponse_photoID());
        battleModel.setReponse_caption(model.getReponse_caption());
        battleModel.setReponse_tag(model.getReponse_tag());

        // challenge and flag
        battleModel.setInvite_country_name(model.getInvite_country_name());
        battleModel.setInvite_countryID(model.getInvite_countryID());
        battleModel.setReponse_country_name(model.getReponse_country_name());
        battleModel.setReponse_countryID(model.getReponse_countryID());
        battleModel.setCountry_name(model.getCountry_name());
        battleModel.setCountryID(model.getCountryID());
        battleModel.setInvite_category_status(model.getInvite_category_status());
        battleModel.setSharing_caption(caption);
        // group
        battleModel.setGroup(model.isGroup());
        battleModel.setGroup_private(model.isGroup_private());
        battleModel.setGroup_public(model.isGroup_public());
        battleModel.setGroup_cover_profile_photo(group_circle_image);
        battleModel.setGroup_recyclerItem(group_recycler);
        battleModel.setGroup_imageUne(group_image_une);
        battleModel.setGroup_videoUne(group_video_une);
        battleModel.setGroup_cover_background_profile_photo(group_circle_image);
        battleModel.setGroup_imageDouble(group_image_double);
        battleModel.setGroup_videoDouble(group_video_double);
        battleModel.setGroup_response_imageDouble(group_response_image_double);
        battleModel.setGroup_response_videoDouble(group_response_video_double);

        battleModel.setGroup_share_single_bottom_circle_image(group_share_single_bottom_circle_image);
        battleModel.setGroup_share_single_bottom_image_double(group_share_single_bottom_image_double);
        battleModel.setGroup_share_single_bottom_image_une(group_share_single_bottom_image_une);
        battleModel.setGroup_share_single_bottom_recycler(group_share_single_bottom_recycler);
        battleModel.setGroup_share_single_bottom_response_image_double(group_share_single_bottom_response_image_double);
        battleModel.setGroup_share_single_bottom_response_video_double(group_share_single_bottom_response_video_double);
        battleModel.setGroup_share_single_bottom_video_double(group_share_single_bottom_video_double);
        battleModel.setGroup_share_single_bottom_video_une(group_share_single_bottom_video_une);

        battleModel.setGroup_share_single_top_circle_image(group_share_single_top_circle_image);
        battleModel.setGroup_share_single_top_image_double(group_share_single_top_image_double);
        battleModel.setGroup_share_single_top_image_une(group_share_single_top_image_une);
        battleModel.setGroup_share_single_top_recycler(group_share_single_top_recycler);
        battleModel.setGroup_share_single_top_response_image_double(group_share_single_top_response_image_double);
        battleModel.setGroup_share_single_top_response_video_double(group_share_single_top_response_video_double);
        battleModel.setGroup_share_single_top_video_double(group_share_single_top_video_double);
        battleModel.setGroup_share_single_top_video_une(group_share_single_top_video_une);

        battleModel.setGroup_share_top_bottom_circle_image(group_share_top_bottom_circle_image);
        battleModel.setGroup_share_top_bottom_image_double(group_share_top_bottom_image_double);
        battleModel.setGroup_share_top_bottom_image_une(group_share_top_bottom_image_une);
        battleModel.setGroup_share_top_bottom_recycler(group_share_top_bottom_recycler);
        battleModel.setGroup_share_top_bottom_response_image_double(group_share_top_bottom_response_image_double);
        battleModel.setGroup_share_top_bottom_response_video_double(group_share_top_bottom_response_video_double);
        battleModel.setGroup_share_top_bottom_video_double(group_share_top_bottom_video_double);
        battleModel.setGroup_share_top_bottom_video_une(group_share_top_bottom_video_une);

        battleModel.setUser_profile_photo(model.getUser_profile_photo());
        battleModel.setUser_full_profile_photo(model.getUser_full_profile_photo());

        battleModel.setDate_shared(model.getDate_created());
        battleModel.setDate_created(date.getTime()); // sharing date
        battleModel.setViews(model.getViews());

        // ID ______________________________________________________________________
        battleModel.setUser_id(user_id);
        battleModel.setUser_id_sharing(model.getUser_id());

        battleModel.setSharing_admin_master(model.getAdmin_master());
        battleModel.setAdmin_master(userGroup.getAdmin_master());

        battleModel.setGroup_id(userGroup.getGroup_id()); // the user group shared the group publication
        battleModel.setShared_group_id(model.getGroup_id());

        battleModel.setPublisher(model.getPublisher());
        battleModel.setGroup_profile_photo(model.getGroup_profile_photo());
        battleModel.setGroup_full_profile_photo(model.getGroup_full_profile_photo());
        battleModel.setGroup_user_background_profile_url(model.getGroup_user_background_profile_url());
        battleModel.setGroup_user_background_full_profile_url(model.getGroup_user_background_full_profile_url());

        battleModel.setComment_text(model.isComment_text());
        battleModel.setComment_subject(model.getComment_subject());
        battleModel.setComment_theme(model.getComment_theme());
        battleModel.setBig_image(model.isBig_image());

        // republication of publication
        battleModel.setUser_profile_shared(user_profile_shared);
        battleModel.setImageDouble_shared(imageDouble_shared);
        battleModel.setImageUne_shared(imageUne_shared);
        battleModel.setRecyclerItem_shared(recyclerItem_shared);
        battleModel.setReponseImageDouble_shared(reponseImageDouble_shared);
        battleModel.setReponseVideoDouble_shared(reponseVideoDouble_shared);
        battleModel.setVideoDouble_shared(videoDouble_shared);
        battleModel.setVideoUne_shared(videoUne_shared);
        battleModel.setUser_profile(circle_image);
        // for saved
        battleModel.setUsername("");
        battleModel.setProfileUrl("");
        battleModel.setAuth_user_id("");
        battleModel.setUser_id_shared("");

        // post identity
        String post_key = myRef.push().getKey();
        battleModel.setPost_identity(post_key);

        // to know if the member admin post approval
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(userGroup.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    GroupFollowersFollowing follower = new GroupFollowersFollowing();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    follower.setPublicationApprobation(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_publicationApprobation))).toString())));

                    assert newPhotoKey != null;
                    if (follower.isPublicationApprobation()) {
                        myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                .child(userGroup.getGroup_id())
                                .child(newPhotoKey)
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_Media_Waiting_for_approval_share))
                                .child(newPhotoKey)
                                .setValue(battleModel).addOnCompleteListener(task -> {

                                    // number of share
                                    if (!TextUtils.isEmpty(battleModel.getPhoto_id_un()))
                                        ShareUtils.countShare(context, battleModel.getPhoto_id_un());
                                    if (!TextUtils.isEmpty(battleModel.getPhoto_id()))
                                        ShareUtils.countShare(context, battleModel.getPhoto_id());
                                    if (!TextUtils.isEmpty(battleModel.getPhotoi_id()))
                                        ShareUtils.countShare(context, battleModel.getPhotoi_id());
                                    if (!TextUtils.isEmpty(battleModel.getReponse_photoID()))
                                        ShareUtils.countShare(context, battleModel.getReponse_photoID());

                                    progressBar.setVisibility(View.GONE);

                                    //add points and send notification
                                    addPostPointsInGroup();

                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });

                    } else {
                        myRef.child(getString(R.string.dbname_group))
                                .child(userGroup.getGroup_id())
                                .child(newPhotoKey)
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_media_share))
                                .child(newPhotoKey)
                                .setValue(battleModel).addOnCompleteListener(task -> {

                                    // number of share
                                    if (!TextUtils.isEmpty(battleModel.getPhoto_id_un()))
                                        ShareUtils.countShare(context, battleModel.getPhoto_id_un());
                                    if (!TextUtils.isEmpty(battleModel.getPhoto_id()))
                                        ShareUtils.countShare(context, battleModel.getPhoto_id());
                                    if (!TextUtils.isEmpty(battleModel.getPhotoi_id()))
                                        ShareUtils.countShare(context, battleModel.getPhotoi_id());
                                    if (!TextUtils.isEmpty(battleModel.getReponse_photoID()))
                                        ShareUtils.countShare(context, battleModel.getReponse_photoID());

                                    //add points and send notification
                                    addPostPointsInGroup();

                                    progressBar.setVisibility(View.GONE);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (userGroup.getAdmin_master().equals(user_id)) {
            assert newPhotoKey != null;
            myRef.child(getString(R.string.dbname_group))
                    .child(userGroup.getGroup_id())
                    .child(newPhotoKey)
                    .setValue(battleModel);

            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(newPhotoKey)
                    .setValue(battleModel).addOnCompleteListener(task -> {

                        // number of share
                        if (!TextUtils.isEmpty(battleModel.getPhoto_id_un()))
                            ShareUtils.countShare(context, battleModel.getPhoto_id_un());
                        if (!TextUtils.isEmpty(battleModel.getPhoto_id()))
                            ShareUtils.countShare(context, battleModel.getPhoto_id());
                        if (!TextUtils.isEmpty(battleModel.getPhotoi_id()))
                            ShareUtils.countShare(context, battleModel.getPhotoi_id());
                        if (!TextUtils.isEmpty(battleModel.getReponse_photoID()))
                            ShareUtils.countShare(context, battleModel.getReponse_photoID());

                        //add points and send notification
                        addPostPointsInGroup();

                        progressBar.setVisibility(View.GONE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }
    }

    // add post point and send notification
    @TargetApi(Build.VERSION_CODES.O)
    private void addPostPointsInGroup() {
        if (userGroup.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(userGroup.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();
                        int number_of_post_points = Integer.parseInt(user_groups.getPost_points());
                        map.put("post_points", number_of_post_points+10);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_user_group))
                                .child(user_groups.getAdmin_master())
                                .child(user_groups.getGroup_id())
                                .updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(userGroup.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();

                        int number_of_post_points = Integer.parseInt(following.getPost_points());
                        map.put("post_points", number_of_post_points+10);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(userGroup.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(userGroup.getGroup_id())
                                .child(user_id)
                                .updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // send notification
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(userGroup.getGroup_id());
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String member_id = dataSnapshot.getKey();

                        assert member_id != null;
                        if (!member_id.equals(user_id))
                            members_id_list.add(member_id);
                    }

                    // add admin aster id
                    if (!userGroup.getAdmin_master().equals(user_id))
                        members_id_list.add(userGroup.getAdmin_master());

                    if (!members_id_list.isEmpty()) {
                        for (int i = 0; i < members_id_list.size(); i++) {
                            // send notification
                            Date date = new Date();
                            String new_key = myRef.push().getKey();
                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                    false, false, false, false, false,
                                    false,false, false, false,
                                    false, false, false, false, false, false,
                                    false,
                                    false, false, false, false, false,
                                    false, false,
                                    false, false, false, false, false,
                                    false, false,
                                    false, "", false, false, false, false,
                                    true,false, false,
                                    members_id_list.get(i), new_key, user_id, userGroup.getAdmin_master(),
                                    "", userGroup.getGroup_id(), date.getTime(),
                                    false, false,
                                    true, false, true, false, false, false, false, false,
                                    false, "", "", "", false, "", "", "", false,
                                    "", "", "", "", "", 0,
                                    "", "", 0, "", "", "",
                                    false, false, false, false,
                                    false, false, false,
                                    false, false);

                            assert new_key != null;
                            myRef.child(context.getString(R.string.dbname_notification))
                                    .child(members_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map_notification);

                            // add badge number
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", members_id_list.get(i));

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(members_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map_number);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}