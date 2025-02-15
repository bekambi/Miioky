package com.bekambimouen.miiokychallenge.groups.publication;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaMetadataRetriever;
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
import android.widget.ImageView;
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
import com.bekambimouen.miiokychallenge.Utils.videocompressor.VideoCompress;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGroupGalleryVideosFolders;
import com.bekambimouen.miiokychallenge.challenge_publication.ViewLocalVideo;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.GroupVideoListAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimType;
import com.bekambimouen.miiokychallenge.video_trimmer.utils.TrimVideo;
import com.bumptech.glide.Glide;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GroupPublicationVideo extends AppCompatActivity {
    private static final String TAG = "GroupPublicationVideo";

    // widgets
    private RelativeLayout parent, relLayout1, relLayout2, relLayout_view_overlay;
    private ImageView imageview;
    private MyEditText edit_caption;
    private ProgressBar progressBar, main_progressBar;

    // vars
    private GroupPublicationVideo context;
    public static final int PICK_VIDEOS = 20;
    private RecyclerView recyclerView;
    private final String[] projection = {MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME, MediaStore.MediaColumns.DURATION};

    private ArrayList<VideoItem> videoList;
    private String selectedVideo;
    private String mCurrentVideoPath;
    private GroupVideoListAdapter videoAdapter;
    private File video;
    private long startTime, endTime;
    private final String outputDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private ProgressDialog progressDialog;
    private boolean isDownloadVisible;
    private boolean isScreenEnabled = true;
    private File file;
    private File thumbnailFile;
    private File compressedFile;
    public ArrayList<File> compressedFilesList;
    private boolean isCompressed = false;

    private UserGroups user_groups;

    private ArrayList<String> members_id_list;

    // Firebase
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_group_publication_video);
        context = this;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        members_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            user_groups = gson.fromJson(getIntent().getStringExtra("user_groups"), UserGroups.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        videoList = new ArrayList<>();
        compressedFilesList = new ArrayList<>();

        // get sharing image from others app
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        relLayout1 = findViewById(R.id.relLayout1);
        relLayout2 = findViewById(R.id.relLayout2);

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

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
        main_progressBar = findViewById(R.id.main_progressBar);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        RelativeLayout relLayout_video = findViewById(R.id.relLayout_video);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview_choose_video);
        progressBar = findViewById(R.id.progressbar_download_video);
        RelativeLayout relLayout_arrowBack_choose_video = findViewById(R.id.relLayout_arrowBack_choose_video);
        RelativeLayout relLayout_arrowBack_download_video = findViewById(R.id.relLayout_arrowBack_download_video);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);

        imageview = findViewById(R.id.imageview_download_video);
        edit_caption = findViewById(R.id.edit_caption_download_video);

        recyclerView = findViewById(R.id.pub_RecyclerView_video);

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
                                ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                                InsetDrawable inset = new InsetDrawable(back, 50);
                                Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(inset);
                                d.show();

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.ok));

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.admin_note_title)));
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.posts_will_be_submitted_to_admin_before_display)));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view1 -> {
                                    // internet control
                                    boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                                    if (!isConnected) {
                                        CheckInternetStatus.internetIsConnected(context, parent);

                                    } else {
                                        closeKeyboard();
                                        isScreenEnabled = false;
                                        progressBar.setVisibility(View.VISIBLE);

                                        try {
                                            uploadOneVideo(selectedVideo);
                                        } catch (Throwable e) {
                                            Log.d(TAG, "onDataChange: error: "+e.getMessage());
                                        }
                                    }
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
                                closeKeyboard();
                                isScreenEnabled = false;
                                progressBar.setVisibility(View.VISIBLE);

                                try {
                                    uploadOneVideo(selectedVideo);
                                } catch (Throwable e) {
                                    Log.d(TAG, "onDataChange: error: "+e.getMessage());
                                }
                            }
                        }

                        if (!snapshot.exists()) {
                            closeKeyboard();
                            isScreenEnabled = false;
                            progressBar.setVisibility(View.VISIBLE);

                            try {
                                uploadOneVideo(selectedVideo);
                            } catch (Throwable e) {
                                Log.d(TAG, "onDataChange: error: "+e.getMessage());
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
            relLayout_publish.setOnClickListener(view -> {
                // internet control
                boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

                if (!isConnected) {
                    CheckInternetStatus.internetIsConnected(context, parent);

                } else {
                    closeKeyboard();
                    isScreenEnabled = false;
                    progressBar.setVisibility(View.VISIBLE);

                    try {
                        uploadOneVideo(selectedVideo);
                    } catch (Throwable e) {
                        Log.d(TAG, "init: error: "+e.getMessage());
                    }
                }
            });
        }

        relLayout_video.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            takeVideo();
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGroupGalleryVideosFolders bottomSheet = new BottomSheetGroupGalleryVideosFolders(context, videoAdapter, videoList, toolBar_textview);
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

        relLayout_arrowBack_download_video.setOnClickListener(view -> {
            isDownloadVisible = false;
            isScreenEnabled = true;

            selectedVideo = "";
            relLayout2.setVisibility(View.GONE);
            relLayout1.setVisibility(View.VISIBLE);
        });

        relLayout_arrowBack_choose_video.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDownloadVisible) {
                    isDownloadVisible = false;
                    isScreenEnabled = true;

                    selectedVideo = "";
                    relLayout2.setVisibility(View.GONE);
                    relLayout1.setVisibility(View.VISIBLE);
                } else {
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
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
                closeKeyboard();
                isScreenEnabled = false;
                progressBar.setVisibility(View.VISIBLE);

                try {
                    uploadOneVideo(selectedVideo);
                } catch (Throwable e) {
                    Log.d(TAG, "getLimitedPosts: error: "+e.getMessage());
                }

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

    @SuppressLint("NotifyDataSetChanged")
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
        videoList.add(1, videoModel);

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

                    Log.d(TAG, "Video size:: " + (length / 1024));
                } else
                    Log.d(TAG, "videoTrimResultLauncher data is null");
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
                        Toast.makeText(context, "failed!", Toast.LENGTH_LONG).show();
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
            Log.d(TAG, "uploadOneVideo: error: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName+"_thumbnail.png");

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "uploadOneVideo: error: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();

        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_VIDEO_STORAGE_GROUP_BATTLE + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
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
                        .child(filePaths.FIREBASE_VIDEO_STORAGE_GROUP_BATTLE + "/" + user_id + "/" +context.getString(R.string.video_path) +
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

    private void addOneVideoToDatabase(String thumbnailUrl, String url) {
        // Get the video height
        int height = 0;
        try {
            height = getVideoWidthOrHeight(new File(selectedVideo), "height");
        } catch (IOException e) {
            Log.d(TAG, "addOneVideoToDatabase: error: "+e.getMessage());
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
        battleModel.setVideoUne(false);
        battleModel.setImageDouble(false);
        battleModel.setVideoDouble(false);
        battleModel.setReponseImageDouble(false);
        battleModel.setReponseVideoDouble(false);
        // pour le grid profile
        battleModel.setGridRecyclerImage(false);

        battleModel.setReponse_deja(false);

        battleModel.setThumbnail(thumbnailUrl);
        battleModel.setThumbnail_invite("");
        battleModel.setThumbnail_response("");
        battleModel.setThumbnail_un("");
        battleModel.setThumbnail_deux("");

        // group
        battleModel.setGroup(true);
        battleModel.setGroup_private(user_groups.isGroup_private());
        battleModel.setGroup_public(user_groups.isGroup_public());
        battleModel.setGroup_cover_profile_photo(false);
        battleModel.setGroup_recyclerItem(false);
        battleModel.setGroup_imageUne(false);
        battleModel.setGroup_videoUne(true);
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
        battleModel.setDetails("");

        battleModel.setSharing_admin_master("");
        battleModel.setShared_group_id("");
        battleModel.setAdmin_master(user_groups.getAdmin_master());
        battleModel.setGroup_id(user_groups.getGroup_id());
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
                    GroupFollowersFollowing follower = new GroupFollowersFollowing();

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    follower.setPublicationApprobation(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_publicationApprobation))).toString())));

                    assert newVideooKey != null;
                    if (follower.isPublicationApprobation()) {
                        myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                .child(user_groups.getGroup_id())
                                .child(newVideooKey)
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_Media_waiting_for_approval))
                                .child(newVideooKey)
                                .setValue(battleModel).addOnCompleteListener(task -> {

                                    // delete file
                                    thumbnailFile.delete();
                                    file.delete();

                                    if (isCompressed)
                                        compressedFile.delete();
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                });

                    } else {
                        myRef.child(getString(R.string.dbname_group))
                                .child(user_groups.getGroup_id())
                                .child(newVideooKey)
                                .setValue(battleModel);

                        myRef.child(getString(R.string.dbname_group_media))
                                .child(newVideooKey)
                                .setValue(battleModel).addOnCompleteListener(task -> {

                                    // delete file
                                    thumbnailFile.delete();
                                    file.delete();

                                    if (isCompressed)
                                        compressedFile.delete();

                                    //add points and send notification
                                    addPostPointsAndSendNotification();
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
                                    finish();
                                });
                    }
                }

                if (!snapshot.exists() && !user_groups.getAdmin_master().equals(user_id)) {
                    assert newVideooKey != null;
                    myRef.child(getString(R.string.dbname_group))
                            .child(user_groups.getGroup_id())
                            .child(newVideooKey)
                            .setValue(battleModel);

                    myRef.child(getString(R.string.dbname_group_media))
                            .child(newVideooKey)
                            .setValue(battleModel).addOnCompleteListener(task -> {

                                // delete file
                                thumbnailFile.delete();
                                file.delete();

                                if (isCompressed)
                                    compressedFile.delete();

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
            assert newVideooKey != null;
            myRef.child(getString(R.string.dbname_group))
                    .child(user_groups.getGroup_id())
                    .child(newVideooKey)
                    .setValue(battleModel);

            myRef.child(getString(R.string.dbname_group_media))
                    .child(newVideooKey)
                    .setValue(battleModel).addOnCompleteListener(task -> {

                        // delete file
                        thumbnailFile.delete();
                        file.delete();

                        if (isCompressed)
                            compressedFile.delete();

                        //add points and send notification
                        addPostPointsAndSendNotification();
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        finish();
                    });
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