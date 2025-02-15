package com.bekambimouen.miiokychallenge.groups.model;

public class GroupMembershipModel {
    private String user_id;
    private String admin_master;
    private String group_id;
    private long date_created;

    public GroupMembershipModel() {
    }

    public GroupMembershipModel(String user_id, String admin_master, String group_id, long date_created) {
        this.user_id = user_id;
        this.admin_master = admin_master;
        this.group_id = group_id;
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAdmin_master() {
        return admin_master;
    }

    public void setAdmin_master(String admin_master) {
        this.admin_master = admin_master;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}

