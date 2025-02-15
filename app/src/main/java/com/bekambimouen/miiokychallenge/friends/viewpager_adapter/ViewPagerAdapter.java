package com.bekambimouen.miiokychallenge.friends.viewpager_adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.display_insta.fragments.FollowApprobationFragment;
import com.bekambimouen.miiokychallenge.display_insta.fragments.FriendConfirmationFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return FriendConfirmationFragment.newInstance();
            case 1:
                return FollowApprobationFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

