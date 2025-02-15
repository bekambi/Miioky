package com.bekambimouen.miiokychallenge.groups.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserGroups implements Parcelable {
    // constant
    // header
    private boolean header;

    private boolean group_paused;

    private String group_theme;
    private String group_id;
    private String group_name;
    private String group_message;
    private String admin_master;
    private String group_profile_photo_id;
    private String profile_photo;
    private String full_photo;
    private String cover_photo;
    private String admin_master_bio;

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

    // admin points
    private String post_points;
    private String share_points;
    private String comment_points;
    private String like_points;

    // admin who create rules and admin who modify rules
    private String admin_created_rules;
    private String admin_modify_rules;

    private boolean group_public;
    private boolean group_private;
    private long date_created;
    // admin last seen in group
    private long lastSeenInGroup;

    // to compare user challenge id with current user id
    private String user_id;
    // suppressed the post
    private boolean suppressed;

    public UserGroups() {
    }

    public UserGroups(boolean header, boolean group_paused, String group_theme, String group_id,
                      String group_name, String group_message, String admin_master,
                      String group_profile_photo_id, String profile_photo, String full_photo,
                      String cover_photo, boolean group_public, boolean group_private,
                      long date_created, String admin_master_bio, String user_id,
                      String rule_title_one, String rule_detail_one, String rule_title_two, String rule_detail_two,
                      String rule_title_three, String rule_detail_three, String rule_title_four, String rule_detail_four,
                      String rule_title_five, String rule_detail_five, String rule_title_six, String rule_detail_six,
                      String rule_title_seven, String rule_detail_seven, String rule_title_eight, String rule_detail_eight,
                      String rule_title_nine, String rule_detail_nine, String rule_title_ten, String rule_detail_ten,
                      String admin_created_rules, String admin_modify_rules, long lastSeenInGroup, String post_points,
                      String share_points, String comment_points, String like_points, boolean suppressed) {
        this.header = header;
        this.group_paused = group_paused;
        this.group_theme = group_theme;
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_message = group_message;
        this.admin_master = admin_master;
        this.group_profile_photo_id = group_profile_photo_id;
        this.profile_photo = profile_photo;
        this.full_photo = full_photo;
        this.cover_photo = cover_photo;
        this.group_public = group_public;
        this.group_private = group_private;
        this.date_created = date_created;
        this.admin_master_bio = admin_master_bio;
        this.user_id = user_id;
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
        this.admin_created_rules = admin_created_rules;
        this.admin_modify_rules = admin_modify_rules;
        this.lastSeenInGroup = lastSeenInGroup;
        this.post_points = post_points;
        this.share_points = share_points;
        this.comment_points = comment_points;
        this.like_points = like_points;
        this.suppressed = suppressed;
    }

    protected UserGroups(Parcel in) {
        suppressed = in.readByte() != 0;
        header = in.readByte() != 0;
        group_paused = in.readByte() != 0;
        group_theme = in.readString();
        group_id = in.readString();
        group_name = in.readString();
        group_message = in.readString();
        admin_master = in.readString();
        group_profile_photo_id = in.readString();
        profile_photo = in.readString();
        full_photo = in.readString();
        cover_photo = in.readString();
        group_public = in.readByte() != 0;
        group_private = in.readByte() != 0;
        date_created = in.readLong();
        admin_master_bio = in.readString();
        user_id = in.readString();
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
        admin_created_rules = in.readString();
        admin_modify_rules = in.readString();
        lastSeenInGroup = in.readLong();
        post_points = in.readString();
        share_points = in.readString();
        comment_points = in.readString();
        like_points = in.readString();
    }

    public static final Creator<UserGroups> CREATOR = new Creator<UserGroups>() {
        @Override
        public UserGroups createFromParcel(Parcel in) {
            return new UserGroups(in);
        }

        @Override
        public UserGroups[] newArray(int size) {
            return new UserGroups[size];
        }
    };

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isGroup_paused() {
        return group_paused;
    }

    public void setGroup_paused(boolean group_paused) {
        this.group_paused = group_paused;
    }

    public String getGroup_theme() {
        return group_theme;
    }

    public void setGroup_theme(String group_theme) {
        this.group_theme = group_theme;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_message() {
        return group_message;
    }

    public void setGroup_message(String group_message) {
        this.group_message = group_message;
    }

    public String getAdmin_master() {
        return admin_master;
    }

    public void setAdmin_master(String admin_master) {
        this.admin_master = admin_master;
    }

    public String getGroup_profile_photo_id() {
        return group_profile_photo_id;
    }

    public void setGroup_profile_photo_id(String group_profile_photo_id) {
        this.group_profile_photo_id = group_profile_photo_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getFull_photo() {
        return full_photo;
    }

    public void setFull_photo(String full_photo) {
        this.full_photo = full_photo;
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }

    public boolean isGroup_public() {
        return group_public;
    }

    public void setGroup_public(boolean group_public) {
        this.group_public = group_public;
    }

    public boolean isGroup_private() {
        return group_private;
    }

    public void setGroup_private(boolean group_private) {
        this.group_private = group_private;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public String getAdmin_master_bio() {
        return admin_master_bio;
    }

    public void setAdmin_master_bio(String admin_master_bio) {
        this.admin_master_bio = admin_master_bio;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getAdmin_created_rules() {
        return admin_created_rules;
    }

    public void setAdmin_created_rules(String admin_created_rules) {
        this.admin_created_rules = admin_created_rules;
    }

    public String getAdmin_modify_rules() {
        return admin_modify_rules;
    }

    public void setAdmin_modify_rules(String admin_modify_rules) {
        this.admin_modify_rules = admin_modify_rules;
    }

    public long getLastSeenInGroup() {
        return lastSeenInGroup;
    }

    public void setLastSeenInGroup(long lastSeenInGroup) {
        this.lastSeenInGroup = lastSeenInGroup;
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (suppressed ? 1 : 0));
        parcel.writeByte((byte) (header ? 1 : 0));
        parcel.writeByte((byte) (group_paused ? 1 : 0));
        parcel.writeString(group_theme);
        parcel.writeString(group_id);
        parcel.writeString(group_name);
        parcel.writeString(group_message);
        parcel.writeString(admin_master);
        parcel.writeString(group_profile_photo_id);
        parcel.writeString(profile_photo);
        parcel.writeString(full_photo);
        parcel.writeString(cover_photo);
        parcel.writeByte((byte) (group_public ? 1 : 0));
        parcel.writeByte((byte) (group_private ? 1 : 0));
        parcel.writeLong(date_created);
        parcel.writeString(admin_master_bio);
        parcel.writeString(user_id);
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
        parcel.writeString(admin_created_rules);
        parcel.writeString(admin_modify_rules);
        parcel.writeLong(lastSeenInGroup);
        parcel.writeString(post_points);
        parcel.writeString(share_points);
        parcel.writeString(comment_points);
        parcel.writeString(like_points);
    }
}

