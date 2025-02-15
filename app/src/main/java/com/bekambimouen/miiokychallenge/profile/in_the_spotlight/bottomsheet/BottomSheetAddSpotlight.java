package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.bottomsheet.adapter.BottomSheetAddSpotlightAdapter;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;
import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class BottomSheetAddSpotlight extends BottomSheetDialogFragment {
    // widgets
    TextView button_add_spotlight;

    // vars
    private final AppCompatActivity context;
    private BottomSheetAddSpotlight mDialogFragment;
    private final StoriesProgressView storiesProgressView;
    private final String user_id;
    private final String storyid;
    private final String mediaUrl;
    private final String caption;
    private CustomShareProgressView mProgresDialog;

    private void showLoading(){
        if (mProgresDialog == null)
            mProgresDialog = new CustomShareProgressView(context);
        mProgresDialog.show();
    }

    // firebase
    private final DatabaseReference myRef;

    public BottomSheetAddSpotlight(AppCompatActivity context, StoriesProgressView storiesProgressView, String user_id,
                                   String storyid, String mediaUrl, String caption) {
        this.context = context;
        this.storiesProgressView = storiesProgressView;
        this.user_id = user_id;
        this.storyid = storyid;
        this.mediaUrl = mediaUrl;
        this.caption = caption;

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @Override
    public void onStart()
    {
        requireNonNull(requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet_add_spotlight, null);
        // widgets
        View view_id = view.findViewById(R.id.view);
        mDialogFragment = this;

        List<SpolightCover> storyList = new ArrayList<>();

        RelativeLayout relLayout_one_item = view.findViewById(R.id.relLayout_one_item);
        RelativeLayout barre_bottomsheet = view.findViewById(R.id.barre_bottomsheet);
        RelativeLayout relLayout_done = view.findViewById(R.id.relLayout_filter_done);
        CircleImageView cover_photo = view.findViewById(R.id.cover_photo);
        EditText add_title = view.findViewById(R.id.add_title);
        button_add_spotlight = view.findViewById(R.id.button_add_spotlight);
        RecyclerView recyclerview = view.findViewById(R.id.recycler_bottomsheet);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerview.setLayoutManager(manager);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_story_spotlight_cover))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    SpolightCover story = new SpolightCover();
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                    assert objectMap != null;
                    story.setUser_id(Objects.requireNonNull(objectMap.get(getString(R.string.field_user_id))).toString());
                    story.setMediaUrl(Objects.requireNonNull(objectMap.get(getString(R.string.field_mediaUrl))).toString());
                    story.setStoryid(Objects.requireNonNull(objectMap.get(getString(R.string.field_storyid))).toString());
                    story.setTitle(Objects.requireNonNull(objectMap.get(getString(R.string.field_title))).toString());

                    storyList.add(story);
                }

                if (storyList.isEmpty()) {
                    relLayout_one_item.setVisibility(View.VISIBLE);
                    button_add_spotlight.setVisibility(View.VISIBLE);
                    recyclerview.setVisibility(View.GONE);
                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .load(mediaUrl)
                                .into(cover_photo);
                    }

                    button_add_spotlight.setEnabled(true);
                    button_add_spotlight.setOnClickListener(view1 -> {
                        String title = add_title.getText().toString();
                        if (!TextUtils.isEmpty(title)) {
                            showLoading();
                            addPhotosToDatabase(title, mediaUrl);

                        } else {
                            Toast.makeText(context, context.getString(R.string.add_a_title), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Collections.reverse(storyList);
                    BottomSheetAddSpotlightAdapter adapter = new BottomSheetAddSpotlightAdapter(context, storyList, mDialogFragment,
                            storiesProgressView, user_id, storyid, mediaUrl, caption, recyclerview, relLayout_one_item, cover_photo,
                            button_add_spotlight, add_title, relLayout_done);
                    recyclerview.setAdapter(adapter);
                    recyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialog.setContentView(view);

        barre_bottomsheet.setOnClickListener(view1 -> {
            storiesProgressView.resume();
            dismiss();
        });

        // dismiss bottomsheet dialog
        view_id.setOnClickListener(view1 -> {
            storiesProgressView.resume();
            dismiss();
        });
    }

    private void addPhotosToDatabase(String title, String url) {
        String newPhotoKey = myRef.child(context.getString(R.string.dbname_story_spotlight)).push().getKey();

        // cover ____________________________________________________________________
        SpolightCover spolightCover = new SpolightCover();
        spolightCover.setUser_id(user_id);
        spolightCover.setMediaUrl(url);
        spolightCover.setStoryid(newPhotoKey);
        spolightCover.setTitle(title);

        assert newPhotoKey != null;
        myRef.child(context.getString(R.string.dbname_story_spotlight_cover))
                .child(user_id)
                .child(newPhotoKey)
                .setValue(spolightCover);
        // __________________________________________________________________________

        Story_spotlight spotlight = new Story_spotlight();
        spotlight.setMediaUrl(url);
        spotlight.setStoryid(storyid);
        spotlight.setUser_id(user_id);
        spotlight.setCaption(caption);
        // suppress
        spotlight.setSuppressed(false);

        myRef.child(context.getString(R.string.dbname_story_spotlight))
                .child(user_id)
                .child(newPhotoKey)
                .child(storyid)
                .setValue(spotlight)
                .addOnCompleteListener(task -> {
                    mProgresDialog.dismiss();
                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
                    mDialogFragment.dismiss();
                    storiesProgressView.resume();
                    button_add_spotlight.setEnabled(false);
                });
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        storiesProgressView.resume();
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

