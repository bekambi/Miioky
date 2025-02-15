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
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.AcceptedChallenges;
import com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.ChallengesIResponded;
import com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.MyChallenges;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetSeeUserChallenges extends BottomSheetDialogFragment {

    // vars
    private final AppCompatActivity context;

    // firebase
    private final String user_id;

    public BottomSheetSeeUserChallenges(AppCompatActivity context, String user_id) {
        this.context = context;
        this.user_id = user_id;
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
                .inflate(R.layout.layout_bottomsheet_see_user_challenges, null);

        // LinearLayout
        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);

        TextView my_challenges = view.findViewById(R.id.my_challenges);
        TextView my_responses = view.findViewById(R.id.my_responses);

        linLayout_view_bar.setOnClickListener(view1 -> dismiss());

        my_challenges.setOnClickListener(view1 -> {
            dismiss();
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, AcceptedChallenges.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });

        my_responses.setOnClickListener(view1 -> {
            dismiss();
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ChallengesIResponded.class);
            intent.putExtra("from_view_profile_user_id", user_id);
            context.startActivity(intent);
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

