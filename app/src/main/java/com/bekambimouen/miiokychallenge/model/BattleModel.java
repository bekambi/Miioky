package com.bekambimouen.miiokychallenge.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BattleModel implements Parcelable {
    // constant
    // recycler story
    private boolean recycler_story; // change this word by header
    // friends suggestion
    private boolean friends_suggestion_one;
    private boolean friends_suggestion_two;
    private boolean friends_suggestion_three;
    private boolean friends_suggestion_four;
    private boolean friends_suggestion_five;
    // groups suggestion
    private boolean groups_suggestion_one;
    private boolean groups_suggestion_two;
    private boolean groups_suggestion_three;
    private boolean groups_suggestion_four;
    private boolean groups_suggestion_five;
    // videos suggestion
    private boolean videos_suggestion_one;
    private boolean videos_suggestion_two;
    private boolean videos_suggestion_three;
    private boolean videos_suggestion_four;
    private boolean videos_suggestion_five;
    private boolean everyone_can_answer_this_challenge;

    private boolean recyclerItem;
    private boolean imageUne;
    private boolean videoUne;
    private boolean imageDouble;
    private boolean videoDouble;
    private boolean reponseImageDouble;
    private boolean reponseVideoDouble;

    // group
    private boolean group;
    private boolean group_private;
    private boolean group_public;
    private boolean group_cover_profile_photo;
    private boolean group_cover_background_profile_photo;

    private boolean group_recyclerItem;
    private boolean group_imageUne;
    private boolean group_videoUne;
    private boolean group_imageDouble;
    private boolean group_videoDouble;
    private boolean group_response_imageDouble;
    private boolean group_response_videoDouble;

    // share
    // took from Miioky to the group
    private boolean group_share_single_bottom_circle_image;
    private boolean group_share_single_bottom_image_double;
    private boolean group_share_single_bottom_image_une;
    private boolean group_share_single_bottom_recycler;
    private boolean group_share_single_bottom_response_image_double;
    private boolean group_share_single_bottom_response_video_double;
    private boolean group_share_single_bottom_video_double;
    private boolean group_share_single_bottom_video_une;

    private boolean group_share_top_bottom_circle_image;
    private boolean group_share_top_bottom_image_double;
    private boolean group_share_top_bottom_image_une;
    private boolean group_share_top_bottom_recycler;
    private boolean group_share_top_bottom_response_image_double;
    private boolean group_share_top_bottom_response_video_double;
    private boolean group_share_top_bottom_video_double;
    private boolean group_share_top_bottom_video_une;

    // took from group to Miioky
    private boolean group_share_single_top_circle_image;
    private boolean group_share_single_top_image_double;
    private boolean group_share_single_top_image_une;
    private boolean group_share_single_top_recycler;
    private boolean group_share_single_top_response_image_double;
    private boolean group_share_single_top_response_video_double;
    private boolean group_share_single_top_video_double;
    private boolean group_share_single_top_video_une;

    private String group_id;
    private String admin_master;
    private String publisher;
    private String group_profile_photo;
    private String group_full_profile_photo;
    private String user_id_sharing;
    private String group_user_background_profile_url;
    private String group_user_background_full_profile_url;

    // group shared
    private String shared_group_id;
    private String sharing_admin_master;

    // pour le Profile Grid
    private boolean gridRecyclerImage;

    private boolean reponse_deja;
    private String details;

    // invite
    private String invite_url;
    private String invite_photoID;
    private String invite_profile_photo;
    private String invite_username;
    private String invite_userID;
    private String invite_caption;
    private String invite_tag;
    private String invite_category;
    private String invite_category_status;
    private String invite_country_name;
    private String invite_countryID;

    // for reponse to invitation challenge
    private String reponse_url;
    private String reponse_profile_photo;
    private String reponse_username;
    private String reponse_user_id;
    private String reponse_photoID;
    private String reponse_caption;
    private String reponse_tag;
    private String reponse_country_name;
    private String reponse_countryID;

    private String caption;
    private String sharing_caption;
    private String captionUn;
    private String url;
    private String urlUn;
    private String urlDeux;
    private long date_created;
    private long date_shared;
    private String photo_id;
    private String photo_id_un;
    private String photo_id_deux;
    private String user_id;
    private String tags;
    private String tagsUn;
    private String tagsDeux;
    private String user_profile_photo;
    private String user_full_profile_photo;
    private List<Like> likes;
    private List<Crush> crush;
    private List<Comment> comments;
    private String country_name;
    private String countryID;

    private String urli;
    private String urlii;
    private String urliii;
    private String urliv;
    private String urlv;
    private String urlvi;
    private String urlvii;
    private String urlviii;
    private String urlix;
    private String urlx;
    private String urlxi;
    private String urlxii;
    private String urlxiii;
    private String urlxiv;
    private String urlxv;
    private String urlxvi;
    private String urlxvii;

    private String photoi_id;
    private String photoii_id;
    private String photoiii_id;
    private String photoiv_id;
    private String photov_id;
    private String photovi_id;
    private String photovii_id;
    private String photoviii_id;
    private String photoix_id;
    private String photox_id;
    private String photoxi_id;
    private String photoxii_id;
    private String photoxiii_id;
    private String photoxiv_id;
    private String photoxv_id;
    private String photoxvi_id;
    private String photoxvii_id;

    private String caption_i;
    private String caption_ii;
    private String caption_iii;
    private String caption_iv;
    private String caption_v;
    private String caption_vi;
    private String caption_vii;
    private String caption_viii;
    private String caption_ix;
    private String caption_x;
    private String caption_xi;
    private String caption_xii;
    private String caption_xiii;
    private String caption_xiv;
    private String caption_xv;
    private String caption_xvi;
    private String caption_xvii;

    // thumbnails
    private String thumbnail;
    private String thumbnail_invite;
    private String thumbnail_response;
    private String thumbnail_un;
    private String thumbnail_deux;
    // shared
    private boolean recyclerItem_shared;
    private boolean imageUne_shared;
    private boolean videoUne_shared;
    private boolean imageDouble_shared;
    private boolean videoDouble_shared;
    private boolean reponseImageDouble_shared;
    private boolean reponseVideoDouble_shared;
    private boolean user_profile_shared;
    private boolean user_profile;
    // comment text
    private boolean comment_text;
    private String comment_subject;
    private String comment_theme;

    // big image
    private boolean big_image;

    // for save publication
    private String username;
    private String profileUrl;
    private String auth_user_id; // replace by user_saved_id
    private String user_id_shared;
    private List<MediaID> media_id;

    // post identity
    private String post_identity;
    // suppressed the post
    private boolean suppressed;
    private long views;

    public BattleModel() {
    }

    public BattleModel(boolean recyclerItem, boolean imageUne, boolean videoUne, boolean imageDouble,
                       boolean videoDouble, boolean reponseImageDouble, boolean reponseVideoDouble,
                       boolean group, boolean group_private, boolean group_public, boolean group_cover_profile_photo,
                       boolean group_cover_background_profile_photo, boolean group_recyclerItem, boolean group_imageUne,
                       boolean group_videoUne, boolean group_imageDouble, boolean group_videoDouble,
                       boolean group_response_imageDouble, boolean group_response_videoDouble,
                       boolean group_share_single_bottom_image_double, boolean group_share_single_bottom_image_une,
                       boolean group_share_single_bottom_recycler, boolean group_share_single_bottom_response_image_double,
                       boolean group_share_single_bottom_response_video_double, boolean group_share_single_bottom_video_double,
                       boolean group_share_single_bottom_video_une, boolean group_share_top_bottom_image_double,
                       boolean group_share_top_bottom_image_une, boolean group_share_top_bottom_recycler,
                       boolean group_share_top_bottom_response_image_double, boolean group_share_top_bottom_response_video_double,
                       boolean group_share_top_bottom_video_double, boolean group_share_top_bottom_video_une,
                       boolean group_share_single_top_image_double, boolean group_share_single_top_image_une,
                       boolean group_share_single_top_recycler, boolean group_share_single_top_response_image_double,
                       boolean group_share_single_top_response_video_double, boolean group_share_single_top_video_double,
                       boolean group_share_single_top_video_une, String admin_master,
                       String group_id, String publisher, String group_profile_photo,
                       String group_full_profile_photo, String group_user_background_profile_url,
                       String group_user_background_full_profile_url, boolean gridRecyclerImage, boolean reponse_deja,
                       String details, String invite_url, String invite_photoID, String invite_profile_photo,
                       String invite_username, String invite_userID, String invite_caption, String invite_tag,
                       String invite_category, String invite_category_status, String invite_country_name, String invite_countryID,
                       String reponse_url, String reponse_profile_photo, String reponse_username, String reponse_user_id,
                       String reponse_photoID, String reponse_caption, String reponse_tag, String reponse_country_name,
                       String reponse_countryID, String caption, String sharing_caption, String captionUn,
                       String url, String urlUn, String urlDeux, long date_created, long date_shared, String photo_id,
                       String photo_id_un, String photo_id_deux, String user_id, String tags, String tagsUn, String tagsDeux,
                       String user_profile_photo, String user_full_profile_photo, List<Like> likes, List<Crush> crush,
                       List<Comment> comments, List<MediaID> media_id, String country_name, String countryID, String urli,
                       String urlii, String urliii, String urliv, String urlv, String urlvi, String urlvii, String urlviii, String urlix,
                       String urlx, String urlxi, String urlxii, String urlxiii, String urlxiv, String urlxv, String urlxvi, String urlxvii,
                       String photoi_id, String photoii_id, String photoiii_id, String photoiv_id, String photov_id, String photovi_id,
                       String photovii_id, String photoviii_id, String photoix_id, String photox_id, String photoxi_id, String photoxii_id,
                       String photoxiii_id, String photoxiv_id, String photoxv_id, String photoxvi_id, String photoxvii_id, String caption_i,
                       String caption_ii, String caption_iii, String caption_iv, String caption_v, String caption_vi, String caption_vii,
                       String caption_viii, String caption_ix, String caption_x, String caption_xi, String caption_xii, String caption_xiii,
                       String caption_xiv, String caption_xv, String caption_xvi, String caption_xvii, String thumbnail,
                       String thumbnail_invite, String thumbnail_response, String thumbnail_un, String thumbnail_deux,
                       boolean recyclerItem_shared, boolean imageUne_shared, boolean videoUne_shared, boolean imageDouble_shared,
                       boolean videoDouble_shared, boolean reponseImageDouble_shared, boolean reponseVideoDouble_shared, String user_id_sharing,
                       boolean user_profile,
                       boolean group_share_single_bottom_circle_image, boolean group_share_top_bottom_circle_image, boolean group_share_single_top_circle_image,
                       boolean user_profile_shared, String shared_group_id, String sharing_admin_master, boolean comment_text, String comment_subject,
                       String comment_theme, boolean big_image, String username, String profileUrl, String auth_user_id, String user_id_shared,
                       boolean recycler_story, boolean friends_suggestion_one, boolean friends_suggestion_two, boolean friends_suggestion_three,
                       boolean friends_suggestion_four, boolean friends_suggestion_five, boolean groups_suggestion_one, boolean groups_suggestion_two,
                       boolean groups_suggestion_three, boolean groups_suggestion_four, boolean groups_suggestion_five, boolean videos_suggestion_one,
                       boolean videos_suggestion_two, boolean videos_suggestion_three, boolean videos_suggestion_four, boolean videos_suggestion_five,
                       boolean everyone_can_answer_this_challenge, String post_identity, boolean suppressed, long views) {
        this.recyclerItem = recyclerItem;
        this.imageUne = imageUne;
        this.videoUne = videoUne;
        this.imageDouble = imageDouble;
        this.videoDouble = videoDouble;
        this.reponseImageDouble = reponseImageDouble;
        this.reponseVideoDouble = reponseVideoDouble;
        this.group = group;
        this.group_private = group_private;
        this.group_public = group_public;
        this.group_cover_profile_photo = group_cover_profile_photo;
        this.group_cover_background_profile_photo = group_cover_background_profile_photo;
        this.group_recyclerItem = group_recyclerItem;
        this.group_imageUne = group_imageUne;
        this.group_videoUne = group_videoUne;
        this.group_imageDouble = group_imageDouble;
        this.group_videoDouble = group_videoDouble;
        this.group_response_imageDouble = group_response_imageDouble;
        this.group_response_videoDouble = group_response_videoDouble;
        this.group_share_single_bottom_image_double = group_share_single_bottom_image_double;
        this.group_share_single_bottom_image_une = group_share_single_bottom_image_une;
        this.group_share_single_bottom_recycler = group_share_single_bottom_recycler;
        this.group_share_single_bottom_response_image_double = group_share_single_bottom_response_image_double;
        this.group_share_single_bottom_response_video_double = group_share_single_bottom_response_video_double;
        this.group_share_single_bottom_video_double = group_share_single_bottom_video_double;
        this.group_share_single_bottom_video_une = group_share_single_bottom_video_une;
        this.group_share_top_bottom_image_double = group_share_top_bottom_image_double;
        this.group_share_top_bottom_image_une = group_share_top_bottom_image_une;
        this.group_share_top_bottom_recycler = group_share_top_bottom_recycler;
        this.group_share_top_bottom_response_image_double = group_share_top_bottom_response_image_double;
        this.group_share_top_bottom_response_video_double = group_share_top_bottom_response_video_double;
        this.group_share_top_bottom_video_double = group_share_top_bottom_video_double;
        this.group_share_top_bottom_video_une = group_share_top_bottom_video_une;
        this.group_share_single_top_image_double = group_share_single_top_image_double;
        this.group_share_single_top_image_une = group_share_single_top_image_une;
        this.group_share_single_top_recycler = group_share_single_top_recycler;
        this.group_share_single_top_response_image_double = group_share_single_top_response_image_double;
        this.group_share_single_top_response_video_double = group_share_single_top_response_video_double;
        this.group_share_single_top_video_double = group_share_single_top_video_double;
        this.group_share_single_top_video_une = group_share_single_top_video_une;
        this.admin_master = admin_master;
        this.group_id = group_id;
        this.publisher = publisher;
        this.group_profile_photo = group_profile_photo;
        this.group_full_profile_photo = group_full_profile_photo;
        this.group_user_background_profile_url = group_user_background_profile_url;
        this.group_user_background_full_profile_url = group_user_background_full_profile_url;
        this.gridRecyclerImage = gridRecyclerImage;
        this.reponse_deja = reponse_deja;
        this.details = details;
        this.invite_url = invite_url;
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
        this.reponse_url = reponse_url;
        this.reponse_profile_photo = reponse_profile_photo;
        this.reponse_username = reponse_username;
        this.reponse_user_id = reponse_user_id;
        this.reponse_photoID = reponse_photoID;
        this.reponse_caption = reponse_caption;
        this.reponse_tag = reponse_tag;
        this.reponse_country_name = reponse_country_name;
        this.reponse_countryID = reponse_countryID;
        this.caption = caption;
        this.sharing_caption = sharing_caption;
        this.captionUn = captionUn;
        this.url = url;
        this.urlUn = urlUn;
        this.urlDeux = urlDeux;
        this.date_created = date_created;
        this.date_shared = date_shared;
        this.photo_id = photo_id;
        this.photo_id_un = photo_id_un;
        this.photo_id_deux = photo_id_deux;
        this.user_id = user_id;
        this.tags = tags;
        this.tagsUn = tagsUn;
        this.tagsDeux = tagsDeux;
        this.user_profile_photo = user_profile_photo;
        this.user_full_profile_photo = user_full_profile_photo;
        this.likes = likes;
        this.crush = crush;
        this.comments = comments;
        this.media_id = media_id;
        this.country_name = country_name;
        this.countryID = countryID;
        this.urli = urli;
        this.urlii = urlii;
        this.urliii = urliii;
        this.urliv = urliv;
        this.urlv = urlv;
        this.urlvi = urlvi;
        this.urlvii = urlvii;
        this.urlviii = urlviii;
        this.urlix = urlix;
        this.urlx = urlx;
        this.urlxi = urlxi;
        this.urlxii = urlxii;
        this.urlxiii = urlxiii;
        this.urlxiv = urlxiv;
        this.urlxv = urlxv;
        this.urlxvi = urlxvi;
        this.urlxvii = urlxvii;
        this.photoi_id = photoi_id;
        this.photoii_id = photoii_id;
        this.photoiii_id = photoiii_id;
        this.photoiv_id = photoiv_id;
        this.photov_id = photov_id;
        this.photovi_id = photovi_id;
        this.photovii_id = photovii_id;
        this.photoviii_id = photoviii_id;
        this.photoix_id = photoix_id;
        this.photox_id = photox_id;
        this.photoxi_id = photoxi_id;
        this.photoxii_id = photoxii_id;
        this.photoxiii_id = photoxiii_id;
        this.photoxiv_id = photoxiv_id;
        this.photoxv_id = photoxv_id;
        this.photoxvi_id = photoxvi_id;
        this.photoxvii_id = photoxvii_id;
        this.caption_i = caption_i;
        this.caption_ii = caption_ii;
        this.caption_iii = caption_iii;
        this.caption_iv = caption_iv;
        this.caption_v = caption_v;
        this.caption_vi = caption_vi;
        this.caption_vii = caption_vii;
        this.caption_viii = caption_viii;
        this.caption_ix = caption_ix;
        this.caption_x = caption_x;
        this.caption_xi = caption_xi;
        this.caption_xii = caption_xii;
        this.caption_xiii = caption_xiii;
        this.caption_xiv = caption_xiv;
        this.caption_xv = caption_xv;
        this.caption_xvi = caption_xvi;
        this.caption_xvii = caption_xvii;
        this.thumbnail = thumbnail;
        this.thumbnail_invite = thumbnail_invite;
        this.thumbnail_response = thumbnail_response;
        this.thumbnail_un = thumbnail_un;
        this.thumbnail_deux = thumbnail_deux;
        this.recyclerItem_shared = recyclerItem_shared;
        this.imageUne_shared = imageUne_shared;
        this.videoUne_shared = videoUne_shared;
        this.imageDouble_shared = imageDouble_shared;
        this.videoDouble_shared = videoDouble_shared;
        this.reponseImageDouble_shared = reponseImageDouble_shared;
        this.reponseVideoDouble_shared = reponseVideoDouble_shared;
        this.user_id_sharing = user_id_sharing;
        this.user_profile = user_profile;
        this.group_share_single_bottom_circle_image = group_share_single_bottom_circle_image;
        this.group_share_single_top_circle_image = group_share_single_top_circle_image;
        this.group_share_top_bottom_circle_image = group_share_top_bottom_circle_image;
        this.user_profile_shared = user_profile_shared;
        this.shared_group_id = shared_group_id;
        this.sharing_admin_master = sharing_admin_master;
        this.comment_text = comment_text;
        this.comment_subject = comment_subject;
        this.comment_theme = comment_theme;
        this.big_image = big_image;
        this.username = username;
        this.profileUrl = profileUrl;
        this.auth_user_id = auth_user_id;
        this.user_id_shared = user_id_shared;
        this.recycler_story = recycler_story;
        this.friends_suggestion_one = friends_suggestion_one;
        this.friends_suggestion_two = friends_suggestion_two;
        this.friends_suggestion_three = friends_suggestion_three;
        this.friends_suggestion_four = friends_suggestion_four;
        this.friends_suggestion_five = friends_suggestion_five;
        this.groups_suggestion_one = groups_suggestion_one;
        this.groups_suggestion_two = groups_suggestion_two;
        this.groups_suggestion_three = groups_suggestion_three;
        this.groups_suggestion_four = groups_suggestion_four;
        this.groups_suggestion_five = groups_suggestion_five;
        this.videos_suggestion_one = videos_suggestion_one;
        this.videos_suggestion_two = videos_suggestion_two;
        this.videos_suggestion_three = videos_suggestion_three;
        this.videos_suggestion_four = videos_suggestion_four;
        this.videos_suggestion_five = videos_suggestion_five;
        this.everyone_can_answer_this_challenge = everyone_can_answer_this_challenge;
        this.post_identity = post_identity;
        this.suppressed = suppressed;
        this.views = views;
    }

    protected BattleModel(Parcel in) {
        suppressed = in.readByte() != 0;
        recyclerItem = in.readByte() != 0;
        imageUne = in.readByte() != 0;
        videoUne = in.readByte() != 0;
        imageDouble = in.readByte() != 0;
        videoDouble = in.readByte() != 0;
        reponseImageDouble = in.readByte() != 0;
        reponseVideoDouble = in.readByte() != 0;
        group = in.readByte() != 0;
        group_private = in.readByte() != 0;
        group_public = in.readByte() != 0;
        group_cover_profile_photo = in.readByte() != 0;
        group_cover_background_profile_photo = in.readByte() != 0;
        group_recyclerItem = in.readByte() != 0;
        group_imageUne = in.readByte() != 0;
        group_videoUne = in.readByte() != 0;
        group_imageDouble = in.readByte() != 0;
        group_videoDouble = in.readByte() != 0;
        group_response_imageDouble = in.readByte() != 0;
        group_response_videoDouble = in.readByte() != 0;
        group_share_single_bottom_image_double = in.readByte() != 0;
        group_share_single_bottom_image_une = in.readByte() != 0;
        group_share_single_bottom_recycler = in.readByte() != 0;
        group_share_single_bottom_response_image_double = in.readByte() != 0;
        group_share_single_bottom_response_video_double = in.readByte() != 0;
        group_share_single_bottom_video_double = in.readByte() != 0;
        group_share_single_bottom_video_une = in.readByte() != 0;
        group_share_top_bottom_image_double = in.readByte() != 0;
        group_share_top_bottom_image_une = in.readByte() != 0;
        group_share_top_bottom_recycler = in.readByte() != 0;
        group_share_top_bottom_response_image_double = in.readByte() != 0;
        group_share_top_bottom_response_video_double = in.readByte() != 0;
        group_share_top_bottom_video_double = in.readByte() != 0;
        group_share_top_bottom_video_une = in.readByte() != 0;
        group_share_single_top_image_double = in.readByte() != 0;
        group_share_single_top_image_une = in.readByte() != 0;
        group_share_single_top_recycler = in.readByte() != 0;
        group_share_single_top_response_image_double = in.readByte() != 0;
        group_share_single_top_response_video_double = in.readByte() != 0;
        group_share_single_top_video_double = in.readByte() != 0;
        group_share_single_top_video_une = in.readByte() != 0;
        user_profile = in.readByte() != 0;
        group_share_single_bottom_circle_image = in.readByte() != 0;
        group_share_single_top_circle_image = in.readByte() != 0;
        group_share_top_bottom_circle_image = in.readByte() != 0;
        user_profile_shared = in.readByte() != 0;
        recycler_story = in.readByte() != 0;
        friends_suggestion_one = in.readByte() != 0;
        friends_suggestion_two = in.readByte() != 0;
        friends_suggestion_three = in.readByte() != 0;
        friends_suggestion_four = in.readByte() != 0;
        friends_suggestion_five = in.readByte() != 0;
        groups_suggestion_one = in.readByte() != 0;
        groups_suggestion_two = in.readByte() != 0;
        groups_suggestion_three = in.readByte() != 0;
        groups_suggestion_four = in.readByte() != 0;
        groups_suggestion_five = in.readByte() != 0;
        videos_suggestion_one = in.readByte() != 0;
        videos_suggestion_two = in.readByte() != 0;
        videos_suggestion_three = in.readByte() != 0;
        videos_suggestion_four = in.readByte() != 0;
        videos_suggestion_five = in.readByte() != 0;
        everyone_can_answer_this_challenge = in.readByte() != 0;
        admin_master = in.readString();
        group_id = in.readString();
        publisher = in.readString();
        group_profile_photo = in.readString();
        group_full_profile_photo = in.readString();
        group_user_background_profile_url = in.readString();
        group_user_background_full_profile_url = in.readString();
        gridRecyclerImage = in.readByte() != 0;
        reponse_deja = in.readByte() != 0;
        details = in.readString();
        invite_url = in.readString();
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
        reponse_url = in.readString();
        reponse_profile_photo = in.readString();
        reponse_username = in.readString();
        reponse_user_id = in.readString();
        reponse_photoID = in.readString();
        reponse_caption = in.readString();
        reponse_tag = in.readString();
        reponse_country_name = in.readString();
        reponse_countryID = in.readString();
        caption = in.readString();
        sharing_caption = in.readString();
        captionUn = in.readString();
        url = in.readString();
        urlUn = in.readString();
        urlDeux = in.readString();
        date_created = in.readLong();
        date_shared = in.readLong();
        photo_id = in.readString();
        photo_id_un = in.readString();
        photo_id_deux = in.readString();
        user_id = in.readString();
        tags = in.readString();
        tagsUn = in.readString();
        tagsDeux = in.readString();
        user_profile_photo = in.readString();
        user_full_profile_photo = in.readString();
        country_name = in.readString();
        countryID = in.readString();
        urli = in.readString();
        urlii = in.readString();
        urliii = in.readString();
        urliv = in.readString();
        urlv = in.readString();
        urlvi = in.readString();
        urlvii = in.readString();
        urlviii = in.readString();
        urlix = in.readString();
        urlx = in.readString();
        urlxi = in.readString();
        urlxii = in.readString();
        urlxiii = in.readString();
        urlxiv = in.readString();
        urlxv = in.readString();
        urlxvi = in.readString();
        urlxvii = in.readString();
        photoi_id = in.readString();
        photoii_id = in.readString();
        photoiii_id = in.readString();
        photoiv_id = in.readString();
        photov_id = in.readString();
        photovi_id = in.readString();
        photovii_id = in.readString();
        photoviii_id = in.readString();
        photoix_id = in.readString();
        photox_id = in.readString();
        photoxi_id = in.readString();
        photoxii_id = in.readString();
        photoxiii_id = in.readString();
        photoxiv_id = in.readString();
        photoxv_id = in.readString();
        photoxvi_id = in.readString();
        photoxvii_id = in.readString();
        caption_i = in.readString();
        caption_ii = in.readString();
        caption_iii = in.readString();
        caption_iv = in.readString();
        caption_v = in.readString();
        caption_vi = in.readString();
        caption_vii = in.readString();
        caption_viii = in.readString();
        caption_ix = in.readString();
        caption_x = in.readString();
        caption_xi = in.readString();
        caption_xii = in.readString();
        caption_xiii = in.readString();
        caption_xiv = in.readString();
        caption_xv = in.readString();
        caption_xvi = in.readString();
        caption_xvii = in.readString();
        thumbnail = in.readString();
        thumbnail_invite = in.readString();
        thumbnail_response = in.readString();
        thumbnail_un = in.readString();
        thumbnail_deux = in.readString();
        recyclerItem_shared = in.readByte() != 0;
        imageUne_shared = in.readByte() != 0;
        videoUne_shared = in.readByte() != 0;
        imageDouble_shared = in.readByte() != 0;
        videoDouble_shared = in.readByte() != 0;
        reponseImageDouble_shared = in.readByte() != 0;
        reponseVideoDouble_shared = in.readByte() != 0;
        user_id_sharing = in.readString();
        shared_group_id = in.readString();
        sharing_admin_master = in.readString();
        comment_text = in.readByte() != 0;
        comment_subject = in.readString();
        comment_theme = in.readString();
        big_image = in.readByte() != 0;
        username = in.readString();
        profileUrl = in.readString();
        auth_user_id = in.readString();
        user_id_shared = in.readString();
        post_identity = in.readString();
        views = in.readLong();
    }

    public static final Creator<BattleModel> CREATOR = new Creator<BattleModel>() {
        @Override
        public BattleModel createFromParcel(Parcel in) {
            return new BattleModel(in);
        }

        @Override
        public BattleModel[] newArray(int size) {
            return new BattleModel[size];
        }
    };

    public boolean isRecycler_story() {
        return recycler_story;
    }

    public void setRecycler_story(boolean recycler_story) {
        this.recycler_story = recycler_story;
    }

    public boolean isFriends_suggestion_one() {
        return friends_suggestion_one;
    }

    public void setFriends_suggestion_one(boolean friends_suggestion_one) {
        this.friends_suggestion_one = friends_suggestion_one;
    }

    public boolean isFriends_suggestion_two() {
        return friends_suggestion_two;
    }

    public void setFriends_suggestion_two(boolean friends_suggestion_two) {
        this.friends_suggestion_two = friends_suggestion_two;
    }

    public boolean isFriends_suggestion_three() {
        return friends_suggestion_three;
    }

    public void setFriends_suggestion_three(boolean friends_suggestion_three) {
        this.friends_suggestion_three = friends_suggestion_three;
    }

    public boolean isFriends_suggestion_four() {
        return friends_suggestion_four;
    }

    public void setFriends_suggestion_four(boolean friends_suggestion_four) {
        this.friends_suggestion_four = friends_suggestion_four;
    }

    public boolean isFriends_suggestion_five() {
        return friends_suggestion_five;
    }

    public void setFriends_suggestion_five(boolean friends_suggestion_five) {
        this.friends_suggestion_five = friends_suggestion_five;
    }

    public boolean isGroups_suggestion_one() {
        return groups_suggestion_one;
    }

    public void setGroups_suggestion_one(boolean groups_suggestion_one) {
        this.groups_suggestion_one = groups_suggestion_one;
    }

    public boolean isGroups_suggestion_two() {
        return groups_suggestion_two;
    }

    public void setGroups_suggestion_two(boolean groups_suggestion_two) {
        this.groups_suggestion_two = groups_suggestion_two;
    }

    public boolean isGroups_suggestion_three() {
        return groups_suggestion_three;
    }

    public void setGroups_suggestion_three(boolean groups_suggestion_three) {
        this.groups_suggestion_three = groups_suggestion_three;
    }

    public boolean isGroups_suggestion_four() {
        return groups_suggestion_four;
    }

    public void setGroups_suggestion_four(boolean groups_suggestion_four) {
        this.groups_suggestion_four = groups_suggestion_four;
    }

    public boolean isGroups_suggestion_five() {
        return groups_suggestion_five;
    }

    public void setGroups_suggestion_five(boolean groups_suggestion_five) {
        this.groups_suggestion_five = groups_suggestion_five;
    }

    public boolean isVideos_suggestion_one() {
        return videos_suggestion_one;
    }

    public void setVideos_suggestion_one(boolean videos_suggestion_one) {
        this.videos_suggestion_one = videos_suggestion_one;
    }

    public boolean isVideos_suggestion_two() {
        return videos_suggestion_two;
    }

    public void setVideos_suggestion_two(boolean videos_suggestion_two) {
        this.videos_suggestion_two = videos_suggestion_two;
    }

    public boolean isVideos_suggestion_three() {
        return videos_suggestion_three;
    }

    public void setVideos_suggestion_three(boolean videos_suggestion_three) {
        this.videos_suggestion_three = videos_suggestion_three;
    }

    public boolean isVideos_suggestion_four() {
        return videos_suggestion_four;
    }

    public void setVideos_suggestion_four(boolean videos_suggestion_four) {
        this.videos_suggestion_four = videos_suggestion_four;
    }

    public boolean isVideos_suggestion_five() {
        return videos_suggestion_five;
    }

    public void setVideos_suggestion_five(boolean videos_suggestion_five) {
        this.videos_suggestion_five = videos_suggestion_five;
    }

    public boolean isEveryone_can_answer_this_challenge() {
        return everyone_can_answer_this_challenge;
    }

    public void setEveryone_can_answer_this_challenge(boolean everyone_can_answer_this_challenge) {
        this.everyone_can_answer_this_challenge = everyone_can_answer_this_challenge;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public boolean isRecyclerItem() {
        return recyclerItem;
    }

    public void setRecyclerItem(boolean recyclerItem) {
        this.recyclerItem = recyclerItem;
    }

    public boolean isImageUne() {
        return imageUne;
    }

    public void setImageUne(boolean imageUne) {
        this.imageUne = imageUne;
    }

    public boolean isVideoUne() {
        return videoUne;
    }

    public void setVideoUne(boolean videoUne) {
        this.videoUne = videoUne;
    }

    public boolean isImageDouble() {
        return imageDouble;
    }

    public void setImageDouble(boolean imageDouble) {
        this.imageDouble = imageDouble;
    }

    public boolean isVideoDouble() {
        return videoDouble;
    }

    public void setVideoDouble(boolean videoDouble) {
        this.videoDouble = videoDouble;
    }

    public boolean isReponseImageDouble() {
        return reponseImageDouble;
    }

    public void setReponseImageDouble(boolean reponseImageDouble) {
        this.reponseImageDouble = reponseImageDouble;
    }

    public boolean isReponseVideoDouble() {
        return reponseVideoDouble;
    }

    public void setReponseVideoDouble(boolean reponseVideoDouble) {
        this.reponseVideoDouble = reponseVideoDouble;
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

    public boolean isGroup_cover_profile_photo() {
        return group_cover_profile_photo;
    }

    public void setGroup_cover_profile_photo(boolean group_cover_profile_photo) {
        this.group_cover_profile_photo = group_cover_profile_photo;
    }

    public boolean isGroup_cover_background_profile_photo() {
        return group_cover_background_profile_photo;
    }

    public void setGroup_cover_background_profile_photo(boolean group_cover_background_profile_photo) {
        this.group_cover_background_profile_photo = group_cover_background_profile_photo;
    }

    public boolean isGroup_recyclerItem() {
        return group_recyclerItem;
    }

    public void setGroup_recyclerItem(boolean group_recyclerItem) {
        this.group_recyclerItem = group_recyclerItem;
    }

    public boolean isGroup_imageUne() {
        return group_imageUne;
    }

    public void setGroup_imageUne(boolean group_imageUne) {
        this.group_imageUne = group_imageUne;
    }

    public boolean isGroup_videoUne() {
        return group_videoUne;
    }

    public void setGroup_videoUne(boolean group_videoUne) {
        this.group_videoUne = group_videoUne;
    }

    public boolean isGroup_imageDouble() {
        return group_imageDouble;
    }

    public void setGroup_imageDouble(boolean group_imageDouble) {
        this.group_imageDouble = group_imageDouble;
    }

    public boolean isGroup_videoDouble() {
        return group_videoDouble;
    }

    public void setGroup_videoDouble(boolean group_videoDouble) {
        this.group_videoDouble = group_videoDouble;
    }

    public boolean isGroup_response_imageDouble() {
        return group_response_imageDouble;
    }

    public void setGroup_response_imageDouble(boolean group_response_imageDouble) {
        this.group_response_imageDouble = group_response_imageDouble;
    }

    public boolean isGroup_response_videoDouble() {
        return group_response_videoDouble;
    }

    public void setGroup_response_videoDouble(boolean group_response_videoDouble) {
        this.group_response_videoDouble = group_response_videoDouble;
    }

    public boolean isGroup_share_single_bottom_image_double() {
        return group_share_single_bottom_image_double;
    }

    public void setGroup_share_single_bottom_image_double(boolean group_share_single_bottom_image_double) {
        this.group_share_single_bottom_image_double = group_share_single_bottom_image_double;
    }

    public boolean isGroup_share_single_bottom_image_une() {
        return group_share_single_bottom_image_une;
    }

    public void setGroup_share_single_bottom_image_une(boolean group_share_single_bottom_image_une) {
        this.group_share_single_bottom_image_une = group_share_single_bottom_image_une;
    }

    public boolean isGroup_share_single_bottom_recycler() {
        return group_share_single_bottom_recycler;
    }

    public void setGroup_share_single_bottom_recycler(boolean group_share_single_bottom_recycler) {
        this.group_share_single_bottom_recycler = group_share_single_bottom_recycler;
    }

    public boolean isGroup_share_single_bottom_response_image_double() {
        return group_share_single_bottom_response_image_double;
    }

    public void setGroup_share_single_bottom_response_image_double(boolean group_share_single_bottom_response_image_double) {
        this.group_share_single_bottom_response_image_double = group_share_single_bottom_response_image_double;
    }

    public boolean isGroup_share_single_bottom_response_video_double() {
        return group_share_single_bottom_response_video_double;
    }

    public void setGroup_share_single_bottom_response_video_double(boolean group_share_single_bottom_response_video_double) {
        this.group_share_single_bottom_response_video_double = group_share_single_bottom_response_video_double;
    }

    public boolean isGroup_share_single_bottom_video_double() {
        return group_share_single_bottom_video_double;
    }

    public void setGroup_share_single_bottom_video_double(boolean group_share_single_bottom_video_double) {
        this.group_share_single_bottom_video_double = group_share_single_bottom_video_double;
    }

    public boolean isGroup_share_single_bottom_video_une() {
        return group_share_single_bottom_video_une;
    }

    public void setGroup_share_single_bottom_video_une(boolean group_share_single_bottom_video_une) {
        this.group_share_single_bottom_video_une = group_share_single_bottom_video_une;
    }

    public boolean isGroup_share_top_bottom_image_double() {
        return group_share_top_bottom_image_double;
    }

    public void setGroup_share_top_bottom_image_double(boolean group_share_top_bottom_image_double) {
        this.group_share_top_bottom_image_double = group_share_top_bottom_image_double;
    }

    public boolean isGroup_share_top_bottom_image_une() {
        return group_share_top_bottom_image_une;
    }

    public void setGroup_share_top_bottom_image_une(boolean group_share_top_bottom_image_une) {
        this.group_share_top_bottom_image_une = group_share_top_bottom_image_une;
    }

    public boolean isGroup_share_top_bottom_recycler() {
        return group_share_top_bottom_recycler;
    }

    public void setGroup_share_top_bottom_recycler(boolean group_share_top_bottom_recycler) {
        this.group_share_top_bottom_recycler = group_share_top_bottom_recycler;
    }

    public boolean isGroup_share_top_bottom_response_image_double() {
        return group_share_top_bottom_response_image_double;
    }

    public void setGroup_share_top_bottom_response_image_double(boolean group_share_top_bottom_response_image_double) {
        this.group_share_top_bottom_response_image_double = group_share_top_bottom_response_image_double;
    }

    public boolean isGroup_share_top_bottom_response_video_double() {
        return group_share_top_bottom_response_video_double;
    }

    public void setGroup_share_top_bottom_response_video_double(boolean group_share_top_bottom_response_video_double) {
        this.group_share_top_bottom_response_video_double = group_share_top_bottom_response_video_double;
    }

    public boolean isGroup_share_top_bottom_video_double() {
        return group_share_top_bottom_video_double;
    }

    public void setGroup_share_top_bottom_video_double(boolean group_share_top_bottom_video_double) {
        this.group_share_top_bottom_video_double = group_share_top_bottom_video_double;
    }

    public boolean isGroup_share_top_bottom_video_une() {
        return group_share_top_bottom_video_une;
    }

    public void setGroup_share_top_bottom_video_une(boolean group_share_top_bottom_video_une) {
        this.group_share_top_bottom_video_une = group_share_top_bottom_video_une;
    }

    public boolean isGroup_share_single_top_image_double() {
        return group_share_single_top_image_double;
    }

    public void setGroup_share_single_top_image_double(boolean group_share_single_top_image_double) {
        this.group_share_single_top_image_double = group_share_single_top_image_double;
    }

    public boolean isGroup_share_single_top_image_une() {
        return group_share_single_top_image_une;
    }

    public void setGroup_share_single_top_image_une(boolean group_share_single_top_image_une) {
        this.group_share_single_top_image_une = group_share_single_top_image_une;
    }

    public boolean isGroup_share_single_top_recycler() {
        return group_share_single_top_recycler;
    }

    public void setGroup_share_single_top_recycler(boolean group_share_single_top_recycler) {
        this.group_share_single_top_recycler = group_share_single_top_recycler;
    }

    public boolean isGroup_share_single_top_response_image_double() {
        return group_share_single_top_response_image_double;
    }

    public void setGroup_share_single_top_response_image_double(boolean group_share_single_top_response_image_double) {
        this.group_share_single_top_response_image_double = group_share_single_top_response_image_double;
    }

    public boolean isGroup_share_single_top_response_video_double() {
        return group_share_single_top_response_video_double;
    }

    public void setGroup_share_single_top_response_video_double(boolean group_share_single_top_response_video_double) {
        this.group_share_single_top_response_video_double = group_share_single_top_response_video_double;
    }

    public boolean isGroup_share_single_top_video_double() {
        return group_share_single_top_video_double;
    }

    public void setGroup_share_single_top_video_double(boolean group_share_single_top_video_double) {
        this.group_share_single_top_video_double = group_share_single_top_video_double;
    }

    public boolean isGroup_share_single_top_video_une() {
        return group_share_single_top_video_une;
    }

    public void setGroup_share_single_top_video_une(boolean group_share_single_top_video_une) {
        this.group_share_single_top_video_une = group_share_single_top_video_une;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGroup_profile_photo() {
        return group_profile_photo;
    }

    public void setGroup_profile_photo(String group_profile_photo) {
        this.group_profile_photo = group_profile_photo;
    }

    public String getGroup_full_profile_photo() {
        return group_full_profile_photo;
    }

    public void setGroup_full_profile_photo(String group_full_profile_photo) {
        this.group_full_profile_photo = group_full_profile_photo;
    }

    public String getGroup_user_background_profile_url() {
        return group_user_background_profile_url;
    }

    public void setGroup_user_background_profile_url(String group_user_background_profile_url) {
        this.group_user_background_profile_url = group_user_background_profile_url;
    }

    public String getGroup_user_background_full_profile_url() {
        return group_user_background_full_profile_url;
    }

    public void setGroup_user_background_full_profile_url(String group_user_background_full_profile_url) {
        this.group_user_background_full_profile_url = group_user_background_full_profile_url;
    }

    public boolean isGridRecyclerImage() {
        return gridRecyclerImage;
    }

    public void setGridRecyclerImage(boolean gridRecyclerImage) {
        this.gridRecyclerImage = gridRecyclerImage;
    }

    public boolean isReponse_deja() {
        return reponse_deja;
    }

    public void setReponse_deja(boolean reponse_deja) {
        this.reponse_deja = reponse_deja;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getInvite_url() {
        return invite_url;
    }

    public void setInvite_url(String invite_url) {
        this.invite_url = invite_url;
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

    public String getReponse_url() {
        return reponse_url;
    }

    public void setReponse_url(String reponse_url) {
        this.reponse_url = reponse_url;
    }

    public String getReponse_profile_photo() {
        return reponse_profile_photo;
    }

    public void setReponse_profile_photo(String reponse_profile_photo) {
        this.reponse_profile_photo = reponse_profile_photo;
    }

    public String getReponse_username() {
        return reponse_username;
    }

    public void setReponse_username(String reponse_username) {
        this.reponse_username = reponse_username;
    }

    public String getReponse_user_id() {
        return reponse_user_id;
    }

    public void setReponse_user_id(String reponse_user_id) {
        this.reponse_user_id = reponse_user_id;
    }

    public String getReponse_photoID() {
        return reponse_photoID;
    }

    public void setReponse_photoID(String reponse_photoID) {
        this.reponse_photoID = reponse_photoID;
    }

    public String getReponse_caption() {
        return reponse_caption;
    }

    public void setReponse_caption(String reponse_caption) {
        this.reponse_caption = reponse_caption;
    }

    public String getReponse_tag() {
        return reponse_tag;
    }

    public void setReponse_tag(String reponse_tag) {
        this.reponse_tag = reponse_tag;
    }

    public String getReponse_country_name() {
        return reponse_country_name;
    }

    public void setReponse_country_name(String reponse_country_name) {
        this.reponse_country_name = reponse_country_name;
    }

    public String getReponse_countryID() {
        return reponse_countryID;
    }

    public void setReponse_countryID(String reponse_countryID) {
        this.reponse_countryID = reponse_countryID;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSharing_caption() {
        return sharing_caption;
    }

    public void setSharing_caption(String sharing_caption) {
        this.sharing_caption = sharing_caption;
    }

    public String getCaptionUn() {
        return captionUn;
    }

    public void setCaptionUn(String captionUn) {
        this.captionUn = captionUn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlUn() {
        return urlUn;
    }

    public void setUrlUn(String urlUn) {
        this.urlUn = urlUn;
    }

    public String getUrlDeux() {
        return urlDeux;
    }

    public void setUrlDeux(String urlDeux) {
        this.urlDeux = urlDeux;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public long getDate_shared() {
        return date_shared;
    }

    public void setDate_shared(long date_shared) {
        this.date_shared = date_shared;
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

    public String getPhoto_id_deux() {
        return photo_id_deux;
    }

    public void setPhoto_id_deux(String photo_id_deux) {
        this.photo_id_deux = photo_id_deux;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTagsUn() {
        return tagsUn;
    }

    public void setTagsUn(String tagsUn) {
        this.tagsUn = tagsUn;
    }

    public String getTagsDeux() {
        return tagsDeux;
    }

    public void setTagsDeux(String tagsDeux) {
        this.tagsDeux = tagsDeux;
    }

    public String getUser_profile_photo() {
        return user_profile_photo;
    }

    public void setUser_profile_photo(String user_profile_photo) {
        this.user_profile_photo = user_profile_photo;
    }

    public String getUser_full_profile_photo() {
        return user_full_profile_photo;
    }

    public void setUser_full_profile_photo(String user_full_profile_photo) {
        this.user_full_profile_photo = user_full_profile_photo;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Crush> getCrush() {
        return crush;
    }

    public void setCrush(List<Crush> crush) {
        this.crush = crush;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<MediaID> getMedia_id() {
        return media_id;
    }

    public void setMedia_id(List<MediaID> media_id) {
        this.media_id = media_id;
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

    public String getUrli() {
        return urli;
    }

    public void setUrli(String urli) {
        this.urli = urli;
    }

    public String getUrlii() {
        return urlii;
    }

    public void setUrlii(String urlii) {
        this.urlii = urlii;
    }

    public String getUrliii() {
        return urliii;
    }

    public void setUrliii(String urliii) {
        this.urliii = urliii;
    }

    public String getUrliv() {
        return urliv;
    }

    public void setUrliv(String urliv) {
        this.urliv = urliv;
    }

    public String getUrlv() {
        return urlv;
    }

    public void setUrlv(String urlv) {
        this.urlv = urlv;
    }

    public String getUrlvi() {
        return urlvi;
    }

    public void setUrlvi(String urlvi) {
        this.urlvi = urlvi;
    }

    public String getUrlvii() {
        return urlvii;
    }

    public void setUrlvii(String urlvii) {
        this.urlvii = urlvii;
    }

    public String getUrlviii() {
        return urlviii;
    }

    public void setUrlviii(String urlviii) {
        this.urlviii = urlviii;
    }

    public String getUrlix() {
        return urlix;
    }

    public void setUrlix(String urlix) {
        this.urlix = urlix;
    }

    public String getUrlx() {
        return urlx;
    }

    public void setUrlx(String urlx) {
        this.urlx = urlx;
    }

    public String getUrlxi() {
        return urlxi;
    }

    public void setUrlxi(String urlxi) {
        this.urlxi = urlxi;
    }

    public String getUrlxii() {
        return urlxii;
    }

    public void setUrlxii(String urlxii) {
        this.urlxii = urlxii;
    }

    public String getUrlxiii() {
        return urlxiii;
    }

    public void setUrlxiii(String urlxiii) {
        this.urlxiii = urlxiii;
    }

    public String getUrlxiv() {
        return urlxiv;
    }

    public void setUrlxiv(String urlxiv) {
        this.urlxiv = urlxiv;
    }

    public String getUrlxv() {
        return urlxv;
    }

    public void setUrlxv(String urlxv) {
        this.urlxv = urlxv;
    }

    public String getUrlxvi() {
        return urlxvi;
    }

    public void setUrlxvi(String urlxvi) {
        this.urlxvi = urlxvi;
    }

    public String getUrlxvii() {
        return urlxvii;
    }

    public void setUrlxvii(String urlxvii) {
        this.urlxvii = urlxvii;
    }

    public String getPhotoi_id() {
        return photoi_id;
    }

    public void setPhotoi_id(String photoi_id) {
        this.photoi_id = photoi_id;
    }

    public String getPhotoii_id() {
        return photoii_id;
    }

    public void setPhotoii_id(String photoii_id) {
        this.photoii_id = photoii_id;
    }

    public String getPhotoiii_id() {
        return photoiii_id;
    }

    public void setPhotoiii_id(String photoiii_id) {
        this.photoiii_id = photoiii_id;
    }

    public String getPhotoiv_id() {
        return photoiv_id;
    }

    public void setPhotoiv_id(String photoiv_id) {
        this.photoiv_id = photoiv_id;
    }

    public String getPhotov_id() {
        return photov_id;
    }

    public void setPhotov_id(String photov_id) {
        this.photov_id = photov_id;
    }

    public String getPhotovi_id() {
        return photovi_id;
    }

    public void setPhotovi_id(String photovi_id) {
        this.photovi_id = photovi_id;
    }

    public String getPhotovii_id() {
        return photovii_id;
    }

    public void setPhotovii_id(String photovii_id) {
        this.photovii_id = photovii_id;
    }

    public String getPhotoviii_id() {
        return photoviii_id;
    }

    public void setPhotoviii_id(String photoviii_id) {
        this.photoviii_id = photoviii_id;
    }

    public String getPhotoix_id() {
        return photoix_id;
    }

    public void setPhotoix_id(String photoix_id) {
        this.photoix_id = photoix_id;
    }

    public String getPhotox_id() {
        return photox_id;
    }

    public void setPhotox_id(String photox_id) {
        this.photox_id = photox_id;
    }

    public String getPhotoxi_id() {
        return photoxi_id;
    }

    public void setPhotoxi_id(String photoxi_id) {
        this.photoxi_id = photoxi_id;
    }

    public String getPhotoxii_id() {
        return photoxii_id;
    }

    public void setPhotoxii_id(String photoxii_id) {
        this.photoxii_id = photoxii_id;
    }

    public String getPhotoxiii_id() {
        return photoxiii_id;
    }

    public void setPhotoxiii_id(String photoxiii_id) {
        this.photoxiii_id = photoxiii_id;
    }

    public String getPhotoxiv_id() {
        return photoxiv_id;
    }

    public void setPhotoxiv_id(String photoxiv_id) {
        this.photoxiv_id = photoxiv_id;
    }

    public String getPhotoxv_id() {
        return photoxv_id;
    }

    public void setPhotoxv_id(String photoxv_id) {
        this.photoxv_id = photoxv_id;
    }

    public String getPhotoxvi_id() {
        return photoxvi_id;
    }

    public void setPhotoxvi_id(String photoxvi_id) {
        this.photoxvi_id = photoxvi_id;
    }

    public String getPhotoxvii_id() {
        return photoxvii_id;
    }

    public void setPhotoxvii_id(String photoxvii_id) {
        this.photoxvii_id = photoxvii_id;
    }

    public String getCaption_i() {
        return caption_i;
    }

    public void setCaption_i(String caption_i) {
        this.caption_i = caption_i;
    }

    public String getCaption_ii() {
        return caption_ii;
    }

    public void setCaption_ii(String caption_ii) {
        this.caption_ii = caption_ii;
    }

    public String getCaption_iii() {
        return caption_iii;
    }

    public void setCaption_iii(String caption_iii) {
        this.caption_iii = caption_iii;
    }

    public String getCaption_iv() {
        return caption_iv;
    }

    public void setCaption_iv(String caption_iv) {
        this.caption_iv = caption_iv;
    }

    public String getCaption_v() {
        return caption_v;
    }

    public void setCaption_v(String caption_v) {
        this.caption_v = caption_v;
    }

    public String getCaption_vi() {
        return caption_vi;
    }

    public void setCaption_vi(String caption_vi) {
        this.caption_vi = caption_vi;
    }

    public String getCaption_vii() {
        return caption_vii;
    }

    public void setCaption_vii(String caption_vii) {
        this.caption_vii = caption_vii;
    }

    public String getCaption_viii() {
        return caption_viii;
    }

    public void setCaption_viii(String caption_viii) {
        this.caption_viii = caption_viii;
    }

    public String getCaption_ix() {
        return caption_ix;
    }

    public void setCaption_ix(String caption_ix) {
        this.caption_ix = caption_ix;
    }

    public String getCaption_x() {
        return caption_x;
    }

    public void setCaption_x(String caption_x) {
        this.caption_x = caption_x;
    }

    public String getCaption_xi() {
        return caption_xi;
    }

    public void setCaption_xi(String caption_xi) {
        this.caption_xi = caption_xi;
    }

    public String getCaption_xii() {
        return caption_xii;
    }

    public void setCaption_xii(String caption_xii) {
        this.caption_xii = caption_xii;
    }

    public String getCaption_xiii() {
        return caption_xiii;
    }

    public void setCaption_xiii(String caption_xiii) {
        this.caption_xiii = caption_xiii;
    }

    public String getCaption_xiv() {
        return caption_xiv;
    }

    public void setCaption_xiv(String caption_xiv) {
        this.caption_xiv = caption_xiv;
    }

    public String getCaption_xv() {
        return caption_xv;
    }

    public void setCaption_xv(String caption_xv) {
        this.caption_xv = caption_xv;
    }

    public String getCaption_xvi() {
        return caption_xvi;
    }

    public void setCaption_xvi(String caption_xvi) {
        this.caption_xvi = caption_xvi;
    }

    public String getCaption_xvii() {
        return caption_xvii;
    }

    public void setCaption_xvii(String caption_xvii) {
        this.caption_xvii = caption_xvii;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail_invite() {
        return thumbnail_invite;
    }

    public void setThumbnail_invite(String thumbnail_invite) {
        this.thumbnail_invite = thumbnail_invite;
    }

    public String getThumbnail_response() {
        return thumbnail_response;
    }

    public void setThumbnail_response(String thumbnail_response) {
        this.thumbnail_response = thumbnail_response;
    }

    public String getThumbnail_un() {
        return thumbnail_un;
    }

    public void setThumbnail_un(String thumbnail_un) {
        this.thumbnail_un = thumbnail_un;
    }

    public String getThumbnail_deux() {
        return thumbnail_deux;
    }

    public void setThumbnail_deux(String thumbnail_deux) {
        this.thumbnail_deux = thumbnail_deux;
    }

    public boolean isRecyclerItem_shared() {
        return recyclerItem_shared;
    }

    public void setRecyclerItem_shared(boolean recyclerItem_shared) {
        this.recyclerItem_shared = recyclerItem_shared;
    }

    public boolean isImageUne_shared() {
        return imageUne_shared;
    }

    public void setImageUne_shared(boolean imageUne_shared) {
        this.imageUne_shared = imageUne_shared;
    }

    public boolean isVideoUne_shared() {
        return videoUne_shared;
    }

    public void setVideoUne_shared(boolean videoUne_shared) {
        this.videoUne_shared = videoUne_shared;
    }

    public boolean isImageDouble_shared() {
        return imageDouble_shared;
    }

    public void setImageDouble_shared(boolean imageDouble_shared) {
        this.imageDouble_shared = imageDouble_shared;
    }

    public boolean isVideoDouble_shared() {
        return videoDouble_shared;
    }

    public void setVideoDouble_shared(boolean videoDouble_shared) {
        this.videoDouble_shared = videoDouble_shared;
    }

    public boolean isReponseImageDouble_shared() {
        return reponseImageDouble_shared;
    }

    public void setReponseImageDouble_shared(boolean reponseImageDouble_shared) {
        this.reponseImageDouble_shared = reponseImageDouble_shared;
    }

    public boolean isReponseVideoDouble_shared() {
        return reponseVideoDouble_shared;
    }

    public void setReponseVideoDouble_shared(boolean reponseVideoDouble_shared) {
        this.reponseVideoDouble_shared = reponseVideoDouble_shared;
    }

    public boolean isUser_profile() {
        return user_profile;
    }

    public void setUser_profile(boolean user_profile) {
        this.user_profile = user_profile;
    }

    public boolean isGroup_share_single_bottom_circle_image() {
        return group_share_single_bottom_circle_image;
    }

    public void setGroup_share_single_bottom_circle_image(boolean group_share_single_bottom_circle_image) {
        this.group_share_single_bottom_circle_image = group_share_single_bottom_circle_image;
    }

    public boolean isGroup_share_top_bottom_circle_image() {
        return group_share_top_bottom_circle_image;
    }

    public void setGroup_share_top_bottom_circle_image(boolean group_share_top_bottom_circle_image) {
        this.group_share_top_bottom_circle_image = group_share_top_bottom_circle_image;
    }

    public boolean isGroup_share_single_top_circle_image() {
        return group_share_single_top_circle_image;
    }

    public void setGroup_share_single_top_circle_image(boolean group_share_single_top_circle_image) {
        this.group_share_single_top_circle_image = group_share_single_top_circle_image;
    }

    public boolean isUser_profile_shared() {
        return user_profile_shared;
    }

    public void setUser_profile_shared(boolean user_profile_shared) {
        this.user_profile_shared = user_profile_shared;
    }

    public String getShared_group_id() {
        return shared_group_id;
    }

    public void setShared_group_id(String shared_group_id) {
        this.shared_group_id = shared_group_id;
    }

    public String getUser_id_sharing() {
        return user_id_sharing;
    }

    public void setUser_id_sharing(String user_id_sharing) {
        this.user_id_sharing = user_id_sharing;
    }

    public String getSharing_admin_master() {
        return sharing_admin_master;
    }

    public void setSharing_admin_master(String sharing_admin_master) {
        this.sharing_admin_master = sharing_admin_master;
    }

    public boolean isComment_text() {
        return comment_text;
    }

    public void setComment_text(boolean comment_text) {
        this.comment_text = comment_text;
    }

    public String getComment_subject() {
        return comment_subject;
    }

    public void setComment_subject(String comment_subject) {
        this.comment_subject = comment_subject;
    }

    public String getComment_theme() {
        return comment_theme;
    }

    public void setComment_theme(String comment_theme) {
        this.comment_theme = comment_theme;
    }

    public boolean isBig_image() {
        return big_image;
    }

    public void setBig_image(boolean big_image) {
        this.big_image = big_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getAuth_user_id() {
        return auth_user_id;
    }

    public void setAuth_user_id(String auth_user_id) {
        this.auth_user_id = auth_user_id;
    }

    public String getUser_id_shared() {
        return user_id_shared;
    }

    public void setUser_id_shared(String user_id_shared) {
        this.user_id_shared = user_id_shared;
    }

    public String getPost_identity() {
        return post_identity;
    }

    public void setPost_identity(String post_identity) {
        this.post_identity = post_identity;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (suppressed ? 1 : 0));
        parcel.writeByte((byte) (recycler_story ? 1 : 0));
        parcel.writeByte((byte) (friends_suggestion_one ? 1 : 0));
        parcel.writeByte((byte) (friends_suggestion_two ? 1 : 0));
        parcel.writeByte((byte) (friends_suggestion_three ? 1 : 0));
        parcel.writeByte((byte) (friends_suggestion_four ? 1 : 0));
        parcel.writeByte((byte) (friends_suggestion_five ? 1 : 0));
        parcel.writeByte((byte) (groups_suggestion_one ? 1 : 0));
        parcel.writeByte((byte) (groups_suggestion_two ? 1 : 0));
        parcel.writeByte((byte) (groups_suggestion_three ? 1 : 0));
        parcel.writeByte((byte) (groups_suggestion_four ? 1 : 0));
        parcel.writeByte((byte) (groups_suggestion_five ? 1 : 0));
        parcel.writeByte((byte) (videos_suggestion_one ? 1 : 0));
        parcel.writeByte((byte) (videos_suggestion_two ? 1 : 0));
        parcel.writeByte((byte) (videos_suggestion_three ? 1 : 0));
        parcel.writeByte((byte) (videos_suggestion_four ? 1 : 0));
        parcel.writeByte((byte) (videos_suggestion_five ? 1 : 0));
        parcel.writeByte((byte) (everyone_can_answer_this_challenge ? 1 : 0));
        parcel.writeByte((byte) (recyclerItem ? 1 : 0));
        parcel.writeByte((byte) (imageUne ? 1 : 0));
        parcel.writeByte((byte) (videoUne ? 1 : 0));
        parcel.writeByte((byte) (imageDouble ? 1 : 0));
        parcel.writeByte((byte) (videoDouble ? 1 : 0));
        parcel.writeByte((byte) (reponseImageDouble ? 1 : 0));
        parcel.writeByte((byte) (reponseVideoDouble ? 1 : 0));
        parcel.writeByte((byte) (group ? 1 : 0));
        parcel.writeByte((byte) (group_private ? 1 : 0));
        parcel.writeByte((byte) (group_public ? 1 : 0));
        parcel.writeByte((byte) (group_cover_profile_photo ? 1 : 0));
        parcel.writeByte((byte) (group_cover_background_profile_photo ? 1 : 0));
        parcel.writeByte((byte) (group_recyclerItem ? 1 : 0));
        parcel.writeByte((byte) (group_imageUne ? 1 : 0));
        parcel.writeByte((byte) (group_videoUne ? 1 : 0));
        parcel.writeByte((byte) (group_imageDouble ? 1 : 0));
        parcel.writeByte((byte) (group_videoDouble ? 1 : 0));
        parcel.writeByte((byte) (group_response_imageDouble ? 1 : 0));
        parcel.writeByte((byte) (group_response_videoDouble ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_image_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_image_une ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_recycler ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_response_image_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_response_video_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_video_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_video_une ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_image_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_image_une ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_recycler ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_response_image_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_response_video_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_video_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_video_une ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_image_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_image_une ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_recycler ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_response_image_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_response_video_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_video_double ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_video_une ? 1 : 0));
        parcel.writeByte((byte) (user_profile ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_bottom_circle_image ? 1 : 0));
        parcel.writeByte((byte) (group_share_single_top_circle_image ? 1 : 0));
        parcel.writeByte((byte) (group_share_top_bottom_circle_image ? 1 : 0));
        parcel.writeByte((byte) (user_profile_shared ? 1 : 0));
        parcel.writeString(admin_master);
        parcel.writeString(group_id);
        parcel.writeString(publisher);
        parcel.writeString(group_profile_photo);
        parcel.writeString(group_full_profile_photo);
        parcel.writeString(group_user_background_profile_url);
        parcel.writeString(group_user_background_full_profile_url);
        parcel.writeByte((byte) (gridRecyclerImage ? 1 : 0));
        parcel.writeByte((byte) (reponse_deja ? 1 : 0));
        parcel.writeString(details);
        parcel.writeString(invite_url);
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
        parcel.writeString(reponse_url);
        parcel.writeString(reponse_profile_photo);
        parcel.writeString(reponse_username);
        parcel.writeString(reponse_user_id);
        parcel.writeString(reponse_photoID);
        parcel.writeString(reponse_caption);
        parcel.writeString(reponse_tag);
        parcel.writeString(reponse_country_name);
        parcel.writeString(reponse_countryID);
        parcel.writeString(caption);
        parcel.writeString(sharing_caption);
        parcel.writeString(captionUn);
        parcel.writeString(url);
        parcel.writeString(urlUn);
        parcel.writeString(urlDeux);
        parcel.writeLong(date_created);
        parcel.writeLong(date_shared);
        parcel.writeString(photo_id);
        parcel.writeString(photo_id_un);
        parcel.writeString(photo_id_deux);
        parcel.writeString(user_id);
        parcel.writeString(tags);
        parcel.writeString(tagsUn);
        parcel.writeString(tagsDeux);
        parcel.writeString(user_profile_photo);
        parcel.writeString(user_full_profile_photo);
        parcel.writeString(country_name);
        parcel.writeString(countryID);
        parcel.writeString(urli);
        parcel.writeString(urlii);
        parcel.writeString(urliii);
        parcel.writeString(urliv);
        parcel.writeString(urlv);
        parcel.writeString(urlvi);
        parcel.writeString(urlvii);
        parcel.writeString(urlviii);
        parcel.writeString(urlix);
        parcel.writeString(urlx);
        parcel.writeString(urlxi);
        parcel.writeString(urlxii);
        parcel.writeString(urlxiii);
        parcel.writeString(urlxiv);
        parcel.writeString(urlxv);
        parcel.writeString(urlxvi);
        parcel.writeString(urlxvii);
        parcel.writeString(photoi_id);
        parcel.writeString(photoii_id);
        parcel.writeString(photoiii_id);
        parcel.writeString(photoiv_id);
        parcel.writeString(photov_id);
        parcel.writeString(photovi_id);
        parcel.writeString(photovii_id);
        parcel.writeString(photoviii_id);
        parcel.writeString(photoix_id);
        parcel.writeString(photox_id);
        parcel.writeString(photoxi_id);
        parcel.writeString(photoxii_id);
        parcel.writeString(photoxiii_id);
        parcel.writeString(photoxiv_id);
        parcel.writeString(photoxv_id);
        parcel.writeString(photoxvi_id);
        parcel.writeString(photoxvii_id);
        parcel.writeString(caption_i);
        parcel.writeString(caption_ii);
        parcel.writeString(caption_iii);
        parcel.writeString(caption_iv);
        parcel.writeString(caption_v);
        parcel.writeString(caption_vi);
        parcel.writeString(caption_vii);
        parcel.writeString(caption_viii);
        parcel.writeString(caption_ix);
        parcel.writeString(caption_x);
        parcel.writeString(caption_xi);
        parcel.writeString(caption_xii);
        parcel.writeString(caption_xiii);
        parcel.writeString(caption_xiv);
        parcel.writeString(caption_xv);
        parcel.writeString(caption_xvi);
        parcel.writeString(caption_xvii);
        parcel.writeString(thumbnail);
        parcel.writeString(thumbnail_invite);
        parcel.writeString(thumbnail_response);
        parcel.writeString(thumbnail_un);
        parcel.writeString(thumbnail_deux);
        parcel.writeByte((byte) (recyclerItem_shared ? 1 : 0));
        parcel.writeByte((byte) (imageUne_shared ? 1 : 0));
        parcel.writeByte((byte) (videoUne_shared ? 1 : 0));
        parcel.writeByte((byte) (imageDouble_shared ? 1 : 0));
        parcel.writeByte((byte) (videoDouble_shared ? 1 : 0));
        parcel.writeByte((byte) (reponseImageDouble_shared ? 1 : 0));
        parcel.writeByte((byte) (reponseVideoDouble_shared ? 1 : 0));
        parcel.writeString(user_id_sharing);
        parcel.writeString(shared_group_id);
        parcel.writeString(sharing_admin_master);
        parcel.writeByte((byte) (comment_text ? 1 : 0));
        parcel.writeString(comment_subject);
        parcel.writeString(comment_theme);
        parcel.writeByte((byte) (big_image ? 1 : 0));
        parcel.writeString(username);
        parcel.writeString(profileUrl);
        parcel.writeString(auth_user_id);
        parcel.writeString(user_id_shared);
        parcel.writeString(post_identity);
        parcel.writeLong(views);
    }
}
