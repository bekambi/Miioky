package com.bekambimouen.miiokychallenge.market_place;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.friends.bottomsheet.BottomSheetManegeFriend;
import com.bekambimouen.miiokychallenge.friends.utils_map.Utils_Map_SubscriptionRequestModel;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.market_place.adapter.RentalItemsAdapter;
import com.bekambimouen.miiokychallenge.market_place.adapter.StoreItemsAdapter;
import com.bekambimouen.miiokychallenge.market_place.comment.product_comment.bottomsheet.BottomSheetProductComments;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.publication.adapter.MarketAboutArticleAdapter;
import com.bekambimouen.miiokychallenge.market_place.recycler_full_image.MarketRecyclerFullImage;
import com.bekambimouen.miiokychallenge.market_place.update.UpdateProductData;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Chat;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Utils_Map_StoreFollowingModel;
import com.bekambimouen.miiokychallenge.views_count.ViewsMarketProductManager;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MarketAboutProduct extends AppCompatActivity {
    private static final String TAG = "MarketAboutProduct";

    // widgets
    private SmallBangView like_heart;
    private ImageView image;
    private TextView number_of_likes, number_of_comments;
    private TextView button_text;
    private TextView bouton_rejoindre;
    private TextView bouton_quitter;
    private TextView button_follow, button_unfollow, button_sent;
    private TextView state;
    private TextView description;
    private TextView other_items_rented_by_this_seller, seller_others_stores, view_the_menu, views;
    private MyEditText edit_write_to_the_seller;
    private RecyclerView recyclerView_to_rent_out, recyclerView_store;
    private RelativeLayout parent1, relLayout_button, relLayout_add, relLayout_comment_icon, relLayout_view_overlay;
    private RelativeLayout relLayout_unfriend;
    private LinearLayout linLayout_not_yet_friend;
    private LinearLayout linLayout_go_chat_with_seller;
    private ProgressBar button_progressBar, progressBar_to_rent_out, progressBar;

    // vars
    private MarketAboutProduct context;
    private MarketModel market_model;
    private ArrayList<String> list, liker_list;
    private User mCurrentUser;
    private StringBuilder mUsers;
    private String from_seller_store;
    // from chat
    private String from_chat, store_id, photoi_id;
    private boolean isFirstTimeStrokeColor = false, isIntentClicked = false;
    private boolean mLikedByCurrentUser;
    private int likes_count = 0, comments_count = 0;

    private List<MarketModel> list_store, list_to_rent_out;
    boolean notify = false;

    private CustomShareProgressView progresDialog;
    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.setCancelable(false);
        progresDialog.show();
    }

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_market_about_product);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        context = this;

        list = new ArrayList<>();
        list_to_rent_out = new ArrayList<>();
        list_store = new ArrayList<>();
        liker_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
            from_seller_store = getIntent().getStringExtra("from_seller_store");
            from_chat = getIntent().getStringExtra("from_chat");
            store_id = getIntent().getStringExtra("store_id");
            photoi_id = getIntent().getStringExtra("photoi_id");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (from_chat != null) {
            Query query = myRef
                    .child(getString(R.string.dbname_market))
                    .child(store_id) // store_id
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(store_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        if (market_model.getPhotoi_id().equals(photoi_id)) {
                            init();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            init();
        }
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        parent1 = findViewById(R.id.parent1);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_deleted_product = findViewById(R.id.relLayout_deleted_product);
        ImageView one_image = findViewById(R.id.one_image);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        TextView tv_information_about_establishment = findViewById(R.id.tv_information_about_establishment);
        TextView product_name = findViewById(R.id.product_name);
        TextView price = findViewById(R.id.price);
        TextView tps_publication = findViewById(R.id.tps_publication);
        TextView for_rent = findViewById(R.id.for_rent);
        TextView neighborhood = findViewById(R.id.neighborhood);
        TextView old_price = findViewById(R.id.old_price);
        button_follow = findViewById(R.id.button_follow);
        button_unfollow = findViewById(R.id.button_unfollow);
        button_sent = findViewById(R.id.button_sent);
        edit_write_to_the_seller = findViewById(R.id.edit_write_to_the_seller);
        button_text = findViewById(R.id.button_text);
        button_progressBar = findViewById(R.id.button_progressBar);
        relLayout_comment_icon = findViewById(R.id.relLayout_comment_icon);
        image = findViewById(R.id.image);
        like_heart = findViewById(R.id.like_heart);
        number_of_likes = findViewById(R.id.number_of_likes);
        number_of_comments = findViewById(R.id.number_of_comments);
        RelativeLayout parent = findViewById(R.id.parent);
        RelativeLayout relLayout_recyclerView = findViewById(R.id.relLayout_recyclerView);
        linLayout_go_chat_with_seller = findViewById(R.id.linLayout_go_chat_with_seller);
        LinearLayout linLayout_write_to_the_seller = findViewById(R.id.linLayout_write_to_the_seller);
        RelativeLayout relLayout_description = findViewById(R.id.relLayout_description);
        RelativeLayout relLayout_all_buttons_follow = findViewById(R.id.relLayout_all_buttons_follow);
        relLayout_button = findViewById(R.id.relLayout_button);
        relLayout_add = findViewById(R.id.relLayout_add);
        relLayout_unfriend = findViewById(R.id.relLayout_unfriend);
        linLayout_not_yet_friend = findViewById(R.id.linLayout_not_yet_friend);
        LinearLayout linLayout_likes_and_comments = findViewById(R.id.linLayout_likes_and_comments);
        RelativeLayout relLayout_follow_user = findViewById(R.id.relLayout_follow_user);
        RelativeLayout relLayout_follow_store = findViewById(R.id.relLayout_follow_store);
        state = findViewById(R.id.state);
        description = findViewById(R.id.description);
        other_items_rented_by_this_seller = findViewById(R.id.other_items_rented_by_this_seller);
        seller_others_stores = findViewById(R.id.seller_others_stores);
        view_the_menu = findViewById(R.id.view_the_menu);
        views = findViewById(R.id.views);

        getCurrentUser();
        getLikesString();
        getUpdateLikesString();
        setLikes();
        setComments();

        if (market_model.isSuppressed()) {
            toolBar_textview.setVisibility(View.GONE);
            relLayout_deleted_product.setVisibility(View.VISIBLE);
        }

        if (market_model.isRestaurant()) {
            toolBar_textview.setText(context.getString(R.string.about_the_dish));
            tv_information_about_establishment.setText(context.getString(R.string.restaurant_information));
            state.setVisibility(View.GONE);
        }
        if (market_model.isBakery()) {
            toolBar_textview.setText(context.getString(R.string.about_the_pastry));
            tv_information_about_establishment.setText(context.getString(R.string.bakery_information));
            state.setVisibility(View.GONE);
        }
        if (market_model.isStore()) {
            toolBar_textview.setText(context.getString(R.string.about_the_article));
            tv_information_about_establishment.setText(context.getString(R.string.store_information));
        }

        if (from_seller_store != null) {
            linLayout_likes_and_comments.setVisibility(View.GONE);
            tv_information_about_establishment.setVisibility(View.GONE);
            parent.setVisibility(View.GONE);
        }

        if (market_model.getUser_id().equals(user_id)) {
            linLayout_write_to_the_seller.setVisibility(View.GONE);
            relLayout_all_buttons_follow.setVisibility(View.GONE);
        }

        if (market_model.isLocation() || market_model.isSell()) {
            relLayout_follow_user.setVisibility(View.VISIBLE);
            relLayout_follow_store.setVisibility(View.GONE);

            if (market_model.isLocation())
                for_rent.setVisibility(View.VISIBLE);
        } else {
            relLayout_follow_user.setVisibility(View.GONE);
            relLayout_follow_store.setVisibility(View.VISIBLE);
        }

        CircleImageView profile_photo = findViewById(R.id.profile_photo);
        TextView store_name = findViewById(R.id.store_name);
        bouton_rejoindre = findViewById(R.id.bouton_rejoindre);
        bouton_quitter = findViewById(R.id.bouton_quitter);

        // set product views
        if (market_model.getViews() != 0) {
            views.setVisibility(View.VISIBLE);
            if (market_model.getViews() == 1)
                views.setText(Html.fromHtml(context.getString(R.string.view)+": <b>"+market_model.getViews()+"</b>"));
            else
                views.setText(Html.fromHtml(context.getString(R.string.views)+": <b>"+market_model.getViews()+"</b>"));
        }

        // preload all product images
        if (!TextUtils.isEmpty(market_model.getUrli()))
            Glide.with(context).load(market_model.getUrli()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlii()))
            Glide.with(context).load(market_model.getUrlii()).preload();
        if (!TextUtils.isEmpty(market_model.getUrliii()))
            Glide.with(context).load(market_model.getUrliii()).preload();
        if (!TextUtils.isEmpty(market_model.getUrliv()))
            Glide.with(context).load(market_model.getUrliv()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlv()))
            Glide.with(context).load(market_model.getUrlv()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlvi()))
            Glide.with(context).load(market_model.getUrlvi()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlvii()))
            Glide.with(context).load(market_model.getUrlvii()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlviii()))
            Glide.with(context).load(market_model.getUrlviii()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlix()))
            Glide.with(context).load(market_model.getUrlix()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlx()))
            Glide.with(context).load(market_model.getUrlx()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxi()))
            Glide.with(context).load(market_model.getUrlxi()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxii()))
            Glide.with(context).load(market_model.getUrlxii()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxiii()))
            Glide.with(context).load(market_model.getUrlxiii()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxiv()))
            Glide.with(context).load(market_model.getUrlxiv()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxv()))
            Glide.with(context).load(market_model.getUrlxv()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxvi()))
            Glide.with(context).load(market_model.getUrlxvi()).preload();
        if (!TextUtils.isEmpty(market_model.getUrlxvii()))
            Glide.with(context).load(market_model.getUrlxvii()).preload();

        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.recyclerIndicator);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        progressBar_to_rent_out = findViewById(R.id.progressBar_to_rent_out);
        recyclerView_to_rent_out = findViewById(R.id.recyclerView_to_rent_out);

        progressBar = findViewById(R.id.progressBar);
        recyclerView_store = findViewById(R.id.recyclerView_store);

        // delete or update announce
        if (market_model.getStore_owner().equals(user_id))
            relLayout_add.setVisibility(View.VISIBLE);

        // get the restaurant menu
        Query query4 = myRef
                .child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(market_model.getStore_id());
        query4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                    String the_menu = market_model.getRestaurant_menu();

                    if (!TextUtils.isEmpty(the_menu))
                        view_the_menu.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        view_the_menu.setOnClickListener(view -> {
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_market_the_menu, null);

            TextView restaurant_name = v.findViewById(R.id.restaurant_name);
            TextView the_menu = v.findViewById(R.id.the_menu);
            TextView button_ok = v.findViewById(R.id.button_ok);
            builder.setView(v);

            Dialog d = builder.create();
            d.show();

            button_ok.setOnClickListener(view1 -> d.dismiss());

            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_user_stores))
                    .child(market_model.getStore_owner())
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(market_model.getStore_id());

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        restaurant_name.setText(market_model.getStore_name());
                        the_menu.setText(market_model.getRestaurant_menu());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        relLayout_add.setOnClickListener(view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, relLayout_add);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_market_update_product);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_update_data) {
                    isIntentClicked = true;
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    String myJson = gson.toJson(market_model);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, UpdateProductData.class);
                    intent.putExtra("market_model", myJson);
                    context.startActivity(intent);

                } else if (item.getItemId() == R.id.menu_edit_menu) {
                    showKeyboard();
                    // show dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.layout_market_update_menu_of_restaurant, null);

                    MyEditText edit_menu_of_the_day = v.findViewById(R.id.edit_menu_of_the_day);
                    TextView button = v.findViewById(R.id.button);
                    RelativeLayout relLayout_menu_of_the_day = v.findViewById(R.id.relLayout_menu_of_the_day);
                    builder.setView(v);

                    Dialog d = builder.create();
                    d.show();

                    edit_menu_of_the_day.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String text = Objects.requireNonNull(edit_menu_of_the_day.getText()).toString();
                            if (text.startsWith(" ")) {
                                edit_menu_of_the_day.setText(text.trim());
                            }

                            if (isFirstTimeStrokeColor) {
                                isFirstTimeStrokeColor = false;

                                GradientDrawable drawable = (GradientDrawable) relLayout_menu_of_the_day.getBackground();
                                drawable.mutate();
                                drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    // get the restaurant menu
                    Query query5 = myRef
                            .child(context.getString(R.string.dbname_user_stores))
                            .child(market_model.getStore_owner())
                            .orderByChild(context.getString(R.string.field_store_id))
                            .equalTo(market_model.getStore_id());
                    query5.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                String the_menu = market_model.getRestaurant_menu();

                                if (!TextUtils.isEmpty(the_menu))
                                    edit_menu_of_the_day.setText(market_model.getRestaurant_menu());

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // add menu
                    button.setOnClickListener(view1 -> {
                        // internet control
                        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                        if (!isConnected) {
                            CheckInternetStatus.internetIsConnected(context, parent);

                        } else {
                            String getMenu = Objects.requireNonNull(edit_menu_of_the_day.getText()).toString();

                            if (TextUtils.isEmpty(getMenu)) {
                                isFirstTimeStrokeColor = true;
                                GradientDrawable drawable = (GradientDrawable) relLayout_menu_of_the_day.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                return;
                            }

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("restaurant_menu", getMenu);

                            myRef.child(context.getString(R.string.dbname_user_stores))
                                    .child(market_model.getStore_owner())
                                    .child(market_model.getStore_id())
                                    .updateChildren(map).addOnSuccessListener(unused -> {
                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();

                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(edit_menu_of_the_day.getWindowToken(), 0);
                                        d.dismiss();
                                    });
                        }
                    });

                } else if (item.getItemId() == R.id.menu_delete_announce) {
                    // show dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                    TextView dialog_title = v.findViewById(R.id.dialog_title);
                    TextView dialog_text = v.findViewById(R.id.dialog_text);
                    TextView negativeButton = v.findViewById(R.id.tv_no);
                    TextView positiveButton = v.findViewById(R.id.tv_yes);
                    builder.setView(v);

                    Dialog d = builder.create();
                    d.show();

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.delete));

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.are_you_sure_you_want_to_delete_this_announce)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view2 -> {
                        // internet control
                        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                        if (!isConnected) {
                            CheckInternetStatus.internetIsConnected(context, parent);

                        } else {
                            d.dismiss();
                            showLoading();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", true);

                            myRef.child(context.getString(R.string.dbname_market))
                                    .child(market_model.getStore_id())
                                    .child(market_model.getPhotoi_id())
                                    .updateChildren(map);

                            myRef.child(context.getString(R.string.dbname_market_media))
                                    .child(market_model.getPhotoi_id())
                                    .updateChildren(map).addOnSuccessListener(unused -> {
                                        progresDialog.dismiss();
                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("from_market", "from_market");
                                        startActivity(intent);
                                        finish();
                                    });
                        }
                    });

                }
                return false;
            });

            // hide some menu items
            Menu popupMenu = popup.getMenu();
            if (market_model.getUser_id().equals(user_id)) {

                if (market_model.isBakery() || market_model.isStore() || market_model.isLocation() || market_model.isSell()) {
                    popupMenu.findItem(R.id.menu_edit_menu).setVisible(false);
                }
            }

            if (!market_model.getUser_id().equals(user_id)) {
                popupMenu.findItem(R.id.menu_update_data).setVisible(false);
                popupMenu.findItem(R.id.menu_edit_menu).setVisible(false);
                popupMenu.findItem(R.id.menu_delete_announce).setVisible(false);
            }

            //displaying the popup
            popup.show();
        });

        // other to_rent_out objects of the same seller
        otherto_rent_outObjects();

        // other stores of the same seller
        otherStores();

        // scroll in edittext
        edit_write_to_the_seller.setOnTouchListener((view, motionEvent) -> {
            if (edit_write_to_the_seller.hasFocus()) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });

        product_name.setText(market_model.getProduct_name());

        if (!TextUtils.isEmpty(market_model.getOld_price())) {
            old_price.setVisibility(View.VISIBLE);
            old_price.setText(Html.fromHtml(market_model.getOld_price().replace(",", " ")+" "+market_model.getDevise()));
            // texte barrÃ©
            old_price.setPaintFlags(old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (market_model.isLocation())
            price.setText(Html.fromHtml(market_model.getPrice().replace(",", " ")
                    + " " + market_model.getDevise() + "/" + market_model.getLocation_period()));
        else
            price.setText(Html.fromHtml(market_model.getPrice().replace(",", " ")+" "+market_model.getDevise()));

        neighborhood.setText(Html.fromHtml(context.getString(R.string.neighborhood)+": "+market_model.getNeighborhood_name()));

        long tv_date = market_model.getDate_created();
        long time = (System.currentTimeMillis() - tv_date);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(Html.fromHtml(context.getString(R.string.published)+" "+context.getString(R.string.the)
                    +" "+ TimeUtils.getTime(context, tv_date)));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.published)+" "+context.getString(R.string.there_is)
                    +" "+ TimeUtils.getTime(context, tv_date)));

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(market_model.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    edit_write_to_the_seller.setText(Html.fromHtml(context.getString(R.string.hello)
                            +" "+user.getUsername()+" "+context.getString(R.string.this_product_is_still_available)));

                    state.setText(Html.fromHtml(" <b>"+context.getString(R.string.state)+":</b> "
                            +market_model.getProduct_state()));

                    if (!TextUtils.isEmpty(market_model.getProduct_description()))
                        relLayout_description.setVisibility(View.VISIBLE);

                    description.setText(market_model.getProduct_description());

                    // go chat with the seller
                    linLayout_go_chat_with_seller.setOnClickListener(view -> {
                        isIntentClicked = true;
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        String myGson2 = gson.toJson(market_model);

                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        intent.putExtra("market_model", myGson2);
                        context.startActivity(intent);
                    });

                    // send message
                    relLayout_button.setOnClickListener(view -> {
                        // internet control
                        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                        if (!isConnected) {
                            CheckInternetStatus.internetIsConnected(context, parent);

                        } else {
                            closeKeyboard();

                            notify = true;
                            String msg = Objects.requireNonNull(edit_write_to_the_seller.getText()).toString();
                            if (!msg.isEmpty()){
                                sendMessage(user_id, user.getUser_id(), msg);
                            } else {
                                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                            }
                            edit_write_to_the_seller.setText("");
                        }
                    });

                    // follow user if is location
                    if (market_model.isLocation() || market_model.isSell()) {
                        follow_unfollow(user);
                        getFollowers(user);
                        // unfriend
                        relLayout_unfriend.setOnClickListener(view -> {
                            BottomSheetManegeFriend bottomSheetManegeFriend = new BottomSheetManegeFriend(context, user.getUser_id(),
                                    linLayout_not_yet_friend, null, relLayout_unfriend, null);
                            if (bottomSheetManegeFriend.isAdded())
                                return;
                            bottomSheetManegeFriend.show(context.getSupportFragmentManager(), "TAG");
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (market_model.isLocation() || market_model.isSell()) {
            Query query2 = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(market_model.getUser_id());

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        if (market_model.isLocation())
                            toolBar_textview.setText(context.getString(R.string.to_rent_out));
                        if (market_model.isSell())
                            toolBar_textview.setText(context.getString(R.string.sell));

                        tv_information_about_establishment.setText(context.getString(R.string.owner_information));
                        store_name.setText(user.getUsername());

                        Glide.with(context.getApplicationContext())
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);

                        // go user profile
                        parent.setOnClickListener(v -> {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    user.getUsername());

                            isIntentClicked = true;
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                intent = new Intent(context, Profile.class);

                            } else {
                                intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                            }
                            context.startActivity(intent);
                        });
                    }

                    if (market_model.isLocation()) {
                        state.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_user_stores))
                    .child(market_model.getStore_owner())
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(market_model.getStore_id());

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        Glide.with(context.getApplicationContext())
                                .load(market_model.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);

                        store_name.setText(market_model.getStore_name());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            parent.setOnClickListener(view -> {
                isIntentClicked = true;
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myJson = gson.toJson(market_model);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketStore.class);
                intent.putExtra("market_model", myJson);
                intent.putExtra("from_announce", "from_announce");
                context.startActivity(intent);
            });
        }

        if (market_model.isImageUne()) {
            relLayout_recyclerView.setVisibility(View.GONE);

            Glide.with(context.getApplicationContext())
                    .load(market_model.getUrli())
                    .into(one_image);

            one_image.setOnClickListener(view -> {
                isIntentClicked = true;
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketRecyclerFullImage.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(market_model);
                intent.putExtra("market_model", myGson);
                context.startActivity(intent);
            });

        } else {
            one_image.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(market_model.getUrli()))
                list.add(market_model.getUrli());

            if (!TextUtils.isEmpty(market_model.getUrlii()))
                list.add(market_model.getUrlii());

            if (!TextUtils.isEmpty(market_model.getUrliii()))
                list.add(market_model.getUrliii());

            if (!TextUtils.isEmpty(market_model.getUrliv()))
                list.add(market_model.getUrliv());

            if (!TextUtils.isEmpty(market_model.getUrlv()))
                list.add(market_model.getUrlv());

            if (!TextUtils.isEmpty(market_model.getUrlvi()))
                list.add(market_model.getUrlvi());

            if (!TextUtils.isEmpty(market_model.getUrlvii()))
                list.add(market_model.getUrlvii());

            if (!TextUtils.isEmpty(market_model.getUrlviii()))
                list.add(market_model.getUrlviii());

            if (!TextUtils.isEmpty(market_model.getUrlix()))
                list.add(market_model.getUrlix());

            if (!TextUtils.isEmpty(market_model.getUrlx()))
                list.add(market_model.getUrlx());

            if (!TextUtils.isEmpty(market_model.getUrlxi()))
                list.add(market_model.getUrlxi());

            if (!TextUtils.isEmpty(market_model.getUrlxii()))
                list.add(market_model.getUrlxii());

            if (!TextUtils.isEmpty(market_model.getUrlxiii()))
                list.add(market_model.getUrlxiii());

            if (!TextUtils.isEmpty(market_model.getUrlxiv()))
                list.add(market_model.getUrlxiv());

            if (!TextUtils.isEmpty(market_model.getUrlxv()))
                list.add(market_model.getUrlxv());

            if (!TextUtils.isEmpty(market_model.getUrlxvi()))
                list.add(market_model.getUrlxvi());

            if (!TextUtils.isEmpty(market_model.getUrlxvii()))
                list.add(market_model.getUrlxvii());

            MarketAboutArticleAdapter adapter = new MarketAboutArticleAdapter(context, list, market_model, relLayout_view_overlay);
            recyclerView.setAdapter(adapter);
            // le slidding: les pointillÃ©s du bas
            recyclerIndicator.attachToRecyclerView(recyclerView);
        }

        BottomSheetProductComments bottomSheetProductComments = new BottomSheetProductComments(context, market_model, relLayout_view_overlay);
        relLayout_comment_icon.setOnClickListener(view -> {
            Util.preventTwoClick(relLayout_comment_icon);

            try {
                if (bottomSheetProductComments.isAdded())
                    return;

                bottomSheetProductComments.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "bind: "+e.getMessage());
            }
        });

        like_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                    image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeLike();
                        }
                    });

                } else {
                    like_heart.setSelected(true);
                    image.setImageResource(R.drawable.ic_heart_red);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mLikedByCurrentUser) {
                                addNewLike();
                            }
                        }
                    });
                }
            }
        });

        arrowBack.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        // follow or unfollow the store
        isFollowing();

        // get the following model data
        HashMap<Object, Object> map = Utils_Map_StoreFollowingModel.storeFollowingModel(market_model.getStore_owner(), user_id, market_model.getStore_id());

        bouton_rejoindre.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_store_following))
                    .child(user_id)
                    .child(market_model.getStore_id())
                    .setValue(map);

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_store_followers))
                    .child(market_model.getStore_id())
                    .child(user_id)
                    .setValue(map);
            setFollowing();
        });

        bouton_quitter.setOnClickListener(v1 -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_store_following))
                    .child(user_id)
                    .child(market_model.getStore_id())
                    .removeValue();

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_store_followers))
                    .child(market_model.getStore_id())
                    .child(user_id)
                    .removeValue();
            setUnfollowing();
        });
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        bouton_rejoindre.setVisibility(View.GONE);
        bouton_quitter.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        bouton_rejoindre.setVisibility(View.VISIBLE);
        bouton_quitter.setVisibility(View.GONE);
    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(context.getString(R.string.dbname_store_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(market_model.getStore_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // other stores of the same seller
    private void otherto_rent_outObjects() {
        Query query = myRef
                .child(getString(R.string.dbname_market))
                .child(market_model.getStore_owner()) // user_id
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(market_model.getStore_owner());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, ds);

                    if (!market_model.getPhotoi_id().equals(marketModel.getPhotoi_id()))
                        if (!marketModel.isSuppressed())
                            list_to_rent_out.add(marketModel);
                }

                Collections.reverse(list_to_rent_out);

                RentalItemsAdapter adapter = new RentalItemsAdapter(context, list_to_rent_out, progressBar, progressBar_to_rent_out,
                        relLayout_view_overlay);
                recyclerView_to_rent_out.setLayoutManager(new LinearLayoutManager(context));
                recyclerView_to_rent_out.setAdapter(adapter);

                if (adapter.getItemCount() != 0) {
                    other_items_rented_by_this_seller.setVisibility(View.VISIBLE);
                } else {
                    progressBar_to_rent_out.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // other stores of the same seller
    private void otherStores() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .orderByChild(context.getString(R.string.field_store_owner))
                .equalTo(market_model.getStore_owner());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                    if (!marketModel.getStore_id().equals(market_model.getStore_id()))
                        if (!marketModel.isSuppressed())
                            list_store.add(marketModel);
                }

                Collections.reverse(list_store);

                StoreItemsAdapter adapter = new StoreItemsAdapter(context, list_store, progressBar, progressBar_to_rent_out,
                        relLayout_view_overlay);
                recyclerView_store.setLayoutManager(new LinearLayoutManager(context));
                recyclerView_store.setAdapter(adapter);

                if (adapter.getItemCount() != 0) {
                    seller_others_stores.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // send message to the seller
    private void sendMessage(String sender, final String receiver, String message){
        button_text.setVisibility(View.GONE);
        button_progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Date date=new Date();

        HashMap<String, Object> hashMap = Utils_Map_Chat.getChat("","", sender, receiver, "",
                message, "", false, market_model.getUrli(), "", "photoSimple", date.getTime(),
                market_model.getStore_id(), market_model.getPhotoi_id(), "no");

        // sender
        reference.child(getString(R.string.dbname_chat))
                .push()
                .setValue(hashMap).addOnSuccessListener(unused -> {

                    button_text.setVisibility(View.VISIBLE);
                    button_progressBar.setVisibility(View.GONE);
                });
    }

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_market_media))
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mLikedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update like count
                        int count = Integer.parseInt(number_of_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            number_of_likes.setVisibility(View.INVISIBLE);
                        number_of_likes.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_market_media))
                                .child(market_model.getPhotoi_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_market))
                                .child(market_model.getStore_id())
                                .child(market_model.getPhotoi_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        getLikesString();
                        getUpdateLikesString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(number_of_likes.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            number_of_likes.setVisibility(View.VISIBLE);
        number_of_likes.setText(str);

        // add new like
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_market_media))
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_market))
                .child(market_model.getStore_id())
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString();
        getUpdateLikesString();
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_market_media))
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_likes));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsers.append(user.getUsername());
                                mUsers.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikeString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupLikeString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUpdateLikesString() {
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_market_media))
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_likes));

        // on parcours tous les likes
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsers.append(user.getUsername());
                                mUsers.append(",");
                            }

                            mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikeString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mLikedByCurrentUser = false;
                    setupLikeString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupLikeString() {
        if (mLikedByCurrentUser) {
            if (!like_heart.isSelected()) {
                like_heart.setSelected(true);
                image.setImageResource(R.drawable.ic_heart_red);
            }

        } else {
            if (like_heart.isSelected()) {
                like_heart.setSelected(false);
                image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    private void getCurrentUser(){
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    mCurrentUser = Util_User.getUser(context, objectMap, ds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public void setLikes() {
        Query query = myRef
                .child(context.getString(R.string.dbname_market_media))
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    number_of_likes.setVisibility(View.VISIBLE);
                    number_of_likes.setText(String.valueOf(likes_count));

                    number_of_likes.setOnClickListener(view -> {
                        isIntentClicked = true;
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Likers.class);
                        intent.putStringArrayListExtra("liker_list", liker_list);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setComments() {
        Query query = myRef
                .child(context.getString(R.string.dbname_market_media))
                .child(market_model.getPhotoi_id())
                .child(context.getString(R.string.field_comments));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_market_media))
                            .child(market_model.getPhotoi_id())
                            .child(context.getString(R.string.field_comments))
                            .child(keyID)
                            .child(context.getString(R.string.field_comments));

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: data: "+data);
                                comments_count++;
                            }

                            if (comments_count >= 1) {
                                number_of_comments.setVisibility(View.VISIBLE);
                                number_of_comments.setText(String.valueOf(comments_count));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // section follow ______________________________________________________________________________
    private void getFollowers(User user) {
        // verify is users are friends
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_friend_follower))
                .child(user.getUser_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());

                    // users are  friend
                    relLayout_unfriend.setVisibility(View.VISIBLE);
                    // users are not yet friend
                    linLayout_not_yet_friend.setVisibility(View.GONE);
                }

                if (!snapshot.exists()) {
                    // verify if user is follower
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_followers))
                            .child(user.getUser_id())
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(user_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String ID = dataSnapshot.getKey();

                                // users are  friend
                                relLayout_unfriend.setVisibility(View.GONE);
                                // users are not yet friend
                                linLayout_not_yet_friend.setVisibility(View.VISIBLE);
                            }

                            if (!snapshot.exists()) {
                                // users are  friend
                                relLayout_unfriend.setVisibility(View.GONE);
                                // users are not yet friend
                                linLayout_not_yet_friend.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void follow_unfollow(User user) {
        // follow unfollow person
        // public account
        if (user.getScope().equals(context.getString(R.string.title_public))) {
            isFollowing_public_account(user);

            HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
            HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

            button_follow.setOnClickListener(view -> {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_following))
                        .child(user_id)
                        .child(user.getUser_id())
                        .setValue(map_other_user);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_followers))
                        .child(user.getUser_id())
                        .child(user_id)
                        .setValue(map_current_user);
                setFollowing_public_account();
            });

            button_unfollow.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_following))
                        .child(user_id)
                        .child(user.getUser_id())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_followers))
                        .child(user.getUser_id())
                        .child(user_id)
                        .removeValue();
                setUnfollowing_public_account();
            });
        }
        // private account
        if (user.getScope().equals(context.getString(R.string.title_private))) {
            // verify if user is already following
            Query query1 = myRef.child(context.getString(R.string.dbname_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUser_id());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+ds.getValue());

                        isFollowing_public_account(user);

                        button_follow.setOnClickListener(view -> {
                            HashMap<Object, Object> map_current_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user_id);
                            HashMap<Object, Object> map_other_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user.getUser_id());

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .setValue(map_other_user);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .setValue(map_current_user);
                            setFollowing_subscription_request();
                        });

                        button_unfollow.setOnClickListener(v -> {
                            // show dialog box
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                            TextView dialog_title = view.findViewById(R.id.dialog_title);
                            TextView dialog_text = view.findViewById(R.id.dialog_text);
                            TextView negativeButton = view.findViewById(R.id.tv_no);
                            TextView positiveButton = view.findViewById(R.id.tv_yes);
                            builder.setView(view);

                            Dialog d = builder.create();
                            d.show();

                            negativeButton.setText(context.getString(R.string.cancel));
                            positiveButton.setText(context.getString(R.string.unsubscribe));

                            dialog_title.setVisibility(View.GONE);
                            dialog_text.setText(Html.fromHtml(context.getString(R.string.would_you_like_to_stop_following)+" "
                                    +user.getUsername()+" ?"));

                            negativeButton.setOnClickListener(view2 -> d.dismiss());

                            positiveButton.setOnClickListener(view1 -> {
                                d.dismiss();
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_following))
                                        .child(user_id)
                                        .child(user.getUser_id())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_followers))
                                        .child(user.getUser_id())
                                        .child(user_id)
                                        .removeValue();
                                setUnfollowing_subscription_request();
                            });
                        });
                    }

                    if (!snapshot.exists()) {
                        isFollowing_subscription_request(user);

                        HashMap<Object, Object> map_current_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user_id);
                        HashMap<Object, Object> map_other_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user.getUser_id());

                        button_follow.setOnClickListener(view -> {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .setValue(map_other_user);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .setValue(map_current_user);
                            setFollowing_subscription_request();
                        });

                        button_sent.setOnClickListener(v -> {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_following))
                                    .child(user_id)
                                    .child(user.getUser_id())
                                    .removeValue();

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_subscription_request_follower))
                                    .child(user.getUser_id())
                                    .child(user_id)
                                    .removeValue();
                            setUnfollowing_subscription_request();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // follow unfollow person ______________________________________________________________________
    // public
    private void setFollowing_public_account(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_follow.setVisibility(View.GONE);
        button_unfollow.setVisibility(View.VISIBLE);
        button_sent.setVisibility(View.GONE);
    }

    private void setUnfollowing_public_account(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_follow.setVisibility(View.VISIBLE);
        button_unfollow.setVisibility(View.GONE);
        button_sent.setVisibility(View.GONE);
    }

    private void isFollowing_public_account(User user){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing_public_account();
                }

                if (!snapshot.exists()) {
                    setUnfollowing_public_account();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // private
    private void setFollowing_subscription_request(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        button_follow.setVisibility(View.GONE);
        button_unfollow.setVisibility(View.GONE);
        button_sent.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing_subscription_request(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        button_follow.setVisibility(View.VISIBLE);
        button_unfollow.setVisibility(View.GONE);
        button_sent.setVisibility(View.GONE);
    }

    private void isFollowing_subscription_request(User user){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_subscription_request_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing_subscription_request();
                }

                if (!snapshot.exists()) {
                    setUnfollowing_subscription_request();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to avoid crash
        if (from_chat == null) {
            CheckInternetStatus.internetIsConnected(context, parent1);

            // set the current user profile visit
            if (!market_model.getStore_owner().equals(user_id)) {
                if (!isIntentClicked) {
                    ViewsMarketProductManager marketProductManager = new ViewsMarketProductManager(context);
                    if (from_seller_store != null) {
                        marketProductManager.incrementViewCount(market_model.getStore_id(), market_model.getPhotoi_id());

                    } else {
                        marketProductManager.incrementViewCount(market_model.getUser_id(), market_model.getPhotoi_id());
                    }
                }
            }
        }
    }
}