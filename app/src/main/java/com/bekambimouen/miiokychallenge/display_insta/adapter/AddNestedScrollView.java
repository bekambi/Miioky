package com.bekambimouen.miiokychallenge.display_insta.adapter;

import com.bekambimouen.miiokychallenge.model.BattleModel;

public class AddNestedScrollView {
    public static BattleModel getBattleModel(boolean recycler_story,
                                             boolean friends_suggestion_one, boolean friends_suggestion_two, boolean friends_suggestion_three, boolean friends_suggestion_four, boolean friends_suggestion_five,
                                             boolean groups_suggestion_one, boolean groups_suggestion_two, boolean groups_suggestion_three, boolean groups_suggestion_four, boolean groups_suggestion_five,
                                             boolean videos_suggestion_one, boolean videos_suggestion_two, boolean videos_suggestion_three, boolean videos_suggestion_four, boolean videos_suggestion_five) {

        return new BattleModel(
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false,
                "", "", "", "", "", "", "", false, false, "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "", "", 0, 0, "", "", "", "", "", "", "", "", "", null, null, null, null,
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", false,
                false, false, false, false, false, false, "", false, false, false, false, false, "", "",
                false, "", "", false, "", "", "", "", recycler_story,
                friends_suggestion_one, friends_suggestion_two, friends_suggestion_three, friends_suggestion_four, friends_suggestion_five,
                groups_suggestion_one, groups_suggestion_two, groups_suggestion_three, groups_suggestion_four, groups_suggestion_five,
                videos_suggestion_one, videos_suggestion_two, videos_suggestion_three, videos_suggestion_four, videos_suggestion_five,
                false, "", false, 0);
    }
}

