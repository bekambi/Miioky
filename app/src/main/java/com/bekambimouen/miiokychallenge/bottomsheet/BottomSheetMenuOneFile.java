package com.bekambimouen.miiokychallenge.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.publication_saved.ViewSavedPublications;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;


public class BottomSheetMenuOneFile extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetMenuOneFile";
    // vars
    private final AppCompatActivity context;
    private BattleModel battle_model;
    private ModelInvite model_invite;
    private String photo_id;

    public BottomSheetMenuOneFile(AppCompatActivity context) {
        this.context = context;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet, null);

        if (this.getArguments() != null) {
            battle_model = this.getArguments().getParcelable("battle_model");
        }
        if (this.getArguments() != null) {
            model_invite = this.getArguments().getParcelable("model_invite");
        }

        // widgets
        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);
        LinearLayout linLayout_report = view.findViewById(R.id.linLayout_report);
        LinearLayout linLayout_saved = view.findViewById(R.id.linLayout_saved);

        linLayout_view_bar.setOnClickListener(view1 -> dismiss());

        if (model_invite != null) {
            if (!TextUtils.isEmpty(model_invite.getInvite_photoID()))
                photo_id = model_invite.getInvite_photoID();

        } else if (battle_model != null) {
            if (!TextUtils.isEmpty(battle_model.getPhoto_id()))
                photo_id = battle_model.getPhoto_id();
            if (!TextUtils.isEmpty(battle_model.getPhotoi_id()))
                photo_id = battle_model.getPhotoi_id();
            if (!TextUtils.isEmpty(battle_model.getPhoto_id_un()))
                photo_id = battle_model.getPhoto_id_un();
            if (!TextUtils.isEmpty(battle_model.getReponse_photoID()))
                photo_id = battle_model.getReponse_photoID();
        }

        linLayout_saved.setOnClickListener(view1 -> {
            dismiss();
            Intent intent = new Intent(context, ViewSavedPublications.class);
            startActivity(intent);
        });

        BottomSheetSignaler mBottomSheetSignaler = new BottomSheetSignaler(context, battle_model, photo_id);
        linLayout_report.setOnClickListener(view1 -> {
            mBottomSheetSignaler.show(context.getSupportFragmentManager(), "TAG");
            dismiss();
        });

        dialog.setContentView(view);
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
