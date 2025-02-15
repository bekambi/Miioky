package com.bekambimouen.miiokychallenge.market_place.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;

import java.util.List;

public class MarketModel implements Parcelable {
    // store
    private boolean store;
    private boolean store_product;
    private boolean restaurant;
    private boolean bakery;
    private boolean recyclerItem;
    private boolean imageUne;
    private boolean sell;
    private boolean location;

    private String store_id;
    private String store_name;
    private String store_owner;
    private String store_message;
    private String restaurant_menu;
    private String store_profile_photo_id;
    private String live_country_name;
    private String town_name;
    private String neighborhood_name;
    private String profile_photo;
    private String full_photo;
    private String dontSuggestAnymore;
    private String user_id;

    // market
    private String product_name;
    private String product_category;
    private String location_category;
    private String old_price;
    private String price;
    private String devise;
    private String tags;
    private String product_state;
    private String product_description;
    private String location_period;
    private long date_created;
    private List<Like> likes;
    private List<Comment> comments;

    private String main_photo;
    private String main_photo_id;

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

    // suppressed the post
    private boolean suppressed;
    private long views;

    public MarketModel() {
    }

    public MarketModel(boolean store, boolean store_product, boolean restaurant, boolean bakery, boolean recyclerItem, boolean imageUne,
                       boolean sell, boolean location, String store_id, String store_name, String store_owner,
                       String store_message, String restaurant_menu, String store_profile_photo_id, String live_country_name,
                       String town_name, String neighborhood_name, String profile_photo, String full_photo,
                       String dontSuggestAnymore, String user_id, String product_name, String product_category,
                       String location_category, String old_price, String price, String devise, String tags,
                       String product_state, String product_description, String location_period, long date_created,
                       List<Like> likes, List<Comment> comments, String main_photo, String main_photo_id, String urli,
                       String urlii, String urliii, String urliv, String urlv, String urlvi, String urlvii, String urlviii,
                       String urlix, String urlx, String urlxi, String urlxii, String urlxiii, String urlxiv, String urlxv,
                       String urlxvi, String urlxvii, String photoi_id, String photoii_id, String photoiii_id, String photoiv_id,
                       String photov_id, String photovi_id, String photovii_id, String photoviii_id, String photoix_id,
                       String photox_id, String photoxi_id, String photoxii_id, String photoxiii_id, String photoxiv_id,
                       String photoxv_id, String photoxvi_id, String photoxvii_id, boolean suppressed,
                       long views) {
        this.store = store;
        this.store_product = store_product;
        this.restaurant = restaurant;
        this.bakery = bakery;
        this.recyclerItem = recyclerItem;
        this.imageUne = imageUne;
        this.sell = sell;
        this.location = location;
        this.store_id = store_id;
        this.store_name = store_name;
        this.store_owner = store_owner;
        this.store_message = store_message;
        this.restaurant_menu = restaurant_menu;
        this.store_profile_photo_id = store_profile_photo_id;
        this.live_country_name = live_country_name;
        this.town_name = town_name;
        this.neighborhood_name = neighborhood_name;
        this.profile_photo = profile_photo;
        this.full_photo = full_photo;
        this.dontSuggestAnymore = dontSuggestAnymore;
        this.user_id = user_id;
        this.product_name = product_name;
        this.product_category = product_category;
        this.location_category = location_category;
        this.old_price = old_price;
        this.price = price;
        this.devise = devise;
        this.tags = tags;
        this.product_state = product_state;
        this.product_description = product_description;
        this.location_period = location_period;
        this.date_created = date_created;
        this.likes = likes;
        this.comments = comments;
        this.main_photo = main_photo;
        this.main_photo_id = main_photo_id;
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
        this.suppressed = suppressed;
        this.views = views;
    }

    protected MarketModel(Parcel in) {
        suppressed = in.readByte() != 0;
        store = in.readByte() != 0;
        store_product = in.readByte() != 0;
        restaurant = in.readByte() != 0;
        bakery = in.readByte() != 0;
        recyclerItem = in.readByte() != 0;
        imageUne = in.readByte() != 0;
        sell = in.readByte() != 0;
        location = in.readByte() != 0;
        store_id = in.readString();
        store_name = in.readString();
        store_owner = in.readString();
        store_message = in.readString();
        restaurant_menu = in.readString();
        store_profile_photo_id = in.readString();
        live_country_name = in.readString();
        town_name = in.readString();
        neighborhood_name = in.readString();
        profile_photo = in.readString();
        full_photo = in.readString();
        dontSuggestAnymore = in.readString();
        user_id = in.readString();
        product_name = in.readString();
        product_category = in.readString();
        location_category = in.readString();
        old_price = in.readString();
        price = in.readString();
        devise = in.readString();
        tags = in.readString();
        product_state = in.readString();
        product_description = in.readString();
        location_period = in.readString();
        date_created = in.readLong();
        main_photo = in.readString();
        main_photo_id = in.readString();
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
        views = in.readLong();
    }

    public static final Creator<MarketModel> CREATOR = new Creator<MarketModel>() {
        @Override
        public MarketModel createFromParcel(Parcel in) {
            return new MarketModel(in);
        }

        @Override
        public MarketModel[] newArray(int size) {
            return new MarketModel[size];
        }
    };

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public boolean isStore_product() {
        return store_product;
    }

    public void setStore_product(boolean store_product) {
        this.store_product = store_product;
    }

    public boolean isRestaurant() {
        return restaurant;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    public boolean isBakery() {
        return bakery;
    }

    public void setBakery(boolean bakery) {
        this.bakery = bakery;
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

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_owner() {
        return store_owner;
    }

    public void setStore_owner(String store_owner) {
        this.store_owner = store_owner;
    }

    public String getStore_message() {
        return store_message;
    }

    public void setStore_message(String store_message) {
        this.store_message = store_message;
    }

    public String getRestaurant_menu() {
        return restaurant_menu;
    }

    public void setRestaurant_menu(String restaurant_menu) {
        this.restaurant_menu = restaurant_menu;
    }

    public String getStore_profile_photo_id() {
        return store_profile_photo_id;
    }

    public void setStore_profile_photo_id(String store_profile_photo_id) {
        this.store_profile_photo_id = store_profile_photo_id;
    }

    public String getLive_country_name() {
        return live_country_name;
    }

    public void setLive_country_name(String live_country_name) {
        this.live_country_name = live_country_name;
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

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getFull_photo() {
        return full_photo;
    }

    public void setFull_photo(String full_photo) {
        this.full_photo = full_photo;
    }

    public String getDontSuggestAnymore() {
        return dontSuggestAnymore;
    }

    public void setDontSuggestAnymore(String dontSuggestAnymore) {
        this.dontSuggestAnymore = dontSuggestAnymore;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getLocation_category() {
        return location_category;
    }

    public void setLocation_category(String location_category) {
        this.location_category = location_category;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getProduct_state() {
        return product_state;
    }

    public void setProduct_state(String product_state) {
        this.product_state = product_state;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getLocation_period() {
        return location_period;
    }

    public void setLocation_period(String location_period) {
        this.location_period = location_period;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public void setMain_photo(String main_photo) {
        this.main_photo = main_photo;
    }

    public void setMain_photo_id(String main_photo_id) {
        this.main_photo_id = main_photo_id;
    }

    public String getMain_photo_id() {
        return main_photo_id;
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
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeByte((byte) (suppressed ? 1 : 0));
        parcel.writeByte((byte) (store ? 1 : 0));
        parcel.writeByte((byte) (store_product ? 1 : 0));
        parcel.writeByte((byte) (restaurant ? 1 : 0));
        parcel.writeByte((byte) (bakery ? 1 : 0));
        parcel.writeByte((byte) (recyclerItem ? 1 : 0));
        parcel.writeByte((byte) (imageUne ? 1 : 0));
        parcel.writeByte((byte) (sell ? 1 : 0));
        parcel.writeByte((byte) (location ? 1 : 0));
        parcel.writeString(store_id);
        parcel.writeString(store_name);
        parcel.writeString(store_owner);
        parcel.writeString(store_message);
        parcel.writeString(restaurant_menu);
        parcel.writeString(store_profile_photo_id);
        parcel.writeString(live_country_name);
        parcel.writeString(town_name);
        parcel.writeString(neighborhood_name);
        parcel.writeString(profile_photo);
        parcel.writeString(full_photo);
        parcel.writeString(dontSuggestAnymore);
        parcel.writeString(user_id);
        parcel.writeString(product_name);
        parcel.writeString(product_category);
        parcel.writeString(location_category);
        parcel.writeString(old_price);
        parcel.writeString(price);
        parcel.writeString(devise);
        parcel.writeString(tags);
        parcel.writeString(product_state);
        parcel.writeString(product_description);
        parcel.writeString(location_period);
        parcel.writeLong(date_created);
        parcel.writeString(main_photo);
        parcel.writeString(main_photo_id);
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
        parcel.writeLong(views);
    }
}

