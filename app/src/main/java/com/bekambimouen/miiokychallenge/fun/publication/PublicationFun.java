package com.bekambimouen.miiokychallenge.fun.publication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.PathUtil;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.challenge_publication.ViewLocalVideo;
import com.bekambimouen.miiokychallenge.fun.FunActivity;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.profile.ViewPostsFun;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class PublicationFun extends AppCompatActivity {
    private static final String TAG = "Publication";

    // widgets
    private ProgressBar progressBar;
    private MyEditText decris_ta_video_fun;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private PublicationFun context;
    private String path;
    private String from_fun_fragment, from_suggestion_fun, from_fun_profile, from_fun_view_post;
    private String video_compressed;
    private boolean isScreenEnabled = true;
    private File file, videoFile;
    private File thumbnailFile;

    // fro received video
    private String  received_vid_path;
    private boolean isFromReceivedVideo = false;

    // Firebase
    private StorageReference mStorageReference;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_publication_fun);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        try {
            // receive image or video from another app _________________________________________________
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                isFromReceivedVideo = true;
                if (type.startsWith("video/")) {
                    Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (videoUri != null) {
                        // Update UI to reflect image being shared
                        received_vid_path = videoUri.toString();
                    }

                }
            }
            // _____________________________________________________________________________________

            from_fun_fragment = getIntent().getStringExtra("from_fun_fragment");
            from_suggestion_fun = getIntent().getStringExtra("from_suggestion_fun");
            from_fun_profile = getIntent().getStringExtra("from_fun_profile");
            from_fun_view_post = getIntent().getStringExtra("from_fun_view_post");

            path = getIntent().getStringExtra("path");
            video_compressed = getIntent().getStringExtra("video_compressed");
            String string_path_compressed = getIntent().getStringExtra("string_path_compressed");
            if (string_path_compressed != null) {
                videoFile = new File(string_path_compressed);
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (isFromReceivedVideo) {
            try {
                path = PathUtil.getPath(context, Uri.parse(received_vid_path));
            } catch (URISyntaxException e) {
                Log.d(TAG, "onCreate: "+e.getMessage());
            }
        }

        init();
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        progressBar = findViewById(R.id.progressBar);
        decris_ta_video_fun = findViewById(R.id.decris_ta_video_fun);
        ImageView videoThumb_publier_fun = findViewById(R.id.videoThumb_publier_fun);
        RelativeLayout arrowBack_pub_fun = findViewById(R.id.arrowBack_pub_fun);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);

        Glide.with(context.getApplicationContext())
                .load(path)
                .into(videoThumb_publier_fun);

        videoThumb_publier_fun.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewLocalVideo.class);
            intent.putExtra("video_url", path);
            context.startActivity(intent);
        });

        relLayout_publish.setOnClickListener(v -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                closeKeyboard();
                decris_ta_video_fun.clearFocus();
                String caption = Objects.requireNonNull(decris_ta_video_fun.getText()).toString();
                startUploadVideo(caption, path);
            }
        });

        arrowBack_pub_fun.setOnClickListener(v -> {
            if (isFromReceivedVideo) {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("from_publish", "from_publish");
                startActivity(intent);

            } else if (video_compressed != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.video_treatement));
                builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    path = "";
                    finish();
                });
                builder.create().show();
            } else {
                path = "";
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFromReceivedVideo) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);

                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from_publish", "from_publish");
                    startActivity(intent);
                } else if (video_compressed != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(getString(R.string.video_treatement));
                    builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                        path = "";
                        finish();
                    });
                    builder.create().show();
                } else {
                    path = "";
                    finish();
                }
            }
        });
    }

    private void startUploadVideo(final String caption, String videoUrl){
        isScreenEnabled = false;
        progressBar.setVisibility(View.VISIBLE);

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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.d(TAG, "startUploadVideo: "+e.getMessage());
        }

        file = new File(vignetteFolder.getAbsolutePath()+"/"+videoName+"_thumbnail.png");

        try {
            copy(thumbnailFile, file);
        } catch (IOException e) {
            Log.d(TAG, "startUploadVideo: "+e.getMessage());
        }

        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String pathThumbnail = file.getPath();
        Uri thumbnailUri = Uri.fromFile(new File(pathThumbnail));
        StorageReference thumbnailReference = mStorageReference
                .child(filePaths.FIREBASE_FUN_VIDEO_STORAGE + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
                        "/" + calendar.getTimeInMillis());

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
                        .child(filePaths.FIREBASE_FUN_VIDEO_STORAGE + "/" + user_id + "/" +context.getString(R.string.video_path) +
                                "/"  + calendar.getTimeInMillis());

                UploadTask uploadTask1 = videoReference.putFile(videoUri);
                uploadTask1.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return videoReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String url = task.getResult().toString();

                        addVideoToDatabase(caption, thumbnailUrl, url);
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

    private void addVideoToDatabase(String caption, String thumbnailUrl, String url) {
        String tags = StringManipulation.getTags(caption);
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // la clé de la photo
        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        Date date=new Date();

        // juste l'apparition du recyclerview
        BattleModel_fun battleModel = new BattleModel_fun();

        battleModel.setUrl(url);
        battleModel.setThumbnail(thumbnailUrl);
        battleModel.setCaption(caption);
        battleModel.setDate_created(date.getTime());
        battleModel.setViews(0);
        battleModel.setPhoto_id(newPhotoKey);
        battleModel.setUser_id(user_id);
        battleModel.setTags(tags);

        // suppress the post
        battleModel.setSuppressed(false);


        assert newPhotoKey != null;
        myRef.child(getString(R.string.dbname_fun))
                .child(user_id)
                .child(newPhotoKey)
                .setValue(battleModel);

        // ensemble des photos
        myRef.child(getString(R.string.dbname_videos))
                .child(newPhotoKey)
                .setValue(battleModel).addOnCompleteListener(task -> {

                    // delete file
                    thumbnailFile.delete();
                    file.delete();;

                    if (video_compressed != null)
                        videoFile.delete();

                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent;
                    if (from_fun_fragment != null) {
                        intent = new Intent(context, Profile.class);
                        intent.putExtra("from_camera_video", "from_camera_video");
                        intent.putExtra("from_fun_publication", "from_fun_publication");

                    } else if (from_fun_profile != null) {
                        intent = new Intent(context, ViewPostsFun.class);
                        intent.putExtra("from_fun_publication", "from_fun_publication");

                    } else if (from_suggestion_fun != null || isFromReceivedVideo) {
                        intent = new Intent(context, FunActivity.class);
                        intent.putExtra("from_suggestion_fun", "from_suggestion_fun");

                    } else if (from_fun_view_post != null) {
                        intent = new Intent(context, ViewPostsFun.class);

                    } else {
                        intent = new Intent(context, FunActivity.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}