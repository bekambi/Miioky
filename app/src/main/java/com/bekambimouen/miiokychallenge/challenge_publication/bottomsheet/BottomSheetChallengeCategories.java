package com.bekambimouen.miiokychallenge.challenge_publication.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.interfaces.PassCategoryListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class BottomSheetChallengeCategories extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetChallengeCategories";
    // interface
    private PassCategoryListener categoryListener;
    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_category;

    public BottomSheetChallengeCategories(AppCompatActivity context, RelativeLayout relLayout_category) {
        this.context = context;
        this.relLayout_category = relLayout_category;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            categoryListener = (PassCategoryListener) getActivity();
        } catch (Exception e) {
            Log.d(TAG, "onAttach: "+e.getMessage());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // to expand completely layout
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
            coordinatorLayout.getParent().requestLayout();
        });
        return super.onCreateView(inflater, container, savedInstanceState);
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
                .inflate(R.layout.layout_bottomsheet_challenge_categories, null);

        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);
        linLayout_view_bar.setOnClickListener(view1 -> dialog.dismiss());

        TextView love = view.findViewById(R.id.love);
        TextView beauty = view.findViewById(R.id.beauty);
        TextView acapella = view.findViewById(R.id.acapella);
        TextView dance = view.findViewById(R.id.dance);
        TextView comedy = view.findViewById(R.id.comedy);
        TextView swag = view.findViewById(R.id.swag);
        TextView decoration = view.findViewById(R.id.decoration);
        TextView couple = view.findViewById(R.id.couple);
        TextView cinema = view.findViewById(R.id.cinema);
        TextView emotions = view.findViewById(R.id.emotions);
        TextView art = view.findViewById(R.id.art);
        TextView sport = view.findViewById(R.id.sport);
        TextView games = view.findViewById(R.id.games);
        TextView vehicle = view.findViewById(R.id.vehicle);
        TextView accessories = view.findViewById(R.id.accessories);
        TextView kitchen = view.findViewById(R.id.kitchen);
        TextView interior_decoration = view.findViewById(R.id.interior_decoration);
        TextView accommodation = view.findViewById(R.id.accommodation);
        TextView restoration = view.findViewById(R.id.restoration);
        TextView journey = view.findViewById(R.id.journey);
        TextView animals = view.findViewById(R.id.animals);
        TextView entertainment = view.findViewById(R.id.entertainment);
        TextView event = view.findViewById(R.id.event);

        love.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(love.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        beauty.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(beauty.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        acapella.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(acapella.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        dance.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(dance.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        comedy.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(comedy.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        swag.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(swag.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        decoration.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(decoration.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        couple.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(couple.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        cinema.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(cinema.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        emotions.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(emotions.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        art.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(art.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        sport.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(sport.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        games.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(games.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        vehicle.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(vehicle.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        accessories.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(accessories.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        kitchen.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(kitchen.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        interior_decoration.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(interior_decoration.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        accommodation.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(accommodation.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        restoration.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(restoration.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        journey.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(journey.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        animals.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(animals.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        entertainment.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(entertainment.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        event.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onSendData(event.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        dialog.setContentView(view);
    }

    // change the stroke color
    private void getStrokeChangeColor() {
        GradientDrawable drawable = (GradientDrawable) relLayout_category.getBackground();
        drawable.mutate();
        drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));
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

