package com.bekambimouen.miiokychallenge.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.ShareUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class BottomSheetShare extends BottomSheetDialogFragment {
    // vars
    private final AppCompatActivity context;
    private final String url;
    private final String photo_id;

    public BottomSheetShare(AppCompatActivity context, String url, String photo_id) {
        this.context = context;
        this.url = url;
        this.photo_id = photo_id;
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
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet_share, null);

        LinearLayout linLayout_whatsapp = view.findViewById(R.id.linLayout_whatsapp);
        LinearLayout linLayout_facebook = view.findViewById(R.id.linLayout_facebook);
        LinearLayout linLayout_twitter = view.findViewById(R.id.linLayout_twitter);
        LinearLayout linLayout_snapchat = view.findViewById(R.id.linLayout_snapchat);
        LinearLayout linLayout_instagram = view.findViewById(R.id.linLayout_instagram);

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

    // Prevent BottomSheetDialogFragment covering navigation bar
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

