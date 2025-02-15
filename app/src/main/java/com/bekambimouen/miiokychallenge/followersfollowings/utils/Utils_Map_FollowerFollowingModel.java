package com.bekambimouen.miiokychallenge.followersfollowings.utils;

import java.util.Date;
import java.util.HashMap;

public class Utils_Map_FollowerFollowingModel {
    public static HashMap<Object, Object> setFollowerFollowingModel(String user_id) {
        Date date = new Date();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("to_block", false);
        map.put("date_created", date.getTime());

        return map;
    }
}
