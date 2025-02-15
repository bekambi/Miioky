package com.bekambimouen.miiokychallenge.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.editprofile.ChooseProfilePhoto;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BottomSheetUpdateProfile extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetUpdateProfile";

    // vars
    private final AppCompatActivity context;
    private final String userID;
    private final String userProfileUrl;

    private CustomShareProgressView progresDialog;
    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.setCancelable(false);
        progresDialog.show();
    }

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public BottomSheetUpdateProfile(AppCompatActivity context, String userID, String userProfileUrl) {
        this.context = context;
        this.userID = userID;
        this.userProfileUrl = userProfileUrl;

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
        requireNonNull(requireNonNull(getDialog()).getWindow())
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
                .inflate(R.layout.layout_bottomsheet_update_profile, null);

        // LinearLayout
        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);

        LinearLayout linLayout_change_profile_photo = view.findViewById(R.id.linLayout_change_profile_photo);
        LinearLayout linLayout_view_profile_picture = view.findViewById(R.id.linLayout_view_profile_picture);
        LinearLayout linLayout_publish_as_story = view.findViewById(R.id.linLayout_publish_as_story);

        linLayout_view_bar.setOnClickListener(view1 -> dismiss());

        linLayout_change_profile_photo.setOnClickListener(view1 -> {
            dismiss();
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ChooseProfilePhoto.class);
            context.startActivity(intent);
        });

        linLayout_view_profile_picture.setOnClickListener(view1 -> {
            dismiss();

            Query query = myRef
                    .child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(userID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        if (!user.getProfileUrl().isEmpty()) {
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                            intent.putExtra("img_url", user.getFull_profileUrl());
                            context.startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        linLayout_publish_as_story.setOnClickListener(view1 -> {
            dismiss();
            // show dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View v12 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = v12.findViewById(R.id.dialog_title);
            TextView dialog_text = v12.findViewById(R.id.dialog_text);
            TextView negativeButton = v12.findViewById(R.id.tv_no);
            TextView positiveButton = v12.findViewById(R.id.tv_yes);
            builder.setView(v12);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.cancel));
            positiveButton.setText(context.getString(R.string.publisher));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.publisher)));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_want_to_post_your_profile_picture_as_tory)));

            negativeButton.setOnClickListener(view2 -> d.dismiss());
            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();
                showLoading();
                setProfilePhoto(userProfileUrl);
            });
        });

        dialog.setContentView(view);
    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.dbname_story))
                .child(user_id);

        String storyid = reference.push().getKey();
        long timeend = System.currentTimeMillis()+86400000; // 1 day later

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("mediaUrl", url);
        hashMap.put("timestart", ServerValue.TIMESTAMP);
        hashMap.put("timeend", timeend);
        hashMap.put("storyid", storyid);
        hashMap.put("user_id", user_id);
        hashMap.put("caption", "");

        assert storyid != null;
        reference.child(storyid).setValue(hashMap)
                .addOnCompleteListener(task -> {
                    progresDialog.dismiss();
                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
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

