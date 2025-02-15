package com.bekambimouen.miiokychallenge.challenge_publication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.Utils.adapter.ImageAdapter;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryPhotosFolders;
import com.bekambimouen.miiokychallenge.challenge_publication.adapter.ChooseFollowersToSendChallengeAdapter;
import com.bekambimouen.miiokychallenge.challenge_publication.bottomsheet.BottomSheetChallengeCategories;
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullChallengeImage;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.interfaces.PassCategoryListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PubChallengePhoto extends AppCompatActivity implements OnLoadMoreItemsListener, PassCategoryListener {
    private static final String TAG = "PubChallengePhoto";

    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // selection multiple
    public static final int PICK_IMAGES = 3;
    private ImageAdapter imageAdapter;
    private RecyclerView imageRecyclerView;
    private ArrayList<ImageModel> imageList;
    private ArrayList<String> selectedImageList;
    private ArrayList<String> url_list;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isPickPhoto = false;

    // widgets
    private SwitchCompat switchCompat;
    private TextView btn_ok, number, text_explain_challenge, txt_category, send;
    private ImageView thumbnail_un, thumbnail_deux, erase;
    private CardView cardView_deux;
    private ProgressBar progressBar, main_progressBar, progressbar_choose_followers;
    private MyEditText edit_search;
    private MyEditText un_editText, deux_editText;
    private LinearLayout linear_deux_editext;
    private RelativeLayout relLayout_choose_followers, txt_VS;
    private RecyclerView recyclerView_choose_followers;
    private TextView number_choose_followers;
    private RelativeLayout relLayout_go_choose_followers, relLayout_download_choose_followers,
            relLayout_category;
    private ProgressBar loading_progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    //vars
    private PubChallengePhoto context;
    private ChooseFollowersToSendChallengeAdapter adapter;
    private ArrayList<String> url0, photo_id_list;
    private ArrayList<String> followers_list;
    private ArrayList<User> userList, multiselect_list, pagination;
    private Handler handler;
    private boolean isDownloadVisible, isSelectedEmpty, isChooseLayoutVisible = false;
    private RelativeLayout relLayout1, relLayout2;
    private int nber = 0, selected_followers = 0;
    private boolean isScreenEnabled = true, isEveryOneCanAnswerThisChallenge = true;
    private String category = "";
    private int resultsCount = 0;

    private boolean isChecke = false, isSelectedUserEmpty = true, isBigImage;
    private SharedPreferences mSharedPreferences;

    private Bitmap bmp;
    private ByteArrayOutputStream baos;


    //firebase
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_pub_challenge_photo);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        // no show dialog box again
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        boolean pref = mSharedPreferences.getBoolean("isChecked", false);

        if (!pref) {
            dialogMessage();
        }

        imageList = new ArrayList<>();
        selectedImageList = new ArrayList<>();
        url0 = new ArrayList<>();
        photo_id_list = new ArrayList<>();
        url_list = new ArrayList<>();

        init();
        choose_followers();
        getAllImages();
        setImageList();
        setSelectedImageList();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void dialogMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_msg_challenge_photo, null);
        CheckBox checkBox = view1.findViewById(R.id.checkbox);
        TextView compris = view1.findViewById(R.id.ok);

        builder.setView(view1);
        Dialog dialog = builder.create();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(inset);
        dialog.show();

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                isChecke = true;
            }
        });

        compris.setOnClickListener(view -> {
            mSharedPreferences.edit().putBoolean("isChecked", isChecke).apply();
            dialog.dismiss();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        parent = findViewById(R.id.parent);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        RelativeLayout relLayout_photo = findViewById(R.id.relLayout_photo);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        switchCompat = findViewById(R.id.switchButton);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);
        progressBar = findViewById(R.id.progressbar);
        main_progressBar = findViewById(R.id.main_progressBar);
        imageRecyclerView = findViewById(R.id.pub_RecyclerView_photo);
        text_explain_challenge = findViewById(R.id.text_explain_challenge);
        txt_category = findViewById(R.id.txt_category);
        txt_VS = findViewById(R.id.txt_VS);
        send = findViewById(R.id.send);
        number = findViewById(R.id.number);
        btn_ok = findViewById(R.id.bouton_ok);
        thumbnail_un = findViewById(R.id.thumbnail_un);
        thumbnail_deux = findViewById(R.id.thumbnail_deux);
        cardView_deux = findViewById(R.id.cardView_deux);
        RelativeLayout iv_arrowBack_simple = findViewById(R.id.iv_arrowBack_simple);
        RelativeLayout relLayout_arrowBack = findViewById(R.id.relLayout_arrowBack);
        RelativeLayout relLayout_arrowBack_category = findViewById(R.id.relLayout_arrowBack_category);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);

        linear_deux_editext = findViewById(R.id.linear_deux_editext);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        edit_search = findViewById(R.id.edit_search);
        erase = findViewById(R.id.erase);
        un_editText = findViewById(R.id.un_editText);
        deux_editText = findViewById(R.id.deux_editText);
        relLayout_category = findViewById(R.id.relLayout_category);

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int size = charSequence.length();
                if (size != 0)
                    erase.setVisibility(View.VISIBLE);
                else
                    erase.setVisibility(View.GONE);
                // filter recycler view when text is changed
                adapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        erase.setOnClickListener(view -> {
            Objects.requireNonNull(edit_search.getText()).clear();
            edit_search.requestFocus();
        });

        BottomSheetChallengeCategories bottomSheet = new BottomSheetChallengeCategories(context, relLayout_category);
        relLayout_category.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(relLayout_category);
            try {
                if (bottomSheet.isAdded())
                    return;

                bottomSheet.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        btn_ok.setOnClickListener(view -> {
            isDownloadVisible = true;
            relLayout1.setVisibility(View.GONE);
            relLayout2.setVisibility(View.VISIBLE);

            // l'édittext apparaît après la prise de photo
            if (selectedImageList.size() == 1) {
                switchCompat.setVisibility(View.VISIBLE);
                cardView_deux.setVisibility(View.GONE);
                un_editText.setVisibility(View.VISIBLE);
                text_explain_challenge.setText(context.getString(R.string.Challenge_your_subscribers));
                linear_deux_editext.setVisibility(View.GONE);
                relLayout_publish.setVisibility(View.GONE);
                relLayout_go_choose_followers.setVisibility(View.VISIBLE);
                txt_VS.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext())
                        .load(selectedImageList.get(0))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(thumbnail_un);

            } else if (selectedImageList.size() == 2) {
                switchCompat.setVisibility(View.GONE);
                un_editText.setVisibility(View.GONE);
                cardView_deux.setVisibility(View.VISIBLE);
                linear_deux_editext.setVisibility(View.VISIBLE);
                text_explain_challenge.setText(context.getString(R.string.your_subscribers_photo_vote));
                relLayout_publish.setVisibility(View.VISIBLE);
                relLayout_go_choose_followers.setVisibility(View.GONE);
                txt_VS.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(selectedImageList.get(0))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(thumbnail_un);

                Glide.with(context.getApplicationContext())
                        .load(selectedImageList.get(1))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(thumbnail_deux);
            }
        });

        // full image
        thumbnail_un.setOnClickListener(view -> {
            url_list.clear();
            if (!selectedImageList.get(0).isEmpty()) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                if (selectedImageList.size() == 1)
                    url_list.add(selectedImageList.get(0));
                if (selectedImageList.size() == 2) {
                    url_list.add(selectedImageList.get(0));
                    url_list.add(selectedImageList.get(1));
                }

                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullChallengeImage.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 0);
                context.startActivity(intent);

            }
        });

        thumbnail_deux.setOnClickListener(view -> {
            url_list.clear();
            if (!selectedImageList.get(1).isEmpty()) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                url_list.add(selectedImageList.get(0));
                url_list.add(selectedImageList.get(1));

                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullChallengeImage.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 1);
                context.startActivity(intent);

            }
        });

        relLayout_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                closeKeyboard();

                if (TextUtils.isEmpty(category)) {
                    GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);

                } else {
                    deux_editText.clearFocus();
                    progressBar.setVisibility(View.VISIBLE);
                    isScreenEnabled = false;

                    String caption2 = Objects.requireNonNull(deux_editText.getText()).toString();
                    for (int i = 0; i < selectedImageList.size(); i++) {
                        if (isPickPhoto) {
                            String size = getImageSize(Uri.parse(selectedImageList.get(i)));
                            long bitmap_size = Long.parseLong(size);

                            Uri uri = Uri.parse(selectedImageList.get(i));
                            if (bitmap_size <= 1000000)
                                uploadTwoPhotoWithoutCompress(caption2, category, uri);
                            else
                                uploadTwoPhotoWithCompress(caption2, category, uri);

                        } else {
                            Uri uri = Uri.parse(selectedImageList.get(0));
                            uploadTwoPhotoWithCompress(caption2, category, uri);
                        }
                    }
                }
            }
        });

        iv_arrowBack_simple.setOnClickListener(view -> {
            if (!isSelectedEmpty) {
                for (int i = 0; i < imageList.size(); i++) {
                    try {
                        if (imageList.get(i).isSelected()) {
                            imageList.get(i).setSelected(false);
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "init: "+e.getMessage());
                    }
                }
                selectedImageList.clear();
                nber = 0;
                btn_ok.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
                imageAdapter.notifyDataSetChanged();
                isSelectedEmpty = true;
            } else {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        relLayout_arrowBack_category.setOnClickListener(view -> {
            if (isDownloadVisible) {
                isDownloadVisible = false;

                relLayout2.setVisibility(View.GONE);
                relLayout1.setVisibility(View.VISIBLE);

            }
        });

        relLayout_arrowBack.setOnClickListener(view -> {
            if (!isSelectedUserEmpty) {
                for (int i = 0; i < pagination.size(); i++) {
                    try {
                        if (pagination.get(i).isSelected()) {
                            pagination.get(i).setSelected(false);
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "init: "+e.getMessage());
                    }
                }
                multiselect_list.clear();
                relLayout_download_choose_followers.setEnabled(false);
                send.setTextColor(getColor(R.color.black_semi_transparent));

                selected_followers = 0;
                number_choose_followers.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                isSelectedUserEmpty = true;

            } else if (isChooseLayoutVisible) {
                isChooseLayoutVisible = false;
                closeKeyboard();
                relLayout_choose_followers.setVisibility(View.GONE);
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                drawable.mutate();
                drawable.setStroke(1, getColor(R.color.black_semi_transparent2));

                if (!isSelectedUserEmpty) {
                    for (int i = 0; i < pagination.size(); i++) {
                        try {
                            if (pagination.get(i).isSelected()) {
                                pagination.get(i).setSelected(false);
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBackPressed: "+e.getMessage());
                        }
                    }
                    multiselect_list.clear();
                    relLayout_download_choose_followers.setEnabled(false);
                    send.setTextColor(getColor(R.color.black_semi_transparent));

                    selected_followers = 0;
                    number_choose_followers.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    isSelectedUserEmpty = true;

                } else if (isChooseLayoutVisible) {
                    isChooseLayoutVisible = false;
                    relLayout_choose_followers.setVisibility(View.GONE);

                } else if (isDownloadVisible) {
                    isDownloadVisible = false;
                    relLayout2.setVisibility(View.GONE);
                    relLayout1.setVisibility(View.VISIBLE);

                } else if (!isSelectedEmpty) {
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
                    nber = 0;
                    btn_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                    imageAdapter.notifyDataSetChanged();

                } else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });

        relLayout_photo.setOnClickListener(view -> {
            if (selectedImageList.size() >= 2) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_two_photos),
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
            BottomSheetGalleryPhotosFolders bottomSheet2 = new BottomSheetGalleryPhotosFolders(context, imageAdapter, imageList, toolBar_textview);
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
    }

    @Override
    public void onSendData(String category) {
        this.category = category;
        txt_category.setText(category);
    }

    private void choose_followers() {
        progressbar_choose_followers = findViewById(R.id.progressbar_choose_followers);
        relLayout_choose_followers = findViewById(R.id.relLayout_choose_followers);
        relLayout_go_choose_followers = findViewById(R.id.relLayout_go_choose_followers);

        recyclerView_choose_followers = findViewById(R.id.recyclerView_choose_followers);
        recyclerView_choose_followers.setLayoutManager(new LinearLayoutManager(context));
        number_choose_followers = findViewById(R.id.number_choose_followers);
        relLayout_download_choose_followers = findViewById(R.id.relLayout_download_choose_followers);
        relLayout_download_choose_followers.setEnabled(false);

        multiselect_list = new ArrayList<>();

        fetchUsers();
        getFriendsFollowers();

        adapter = new ChooseFollowersToSendChallengeAdapter(context, pagination, loading_progressBar);

        adapter.setOnItemClickListener((position1, v) -> {
            try {
                if (!pagination.get(position1).isSelected()) {
                    select_follower(position1);
                } else {
                    unSelect_follower(position1);
                }
            } catch (ArrayIndexOutOfBoundsException ed) {
                Log.d(TAG, "choose_followers: "+ed.getMessage());
            }
        });

        relLayout_go_choose_followers.setOnClickListener(view -> {
            closeKeyboard();
            if (TextUtils.isEmpty(category)) {
                GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);

            } else {
                // verify if user has followers or friends
                Query query = myRef
                        .child(context.getString(R.string.dbname_followers))
                        .child(user_id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Query query1 = myRef
                                    .child(context.getString(R.string.dbname_friend_following))
                                    .child(user_id);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // show dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                                        TextView negativeButton = v.findViewById(R.id.tv_no);
                                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                                        builder.setView(v);

                                        Dialog d = builder.create();
                                        d.show();

                                        negativeButton.setText(context.getString(R.string.no));
                                        positiveButton.setText(context.getString(R.string.ok));

                                        dialog_title.setVisibility(View.GONE);
                                        dialog_text.setText(Html.fromHtml(context.getString(R.string.no_subscribers_at_the_moment)));

                                        negativeButton.setVisibility(View.GONE);
                                        positiveButton.setOnClickListener(view1 -> d.dismiss());

                                    } else {
                                        isChooseLayoutVisible = true;
                                        relLayout_choose_followers.setVisibility(View.VISIBLE);
                                        edit_search.requestFocus();
                                        showKeyboard();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            isChooseLayoutVisible = true;
                            relLayout_choose_followers.setVisibility(View.VISIBLE);
                            edit_search.requestFocus();
                            showKeyboard();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        relLayout_download_choose_followers.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                closeKeyboard();
                un_editText.clearFocus();
                progressbar_choose_followers.setVisibility(View.VISIBLE);
                isScreenEnabled = false;

                String caption = Objects.requireNonNull(un_editText.getText()).toString();
                if (isPickPhoto) {
                    String size = getImageSize(Uri.parse(selectedImageList.get(0)));
                    long bitmap_size = Long.parseLong(size);

                    Uri uri = Uri.parse(selectedImageList.get(0));
                    if (bitmap_size <= 1000000)
                        uploadOnePhotoWithoutCompress(caption, category, uri);
                    else
                        uploadOnePhotoWithCompress(caption, category, uri);

                } else {
                    Uri uri = Uri.parse(selectedImageList.get(0));
                    uploadOnePhotoWithCompress(caption, category, uri);
                }
            }
        });
    }

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    private void select_follower(int position) {
        // Check before add new item in ArrayList;
        if (!multiselect_list.contains(pagination.get(position))) {
            if (multiselect_list.size() >= 10) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_ten_person),
                        Toast.LENGTH_SHORT).show();
            } else {
                multiselect_list.add(0, pagination.get(position));
                selected_followers++;
                number_choose_followers.setText(String.valueOf(selected_followers));
                isSelectedUserEmpty = false;

                pagination.get(position).setSelected(true);
                number_choose_followers.setVisibility(View.VISIBLE);
                if (!multiselect_list.isEmpty()) {
                    relLayout_download_choose_followers.setEnabled(true);
                    send.setTextColor(getColor(R.color.shinning_blue));
                }

                adapter.notifyDataSetChanged();
            }
        }
    }

    // Remove image from selectedImageList
    @SuppressLint("NotifyDataSetChanged")
    private void unSelect_follower(int position) {
        for (int i = 0; i < multiselect_list.size(); i++) {
            if (pagination.get(position) != null) {
                if (multiselect_list.get(i).equals(pagination.get(position))) {
                    pagination.get(position).setSelected(false);
                    multiselect_list.remove(i);
                    selected_followers--;
                    number_choose_followers.setText(String.valueOf(selected_followers));

                    isSelectedUserEmpty = multiselect_list.isEmpty();

                    adapter.notifyDataSetChanged();
                }
                if (multiselect_list.size() <= 0) {
                    relLayout_download_choose_followers.setEnabled(false);
                    send.setTextColor(getColor(R.color.black_semi_transparent));
                    number_choose_followers.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * fetches json by making http calls
     */
    private void fetchUsers() {
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(URL,
                response -> {
                    if (response == null) {
                        Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<User> items = new Gson().fromJson(response.toString(), new TypeToken<List<User>>() {
                    }.getType());

                    // adding contacts to contacts list
                    userList.clear();
                    userList.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.d(TAG, "fetchUsers: " + error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
    }

    private void clearAll() {
        if (followers_list != null)
            followers_list.clear();

        if (userList != null)
            userList.clear();

        if (pagination != null)
            pagination.clear();

        if (adapter != null)
            adapter = null;

        if(recyclerView_choose_followers != null)
            handler.post(() -> recyclerView_choose_followers.setAdapter(null));

        followers_list = new ArrayList<>();
        userList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getFriendsFollowers() {
        clearAll();
        Query query = myRef
                .child(context.getString(R.string.dbname_friend_follower))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    FriendsFollowerFollowing following = Util_FriendsFollowerFollowing.getFriendsFollowerFollowing(context, objectMap);

                    String followingId = following.getUser_id();

                    followers_list.add(followingId);
                }

                getFollowers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_followers))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    User user = new User();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    user.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());

                    String userID = user.getUser_id();
                    followers_list.add(userID);
                }

                for (int i = 0; i < followers_list.size(); i++) {
                    final int count = i;

                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(followers_list.get(i));
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                userList.add(user);
                            }

                            if(count >= followers_list.size() -1){
                                int iterations = userList.size();

                                if(iterations > 20){
                                    iterations = 20;
                                }

                                resultsCount = 20;
                                for(int i = 0; i < iterations; i++){
                                    pagination.add(userList.get(i));
                                }

                                adapter.notifyDataSetChanged();
                                recyclerView_choose_followers.setAdapter(adapter);

                                if (adapter.getItemCount() == 0) {
                                    relLayout_download_choose_followers.setEnabled(false);
                                    send.setTextColor(getColor(R.color.black_semi_transparent));
                                    number_choose_followers.setVisibility(View.GONE);
                                }
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

    private void displayMorePhotos() {
        android.util.Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(userList.size() > resultsCount && !userList.isEmpty()){

                int iterations;
                if(userList.size() > (resultsCount + 20)){
                    android.util.Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
                }else{
                    android.util.Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = userList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(userList.get(i));
                }

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            android.util.Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            android.util.Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }

    @Override
    public void onLoadMoreItems() {
        loading_progressBar.setVisibility(View.VISIBLE);
        displayMorePhotos();
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    public void setImageList(){
        imageRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        imageRecyclerView.setNestedScrollingEnabled(false);

        Collections.reverse(imageList);
        imageAdapter = new  ImageAdapter(context, imageList, main_progressBar);
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

    public void setSelectedImageList(){
        if (selectedImageList.isEmpty()) {
            isSelectedEmpty = true;
        }
    }

    // get all images from external storage
    public void getAllImages(){
        imageList.clear();
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null,null, null);

        try {
            while (true) {
                assert cursor != null;
                if (!cursor.moveToNext()) break;
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
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, projection, null,    null, null);
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
            selectedImageList.add(0, path);
            isSelectedEmpty = false;

            nber++;
            number.setText(String.valueOf(nber));
            number.setVisibility(View.VISIBLE);
            // to add  image in cropview
            btn_ok.setVisibility(View.VISIBLE);

            imageAdapter.notifyDataSetChanged();
        }
    }

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    public void selectImage(int position) {
        // Check before add new item in ArrayList;
        if (!selectedImageList.contains(imageList.get(position).getImage())) {
            if (selectedImageList.size() >= 2) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_two_photos),
                        Toast.LENGTH_SHORT).show();
            } else {
                selectedImageList.add(0, imageList.get(position).getImage());
                nber++;
                number.setText(String.valueOf(nber));
                isSelectedEmpty = false;

                imageList.get(position).setSelected(true);
                btn_ok.setVisibility(View.VISIBLE);
                number.setVisibility(View.VISIBLE);

                imageAdapter.notifyDataSetChanged();
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
                    btn_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                }
            }
        }
    }

    private void uploadOnePhotoWithoutCompress(final String caption, String category, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

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
                addOnePhotoToDatabase(caption, category, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadOnePhotoWithCompress(final String caption, String category, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadOnePhotoWithCompress: "+e.getMessage());
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
                addOnePhotoToDatabase(caption, category, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void addOnePhotoToDatabase(String caption, String category, String url) {
        Glide.with(context)
                .asBitmap()
                .load(selectedImageList.get(0))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int height = resource.getHeight();
                        isBigImage = height > 1024;

                        String tags = StringManipulation.getTags(caption);
                        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();

                        // send badge notification
                        for (int i = 0; i < multiselect_list.size(); i++) {
                            // add badge number
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", multiselect_list.get(i).getUser_id());

                            assert newPhotoKey != null;
                            myRef.child(context.getString(R.string.dbname_badge_challenge_invitation_number))
                                    .child(multiselect_list.get(i).getUser_id())
                                    .child(newPhotoKey)
                                    .setValue(map_number);
                        }

                        Query query = myRef
                                .child(getString(R.string.dbname_users))
                                .orderByChild(getString(R.string.field_user_id))
                                .equalTo(user_id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    String username = user.getUsername();
                                    String profilePhoto = user.getProfileUrl();

                                    String invite_category_status = null;
                                    if (category.equals(context.getString(R.string.love)))
                                        invite_category_status = getString(R.string.love_choice);
                                    else if (category.equals(context.getString(R.string.beauty)))
                                        invite_category_status = getString(R.string.beauty_choice);
                                    else if (category.equals(context.getString(R.string.acapella)))
                                        invite_category_status = getString(R.string.acapella_choice);
                                    else if (category.equals(context.getString(R.string.dance)))
                                        invite_category_status = getString(R.string.dance_choice);
                                    else if (category.equals(context.getString(R.string.comedy)))
                                        invite_category_status = getString(R.string.comedy_choice);
                                    else if (category.equals(context.getString(R.string.swag)))
                                        invite_category_status = getString(R.string.swag_choice);
                                    else if (category.equals(context.getString(R.string.decoration)))
                                        invite_category_status = getString(R.string.decoration_choice);
                                    else if (category.equals(context.getString(R.string.couple)))
                                        invite_category_status = getString(R.string.couple_choice);
                                    else if (category.equals(context.getString(R.string.cinema)))
                                        invite_category_status = getString(R.string.cinema_choice);
                                    else if (category.equals(context.getString(R.string.emotions)))
                                        invite_category_status = getString(R.string.emotions_choice);
                                    else if (category.equals(context.getString(R.string.art)))
                                        invite_category_status = getString(R.string.art_choice);
                                    else if (category.equals(context.getString(R.string.sport)))
                                        invite_category_status = getString(R.string.sport_choice);
                                    else if (category.equals(context.getString(R.string.games)))
                                        invite_category_status = getString(R.string.games_choice);
                                    else if (category.equals(context.getString(R.string.vehicle)))
                                        invite_category_status = getString(R.string.vehicle_choice);
                                    else if (category.equals(context.getString(R.string.accessories)))
                                        invite_category_status = getString(R.string.accessories_choice);
                                    else if (category.equals(context.getString(R.string.kitchen)))
                                        invite_category_status = getString(R.string.kitchen_choice);
                                    else if (category.equals(context.getString(R.string.interior_decoration)))
                                        invite_category_status = getString(R.string.interior_decoration_choice);
                                    else if (category.equals(context.getString(R.string.accommodation)))
                                        invite_category_status = getString(R.string.accommodation_choice);
                                    else if (category.equals(context.getString(R.string.restoration)))
                                        invite_category_status = getString(R.string.restoration_choice);
                                    else if (category.equals(context.getString(R.string.journey)))
                                        invite_category_status = getString(R.string.journey_choice);
                                    else if (category.equals(context.getString(R.string.animals)))
                                        invite_category_status = getString(R.string.animals_choice);
                                    else if (category.equals(context.getString(R.string.entertainment)))
                                        invite_category_status = getString(R.string.entertainment_choice);
                                    else if (category.equals(context.getString(R.string.event)))
                                        invite_category_status = getString(R.string.event_choice);

                                    Date date=new Date();
                                    long timeend = System.currentTimeMillis()+86400000; // 1 day later

                                    if (!switchCompat.isChecked())
                                        isEveryOneCanAnswerThisChallenge = false;

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("everyone_can_answer_this_challenge", isEveryOneCanAnswerThisChallenge);
                                    map.put("miioky_challenge", true);
                                    map.put("group_challenge", false);
                                    map.put("group", false);
                                    map.put("group_name", "");
                                    map.put("group_admin", "");
                                    map.put("group_private", false);
                                    map.put("group_public", false);
                                    map.put("admin_master", "");
                                    map.put("group_id", "");
                                    map.put("video", false);
                                    map.put("image", true);
                                    map.put("group_video", false);
                                    map.put("group_image", false);
                                    map.put("allReadyResponded", false);
                                    map.put("invite_url", url);
                                    map.put("thumbnail_invite", "");
                                    map.put("invite_photoID", newPhotoKey);
                                    map.put("invite_profile_photo", profilePhoto);
                                    map.put("invite_userID", user_id);
                                    map.put("user_id", user_id);
                                    map.put("invite_username", username);
                                    map.put("invite_caption", caption);
                                    map.put("invite_tag", tags);
                                    map.put("invite_category", category);
                                    map.put("invite_category_status", invite_category_status);
                                    map.put("date_created", date.getTime());
                                    map.put("invite_country_name", GetCountryName());
                                    map.put("invite_countryID", GetCountryZipCode());
                                    map.put("timestart", ServerValue.TIMESTAMP);
                                    map.put("timeend", timeend);
                                    map.put("big_image", isBigImage);
                                    // suppress the challenge
                                    map.put("suppressed", false);

                                    if (multiselect_list.size() == 1) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", "");
                                        map.put("index_iii", "");
                                        map.put("index_iv", "");
                                        map.put("index_v", "");
                                        map.put("index_vi", "");
                                        map.put("index_vii", "");
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 2) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", "");
                                        map.put("index_iv", "");
                                        map.put("index_v", "");
                                        map.put("index_vi", "");
                                        map.put("index_vii", "");
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 3) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", "");
                                        map.put("index_v", "");
                                        map.put("index_vi", "");
                                        map.put("index_vii", "");
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 4) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", "");
                                        map.put("index_vi", "");
                                        map.put("index_vii", "");
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 5) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", multiselect_list.get(4).getUser_id());
                                        map.put("index_vi", "");
                                        map.put("index_vii", "");
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 6) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", multiselect_list.get(4).getUser_id());
                                        map.put("index_vi", multiselect_list.get(5).getUser_id());
                                        map.put("index_vii", "");
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 7) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", multiselect_list.get(4).getUser_id());
                                        map.put("index_vi", multiselect_list.get(5).getUser_id());
                                        map.put("index_vii", multiselect_list.get(6).getUser_id());
                                        map.put("index_viii", "");
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 8) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", multiselect_list.get(4).getUser_id());
                                        map.put("index_vi", multiselect_list.get(5).getUser_id());
                                        map.put("index_vii", multiselect_list.get(6).getUser_id());
                                        map.put("index_viii", multiselect_list.get(7).getUser_id());
                                        map.put("index_ix", "");
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 9) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", multiselect_list.get(4).getUser_id());
                                        map.put("index_vi", multiselect_list.get(5).getUser_id());
                                        map.put("index_vii", multiselect_list.get(6).getUser_id());
                                        map.put("index_viii", multiselect_list.get(7).getUser_id());
                                        map.put("index_ix", multiselect_list.get(8).getUser_id());
                                        map.put("index_x", "");
                                    }

                                    if (multiselect_list.size() == 10) {
                                        map.put("index_i", multiselect_list.get(0).getUser_id());
                                        map.put("index_ii", multiselect_list.get(1).getUser_id());
                                        map.put("index_iii", multiselect_list.get(2).getUser_id());
                                        map.put("index_iv", multiselect_list.get(3).getUser_id());
                                        map.put("index_v", multiselect_list.get(4).getUser_id());
                                        map.put("index_vi", multiselect_list.get(5).getUser_id());
                                        map.put("index_vii", multiselect_list.get(6).getUser_id());
                                        map.put("index_viii", multiselect_list.get(7).getUser_id());
                                        map.put("index_ix", multiselect_list.get(8).getUser_id());
                                        map.put("index_x", multiselect_list.get(9).getUser_id());
                                    }

                                    assert newPhotoKey != null;
                                    myRef.child(getString(R.string.dbname_invite_battle_media))
                                            .child(newPhotoKey)
                                            .setValue(map);

                                    myRef.child(getString(R.string.dbname_invite_battle))
                                            .child(user_id)
                                            .child(newPhotoKey)
                                            .setValue(map).addOnCompleteListener(task -> {
                                                progressbar_choose_followers.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.GONE);

                                                Intent intent = new Intent();
                                                setResult(Activity.RESULT_OK, intent);
                                                finish();
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void uploadTwoPhotoWithoutCompress(final String caption, String category, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

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
                addTwoPhotosToDatabase(caption, category, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadTwoPhotoWithCompress(final String caption, String category, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadTwoPhotoWithCompress: "+e.getMessage());
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
                addTwoPhotosToDatabase(caption, category, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void addTwoPhotosToDatabase(String caption, String category, String url) {
        // la clé de la photo
        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        url0.add(0, url);
        photo_id_list.add(0, newPhotoKey);

        String invite_category_status = null;
        if (category.equals(context.getString(R.string.love)))
            invite_category_status = getString(R.string.love_choice);
        else if (category.equals(context.getString(R.string.beauty)))
            invite_category_status = getString(R.string.beauty_choice);
        else if (category.equals(context.getString(R.string.acapella)))
            invite_category_status = getString(R.string.acapella_choice);
        else if (category.equals(context.getString(R.string.dance)))
            invite_category_status = getString(R.string.dance_choice);
        else if (category.equals(context.getString(R.string.comedy)))
            invite_category_status = getString(R.string.comedy_choice);
        else if (category.equals(context.getString(R.string.swag)))
            invite_category_status = getString(R.string.swag_choice);
        else if (category.equals(context.getString(R.string.decoration)))
            invite_category_status = getString(R.string.decoration_choice);
        else if (category.equals(context.getString(R.string.couple)))
            invite_category_status = getString(R.string.couple_choice);
        else if (category.equals(context.getString(R.string.cinema)))
            invite_category_status = getString(R.string.cinema_choice);
        else if (category.equals(context.getString(R.string.emotions)))
            invite_category_status = getString(R.string.emotions_choice);
        else if (category.equals(context.getString(R.string.art)))
            invite_category_status = getString(R.string.art_choice);
        else if (category.equals(context.getString(R.string.sport)))
            invite_category_status = getString(R.string.sport_choice);
        else if (category.equals(context.getString(R.string.games)))
            invite_category_status = getString(R.string.games_choice);
        else if (category.equals(context.getString(R.string.vehicle)))
            invite_category_status = getString(R.string.vehicle_choice);
        else if (category.equals(context.getString(R.string.accessories)))
            invite_category_status = getString(R.string.accessories_choice);
        else if (category.equals(context.getString(R.string.kitchen)))
            invite_category_status = getString(R.string.kitchen_choice);
        else if (category.equals(context.getString(R.string.interior_decoration)))
            invite_category_status = getString(R.string.interior_decoration_choice);
        else if (category.equals(context.getString(R.string.accommodation)))
            invite_category_status = getString(R.string.accommodation_choice);
        else if (category.equals(context.getString(R.string.restoration)))
            invite_category_status = getString(R.string.restoration_choice);
        else if (category.equals(context.getString(R.string.journey)))
            invite_category_status = getString(R.string.journey_choice);
        else if (category.equals(context.getString(R.string.animals)))
            invite_category_status = getString(R.string.animals_choice);
        else if (category.equals(context.getString(R.string.entertainment)))
            invite_category_status = getString(R.string.entertainment_choice);
        else if (category.equals(context.getString(R.string.event)))
            invite_category_status = getString(R.string.event_choice);

        if (url0.size() == selectedImageList.size()) {
            String tags = StringManipulation.getTags(caption);
            String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            Date date=new Date();

            // juste l'apparition du recyclerview
            BattleModel battleModel = new BattleModel();

            battleModel.setSuppressed(false);
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

            battleModel.setRecyclerItem(false);
            battleModel.setImageUne(false);
            battleModel.setVideoUne(false);
            battleModel.setImageDouble(true);
            battleModel.setVideoDouble(false);
            battleModel.setReponseImageDouble(false);
            battleModel.setReponseVideoDouble(false);
            // pour le grid profile
            battleModel.setGridRecyclerImage(false);

            battleModel.setReponse_deja(false);
            battleModel.setDetails("");

            battleModel.setUrli("");
            battleModel.setUrlii("");
            battleModel.setUrliii("");
            battleModel.setUrliv("");
            battleModel.setUrlv("");
            battleModel.setUrlvi("");
            battleModel.setUrlvii("");
            battleModel.setUrlviii("");
            battleModel.setUrlix("");
            battleModel.setUrlx("");
            battleModel.setUrlxi("");
            battleModel.setUrlxii("");
            battleModel.setUrlxiii("");
            battleModel.setUrlxiv("");
            battleModel.setUrlxv("");
            battleModel.setUrlxvi("");
            battleModel.setUrlxvii("");

            battleModel.setPhotoi_id("");
            battleModel.setPhotoii_id("");
            battleModel.setPhotoiii_id("");
            battleModel.setPhotoiv_id("");
            battleModel.setPhotov_id("");
            battleModel.setPhotovi_id("");
            battleModel.setPhotovii_id("");
            battleModel.setPhotoviii_id("");
            battleModel.setPhotoix_id("");
            battleModel.setPhotox_id("");
            battleModel.setPhotoxi_id("");
            battleModel.setPhotoxii_id("");
            battleModel.setPhotoxiii_id("");
            battleModel.setPhotoxiv_id("");
            battleModel.setPhotoxv_id("");
            battleModel.setPhotoxvi_id("");
            battleModel.setPhotoxvii_id("");

            battleModel.setCaption_i("");
            battleModel.setCaption_ii("");
            battleModel.setCaption_iii("");
            battleModel.setCaption_iv("");
            battleModel.setCaption_v("");
            battleModel.setCaption_vi("");
            battleModel.setCaption_vii("");
            battleModel.setCaption_viii("");
            battleModel.setCaption_ix("");
            battleModel.setCaption_x("");
            battleModel.setCaption_xi("");
            battleModel.setCaption_xii("");
            battleModel.setCaption_xiii("");
            battleModel.setCaption_xiv("");
            battleModel.setCaption_xv("");
            battleModel.setCaption_xvi("");
            battleModel.setCaption_xvii("");

            battleModel.setThumbnail_un("");
            battleModel.setThumbnail_deux("");
            battleModel.setThumbnail("");
            battleModel.setThumbnail_invite("");
            battleModel.setThumbnail_response("");

            battleModel.setCaption(caption);
            battleModel.setTags(tags);
            battleModel.setDate_created(date.getTime());
            battleModel.setUser_id(user_id);

            battleModel.setUrl("");
            battleModel.setPhoto_id("");
            battleModel.setCaptionUn("");
            battleModel.setTagsUn("");
            battleModel.setTagsDeux("");
            battleModel.setUrlUn(url0.get(0));
            battleModel.setUrlDeux(url0.get(1));
            battleModel.setPhoto_id_un(photo_id_list.get(0));
            battleModel.setPhoto_id_deux(photo_id_list.get(1));
            //group
            battleModel.setGroup(false);
            battleModel.setGroup_private(false);
            battleModel.setGroup_public(false);
            battleModel.setGroup_cover_profile_photo(false);
            battleModel.setGroup_recyclerItem(false);
            battleModel.setGroup_imageUne(false);
            battleModel.setGroup_videoUne(false);
            battleModel.setGroup_cover_background_profile_photo(false);
            battleModel.setGroup_imageDouble(false);
            battleModel.setGroup_videoDouble(false);
            battleModel.setGroup_response_imageDouble(false);
            battleModel.setGroup_response_videoDouble(false);

            battleModel.setGroup_share_single_bottom_image_double(false);
            battleModel.setGroup_share_single_bottom_image_une(false);
            battleModel.setGroup_share_single_bottom_recycler(false);
            battleModel.setGroup_share_single_bottom_response_image_double(false);
            battleModel.setGroup_share_single_bottom_response_video_double(false);
            battleModel.setGroup_share_single_bottom_video_double(false);
            battleModel.setGroup_share_single_bottom_video_une(false);

            battleModel.setGroup_share_single_top_image_double(false);
            battleModel.setGroup_share_single_top_image_une(false);
            battleModel.setGroup_share_single_top_recycler(false);
            battleModel.setGroup_share_single_top_response_image_double(false);
            battleModel.setGroup_share_single_top_response_video_double(false);
            battleModel.setGroup_share_single_top_video_double(false);
            battleModel.setGroup_share_single_top_video_une(false);

            battleModel.setGroup_share_top_bottom_image_double(false);
            battleModel.setGroup_share_top_bottom_image_une(false);
            battleModel.setGroup_share_top_bottom_recycler(false);
            battleModel.setGroup_share_top_bottom_response_image_double(false);
            battleModel.setGroup_share_top_bottom_response_video_double(false);
            battleModel.setGroup_share_top_bottom_video_double(false);
            battleModel.setGroup_share_top_bottom_video_une(false);

            battleModel.setUser_profile_shared(false);
            battleModel.setGroup_share_single_bottom_circle_image(false);
            battleModel.setGroup_share_single_top_circle_image(false);
            battleModel.setGroup_share_top_bottom_circle_image(false);

            battleModel.setUser_profile_photo("");
            battleModel.setUser_full_profile_photo("");
            battleModel.setDate_shared(0);
            battleModel.setViews(0);

            battleModel.setSharing_admin_master("");
            battleModel.setShared_group_id("");
            battleModel.setAdmin_master("");
            battleModel.setGroup_id("");
            battleModel.setPublisher("");
            battleModel.setGroup_profile_photo("");
            battleModel.setGroup_full_profile_photo("");
            battleModel.setGroup_user_background_profile_url("");
            battleModel.setGroup_user_background_full_profile_url("");

            battleModel.setComment_text(false);
            battleModel.setComment_subject("");
            battleModel.setComment_theme("");
            battleModel.setBig_image(false);

            // challenge
            battleModel.setInvite_url("");
            battleModel.setInvite_photoID("");
            battleModel.setInvite_username("");
            battleModel.setInvite_userID("");
            battleModel.setInvite_caption("");
            battleModel.setInvite_tag("");
            battleModel.setInvite_category("");
            battleModel.setInvite_profile_photo("");
            battleModel.setInvite_category_status(invite_category_status);
            battleModel.setSharing_caption("");

            battleModel.setReponse_profile_photo("");
            battleModel.setReponse_username("");
            battleModel.setReponse_user_id("");
            battleModel.setReponse_url("");
            battleModel.setReponse_photoID("");
            battleModel.setReponse_caption("");
            battleModel.setReponse_tag("");

            // challenge and flag
            battleModel.setInvite_country_name("");
            battleModel.setInvite_countryID("");
            battleModel.setReponse_country_name("");
            battleModel.setReponse_countryID("");
            battleModel.setCountry_name(GetCountryName());
            battleModel.setCountryID(GetCountryZipCode());

            // republication of publication
            battleModel.setImageDouble_shared(false);
            battleModel.setImageUne_shared(false);
            battleModel.setRecyclerItem_shared(false);
            battleModel.setReponseImageDouble_shared(false);
            battleModel.setReponseVideoDouble_shared(false);
            battleModel.setVideoDouble_shared(false);
            battleModel.setVideoUne_shared(false);
            battleModel.setUser_profile(false);
            battleModel.setUser_id_sharing("");
            // for saved
            battleModel.setUsername("");
            battleModel.setProfileUrl("");
            battleModel.setAuth_user_id("");
            battleModel.setUser_id_shared("");

            // post identity
            String post_key = myRef.push().getKey();
            battleModel.setPost_identity(post_key);

            // the photo key of the first photo is priority
            myRef.child(getString(R.string.dbname_battle))
                    .child(user_id)
                    .child(photo_id_list.get(0))
                    .setValue(battleModel);

            // ensemble des photos
            myRef.child(getString(R.string.dbname_battle_media))
                    .child(photo_id_list.get(0))
                    .setValue(battleModel).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
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

    // country name corresponding to country code
    public String GetCountryZipCode(){
        String CountryID;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso();
        return CountryID;
    }

    // country name corresponding to country name
    public String GetCountryName(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return CountryZipCode;
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
}