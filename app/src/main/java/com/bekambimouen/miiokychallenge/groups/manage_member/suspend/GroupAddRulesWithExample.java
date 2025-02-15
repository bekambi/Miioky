package com.bekambimouen.miiokychallenge.groups.manage_member.suspend;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.AddRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.VerifyRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupAddRulesWithExample extends AppCompatActivity {
    private static final String TAG = "GroupAddRulesWithExample";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout linLayout_list_rules, relLayout_rules0, verify, linLayout_verify_rules;
    private LinearLayout parent, linLayout_rules_with_examples, linLayout_write_rules, linLayout_icon_start;
    private ProgressBar progressBar;

    // vars
    private GroupAddRulesWithExample context;
    private UserGroups user_groups;
    private List<Rule> list, list_verify;
    private LinearLayoutManager layoutManager;
    private String title, details;
    private int list_size = 0;
    private boolean isWriterRulesVisible = false;
    private boolean isListOfRulesVisible = false;
    private boolean isVerifyRulesVisible = false;
    private boolean isButtonCreateAnotherRuleClicked = false;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_add_rules_with_example);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        list_verify = new ArrayList<>();

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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        progressBar = findViewById(R.id.progressBar);
        linLayout_rules_with_examples = findViewById(R.id.linLayout_rules_with_examples);
        linLayout_write_rules = findViewById(R.id.linLayout_write_rules);
        linLayout_list_rules = findViewById(R.id.linLayout_list_rules);
        verify = findViewById(R.id.verify);
        relLayout_rules0 = findViewById(R.id.relLayout_rules0);
        linLayout_icon_start = findViewById(R.id.linLayout_icon_start);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        TextView button_start = findViewById(R.id.button_start);
        RelativeLayout relLayout_rules1 = findViewById(R.id.relLayout_rules1);
        RelativeLayout relLayout_rules2 = findViewById(R.id.relLayout_rules2);
        RelativeLayout relLayout_rules3 = findViewById(R.id.relLayout_rules3);
        RelativeLayout relLayout_rules4 = findViewById(R.id.relLayout_rules4);

        TextView title_example1 = findViewById(R.id.title_example1);
        TextView title_example2 = findViewById(R.id.title_example2);
        TextView title_example3 = findViewById(R.id.title_example3);
        TextView title_example4 = findViewById(R.id.title_example4);

        TextView example1 = findViewById(R.id.example1);
        TextView example2 = findViewById(R.id.example2);
        TextView example3 = findViewById(R.id.example3);
        TextView example4 = findViewById(R.id.example4);

        // rules
        RelativeLayout arrowBack_write_rules = findViewById(R.id.arrowBack_write_rules);
        RelativeLayout relLayout_pan = findViewById(R.id.relLayout_pan);
        RelativeLayout close_rules = findViewById(R.id.close_rules);
        RelativeLayout relLayout_next = findViewById(R.id.relLayout_next);
        TextView you_can_add_rule = findViewById(R.id.you_can_add_rule);
        TextView button_create_another_rule = findViewById(R.id.button_create_another_rule);
        MyEditText edit_rule_title = findViewById(R.id.edit_rule_title);
        TextView nber_char_seq_title_rule = findViewById(R.id.nber_char_seq_title_rule);
        MyEditText edit_rule_details = findViewById(R.id.edit_rule_details);
        TextView nber_rule_details = findViewById(R.id.nber_rule_details);

        // rules adapter
        RelativeLayout arrowBack_list_of_rules = findViewById(R.id.arrowBack_list_of_rules);

        // verify rules
        linLayout_verify_rules = findViewById(R.id.linLayout_verify_rules);
        RelativeLayout arrowBack_verify_rules = findViewById(R.id.arrowBack_verify_rules);
        RelativeLayout publish = findViewById(R.id.publish);
        RecyclerView recyclerView_verify = findViewById(R.id.recyclerView_verify);
        recyclerView_verify.setLayoutManager(new LinearLayoutManager(context));

        button_start.setOnClickListener(view -> {
            isWriterRulesVisible = true;
            Objects.requireNonNull(edit_rule_title.getText()).clear();
            Objects.requireNonNull(edit_rule_details.getText()).clear();

            linLayout_icon_start.setVisibility(View.GONE);
            relLayout_rules0.setVisibility(View.VISIBLE);
            linLayout_rules_with_examples.setVisibility(View.GONE);
            linLayout_write_rules.setVisibility(View.VISIBLE);
        });

        relLayout_rules0.setOnClickListener(view -> {
            isWriterRulesVisible = true;
            Objects.requireNonNull(edit_rule_title.getText()).clear();
            Objects.requireNonNull(edit_rule_details.getText()).clear();

            linLayout_icon_start.setVisibility(View.GONE);
            relLayout_rules0.setVisibility(View.VISIBLE);
            linLayout_rules_with_examples.setVisibility(View.GONE);
            linLayout_write_rules.setVisibility(View.VISIBLE);
        });

        relLayout_rules1.setOnClickListener(view -> {
            isWriterRulesVisible = true;
            edit_rule_title.setText(title_example1.getText().toString());
            edit_rule_details.setText(example1.getText().toString());

            linLayout_icon_start.setVisibility(View.GONE);
            relLayout_rules0.setVisibility(View.VISIBLE);
            linLayout_rules_with_examples.setVisibility(View.GONE);
            linLayout_write_rules.setVisibility(View.VISIBLE);
        });

        relLayout_rules2.setOnClickListener(view -> {
            isWriterRulesVisible = true;
            edit_rule_title.setText(title_example2.getText().toString());
            edit_rule_details.setText(example2.getText().toString());

            linLayout_icon_start.setVisibility(View.GONE);
            relLayout_rules0.setVisibility(View.VISIBLE);
            linLayout_rules_with_examples.setVisibility(View.GONE);
            linLayout_write_rules.setVisibility(View.VISIBLE);
        });

        relLayout_rules3.setOnClickListener(view -> {
            isWriterRulesVisible = true;
            edit_rule_title.setText(title_example3.getText().toString());
            edit_rule_details.setText(example3.getText().toString());

            linLayout_icon_start.setVisibility(View.GONE);
            relLayout_rules0.setVisibility(View.VISIBLE);
            linLayout_rules_with_examples.setVisibility(View.GONE);
            linLayout_write_rules.setVisibility(View.VISIBLE);
        });

        relLayout_rules4.setOnClickListener(view -> {
            isWriterRulesVisible = true;
            edit_rule_title.setText(title_example4.getText().toString());
            edit_rule_details.setText(example4.getText().toString());

            linLayout_icon_start.setVisibility(View.GONE);
            relLayout_rules0.setVisibility(View.VISIBLE);
            linLayout_rules_with_examples.setVisibility(View.GONE);
            linLayout_write_rules.setVisibility(View.VISIBLE);
        });

        int title_char_sequence = Objects.requireNonNull(edit_rule_title.getText()).toString().length();
        int detail_char_sequence = Objects.requireNonNull(edit_rule_details.getText()).toString().length();

        nber_char_seq_title_rule.setText(String.valueOf(title_char_sequence));
        nber_rule_details.setText(String.valueOf(detail_char_sequence));

        edit_rule_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_seq_title_rule.setText(String.valueOf(char_length));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_rule_details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_rule_details.setText(String.valueOf(char_length));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        relLayout_next.setOnClickListener(view -> {
            // get editText text
            title = edit_rule_title.getText().toString();
            details = edit_rule_details.getText().toString();

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(details)) {
                isWriterRulesVisible = false;
                isListOfRulesVisible = true;

                linLayout_write_rules.setVisibility(View.GONE);
                linLayout_list_rules.setVisibility(View.VISIBLE);

                // hide keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                list.add(new Rule(title, details));
                AddRulesAdapter adapter = new AddRulesAdapter(context, list, you_can_add_rule,
                        button_create_another_rule, verify);
                recyclerView.setAdapter(adapter);

                list_size = list.size();
                you_can_add_rule.setText(Html.fromHtml(context.getString(R.string.you_can_create)
                        +" "+(10 - (list_size))+" "+context.getString(R.string.more_rules)));

                if (list_size == 10) {
                    button_create_another_rule.setVisibility(View.GONE);
                    you_can_add_rule.setVisibility(View.GONE);

                } else {
                    button_create_another_rule.setVisibility(View.VISIBLE);
                    you_can_add_rule.setVisibility(View.VISIBLE);
                }

                // scroll bottom of list
                RecyclerView.SmoothScroller smoothScroller = new
                        LinearSmoothScroller(context.getApplicationContext()) {
                            @Override protected int getVerticalSnapPreference() {
                                return LinearSmoothScroller.SNAP_TO_START;
                            }
                        };

                smoothScroller.setTargetPosition(list_size-1);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        });

        button_create_another_rule.setOnClickListener(view -> {
            isButtonCreateAnotherRuleClicked = true;
            isListOfRulesVisible = false;
            verify.setEnabled(true);

            linLayout_rules_with_examples.setVisibility(View.VISIBLE);
            linLayout_list_rules.setVisibility(View.GONE);
        });

        // information pan with letter i
        close_rules.setOnClickListener(view -> relLayout_pan.setVisibility(View.GONE));

        arrowBack_write_rules.setOnClickListener(view -> {
            isWriterRulesVisible = false;

            linLayout_rules_with_examples.setVisibility(View.VISIBLE);
            linLayout_write_rules.setVisibility(View.GONE);
        });

        arrowBack_verify_rules.setOnClickListener(view -> {
            isVerifyRulesVisible = false;
            isListOfRulesVisible = true;
            list_verify.clear();

            linLayout_list_rules.setVisibility(View.VISIBLE);
            linLayout_verify_rules.setVisibility(View.GONE);
        });

        arrowBack_list_of_rules.setOnClickListener(view -> {
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView negativeButton = view1.findViewById(R.id.tv_no);
            TextView positiveButton = view1.findViewById(R.id.tv_yes);
            builder.setView(view1);

            Dialog dialog = builder.create();
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 50);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(inset);
            dialog.show();

            negativeButton.setOnClickListener(view2 -> dialog.dismiss());
            positiveButton.setOnClickListener(view3 -> {
                isListOfRulesVisible = false;
                dialog.dismiss();
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            });
        });

        verify.setOnClickListener(view -> {
            isVerifyRulesVisible = true;
            isListOfRulesVisible = false;

            linLayout_list_rules.setVisibility(View.GONE);
            linLayout_verify_rules.setVisibility(View.VISIBLE);

            // verify adapter
            list_verify.addAll(list);
            VerifyRulesAdapter verifyRulesAdapter = new VerifyRulesAdapter(context, list_verify);
            recyclerView_verify.setAdapter(verifyRulesAdapter);
        });

        arrowBack.setOnClickListener(view -> {
            if (isButtonCreateAnotherRuleClicked) {
                isButtonCreateAnotherRuleClicked = false;
                isListOfRulesVisible = true;

                linLayout_list_rules.setVisibility(View.VISIBLE);
                linLayout_rules_with_examples.setVisibility(View.GONE);
                return;
            }

            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();

        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isWriterRulesVisible) {
                    isWriterRulesVisible = false;

                    linLayout_rules_with_examples.setVisibility(View.VISIBLE);
                    linLayout_write_rules.setVisibility(View.GONE);

                } else if (isListOfRulesVisible) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                    TextView negativeButton = view1.findViewById(R.id.tv_no);
                    TextView positiveButton = view1.findViewById(R.id.tv_yes);
                    builder.setView(view1);

                    Dialog dialog = builder.create();
                    ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                    InsetDrawable inset = new InsetDrawable(back, 50);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(inset);
                    dialog.show();

                    negativeButton.setOnClickListener(view2 -> dialog.dismiss());
                    positiveButton.setOnClickListener(view -> {
                        isListOfRulesVisible = false;
                        dialog.dismiss();
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    });

                } else if (isVerifyRulesVisible) {
                    isVerifyRulesVisible = false;
                    isListOfRulesVisible = true;
                    list_verify.clear();

                    linLayout_list_rules.setVisibility(View.VISIBLE);
                    linLayout_verify_rules.setVisibility(View.GONE);

                } else if (isButtonCreateAnotherRuleClicked) {
                    isButtonCreateAnotherRuleClicked = false;
                    isListOfRulesVisible = true;

                    linLayout_list_rules.setVisibility(View.VISIBLE);
                    linLayout_rules_with_examples.setVisibility(View.GONE);

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });

        publish.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            Map<String, Object> map = new HashMap<>();
            map.put("admin_created_rules", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            if (list_verify.size() == 1) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
            }
            if (list_verify.size() == 2) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
            }
            if (list_verify.size() == 3) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
            }
            if (list_verify.size() == 4) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
            }
            if (list_verify.size() == 5) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
                map.put("rule_title_five", list_verify.get(4).getTitle());
                map.put("rule_detail_five", list_verify.get(4).getDetails());
            }
            if (list_verify.size() == 6) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
                map.put("rule_title_five", list_verify.get(4).getTitle());
                map.put("rule_detail_five", list_verify.get(4).getDetails());
                map.put("rule_title_six", list_verify.get(5).getTitle());
                map.put("rule_detail_six", list_verify.get(5).getDetails());
            }
            if (list_verify.size() == 7) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
                map.put("rule_title_five", list_verify.get(4).getTitle());
                map.put("rule_detail_five", list_verify.get(4).getDetails());
                map.put("rule_title_six", list_verify.get(5).getTitle());
                map.put("rule_detail_six", list_verify.get(5).getDetails());
                map.put("rule_title_seven", list_verify.get(6).getTitle());
                map.put("rule_detail_seven", list_verify.get(6).getDetails());
            }
            if (list_verify.size() == 8) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
                map.put("rule_title_five", list_verify.get(4).getTitle());
                map.put("rule_detail_five", list_verify.get(4).getDetails());
                map.put("rule_title_six", list_verify.get(5).getTitle());
                map.put("rule_detail_six", list_verify.get(5).getDetails());
                map.put("rule_title_seven", list_verify.get(6).getTitle());
                map.put("rule_detail_seven", list_verify.get(6).getDetails());
                map.put("rule_title_eight", list_verify.get(7).getTitle());
                map.put("rule_detail_eight", list_verify.get(7).getDetails());
            }
            if (list_verify.size() == 9) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
                map.put("rule_title_five", list_verify.get(4).getTitle());
                map.put("rule_detail_five", list_verify.get(4).getDetails());
                map.put("rule_title_six", list_verify.get(5).getTitle());
                map.put("rule_detail_six", list_verify.get(5).getDetails());
                map.put("rule_title_seven", list_verify.get(6).getTitle());
                map.put("rule_detail_seven", list_verify.get(6).getDetails());
                map.put("rule_title_eight", list_verify.get(7).getTitle());
                map.put("rule_detail_eight", list_verify.get(7).getDetails());
                map.put("rule_title_nine", list_verify.get(8).getTitle());
                map.put("rule_detail_nine", list_verify.get(8).getDetails());
            }
            if (list_verify.size() == 10) {
                map.put("rule_title_one", list_verify.get(0).getTitle());
                map.put("rule_detail_one", list_verify.get(0).getDetails());
                map.put("rule_title_two", list_verify.get(1).getTitle());
                map.put("rule_detail_two", list_verify.get(1).getDetails());
                map.put("rule_title_three", list_verify.get(2).getTitle());
                map.put("rule_detail_three", list_verify.get(2).getDetails());
                map.put("rule_title_four", list_verify.get(3).getTitle());
                map.put("rule_detail_four", list_verify.get(3).getDetails());
                map.put("rule_title_five", list_verify.get(4).getTitle());
                map.put("rule_detail_five", list_verify.get(4).getDetails());
                map.put("rule_title_six", list_verify.get(5).getTitle());
                map.put("rule_detail_six", list_verify.get(5).getDetails());
                map.put("rule_title_seven", list_verify.get(6).getTitle());
                map.put("rule_detail_seven", list_verify.get(6).getDetails());
                map.put("rule_title_eight", list_verify.get(7).getTitle());
                map.put("rule_detail_eight", list_verify.get(7).getDetails());
                map.put("rule_title_nine", list_verify.get(8).getTitle());
                map.put("rule_detail_nine", list_verify.get(8).getDetails());
                map.put("rule_title_ten", list_verify.get(9).getTitle());
                map.put("rule_detail_ten", list_verify.get(9).getDetails());
            }

            myRef.child(context.getString(R.string.dbname_user_group))
                    .child(user_groups.getAdmin_master())
                    .child(user_groups.getGroup_id())
                    .updateChildren(map).addOnSuccessListener(unused -> {
                        progressBar.setVisibility(View.GONE);

                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}