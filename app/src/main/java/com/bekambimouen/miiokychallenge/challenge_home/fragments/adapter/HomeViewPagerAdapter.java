package com.bekambimouen.miiokychallenge.challenge_home.fragments.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.challenge_home.fragments.ChallengeInvitationFragment;
import com.bekambimouen.miiokychallenge.challenge_home.fragments.ChallengesFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {

    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ChallengesFragment.newInstance();
            case 1:
                return ChallengeInvitationFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

