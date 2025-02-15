package com.bekambimouen.miiokychallenge.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.ShareUtils;
import com.bekambimouen.miiokychallenge.groups.shared_in_group.GroupSharePublicationInGroup;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.share_publication.SharePublicationInMiioky;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class BottomSheetSharePublication extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetSharePublication";
    // vars
    private final AppCompatActivity context;
    private final BattleModel model;
    private final String url;
    private final String photo_id;
    private final String userID;
    private final String user_id;
    private final String from;
    private final String value;
    private final boolean isShared;
    private final DatabaseReference myRef;

    private CustomShareProgressView progresDialog;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.setCancelable(false);
        progresDialog.show();
    }

    // share on group
    private boolean group_share_single_bottom_circle_image = false;
    private boolean group_share_single_bottom_image_double = false;
    private boolean group_share_single_bottom_image_une = false;
    private boolean group_share_single_bottom_recycler = false;
    private boolean group_share_single_bottom_response_image_double = false;
    private boolean group_share_single_bottom_response_video_double = false;
    private boolean group_share_single_bottom_video_double = false;
    private boolean group_share_single_bottom_video_une = false;

    private boolean group_share_top_bottom_circle_image = false;
    private boolean group_share_top_bottom_image_double = false;
    private boolean group_share_top_bottom_image_une = false;
    private boolean group_share_top_bottom_recycler = false;
    private boolean group_share_top_bottom_response_image_double = false;
    private boolean group_share_top_bottom_response_video_double = false;
    private boolean group_share_top_bottom_video_double = false;
    private boolean group_share_top_bottom_video_une = false;

    private boolean group_share_single_top_circle_image = false;
    private boolean group_share_single_top_image_double = false;
    private boolean group_share_single_top_image_une = false;
    private boolean group_share_single_top_recycler = false;
    private boolean group_share_single_top_response_image_double = false;
    private boolean group_share_single_top_response_video_double = false;
    private boolean group_share_single_top_video_double = false;
    private boolean group_share_single_top_video_une = false;

    private final boolean group_circle_image = false;
    private final boolean group_image_double = false;
    private final boolean group_image_une = false;
    private final boolean group_recycler = false;
    private final boolean group_response_image_double = false;
    private final boolean group_response_video_double = false;
    private final boolean group_video_double = false;
    private final boolean group_video_une = false;

    // share on Miioky
    private boolean user_profile_shared = false;
    private boolean recyclerItem_shared = false;
    private boolean imageUne_shared = false;
    private boolean videoUne_shared = false;
    private boolean imageDouble_shared = false;
    private boolean videoDouble_shared = false;
    private boolean reponseImageDouble_shared = false;
    private boolean reponseVideoDouble_shared = false;

    private final boolean circle_image = false;
    private final boolean image_double = false;
    private final boolean image_une = false;
    private final boolean recycler = false;
    private final boolean response_image_double = false;
    private final boolean response_video_double = false;
    private final boolean video_double = false;
    private final boolean video_une = false;

    private String item_view;

    public BottomSheetSharePublication(AppCompatActivity context, BattleModel model, String userID, String url,
                                       String photo_id, String from, String value, boolean isShared) {
        this.context = context;
        this.model = model;
        this.userID = userID;
        this.url = url;
        this.photo_id = photo_id;
        this.from = from;
        this.value = value;
        this.isShared = isShared;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onStart()
    {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottomsheet_share_publication, null);

        View view_bar = view.findViewById(R.id.view_bar);
        view_bar.setOnClickListener(view1 -> dialog.dismiss());

        LinearLayout linLayout_share_in_app = view.findViewById(R.id.linLayout_share_in_app);
        LinearLayout linLayout_share_on_miioky = view.findViewById(R.id.linLayout_share_on_miioky);
        LinearLayout linLayout_group = view.findViewById(R.id.linLayout_group);
        LinearLayout linLayout_download = view.findViewById(R.id.linLayout_download);
        LinearLayout linLayout_extarnal_share = view.findViewById(R.id.linLayout_extarnal_share);

        LinearLayout linLayout_share_link = view.findViewById(R.id.linLayout_share_link);
        LinearLayout linLayout_whatsapp = view.findViewById(R.id.linLayout_whatsapp);
        LinearLayout linLayout_facebook = view.findViewById(R.id.linLayout_facebook);
        LinearLayout linLayout_twitter = view.findViewById(R.id.linLayout_twitter);
        LinearLayout linLayout_snapchat = view.findViewById(R.id.linLayout_snapchat);
        LinearLayout linLayout_instagram = view.findViewById(R.id.linLayout_instagram);

        if (this.getArguments() != null) {
            item_view = this.getArguments().getString("item_view");
        }

        if (item_view == null) {
            linLayout_share_on_miioky.setVisibility(View.GONE);
            linLayout_group.setVisibility(View.GONE);
        }

        if (userID.equals(user_id))
            linLayout_share_on_miioky.setVisibility(View.GONE);

        // to hide some line of bottomSheet ________________________________
        if (isShared) {
            linLayout_share_on_miioky.setVisibility(View.GONE);
            linLayout_group.setVisibility(View.GONE);
        }

        if (from.equals("from_video") && isShared) {
            linLayout_share_in_app.setVisibility(View.GONE);
            linLayout_download.setVisibility(View.GONE);
            linLayout_extarnal_share.setVisibility(View.GONE);

        } else if (from.equals("from_video")) {
            linLayout_download.setVisibility(View.GONE);
            linLayout_extarnal_share.setVisibility(View.GONE);
        }

        if (from.equals("from_recyclerView_item") || from.equals("from_full_image")) {
            linLayout_share_on_miioky.setVisibility(View.GONE);
            linLayout_group.setVisibility(View.GONE);
        }

        if (from.equals("from_recyclerView")) {
            linLayout_download.setVisibility(View.GONE);
            linLayout_extarnal_share.setVisibility(View.GONE);
            linLayout_share_link.setVisibility(View.GONE);
        }

        // hide view if current user not member of Group
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_id);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Query query = myRef
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                linLayout_group.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // download on user phone _____________________________________________________________
        linLayout_download.setOnClickListener(view1 -> {
            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

            } else {
                addNewSavePhoto();
            }
            dialog.dismiss();
        });

        linLayout_group.setOnClickListener(view1 -> {
            dialog.dismiss();

            if (item_view != null) {
                switch (item_view) {
                    case "group_cover":
                        group_share_top_bottom_circle_image = true;
                        break;
                    case "group_image_double":
                        group_share_top_bottom_image_double = true;
                        break;
                    case "group_image_une":
                        group_share_top_bottom_image_une = true;
                        break;
                    case "group_recyclerview":
                        group_share_top_bottom_recycler = true;
                        break;
                    case "group_response_image_double":
                        group_share_top_bottom_response_image_double = true;
                        break;
                    case "group_response_video_double":
                        group_share_top_bottom_response_video_double = true;
                        break;
                    case "group_video_double":
                        group_share_top_bottom_video_double = true;
                        break;
                    case "group_video_une":
                        group_share_top_bottom_video_une = true;
                        break;
                    case "user_profile":
                        group_share_single_bottom_circle_image = true;
                        break;
                    case "image_double":
                        group_share_single_bottom_image_double = true;
                        break;
                    case "image_une":
                        group_share_single_bottom_image_une = true;
                        break;
                    case "recyclerview":
                        group_share_single_bottom_recycler = true;
                        break;
                    case "response_image_double":
                        group_share_single_bottom_response_image_double = true;
                        break;
                    case "response_video_double":
                        group_share_single_bottom_response_video_double = true;
                        break;
                    case "video_double":
                        group_share_single_bottom_video_double = true;
                        break;
                    case "video_une":
                        group_share_single_bottom_video_une = true;
                        break;
                }
            }

            Intent intent = new Intent(context, GroupSharePublicationInGroup.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("model", myGson);
            intent.putExtra("value", value);

            intent.putExtra("group_share_single_bottom_circle_image", group_share_single_bottom_circle_image);
            intent.putExtra("group_share_single_bottom_image_double", group_share_single_bottom_image_double);
            intent.putExtra("group_share_single_bottom_image_une", group_share_single_bottom_image_une);
            intent.putExtra("group_share_single_bottom_recycler", group_share_single_bottom_recycler);
            intent.putExtra("group_share_single_bottom_response_image_double", group_share_single_bottom_response_image_double);
            intent.putExtra("group_share_single_bottom_response_video_double", group_share_single_bottom_response_video_double);
            intent.putExtra("group_share_single_bottom_video_double", group_share_single_bottom_video_double);
            intent.putExtra("group_share_single_bottom_video_une", group_share_single_bottom_video_une);

            intent.putExtra("group_share_single_top_circle_image", group_share_single_top_circle_image);
            intent.putExtra("group_share_single_top_image_double", group_share_single_top_image_double);
            intent.putExtra("group_share_single_top_image_une", group_share_single_top_image_une);
            intent.putExtra("group_share_single_top_recycler", group_share_single_top_recycler);
            intent.putExtra("group_share_single_top_response_image_double", group_share_single_top_response_image_double);
            intent.putExtra("group_share_single_top_response_video_double", group_share_single_top_response_video_double);
            intent.putExtra("group_share_single_top_video_double", group_share_single_top_video_double);
            intent.putExtra("group_share_single_top_video_une", group_share_single_top_video_une);

            intent.putExtra("group_share_top_bottom_circle_image", group_share_top_bottom_circle_image);
            intent.putExtra("group_share_top_bottom_image_double", group_share_top_bottom_image_double);
            intent.putExtra("group_share_top_bottom_image_une", group_share_top_bottom_image_une);
            intent.putExtra("group_share_top_bottom_recycler", group_share_top_bottom_recycler);
            intent.putExtra("group_share_top_bottom_response_image_double", group_share_top_bottom_response_image_double);
            intent.putExtra("group_share_top_bottom_response_video_double", group_share_top_bottom_response_video_double);
            intent.putExtra("group_share_top_bottom_video_double", group_share_top_bottom_video_double);
            intent.putExtra("group_share_top_bottom_video_une", group_share_top_bottom_video_une);

            intent.putExtra("user_profile_shared", user_profile_shared);
            intent.putExtra("imageDouble_shared", imageDouble_shared);
            intent.putExtra("imageUne_shared", imageUne_shared);
            intent.putExtra("recyclerItem_shared", recyclerItem_shared);
            intent.putExtra("reponseImageDouble_shared", reponseImageDouble_shared);
            intent.putExtra("reponseVideoDouble_shared", reponseVideoDouble_shared);
            intent.putExtra("videoDouble_shared", videoDouble_shared);
            intent.putExtra("videoUne_shared", videoUne_shared);

            intent.putExtra("group_circle_image", group_circle_image);
            intent.putExtra("group_image_double", group_image_double);
            intent.putExtra("group_image_une", group_image_une);
            intent.putExtra("group_recycler", group_recycler);
            intent.putExtra("group_response_image_double", group_response_image_double);
            intent.putExtra("group_response_video_double", group_response_video_double);
            intent.putExtra("group_video_double", group_video_double);
            intent.putExtra("group_video_une", group_video_une);

            intent.putExtra("circle_image", circle_image);
            intent.putExtra("image_double", image_double);
            intent.putExtra("image_une", image_une);
            intent.putExtra("recycler", recycler);
            intent.putExtra("response_image_double", response_image_double);
            intent.putExtra("response_video_double", response_video_double);
            intent.putExtra("video_double", video_double);
            intent.putExtra("video_une", video_une);

            startActivity(intent);
        });

        linLayout_share_on_miioky.setOnClickListener(view1 -> {
            dialog.dismiss();

            if (item_view != null) {
                switch (item_view) {
                    case "group_cover":
                        group_share_single_top_circle_image = true;
                        break;
                    case "group_image_double":
                        group_share_single_top_image_double = true;
                        break;
                    case "group_image_une":
                        group_share_single_top_image_une = true;
                        break;
                    case "group_recyclerview":
                        group_share_single_top_recycler = true;
                        break;
                    case "group_response_image_double":
                        group_share_single_top_response_image_double = true;
                        break;
                    case "group_response_video_double":
                        group_share_single_top_response_video_double = true;
                        break;
                    case "group_video_double":
                        group_share_single_top_video_double = true;
                        break;
                    case "group_video_une":
                        group_share_single_top_video_une = true;
                        break;

                    case "user_profile":
                        user_profile_shared = true;
                        break;
                    case "image_double":
                        imageDouble_shared = true;
                        break;
                    case "image_une":
                        imageUne_shared = true;
                        break;
                    case "recyclerview":
                        recyclerItem_shared = true;
                        break;
                    case "response_image_double":
                        reponseImageDouble_shared = true;
                        break;
                    case "response_video_double":
                        reponseVideoDouble_shared = true;
                        break;
                    case "video_double":
                        videoDouble_shared = true;
                        break;
                    case "video_une":
                        videoUne_shared = true;
                        break;
                }
            }

            Intent intent = new Intent(context, SharePublicationInMiioky.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("model", myGson);
            intent.putExtra("value", value);

            intent.putExtra("group_share_single_bottom_circle_image", group_share_single_bottom_circle_image);
            intent.putExtra("group_share_single_bottom_image_double", group_share_single_bottom_image_double);
            intent.putExtra("group_share_single_bottom_image_une", group_share_single_bottom_image_une);
            intent.putExtra("group_share_single_bottom_recycler", group_share_single_bottom_recycler);
            intent.putExtra("group_share_single_bottom_response_image_double", group_share_single_bottom_response_image_double);
            intent.putExtra("group_share_single_bottom_response_video_double", group_share_single_bottom_response_video_double);
            intent.putExtra("group_share_single_bottom_video_double", group_share_single_bottom_video_double);
            intent.putExtra("group_share_single_bottom_video_une", group_share_single_bottom_video_une);

            intent.putExtra("group_share_single_top_circle_image", group_share_single_top_circle_image);
            intent.putExtra("group_share_single_top_image_double", group_share_single_top_image_double);
            intent.putExtra("group_share_single_top_image_une", group_share_single_top_image_une);
            intent.putExtra("group_share_single_top_recycler", group_share_single_top_recycler);
            intent.putExtra("group_share_single_top_response_image_double", group_share_single_top_response_image_double);
            intent.putExtra("group_share_single_top_response_video_double", group_share_single_top_response_video_double);
            intent.putExtra("group_share_single_top_video_double", group_share_single_top_video_double);
            intent.putExtra("group_share_single_top_video_une", group_share_single_top_video_une);

            intent.putExtra("group_share_top_bottom_circle_image", group_share_top_bottom_circle_image);
            intent.putExtra("group_share_top_bottom_image_double", group_share_top_bottom_image_double);
            intent.putExtra("group_share_top_bottom_image_une", group_share_top_bottom_image_une);
            intent.putExtra("group_share_top_bottom_recycler", group_share_top_bottom_recycler);
            intent.putExtra("group_share_top_bottom_response_image_double", group_share_top_bottom_response_image_double);
            intent.putExtra("group_share_top_bottom_response_video_double", group_share_top_bottom_response_video_double);
            intent.putExtra("group_share_top_bottom_video_double", group_share_top_bottom_video_double);
            intent.putExtra("group_share_top_bottom_video_une", group_share_top_bottom_video_une);

            intent.putExtra("user_profile_shared", user_profile_shared);
            intent.putExtra("imageDouble_shared", imageDouble_shared);
            intent.putExtra("imageUne_shared", imageUne_shared);
            intent.putExtra("recyclerItem_shared", recyclerItem_shared);
            intent.putExtra("reponseImageDouble_shared", reponseImageDouble_shared);
            intent.putExtra("reponseVideoDouble_shared", reponseVideoDouble_shared);
            intent.putExtra("videoDouble_shared", videoDouble_shared);
            intent.putExtra("videoUne_shared", videoUne_shared);

            intent.putExtra("group_circle_image", group_circle_image);
            intent.putExtra("group_image_double", group_image_double);
            intent.putExtra("group_image_une", group_image_une);
            intent.putExtra("group_recycler", group_recycler);
            intent.putExtra("group_response_image_double", group_response_image_double);
            intent.putExtra("group_response_video_double", group_response_video_double);
            intent.putExtra("group_video_double", group_video_double);
            intent.putExtra("group_video_une", group_video_une);

            intent.putExtra("circle_image", circle_image);
            intent.putExtra("image_double", image_double);
            intent.putExtra("image_une", image_une);
            intent.putExtra("recycler", recycler);
            intent.putExtra("response_image_double", response_image_double);
            intent.putExtra("response_video_double", response_video_double);
            intent.putExtra("video_double", video_double);
            intent.putExtra("video_une", video_une);

            startActivity(intent);
        });

        // external share
        linLayout_extarnal_share.setOnClickListener(view1 -> {
            dialog.dismiss();

            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

            } else {
                ShareUtils.countShare(context, photo_id);

                Thread thread = new Thread(() -> {
                    URL url1 = null;
                    try {
                        url1 = new URL(url);
                    } catch (MalformedURLException e) {
                        Log.d(TAG, "setupDialog: "+e.getMessage());
                    }
                    HttpURLConnection connection = null;
                    try {
                        assert url1 != null;
                        connection = (HttpURLConnection)
                                url1.openConnection();
                    } catch (IOException e) {
                        Log.d(TAG, "setupDialog: "+e.getMessage());
                    }
                    assert connection != null;
                    connection.setDoInput(true);
                    try {
                        connection.connect();
                    } catch (IOException e) {
                        Log.d(TAG, "setupDialog: "+e.getMessage());
                    }
                    InputStream input = null;
                    try {
                        input = connection.getInputStream();
                    } catch (IOException e) {
                        Log.d(TAG, "setupDialog: "+e.getMessage());
                    }
                    Bitmap imgBitmap = BitmapFactory.decodeStream(input);
                    Random rand = new Random();
                    int randNo = rand.nextInt(100000);
                    String imgBitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), imgBitmap, "IMG:" + randNo, null);
                    Uri imgBitmapUri = Uri.parse(imgBitmapPath);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
                    shareIntent.setType("image/png");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Miioky");
                    context.startActivity(Intent.createChooser(shareIntent, "Share with"));
                });
                thread.start();
            }
        });

        linLayout_whatsapp.setOnClickListener(view1 -> {
            ShareUtils.shareWhatsapp(context, "Plus de publication sur Miioky", url, photo_id);
            dialog.dismiss();
        });

        linLayout_facebook.setOnClickListener(view1 -> {
            ShareUtils.shareFacebook(context, "Plus de publication sur Miioky", url, photo_id);
            dialog.dismiss();
        });

        linLayout_twitter.setOnClickListener(view1 -> {
            ShareUtils.shareTwitter(context, "Plus de publication sur Miioky", url, "Twitter", "Miioky", photo_id);
            dialog.dismiss();
        });

        linLayout_instagram.setOnClickListener(view1 -> {
            ShareUtils.shareInstagram(context, "Plus de publication sur Miioky", url, photo_id);
            dialog.dismiss();
        });

        linLayout_snapchat.setOnClickListener(view1 -> {
            ShareUtils.shareSnapchat(context, "Plus de publication sur Miioky", url, photo_id);
            dialog.dismiss();
        });

        dialog.setContentView(view);
    }

    // save photo in gallery
    private void addNewSavePhoto() {
        showLoading();
        DownloadImageFile videoFile = new DownloadImageFile();
        videoFile.execute(url);

        // to count number of downloading
        HashMap<String, Object> map = new HashMap<>();
        map.put(context.getString(R.string.field_user_id), user_id);

        String newKey = myRef.push().getKey();
        assert newKey != null;
        myRef.child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(photo_id)
                .child(newKey)
                .setValue(map);
    }

    // download image from URL in user gallery
    @SuppressLint("StaticFieldLeak")
    class DownloadImageFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... videoURL) {
            int count;
            Calendar calendar=Calendar.getInstance();
            try {
                URL url = new URL(videoURL[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), calendar.getTimeInMillis()+".jpg");
                MediaScannerConnection.scanFile(context, new String[] { file.getPath() }, new String[] { "image/jpg" }, null);
                OutputStream output = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    output = Files.newOutputStream(file.toPath());
                }

                byte[] data = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    assert output != null;
                    output.write(data, 0, count);
                }

                assert output != null;
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                progresDialog.dismiss();
                context.runOnUiThread(() -> Toast.makeText(context, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progresDialog.dismiss();
            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prevent BottomSheetDialogFragment covering navigation bar
     */
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}

