package com.bekambimouen.miiokychallenge.market_place.publication;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.NumberTextWatcher;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGroupGalleryPhotosFolders;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.GroupImageAdapter;
import com.bekambimouen.miiokychallenge.interfaces.CategoryListener;
import com.bekambimouen.miiokychallenge.interfaces.OnPicPhotoListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.bottomsheet.BottomSheetMarketCategory;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.publication.adapter.MarketSelectedImageAdapter;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MarketSellInStore extends AppCompatActivity {
    private static final String TAG = "MarketSell";

    // widgets _________________________________________
    private CircleImageView profile_photo;
    private ImageView camera_id;
    private TextView photos_max, store_name, txt_category;
    private MyEditText edit_product_name, edit_old_price, edit_price,
            edit_description;
    private RelativeLayout parent, relLayout_post, relLayout_product,
            relLayout_price, relLayout_category, relLayout_post_product, relLayout_view_overlay;
    private RadioButton radio_new, radio_second_hand, radio_old;

    // vars
    private MarketSellInStore context;
    private String productName, category_choice, price;
    private OnPicPhotoListener onPicPhotoListener;
    private String product_state = "";
    private boolean isFirstTime_stroke_product_name = false, isFirstTime_stroke_edit_price = false;
    // widgets pic photo _________________________________________

    // widgets
    private RecyclerView imageRecyclerView, recyclerView;
    private ScrollingPagerIndicator recyclerIndicator ;
    private TextView number;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_pic_photo;

    private RelativeLayout relLayout_button_ok;
    private LinearLayout linLayout_add_photo;

    //vars
    private MarketModel market_model;
    private ArrayList<String> url0, photo_id_list, members_id_list;
    private boolean isSelectedEmpty = true;
    private int nber = 0;
    private boolean isScreenEnabled = true, isPickPhotoLayoutVisible = false;

    private Bitmap bmp;
    private ByteArrayOutputStream baos;

    // selection multiple
    public static final int PICK_IMAGES = 3;
    private GroupImageAdapter imageAdapter;
    private MarketSelectedImageAdapter selectedImageAdapter;
    private ArrayList<ImageModel> imageList;
    private ArrayList<String> selectedImageList;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isPickPhoto = false;

    //firebase
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_market_sell_in_store);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        selectPhotos();
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
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        RelativeLayout relLayout_photo = findViewById(R.id.relLayout_photo);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview_choose_photo);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        relLayout_post_product = findViewById(R.id.relLayout_post_product);
        relLayout_pic_photo = findViewById(R.id.relLayout_pic_photo);
        relLayout_post = findViewById(R.id.relLayout_post);
        relLayout_product = findViewById(R.id.relLayout_product);
        relLayout_price = findViewById(R.id.relLayout_price);
        relLayout_category = findViewById(R.id.relLayout_category);
        LinearLayout linLayout_product_state = findViewById(R.id.linLayout_product_state);
        radio_new = findViewById(R.id.radio_new);
        radio_second_hand = findViewById(R.id.radio_second_hand);
        radio_old = findViewById(R.id.radio_old);
        profile_photo = findViewById(R.id.profile_photo);
        camera_id = findViewById(R.id.camera_id);
        photos_max = findViewById(R.id.photos_max);
        store_name = findViewById(R.id.store_name);
        txt_category = findViewById(R.id.txt_category);
        TextView country_devise1 = findViewById(R.id.country_devise1);
        TextView country_devise2 = findViewById(R.id.country_devise2);
        edit_product_name = findViewById(R.id.edit_product_name);
        edit_old_price = findViewById(R.id.edit_old_price);
        edit_old_price.setHint(Html.fromHtml("<s>"+context.getString(R.string.old_price)+"</s>"));
        edit_price = findViewById(R.id.edit_price);
        edit_description = findViewById(R.id.edit_description);

        if (market_model.isRestaurant()) {
            relLayout_category.setVisibility(View.GONE);
            edit_product_name.setHint(context.getString(R.string.name_the_dish));
            linLayout_product_state.setVisibility(View.GONE);
        }

        if (market_model.isBakery()) {
            relLayout_category.setVisibility(View.GONE);
            edit_product_name.setHint(context.getString(R.string.name_the_pastry));
            linLayout_product_state.setVisibility(View.GONE);
        }

        edit_product_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTime_stroke_product_name) {
                    isFirstTime_stroke_product_name = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_product.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTime_stroke_edit_price) {
                    isFirstTime_stroke_edit_price = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_price.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // radio button items
        radio_new.setOnClickListener(view -> {

            radio_second_hand.setChecked(false);
            radio_old.setChecked(false);

            product_state = context.getString(R.string.radio_new);
        });

        radio_second_hand.setOnClickListener(view -> {

            radio_new.setChecked(false);
            radio_old.setChecked(false);

            product_state = context.getString(R.string.radio_second_hand);
        });

        radio_old.setOnClickListener(view -> {

            radio_new.setChecked(false);
            radio_second_hand.setChecked(false);

            product_state = context.getString(R.string.radio_old);
        });

        // country devise
        country_devise1.setText(GetCountryDevise());
        country_devise2.setText(GetCountryDevise());

        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(market_model.getStore_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                    store_name.setText(market_model.getStore_name());

                    Glide.with(context.getApplicationContext())
                            .load(market_model.getProfile_photo())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        relLayout_post.setOnClickListener(view -> {
            relLayout_post_product.setVisibility(View.GONE);
            relLayout_pic_photo.setVisibility(View.VISIBLE);
            isPickPhotoLayoutVisible = true;
        });

        // get category from bottomSheet
        CategoryListener categoryListener = category -> {
            txt_category.setText(category);
            txt_category.setTextColor(context.getColor(R.color.caption_color));
        };

        BottomSheetMarketCategory bottomSheetMarketCategory = new BottomSheetMarketCategory(context, relLayout_category, categoryListener);
        relLayout_category.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(relLayout_category);

            try {
                if (bottomSheetMarketCategory.isAdded())
                    return;

                bottomSheetMarketCategory.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        relLayout_photo.setOnClickListener(view -> {
            if (selectedImageList.size() >= 17) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_seventeen_photos),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                takePicture();
            }
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGroupGalleryPhotosFolders bottomSheet2 = new BottomSheetGroupGalleryPhotosFolders(context, imageAdapter, imageList, toolBar_textview);
            // prevent two clicks
            Util.preventTwoClick(menu_item);
            try {
                if (bottomSheet2.isAdded())
                    return;
                bottomSheet2.show(context.getSupportFragmentManager(), TAG);

            } catch (NullPointerException e) {
                Log.d(TAG, "init: "+e.getMessage());
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
                if (!isSelectedEmpty) {
                    isSelectedEmpty = true;
                    for (int i = 0; i < imageList.size(); i++) {
                        try {
                            if (imageList.get(i).isSelected()) {
                                imageList.get(i).setSelected(false);
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBackPressed: "+e.getMessage());
                        }
                    }

                    selectedImageList.clear();
                    if (View.VISIBLE == camera_id.getVisibility())
                        camera_id.setVisibility(View.GONE);

                    if (View.VISIBLE == recyclerView.getVisibility())
                        recyclerView.setVisibility(View.GONE);

                    if(selectedImageAdapter != null)
                        selectedImageAdapter = null;

                    if (market_model.isStore())
                        photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> 0/10 "
                                +context.getString(R.string.market_max_post_photo_store)));

                    if (market_model.isRestaurant())
                        photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> 0/10 "
                                +context.getString(R.string.market_max_post_photo_restaurant)));

                    if (market_model.isBakery())
                        photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> 0/10 "
                                +context.getString(R.string.market_max_post_photo_bakery)));

                    linLayout_add_photo.setVisibility(View.VISIBLE);
                    recyclerIndicator.setVisibility(View.GONE);

                    nber = 0;
                    relLayout_button_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                    imageAdapter.notifyDataSetChanged();

                    edit_old_price.setText("");
                    edit_price.setText("");

                    GradientDrawable drawable = (GradientDrawable) relLayout_post.getBackground();
                    drawable.mutate();
                    drawable.setStroke(0, Color.WHITE);

                } else if (isPickPhotoLayoutVisible) {
                    isPickPhotoLayoutVisible = false;
                    relLayout_post_product.setVisibility(View.VISIBLE);
                    relLayout_pic_photo.setVisibility(View.GONE);

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    // get country devise money
    private String GetCountryDevise() {
        String CountryID;
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = Currency.getInstance(new Locale("en", g[0])).getSymbol().toLowerCase();

                // format text inside editText _____________________________________________________
                // old price
                Locale locale1 = new Locale("en", g[0]); // For example Argentina
                int numDecs1 = 2; // Let's use 2 decimals
                TextWatcher tw1 = new NumberTextWatcher(edit_old_price, locale1, numDecs1);
                edit_old_price.addTextChangedListener(tw1);

                // current price
                Locale locale = new Locale("en", g[0]); // For example Argentina
                int numDecs = 2; // Let's use 2 decimals
                TextWatcher tw = new NumberTextWatcher(edit_price, locale, numDecs);
                edit_price.addTextChangedListener(tw);
                // _________________________________________________________________________________
                break;
            }
        }
        return CountryZipCode;
    }

    // pic photo ___________________________________________________________________________________
    private void selectPhotos() {
        imageList = new ArrayList<>();
        selectedImageList = new ArrayList<>();
        url0 = new ArrayList<>();
        photo_id_list = new ArrayList<>();
        members_id_list = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        initPicPhoto();
        getAllImages();
        setImageList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initPicPhoto() {
        progressBar = findViewById(R.id.progressBar);
        RelativeLayout relLayout_arrowBack_choose_photo = findViewById(R.id.relLayout_arrowBack_choose_photo);
        relLayout_button_ok = findViewById(R.id.relLayout_button_ok);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        linLayout_add_photo = findViewById(R.id.linLayout_add_photo);

        imageRecyclerView = findViewById(R.id.pub_RecyclerView_photo);
        recyclerIndicator  = findViewById(R.id.recyclerIndicator );
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        number = findViewById(R.id.number);

        if (market_model.isStore())
            photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> 0/10 "
                    +context.getString(R.string.market_max_post_photo_store)));

        if (market_model.isRestaurant())
            photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> 0/10 "
                    +context.getString(R.string.market_max_post_photo_restaurant)));

        if (market_model.isBakery())
            photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> 0/10 "
                    +context.getString(R.string.market_max_post_photo_bakery)));

        // listener interface to verify if pic photo layout is visible
        onPicPhotoListener = isLayoutVisible -> isPickPhotoLayoutVisible = isLayoutVisible;

        relLayout_button_ok.setOnClickListener(view -> {
            isPickPhotoLayoutVisible = false;
            relLayout_post_product.setVisibility(View.VISIBLE);
            linLayout_add_photo.setVisibility(View.GONE);
            relLayout_pic_photo.setVisibility(View.GONE);
            if (selectedImageList.size() == 1) {
                camera_id.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                recyclerIndicator.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext())
                        .load(selectedImageList.get(0))
                        .into(camera_id);

                if (market_model.isStore())
                    photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)
                            +" 1 </b>"+context.getString(R.string.market_max_post_photo_store)));

                if (market_model.isRestaurant())
                    photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)
                            +" 1 </b>"+context.getString(R.string.market_max_post_photo_restaurant)));

                if (market_model.isBakery())
                    photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)
                            +" 1 </b>"+context.getString(R.string.market_max_post_photo_bakery)));

            } else {
                camera_id.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerIndicator.setVisibility(View.VISIBLE);

                selectedImageAdapter = new MarketSelectedImageAdapter(context, selectedImageList,
                        relLayout_post_product, relLayout_pic_photo, onPicPhotoListener);
                recyclerView.setAdapter(selectedImageAdapter);
                // le slidding: les pointillÃ©s du bas
                recyclerIndicator.attachToRecyclerView(recyclerView);

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int position = ((LinearLayoutManager) requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                            if (market_model.isStore())
                                photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> "+
                                        ((position + 1))+"/"+selectedImageList.size()+" "+context.getString(R.string.market_max_post_photo_store)));

                            if (market_model.isRestaurant())
                                photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> "+
                                        ((position + 1))+"/"+selectedImageList.size()+" "+context.getString(R.string.market_max_post_photo_restaurant)));

                            if (market_model.isBakery())
                                photos_max.setText(Html.fromHtml("<b>"+context.getString(R.string.photos)+"</b> "+
                                        ((position + 1))+"/"+selectedImageList.size()+" "+context.getString(R.string.market_max_post_photo_bakery)));
                        }
                    }
                });
            }
        });

        // publish
        relLayout_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                productName = Objects.requireNonNull(requireNonNull(edit_product_name.getText()).toString().trim());
                category_choice = Objects.requireNonNull(txt_category.getText().toString().trim());
                price = Objects.requireNonNull(requireNonNull(requireNonNull(edit_price.getText()).toString().trim()));

                if (selectedImageList.isEmpty()) {
                    GradientDrawable drawable = (GradientDrawable) relLayout_post.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                if (market_model.isStore()) {
                    if (category_choice.equals(context.getString(R.string.the_category))){
                        GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                        drawable.mutate();
                        drawable.setStroke(3, Color.RED);
                        return;
                    }
                }

                if (TextUtils.isEmpty(productName)){
                    isFirstTime_stroke_product_name = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_product.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                if (TextUtils.isEmpty(price)){
                    isFirstTime_stroke_edit_price = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_price.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }
                // download data to firebase
                getDownload();
            }
        });

        relLayout_arrowBack_choose_photo.setOnClickListener(view -> {
            if (!isSelectedEmpty) {
                for (int i = 0; i < imageList.size(); i++) {
                    try {
                        if (imageList.get(i).isSelected()) {
                            imageList.get(i).setSelected(false);
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "initPicPhoto: "+e.getMessage());
                    }
                }
                selectedImageList.clear();

                nber = 0;
                relLayout_button_ok.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
                imageAdapter.notifyDataSetChanged();
                isSelectedEmpty = true;
            } else {

                relLayout_post_product.setVisibility(View.VISIBLE);
                relLayout_pic_photo.setVisibility(View.GONE);
                isPickPhotoLayoutVisible = false;
            }
        });
    }

    // download photos to firebase
    private void getDownload() {
        closeKeyboard();
        isScreenEnabled = false;
        progressBar.setVisibility(View.VISIBLE);

        if (selectedImageList.size() == 1) {
            if (isPickPhoto) {
                String size = getImageSize(Uri.parse(selectedImageList.get(0)));
                long bitmap_size = Long.parseLong(size);

                Uri uri = Uri.parse(selectedImageList.get(0));
                if (bitmap_size <= 1000000)
                    uploadOnePhotoWithoutCompress(uri);
                else
                    uploadOnePhotoWithCompres(uri);

            } else {
                Uri uri = Uri.parse(selectedImageList.get(0));
                uploadOnePhotoWithCompres(uri);
            }

        } else {
            for (int i = 0; i < selectedImageList.size(); i++) {
                if (isPickPhoto) {
                    String size = getImageSize(Uri.parse(selectedImageList.get(i)));
                    long bitmap_size = Long.parseLong(size);

                    Uri uri = Uri.parse(selectedImageList.get(i));
                    if (bitmap_size <= 1000000)
                        uploadSeveralPhotoWithoutCompress(uri);
                    else
                        uploadSeveralPhotosWithCompress(uri);

                } else {
                    Uri uri = Uri.parse(selectedImageList.get(i));
                    uploadSeveralPhotosWithCompress(uri);
                }
            }
        }
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(uri.getPath()).length());
    }

    public void setImageList(){
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        imageRecyclerView.setLayoutManager(layoutManager);
        imageRecyclerView.setNestedScrollingEnabled(false);
        imageRecyclerView.setItemAnimator(null);
        imageRecyclerView.setHasFixedSize(true);

        Collections.reverse(imageList);
        imageAdapter = new  GroupImageAdapter(context, imageList, null);
        imageRecyclerView.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener((position, v) -> {
            isPickPhoto = true;
            try {
                if (!imageList.get(position).isSelected()) {
                    selectImage(position);
                } else {
                    unSelectImage(position);
                }
            } catch (ArrayIndexOutOfBoundsException ed) {
                Log.d(TAG, "setImageList: "+ed.getMessage());
            }
        });
    }

    // get all images from external storage
    public void getAllImages(){
        imageList.clear();
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null,null, null);

        try {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String absolutePathOfImage =
                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                ImageModel ImageModel = new ImageModel();
                ImageModel.setImage(absolutePathOfImage);
                imageList.add(ImageModel);
            }
            cursor.close();

        } catch (NullPointerException e) {
            Log.d(TAG, "getAllImages: "+e.getMessage());
        }
    }

    // start the image capture Intent
    public void takePicture(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Continue only if the File was successfully created;
        File photoFile = createImageFile();
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            mLauncher.launch(cameraIntent);
        }
    }

    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (mCurrentPhotoPath != null) {
                            addImage(mCurrentPhotoPath);
                        }

                    } else if (result.getResultCode() == PICK_IMAGES){
                        Intent data = result.getData();
                        assert data != null;
                        if (data.getData() != null) {
                            Uri uri = data.getData();
                            getImageFilePath(uri);
                        }
                    }
                }
            }
    );

    public File createImageFile() {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + dateTime + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d(TAG, "createImageFile: "+e.getMessage());
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // Get image file path
    public void getImageFilePath(Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(uri, projection, null,    null, null);
        if (cursor != null) {
            while  (cursor.moveToNext()) {
                @SuppressLint("Range") String absolutePathOfImage =
                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (absolutePathOfImage != null) {
                    checkImage(absolutePathOfImage);
                } else {
                    checkImage(String.valueOf(uri));
                }
            }
        }
    }

    public void checkImage(String filePath) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList.contains(filePath)) {
            for (int pos = 0; pos < imageList.size(); pos++) {
                if (imageList.get(pos).getImage() != null) {
                    if (imageList.get(pos).getImage().equalsIgnoreCase(filePath)) {
                        imageList.remove(pos);
                    }
                }
            }
            addImage(filePath);
        }
    }

    // add image in selectedImageList and imageList
    @SuppressLint("NotifyDataSetChanged")
    public void addImage(String filePath) {
        if (filePath != null) {
            String path = filePath.replace("file:", "");

            ImageModel imageModel = new ImageModel();
            imageModel.setImage(path);
            imageModel.setSelected(true);
            imageList.add(0, imageModel);
            selectedImageList.add(path);
            isSelectedEmpty = false;

            // to add  image in cropview
            nber++;
            number.setText(String.valueOf(nber));
            number.setVisibility(View.VISIBLE);
            relLayout_button_ok.setVisibility(View.VISIBLE);
            imageAdapter.notifyDataSetChanged();
        }
    }

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    public void selectImage(int position) {
        // Check before add new item in ArrayList;
        if (!selectedImageList.contains(imageList.get(position).getImage())) {
            if (selectedImageList.size() >= 17) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_ten_photos),
                        Toast.LENGTH_SHORT).show();
            } else {
                selectedImageList.add(imageList.get(position).getImage());
                nber++;
                number.setText(String.valueOf(nber));
                isSelectedEmpty = false;

                imageList.get(position).setSelected(true);
                relLayout_button_ok.setVisibility(View.VISIBLE);
                number.setVisibility(View.VISIBLE);

                imageAdapter.notifyDataSetChanged();

                GradientDrawable drawable = (GradientDrawable) relLayout_post.getBackground();
                drawable.mutate();
                drawable.setStroke(0, Color.WHITE);
            }
        }
    }

    // Remove image from selectedImageList
    @SuppressLint("NotifyDataSetChanged")
    public void unSelectImage(int position) {
        for (int i = 0; i < selectedImageList.size(); i++) {
            if (imageList.get(position).getImage() != null) {
                if (selectedImageList.get(i).equals(imageList.get(position).getImage())) {
                    imageList.get(position).setSelected(false);
                    selectedImageList.remove(i);
                    nber--;
                    number.setText(String.valueOf(nber));

                    isSelectedEmpty = selectedImageList.isEmpty();
                    imageAdapter.notifyDataSetChanged();
                }
                if (selectedImageList.size() <= 0) {
                    relLayout_button_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                }
            }
        }
    }

    private void uploadOnePhotoWithoutCompress(Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_MARKET + "/" + user_id + "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                addOnePhotoToDatabase(url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadOnePhotoWithCompres(Uri imageUri) {

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_MARKET + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadOnePhotoWithCompres: "+e.getMessage());
        }
        baos = new ByteArrayOutputStream();

        // here we can choose quality factor
        // in third parameter(ex. here it is 25)
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);

        byte[] fileInBytes = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(fileInBytes);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                //add the new photo to 'photos' node and 'user_photos' node
                addOnePhotoToDatabase(url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addOnePhotoToDatabase(String url) {
        String productName = requireNonNull(requireNonNull(edit_product_name.getText()).toString().trim());
        String category_choice = txt_category.getText().toString().trim();
        String oldPrice = requireNonNull(requireNonNull(edit_old_price.getText()).toString().trim());
        String price = requireNonNull(requireNonNull(edit_price.getText()).toString().trim());
        String description = requireNonNull(requireNonNull(edit_description.getText()).toString().trim());
        String tags = StringManipulation.getTags("");
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String newPhotoKey = myRef.push().getKey();

        if (market_model.isRestaurant() || market_model.isBakery()) {
            category_choice = "";
            product_state = "";

        } else {
            if (category_choice.equals(context.getString(R.string.to_rent_out)))
                product_state = "";
            else if (TextUtils.isEmpty(product_state))
                product_state = context.getString(R.string.radio_new);
        }

        Date date=new Date();

        // juste l'apparition du recyclerview
        MarketModel model = new MarketModel();

        model.setStore(market_model.isStore());
        model.setStore_product(true);
        model.setRestaurant(market_model.isRestaurant());
        model.setBakery(market_model.isBakery());
        model.setRecyclerItem(false);
        model.setImageUne(true);
        model.setSell(false);
        model.setLocation(false); // for to_rent_out

        model.setStore_id(market_model.getStore_id());
        model.setStore_name("");
        model.setStore_owner(market_model.getStore_owner());
        model.setStore_message("");
        model.setRestaurant_menu("");
        model.setStore_profile_photo_id("");
        model.setLive_country_name("");
        model.setTown_name("");
        model.setNeighborhood_name(market_model.getNeighborhood_name());
        model.setProfile_photo("");
        model.setFull_photo("");
        model.setDontSuggestAnymore("");
        model.setUser_id(user_id);
        // market
        model.setProduct_name(productName);
        model.setProduct_category(category_choice);
        model.setLocation_category(""); // for to_rent_out to full
        model.setOld_price(oldPrice);
        model.setPrice(price);
        model.setDevise(GetCountryDevise());
        model.setTags(tags);
        model.setProduct_state(product_state);
        model.setProduct_description(description);
        model.setLocation_period("");
        model.setDate_created(date.getTime());
        model.setViews(0);
        model.setMain_photo("");
        model.setMain_photo_id("");

        // suppress product
        model.setSuppressed(false);

        model.setUrli(url);
        model.setUrlii("");
        model.setUrliii("");
        model.setUrliv("");
        model.setUrlv("");
        model.setUrlvi("");
        model.setUrlvii("");
        model.setUrlviii("");
        model.setUrlix("");
        model.setUrlx("");
        model.setUrlxi("");
        model.setUrlxii("");
        model.setUrlxiii("");
        model.setUrlxiv("");
        model.setUrlxv("");
        model.setUrlxvi("");
        model.setUrlxvii("");

        model.setPhotoi_id(newPhotoKey);
        model.setPhotoii_id("");
        model.setPhotoiii_id("");
        model.setPhotoiv_id("");
        model.setPhotov_id("");
        model.setPhotovi_id("");
        model.setPhotovii_id("");
        model.setPhotoviii_id("");
        model.setPhotoix_id("");
        model.setPhotox_id("");
        model.setPhotoxi_id("");
        model.setPhotoxii_id("");
        model.setPhotoxiii_id("");
        model.setPhotoxiv_id("");
        model.setPhotoxv_id("");
        model.setPhotoxvi_id("");
        model.setPhotoxvii_id("");

        assert newPhotoKey != null;
        myRef.child(getString(R.string.dbname_market))
                .child(market_model.getStore_id())
                .child(newPhotoKey)
                .setValue(model);

        myRef.child(getString(R.string.dbname_market_media))
                .child(newPhotoKey)
                .setValue(model).addOnCompleteListener(task -> {
                    // add main photo
                    if (isPickPhoto) {
                        String size = getImageSize(Uri.parse(selectedImageList.get(0)));
                        long bitmap_size = Long.parseLong(size);

                        Uri uri = Uri.parse(selectedImageList.get(0));
                        if (bitmap_size <= 1000000)
                            uploadMainPhotoWithoutCompress(uri, newPhotoKey);
                        else
                            uploadMainPhotoWithCompres(uri, newPhotoKey);

                    } else {
                        Uri uri = Uri.parse(selectedImageList.get(0));
                        uploadMainPhotoWithCompres(uri, newPhotoKey);
                    }
                });
    }

    private void uploadSeveralPhotoWithoutCompress(Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_MARKET + "/" + user_id + "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                //add the new photo to 'photos' node and 'user_photos' node
                addSeveralPhotosToDatabase(url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadSeveralPhotosWithCompress(Uri imageUri) {

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_MARKET + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadSeveralPhotosWithCompress: "+e.getMessage());
        }
        baos = new ByteArrayOutputStream();

        // here we can choose quality factor
        // in third parameter(ex. here it is 25)
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);

        byte[] fileInBytes = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(fileInBytes);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                //add the new photo to 'photos' node and 'user_photos' node
                addSeveralPhotosToDatabase(url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addSeveralPhotosToDatabase(String url) {
        // la clÃ© de la photo
        String newPhotoKey = myRef.push().getKey();
        url0.add(url);
        photo_id_list.add(newPhotoKey);

        if (url0.size() == selectedImageList.size()) {
            String productName = requireNonNull(requireNonNull(edit_product_name.getText()).toString().trim());
            String category_choice = txt_category.getText().toString().trim();
            String oldPrice = requireNonNull(requireNonNull(edit_old_price.getText()).toString().trim());
            String price = requireNonNull(requireNonNull(edit_price.getText()).toString().trim());
            String description = requireNonNull(requireNonNull(edit_description.getText()).toString().trim());
            String tags = StringManipulation.getTags("");
            String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            if (market_model.isRestaurant() || market_model.isBakery()) {
                category_choice = "";
                product_state = "";

            } else {
                if (category_choice.equals(context.getString(R.string.to_rent_out)))
                    product_state = "";
                else if (TextUtils.isEmpty(product_state))
                    product_state = context.getString(R.string.radio_new);
            }

            Date date = new Date();
            // juste l'apparition du recyclerview
            MarketModel model = new MarketModel();

            model.setStore(market_model.isStore());
            model.setStore_product(true);
            model.setRestaurant(market_model.isRestaurant());
            model.setBakery(market_model.isBakery());
            model.setRecyclerItem(true);
            model.setImageUne(false);
            model.setSell(false);
            model.setLocation(false); // for to_rent_out

            model.setStore_id(market_model.getStore_id());
            model.setStore_name("");
            model.setStore_owner(market_model.getStore_owner());
            model.setStore_message("");
            model.setRestaurant_menu("");
            model.setStore_profile_photo_id("");
            model.setLive_country_name("");
            model.setTown_name("");
            model.setNeighborhood_name(market_model.getNeighborhood_name());
            model.setProfile_photo("");
            model.setFull_photo("");
            model.setDontSuggestAnymore("");
            model.setUser_id(user_id);
            // market
            model.setProduct_name(productName);
            model.setProduct_category(category_choice);
            model.setLocation_category("");
            model.setOld_price(oldPrice);
            model.setPrice(price);
            model.setDevise(GetCountryDevise());
            model.setTags(tags);
            model.setProduct_state(product_state);
            model.setProduct_description(description);
            model.setLocation_period("");
            model.setDate_created(date.getTime());
            model.setViews(0);
            model.setMain_photo("");
            model.setMain_photo_id("");

            // suppress product
            model.setSuppressed(false);

            if (url0.size() == 2) {
                model.setUrli(url0.get(0));
                model.setPhotoi_id(photo_id_list.get(0));
                model.setUrlii(url0.get(1));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setUrliii("");
                model.setPhotoiii_id("");

                model.setUrliv("");
                model.setUrlv("");
                model.setUrlvi("");
                model.setUrlvii("");
                model.setUrlviii("");
                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoiv_id("");
                model.setPhotov_id("");
                model.setPhotovi_id("");
                model.setPhotovii_id("");
                model.setPhotoviii_id("");
                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 3) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));

                model.setUrliv("");
                model.setUrlv("");
                model.setUrlvi("");
                model.setUrlvii("");
                model.setUrlviii("");
                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoiv_id("");
                model.setPhotov_id("");
                model.setPhotovi_id("");
                model.setPhotovii_id("");
                model.setPhotoviii_id("");
                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 4) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));

                model.setUrlv("");
                model.setUrlvi("");
                model.setUrlvii("");
                model.setUrlviii("");
                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotov_id("");
                model.setPhotovi_id("");
                model.setPhotovii_id("");
                model.setPhotoviii_id("");
                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 5) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));

                model.setUrlvi("");
                model.setUrlvii("");
                model.setUrlviii("");
                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotovi_id("");
                model.setPhotovii_id("");
                model.setPhotoviii_id("");
                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 6) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));

                model.setUrlvii("");
                model.setUrlviii("");
                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotovii_id("");
                model.setPhotoviii_id("");
                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 7) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));

                model.setUrlviii("");
                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoviii_id("");
                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 8) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));

                model.setUrlix("");
                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoix_id("");
                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 9) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));

                model.setUrlx("");
                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotox_id("");
                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 10) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));

                model.setUrlxi("");
                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoxi_id("");
                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 11) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));

                model.setUrlxii("");
                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoxii_id("");
                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 12) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));
                model.setUrlxii(url0.get(11));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));
                model.setPhotoxii_id(photo_id_list.get(11));

                model.setUrlxiii("");
                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoxiii_id("");
                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 13) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));
                model.setUrlxii(url0.get(11));
                model.setUrlxiii(url0.get(12));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));
                model.setPhotoxii_id(photo_id_list.get(11));
                model.setPhotoxiii_id(photo_id_list.get(12));

                model.setUrlxiv("");
                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoxiv_id("");
                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 14) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));
                model.setUrlxii(url0.get(11));
                model.setUrlxiii(url0.get(12));
                model.setUrlxiv(url0.get(13));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));
                model.setPhotoxii_id(photo_id_list.get(11));
                model.setPhotoxiii_id(photo_id_list.get(12));
                model.setPhotoxiv_id(photo_id_list.get(13));

                model.setUrlxv("");
                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoxv_id("");
                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 15) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));
                model.setUrlxii(url0.get(11));
                model.setUrlxiii(url0.get(12));
                model.setUrlxiv(url0.get(13));
                model.setUrlxv(url0.get(14));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));
                model.setPhotoxii_id(photo_id_list.get(11));
                model.setPhotoxiii_id(photo_id_list.get(12));
                model.setPhotoxiv_id(photo_id_list.get(13));
                model.setPhotoxv_id(photo_id_list.get(14));

                model.setUrlxvi("");
                model.setUrlxvii("");

                model.setPhotoxvi_id("");
                model.setPhotoxvii_id("");
            } else if (url0.size() == 16) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));
                model.setUrlxii(url0.get(11));
                model.setUrlxiii(url0.get(12));
                model.setUrlxiv(url0.get(13));
                model.setUrlxv(url0.get(14));
                model.setUrlxvi(url0.get(15));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));
                model.setPhotoxii_id(photo_id_list.get(11));
                model.setPhotoxiii_id(photo_id_list.get(12));
                model.setPhotoxiv_id(photo_id_list.get(13));
                model.setPhotoxv_id(photo_id_list.get(14));
                model.setPhotoxvi_id(photo_id_list.get(15));

                model.setUrlxvii("");

                model.setPhotoxvii_id("");
            } else if (url0.size() == 17) {
                model.setUrli(url0.get(0));
                model.setUrlii(url0.get(1));
                model.setUrliii(url0.get(2));
                model.setUrliv(url0.get(3));
                model.setUrlv(url0.get(4));
                model.setUrlvi(url0.get(5));
                model.setUrlvii(url0.get(6));
                model.setUrlviii(url0.get(7));
                model.setUrlix(url0.get(8));
                model.setUrlx(url0.get(9));
                model.setUrlxi(url0.get(10));
                model.setUrlxii(url0.get(11));
                model.setUrlxiii(url0.get(12));
                model.setUrlxiv(url0.get(13));
                model.setUrlxv(url0.get(14));
                model.setUrlxvi(url0.get(15));
                model.setUrlxvii(url0.get(16));

                model.setPhotoi_id(photo_id_list.get(0));
                model.setPhotoii_id(photo_id_list.get(1));
                model.setPhotoiii_id(photo_id_list.get(2));
                model.setPhotoiv_id(photo_id_list.get(3));
                model.setPhotov_id(photo_id_list.get(4));
                model.setPhotovi_id(photo_id_list.get(5));
                model.setPhotovii_id(photo_id_list.get(6));
                model.setPhotoviii_id(photo_id_list.get(7));
                model.setPhotoix_id(photo_id_list.get(8));
                model.setPhotox_id(photo_id_list.get(9));
                model.setPhotoxi_id(photo_id_list.get(10));
                model.setPhotoxii_id(photo_id_list.get(11));
                model.setPhotoxiii_id(photo_id_list.get(12));
                model.setPhotoxiv_id(photo_id_list.get(13));
                model.setPhotoxv_id(photo_id_list.get(14));
                model.setPhotoxvi_id(photo_id_list.get(15));
                model.setPhotoxvii_id(photo_id_list.get(16));
            }

            assert newPhotoKey != null;
            myRef.child(getString(R.string.dbname_market))
                    .child(market_model.getStore_id())
                    .child(photo_id_list.get(0))
                    .setValue(model);

            myRef.child(getString(R.string.dbname_market_media))
                    .child(photo_id_list.get(0))
                    .setValue(model).addOnCompleteListener(task -> {
                        // add main photo
                        if (isPickPhoto) {
                            String size = getImageSize(Uri.parse(selectedImageList.get(0)));
                            long bitmap_size = Long.parseLong(size);

                            Uri uri = Uri.parse(selectedImageList.get(0));
                            if (bitmap_size <= 1000000)
                                uploadMainPhotoWithoutCompress(uri, photo_id_list.get(0));
                            else
                                uploadMainPhotoWithCompres(uri, photo_id_list.get(0));

                        } else {
                            Uri uri = Uri.parse(selectedImageList.get(0));
                            uploadMainPhotoWithCompres(uri, photo_id_list.get(0));
                        }
                    });
        }
    }

    // add main photo ______________________________________________________________________________
    private void uploadMainPhotoWithoutCompress(Uri imageUri, String mainPhoto_id) {
        FilePaths filePaths = new FilePaths();
        String user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_MARKET + "/" + user_id + "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                addMainPhotoToDatabase(url1, mainPhoto_id);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadMainPhotoWithCompres(Uri imageUri, String mainPhoto_id) {

        FilePaths filePaths = new FilePaths();
        String user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_MARKET + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadMainPhotoWithCompres: "+e.getMessage());
        }
        baos = new ByteArrayOutputStream();

        // here we can choose quality factor
        // in third parameter(ex. here it is 25)
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);

        byte[] fileInBytes = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(fileInBytes);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                //add the new photo to 'photos' node and 'user_photos' node
                addMainPhotoToDatabase(url1, mainPhoto_id);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addMainPhotoToDatabase(String url, String mainPhoto_id) {
        String mainPhotoKey = myRef.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("main_photo", url);
        map.put("main_photo_id", mainPhotoKey); // to verify and remove

        myRef.child(getString(R.string.dbname_market))
                .child(market_model.getStore_id())
                .child(mainPhoto_id)
                .updateChildren(map);

        myRef.child(getString(R.string.dbname_market_media))
                .child(mainPhoto_id)
                .updateChildren(map).addOnSuccessListener(unused -> {
                    // send notification
                    sendNotification();

                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from_market", "from_market");
                    intent.putExtra("from_publish", "from_publish");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    // send notification
    private void sendNotification() {
        Query query1 = myRef
                .child(context.getString(R.string.dbname_store_followers))
                .child(market_model.getStore_id());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String member_id = dataSnapshot.getKey();

                    assert member_id != null;
                    if (!member_id.equals(user_id))
                        members_id_list.add(member_id);
                }

                if (!members_id_list.isEmpty()) {
                    for (int i = 0; i < members_id_list.size(); i++) {
                        // send notification
                        Date date = new Date();
                        String new_key = myRef.push().getKey();
                        HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                false, false, false, false, false,
                                false,false, false, false,
                                false, false, false, false, false, false,
                                false,
                                false, false, false, false, false,
                                false, false,
                                false, false, false, false, false,
                                false, false,
                                false, "", false, false, false, false,
                                false,true, false,
                                members_id_list.get(i), new_key, user_id, market_model.getStore_owner(),
                                "", market_model.getStore_id(), date.getTime(),
                                false, false,
                                false, false, false, false, false, false, false, false,
                                false, "", "", "", false, "", "", "", false,
                                "", "", "", "", "", 0,
                                "", "", 0, "", "", "",
                                false, false, false, false,
                                false, false, false,
                                false, false);

                        assert new_key != null;
                        myRef.child(context.getString(R.string.dbname_notification))
                                .child(members_id_list.get(i))
                                .child(new_key)
                                .setValue(map_notification);

                        // add badge number
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", members_id_list.get(i));

                        myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                .child(members_id_list.get(i))
                                .child(new_key)
                                .setValue(map_number);

                        myRef.child(context.getString(R.string.dbname_badge_market_number))
                                .child(members_id_list.get(i))
                                .child(new_key)
                                .setValue(map_number);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}