package com.bekambimouen.miiokychallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable {
    private String user_id;
    private String fullName;
    private String username;
    private String bio;
    private String birthDay;
    private String profileUrl;
    private String full_profileUrl;
    private String photo_id;
    private String phoneNumber;
    private String status;
    private String marital_status;
    private String gender;
    private String native_country_name;
    private String hometown_name;
    private String live_country_name;
    private String town_name;
    private String neighborhood_name;
    private List<SchoolAttended> school;
    private List<FrequentedEstablishment> establishments;
    private List<WorkAt> workAts;
    private String scope;
    private long date_created;
    private long views;
    boolean verified;
    boolean isSelected;

    public User() {
    }

    public User(String user_id, String fullName, String username, String bio, String birthDay, String profileUrl,
                String full_profileUrl, String photo_id, String phoneNumber, String status,
                String town_name, String neighborhood_name,
                String native_country_name, String hometown_name, String marital_status, String gender,
                String live_country_name, List<SchoolAttended> school, List<FrequentedEstablishment> establishments,
                List<WorkAt> workAts, String scope, long date_created, boolean verified, long views) {
        this.user_id = user_id;
        this.fullName = fullName;
        this.username = username;
        this.bio = bio;
        this.birthDay = birthDay;
        this.profileUrl = profileUrl;
        this.full_profileUrl = full_profileUrl;
        this.photo_id = photo_id;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.town_name = town_name;
        this.neighborhood_name = neighborhood_name;
        this.native_country_name = native_country_name;
        this.hometown_name = hometown_name;
        this.marital_status = marital_status;
        this.gender = gender;
        this.live_country_name = live_country_name;
        this.school = school;
        this.establishments = establishments;
        this.workAts = workAts;
        this.scope = scope;
        this.date_created = date_created;
        this.verified = verified;
        this.views = views;
    }

    protected User(Parcel in) {
        user_id = in.readString();
        fullName = in.readString();
        username = in.readString();
        bio = in.readString();
        birthDay = in.readString();
        profileUrl = in.readString();
        full_profileUrl = in.readString();
        photo_id = in.readString();
        phoneNumber = in.readString();
        status = in.readString();
        town_name = in.readString();
        neighborhood_name = in.readString();
        native_country_name = in.readString();
        hometown_name = in.readString();
        marital_status = in.readString();
        gender = in.readString();
        live_country_name = in.readString();
        scope = in.readString();
        date_created = in.readLong();
        verified = in.readByte() != 0;
        views = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFull_profileUrl() {
        return full_profileUrl;
    }

    public void setFull_profileUrl(String full_profileUrl) {
        this.full_profileUrl = full_profileUrl;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public String getNeighborhood_name() {
        return neighborhood_name;
    }

    public void setNeighborhood_name(String neighborhood_name) {
        this.neighborhood_name = neighborhood_name;
    }

    public String getNative_country_name() {
        return native_country_name;
    }

    public void setNative_country_name(String native_country_name) {
        this.native_country_name = native_country_name;
    }

    public String getHometown_name() {
        return hometown_name;
    }

    public void setHometown_name(String hometown_name) {
        this.hometown_name = hometown_name;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLive_country_name() {
        return live_country_name;
    }

    public void setLive_country_name(String live_country_name) {
        this.live_country_name = live_country_name;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<SchoolAttended> getSchool() {
        return school;
    }

    public void setSchool(List<SchoolAttended> school) {
        this.school = school;
    }

    public List<FrequentedEstablishment> getEstablishments() {
        return establishments;
    }

    public void setEstablishments(List<FrequentedEstablishment> establishments) {
        this.establishments = establishments;
    }

    public List<WorkAt> getWorkAts() {
        return workAts;
    }

    public void setWorkAts(List<WorkAt> workAts) {
        this.workAts = workAts;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(fullName);
        parcel.writeString(username);
        parcel.writeString(bio);
        parcel.writeString(birthDay);
        parcel.writeString(profileUrl);
        parcel.writeString(full_profileUrl);
        parcel.writeString(photo_id);
        parcel.writeString(phoneNumber);
        parcel.writeString(status);
        parcel.writeString(town_name);
        parcel.writeString(neighborhood_name);
        parcel.writeString(native_country_name);
        parcel.writeString(hometown_name);
        parcel.writeString(marital_status);
        parcel.writeString(gender);
        parcel.writeString(live_country_name);
        parcel.writeString(scope);
        parcel.writeLong(date_created);
        parcel.writeByte((byte) (verified ? 1 : 0));
        parcel.writeLong(views);
    }
}
