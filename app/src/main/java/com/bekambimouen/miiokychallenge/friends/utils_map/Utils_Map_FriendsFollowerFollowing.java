package com.bekambimouen.miiokychallenge.friends.utils_map;

import java.util.Date;
import java.util.HashMap;

public class Utils_Map_FriendsFollowerFollowing {
    public static HashMap<Object, Object> setFriendsFollowerFollowingModel(String user_id) {
        Date date = new Date();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("unSubscribe", false);
        map.put("date_created", date.getTime());

        return map;
    }
}
