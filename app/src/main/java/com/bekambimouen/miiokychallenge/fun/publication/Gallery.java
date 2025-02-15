package com.bekambimouen.miiokychallenge.fun.publication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.VideoCompress;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryFunFolders;
import com.bekambimouen.miiokychallenge.challenge.DownloadResponseChallenge;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.fun.adapter.VideoListFunAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class Gallery extends AppCompatActivity {
    private static final String TAG = "Gallery";
    // widgets
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private Gallery context;
    private final String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DURATION};
    public static ArrayList<VideoItem> videoArrayList;
    private final String outputDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private long startTime, endTime;
    private ProgressDialog progressDialog = null;
    private String from_fun_fragment, from_suggestion_fun, from_fun_profile, from_fun_view_post, from_challenge,
            from_home_challenge;
    private ModelInvite model_invite;
    private String category_status, from_group_challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_gallery);
        context = this;

        try {
            from_fun_fragment = getIntent().getStringExtra("from_fun_fragment");
            from_suggestion_fun = getIntent().getStringExtra("from_suggestion_fun");
            from_fun_profile = getIntent().getStringExtra("from_fun_profile");
            from_fun_view_post = getIntent().getStringExtra("from_fun_view_post");
            from_challenge = getIntent().getStringExtra("from_challenge");
            from_home_challenge = getIntent().getStringExtra("from_home_challenge");

            // from challenge
            Gson gson = new Gson();
            model_invite = gson.fromJson(getIntent().getStringExtra("model_invite"), ModelInvite.class);
            category_status = getIntent().getStringExtra("category_status");
            from_group_challenge = getIntent().getStringExtra("from_group_challenge");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        getVideos();
        init();
    }

    private void init() {
        parent = findViewById(R.id.parent);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        ProgressBar main_progressBar = findViewById(R.id.main_progressBar);
        ImageView toolbar_photo = findViewById(R.id.toolbar_photo);
        ImageView img_play_un = findViewById(R.id.img_play_un);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));

        if (from_challenge != null) {
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
        }

        Collections.reverse(videoArrayList);
        VideoListFunAdapter videoGalleryAdapter = new VideoListFunAdapter(this, videoArrayList, main_progressBar);
        recyclerView.setAdapter(videoGalleryAdapter);

        videoGalleryAdapter.setOnItemClickListener((position, v) -> {
            try {
                selectVideo(position);
            } catch (ArrayIndexOutOfBoundsException ed) {
                Log.d(TAG, "init: error: "+ed.getMessage());
            }
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGalleryFunFolders bottomSheet2 = new BottomSheetGalleryFunFolders(context, videoGalleryAdapter, videoArrayList, toolBar_textview);
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

        arrowBack.setOnClickListener(view -> finish());
    }

    //get video files from storage
    @SuppressLint({"NewApi", "NotifyDataSetChanged"})
    public void getVideos() {
        videoArrayList = new ArrayList<>();
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
                videoArrayList.add(VideoModel);
            }
            cursor.close();

        } catch (NumberFormatException e) {
            Log.d(TAG, "getVideos: "+e.getMessage());
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

    // Add image in SelectedArrayList
    @SuppressLint("NotifyDataSetChanged")
    public void selectVideo(int position) {
        String galleryVideoPath = videoArrayList.get(position).getVideo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(galleryVideoPath);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "selectVideo: "+e.getMessage());
        }

        long duration = 0;
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        try {
            assert time != null;
            duration = Long.parseLong(time);
        } catch (NumberFormatException e) {
            Log.d(TAG, "selectVideo: "+e.getMessage());
        }

        File file = new File(galleryVideoPath);
        long length = file.length();

        if (length >= 10000001) {
            compressVideo(galleryVideoPath);

        } else {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));

            if (from_fun_fragment != null) {
                Intent intent = new Intent(context, PublicationFun.class);
                intent.putExtra("path", galleryVideoPath);
                intent.putExtra("from_fun_fragment", "data");
                startActivity(intent);

            } else if (from_fun_profile != null) {
                Intent intent = new Intent(context, PublicationFun.class);
                intent.putExtra("path", galleryVideoPath);
                intent.putExtra("from_fun_profile", "data");
                startActivity(intent);

            } else if (from_suggestion_fun != null) {
                Intent intent = new Intent(context, PublicationFun.class);
                intent.putExtra("path", galleryVideoPath);
                intent.putExtra("from_suggestion_fun", "data");
                startActivity(intent);

            } else if (from_fun_view_post != null) {
                Intent intent = new Intent(context, PublicationFun.class);
                intent.putExtra("path", galleryVideoPath);
                intent.putExtra("from_fun_view_post", "data");
                startActivity(intent);

            } else if (from_challenge != null) {
                Intent intent = new Intent(context, DownloadResponseChallenge.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model_invite);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", category_status);
                intent.putExtra("from_home_challenge", from_home_challenge);

                intent.putExtra("from_gallery_video", "from_gallery_video");
                intent.putExtra("url", galleryVideoPath);
                if (from_group_challenge != null)
                    intent.putExtra("from_group_challenge", "from_group_challenge");
                startActivity(intent);

            } else {
                Intent intent = new Intent(context, PublicationFun.class);
                intent.putExtra("path", galleryVideoPath);
                startActivity(intent);
            }
        }
    }

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
                        progressDialog.dismiss();
                        progressDialog = null;

                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        if (from_fun_fragment != null) {
                            Intent intent = new Intent(context, PublicationFun.class);
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_fun_fragment", "from_fun_fragment");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_fun_profile != null) {
                            Intent intent = new Intent(context, PublicationFun.class);
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_fun_profile", "from_fun_profile");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_suggestion_fun != null) {
                            Intent intent = new Intent(context, PublicationFun.class);
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_suggestion_fun", "from_suggestion_fun");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_fun_view_post != null) {
                            Intent intent = new Intent(context, PublicationFun.class);
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_fun_view_post", "from_fun_view_post");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_challenge != null) {
                            Intent intent = new Intent(context, DownloadResponseChallenge.class);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(model_invite);
                            intent.putExtra("model_invite", myGson);
                            intent.putExtra("category_status", category_status);
                            intent.putExtra("from_home_challenge", from_home_challenge);

                            intent.putExtra("from_gallery_video", "from_gallery_video");
                            intent.putExtra("from_compress", "from_compress");
                            intent.putExtra("video_compressed", "data");
                            intent.putExtra("url", compressVideoPath);
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(context, PublicationFun.class);
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("video_compressed", "data");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);
                        }

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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}