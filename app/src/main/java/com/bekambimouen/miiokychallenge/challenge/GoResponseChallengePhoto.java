package com.bekambimouen.miiokychallenge.challenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryResponseChallengePhotosFolders;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GoResponseChallengePhoto extends AppCompatActivity {
    private static final String TAG = "GoResponseChallengePhoto";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_view_overlay;
    private ProgressBar main_progressBar;

    // vars
    private GoResponseChallengePhoto context;
    private StoryGridImageAdapter adapter;
    ArrayList<ImageModel> imageList;
    private String mSelectedImage, originOfPhoto;
    String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private ModelInvite model_invite;
    private String category_status, from_group_challenge, from_home_challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_go_response_challenge_photo);
        context = this;

        imageList = new ArrayList<>();

        try {
            Gson gson = new Gson();
            model_invite = gson.fromJson(getIntent().getStringExtra("model_invite"), ModelInvite.class);
            category_status = getIntent().getStringExtra("category_status");
            from_group_challenge = getIntent().getStringExtra("from_group_challenge");
            from_home_challenge = getIntent().getStringExtra("from_home_challenge");

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        ImageView toolbar_photo = findViewById(R.id.toolbar_photo);
        ImageView img_play_un = findViewById(R.id.img_play_un);
        ImageView iv_photo = findViewById(R.id.iv_photo);
        ImageView shareClose = findViewById(R.id.ivCloseShare);
        TextView nextScreen = findViewById(R.id.tv_Next);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        main_progressBar = findViewById(R.id.main_progressBar);
        nextScreen.setVisibility(View.INVISIBLE);
        nextScreen.setEnabled(false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));

        String invite_url;
        if (!model_invite.getThumbnail_invite().isEmpty()) {
            invite_url = model_invite.getThumbnail_invite();
            img_play_un.setVisibility(View.VISIBLE);
        } else {
            invite_url = model_invite.getInvite_url();
        }

        Glide.with(context)
                .load(invite_url)
                .into(toolbar_photo);

        if (category_status != null) {
            String Category = Util_InviteChallengeCategory.getInviteChallengeCategory(context, model_invite);
            toolBar_textview.setText(Category);
        }

        // get all images from gallery
        getAllImages();

        iv_photo.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
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

        shareClose.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing the gallery fragment.");
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
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
        ImageModel imageModel = new ImageModel();
        imageModel.setImage(filePath);
        imageModel.setSelected(true);
        imageList.add(0, imageModel);

        String takePicture = "takePicture";
        setupGridView(takePicture);
        adapter.notifyDataSetChanged();
    }

    // get all images from external storage
    public void getAllImages(){
        imageList.clear();
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null,null, null);
        assert cursor != null;
        cursor.moveToLast();
        do
        {
            @SuppressLint("Range") String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            ImageModel imageModel = new ImageModel();
            imageModel.setImage(absolutePathOfImage);
            imageList.add(imageModel);
        }
        while (cursor.moveToPrevious());
        cursor.close();
        String pickPhoto = "pickPhoto";
        setupGridView(pickPhoto);
    }

    private void setupGridView(String imageOrigine){
        //use the grid adapter to adapter the images to gridview
        adapter = new StoryGridImageAdapter(context, imageList, main_progressBar);
        recyclerView.setAdapter(adapter);
        // the origine of photo
        originOfPhoto = imageOrigine;

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            mSelectedImage = imageList.get(0).getImage();
        } catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "setupGridView: error: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mSelectedImage = imageList.get(position).getImage();
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);

            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, DownloadResponseChallenge.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model_invite);

            intent.putExtra("model_invite", myGson);
            intent.putExtra("category_status", category_status);
            intent.putExtra("url", mSelectedImage);
            intent.putExtra("originOfPhoto", originOfPhoto);
            intent.putExtra("category_status", category_status);
            intent.putExtra("from_home_challenge", from_home_challenge);
            if (from_group_challenge != null)
                intent.putExtra("from_group_challenge", "from_group_challenge");
            startActivity(intent);
        });

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