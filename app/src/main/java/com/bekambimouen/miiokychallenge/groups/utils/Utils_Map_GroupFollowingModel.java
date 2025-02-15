package com.bekambimouen.miiokychallenge.groups.utils;

import java.util.Date;
import java.util.HashMap;

public class Utils_Map_GroupFollowingModel {

    // get the following model data
    public static HashMap<Object, Object> groupFollowingModel(String admin_master, String user_id, String adding_by, String group_id) {
        HashMap<Object, Object> map = new HashMap<>();
        Date date = new Date();

        map.put("suspended", false);
        map.put("publicationApprobation", false);
        map.put("activityLimitation", false);
        map.put("postsActivityLimited", false);
        map.put("commentsActivityLimited", false);
        map.put("banFromGroup", false);
        map.put("adminInvitation", false);
        map.put("moderatorAdding", false);

        map.put("adminInGroup", false);
        map.put("moderator", false);
        map.put("memberBlocked", false);
        map.put("group_paused", false);
        map.put("putGroupOnPauseForMember", false);

        map.put("admin_master", admin_master);
        map.put("user_id", user_id);
        map.put("adding_by", adding_by);
        map.put("group_id", group_id);
        map.put("member_bio", "");
        map.put("suspension_duration", "");
        map.put("number_posts_per_day", "");
        map.put("number_posts_per_day_expiration", "");
        map.put("number_comments_per_day", "");
        map.put("number_comments_per_day_expiration", "");
        map.put("sanction_admin_comment", "");
        map.put("number_of_posts_today", 0);
        map.put("number_of_comments_today", 0);
        map.put("date_last_post_or_last_comment", 0);
        map.put("date_admin_applied_sanction_in_group", 0);
        map.put("lastSeenInGroup", 0);
        map.put("date_following", date.getTime());

        // rules
        map.put("rule_title_one", "");
        map.put("rule_detail_one", "");
        map.put("rule_title_two", "");
        map.put("rule_detail_two", "");
        map.put("rule_title_three", "");
        map.put("rule_detail_three", "");
        map.put("rule_title_four", "");
        map.put("rule_detail_four", "");
        map.put("rule_title_five", "");
        map.put("rule_detail_five", "");
        map.put("rule_title_six", "");
        map.put("rule_detail_six", "");
        map.put("rule_title_seven", "");
        map.put("rule_detail_seven", "");
        map.put("rule_title_eight", "");
        map.put("rule_detail_eight", "");
        map.put("rule_title_nine", "");
        map.put("rule_detail_nine", "");
        map.put("rule_title_ten", "");
        map.put("rule_detail_ten", "");
        // member points
        map.put("post_points", "0");
        map.put("share_points", "0");
        map.put("comment_points", "0");
        map.put("like_points", "0");
        // comments

        return map;
    }
}

