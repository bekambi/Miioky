package com.bekambimouen.miiokychallenge.Utils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class FullImageAdapter extends RecyclerView.Adapter<FullImageAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<String> url_list;
    private final LinearLayout linLayout_all_data;
    private final ImageView menu_item;

    public FullImageAdapter(AppCompatActivity context, List<String> url_list,
                            LinearLayout linLayout_all_data, ImageView menu_item) {
        this.context = context;
        this.url_list = url_list;
        this.linLayout_all_data = linLayout_all_data;
        this.menu_item = menu_item;
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

        // vars
        private boolean isShown = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
        }

        private void bind(String url) {
            if (!context.isFinishing()) {
                Glide.with(context)
                        .load(url)
                        .placeholder(R.color.black)
                        .into(iv_photo);

                iv_photo.setOnClickListener(view -> {
                    if (isShown) {
                        linLayout_all_data.setVisibility(View.GONE);
                        menu_item.setVisibility(View.GONE);
                        isShown = false;
                    } else {
                        linLayout_all_data.setVisibility(View.VISIBLE);
                        menu_item.setVisibility(View.VISIBLE);
                        isShown = true;
                    }
                });
            }
        }
    }
}

