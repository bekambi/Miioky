package com.bekambimouen.miiokychallenge.preload_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PreloadMainFragment {
    public static void getPreloadImages(AppCompatActivity context, BattleModel model) {
        if (!model.isGroup() && model.isComment_text()) {
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            
        } else if (model.isUser_profile() && !model.isGroup()) {
            Glide.with(context).load(model.getUser_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());

        } else if (model.isRecyclerItem() && !model.isGroup()) {
            Glide.with(context).load(model.getUrli()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliii().isEmpty()) 
                Glide.with(context).load(model.getUrliii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliv().isEmpty())
                Glide.with(context).load(model.getUrliv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlv().isEmpty())
                Glide.with(context).load(model.getUrlv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvi().isEmpty())
                Glide.with(context).load(model.getUrlvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvii().isEmpty())
                Glide.with(context).load(model.getUrlvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlviii().isEmpty())
                Glide.with(context).load(model.getUrlviii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlix().isEmpty())
                Glide.with(context).load(model.getUrlix()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlx().isEmpty())
                Glide.with(context).load(model.getUrlx()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxi().isEmpty())
                Glide.with(context).load(model.getUrlxi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxii().isEmpty())
                Glide.with(context).load(model.getUrlxii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiii().isEmpty())
                Glide.with(context).load(model.getUrlxiii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiv().isEmpty())
                Glide.with(context).load(model.getUrlxiv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxv().isEmpty())
                Glide.with(context).load(model.getUrlxv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvi().isEmpty())
                Glide.with(context).load(model.getUrlxvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvii().isEmpty())
                Glide.with(context).load(model.getUrlxvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());

        } else if (model.isImageUne() && !model.isGroup()) {
            Glide.with(context).load(model.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());

        } else if (model.isVideoUne() && !model.isGroup()) {
            Glide.with(context).load(model.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());

        } else if (model.isImageDouble() && !model.isGroup()) {
            Glide.with(context).load(model.getUrlUn()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlDeux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());

        } else if (model.isVideoDouble() && !model.isGroup()) {

            Glide.with(context).load(model.getThumbnail_un()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_deux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());

        } else if (model.isReponseImageDouble() && !model.isGroup()) {
            Glide.with(context).load(model.getReponse_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getInvite_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());

        } else if (model.isReponseVideoDouble() && !model.isGroup()) {
            Glide.with(context).load(model.getThumbnail_response()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_invite()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());

        } else if (model.isUser_profile_shared()) {
            Glide.with(context).load(model.getUser_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            
            //get the profile image and username top
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username bottom
            getUserProfilePhoto(context, model.getUser_id_sharing());
            
        } else if (model.isRecyclerItem_shared()) {
            Glide.with(context).load(model.getUrli()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliii().isEmpty())
                Glide.with(context).load(model.getUrliii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliv().isEmpty())
                Glide.with(context).load(model.getUrliv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlv().isEmpty())
                Glide.with(context).load(model.getUrlv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvi().isEmpty())
                Glide.with(context).load(model.getUrlvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvii().isEmpty())
                Glide.with(context).load(model.getUrlvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlviii().isEmpty())
                Glide.with(context).load(model.getUrlviii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlix().isEmpty())
                Glide.with(context).load(model.getUrlix()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlx().isEmpty())
                Glide.with(context).load(model.getUrlx()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxi().isEmpty())
                Glide.with(context).load(model.getUrlxi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxii().isEmpty())
                Glide.with(context).load(model.getUrlxii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiii().isEmpty())
                Glide.with(context).load(model.getUrlxiii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiv().isEmpty())
                Glide.with(context).load(model.getUrlxiv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxv().isEmpty())
                Glide.with(context).load(model.getUrlxv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvi().isEmpty())
                Glide.with(context).load(model.getUrlxvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvii().isEmpty())
                Glide.with(context).load(model.getUrlxvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username top
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username bottom
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else if (model.isImageUne_shared()) {
            Glide.with(context).load(model.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username top
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username bottom
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else if (model.isVideoUne_shared()) {
            Glide.with(context).load(model.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username top
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username bottom
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else if (model.isImageDouble_shared()) {
            Glide.with(context).load(model.getUrlUn()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlDeux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username top
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username bottom
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else if (model.isVideoDouble_shared()) {
            Glide.with(context).load(model.getThumbnail_un()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_deux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username top
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username bottom
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else if (model.isReponseImageDouble_shared()) {
            Glide.with(context).load(model.getReponse_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getInvite_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());

        } else if (model.isReponseVideoDouble_shared()) {
            Glide.with(context).load(model.getThumbnail_response()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_invite()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getReponse_user_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getInvite_userID());

        } else  if (model.isGroup_share_single_top_circle_image()) {
            if (!model.getGroup_user_background_profile_url().isEmpty())
                Glide.with(context).load(model.getGroup_user_background_profile_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getGroup_profile_photo().isEmpty())
                Glide.with(context).load(model.getGroup_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else  if (model.isGroup_share_single_top_image_double()) {
            Glide.with(context).load(model.getUrlUn()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlDeux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else  if (model.isGroup_share_single_top_image_une()) {
            if (!model.getGroup_user_background_profile_url().isEmpty())
                Glide.with(context).load(model.getGroup_user_background_profile_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            else if (!model.getGroup_profile_photo().isEmpty())
                Glide.with(context).load(model.getGroup_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            else
                Glide.with(context).load(model.getUrl()).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else  if (model.isGroup_share_single_top_recycler()) {
            Glide.with(context).load(model.getUrli()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliii().isEmpty())
                Glide.with(context).load(model.getUrliii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliv().isEmpty())
                Glide.with(context).load(model.getUrliv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlv().isEmpty())
                Glide.with(context).load(model.getUrlv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvi().isEmpty())
                Glide.with(context).load(model.getUrlvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvii().isEmpty())
                Glide.with(context).load(model.getUrlvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlviii().isEmpty())
                Glide.with(context).load(model.getUrlviii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlix().isEmpty())
                Glide.with(context).load(model.getUrlix()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlx().isEmpty())
                Glide.with(context).load(model.getUrlx()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxi().isEmpty())
                Glide.with(context).load(model.getUrlxi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxii().isEmpty())
                Glide.with(context).load(model.getUrlxii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiii().isEmpty())
                Glide.with(context).load(model.getUrlxiii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiv().isEmpty())
                Glide.with(context).load(model.getUrlxiv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxv().isEmpty())
                Glide.with(context).load(model.getUrlxv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvi().isEmpty())
                Glide.with(context).load(model.getUrlxvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvii().isEmpty())
                Glide.with(context).load(model.getUrlxvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else  if (model.isGroup_share_single_top_response_image_double()) {
            Glide.with(context).load(model.getReponse_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getInvite_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getReponse_user_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getInvite_userID());

        } else  if (model.isGroup_share_single_top_response_video_double()) {
            Glide.with(context).load(model.getThumbnail_response()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_invite()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getReponse_user_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getInvite_userID());

        } else  if (model.isGroup_share_single_top_video_double()) {
            Glide.with(context).load(model.getThumbnail_un()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_deux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } else  if (model.isGroup_share_single_top_video_une()) {
            Glide.with(context).load(model.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getSharing_admin_master());

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id_sharing());

        } // group
        else if (model.isGroup() && model.isComment_text()) {
                //get the profile image and username
                getUserProfilePhoto(context, model.getUser_id());
                //get the group profile image
                getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());


        } if (model.isGroup_cover_profile_photo()) {
            Glide.with(context).load(model.getGroup_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_cover_background_profile_photo()) {
            Glide.with(context).load(model.getGroup_user_background_profile_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_recyclerItem()) {
            Glide.with(context).load(model.getUrli()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliii().isEmpty())
                Glide.with(context).load(model.getUrliii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliv().isEmpty())
                Glide.with(context).load(model.getUrliv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlv().isEmpty())
                Glide.with(context).load(model.getUrlv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvi().isEmpty())
                Glide.with(context).load(model.getUrlvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvii().isEmpty())
                Glide.with(context).load(model.getUrlvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlviii().isEmpty())
                Glide.with(context).load(model.getUrlviii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlix().isEmpty())
                Glide.with(context).load(model.getUrlix()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlx().isEmpty())
                Glide.with(context).load(model.getUrlx()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxi().isEmpty())
                Glide.with(context).load(model.getUrlxi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxii().isEmpty())
                Glide.with(context).load(model.getUrlxii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiii().isEmpty())
                Glide.with(context).load(model.getUrlxiii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiv().isEmpty())
                Glide.with(context).load(model.getUrlxiv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxv().isEmpty())
                Glide.with(context).load(model.getUrlxv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvi().isEmpty())
                Glide.with(context).load(model.getUrlxvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvii().isEmpty())
                Glide.with(context).load(model.getUrlxvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_imageUne()) {
            Glide.with(context).load(model.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_videoUne()) {
            Glide.with(context).load(model.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the profile image and username
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_imageDouble()) {
            Glide.with(context).load(model.getUrlUn()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlDeux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_videoDouble()) {
            Glide.with(context).load(model.getThumbnail_un()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_deux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_response_imageDouble()) {
            Glide.with(context).load(model.getReponse_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getInvite_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else if (model.isGroup_response_videoDouble()) {
            Glide.with(context).load(model.getThumbnail_response()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_invite()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
        } // share
        else if (model.isGroup_share_single_bottom_circle_image()) {
            Glide.with(context).load(model.getUser_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_image_double()) {
            Glide.with(context).load(model.getUrlUn()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlDeux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_image_une()) {
            Glide.with(context).load(model.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_recycler()) {
            Glide.with(context).load(model.getUrli()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliii().isEmpty())
                Glide.with(context).load(model.getUrliii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliv().isEmpty())
                Glide.with(context).load(model.getUrliv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlv().isEmpty())
                Glide.with(context).load(model.getUrlv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvi().isEmpty())
                Glide.with(context).load(model.getUrlvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvii().isEmpty())
                Glide.with(context).load(model.getUrlvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlviii().isEmpty())
                Glide.with(context).load(model.getUrlviii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlix().isEmpty())
                Glide.with(context).load(model.getUrlix()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlx().isEmpty())
                Glide.with(context).load(model.getUrlx()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxi().isEmpty())
                Glide.with(context).load(model.getUrlxi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxii().isEmpty())
                Glide.with(context).load(model.getUrlxii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiii().isEmpty())
                Glide.with(context).load(model.getUrlxiii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiv().isEmpty())
                Glide.with(context).load(model.getUrlxiv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxv().isEmpty())
                Glide.with(context).load(model.getUrlxv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvi().isEmpty())
                Glide.with(context).load(model.getUrlxvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvii().isEmpty())
                Glide.with(context).load(model.getUrlxvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_response_image_double()) {
            Glide.with(context).load(model.getReponse_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getInvite_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_response_video_double()) {
            Glide.with(context).load(model.getThumbnail_response()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_invite()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_video_double()) {
            Glide.with(context).load(model.getThumbnail_un()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_deux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_single_bottom_video_une()) {
            Glide.with(context).load(model.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());

        } else  if (model.isGroup_share_top_bottom_circle_image()) {
            if (!model.getGroup_user_background_profile_url().isEmpty())
                Glide.with(context).load(model.getGroup_user_background_profile_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            else
                Glide.with(context).load(model.getGroup_profile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_image_double()) {
            Glide.with(context).load(model.getUrlUn()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlDeux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_image_une()) {
            Glide.with(context).load(model.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_recycler()) {
            Glide.with(context).load(model.getUrli()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getUrlii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliii().isEmpty())
                Glide.with(context).load(model.getUrliii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrliv().isEmpty())
                Glide.with(context).load(model.getUrliv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlv().isEmpty())
                Glide.with(context).load(model.getUrlv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvi().isEmpty())
                Glide.with(context).load(model.getUrlvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlvii().isEmpty())
                Glide.with(context).load(model.getUrlvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlviii().isEmpty())
                Glide.with(context).load(model.getUrlviii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlix().isEmpty())
                Glide.with(context).load(model.getUrlix()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlx().isEmpty())
                Glide.with(context).load(model.getUrlx()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxi().isEmpty())
                Glide.with(context).load(model.getUrlxi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxii().isEmpty())
                Glide.with(context).load(model.getUrlxii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiii().isEmpty())
                Glide.with(context).load(model.getUrlxiii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxiv().isEmpty())
                Glide.with(context).load(model.getUrlxiv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxv().isEmpty())
                Glide.with(context).load(model.getUrlxv()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvi().isEmpty())
                Glide.with(context).load(model.getUrlxvi()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            if (!model.getUrlxvii().isEmpty())
                Glide.with(context).load(model.getUrlxvii()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_response_image_double()) {
            Glide.with(context).load(model.getReponse_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getInvite_url()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_response_video_double()) {
            Glide.with(context).load(model.getThumbnail_response()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_invite()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getReponse_user_id());
            getUserProfilePhoto(context, model.getInvite_userID());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_video_double()) {
            Glide.with(context).load(model.getThumbnail_un()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
            Glide.with(context).load(model.getThumbnail_deux()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        } else  if (model.isGroup_share_top_bottom_video_une()) {
            Glide.with(context).load(model.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();

            //get the user profile image
            getUserProfilePhoto(context, model.getUser_id());
            getUserProfilePhoto(context, model.getUser_id_sharing());
            //get the group profile image
            getGroupProfilePhoto(context, model.getGroup_id(), model.getAdmin_master());
            getGroupProfilePhoto(context, model.getShared_group_id(), model.getSharing_admin_master());

        }
    }

    // preload profile photo // if share, top
    private static void getUserProfilePhoto(AppCompatActivity context, String user_id) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Glide.with(context).load(user.getProfileUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // preload group profile photo
    private static void getGroupProfilePhoto(AppCompatActivity context, String group_id, String admin_master_id) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_user_group))
                .child(admin_master_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(group_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    Glide.with(context).load(user_groups.getProfile_photo()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
