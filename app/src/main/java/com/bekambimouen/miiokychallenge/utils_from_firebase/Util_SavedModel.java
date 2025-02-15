package com.bekambimouen.miiokychallenge.utils_from_firebase;

import com.bekambimouen.miiokychallenge.model.BattleModel;

import java.util.Date;

public class Util_SavedModel {
    public static BattleModel getBattleModel(BattleModel model, String user_id, String username, String profilUrl) {
        BattleModel battleModel = new BattleModel();

        battleModel.setRecycler_story(false);
        battleModel.setFriends_suggestion_one(false);
        battleModel.setFriends_suggestion_two(false);
        battleModel.setFriends_suggestion_three(false);
        battleModel.setFriends_suggestion_four(false);
        battleModel.setFriends_suggestion_five(false);
        battleModel.setGroups_suggestion_one(false);
        battleModel.setGroups_suggestion_two(false);
        battleModel.setGroups_suggestion_three(false);
        battleModel.setGroups_suggestion_four(false);
        battleModel.setGroups_suggestion_five(false);
        battleModel.setVideos_suggestion_one(false);
        battleModel.setVideos_suggestion_two(false);
        battleModel.setVideos_suggestion_three(false);
        battleModel.setVideos_suggestion_four(false);
        battleModel.setVideos_suggestion_five(false);
        battleModel.setEveryone_can_answer_this_challenge(false);

        battleModel.setRecyclerItem(model.isRecyclerItem());
        battleModel.setImageUne(model.isImageUne());
        battleModel.setVideoUne(model.isVideoUne());
        battleModel.setImageDouble(model.isImageDouble());
        battleModel.setVideoDouble(model.isVideoDouble());
        battleModel.setReponseImageDouble(model.isReponseImageDouble());
        battleModel.setReponseVideoDouble(model.isReponseVideoDouble());
        // group
        battleModel.setGroup(model.isGroup());
        battleModel.setGroup_private(model.isGroup_private());
        battleModel.setGroup_public(model.isGroup_public());
        battleModel.setGroup_cover_profile_photo(model.isGroup_cover_profile_photo());
        battleModel.setGroup_cover_background_profile_photo(model.isGroup_cover_background_profile_photo());
        battleModel.setGroup_recyclerItem(model.isGroup_recyclerItem());
        battleModel.setGroup_imageUne(model.isGroup_imageUne());
        battleModel.setGroup_videoUne(model.isGroup_videoUne());
        battleModel.setGroup_imageDouble(model.isGroup_imageDouble());
        battleModel.setGroup_videoDouble(model.isGroup_videoDouble());
        battleModel.setGroup_response_imageDouble(model.isGroup_response_imageDouble());
        battleModel.setGroup_response_videoDouble(model.isGroup_response_videoDouble());
        battleModel.setGroup_share_single_bottom_circle_image(model.isGroup_share_single_bottom_circle_image());
        battleModel.setGroup_share_single_bottom_image_double(model.isGroup_share_single_bottom_image_double());
        battleModel.setGroup_share_single_bottom_image_une(model.isGroup_share_single_bottom_image_une());
        battleModel.setGroup_share_single_bottom_recycler(model.isGroup_share_single_bottom_recycler());
        battleModel.setGroup_share_single_bottom_response_image_double(model.isGroup_share_single_bottom_response_image_double());
        battleModel.setGroup_share_single_bottom_response_video_double(model.isGroup_share_single_bottom_response_video_double());
        battleModel.setGroup_share_single_bottom_video_double(model.isGroup_share_single_bottom_video_double());
        battleModel.setGroup_share_single_bottom_video_une(model.isGroup_share_single_bottom_video_une());
        battleModel.setGroup_share_top_bottom_circle_image(model.isGroup_share_top_bottom_circle_image());
        battleModel.setGroup_share_top_bottom_image_double(model.isGroup_share_top_bottom_image_double());
        battleModel.setGroup_share_top_bottom_image_une(model.isGroup_share_top_bottom_image_une());
        battleModel.setGroup_share_top_bottom_recycler(model.isGroup_share_top_bottom_recycler());
        battleModel.setGroup_share_top_bottom_response_image_double(model.isGroup_share_top_bottom_response_image_double());
        battleModel.setGroup_share_top_bottom_response_video_double(model.isGroup_share_top_bottom_response_video_double());
        battleModel.setGroup_share_top_bottom_video_double(model.isGroup_share_top_bottom_video_double());
        battleModel.setGroup_share_top_bottom_video_une(model.isGroup_share_top_bottom_video_une());
        battleModel.setGroup_share_single_top_circle_image(model.isGroup_share_single_top_circle_image());
        battleModel.setGroup_share_single_top_image_double(model.isGroup_share_single_top_image_double());
        battleModel.setGroup_share_single_top_image_une(model.isGroup_share_single_top_image_une());
        battleModel.setGroup_share_single_top_recycler(model.isGroup_share_single_top_recycler());
        battleModel.setGroup_share_single_top_response_image_double(model.isGroup_share_single_top_response_image_double());
        battleModel.setGroup_share_single_top_response_video_double(model.isGroup_share_single_top_response_video_double());
        battleModel.setGroup_share_single_top_video_double(model.isGroup_share_single_top_video_double());
        battleModel.setGroup_share_single_top_video_une(model.isGroup_share_single_top_video_une());
        battleModel.setGroup_id(model.getGroup_id());
        battleModel.setAdmin_master(model.getAdmin_master());
        battleModel.setPublisher(model.getPublisher());
        battleModel.setGroup_profile_photo(model.getGroup_profile_photo());
        battleModel.setGroup_full_profile_photo(model.getGroup_full_profile_photo());
        battleModel.setUser_id_sharing(model.getUser_id_sharing());
        battleModel.setGroup_user_background_profile_url(model.getGroup_user_background_profile_url());
        battleModel.setGroup_user_background_full_profile_url(model.getGroup_user_background_full_profile_url());
        battleModel.setShared_group_id(model.getShared_group_id());
        battleModel.setSharing_admin_master(model.getSharing_admin_master());
        // pour le grid saved
        battleModel.setGridRecyclerImage(model.isGridRecyclerImage());
        battleModel.setReponse_deja(model.isReponse_deja());
        battleModel.setDetails(model.getDetails());

        // challenge
        battleModel.setInvite_url(model.getInvite_url());
        battleModel.setInvite_photoID(model.getInvite_photoID());
        battleModel.setInvite_profile_photo(model.getInvite_profile_photo());
        battleModel.setInvite_username(model.getInvite_username());
        battleModel.setInvite_userID(model.getInvite_userID());
        battleModel.setInvite_caption(model.getInvite_caption());
        battleModel.setInvite_tag(model.getInvite_tag());
        battleModel.setInvite_category(model.getInvite_category());
        battleModel.setInvite_category_status(model.getInvite_category_status());
        battleModel.setInvite_country_name(model.getInvite_country_name());
        battleModel.setInvite_countryID(model.getInvite_countryID());

        battleModel.setPost_identity(model.getPost_identity());
        battleModel.setReponse_url(model.getReponse_url());
        battleModel.setReponse_profile_photo(model.getReponse_profile_photo());
        battleModel.setReponse_username(model.getReponse_username());
        battleModel.setReponse_user_id(model.getReponse_user_id());
        battleModel.setReponse_photoID(model.getReponse_photoID());
        battleModel.setReponse_caption(model.getReponse_caption());
        battleModel.setReponse_tag(model.getReponse_tag());
        battleModel.setReponse_country_name(model.getReponse_country_name());
        battleModel.setReponse_countryID(model.getReponse_countryID());
        battleModel.setCaption(model.getCaption());
        battleModel.setSharing_caption(model.getSharing_caption());
        battleModel.setCaptionUn(model.getCaptionUn());
        battleModel.setUrl(model.getUrl());
        battleModel.setUrlUn(model.getUrlUn());
        battleModel.setUrlDeux(model.getUrlDeux());
        Date date = new Date();
        battleModel.setDate_created(date.getTime());
        battleModel.setDate_shared(model.getDate_shared());
        battleModel.setViews(model.getViews());
        battleModel.setPhoto_id(model.getPhoto_id());
        battleModel.setPhoto_id_un(model.getPhoto_id_un());
        battleModel.setPhoto_id_deux(model.getPhoto_id_deux());
        battleModel.setUser_id(model.getUser_id());
        battleModel.setTags(model.getTags());
        battleModel.setTagsUn(model.getTagsUn());
        battleModel.setTagsDeux(model.getTagsDeux());
        battleModel.setUser_profile_photo(model.getUser_profile_photo());
        battleModel.setUser_full_profile_photo(model.getUser_full_profile_photo());
        battleModel.setCountry_name(model.getCountry_name());
        battleModel.setCountryID(model.getCountryID());

        battleModel.setUrli(model.getUrli());
        battleModel.setUrlii(model.getUrlii());
        battleModel.setUrliii(model.getUrliii());
        battleModel.setUrliv(model.getUrliv());
        battleModel.setUrlv(model.getUrlv());
        battleModel.setUrlvi(model.getUrlvi());
        battleModel.setUrlvii(model.getUrlvii());
        battleModel.setUrlviii(model.getUrlviii());
        battleModel.setUrlix(model.getUrlix());
        battleModel.setUrlx(model.getUrlx());
        battleModel.setUrlxi(model.getUrlxi());
        battleModel.setUrlxii(model.getUrlxii());
        battleModel.setUrlxiii(model.getUrlxiii());
        battleModel.setUrlxiv(model.getUrlxiv());
        battleModel.setUrlxv(model.getUrlxv());
        battleModel.setUrlxvi(model.getUrlxvi());
        battleModel.setUrlxvii(model.getUrlxvii());

        battleModel.setPhotoi_id(model.getPhotoi_id());
        battleModel.setPhotoii_id(model.getPhotoii_id());
        battleModel.setPhotoiii_id(model.getPhotoiii_id());
        battleModel.setPhotoiv_id(model.getPhotoiv_id());
        battleModel.setPhotov_id(model.getPhotov_id());
        battleModel.setPhotovi_id(model.getPhotovi_id());
        battleModel.setPhotovii_id(model.getPhotovii_id());
        battleModel.setPhotoviii_id(model.getPhotoviii_id());
        battleModel.setPhotoix_id(model.getPhotoix_id());
        battleModel.setPhotox_id(model.getPhotox_id());
        battleModel.setPhotoxi_id(model.getPhotoxi_id());
        battleModel.setPhotoxii_id(model.getPhotoxii_id());
        battleModel.setPhotoxiii_id(model.getPhotoxiii_id());
        battleModel.setPhotoxiv_id(model.getPhotoxiv_id());
        battleModel.setPhotoxv_id(model.getPhotoxv_id());
        battleModel.setPhotoxvi_id(model.getPhotoxvi_id());
        battleModel.setPhotoxvii_id(model.getPhotoxvii_id());

        battleModel.setCaption_i(model.getCaption_i());
        battleModel.setCaption_ii(model.getCaption_ii());
        battleModel.setCaption_iii(model.getCaption_iii());
        battleModel.setCaption_iv(model.getCaption_iv());
        battleModel.setCaption_v(model.getCaption_v());
        battleModel.setCaption_vi(model.getCaption_vi());
        battleModel.setCaption_vii(model.getCaption_vii());
        battleModel.setCaption_viii(model.getCaption_viii());
        battleModel.setCaption_ix(model.getCaption_ix());
        battleModel.setCaption_x(model.getCaption_x());
        battleModel.setCaption_xi(model.getCaption_xi());
        battleModel.setCaption_xii(model.getCaption_xii());
        battleModel.setCaption_xiii(model.getCaption_xiii());
        battleModel.setCaption_xiv(model.getCaption_xiv());
        battleModel.setCaption_xv(model.getCaption_xv());
        battleModel.setCaption_xvi(model.getCaption_xvi());
        battleModel.setCaption_xvii(model.getCaption_xvii());

        battleModel.setThumbnail_invite(model.getThumbnail_invite());
        battleModel.setThumbnail_response(model.getThumbnail_response());
        battleModel.setThumbnail_un(model.getThumbnail_un());
        battleModel.setThumbnail_deux(model.getThumbnail_deux());
        battleModel.setThumbnail(model.getThumbnail());
        // shared
        battleModel.setRecyclerItem_shared(model.isRecyclerItem_shared());
        battleModel.setImageUne_shared(model.isImageUne_shared());
        battleModel.setVideoUne_shared(model.isVideoUne_shared());
        battleModel.setImageDouble_shared(model.isImageDouble_shared());
        battleModel.setVideoDouble_shared(model.isVideoDouble_shared());
        battleModel.setReponseImageDouble_shared(model.isReponseImageDouble_shared());
        battleModel.setReponseVideoDouble_shared(model.isReponseVideoDouble_shared());
        battleModel.setUser_profile_shared(model.isUser_profile_shared());
        battleModel.setUser_profile(model.isUser_profile());
        // comment text
        battleModel.setComment_text(model.isComment_text());
        battleModel.setComment_subject(model.getComment_subject());
        battleModel.setComment_theme(model.getComment_theme());
        battleModel.setBig_image(model.isBig_image());

        // publication saved
        battleModel.setUsername(username);
        battleModel.setProfileUrl(profilUrl);
        battleModel.setAuth_user_id(user_id);
        battleModel.setUser_id_shared(model.getUser_id_shared());

        return battleModel;
    }
}

