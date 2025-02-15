package com.bekambimouen.miiokychallenge.fun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoListFunAdapter extends RecyclerView.Adapter<VideoListFunAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<VideoItem> videoList;
    private final ProgressBar main_progressBar;
    private static OnVideoItemClickListener onItemClickListener;

    public VideoListFunAdapter(Context context, ArrayList<VideoItem> videoList, ProgressBar main_progressBar) {
        this.context = context;
        this.videoList = videoList;
        this.main_progressBar = main_progressBar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ivideo_gallery_fun, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.duration.setText(videoList.get(position).getVideoDuration());
        Glide.with(context)
                .asBitmap()
                .load(videoList.get(position).getVideo())
                .placeholder(R.color.white)
                .centerCrop()
                .into(holder.image);

        if (main_progressBar != null && main_progressBar.getVisibility() == View.VISIBLE)
            main_progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(videoList==null) return 0;
        return videoList.size();
    }

    public void setOnItemClickListener(OnVideoItemClickListener onItemClickListener) {
        VideoListFunAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnVideoItemClickListener {
        void onItemClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.video_list);
            duration = itemView.findViewById(R.id.videoDuration_video);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getLayoutPosition(), v));

        }
    }
}

