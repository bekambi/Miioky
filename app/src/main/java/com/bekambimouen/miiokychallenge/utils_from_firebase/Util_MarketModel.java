package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util_MarketModel {
    public static MarketModel getMarketModel(AppCompatActivity context, Map<Object, Object> objectMap, DataSnapshot ds) {
        MarketModel marketModel = new MarketModel();

        marketModel.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));
        marketModel.setStore(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store))).toString()));
        marketModel.setStore_product(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_product))).toString()));
        marketModel.setRestaurant(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_restaurant))).toString()));
        marketModel.setBakery(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_bakery))).toString()));
        marketModel.setRecyclerItem(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_recyclerItem))).toString()));
        marketModel.setImageUne(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_imageUne))).toString()));
        marketModel.setSell(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_sell))).toString()));
        marketModel.setLocation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_location))).toString()));

        marketModel.setStore_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_id))).toString());
        marketModel.setStore_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_name))).toString());
        marketModel.setStore_owner(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_owner))).toString());
        marketModel.setStore_message(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_message))).toString());
        marketModel.setRestaurant_menu(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_restaurant_menu))).toString());
        marketModel.setStore_profile_photo_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_profile_photo_id))).toString());
        marketModel.setLive_country_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_live_country_name))).toString());
        marketModel.setTown_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_town_name))).toString());
        marketModel.setNeighborhood_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_neighborhood_name))).toString());
        marketModel.setProfile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profile_photo))).toString());
        marketModel.setFull_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_full_photo))).toString());
        marketModel.setDontSuggestAnymore(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_dontSuggestAnymore))).toString());
        marketModel.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        marketModel.setProduct_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_product_name))).toString());
        marketModel.setProduct_category(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_product_category))).toString());
        marketModel.setLocation_category(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_location_category))).toString());
        marketModel.setOld_price(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_old_price))).toString());
        marketModel.setPrice(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_price))).toString());
        marketModel.setDevise(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_devise))).toString());
        marketModel.setTags(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_tags))).toString());
        marketModel.setProduct_state(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_product_state))).toString());
        marketModel.setProduct_description(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_product_description))).toString());
        marketModel.setLocation_period(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_location_period))).toString());
        marketModel.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        marketModel.setViews(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_views))).toString()));
        marketModel.setMain_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_main_photo))).toString());
        marketModel.setMain_photo_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_main_photo_id))).toString());

        marketModel.setUrli(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urli))).toString());
        marketModel.setUrlii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlii))).toString());
        marketModel.setUrliii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urliii))).toString());
        marketModel.setUrliv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urliv))).toString());
        marketModel.setUrlv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlv))).toString());
        marketModel.setUrlvi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlvi))).toString());
        marketModel.setUrlvii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlvii))).toString());
        marketModel.setUrlviii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlviii))).toString());
        marketModel.setUrlix(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlix))).toString());
        marketModel.setUrlx(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlx))).toString());
        marketModel.setUrlxi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxi))).toString());
        marketModel.setUrlxii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxii))).toString());
        marketModel.setUrlxiii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxiii))).toString());
        marketModel.setUrlxiv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxiv))).toString());
        marketModel.setUrlxv(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxv))).toString());
        marketModel.setUrlxvi(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxvi))).toString());
        marketModel.setUrlxvii(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_urlxvii))).toString());

        marketModel.setPhotoi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoi_id))).toString());
        marketModel.setPhotoii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoii_id))).toString());
        marketModel.setPhotoiii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoiii_id))).toString());
        marketModel.setPhotoiv_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoiv_id))).toString());
        marketModel.setPhotov_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photov_id))).toString());
        marketModel.setPhotovi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photovi_id))).toString());
        marketModel.setPhotovii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photovii_id))).toString());
        marketModel.setPhotoviii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoviii_id))).toString());
        marketModel.setPhotoix_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoix_id))).toString());
        marketModel.setPhotox_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photox_id))).toString());
        marketModel.setPhotoxi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxi_id))).toString());
        marketModel.setPhotoxii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxii_id))).toString());
        marketModel.setPhotoxiii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxiii_id))).toString());
        marketModel.setPhotoxiv_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxiv_id))).toString());
        marketModel.setPhotoxv_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxv_id))).toString());
        marketModel.setPhotoxvi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxvi_id))).toString());
        marketModel.setPhotoxvii_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoxvii_id))).toString());

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
        marketModel.setComments(commentsList);

        List<Like> likesList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_likes)).getChildren()) {
            Like like = new Like();
            like.setUser_id(Objects.requireNonNull(dSnapshot.getValue(Like.class)).getUser_id());
            likesList.add(like);
        }
        marketModel.setLikes(likesList);

        return marketModel;
    }
}

