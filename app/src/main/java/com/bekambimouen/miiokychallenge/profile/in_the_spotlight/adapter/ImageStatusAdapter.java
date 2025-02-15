package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class ImageStatusAdapter extends RecyclerView.Adapter<ImageStatusAdapter.ViewHolder> {
    private static final String TAG = "ImageStatusAdapter";

    private final AppCompatActivity context;
    private final List<Story_spotlight> imageList;
    private final ProgressBar main_progressBar;
    private static OnItemClickListener onItemClickListener;

    public ImageStatusAdapter(AppCompatActivity context, List<Story_spotlight> imageList, ProgressBar main_progressBar) {
        this.context = context;
        this.imageList = imageList;
        this.main_progressBar = main_progressBar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_spotlight_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story_spotlight story = imageList.get(position);
        try {
            Glide.with(context)
                    .load(story.getMediaUrl())
                    .placeholder(R.color.white)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(holder.image);

            holder.checkBox.setChecked(story.isSelected());

        } catch (NullPointerException e) {
            Log.d(TAG, "onBindViewHolder: error: "+e.getMessage());
        }

        if (main_progressBar != null && main_progressBar.getVisibility() == View.VISIBLE)
            main_progressBar.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        if(imageList==null) return 0;
        return imageList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        ImageStatusAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.circle);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getLayoutPosition(), v));
        }
    }
}

