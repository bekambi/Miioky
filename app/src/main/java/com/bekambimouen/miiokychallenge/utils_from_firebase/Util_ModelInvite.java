package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;

import java.util.Map;
import java.util.Objects;

public class Util_ModelInvite {
    public static ModelInvite getModelInvite(AppCompatActivity context, Map<String, Object> objectMap) {
        ModelInvite model = new ModelInvite();

        model.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));
        model.setMiioky_challenge(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_miioky_challenge))).toString()));
        model.setGroup_challenge(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_challenge))).toString()));
        model.setGroup(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group))).toString()));
        model.setGroup_private(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_private))).toString()));
        model.setGroup_public(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_public))).toString()));
        model.setVideo(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_video))).toString()));
        model.setImage(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_image))).toString()));
        model.setGroup_video(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_video))).toString()));
        model.setGroup_image(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_image))).toString()));
        model.setBig_image(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_big_image))).toString()));

        model.setAdmin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString());
        model.setGroup_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_id))).toString());
        model.setGroup_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_name))).toString());
        model.setGroup_admin(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_admin))).toString());
        model.setInvite_url(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_url))).toString());
        model.setThumbnail_invite(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail_invite))).toString());
        model.setInvite_photoID(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_photoID))).toString()));
        model.setInvite_profile_photo(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_profile_photo))).toString()));
        model.setInvite_userID(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_userID))).toString()));
        model.setUser_id(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString()));
        model.setInvite_username(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_username))).toString());
        model.setInvite_caption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_caption))).toString());
        model.setInvite_tag(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_tag))).toString());
        model.setInvite_category(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_category))).toString());
        model.setInvite_category_status(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_category_status))).toString());
        model.setInvite_country_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_country_name))).toString());
        model.setInvite_countryID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_countryID))).toString());
        model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        model.setTimestart(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_timestart))).toString()));
        model.setTimeend(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_timeend))).toString()));

        model.setIndex_i(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_i))).toString());
        model.setIndex_ii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_ii))).toString());
        model.setIndex_iii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_iii))).toString());
        model.setIndex_iv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_iv))).toString());
        model.setIndex_v(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_v))).toString());
        model.setIndex_vi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_vi))).toString());
        model.setIndex_vii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_vii))).toString());
        model.setIndex_viii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_viii))).toString());
        model.setIndex_ix(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_ix))).toString());
        model.setIndex_x(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_index_x))).toString());

        return model;
    }
}

