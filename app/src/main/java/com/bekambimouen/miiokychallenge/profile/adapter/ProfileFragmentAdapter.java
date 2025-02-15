package com.bekambimouen.miiokychallenge.profile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.profile.fragments.BattleGridFragment;
import com.bekambimouen.miiokychallenge.profile.fragments.FunGridCurrentUserProfileFragment;

public class ProfileFragmentAdapter extends FragmentStateAdapter {

    public ProfileFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return BattleGridFragment.newInstance();
            case 1:
                return FunGridCurrentUserProfileFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

