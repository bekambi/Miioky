package com.bekambimouen.miiokychallenge.display_insta;

import com.bekambimouen.miiokychallenge.display_insta.adapter.AddNestedScrollView;
import com.bekambimouen.miiokychallenge.model.BattleModel;

import java.util.ArrayList;

public class Utils_list_with_nested_recyclerview {

    public static ArrayList<BattleModel> getMainList(ArrayList<BattleModel> final_list) {

        // add the header
        final_list.add(0, AddNestedScrollView.getBattleModel(true,
                false, false, false, false,
                false, false, false, false,
                false, false, false, false,
                false, false, false));

        // _________________________________________________________________________________________
        if (final_list.size() >= 4 && final_list.size() < 9) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 9 && final_list.size() < 11) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

        } else if (final_list.size() >= 11 && final_list.size() < 13) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 13 && final_list.size() < 17) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

        } else if (final_list.size() >= 17 && final_list.size() < 19) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 19 && final_list.size() < 25) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 25 && final_list.size() < 27) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

        } else if (final_list.size() >= 27 && final_list.size() < 31) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

        } else if (final_list.size() >= 31 && final_list.size() < 33) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 33 && final_list.size() < 36) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(35, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true));

        } else if (final_list.size() >= 36 && final_list.size() < 38) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(35, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true));

            final_list.add(36, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 38 && final_list.size() < 42) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(35, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true));

            final_list.add(36, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false));

            final_list.add(41,AddNestedScrollView.getBattleModel(false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 42 && final_list.size() < 48) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));


            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(35, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true));

            final_list.add(36, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false));

            final_list.add(41,AddNestedScrollView.getBattleModel(false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(47, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 48 && final_list.size() < 59) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(35, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true));

            final_list.add(36, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false));

            final_list.add(41,AddNestedScrollView.getBattleModel(false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(47, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false, false,
                    false, false, false));

            final_list.add(53, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false));

        } else if (final_list.size() >= 59) {
            final_list.add(4, AddNestedScrollView.getBattleModel(false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(10, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false));

            final_list.add(11, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(16, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false));

            final_list.add(18, AddNestedScrollView.getBattleModel(false,
                    false, true, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(24, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(26, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false));

            final_list.add(30, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false));

            final_list.add(31, AddNestedScrollView.getBattleModel(false,
                    false, false, true, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(35, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, true));

            final_list.add(36, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false));

            final_list.add(41,AddNestedScrollView.getBattleModel(false,
                    false, false, false, true,
                    false, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(47, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    true, false, false, false,
                    false, false, false));

            final_list.add(53, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    true, false, false, false,
                    false, false, false, false,
                    false, false, false));

            final_list.add(59, AddNestedScrollView.getBattleModel(false,
                    false, false, false, false,
                    false, false, false, false,
                    false, true, false, false,
                    false, false, false));
        }

        return final_list;
    }
}

