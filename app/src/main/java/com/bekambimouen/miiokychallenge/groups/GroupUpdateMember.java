package com.bekambimouen.miiokychallenge.groups;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile.SeeUserAllProfileOnMiioky;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.GroupCreateMemberBackgroundProfilePhoto;
import com.bekambimouen.miiokychallenge.groups.see_member_profile.SeeGroupMemberAllBackgroundProfile;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupUpdateMember extends AppCompatActivity {
    private static final String TAG = "GroupUpdateMember";

    // widggets
    private ImageView profile_photo;
    private TextView date_following, nber_char_sequence, group_message;
    private MyEditText edit_bio;
    private LinearLayout linLayout_nber_char_sequence;
    private ProgressBar progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_group_name;
    private Toolbar toolBar;

    // vars
    private GroupUpdateMember context;
    private UserGroups user_groups;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_update_member);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        theme();
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
        toolBar = findViewById(R.id.toolBar);
        arrowBack = findViewById(R.id.arrowBack);
        close = findViewById(R.id.close);

        progressBar = findViewById(R.id.progressBar);
        RelativeLayout relLayout_profile_update = findViewById(R.id.relLayout_profile_update);
        linLayout_nber_char_sequence = findViewById(R.id.linLayout_nber_char_sequence);
        group_message = findViewById(R.id.group_message);
        nber_char_sequence = findViewById(R.id.nber_char_sequence);
        edit_bio = findViewById(R.id.edit_bio);
        toolBar_group_name = findViewById(R.id.toolBar_group_name);
        profile_photo = findViewById(R.id.profile_photo);
        CircleImageView user_profile_photo = findViewById(R.id.user_profile_photo);
        TextView username = findViewById(R.id.username);
        TextView btn_save = findViewById(R.id.btn_save);
        date_following = findViewById(R.id.date_following);

        toolBar_group_name.setText(user_groups.getGroup_name());

        relLayout_profile_update.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupCreateMemberBackgroundProfilePhoto.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
        });

        // action add bio
        btn_save.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String message = Objects.requireNonNull(edit_bio.getText()).toString();
            closeKeyboard();

            if (user_id.equals(user_groups.getAdmin_master())) {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_user_group))
                        .child(user_groups.getAdmin_master())
                        .child(user_groups.getGroup_id())
                        .child(context.getString(R.string.field_admin_master_bio))
                        .setValue(message)
                        .addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.GONE);
                            edit_bio.setText("");
                            edit_bio.clearFocus();
                            group_message.setTextColor(context.getColor(R.color.black_semi_transparent));
                            linLayout_nber_char_sequence.setVisibility(View.GONE);
                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                        });

            } else {

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .child(user_groups.getGroup_id())
                        .child(context.getString(R.string.field_member_bio))
                        .setValue(message);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id())
                        .child(user_id)
                        .child(context.getString(R.string.field_member_bio))
                        .setValue(message)
                        .addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.GONE);
                            edit_bio.setText("");
                            edit_bio.clearFocus();
                            group_message.setTextColor(context.getColor(R.color.black_semi_transparent));
                            linLayout_nber_char_sequence.setVisibility(View.GONE);
                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                        });
            }
        });

        if (user_groups.getAdmin_master().equals(user_id)) {
            // update in real time
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_groups.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        long date_member = user_groups.getDate_created();
                        String bio = user_groups.getAdmin_master_bio();
                        String the_date = TimeUtils.getTime(context, date_member);

                        date_following.setText(Html.fromHtml(context.getString(R.string.created_the_group)+" <b>"
                                +user_groups.getGroup_name()+"</b> "+context.getString(R.string.from)+" "+the_date));

                        if (!TextUtils.isEmpty(bio))
                            edit_bio.setText(bio);

                        if (!TextUtils.isEmpty(bio) && Objects.requireNonNull(edit_bio.getText()).toString().trim().equals(bio)) {
                            group_message.setTextColor(context.getColor(R.color.shinning_blue));
                            btn_save.setBackground(ContextCompat.getDrawable(context, R.color.blue_grey_50));
                            btn_save.setTextColor(context.getColor(R.color.black_semi_transparent4));
                            btn_save.setEnabled(false);
                        }

                        edit_bio.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                int char_length = charSequence.length();
                                nber_char_sequence.setText(String.valueOf(char_length));

                                if (char_length == 0) {
                                    group_message.setTextColor(context.getColor(R.color.black_semi_transparent));
                                    linLayout_nber_char_sequence.setVisibility(View.INVISIBLE);

                                } else if (char_length >= 1) {
                                    group_message.setTextColor(context.getColor(R.color.shinning_blue));
                                    linLayout_nber_char_sequence.setVisibility(View.VISIBLE);

                                }
                                if (char_length >= 10) {
                                    if (Objects.requireNonNull(edit_bio.getText()).toString().trim().equals(bio)) {
                                        btn_save.setBackground(ContextCompat.getDrawable(context, R.color.blue_grey_50));
                                        btn_save.setTextColor(context.getColor(R.color.black_semi_transparent4));
                                        btn_save.setEnabled(false);
                                    } else {
                                        btn_save.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                                        btn_save.setTextColor(context.getColor(R.color.white));
                                        btn_save.setEnabled(true);
                                    }

                                } else {
                                    btn_save.setBackground(ContextCompat.getDrawable(context, R.color.blue_grey_50));
                                    btn_save.setTextColor(context.getColor(R.color.black_semi_transparent4));
                                    btn_save.setEnabled(false);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        long date_member = following.getDate_following();
                        String bio = following.getMember_bio();
                        String the_date = TimeUtils.getTime(context, date_member);

                        date_following.setText(Html.fromHtml(context.getString(R.string.member_of_group)+" <b>"
                                +user_groups.getGroup_name()+"</b> "+context.getString(R.string.from)+" "+the_date));

                        if (!TextUtils.isEmpty(bio))
                            edit_bio.setText(bio);

                        if (!TextUtils.isEmpty(bio) && Objects.requireNonNull(edit_bio.getText()).toString().trim().equals(bio)) {
                            group_message.setTextColor(context.getColor(R.color.shinning_blue));
                            btn_save.setBackground(ContextCompat.getDrawable(context, R.color.blue_grey_50));
                            btn_save.setTextColor(context.getColor(R.color.black_semi_transparent4));
                            btn_save.setEnabled(false);
                        }

                        edit_bio.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                int char_length = charSequence.length();
                                nber_char_sequence.setText(String.valueOf(char_length));

                                if (char_length == 0) {
                                    group_message.setTextColor(context.getColor(R.color.black_semi_transparent));
                                    linLayout_nber_char_sequence.setVisibility(View.INVISIBLE);

                                } else if (char_length >= 1) {
                                    group_message.setTextColor(context.getColor(R.color.shinning_blue));
                                    linLayout_nber_char_sequence.setVisibility(View.VISIBLE);

                                }
                                if (char_length >= 10) {
                                    if (Objects.requireNonNull(edit_bio.getText()).toString().trim().equals(bio)) {
                                        btn_save.setBackground(ContextCompat.getDrawable(context, R.color.blue_grey_50));
                                        btn_save.setTextColor(context.getColor(R.color.black_semi_transparent4));
                                        btn_save.setEnabled(false);
                                    } else {
                                        btn_save.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                                        btn_save.setTextColor(context.getColor(R.color.white));
                                        btn_save.setEnabled(true);
                                    }

                                } else {
                                    btn_save.setBackground(ContextCompat.getDrawable(context, R.color.blue_grey_50));
                                    btn_save.setTextColor(context.getColor(R.color.black_semi_transparent4));
                                    btn_save.setEnabled(false);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // to get user profile photo background
        Query theQuery = myRef
                .child(getString(R.string.dbname_group))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        theQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    String full_profile_photo = model.getGroup_user_background_full_profile_url();
                    String background_profile_photo = model.getGroup_user_background_profile_url();
                    boolean isBackgroundProfile = model.isGroup_cover_background_profile_photo();

                    if (isBackgroundProfile) {
                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(background_profile_photo)
                                .into(profile_photo);

                        Glide.with(context.getApplicationContext())
                                .load(full_profile_photo)
                                .preload();

                        profile_photo.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, SeeGroupMemberAllBackgroundProfile.class);
                            intent.putExtra("group_id", user_groups.getGroup_id());
                            context.startActivity(intent);
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String profilUrl = user.getProfileUrl();
                    username.setText(user.getUsername());

                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(profilUrl)
                            .into(user_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    user_profile_photo.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, SeeUserAllProfileOnMiioky.class);
                        intent.putExtra("userID", user.getUser_id());
                        context.startActivity(intent);
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    private void theme() {
        String theme = user_groups.getGroup_theme();

        if (theme.equals(context.getString(R.string.theme_normal))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);

            // changer a couleur des icons en noir
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
            toolBar_group_name.setTextColor(context.getColor(R.color.black));
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            close.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_blue))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.blue_600));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_brown))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.background_brown));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_pink))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.pink));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_green))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.vertWatsApp));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if (theme.equals(context.getString(R.string.theme_black))) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getColor(R.color.black));

            // changer a couleur des icons en blanc
            View decor = context.getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
            toolBar_group_name.setTextColor(context.getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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