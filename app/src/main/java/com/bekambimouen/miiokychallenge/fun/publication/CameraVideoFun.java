package com.bekambimouen.miiokychallenge.fun.publication;


import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.VideoCompress;
import com.bekambimouen.miiokychallenge.Utils.videocompressor.utils.Util;
import com.bekambimouen.miiokychallenge.challenge.DownloadResponseChallenge;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.databinding.ActivityCameraVideoFunBinding;
import com.bekambimouen.miiokychallenge.fun.listener.CameraListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.annotations.Nullable;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraVideoFun extends AppCompatActivity implements CameraListener {
    private static final String TAG = "CameraVideoFun";
    private ActivityCameraVideoFunBinding cameraVideoFunBinding;

    // widgets
    private LinearLayout linLayout_countDown;
    private PreviewView previewViewFender;
    private ImageView captureVideo;
    private ImageView flashLight;
    private ImageView view_ok;
    private TextView mili_zero_rec;
    private TextView mili_second_rec;
    private VideoView videoView;

    // cameraclose
    private ExecutorService executor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    @SuppressLint("RestrictedApi")
    private VideoCapture videoCapture;
    private Camera camera;
    private int lenceFacing;
    private String cameraVideoPath = " ";
    private int milisec = 16;
    private boolean isFlashOn = false, isPlayOn = false;
    private boolean started = false, isViewOKVisible = false;
    private final Handler handler = new Handler();
    private boolean isPressBackWhenCapturing = false;
    private String from_fun_fragment, from_suggestion_fun, from_fun_profile, from_fun_view_post, from_challenge;

    // vars
    private CameraVideoFun context;
    private long startTime, endTime;
    private ProgressDialog progressDialog = null;
    private final String outputDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private boolean isPreviewVideoVisible = false;
    private ModelInvite model_invite;
    private String category_status, from_group_challenge, from_home_challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        cameraVideoFunBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_video_fun);
        context = this;

        try {
            from_fun_fragment = getIntent().getStringExtra("from_fun_fragment");
            from_fun_profile = getIntent().getStringExtra("from_fun_profile");
            from_suggestion_fun = getIntent().getStringExtra("from_suggestion_fun");
            from_fun_view_post = getIntent().getStringExtra("from_fun_view_post");

            // from challenge
            Gson gson = new Gson();
            model_invite = gson.fromJson(getIntent().getStringExtra("model_invite"), ModelInvite.class);
            from_challenge = getIntent().getStringExtra("from_challenge");
            category_status = getIntent().getStringExtra("category_status");
            from_group_challenge = getIntent().getStringExtra("from_group_challenge");
            from_home_challenge = getIntent().getStringExtra("from_home_challenge");

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
    }

    private void init() {
        previewViewFender = findViewById(R.id.previewViewFender);
        captureVideo = findViewById(R.id.captureVideo);
        linLayout_countDown = findViewById(R.id.linLayout_countDown);
        mili_zero_rec = findViewById(R.id.mili_zero_rec);
        mili_second_rec = findViewById(R.id.mili_second_rec);
        flashLight = findViewById(R.id.flashLight);
        view_ok = findViewById(R.id.view_ok);
        videoView = findViewById(R.id.videoView);

        cameraVideoFunBinding.setCameraListener(this);
        executor = Executors.newSingleThreadExecutor();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        flashLight.setEnabled(false);
        captureVideo.setEnabled(false);

        try {
            startCamera();
        } catch (Exception e) {
            Log.d(TAG, "init: "+e.getMessage());
        }

        cameraVideoFunBinding.ivGallery.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, Gallery.class);
            if (from_fun_fragment != null) {
                intent.putExtra("from_fun_fragment", "from_fun_fragment");
                startActivity(intent);

            } else if (from_fun_view_post != null) {
                intent.putExtra("from_fun_view_post", "from_fun_view_post");
                startActivity(intent);

            } else if (from_fun_profile != null) {
                intent.putExtra("from_fun_profile", "from_fun_profile");
                startActivity(intent);

            } else if (from_suggestion_fun != null) {
                intent.putExtra("from_suggestion_fun", "from_suggestion_fun");
                startActivity(intent);

            } else if (from_challenge != null) {
                Gson gson = new Gson();
                String myGson = gson.toJson(model_invite);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", category_status);
                intent.putExtra("from_challenge", "from_challenge");
                intent.putExtra("from_home_challenge", from_home_challenge);
                if (from_group_challenge != null)
                    intent.putExtra("from_group_challenge", "from_group_challenge");
                startActivity(intent);

            } else {
                startActivity(intent);
            }
        });
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

                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, PublicationFun.class);
                        if (from_fun_fragment != null) {
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_fun_fragment", "from_fun_fragment");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_fun_profile != null) {
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_fun_profile", "from_fun_profile");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_suggestion_fun != null) {
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_suggestion_fun", "from_suggestion_fun");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_fun_view_post != null) {
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("from_fun_view_post", "from_fun_view_post");
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);

                        } else if (from_challenge != null) {
                            Intent intent1 = new Intent(context, DownloadResponseChallenge.class);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(model_invite);
                            intent1.putExtra("model_invite", myGson);
                            intent1.putExtra("category_status", category_status);

                            intent1.putExtra("from_gallery_video", "from_gallery_video");
                            intent1.putExtra("from_compress", "from_compress");
                            intent1.putExtra("url", compressVideoPath);
                            intent1.putExtra("video_compressed", "data");
                            intent1.putExtra("string_path_compressed", destPath);
                            intent1.putExtra("from_home_challenge", from_home_challenge);
                            if (from_group_challenge != null)
                                intent1.putExtra("from_group_challenge", "from_group_challenge");
                            startActivity(intent1);

                        } else {
                            intent.putExtra("path", compressVideoPath);
                            intent.putExtra("video_compressed", "video_compressed");
                            intent.putExtra("string_path_compressed", destPath);
                            startActivity(intent);
                        }

                        endTime = System.currentTimeMillis();
                        Util.writeFile(context, "End at: " + new SimpleDateFormat("HH:mm:ss",
                                getLocale()).format(new Date()) + "\n");
                        Util.writeFile(context, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n");
                        Util.writeFile(context);

                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(context, "fail!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onProgress(float percent) {

                        progressDialog.setMessage(getString(R.string.compressing_video) + percent + "%");
                    }
                });

    }

    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale;
        sysLocale = getSystemLocale(config);

        return sysLocale;
    }

    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                lenceFacing = CameraSelector.LENS_FACING_FRONT;
                initPreview(cameraProvider, lenceFacing);
            } catch (ExecutionException | InterruptedException e) {
                Log.d(TAG, "startCamera: "+e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("RestrictedApi")
    private void initPreview(ProcessCameraProvider cameraProvider, int lenceFacing) {
        cameraProvider.unbindAll();
        try {
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(previewViewFender.getSurfaceProvider());

            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(lenceFacing).build();

            videoCapture = new VideoCapture.Builder()
                    .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                    .setCameraSelector(cameraSelector)
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build();

            preview.setSurfaceProvider(previewViewFender.getSurfaceProvider());
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture);


        } catch (Exception e) {
            Log.d(TAG, "initPreview: "+e.getMessage());
        }
    }

    public static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "Compress...");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        // adds timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");

        return mediaFile;
    }

    @SuppressLint("RestrictedApi")
    private void stopRecording() {
        videoCapture.stopRecording();
        isPlayOn = false;
        stopCount();
    }

    private final Runnable runnable = () -> {
        milisec--;
        mili_second_rec.setText(String.valueOf(milisec));

        if (milisec > 16)
            mili_zero_rec.setVisibility(View.GONE);
        else
            mili_zero_rec.setVisibility(View.VISIBLE);

        if (milisec == 0) {
            stopCount();
            milisec = 16;
            captureVideo.setEnabled(false);
            stopRecording();

            if (isFlashOn) {
                camera.getCameraControl().enableTorch(false);
                isFlashOn = false;
                flashLight.setEnabled(false);
                captureVideo.setEnabled(false);
            }
        }

        if (started) {
            startCount();
            cameraVideoFunBinding.relLayoutFlashLight.setVisibility(View.GONE);
            cameraVideoFunBinding.relLayoutTunrFront.setVisibility(View.GONE);
            cameraVideoFunBinding.ivGallery.setVisibility(View.GONE);
        }
    };

    private void stopCount() {
        started = false;
        milisec = 16;
        handler.removeCallbacks(runnable);
    }

    private void startCount() {
        linLayout_countDown.setVisibility(View.VISIBLE);
        started = true;
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void changeCamera(View view) {
        cameraProvider.unbindAll();
        if (lenceFacing == CameraSelector.LENS_FACING_FRONT) {
            initPreview(cameraProvider, CameraSelector.LENS_FACING_BACK);
            lenceFacing = CameraSelector.LENS_FACING_BACK;
        } else {
            initPreview(cameraProvider, CameraSelector.LENS_FACING_FRONT);
            lenceFacing = CameraSelector.LENS_FACING_FRONT;
        }
    }

    @Override
    public void retoucheBouton(View view) {

    }

    @Override
    public void closeBackToCamera(View view) {
        runOnUiThread(() -> {
            cameraVideoPath = "";
            videoView.stopPlayback();
            cameraVideoFunBinding.relLayoutPreviewLayout.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            cameraVideoFunBinding.relLayoutSectionCamera.setVisibility(View.VISIBLE);
            previewViewFender.setVisibility(View.VISIBLE);

            linLayout_countDown.setVisibility(View.GONE);

            cameraVideoFunBinding.relLayoutFlashLight.setVisibility(View.VISIBLE);
            cameraVideoFunBinding.relLayoutTunrFront.setVisibility(View.VISIBLE);
            cameraVideoFunBinding.ivGallery.setVisibility(View.VISIBLE);

            view_ok.setVisibility(View.GONE);
            isViewOKVisible = false;

            cameraProvider.unbindAll();
            initPreview(cameraProvider, CameraSelector.LENS_FACING_BACK);
            lenceFacing = CameraSelector.LENS_FACING_BACK;
        });
    }

    @Override
    public void submitClick(View view) {
        videoView.pause();
        compressVideo(cameraVideoPath);
    }

    @Override
    public void flashOff(View view, ImageView imageView) {
        if (isFlashOn) {
            camera.getCameraControl().enableTorch(false);
            isFlashOn = false;
            imageView.setEnabled(false);
        } else {
            camera.getCameraControl().enableTorch(true);
            isFlashOn = true;
            imageView.setEnabled(true);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void playPause(View view, ImageView imageView) {

        try {
            if (isPlayOn) {
                imageView.setEnabled(false);
                stopRecording();

                if (isFlashOn) {
                    camera.getCameraControl().enableTorch(false);
                    isFlashOn = false;
                    flashLight.setEnabled(false);
                }
                isPlayOn = false;
            } else {
                try {
                    isPlayOn = true;
                    imageView.setEnabled(true);
                    isViewOKVisible = false;

                    view_ok.setVisibility(View.GONE);
                    cameraVideoFunBinding.relLayoutFlashLight.setVisibility(View.GONE);
                    cameraVideoFunBinding.relLayoutTunrFront.setVisibility(View.GONE);
                    cameraVideoFunBinding.ivGallery.setVisibility(View.GONE);

                    File myPath = getOutputMediaFile();
                    VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions
                            .Builder(myPath).build();

                    startCount();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    videoCapture.startRecording(outputFileOptions, executor, new VideoCapture.OnVideoSavedCallback() {
                        @Override
                        public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                            runOnUiThread(() -> {
                                view_ok.setVisibility(View.VISIBLE);
                                isViewOKVisible = true;

                                view_ok.setOnClickListener(v -> {
                                    cameraVideoFunBinding.relLayoutSectionCamera.setVisibility(View.GONE);
                                    previewViewFender.setVisibility(View.GONE);

                                    cameraVideoFunBinding.relLayoutPreviewLayout.setVisibility(View.VISIBLE);
                                    videoView.setVisibility(View.VISIBLE);

                                    MediaController mediaController = new MediaController(context);
                                    mediaController.setAnchorView(videoView);

                                    //cameraVideoBinding.previewVideo.setMediaController(mediaController);
                                    videoView.setVideoURI(outputFileResults.getSavedUri());
                                    videoView.requestFocus();
                                    videoView.setZOrderOnTop(true);
                                    videoView.start();

                                    videoView.setOnPreparedListener(mp -> mp.setLooping(true));

                                    isPreviewVideoVisible = true;

                                    cameraVideoPath = Objects.requireNonNull(outputFileResults.getSavedUri()).getPath();

                                });
                            });
                        }

                        @Override
                        public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            runOnUiThread(() -> Toast.makeText(context, "" + cause,
                                    Toast.LENGTH_LONG).show());
                        }
                    });
                } catch (Exception e){
                    Log.d(TAG, "playPause: "+e.getMessage());
                }
            }
        } catch (Exception e){
            Log.d(TAG, "playPause: "+e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (isPreviewVideoVisible) {
            videoView.stopPlayback();
            cameraVideoFunBinding.relLayoutPreviewLayout.setVisibility(View.GONE);
            cameraVideoFunBinding.relLayoutSectionCamera.setVisibility(View.VISIBLE);
            previewViewFender.setVisibility(View.VISIBLE);

            isPreviewVideoVisible = false;
        } else if (isPlayOn) {
            try {
                stopRecording();
                cameraVideoPath = "";
                captureVideo.setEnabled(false);
                if (isFlashOn){
                    camera.getCameraControl().enableTorch(false);
                    isFlashOn = false;
                    flashLight.setEnabled(false);
                    //captureVideo.setEnabled(false);

                }
            } catch (Exception e){
                Log.d(TAG, "onBackPressed: "+e.getMessage());
            }
            isPlayOn = false;
        } else if (isPressBackWhenCapturing){
            try {
                runOnUiThread(() -> {
                    cameraVideoPath = "";
                    captureVideo.setEnabled(false);
                    cameraVideoFunBinding.relLayoutSectionCamera.setVisibility(View.VISIBLE);
                    previewViewFender.setVisibility(View.VISIBLE);
                    view_ok.setVisibility(View.VISIBLE);
                    isViewOKVisible = true;
                    isPressBackWhenCapturing = false;
                });
            } catch (Exception e){
                Log.d(TAG, "onBackPressed: "+e.getMessage());
            }
        } else if (isViewOKVisible){
            cameraVideoPath = "";
            view_ok.setVisibility(View.GONE);
            linLayout_countDown.setVisibility(View.GONE);
            cameraVideoFunBinding.relLayoutFlashLight.setVisibility(View.VISIBLE);
            cameraVideoFunBinding.relLayoutTunrFront.setVisibility(View.VISIBLE);
            cameraVideoFunBinding.ivGallery.setVisibility(View.VISIBLE);
            isViewOKVisible = false;
        } else {
            super.onBackPressed();
            cameraVideoPath = "";
        }
    }
}