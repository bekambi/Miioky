package com.bekambimouen.miiokychallenge.messages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.messages.model.ImageChatModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class ImageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ArrayList<ImageChatModel> imageList;
    private static OnItemClickListener onItemClickListener;
    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;

    public ImageChatAdapter(Context context, ArrayList<ImageChatModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // la liste des images
        if (viewType == IMAGE_LIST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_chat_list, parent, false);
            return new ImageListViewHolder(view);
        } else {
            // l'appareil photo
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_picker_list, parent, false);
            return new ImagePickerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < 1 ? IMAGE_PICKER : IMAGE_LIST;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == IMAGE_LIST) {
            final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;

            // pour rÃ©gler le pb de activity destroyed dÃ» au context du glide
            Glide.with(context)
                    .load(imageList.get(position).getImage())
                    .placeholder(R.color.white)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.image);

        } else {
            ImagePickerViewHolder viewHolder = (ImagePickerViewHolder) holder;
            viewHolder.image.setImageResource(imageList.get(position).getResImg());
            viewHolder.title.setText(imageList.get(position).getTitle());
        }

    }

    @Override
    public int getItemCount() {
        if(imageList==null) return 0;
        return imageList.size();
    }

    // image des photos
    public static class ImageListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            //itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getBindingAdapterPosition(), v));
        }
    }

    // image de l'appareil photo
    public static class ImagePickerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ImagePickerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            //itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getBindingAdapterPosition(), v));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        ImageChatAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

}

