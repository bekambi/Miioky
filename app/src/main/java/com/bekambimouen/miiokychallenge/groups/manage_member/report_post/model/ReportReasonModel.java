package com.bekambimouen.miiokychallenge.groups.manage_member.report_post.model;

public class ReportReasonModel {
    private String reason_of_report;
    private String number_of_report;
    private String photo_id;

    public ReportReasonModel() {
    }

    public ReportReasonModel(String reason_of_report, String number_of_report, String photo_id) {
        this.reason_of_report = reason_of_report;
        this.number_of_report = number_of_report;
        this.photo_id = photo_id;
    }

    public String getReason_of_report() {
        return reason_of_report;
    }

    public void setReason_of_report(String reason_of_report) {
        this.reason_of_report = reason_of_report;
    }

    public String getNumber_of_report() {
        return number_of_report;
    }

    public void setNumber_of_report(String number_of_report) {
        this.number_of_report = number_of_report;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }
}

