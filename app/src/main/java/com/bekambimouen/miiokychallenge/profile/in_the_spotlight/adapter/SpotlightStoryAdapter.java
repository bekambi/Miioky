package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.StatusGallery;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.StorySpotLightActivity;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpotlightStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // constants
    private static final int ICON_ADD = 1;
    private static final int ITEM = 2;

    private final AppCompatActivity context;
    private final List<SpolightCover> storyList;
    private final RelativeLayout relLayout_view_overlay;

    public SpotlightStoryAdapter(AppCompatActivity context, List<SpolightCover> storyList, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.storyList = storyList;
        this.relLayout_view_overlay = relLayout_view_overlay;

        this.storyList.remove(null);
        this.storyList.add(0, null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ICON_ADD) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_story_add_spotlight_icon,parent,false);
            return new AddIcon(view);

        } else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_story_spotlight_item,parent,false);
            return new Item(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemViewType = getItemViewType(position);

        SpolightCover spotlight = storyList.get(position);

        if (itemViewType == ICON_ADD) {
            AddIcon addIcon = (AddIcon) holder;
            addIcon.iv_add.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                context.startActivity(new Intent(context, StatusGallery.class));
            });

        } else if (itemViewType == ITEM) {
            Item item = (Item) holder;
            if (!context.isFinishing()) {
                Glide.with(context)
                        .load(spotlight.getMediaUrl())
                        .placeholder(R.drawable.cercle_vide_ala_une)
                        .into(item.cover);
            }

            item.title.setText(spotlight.getTitle());
            item.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, StorySpotLightActivity.class);
                intent.putExtra("user_id", spotlight.getUser_id());
                intent.putExtra("storyID", spotlight.getStoryid());
                intent.putExtra("cover_url", spotlight.getMediaUrl());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        if(storyList==null) return 0;
        return storyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ICON_ADD;
        else
            return ITEM;
    }

    public static class AddIcon extends RecyclerView.ViewHolder {
        ImageView iv_add;
        public AddIcon(@NonNull View itemView) {
            super(itemView);
            iv_add = itemView.findViewById(R.id.iv_add);
        }
    }

    public static class Item extends RecyclerView.ViewHolder {
        private final CircleImageView cover;
        private final TextView title;
        public Item(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
        }
    }
}

