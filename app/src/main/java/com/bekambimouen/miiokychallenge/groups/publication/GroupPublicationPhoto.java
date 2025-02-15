package com.bekambimouen.miiokychallenge.groups.publication;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGroupGalleryPhotosFolders;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.GroupImageAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.publication_insta.adapter.SelectedImageAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupPublicationPhoto extends AppCompatActivity {
    private static final String TAG = "GroupPublicationPhoto";

    // selection multiple
    public static final int PICK_IMAGES = 3;
    private SelectedImageAdapter selectedImageAdapter;
    private GroupImageAdapter imageAdapter;
    private ArrayList<ImageModel> imageList;
    private ArrayList<String> selectedImageList;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isPickPhoto = false;

    // widgets
    private RecyclerView imageRecyclerView, selectedImageRecyclerView;
    private TextView number;
    private MyEditText edit_caption;
    private ProgressBar progressBar, main_progressBar;
    private RelativeLayout parent, relLayout1, relLayout2, relLayout_view_overlay;
    private LinearLayout linLayout_go_activity;

    private Button button_ok;

    //vars
    private UserGroups user_groups;
    private ArrayList<String> url0, photo_id_list, members_id_list;
    private boolean isDownloadVisible, isSelectedEmpty;
    private GroupPublicationPhoto context;
    private int nber = 0;
    private boolean isScreenEnabled = true;

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
        setContentView(R.layout.activity_group_publication_photo);
        context = this;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        imageList = new ArrayList<>();
        selectedImageList = new ArrayList<>();
        url0 = new ArrayList<>();
        photo_id_list = new ArrayList<>();
        members_id_list = new ArrayList<>();

        // get sharing image from others app
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        init();
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

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        main_progressBar = findViewById(R.id.main_progressBar);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        RelativeLayout relLayout_photo = findViewById(R.id.relLayout_photo);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview_choose_photo);
        progressBar = findViewById(R.id.progressbar_download_photo);
        RelativeLayout relLayout_arrowBack_choose_photo = findViewById(R.id.relLayout_arrowBack_choose_photo);
        RelativeLayout relLayout_arrowBack_download_photo = findViewById(R.id.relLayout_arrowBack_download_photo);
        button_ok = findViewById(R.id.button_ok);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        linLayout_go_activity = findViewById(R.id.linLayout_go_activity);

        imageRecyclerView = findViewById(R.id.pub_RecyclerView_photo);
        selectedImageRecyclerView = findViewById(R.id.recyclerView_download_photo);
        edit_caption = findViewById(R.id.edit_caption_download_photo);
        number = findViewById(R.id.number);

        button_ok.setOnClickListener(view -> {
            isDownloadVisible = true;
            relLayout1.setVisibility(View.GONE);
            relLayout2.setVisibility(View.VISIBLE);
        });

        // detect if user is admin master
        if (user_groups.getAdmin_master().equals(user_id)) {
            relLayout_publish.setOnClickListener(view -> {
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {
                    // download data to firebase
                    getDownload();
                }
            });

        } else {
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
                        @TargetApi(Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                long sanction_date = follower.getDate_admin_applied_sanction_in_group();
                                long currentTime = System.currentTimeMillis();

                                // to know if the member admin post approval
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

                                    positiveButton.setOnClickListener(view3 -> {
                                        // download data to firebase
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
                                    // download to firebase
                                    getDownload();
                                }
                            }

                            if (!snapshot.exists()) {
                                // download to firebase
                                getDownload();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }

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
            BottomSheetGroupGalleryPhotosFolders bottomSheet = new BottomSheetGroupGalleryPhotosFolders(context, imageAdapter, imageList, toolBar_textview);
            // prevent two clicks
            Util.preventTwoClick(menu_item);
            try {
                if (bottomSheet.isAdded())
                    return;
                bottomSheet.show(context.getSupportFragmentManager(), TAG);

            } catch (NullPointerException e) {
                Log.d(TAG, "init: "+e.getMessage());
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
                        Log.d(TAG, "init: error: "+e.getMessage());
                    }
                }
                selectedImageList.clear();

                nber = 0;
                button_ok.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
                linLayout_go_activity.setVisibility(View.VISIBLE);
                selectedImageAdapter.notifyDataSetChanged();
                imageAdapter.notifyDataSetChanged();
                isSelectedEmpty = true;
            } else {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        linLayout_go_activity.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupPublicationVideo.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            intent.putExtra("user_groups", myGson);

            startActivity(intent);
        });

        relLayout_arrowBack_download_photo.setOnClickListener(view -> {
            isDownloadVisible = false;
            isScreenEnabled = true;
            relLayout2.setVisibility(View.GONE);
            relLayout1.setVisibility(View.VISIBLE);
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDownloadVisible) {
                    isDownloadVisible = false;
                    isScreenEnabled = true;
                    relLayout2.setVisibility(View.GONE);
                    relLayout1.setVisibility(View.VISIBLE);
                } else if (!isSelectedEmpty) {
                    for (int i = 0; i < imageList.size(); i++) {
                        try {
                            if (imageList.get(i).isSelected()) {
                                imageList.get(i).setSelected(false);
                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onBackPressed: error: "+e.getMessage());
                        }
                    }
                    selectedImageList.clear();
                    nber = 0;
                    button_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                    linLayout_go_activity.setVisibility(View.VISIBLE);
                    selectedImageAdapter.notifyDataSetChanged();
                    imageAdapter.notifyDataSetChanged();
                    isSelectedEmpty = true;
                } else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    // download photos to firebase
    private void getDownload() {
        closeKeyboard();
        isScreenEnabled = false;
        edit_caption.clearFocus();
        String caption = Objects.requireNonNull(edit_caption.getText()).toString();
        progressBar.setVisibility(View.VISIBLE);

        if (selectedImageList.size() == 1) {
            if (isPickPhoto) {
                String size = getImageSize(Uri.parse(selectedImageList.get(0)));
                long bitmap_size = Long.parseLong(size);

                Uri uri = Uri.parse(selectedImageList.get(0));
                if (bitmap_size <= 1000000)
                    uploadOnePhotoWithoutCompress(caption, uri);
                else
                    uploadOnePhotoWithCompres(caption, uri);

            } else {
                Uri uri = Uri.parse(selectedImageList.get(0));
                uploadOnePhotoWithCompres(caption, uri);
            }

        } else {
            for (int i = 0; i < selectedImageList.size(); i++) {
                if (isPickPhoto) {
                    String size = getImageSize(Uri.parse(selectedImageList.get(i)));
                    long bitmap_size = Long.parseLong(size);

                    Uri uri = Uri.parse(selectedImageList.get(i));
                    if (bitmap_size <= 1000000)
                        uploadSeveralPhotoWithoutCompress(caption, uri);
                    else
                        uploadSeveralPhotosWithCompress(caption, uri);

                } else {
                    Uri uri = Uri.parse(selectedImageList.get(i));
                    uploadSeveralPhotosWithCompress(caption, uri);
                }
            }
        }
    }

    // limit the post
    @TargetApi(Build.VERSION_CODES.O)
    private void getLimitedPosts(GroupFollowersFollowing follower) {
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

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    public void setImageList(){
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        imageRecyclerView.setLayoutManager(layoutManager);
        imageRecyclerView.setNestedScrollingEnabled(false);
        imageRecyclerView.setItemAnimator(null);
        imageRecyclerView.setHasFixedSize(true);

        Collections.reverse(imageList);
        imageAdapter = new  GroupImageAdapter(context, imageList, main_progressBar);
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
                Log.d(TAG, "setImageList: error: "+ed.getMessage());
            }
        });
    }

    public void setSelectedImageList(){
        selectedImageRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        selectedImageRecyclerView.setNestedScrollingEnabled(false);
        selectedImageRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));

        if (selectedImageList.isEmpty()) {
            isSelectedEmpty = true;
        }

        selectedImageAdapter = new SelectedImageAdapter(context, selectedImageList);
        selectedImageRecyclerView.setAdapter(selectedImageAdapter);
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
            Log.d(TAG, "getAllImages: error: "+e.getMessage());
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
            Log.d(TAG, "createImageFile: error: "+e.getMessage());
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
            imageList.add(1, imageModel);
            selectedImageList.add(0, path);
            isSelectedEmpty = false;

            // to add  image in cropview
            nber++;
            number.setText(String.valueOf(nber));
            number.setVisibility(View.VISIBLE);
            button_ok.setVisibility(View.VISIBLE);
            linLayout_go_activity.setVisibility(View.GONE);
            selectedImageAdapter.notifyDataSetChanged();
            imageAdapter.notifyDataSetChanged();
        }
    }

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    public void selectImage(int position) {
        // Check before add new item in ArrayList;
        if (!selectedImageList.contains(imageList.get(position).getImage())) {
            if (selectedImageList.size() >= 17) {
                Toast.makeText(context, getString(R.string.cant_selecte_more_than_seventeen_photos),
                        Toast.LENGTH_SHORT).show();
            } else {
                selectedImageList.add(0, imageList.get(position).getImage());
                nber++;
                number.setText(String.valueOf(nber));
                isSelectedEmpty = false;

                imageList.get(position).setSelected(true);
                button_ok.setVisibility(View.VISIBLE);
                number.setVisibility(View.VISIBLE);
                linLayout_go_activity.setVisibility(View.GONE);

                selectedImageAdapter.notifyDataSetChanged();
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
                    selectedImageAdapter.notifyDataSetChanged();
                    imageAdapter.notifyDataSetChanged();
                }
                if (selectedImageList.size() <= 0) {
                    button_ok.setVisibility(View.GONE);
                    number.setVisibility(View.GONE);
                    linLayout_go_activity.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void uploadOnePhotoWithoutCompress(final String caption, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP_BATTLE + "/" + user_id + "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                addOnePhotoToDatabase(caption, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadOnePhotoWithCompres(final String caption, Uri imageUri) {

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP_BATTLE + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadOnePhotoWithCompres: error: "+e.getMessage());
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
                addOnePhotoToDatabase(caption, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addOnePhotoToDatabase(String caption, String url) {
        Glide.with(context)
                .asBitmap()
                .load(selectedImageList.get(0))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int height = resource.getHeight();

                        String tags = StringManipulation.getTags(caption);
                        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
                        Date date=new Date();

                        // juste l'apparition du recyclerview
                        BattleModel battleModel = new BattleModel();

                        // to verify if image is big
                        battleModel.setBig_image(height > 1024);

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
                        // pour le grid profile
                        battleModel.setGridRecyclerImage(false);

                        battleModel.setReponse_deja(false);
                        battleModel.setDetails("");

                        // group
                        battleModel.setGroup(true);
                        battleModel.setGroup_private(user_groups.isGroup_private());
                        battleModel.setGroup_public(user_groups.isGroup_public());
                        battleModel.setGroup_cover_profile_photo(false);
                        battleModel.setGroup_recyclerItem(false);
                        battleModel.setGroup_imageUne(true);
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

                        battleModel.setCaption(caption);
                        battleModel.setUrl(url);
                        battleModel.setDate_created(date.getTime());
                        battleModel.setPhoto_id(newPhotoKey);
                        battleModel.setUser_id(user_id);
                        battleModel.setTags(tags);

                        battleModel.setCaptionUn("");
                        battleModel.setTagsUn("");
                        battleModel.setTagsDeux("");
                        battleModel.setUrlUn("");
                        battleModel.setUrlDeux("");
                        battleModel.setPhoto_id_un("");
                        battleModel.setPhoto_id_deux("");

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

                        // challenge
                        battleModel.setInvite_url("");
                        battleModel.setInvite_photoID("");
                        battleModel.setInvite_username("");
                        battleModel.setInvite_userID("");
                        battleModel.setInvite_caption("");
                        battleModel.setInvite_tag("");
                        battleModel.setInvite_category("");
                        battleModel.setInvite_profile_photo("");

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
                        battleModel.setCountry_name("");
                        battleModel.setCountryID("");
                        battleModel.setInvite_category_status("");
                        battleModel.setSharing_caption("");

                        // republication of publication
                        battleModel.setImageDouble_shared(false);
                        battleModel.setImageUne_shared(false);
                        battleModel.setRecyclerItem_shared(false);
                        battleModel.setReponseImageDouble_shared(false);
                        battleModel.setReponseVideoDouble_shared(false);
                        battleModel.setVideoDouble_shared(false);
                        battleModel.setVideoUne_shared(false);
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

                                    assert newPhotoKey != null;
                                    if (follower.isPublicationApprobation()) {
                                        // un autre adaptateur pour le recyclerview
                                        myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                                .child(user_groups.getGroup_id())
                                                .child(newPhotoKey)
                                                .setValue(battleModel);

                                        myRef.child(getString(R.string.dbname_group_Media_waiting_for_approval))
                                                .child(newPhotoKey)
                                                .setValue(battleModel).addOnCompleteListener(task -> {
                                                    isScreenEnabled = true;
                                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    finish();
                                                });

                                    } else {
                                        myRef.child(getString(R.string.dbname_group))
                                                .child(user_groups.getGroup_id())
                                                .child(newPhotoKey)
                                                .setValue(battleModel);

                                        myRef.child(getString(R.string.dbname_group_media))
                                                .child(newPhotoKey)
                                                .setValue(battleModel).addOnCompleteListener(task -> {
                                                    isScreenEnabled = true;
                                                    //add points and send notification
                                                    addPostPointsAndSendNotification();
                                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    finish();
                                                });
                                    }
                                }

                                if (!snapshot.exists() && !user_groups.getAdmin_master().equals(user_id)) {
                                    assert newPhotoKey != null;
                                    myRef.child(getString(R.string.dbname_group))
                                            .child(user_groups.getGroup_id())
                                            .child(newPhotoKey)
                                            .setValue(battleModel);

                                    myRef.child(getString(R.string.dbname_group_media))
                                            .child(newPhotoKey)
                                            .setValue(battleModel).addOnCompleteListener(task -> {
                                                isScreenEnabled = true;
                                                //add points and send notification
                                                addPostPointsAndSendNotification();
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
                            assert newPhotoKey != null;
                            myRef.child(getString(R.string.dbname_group))
                                    .child(user_groups.getGroup_id())
                                    .child(newPhotoKey)
                                    .setValue(battleModel);

                            myRef.child(getString(R.string.dbname_group_media))
                                    .child(newPhotoKey)
                                    .setValue(battleModel).addOnCompleteListener(task -> {
                                        isScreenEnabled = true;
                                        //add points and send notification
                                        addPostPointsAndSendNotification();
                                        getWindow().setExitTransition(new Slide(Gravity.END));
                                        getWindow().setEnterTransition(new Slide(Gravity.START));
                                        finish();
                                    });
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void uploadSeveralPhotoWithoutCompress(final String caption, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP_BATTLE + "/" + user_id + "/" + calendar.getTimeInMillis());

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
                addSeveralPhotosToDatabase(caption, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadSeveralPhotosWithCompress(final String caption, Uri imageUri) {

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP_BATTLE + "/" + user_id + "/" + calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadSeveralPhotosWithCompress: error: "+e.getMessage());
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
                addSeveralPhotosToDatabase(caption, url1);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addSeveralPhotosToDatabase(String caption, String url) {
        // la clÃ© de la photo
        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        url0.add(0, url);
        photo_id_list.add(0, newPhotoKey);

        if (url0.size() == selectedImageList.size()) {
            String tags = StringManipulation.getTags(caption);
            String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            Date date = new Date();
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
            // pour le grid profile
            battleModel.setGridRecyclerImage(true);

            battleModel.setReponse_deja(false);
            battleModel.setDetails("");

            if (url0.size() == 2) {
                battleModel.setUrli(url0.get(0));
                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setUrliii("");
                battleModel.setPhotoiii_id("");

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
            } else if (url0.size() == 3) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));

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
            } else if (url0.size() == 4) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));

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
            } else if (url0.size() == 5) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));

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
            } else if (url0.size() == 6) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));

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
            } else if (url0.size() == 7) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));

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
            } else if (url0.size() == 8) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));

                battleModel.setUrlix("");
                battleModel.setUrlx("");
                battleModel.setUrlxi("");
                battleModel.setUrlxii("");
                battleModel.setUrlxiii("");
                battleModel.setUrlxiv("");
                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoix_id("");
                battleModel.setPhotox_id("");
                battleModel.setPhotoxi_id("");
                battleModel.setPhotoxii_id("");
                battleModel.setPhotoxiii_id("");
                battleModel.setPhotoxiv_id("");
                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 9) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));

                battleModel.setUrlx("");
                battleModel.setUrlxi("");
                battleModel.setUrlxii("");
                battleModel.setUrlxiii("");
                battleModel.setUrlxiv("");
                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotox_id("");
                battleModel.setPhotoxi_id("");
                battleModel.setPhotoxii_id("");
                battleModel.setPhotoxiii_id("");
                battleModel.setPhotoxiv_id("");
                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 10) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));

                battleModel.setUrlxi("");
                battleModel.setUrlxii("");
                battleModel.setUrlxiii("");
                battleModel.setUrlxiv("");
                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoxi_id("");
                battleModel.setPhotoxii_id("");
                battleModel.setPhotoxiii_id("");
                battleModel.setPhotoxiv_id("");
                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 11) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));

                battleModel.setUrlxii("");
                battleModel.setUrlxiii("");
                battleModel.setUrlxiv("");
                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoxii_id("");
                battleModel.setPhotoxiii_id("");
                battleModel.setPhotoxiv_id("");
                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 12) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));
                battleModel.setUrlxii(url0.get(11));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));
                battleModel.setPhotoxii_id(photo_id_list.get(11));

                battleModel.setUrlxiii("");
                battleModel.setUrlxiv("");
                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoxiii_id("");
                battleModel.setPhotoxiv_id("");
                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 13) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));
                battleModel.setUrlxii(url0.get(11));
                battleModel.setUrlxiii(url0.get(12));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));
                battleModel.setPhotoxii_id(photo_id_list.get(11));
                battleModel.setPhotoxiii_id(photo_id_list.get(12));

                battleModel.setUrlxiv("");
                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoxiv_id("");
                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 14) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));
                battleModel.setUrlxii(url0.get(11));
                battleModel.setUrlxiii(url0.get(12));
                battleModel.setUrlxiv(url0.get(13));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));
                battleModel.setPhotoxii_id(photo_id_list.get(11));
                battleModel.setPhotoxiii_id(photo_id_list.get(12));
                battleModel.setPhotoxiv_id(photo_id_list.get(13));

                battleModel.setUrlxv("");
                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoxv_id("");
                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 15) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));
                battleModel.setUrlxii(url0.get(11));
                battleModel.setUrlxiii(url0.get(12));
                battleModel.setUrlxiv(url0.get(13));
                battleModel.setUrlxv(url0.get(14));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));
                battleModel.setPhotoxii_id(photo_id_list.get(11));
                battleModel.setPhotoxiii_id(photo_id_list.get(12));
                battleModel.setPhotoxiv_id(photo_id_list.get(13));
                battleModel.setPhotoxv_id(photo_id_list.get(14));

                battleModel.setUrlxvi("");
                battleModel.setUrlxvii("");

                battleModel.setPhotoxvi_id("");
                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 16) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));
                battleModel.setUrlxii(url0.get(11));
                battleModel.setUrlxiii(url0.get(12));
                battleModel.setUrlxiv(url0.get(13));
                battleModel.setUrlxv(url0.get(14));
                battleModel.setUrlxvi(url0.get(15));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));
                battleModel.setPhotoxii_id(photo_id_list.get(11));
                battleModel.setPhotoxiii_id(photo_id_list.get(12));
                battleModel.setPhotoxiv_id(photo_id_list.get(13));
                battleModel.setPhotoxv_id(photo_id_list.get(14));
                battleModel.setPhotoxvi_id(photo_id_list.get(15));

                battleModel.setUrlxvii("");

                battleModel.setPhotoxvii_id("");
            } else if (url0.size() == 17) {
                battleModel.setUrli(url0.get(0));
                battleModel.setUrlii(url0.get(1));
                battleModel.setUrliii(url0.get(2));
                battleModel.setUrliv(url0.get(3));
                battleModel.setUrlv(url0.get(4));
                battleModel.setUrlvi(url0.get(5));
                battleModel.setUrlvii(url0.get(6));
                battleModel.setUrlviii(url0.get(7));
                battleModel.setUrlix(url0.get(8));
                battleModel.setUrlx(url0.get(9));
                battleModel.setUrlxi(url0.get(10));
                battleModel.setUrlxii(url0.get(11));
                battleModel.setUrlxiii(url0.get(12));
                battleModel.setUrlxiv(url0.get(13));
                battleModel.setUrlxv(url0.get(14));
                battleModel.setUrlxvi(url0.get(15));
                battleModel.setUrlxvii(url0.get(16));

                battleModel.setPhotoi_id(photo_id_list.get(0));
                battleModel.setPhotoii_id(photo_id_list.get(1));
                battleModel.setPhotoiii_id(photo_id_list.get(2));
                battleModel.setPhotoiv_id(photo_id_list.get(3));
                battleModel.setPhotov_id(photo_id_list.get(4));
                battleModel.setPhotovi_id(photo_id_list.get(5));
                battleModel.setPhotovii_id(photo_id_list.get(6));
                battleModel.setPhotoviii_id(photo_id_list.get(7));
                battleModel.setPhotoix_id(photo_id_list.get(8));
                battleModel.setPhotox_id(photo_id_list.get(9));
                battleModel.setPhotoxi_id(photo_id_list.get(10));
                battleModel.setPhotoxii_id(photo_id_list.get(11));
                battleModel.setPhotoxiii_id(photo_id_list.get(12));
                battleModel.setPhotoxiv_id(photo_id_list.get(13));
                battleModel.setPhotoxv_id(photo_id_list.get(14));
                battleModel.setPhotoxvi_id(photo_id_list.get(15));
                battleModel.setPhotoxvii_id(photo_id_list.get(16));
            }

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
            battleModel.setDate_created(date.getTime());
            battleModel.setUser_id(user_id);
            battleModel.setTags(tags);

            // group
            battleModel.setGroup(true);
            battleModel.setGroup_private(user_groups.isGroup_private());
            battleModel.setGroup_public(user_groups.isGroup_public());
            battleModel.setGroup_cover_profile_photo(false);
            battleModel.setGroup_recyclerItem(true);
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

            battleModel.setUrl("");
            battleModel.setCaptionUn("");
            battleModel.setTagsUn("");
            battleModel.setTagsDeux("");
            battleModel.setUrlUn("");
            battleModel.setUrlDeux("");
            battleModel.setPhoto_id("");
            battleModel.setPhoto_id_un("");
            battleModel.setPhoto_id_deux("");

            battleModel.setThumbnail_un("");
            battleModel.setThumbnail_deux("");
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
            battleModel.setCountry_name("");
            battleModel.setCountryID("");
            battleModel.setInvite_category_status("");
            battleModel.setSharing_caption("");

            // shared
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

                        if (follower.isPublicationApprobation()) {
                            // the photo key of the first photo is priority
                            myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                    .child(user_groups.getGroup_id())
                                    .child(photo_id_list.get(0))
                                    .setValue(battleModel);

                            // ensemble des photos
                            myRef.child(getString(R.string.dbname_group_Media_waiting_for_approval))
                                    .child(photo_id_list.get(0))
                                    .setValue(battleModel).addOnCompleteListener(task -> {
                                        getWindow().setExitTransition(new Slide(Gravity.END));
                                        getWindow().setEnterTransition(new Slide(Gravity.START));
                                        finish();
                                    });

                        } else {
                            myRef.child(getString(R.string.dbname_group))
                                    .child(user_groups.getGroup_id())
                                    .child(photo_id_list.get(0))
                                    .setValue(battleModel);

                            myRef.child(getString(R.string.dbname_group_media))
                                    .child(photo_id_list.get(0))
                                    .setValue(battleModel).addOnCompleteListener(task -> {
                                        //add points and send notification
                                        addPostPointsAndSendNotification();
                                        getWindow().setExitTransition(new Slide(Gravity.END));
                                        getWindow().setEnterTransition(new Slide(Gravity.START));
                                        finish();
                                    });
                        }
                    }

                    if (!snapshot.exists() && !user_groups.getAdmin_master().equals(user_id)) {
                        myRef.child(getString(R.string.dbname_group))
                                .child(user_groups.getGroup_id())
                                .child(photo_id_list.get(0))
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_media))
                                .child(photo_id_list.get(0))
                                .setValue(battleModel).addOnCompleteListener(task -> {
                                    //add points and send notification
                                    addPostPointsAndSendNotification();
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
                myRef.child(getString(R.string.dbname_group))
                        .child(user_groups.getGroup_id())
                        .child(photo_id_list.get(0))
                        .setValue(battleModel);

                myRef.child(getString(R.string.dbname_group_media))
                        .child(photo_id_list.get(0))
                        .setValue(battleModel).addOnCompleteListener(task -> {
                            //add points and send notification
                            addPostPointsAndSendNotification();
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            finish();
                        });
            }
        }
    }

    // add post point and send notification
    @TargetApi(Build.VERSION_CODES.O)
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

    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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