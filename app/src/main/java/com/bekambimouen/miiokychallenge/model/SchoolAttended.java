package com.bekambimouen.miiokychallenge.model;

public class SchoolAttended {
    private String user_school_attended;
    private String user_school_attended_header;
    private String user_id;
    private String schoolKey;
    private long date_created;

    public SchoolAttended() {
    }

    public SchoolAttended(String user_school_attended_header, String user_school_attended, String user_id, String schoolKey, long date_created) {
        this.user_school_attended_header = user_school_attended_header;
        this.user_school_attended = user_school_attended;
        this.user_id = user_id;
        this.schoolKey = schoolKey;
        this.date_created = date_created;
    }

    public String getUser_school_attended_header() {
        return user_school_attended_header;
    }

    public void setUser_school_attended_header(String user_school_attended_header) {
        this.user_school_attended_header = user_school_attended_header;
    }

    public String getUser_school_attended() {
        return user_school_attended;
    }

    public void setUser_school_attended(String user_school_attended) {
        this.user_school_attended = user_school_attended;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSchoolKey() {
        return schoolKey;
    }

    public void setSchoolKey(String schoolKey) {
        this.schoolKey = schoolKey;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}
