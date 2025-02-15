package com.bekambimouen.miiokychallenge.story.publication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class StoryGridImageAdapter extends RecyclerView.Adapter<StoryGridImageAdapter.MyViewHolder> {

    private final AppCompatActivity context;
    private final ArrayList<ImageModel> imgURLs;
    private final ProgressBar main_progressBar;
    private static OnItemClickListener onItemClickListener;

    public StoryGridImageAdapter(AppCompatActivity context, ArrayList<ImageModel> imgURLs, ProgressBar main_progressBar) {
        this.context = context;
        this.imgURLs = imgURLs;
        this.main_progressBar = main_progressBar;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_grid_imageview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageModel url = imgURLs.get(position);
        holder.bindView(url);

        if (main_progressBar != null && main_progressBar.getVisibility() == View.VISIBLE)
            main_progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(imgURLs==null) return 0;
        return imgURLs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gridImageView);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getLayoutPosition(), v));
        }

        public void bindView(ImageModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getImage())
                    .placeholder(R.color.white)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(image);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        StoryGridImageAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }
}
