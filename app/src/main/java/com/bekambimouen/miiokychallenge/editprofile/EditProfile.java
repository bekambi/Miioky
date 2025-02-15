package com.bekambimouen.miiokychallenge.editprofile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.editprofile.adapter.AddCollegeAdapter;
import com.bekambimouen.miiokychallenge.editprofile.adapter.AddEstablishmentAdapter;
import com.bekambimouen.miiokychallenge.editprofile.adapter.AddWorkPlaceAdapter;
import com.bekambimouen.miiokychallenge.followersfollowings.model.FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.messages.model.Chat;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;
import com.bekambimouen.miiokychallenge.model.SchoolAttended;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.WorkAt;
import com.bekambimouen.miiokychallenge.register.RegisterLogin;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel_fun;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_Chat;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";

    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private ImageView profile_photo_blur;
    private TextView tv_phone_number, txt_username, txt_fullName, txt_bio, txt_town_name, txt_neighborhood_name;
    private TextView tv_birthday, relationship_status, gender, native_country, hometown;
    private CircleImageView user_profile_photo;
    private RecyclerView recyclerView_school, recyclerView_establishment, recyclerView_workplace;
    private RelativeLayout main, relLayout_no_connexion, relLayout_view_overlay;
    private RadioButton radio_public, radio_private;
    private ProgressBar progressBar;

    // vars
    private EditProfile context;
    private Dialog d, dialog, dialog_public, dialog_private;
    private ArrayList<SchoolAttended> schools_list;
    private ArrayList<FrequentedEstablishment> establishments_list;
    private ArrayList<WorkAt> workplaces_list;
    private ArrayList<String> group_created_id_list, group_following_id_list, following_user_id_list, friend_following_user_id_list,
            chat_keys_list, request_followers_id_list, invite_photo_id_list, invite_friends_following_id_list;
    private boolean isEmptyEmail, isEmptyPassword;
    private boolean isPasswordVisible = false;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_edit_profile);
        context = this;

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        schools_list = new ArrayList<>();
        establishments_list = new ArrayList<>();
        workplaces_list = new ArrayList<>();
        group_created_id_list = new ArrayList<>();
        group_following_id_list = new ArrayList<>();
        following_user_id_list = new ArrayList<>();
        friend_following_user_id_list = new ArrayList<>();
        chat_keys_list = new ArrayList<>();
        request_followers_id_list = new ArrayList<>();
        invite_photo_id_list = new ArrayList<>();
        invite_friends_following_id_list = new ArrayList<>();

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
        progressBar = findViewById(R.id.progressBar);
        main = findViewById(R.id.main);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout relLayout_username = findViewById(R.id.relLayout_username);
        RelativeLayout relLayout_fullname = findViewById(R.id.relLayout_fullname);
        RelativeLayout relLayout_bio = findViewById(R.id.relLayout_bio);
        RelativeLayout relLayout_town_name = findViewById(R.id.relLayout_town_name);
        RelativeLayout relLayout_neighborhood_name = findViewById(R.id.relLayout_neighborhood_name);
        RelativeLayout relLayout_relationship_status = findViewById(R.id.relLayout_relationship_status);
        RelativeLayout relLayout_gender = findViewById(R.id.relLayout_gender);
        user_profile_photo = findViewById(R.id.user_profile_photo);
        profile_photo_blur = findViewById(R.id.profile_photo_blur);
        txt_username = findViewById(R.id.txt_username);
        txt_bio = findViewById(R.id.txt_bio);
        txt_town_name = findViewById(R.id.txt_town_name);
        txt_neighborhood_name = findViewById(R.id.txt_neighborhood_name);
        txt_fullName = findViewById(R.id.txt_fullname);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_birthday = findViewById(R.id.tv_birthday);
        relationship_status = findViewById(R.id.relationship_status);
        gender = findViewById(R.id.gender);
        native_country = findViewById(R.id.native_country);
        hometown = findViewById(R.id.hometown);
        radio_public = findViewById(R.id.radio_public);
        radio_private = findViewById(R.id.radio_private);
        TextView tv_disconnexion = findViewById(R.id.tv_disconnexion);
        TextView tv_delete_account = findViewById(R.id.tv_delete_account);
        TextView button_add_college = findViewById(R.id.button_add_college);
        TextView button_add_establishment = findViewById(R.id.button_add_establishment);
        TextView button_add_workplace = findViewById(R.id.button_add_workplace);
        recyclerView_school = findViewById(R.id.recyclerView_school);
        recyclerView_school.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_establishment = findViewById(R.id.recyclerView_establishment);
        recyclerView_establishment.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_workplace = findViewById(R.id.recyclerView_workplace);
        recyclerView_workplace.setLayoutManager(new LinearLayoutManager(context));
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_no_connexion = findViewById(R.id.relLayout_no_connexion);
        AppCompatButton btn_retry = findViewById(R.id.btn_retry);

        user_profile_photo.setOnClickListener(view -> {
            Log.d(TAG, "onClick: changing profile photo");
            if (allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
            } else {
                Query query = myRef
                        .child(context.getString(R.string.dbname_users))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));

                            Intent intent = new Intent(context, ChooseProfilePhoto.class);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user);
                            intent.putExtra("user", myGson);

                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: query cancelled.");
                    }
                });
            }
        });

        // active connexion
        btn_retry.setOnClickListener(view1 -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

            if (!isConnected) {
                relLayout_no_connexion.setVisibility(View.VISIBLE);

            } else {
                relLayout_no_connexion.setVisibility(View.GONE);
            }
        });

        // get all school
        getAllSchool();
        // get all establishment
        getAllEstablishment();
        // get all workplace
        getAllWorkplace();

        tv_disconnexion.setOnClickListener(view -> {
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = view1.findViewById(R.id.dialog_title);
            TextView dialog_text = view1.findViewById(R.id.dialog_text);
            TextView negativeButton = view1.findViewById(R.id.tv_no);
            TextView positiveButton = view1.findViewById(R.id.tv_yes);
            builder.setView(view1);

            d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.cancel));
            positiveButton.setText(context.getString(R.string.ok));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            dialog_title.setVisibility(View.GONE);
            dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_disconnect)));
            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, main);
                } else {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    mAuth.signOut();
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));

                    startActivity(new Intent(context, RegisterLogin.class));
                    finish();
                }
            });
        });

        // delete account
        tv_delete_account.setOnClickListener(view -> {
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = view1.findViewById(R.id.dialog_title);
            TextView dialog_text = view1.findViewById(R.id.dialog_text);
            TextView negativeButton = view1.findViewById(R.id.tv_no);
            TextView positiveButton = view1.findViewById(R.id.tv_yes);
            builder.setView(view1);

            d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.cancel));
            positiveButton.setText(context.getString(R.string.delete));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            dialog_title.setVisibility(View.GONE);
            dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delet_your_account)));
            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();

                // show dialog box
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_delete_account, null);

                RelativeLayout rel_email = v.findViewById(R.id.rel_email);
                RelativeLayout rel_password = v.findViewById(R.id.rel_password);
                EditText edit_email = v.findViewById(R.id.edit_email);
                EditText edit_password = v.findViewById(R.id.edit_password);
                CheckBox checkBox = v.findViewById(R.id.checkBox);
                AppCompatButton btn_delete = v.findViewById(R.id.btn_delete);

                builder2.setView(v);
                dialog = builder2.create();
                dialog.show();

                edit_email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (isEmptyEmail) {
                            isEmptyEmail = false;

                            GradientDrawable drawable = (GradientDrawable) rel_email.getBackground();
                            drawable.mutate();
                            drawable.setStroke(1, context.getColor(R.color.grey));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                edit_password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (isEmptyPassword) {
                            isEmptyPassword = false;

                            GradientDrawable drawable = (GradientDrawable) rel_password.getBackground();
                            drawable.mutate();
                            drawable.setStroke(1, context.getColor(R.color.grey));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                // show/hide password
                checkBox.setOnClickListener(v1 -> {
                    boolean checked = ((CheckBox) v1).isChecked();

                    if (checked) {
                        edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        edit_password.setSelection(edit_password.getText().length());
                        checkBox.setText(context.getString(R.string.hide_password));

                    } else {
                        edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        edit_password.setSelection(edit_password.getText().length());
                        checkBox.setText(context.getString(R.string.show_password));
                    }
                });

                btn_delete.setOnClickListener(view3 -> {
                    // internet control
                    boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

                    if (!isConnected) {
                        CheckInternetStatus.internetIsConnected(context, main);
                    } else {
                        String email = edit_email.getText().toString().trim();
                        String password = edit_password.getText().toString().trim();

                        // delete account
                        delete_account(edit_email, edit_password, email, password, rel_email, rel_password);
                    }
                });
            });
        });

        relLayout_username.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_username", "update_username");
            startActivity(intent);
        });

        relLayout_fullname.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_fullName", "update_fullName");
            startActivity(intent);
        });

        relLayout_bio.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_bio", "update_bio");
            startActivity(intent);
        });

        relLayout_town_name.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_town_name", "update_town_name");
            startActivity(intent);
        });

        relLayout_neighborhood_name.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_neighborhood_name", "update_neighborhood_name");
            startActivity(intent);
        });

        relLayout_relationship_status.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_relationship_status", "update_relationship_status");
            startActivity(intent);
        });

        relLayout_gender.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("update_gender", "update_gender");
            startActivity(intent);
        });

        button_add_college.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("add_school", "add_school");
            startActivity(intent);
        });

        button_add_establishment.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("add_establishment", "add_establishment");
            startActivity(intent);
        });

        button_add_workplace.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, UpdateProfile.class);
            intent.putExtra("add_workplace", "add_workplace");
            startActivity(intent);
        });

        arrowBack.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));

                finish();
            }
        });
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

                    txt_bio.setText(user.getBio());

                    String scope = user.getScope();

                    if (scope.equals(context.getString(R.string.title_public)))
                        radio_public.setChecked(true);
                    if (scope.equals(context.getString(R.string.title_private)))
                        radio_private.setChecked(true);

                    // update the scope
                    radio_public.setOnClickListener(view -> {
                        radio_private.setChecked(false);

                        // show dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                        TextView negativeButton = v.findViewById(R.id.tv_no);
                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                        builder.setView(v);

                        dialog_public = builder.create();
                        dialog_public.setCancelable(false);
                        dialog_public.show();

                        negativeButton.setText(context.getString(R.string.cancel));
                        positiveButton.setText(context.getString(R.string.ok));

                        dialog_title.setText(context.getString(R.string.public_count));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.yours_news_feed_will_be_visible_to_everyone)));

                        negativeButton.setOnClickListener(view2 -> {
                            radio_private.setChecked(true);
                            radio_public.setChecked(false);
                            dialog_public.dismiss();
                        });

                        positiveButton.setOnClickListener(view2 -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(context.getString(R.string.field_scope), context.getString(R.string.title_public));

                            myRef.child(context.getString(R.string.dbname_users))
                                    .child(user_id)
                                    .updateChildren(map);

                            dialog_public.dismiss();
                        });
                    });

                    radio_private.setOnClickListener(view -> {
                        radio_public.setChecked(false);

                        // show dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                        TextView negativeButton = v.findViewById(R.id.tv_no);
                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                        builder.setView(v);

                        dialog_private = builder.create();
                        dialog_private.setCancelable(false);
                        dialog_private.show();

                        negativeButton.setText(context.getString(R.string.cancel));
                        positiveButton.setText(context.getString(R.string.ok));

                        dialog_title.setText(context.getString(R.string.private_count));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.anyone_who_wants_to_follow_yours_news_will_need_your_approval)));

                        negativeButton.setOnClickListener(view2 -> {
                            radio_private.setChecked(false);
                            radio_public.setChecked(true);
                            dialog_private.dismiss();
                        });

                        positiveButton.setOnClickListener(view2 -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(context.getString(R.string.field_scope), context.getString(R.string.title_private));

                            myRef.child(context.getString(R.string.dbname_users))
                                    .child(user_id)
                                    .updateChildren(map);
                            dialog_private.dismiss();
                        });
                    });

                    native_country.setText(user.getNative_country_name());
                    hometown.setText(Html.fromHtml(context.getString(R.string.is_from)+" "+user.getHometown_name()));

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(user_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo_blur);

                    txt_username.setText(user.getUsername());
                    txt_fullName.setText(user.getFullName());
                    // town and neighborhood
                    txt_town_name.setText(user.getTown_name());
                    txt_neighborhood_name.setText(user.getNeighborhood_name());
                    relationship_status.setText(user.getMarital_status());
                    gender.setText(user.getGender());

                    String formattedPhone = PhoneNumberUtils.formatNumber(user.getPhoneNumber(), Locale.getDefault().getCountry());
                    tv_phone_number.setText(formattedPhone);
                    tv_birthday.setText(String.valueOf(user.getBirthDay()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get all school
    private void getAllSchool() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                schools_list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_school)).getChildren()){
                        SchoolAttended school = new SchoolAttended();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        school.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        school.setSchoolKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_schoolKey))).toString());
                        school.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        school.setUser_school_attended(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended))).toString());
                        school.setUser_school_attended_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_school_attended_header))).toString());

                        schools_list.add(school);
                    }
                }

                AddCollegeAdapter adapter = new AddCollegeAdapter(context, schools_list, relLayout_view_overlay);
                recyclerView_school.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get all establishment
    private void getAllEstablishment() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                establishments_list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_establishments)).getChildren()){
                        FrequentedEstablishment establishment = new FrequentedEstablishment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        establishment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        establishment.setEstablishmentKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_establishmentKey))).toString());
                        establishment.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        establishment.setUser_establishment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment))).toString());
                        establishment.setUser_establishment_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_establishment_header))).toString());

                        establishments_list.add(establishment);
                    }
                }

                AddEstablishmentAdapter adapter = new AddEstablishmentAdapter(context, establishments_list, relLayout_view_overlay);
                recyclerView_establishment.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get all workplace
    private void getAllWorkplace() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workplaces_list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    for (DataSnapshot dSnapshot : dataSnapshot.child(context.getString(R.string.field_workAts)).getChildren()){
                        WorkAt workAt = new WorkAt();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        workAt.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
                        workAt.setWorkAtKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_workAtKey))).toString());
                        workAt.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
                        workAt.setUser_work_at(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at))).toString());
                        workAt.setUser_work_at_header(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_work_at_header))).toString());

                        workplaces_list.add(workAt);
                    }
                }

                AddWorkPlaceAdapter adapter = new AddWorkPlaceAdapter(context, workplaces_list, relLayout_view_overlay);
                recyclerView_workplace.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // delete account
    private void delete_account(EditText edit_email, EditText edit_password, String email, String password,
                                RelativeLayout rel_email, RelativeLayout rel_password) {
        String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern email_pattern = Pattern.compile(email_regex);
        Matcher email_matcher = email_pattern.matcher(email);
        boolean isValid_email = email_matcher.matches();

        if (!isValid_email || TextUtils.isEmpty(email)) {
            isEmptyEmail = true;
            GradientDrawable drawable = (GradientDrawable) rel_email.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_email.requestFocus();

        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            isEmptyPassword = true;
            GradientDrawable drawable = (GradientDrawable) rel_password.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_password.requestFocus();

        } else {
            dialog.dismiss();
            closeKeyboard();
            progressBar.setVisibility(View.VISIBLE);

            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            // Prompt the user to re-provide their sign-in credentials
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {

                            myRef.child(context.getString(R.string.dbname_users))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_user_account_settings))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_user_stores))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_already_response_challenge))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_battle))
                                    .child(user.getUid()).removeValue();

                            Query query12 = myRef
                                    .child(context.getString(R.string.dbname_battle_media))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .endAt(user.getUid());
                            query12.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                        if (model.getUser_id().equals(user.getUid())) {
                                            if (!model.getPhotoi_id().isEmpty()) {
                                                myRef.child(context.getString(R.string.dbname_battle_media))
                                                        .child(model.getPhotoi_id()).removeValue();
                                            }

                                            if (!model.getPhoto_id().isEmpty()) {
                                                myRef.child(context.getString(R.string.dbname_battle_media))
                                                        .child(model.getPhoto_id()).removeValue();
                                            }

                                            if (!model.getPhoto_id_un().isEmpty()) {
                                                myRef.child(context.getString(R.string.dbname_battle_media))
                                                        .child(model.getPhoto_id_un()).removeValue();
                                            }

                                            if (!model.getReponse_photoID().isEmpty()) {
                                                myRef.child(context.getString(R.string.dbname_battle_media))
                                                        .child(model.getReponse_photoID()).removeValue();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query13 = myRef
                                    .child(context.getString(R.string.dbname_battle_media_share))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .endAt(user.getUid());
                            query13.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                        if (model.getUser_id().equals(user.getUid())) {
                                            if (!model.getPhoto_id().isEmpty()) {
                                                myRef.child(context.getString(R.string.dbname_battle_media))
                                                        .child(model.getPhoto_id()).removeValue();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_complet_profile))
                                    .child(user.getUid()).removeValue();

                            Query query11 = myRef
                                    .child(context.getString(R.string.dbname_chat));
                            query11.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        chat_keys_list.add(ds.getKey());
                                    }

                                    if (!chat_keys_list.isEmpty()) {
                                        for(int i = 0; i < chat_keys_list.size(); i++){
                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_chat))
                                                    .child(chat_keys_list.get(i))
                                                    .orderByChild(context.getString(R.string.field_chat_key))
                                                    .equalTo(chat_keys_list.get(i));
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        Chat chat = Util_Chat.getChat(context, objectMap);

                                                        if (chat.getSender().equals(user.getUid())) {
                                                            myRef.child(context.getString(R.string.dbname_chat))
                                                                    .child(chat.getChat_key()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query10 = myRef
                                    .child(context.getString(R.string.dbname_friend_following))
                                    .child(user.getUid());
                            query10.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        friend_following_user_id_list.add(ds.getKey());
                                    }

                                    if (!friend_following_user_id_list.isEmpty()) {
                                        for(int i = 0; i < friend_following_user_id_list.size(); i++){
                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_friend_follower))
                                                    .child(friend_following_user_id_list.get(i))
                                                    .orderByChild(context.getString(R.string.field_user_id))
                                                    .equalTo(user.getUid());
                                            int finalI = i;
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        FriendsFollowerFollowing followers = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                                                        if (followers.getUser_id().equals(user.getUid())) {
                                                            myRef.child(context.getString(R.string.dbname_friend_follower))
                                                                    .child(friend_following_user_id_list.get(finalI))
                                                                    .child(user.getUid()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_friend_following))
                                    .child(user.getUid()).removeValue();

                            Query query9 = myRef
                                    .child(context.getString(R.string.dbname_following))
                                    .child(user.getUid());
                            query9.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        following_user_id_list.add(ds.getKey());
                                    }

                                    if (!following_user_id_list.isEmpty()) {
                                        for(int i = 0; i < following_user_id_list.size(); i++){
                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_followers))
                                                    .child(following_user_id_list.get(i))
                                                    .orderByChild(context.getString(R.string.field_user_id))
                                                    .equalTo(user.getUid());
                                            int finalI = i;
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        FollowerFollowingModel followers = Util_FollowerFollowingModel.getUtil_FollowerFollowingModel(context, objectMap);

                                                        if (followers.getUser_id().equals(user.getUid())) {
                                                            myRef.child(context.getString(R.string.dbname_followers))
                                                                    .child(following_user_id_list.get(finalI))
                                                                    .child(user.getUid()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_following))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_fun))
                                    .child(user.getUid()).removeValue();

                            Query query8 = myRef
                                    .child(context.getString(R.string.dbname_videos))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user.getUid());
                            query8.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, ds);

                                        if (model.getUser_id().equals(user.getUid())) {
                                            myRef.child(context.getString(R.string.dbname_videos))
                                                    .child(model.getPhoto_id()).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query = myRef
                                    .child(context.getString(R.string.dbname_user_stores_media))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user.getUid());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                        if (market_model.getUser_id().equals(user.getUid())) {
                                            myRef.child(context.getString(R.string.dbname_user_stores_media))
                                                    .child(market_model.getStore_id()).removeValue();

                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(market_model.getStore_id()).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_market))
                                    .child(user.getUid()).removeValue();

                            Query query4 = myRef
                                    .child(context.getString(R.string.dbname_market_media))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user.getUid());
                            query4.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                        if (market_model.getUser_id().equals(user.getUid())) {
                                            myRef.child(context.getString(R.string.dbname_market_media))
                                                    .child(market_model.getStore_id()).removeValue();

                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(market_model.getStore_id()).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query5 = myRef
                                    .child(context.getString(R.string.dbname_invite_battle))
                                    .child(user.getUid());
                            query5.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        invite_photo_id_list.add(ds.getKey());
                                    }

                                    if (!invite_photo_id_list.isEmpty()) {
                                        for(int i = 0; i < invite_photo_id_list.size(); i++){

                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_invite_battle_media))
                                                    .orderByChild(context.getString(R.string.field_invite_photoID))
                                                    .equalTo(invite_photo_id_list.get(i));
                                            int finalI = i;
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<String, Object> objectMap = (HashMap<String, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                                                        if (model.getInvite_photoID().equals(invite_photo_id_list.get(finalI))) {
                                                            myRef.child(context.getString(R.string.dbname_invite_battle_media))
                                                                    .child(model.getInvite_photoID()).removeValue();
                                                        }


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_invite_battle))
                                    .child(user.getUid()).removeValue();


                            Query query6 = myRef
                                    .child(context.getString(R.string.dbname_invitation_friend_following))
                                    .child(user.getUid());
                            query6.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        invite_friends_following_id_list.add(ds.getKey());
                                    }

                                    if (!invite_friends_following_id_list.isEmpty()) {
                                        for(int i = 0; i < invite_friends_following_id_list.size(); i++){

                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_invitation_friend_follower))
                                                    .child(invite_friends_following_id_list.get(i))
                                                    .orderByChild(context.getString(R.string.field_user_id))
                                                    .equalTo(user.getUid());
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        FriendsFollowerFollowing following = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                                                        if (following.getUser_id().equals(user.getUid())) {
                                                            myRef.child(context.getString(R.string.dbname_invitation_friend_follower))
                                                                    .child(following.getUser_id()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_invitation_friend_following))
                                    .child(user.getUid()).removeValue();

                            Query quer3 = myRef
                                    .child(context.getString(R.string.dbname_subscription_request_following));
                            quer3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        request_followers_id_list.add(ds.getKey());
                                    }

                                    if (!request_followers_id_list.isEmpty()) {
                                        for(int i = 0; i < request_followers_id_list.size(); i++){
                                            myRef.child(context.getString(R.string.dbname_subscription_request_follower))
                                                    .child(request_followers_id_list.get(i))
                                                    .child(user.getUid()).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_story))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_presence))
                                    .child(user.getUid()).removeValue();

                            myRef.child(context.getString(R.string.dbname_notification))
                                    .child(user.getUid()).removeValue();

                            // group followed by current user
                            Query query7 = myRef
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user.getUid());
                            query7.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        group_following_id_list.add(ds.getKey());
                                    }

                                    if (!group_following_id_list.isEmpty()) {
                                        for(int i = 0; i < group_following_id_list.size(); i++){
                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(group_following_id_list.get(i))
                                                    .orderByChild(context.getString(R.string.field_user_id))
                                                    .equalTo(user.getUid());
                                            int finalI = i;
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        GroupFollowersFollowing followers = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                                        if (followers.getUser_id().equals(user.getUid())) {
                                                            myRef.child(context.getString(R.string.dbname_group_followers))
                                                                    .child(group_following_id_list.get(finalI))
                                                                    .child(user.getUid()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_group_following))
                                    .child(user.getUid()).removeValue();

                            // group created by current user
                            Query query2 = myRef
                                    .child(context.getString(R.string.dbname_user_group))
                                    .child(user.getUid());
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        group_created_id_list.add(ds.getKey());
                                    }

                                    if (!group_created_id_list.isEmpty()) {
                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            myRef.child(context.getString(R.string.dbname_group))
                                                    .child(group_created_id_list.get(i)).removeValue();
                                        }

                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_group_media))
                                                    .orderByChild(context.getString(R.string.field_group_id))
                                                    .equalTo(group_created_id_list.get(i));
                                            int finalI = i;
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                                        if (model.getGroup_id().equals(group_created_id_list.get(finalI))) {
                                                            if (!model.getPhotoi_id().isEmpty()) {
                                                                myRef.child(context.getString(R.string.dbname_group_media))
                                                                        .child(model.getPhotoi_id()).removeValue();
                                                            }

                                                            if (!model.getPhoto_id().isEmpty()) {
                                                                myRef.child(context.getString(R.string.dbname_group_media))
                                                                        .child(model.getPhoto_id()).removeValue();
                                                            }

                                                            if (!model.getPhoto_id_un().isEmpty()) {
                                                                myRef.child(context.getString(R.string.dbname_group_media))
                                                                        .child(model.getPhoto_id_un()).removeValue();
                                                            }

                                                            if (!model.getReponse_photoID().isEmpty()) {
                                                                myRef.child(context.getString(R.string.dbname_group_media))
                                                                        .child(model.getReponse_photoID()).removeValue();
                                                            }

                                                            myRef.child(context.getString(R.string.dbname_notification))
                                                                    .child(model.getGroup_id()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            Query query = myRef
                                                    .child(context.getString(R.string.dbname_group_media_share))
                                                    .orderByChild(context.getString(R.string.field_group_id))
                                                    .equalTo(group_created_id_list.get(i));
                                            int finalI = i;
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                        assert objectMap != null;
                                                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                                        if (model.getGroup_id().equals(group_created_id_list.get(finalI))) {
                                                            myRef.child(context.getString(R.string.dbname_group_media_share))
                                                                    .child(model.getPhoto_id()).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            myRef.child(context.getString(R.string.dbname_group_report_post))
                                                    .child(group_created_id_list.get(i)).removeValue();
                                        }

                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            myRef.child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                                    .child(group_created_id_list.get(i)).removeValue();
                                        }

                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            myRef.child(context.getString(R.string.dbname_group_invitation_challenge))
                                                    .child(group_created_id_list.get(i)).removeValue();
                                        }

                                        for(int i = 0; i < group_created_id_list.size(); i++){
                                            myRef.child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                                    .child(group_created_id_list.get(i)).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            myRef.child(context.getString(R.string.dbname_user_group))
                                    .child(user.getUid()).removeValue();

                            user.delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));

                                    startActivity(new Intent(context, RegisterLogin.class));
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_LONG).show();
                                }
                            });

                        });
            }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            Intent intent = new Intent(context, ChooseProfilePhoto.class);
            context.startActivity(intent);
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

    public void internetIsConnected() {
        // internet control
        boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

        if (!isConnected) {
            relLayout_no_connexion.setVisibility(View.VISIBLE);

        } else {
            relLayout_no_connexion.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // verify internet connection
        internetIsConnected();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (d != null)
            d.dismiss();

        if (dialog != null)
            dialog.dismiss();

        if (dialog_public != null)
            dialog_public.dismiss();

        if (dialog_private != null)
            dialog_private.dismiss();
    }
}