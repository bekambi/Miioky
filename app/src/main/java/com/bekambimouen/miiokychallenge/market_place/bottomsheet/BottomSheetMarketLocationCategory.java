package com.bekambimouen.miiokychallenge.market_place.bottomsheet;

import static java.util.Objects.requireNonNull;

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
import com.bekambimouen.miiokychallenge.interfaces.CategoryListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class BottomSheetMarketLocationCategory extends BottomSheetDialogFragment {
    // interface
    private final CategoryListener categoryListener;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_category;

    public BottomSheetMarketLocationCategory(AppCompatActivity context, RelativeLayout relLayout_category, CategoryListener categoryListener) {
        this.context = context;
        this.relLayout_category = relLayout_category;
        this.categoryListener = categoryListener;
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
                .inflate(R.layout.layout_bottomsheet_market_location_category, null);

        // LinearLayout
        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);

        LinearLayout linLayout_accommodation = view.findViewById(R.id.linLayout_accommodation);
        TextView accommodation = view.findViewById(R.id.accommodation);
        LinearLayout linLayout_vehicle = view.findViewById(R.id.linLayout_vehicle);
        TextView vehicle = view.findViewById(R.id.vehicle);
        LinearLayout linLayout_machine = view.findViewById(R.id.linLayout_machine);
        TextView machine = view.findViewById(R.id.machine);
        LinearLayout linLayout_real_estate = view.findViewById(R.id.linLayout_real_estate);
        TextView real_estate = view.findViewById(R.id.real_estate);
        LinearLayout linLayout_decorative_objects = view.findViewById(R.id.linLayout_decorative_objects);
        TextView decorative_objects = view.findViewById(R.id.decorative_objects);
        LinearLayout linLayout_event_equipment = view.findViewById(R.id.linLayout_event_equipment);
        TextView event_equipment = view.findViewById(R.id.event_equipment);
        LinearLayout linLayout_device = view.findViewById(R.id.linLayout_device);
        TextView device = view.findViewById(R.id.device);
        LinearLayout linLayout_article = view.findViewById(R.id.linLayout_article);
        TextView article = view.findViewById(R.id.article);

        linLayout_view_bar.setOnClickListener(view1 -> dismiss());

        linLayout_accommodation.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(accommodation.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_vehicle.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(vehicle.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_machine.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(machine.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_real_estate.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(real_estate.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_decorative_objects.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(decorative_objects.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_event_equipment.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(event_equipment.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_device.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(device.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_article.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(article.getText().toString());
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

