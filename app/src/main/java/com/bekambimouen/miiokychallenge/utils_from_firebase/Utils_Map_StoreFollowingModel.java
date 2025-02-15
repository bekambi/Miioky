package com.bekambimouen.miiokychallenge.utils_from_firebase;

import java.util.Date;
import java.util.HashMap;

public class Utils_Map_StoreFollowingModel {

    // get the following model data
    public static HashMap<Object, Object> storeFollowingModel(String store_owner, String user_id, String store_id) {
        HashMap<Object, Object> map = new HashMap<>();
        Date date = new Date();

        map.put("store_owner", store_owner);
        map.put("user_id", user_id);
        map.put("store_id", store_id);
        map.put("date_following", date.getTime());

        return map;
    }
}

