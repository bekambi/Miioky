package com.bekambimouen.miiokychallenge.market_place.publication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.interfaces.OnPicPhotoListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MarketSelectedImageAdapter extends RecyclerView.Adapter<MarketSelectedImageAdapter.ViewHolder> {

    // adaptateur du deuxiÃ¨me recyclerview
    private final AppCompatActivity context;
    private final ArrayList<String> stringArrayList;
    private final RelativeLayout relLayout_post_product;
    private final RelativeLayout relLayout_pic_photo;
    private final OnPicPhotoListener onPicPhotoListener;

    public MarketSelectedImageAdapter(AppCompatActivity context, ArrayList<String> stringArrayList,
                                      RelativeLayout relLayout_post_product, RelativeLayout relLayout_pic_photo,
                                      OnPicPhotoListener onPicPhotoListener) {
        this.context = context;
        this.stringArrayList = stringArrayList;
        this.relLayout_post_product = relLayout_post_product;
        this.relLayout_pic_photo = relLayout_pic_photo;
        this.onPicPhotoListener = onPicPhotoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_market_selected_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(stringArrayList.get(position))
                .placeholder(R.color.white)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(view -> {
            relLayout_post_product.setVisibility(View.GONE);
            relLayout_pic_photo.setVisibility(View.VISIBLE);
            onPicPhotoListener.isLayoutVisible(true);
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

