package com.bekambimouen.miiokychallenge.groups.discover.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.fragments.BottomSheetInformationFragment;
import com.bekambimouen.miiokychallenge.groups.discover.bottomsheet.fragments.BottomsheetRulesFragment;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;

public class DiscoverBottomSheetFragmentAdapter extends FragmentStateAdapter {
    private final UserGroups user_group;

    public DiscoverBottomSheetFragmentAdapter(@NonNull FragmentActivity fragmentActivity, UserGroups user_group) {
        super(fragmentActivity);
        this.user_group = user_group;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return BottomSheetInformationFragment.newInstance(user_group);
            case 1:
                return BottomsheetRulesFragment.newInstance(user_group);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

