package com.bekambimouen.miiokychallenge.groups.utils;

import java.util.Date;
import java.util.HashMap;

public class Utils_Map_GroupMembershipModel {
    public static HashMap<Object, Object> setGroupMembershipModel(String user_id, String admin_master, String group_id) {
        Date date = new Date();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("admin_master", admin_master);
        map.put("group_id", group_id);
        map.put("date_created", date.getTime());

        return map;
    }
}

