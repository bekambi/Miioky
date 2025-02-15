package com.bekambimouen.miiokychallenge.publication_insta.adapter;

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
import com.bekambimouen.miiokychallenge.challenge_publication.adapter.VideoListAdapter;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bekambimouen.miiokychallenge.publication_insta.model.FolderItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderVideosAdapter extends RecyclerView.Adapter<FolderVideosAdapter.ViewHolder> {
    private static final String TAG = "FolderAdapter";

    private final Context context;
    private final List<FolderItem> folderList;
    private final VideoListAdapter videoAdapter;
    private final ArrayList<VideoItem> videosList;
    private final Dialog dialog;
    private final TextView toolBar_textview;
    private final ProgressBar progressBar;

    // vars
    private final String[] projection = {MediaStore.Video.VideoColumns.DATA ,MediaStore.Video.Media.DISPLAY_NAME, MediaStore.MediaColumns.DURATION};

    public FolderVideosAdapter(Context context, List<FolderItem> folderList, VideoListAdapter videoAdapter,
                               ArrayList<VideoItem> videosList, Dialog dialog, TextView toolBar_textview,
                               ProgressBar progressBar) {
        this.context = context;
        this.folderList = folderList;
        this.videoAdapter = videoAdapter;
        this.videosList = videosList;
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
            videosList.clear();
            String absolutePathOfImage;

            if (position == 0) {
                Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                assert cursor != null;
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                try {

                    while (cursor.moveToNext()) {
                        absolutePathOfImage = cursor.getString(column_index_data);
                        @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION));
                        VideoItem VideoModel = new VideoItem();
                        VideoModel.setVideo(absolutePathOfImage);
                        VideoModel.setVideoDuration(timeConversion(Long.parseLong(duration)));
                        videosList.add(VideoModel);
                    }
                    cursor.close();
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getAllVideos: error: "+e.getMessage());
                }

            } else {
                String selection = MediaStore.Video.Media.DATA + " LIKE ?";
                String[] selectionArgs = new String[]{ folderPath + "%" };

                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);

                try {
                    while (true) {
                        assert cursor != null;
                        if (!cursor.moveToNext()) break;
                        absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION));
                        VideoItem videoItem = new VideoItem();
                        videoItem.setVideo(absolutePathOfImage);
                        videoItem.setVideoDuration(timeConversion(Long.parseLong(duration)));
                        videosList.add(videoItem);
                    }
                    cursor.close();
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getAllVideos: error: "+e.getMessage());
                }
            }

            Collections.reverse(videosList);
            videoAdapter.notifyDataSetChanged();

        });

        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    //time conversion
    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
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
