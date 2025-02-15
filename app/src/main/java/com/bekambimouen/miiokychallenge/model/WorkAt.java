package com.bekambimouen.miiokychallenge.model;

public class WorkAt {
    private String user_work_at;
    private String user_work_at_header;
    private String user_id;
    private String workAtKey;
    private long date_created;

    public WorkAt() {
    }

    public WorkAt(String user_work_at_header, String user_work_at, String user_id, String workAtKey, long date_created) {
        this.user_work_at_header = user_work_at_header;
        this.user_work_at = user_work_at;
        this.user_id = user_id;
        this.workAtKey = workAtKey;
        this.date_created = date_created;
    }

    public String getUser_work_at_header() {
        return user_work_at_header;
    }

    public void setUser_work_at_header(String user_work_at_header) {
        this.user_work_at_header = user_work_at_header;
    }

    public String getUser_work_at() {
        return user_work_at;
    }

    public void setUser_work_at(String user_work_at) {
        this.user_work_at = user_work_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWorkAtKey() {
        return workAtKey;
    }

    public void setWorkAtKey(String workAtKey) {
        this.workAtKey = workAtKey;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}
