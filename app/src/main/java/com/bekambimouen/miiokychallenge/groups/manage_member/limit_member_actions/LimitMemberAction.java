package com.bekambimouen.miiokychallenge.groups.manage_member.limit_member_actions;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

public class LimitMemberAction extends AppCompatActivity {
    private static final String TAG = "LimitMemberAction";

    // widgets
    private Spinner spinnerCustom_number_posts, spinnerCustom_limit_posts_expiration,
            spinnerCustom_number_comments, spinnerCustom_limit_comments_expiration;
    private SwitchCompat switchCompat_number_posts, switchCompat_number_comments;
    private RecyclerView recyclerView;
    private TextView tv_add_comment, tv_non_respected_rules;
    private MyEditText edit_about_group;
    private LinearLayout linLayout_admin_notes;
    private RelativeLayout parent, relLayout_edit_about_group;

    // theme
    private TextView toolBar_textview, button_save;

    // vars
    private LimitMemberAction context;
    private ShowRulesAdapter adapter;
    private UserGroups user_groups;
    private List<String> spinnerPostsNumberList, spinnerPostsExpirationList,
            spinnerCommentsNumberList, spinnerCommentsExpirationList;
    private List<Rule> rules_list, currentSelectedRules;
    private String the_user_id;
    private String posts_per_day, posts_per_day_expiration, comments_per_day, comments_per_day_expiration;
    private boolean postsActivityLimited = false, commentsActivityLimited = false;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_limit_member_action);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        rules_list = new ArrayList<>();
        currentSelectedRules = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
            the_user_id = getIntent().getStringExtra("the_user_id");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        // posts limit number
        spinnerPostsNumberList = new ArrayList<>();
        spinnerPostsNumberList.add("1");
        spinnerPostsNumberList.add("2");
        spinnerPostsNumberList.add("3");
        spinnerPostsNumberList.add("4");
        spinnerPostsNumberList.add("5");
        spinnerPostsNumberList.add("6");
        spinnerPostsNumberList.add("7");
        spinnerPostsNumberList.add("8");
        spinnerPostsNumberList.add("9");
        // posts limit expiration
        spinnerPostsExpirationList = new ArrayList<>();
        spinnerPostsExpirationList.add("12 "+context.getString(R.string.hours));
        spinnerPostsExpirationList.add("24 "+context.getString(R.string.hours));
        spinnerPostsExpirationList.add("3 "+context.getString(R.string.days));
        spinnerPostsExpirationList.add("7 "+context.getString(R.string.days));
        spinnerPostsExpirationList.add("14 "+context.getString(R.string.days));
        spinnerPostsExpirationList.add("28 "+context.getString(R.string.days));

        // cmoments limit number
        spinnerCommentsNumberList = new ArrayList<>();
        spinnerCommentsNumberList.add("1");
        spinnerCommentsNumberList.add("5");
        spinnerCommentsNumberList.add("10");
        spinnerCommentsNumberList.add("15");
        spinnerCommentsNumberList.add("20");
        spinnerCommentsNumberList.add("25");
        spinnerCommentsNumberList.add("30");
        // comments expiration limit
        spinnerCommentsExpirationList = new ArrayList<>();
        spinnerCommentsExpirationList.add("12 "+context.getString(R.string.hours));
        spinnerCommentsExpirationList.add("24 "+context.getString(R.string.hours));
        spinnerCommentsExpirationList.add("3 "+context.getString(R.string.days));
        spinnerCommentsExpirationList.add("7 "+context.getString(R.string.days));
        spinnerCommentsExpirationList.add("14 "+context.getString(R.string.days));
        spinnerCommentsExpirationList.add("28 "+context.getString(R.string.days));

        init();
        getGroupRules();
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
        toolBar_textview = findViewById(R.id.toolBar_textview);
        TextView button_cancel = findViewById(R.id.button_cancel);
        button_save = findViewById(R.id.button_save);
        TextView tv_firstInformation = findViewById(R.id.tv_firstInformation);
        relLayout_edit_about_group = findViewById(R.id.relLayout_edit_about_group);
        linLayout_admin_notes = findViewById(R.id.linLayout_admin_notes);
        tv_add_comment = findViewById(R.id.tv_add_comment);
        tv_non_respected_rules = findViewById(R.id.tv_non_respected_rules);
        edit_about_group = findViewById(R.id.edit_about_group);
        TextView nber_char_sequence = findViewById(R.id.nber_char_sequence);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        edit_about_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_sequence.setText(String.valueOf(char_length));

                GradientDrawable drawable = (GradientDrawable) relLayout_edit_about_group.getBackground();
                drawable.mutate();
                drawable.setStroke(1, context.getColor(R.color.blue_grey_100));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spinnerCustom_number_posts = findViewById(R.id.spinnerCustom_number_posts);
        spinnerCustom_limit_posts_expiration = findViewById(R.id.spinnerCustom_limit_posts_expiration);
        spinnerCustom_number_comments = findViewById(R.id.spinnerCustom_number_comments);
        spinnerCustom_limit_comments_expiration = findViewById(R.id.spinnerCustom_limit_comments_expiration);

        switchCompat_number_posts = findViewById(R.id.switchCompat_number_posts);
        switchCompat_number_comments = findViewById(R.id.switchCompat_number_comments);

        // posts
        spinnerCustom_number_posts.setEnabled(false);
        spinnerCustom_number_posts.setClickable(false);
        GradientDrawable drawable_post_number = (GradientDrawable) spinnerCustom_number_posts.getBackground();
        drawable_post_number.mutate();
        drawable_post_number.setColor(context.getColor(R.color.grey_200));

        spinnerCustom_limit_posts_expiration.setEnabled(false);
        spinnerCustom_limit_posts_expiration.setClickable(false);
        GradientDrawable drawable_expiration_post_number = (GradientDrawable) spinnerCustom_limit_posts_expiration.getBackground();
        drawable_expiration_post_number.mutate();
        drawable_expiration_post_number.setColor(context.getColor(R.color.grey_200));

        // comments
        spinnerCustom_number_comments.setEnabled(false);
        spinnerCustom_number_comments.setClickable(false);
        GradientDrawable drawable_comment_number = (GradientDrawable) spinnerCustom_number_comments.getBackground();
        drawable_comment_number.mutate();
        drawable_comment_number.setColor(context.getColor(R.color.grey_200));

        spinnerCustom_limit_comments_expiration.setEnabled(false);
        spinnerCustom_limit_comments_expiration.setClickable(false);
        GradientDrawable drawable_expiration_comment_number = (GradientDrawable) spinnerCustom_limit_comments_expiration.getBackground();
        drawable_expiration_comment_number.mutate();
        drawable_expiration_comment_number.setColor(context.getColor(R.color.grey_200));

        // limit posts __________________________________________________________
        CustomSpinnerAdapter customSpinnerNberPostsAdapter = new CustomSpinnerAdapter(context, spinnerPostsNumberList);
        spinnerCustom_number_posts.setAdapter(customSpinnerNberPostsAdapter);
        spinnerCustom_number_posts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posts_per_day = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerCustom_number_posts.setSelection(1);

        switchCompat_number_posts.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                postsActivityLimited = true;
                spinnerCustom_number_posts.setEnabled(true);
                spinnerCustom_number_posts.setClickable(true);
                drawable_post_number.mutate();
                drawable_post_number.setColor(context.getColor(R.color.white));

                spinnerCustom_limit_posts_expiration.setEnabled(true);
                spinnerCustom_limit_posts_expiration.setClickable(true);
                drawable_expiration_post_number.mutate();
                drawable_expiration_post_number.setColor(context.getColor(R.color.white));

                if (!switchCompat_number_comments.isChecked()) {
                    linLayout_admin_notes.setVisibility(View.VISIBLE);
                    button_save.setEnabled(true);

                    // change cadre the stroke color
                    GradientDrawable drawable = (GradientDrawable) relLayout_edit_about_group.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.blue_grey_100));
                }

            } else {
                postsActivityLimited = false;
                spinnerCustom_number_posts.setSelection(1);
                spinnerCustom_limit_posts_expiration.setSelection(0);

                spinnerCustom_number_posts.setEnabled(false);
                spinnerCustom_number_posts.setClickable(false);
                drawable_post_number.mutate();
                drawable_post_number.setColor(context.getColor(R.color.grey_200));

                spinnerCustom_limit_posts_expiration.setEnabled(false);
                spinnerCustom_limit_posts_expiration.setClickable(false);
                drawable_expiration_post_number.mutate();
                drawable_expiration_post_number.setColor(context.getColor(R.color.grey_200));

                if (!switchCompat_number_comments.isChecked()) {
                    linLayout_admin_notes.setVisibility(View.GONE);
                    button_save.setEnabled(false);

                    // change cadre the stroke color
                    GradientDrawable drawable = (GradientDrawable) relLayout_edit_about_group.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.blue_grey_100));
                }
            }
        });

        CustomSpinnerAdapter customSpinnerExpirationPostsLimitAdapter = new CustomSpinnerAdapter(context, spinnerPostsExpirationList);
        spinnerCustom_limit_posts_expiration.setAdapter(customSpinnerExpirationPostsLimitAdapter);
        spinnerCustom_limit_posts_expiration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posts_per_day_expiration = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // limit comments_________________________________________________________________________
        CustomSpinnerAdapter customSpinnerCommentAdapter = new CustomSpinnerAdapter(context, spinnerCommentsNumberList);
        spinnerCustom_number_comments.setAdapter(customSpinnerCommentAdapter);
        spinnerCustom_number_comments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                comments_per_day = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switchCompat_number_comments.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                commentsActivityLimited = true;
                spinnerCustom_number_comments.setEnabled(true);
                spinnerCustom_number_comments.setClickable(true);
                drawable_comment_number.mutate();
                drawable_comment_number.setColor(context.getColor(R.color.white));

                spinnerCustom_limit_comments_expiration.setEnabled(true);
                spinnerCustom_limit_comments_expiration.setClickable(true);
                drawable_expiration_comment_number.mutate();
                drawable_expiration_comment_number.setColor(context.getColor(R.color.white));

                if (!switchCompat_number_posts.isChecked()) {
                    linLayout_admin_notes.setVisibility(View.VISIBLE);
                    button_save.setEnabled(true);
                }

            } else {
                commentsActivityLimited = false;
                spinnerCustom_number_comments.setSelection(0);
                spinnerCustom_limit_comments_expiration.setSelection(0);

                spinnerCustom_number_comments.setEnabled(false);
                spinnerCustom_number_comments.setClickable(false);
                drawable_comment_number.mutate();
                drawable_comment_number.setColor(context.getColor(R.color.grey_200));

                spinnerCustom_limit_comments_expiration.setEnabled(false);
                spinnerCustom_limit_comments_expiration.setClickable(false);
                drawable_expiration_comment_number.mutate();
                drawable_expiration_comment_number.setColor(context.getColor(R.color.grey_200));

                if (!switchCompat_number_posts.isChecked()) {
                    linLayout_admin_notes.setVisibility(View.GONE);
                    button_save.setEnabled(false);
                }
            }
        });

        CustomSpinnerAdapter customSpinnerExpirationcommentsLimitAdapter = new CustomSpinnerAdapter(context, spinnerCommentsExpirationList);
        spinnerCustom_limit_comments_expiration.setAdapter(customSpinnerExpirationcommentsLimitAdapter);
        spinnerCustom_limit_comments_expiration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                comments_per_day_expiration = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // get user data
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

                    toolBar_textview.setText(Html.fromHtml(context.getString(R.string.limit_activity_of) + " <b>" + user.getUsername() + "</b>"));
                    tv_firstInformation.setText(Html.fromHtml(context.getString(R.string.this_settings_allow_you)
                            + " " + user.getUsername() + " "+context.getString(R.string.member_can_post_or_coment)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_save.setOnClickListener(view -> {
            saveLimitActivity();
        });

        button_cancel.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
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

    // get group rules
    private void getGroupRules() {
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
                }

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

                if (adapter.getItemCount() == 0) {
                    tv_add_comment.setVisibility(View.GONE);
                    tv_non_respected_rules.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveLimitActivity() {
        // when rules exits
        if (adapter.getItemCount() != 0 && currentSelectedRules.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.the_non_respected_rules), Toast.LENGTH_LONG).show();
            return;
        }

        String adminComment = Objects.requireNonNull(edit_about_group.getText()).toString();

        if (TextUtils.isEmpty(adminComment)) {
            GradientDrawable drawable = (GradientDrawable) relLayout_edit_about_group.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_about_group.requestFocus();
            return;
        }

        button_save.setEnabled(false);

        // posts
        if (spinnerCustom_limit_posts_expiration.getSelectedItemPosition() == 0)
            posts_per_day_expiration = "12";
        if (spinnerCustom_limit_posts_expiration.getSelectedItemPosition() == 1)
            posts_per_day_expiration = "24";
        if (spinnerCustom_limit_posts_expiration.getSelectedItemPosition() == 2)
            posts_per_day_expiration = "3";
        if (spinnerCustom_limit_posts_expiration.getSelectedItemPosition() == 3)
            posts_per_day_expiration = "7";
        if (spinnerCustom_limit_posts_expiration.getSelectedItemPosition() == 4)
            posts_per_day_expiration = "14";
        if (spinnerCustom_limit_posts_expiration.getSelectedItemPosition() == 5)
            posts_per_day_expiration = "28";
        // comments
        if (spinnerCustom_limit_comments_expiration.getSelectedItemPosition() == 0)
            comments_per_day_expiration = "12";
        if (spinnerCustom_limit_comments_expiration.getSelectedItemPosition() == 1)
            comments_per_day_expiration = "24";
        if (spinnerCustom_limit_comments_expiration.getSelectedItemPosition() == 2)
            comments_per_day_expiration = "3";
        if (spinnerCustom_limit_comments_expiration.getSelectedItemPosition() == 3)
            comments_per_day_expiration = "7";
        if (spinnerCustom_limit_comments_expiration.getSelectedItemPosition() == 4)
            comments_per_day_expiration = "14";
        if (spinnerCustom_limit_comments_expiration.getSelectedItemPosition() == 5)
            comments_per_day_expiration = "28";

        if (!switchCompat_number_posts.isChecked()) {
            posts_per_day = "";
            posts_per_day_expiration = "";
        }
        if (!switchCompat_number_comments.isChecked()) {
            comments_per_day = "";
            comments_per_day_expiration = "";
        }

        Date date = new Date();
        HashMap<String, Object> map = new HashMap<>();
        map.put("activityLimitation", true);
        map.put("postsActivityLimited", postsActivityLimited);
        map.put("commentsActivityLimited", commentsActivityLimited);
        map.put("number_posts_per_day", posts_per_day);
        map.put("number_posts_per_day_expiration", posts_per_day_expiration);
        map.put("number_comments_per_day", comments_per_day);
        map.put("number_comments_per_day_expiration", comments_per_day_expiration);
        map.put("sanction_admin_comment", adminComment);
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
                false, false, false, false, false,
                false,false, false, false,
                true, postsActivityLimited, commentsActivityLimited, postsActivityLimited, commentsActivityLimited, false,
                false,
                false, false, false, false, false,
                false, false,
                false, false, false, false, false,
                false, false,
                false, "", false, false, false, false,
                true, false, false,
                the_user_id, new_key, "", user_groups.getAdmin_master(), "", user_groups.getGroup_id(), date1.getTime(),
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
                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                });

    }

    // custom Spinner adapter
    public static class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final AppCompatActivity context;
        private final List<String> list;

        public CustomSpinnerAdapter(AppCompatActivity context, List<String> list) {
            this.context = context;
            this.list = list;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(list.get(position));
            txt.setTextColor(context.getColor(R.color.caption_color));
            return txt;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
            txt.setText(list.get(i));
            txt.setTextColor(context.getColor(R.color.caption_color));
            return txt;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}