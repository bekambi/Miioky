package com.bekambimouen.miiokychallenge.search.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.search.fragments.BattleGridProfileFragment;
import com.bekambimouen.miiokychallenge.search.fragments.FunUserGridProfileFragment;

public class ViewProfileFragmentAdapter extends FragmentStateAdapter {

    public ViewProfileFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return BattleGridProfileFragment.newInstance();
            case 1:
                return FunUserGridProfileFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

