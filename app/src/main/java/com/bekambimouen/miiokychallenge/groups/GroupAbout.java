package com.bekambimouen.miiokychallenge.groups;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupSeeAllMembersByAdmin;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.VerifyRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupSeeAllMembers;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAbout extends AppCompatActivity {
    private static final String TAG = "GroupAbout";

    // widgets
    private TextView publications_today;
    private TextView publications_last_month;
    private TextView total_members;
    private TextView total_members_last_sevens_days;
    private CircleImageView member_i, member_ii, member_iii, member_iv, member_v, member_vi, member_vii,
            member_viii, member_ix;
    private RelativeLayout relLayout_profile_members, relLayout_member_i, relLayout_member_ii,
            relLayout_member_iii, relLayout_member_iv, relLayout_member_v, relLayout_member_vi,
            relLayout_member_vii, relLayout_member_viii, relLayout_member_ix, relLayout_member_x;
    private RelativeLayout parent, relLayout_view_overlay;
    private LinearLayout linLayout_recyclerview_rules, linLayout_members;
    private RecyclerView recyclerView;
    private View view_bar;

    // theme
    private RelativeLayout arrowBack;
    private ImageView close;
    private TextView toolBar_group_name;
    private Toolbar toolBar;

    // vars
    private GroupAbout context;
    private UserGroups user_groups;
    private List<Rule> rules_list;
    private String from_admin;
    private int n = 0, m = 0, t = 0, k = 0;
    private ArrayList<String> membersList;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_about);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        membersList = new ArrayList<>();
        rules_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);

            from_admin = getIntent().getStringExtra("from_admin");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        theme();
        getData();
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

    private void getData() {
        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one()))
                        rules_list.add(new Rule(user_groups.getRule_title_one(), user_groups.getRule_detail_one()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_two()) && !TextUtils.isEmpty(user_groups.getRule_detail_two()))
                        rules_list.add(new Rule(user_groups.getRule_title_two(), user_groups.getRule_detail_two()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_three()) && !TextUtils.isEmpty(user_groups.getRule_detail_three()))
                        rules_list.add(new Rule(user_groups.getRule_title_three(), user_groups.getRule_detail_three()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_four()) && !TextUtils.isEmpty(user_groups.getRule_detail_four()))
                        rules_list.add(new Rule(user_groups.getRule_title_four(), user_groups.getRule_detail_four()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_five()) && !TextUtils.isEmpty(user_groups.getRule_detail_five()))
                        rules_list.add(new Rule(user_groups.getRule_title_five(), user_groups.getRule_detail_five()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_six()) && !TextUtils.isEmpty(user_groups.getRule_detail_six()))
                        rules_list.add(new Rule(user_groups.getRule_title_six(), user_groups.getRule_detail_six()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_seven()) && !TextUtils.isEmpty(user_groups.getRule_detail_seven()))
                        rules_list.add(new Rule(user_groups.getRule_title_seven(), user_groups.getRule_detail_seven()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_eight()) && !TextUtils.isEmpty(user_groups.getRule_detail_eight()))
                        rules_list.add(new Rule(user_groups.getRule_title_eight(), user_groups.getRule_detail_eight()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_nine()) && !TextUtils.isEmpty(user_groups.getRule_detail_nine()))
                        rules_list.add(new Rule(user_groups.getRule_title_nine(), user_groups.getRule_detail_nine()));
                    if (!TextUtils.isEmpty(user_groups.getRule_title_ten()) && !TextUtils.isEmpty(user_groups.getRule_detail_ten()))
                        rules_list.add(new Rule(user_groups.getRule_title_ten(), user_groups.getRule_detail_ten()));

                    // get different rules
                    VerifyRulesAdapter adapter = new VerifyRulesAdapter(context, rules_list);
                    recyclerView.setAdapter(adapter);

                    if (adapter.getItemCount() == 0)
                        linLayout_recyclerview_rules.setVisibility(View.GONE);
                    else
                        view_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        toolBar = findViewById(R.id.toolBar);
        arrowBack = findViewById(R.id.arrowBack);
        close = findViewById(R.id.close);
        toolBar_group_name = findViewById(R.id.toolBar_group_name);
        RelativeLayout relLayout_see_more = findViewById(R.id.relLayout_see_more);
        RelativeLayout relLayout_go_group_history = findViewById(R.id.relLayout_go_group_history);
        relLayout_profile_members = findViewById(R.id.relLayout_profile_members);
        relLayout_member_i = findViewById(R.id.relLayout_member_i);
        relLayout_member_ii = findViewById(R.id.relLayout_member_ii);
        relLayout_member_iii = findViewById(R.id.relLayout_member_iii);
        relLayout_member_iv = findViewById(R.id.relLayout_member_iv);
        relLayout_member_v = findViewById(R.id.relLayout_member_v);
        relLayout_member_vi = findViewById(R.id.relLayout_member_vi);
        relLayout_member_vii = findViewById(R.id.relLayout_member_vii);
        relLayout_member_viii = findViewById(R.id.relLayout_member_viii);
        relLayout_member_ix = findViewById(R.id.relLayout_member_ix);
        relLayout_member_x = findViewById(R.id.relLayout_member_x);
        member_i = findViewById(R.id.member_i);
        member_ii = findViewById(R.id.member_ii);
        member_iii = findViewById(R.id.member_iii);
        member_iv = findViewById(R.id.member_iv);
        member_v = findViewById(R.id.member_v);
        member_vi = findViewById(R.id.member_vi);
        member_vii = findViewById(R.id.member_vii);
        member_viii = findViewById(R.id.member_viii);
        member_ix = findViewById(R.id.member_ix);
        TextView group_msg = findViewById(R.id.group_msg);
        TextView date_group_creation = findViewById(R.id.date_group_creation);
        TextView date_group_creation2 = findViewById(R.id.date_group_creation2);
        TextView admin_master_username = findViewById(R.id.admin_master_username);
        publications_today = findViewById(R.id.publications_today);
        publications_last_month = findViewById(R.id.publications_last_month);
        total_members = findViewById(R.id.total_members);
        total_members_last_sevens_days = findViewById(R.id.total_members_last_sevens_days);
        TextView title_public_private = findViewById(R.id.title_public_private);
        TextView tv_title_public_private = findViewById(R.id.tv_title_public_private);

        linLayout_recyclerview_rules = findViewById(R.id.linLayout_recyclerview_rules);
        linLayout_members = findViewById(R.id.linLayout_members);
        view_bar = findViewById(R.id.view_bar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CircleImageView admin_master_profile_photo = findViewById(R.id.admin_master_profile_photo);

        toolBar_group_name.setText(user_groups.getGroup_name());
        if (!TextUtils.isEmpty(user_groups.getGroup_message()))
            group_msg.setVisibility(View.VISIBLE);
        group_msg.setText(user_groups.getGroup_message());

        long date = user_groups.getDate_created();
        long time = (System.currentTimeMillis() - date);

        if (time >= 2*3600*24000254025L)
            date_group_creation.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                    +" "+context.getString(R.string.the)+" "+ TimeUtils.getTime(context, date)));
        else
            date_group_creation.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                    +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date)));

        if (time >= 2*3600*24000254025L)
            date_group_creation2.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                    +" "+context.getString(R.string.the)+" "+ TimeUtils.getTime(context, date)));
        else
            date_group_creation2.setText(Html.fromHtml(context.getString(R.string.group_created_today)+" "
                    +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date)));

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_groups.getAdmin_master());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    admin_master_username.setText(Html.fromHtml("<b>"+user.getUsername()+"</b> "+context.getString(R.string.created_the_group2)));

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user.getProfileUrl())
                                .into(admin_master_profile_photo);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        relLayout_go_group_history.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupHistory.class);
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
        });

        // verify if user is member or admin of group
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_following))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    linLayout_members.setVisibility(View.VISIBLE);
                }
                if (user_groups.getAdmin_master().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    linLayout_members.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (user_groups.isGroup_public()) {
            title_public_private.setText(context.getString(R.string.title_public));
            tv_title_public_private.setText(context.getString(R.string.everyone_can_see_who_is_in_the_group));
            linLayout_members.setVisibility(View.VISIBLE);
        } else {
            title_public_private.setText(context.getString(R.string.title_private));
            tv_title_public_private.setText(context.getString(R.string.only_members_can_see_who_is_in_the_group));
            linLayout_members.setVisibility(View.GONE);
        }

        relLayout_see_more.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent;
            if (from_admin != null) {
                intent = new Intent(context, GroupSeeAllMembersByAdmin.class);
            }
            else {
                intent = new Intent(context, GroupSeeAllMembers.class);
            }
            intent.putExtra("user_groups", myGson);
            startActivity(intent);
        });

        getNewPublicatiosNumbers();
        getNewMembersNumbers();

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

    private void getNewPublicatiosNumbers() {
        Log.d(TAG, "getPhotos: getting list of photos");
        Query query = myRef
                .child(getString(R.string.dbname_group))
                .child(user_groups.getGroup_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    BattleModel model = new BattleModel();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));

                    long date_create = model.getDate_created();
                    // 1 day later
                    if (TimeUtils.isDateToday(date_create))
                        n++;

                    // 1 month later
                    if ((model.getDate_created() + 86400000L*28) > System.currentTimeMillis())
                        m++;
                }

                publications_today.setText(Html.fromHtml(n +" "+context.getString(R.string.publications_today)));
                publications_last_month.setText(Html.fromHtml(m +" "+context.getString(R.string.publications_last_month)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNewMembersNumbers() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    // total members
                    t++;

                    // member from last seven days
                    if ((follower.getDate_following() + 86400000L*7) > System.currentTimeMillis())
                        k++;

                    String member = follower.getUser_id();
                    if (!follower.isBanFromGroup())
                        membersList.add(member);
                }

                total_members.setText(Html.fromHtml(""+ (t+1)+" "+context.getString(R.string.total_members)));
                total_members_last_sevens_days.setText(Html.fromHtml("+"+ k +" "+context.getString(R.string.total_members_last_sevens_days)));

                if (!membersList.isEmpty()) {
                    relLayout_profile_members.setVisibility(View.VISIBLE);

                    if (membersList.size() == 1) {
                        relLayout_member_i.setVisibility(View.VISIBLE);

                        Query query = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 2) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 3) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 4) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 5) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 6) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 7) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() == 8) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);
                        relLayout_member_viii.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_viii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(7));

                        query_viii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_viii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (membersList.size() >= 9) {
                        relLayout_member_i.setVisibility(View.VISIBLE);
                        relLayout_member_ii.setVisibility(View.VISIBLE);
                        relLayout_member_iii.setVisibility(View.VISIBLE);
                        relLayout_member_iv.setVisibility(View.VISIBLE);
                        relLayout_member_v.setVisibility(View.VISIBLE);
                        relLayout_member_vi.setVisibility(View.VISIBLE);
                        relLayout_member_vii.setVisibility(View.VISIBLE);
                        relLayout_member_viii.setVisibility(View.VISIBLE);
                        relLayout_member_ix.setVisibility(View.VISIBLE);
                        relLayout_member_x.setVisibility(View.VISIBLE);

                        Query query_i = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(0));

                        query_i.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_i);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(1));

                        query_ii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(2));

                        query_iii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_iv = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(3));

                        query_iv.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_iv);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_v = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(4));

                        query_v.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_v);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vi = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(5));

                        query_vi.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vi);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_vii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(6));

                        query_vii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_vii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_viii = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(7));

                        query_viii.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_viii);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query query_ix = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(membersList.get(8));

                        query_ix.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    if (!context.isFinishing()) {
                                        Glide.with(context)
                                                .load(user.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(member_ix);
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