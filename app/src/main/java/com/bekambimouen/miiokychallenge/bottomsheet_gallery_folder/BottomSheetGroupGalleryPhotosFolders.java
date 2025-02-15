package com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.FolderGroupImagesAdapter;
import com.bekambimouen.miiokychallenge.groups.publication.adapter.GroupImageAdapter;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.publication_insta.model.FolderItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BottomSheetGroupGalleryPhotosFolders extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetGalleyPhotoFolders";

    // vars
    private final List<FolderItem> folderList = new ArrayList<>();
    private final Set<String> folderPaths = new HashSet<>();
    private FolderGroupImagesAdapter folderImagesAdapter;
    private final String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

    private final AppCompatActivity context;
    private final GroupImageAdapter imageAdapter;
    private final ArrayList<ImageModel> imageList;
    private final TextView toolBar_textview;

    public BottomSheetGroupGalleryPhotosFolders(AppCompatActivity context, GroupImageAdapter imageAdapter
            , ArrayList<ImageModel> imageList, TextView toolBar_textview) {
        this.context = context;
        this.imageAdapter = imageAdapter;
        this.imageList = imageList;
        this.toolBar_textview = toolBar_textview;

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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_personalized_images_gallery, null);

        new Handler().postDelayed(() -> {
            RelativeLayout iv_arrowBack_principal = view.findViewById(R.id.iv_arrowBack_principal);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);

            RecyclerView recyclerView = view.findViewById(R.id.RecyclerView_video);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            folderList.add(0, new FolderItem(context.getString(R.string.gallery)));
            folderImagesAdapter = new FolderGroupImagesAdapter(context, folderList, imageAdapter, imageList, dialog, toolBar_textview,
                    progressBar);
            recyclerView.setAdapter(folderImagesAdapter);

            iv_arrowBack_principal.setOnClickListener(v -> {
                dialog.dismiss();
            });
            getAllVideos();

        }, 1000);

        dialog.setContentView(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getAllVideos() {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        assert cursor != null;

        try {
            while (cursor.moveToNext()) {
                // Extract folder path using the Cursor
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                String absolutePathOfImage = cursor.getString(column_index_data);
                File videoFile = new File(absolutePathOfImage);
                String folderPath = videoFile.getParent();

                // Add folder path to the set if it's not already present
                if (folderPaths.add(folderPath)) {
                    // Create FolderItem and add to folderList
                    FolderItem folderItem = new FolderItem(folderPath);
                    folderList.add(folderItem);
                }
            }
            cursor.close();

            // Update the adapter after retrieving folders
            folderImagesAdapter.notifyDataSetChanged();
        } catch (NumberFormatException e) {
            Log.d(TAG, "getAllVideos: error: "+e.getMessage());
        }
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
