package com.bekambimouen.miiokychallenge.publication_insta.adapter;

import android.content.Context;
import android.content.Intent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullChallengeImage;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {
    // adaptateur du deuxième recyclerview
    private final Context context;
    private final ArrayList<String> stringArrayList;
    private final ArrayList<String> url_list;

    public SelectedImageAdapter(Context context, ArrayList<String> stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;

        this.url_list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_selected_image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(stringArrayList.get(position))
                .placeholder(R.color.white)
                .centerCrop()
                .into(holder.image);

        holder.image.setOnClickListener(view -> {
            url_list.clear();
            url_list.addAll(stringArrayList);
            Intent intent = new Intent(context, SimpleFullChallengeImage.class);
            intent.putExtra("url_list", url_list);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(stringArrayList==null) return 0;
        return stringArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}

