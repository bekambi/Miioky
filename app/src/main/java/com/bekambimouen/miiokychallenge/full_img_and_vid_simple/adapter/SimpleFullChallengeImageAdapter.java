package com.bekambimouen.miiokychallenge.full_img_and_vid_simple.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class SimpleFullChallengeImageAdapter extends RecyclerView.Adapter<SimpleFullChallengeImageAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final ArrayList<String> url_list;

    public SimpleFullChallengeImageAdapter(AppCompatActivity context, ArrayList<String> url_list) {
        this.context = context;
        this.url_list = url_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_full_img_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = url_list.get(position);
        holder.bind(url);
    }

    @Override
    public int getItemCount() {
        return url_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final PhotoView iv_photo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
        }

        private void bind(String url) {
            Glide.with(context.getApplicationContext())
                    .load(url)
                    .placeholder(R.color.black)
                    .into(iv_photo);
        }
    }
}

