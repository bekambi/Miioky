package com.bekambimouen.miiokychallenge.groups.child_recyclerview_share.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class GroupBottomSheetAddRecyclerImageDescriptionShare extends BottomSheetDialogFragment {
    // widgets
    private MyEditText edit_add_description;

    // vars
    private final AppCompatActivity mContext;
    private final BattleModel model;
    private final String imgUrl;
    private final String caption_field;
    private final LinearLayout linLayout_add_description;
    private final TextView caption;

    private String description;

    // firebase
    private DatabaseReference myRef;

    public GroupBottomSheetAddRecyclerImageDescriptionShare(AppCompatActivity context, BattleModel model,
                                                            String imgUrl, String caption_field, LinearLayout linLayout_add_description,
                                                            TextView caption) {
        this.mContext = context;
        this.model = model;
        this.caption_field = caption_field;
        this.imgUrl = imgUrl;
        this.linLayout_add_description = linLayout_add_description;
        this.caption = caption;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet_add_recycler_description, null);

        assert Objects.requireNonNull(getDialog()).getWindow() != null;
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        publishDescription(view);
        return  view;
    }

    private void publishDescription(View view) {
        edit_add_description = view.findViewById(R.id.edit_add_description);
        edit_add_description.requestFocus();
        ImageView imageview = view.findViewById(R.id.imageview);
        RelativeLayout relLayout_send = view.findViewById(R.id.relLayout_send);

        if (!mContext.isFinishing()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(imageview);
        }

        relLayout_send.setOnClickListener(view1 -> {
            description = Objects.requireNonNull(edit_add_description.getText()).toString();
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(mContext, mContext.getString(R.string.you_must_write_comment), Toast.LENGTH_SHORT).show();
                return;
            }

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_group))
                    .child(model.getGroup_id())
                    .child(model.getPhoto_id())
                    .child(caption_field)
                    .setValue(description);

            //insert into photos node
            myRef.child(getString(R.string.dbname_group_media_share))
                    .child(model.getPhoto_id())
                    .child(caption_field)
                    .setValue(description).addOnCompleteListener(task -> {
                        edit_add_description.setText("");
                        linLayout_add_description.setVisibility(View.GONE);
                        caption.setText(description);
                        dismiss();
                    });
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

