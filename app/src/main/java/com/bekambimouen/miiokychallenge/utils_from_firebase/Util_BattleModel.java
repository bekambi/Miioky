package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.MediaID;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util_BattleModel {
    public static BattleModel getBattleModel(AppCompatActivity context, Map<Object, Object> objectMap, DataSnapshot ds) {
        BattleModel model = new BattleModel();

        model.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));
        model.setRecycler_story(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_recycler_story))).toString()));
        model.setFriends_suggestion_one(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_friends_suggestion_one))).toString()));
        model.setFriends_suggestion_two(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_friends_suggestion_two))).toString()));
        model.setFriends_suggestion_three(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_friends_suggestion_three))).toString()));
        model.setFriends_suggestion_four(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_friends_suggestion_four))).toString()));
        model.setFriends_suggestion_five(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_friends_suggestion_five))).toString()));
        model.setGroups_suggestion_one(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_groups_suggestion_one))).toString()));
        model.setGroups_suggestion_two(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_groups_suggestion_two))).toString()));
        model.setGroups_suggestion_three(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_groups_suggestion_three))).toString()));
        model.setGroups_suggestion_four(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_groups_suggestion_four))).toString()));
        model.setGroups_suggestion_five(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_groups_suggestion_five))).toString()));
        model.setVideos_suggestion_one(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videos_suggestion_one))).toString()));
        model.setVideos_suggestion_two(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videos_suggestion_two))).toString()));
        model.setVideos_suggestion_three(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videos_suggestion_three))).toString()));
        model.setVideos_suggestion_four(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videos_suggestion_four))).toString()));
        model.setVideos_suggestion_five(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videos_suggestion_five))).toString()));
        model.setEveryone_can_answer_this_challenge(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_everyone_can_answer_this_challenge))).toString()));

        model.setRecyclerItem(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_recyclerItem))).toString()));
        model.setImageUne(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_imageUne))).toString()));
        model.setVideoUne(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videoUne))).toString()));
        model.setImageDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_imageDouble))).toString()));
        model.setVideoDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videoDouble))).toString()));
        model.setReponseImageDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponseImageDouble))).toString()));
        model.setReponseVideoDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponseVideoDouble))).toString()));

        // group
        model.setGroup(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group))).toString()));
        model.setGroup_private(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_private))).toString()));
        model.setGroup_public(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_public))).toString()));
        model.setGroup_cover_profile_photo(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_cover_profile_photo))).toString()));
        model.setGroup_cover_background_profile_photo(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_cover_background_profile_photo))).toString()));
        model.setGroup_recyclerItem(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_recyclerItem))).toString()));
        model.setGroup_imageUne(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_imageUne))).toString()));
        model.setGroup_videoUne(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_videoUne))).toString()));
        model.setGroup_imageDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_imageDouble))).toString()));
        model.setGroup_videoDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_videoDouble))).toString()));
        model.setGroup_response_imageDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_response_imageDouble))).toString()));
        model.setGroup_response_videoDouble(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_response_videoDouble))).toString()));

        model.setGroup_share_single_bottom_circle_image(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_circle_image))).toString()));
        model.setGroup_share_single_bottom_image_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_image_double))).toString()));
        model.setGroup_share_single_bottom_image_une(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_image_une))).toString()));
        model.setGroup_share_single_bottom_recycler(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_recycler))).toString()));
        model.setGroup_share_single_bottom_response_image_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_response_image_double))).toString()));
        model.setGroup_share_single_bottom_response_video_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_response_video_double))).toString()));
        model.setGroup_share_single_bottom_video_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_video_double))).toString()));
        model.setGroup_share_single_bottom_video_une(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_bottom_video_une))).toString()));

        model.setGroup_share_top_bottom_circle_image(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_circle_image))).toString()));
        model.setGroup_share_top_bottom_image_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_image_double))).toString()));
        model.setGroup_share_top_bottom_image_une(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_image_une))).toString()));
        model.setGroup_share_top_bottom_recycler(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_recycler))).toString()));
        model.setGroup_share_top_bottom_response_image_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_response_image_double))).toString()));
        model.setGroup_share_top_bottom_response_video_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_response_video_double))).toString()));
        model.setGroup_share_top_bottom_video_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_video_double))).toString()));
        model.setGroup_share_top_bottom_video_une(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_top_bottom_video_une))).toString()));

        model.setGroup_share_single_top_circle_image(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_circle_image))).toString()));
        model.setGroup_share_single_top_image_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_image_double))).toString()));
        model.setGroup_share_single_top_image_une(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_image_une))).toString()));
        model.setGroup_share_single_top_recycler(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_recycler))).toString()));
        model.setGroup_share_single_top_response_image_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_response_image_double))).toString()));
        model.setGroup_share_single_top_response_video_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_response_video_double))).toString()));
        model.setGroup_share_single_top_video_double(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_video_double))).toString()));
        model.setGroup_share_single_top_video_une(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_share_single_top_video_une))).toString()));

        model.setGroup_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_id))).toString());
        model.setAdmin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString());
        model.setPublisher(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_publisher))).toString());
        model.setGroup_profile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_profile_photo))).toString());
        model.setGroup_full_profile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_full_profile_photo))).toString());
        model.setUser_id_sharing(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id_sharing))).toString());
        model.setGroup_user_background_profile_url(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_user_background_profile_url))).toString());
        model.setGroup_user_background_full_profile_url(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_user_background_full_profile_url))).toString());
        model.setShared_group_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_shared_group_id))).toString());
        model.setSharing_admin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_sharing_admin_master))).toString());

        // pour le Profile Grid
        model.setGridRecyclerImage(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_gridRecyclerImage))).toString()));
        model.setReponse_deja(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_deja))).toString()));
        model.setDetails(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_details))).toString());

        // invite
        model.setInvite_url(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_url))).toString());
        model.setInvite_photoID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_photoID))).toString());
        model.setInvite_profile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_profile_photo))).toString());
        model.setInvite_username(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_username))).toString());
        model.setInvite_userID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_userID))).toString());
        model.setInvite_caption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_caption))).toString());
        model.setInvite_tag(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_tag))).toString());
        model.setInvite_category(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_category))).toString());
        model.setInvite_category_status(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_category_status))).toString());
        model.setInvite_country_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_country_name))).toString());
        model.setInvite_countryID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_countryID))).toString());

        model.setReponse_url(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_url))).toString());
        model.setReponse_profile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_profile_photo))).toString());
        model.setReponse_username(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_username))).toString());
        model.setReponse_user_id(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_user_id))).toString()));
        model.setReponse_photoID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_photoID))).toString());
        model.setReponse_caption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_caption))).toString());
        model.setReponse_tag(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_tag))).toString());
        model.setReponse_country_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_country_name))).toString());
        model.setReponse_countryID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponse_countryID))).toString());

        model.setPost_identity(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_identity))).toString());
        model.setCaption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption))).toString());
        model.setSharing_caption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_sharing_caption))).toString());
        model.setCaptionUn(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_captionUn))).toString()));
        model.setUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_url))).toString());
        model.setUrlUn(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlUn))).toString());
        model.setUrlDeux(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlDeux))).toString());
        model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        model.setDate_shared(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_shared))).toString()));
        model.setPhoto_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id))).toString());
        model.setPhoto_id_un(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id_un))).toString());
        model.setPhoto_id_deux(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id_deux))).toString());
        model.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        model.setTags(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_tags))).toString());
        model.setTagsUn(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_tagsUn))).toString());
//        model.setTagsDeux(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_tagsDeux))).toString());
        model.setUser_profile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_profile_photo))).toString());
        model.setUser_full_profile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_full_profile_photo))).toString());
        model.setCountry_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_country_name))).toString());
        model.setCountryID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_countryID))).toString());

        model.setUrli(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urli))).toString());
        model.setUrlii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlii))).toString());
        model.setUrliii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urliii))).toString());
        model.setUrliv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urliv))).toString());
        model.setUrlv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlv))).toString());
        model.setUrlvi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlvi))).toString());
        model.setUrlvii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlvii))).toString());
        model.setUrlviii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlviii))).toString());
        model.setUrlix(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlix))).toString());
        model.setUrlx(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlx))).toString());
        model.setUrlxi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxi))).toString());
        model.setUrlxii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxii))).toString());
        model.setUrlxiii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxiii))).toString());
        model.setUrlxiv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxiv))).toString());
        model.setUrlxv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxv))).toString());
        model.setUrlxvi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxvi))).toString());
        model.setUrlxvii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxvii))).toString());

        model.setPhotoi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoi_id))).toString());
        model.setPhotoii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoii_id))).toString());
        model.setPhotoiii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoiii_id))).toString());
        model.setPhotoiv_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoiv_id))).toString());
        model.setPhotov_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photov_id))).toString());
        model.setPhotovi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photovi_id))).toString());
        model.setPhotovii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photovii_id))).toString());
        model.setPhotoviii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoviii_id))).toString());
        model.setPhotoix_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoix_id))).toString());
        model.setPhotox_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photox_id))).toString());
        model.setPhotoxi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxi_id))).toString());
        model.setPhotoxii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxii_id))).toString());
        model.setPhotoxiii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxiii_id))).toString());
        model.setPhotoxiv_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxiv_id))).toString());
        model.setPhotoxv_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxv_id))).toString());
        model.setPhotoxvi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxvi_id))).toString());
        model.setPhotoxvii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxvii_id))).toString());

        model.setCaption_i(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_i))).toString());
        model.setCaption_ii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_ii))).toString());
        model.setCaption_iii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_iii))).toString());
        model.setCaption_iv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_iv))).toString());
        model.setCaption_v(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_v))).toString());
        model.setCaption_vi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_vi))).toString());
        model.setCaption_vii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_vii))).toString());
        model.setCaption_viii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_viii))).toString());
        model.setCaption_ix(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_ix))).toString());
        model.setCaption_x(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_x))).toString());
        model.setCaption_xi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xi))).toString());
        model.setCaption_xii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xii))).toString());
        model.setCaption_xiii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xiii))).toString());
        model.setCaption_xiv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xiv))).toString());
        model.setCaption_xv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xv))).toString());
        model.setCaption_xvi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xvi))).toString());
        model.setCaption_xvii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption_xvii))).toString());

        model.setThumbnail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail))).toString());
        model.setThumbnail_invite(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail_invite))).toString());
        model.setThumbnail_response(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail_response))).toString());
        model.setThumbnail_un(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail_un))).toString());
        model.setThumbnail_deux(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail_deux))).toString());
        //shared
        model.setRecyclerItem_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_recyclerItem_shared))).toString()));
        model.setImageUne_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_imageUne_shared))).toString()));
        model.setVideoUne_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videoUne_shared))).toString()));
        model.setImageDouble_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_imageDouble_shared))).toString()));
        model.setVideoDouble_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_videoDouble_shared))).toString()));
        model.setReponseImageDouble_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponseImageDouble_shared))).toString()));
        model.setReponseVideoDouble_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_reponseVideoDouble_shared))).toString()));
        model.setUser_profile_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_profile_shared))).toString()));
        model.setUser_profile(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_profile))).toString()));
        // comment text
        model.setComment_text(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_text))).toString()));
        model.setComment_subject(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_subject))).toString());
        model.setComment_theme(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_theme))).toString());
        // big image
        model.setBig_image(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_big_image))).toString()));
        // for saved
        model.setUsername(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_username))).toString());
        model.setProfileUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profileUrl))).toString());
        model.setAuth_user_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_auth_user_id))).toString());
        model.setUser_id_shared(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id_shared))).toString());
        model.setViews(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_views))).toString()));

        List<Comment> commentsList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_comments)).getChildren()){
            Comment comment = new Comment();
            Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

            assert objectHashMap != null;
            comment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
            comment.setComment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_comment))).toString());
            comment.setCommentKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_commentKey))).toString());
            comment.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));
            comment.setUrl(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_url))).toString());
            comment.setThumbnail(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_thumbnail))).toString());

            List<Like> likeList = new ArrayList<>();
            for (DataSnapshot dataSnapshot : dSnapshot
                    .child(context.getString(R.string.field_likes)).getChildren()) {
                Like like = new Like();
                like.setUser_id(Objects.requireNonNull(dataSnapshot.getValue(Like.class)).getUser_id());
                likeList.add(like);
            }
            comment.setLikes(likeList);
            commentsList.add(comment);
        }
        model.setComments(commentsList);

        List<Like> likesList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_likes)).getChildren()) {
            Like like = new Like();
            like.setUser_id(Objects.requireNonNull(dSnapshot.getValue(Like.class)).getUser_id());
            likesList.add(like);
        }
        model.setLikes(likesList);

        List<Crush> crushList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_crush)).getChildren()) {
            Crush crush = new Crush();
            crush.setUser_id(Objects.requireNonNull(dSnapshot.getValue(Crush.class)).getUser_id());
            crushList.add(crush);
        }
        model.setCrush(crushList);

        List<MediaID> mediasList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_media_id)).getChildren()) {
            MediaID mediaID = new MediaID();
            mediaID.setMedia_id(Objects.requireNonNull(dSnapshot.getValue(MediaID.class)).getMedia_id());
            mediasList.add(mediaID);
        }
        model.setMedia_id(mediasList);

        return model;
    }
}

