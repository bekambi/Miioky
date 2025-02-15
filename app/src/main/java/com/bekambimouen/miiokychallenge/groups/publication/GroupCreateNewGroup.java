package com.bekambimouen.miiokychallenge.groups.publication;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryResponseChallengePhotosFolders;
import com.bekambimouen.miiokychallenge.crop_profile_image.CropImage;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.InvitePersonsAfterGroupCreated;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class GroupCreateNewGroup extends AppCompatActivity {
    private static final String TAG = "GroupCreateNewGroup";

    //widgets
    private RecyclerView recyclerView;
    private TextView title, nber_char_sequence;
    private ProgressBar progressbar;
    private MyEditText edit_group_name, edit_about_group;
    private RelativeLayout parent, relLayout_profile, relLayout_view_overlay;
    private RelativeLayout relLayout_group_name;
    private RelativeLayout relLayout_pub_profile_photo;
    private LinearLayout linLayout_add_photo, linLayout_parent;
    private LinearLayout linLayout_update_photo, linLayout_nber_char_sequence;
    private ImageView camera_id;

    // theme
    private RelativeLayout arrowBack, relLayout_update;
    private ImageView ivCloseShare;
    private TextView toolbar_title;
    private Toolbar toolBar;

    //vars
    private GroupCreateNewGroup context;
    private StoryGridImageAdapter adapter;
    private UserGroups user_groups;
    private ArrayList<ImageModel> imageList;
    private String mSelectedImage, originOfPhoto;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isScreenEnabled = true;
    private boolean isPublic = false, isPrivate = false, isCameraVisible = true,
            isLinLayout_invite_visible = false;
    private Dialog dialog;
    private String imgUrl, fullImgUrl, group_name, group_message, theme = "";

    private Bitmap bmp;
    private ByteArrayOutputStream baos;

    // firebase
    private DatabaseReference reference;
    private StorageReference mStorageReference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_group_create_new_group);
        context = this;

        reference = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        imageList = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();

        relLayout_pub_profile_photo = findViewById(R.id.relLayout_pub_profile_photo);
        linLayout_add_photo = findViewById(R.id.linLayout_add_photo);
        linLayout_update_photo = findViewById(R.id.linLayout_update_photo);
        LinearLayout linLayout_data = findViewById(R.id.linLayout);
        progressbar = findViewById(R.id.progressbar);
        ImageView iv_photo = findViewById(R.id.iv_photo);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        // coming from profile group to update profile photo
        if (user_groups != null) {
            linLayout_parent.setVisibility(View.GONE);
            relLayout_pub_profile_photo.setVisibility(View.VISIBLE);
            linLayout_data.setVisibility(View.GONE);
            toolbar_title.setText(context.getString(R.string.update));
            title.setText(context.getString(R.string.update_group_profile_photo_title));
            isCameraVisible = false;
        }

        // get all images from gallery
        getAllImages();

        ivCloseShare.setOnClickListener(v -> {
            android.util.Log.d(TAG, "onClick: closing the gallery fragment.");
            finish();
        });

        iv_photo.setOnClickListener(view1 -> {
            Log.d(TAG, "onCreate: takePicture");
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            takePicture();
        });

        dialog = new Dialog(this);
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
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        linLayout_parent = findViewById(R.id.linLayout_parent);
        arrowBack = findViewById(R.id.arrowBack);
        ivCloseShare = findViewById(R.id.ivCloseShare);

        RelativeLayout relLayout_choose_confidentiality = findViewById(R.id.relLayout_choose_confidentiality);
        ImageView close = findViewById(R.id.close);
        linLayout_nber_char_sequence = findViewById(R.id.linLayout_nber_char_sequence);
        nber_char_sequence = findViewById(R.id.nber_char_sequence);
        relLayout_profile = findViewById(R.id.relLayout_profile);
        relLayout_group_name = findViewById(R.id.relLayout_group_name);
        relLayout_update = findViewById(R.id.relLayout_update);
        toolbar_title = findViewById(R.id.toolbar_title);
        title = findViewById(R.id.title);
        edit_group_name = findViewById(R.id.edit_group_name);
        edit_about_group = findViewById(R.id.edit_about_group);
        camera_id = findViewById(R.id.camera_id);
        toolBar = findViewById(R.id.toolBar);
        RelativeLayout normal_theme = findViewById(R.id.normal_theme);
        CardView blue_theme = findViewById(R.id.blue_theme);
        CardView brown_theme = findViewById(R.id.brown_theme);
        CardView pink_theme = findViewById(R.id.pink_theme);
        CardView green_theme = findViewById(R.id.green_theme);
        CardView black_theme = findViewById(R.id.black_theme);

        edit_group_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GradientDrawable drawable = (GradientDrawable) relLayout_group_name.getBackground();
                drawable.mutate();
                drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_about_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_sequence.setText(String.valueOf(char_length));

                if (char_length == 0) {
                    linLayout_nber_char_sequence.setVisibility(View.GONE);

                } else if (char_length >= 1) {
                    linLayout_nber_char_sequence.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        relLayout_choose_confidentiality.setOnClickListener(view -> {
            group_name = Objects.requireNonNull(edit_group_name.getText()).toString();
            group_message = Objects.requireNonNull(edit_about_group.getText()).toString();

            if (imgUrl == null) {
                GradientDrawable drawable = (GradientDrawable) relLayout_profile.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);

            } else if (TextUtils.isEmpty(group_name)){
                GradientDrawable drawable = (GradientDrawable) relLayout_group_name.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);

            } else
                showDialogConfidentiality();
        });

        camera_id.setOnClickListener(view -> {
            if (isCameraVisible) {
                linLayout_parent.setVisibility(View.GONE);
                relLayout_pub_profile_photo.setVisibility(View.VISIBLE);
                isCameraVisible = false;
            }
        });

        normal_theme.setOnClickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);

            // changer a couleur des icons en noir
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            toolBar.setBackgroundResource(R.drawable.white_grey_border_bottom);
            toolbar_title.setTextColor(getColor(R.color.black));
            arrowBack.setBackgroundResource(R.drawable.selector_circle);
            close.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

            theme = getString(R.string.theme_normal);
        });

        blue_theme.setOnClickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.blue_600));

            // changer a couleur des icons en blanc
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_blue_background);
            toolbar_title.setTextColor(getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            theme = getString(R.string.theme_blue);
        });

        brown_theme.setOnClickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.background_brown));

            // changer a couleur des icons en blanc
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_brown_background);
            toolbar_title.setTextColor(getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            theme = getString(R.string.theme_brown);
        });

        pink_theme.setOnClickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.pink));

            // changer a couleur des icons en blanc
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_pink_background);
            toolbar_title.setTextColor(getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            theme = getString(R.string.theme_pink);
        });

        green_theme.setOnClickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.vertWatsApp));

            // changer a couleur des icons en blanc
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_green_background);
            toolbar_title.setTextColor(getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            theme = getString(R.string.theme_green);
        });

        black_theme.setOnClickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.black));

            // changer a couleur des icons en blanc
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(0);

            toolBar.setBackgroundResource(R.drawable.toolbar_black_background);
            toolbar_title.setTextColor(getColor(R.color.white));
            arrowBack.setBackgroundResource(R.drawable.selector_circle2);
            close.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            theme = getString(R.string.theme_black);
        });

        relLayout_update.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                isScreenEnabled = false;
                progressbar.setVisibility(View.VISIBLE);
                if (user_groups != null) {
                    updateData();
                }
            }
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGalleryResponseChallengePhotosFolders bottomSheet2 = new BottomSheetGalleryResponseChallengePhotosFolders(context, adapter, imageList, toolBar_textview);
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
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isLinLayout_invite_visible) {
                    if (!isCameraVisible) {
                        if (user_groups != null) {
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            finish();

                        } else {
                            linLayout_parent.setVisibility(View.VISIBLE);
                            relLayout_pub_profile_photo.setVisibility(View.GONE);
                            isCameraVisible = true;
                        }
                    } else {
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    }
                }
            }
        });
    }

    private void showDialogConfidentiality() {
        dialog.setContentView(R.layout.layout_choose_confidentiality);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        RadioButton public_radio = dialog.findViewById(R.id.radio1);
        RadioButton private_radio = dialog.findViewById(R.id.radio2);

        RelativeLayout relLayout = dialog.findViewById(R.id.relLayout);
        TextView msg_public = dialog.findViewById(R.id.msg_public);
        TextView msg_private = dialog.findViewById(R.id.msg_private);
        TextView btn_finish = dialog.findViewById(R.id.btn_finish);

        public_radio.setOnClickListener(view -> {
            msg_public.setVisibility(View.VISIBLE);
            msg_private.setVisibility(View.GONE);
            relLayout.setVisibility(View.VISIBLE);
            btn_finish.setVisibility(View.VISIBLE);

            isPublic = true;
            isPrivate = false;
        });

        private_radio.setOnClickListener(view -> {
            msg_private.setVisibility(View.VISIBLE);
            msg_public.setVisibility(View.GONE);
            relLayout.setVisibility(View.VISIBLE);
            btn_finish.setVisibility(View.VISIBLE);

            isPublic = false;
            isPrivate = true;
        });

        dialog.setOnCancelListener(dialogInterface -> {
            relLayout.setVisibility(View.GONE);
            btn_finish.setVisibility(View.GONE);
        });

        // show dialog
        dialog.show();

        btn_finish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                dialog.dismiss();
                progressbar.setVisibility(View.VISIBLE);
                uploadData();
            }
        });
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    private void uploadData() {
        edit_group_name.setCursorVisible(false);
        edit_about_group.setCursorVisible(false);
        String size = getImageSize(Uri.parse(imgUrl));
        long bitmap_size = Long.parseLong(size);

        Uri uri = Uri.parse(imgUrl);
        Uri fullUri = Uri.parse(fullImgUrl);
        if (originOfPhoto.equals("pickPhoto")) {
            if (bitmap_size <= 1000000)
                uploadPhotoWithoutCompress(uri, fullUri);
            else
                uploadPhotoWithCompress(uri, fullUri);

        } else {
            uploadPhotoWithCompress(uri, fullUri);
        }
    }

    private void updateData() {
        String size = getImageSize(Uri.parse(imgUrl));
        long bitmap_size = Long.parseLong(size);

        Uri uri = Uri.parse(imgUrl);
        Uri fullUri = Uri.parse(fullImgUrl);
        if (originOfPhoto.equals("pickPhoto")) {
            if (bitmap_size <= 1000000)
                updatePhotoWithoutCompress(uri, fullUri);
            else
                updatePhotoWithCompress(uri, fullUri);

        } else {
            updatePhotoWithCompress(uri, fullUri);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                imgUrl = String.valueOf(resultUri);
                GradientDrawable drawable = (GradientDrawable) relLayout_profile.getBackground();
                drawable.mutate();
                drawable.setStroke(0, Color.WHITE);

                if (!isCameraVisible) {
                    if (user_groups != null)
                        relLayout_update.setVisibility(View.VISIBLE);

                    linLayout_parent.setVisibility(View.VISIBLE);
                    relLayout_pub_profile_photo.setVisibility(View.GONE);
                    linLayout_update_photo.setVisibility(View.VISIBLE);
                    linLayout_add_photo.setVisibility(View.GONE);
                    isCameraVisible = true;

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .load(imgUrl)
                                .placeholder(R.drawable.ic_baseline_photo_camera_24)
                                .into(camera_id);
                    }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                android.util.Log.d(TAG, "onActivityResult: error: "+error);
            }
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
            Collections.reverse(imageList);

            String takePicture = "takePicture";
            setupGridView(takePicture);
            adapter.notifyDataSetChanged();
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
        String pickPhoto = "pickPhoto";
        setupGridView(pickPhoto);
    }

    private void setupGridView(String imageOrigine){
        //use the grid adapter to adapter the images to gridview
        Collections.reverse(imageList);
        adapter = new StoryGridImageAdapter(context, imageList, null);
        recyclerView.setAdapter(adapter);
        // the origine of photo
        originOfPhoto = imageOrigine;

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            mSelectedImage = imageList.get(0).getImage();
        } catch (ArrayIndexOutOfBoundsException e){
            android.util.Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "setupGridView: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            android.util.Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mSelectedImage = imageList.get(position).getImage();

            fullImgUrl = mSelectedImage;
            Uri uri = Uri.fromFile(new File(mSelectedImage));
            CropImage.activity(uri).start(this);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    private void uploadPhotoWithoutCompress(Uri profileUri, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+profileUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String profileUrl = String.valueOf(firebaseUrl);

                downloadDataWithoutCompress(profileUrl, fullUri);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadPhotoWithCompress(Uri profileUri, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+profileUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadPhotoWithCompress: "+e.getMessage());
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
                String profileUrl = String.valueOf(firebaseUrl);

                downloadDataWithCompress(profileUrl,  fullUri);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadDataWithoutCompress(String profileUrl, Uri fullUri) {
        String newGroupKey = reference.push().getKey();
        Date date=new Date();

        if (TextUtils.isEmpty(theme))
            theme = "Normal";

        UserGroups userGroups = new UserGroups();

        // suppress the group
        userGroups.setSuppressed(false);

        userGroups.setGroup_id(newGroupKey);
        userGroups.setHeader(false);
        userGroups.setGroup_paused(false);
        userGroups.setFull_photo("");
        userGroups.setAdmin_master(user_id);
        userGroups.setGroup_name(group_name);
        userGroups.setUser_id(user_id);
        userGroups.setGroup_profile_photo_id(newGroupKey);
        userGroups.setProfile_photo(profileUrl);
        userGroups.setCover_photo("");
        userGroups.setAdmin_master_bio("");
        userGroups.setGroup_message(group_message);
        userGroups.setGroup_theme(theme);
        userGroups.setGroup_private(isPrivate);
        userGroups.setGroup_public(isPublic);
        userGroups.setDate_created(date.getTime());
        userGroups.setLastSeenInGroup(0);
        userGroups.setRule_title_one("");
        userGroups.setRule_detail_one("");
        userGroups.setRule_title_two("");
        userGroups.setRule_detail_two("");
        userGroups.setRule_title_three("");
        userGroups.setRule_detail_three("");
        userGroups.setRule_title_four("");
        userGroups.setRule_detail_four("");
        userGroups.setRule_title_five("");
        userGroups.setRule_detail_five("");
        userGroups.setRule_title_six("");
        userGroups.setRule_detail_six("");
        userGroups.setRule_title_seven("");
        userGroups.setRule_detail_seven("");
        userGroups.setRule_title_eight("");
        userGroups.setRule_detail_eight("");
        userGroups.setRule_title_nine("");
        userGroups.setRule_detail_nine("");
        userGroups.setRule_title_ten("");
        userGroups.setRule_detail_ten("");
        userGroups.setAdmin_created_rules("");
        userGroups.setAdmin_modify_rules("");
        userGroups.setPost_points("0");
        userGroups.setShare_points("0");
        userGroups.setComment_points("0");
        userGroups.setLike_points("0");

        assert newGroupKey != null;
        reference.child(context.getString(R.string.dbname_user_group))
                .child(user_id)
                .child(newGroupKey)
                .setValue(userGroups).addOnCompleteListener(task -> uploadNewFullPhotoWithoutCompress(profileUrl, fullUri, newGroupKey));
    }

    private void downloadDataWithCompress(String profileUrl, Uri fullUri) {
        String newGroupKey = reference.push().getKey();
        Date date=new Date();
        if (TextUtils.isEmpty(theme))
            theme = "Normal";

        UserGroups userGroups = new UserGroups();

        // suppress the group
        userGroups.setSuppressed(false);

        userGroups.setGroup_id(newGroupKey);
        userGroups.setHeader(false);
        userGroups.setGroup_paused(false);
        userGroups.setFull_photo("");
        userGroups.setAdmin_master(user_id);
        userGroups.setGroup_name(group_name);
        userGroups.setUser_id(user_id);
        userGroups.setGroup_profile_photo_id(newGroupKey);
        userGroups.setProfile_photo(profileUrl);
        userGroups.setCover_photo("");
        userGroups.setAdmin_master_bio("");
        userGroups.setGroup_message(group_message);
        userGroups.setGroup_theme(theme);
        userGroups.setGroup_private(isPrivate);
        userGroups.setGroup_public(isPublic);
        userGroups.setDate_created(date.getTime());
        userGroups.setLastSeenInGroup(0);
        userGroups.setRule_title_one("");
        userGroups.setRule_detail_one("");
        userGroups.setRule_title_two("");
        userGroups.setRule_detail_two("");
        userGroups.setRule_title_three("");
        userGroups.setRule_detail_three("");
        userGroups.setRule_title_four("");
        userGroups.setRule_detail_four("");
        userGroups.setRule_title_five("");
        userGroups.setRule_detail_five("");
        userGroups.setRule_title_six("");
        userGroups.setRule_detail_six("");
        userGroups.setRule_title_seven("");
        userGroups.setRule_detail_seven("");
        userGroups.setRule_title_eight("");
        userGroups.setRule_detail_eight("");
        userGroups.setRule_title_nine("");
        userGroups.setRule_detail_nine("");
        userGroups.setRule_title_ten("");
        userGroups.setRule_detail_ten("");
        userGroups.setAdmin_created_rules("");
        userGroups.setAdmin_modify_rules("");
        userGroups.setPost_points("0");
        userGroups.setShare_points("0");
        userGroups.setComment_points("0");
        userGroups.setLike_points("0");

        assert newGroupKey != null;
        reference.child(context.getString(R.string.dbname_user_group))
                .child(user_id)
                .child(newGroupKey)
                .setValue(userGroups).addOnCompleteListener(task -> uploadNewFullPhotoWithCompress(profileUrl, fullUri, newGroupKey));
    }

    public void uploadNewFullPhotoWithoutCompress(String profileUrl, Uri fullUri, String newGroupKey) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+fullUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String fullProfileUrl = String.valueOf(firebaseUrl);

                uploadFullPhotoData(profileUrl, fullProfileUrl, newGroupKey);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });

    }

    public void uploadNewFullPhotoWithCompress(String profileUrl, Uri fullUri, String newGroupKey) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+fullUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadNewFullPhotoWithCompress: "+e.getMessage());
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
                String fullProfileUrl = String.valueOf(firebaseUrl);

                uploadFullPhotoData(profileUrl, fullProfileUrl, newGroupKey);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });

    }

    private void uploadFullPhotoData(String profileUrl, String fullProfileUrl, String newGroupKey) {
        // add cover profile photo to group node
        addPhotoToGroupDatabase(profileUrl, fullProfileUrl, newGroupKey);

        reference.child(context.getString(R.string.dbname_user_group))
                .child(user_id)
                .child(newGroupKey)
                .child(context.getString(R.string.field_full_photo))
                .setValue(fullProfileUrl)
                .addOnSuccessListener(unused -> {
                    progressbar.setVisibility(View.GONE);
                    isLinLayout_invite_visible = true;
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, InvitePersonsAfterGroupCreated.class);
                    intent.putExtra("theme", theme);
                    intent.putExtra("newGroupKey", newGroupKey);
                    startActivity(intent);
                    finish();
                });
    }

    private void addPhotoToGroupDatabase(String profileUrl, String fullProfileUrl, String newGroupKey) {
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
        // pour le grid profile
        battleModel.setGridRecyclerImage(false);
        battleModel.setReponse_deja(false);
        battleModel.setDetails("");

        // group
        battleModel.setGroup(true);
        battleModel.setGroup_private(isPrivate);
        battleModel.setGroup_public(isPublic);
        battleModel.setGroup_cover_profile_photo(true);
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
        battleModel.setAdmin_master(user_id);
        battleModel.setGroup_id(newGroupKey);
        battleModel.setPublisher(user_id);
        battleModel.setGroup_profile_photo(profileUrl);
        battleModel.setGroup_full_profile_photo(fullProfileUrl);
        battleModel.setGroup_user_background_profile_url("");
        battleModel.setGroup_user_background_full_profile_url("");

        battleModel.setComment_text(false);
        battleModel.setComment_subject("");
        battleModel.setComment_theme("");
        battleModel.setBig_image(false);

        battleModel.setCaption("");
        battleModel.setUrl("");
        battleModel.setDate_created(date.getTime());
        battleModel.setPhoto_id(newGroupKey);
        battleModel.setUser_id(user_id);
        battleModel.setTags("");

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
        battleModel.setUser_profile(false);
        battleModel.setUser_id_sharing("");
        // for saved
        battleModel.setUsername("");
        battleModel.setProfileUrl("");
        battleModel.setAuth_user_id("");
        battleModel.setUser_id_shared("");

        // post identity
        String post_key = reference.push().getKey();
        battleModel.setPost_identity(post_key);

        reference.child(getString(R.string.dbname_group))
                .child(newGroupKey) // in place of group_id
                .child(newGroupKey) // in place of photo_id necessary is not duplication
                .setValue(battleModel);

        reference.child(getString(R.string.dbname_group_media))
                .child(newGroupKey)
                .setValue(battleModel);
    }

    // update profile group photo __________________________________________________________________
    private void updatePhotoWithoutCompress(Uri profileUri, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+profileUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String profileUrl = String.valueOf(firebaseUrl);

                updateProfileUrlWithoutCompress(profileUrl, fullUri);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void updatePhotoWithCompress(Uri profileUri, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+profileUri));
        } catch (IOException e) {
            Log.d(TAG, "updatePhotoWithCompress: "+e.getMessage());
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
                String profileUrl = String.valueOf(firebaseUrl);

                updateProfileUrlWithCompress(profileUrl, fullUri);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileUrlWithoutCompress(String profileUrl, Uri fullUri) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("profile_photo", profileUrl);

        reference.child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .child(user_groups.getGroup_id())
                .updateChildren(map).addOnSuccessListener(unused -> updatedNewFullPhotoWithoutCompress(profileUrl, fullUri));
    }

    public void updatedNewFullPhotoWithoutCompress(String profileUrl, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+fullUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String fullProfileUrl = String.valueOf(firebaseUrl);

                updateFullProfileUrl(profileUrl, fullProfileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });

    }

    private void updateProfileUrlWithCompress(String profileUrl, Uri fullUri) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("profile_photo", profileUrl);

        reference.child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .child(user_groups.getGroup_id())
                .updateChildren(map).addOnSuccessListener(unused -> updatedNewFullPhotoWithCompress(profileUrl, fullUri));
    }

    public void updatedNewFullPhotoWithCompress(String profileUrl, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+fullUri));
        } catch (IOException e) {
            Log.d(TAG, "updatedNewFullPhotoWithCompress: "+e.getMessage());
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
                String fullProfileUrl = String.valueOf(firebaseUrl);

                updateFullProfileUrl(profileUrl, fullProfileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });

    }

    private void updateFullProfileUrl(String profileUrl, String fullProfileUrl) {
        updatePhotoToGroupDatabase(profileUrl, fullProfileUrl, user_groups.getGroup_id());

        // upadte full image
        HashMap<String, Object> map = new HashMap<>();
        map.put("full_photo", fullProfileUrl);

        reference.child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .child(user_groups.getGroup_id())
                .updateChildren(map).addOnSuccessListener(unused -> {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                });
    }

    private void updatePhotoToGroupDatabase(String profileUrl, String fullProfileUrl, String group_id) {
        Date date=new Date();

        String photo_id = reference.push().getKey();

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
        battleModel.setGridRecyclerImage(false);
        battleModel.setReponse_deja(false);
        battleModel.setDetails("");

        // group
        battleModel.setGroup(true);
        battleModel.setGroup_private(isPrivate);
        battleModel.setGroup_public(isPublic);
        battleModel.setGroup_cover_profile_photo(true);
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
        battleModel.setAdmin_master(user_id);
        battleModel.setGroup_id(group_id);
        battleModel.setPublisher(user_id);
        battleModel.setGroup_profile_photo(profileUrl);
        battleModel.setGroup_full_profile_photo(fullProfileUrl);
        battleModel.setGroup_user_background_profile_url("");
        battleModel.setGroup_user_background_full_profile_url("");

        battleModel.setComment_text(false);
        battleModel.setComment_subject("");
        battleModel.setComment_theme("");
        battleModel.setBig_image(false);

        battleModel.setCaption("");
        battleModel.setUrl("");
        battleModel.setDate_created(date.getTime());
        battleModel.setPhoto_id(photo_id);
        battleModel.setUser_id(user_id);
        battleModel.setTags("");

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
        battleModel.setUser_profile(false);
        battleModel.setUser_id_sharing("");
        // for saved
        battleModel.setUsername("");
        battleModel.setProfileUrl("");
        battleModel.setAuth_user_id("");
        battleModel.setUser_id_shared("");

        // post identity
        String post_key = reference.push().getKey();
        battleModel.setPost_identity(post_key);

        assert photo_id != null;
        reference.child(getString(R.string.dbname_group))
                .child(group_id)
                .child(photo_id)
                .setValue(battleModel);

        reference.child(getString(R.string.dbname_group_media))
                .child(photo_id)
                .setValue(battleModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }
}