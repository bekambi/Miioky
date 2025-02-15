package com.bekambimouen.miiokychallenge.challenge.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.publication_insta.model.FolderItem;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderResponseChallengeImagesAdapter extends RecyclerView.Adapter<FolderResponseChallengeImagesAdapter.ViewHolder> {
    private static final String TAG = "FolderAdapter";

    private final Context context;
    private final List<FolderItem> folderList;
    private final StoryGridImageAdapter imageAdapter;
    private final ArrayList<ImageModel> imageList;
    private final Dialog dialog;
    private final TextView toolBar_textview;
    private final ProgressBar progressBar;

    // vars
    private final String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

    public FolderResponseChallengeImagesAdapter(Context context, List<FolderItem> folderList, StoryGridImageAdapter imageAdapter,
                                                ArrayList<ImageModel> imageList, Dialog dialog, TextView toolBar_textview, ProgressBar progressBar) {
        this.context = context;
        this.folderList = folderList;
        this.imageAdapter = imageAdapter;
        this.imageList = imageList;
        this.dialog = dialog;
        this.toolBar_textview = toolBar_textview;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FolderItem folderItem = folderList.get(position);

        // Extract folder name from path
        String folderPath = folderItem.getFolderPath();
        File file = new File(folderPath);
        String folderName = file.getName(); // Get only the folder name

        // Set folder name to TextView
        holder.folderNameTextView.setText(folderName);

        holder.itemView.setOnClickListener(v -> {
            toolBar_textview.setText(folderName);
            dialog.dismiss();
            imageList.clear();
            String absolutePathOfImage;

            if (position == 0) {
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,null, null);

                try {
                    while (true) {
                        assert cursor != null;
                        if (!cursor.moveToNext()) break;
                        absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        ImageModel ImageModel = new ImageModel();
                        ImageModel.setImage(absolutePathOfImage);
                        imageList.add(ImageModel);
                    }
                    cursor.close();

                } catch (NullPointerException e) {
                    Log.d(TAG, "getAllImages: "+e.getMessage());
                }

            } else {
                String selection = MediaStore.Images.Media.DATA + " LIKE ?";
                String[] selectionArgs = new String[]{ folderPath + "%" };

                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);

                try {

                    while (true) {
                        assert cursor != null;
                        if (!cursor.moveToNext()) break;
                        absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        ImageModel imageModel = new ImageModel();
                        imageModel.setImage(absolutePathOfImage);
                        imageList.add(imageModel);
                    }
                    cursor.close();
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getAllImages: error: "+e.getMessage());
                }
            }

            Collections.reverse(imageList);
            imageAdapter.notifyDataSetChanged();
        });

        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderNameTextView = itemView.findViewById(R.id.folderNameTextView);
        }
    }
}
