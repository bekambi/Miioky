package com.bekambimouen.miiokychallenge.publication_insta;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.VideoCompress;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryVideosFolders;
import com.bekambimouen.miiokychallenge.challenge_publication.ViewLocalVideo;
import com.bekambimouen.miiokychallenge.challenge_publication.adapter.VideoListAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.LogMessage;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimType;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimVideo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.Locale;
import java.util.Objects;

@TargetApi(Build.VERSION_CODES.Q)
public class PubInstagVideo extends AppCompatActivity {
    private static final String TAG = "PubInstagVideo";

    // widgets
    private RelativeLayout parent, relLayout1, relLayout2, relLayout_view_overlay;
    private ImageView imageview;
    private MyEditText edit_caption;
    private ProgressBar progressBar;
    private ProgressBar main_progressBar;

    // vars
    private PubInstagVideo context;
    public static final int PICK_VIDEOS = 20;
    private RecyclerView recyclerView;
    private final String[] projection = {MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME, MediaStore.MediaColumns.DURATION};

    private ArrayList<VideoItem> videoList;
    private String selectedVideo;
    private String mCurrentVideoPath;
    private VideoListAdapter videoAdapter;
    private File video;
    private long startTime, endTime;
    private final String outputDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private ProgressDialog progressDialog;
    private boolean isDownloadVisible;
    private boolean isScreenEnabled = true, isPublisher = false;
    private String from_battle_fragment;
    private File file;
    private File thumbnailFile;
    private File compressedFile;
    public ArrayList<File> compressedFilesList;
    private boolean isCompressed = false;

    // Firebase
    private StorageReference mStorageReference;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_pub_instag_video);
        context = this;

        videoList = new ArrayList<>();
        compressedFilesList = new ArrayList<>();

        // get sharing image from others app
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        try {
            from_battle_fragment = getIntent().getStringExtra("from_battle_fragment");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init();
        getAllVideos();
        setVideoList();
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
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        RelativeLayout relLayout_video = findViewById(R.id.relLayout_video);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        progressBar = findViewById(R.id.progressbar_download_video);
        imageview = findViewById(R.id.imageview_download_video);
        RelativeLayout iv_arrowBack_principal = findViewById(R.id.iv_arrowBack_principal);
        RelativeLayout relLayout_arrowBack_download_video = findViewById(R.id.relLayout_arrowBack_download_video);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);
        edit_caption = findViewById(R.id.edit_caption_download_video);

        main_progressBar = findViewById(R.id.main_progressBar);
        recyclerView = findViewById(R.id.pub_RecyclerView_video);

        relLayout_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                closeKeyboard();
                isScreenEnabled = false;
                isPublisher = true;
                edit_caption.clearFocus();

                try {
                    uploadOneVideo(selectedVideo);
                } catch (Throwable throwable) {
                    Log.d(TAG, "init: "+throwable.getMessage());
                }
            }
        });

        relLayout_arrowBack_download_video.setOnClickListener(view -> {
            isDownloadVisible = false;
            isScreenEnabled = true;

            selectedVideo = "";
            relLayout2.setVisibility(View.GONE);
            relLayout1.setVisibility(View.VISIBLE);
        });

        iv_arrowBack_principal.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isPublisher) {
                    if (isDownloadVisible) {
                        isDownloadVisible = false;
                        isScreenEnabled = true;

                        selectedVideo = "";
                        relLayout2.setVisibility(View.GONE);
                        relLayout1.setVisibility(View.VISIBLE);
                    } else {
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    }
                }
            }
        });

        relLayout_video.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            takeVideo();
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGalleryVideosFolders bottomSheet = new BottomSheetGalleryVideosFolders(context, videoAdapter, videoList, toolBar_textview);
            // prevent two clicks
            com.bekambimouen.miiokychallenge.Utils.Util.preventTwoClick(menu_item);
            try {
                if (bottomSheet.isAdded())
                    return;
                bottomSheet.show(context.getSupportFragmentManager(), TAG);

            } catch (NullPointerException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVideoList(){
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));
        recyclerView.setLayoutManager(layoutManager);

        Collections.reverse(videoList);
        videoAdapter = new VideoListAdapter(context, videoList, main_progressBar);
        recyclerView.setAdapter(videoAdapter);

        videoAdapter.setOnItemClickListener((position, v) -> {
            selectedVideo = videoList.get(position).getVideo();

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(selectedVideo);
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "setVideoList: error: "+e.getMessage());
            }

            File file = new File(selectedVideo);
            long length = file.length();

            if (length >= 10000001) {
                // compression
                compressVideo(selectedVideo);

            } else {
                isScreenEnabled = true;

                Glide.with(context.getApplicationContext())
                        .load(selectedVideo)
                        .into(imageview);

                imageview.setOnClickListener(view -> {
                    if (!selectedVideo.isEmpty()) {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, ViewLocalVideo.class);
                        intent.putExtra("video_url", selectedVideo);
                        context.startActivity(intent);

                    }
                });

                isDownloadVisible = true;
                relLayout1.setVisibility(View.GONE);
                relLayout2.setVisibility(View.VISIBLE);
                videoAdapter.notifyDataSetChanged();
            }

        });
    }

    // get all videos from external storage
    public void getAllVideos(){
        videoList.clear();

        String absolutePathOfImage;

        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        assert cursor != null;
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

        try {

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
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
        if (!selectedVideo.equals(filePath)) {
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
        videoList.add(0, videoModel);

        TrimVideo.activity(filePath)
                .setTrimType(TrimType.FIXED_DURATION)
                .setFixedDuration(15)
                .setLocal("en")
                .start(this, videoTrimResultLauncher);

        videoAdapter.notifyDataSetChanged();
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

                        Glide.with(context.getApplicationContext())
                                .load(selectedVideo)
                                .into(imageview);

                        imageview.setOnClickListener(view -> {
                            if (!selectedVideo.isEmpty()) {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                getWindow().setExitTransition(new Slide(Gravity.END));
                                getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ViewLocalVideo.class);
                                intent.putExtra("video_url", selectedVideo);
                                context.startActivity(intent);

                            }
                        });

                        isDownloadVisible = true;
                        relLayout1.setVisibility(View.GONE);
                        relLayout2.setVisibility(View.VISIBLE);
                        videoAdapter.notifyDataSetChanged();
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
                        compressedFilesList.add(compressedFile);

                        selectedVideo = compressVideoPath;
                        isScreenEnabled = true;

                        Glide.with(context.getApplicationContext())
                                .load(selectedVideo)
                                .into(imageview);

                        imageview.setOnClickListener(view -> {
                            if (!selectedVideo.isEmpty()) {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                getWindow().setExitTransition(new Slide(Gravity.END));
                                getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ViewLocalVideo.class);
                                intent.putExtra("video_url", selectedVideo);
                                context.startActivity(intent);

                            }
                        });

                        isDownloadVisible = true;
                        relLayout1.setVisibility(View.GONE);
                        relLayout2.setVisibility(View.VISIBLE);

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

    private void uploadOneVideo(String imgUrl) throws IOException {
        isScreenEnabled = false;
        progressBar.setVisibility(View.VISIBLE);

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
        String thumbnailPath = vignetteFolder.getAbsolutePath()+"/"+videoName+".png";

        thumbnailFile = new File(thumbnailPath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(thumbnailFile);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "uploadOneVideo: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName+"_thumbnail.png");

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "uploadOneVideo: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();

        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_BATTLE_VIDEO_STORAGE + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
                        "/" +calendar.getTimeInMillis());

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
                        .child(filePaths.FIREBASE_BATTLE_VIDEO_STORAGE + "/" + user_id + "/" +context.getString(R.string.video_path) +
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

                        addOneVideoToDatabase(thumbnailUrl, url);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

            }
        }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));
        retriever.release();
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
            Log.d(TAG, "getVideoWidthOrHeight: "+e.getMessage());
        } finally{
            if (retriever != null){
                retriever.release();
            }if (inputStream != null){
                inputStream.close();
            }
        }
        return mWidthHeight;
    }

    private void addOneVideoToDatabase(String thumbnailUrl, String url) {
        // Get the video height
        int height = 0;
        try {
            height = getVideoWidthOrHeight(new File(selectedVideo), "height");
        } catch (IOException e) {
            Log.d(TAG, "addOneVideoToDatabase: "+e.getMessage());
        }

        String caption = Objects.requireNonNull(edit_caption.getText()).toString();
        String tags = StringManipulation.getTags(caption);
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String newVideooKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        Date date=new Date();

        // juste l'apparition du recyclerview
        BattleModel battleModel = new BattleModel();
        // to verify if image is big
        battleModel.setBig_image(height > 600);

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
        battleModel.setVideoUne(true);
        battleModel.setImageDouble(false);
        battleModel.setVideoDouble(false);
        battleModel.setReponseImageDouble(false);
        battleModel.setReponseVideoDouble(false);
        // pour le grid profile
        battleModel.setGridRecyclerImage(false);

        battleModel.setReponse_deja(false);
        battleModel.setDetails("");

        battleModel.setThumbnail(thumbnailUrl);
        battleModel.setThumbnail_invite("");
        battleModel.setThumbnail_response("");
        battleModel.setThumbnail_un("");
        battleModel.setThumbnail_deux("");

        battleModel.setCaption(caption);
        battleModel.setUrl(url);
        battleModel.setDate_created(date.getTime());
        battleModel.setPhoto_id(newVideooKey);
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

        // un autre adaptateur pour le recyclerview
        assert newVideooKey != null;
        myRef.child(getString(R.string.dbname_battle))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newVideooKey)
                .setValue(battleModel);

        myRef.child(getString(R.string.dbname_battle_media))
                .child(newVideooKey)
                .setValue(battleModel).addOnCompleteListener(task -> {

                    // delete file
                    thumbnailFile.delete();
                    file.delete();

                    if (isCompressed)
                        compressedFile.delete();

                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent;
                    if (from_battle_fragment != null) {
                        intent = new Intent(context, Profile.class);
                        intent.putExtra("from_publication", "from_publication");

                    } else {
                        intent = new Intent(context, MainActivity.class);
                        intent.putExtra("from_publish", "from_publish");
                    }
                    startActivity(intent);
                    finish();
                });
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