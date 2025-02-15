package com.bekambimouen.miiokychallenge.publication_insta;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PubCommentText extends AppCompatActivity {
    private static final String TAG = "PubCommentText";

    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private CircleImageView profile_photo;
    private TextView username, nber_char_sequence;
    private MyEditText edit_comment;
    private RelativeLayout main, relLayout_edittext_cadre, relLayout_view_overlay;

    // vars
    private PubCommentText context;
    private String theme = "gradient_blue";
    private boolean isScreenEnabled = true, toPostPhoto = false, toPostVideo = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_pub_comment_text);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        init();
        setProfileWidgets();
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
        main = findViewById(R.id.main);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        profile_photo = findViewById(R.id.profile_photo);
        username = findViewById(R.id.username);
        nber_char_sequence = findViewById(R.id.nber_char_sequence);
        edit_comment = findViewById(R.id.edit_comment);
        relLayout_edittext_cadre = findViewById(R.id.relLayout_edittext_cadre);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_go_user_profile = findViewById(R.id.relLayout_go_user_profile);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        LinearLayout linLayout_camera_photo = findViewById(R.id.linLayout_camera_photo);
        LinearLayout linLayout_camera_video = findViewById(R.id.linLayout_camera_video);

        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_sequence.setText(String.valueOf(char_length));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        linLayout_camera_photo.setOnClickListener(view -> {
            Log.d(TAG, "onClick: changing profile photo");
            if (allPermissionsGranted()) {
                toPostPhoto = true;
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubInstagPhoto.class);
                startActivity(intent);
            }
        });

        linLayout_camera_video.setOnClickListener(view -> {
            Log.d(TAG, "onClick: changing profile photo");
            if (allPermissionsGranted()) {
                toPostVideo = true;
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubInstagVideo.class);
                startActivity(intent);
            }
        });

        relLayout_go_user_profile.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, Profile.class);
            startActivity(intent);
        });

        arrowBack.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        // publish comment text
        relLayout_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, main);

            } else {
                closeKeyboard();
                isScreenEnabled = false;
                addPostCommentToDatabase();
            }
        });

        // comment text background
        CardView gradient_blue = findViewById(R.id.gradient_blue);
        CardView gradient_red = findViewById(R.id.gradient_red);
        CardView gradient_brown = findViewById(R.id.gradient_brown);
        CardView gradient_darkred_black_blue_shinning = findViewById(R.id.gradient_darkred_black_blue_shinning);
        CardView gradient_black = findViewById(R.id.gradient_black);
        CardView gradient_shinning_blue_darkred_chinning_blue = findViewById(R.id.gradient_shinning_blue_darkred_chinning_blue);
        CardView gradient_shinning_blue_darkred_pink = findViewById(R.id.gradient_shinning_blue_darkred_pink);
        CardView gradient_yellow_pink_dark_violet = findViewById(R.id.gradient_yellow_pink_dark_violet);
        CardView gradient_pink = findViewById(R.id.gradient_pink);
        CardView gradient_vertwhatsapp = findViewById(R.id.gradient_vertwhatsapp);
        CardView gradient_dark_violet = findViewById(R.id.gradient_dark_violet);
        CardView gradient_blue_green = findViewById(R.id.gradient_blue_green);
        CardView gradient_primary_and_dark_violet = findViewById(R.id.gradient_primary_and_dark_violet);

        gradient_blue.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_blue));
            theme = context.getString(R.string.gradient_blue);
        });

        gradient_red.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_red));
            theme = context.getString(R.string.gradient_red);
        });

        gradient_brown.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_brown));
            theme = context.getString(R.string.gradient_brown);
        });

        gradient_primary_and_dark_violet.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_yellow_yellow_and_pink));
            theme = context.getString(R.string.gradient_yellow_yellow_and_pink);
        });

        gradient_darkred_black_blue_shinning.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_darkred_black_blue_shinning));
            theme = context.getString(R.string.gradient_darkred_black_blue_shinning);
        });

        gradient_black.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_black));
            theme = context.getString(R.string.gradient_black);
        });

        gradient_shinning_blue_darkred_chinning_blue.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_shinning_blue_darkred_chinning_blue));
            theme = context.getString(R.string.gradient_shinning_blue_darkred_chinning_blue);
        });

        gradient_shinning_blue_darkred_pink.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_shinning_blue_darkred_pink));
            theme = context.getString(R.string.gradient_shinning_blue_darkred_pink);
        });

        gradient_yellow_pink_dark_violet.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_yellow_pink_dark_violet));
            theme = context.getString(R.string.gradient_yellow_pink_dark_violet);
        });

        gradient_pink.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_pink));
            theme = context.getString(R.string.gradient_pink);
        });

        gradient_vertwhatsapp.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_vertwhatsapp));
            theme = context.getString(R.string.gradient_vertwhatsapp);
        });

        gradient_dark_violet.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_dark_violet));
            theme = context.getString(R.string.gradient_dark_violet);
        });

        gradient_blue_green.setOnClickListener(view -> {
            relLayout_edittext_cadre.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_blue_green));
            theme = context.getString(R.string.gradient_blue_green);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);

            if (toPostVideo) {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubInstagVideo.class);
                context.startActivity(intent);
            }

            if (toPostPhoto) {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubInstagPhoto.class);
                context.startActivity(intent);
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void setProfileWidgets() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo);

                    username.setText(user.getUsername());

                    profile_photo.setOnClickListener(view -> {
                        if (!user.getProfileUrl().isEmpty()) {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                            intent.putExtra("img_url", user.getProfileUrl());
                            context.startActivity(intent);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addPostCommentToDatabase() {
        String comment = Objects.requireNonNull(edit_comment.getText()).toString();
        if (TextUtils.isEmpty(comment)) {
            isScreenEnabled = true;
            Toast.makeText(context, context.getString(R.string.you_must_write_comment), Toast.LENGTH_LONG).show();
            return;
        }
        String tags = StringManipulation.getTags(comment);
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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

        battleModel.setRecyclerItem(false);
        battleModel.setImageUne(false);
        battleModel.setVideoUne(false);
        battleModel.setImageDouble(false);
        battleModel.setVideoDouble(false);
        battleModel.setReponseImageDouble(false);
        battleModel.setReponseVideoDouble(false);
        // pour le grid profile
        battleModel.setGridRecyclerImage(false);

        battleModel.setReponse_deja(false);
        battleModel.setDetails("");

        // group
        battleModel.setGroup(false);
        battleModel.setGroup_private(false);
        battleModel.setGroup_public(false);
        battleModel.setGroup_cover_profile_photo(false);
        battleModel.setGroup_recyclerItem(false);
        battleModel.setGroup_imageUne(false);
        battleModel.setGroup_videoUne(false);
        battleModel.setGroup_cover_background_profile_photo(false);
        battleModel.setGroup_imageDouble(false);
        battleModel.setGroup_videoDouble(false);
        battleModel.setGroup_response_imageDouble(false);
        battleModel.setGroup_response_videoDouble(false);

        battleModel.setGroup_share_single_bottom_image_double(false);
        battleModel.setGroup_share_single_bottom_image_une(false);
        battleModel.setGroup_share_single_bottom_recycler(false);
        battleModel.setGroup_share_single_bottom_response_image_double(false);
        battleModel.setGroup_share_single_bottom_response_video_double(false);
        battleModel.setGroup_share_single_bottom_video_double(false);
        battleModel.setGroup_share_single_bottom_video_une(false);

        battleModel.setGroup_share_single_top_image_double(false);
        battleModel.setGroup_share_single_top_image_une(false);
        battleModel.setGroup_share_single_top_recycler(false);
        battleModel.setGroup_share_single_top_response_image_double(false);
        battleModel.setGroup_share_single_top_response_video_double(false);
        battleModel.setGroup_share_single_top_video_double(false);
        battleModel.setGroup_share_single_top_video_une(false);

        battleModel.setGroup_share_top_bottom_image_double(false);
        battleModel.setGroup_share_top_bottom_image_une(false);
        battleModel.setGroup_share_top_bottom_recycler(false);
        battleModel.setGroup_share_top_bottom_response_image_double(false);
        battleModel.setGroup_share_top_bottom_response_video_double(false);
        battleModel.setGroup_share_top_bottom_video_double(false);
        battleModel.setGroup_share_top_bottom_video_une(false);

        battleModel.setUser_profile_shared(false);
        battleModel.setGroup_share_single_bottom_circle_image(false);
        battleModel.setGroup_share_single_top_circle_image(false);
        battleModel.setGroup_share_top_bottom_circle_image(false);

        battleModel.setUser_profile_photo("");
        battleModel.setUser_full_profile_photo("");
        battleModel.setDate_shared(0);
        battleModel.setViews(0);

        battleModel.setAdmin_master("");
        battleModel.setGroup_id("");
        battleModel.setSharing_admin_master("");
        battleModel.setShared_group_id("");
        battleModel.setPublisher(user_id);
        battleModel.setGroup_profile_photo("");
        battleModel.setGroup_full_profile_photo("");
        battleModel.setGroup_user_background_profile_url("");
        battleModel.setGroup_user_background_full_profile_url("");

        battleModel.setComment_text(true);
        battleModel.setComment_subject(comment);
        battleModel.setComment_theme(theme);
        battleModel.setBig_image(false);

        battleModel.setCaption("");
        battleModel.setUrl("");
        battleModel.setDate_created(date.getTime());
        battleModel.setPhoto_id(newPhotoKey);
        battleModel.setUser_id(user_id);
        battleModel.setTags(tags);

        battleModel.setCaptionUn("");
        battleModel.setTagsUn("");
        battleModel.setTagsDeux("");
        battleModel.setUrlUn("");
        battleModel.setUrlDeux("");
        battleModel.setPhoto_id_un("");
        battleModel.setPhoto_id_deux("");

        battleModel.setUrli("");
        battleModel.setUrlii("");
        battleModel.setUrliii("");
        battleModel.setUrliv("");
        battleModel.setUrlv("");
        battleModel.setUrlvi("");
        battleModel.setUrlvii("");
        battleModel.setUrlviii("");
        battleModel.setUrlix("");
        battleModel.setUrlx("");
        battleModel.setUrlxi("");
        battleModel.setUrlxii("");
        battleModel.setUrlxiii("");
        battleModel.setUrlxiv("");
        battleModel.setUrlxv("");
        battleModel.setUrlxvi("");
        battleModel.setUrlxvii("");

        battleModel.setPhotoi_id("");
        battleModel.setPhotoii_id("");
        battleModel.setPhotoiii_id("");
        battleModel.setPhotoiv_id("");
        battleModel.setPhotov_id("");
        battleModel.setPhotovi_id("");
        battleModel.setPhotovii_id("");
        battleModel.setPhotoviii_id("");
        battleModel.setPhotoix_id("");
        battleModel.setPhotox_id("");
        battleModel.setPhotoxi_id("");
        battleModel.setPhotoxii_id("");
        battleModel.setPhotoxiii_id("");
        battleModel.setPhotoxiv_id("");
        battleModel.setPhotoxv_id("");
        battleModel.setPhotoxvi_id("");
        battleModel.setPhotoxvii_id("");

        battleModel.setCaption_i("");
        battleModel.setCaption_ii("");
        battleModel.setCaption_iii("");
        battleModel.setCaption_iv("");
        battleModel.setCaption_v("");
        battleModel.setCaption_vi("");
        battleModel.setCaption_vii("");
        battleModel.setCaption_viii("");
        battleModel.setCaption_ix("");
        battleModel.setCaption_x("");
        battleModel.setCaption_xi("");
        battleModel.setCaption_xii("");
        battleModel.setCaption_xiii("");
        battleModel.setCaption_xiv("");
        battleModel.setCaption_xv("");
        battleModel.setCaption_xvi("");
        battleModel.setCaption_xvii("");

        battleModel.setThumbnail_un("");
        battleModel.setThumbnail_deux("");
        battleModel.setThumbnail("");
        battleModel.setThumbnail_invite("");
        battleModel.setThumbnail_response("");

        // challenge
        battleModel.setInvite_url("");
        battleModel.setInvite_photoID("");
        battleModel.setInvite_username("");
        battleModel.setInvite_userID("");
        battleModel.setInvite_caption("");
        battleModel.setInvite_tag("");
        battleModel.setInvite_category("");
        battleModel.setInvite_profile_photo("");

        battleModel.setReponse_profile_photo("");
        battleModel.setReponse_username("");
        battleModel.setReponse_user_id("");
        battleModel.setReponse_url("");
        battleModel.setReponse_photoID("");
        battleModel.setReponse_caption("");
        battleModel.setReponse_tag("");

        // challenge and flag
        battleModel.setInvite_country_name("");
        battleModel.setInvite_countryID("");
        battleModel.setReponse_country_name("");
        battleModel.setReponse_countryID("");
        battleModel.setCountry_name("");
        battleModel.setCountryID("");
        battleModel.setInvite_category_status("");
        battleModel.setSharing_caption("");

        // republication of publication
        battleModel.setImageDouble_shared(false);
        battleModel.setImageUne_shared(false);
        battleModel.setRecyclerItem_shared(false);
        battleModel.setReponseImageDouble_shared(false);
        battleModel.setReponseVideoDouble_shared(false);
        battleModel.setVideoDouble_shared(false);
        battleModel.setVideoUne_shared(false);
        battleModel.setUser_id_sharing("");
        // for saved
        battleModel.setUsername("");
        battleModel.setProfileUrl("");
        battleModel.setAuth_user_id("");
        battleModel.setUser_id_shared("");

        // post identity
        String post_key = myRef.push().getKey();
        battleModel.setPost_identity(post_key);

        // un autre adaptateur pour le recyclerview
        assert newPhotoKey != null;
        myRef.child(getString(R.string.dbname_battle))
                .child(user_id)
                .child(newPhotoKey)
                .setValue(battleModel);

        myRef.child(getString(R.string.dbname_battle_media))
                .child(newPhotoKey)
                .setValue(battleModel).addOnCompleteListener(task -> {

                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from_publish", "from_publish");
                    startActivity(intent);
                    finish();
                });
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
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, main);
    }
}