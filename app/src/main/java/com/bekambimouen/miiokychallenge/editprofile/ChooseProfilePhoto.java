package com.bekambimouen.miiokychallenge.editprofile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryResponseChallengePhotosFolders;
import com.bekambimouen.miiokychallenge.crop_profile_image.CropImage;
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

public class ChooseProfilePhoto extends AppCompatActivity {
    private static final String TAG = "ChooseProfilePhotoFragment";

    //widgets
    private RecyclerView recyclerView;
    private InstaCropperView mInstaCropper;
    private TextView nextScreen;
    private ProgressBar progressbar;
    private ProgressBar main_progressBar;
    private RelativeLayout main;

    //vars
    private ChooseProfilePhoto context;
    private StoryGridImageAdapter adapter;
    ArrayList<ImageModel> imageList;
    private String mSelectedImage, originOfPhoto;
    String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isScreenEnabled = true;

    private String fullImgUrl;
    private Bitmap bmp;
    private ByteArrayOutputStream baos;

    // firebase
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String user_id;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_choose_profile_photo);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        imageList = new ArrayList<>();

        main = findViewById(R.id.main);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        progressbar = findViewById(R.id.progressbar);
        ImageView iv_photo = findViewById(R.id.iv_photo);
        nextScreen = findViewById(R.id.tv_Next);
        mInstaCropper = findViewById(R.id.instacropper);

        CardView cardView = findViewById(R.id.cardView);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        ImageView shareClose = findViewById(R.id.ivCloseShare);
        main_progressBar = findViewById(R.id.main_progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        cardView.setVisibility(View.GONE);
        toolBar_textview.setText(context.getString(R.string.profile_photo));

        // get all images from gallery
        getAllImages();

        shareClose.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing the gallery fragment.");
            finish();
        });

        nextScreen.setOnClickListener(v -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, main);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, main);

            } else {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                fullImgUrl = mSelectedImage;
                Uri uri = Uri.fromFile(new File(mSelectedImage));
                CropImage.activity(uri).start(context);
            }
        });

        iv_photo.setOnClickListener(view1 -> {
            Log.d(TAG, "onCreate: takePicture");
            takePicture();
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
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
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
        Cursor cursor = context.getContentResolver()
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
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "setupGridView: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mInstaCropper.setImageUri(Uri.fromFile(new File(imageList.get(position).getImage())));
            mSelectedImage = imageList.get(position).getImage();

            nextScreen.setEnabled(true);
            nextScreen.setVisibility(View.VISIBLE);
        });

    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri uri = result.getUri();
                mInstaCropper.setImageUri(uri);
                nextScreen.setEnabled(false);
                nextScreen.setVisibility(View.INVISIBLE);

                String imgUrl = String.valueOf(uri);

                // download image
                isScreenEnabled = false;
                progressbar.setVisibility(View.VISIBLE);

                String size = getImageSize(Uri.parse(imgUrl));
                long bitmap_size = Long.parseLong(size);
                Uri fullUri = Uri.parse(fullImgUrl);

                if (originOfPhoto.equals("pickPhoto")) {
                    if (bitmap_size <= 1000000)
                        uploadPhotoWithoutCompress(uri, fullUri);
                    else
                        uploadPhotoWithCompress(uri, fullUri);

                } else {
                    uploadPhotoWithCompress(uri, fullUri);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: error: "+error);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    // upload without compress ___________________________________________________________
    private void uploadPhotoWithoutCompress(Uri profileUri, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id  +  "/profile_photo" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+profileUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

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

    private void downloadDataWithoutCompress(String profileUrl, Uri fullUri) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + profileUrl);
        String newPhotoKey = myRef.push().getKey();

        assert newPhotoKey != null;
        // add url __________________________________________________________________________
        myRef.child(context.getString(R.string.dbname_invite_battle))
                .child(user_id)
                .child(newPhotoKey)
                .child(context.getString(R.string.field_invite_profile_photo))
                .setValue(profileUrl);

        // to make icon done appear on complete
        myRef.child(context.getString(R.string.dbname_complet_profile))
                .child(user_id)
                .child(context.getString(R.string.field_profileUrl))
                .setValue(profileUrl);

        // update profile url and photo_id ____________________________________________________
        HashMap<String, Object> map = new HashMap<>();
        map.put("photo_id", newPhotoKey);
        map.put("profileUrl", profileUrl);

        myRef.child(context.getString(R.string.dbname_user_account_settings))
                .child(user_id)
                .updateChildren(map);

        myRef.child(context.getString(R.string.dbname_users))
                .child(user_id)
                .updateChildren(map).addOnCompleteListener(task -> uploadNewFullPhotoWithoutCompress(profileUrl, fullUri, newPhotoKey));
    }

    private void uploadNewFullPhotoWithoutCompress(String profileUrl, Uri fullUri, String newPhotoKey) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id  +  "/full_profile_photo" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+fullUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String fullProfileUrl = String.valueOf(firebaseUrl);

                uploadFullPhotoData(profileUrl, fullProfileUrl, newPhotoKey);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    // upload with compress ___________________________________________________________
    private void uploadPhotoWithCompress(Uri profileUri, Uri fullUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id  +  "/profile_photo" + calendar.getTimeInMillis());

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

    private void downloadDataWithCompress(String profileUrl, Uri fullUri) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + profileUrl);
        String newPhotoKey = myRef.push().getKey();

        assert newPhotoKey != null;
        // add url __________________________________________________________________________
        myRef.child(context.getString(R.string.dbname_invite_battle))
                .child(user_id)
                .child(newPhotoKey)
                .child(context.getString(R.string.field_invite_profile_photo))
                .setValue(profileUrl);

        // to make icon done appear on complete
        myRef.child(context.getString(R.string.dbname_complet_profile))
                .child(user_id)
                .child(context.getString(R.string.field_profileUrl))
                .setValue(profileUrl);

        // update profile url and photo_id ____________________________________________________
        HashMap<String, Object> map = new HashMap<>();
        map.put("photo_id", newPhotoKey);
        map.put("profileUrl", profileUrl);

        myRef.child(context.getString(R.string.dbname_user_account_settings))
                .child(user_id)
                .updateChildren(map);

        myRef.child(context.getString(R.string.dbname_users))
                .child(user_id)
                .updateChildren(map).addOnCompleteListener(task -> uploadNewFullPhotoWithCompress(profileUrl, fullUri, newPhotoKey));
    }

    private void uploadNewFullPhotoWithCompress(String profileUrl, Uri fullUri, String newPhotoKey) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id  +  "/full_profile_photo" + calendar.getTimeInMillis());

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

                uploadFullPhotoData(profileUrl, fullProfileUrl, newPhotoKey);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }
    // End __________________________________________________________________________________
    private void uploadFullPhotoData(String profileUrl, String fullProfileUrl, String newGroupKey) {
        addPhotoToBattleDatabase(profileUrl, fullProfileUrl, newGroupKey);

        // update full profile url
        HashMap<String, Object> map = new HashMap<>();
        map.put("full_profileUrl", fullProfileUrl);

        myRef.child(context.getString(R.string.dbname_user_account_settings))
                .child(user_id)
                .updateChildren(map);

        myRef.child(context.getString(R.string.dbname_users))
                .child(user_id)
                .updateChildren(map).addOnCompleteListener(task -> {
                    progressbar.setVisibility(View.GONE);
                    finish();
                });
    }

    private void addPhotoToBattleDatabase(String profileUrl, String fullProfileUrl, String newPhotoKey) {
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

        battleModel.setCaption("");
        battleModel.setUrl("");
        battleModel.setDate_created(date.getTime());
        battleModel.setPhoto_id(newPhotoKey);
        battleModel.setUser_id(user_id);
        battleModel.setTags("");
        battleModel.setDetails("");

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
        // group
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

        // user profile photo ______________________________________________
        battleModel.setUser_profile(true);
        battleModel.setUser_profile_photo(profileUrl);
        battleModel.setUser_full_profile_photo(fullProfileUrl);
        // ______________________________________________________________
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

        // _______________________________________________________________________
        myRef.child(getString(R.string.dbname_battle))
                .child(user_id)
                .child(newPhotoKey)
                .setValue(battleModel);

        myRef.child(getString(R.string.dbname_battle_media))
                .child(newPhotoKey)
                .setValue(battleModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, main);
    }
}