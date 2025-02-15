package com.bekambimouen.miiokychallenge.search.user_common_friends.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FriendViewPagerAdapter extends FragmentStateAdapter {

    public FriendViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AllUserFriendsFragment.newInstance();
            case 1:
                return CommonFriendsFragment.newInstance();
            case 2:
                return FriendsSuggestionsFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

