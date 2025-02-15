package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;

import java.util.Map;
import java.util.Objects;

public class Util_GroupFollowersFollowing {

    public static GroupFollowersFollowing getGroupFollowersFollowing(AppCompatActivity context, Map<Object, Object> objectMap) {
        GroupFollowersFollowing follower = new GroupFollowersFollowing();

        follower.setSuspended(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suspended))).toString())));
        follower.setPublicationApprobation(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_publicationApprobation))).toString())));
        follower.setActivityLimitation(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_activityLimitation))).toString())));
        follower.setPostsActivityLimited(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_postsActivityLimited))).toString())));
        follower.setCommentsActivityLimited(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_commentsActivityLimited))).toString())));
        follower.setBanFromGroup(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_banFromGroup))).toString())));
        follower.setAdminInvitation(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_adminInvitation))).toString())));
        follower.setModeratorAdding(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_moderatorAdding))).toString())));
        follower.setAdminInGroup(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_adminInGroup))).toString())));
        follower.setModerator(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_moderator))).toString())));
        follower.setMemberBlocked(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_memberBlocked))).toString())));
        follower.setGroup_paused(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_paused))).toString())));
        follower.setAdmin_master(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString()));
        follower.setUser_id(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString()));
        follower.setAdding_by(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_adding_by))).toString()));
        follower.setGroup_id(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_id))).toString()));
        follower.setMember_bio(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_member_bio))).toString()));
        follower.setSuspension_duration(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suspension_duration))).toString()));
        follower.setNumber_posts_per_day(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_posts_per_day))).toString()));
        follower.setNumber_posts_per_day_expiration(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_posts_per_day_expiration))).toString()));
        follower.setNumber_comments_per_day(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_comments_per_day))).toString()));
        follower.setNumber_comments_per_day_expiration(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_comments_per_day_expiration))).toString()));
        follower.setSanction_admin_comment(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_sanction_admin_comment))).toString()));
        follower.setNumber_of_posts_today(Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_of_posts_today))).toString())));
        follower.setNumber_of_comments_today(Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_number_of_comments_today))).toString())));
        follower.setDate_admin_applied_sanction_in_group(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_admin_applied_sanction_in_group))).toString()));
        follower.setDate_last_post_or_last_comment(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_last_post_or_last_comment))).toString()));
        follower.setLastSeenInGroup(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_lastSeenInGroup))).toString()));
        follower.setDate_following(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_following))).toString()));
        follower.setRule_title_one(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_one))).toString());
        follower.setRule_detail_one(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_one))).toString());
        follower.setRule_title_two(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_two))).toString());
        follower.setRule_detail_two(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_two))).toString());
        follower.setRule_title_three(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_three))).toString());
        follower.setRule_detail_three(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_three))).toString());
        follower.setRule_title_four(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_four))).toString());
        follower.setRule_detail_four(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_four))).toString());
        follower.setRule_title_five(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_five))).toString());
        follower.setRule_detail_five(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_five))).toString());
        follower.setRule_title_six(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_six))).toString());
        follower.setRule_detail_six(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_six))).toString());
        follower.setRule_title_seven(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_seven))).toString());
        follower.setRule_detail_seven(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_seven))).toString());
        follower.setRule_title_eight(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_eight))).toString());
        follower.setRule_detail_eight(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_eight))).toString());
        follower.setRule_title_nine(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_nine))).toString());
        follower.setRule_detail_nine(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_nine))).toString());
        follower.setRule_title_ten(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_title_ten))).toString());
        follower.setRule_detail_ten(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_rule_detail_ten))).toString());
        follower.setPost_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_points))).toString());
        follower.setShare_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_share_points))).toString());
        follower.setComment_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_points))).toString());
        follower.setLike_points(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_like_points))).toString());

        return follower;
    }
}

