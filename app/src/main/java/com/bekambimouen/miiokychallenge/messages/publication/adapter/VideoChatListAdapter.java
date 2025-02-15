package com.bekambimouen.miiokychallenge.messages.publication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.VideoItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

// classe appelante VideoFragment
public class VideoChatListAdapter extends RecyclerView.Adapter<VideoChatListAdapter.VideoListViewHolder> {
    private final AppCompatActivity context;
    private final ArrayList<VideoItem> videoList;
    private static OnVideoItemClickListener onItemClickListener;

    public VideoChatListAdapter(AppCompatActivity context, ArrayList<VideoItem> videoList) {
        this.context = context;
        this.videoList = videoList;
    }


    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_videoview, parent, false);
        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {
        VideoItem videoItem = videoList.get(position);
        holder.bindView(videoItem);
    }

    @Override
    public int getItemCount() {
        if(videoList==null) return 0;
        return videoList.size();
    }

    // video de la gallery
    public class VideoListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView duration;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.video_list);
            duration = itemView.findViewById(R.id.videoDuration_video);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getLayoutPosition(), v));
        }

        public void bindView(VideoItem model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getVideo())
                    .placeholder(R.color.white)
                    .centerCrop()
                    .into(image);

            duration.setText(model.getVideoDuration());
        }
    }

    public void setOnItemClickListener(OnVideoItemClickListener onItemClickListener) {
        VideoChatListAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnVideoItemClickListener {
        void onItemClick(int position, View v);
    }

}

