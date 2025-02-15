package com.bekambimouen.miiokychallenge.challenge;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.StringManipulation;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullChallengeImage;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullChallengeVideo;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DownloadResponseChallenge extends AppCompatActivity {
    private static final String TAG = "DownloadReponseChallenge";

    // widgets
    private MyEditText response_description;
    private ProgressBar progressBar;
    private ImageView thumbnail_un, thumbnail_deux;
    private RelativeLayout parent, relLayout_img_play_un, relLayout_img_play_deux, relLayout_publish, relLayout_view_overlay;

    // vars
    private DownloadResponseChallenge context;
    private ModelInvite model_invite;
    private String from_gallery_video;
    private String url;
    private String originOfPhoto;
    private String category_status;
    private String reponse_profile_photo, repnse_usename;
    private String video_compressed;
    private String from_group_challenge;
    private String from_home_challenge;
    private boolean isScreenEnabled = true;
    private File file, videoFile;
    private File thumbnailFile;
    private HashMap<String, Object> map;
    private ArrayList<String> members_id_list;
    private ArrayList<String> url_list;

    // firebase
    private FirebaseMethods mFirebaseMethods;
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_download_response_challenge);
        context = this;

        // firebase
        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        members_id_list = new ArrayList<>();
        url_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            model_invite = gson.fromJson(getIntent().getStringExtra("model_invite"), ModelInvite.class);
            // photo
            originOfPhoto = getIntent().getStringExtra("originOfPhoto");
            // video
            video_compressed = getIntent().getStringExtra("video_compressed");
            String string_path_compressed = getIntent().getStringExtra("string_path_compressed");
            if (string_path_compressed != null) {
                videoFile = new File(string_path_compressed);
            }

            from_gallery_video = getIntent().getStringExtra("from_gallery_video");
            // common
            url = getIntent().getStringExtra("url");
            category_status = getIntent().getStringExtra("category_status");
            from_group_challenge = getIntent().getStringExtra("from_group_challenge");
            from_home_challenge = getIntent().getStringExtra("from_home_challenge");

        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        setupFirebaseAuth();
        setSelectedImageList();
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
        TextView tv_msg_vote = findViewById(R.id.tv_msg_vote);
        if (from_group_challenge != null)
            if (from_gallery_video != null)
                tv_msg_vote.setText(getText(R.string.members_of_group_video_vote));
            else
                tv_msg_vote.setText(getText(R.string.members_of_group_photo_vote));
        else {
            if (from_gallery_video != null)
                tv_msg_vote.setText(getText(R.string.your_subscribers_video_vote));
            else
                tv_msg_vote.setText(getText(R.string.your_subscribers_photo_vote));
        }

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout relLayout_arrowBack = findViewById(R.id.relLayout_arrowBack);
        relLayout_publish = findViewById(R.id.relLayout_publish);
        progressBar = findViewById(R.id.main_progressBar);
        response_description = findViewById(R.id.response_description);
        TextView category_un = findViewById(R.id.category_un);
        TextView category_deux = findViewById(R.id.category_deux);
        RelativeLayout relLayout_category_un = findViewById(R.id.relLayout_category_un);
        RelativeLayout relLayout_category_deux = findViewById(R.id.relLayout_category_deux);
        // widgets
        TextView invite_comment = findViewById(R.id.invite_comment);

        thumbnail_un = findViewById(R.id.thumbnail_un);
        thumbnail_deux = findViewById(R.id.thumbnail_deux);
        relLayout_img_play_un = findViewById(R.id.relLayout_img_play_un);
        relLayout_img_play_deux = findViewById(R.id.relLayout_img_play_deux);

        if (model_invite.getInvite_caption().isEmpty())
            invite_comment.setVisibility(View.GONE);
        invite_comment.setText(Html.fromHtml("<b>"+model_invite.getInvite_username()+"</b>"+"_ "+model_invite.getInvite_caption()));

        // to know if the member admin post approval
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(model_invite.getGroup_id())
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
                        relLayout_publish.setOnClickListener(view -> {
                            // show dialog box
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
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
                                // download photos to firebase
                                getDownload();
                                d.dismiss();
                            });
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
                        relLayout_publish.setOnClickListener(view -> {
                            // download photos to firebase
                            getDownload();
                        });
                    }
                }

                if (!snapshot.exists()) {
                    relLayout_publish.setOnClickListener(view -> {
                        // download photos to firebase
                        getDownload();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // detect if user is admin master
        if (model_invite.getAdmin_master().equals(user_id)) {
            relLayout_publish.setOnClickListener(view -> {
                // download photos to firebase
                getDownload();
            });
        }

        String set_category = Util_InviteChallengeCategory.getInviteChallengeCategory(context, model_invite);
        category_un.setText(Html.fromHtml("#"+set_category));
        category_deux.setText(Html.fromHtml("#"+set_category));

        relLayout_category_un.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            intent.putExtra("category_status", category_status);
            context.startActivity(intent);
        });

        relLayout_category_deux.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            intent.putExtra("category_status", category_status);
            context.startActivity(intent);
        });

        relLayout_arrowBack.setOnClickListener(view -> {
            if (video_compressed != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.video_treatement));
                builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    url = "";
                    finish();
                });
                builder.create().show();
            } else {
                url = "";
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (video_compressed != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(getString(R.string.video_treatement));
                    builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                        url = "";
                        finish();
                    });
                    builder.create().show();
                } else {
                    url = "";
                    getWindow().setExitTransition(new Slide(Gravity.END));
                    getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });

    }

    // download photos to firebase
    private void getDownload() {
        // internet control
        boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

        if (!isConnected) {
            CheckInternetStatus.internetIsConnected(context, parent);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            isScreenEnabled = false;
            closeKeyboard();
            response_description.clearFocus();
            String caption = Objects.requireNonNull(response_description.getText()).toString();

            if (from_gallery_video != null) {
                try {
                    startUploadVideo(url, caption);
                } catch (IOException e) {
                    Log.d(TAG, "getDownload: "+e.getMessage());
                }

            } else {
                if (originOfPhoto.equals("pickPhoto")) {
                    String size = getImageSize(Uri.parse(url));
                    long bitmap_size = Long.parseLong(size);

                    Uri uri = Uri.parse(url);
                    if (bitmap_size <= 1000000)
                        uploadOnePhotoWithoutCompress(caption, uri);
                    else
                        uploadOnePhotoWithCompress(caption, uri);

                } else {
                    Uri uri = Uri.parse(url);
                    uploadOnePhotoWithCompress(caption, uri);
                }
            }
        }
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

                relLayout_publish.setOnClickListener(view -> {
                    // increment number of posts today
                    HashMap<String, Object> map = new HashMap<>();
                    Date date1 = new Date();
                    int number_of_posts = follower.getNumber_of_posts_today();
                    map.put("number_of_posts_today", number_of_posts+1);
                    map.put("date_last_post_or_last_comment", date1.getTime());

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id)
                            .child(model_invite.getGroup_id())
                            .updateChildren(map);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(model_invite.getGroup_id())
                            .child(user_id)
                            .updateChildren(map);

                    // add posts to firebase
                    getDownload();
                });

            } else {
                relLayout_publish.setOnClickListener(view -> {
                    // show dialog box
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
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
                });
            }
        }
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    public void setSelectedImageList(){
        String invite_url;
        if (!model_invite.getThumbnail_invite().isEmpty()) {
            invite_url = model_invite.getThumbnail_invite();
            relLayout_img_play_un.setVisibility(View.VISIBLE);
            relLayout_img_play_deux.setVisibility(View.VISIBLE);
        } else {
            invite_url = model_invite.getInvite_url();
        }

        Glide.with(context.getApplicationContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(thumbnail_un);

        Glide.with(context.getApplicationContext())
                .load(invite_url)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(thumbnail_deux);

        // full image
        thumbnail_un.setOnClickListener(view -> {
            url_list.clear();
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent;
            if (!model_invite.getThumbnail_invite().isEmpty()) {
                url_list.add(url);
                url_list.add(model_invite.getInvite_url());

                intent = new Intent(context, SimpleFullChallengeVideo.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 0);

            } else {
                url_list.add(url);
                url_list.add(invite_url);

                intent = new Intent(context, SimpleFullChallengeImage.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 0);
            }
            context.startActivity(intent);
        });

        thumbnail_deux.setOnClickListener(view -> {
            url_list.clear();
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent;
            if (!model_invite.getThumbnail_invite().isEmpty()) {
                url_list.add(url);
                url_list.add(model_invite.getInvite_url());

                intent = new Intent(context, SimpleFullChallengeVideo.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 1);
            } else {

                url_list.add(url);
                url_list.add(invite_url);

                intent = new Intent(context, SimpleFullChallengeImage.class);
                intent.putExtra("url_list", url_list);
                intent.putExtra("position", 1);
            }
            context.startActivity(intent);
        });
    }

    private void startUploadVideo(final String videoUrl, String caption) throws IOException {
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
            assert bitmap != null;
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
                .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" +context.getString(R.string.thumbnails_path) +
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
                        .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" +context.getString(R.string.video_path) +
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

                        if (model_invite.isGroup())
                            addGroupMediaToDatabase(url, caption, thumbnailUrl);
                        else
                            addMediaToDatabase(url, caption, thumbnailUrl);
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

            }
        }).addOnFailureListener(e -> Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));

        retriever.release();
    }

    private void uploadOnePhotoWithoutCompress(final String caption, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference;
        if (model_invite.isGroup()) {
            storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP_BATTLE + "/" + user_id + "/" + calendar.getTimeInMillis());

        } else {
            storageReference = mStorageReference
                    .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());
        }

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+imageUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String url1 = String.valueOf(firebaseUrl);

                if (model_invite.isGroup())
                    addGroupMediaToDatabase(url1, caption, "");
                else
                    addMediaToDatabase(url1, caption, "");

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadOnePhotoWithCompress(final String caption, Uri imageUri) {
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference;
        if (model_invite.isGroup()) {
            storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE_GROUP_BATTLE + "/" + user_id + "/" + calendar.getTimeInMillis());

        } else {
            storageReference = mStorageReference
                    .child(filePaths.FIREBASE_CHALLENGE_STORAGE + "/" + user_id + "/" + calendar.getTimeInMillis());
        }

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+imageUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadOnePhotoWithCompress: "+e.getMessage());
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
                if (model_invite.isGroup())
                    addGroupMediaToDatabase(url1, caption, "");
                else
                    addMediaToDatabase(url1, caption, "");

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    // publication dans le main battle (miioky)
    private void addMediaToDatabase(String url, String caption, String thumbnail_response) {
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        Date date=new Date();

        String tags = StringManipulation.getTags(caption);
        BattleModel model = new BattleModel();

        if (from_gallery_video != null) {
            model.setReponseImageDouble(false);
            model.setReponseVideoDouble(true);

        } else {
            model.setReponseImageDouble(true);
            model.setReponseVideoDouble(false);
        }
        model.setReponse_deja(false);

        model.setDate_created(date.getTime());

        if (model_invite.getThumbnail_invite() != null && !TextUtils.isEmpty(model_invite.getThumbnail_invite())) {
            model.setThumbnail_invite(model_invite.getThumbnail_invite());
            model.setThumbnail_response(thumbnail_response);

        } else {
            model.setThumbnail_invite("");
            model.setThumbnail_response("");
        }

        model.setSuppressed(false);
        model.setRecycler_story(false);
        model.setFriends_suggestion_one(false);
        model.setFriends_suggestion_two(false);
        model.setFriends_suggestion_three(false);
        model.setFriends_suggestion_four(false);
        model.setFriends_suggestion_five(false);
        model.setGroups_suggestion_one(false);
        model.setGroups_suggestion_two(false);
        model.setGroups_suggestion_three(false);
        model.setGroups_suggestion_four(false);
        model.setGroups_suggestion_five(false);
        model.setVideos_suggestion_one(false);
        model.setVideos_suggestion_two(false);
        model.setVideos_suggestion_three(false);
        model.setVideos_suggestion_four(false);
        model.setVideos_suggestion_five(false);
        model.setEveryone_can_answer_this_challenge(model_invite.isEveryone_can_answer_this_challenge());
        model.setDetails("");

        model.setRecyclerItem(false);
        model.setImageUne(false);
        model.setVideoUne(false);
        model.setImageDouble(false);
        model.setVideoDouble(false);
        // pour le grid profile
        model.setGridRecyclerImage(false);

        model.setInvite_url(model_invite.getInvite_url());
        model.setInvite_photoID(model_invite.getInvite_photoID());
        model.setInvite_username(model_invite.getInvite_username());
        model.setInvite_userID(model_invite.getInvite_userID());
        model.setInvite_caption(model_invite.getInvite_caption());
        model.setInvite_tag(model_invite.getInvite_tag());
        model.setInvite_category(model_invite.getInvite_category());
        model.setInvite_category_status(category_status);
        model.setInvite_profile_photo(model_invite.getInvite_profile_photo());
        model.setInvite_country_name(model_invite.getInvite_country_name());
        model.setInvite_countryID(model_invite.getInvite_countryID());

        model.setReponse_profile_photo(reponse_profile_photo);
        model.setReponse_username(repnse_usename);
        model.setReponse_user_id(user_id);
        model.setReponse_url(url);
        model.setReponse_photoID(newPhotoKey);
        model.setReponse_caption(caption);
        model.setReponse_tag(tags);
        model.setReponse_country_name(GetCountryName());
        model.setReponse_countryID(GetCountryZipCode());

        model.setCaptionUn("");
        model.setUrlUn("");
        model.setUrlDeux("");
        model.setTagsUn("");
        model.setTagsDeux("");
        model.setPhoto_id_un("");
        model.setPhoto_id_deux("");

        model.setCaption("");
        model.setSharing_caption("");
        model.setTags("");
        model.setUrl("");
        model.setPhoto_id("");
        // group
        model.setGroup(false);
        model.setGroup_private(false);
        model.setGroup_public(false);
        model.setGroup_cover_profile_photo(false);
        model.setGroup_recyclerItem(false);
        model.setGroup_imageUne(false);
        model.setGroup_videoUne(false);
        model.setGroup_cover_background_profile_photo(false);
        model.setGroup_imageDouble(false);
        model.setGroup_videoDouble(false);
        model.setGroup_response_imageDouble(false);
        model.setGroup_response_videoDouble(false);

        model.setGroup_share_single_bottom_image_double(false);
        model.setGroup_share_single_bottom_image_une(false);
        model.setGroup_share_single_bottom_recycler(false);
        model.setGroup_share_single_bottom_response_image_double(false);
        model.setGroup_share_single_bottom_response_video_double(false);
        model.setGroup_share_single_bottom_video_double(false);
        model.setGroup_share_single_bottom_video_une(false);

        model.setGroup_share_single_top_image_double(false);
        model.setGroup_share_single_top_image_une(false);
        model.setGroup_share_single_top_recycler(false);
        model.setGroup_share_single_top_response_image_double(false);
        model.setGroup_share_single_top_response_video_double(false);
        model.setGroup_share_single_top_video_double(false);
        model.setGroup_share_single_top_video_une(false);

        model.setGroup_share_top_bottom_image_double(false);
        model.setGroup_share_top_bottom_image_une(false);
        model.setGroup_share_top_bottom_recycler(false);
        model.setGroup_share_top_bottom_response_image_double(false);
        model.setGroup_share_top_bottom_response_video_double(false);
        model.setGroup_share_top_bottom_video_double(false);
        model.setGroup_share_top_bottom_video_une(false);

        model.setUser_profile_shared(false);
        model.setGroup_share_single_bottom_circle_image(false);
        model.setGroup_share_single_top_circle_image(false);
        model.setGroup_share_top_bottom_circle_image(false);

        model.setUser_profile_photo("");
        model.setUser_full_profile_photo("");
        model.setDate_shared(0);
        model.setViews(0);

        model.setSharing_admin_master("");
        model.setShared_group_id("");
        model.setAdmin_master("");
        model.setGroup_id("");
        model.setPublisher("");
        model.setGroup_profile_photo("");
        model.setGroup_full_profile_photo("");
        model.setGroup_user_background_profile_url("");
        model.setGroup_user_background_full_profile_url("");

        model.setComment_text(false);
        model.setComment_subject("");
        model.setComment_theme("");
        model.setBig_image(false);

        model.setUrli("");
        model.setUrlii("");
        model.setUrliii("");
        model.setUrliv("");
        model.setUrlv("");
        model.setUrlvi("");
        model.setUrlvii("");
        model.setUrlviii("");
        model.setUrlix("");
        model.setUrlx("");
        model.setUrlxi("");
        model.setUrlxii("");
        model.setUrlxiii("");
        model.setUrlxiv("");
        model.setUrlxv("");
        model.setUrlxvi("");
        model.setUrlxvii("");

        model.setPhotoi_id("");
        model.setPhotoii_id("");
        model.setPhotoiii_id("");
        model.setPhotoiv_id("");
        model.setPhotov_id("");
        model.setPhotovi_id("");
        model.setPhotovii_id("");
        model.setPhotoviii_id("");
        model.setPhotoix_id("");
        model.setPhotox_id("");
        model.setPhotoxi_id("");
        model.setPhotoxii_id("");
        model.setPhotoxiii_id("");
        model.setPhotoxiv_id("");
        model.setPhotoxv_id("");
        model.setPhotoxvi_id("");
        model.setPhotoxvii_id("");

        model.setCaption_i("");
        model.setCaption_ii("");
        model.setCaption_iii("");
        model.setCaption_iv("");
        model.setCaption_v("");
        model.setCaption_vi("");
        model.setCaption_vii("");
        model.setCaption_viii("");
        model.setCaption_ix("");
        model.setCaption_x("");
        model.setCaption_xi("");
        model.setCaption_xii("");
        model.setCaption_xiii("");
        model.setCaption_xiv("");
        model.setCaption_xv("");
        model.setCaption_xvi("");
        model.setCaption_xvii("");

        model.setThumbnail_un("");
        model.setThumbnail_deux("");
        model.setThumbnail("");

        model.setCountry_name("");
        model.setCountryID("");

        // republication of publication
        model.setImageDouble_shared(false);
        model.setImageUne_shared(false);
        model.setRecyclerItem_shared(false);
        model.setReponseImageDouble_shared(false);
        model.setReponseVideoDouble_shared(false);
        model.setVideoDouble_shared(false);
        model.setVideoUne_shared(false);
        model.setUser_profile(false);
        model.setUser_id_sharing("");
        // for saved
        model.setUsername("");
        model.setProfileUrl("");
        model.setAuth_user_id("");
        model.setUser_id_shared("");

        // post identity
        String post_key = myRef.push().getKey();
        model.setPost_identity(post_key);

        assert newPhotoKey != null;
        // free answering challenge
        if (from_home_challenge != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", user_id);
            map.put("invite_photoID", model_invite.getInvite_photoID());

            myRef.child(getString(R.string.dbname_battle_response_challenge))
                    .child(model_invite.getInvite_photoID())
                    .child(newPhotoKey)
                    .setValue(map);

            // response user ID
            model.setUser_id(user_id);

            myRef.child(getString(R.string.dbname_battle))
                    .child(user_id)
                    .child(newPhotoKey)
                    .setValue(model);

            myRef.child(getString(R.string.dbname_battle_media))
                    .child(newPhotoKey)
                    .setValue(model).addOnCompleteListener(task -> {

                        // delete file
                        if (thumbnailFile != null)
                            thumbnailFile.delete();
                        if (file != null)
                            file.delete();

                        if (video_compressed != null)
                            videoFile.delete();

                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });

        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", user_id);
            map.put("invite_photoID", model_invite.getInvite_photoID());

            myRef.child(getString(R.string.dbname_battle_response_challenge))
                    .child(model_invite.getInvite_photoID())
                    .child(newPhotoKey)
                    .setValue(map);

            // response user ID
            model.setUser_id(user_id);

            myRef.child(getString(R.string.dbname_battle))
                    .child(user_id)
                    .child(newPhotoKey)
                    .setValue(model);

            // change invitation user ID to receive post
            model.setUser_id(model_invite.getInvite_userID());

            myRef.child(getString(R.string.dbname_battle))
                    .child(model_invite.getInvite_userID())
                    .child(newPhotoKey)
                    .setValue(model);

            myRef.child(getString(R.string.dbname_battle_media))
                    .child(newPhotoKey)
                    .setValue(model);

            // to make the challenge not appear again on the challenge list
            map = new HashMap<>();
            map.put("reponse_user_id", user_id);
            map.put("invite_user_id", model_invite.getInvite_userID());
            map.put("invite_photo_id", model_invite.getInvite_photoID());
            map.put("response_photo_id", newPhotoKey);
            map.put("group_id", "");
            map.put("date_created", date.getTime());

            myRef.child(getString(R.string.dbname_already_response_challenge))
                    .child(user_id)
                    .child(model_invite.getInvite_photoID())
                    .setValue(map).addOnCompleteListener(task -> {

                        // delete file
                        if (thumbnailFile != null)
                            thumbnailFile.delete();
                        if (file != null)
                            file.delete();

                        if (video_compressed != null)
                            videoFile.delete();

                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }
    }

    // publication dans le main battle (group)
    private void addGroupMediaToDatabase(String url, String caption, String thumbnail_response) {
        String newPhotoKey = myRef.child(getString(R.string.dbname_battle_media)).push().getKey();
        Date date=new Date();

        String tags = StringManipulation.getTags(caption);
        BattleModel model = new BattleModel();

        if (from_gallery_video != null) {
            model.setGroup_response_imageDouble(false);
            model.setGroup_response_videoDouble(true);

        } else {
            model.setGroup_response_imageDouble(true);
            model.setGroup_response_videoDouble(false);
        }
        model.setReponse_deja(false);

        model.setDate_created(date.getTime());
        model.setUser_id(user_id);

        if (model_invite.getThumbnail_invite() != null && !TextUtils.isEmpty(model_invite.getThumbnail_invite())) {
            model.setThumbnail_invite(model_invite.getThumbnail_invite());
            model.setThumbnail_response(thumbnail_response);

        } else {
            model.setThumbnail_invite("");
            model.setThumbnail_response("");
        }

        model.setSuppressed(false);
        model.setRecycler_story(false);
        model.setFriends_suggestion_one(false);
        model.setFriends_suggestion_two(false);
        model.setFriends_suggestion_three(false);
        model.setFriends_suggestion_four(false);
        model.setFriends_suggestion_five(false);
        model.setGroups_suggestion_one(false);
        model.setGroups_suggestion_two(false);
        model.setGroups_suggestion_three(false);
        model.setGroups_suggestion_four(false);
        model.setGroups_suggestion_five(false);
        model.setVideos_suggestion_one(false);
        model.setVideos_suggestion_two(false);
        model.setVideos_suggestion_three(false);
        model.setVideos_suggestion_four(false);
        model.setVideos_suggestion_five(false);
        model.setEveryone_can_answer_this_challenge(model_invite.isEveryone_can_answer_this_challenge());
        model.setDetails("");

        model.setRecyclerItem(false);
        model.setImageUne(false);
        model.setVideoUne(false);
        model.setImageDouble(false);
        model.setVideoDouble(false);
        model.setReponseImageDouble(false);
        model.setReponseVideoDouble(false);
        // pour le grid profile
        model.setGridRecyclerImage(false);

        model.setInvite_url(model_invite.getInvite_url());
        model.setInvite_photoID(model_invite.getInvite_photoID());
        model.setInvite_username(model_invite.getInvite_username());
        model.setInvite_userID(model_invite.getInvite_userID());
        model.setInvite_caption(model_invite.getInvite_caption());
        model.setInvite_tag(model_invite.getInvite_tag());
        model.setInvite_category(model_invite.getInvite_category());
        model.setInvite_category_status(category_status);
        model.setInvite_profile_photo(model_invite.getInvite_profile_photo());
        model.setInvite_country_name(model_invite.getInvite_country_name());
        model.setInvite_countryID(model_invite.getInvite_countryID());

        model.setReponse_profile_photo(reponse_profile_photo);
        model.setReponse_username(repnse_usename);
        model.setReponse_user_id(user_id);
        model.setReponse_url(url);
        model.setReponse_photoID(newPhotoKey);
        model.setReponse_caption(caption);
        model.setReponse_tag(tags);
        model.setReponse_country_name(GetCountryName());
        model.setReponse_countryID(GetCountryZipCode());

        model.setCaptionUn("");
        model.setUrlUn("");
        model.setUrlDeux("");
        model.setTagsUn("");
        model.setTagsDeux("");
        model.setPhoto_id_un("");
        model.setPhoto_id_deux("");

        model.setCaption("");
        model.setSharing_caption("");
        model.setTags("");
        model.setUrl("");
        model.setPhoto_id("");
        // group
        model.setGroup(true);
        model.setGroup_private(model_invite.isGroup_private());
        model.setGroup_public(model_invite.isGroup_public());
        model.setGroup_cover_profile_photo(false);
        model.setGroup_recyclerItem(false);
        model.setGroup_imageUne(false);
        model.setGroup_videoUne(false);
        model.setGroup_cover_background_profile_photo(false);
        model.setGroup_imageDouble(false);
        model.setGroup_videoDouble(false);

        model.setGroup_share_single_bottom_image_double(false);
        model.setGroup_share_single_bottom_image_une(false);
        model.setGroup_share_single_bottom_recycler(false);
        model.setGroup_share_single_bottom_response_image_double(false);
        model.setGroup_share_single_bottom_response_video_double(false);
        model.setGroup_share_single_bottom_video_double(false);
        model.setGroup_share_single_bottom_video_une(false);

        model.setGroup_share_single_top_image_double(false);
        model.setGroup_share_single_top_image_une(false);
        model.setGroup_share_single_top_recycler(false);
        model.setGroup_share_single_top_response_image_double(false);
        model.setGroup_share_single_top_response_video_double(false);
        model.setGroup_share_single_top_video_double(false);
        model.setGroup_share_single_top_video_une(false);

        model.setGroup_share_top_bottom_image_double(false);
        model.setGroup_share_top_bottom_image_une(false);
        model.setGroup_share_top_bottom_recycler(false);
        model.setGroup_share_top_bottom_response_image_double(false);
        model.setGroup_share_top_bottom_response_video_double(false);
        model.setGroup_share_top_bottom_video_double(false);
        model.setGroup_share_top_bottom_video_une(false);

        model.setUser_profile_shared(false);
        model.setGroup_share_single_bottom_circle_image(false);
        model.setGroup_share_single_top_circle_image(false);
        model.setGroup_share_top_bottom_circle_image(false);

        model.setUser_profile_photo("");
        model.setUser_full_profile_photo("");
        model.setDate_shared(0);
        model.setViews(0);

        model.setSharing_admin_master("");
        model.setShared_group_id("");
        model.setAdmin_master(model_invite.getAdmin_master());
        model.setGroup_id(model_invite.getGroup_id());
        model.setPublisher("");
        model.setGroup_profile_photo("");
        model.setGroup_full_profile_photo("");
        model.setGroup_user_background_profile_url("");
        model.setGroup_user_background_full_profile_url("");

        model.setComment_text(false);
        model.setComment_subject("");
        model.setComment_theme("");
        model.setBig_image(false);

        model.setUrli("");
        model.setUrlii("");
        model.setUrliii("");
        model.setUrliv("");
        model.setUrlv("");
        model.setUrlvi("");
        model.setUrlvii("");
        model.setUrlviii("");
        model.setUrlix("");
        model.setUrlx("");
        model.setUrlxi("");
        model.setUrlxii("");
        model.setUrlxiii("");
        model.setUrlxiv("");
        model.setUrlxv("");
        model.setUrlxvi("");
        model.setUrlxvii("");

        model.setPhotoi_id("");
        model.setPhotoii_id("");
        model.setPhotoiii_id("");
        model.setPhotoiv_id("");
        model.setPhotov_id("");
        model.setPhotovi_id("");
        model.setPhotovii_id("");
        model.setPhotoviii_id("");
        model.setPhotoix_id("");
        model.setPhotox_id("");
        model.setPhotoxi_id("");
        model.setPhotoxii_id("");
        model.setPhotoxiii_id("");
        model.setPhotoxiv_id("");
        model.setPhotoxv_id("");
        model.setPhotoxvi_id("");
        model.setPhotoxvii_id("");

        model.setCaption_i("");
        model.setCaption_ii("");
        model.setCaption_iii("");
        model.setCaption_iv("");
        model.setCaption_v("");
        model.setCaption_vi("");
        model.setCaption_vii("");
        model.setCaption_viii("");
        model.setCaption_ix("");
        model.setCaption_x("");
        model.setCaption_xi("");
        model.setCaption_xii("");
        model.setCaption_xiii("");
        model.setCaption_xiv("");
        model.setCaption_xv("");
        model.setCaption_xvi("");
        model.setCaption_xvii("");

        model.setThumbnail_un("");
        model.setThumbnail_deux("");
        model.setThumbnail("");

        model.setCountry_name("");
        model.setCountryID("");

        // republication of publication
        model.setImageDouble_shared(false);
        model.setImageUne_shared(false);
        model.setRecyclerItem_shared(false);
        model.setReponseImageDouble_shared(false);
        model.setReponseVideoDouble_shared(false);
        model.setVideoDouble_shared(false);
        model.setVideoUne_shared(false);
        model.setUser_profile(false);
        model.setUser_id_sharing("");
        // for saved
        model.setUsername("");
        model.setProfileUrl("");
        model.setAuth_user_id("");
        model.setUser_id_shared("");

        // post identity
        String post_key = myRef.push().getKey();
        model.setPost_identity(post_key);

        // to know if the member admin post approval
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(model_invite.getGroup_id())
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

                    assert newPhotoKey != null;
                    if (follower.isPublicationApprobation()) {
                        // upload to group node
                        myRef.child(getString(R.string.dbname_group_waiting_for_approval))
                                .child(model_invite.getGroup_id())
                                .child(newPhotoKey)
                                .setValue(model);

                        myRef.child(getString(R.string.dbname_group_Media_waiting_for_approval))
                                .child(newPhotoKey)
                                .setValue(model);


                        // to make the challenge not appear again on the challenge list
                        map = new HashMap<>();
                        map.put("reponse_user_id", user_id);
                        map.put("invite_user_id", model_invite.getInvite_userID());
                        map.put("invite_photo_id", model_invite.getInvite_photoID());
                        map.put("response_photo_id", newPhotoKey);
                        map.put("group_id", model_invite.getGroup_id());
                        map.put("date_created", date.getTime());

                        myRef.child(getString(R.string.dbname_already_response_challenge))
                                .child(user_id)
                                .child(model_invite.getInvite_photoID())
                                .setValue(map).addOnCompleteListener(task -> {

                                    // delete file
                                    if (thumbnailFile != null)
                                        thumbnailFile.delete();
                                    if (file != null)
                                        file.delete();

                                    if (video_compressed != null)
                                        videoFile.delete();

                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });

                    } else {
                        // upload to group node
                        if (from_home_challenge != null) {
                            // for response of challenge
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", model_invite.getGroup_id());
                            map.put("invite_photoID", model_invite.getInvite_photoID());

                            myRef.child(getString(R.string.dbname_group_response_challenge))
                                    .child(model_invite.getInvite_photoID())
                                    .child(newPhotoKey)
                                    .setValue(map);

                            myRef.child(getString(R.string.dbname_group))
                                    .child(model_invite.getGroup_id())
                                    .child(newPhotoKey)
                                    .setValue(model);

                            myRef.child(getString(R.string.dbname_group_media))
                                    .child(newPhotoKey)
                                    .setValue(model).addOnCompleteListener(task -> {

                                        // delete file
                                        if (thumbnailFile != null)
                                            thumbnailFile.delete();
                                        if (file != null)
                                            file.delete();

                                        if (video_compressed != null)
                                            videoFile.delete();

                                        //add points and send notification
                                        addPostPointsAndSendNotification();

                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });

                        } else {
                            // for response of challenge
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", model_invite.getGroup_id());
                            map.put("invite_photoID", model_invite.getInvite_photoID());

                            myRef.child(getString(R.string.dbname_group_response_challenge))
                                    .child(model_invite.getInvite_photoID())
                                    .child(newPhotoKey)
                                    .setValue(map);

                            myRef.child(getString(R.string.dbname_group))
                                    .child(model_invite.getGroup_id())
                                    .child(newPhotoKey)
                                    .setValue(model);

                            myRef.child(getString(R.string.dbname_group_media))
                                    .child(newPhotoKey)
                                    .setValue(model);


                            // to make the challenge not appear again on the challenge list
                            map = new HashMap<>();
                            map.put("reponse_user_id", user_id);
                            map.put("invite_user_id", model_invite.getInvite_userID());
                            map.put("invite_photo_id", model_invite.getInvite_photoID());
                            map.put("response_photo_id", newPhotoKey);
                            map.put("group_id", model_invite.getGroup_id());
                            map.put("date_created", date.getTime());

                            myRef.child(getString(R.string.dbname_already_response_challenge))
                                    .child(user_id)
                                    .child(model_invite.getInvite_photoID())
                                    .setValue(map).addOnCompleteListener(task -> {

                                        // delete file
                                        if (thumbnailFile != null)
                                            thumbnailFile.delete();
                                        if (file != null)
                                            file.delete();

                                        if (video_compressed != null)
                                            videoFile.delete();

                                        //add points and send notification
                                        addPostPointsAndSendNotification();

                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });
                        }
                    }
                }

                if (!snapshot.exists()) {
                    // for response of challenge
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", model_invite.getGroup_id());
                    map.put("invite_photoID", model_invite.getInvite_photoID());

                    assert newPhotoKey != null;
                    myRef.child(getString(R.string.dbname_group_response_challenge))
                            .child(model_invite.getInvite_photoID())
                            .child(newPhotoKey)
                            .setValue(map);

                    myRef.child(getString(R.string.dbname_group))
                            .child(model_invite.getGroup_id())
                            .child(newPhotoKey)
                            .setValue(model);

                    myRef.child(getString(R.string.dbname_group_media))
                            .child(newPhotoKey)
                            .setValue(model);


                    // to make the challenge not appear again on the challenge list
                    map = new HashMap<>();
                    map.put("reponse_user_id", user_id);
                    map.put("invite_user_id", model_invite.getInvite_userID());
                    map.put("invite_photo_id", model_invite.getInvite_photoID());
                    map.put("response_photo_id", newPhotoKey);
                    map.put("group_id", model_invite.getGroup_id());
                    map.put("date_created", date.getTime());

                    myRef.child(getString(R.string.dbname_already_response_challenge))
                            .child(user_id)
                            .child(model_invite.getInvite_photoID())
                            .setValue(map).addOnCompleteListener(task -> {

                                // delete file
                                if (thumbnailFile != null)
                                    thumbnailFile.delete();
                                if (file != null)
                                    file.delete();

                                if (video_compressed != null)
                                    videoFile.delete();

                                //add points and send notification
                                addPostPointsAndSendNotification();

                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // detect if user is admin master
        if (model_invite.getAdmin_master().equals(user_id)) {
            // upload to group node
            if (from_home_challenge != null) {
                // for response of challenge
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", model_invite.getGroup_id());
                map.put("invite_photoID", model_invite.getInvite_photoID());

                assert newPhotoKey != null;
                myRef.child(getString(R.string.dbname_group_response_challenge))
                        .child(model_invite.getInvite_photoID())
                        .child(newPhotoKey)
                        .setValue(map);

                myRef.child(getString(R.string.dbname_group))
                        .child(model_invite.getGroup_id())
                        .child(newPhotoKey)
                        .setValue(model);

                myRef.child(getString(R.string.dbname_group_media))
                        .child(newPhotoKey)
                        .setValue(model).addOnCompleteListener(task -> {

                            // delete file
                            if (thumbnailFile != null)
                                thumbnailFile.delete();
                            if (file != null)
                                file.delete();

                            if (video_compressed != null)
                                videoFile.delete();

                            //add points and send notification
                            addPostPointsAndSendNotification();

                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });

            } else {
                // for response of challenge
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", model_invite.getGroup_id());
                map.put("invite_photoID", model_invite.getInvite_photoID());

                assert newPhotoKey != null;
                myRef.child(getString(R.string.dbname_group_response_challenge))
                        .child(model_invite.getInvite_photoID())
                        .child(newPhotoKey)
                        .setValue(map);

                myRef.child(getString(R.string.dbname_group))
                        .child(model_invite.getGroup_id())
                        .child(newPhotoKey)
                        .setValue(model);

                myRef.child(getString(R.string.dbname_group_media))
                        .child(newPhotoKey)
                        .setValue(model);

                // to make the challenge not appear again on the challenge list
                map = new HashMap<>();
                map.put("reponse_user_id", user_id);
                map.put("invite_user_id", model_invite.getInvite_userID());
                map.put("invite_photo_id", model_invite.getInvite_photoID());
                map.put("response_photo_id", newPhotoKey);
                map.put("group_id", model_invite.getGroup_id());
                map.put("date_created", date.getTime());

                myRef.child(getString(R.string.dbname_already_response_challenge))
                        .child(user_id)
                        .child(model_invite.getInvite_photoID())
                        .setValue(map).addOnCompleteListener(task -> {

                            // delete file
                            if (thumbnailFile != null)
                                thumbnailFile.delete();
                            if (file != null)
                                file.delete();

                            if (video_compressed != null)
                                videoFile.delete();

                            //add points and send notification
                            addPostPointsAndSendNotification();

                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
            }
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

    void setProfileWidgets(UserSettings userSettings) {
        UserAccountSettings settings = userSettings.getSettings();

        reponse_profile_photo = settings.getProfileUrl();
        repnse_usename = settings.getUsername();
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

    // country name corresponding to country code
    public String GetCountryZipCode(){
        String CountryID;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso();
        return CountryID;
    }

    // country name corresponding to country name
    public String GetCountryName(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return CountryZipCode;
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mFirebaseMethods = new FirebaseMethods(context);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // add post point and send notification
    @TargetApi(Build.VERSION_CODES.O)
    private void addPostPointsAndSendNotification() {
        if (model_invite.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model_invite.getGroup_id());

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
                    .child(model_invite.getGroup_id());
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
                                    members_id_list.get(i), new_key, user_id, model_invite.getAdmin_master(),
                                    "", model_invite.getGroup_id(), date.getTime(),
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
                    .equalTo(model_invite.getGroup_id());

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
                                .child(model_invite.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(model_invite.getGroup_id())
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
                    .child(model_invite.getGroup_id());
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
                    if (!model_invite.getAdmin_master().equals(user_id))
                        members_id_list.add(model_invite.getAdmin_master());

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
                                    members_id_list.get(i), new_key, user_id, model_invite.getAdmin_master(),
                                    "", model_invite.getGroup_id(), date.getTime(),
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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}