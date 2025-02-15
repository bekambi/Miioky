package com.bekambimouen.miiokychallenge.messages.publication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.VideoCompress;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryResponseChallengePhotosFolders;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryVideosChatFolders;
import com.bekambimouen.miiokychallenge.challenge_publication.ViewLocalVideo;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.messages.publication.adapter.VideoChatListAdapter;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Chat;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.LogMessage;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimType;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimVideo;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class PubPhotosChat extends AppCompatActivity {
    private static final String TAG = "PubPhotosChat";

    // video publication ____________________________________________________________________
    // widgets
    private RelativeLayout parent, relLayout_select_video, relLayout_img_play, relLayout_view_overlay;
    private LinearLayout linLayout_select_video;
    private RecyclerView recyclerView_video;
    private ProgressBar main_progressBar;

    // comment data
    private String userid;

    // vars
    public static final int PICK_VIDEOS = 20;
    private final String[] projection_video = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DURATION};
    private ArrayList<VideoItem> videoList;
    private String selectedVideo;
    private String mCurrentVideoPath;
    private VideoChatListAdapter videoAdapter;
    private File video;
    private File thumbnailFile;
    private File compressedFile;
    private long startTime, endTime;
    private final String outputDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private ProgressDialog progressDialog;
    private boolean isPubVideoVisible = false;
    private boolean isVideo = false;

    // image publication ____________________________________________________________________
    //widgets
    private RelativeLayout relLayout_download_photo_and_video;
    private LinearLayout linLayout_select_photo;
    private RecyclerView recyclerView;

    //vars
    private PubPhotosChat context;
    private StoryGridImageAdapter adapter;
    private ArrayList<ImageModel> imageList;
    private String mSelectedImage, originOfPhoto;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isPubPhotoVisible = true, isPubPhotoTaked = false;
    private boolean isDownloadVisible = false;

    // download image and video ____________________________________________________________________
    private String user_id;
    private boolean isScreenEnabled = true;

    // widgets
    private ProgressBar progressbar;
    private MyEditText edit_caption;
    private PhotoView view_image;
    private ImageView view_video;

    // vars
    private File file;
    private boolean isCompressed = false;
    // _____________________________________________________________________________________________

    // firebase
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_pub_photos_chat);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            userid = getIntent().getStringExtra("userid");

        } catch (IllegalStateException | JsonSyntaxException exception) {
            Log.d(TAG, "onCreate: "+exception.getMessage());
        }

        // video ___________________________________________________________________________________
        initVideo();
        getAllVideos();
        setVideoList();

        // image ___________________________________________________________________________________
        initImage();
        init_download();
        getAllImages();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void initVideo() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout menu_item_pub_video = findViewById(R.id.menu_item_pub_video);
        TextView toolBar_textview_pub_video = findViewById(R.id.toolBar_textview_pub_video);
        relLayout_img_play = findViewById(R.id.relLayout_img_play);
        relLayout_select_video = findViewById(R.id.relLayout_select_video);
        linLayout_select_video = findViewById(R.id.linLayout_select_video);
        recyclerView_video = findViewById(R.id.recyclerView_video);
        RelativeLayout arrowBack_video = findViewById(R.id.arrowBack_video);
        view_video = findViewById(R.id.view_video);
        ImageView iv_video = findViewById(R.id.iv_video);

        videoList = new ArrayList<>();

        menu_item_pub_video.setOnClickListener(view -> {
            BottomSheetGalleryVideosChatFolders bottomSheet2 = new BottomSheetGalleryVideosChatFolders(context, videoAdapter,
                    videoList, toolBar_textview_pub_video);
            // prevent two clicks
            com.bekambimouen.miiokychallenge.Utils.Util.preventTwoClick(menu_item_pub_video);
            try {
                if (bottomSheet2.isAdded())
                    return;
                bottomSheet2.show(context.getSupportFragmentManager(), TAG);

            } catch (NullPointerException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        arrowBack_video.setOnClickListener(view -> {
            selectedVideo = "";
            linLayout_select_photo.setVisibility(View.VISIBLE);
            relLayout_select_video.setVisibility(View.GONE);

            isPubVideoVisible = false;
            isVideo = false;
            isPubPhotoVisible = true;
        });

        iv_video.setOnClickListener(view -> takeVideo());
    }

    private void initImage() {
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        linLayout_select_photo = findViewById(R.id.linLayout_select_photo);
        LinearLayout linLayout_icon_go_select_video = findViewById(R.id.linLayout_icon_go_select_video);
        relLayout_download_photo_and_video = findViewById(R.id.relLayout_download_photo_and_video);
        ImageView iv_photo = findViewById(R.id.iv_photo);
        RelativeLayout arrowBack_photo = findViewById(R.id.arrowBack_photo);
        main_progressBar = findViewById(R.id.main_progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView.setNestedScrollingEnabled(false);

        imageList = new ArrayList<>();

        linLayout_icon_go_select_video.setOnClickListener(view -> {
            linLayout_select_photo.setVisibility(View.GONE);
            relLayout_select_video.setVisibility(View.VISIBLE);

            isPubVideoVisible = true;
            isVideo = true;
            isPubPhotoVisible = false;
        });

        iv_photo.setOnClickListener(view -> takePicture());

        menu_item.setOnClickListener(view -> {
            BottomSheetGalleryResponseChallengePhotosFolders bottomSheet2 = new BottomSheetGalleryResponseChallengePhotosFolders(context, adapter, imageList, toolBar_textview);
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

        arrowBack_photo.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                selectedVideo = "";
                mSelectedImage = "";
                if (isPubVideoVisible) {
                    linLayout_select_photo.setVisibility(View.VISIBLE);
                    relLayout_select_video.setVisibility(View.GONE);

                    isPubVideoVisible = false;
                    isVideo = false;
                    isPubPhotoVisible = true;

                } else if (isDownloadVisible) {
                    isDownloadVisible = false;
                    if (isPubPhotoVisible || isPubPhotoTaked) {
                        linLayout_select_photo.setVisibility(View.VISIBLE);
                        isPubPhotoVisible = true;
                        isPubPhotoTaked = false;

                    } else {
                        linLayout_select_video.setVisibility(View.VISIBLE);
                        isPubVideoVisible = true;
                    }

                    relLayout_download_photo_and_video.setVisibility(View.GONE);

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
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
        } else
            Toast.makeText(context, context.getString(R.string.error_has_occured), Toast.LENGTH_SHORT).show();
    }

    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (mCurrentPhotoPath != null) {
                            addImage(mCurrentPhotoPath);

                            view_image.setVisibility(View.VISIBLE);
                            view_video.setVisibility(View.GONE);

                            // to go to download
                            isDownloadVisible = true;
                            isPubPhotoVisible = false;
                            isPubPhotoTaked = true;
                            Glide.with(context.getApplicationContext())
                                    .load(mSelectedImage)
                                    .into(view_image);
                            linLayout_select_photo.setVisibility(View.GONE);
                            relLayout_download_photo_and_video.setVisibility(View.VISIBLE);
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
            Log.d(TAG, "setupGridView: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mSelectedImage = imageList.get(position).getImage();

            view_image.setVisibility(View.VISIBLE);
            view_video.setVisibility(View.GONE);

            isDownloadVisible = true;
            isPubPhotoVisible = true;
            // to go to download
            Glide.with(context.getApplicationContext())
                    .load(mSelectedImage)
                    .into(view_image);
            linLayout_select_photo.setVisibility(View.GONE);
            relLayout_download_photo_and_video.setVisibility(View.VISIBLE);

            relLayout_img_play.setVisibility(View.GONE);
        });

    }

    // download image
    private void init_download() {
        RelativeLayout arrowBack_download = findViewById(R.id.arrowBack_download);
        ImageView btn_publish = findViewById(R.id.btn_publish);
        progressbar = findViewById(R.id.progressbar);
        edit_caption = findViewById(R.id.edit_caption);
        view_image = findViewById(R.id.view_image);

        arrowBack_download.setOnClickListener(view -> {
            if (isDownloadVisible) {
                isDownloadVisible = false;
                relLayout_download_photo_and_video.setVisibility(View.GONE);

                if (isPubPhotoVisible || isPubPhotoTaked) {
                    linLayout_select_photo.setVisibility(View.VISIBLE);
                    isPubPhotoVisible = true;
                    isPubPhotoTaked = false;

                } else {
                    linLayout_select_video.setVisibility(View.VISIBLE);
                    isPubVideoVisible = true;
                }

            } else {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        btn_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                progressbar.setVisibility(View.VISIBLE);
                isScreenEnabled = false;
                closeKeyboard();
                edit_caption.clearFocus();
                String caption = Objects.requireNonNull(edit_caption.getText()).toString();

                if (isVideo) {
                    try {
                        uploadNewVideo(selectedVideo, caption);
                    } catch (Throwable throwable) {
                        Log.d(TAG, "init_dowload: "+throwable.getMessage());
                    }
                } else {

                    if (originOfPhoto.equals("pickPhoto")) {
                        String size = getImageSize(Uri.parse(mSelectedImage));
                        long bitmap_size = Long.parseLong(size);

                        Uri uri = Uri.parse(mSelectedImage);
                        if (bitmap_size <= 1000000)
                            uploadOnePhotoWithoutCompress(caption, uri);
                        else
                            uploadOnePhotoWithCompres(caption, uri);

                    } else {
                        Uri uri = Uri.parse(mSelectedImage);
                        uploadOnePhotoWithCompres(caption, uri);
                    }
                }
            }
        });
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    // video publication ___________________________________________________________________________
    public void setVideoList(){
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView_video.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));
        recyclerView_video.setLayoutManager(layoutManager);
        recyclerView_video.setItemAnimator(null);
        recyclerView_video.setHasFixedSize(true);

        Collections.reverse(videoList);
        videoAdapter = new VideoChatListAdapter(context, videoList);
        recyclerView_video.setAdapter(videoAdapter);

        videoAdapter.setOnItemClickListener((position, v) -> {
            Log.d(TAG, "onItemClick: selected an video: " + videoList.get(position).getVideo());
            selectedVideo = videoList.get(position).getVideo();

            view_image.setVisibility(View.GONE);
            view_video.setVisibility(View.VISIBLE);

            // to go to download
            isDownloadVisible = true;
            Glide.with(context.getApplicationContext())
                    .load(selectedVideo)
                    .into(view_video);

            isPubVideoVisible = false;
            linLayout_select_video.setVisibility(View.GONE);
            relLayout_download_photo_and_video.setVisibility(View.VISIBLE);

            relLayout_img_play.setVisibility(View.VISIBLE);
            view_video.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewLocalVideo.class);
                intent.putExtra("video_url", selectedVideo);
                context.startActivity(intent);
            });
        });
    }

    // get all videos from external storage
    public void getAllVideos(){
        videoList.clear();
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection_video, null,null, null);

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
            Log.d(TAG, "getAllVideos: "+e.getMessage());
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
        } else
            Toast.makeText(context, context.getString(R.string.error_has_occured), Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "createVideoFile: "+e.getMessage());
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
        for (int pos = 0; pos < videoList.size(); pos++) {
            if (videoList.get(pos).getVideo() != null) {
                if (videoList.get(pos).getVideo().equalsIgnoreCase(filePath)) {
                    videoList.remove(pos);
                }
            }
        }
        addVideo(filePath);
    }

    // add image in selectedImageList and imageList
    @SuppressLint("NotifyDataSetChanged")
    public void addVideo(String filePath) {
        VideoItem videoModel = new VideoItem();
        videoModel.setVideo(filePath);
        videoList.add(0, videoModel);
        videoAdapter.notifyDataSetChanged();

        TrimVideo.activity(filePath)
                .setTrimType(TrimType.FIXED_DURATION)
                .setFixedDuration(15)
                .setLocal("en")
                .start(this, videoTrimResultLauncher);
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
                        selectedVideo = videoUri;

                        view_image.setVisibility(View.GONE);
                        view_video.setVisibility(View.VISIBLE);

                        // to go to download
                        isDownloadVisible = true;
                        Glide.with(context.getApplicationContext())
                                .load(selectedVideo)
                                .into(view_video);
                        isPubVideoVisible = false;
                        linLayout_select_video.setVisibility(View.GONE);
                        relLayout_download_photo_and_video.setVisibility(View.VISIBLE);
                    }

                    Log.d(TAG, "Video size:: " + (length / 1024));
                } else
                    LogMessage.v("videoTrimResultLauncher data is null");
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
                        selectedVideo = compressVideoPath;
                        isScreenEnabled = true;
                        videoAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        progressDialog = null;

                        view_image.setVisibility(View.GONE);
                        view_video.setVisibility(View.VISIBLE);

                        // to go to download
                        isDownloadVisible = true;
                        Glide.with(context.getApplicationContext())
                                .load(selectedVideo)
                                .into(view_video);
                        isPubVideoVisible = false;
                        linLayout_select_video.setVisibility(View.GONE);
                        relLayout_download_photo_and_video.setVisibility(View.VISIBLE);

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
    // upload video ________________________________________________________________________________
    private void uploadNewVideo(String videoUrl, String caption) {

        Calendar calendar=Calendar.getInstance();
        File vignetteFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!vignetteFolder.exists()) {
            vignetteFolder.mkdirs();
        }

        // get video name
        File videoFile = new File(videoUrl);
        String videoName = videoFile.getName();

        // get video thumbnail as bitmap
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoUrl);
        Bitmap bitmap = retriever.getFrameAtTime();

        // saved thumbnail as png file
        String thumbnailPath = vignetteFolder.getAbsolutePath()+"/"+videoName+".png";

        thumbnailFile = new File(thumbnailPath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(thumbnailFile);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "uploadNewVideo: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName+"_thumbnail.png");

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "uploadNewVideo: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();
        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_CHAT_IMAGE_STORAGE + "/" + user_id + "/video/" +context.getString(R.string.thumbnails_path) +
                        "/"+ calendar.getTimeInMillis());

        UploadTask uploadTask = thumbnailReference.putFile(thumbnailUri);
        uploadTask.continueWithTask(task1 -> {
            if (!task1.isSuccessful())
                throw Objects.requireNonNull(task1.getException());
            return thumbnailReference.getDownloadUrl();

        }).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String thumbnailUrl = task1.getResult().toString();

                Uri videoUri = Uri.fromFile(new File(videoUrl));
                StorageReference videoReference = mStorageReference
                        .child(filePaths.FIREBASE_CHAT_IMAGE_STORAGE + "/" + user_id + "/video/" +context.getString(R.string.video_path) +
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
                        addVideosToDatabase(caption, thumbnailUrl,url);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

            }
        }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));
    }

    public static void copy(File src, File dst) throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
    }

    private void addVideosToDatabase(String caption, String thumbnailUrl, String filePath) {
        // la clÃ© de la photo
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Date date=new Date();

        String photo = "";
        if (TextUtils.isEmpty(caption)) {
            photo = "photoSimple";
        }

        String chat_key = myRef.push().getKey();
        HashMap<String, Object> hashMap = Utils_Map_Chat.getChat(chat_key,"video", user_id, userid, "",
                caption, "", false, filePath, thumbnailUrl, photo, date.getTime(),
                "", "", "no");

        assert chat_key != null;
        reference1.child(getString(R.string.dbname_chat))
                .child(chat_key)
                .setValue(hashMap)
                .addOnCompleteListener(task -> {
                    // delete file
                    thumbnailFile.delete();
                    file.delete();

                    if (isCompressed)
                        compressedFile.delete();


                    if (task.isSuccessful()) {
                        finish();
                    }
                });
    }

    private void uploadOnePhotoWithoutCompress(final String caption, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_CHAT_IMAGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();

                addPhotosToDatabase(caption, firebaseUrl.toString());

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
                .child(filePaths.FIREBASE_CHAT_IMAGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());

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

                addPhotosToDatabase(caption, firebaseUrl.toString());

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void addPhotosToDatabase(String caption, String filePath) {
        // la clÃ© de la photo
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Date date=new Date();

        String photo = "";
        if (TextUtils.isEmpty(caption)) {
            photo = "photoSimple";
        }

        String chat_key = myRef.push().getKey();
        HashMap<String, Object> hashMap = Utils_Map_Chat.getChat(chat_key,"", user_id, userid, "",
                caption, "", false, filePath, "", photo, date.getTime(),
                "", "", "no");

        assert chat_key != null;
        reference1.child(getString(R.string.dbname_chat))
                .child(chat_key)
                .setValue(hashMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    }
                });
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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