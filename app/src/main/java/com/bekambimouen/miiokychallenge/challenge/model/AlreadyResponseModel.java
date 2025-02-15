package com.bekambimouen.miiokychallenge.challenge.model;

public class AlreadyResponseModel {
    private String reponse_user_id;
    private String invite_user_id;
    private String invite_photo_id;
    private String response_photo_id;
    private String group_id;
    private long date_created;

    public AlreadyResponseModel() { }

    public AlreadyResponseModel(String reponse_user_id, String invite_user_id, String invite_photo_id,
                                String response_photo_id, String group_id, long date_created) {
        this.reponse_user_id = reponse_user_id;
        this.invite_user_id = invite_user_id;
        this.invite_photo_id = invite_photo_id;
        this.response_photo_id = response_photo_id;
        this.group_id = group_id;
        this.date_created = date_created;
    }

    public String getReponse_user_id() {
        return reponse_user_id;
    }

    public void setReponse_user_id(String reponse_user_id) {
        this.reponse_user_id = reponse_user_id;
    }

    public String getInvite_user_id() {
        return invite_user_id;
    }

    public void setInvite_user_id(String invite_user_id) {
        this.invite_user_id = invite_user_id;
    }

    public String getInvite_photo_id() {
        return invite_photo_id;
    }

    public void setInvite_photo_id(String invite_photo_id) {
        this.invite_photo_id = invite_photo_id;
    }

    public String getResponse_photo_id() {
        return response_photo_id;
    }

    public void setResponse_photo_id(String response_photo_id) {
        this.response_photo_id = response_photo_id;
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

