package com.bekambimouen.miiokychallenge.groups.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class GroupFollowersFollowing implements Parcelable {
    private boolean suspended;
    private boolean publicationApprobation;
    private boolean activityLimitation;
    private boolean postsActivityLimited;
    private boolean commentsActivityLimited;
    private boolean banFromGroup;
    private boolean adminInvitation;
    private boolean moderatorAdding;

    private boolean adminInGroup;
    private boolean moderator;
    private boolean memberBlocked;
    private boolean group_paused;

    private String admin_master;
    private String user_id;
    private String adding_by;
    private String group_id;
    private String member_bio;
    private String suspension_duration;
    private String number_posts_per_day;
    private String number_posts_per_day_expiration;
    private String number_comments_per_day;
    private String number_comments_per_day_expiration;
    private String sanction_admin_comment;
    private int number_of_posts_today;
    private int number_of_comments_today;
    private long date_admin_applied_sanction_in_group;
    private long date_last_post_or_last_comment;
    private long lastSeenInGroup;
    private long date_following;

    // member points
    private String post_points;
    private String share_points;
    private String comment_points;
    private String like_points;

    // rules and details
    private String rule_title_one;
    private String rule_detail_one;
    private String rule_title_two;
    private String rule_detail_two;
    private String rule_title_three;
    private String rule_detail_three;
    private String rule_title_four;
    private String rule_detail_four;
    private String rule_title_five;
    private String rule_detail_five;
    private String rule_title_six;
    private String rule_detail_six;
    private String rule_title_seven;
    private String rule_detail_seven;
    private String rule_title_eight;
    private String rule_detail_eight;
    private String rule_title_nine;
    private String rule_detail_nine;
    private String rule_title_ten;
    private String rule_detail_ten;

    public GroupFollowersFollowing() {
    }

    public GroupFollowersFollowing(boolean suspended, boolean publicationApprobation, boolean activityLimitation,
                                   boolean postsActivityLimited, boolean commentsActivityLimited,
                                   boolean banFromGroup, boolean adminInvitation, boolean moderatorAdding,
                                   boolean adminInGroup, boolean moderator, boolean memberBlocked,
                                   boolean group_paused, String admin_master, String user_id, String adding_by,
                                   String group_id, String member_bio, String suspension_duration,
                                   String number_posts_per_day, String number_posts_per_day_expiration,
                                   String number_comments_per_day, String number_comments_per_day_expiration,
                                   String sanction_admin_comment, int number_of_posts_today, int number_of_comments_today,
                                   long date_last_post_or_last_comment, long lastSeenInGroup, long date_admin_applied_sanction_in_group,
                                   long date_following,
                                   String rule_title_one, String rule_detail_one, String rule_title_two,
                                   String rule_detail_two, String rule_title_three, String rule_detail_three,
                                   String rule_title_four, String rule_detail_four, String rule_title_five,
                                   String rule_detail_five, String rule_title_six, String rule_detail_six,
                                   String rule_title_seven, String rule_detail_seven, String rule_title_eight,
                                   String rule_detail_eight, String rule_title_nine, String rule_detail_nine,
                                   String rule_title_ten, String rule_detail_ten, String post_points, String share_points,
                                   String comment_points, String like_points) {
        this.suspended = suspended;
        this.publicationApprobation = publicationApprobation;
        this.activityLimitation = activityLimitation;
        this.postsActivityLimited = postsActivityLimited;
        this.commentsActivityLimited = commentsActivityLimited;
        this.banFromGroup = banFromGroup;
        this.adminInvitation = adminInvitation;
        this.moderatorAdding = moderatorAdding;
        this.adminInGroup = adminInGroup;
        this.moderator = moderator;
        this.memberBlocked = memberBlocked;
        this.group_paused = group_paused;
        this.number_of_posts_today = number_of_posts_today;
        this.number_of_comments_today = number_of_comments_today;
        this.date_last_post_or_last_comment = date_last_post_or_last_comment;
        this.admin_master = admin_master;
        this.adding_by = adding_by;
        this.user_id = user_id;
        this.group_id = group_id;
        this.member_bio = member_bio;
        this.suspension_duration = suspension_duration;
        this.number_posts_per_day = number_posts_per_day;
        this.number_posts_per_day_expiration = number_posts_per_day_expiration;
        this.number_comments_per_day = number_comments_per_day;
        this.number_comments_per_day_expiration = number_comments_per_day_expiration;
        this.sanction_admin_comment = sanction_admin_comment;
        this.lastSeenInGroup = lastSeenInGroup;
        this.date_admin_applied_sanction_in_group = date_admin_applied_sanction_in_group;
        this.date_following = date_following;
        this.rule_title_one = rule_title_one;
        this.rule_detail_one = rule_detail_one;
        this.rule_title_two = rule_title_two;
        this.rule_detail_two = rule_detail_two;
        this.rule_title_three = rule_title_three;
        this.rule_detail_three = rule_detail_three;
        this.rule_title_four = rule_title_four;
        this.rule_detail_four = rule_detail_four;
        this.rule_title_five = rule_title_five;
        this.rule_detail_five = rule_detail_five;
        this.rule_title_six = rule_title_six;
        this.rule_detail_six = rule_detail_six;
        this.rule_title_seven = rule_title_seven;
        this.rule_detail_seven = rule_detail_seven;
        this.rule_title_eight = rule_title_eight;
        this.rule_detail_eight = rule_detail_eight;
        this.rule_title_nine = rule_title_nine;
        this.rule_detail_nine = rule_detail_nine;
        this.rule_title_ten = rule_title_ten;
        this.rule_detail_ten = rule_detail_ten;
        this.post_points = post_points;
        this.share_points = share_points;
        this.comment_points = comment_points;
        this.like_points = like_points;
    }

    protected GroupFollowersFollowing(Parcel in) {
        suspended = in.readByte() != 0;
        publicationApprobation = in.readByte() != 0;
        activityLimitation = in.readByte() != 0;
        postsActivityLimited = in.readByte() != 0;
        commentsActivityLimited = in.readByte() != 0;
        banFromGroup = in.readByte() != 0;
        adminInvitation = in.readByte() != 0;
        moderatorAdding = in.readByte() != 0;
        adminInGroup = in.readByte() != 0;
        moderator = in.readByte() != 0;
        memberBlocked = in.readByte() != 0;
        group_paused = in.readByte() != 0;
        admin_master = in.readString();
        user_id = in.readString();
        adding_by = in.readString();
        group_id = in.readString();
        member_bio = in.readString();
        suspension_duration = in.readString();
        number_posts_per_day = in.readString();
        number_posts_per_day_expiration = in.readString();
        number_comments_per_day = in.readString();
        number_comments_per_day_expiration = in.readString();
        sanction_admin_comment = in.readString();
        number_of_posts_today = in.readInt();
        number_of_comments_today = in.readInt();
        date_admin_applied_sanction_in_group = in.readLong();
        date_last_post_or_last_comment = in.readLong();
        lastSeenInGroup = in.readLong();
        date_following = in.readLong();
        post_points = in.readString();
        share_points = in.readString();
        comment_points = in.readString();
        like_points = in.readString();
        rule_title_one = in.readString();
        rule_detail_one = in.readString();
        rule_title_two = in.readString();
        rule_detail_two = in.readString();
        rule_title_three = in.readString();
        rule_detail_three = in.readString();
        rule_title_four = in.readString();
        rule_detail_four = in.readString();
        rule_title_five = in.readString();
        rule_detail_five = in.readString();
        rule_title_six = in.readString();
        rule_detail_six = in.readString();
        rule_title_seven = in.readString();
        rule_detail_seven = in.readString();
        rule_title_eight = in.readString();
        rule_detail_eight = in.readString();
        rule_title_nine = in.readString();
        rule_detail_nine = in.readString();
        rule_title_ten = in.readString();
        rule_detail_ten = in.readString();
    }

    public static final Creator<GroupFollowersFollowing> CREATOR = new Creator<GroupFollowersFollowing>() {
        @Override
        public GroupFollowersFollowing createFromParcel(Parcel in) {
            return new GroupFollowersFollowing(in);
        }

        @Override
        public GroupFollowersFollowing[] newArray(int size) {
            return new GroupFollowersFollowing[size];
        }
    };

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isPublicationApprobation() {
        return publicationApprobation;
    }

    public void setPublicationApprobation(boolean publicationApprobation) {
        this.publicationApprobation = publicationApprobation;
    }

    public boolean isActivityLimitation() {
        return activityLimitation;
    }

    public void setActivityLimitation(boolean activityLimitation) {
        this.activityLimitation = activityLimitation;
    }

    public boolean isPostsActivityLimited() {
        return postsActivityLimited;
    }

    public void setPostsActivityLimited(boolean postsActivityLimited) {
        this.postsActivityLimited = postsActivityLimited;
    }

    public boolean isCommentsActivityLimited() {
        return commentsActivityLimited;
    }

    public void setCommentsActivityLimited(boolean commentsActivityLimited) {
        this.commentsActivityLimited = commentsActivityLimited;
    }

    public boolean isBanFromGroup() {
        return banFromGroup;
    }

    public void setBanFromGroup(boolean banFromGroup) {
        this.banFromGroup = banFromGroup;
    }

    public boolean isAdminInvitation() {
        return adminInvitation;
    }

    public void setAdminInvitation(boolean adminInvitation) {
        this.adminInvitation = adminInvitation;
    }

    public boolean isModeratorAdding() {
        return moderatorAdding;
    }

    public void setModeratorAdding(boolean moderatorAdding) {
        this.moderatorAdding = moderatorAdding;
    }

    public boolean isAdminInGroup() {
        return adminInGroup;
    }

    public void setAdminInGroup(boolean adminInGroup) {
        this.adminInGroup = adminInGroup;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isMemberBlocked() {
        return memberBlocked;
    }

    public void setMemberBlocked(boolean memberBlocked) {
        this.memberBlocked = memberBlocked;
    }

    public int getNumber_of_posts_today() {
        return number_of_posts_today;
    }

    public void setNumber_of_posts_today(int number_of_posts_today) {
        this.number_of_posts_today = number_of_posts_today;
    }

    public int getNumber_of_comments_today() {
        return number_of_comments_today;
    }

    public void setNumber_of_comments_today(int number_of_comments_today) {
        this.number_of_comments_today = number_of_comments_today;
    }

    public long getDate_last_post_or_last_comment() {
        return date_last_post_or_last_comment;
    }

    public void setDate_last_post_or_last_comment(long date_last_post_or_last_comment) {
        this.date_last_post_or_last_comment = date_last_post_or_last_comment;
    }

    public boolean isGroup_paused() {
        return group_paused;
    }

    public void setGroup_paused(boolean group_paused) {
        this.group_paused = group_paused;
    }

    public String getAdmin_master() {
        return admin_master;
    }

    public void setAdmin_master(String admin_master) {
        this.admin_master = admin_master;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAdding_by() {
        return adding_by;
    }

    public void setAdding_by(String adding_by) {
        this.adding_by = adding_by;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMember_bio() {
        return member_bio;
    }

    public void setMember_bio(String member_bio) {
        this.member_bio = member_bio;
    }

    public long getLastSeenInGroup() {
        return lastSeenInGroup;
    }

    public void setLastSeenInGroup(long lastSeenInGroup) {
        this.lastSeenInGroup = lastSeenInGroup;
    }

    public String getSuspension_duration() {
        return suspension_duration;
    }

    public void setSuspension_duration(String suspension_duration) {
        this.suspension_duration = suspension_duration;
    }

    public String getNumber_posts_per_day() {
        return number_posts_per_day;
    }

    public void setNumber_posts_per_day(String number_posts_per_day) {
        this.number_posts_per_day = number_posts_per_day;
    }

    public String getNumber_posts_per_day_expiration() {
        return number_posts_per_day_expiration;
    }

    public void setNumber_posts_per_day_expiration(String number_posts_per_day_expiration) {
        this.number_posts_per_day_expiration = number_posts_per_day_expiration;
    }

    public String getNumber_comments_per_day() {
        return number_comments_per_day;
    }

    public void setNumber_comments_per_day(String number_comments_per_day) {
        this.number_comments_per_day = number_comments_per_day;
    }

    public String getNumber_comments_per_day_expiration() {
        return number_comments_per_day_expiration;
    }

    public void setNumber_comments_per_day_expiration(String number_comments_per_day_expiration) {
        this.number_comments_per_day_expiration = number_comments_per_day_expiration;
    }

    public String getSanction_admin_comment() {
        return sanction_admin_comment;
    }

    public void setSanction_admin_comment(String sanction_admin_comment) {
        this.sanction_admin_comment = sanction_admin_comment;
    }

    public long getDate_admin_applied_sanction_in_group() {
        return date_admin_applied_sanction_in_group;
    }

    public void setDate_admin_applied_sanction_in_group(long date_admin_applied_sanction_in_group) {
        this.date_admin_applied_sanction_in_group = date_admin_applied_sanction_in_group;
    }

    public long getDate_following() {
        return date_following;
    }

    public void setDate_following(long date_following) {
        this.date_following = date_following;
    }

    public String getRule_title_one() {
        return rule_title_one;
    }

    public void setRule_title_one(String rule_title_one) {
        this.rule_title_one = rule_title_one;
    }

    public String getRule_detail_one() {
        return rule_detail_one;
    }

    public void setRule_detail_one(String rule_detail_one) {
        this.rule_detail_one = rule_detail_one;
    }

    public String getRule_title_two() {
        return rule_title_two;
    }

    public void setRule_title_two(String rule_title_two) {
        this.rule_title_two = rule_title_two;
    }

    public String getRule_detail_two() {
        return rule_detail_two;
    }

    public void setRule_detail_two(String rule_detail_two) {
        this.rule_detail_two = rule_detail_two;
    }

    public String getRule_title_three() {
        return rule_title_three;
    }

    public void setRule_title_three(String rule_title_three) {
        this.rule_title_three = rule_title_three;
    }

    public String getRule_detail_three() {
        return rule_detail_three;
    }

    public void setRule_detail_three(String rule_detail_three) {
        this.rule_detail_three = rule_detail_three;
    }

    public String getRule_title_four() {
        return rule_title_four;
    }

    public void setRule_title_four(String rule_title_four) {
        this.rule_title_four = rule_title_four;
    }

    public String getRule_detail_four() {
        return rule_detail_four;
    }

    public void setRule_detail_four(String rule_detail_four) {
        this.rule_detail_four = rule_detail_four;
    }

    public String getRule_title_five() {
        return rule_title_five;
    }

    public void setRule_title_five(String rule_title_five) {
        this.rule_title_five = rule_title_five;
    }

    public String getRule_detail_five() {
        return rule_detail_five;
    }

    public void setRule_detail_five(String rule_detail_five) {
        this.rule_detail_five = rule_detail_five;
    }

    public String getRule_title_six() {
        return rule_title_six;
    }

    public void setRule_title_six(String rule_title_six) {
        this.rule_title_six = rule_title_six;
    }

    public String getRule_detail_six() {
        return rule_detail_six;
    }

    public void setRule_detail_six(String rule_detail_six) {
        this.rule_detail_six = rule_detail_six;
    }

    public String getRule_title_seven() {
        return rule_title_seven;
    }

    public void setRule_title_seven(String rule_title_seven) {
        this.rule_title_seven = rule_title_seven;
    }

    public String getRule_detail_seven() {
        return rule_detail_seven;
    }

    public void setRule_detail_seven(String rule_detail_seven) {
        this.rule_detail_seven = rule_detail_seven;
    }

    public String getRule_title_eight() {
        return rule_title_eight;
    }

    public void setRule_title_eight(String rule_title_eight) {
        this.rule_title_eight = rule_title_eight;
    }

    public String getRule_detail_eight() {
        return rule_detail_eight;
    }

    public void setRule_detail_eight(String rule_detail_eight) {
        this.rule_detail_eight = rule_detail_eight;
    }

    public String getRule_title_nine() {
        return rule_title_nine;
    }

    public void setRule_title_nine(String rule_title_nine) {
        this.rule_title_nine = rule_title_nine;
    }

    public String getRule_detail_nine() {
        return rule_detail_nine;
    }

    public void setRule_detail_nine(String rule_detail_nine) {
        this.rule_detail_nine = rule_detail_nine;
    }

    public String getRule_title_ten() {
        return rule_title_ten;
    }

    public void setRule_title_ten(String rule_title_ten) {
        this.rule_title_ten = rule_title_ten;
    }

    public String getRule_detail_ten() {
        return rule_detail_ten;
    }

    public void setRule_detail_ten(String rule_detail_ten) {
        this.rule_detail_ten = rule_detail_ten;
    }

    public String getPost_points() {
        return post_points;
    }

    public void setPost_points(String post_points) {
        this.post_points = post_points;
    }

    public String getShare_points() {
        return share_points;
    }

    public void setShare_points(String share_points) {
        this.share_points = share_points;
    }

    public String getComment_points() {
        return comment_points;
    }

    public void setComment_points(String comment_points) {
        this.comment_points = comment_points;
    }

    public String getLike_points() {
        return like_points;
    }

    public void setLike_points(String like_points) {
        this.like_points = like_points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeByte((byte) (suspended ? 1 : 0));
        parcel.writeByte((byte) (publicationApprobation ? 1 : 0));
        parcel.writeByte((byte) (activityLimitation ? 1 : 0));
        parcel.writeByte((byte) (postsActivityLimited ? 1 : 0));
        parcel.writeByte((byte) (commentsActivityLimited ? 1 : 0));
        parcel.writeByte((byte) (banFromGroup ? 1 : 0));
        parcel.writeByte((byte) (adminInvitation ? 1 : 0));
        parcel.writeByte((byte) (moderatorAdding ? 1 : 0));
        parcel.writeByte((byte) (adminInGroup ? 1 : 0));
        parcel.writeByte((byte) (moderator ? 1 : 0));
        parcel.writeByte((byte) (memberBlocked ? 1 : 0));
        parcel.writeByte((byte) (group_paused ? 1 : 0));
        parcel.writeString(admin_master);
        parcel.writeString(user_id);
        parcel.writeString(adding_by);
        parcel.writeString(group_id);
        parcel.writeString(member_bio);
        parcel.writeString(suspension_duration);
        parcel.writeString(number_posts_per_day);
        parcel.writeString(number_posts_per_day_expiration);
        parcel.writeString(number_comments_per_day);
        parcel.writeString(number_comments_per_day_expiration);
        parcel.writeString(sanction_admin_comment);
        parcel.writeInt(number_of_posts_today);
        parcel.writeInt(number_of_comments_today);
        parcel.writeLong(date_admin_applied_sanction_in_group);
        parcel.writeLong(date_last_post_or_last_comment);
        parcel.writeLong(lastSeenInGroup);
        parcel.writeLong(date_following);
        parcel.writeString(post_points);
        parcel.writeString(share_points);
        parcel.writeString(comment_points);
        parcel.writeString(like_points);
        parcel.writeString(rule_title_one);
        parcel.writeString(rule_detail_one);
        parcel.writeString(rule_title_two);
        parcel.writeString(rule_detail_two);
        parcel.writeString(rule_title_three);
        parcel.writeString(rule_detail_three);
        parcel.writeString(rule_title_four);
        parcel.writeString(rule_detail_four);
        parcel.writeString(rule_title_five);
        parcel.writeString(rule_detail_five);
        parcel.writeString(rule_title_six);
        parcel.writeString(rule_detail_six);
        parcel.writeString(rule_title_seven);
        parcel.writeString(rule_detail_seven);
        parcel.writeString(rule_title_eight);
        parcel.writeString(rule_detail_eight);
        parcel.writeString(rule_title_nine);
        parcel.writeString(rule_detail_nine);
        parcel.writeString(rule_title_ten);
        parcel.writeString(rule_detail_ten);
    }
}

