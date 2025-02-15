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

public class BottomSheetMarketCategory  extends BottomSheetDialogFragment {
    // interface
    private final CategoryListener categoryListener;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_category;

    public BottomSheetMarketCategory(AppCompatActivity context, RelativeLayout relLayout_category, CategoryListener categoryListener) {
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
                .inflate(R.layout.layout_bottomsheet_market_category, null);

        // LinearLayout
        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);
        LinearLayout linLayout_vehicles = view.findViewById(R.id.linLayout_vehicles);
        LinearLayout linLayout_to_rent_out = view.findViewById(R.id.linLayout_to_rent_out);
        LinearLayout linLayout_women_clothing_and_shoes = view.findViewById(R.id.linLayout_women_clothing_and_shoes);
        LinearLayout linLayout_men_clothing_and_shoes = view.findViewById(R.id.linLayout_men_clothing_and_shoes);
        LinearLayout linLayout_baby = view.findViewById(R.id.linLayout_baby);
        LinearLayout linLayout_furniture = view.findViewById(R.id.linLayout_furniture);
        LinearLayout linLayout_electronic_appliances = view.findViewById(R.id.linLayout_electronic_appliances);
        LinearLayout linLayout_home_appliances = view.findViewById(R.id.linLayout_home_appliances);
        LinearLayout linLayout_antiques_and_collectibles = view.findViewById(R.id.linLayout_antiques_and_collectibles);
        LinearLayout linLayout_art_and_crafts = view.findViewById(R.id.linLayout_art_and_crafts);
        LinearLayout linLayout_car_parts = view.findViewById(R.id.linLayout_car_parts);
        LinearLayout linLayout_book_film_and_music = view.findViewById(R.id.linLayout_book_film_and_music);
        LinearLayout linLayout_garage_sale = view.findViewById(R.id.linLayout_garage_sale);
        LinearLayout linLayout_health_and_beauty = view.findViewById(R.id.linLayout_health_and_beauty);
        LinearLayout linLayout_interior_items_and_decoration = view.findViewById(R.id.linLayout_interior_items_and_decoration);
        LinearLayout linLayout_interior_design_and_tools = view.findViewById(R.id.linLayout_interior_design_and_tools);
        LinearLayout linLayout_sale_of_housing = view.findViewById(R.id.linLayout_sale_of_housing);
        LinearLayout linLayout_jewelry_and_watches = view.findViewById(R.id.linLayout_jewelry_and_watches);
        LinearLayout linLayout_luggage_and_bags = view.findViewById(R.id.linLayout_luggage_and_bags);
        LinearLayout linLayout_miscellaneous = view.findViewById(R.id.linLayout_miscellaneous);
        LinearLayout linLayout_musical_instruments = view.findViewById(R.id.linLayout_musical_instruments);
        LinearLayout linLayout_patio_and_garden = view.findViewById(R.id.linLayout_patio_and_garden);
        LinearLayout linLayout_animal_product = view.findViewById(R.id.linLayout_animal_product);
        LinearLayout linLayout_sport_stuff = view.findViewById(R.id.linLayout_sport_stuff);
        LinearLayout linLayout_game_and_toys = view.findViewById(R.id.linLayout_game_and_toys);

        // TextView
        TextView vehicles = view.findViewById(R.id.vehicles);
        TextView to_rent_out = view.findViewById(R.id.to_rent_out);
        TextView women_clothing_and_shoes = view.findViewById(R.id.women_clothing_and_shoes);
        TextView men_clothing_and_shoes = view.findViewById(R.id.men_clothing_and_shoes);
        TextView baby = view.findViewById(R.id.baby);
        TextView furniture = view.findViewById(R.id.furniture);
        TextView electronic_appliances = view.findViewById(R.id.electronic_appliances);
        TextView home_appliances = view.findViewById(R.id.home_appliances);
        TextView antiques_and_collectibles = view.findViewById(R.id.antiques_and_collectibles);
        TextView art_and_crafts = view.findViewById(R.id.art_and_crafts);
        TextView car_parts = view.findViewById(R.id.car_parts);
        TextView book_film_and_music = view.findViewById(R.id.book_film_and_music);
        TextView garage_sale = view.findViewById(R.id.garage_sale);
        TextView health_and_beauty = view.findViewById(R.id.health_and_beauty);
        TextView interior_items_and_decoration = view.findViewById(R.id.interior_items_and_decoration);
        TextView interior_design_and_tools = view.findViewById(R.id.interior_design_and_tools);
        TextView sale_of_housing = view.findViewById(R.id.sale_of_housing);
        TextView jewelry_and_watches = view.findViewById(R.id.jewelry_and_watches);
        TextView luggage_and_bags = view.findViewById(R.id.luggage_and_bags);
        TextView miscellaneous = view.findViewById(R.id.miscellaneous);
        TextView musical_instruments = view.findViewById(R.id.musical_instruments);
        TextView patio_and_garden = view.findViewById(R.id.patio_and_garden);
        TextView animal_product = view.findViewById(R.id.animal_product);
        TextView sport_stuff = view.findViewById(R.id.sport_stuff);
        TextView game_and_toys = view.findViewById(R.id.game_and_toys);

        linLayout_vehicles.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(vehicles.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_to_rent_out.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(to_rent_out.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_women_clothing_and_shoes.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(women_clothing_and_shoes.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_men_clothing_and_shoes.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(men_clothing_and_shoes.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_baby.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(baby.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_furniture.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(furniture.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_electronic_appliances.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(electronic_appliances.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_home_appliances.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(home_appliances.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_antiques_and_collectibles.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(antiques_and_collectibles.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_art_and_crafts.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(art_and_crafts.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_car_parts.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(car_parts.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_book_film_and_music.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(book_film_and_music.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_garage_sale.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(garage_sale.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_health_and_beauty.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(health_and_beauty.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_interior_items_and_decoration.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(interior_items_and_decoration.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_interior_design_and_tools.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(interior_design_and_tools.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_sale_of_housing.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(sale_of_housing.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_jewelry_and_watches.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(jewelry_and_watches.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_luggage_and_bags.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(luggage_and_bags.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_miscellaneous.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(miscellaneous.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_musical_instruments.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(musical_instruments.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_patio_and_garden.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(patio_and_garden.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_animal_product.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(animal_product.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_sport_stuff.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(sport_stuff.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_game_and_toys.setOnClickListener(view1 -> {
            dismiss();
            categoryListener.onCategory(game_and_toys.getText().toString());
            // change the stroke color
            getStrokeChangeColor();
        });

        linLayout_view_bar.setOnClickListener(view1 -> dismiss());

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

