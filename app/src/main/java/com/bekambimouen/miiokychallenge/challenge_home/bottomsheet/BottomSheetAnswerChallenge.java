package com.bekambimouen.miiokychallenge.challenge_home.bottomsheet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.GoResponseChallengePhoto;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.fun.publication.CameraVideoFun;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ModelInvite;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BottomSheetAnswerChallenge extends BottomSheetDialogFragment {

    // Camera Permissions
    private final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // widgets
    private LinearLayout linLayout_answer_challenge;
    private final String video;
    private final String url;
    private final boolean isMiiokyImage;
    private final boolean isMiiokyVideo;
    private final boolean isGroupImage;
    private final boolean isGroupVideo;

    // vars
    private final AppCompatActivity context;
    private final BattleModel battleModel;

    public BottomSheetAnswerChallenge(AppCompatActivity context, BattleModel battleModel, String video, String url,
                                      boolean isMiiokyImage, boolean isMiiokyVideo, boolean isGroupImage, boolean isGroupVideo) {
        this.context = context;
        this.battleModel = battleModel;
        this.video = video;
        this.url = url;
        this.isMiiokyImage = isMiiokyImage;
        this.isMiiokyVideo = isMiiokyVideo;
        this.isGroupImage = isGroupImage;
        this.isGroupVideo = isGroupVideo;
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
                .inflate(R.layout.layout_bottomsheet_answer_challenge, null);

        // widgets
        ImageView thumbnail = view.findViewById(R.id.thumbnail);
        TextView category = view.findViewById(R.id.category);
        RelativeLayout relLayout_img_play_un = view.findViewById(R.id.relLayout_img_play_un);
        linLayout_answer_challenge = view.findViewById(R.id.linLayout_answer_challenge);

        if (video != null)
            relLayout_img_play_un.setVisibility(View.VISIBLE);
        else
            relLayout_img_play_un.setVisibility(View.GONE);

        Glide.with(context.getApplicationContext())
                .load(url)
                .into(thumbnail);

        // check permission
        if (!allPermissionsGranted()) {
            int REQUEST_CODE_CAMERA = 101;
            ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();

        } else {
            if (isMiiokyImage || isMiiokyVideo) {
                Query query = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_invite_battle))
                        .child(battleModel.getInvite_userID())
                        .orderByChild(context.getString(R.string.field_invite_photoID))
                        .equalTo(battleModel.getInvite_photoID());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) ds.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            category.setText(model.getInvite_category());

                            if (isMiiokyImage) {
                                linLayout_answer_challenge.setOnClickListener(view1 -> {
                                    dismiss();
                                    Intent intent = new Intent(context, GoResponseChallengePhoto.class);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(model);
                                    intent.putExtra("model_invite", myGson);
                                    intent.putExtra("category_status", model.getInvite_category_status());
                                    intent.putExtra("from_home_challenge", "from_home_challenge");
                                    context.startActivity(intent);
                                });

                            } else {
                                linLayout_answer_challenge.setOnClickListener(view1 -> {
                                    dismiss();
                                    Intent intent = new Intent(context, CameraVideoFun.class);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(model);
                                    intent.putExtra("model_invite", myGson);
                                    intent.putExtra("category_status", model.getInvite_category_status());
                                    intent.putExtra("from_challenge", "from_challenge");
                                    intent.putExtra("from_home_challenge", "from_home_challenge");
                                    context.startActivity(intent);
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            if (isGroupImage || isGroupVideo) {
                Query query2 = FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_invitation_challenge))
                        .child(battleModel.getGroup_id())
                        .orderByChild(context.getString(R.string.field_invite_photoID))
                        .equalTo(battleModel.getInvite_photoID());
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) ds.getValue();
                            assert objectMap != null;
                            ModelInvite model = Util_ModelInvite.getModelInvite(context, objectMap);

                            category.setText(model.getInvite_category());

                            if (isGroupImage) {
                                linLayout_answer_challenge.setOnClickListener(view -> {
                                    dismiss();
                                    Intent intent = new Intent(context, GoResponseChallengePhoto.class);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(model);
                                    intent.putExtra("model_invite", myGson);
                                    intent.putExtra("category_status", model.getInvite_category_status());
                                    intent.putExtra("from_group_challenge", "from_group_challenge");
                                    intent.putExtra("from_home_challenge", "from_home_challenge");
                                    context.startActivity(intent);
                                });

                            } else {
                                linLayout_answer_challenge.setOnClickListener(view -> {
                                    dismiss();
                                    Intent intent = new Intent(context, CameraVideoFun.class);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(model);
                                    intent.putExtra("model_invite", myGson);
                                    intent.putExtra("category_status", model.getInvite_category_status());
                                    intent.putExtra("from_challenge", "from_challenge"); // necessary
                                    intent.putExtra("from_group_challenge", "from_group_challenge"); // necessary
                                    intent.putExtra("from_home_challenge", "from_home_challenge");
                                    context.startActivity(intent);

                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        dialog.setContentView(view);

        DisplayMetrics displayMetrics = requireActivity().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int maxHeight = (int) (height*0.44); //custom height of bottom sheet

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        assert behavior != null;
        ((BottomSheetBehavior<?>) behavior).setPeekHeight(maxHeight);  //changed default peek height of bottom sheet

        ((BottomSheetBehavior<?>) behavior).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                String state = "";
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        state = "DRAGGING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {
                        state = "SETTLING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        state = "EXPANDED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        state = "COLLAPSED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        dismiss();
                        state = "HIDDEN";
                        break;
                    }
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
                Log.i("BottomSheetFrag", "onStateChanged: " + state);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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

