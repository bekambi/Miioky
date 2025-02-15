package com.bekambimouen.miiokychallenge.challenge_publication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

// classe appelante VideoFragment
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> {
    private final Context context;
    private final ArrayList<VideoItem> videoList;
    private final ProgressBar main_progressBar;
    private static OnVideoItemClickListener onItemClickListener;

    public VideoListAdapter(Context context, ArrayList<VideoItem> videoList, ProgressBar main_progressBar) {
        this.context = context;
        this.videoList = videoList;
        this.main_progressBar = main_progressBar;
    }

    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ivideo_list, parent, false);
        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {
        holder.duration.setText(videoList.get(position).getVideoDuration());
        Glide.with(context)
                .load(videoList.get(position).getVideo())
                .placeholder(R.color.white)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.image);

        holder.checkBox.setChecked(videoList.get(position).isSelected());

        if (main_progressBar != null && main_progressBar.getVisibility() == View.VISIBLE)
            main_progressBar.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        if(videoList==null) return 0;
        return videoList.size();
    }

    // video de la gallery
    public static class VideoListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CheckBox checkBox;
        TextView duration;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.video_list);
            duration = itemView.findViewById(R.id.videoDuration_video);
            checkBox = itemView.findViewById(R.id.circle_video);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getLayoutPosition(), v));
        }
    }

    public void setOnItemClickListener(OnVideoItemClickListener onItemClickListener) {
        VideoListAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnVideoItemClickListener {
        void onItemClick(int position, View v);
    }

}

