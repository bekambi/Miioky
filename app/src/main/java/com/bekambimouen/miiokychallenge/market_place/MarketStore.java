package com.bekambimouen.miiokychallenge.market_place;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile.SeeUserAllProfileOnMiioky;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.friends.bottomsheet.BottomSheetManegeFriend;
import com.bekambimouen.miiokychallenge.friends.utils_map.Utils_Map_SubscriptionRequestModel;
import com.bekambimouen.miiokychallenge.interfaces.BooleanInfoListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.market_place.adapter.MarketGridAdapter;
import com.bekambimouen.miiokychallenge.market_place.comment.store_comment.bottomsheet.BottomSheetStoreComments;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.publication.MarketSellInStore;
import com.bekambimouen.miiokychallenge.market_place.update.UpdateStoreData;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Utils_Map_StoreFollowingModel;
import com.bekambimouen.miiokychallenge.views_count.ViewsMarketStoreManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarketStore extends AppCompatActivity {
    private static final String TAG = "MarketSellerInStore";

    // widggets
    private SmallBangView like_heart;
    private ImageView image;
    private CircleImageView seller_profile_photo, profile_photo_store;
    private ImageView profile_photo;
    private TextView toolBar_store_name;
    private TextView date_following;
    private TextView description;
    private TextView number_of_announce;
    private TextView seller_name;
    private TextView store_name;
    private TextView neighborhood;
    private TextView catalog;
    private TextView view_the_menu;
    private TextView number_of_likes;
    private TextView number_of_comments;
    private TextView button_follow, button_unfollow, button_sent;
    private TextView bouton_rejoindre_store;
    private TextView bouton_quitter_store;
    private RecyclerView recyclerView;
    private RelativeLayout parent1, relLayout_add, relLayout_comment_icon, relLayout_view_overlay;
    private LinearLayout linLayout_not_yet_friend, linLayout_already_friend;

    // vars
    private MarketStore context;
    private MarketModel market_model;
    private String from_announce;
    private List<MarketModel> list;
    private Dialog d;
    private int t;
    private BooleanInfoListener booleanInfoListener;
    private boolean isFirstTimeStrokeColor = false, isClickedInAdapter = false, isIntentClicked = false;
    // likes and comments ____________________________________
    private StringBuilder mUsers;
    private User mCurrentUser;
    private ArrayList<String> liker_list;
    private int likes_count;
    private int comments_count;
    private boolean mLikedByCurrentUser;

    private CustomShareProgressView progressDialog;
    private void showLoading(){
        if (progressDialog == null)
            progressDialog = new CustomShareProgressView(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_market_store);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        list = new ArrayList<>();
        liker_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
            from_announce = getIntent().getStringExtra("from_announce");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void init() {
        parent1 = findViewById(R.id.parent1);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout relLayout_see_the_profile = findViewById(R.id.relLayout_see_the_profile);
        RelativeLayout relLayout_follow = findViewById(R.id.relLayout_follow);
        LinearLayout linLayout_about_store = findViewById(R.id.linLayout_about_store);
        toolBar_store_name = findViewById(R.id.toolBar_store_name);
        profile_photo = findViewById(R.id.profile_photo);
        seller_profile_photo = findViewById(R.id.seller_profile_photo);
        seller_name = findViewById(R.id.seller_name);
        date_following = findViewById(R.id.date_following);
        TextView about_store = findViewById(R.id.about_store);
        description = findViewById(R.id.description);
        number_of_announce = findViewById(R.id.number_of_announce);
        button_follow = findViewById(R.id.button_follow);
        button_unfollow = findViewById(R.id.button_unfollow);
        button_sent = findViewById(R.id.button_sent);
        relLayout_comment_icon = findViewById(R.id.relLayout_comment_icon);
        image = findViewById(R.id.image);
        like_heart = findViewById(R.id.like_heart);
        number_of_likes = findViewById(R.id.number_of_likes);
        number_of_comments = findViewById(R.id.number_of_comments);

        profile_photo_store = findViewById(R.id.profile_photo_store);
        store_name = findViewById(R.id.store_name);
        neighborhood = findViewById(R.id.neighborhood);
        catalog = findViewById(R.id.catalog);
        view_the_menu = findViewById(R.id.view_the_menu);
        TextView views = findViewById(R.id.views);
        bouton_quitter_store = findViewById(R.id.bouton_quitter_store);
        bouton_rejoindre_store = findViewById(R.id.bouton_rejoindre_store);
        relLayout_add = findViewById(R.id.relLayout_add);
        RelativeLayout relLayout_section_follow = findViewById(R.id.relLayout_section_follow);
        RelativeLayout relLayout_unfriend = findViewById(R.id.relLayout_unfriend);
        RelativeLayout relLayout_discussion_friend = findViewById(R.id.relLayout_discussion_friend);
        RelativeLayout relLayoutDiscussion = findViewById(R.id.relLayoutDiscussion);
        linLayout_not_yet_friend = findViewById(R.id.linLayout_not_yet_friend);
        linLayout_already_friend = findViewById(R.id.linLayout_already_friend);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 10, false));

        // delete or update announce
        if (market_model.getStore_owner().equals(user_id)) {
            relLayout_add.setVisibility(View.VISIBLE);
            relLayout_section_follow.setVisibility(View.GONE);
            relLayout_follow.setVisibility(View.GONE);
        }

        setLikes();
        setComments();
        getCurrentUser();
        getLikesString();
        getUpdateLikesString();

        // set store views
        if (market_model.getViews() != 0) {
            views.setVisibility(View.VISIBLE);
            if (market_model.getViews() == 1)
                views.setText(Html.fromHtml(context.getString(R.string.visitor)+": <b>"+market_model.getViews()+"</b>"));
            else
                views.setText(Html.fromHtml(context.getString(R.string.visitors)+": <b>"+market_model.getViews()+"</b>"));
        }

        // to prevent reload views count when coming from Market About Product
        booleanInfoListener = isClickedFromAdapter -> isClickedInAdapter = isClickedFromAdapter;

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
                    MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, ds);

                    String the_menu = marketModel.getRestaurant_menu();

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

            d = builder.create();
            d.show();

            // hide keyboard when dialog box dismiss
            closeKeyboard();

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
                        MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        restaurant_name.setText(marketModel.getStore_name());
                        the_menu.setText(marketModel.getRestaurant_menu());
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
            popup.inflate(R.menu.menu_store);
            //adding click listener
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_sell) {
                    isIntentClicked = true;
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    String myJson;
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MarketSellInStore.class);
                    myJson = gson.toJson(market_model);
                    intent.putExtra("market_model", myJson);
                    context.startActivity(intent);

                } else if (menuItem.getItemId() == R.id.menu_edit_menu) {
                    showKeyboard();
                    // show dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v = LayoutInflater.from(context).inflate(R.layout.layout_market_update_menu_of_restaurant, null);

                    MyEditText edit_menu_of_the_day = v.findViewById(R.id.edit_menu_of_the_day);
                    TextView button = v.findViewById(R.id.button);
                    RelativeLayout relLayout_menu_of_the_day = v.findViewById(R.id.relLayout_menu_of_the_day);
                    builder.setView(v);

                    d = builder.create();
                    d.show();

                    // hide keyboard when dialog box is dismiss
                    d.setOnDismissListener(dialogInterface -> {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    });

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
                        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent1);

                        if (!isConnected) {
                            CheckInternetStatus.internetIsConnected(context, parent1);

                        } else {
                            String getMenu = Objects.requireNonNull(edit_menu_of_the_day.getText()).toString();

                            if (TextUtils.isEmpty(getMenu)) {
                                isFirstTimeStrokeColor = true;
                                GradientDrawable drawable = (GradientDrawable) relLayout_menu_of_the_day.getBackground();
                                drawable.mutate();
                                drawable.setStroke(3, Color.RED);
                                return;
                            }

                            closeKeyboard();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("restaurant_menu", getMenu);

                            myRef.child(context.getString(R.string.dbname_user_stores))
                                    .child(market_model.getStore_owner())
                                    .child(market_model.getStore_id())
                                    .updateChildren(map).addOnSuccessListener(unused -> {
                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();

                                        d.dismiss();
                                    });
                        }
                    });

                } else if (menuItem.getItemId() == R.id.menu_update) {
                    Query myQuery = myRef
                            .child(context.getString(R.string.dbname_user_stores))
                            .child(user_id)
                            .orderByChild(context.getString(R.string.field_store_id))
                            .equalTo(market_model.getStore_id());

                    myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                                isIntentClicked = true;
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(market_model);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, UpdateStoreData.class);
                                intent.putExtra("market_model", myJson);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (menuItem.getItemId() == R.id.menu_delete) {
                    // show dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View v12 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                    TextView dialog_title = v12.findViewById(R.id.dialog_title);
                    TextView dialog_text = v12.findViewById(R.id.dialog_text);
                    TextView negativeButton = v12.findViewById(R.id.tv_no);
                    TextView positiveButton = v12.findViewById(R.id.tv_yes);
                    builder.setView(v12);

                    Dialog d = builder.create();
                    d.show();

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.delete));

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.be_careful_you_are_about_to_delete_this_store)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view2 -> {
                        // internet control
                        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent1);

                        if (!isConnected) {
                            CheckInternetStatus.internetIsConnected(context, parent1);

                        } else {
                            d.dismiss();
                            showLoading();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", true);

                            // first delete product in the store
                            Query query = FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbname_market))
                                    .child(market_model.getStore_id()) // store_id
                                    .orderByChild(context.getString(R.string.field_store_id))
                                    .equalTo(market_model.getStore_id());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                        marketModel.setSuppressed(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query2 = FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbname_market_media))
                                    .orderByChild(context.getString(R.string.field_store_id))
                                    .equalTo(market_model.getStore_id());

                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                        marketModel.setSuppressed(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_user_stores_media))
                                    .child(market_model.getStore_id())
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_user_stores))
                                    .child(user_id)
                                    .child(market_model.getStore_id())
                                    .updateChildren(map).addOnSuccessListener(unused1 -> {
                                        progressDialog.dismiss();
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

            // hide some enu items
            Menu popupMenu = popup.getMenu();

            if (market_model.isBakery() || market_model.isStore()) {
                popupMenu.findItem(R.id.menu_edit_menu).setVisible(false);
            }

            //displaying the popup
            popup.show();
        });

        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(market_model.getStore_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                    toolBar_store_name.setText(market_model.getStore_name());
                    store_name.setText(market_model.getStore_name());
                    neighborhood.setText(market_model.getNeighborhood_name());
                    String store_description = market_model.getStore_message();

                    if (!TextUtils.isEmpty(store_description))
                        linLayout_about_store.setVisibility(View.VISIBLE);

                    description.setText(market_model.getStore_message());

                    about_store.setText(Html.fromHtml(context.getString(R.string.about_the)+" "+market_model.getStore_name()));

                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(market_model.getProfile_photo())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(market_model.getProfile_photo())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo_store);

                    // number of announce
                    Query query2 = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_market))
                            .child(market_model.getStore_id()) // store_id
                            .orderByChild(context.getString(R.string.field_store_id))
                            .equalTo(market_model.getStore_id());

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+ds.getKey());
                                t++;
                            }

                            if (t == 1)
                                number_of_announce.setText(Html.fromHtml("<b> "+t+"+</b> "
                                        +context.getString(R.string.announce_on)+" "+market_model.getStore_name()));
                            else
                                number_of_announce.setText(Html.fromHtml("<b> "+t+"+</b> "
                                        +context.getString(R.string.announces_on)+" "+market_model.getStore_name()));
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

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(market_model.getStore_owner());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    seller_name.setText(user.getUsername());

                    long tv_date = user.getDate_created();
                    date_following.setText(Html.fromHtml(context.getString(R.string.joined_miioky)+" "
                            +context.getString(R.string.from)+" "+ TimeUtils.getTime(context, tv_date)));

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(seller_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    seller_profile_photo.setOnClickListener(view -> {
                        isIntentClicked = true;
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, SeeUserAllProfileOnMiioky.class);
                        intent.putExtra("userID", user.getUser_id());
                        startActivity(intent);
                    });

                    // go chat with the seller
                    relLayout_discussion_friend.setOnClickListener(view -> {
                        isIntentClicked = true;
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    });
                    relLayoutDiscussion.setOnClickListener(view -> {
                        isIntentClicked = true;
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    });

                    // go to seller profile
                    relLayout_see_the_profile.setOnClickListener(v -> {
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

                    follow_unfollow(user);
                    getFollowers(user);
                    // unfriend
                    relLayout_unfriend.setOnClickListener(view -> {
                        BottomSheetManegeFriend bottomSheetManegeFriend = new BottomSheetManegeFriend(context, user.getUser_id(),
                                linLayout_not_yet_friend, linLayout_already_friend, null, null);
                        if (bottomSheetManegeFriend.isAdded())
                            return;
                        bottomSheetManegeFriend.show(context.getSupportFragmentManager(), "TAG");
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // grid view
        Query query2 = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_market))
                .child(market_model.getStore_id()) // store_id
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(market_model.getStore_id());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    MarketModel marketModel = Util_MarketModel.getMarketModel(context, objectMap, ds);

                    if (from_announce != null) {
                        if (!marketModel.getPhotoi_id().equals(market_model.getPhotoi_id()))
                            if (!marketModel.isSuppressed())
                                list.add(marketModel);
                    } else {
                        if (!marketModel.isSuppressed())
                            list.add(marketModel);
                    }
                }

                //sort for newest to oldest
                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                MarketGridAdapter adapter = new MarketGridAdapter(context, list, booleanInfoListener, relLayout_view_overlay);
                recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() != 0)
                    catalog.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomSheetStoreComments bottomSheetStoreComments = new BottomSheetStoreComments(context, market_model, relLayout_view_overlay);
        relLayout_comment_icon.setOnClickListener(view -> {
            Util.preventTwoClick(relLayout_comment_icon);
            try {
                if (bottomSheetStoreComments.isAdded())
                    return;
                bottomSheetStoreComments.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "bind: error: "+e.getMessage());
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

        // get out or jfollow the store
        isFollowing_store();

        // get the following model data
        HashMap<Object, Object> map = Utils_Map_StoreFollowingModel.storeFollowingModel(market_model.getStore_owner(), user_id, market_model.getStore_id());

        bouton_rejoindre_store.setOnClickListener(view -> {
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
            setFollowing_store();
        });

        bouton_quitter_store.setOnClickListener(v1 -> {
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
            setUnfollowing_store();
        });
    }

    // follow or unfollow sstore
    private void setFollowing_store(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        bouton_rejoindre_store.setVisibility(View.GONE);
        bouton_quitter_store.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing_store(){
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        bouton_rejoindre_store.setVisibility(View.VISIBLE);
        bouton_quitter_store.setVisibility(View.GONE);
    }

    private void isFollowing_store(){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing_store();

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

                    setFollowing_store();
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
    //______________________________________________________________________________________________
    private void getFollowers(User user) {
        // verify if account is private _______________________________________
        if (user.getScope().equals(context.getString(R.string.title_private))) {
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
                        linLayout_already_friend.setVisibility(View.VISIBLE);
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
                                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
                                    // users are not yet friend
                                    linLayout_not_yet_friend.setVisibility(View.VISIBLE);
                                }

                                if (!snapshot.exists()) {
                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
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

        } else {
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
                        linLayout_already_friend.setVisibility(View.VISIBLE);
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
                                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
                                    // users are not yet friend
                                    linLayout_not_yet_friend.setVisibility(View.VISIBLE);
                                }

                                if (!snapshot.exists()) {
                                    // users are  friend
                                    linLayout_already_friend.setVisibility(View.GONE);
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
    }

    private void follow_unfollow(User user) {
        // follow unfollow person ______________________________________________________________
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

    // like and comment

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
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
                        myRef.child(context.getString(R.string.dbname_user_stores_media))
                                .child(market_model.getStore_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_user_stores))
                                .child(market_model.getStore_owner())
                                .child(market_model.getStore_id())
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
        myRef.child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .child(market_model.getStore_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        // affichage Ã  l'Ã©cran
        getLikesString();
        getUpdateLikesString();
    }

    private void getLikesString(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .child(context.getString(R.string.field_likes));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on rÃ©cupÃ¨re l'identifiant du likeur et le like
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

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
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
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .child(context.getString(R.string.field_likes));

        // on parcours tous les likes
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on rÃ©cupÃ¨re l'identifiant du likeur et le like
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

    private void setLikes() {
        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
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

    private void setComments() {
        comments_count = 0;

        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .child(context.getString(R.string.field_comments));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_user_stores_media))
                            .child(market_model.getStore_id())
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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent1);

        // set the current user profile visit
        if (!market_model.getStore_owner().equals(user_id)) {
            if (!isClickedInAdapter && !isIntentClicked) {
                ViewsMarketStoreManager marketStoreManager = new ViewsMarketStoreManager(context);
                marketStoreManager.incrementViewCount(market_model, market_model.getStore_id());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (d != null)
            d.dismiss();
    }
}