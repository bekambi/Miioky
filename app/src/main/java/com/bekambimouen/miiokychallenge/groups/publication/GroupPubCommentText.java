package com.bekambimouen.miiokychallenge.groups.publication;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupPubCommentText extends AppCompatActivity {
    private static final String TAG = "GroupPubCommentText";
    // widgets
    private CircleImageView profile_photo;
    private TextView username, nber_char_sequence;
    private MyEditText edit_comment;
    private RelativeLayout parent, relLayout_edittext_cadre, relLayout_view_overlay;

    // vars
    private GroupPubCommentText context;
    private UserGroups user_groups;
    private ArrayList<String> members_id_list;
    private String theme = "gradient_blue";

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_pub_comment_text);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        members_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
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
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        profile_photo = findViewById(R.id.profile_photo);
        username = findViewById(R.id.username);
        nber_char_sequence = findViewById(R.id.nber_char_sequence);
        edit_comment = findViewById(R.id.edit_comment);
        relLayout_edittext_cadre = findViewById(R.id.relLayout_edittext_cadre);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_go_user_profile = findViewById(R.id.relLayout_go_user_profile);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);

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

        /*linLayout_camera_photo.setOnClickListener(view -> {

            bottomSheetGroupPublication = new BottomSheetGroupPublication(context,
                    user_groups, "photo");

            if (bottomSheetGroupPublication.isAdded())
                return;
            bottomSheetGroupPublication.show(context.getSupportFragmentManager(), "TAG");
        });

        linLayout_camera_video.setOnClickListener(view -> {

            bottomSheetGroupPublication = new BottomSheetGroupPublication(context,
                    user_groups, "video");

            if (bottomSheetGroupPublication.isAdded())
                return;
            bottomSheetGroupPublication.show(context.getSupportFragmentManager(), "TAG");
        });*/

        // publish comment text
        relLayout_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                // to know if the member admin post approval
                relLayout_publish.setEnabled(false);
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            long sanction_date = follower.getDate_admin_applied_sanction_in_group();
                            long currentTime = System.currentTimeMillis();

                            // to know if the member admin post approval
                            if (follower.isPublicationApprobation()) {
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

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.ok));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.posts_will_be_submitted_to_admin_before_display)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view3 -> {
                                    // download data to firebase
                                    addPostCommentToDatabase();
                                    d.dismiss();
                                });

                            }
                            // limited posts activity
                            else if (follower.isActivityLimitation()) {
                                if (follower.isPostsActivityLimited()) {
                                    switch (follower.getNumber_posts_per_day_expiration()) {
                                        case "12":
                                            // check that the date of the sanctions has not expired
                                            if ((sanction_date + 12*3600000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "24":
                                            if ((sanction_date + 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "3":
                                            if ((sanction_date + 3 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "7":
                                            if ((sanction_date + 7 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "14":
                                            if ((sanction_date + 14 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "28":
                                            if ((sanction_date + 28 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                    }
                                }

                            } else {
                                // download to firebase
                                addPostCommentToDatabase();
                            }
                        }

                        if (!snapshot.exists()) {
                            // download to firebase
                            addPostCommentToDatabase();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // detect if user is admin master
        if (user_groups.getAdmin_master().equals(user_id)) {
            relLayout_publish.setOnClickListener(view -> {
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {
                    // download data to firebase
                    addPostCommentToDatabase();
                }
            });
        }

        Query query1 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dts: snapshot.getChildren()) {
                    User user = new User();

                    Map<String, Object> objectMap = (HashMap<String, Object>) dts.getValue();
                    assert objectMap != null;
                    user.setUsername(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_username))).toString()));
                    user.setFullName(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_fullName))).toString()));
                    user.setProfileUrl(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profileUrl))).toString()));
                    user.setUser_id(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString()));
                    user.setPhoneNumber(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_phoneNumber))).toString()));

                    username.setText(user.getUsername());
                    String userID = user.getUser_id();

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    relLayout_go_user_profile.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("user_groups", myGson);
                        intent.putExtra("userID", userID);
                        intent.putExtra("group_id", user_groups.getGroup_id());
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    }

    // limit the post
    @TargetApi(Build.VERSION_CODES.O)
    private void getLimitedPosts(GroupFollowersFollowing follower) {
        // get the day of today
        LocalDate date = LocalDate.now();
        long time = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        // it's today
        if (TimeUtils.isDateToday(time)) {
            // we check that the number os publications is lower than the desire limit
            if (follower.getNumber_of_posts_today() < Integer.parseInt(follower.getNumber_posts_per_day())) {

                // increment number of posts today
                HashMap<String, Object> map = new HashMap<>();
                Date date1 = new Date();
                int number_of_posts = follower.getNumber_of_posts_today();
                map.put("number_of_posts_today", number_of_posts+1);
                map.put("date_last_post_or_last_comment", date1.getTime());

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .child(user_groups.getGroup_id())
                        .updateChildren(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id())
                        .child(user_id)
                        .updateChildren(map);

                // add posts to firebase
                addPostCommentToDatabase();

            } else {
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

                negativeButton.setText(context.getString(R.string.cancel));
                negativeButton.setVisibility(View.GONE);
                positiveButton.setText(context.getString(R.string.ok));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_have_reached_the_post_quota)
                        +" "+follower.getNumber_posts_per_day()+" "+context.getString(R.string.publications)
                        +" "+context.getString(R.string.per_day)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());
                positiveButton.setOnClickListener(view3 -> d.dismiss());
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addPostCommentToDatabase() {
        String comment = Objects.requireNonNull(edit_comment.getText()).toString();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(context, context.getString(R.string.you_must_write_comment), Toast.LENGTH_SHORT).show();
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
        battleModel.setGroup(true);
        battleModel.setGroup_private(user_groups.isGroup_private());
        battleModel.setGroup_public(user_groups.isGroup_public());
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

        battleModel.setAdmin_master(user_groups.getAdmin_master());
        battleModel.setGroup_id(user_groups.getGroup_id());
        battleModel.setSharing_admin_master("");
        battleModel.setShared_group_id("");
        battleModel.setPublisher(user_id);
        battleModel.setGroup_profile_photo(user_groups.getProfile_photo());
        battleModel.setGroup_full_profile_photo(user_groups.getFull_photo());
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

        // to know if the member admin post approval
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    assert newPhotoKey != null;
                    if (follower.isPublicationApprobation()) {
                        // un autre adaptateur pour le recyclerview
                        myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                .child(user_groups.getGroup_id())
                                .child(newPhotoKey)
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_Media_waiting_for_approval))
                                .child(newPhotoKey)
                                .setValue(battleModel).addOnCompleteListener(task -> {
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                });

                    } else {
                        myRef.child(getString(R.string.dbname_group))
                                .child(user_groups.getGroup_id())
                                .child(newPhotoKey)
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_media))
                                .child(newPhotoKey)
                                .setValue(battleModel).addOnCompleteListener(task -> {
                                    //add points and send notification
                                    addPostPointsAndSendNotification();
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                });
                    }
                }

                if (!snapshot.exists() && !user_groups.getAdmin_master().equals(user_id)) {
                    assert newPhotoKey != null;
                    myRef.child(getString(R.string.dbname_group))
                            .child(user_groups.getGroup_id())
                            .child(newPhotoKey)
                            .setValue(battleModel);

                    myRef.child(getString(R.string.dbname_group_media))
                            .child(newPhotoKey)
                            .setValue(battleModel).addOnCompleteListener(task -> {
                                //add points and send notification
                                addPostPointsAndSendNotification();
                                getWindow().setExitTransition(new Slide(Gravity.END));
                                getWindow().setEnterTransition(new Slide(Gravity.START));
                                finish();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // detect if user is admin master
        if (user_groups.getAdmin_master().equals(user_id)) {
            assert newPhotoKey != null;
            myRef.child(getString(R.string.dbname_group))
                    .child(user_groups.getGroup_id())
                    .child(newPhotoKey)
                    .setValue(battleModel);

            myRef.child(getString(R.string.dbname_group_media))
                    .child(newPhotoKey)
                    .setValue(battleModel).addOnCompleteListener(task -> {
                        //add points and send notification
                        addPostPointsAndSendNotification();
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    });
        }
    }

    // add post point and send notification
    @TargetApi(Build.VERSION_CODES.O)
    private void addPostPointsAndSendNotification() {
        if (user_groups.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

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

            // send notification
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id());
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String member_id = dataSnapshot.getKey();

                        assert member_id != null;
                        if (!member_id.equals(user_id))
                            members_id_list.add(member_id);
                    }

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
                                    members_id_list.get(i), new_key, user_id, user_groups.getAdmin_master(),
                                    "", user_groups.getGroup_id(), date.getTime(),
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
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("user_id", members_id_list.get(i));

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(members_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map);
                        }
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
                    .equalTo(user_groups.getGroup_id());

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
                                .child(user_groups.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(user_groups.getGroup_id())
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
                    .child(user_groups.getGroup_id());
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
                    if (!user_groups.getAdmin_master().equals(user_id))
                        members_id_list.add(user_groups.getAdmin_master());

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
                                    members_id_list.get(i), new_key, user_id, user_groups.getAdmin_master(),
                                    "", user_groups.getGroup_id(), date.getTime(),
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
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("user_id", members_id_list.get(i));

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(members_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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