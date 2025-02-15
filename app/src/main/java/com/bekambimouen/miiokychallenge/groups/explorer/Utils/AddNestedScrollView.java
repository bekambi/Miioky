package com.bekambimouen.miiokychallenge.groups.explorer.Utils;

import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.BattleModel;

public class AddNestedScrollView {
    public static UserGroups getUserGroups(boolean header) {

        return new UserGroups(
                header, false, "", "", "", "",
                "", "", "", "", "", false,
                false, 0, "", "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "", "",
                "", 0, "", "", "", "", false);
    }
}

