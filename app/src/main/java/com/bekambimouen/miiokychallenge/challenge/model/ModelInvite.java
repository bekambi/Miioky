package com.bekambimouen.miiokychallenge.challenge.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelInvite implements Parcelable {
    private boolean everyone_can_answer_this_challenge;
    private boolean miioky_challenge;
    private boolean group_challenge;
    private boolean video;
    private boolean image;
    private boolean group_video;
    private boolean group_image;
    // group
    private boolean group;
    private boolean group_private;
    private boolean group_public;
    private String admin_master;
    private String group_id;
    private String group_name;
    private String group_admin;

    private String invite_url;
    private String thumbnail_invite;
    private String invite_photoID;
    private String invite_profile_photo;
    private String invite_username;
    private String invite_userID;
    private String invite_caption;
    private String invite_tag;
    private String invite_category; // to remove
    private String invite_category_status;
    private String invite_country_name;
    private String invite_countryID;
    private long date_created;
    private long timestart; // replace by date created
    private long timeend; // remove

    // indexation
    private String index_i;
    private String index_ii;
    private String index_iii;
    private String index_iv;
    private String index_v;
    private String index_vi;
    private String index_vii;
    private String index_viii;
    private String index_ix;
    private String index_x;

    // big image
    private boolean big_image;
    private String user_id;
    // suppressed the post
    private boolean suppressed;

    public ModelInvite() { }

    public ModelInvite(boolean miioky_challenge, boolean group_challenge, boolean group, boolean group_private,
                       boolean group_public, boolean video, boolean image, boolean group_video, boolean group_image,
                       String admin_master, String group_id, String group_name, String group_admin,
                       String invite_url, String thumbnail_invite, String invite_photoID,
                       String invite_profile_photo, String invite_username, String invite_userID,
                       String invite_caption, String invite_tag, String invite_category,
                       String invite_category_status, String invite_country_name, String invite_countryID,
                       long date_created, long timestart, long timeend, String index_i, String index_ii,
                       String index_iii, String index_iv, String index_v, String index_vi, String index_vii,
                       String index_viii, String index_ix, String index_x, boolean big_image, String user_id,
                       boolean everyone_can_answer_this_challenge, boolean suppressed) {
        this.miioky_challenge = miioky_challenge;
        this.group_challenge = group_challenge;
        this.group = group;
        this.group_private = group_private;
        this.group_public = group_public;
        this.video = video;
        this.image = image;
        this.group_video = group_video;
        this.group_image = group_image;
        this.admin_master = admin_master;
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_admin = group_admin;
        this.invite_url = invite_url;
        this.thumbnail_invite = thumbnail_invite;
        this.invite_photoID = invite_photoID;
        this.invite_profile_photo = invite_profile_photo;
        this.invite_username = invite_username;
        this.invite_userID = invite_userID;
        this.invite_caption = invite_caption;
        this.invite_tag = invite_tag;
        this.invite_category = invite_category;
        this.invite_category_status = invite_category_status;
        this.invite_country_name = invite_country_name;
        this.invite_countryID = invite_countryID;
        this.date_created = date_created;
        this.timestart = timestart;
        this.timeend = timeend;
        this.index_i = index_i;
        this.index_ii = index_ii;
        this.index_iii = index_iii;
        this.index_iv = index_iv;
        this.index_v = index_v;
        this.index_vi = index_vi;
        this.index_vii = index_vii;
        this.index_viii = index_viii;
        this.index_ix = index_ix;
        this.index_x = index_x;
        this.big_image = big_image;
        this.user_id = user_id;
        this.everyone_can_answer_this_challenge = everyone_can_answer_this_challenge;
        this.suppressed = suppressed;
    }

    protected ModelInvite(Parcel in) {
        suppressed = in.readByte() != 0;
        miioky_challenge = in.readByte() != 0;
        group_challenge = in.readByte() != 0;
        group = in.readByte() != 0;
        group_private = in.readByte() != 0;
        group_public = in.readByte() != 0;
        video = in.readByte() != 0;
        image = in.readByte() != 0;
        group_video = in.readByte() != 0;
        group_image = in.readByte() != 0;
        invite_url = in.readString();
        thumbnail_invite = in.readString();
        invite_photoID = in.readString();
        invite_profile_photo = in.readString();
        invite_username = in.readString();
        invite_userID = in.readString();
        invite_caption = in.readString();
        invite_tag = in.readString();
        invite_category = in.readString();
        invite_category_status = in.readString();
        invite_country_name = in.readString();
        invite_countryID = in.readString();
        date_created = in.readLong();
        timestart = in.readLong();
        timeend = in.readLong();
        admin_master = in.readString();
        group_id = in.readString();
        group_name = in.readString();
        group_admin = in.readString();
        index_i = in.readString();
        index_ii = in.readString();
        index_iii = in.readString();
        index_iv = in.readString();
        index_v = in.readString();
        index_vi = in.readString();
        index_vii = in.readString();
        index_viii = in.readString();
        index_ix = in.readString();
        index_x = in.readString();
        big_image = in.readByte() != 0;
        user_id = in.readString();
        everyone_can_answer_this_challenge = in.readByte() != 0;
    }

    public static final Creator<ModelInvite> CREATOR = new Creator<ModelInvite>() {
        @Override
        public ModelInvite createFromParcel(Parcel in) {
            return new ModelInvite(in);
        }

        @Override
        public ModelInvite[] newArray(int size) {
            return new ModelInvite[size];
        }
    };

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public boolean isMiioky_challenge() {
        return miioky_challenge;
    }

    public void setMiioky_challenge(boolean miioky_challenge) {
        this.miioky_challenge = miioky_challenge;
    }

    public boolean isGroup_challenge() {
        return group_challenge;
    }

    public void setGroup_challenge(boolean group_challenge) {
        this.group_challenge = group_challenge;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public boolean isGroup_video() {
        return group_video;
    }

    public void setGroup_video(boolean group_video) {
        this.group_video = group_video;
    }

    public boolean isGroup_image() {
        return group_image;
    }

    public void setGroup_image(boolean group_image) {
        this.group_image = group_image;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isGroup_private() {
        return group_private;
    }

    public void setGroup_private(boolean group_private) {
        this.group_private = group_private;
    }

    public boolean isGroup_public() {
        return group_public;
    }

    public void setGroup_public(boolean group_public) {
        this.group_public = group_public;
    }

    public String getInvite_url() {
        return invite_url;
    }

    public void setInvite_url(String invite_url) {
        this.invite_url = invite_url;
    }

    public String getThumbnail_invite() {
        return thumbnail_invite;
    }

    public void setThumbnail_invite(String thumbnail_invite) {
        this.thumbnail_invite = thumbnail_invite;
    }

    public String getInvite_photoID() {
        return invite_photoID;
    }

    public void setInvite_photoID(String invite_photoID) {
        this.invite_photoID = invite_photoID;
    }

    public String getInvite_profile_photo() {
        return invite_profile_photo;
    }

    public void setInvite_profile_photo(String invite_profile_photo) {
        this.invite_profile_photo = invite_profile_photo;
    }

    public String getInvite_username() {
        return invite_username;
    }

    public void setInvite_username(String invite_username) {
        this.invite_username = invite_username;
    }

    public String getInvite_userID() {
        return invite_userID;
    }

    public void setInvite_userID(String invite_userID) {
        this.invite_userID = invite_userID;
    }

    public String getInvite_caption() {
        return invite_caption;
    }

    public void setInvite_caption(String invite_caption) {
        this.invite_caption = invite_caption;
    }

    public String getInvite_tag() {
        return invite_tag;
    }

    public void setInvite_tag(String invite_tag) {
        this.invite_tag = invite_tag;
    }

    public String getInvite_category() {
        return invite_category;
    }

    public void setInvite_category(String invite_category) {
        this.invite_category = invite_category;
    }

    public String getInvite_category_status() {
        return invite_category_status;
    }

    public void setInvite_category_status(String invite_category_status) {
        this.invite_category_status = invite_category_status;
    }

    public String getInvite_country_name() {
        return invite_country_name;
    }

    public void setInvite_country_name(String invite_country_name) {
        this.invite_country_name = invite_country_name;
    }

    public String getInvite_countryID() {
        return invite_countryID;
    }

    public void setInvite_countryID(String invite_countryID) {
        this.invite_countryID = invite_countryID;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getIndex_i() {
        return index_i;
    }

    public void setIndex_i(String index_i) {
        this.index_i = index_i;
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

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_admin() {
        return group_admin;
    }

    public void setGroup_admin(String group_admin) {
        this.group_admin = group_admin;
    }

    public String getIndex_ii() {
        return index_ii;
    }

    public void setIndex_ii(String index_ii) {
        this.index_ii = index_ii;
    }

    public String getIndex_iii() {
        return index_iii;
    }

    public void setIndex_iii(String index_iii) {
        this.index_iii = index_iii;
    }

    public String getIndex_iv() {
        return index_iv;
    }

    public void setIndex_iv(String index_iv) {
        this.index_iv = index_iv;
    }

    public String getIndex_v() {
        return index_v;
    }

    public void setIndex_v(String index_v) {
        this.index_v = index_v;
    }

    public String getIndex_vi() {
        return index_vi;
    }

    public void setIndex_vi(String index_vi) {
        this.index_vi = index_vi;
    }

    public String getIndex_vii() {
        return index_vii;
    }

    public void setIndex_vii(String index_vii) {
        this.index_vii = index_vii;
    }

    public String getIndex_viii() {
        return index_viii;
    }

    public void setIndex_viii(String index_viii) {
        this.index_viii = index_viii;
    }

    public String getIndex_ix() {
        return index_ix;
    }

    public void setIndex_ix(String index_ix) {
        this.index_ix = index_ix;
    }

    public String getIndex_x() {
        return index_x;
    }

    public void setIndex_x(String index_x) {
        this.index_x = index_x;
    }

    public boolean isBig_image() {
        return big_image;
    }

    public void setBig_image(boolean big_image) {
        this.big_image = big_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isEveryone_can_answer_this_challenge() {
        return everyone_can_answer_this_challenge;
    }

    public void setEveryone_can_answer_this_challenge(boolean everyone_can_answer_this_challenge) {
        this.everyone_can_answer_this_challenge = everyone_can_answer_this_challenge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (suppressed ? 1 : 0));
        parcel.writeByte((byte) (miioky_challenge ? 1 : 0));
        parcel.writeByte((byte) (group_challenge ? 1 : 0));
        parcel.writeByte((byte) (group ? 1 : 0));
        parcel.writeByte((byte) (group_private ? 1 : 0));
        parcel.writeByte((byte) (group_public ? 1 : 0));
        parcel.writeByte((byte) (video ? 1 : 0));
        parcel.writeByte((byte) (image ? 1 : 0));
        parcel.writeByte((byte) (group_video ? 1 : 0));
        parcel.writeByte((byte) (group_image ? 1 : 0));
        parcel.writeString(invite_url);
        parcel.writeString(thumbnail_invite);
        parcel.writeString(invite_photoID);
        parcel.writeString(invite_profile_photo);
        parcel.writeString(invite_username);
        parcel.writeString(invite_userID);
        parcel.writeString(invite_caption);
        parcel.writeString(invite_tag);
        parcel.writeString(invite_category);
        parcel.writeString(invite_category_status);
        parcel.writeString(invite_country_name);
        parcel.writeString(invite_countryID);
        parcel.writeLong(date_created);
        parcel.writeLong(timestart);
        parcel.writeLong(timeend);
        parcel.writeString(admin_master);
        parcel.writeString(group_id);
        parcel.writeString(group_name);
        parcel.writeString(group_admin);
        parcel.writeString(index_i);
        parcel.writeString(index_ii);
        parcel.writeString(index_iii);
        parcel.writeString(index_iv);
        parcel.writeString(index_v);
        parcel.writeString(index_vi);
        parcel.writeString(index_vii);
        parcel.writeString(index_viii);
        parcel.writeString(index_ix);
        parcel.writeString(index_x);
        parcel.writeByte((byte) (big_image ? 1 : 0));
        parcel.writeString(user_id);
        parcel.writeByte((byte) (everyone_can_answer_this_challenge ? 1 : 0));
    }
}

