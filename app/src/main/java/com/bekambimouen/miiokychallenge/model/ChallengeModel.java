package com.bekambimouen.miiokychallenge.model;

public class ChallengeModel {
    private String profil_url;
    private String url;
    private String thumbnail;
    private String photo_id;
    private String photo_id_un;
    private String name;
    private String caption;
    private String invite_category;
    private String user_id;
    private User user;
    private long date_created;
    private String country_name;
    private String countryID;

    public ChallengeModel() {
    }

    public ChallengeModel(String profil_url, String url , String thumbnail, String photo_id, String photo_id_un,
                          String name, String caption, String invite_category,
                          String user_id, User user, long date_created, String country_name, String countryID) {
        this.profil_url = profil_url;
        this.url = url;
        this.thumbnail = thumbnail;
        this.photo_id = photo_id;
        this.photo_id_un = photo_id_un;
        this.name = name;
        this.caption = caption;
        this.invite_category = invite_category;
        this.user_id = user_id;
        this.user = user;
        this.date_created = date_created;
        this.country_name = country_name;
        this.countryID = countryID;
    }

    public String getProfil_url() {
        return profil_url;
    }

    public void setProfil_url(String profil_url) {
        this.profil_url = profil_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoto_id_un() {
        return photo_id_un;
    }

    public void setPhoto_id_un(String photo_id_un) {
        this.photo_id_un = photo_id_un;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getInvite_category() {
        return invite_category;
    }

    public void setInvite_category(String invite_category) {
        this.invite_category = invite_category;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }
}

