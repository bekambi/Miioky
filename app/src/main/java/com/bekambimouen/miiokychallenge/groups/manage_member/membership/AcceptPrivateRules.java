package com.bekambimouen.miiokychallenge.groups.manage_member.membership;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.VerifyRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupMembershipModel;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AcceptPrivateRules extends AppCompatActivity {
    private static final String TAG = "AcceptPrivateRules";
    // widgets
    private RelativeLayout parent;
    // vars
    private AcceptPrivateRules context;
    private UserGroups user_groups;
    private List<Rule> rules_list;
    private List<String> admins_id_list;
    private String portee;
    private int t = 0;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_accept_private_rules);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        rules_list = new ArrayList<>();
        admins_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_buttons_join_bottom = findViewById(R.id.relLayout_buttons_join_bottom);
        ImageView group_profile_photo = findViewById(R.id.group_profile_photo);
        TextView group_name = findViewById(R.id.group_name);
        CheckBox checkBox = findViewById(R.id.checkBox);
        TextView total_members = findViewById(R.id.total_members);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                relLayout_buttons_join_bottom.setVisibility(View.VISIBLE);
            else
                relLayout_buttons_join_bottom.setVisibility(View.GONE);
        });

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

        VerifyRulesAdapter verifyRulesAdapter = new VerifyRulesAdapter(context, rules_list);
        recyclerView.setAdapter(verifyRulesAdapter);

        group_name.setText(user_groups.getGroup_name());

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(user_groups.getProfile_photo())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(group_profile_photo);

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                    t++;
                }

                portee = context.getString(R.string.title_private);

                if (t == 0) {
                    total_members.setText(Html.fromHtml(context.getString(R.string.group)+" "+
                            context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                            "<b>"+ (t+1)+"</b> "+context.getString(R.string.member_minus)));

                } else {
                    total_members.setText(Html.fromHtml(context.getString(R.string.group)+" "+
                            context.getString(R.string.open)+portee+context.getString(R.string.close)+" "+
                            "<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        relLayout_buttons_join_bottom.setOnClickListener(view1 -> {
            HashMap<Object, Object> map = Utils_Map_GroupMembershipModel.setGroupMembershipModel(user_id, user_groups.getAdmin_master(), user_groups.getGroup_id());
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_following))
                    .child(user_id)
                    .child(user_groups.getGroup_id())
                    .setValue(map);

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_follower))
                    .child(user_groups.getGroup_id())
                    .child(user_id)
                    .setValue(map);

            // send notification to all admins
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                    // first add admin aster id
                    admins_id_list.add(user_groups.getAdmin_master());

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        if (follower.isAdminInGroup() || follower.isModerator())
                            admins_id_list.add(follower.getUser_id());
                    }

                    // send notification
                    for (int i = 0; i < admins_id_list.size(); i++) {
                        Date date = new Date();
                        String new_key = myRef.push().getKey();
                        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                false, false, false, false, false,
                                false,false, false, false,
                                false, false, false, false, false, false,
                                false,
                                false, false, false, false, false,
                                false, true,
                                false, false, false, false, false,
                                false, false,
                                false, "", false, false, false, false,
                                true,false, false,
                                admins_id_list.get(i), new_key, user_id, user_groups.getAdmin_master(),
                                "", user_groups.getGroup_id(), date.getTime(),
                                false, false,
                                false, false, false, false, false, false, false, false,
                                false, "", "", "", false, "", "", "", false,
                                "", "", "", "", "", 0,
                                "", "", 0, "", "", "",
                                true, true, false, false,
                                false, false, false,
                                false, false);

                        assert new_key != null;
                        myRef.child(context.getString(R.string.dbname_notification))
                                .child(admins_id_list.get(i))
                                .child(new_key)
                                .setValue(map_notification);

                        // add badge number
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", admins_id_list.get(i));

                        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                .child(admins_id_list.get(i))
                                .child(new_key)
                                .setValue(map_number);
                    }
                }

                @Override
                public void onCancelled(@org.jetbrains.annotations.NotNull DatabaseError error) {

                }
            });

            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
            finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
        // change status bar color
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.WHITE);

        // changer a couleur des icons en noir
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}