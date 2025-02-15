package com.bekambimouen.miiokychallenge.market_place.bottomsheet;

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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.bottomsheet.adapter.SelectedStoreAdapter;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.publication.LocationPublication;
import com.bekambimouen.miiokychallenge.market_place.publication.SellInMarket;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BottomSheetMarketSell extends BottomSheetDialogFragment {

    // widgets
    private TextView tv_sell_in;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    // vars
    private final AppCompatActivity context;

    private List<MarketModel> list;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public BottomSheetMarketSell(AppCompatActivity context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // to expand completely layout
        requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheetInternal != null;
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
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
                .inflate(R.layout.layout_bottomsheet_market_select_store, null);

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        list = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);
        tv_sell_in = view.findViewById(R.id.tv_sell_in);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayout linLayout_view_bar = view.findViewById(R.id.linLayout_view_bar);
        LinearLayout linLayout_sell = view.findViewById(R.id.linLayout_sell);
        LinearLayout linLayout_rent = view.findViewById(R.id.linLayout_rent);

        linLayout_view_bar.setOnClickListener(view1 -> dismiss());

        linLayout_sell.setOnClickListener(view1 -> {
            dismiss();
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SellInMarket.class);
            context.startActivity(intent);
        });

        linLayout_rent.setOnClickListener(view1 -> {
            dismiss();
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, LocationPublication.class);
            context.startActivity(intent);
        });

        getUserStore(dialog);
        dialog.setContentView(view);
    }

    private void getUserStore(Dialog dialog) {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_stores))
                .child(user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);
                    if (!market_model.isSuppressed())
                        list.add(market_model);
                }

                Collections.reverse(list);

                SelectedStoreAdapter adapter = new SelectedStoreAdapter(context, list, dialog, progressBar);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    tv_sell_in.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

