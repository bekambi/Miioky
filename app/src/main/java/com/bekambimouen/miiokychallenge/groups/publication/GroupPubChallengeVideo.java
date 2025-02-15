package com.bekambimouen.miiokychallenge.groups.publication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.VideoCompress;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGroupGalleryVideosFolders;
import com.bekambimouen.miiokychallenge.challenge.ViewMyChallenges;
import com.bekambimouen.miiokychallenge.challenge_publication.bottomsheet.BottomSheetChallengeCategories;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullChallengeVideo;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.GroupChooseFollowersToSendChallengeAdapter;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.GroupVideoListAdapter;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.interfaces.PassCategoryListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimType;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimVideo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class GroupPubChallengeVideo extends AppCompatActivity implements OnLoadMoreItemsListener, PassCategoryListener {
    private static final String TAG = "GroupPubChallengeVideo";

    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private SwitchCompat switchCompat;
    private RelativeLayout parent, relLayout1, relLayout2, relLayout_view_overlay;
    private ImageView thumbnail_un, thumbnail_deux, erase;
    private CardView cardView_deux;
    private LinearLayout linear_deux_editext;
    private TextView btn_ok, number, text_explain_challenge, txt_category, send;
    private MyEditText un_editText, deux_editText;
    private MyEditText edit_search;
    private ProgressBar progressBar, main_progressBar, progressbar_choose_followers;
    private ProgressBar loading_progressBar;

    private RelativeLayout relLayout_choose_followers;
    private RecyclerView recyclerView_choose_followers;
    private TextView number_choose_followers;
    private RelativeLayout relLayout_go_choose_followers, relLayout_download_choose_followers,
            relLayout_category, txt_VS;

    // vars
    private GroupPubChallengeVideo context;
    public static final int PICK_VIDEOS = 20;
    private RecyclerView recyclerView;
    private final String[] projection = {MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME, MediaStore.MediaColumns.DURATION};
    private ArrayList<VideoItem> videoList;
    private ArrayList<String> selectedVideoList, selectedVideoListTrimmed;
    private String mCurrentVideoPath;
    private GroupVideoListAdapter videoAdapter;
    private GroupChooseFollowersToSendChallengeAdapter adapter;
    private File video;
    private long startTime, endTime;
    private final String outputDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private ProgressDialog progressDialog;
    private int pos, nber = 0;
    private boolean isDownloadVisible, isSelectedEmpty;
    private boolean isScreenEnabled = true;
    private String category = "";
    private File file;
    private File thumbnailFile;
    private File compressedFile;
    private ArrayList<File> filesList;
    private ArrayList<File> compressedFilesList;
    private ArrayList<String> followers_list, members_id_list;
    private ArrayList<String> url_list;
    private ArrayList<User> userList, multiselect_list, pagination;
    private Handler handler;
    private boolean isCompressed = false;
    private boolean isChooseLayoutVisible = false;
    private int resultsCount = 0, selected_followers = 0;

    private boolean isChecke = false, isSelectedUserEmpty = true, isBigImage, isEveryOneCanAnswerThisChallenge = true;
    private SharedPreferences mSharedPreferences;
    // from
    private UserGroups user_groups;
    private String admin_user_id;

    // Firebase
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_group_pub_challenge_video);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());
        members_id_list = new ArrayList<>();

        // no show dialog box again
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        boolean pref = mSharedPreferences.getBoolean("isChecked", false);

        if (!pref) {
            dialogMessage();
        }

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
            admin_user_id = getIntent().getStringExtra("admin_user_id");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        videoList = new ArrayList<>();
        selectedVideoList = new ArrayList<>();
        selectedVideoListTrimmed = new ArrayList<>();
        filesList = new ArrayList<>();
        compressedFilesList = new ArrayList<>();
        url_list = new ArrayList<>();

        init();
        choose_followers();
        getAllVideos();
        setVideoList();
        setSelectedVideoList();
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
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_msg_challenge_video, null);
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
        RelativeLayout relLayout_video = findViewById(R.id.relLayout_video);
        TextView toolBar_textview = findViewById(R.id.toolBar_group_name);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        switchCompat = findViewById(R.id.switchButton);
        progressBar = findViewById(R.id.progressbar);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);
        number = findViewById(R.id.number);
        btn_ok = findViewById(R.id.bouton_ok);
        thumbnail_un = findViewById(R.id.thumbnail_un);
        thumbnail_deux = findViewById(R.id.thumbnail_deux);
        txt_VS = findViewById(R.id.txt_VS);
        cardView_deux = findViewById(R.id.cardView_deux);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        edit_search = findViewById(R.id.edit_search);
        erase = findViewById(R.id.erase);
        un_editText = findViewById(R.id.un_editText);
        deux_editText = findViewById(R.id.deux_editText);
        text_explain_challenge = findViewById(R.id.text_explain_challenge);
        txt_category = findViewById(R.id.txt_category);
        send = findViewById(R.id.send);
        linear_deux_editext = findViewById(R.id.linear_deux_editext);
        RelativeLayout iv_arrowBack_simple = findViewById(R.id.iv_arrowBack_simple);
        RelativeLayout relLayout_arrowBack = findViewById(R.id.relLayout_arrowBack);
        RelativeLayout relLayout_arrowBack_category = findViewById(R.id.relLayout_arrowBack_category);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        relLayout_category = findViewById(R.id.relLayout_category);

        edit_search.requestFocus();
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

            try {
                if (bottomSheet.isAdded())
                    return;
                bottomSheet.show(context.getSupportFragmentManager(), TAG);

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: error: "+e.getMessage());
            }
        });

        main_progressBar = findViewById(R.id.main_progressBar);
        recyclerView = findViewById(R.id.pub_RecyclerView_video);

        btn_ok.setOnClickListener(view -> {
            isDownloadVisible = true;
            relLayout1.setVisibility(View.GONE);
            relLayout2.setVisibility(View.VISIBLE);

            // l'Ã©dittext apparaÃ®t aprÃ¨s la prise de photo
            if (selectedVideoList.size() == 1) {
                switchCompat.setVisibility(View.VISIBLE);
                cardView_deux.setVisibility(View.GONE);
                un_editText.setVisibility(View.VISIBLE);
                text_explain_challenge.setText(context.getString(R.string.Challenge_group_members_subscribers));
                linear_deux_editext.setVisibility(View.GONE);
                relLayout_publish.setVisibility(View.GONE);
                relLayout_go_choose_followers.setVisibility(View.VISIBLE);
                txt_VS.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext())
                        .load(selectedVideoListTrimmed.get(0))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(thumbnail_un);

            } else if (selectedVideoList.size() == 2) {
                switchCompat.setVisibility(View.GONE);
                un_editText.setVisibility(View.GONE);
                cardView_deux.setVisibility(View.VISIBLE);
                linear_deux_editext.setVisibility(View.VISIBLE);
                text_explain_challenge.setText(context.getString(R.string.members_of_group_video_vote));
                relLayout_publish.setVisibility(View.VISIBLE);
                relLayout_go_choose_followers.setVisibility(View.GONE);
                txt_VS.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(selectedVideoListTrimmed.get(0))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(thumbnail_un);

                Glide.with(context.getApplicationContext())
                        .load(selectedVideoListTrimmed.get(1))
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(thumbnail_deux);
            }
        });

        // full image
        thumbnail_un.setOnClickListener(view -> {
            url_list.clear();
            if (!selectedVideoListTrimmed.get(0).isEmpty()) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                if (selectedVideoListTrimmed.size() == 1)
                    url_list.add(selectedVideoListTrimmed.get(0));
                if (selectedVideoListTrimmed.size() == 2) {
                    url_list.add(selectedVideoListTrimmed.get(0));
                    url_list.add(selectedVideoListTrimmed.get(1));
                }

                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullChallengeVideo.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 0);
                context.startActivity(intent);

            }
        });

        thumbnail_deux.setOnClickListener(view -> {
            url_list.clear();
            if (!selectedVideoListTrimmed.get(1).isEmpty()) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                url_list.add(selectedVideoListTrimmed.get(0));
                url_list.add(selectedVideoListTrimmed.get(1));

                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullChallengeVideo.class);
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
                // to know if the member admin post approval
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id())
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user_id);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            long sanction_date = follower.getDate_admin_applied_sanction_in_group();
                            long currentTime = System.currentTimeMillis();

                            if (follower.isPublicationApprobation()) {
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
                                positiveButton.setText(context.getString(R.string.ok));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.posts_will_be_submitted_to_admin_before_display)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view1 -> {
                                    getDownload();

                                    d.dismiss();
                                });

                            }
                            // limited posts activity
                            else if (follower.isActivityLimitation()) {
                                if (follower.isPostsActivityLimited()) {
                                    switch (follower.getNumber_posts_per_day_expiration()) {
                                        case "12":
                                            // check that the date of the sanctions has not expired
                                            if ((sanction_date + 12*3600000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "24":
                                            if ((sanction_date + 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "3":
                                            if ((sanction_date + 3 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "7":
                                            if ((sanction_date + 7 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "14":
                                            if ((sanction_date + 14 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                        case "28":
                                            if ((sanction_date + 28 * 86400000L) > currentTime) {
                                                getLimitedPosts(follower);
                                            }
                                            break;
                                    }
                                }

                            } else {
                                if (selectedVideoListTrimmed.size() == 1) {
                                    if (TextUtils.isEmpty(category)) {
                                        GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                                        drawable.mutate();
                                        drawable.setStroke(3, Color.RED);

                                    } else {
                                        closeKeyboard();
                                        un_editText.clearFocus();
                                        progressBar.setVisibility(View.VISIBLE);
                                        isScreenEnabled = false;

                                        String caption = Objects.requireNonNull(un_editText.getText()).toString();
                                        uploadOneNewVideo(caption, category, selectedVideoListTrimmed.get(0));
                                    }

                                } else {
                                    if (TextUtils.isEmpty(category)) {
                                        GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                                        drawable.mutate();
                                        drawable.setStroke(3, Color.RED);

                                    } else {
                                        closeKeyboard();
                                        deux_editText.clearFocus();
                                        progressBar.setVisibility(View.VISIBLE);
                                        isScreenEnabled = false;

                                        String caption2 = Objects.requireNonNull(deux_editText.getText()).toString();
                                        try {
                                            uploadTwoNewVideos_un(caption2, category, selectedVideoListTrimmed.get(0), selectedVideoListTrimmed.get(1));
                                        } catch (IOException e) {
                                            Log.d(TAG, "onDataChange: error: "+e.getMessage());
                                        }
                                    }
                                }
                            }
                        }

                        if (!snapshot.exists()) {
                            if (selectedVideoListTrimmed.size() == 1) {
                                if (TextUtils.isEmpty(category)) {
                                    GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                                    drawable.mutate();
                                    drawable.setStroke(3, Color.RED);

                                } else {
                                    closeKeyboard();
                                    un_editText.clearFocus();
                                    progressBar.setVisibility(View.VISIBLE);
                                    isScreenEnabled = false;

                                    String caption = Objects.requireNonNull(un_editText.getText()).toString();
                                    uploadOneNewVideo(caption, category, selectedVideoListTrimmed.get(0));
                                }

                            } else {
                                if (TextUtils.isEmpty(category)) {
                                    GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                                    drawable.mutate();
                                    drawable.setStroke(3, Color.RED);

                                } else {
                                    closeKeyboard();
                                    deux_editText.clearFocus();
                                    progressBar.setVisibility(View.VISIBLE);
                                    isScreenEnabled = false;

                                    String caption2 = Objects.requireNonNull(deux_editText.getText()).toString();
                                    try {
                                        uploadTwoNewVideos_un(caption2, category, selectedVideoListTrimmed.get(0), selectedVideoListTrimmed.get(1));
                                    } catch (IOException e) {
                                        Log.d(TAG, "onDataChange: error: "+e.getMessage());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // detect if user is admin master
        if (user_groups.getAdmin_master().equals(user_id)) {
            relLayout_publish.setOnClickListener(view1 -> {
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {

                    if (TextUtils.isEmpty(category)) {
                        GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                        drawable.mutate();
                        drawable.setStroke(3, Color.RED);

                    } else {
                        closeKeyboard();
                        deux_editText.clearFocus();
                        progressBar.setVisibility(View.VISIBLE);
                        isScreenEnabled = false;

                        String caption2 = Objects.requireNonNull(deux_editText.getText()).toString();
                        try {
                            uploadTwoNewVideos_un(caption2, category, selectedVideoListTrimmed.get(0), selectedVideoListTrimmed.get(1));
                        } catch (IOException e) {
                            Log.d(TAG, "init: error: "+e.getMessage());
                        }
                    }
                }
            });
        }

        relLayout_video.setOnClickListener(view -> {
            if (selectedVideoListTrimmed.size() >= 2) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_two_videos),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                takeVideo();
            }
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGroupGalleryVideosFolders bottomSheet2 = new BottomSheetGroupGalleryVideosFolders(context, videoAdapter, videoList, toolBar_textview);
            // prevent two clicks
            com.bekambimouen.miiokychallenge.Utils.Util.preventTwoClick(menu_item);
            try {
                if (bottomSheet2.isAdded())
                    return;
                bottomSheet2.show(context.getSupportFragmentManager(), TAG);

            } catch (NullPointerException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        iv_arrowBack_simple.setOnClickListener(view -> {
            if (!isSelectedEmpty) {
                isSelectedEmpty = true;
                for (int i = 0; i < videoList.size(); i++) {
                    try {
                        if (videoList.get(i).isSelected()) {
                            videoList.get(i).setSelected(false);
                        }
                    } catch (NullPointerException e) {
                        Log.d(TAG, "init: error: "+e.getMessage());
                    }
                }
                selectedVideoList.clear();
                selectedVideoListTrimmed.clear();
                nber = 0;
                btn_ok.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
                videoAdapter.notifyDataSetChanged();

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
                        Log.d(TAG, "init: error: "+e.getMessage());
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
                            Log.d(TAG, "onBackPressed: error: "+e.getMessage());
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
                    isScreenEnabled = true;
                    relLayout2.setVisibility(View.GONE);
                    relLayout1.setVisibility(View.VISIBLE);

                } else if (!isSelectedEmpty) {
                    isSelectedEmpty = true;
                    for (int i = 0; i < videoList.size(); i++) {
                        try {
                            if (videoList.get(i).isSelected()) {
                                videoList.get(i).setSelected(false);
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBackPressed: error: "+e.getMessage());
                        }
                    }
                    selectedVideoList.clear();
                    selectedVideoListTrimmed.clear();
                    nber = 0;
                    btn_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                    videoAdapter.notifyDataSetChanged();

                } else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
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
        recyclerView_choose_followers.setItemAnimator(new DefaultItemAnimator());
        number_choose_followers = findViewById(R.id.number_choose_followers);
        relLayout_download_choose_followers = findViewById(R.id.relLayout_download_choose_followers);
        relLayout_download_choose_followers.setEnabled(false);

        multiselect_list = new ArrayList<>();

        fetchUsers();
        getGroupFollowers();

        adapter = new GroupChooseFollowersToSendChallengeAdapter(context, pagination, loading_progressBar);

        adapter.setOnItemClickListener((position1, v) -> {
            try {
                if (!pagination.get(position1).isSelected()) {
                    select_follower(position1);
                } else {
                    unSelect_follower(position1);
                }
            } catch (ArrayIndexOutOfBoundsException ed) {
                Log.d(TAG, "choose_followers: error: "+ed.getMessage());
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
                        .child(context.getString(R.string.dbname_group_followers))
                        .child(user_groups.getGroup_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            dialog_text.setText(Html.fromHtml(context.getString(R.string.no_members_at_the_moment)));

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
                uploadOneNewVideo(caption, category, selectedVideoListTrimmed.get(0));
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
            Log.d(TAG, "fetchUsers: "+ error.getMessage());
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

    private void getGroupFollowers() {
        clearAll();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    String userID = follower.getUser_id();
                    // add admin master id
                    if (!followers_list.contains(follower.getAdmin_master()))
                        followers_list.add(follower.getAdmin_master());
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

                                if (!user.getUser_id().equals(user_id))
                                    userList.add(user);
                            }

                            if (count >= followers_list.size() - 1) {
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

    // download data to firebase when selectedImageList.size() == 2
    private void getDownload() {
        if (TextUtils.isEmpty(category)) {
            GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);

        } else {
            closeKeyboard();
            deux_editText.clearFocus();
            progressBar.setVisibility(View.VISIBLE);
            isScreenEnabled = false;

            String caption2 = Objects.requireNonNull(deux_editText.getText()).toString();
            try {
                uploadTwoNewVideos_un(caption2, category, selectedVideoListTrimmed.get(0), selectedVideoListTrimmed.get(1));
            } catch (IOException e) {
                Log.d(TAG, "getDownload: error: "+e.getMessage());
            }
        }
    }

    // limit the post
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getLimitedPosts(GroupFollowersFollowing follower) {
        if (selectedVideoListTrimmed.size() == 1) {
            if (TextUtils.isEmpty(category)) {
                GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);

            } else {
                closeKeyboard();
                un_editText.clearFocus();
                progressBar.setVisibility(View.VISIBLE);
                isScreenEnabled = false;

                String caption = Objects.requireNonNull(un_editText.getText()).toString();
                uploadOneNewVideo(caption, category, selectedVideoListTrimmed.get(0));
            }

        } else {
            // get the day of today
            LocalDate date = LocalDate.now();
            long time = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

            // it's today
            if (TimeUtils.isDateToday(time)) {
                // we check that the number os publications is lower than the desire limit
                if (follower.getNumber_of_posts_today() < Integer.parseInt(follower.getNumber_posts_per_day())) {

                    // increment number of posts today
                    HashMap<String, Object> map = new HashMap<>();
                    Date date1 = new Date();
                    int number_of_posts = follower.getNumber_of_posts_today();
                    map.put("number_of_posts_today", number_of_posts+1);
                    map.put("date_last_post_or_last_comment", date1.getTime());

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id)
                            .child(user_groups.getGroup_id())
                            .updateChildren(map);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(user_groups.getGroup_id())
                            .child(user_id)
                            .updateChildren(map);

                    // add posts to firebase
                    getDownload();

                } else {
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
                    negativeButton.setVisibility(View.GONE);
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.you_have_reached_the_post_quota)
                            +" "+follower.getNumber_posts_per_day()+" "+context.getString(R.string.publications)
                            +" "+context.getString(R.string.per_day)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());
                    positiveButton.setOnClickListener(view3 -> d.dismiss());
                }
            }
        }
    }

    public void setVideoList(){
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);

        Collections.reverse(videoList);
        videoAdapter = new GroupVideoListAdapter(context, videoList, main_progressBar);
        recyclerView.setAdapter(videoAdapter);

        videoAdapter.setOnItemClickListener((position, v) -> {
            try {
                if (!videoList.get(position).isSelected()) {
                    selectVideo(position);
                } else {
                    unSelectVideo(position);
                }
            } catch (ArrayIndexOutOfBoundsException ed) {
                Log.d(TAG, "setVideoList: error: "+ed.getMessage());
            }
        });
    }

    public void setSelectedVideoList(){
        if (selectedVideoListTrimmed.isEmpty()) {
            isSelectedEmpty = true;
        }
    }

    // get all videos from external storage
    public void getAllVideos(){
        videoList.clear();
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null,null, null);

        try {

            while (true) {
                assert cursor != null;
                if (!cursor.moveToNext()) break;
                @SuppressLint("Range") String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION));
                VideoItem VideoModel = new VideoItem();
                VideoModel.setVideo(absolutePathOfImage);
                VideoModel.setVideoDuration(timeConversion(Long.parseLong(duration)));
                videoList.add(VideoModel);
            }
            cursor.close();
        } catch (NumberFormatException e) {
            Log.d(TAG, "getAllVideos: error: "+e.getMessage());
        }
    }

    //time conversion
    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }

    // start the image capture Intent
    public void takeVideo(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Continue only if the File was successfully created;
        File videoFile = createVideoFile();
        if (videoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
            mResultLauncher.launch(cameraIntent);
        }
    }

    ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (mCurrentVideoPath != null) {
                            addVideo(mCurrentVideoPath);
                        }

                    } else if (result.getResultCode() == PICK_VIDEOS){
                        Intent data = result.getData();
                        assert data != null;
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                getVideoFilePath(uri);
                            }
                        } else if (data.getData() != null) {
                            Uri uri = data.getData();
                            getVideoFilePath(uri);
                        }
                    }
                }
            }
    );

    public File createVideoFile() {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "VID_" + dateTime + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        try {
            video = File.createTempFile(imageFileName, ".mp4", storageDir);
        } catch (IOException e) {
            Log.d(TAG, "createVideoFile: error: "+e.getMessage());
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentVideoPath = "file:" + video.getAbsolutePath();
        return video;
    }

    // Get image file path
    public void getVideoFilePath(Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, projection, null,    null,
                null);
        if (cursor != null) {
            while  (cursor.moveToNext()) {
                @SuppressLint("Range") String absolutePathOfImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.MediaColumns.DATA));
                if (absolutePathOfImage != null) {
                    checkVideo(absolutePathOfImage);
                } else {
                    checkVideo(String.valueOf(uri));
                }
            }
        }
    }

    public void checkVideo(String filePath) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedVideoList.contains(filePath)) {
            for (int pos = 0; pos < videoList.size(); pos++) {
                if (videoList.get(pos).getVideo() != null) {
                    if (videoList.get(pos).getVideo().equalsIgnoreCase(filePath)) {
                        videoList.remove(pos);
                    }
                }
            }
            addVideo(filePath);
        }
    }

    // add image in selectedImageList and imageList
    @SuppressLint("NotifyDataSetChanged")
    public void addVideo(String filePath) {
        VideoItem videoModel = new VideoItem();
        videoModel.setVideo(filePath);
        videoModel.setSelected(true);
        videoList.add(1, videoModel);
        selectedVideoList.add(0, filePath);
        isSelectedEmpty = false;

        TrimVideo.activity(filePath)
                .setTrimType(TrimType.FIXED_DURATION)
                .setFixedDuration(15)
                .setLocal("en")
                .start(this, videoTrimResultLauncher);

        videoAdapter.notifyDataSetChanged();
    }

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    public void selectVideo(int position) {
        // Check before add new item in ArrayList;
        if (!selectedVideoList.contains(videoList.get(position).getVideo())) {
            if (selectedVideoListTrimmed.size() >= 2) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_two_videos),
                        Toast.LENGTH_SHORT).show();
            } else {
                isScreenEnabled = false;
                pos = position;
                selectedVideoList.add(0, videoList.get(position).getVideo());

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(selectedVideoList.get(0));
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, "selectVideo: error: "+e.getMessage());
                }

                File file = new File(selectedVideoList.get(0));
                long length = file.length();

                if (length >= 10000001) {
                    // compression
                    compressVideo(selectedVideoList.get(0));

                } else {
                    isScreenEnabled = true;
                    selectedVideoListTrimmed.add(0, selectedVideoList.get(0));
                    isSelectedEmpty = false;
                    videoList.get(pos).setSelected(true);
                    nber++;
                    number.setText(String.valueOf(nber));
                    number.setVisibility(View.VISIBLE);
                    btn_ok.setVisibility(View.VISIBLE);

                    videoAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> videoTrimResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    isScreenEnabled = true;
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
                    Log.d(TAG, "Trimmed path:: " + uri);

                    String videoUri = String.valueOf(uri);

                    File file = new File(videoUri);
                    long length = file.length();

                    if (length >= 5000001) {
                        // compression
                        compressVideo(videoUri);
                    } else {
                        selectedVideoListTrimmed.add(0, videoUri);
                        isSelectedEmpty = false;
                        videoList.get(pos).setSelected(true);
                        nber++;
                        number.setText(String.valueOf(nber));
                        number.setVisibility(View.VISIBLE);
                        btn_ok.setVisibility(View.VISIBLE);

                        videoAdapter.notifyDataSetChanged();
                    }

                    Log.d(TAG, "Video size:: " + (length / 1024));
                } else
                    Log.d(TAG,"videoTrimResultLauncher data is null");
            });

    private void compressVideo(String inputPath) {
        String destPath = outputDir + File.separator + "VID_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";

        VideoCompress.compressVideoLow(inputPath, destPath,
                new VideoCompress.CompressListener() {
                    @Override
                    public void onStart() {
                        startTime = System.currentTimeMillis();
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(String compressVideoPath) {
                        isCompressed = true;

                        // file to delete
                        compressedFile = new File(destPath);
                        compressedFilesList.add(compressedFile);

                        selectedVideoListTrimmed.add(0, compressVideoPath);
                        isSelectedEmpty = false;
                        isScreenEnabled = true;
                        try {
                            videoList.get(pos).setSelected(true);
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onSuccess: error: "+e.getMessage());
                        }

                        nber++;
                        number.setText(String.valueOf(nber));
                        number.setVisibility(View.VISIBLE);
                        btn_ok.setVisibility(View.VISIBLE);

                        videoAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        progressDialog = null;

                        endTime = System.currentTimeMillis();
                        Util.writeFile(context, "End at: " + new SimpleDateFormat("HH:mm:ss",
                                getLocale()).format(new Date()) + "\n");
                        Util.writeFile(context, "Total: " + ((endTime - startTime)/1000) + "s" + "\n");
                        Util.writeFile(context);

                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(context, "fail!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onProgress(float percent) {

                        progressDialog.setMessage(getString(R.string.compressing_video)+ percent +"%");
                    }
                });

    }
    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale;
        sysLocale = getSystemLocale(config);

        return sysLocale;
    }

    public static Locale getSystemLocale(Configuration config){
        return config.getLocales().get(0);
    }

    // Remove image from selectedImageList
    @SuppressLint("NotifyDataSetChanged")
    public void unSelectVideo(int position) {
        for (int i = 0; i < selectedVideoList.size(); i++) {
            if (videoList.get(position).getVideo() != null) {
                if (selectedVideoList.get(i).equals(videoList.get(position).getVideo())) {
                    videoList.get(position).setSelected(false);
                    selectedVideoList.remove(i);
                    selectedVideoListTrimmed.remove(i);
                    nber--;
                    number.setText(String.valueOf(nber));

                    isSelectedEmpty = selectedVideoList.isEmpty();

                    videoAdapter.notifyDataSetChanged();
                }
                if (selectedVideoListTrimmed.isEmpty()) {
                    btn_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                }
            }
        }
    }

    private void uploadOneNewVideo(String caption, String category, String imgUrl) {
        isScreenEnabled = false;

        Calendar calendar=Calendar.getInstance();
        File vignetteFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!vignetteFolder.exists()) {
            vignetteFolder.mkdirs();
        }

        // get video name
        File videoFile = new File(imgUrl);
        String videoName = videoFile.getName();

        // get video thumbnail as bitmap
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(imgUrl);
        Bitmap bitmap = retriever.getFrameAtTime();

        // saved thumbnail as png file
        String thumbnailPath = vignetteFolder.getAbsolutePath()+"/"+videoName.replace(".mp4", "")+".png";

        thumbnailFile = new File(thumbnailPath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(thumbnailFile);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "uploadOneNewVideo: error: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName.replace(".mp4", "")+"_thumbnail.png");

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "uploadOneNewVideo: error: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();

        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_GROUP_VIDEO + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
                        "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = thumbnailReference.putFile(thumbnailUri);
        uploadTask.continueWithTask(task1 -> {
            if (!task1.isSuccessful())
                throw Objects.requireNonNull(task1.getException());
            return thumbnailReference.getDownloadUrl();

        }).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String thumbnailUrl = task1.getResult().toString();

                Uri videoUri = Uri.fromFile(new File(imgUrl));
                StorageReference videoReference = mStorageReference
                        .child(filePaths.FIREBASE_CHALLENGE_GROUP_VIDEO + "/" + user_id + "/" +context.getString(R.string.video_path) +
                                "/" + calendar.getTimeInMillis());

                UploadTask uploadTask1 = videoReference.putFile(videoUri);
                uploadTask1.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return videoReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String url = task.getResult().toString();

                        addOneVideoToDatabase(caption, category, thumbnailUrl, url);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

            }
        }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = Files.newInputStream(src.toPath())) {
            try (OutputStream out = Files.newOutputStream(dst.toPath())) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public int getVideoWidthOrHeight(File file, String widthOrHeight) throws IOException {
        MediaMetadataRetriever retriever = null;
        Bitmap bmp;
        FileInputStream inputStream = null;
        int mWidthHeight = 0;
        try {
            retriever = new  MediaMetadataRetriever();
            inputStream = new FileInputStream(file.getAbsolutePath());
            retriever.setDataSource(inputStream.getFD());
            bmp = retriever.getFrameAtTime();
            if (widthOrHeight.equals("width")){
                assert bmp != null;
                mWidthHeight = bmp.getWidth();
            }else {
                assert bmp != null;
                mWidthHeight = bmp.getHeight();
            }
        } catch (IOException | RuntimeException e) {
            Log.d(TAG, "getVideoWidthOrHeight: error: "+e.getMessage());
        } finally{
            if (retriever != null){
                retriever.release();
            }if (inputStream != null){
                inputStream.close();
            }
        }
        return mWidthHeight;
    }

    private void addOneVideoToDatabase(String caption, String category, String thumbnailUrl, String url) {
        // Get the video height
        int height = 0;
        try {
            height = getVideoWidthOrHeight(new File(selectedVideoListTrimmed.get(0)), "height");
        } catch (IOException e) {
            Log.d(TAG, "addOneVideoToDatabase: error: "+e.getMessage());
        }

        isBigImage = height > 600;

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

                    // flex to remove admin_user_id in database
                    if (admin_user_id == null)
                        admin_user_id = "";

                    if (!switchCompat.isChecked())
                        isEveryOneCanAnswerThisChallenge = false;

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("everyone_can_answer_this_challenge", isEveryOneCanAnswerThisChallenge);
                    map.put("miioky_challenge", false);
                    map.put("group_challenge", true);
                    map.put("video", false);
                    map.put("image", false);
                    map.put("group_video", true);
                    map.put("group_image", false);
                    map.put("group", true);
                    map.put("group_private", user_groups.isGroup_private());
                    map.put("group_public", user_groups.isGroup_public());
                    map.put("admin_master", user_groups.getAdmin_master());
                    map.put("group_id", user_groups.getGroup_id());
                    map.put("group_name", user_groups.getGroup_name());
                    map.put("group_admin", admin_user_id);
                    map.put("invite_url", url);
                    map.put("thumbnail_invite", thumbnailUrl);
                    map.put("invite_photoID", newPhotoKey);
                    map.put("invite_profile_photo", profilePhoto);
                    map.put("invite_userID", user_id);
                    map.put("invite_username", username);
                    map.put("invite_caption", caption);
                    map.put("invite_tag", tags);
                    map.put("invite_category", category);
                    map.put("invite_category_status", invite_category_status);
                    map.put("invite_country_name", GetCountryName());
                    map.put("invite_countryID", GetCountryZipCode());
                    map.put("date_created", date.getTime());
                    map.put("timestart", ServerValue.TIMESTAMP);
                    map.put("timeend", timeend);
                    map.put("allReadyResponded", false);
                    map.put("user_id", user_id);
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

                    //insert into database
                    assert newPhotoKey != null;
                    myRef.child(getString(R.string.dbname_invite_battle_media))
                            .child(newPhotoKey)
                            .setValue(map);

                    myRef.child(getString(R.string.dbname_group_invitation_challenge))
                            .child(user_groups.getGroup_id())
                            .child(newPhotoKey)
                            .setValue(map).addOnCompleteListener(task -> {

                                // delete file
                                thumbnailFile.delete();
                                file.delete();

                                if (isCompressed)
                                    compressedFile.delete();

                                getWindow().setExitTransition(new Slide(Gravity.END));
                                getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ViewMyChallenges.class);
                                Gson gson = new Gson();
                                String myGson_group;
                                myGson_group = gson.toJson(user_groups);
                                intent.putExtra("user_groups", myGson_group);
                                intent.putExtra("from_group_challenge", "from_group_challenge");
                                startActivity(intent);
                                finish();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadTwoNewVideos_un(String caption, String category, String imgUrl, String imgUrl2) throws IOException {
        isScreenEnabled = false;

        Calendar calendar=Calendar.getInstance();
        File vignetteFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!vignetteFolder.exists()) {
            vignetteFolder.mkdirs();
        }

        // get video name
        File videoFile = new File(imgUrl);
        String videoName = videoFile.getName();

        // get video thumbnail as bitmap
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(imgUrl);
        Bitmap bitmap = retriever.getFrameAtTime();

        // saved thumbnail as png file
        String thumbnailPath = vignetteFolder.getAbsolutePath()+"/"+videoName.replace(".mp4", "")+".png";

        File thumbnailFile = new File(thumbnailPath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(thumbnailFile);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "uploadTwoNewVideos_un: error: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName.replace(".mp4", "")+"_thumbnail.png");

        // add in list to delete
        filesList.add(thumbnailFile);
        filesList.add(file);

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "uploadTwoNewVideos_un: error: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();

        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_GROUP_VIDEO + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
                        "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = thumbnailReference.putFile(thumbnailUri);
        uploadTask.continueWithTask(task1 -> {
            if (!task1.isSuccessful())
                throw Objects.requireNonNull(task1.getException());
            return thumbnailReference.getDownloadUrl();

        }).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String thumbnailUrl = task1.getResult().toString();

                Uri videoUri = Uri.fromFile(new File(imgUrl));
                StorageReference videoReference = mStorageReference
                        .child(filePaths.FIREBASE_CHALLENGE_GROUP_VIDEO + "/" + user_id + "/" +context.getString(R.string.video_path) +
                                "/" + calendar.getTimeInMillis());

                UploadTask uploadTask1 = videoReference.putFile(videoUri);
                uploadTask1.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return videoReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imgUrl1 = task.getResult().toString();

                        try {
                            uploadTwoNewVideos_deux(caption, category, thumbnailUrl, imgUrl1, imgUrl2);
                        } catch (IOException e) {
                            Log.d(TAG, "uploadTwoNewVideos_un: error: "+e.getMessage());
                        }
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

            }
        }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

        retriever.release();
    }

    private void uploadTwoNewVideos_deux(String caption, String category, String thumbnailUrl1, String url1, String imgUrl2) throws IOException {

        Calendar calendar=Calendar.getInstance();
        File vignetteFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!vignetteFolder.exists()) {
            vignetteFolder.mkdirs();
        }

        // get video name
        File videoFile = new File(imgUrl2);
        String videoName = videoFile.getName();

        // get video thumbnail as bitmap
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(imgUrl2);
        Bitmap bitmap = retriever.getFrameAtTime();

        // saved thumbnail as png file
        String thumbnailPath = vignetteFolder.getAbsolutePath()+"/"+videoName.replace(".mp4", "")+".png";

        File thumbnailFile = new File(thumbnailPath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(thumbnailFile);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "uploadTwoNewVideos_deux: error: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName.replace(".mp4", "")+"_thumbnail.png");

        // add in list to delete
        filesList.add(thumbnailFile);
        filesList.add(file);

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "uploadTwoNewVideos_deux: error: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();

        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_CHALLENGE_GROUP_VIDEO + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
                        "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = thumbnailReference.putFile(thumbnailUri);
        uploadTask.continueWithTask(task1 -> {
            if (!task1.isSuccessful())
                throw Objects.requireNonNull(task1.getException());
            return thumbnailReference.getDownloadUrl();

        }).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String thumbnailUrl2 = task1.getResult().toString();

                Uri videoUri = Uri.fromFile(new File(imgUrl2));
                StorageReference videoReference = mStorageReference
                        .child(filePaths.FIREBASE_CHALLENGE_GROUP_VIDEO + "/" + user_id + "/" +context.getString(R.string.video_path) +
                                "/" + calendar.getTimeInMillis());

                UploadTask uploadTask1 = videoReference.putFile(videoUri);
                uploadTask1.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return videoReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String url2 = task.getResult().toString();

                        addTwoVideosToDatabase(caption, category, thumbnailUrl1, url1, thumbnailUrl2, url2);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

            }
        }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

        retriever.release();
    }

    private void addTwoVideosToDatabase(String caption, String category, String thumbnailUrl1, String url1, String thumbnailUrl2, String url2) {
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

        // la clÃ© de la photo
        String newPhotoKey1 = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        String newPhotoKey2 = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();

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
        battleModel.setImageDouble(false);
        battleModel.setVideoDouble(false);
        battleModel.setReponseImageDouble(false);
        battleModel.setReponseVideoDouble(false);
        battleModel.setGroup(false);
        battleModel.setGroup_private(false);
        battleModel.setGroup_public(false);
        battleModel.setGroup_cover_profile_photo(false);
        battleModel.setGroup_recyclerItem(false);
        battleModel.setGroup_imageUne(false);
        battleModel.setGroup_videoUne(false);
        // group
        battleModel.setGroup(true);
        battleModel.setGroup_private(user_groups.isGroup_private());
        battleModel.setGroup_public(user_groups.isGroup_public());
        battleModel.setGroup_cover_profile_photo(false);
        battleModel.setGroup_recyclerItem(false);
        battleModel.setGroup_imageUne(false);
        battleModel.setGroup_videoUne(false);
        battleModel.setGroup_cover_background_profile_photo(false);
        battleModel.setGroup_imageDouble(false);
        battleModel.setGroup_videoDouble(true);
        battleModel.setGroup_response_imageDouble(false);
        battleModel.setGroup_response_videoDouble(false);
        // pour le grid profile
        battleModel.setGridRecyclerImage(false);

        battleModel.setReponse_deja(false);

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

        battleModel.setCaption(caption);
        battleModel.setTags(tags);
        battleModel.setDate_created(date.getTime());
        battleModel.setUser_id(user_id);

        battleModel.setUrl("");
        battleModel.setPhoto_id("");
        battleModel.setCaptionUn("");
        battleModel.setTagsUn("");
        battleModel.setTagsDeux("");
        battleModel.setUrlUn(url1);
        battleModel.setUrlDeux(url2);
        battleModel.setThumbnail_un(thumbnailUrl1);
        battleModel.setThumbnail_deux(thumbnailUrl2);
        battleModel.setPhoto_id_un(newPhotoKey1);
        battleModel.setPhoto_id_deux(newPhotoKey2);
        // group
        battleModel.setAdmin_master(user_groups.getAdmin_master());
        battleModel.setGroup_id(user_groups.getGroup_id());
        battleModel.setSharing_admin_master("");
        battleModel.setShared_group_id("");
        battleModel.setPublisher(user_id);
        battleModel.setGroup_profile_photo(user_groups.getProfile_photo());
        battleModel.setGroup_full_profile_photo(user_groups.getFull_photo());
        battleModel.setGroup_user_background_profile_url("");
        battleModel.setGroup_user_background_full_profile_url("");

        battleModel.setComment_text(false);
        battleModel.setComment_subject("");
        battleModel.setComment_theme("");
        battleModel.setBig_image(false);

        battleModel.setThumbnail("");
        battleModel.setThumbnail_invite("");
        battleModel.setThumbnail_response("");

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

        // to know if the member admin post approval
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    assert newPhotoKey1 != null;
                    if (follower.isPublicationApprobation()) {
                        myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                .child(user_groups.getGroup_id())
                                .child(newPhotoKey1)
                                .setValue(battleModel);

                        // ensemble des photos
                        myRef.child(getString(R.string.dbname_group_Media_waiting_for_approval))
                                .child(newPhotoKey1)
                                .setValue(battleModel).addOnCompleteListener(task -> {

                                    // delete folder
                                    for (int i = 0; i < filesList.size(); i++) {
                                        filesList.get(i).delete();
                                    }

                                    if (isCompressed) {
                                        for (int i = 0; i < compressedFilesList.size(); i++) {
                                            compressedFilesList.get(i).delete();
                                        }
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                });

                    } else {
                        myRef.child(getString(R.string.dbname_group))
                                .child(user_groups.getGroup_id())
                                .child(newPhotoKey1)
                                .setValue(battleModel);

                        // ensemble des photos
                        myRef.child(getString(R.string.dbname_group_media))
                                .child(newPhotoKey1)
                                .setValue(battleModel).addOnCompleteListener(task -> {

                                    // delete folder
                                    for (int i = 0; i < filesList.size(); i++) {
                                        filesList.get(i).delete();
                                    }

                                    if (isCompressed) {
                                        for (int i = 0; i < compressedFilesList.size(); i++) {
                                            compressedFilesList.get(i).delete();
                                        }
                                    }

                                    //add points and send notification
                                    addPostPointsAndSendNotification();
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                });
                    }
                }

                if (!snapshot.exists() && !user_groups.getAdmin_master().equals(user_id)) {
                    assert newPhotoKey1 != null;
                    myRef.child(getString(R.string.dbname_group))
                            .child(user_groups.getGroup_id())
                            .child(newPhotoKey1)
                            .setValue(battleModel);

                    // ensemble des photos
                    myRef.child(getString(R.string.dbname_group_media))
                            .child(newPhotoKey1)
                            .setValue(battleModel).addOnCompleteListener(task -> {

                                // delete folder
                                for (int i = 0; i < filesList.size(); i++) {
                                    filesList.get(i).delete();
                                }

                                if (isCompressed) {
                                    for (int i = 0; i < compressedFilesList.size(); i++) {
                                        compressedFilesList.get(i).delete();
                                    }
                                }

                                //add points and send notification
                                addPostPointsAndSendNotification();
                                progressBar.setVisibility(View.GONE);
                                getWindow().setExitTransition(new Slide(Gravity.END));
                                getWindow().setEnterTransition(new Slide(Gravity.START));
                                finish();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // detect if user is admin master
        if (user_groups.getAdmin_master().equals(user_id)) {
            assert newPhotoKey1 != null;
            myRef.child(getString(R.string.dbname_group))
                    .child(user_groups.getGroup_id())
                    .child(newPhotoKey1)
                    .setValue(battleModel);

            // ensemble des photos
            myRef.child(getString(R.string.dbname_group_media))
                    .child(newPhotoKey1)
                    .setValue(battleModel).addOnCompleteListener(task -> {

                        // delete folder
                        for (int i = 0; i < filesList.size(); i++) {
                            filesList.get(i).delete();
                        }

                        if (isCompressed) {
                            for (int i = 0; i < compressedFilesList.size(); i++) {
                                compressedFilesList.get(i).delete();
                            }
                        }

                        //add points and send notification
                        addPostPointsAndSendNotification();
                        progressBar.setVisibility(View.GONE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    });
        }
    }

    // add post point and send notification
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void addPostPointsAndSendNotification() {
        if (user_groups.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();
                        int number_of_post_points = Integer.parseInt(user_groups.getPost_points());
                        map.put("post_points", number_of_post_points+10);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_user_group))
                                .child(user_groups.getAdmin_master())
                                .child(user_groups.getGroup_id())
                                .updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // send notification
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id());
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
                                    true,false, false,
                                    members_id_list.get(i), new_key, user_id, user_groups.getAdmin_master(),
                                    "", user_groups.getGroup_id(), date.getTime(),
                                    false, false,
                                    true, false, true, false, false, false, false, false,
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
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("user_id", members_id_list.get(i));

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(members_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        HashMap<String, Object> map = new HashMap<>();

                        int number_of_post_points = Integer.parseInt(following.getPost_points());
                        map.put("post_points", number_of_post_points+10);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(user_groups.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(user_groups.getGroup_id())
                                .child(user_id)
                                .updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // send notification
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id());
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String member_id = dataSnapshot.getKey();

                        assert member_id != null;
                        if (!member_id.equals(user_id))
                            members_id_list.add(member_id);
                    }

                    // add admin aster id
                    if (!user_groups.getAdmin_master().equals(user_id))
                        members_id_list.add(user_groups.getAdmin_master());

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
                                    true,false, false,
                                    members_id_list.get(i), new_key, user_id, user_groups.getAdmin_master(),
                                    "", user_groups.getGroup_id(), date.getTime(),
                                    false, false,
                                    true, false, true, false, false, false, false, false,
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
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("user_id", members_id_list.get(i));

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(members_id_list.get(i))
                                    .child(new_key)
                                    .setValue(map);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}