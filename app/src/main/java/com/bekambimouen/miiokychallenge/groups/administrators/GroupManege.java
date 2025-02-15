package com.bekambimouen.miiokychallenge.groups.administrators;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.GroupMembership;
import com.bekambimouen.miiokychallenge.groups.manage_member.post_approval.GroupWaitingApproval;
import com.bekambimouen.miiokychallenge.groups.manage_member.report_post.GroupReportPost;
import com.bekambimouen.miiokychallenge.groups.manage_member.suspend.GroupAddRulesWithExample;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.GroupMembershipModel;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupMembershipModel;
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

public class GroupManege extends AppCompatActivity {
    private static final String TAG = "GroupManege";

    // widgets
    private TextView waiting_approbation_number_today, waiting_approbation_number,
            report_post_number_today, report_post_number, membership_request_number_today,
            membership_request_number;
    private RelativeLayout parent, relLayout_view_overlay;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_group_name;
    private Toolbar toolBar;

    // vars
    private GroupManege context;
    private UserGroups user_groups;
    private int t = 0, k = 0, m = 0, n = 0, p = 0, c = 0;

    private CustomShareProgressView progressDialog;

    private void showLoading(){
        if (progressDialog == null)
            progressDialog = new CustomShareProgressView(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_manege);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: erro: "+e.getMessage());
        }

        init();
        theme();
        getReportPostsCount();
        getWaitingApprovalPostsCount();
        getMemberShipRequestsCount();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
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

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        toolBar = findViewById(R.id.toolBar);
        arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_report_post = findViewById(R.id.relLayout_report_post);
        RelativeLayout relLayout_waiting_approval = findViewById(R.id.relLayout_waiting_approval);
        RelativeLayout relLayout_membership_request = findViewById(R.id.relLayout_membership_request);
        RelativeLayout relLayout_add_rules_in_the_group = findViewById(R.id.relLayout_add_rules_in_the_group);
        LinearLayout linLayout_put_in_pause = findViewById(R.id.linLayout_put_in_pause);
        LinearLayout linLayout_delete_group = findViewById(R.id.linLayout_delete_group);
        LinearLayout linLayout_leave_group = findViewById(R.id.linLayout_leave_group);
        TextView tv_put_on_pause = findViewById(R.id.tv_put_on_pause);
        ImageView image_play_pause = findViewById(R.id.image_play_pause);
        toolBar_group_name = findViewById(R.id.toolBar_group_name);
        close = findViewById(R.id.close);

        waiting_approbation_number_today = findViewById(R.id.waiting_approbation_number_today);
        waiting_approbation_number = findViewById(R.id.waiting_approbation_number);
        report_post_number_today = findViewById(R.id.report_post_number_today);
        report_post_number = findViewById(R.id.report_post_number);
        membership_request_number_today = findViewById(R.id.membership_request_number_today);
        membership_request_number = findViewById(R.id.membership_request_number);

        ImageView profile_photo = findViewById(R.id.profile_photo);
        toolBar_group_name.setText(user_groups.getGroup_name());

        // hide request membership if group is private
        if (user_groups.isGroup_private())
            relLayout_membership_request.setVisibility(View.VISIBLE);


        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(user_groups.getProfile_photo())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(profile_photo);

        relLayout_report_post.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupReportPost.class);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
        });

        // go to waiting approval class
        relLayout_waiting_approval.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupWaitingApproval.class);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
        });


        // go to membership class
        relLayout_membership_request.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupMembership.class);
            intent.putExtra("user_groups", myGson);
            context.startActivity(intent);
        });

        // add rules in the group
        Query query_rules = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query_rules.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    // hide the view if the group already have rules
                    if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
                        relLayout_add_rules_in_the_group.setVisibility(View.GONE);
                    }
                }

                relLayout_add_rules_in_the_group.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(user_groups);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupAddRulesWithExample.class);
                    intent.putExtra("user_groups", myGson);
                    startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // put group in pause for us
        if (user_id.equals(user_groups.getAdmin_master())) {
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            myQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups userGroups = Util_UserGroups.getUserGroups(context, objectMap);

                        if (userGroups.isGroup_paused()) {
                            image_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            tv_put_on_pause.setText(context.getString(R.string.cancel_group_paused));

                            linLayout_put_in_pause.setOnClickListener(view -> {
                                // show dialog box
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                                TextView negativeButton = view1.findViewById(R.id.tv_no);
                                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                                builder.setView(view1);

                                Dialog d = builder.create();
                                d.show();

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.ok));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.cancel_group_paused)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_will_see_post_again)
                                        +" <b>" +user_groups.getGroup_name()+"</b> "+context.getString(R.string.on_miioky)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view3 -> {
                                    image_play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                                    tv_put_on_pause.setText(context.getString(R.string.put_on_pause));

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("group_paused", false);

                                    myRef.child(context.getString(R.string.dbname_user_group))
                                            .child(user_groups.getAdmin_master())
                                            .child(user_groups.getGroup_id())
                                            .updateChildren(map);

                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    d.dismiss();
                                });
                            });

                        } else {
                            image_play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                            tv_put_on_pause.setText(context.getString(R.string.put_on_pause));

                            linLayout_put_in_pause.setOnClickListener(view -> {
                                // show dialog box
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                                TextView negativeButton = view1.findViewById(R.id.tv_no);
                                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                                builder.setView(view1);

                                Dialog d = builder.create();
                                d.show();

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.put_on_pause));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.put_on_pause)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_always_member)
                                        +" <b> " +user_groups.getGroup_name()+"</b>  "+context.getString(R.string.you_will_not_longer_see)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view3 -> {
                                    image_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                    tv_put_on_pause.setText(context.getString(R.string.cancel_group_paused));

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("group_paused", true);

                                    myRef.child(context.getString(R.string.dbname_user_group))
                                            .child(user_groups.getAdmin_master())
                                            .child(user_groups.getGroup_id())
                                            .updateChildren(map);

                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    d.dismiss();
                                });
                            });
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

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        if (following.isGroup_paused()) {
                            image_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            tv_put_on_pause.setText(context.getString(R.string.cancel_group_paused));

                            linLayout_put_in_pause.setOnClickListener(view -> {
                                // show dialog box
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                                TextView negativeButton = view1.findViewById(R.id.tv_no);
                                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                                builder.setView(view1);

                                Dialog d = builder.create();
                                d.show();

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.ok));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.cancel_group_paused)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_will_see_post_again)
                                        +" <b>" +user_groups.getGroup_name()+"</b> "+context.getString(R.string.on_miioky)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view3 -> {
                                    image_play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                                    tv_put_on_pause.setText(context.getString(R.string.put_on_pause));

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("group_paused", false);

                                    myRef.child(context.getString(R.string.dbname_group_following))
                                            .child(user_id)
                                            .child(user_groups.getGroup_id())
                                            .updateChildren(map);

                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    d.dismiss();
                                });
                            });

                        } else {
                            image_play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                            tv_put_on_pause.setText(context.getString(R.string.put_on_pause));

                            linLayout_put_in_pause.setOnClickListener(view -> {
                                // show dialog box
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                                TextView negativeButton = view1.findViewById(R.id.tv_no);
                                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                                builder.setView(view1);

                                Dialog d = builder.create();
                                d.show();

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.put_on_pause));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.put_on_pause)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_always_member)
                                        +" <b> " +user_groups.getGroup_name()+"</b>  "+context.getString(R.string.you_will_not_longer_see)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view3 -> {
                                    image_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                    tv_put_on_pause.setText(context.getString(R.string.cancel_group_paused));

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("group_paused", true);

                                    myRef.child(context.getString(R.string.dbname_group_following))
                                            .child(user_id)
                                            .child(user_groups.getGroup_id())
                                            .updateChildren(map);

                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    d.dismiss();
                                });
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (user_groups.getAdmin_master().equals(user_id)) {
            linLayout_delete_group.setVisibility(View.VISIBLE);

        } else {
            linLayout_leave_group.setVisibility(View.VISIBLE);
        }

        // delete this group
        linLayout_delete_group.setOnClickListener(view -> {
            if (user_groups.getAdmin_master().equals(user_id)) {
                // show dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                TextView negativeButton = view1.findViewById(R.id.tv_no);
                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                builder.setView(view1);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.cancel));
                positiveButton.setText(context.getString(R.string.continue_tv));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_group)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.be_careful_you_are_about_to_delete_your_group)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());

                // delete from group
                positiveButton.setOnClickListener(view2 -> {
                    d.dismiss();
                    showLoading();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("suppressed", true);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_user_group))
                            .child(user_id)
                            .child(user_groups.getGroup_id())
                            .updateChildren(map).addOnSuccessListener(unused1 -> {
                                progressDialog.dismiss();
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                            });
                });
            }
        });

        // when member admin leave the group
        linLayout_leave_group.setOnClickListener(view -> {
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = view1.findViewById(R.id.dialog_title);
            TextView dialog_text = view1.findViewById(R.id.dialog_text);
            TextView negativeButton = view1.findViewById(R.id.tv_no);
            TextView positiveButton = view1.findViewById(R.id.tv_yes);
            builder.setView(view1);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.cancel));
            positiveButton.setText(context.getString(R.string.quit));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.leave_the_group_title)));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_really_want_to_leave_group)
                    +" <b> " +user_groups.getGroup_name()+"?</b>  "));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .child(user_groups.getGroup_id())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id())
                        .child(user_id)
                        .removeValue().addOnSuccessListener(unused -> {
                            // clear all back stack fragments
                            int backStackCount = context.getSupportFragmentManager().getBackStackEntryCount();

                            for (int i = 0; i < backStackCount; i++) {
                                // get the back stack fragment id
                                int backStackId = context.getSupportFragmentManager().getBackStackEntryAt(i).getId();

                                context.getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        });
            });
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

    // get report post number
    private void getReportPostsCount() {
        Log.d(TAG, "getPhotos: getting list of posts");
        Query query = myRef
                .child(getString(R.string.dbname_group_report_post))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, dataSnapshot);

                    m++;

                    long currentTime = System.currentTimeMillis();
                    if ((model.getDate_created() + 86400000L) > currentTime)
                        n++;
                }

                report_post_number.setText(String.valueOf(m));
                report_post_number_today.setText(Html.fromHtml(context.getString(R.string.today)
                        +" "+ n));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get waiting approval number
    private void getWaitingApprovalPostsCount() {
        Log.d(TAG, "getPhotos: getting list of posts");
        Query query = myRef
                .child(getString(R.string.dbname_group_waiting_for_approval))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    BattleModel model = new BattleModel();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(getString(R.string.field_date_created))).toString()));

                    t++;

                    long currentTime = System.currentTimeMillis();
                    if ((model.getDate_created() + 86400000L) > currentTime)
                        k++;
                }

                waiting_approbation_number.setText(String.valueOf(t));
                waiting_approbation_number_today.setText(Html.fromHtml(context.getString(R.string.today)
                        +" "+ k));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get membership request number
    private void getMemberShipRequestsCount() {
        Log.d(TAG, "getPhotos: getting list of posts");
        Query query = myRef
                .child(getString(R.string.dbname_group_Membership_request_follower))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupMembershipModel model = Util_GroupMembershipModel.getGroupMembershipModel(context, objectMap);

                    p++;

                    long currentTime = System.currentTimeMillis();
                    if ((model.getDate_created() + 86400000L) > currentTime)
                        c++;
                }

                membership_request_number.setText(String.valueOf(p));
                membership_request_number_today.setText(Html.fromHtml(context.getString(R.string.today)
                        +" "+ c));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
        theme();
    }
}