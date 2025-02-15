package com.bekambimouen.miiokychallenge.market_place.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.interfaces.BackPressedListener;
import com.bekambimouen.miiokychallenge.market_place.bottomsheet.BottomSheetMarketSell;
import com.bekambimouen.miiokychallenge.market_place.bottomsheet.BottomSheetSellOrCreateStore;
import com.bekambimouen.miiokychallenge.market_place.fragments.viewpager.MarketExplorerViewPagerAdapter;
import com.bekambimouen.miiokychallenge.market_place.search.MarketSearchStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketExplorerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketExplorerFragment extends Fragment implements BackPressedListener {
    private static final String TAG = "MarketExplorerFragment";

    // creating object of BackPressedListener interface
    public static BackPressedListener backpressedlistener;

    // Permissions
    private final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private ViewPager2 viewPager, mainViewPager;
    private TextView tv_recommendations, tv_your_shop, tv_suggestion;
    private RelativeLayout relLayout_recommendations, relLayout_see_your_shops, relLayout_suggestion,
            relLayout_view_overlay;
    private HorizontalScrollView scrollView;

    // vars
    private MainActivity context;
    private BottomSheetMarketSell bottomSheetMarketSell;

    private boolean isRecommendations = false, isYourShops = false, isSuggestions = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public MarketExplorerFragment() {
        // Required empty public constructor
    }

    public static MarketExplorerFragment newInstance() {
        MarketExplorerFragment fragment = new MarketExplorerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market_explorer, container, false);
        context = (MainActivity) getActivity();

        try {
            assert context != null;
            mainViewPager = context.getViewPager();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: error: "+e.getMessage());
        }

        new Handler().postDelayed(() -> {
            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            init(view);
        }, 300);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init(View v) {
        scrollView = v.findViewById(R.id.scrollView);
        RelativeLayout relLayout_sell = v.findViewById(R.id.relLayout_sell);
        RelativeLayout relLayout_add = v.findViewById(R.id.relLayout_add);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);
        tv_recommendations = v.findViewById(R.id.tv_recommendations);
        tv_your_shop = v.findViewById(R.id.tv_your_shop);
        tv_suggestion = v.findViewById(R.id.tv_suggestion);
        relLayout_recommendations = v.findViewById(R.id.relLayout_recommendations);
        RelativeLayout relLayout_search = v.findViewById(R.id.relLayout_search);
        relLayout_see_your_shops = v.findViewById(R.id.relLayout_see_your_shops);
        relLayout_suggestion = v.findViewById(R.id.relLayout_suggestion);
        // viewPager2
        viewPager = v.findViewById(R.id.viewPager);
        try {
            viewPager.setAdapter(new MarketExplorerViewPagerAdapter(context));
        } catch (IllegalStateException e) {
            Log.d(TAG, "init: error: "+e.getMessage());
        }

        // disable viewpager swiping
        viewPager.setUserInputEnabled(false);

        relLayout_add.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(relLayout_add);

            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

            } else {
                BottomSheetSellOrCreateStore bottomSheet = new BottomSheetSellOrCreateStore(context);
                if (bottomSheet.isAdded())
                    return;
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            }
        });

        relLayout_search.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(relLayout_search);
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            startActivity(new Intent(context, MarketSearchStore.class));
        });

        relLayout_recommendations.setOnClickListener(view -> {
            if (!isRecommendations) {
                scrollView.smoothScrollTo(relLayout_recommendations.getLeft(), 0);

                viewPager.setCurrentItem(0, true);

                isRecommendations = true;
                tv_recommendations.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                isYourShops = false;
                tv_your_shop.setTextColor(context.getColor(R.color.black));
                relLayout_see_your_shops.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSuggestions = false;
                tv_suggestion.setTextColor(context.getColor(R.color.black));
                relLayout_suggestion.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });

        relLayout_see_your_shops.setOnClickListener(view -> {
            if (!isYourShops) {
                scrollView.smoothScrollTo(relLayout_see_your_shops.getLeft(), 0);

                viewPager.setCurrentItem(1, true);

                isRecommendations = false;
                tv_recommendations.setTextColor(context.getColor(R.color.black));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isYourShops = true;
                tv_your_shop.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_see_your_shops.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                isSuggestions = false;
                tv_suggestion.setTextColor(context.getColor(R.color.black));
                relLayout_suggestion.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));
            }
        });

        relLayout_suggestion.setOnClickListener(view -> {
            if (!isSuggestions) {
                scrollView.smoothScrollTo(relLayout_suggestion.getLeft(), 0);

                viewPager.setCurrentItem(2, true);

                isRecommendations = false;
                tv_recommendations.setTextColor(context.getColor(R.color.black));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isYourShops = false;
                tv_your_shop.setTextColor(context.getColor(R.color.black));
                relLayout_see_your_shops.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSuggestions = true;
                tv_suggestion.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_suggestion.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));
            }
        });

        relLayout_sell.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(relLayout_sell);

            scrollView.smoothScrollTo(relLayout_sell.getLeft(), 0);

            if (!allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

            } else {
                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_user_stores))
                        .child(user_id);
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Query query = myRef
                                    .child(getString(R.string.dbname_market))
                                    .child(user_id) // store_id
                                    .orderByChild(context.getString(R.string.field_store_id))
                                    .equalTo(user_id);

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // show dialog box
                                        BottomSheetSellOrCreateStore bottomSheet = new BottomSheetSellOrCreateStore(context);
                                        if (bottomSheet.isAdded())
                                            return;
                                        bottomSheet.show(context.getSupportFragmentManager(), "TAG");

                                    } else {
                                        bottomSheetMarketSell = new BottomSheetMarketSell(context);
                                        bottomSheetMarketSell.show(context.getSupportFragmentManager(), "TAG");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        } else {
                            bottomSheetMarketSell = new BottomSheetMarketSell(context);
                            bottomSheetMarketSell.show(context.getSupportFragmentManager(), "TAG");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            mainViewPager.setCurrentItem(0);

        } else {
            if (!isRecommendations) {
                scrollView.smoothScrollTo(relLayout_recommendations.getLeft(), 0);

                viewPager.setCurrentItem(0, true);

                isRecommendations = true;
                tv_recommendations.setTextColor(context.getColor(R.color.shinning_blue));
                relLayout_recommendations.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_date));

                isYourShops = false;
                tv_your_shop.setTextColor(context.getColor(R.color.black));
                relLayout_see_your_shops.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

                isSuggestions = false;
                tv_suggestion.setTextColor(context.getColor(R.color.black));
                relLayout_suggestion.setBackground(ContextCompat.getDrawable(context, R.drawable.cadre_textview));

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener=this;
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        backpressedlistener=null;
        super.onPause();
    }
}