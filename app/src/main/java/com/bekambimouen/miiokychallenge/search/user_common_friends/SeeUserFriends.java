package com.bekambimouen.miiokychallenge.search.user_common_friends;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.user_common_friends.fragments.FriendViewPagerAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SeeUserFriends extends AppCompatActivity {
    private static final String TAG = "SeeUserFriends";

    // widgets
    private TextView username;
    private TextView tv_all_friends, tv_common_friends, tv_friend_suggestions;
    private LinearLayout parent;
    private RelativeLayout relLayout_all_friends, relLayout_common_friends, relLayout_friend_suggestions;
    private HorizontalScrollView scrollView;
    private ViewPager2 viewPager;

    // vars
    private SeeUserFriends context;
    private String userID;
    private boolean isAllFriends = true, isCommonFriends = false, isSuggestion = false;

    // firebase
    private DatabaseReference myRef;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_see_user_friends);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();

        try {
            userID = getIntent().getStringExtra("userID");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        configureViewPager();
    }

    public String getUserID() {
        return userID;
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
        scrollView = findViewById(R.id.scrollView);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_all_friends = findViewById(R.id.relLayout_all_friends);
        relLayout_common_friends = findViewById(R.id.relLayout_common_friends);
        relLayout_friend_suggestions = findViewById(R.id.relLayout_friend_suggestions);
        tv_all_friends = findViewById(R.id.tv_all_friends);
        tv_common_friends = findViewById(R.id.tv_common_friends);
        tv_friend_suggestions = findViewById(R.id.tv_friend_suggestions);
        username = findViewById(R.id.username);

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    username.setText(user.getUsername());
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

    private void configureViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new FriendViewPagerAdapter(this));

        // disable viewpager swiping
        viewPager.setUserInputEnabled(false);

        relLayout_all_friends.setOnClickListener(view -> {
            closeKeyboard();
            if (!isAllFriends) {
                scrollView.smoothScrollTo(relLayout_all_friends.getLeft(), 0);

                viewPager.setCurrentItem(0, true);

                isAllFriends = true;
                tv_all_friends.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_all_friends.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview_blue));

                isCommonFriends = false;
                tv_common_friends.setTextColor(context.getColor(R.color.black));
                relLayout_common_friends.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSuggestion = false;
                tv_friend_suggestions.setTextColor(context.getColor(R.color.black));
                relLayout_friend_suggestions.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });

        relLayout_common_friends.setOnClickListener(view -> {
            closeKeyboard();
            if (!isCommonFriends) {
                scrollView.smoothScrollTo(relLayout_common_friends.getLeft(), 0);

                viewPager.setCurrentItem(1, true);

                isAllFriends = false;
                tv_all_friends.setTextColor(context.getColor(R.color.black));
                relLayout_all_friends.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isCommonFriends = true;
                tv_common_friends.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_common_friends.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview_blue));

                isSuggestion = false;
                tv_friend_suggestions.setTextColor(context.getColor(R.color.black));
                relLayout_friend_suggestions.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });

        relLayout_friend_suggestions.setOnClickListener(view -> {
            closeKeyboard();
            if (!isSuggestion) {
                scrollView.smoothScrollTo(relLayout_friend_suggestions.getLeft(), 0);

                viewPager.setCurrentItem(2, true);

                isAllFriends = false;
                tv_all_friends.setTextColor(context.getColor(R.color.black));
                relLayout_all_friends.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isCommonFriends = false;
                tv_common_friends.setTextColor(context.getColor(R.color.black));
                relLayout_common_friends.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSuggestion = true;
                tv_friend_suggestions.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_friend_suggestions.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview_blue));
            }
        });
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
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}