package com.bekambimouen.miiokychallenge.groups.publication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class GroupImageAdapter extends RecyclerView.Adapter<GroupImageAdapter.ImageListViewHolder> {
    private final Context context;
    private final ArrayList<ImageModel> imageList;
    private final ProgressBar main_progressBar;
    private static OnItemClickListener onItemClickListener;

    public GroupImageAdapter(Context context, ArrayList<ImageModel> imageList, ProgressBar main_progressBar) {
        this.context = context;
        this.imageList = imageList;
        this.main_progressBar = main_progressBar;
    }

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_image_list, parent, false);
        return new ImageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {
        ImageModel model = imageList.get(position);

        Glide.with(context)
                .load(model.getImage())
                .placeholder(R.color.white)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(holder.image);

        holder.checkBox.setChecked(model.isSelected());

        if (main_progressBar != null && main_progressBar.getVisibility() == View.VISIBLE)
            main_progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(imageList==null) return 0;
        return imageList.size();
    }

    // image de la gallery
    public static class ImageListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CheckBox checkBox;

        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.circle);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getLayoutPosition(), v));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        GroupImageAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }
}

