package com.bekambimouen.miiokychallenge;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.display_insta.fragments.MainFragment;
import com.bekambimouen.miiokychallenge.display_insta.fragments.ParametersFragment;
import com.bekambimouen.miiokychallenge.display_insta.fragments.InviteAndFollowFragment;
import com.bekambimouen.miiokychallenge.market_place.fragments.MarketExplorerFragment;
import com.bekambimouen.miiokychallenge.display_insta.fragments.ChatFragment;
import com.bekambimouen.miiokychallenge.notification.NotificationFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MainFragment.newInstance();
            case 1:
                return InviteAndFollowFragment.newInstance();
            case 2:
                return ChatFragment.newInstance();
            case 3:
                return MarketExplorerFragment.newInstance();
            case 4:
                return NotificationFragment.newInstance();
            case 5:
                return ParametersFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}

