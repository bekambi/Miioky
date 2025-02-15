package com.bekambimouen.miiokychallenge.groups.publication;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.crop_profile_image.CropImage;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.instacropper.InstaCropperView;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
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
import java.util.Objects;

public class GroupCreateMemberBackgroundProfilePhoto extends AppCompatActivity {
    private static final String TAG = "GroupCreateUserBackgroundProfilePhoto";

    //widgets
    private RecyclerView recyclerView;
    private InstaCropperView mInstaCropper;
    private TextView nextScreen;
    private ProgressBar progressbar, main_progressBar;
    private RelativeLayout parent;

    //vars
    private GroupCreateMemberBackgroundProfilePhoto context;
    private StoryGridImageAdapter adapter;
    private UserGroups user_groups;
    private ArrayList<ImageModel> imageList;
    private String mSelectedImage, originOfPhoto;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isScreenEnabled = true, isPhotoDownloading = false;
    private String fullImgUrl;

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
        setContentView(R.layout.activity_group_create_member_background_profile_photo);
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

        parent = findViewById(R.id.parent);
        progressbar = findViewById(R.id.progressbar);
        ImageView iv_photo = findViewById(R.id.iv_photo);
        nextScreen = findViewById(R.id.tv_Next);
        mInstaCropper = findViewById(R.id.instacropper);
        main_progressBar = findViewById(R.id.main_progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // get all images from gallery
        getAllImages();

        ImageView shareClose = findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(v -> {
            android.util.Log.d(TAG, "onClick: closing the gallery fragment.");
            finish();
        });

        nextScreen.setOnClickListener(v -> {
            fullImgUrl = mSelectedImage;
            Uri uri = Uri.fromFile(new File(mSelectedImage));
            CropImage.activity(uri).start(this);
        });

        iv_photo.setOnClickListener(view1 -> {
            Log.d(TAG, "onCreate: takePicture");
            takePicture();
        });
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
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);

        arrowBack.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isPhotoDownloading)
                    Toast.makeText(context, context.getString(R.string.pleaseWait), Toast.LENGTH_SHORT).show();
                else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mInstaCropper.setImageUri(resultUri);
                nextScreen.setEnabled(false);
                isPhotoDownloading = true;
                nextScreen.setVisibility(View.INVISIBLE);

                String imgUrl = String.valueOf(resultUri);

                uploadData(imgUrl);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                android.util.Log.d(TAG, "onActivityResult: error: "+error);
            }
        }
    }

    private void uploadData(String imgUrl) {
        isScreenEnabled = false;
        progressbar.setVisibility(View.VISIBLE);

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

            // to add  image in cropview
            mInstaCropper.setImageUri(Uri.fromFile(new File(path)));
            String takePicture = "takePicture";
            setupGridView(takePicture);
            adapter.notifyDataSetChanged();

            nextScreen.setEnabled(true);
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
        adapter = new StoryGridImageAdapter(context, imageList, main_progressBar);
        recyclerView.setAdapter(adapter);
        // the origine of photo
        originOfPhoto = imageOrigine;

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            mInstaCropper.setImageUri(Uri.fromFile(new File(imageList.get(0).getImage())));
            mSelectedImage = imageList.get(0).getImage();
        } catch (ArrayIndexOutOfBoundsException e){
            android.util.Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "setupGridView: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            android.util.Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mInstaCropper.setImageUri(Uri.fromFile(new File(imageList.get(position).getImage())));
            mSelectedImage = imageList.get(position).getImage();

            nextScreen.setEnabled(true);
            nextScreen.setVisibility(View.VISIBLE);
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

                uploadNewFullPhotoWithoutCompress(profileUrl, fullUri);

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

                uploadNewFullPhotoWithCompress(profileUrl,  fullUri);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    public void uploadNewFullPhotoWithoutCompress(String profileUrl, Uri fullUri) {
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

                addPhotoToGroupDatabase(profileUrl, fullProfileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });

    }

    public void uploadNewFullPhotoWithCompress(String profileUrl, Uri fullUri) {
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

                addPhotoToGroupDatabase(profileUrl, fullProfileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void addPhotoToGroupDatabase(String profileUrl, String fullProfileUrl) {
        String newPhotoKey = reference.child(getString(R.string.dbname_battle_media)).push().getKey();
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
        battleModel.setGroup_private(user_groups.isGroup_private());
        battleModel.setGroup_public(user_groups.isGroup_public());
        battleModel.setGroup_cover_profile_photo(false);
        battleModel.setGroup_cover_background_profile_photo(true);
        battleModel.setGroup_recyclerItem(false);
        battleModel.setGroup_imageUne(false);
        battleModel.setGroup_videoUne(false);
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
        battleModel.setAdmin_master(user_groups.getAdmin_master());
        battleModel.setGroup_id(user_groups.getGroup_id());
        battleModel.setPublisher(user_id);
        battleModel.setGroup_profile_photo(user_groups.getProfile_photo());
        battleModel.setGroup_full_profile_photo("");
        battleModel.setGroup_user_background_profile_url(profileUrl);
        battleModel.setGroup_user_background_full_profile_url(fullProfileUrl);

        battleModel.setComment_text(false);
        battleModel.setComment_subject("");
        battleModel.setComment_theme("");
        battleModel.setBig_image(false);

        battleModel.setCaption("");
        battleModel.setUrl("");
        battleModel.setDate_created(date.getTime());
        battleModel.setPhoto_id(newPhotoKey);
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

        // un autre adaptateur pour le recyclerview
        assert newPhotoKey != null;
        reference.child(getString(R.string.dbname_group))
                .child(user_groups.getGroup_id())
                .child(newPhotoKey)
                .setValue(battleModel);

        reference.child(getString(R.string.dbname_group_media))
                .child(newPhotoKey)
                .setValue(battleModel).addOnCompleteListener(task -> {
                    progressbar.setVisibility(View.GONE);
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}