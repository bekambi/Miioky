package com.bekambimouen.miiokychallenge.market_place.fragments.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.market_place.fragments.MarketRecommendationsFragment;
import com.bekambimouen.miiokychallenge.market_place.fragments.StoresSuggestionFragment;
import com.bekambimouen.miiokychallenge.market_place.fragments.YourShopFragment;

public class MarketExplorerViewPagerAdapter extends FragmentStateAdapter {

    public MarketExplorerViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MarketRecommendationsFragment.newInstance();
            case 1:
                return YourShopFragment.newInstance();
            case 2:
                return StoresSuggestionFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

