package com.bekambimouen.miiokychallenge.groups.manage_member.suspend;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.ShowRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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

public class GroupSuspendMember extends AppCompatActivity {
    private static final String TAG = "GroupSuspendMember";
    // widgets
    private MyEditText edit_about_group;
    private TextView button_suspend;
    private RelativeLayout parent, relLayout_edit_about_group, relLayout_view_overlay;

    // vars
    private GroupSuspendMember context;
    private ShowRulesAdapter adapter;
    private UserGroups user_groups;
    private String the_user_id, suspension_duration;
    private List<Rule> rules_list, rules_item, currentSelectedRules;
    private int position;
    private boolean isExpand = false, isRadioButtonClicked = false;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_suspend_member);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        rules_list = new ArrayList<>();
        rules_item = new ArrayList<>();
        currentSelectedRules = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
            the_user_id = getIntent().getStringExtra("the_user_id");
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
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_edit_about_group = findViewById(R.id.relLayout_edit_about_group);
        RelativeLayout relLayout_see_more = findViewById(R.id.relLayout_see_more);
        LinearLayout linLayout_recyclerView = findViewById(R.id.linLayout_recyclerView);
        LinearLayout linLayout_invite_to_write_rules = findViewById(R.id.linLayout_invite_to_write_rules);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        edit_about_group = findViewById(R.id.edit_about_group);
        TextView nber_char_sequence = findViewById(R.id.nber_char_sequence);
        TextView toolBar_user_name = findViewById(R.id.toolBar_user_name);
        TextView button_suspension_message = findViewById(R.id.button_suspension_message);
        TextView suspension_reason = findViewById(R.id.suspension_reason);
        TextView leave_comment = findViewById(R.id.leave_comment);
        TextView suspension_time = findViewById(R.id.suspension_time);
        TextView go_add_rules = findViewById(R.id.go_add_rules);
        button_suspend = findViewById(R.id.button_suspend);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        edit_about_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_sequence.setText(String.valueOf(char_length));

                if (char_length == 1) {
                    GradientDrawable drawable = (GradientDrawable) relLayout_edit_about_group.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.black_transparent));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // part of textView clickabble
        SpannableString ss = new SpannableString(context.getString(R.string.group_without_rules));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupAddRulesWithExample.class);
                intent.putExtra("user_groups", myGson);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 32, 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getColor(R.color.blue_600));
        ss.setSpan(colorSpan, 32, 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), 32, 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.ITALIC), 32, 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        go_add_rules.setClickable(true);

        go_add_rules.setText(ss);
        go_add_rules.setMovementMethod(LinkMovementMethod.getInstance());
        go_add_rules.setHighlightColor(Color.TRANSPARENT);

        // go to user group profile
        Query query1 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(the_user_id);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    toolBar_user_name.setText(Html.fromHtml(context.getString(R.string.suspend_member) + " <b>" + user.getUsername() + "</b>"));
                    suspension_time.setText(Html.fromHtml(context.getString(R.string.suspension_time) + " " + user.getUsername() + " ?"));
                    button_suspension_message.setText(Html.fromHtml(user.getUsername() + " " + context.getString(R.string.suspended_will_not_see_your_name)));
                    suspension_reason.setText(Html.fromHtml(context.getString(R.string.indicate_to)
                            + " " + user.getUsername() + " " + context.getString(R.string.the_reason_of_suspension)));
                    leave_comment.setText(Html.fromHtml(context.getString(R.string.leave_a_comment_to) + " " + user.getUsername()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
                        rules_list.add(new Rule(user_groups.getRule_title_one(), user_groups.getRule_detail_one()));
                        rules_item.add(new Rule(user_groups.getRule_title_one(), user_groups.getRule_detail_one()));
                    }
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
                }

                if (!isExpand) {
                    adapter = new ShowRulesAdapter(context, rules_item, new ShowRulesAdapter.OnItemCheckListener() {
                        @Override
                        public void onItemCheck(Rule rule) {
                            currentSelectedRules.add(rule);
                        }

                        @Override
                        public void onItemUncheck(Rule rule) {
                            currentSelectedRules.remove(rule);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

                if (rules_list.size() == 1)
                    relLayout_see_more.setVisibility(View.GONE);

                if (adapter.getItemCount() >= 1) {
                    linLayout_invite_to_write_rules.setVisibility(View.GONE);
                    linLayout_recyclerView.setVisibility(View.VISIBLE);
                }

                relLayout_see_more.setOnClickListener(view -> {
                    isExpand = true;
                    rules_item.clear();
                    currentSelectedRules.clear();
                    adapter = null;
                    adapter = new ShowRulesAdapter(context, rules_list, new ShowRulesAdapter.OnItemCheckListener() {
                        @Override
                        public void onItemCheck(Rule rule) {
                            currentSelectedRules.add(rule);
                        }

                        @Override
                        public void onItemUncheck(Rule rule) {
                            currentSelectedRules.remove(rule);
                        }
                    });

                    recyclerView.setAdapter(adapter);
                    relLayout_see_more.setVisibility(View.GONE);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // if radio button not clicked
        if (!isRadioButtonClicked) {
            setSuspendMember(1);
        }

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            isRadioButtonClicked = true;
            int radioBtnID = radioGroup1.getCheckedRadioButtonId();

            if(radioBtnID == R.id.radio_id1){
                position = 1;
            } else if(radioBtnID == R.id.radio_id2){
                position = 2;
            } else if(radioBtnID == R.id.radio_id3){
                position = 3;
            } else if(radioBtnID == R.id.radio_id4){
                position = 4;
            } else if(radioBtnID == R.id.radio_id5){
                position = 5;
            } else if(radioBtnID == R.id.radio_id6){
                position = 6;
            }

            setSuspendMember(position);
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

    private void setSuspendMember(int position) {
        button_suspend.setOnClickListener(view -> {
            String adminComment = Objects.requireNonNull(edit_about_group.getText()).toString();

            if (TextUtils.isEmpty(adminComment)) {
                GradientDrawable drawable = (GradientDrawable) relLayout_edit_about_group.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_about_group.requestFocus();
                return;
            }

            if (adapter.getItemCount() != 0 && currentSelectedRules.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.the_non_respected_rules), Toast.LENGTH_LONG).show();
                return;
            }

            button_suspend.setEnabled(false);

            if (position == 1)
                suspension_duration = "12";
            if (position == 2)
                suspension_duration = "24";
            if (position == 3)
                suspension_duration = "3";
            if (position == 4)
                suspension_duration = "7";
            if (position == 5)
                suspension_duration = "14";
            if (position == 6)
                suspension_duration = "28";

            Date date = new Date();
            HashMap<String, Object> map = new HashMap<>();
            map.put("suspended", true);
            map.put("sanction_admin_comment", adminComment);
            map.put("suspension_duration", suspension_duration);
            map.put("date_admin_applied_sanction_in_group", date.getTime());

            if (currentSelectedRules.size() == 1) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
            }
            if (currentSelectedRules.size() == 2) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
            }
            if (currentSelectedRules.size() == 3) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
            }
            if (currentSelectedRules.size() == 4) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
            }
            if (currentSelectedRules.size() == 5) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
                map.put("rule_title_five", currentSelectedRules.get(4).getTitle());
                map.put("rule_detail_five", currentSelectedRules.get(4).getDetails());
            }
            if (currentSelectedRules.size() == 6) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
                map.put("rule_title_five", currentSelectedRules.get(4).getTitle());
                map.put("rule_detail_five", currentSelectedRules.get(4).getDetails());
                map.put("rule_title_six", currentSelectedRules.get(5).getTitle());
                map.put("rule_detail_six", currentSelectedRules.get(5).getDetails());
            }
            if (currentSelectedRules.size() == 7) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
                map.put("rule_title_five", currentSelectedRules.get(4).getTitle());
                map.put("rule_detail_five", currentSelectedRules.get(4).getDetails());
                map.put("rule_title_six", currentSelectedRules.get(5).getTitle());
                map.put("rule_detail_six", currentSelectedRules.get(5).getDetails());
                map.put("rule_title_seven", currentSelectedRules.get(6).getTitle());
                map.put("rule_detail_seven", currentSelectedRules.get(6).getDetails());
            }
            if (currentSelectedRules.size() == 8) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
                map.put("rule_title_five", currentSelectedRules.get(4).getTitle());
                map.put("rule_detail_five", currentSelectedRules.get(4).getDetails());
                map.put("rule_title_six", currentSelectedRules.get(5).getTitle());
                map.put("rule_detail_six", currentSelectedRules.get(5).getDetails());
                map.put("rule_title_seven", currentSelectedRules.get(6).getTitle());
                map.put("rule_detail_seven", currentSelectedRules.get(6).getDetails());
                map.put("rule_title_eight", currentSelectedRules.get(7).getTitle());
                map.put("rule_detail_eight", currentSelectedRules.get(7).getDetails());
            }
            if (currentSelectedRules.size() == 9) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
                map.put("rule_title_five", currentSelectedRules.get(4).getTitle());
                map.put("rule_detail_five", currentSelectedRules.get(4).getDetails());
                map.put("rule_title_six", currentSelectedRules.get(5).getTitle());
                map.put("rule_detail_six", currentSelectedRules.get(5).getDetails());
                map.put("rule_title_seven", currentSelectedRules.get(6).getTitle());
                map.put("rule_detail_seven", currentSelectedRules.get(6).getDetails());
                map.put("rule_title_eight", currentSelectedRules.get(7).getTitle());
                map.put("rule_detail_eight", currentSelectedRules.get(7).getDetails());
                map.put("rule_title_nine", currentSelectedRules.get(8).getTitle());
                map.put("rule_detail_nine", currentSelectedRules.get(8).getDetails());
            }
            if (currentSelectedRules.size() == 10) {
                map.put("rule_title_one", currentSelectedRules.get(0).getTitle());
                map.put("rule_detail_one", currentSelectedRules.get(0).getDetails());
                map.put("rule_title_two", currentSelectedRules.get(1).getTitle());
                map.put("rule_detail_two", currentSelectedRules.get(1).getDetails());
                map.put("rule_title_three", currentSelectedRules.get(2).getTitle());
                map.put("rule_detail_three", currentSelectedRules.get(2).getDetails());
                map.put("rule_title_four", currentSelectedRules.get(3).getTitle());
                map.put("rule_detail_four", currentSelectedRules.get(3).getDetails());
                map.put("rule_title_five", currentSelectedRules.get(4).getTitle());
                map.put("rule_detail_five", currentSelectedRules.get(4).getDetails());
                map.put("rule_title_six", currentSelectedRules.get(5).getTitle());
                map.put("rule_detail_six", currentSelectedRules.get(5).getDetails());
                map.put("rule_title_seven", currentSelectedRules.get(6).getTitle());
                map.put("rule_detail_seven", currentSelectedRules.get(6).getDetails());
                map.put("rule_title_eight", currentSelectedRules.get(7).getTitle());
                map.put("rule_detail_eight", currentSelectedRules.get(7).getDetails());
                map.put("rule_title_nine", currentSelectedRules.get(8).getTitle());
                map.put("rule_detail_nine", currentSelectedRules.get(8).getDetails());
                map.put("rule_title_ten", currentSelectedRules.get(9).getTitle());
                map.put("rule_detail_ten", currentSelectedRules.get(9).getDetails());
            }

            // suspend member
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_following))
                    .child(the_user_id)
                    .child(user_groups.getGroup_id())
                    .updateChildren(map);

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id())
                    .child(the_user_id)
                    .updateChildren(map);

            // send notification
            String new_key = myRef.push().getKey();
            Date date1 = new Date();

            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                    true, false, true, true, false,
                    false,false, false, false,
                    false, false, false, false, false, false,
                    false,
                    false, false, false, false, false,
                    false, false,
                    false, false, false, false, false,
                    false, false,
                    false, "", false, false, false, false,
                    true,false, false,
                    the_user_id, new_key, the_user_id, user_groups.getAdmin_master(),
                    "", user_groups.getGroup_id(), date1.getTime(),
                    false, false,
                    false, false, false, false, false, false, false, false,
                    false, "", "", "", false, "", "", "", false,
                    "", "", "", "", "", 0,
                    "", "", 0, "", "", "",
                    false, false, false, false,
                    false, false, false,
                    false, false);

            assert new_key != null;
            myRef.child(context.getString(R.string.dbname_notification))
                    .child(the_user_id)
                    .child(new_key)
                    .setValue(map_notification).addOnSuccessListener(unused -> {
                        // add badge number
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", the_user_id);

                        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                .child(the_user_id)
                                .child(new_key)
                                .setValue(map_number);
                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                        finish();
                    });
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);

        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.WHITE);

        // changer a couleur des icons en noir
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}