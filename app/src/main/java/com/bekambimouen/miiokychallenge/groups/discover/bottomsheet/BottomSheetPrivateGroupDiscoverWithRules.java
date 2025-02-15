package com.bekambimouen.miiokychallenge.groups.discover.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.adapter.DiscoverBottomSheetFragmentAdapter;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.AcceptPrivateRules;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

public class BottomSheetPrivateGroupDiscoverWithRules extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetGroupDiscover";

    // widgets
    private TextView group_name;
    private TextView private_public;
    private TextView total_members;
    private ImageView profile_photo;
    private RelativeLayout relLayout_go_about_class, relLayout_button_cancel, relLayout_button_join;
    private int t;

    // vars
    private final AppCompatActivity context;
    private final UserGroups user_groups;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public BottomSheetPrivateGroupDiscoverWithRules(AppCompatActivity context, UserGroups userGroups) {
        this.context = context;
        this.user_groups = userGroups;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            setupFullHeight(bottomSheetDialog);
        });

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onStart()
    {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // expand BottomSheetDialog on full screen
    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheet != null;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight - 100;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        behavior.setFitToContents(true);
        behavior.setHideable(true);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_group_bottomsheet_private_discover_with_rules, null);

        View iv_view = view.findViewById(R.id.iv_view);
        profile_photo = view.findViewById(R.id.profile_photo);
        relLayout_go_about_class = view.findViewById(R.id.relLayout_go_about_class);
        total_members = view.findViewById(R.id.total_members);
        group_name = view.findViewById(R.id.group_name);
        private_public = view.findViewById(R.id.private_public);
        relLayout_button_cancel = view.findViewById(R.id.relLayout_button_cancel);
        relLayout_button_join = view.findViewById(R.id.relLayout_button_join);
        ViewMoreTextView group_msg = view.findViewById(R.id.group_msg);

        dialog.setContentView(view);

        iv_view.setOnClickListener(view1 -> dialog.dismiss());

        configureViewPagerAndTabs(view);

        getUserData();

        // get the group message
        if (!TextUtils.isEmpty(user_groups.getGroup_message()))
            group_msg.setVisibility(View.VISIBLE);
        group_msg.setCharText(user_groups.getGroup_message());
    }

    private void configureViewPagerAndTabs(View v) {
        TabLayout tabLayout_profile = v.findViewById(R.id.tabLayout);
        ViewPager2 viewPager_main = v.findViewById(R.id.viewPager);
        viewPager_main.setAdapter(new DiscoverBottomSheetFragmentAdapter(context, user_groups));
        viewPager_main.setFocusable(true);
        viewPager_main.requestFocus();

        String[] titles = {"Informations", context.getString(R.string.rules)};

        new TabLayoutMediator(tabLayout_profile, viewPager_main,
                (tabLayout, position) -> tabLayout.setText(titles[position])).attach();

        // to view battle and fun post number
        tabLayout_profile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getUserData() {
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

                    group_name.setText(Html.fromHtml("<b>" + user_groups.getGroup_name() + "</b> >"));

                    private_public.setText(context.getString(R.string.title_private));

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);
                    }

                    profile_photo.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                        intent.putExtra("user_groups", myGson);
                        context.startActivity(intent);
                    });

                    relLayout_go_about_class.setOnClickListener(view -> {
                        dismiss();
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                        intent.putExtra("user_groups", myGson);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isFollowing(user_groups);

        // top button
        relLayout_button_join.setOnClickListener(view -> {
            dismiss();
            if (!TextUtils.isEmpty(user_groups.getRule_title_one()) && !TextUtils.isEmpty(user_groups.getRule_detail_one())) {
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                Intent intent = new Intent(context, AcceptPrivateRules.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);

            }
        });

        relLayout_button_cancel.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_following))
                    .child(user_id)
                    .child(user_groups.getGroup_id())
                    .removeValue();

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_follower))
                    .child(user_groups.getGroup_id())
                    .child(user_id)
                    .removeValue();
            setUnfollowing();
        });

        t = 0;

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

                if (t == 0)
                    total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.member_minus)));
                else
                    total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_button_join.setVisibility(View.GONE);
        relLayout_button_cancel.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        relLayout_button_join.setVisibility(View.VISIBLE);
        relLayout_button_cancel.setVisibility(View.GONE);
    }

    private void isFollowing(UserGroups userGroups){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(context.getString(R.string.dbname_group_Membership_request_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(userGroups.getGroup_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Prevent BottomSheetDialogFragment covering navigation bar
     */
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}

