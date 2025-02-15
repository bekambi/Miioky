package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;

import java.util.Map;
import java.util.Objects;

public class Util_UserGroups {
    public static UserGroups getUserGroups(AppCompatActivity context, Map<Object, Object> objectMap) {
        UserGroups user_groups = new UserGroups();

        user_groups.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));
        user_groups.setHeader(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_header))).toString()));
        user_groups.setGroup_paused(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_paused))).toString()));
        user_groups.setGroup_theme(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_theme))).toString());
        user_groups.setGroup_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_id))).toString());
        user_groups.setGroup_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_name))).toString());
        user_groups.setGroup_message(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_message))).toString());
        user_groups.setAdmin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString());
        user_groups.setGroup_profile_photo_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_profile_photo_id))).toString());
        user_groups.setProfile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profile_photo))).toString());
        user_groups.setFull_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_full_photo))).toString());
        user_groups.setCover_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_cover_photo))).toString());
        user_groups.setAdmin_master_bio(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master_bio))).toString());

        user_groups.setRule_title_one(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_one))).toString());
        user_groups.setRule_detail_one(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_one))).toString());
        user_groups.setRule_title_two(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_two))).toString());
        user_groups.setRule_detail_two(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_two))).toString());
        user_groups.setRule_title_three(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_three))).toString());
        user_groups.setRule_detail_three(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_three))).toString());
        user_groups.setRule_title_four(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_four))).toString());
        user_groups.setRule_detail_four(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_four))).toString());
        user_groups.setRule_title_five(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_five))).toString());
        user_groups.setRule_detail_five(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_five))).toString());
        user_groups.setRule_title_six(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_six))).toString());
        user_groups.setRule_detail_six(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_six))).toString());
        user_groups.setRule_title_seven(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_seven))).toString());
        user_groups.setRule_detail_seven(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_seven))).toString());
        user_groups.setRule_title_eight(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_eight))).toString());
        user_groups.setRule_detail_eight(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_eight))).toString());
        user_groups.setRule_title_nine(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_nine))).toString());
        user_groups.setRule_detail_nine(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_nine))).toString());
        user_groups.setRule_title_ten(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_ten))).toString());
        user_groups.setRule_detail_ten(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_ten))).toString());

        user_groups.setAdmin_created_rules(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_created_rules))).toString());
        user_groups.setAdmin_modify_rules(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_modify_rules))).toString());

        user_groups.setGroup_public(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_public))).toString()));
        user_groups.setGroup_private(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_private))).toString()));
        user_groups.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        user_groups.setLastSeenInGroup(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_lastSeenInGroup))).toString()));
        user_groups.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        user_groups.setPost_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_points))).toString());
        user_groups.setShare_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_share_points))).toString());
        user_groups.setComment_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_points))).toString());
        user_groups.setLike_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_like_points))).toString());

        return user_groups;
    }
}

