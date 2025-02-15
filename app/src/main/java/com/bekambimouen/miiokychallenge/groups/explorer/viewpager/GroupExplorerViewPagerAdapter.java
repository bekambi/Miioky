package com.bekambimouen.miiokychallenge.groups.explorer.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.groups.explorer.fragments.GroupYourGroupsFragment;
import com.bekambimouen.miiokychallenge.groups.explorer.fragments.GroupDiscoverFragment;
import com.bekambimouen.miiokychallenge.groups.explorer.fragments.GroupExplorerFragment;

public class GroupExplorerViewPagerAdapter extends FragmentStateAdapter {

    public GroupExplorerViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return GroupExplorerFragment.newInstance();
            case 1:
                return GroupYourGroupsFragment.newInstance();
            case 2:
                return GroupDiscoverFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

