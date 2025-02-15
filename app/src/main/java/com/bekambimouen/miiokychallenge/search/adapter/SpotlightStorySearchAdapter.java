package com.bekambimouen.miiokychallenge.search.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.StorySpotLightActivity;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpotlightStorySearchAdapter extends RecyclerView.Adapter<SpotlightStorySearchAdapter.ViewHolder> {
    private static final String TAG = "SpotlightStorySearchAdapter";

    private final AppCompatActivity context;
    private final List<SpolightCover> storyList;
    private final RelativeLayout relLayout_view_overlay;

    public SpotlightStorySearchAdapter(AppCompatActivity context, List<SpolightCover> storyList, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.storyList = storyList;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_story_spotlight_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotlightStorySearchAdapter.ViewHolder holder, int position) {
        SpolightCover spotlight = storyList.get(position);

        try {
            if (!context.isFinishing()) {
                Glide.with(context)
                        .load(spotlight.getMediaUrl())
                        .placeholder(R.drawable.cercle_vide_ala_une)
                        .into(holder.cover);
            }

            holder.title.setText(spotlight.getTitle());
            holder.itemView.setOnClickListener(view -> {
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

        } catch (NullPointerException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if(storyList==null) return 0;
        return storyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView cover;
        private final TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
        }
    }
}

