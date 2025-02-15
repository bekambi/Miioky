package com.bekambimouen.miiokychallenge.groups.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ViewHolder> {

    private final AppCompatActivity context;
    private final List<String> modelList;

    public PhotosRecyclerViewAdapter(AppCompatActivity context, List<String> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_group_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = modelList.get(position);
        holder.bindImageUne(url);
    }

    @Override
    public int getItemCount() {
        if(modelList==null) return 0;
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.main_image_une);
        }

        void bindImageUne(String url) {
            if (!context.isFinishing()) {
                Glide.with(context.getApplicationContext())
                        .load(url)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(img);
            }
        }
    }
}

