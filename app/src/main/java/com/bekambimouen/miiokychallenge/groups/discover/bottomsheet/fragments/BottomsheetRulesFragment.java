package com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.adapter.VerifyRulesAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomsheetRulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomsheetRulesFragment extends Fragment {
    // widgets
    private LinearLayout parent;

    // vars
    private MainActivity context;
    private UserGroups user_groups;
    private List<Rule> rules_list;

    public BottomsheetRulesFragment() {
        // Required empty public constructor
    }

    public static BottomsheetRulesFragment newInstance(UserGroups userGroups) {
        BottomsheetRulesFragment fragment = new BottomsheetRulesFragment();
        Gson gson = new Gson();
        String myGson = gson.toJson(userGroups);

        Bundle args = new Bundle();
        args.putString("user_group", myGson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getArguments().getString("user_group"), UserGroups.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottomsheet_rules, container, false);
        context = (MainActivity) getActivity();

        rules_list = new ArrayList<>();

        init(view);

        return view;
    }

    private void init(View v) {
        parent = v.findViewById(R.id.parent);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
    }

    @Override
    public void onResume() {
        super.onResume();
        // to make fragment wrap view
        parent.requestLayout();
    }
}