package com.bekambimouen.miiokychallenge.groups.invite_user_in_group.fragments.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.fragments.InvitationHomeTownFragment;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.fragments.InvitationFromSameTownFragment;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.fragments.InvitationSuggestionsFragment;

public class InvitationViewPagerAdapter extends FragmentStateAdapter {

    public InvitationViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return InvitationSuggestionsFragment.newInstance();
            case 1:
                return InvitationFromSameTownFragment.newInstance();
            case 2:
                return InvitationHomeTownFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

