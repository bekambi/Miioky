package com.bekambimouen.miiokychallenge.story.publication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryResponseChallengePhotosFolders;
import com.bekambimouen.miiokychallenge.instacropper.InstaCropperView;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
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

public class PublicationStories extends AppCompatActivity {
    private static final String TAG = "PublicationStories";

    // select image ______________________________________________________________
    //constants
    private PublicationStories context;

    //widgets
    private RecyclerView recyclerView;
    private InstaCropperView mInstaCropper;
    private RelativeLayout relLayout_download;
    private RelativeLayout relLayout_select;
    private ProgressBar progressbar, main_progressBar;
    private MyEditText edit_caption;

    //vars
    private StoryGridImageAdapter adapter;
    private ArrayList<ImageModel> imageList;
    private String mSelectedImage, originOfPhoto;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isDownloadVisible = false;

    // download image ______________________________________________________________
    // vars
    private String imageUrl;
    private int imageCount = 0;
    private boolean isScreenEnabled = true;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageReference;
    private FirebaseDatabase database;
    private FirebaseMethods mFirebaseMethods;


    private void getWhiteTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.white));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //change bottom bar calor
        updateNavigationBarWhiteColor(window);
    }

    // set bottom navigation bar color
    protected void updateNavigationBarWhiteColor(Window window) {
        if (window == null) {
            return;
        }

        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setNavigationBarColor(getColor(R.color.white));//black or white
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
            controller.setAppearanceLightNavigationBars(true);
        }
    }

    private void getBlackTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);

        //change bottom bar calor
        updateNavigationBarBlackColor(window);
    }

    // set bottom navigation bar color
    protected void updateNavigationBarBlackColor(Window window) {
        if (window == null) {
            return;
        }

        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setNavigationBarColor(getColor(R.color.black));//black or white
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
            controller.setAppearanceLightNavigationBars(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWhiteTheme();
        setContentView(R.layout.activity_publication_stories);
        context = this;

        new Handler().postDelayed(() -> {
            mStorageReference = FirebaseStorage.getInstance().getReference();
            database=FirebaseDatabase.getInstance();

            imageList = new ArrayList<>();

            main_progressBar = findViewById(R.id.main_progressBar);
            RelativeLayout menu_item = findViewById(R.id.menu_item);
            TextView toolBar_textview = findViewById(R.id.toolBar_textview);
            relLayout_select = findViewById(R.id.relLayout_select);
            relLayout_download = findViewById(R.id.relLayout_download);
            ImageView iv_image = findViewById(R.id.iv_image);
            ImageView iv_photo = findViewById(R.id.iv_photo);
            CardView cardView = findViewById(R.id.cardView);
            mInstaCropper = findViewById(R.id.instacropper);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setItemAnimator(null);
            recyclerView.setHasFixedSize(true);

            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

            cardView.setVisibility(View.GONE);
            toolBar_textview.setText(context.getString(R.string.story));

            // get all images from gallery
            getAllImages();

            ImageView shareClose = findViewById(R.id.ivCloseShare);
            shareClose.setOnClickListener(v -> {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                if (isDownloadVisible) {
                    isDownloadVisible = false;

                    imageUrl = "";
                    relLayout_download.setVisibility(View.GONE);
                    relLayout_select.setVisibility(View.VISIBLE);
                    getWhiteTheme();

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            });

            getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (isDownloadVisible) {
                        isDownloadVisible = false;
                        getWhiteTheme();

                        imageUrl = "";
                        relLayout_download.setVisibility(View.GONE);
                        relLayout_select.setVisibility(View.VISIBLE);

                    } else {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    }
                }
            });

            TextView nextScreen = findViewById(R.id.tv_Next);
            nextScreen.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                isDownloadVisible = true;
                relLayout_select.setVisibility(View.GONE);
                relLayout_download.setVisibility(View.VISIBLE);
                getBlackTheme();
                imageUrl = mSelectedImage;

                if (!isFinishing()) {
                    Glide.with(context)
                            .load(imageUrl)
                            .into(iv_image);
                }
            });

            iv_photo.setOnClickListener(view1 -> takePicture());

            // download image _______________________________________________________________
            initdownload();
            setupFirebaseAuth();

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

        }, 1000);
    }

    // select image ______________________________________________________________________
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
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "setupGridView: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mInstaCropper.setImageUri(Uri.fromFile(new File(imageList.get(position).getImage())));
            mSelectedImage = imageList.get(position).getImage();
        });

    }
    // download image _______________________________________________________________
    private void initdownload() {
        RelativeLayout arrowBack_download = findViewById(R.id.arrowBack_download);
        ImageView btn_publish = findViewById(R.id.btn_publish);
        progressbar = findViewById(R.id.progressbar);
        edit_caption = findViewById(R.id.edit_caption);

        arrowBack_download.setOnClickListener(view -> {
            isDownloadVisible = false;
            imageUrl = "";
            relLayout_download.setVisibility(View.GONE);
            relLayout_select.setVisibility(View.VISIBLE);
            getWhiteTheme();
        });

        btn_publish.setOnClickListener(view -> {
            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(this, getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

            } else {
                progressbar.setVisibility(View.VISIBLE);
                isScreenEnabled = false;
                closeKeyboard();
                edit_caption.clearFocus();
                String caption = Objects.requireNonNull(edit_caption.getText()).toString();

                if (originOfPhoto.equals("pickPhoto")) {
                    String size = getImageSize(Uri.parse(imageUrl));
                    long bitmap_size = Long.parseLong(size);

                    Uri uri = Uri.parse(imageUrl);
                    if (bitmap_size <= 1000000)
                        uploadOnePhotoWithoutCompress(caption, uri);
                    else
                        uploadOnePhotoWithCompres(caption, uri);

                } else {
                    Uri uri = Uri.parse(imageUrl);
                    uploadOnePhotoWithCompres(caption, uri);
                }
            }
        });
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    private void uploadOnePhotoWithoutCompress(final String caption, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_STORIES_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                setProfilePhoto(url1, caption);

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
                .child(filePaths.FIREBASE_STORIES_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadOnePhotoWithCompres: "+e.getMessage());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // here we can choose quality factor
        // in third parameter(ex. here it is 25)
        assert bmp != null;
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

                setProfilePhoto(url1, caption);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void setProfilePhoto(String url, String caption) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        String myid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_story))
                .child(myid);

        String storyid = reference.push().getKey();
        long timeend = System.currentTimeMillis()+86400000; // 1 day later

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("mediaUrl", url);
        hashMap.put("timestart", ServerValue.TIMESTAMP);
        hashMap.put("timeend", timeend);
        hashMap.put("storyid", storyid);
        hashMap.put("user_id", myid);
        hashMap.put("caption", caption);
        // suppress story
        hashMap.put("suppressed", false);

        assert storyid != null;
        reference.child(storyid).setValue(hashMap)
                .addOnCompleteListener(task -> {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                });
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

    /*
     ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(context);
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        new Handler().postDelayed(() -> mAuth.addAuthStateListener(mAuthListener), 1000);
    }

    public void internetIsConnected() {
        // internet control
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(this, getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}