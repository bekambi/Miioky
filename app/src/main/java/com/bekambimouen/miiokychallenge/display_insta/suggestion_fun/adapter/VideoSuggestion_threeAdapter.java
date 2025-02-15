package com.bekambimouen.miiokychallenge.display_insta.suggestion_fun.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.fun.FunSuggestions;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.preload_image.PreloadSuggestionVideos;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class VideoSuggestion_threeAdapter extends RecyclerView.Adapter<VideoSuggestion_threeAdapter.ViewHolder> {
    private static final String TAG = "VideoSuggestion_threeAdapter";

    private final AppCompatActivity context;
    private final ArrayList<BattleModel_fun> list;
    private final RelativeLayout relLayout_view_overlay;

    public VideoSuggestion_threeAdapter(AppCompatActivity context, ArrayList<BattleModel_fun> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_fun_suggestios_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BattleModel_fun model = list.get(position);

        try {
            PreloadSuggestionVideos.getPreloadSuggestionVideos(context, list.get(position+1));
            PreloadSuggestionVideos.getPreloadSuggestionVideos(context, list.get(position+2));
            PreloadSuggestionVideos.getPreloadSuggestionVideos(context, list.get(position+3));
            PreloadSuggestionVideos.getPreloadSuggestionVideos(context, list.get(position+4));
            PreloadSuggestionVideos.getPreloadSuggestionVideos(context, list.get(position+5));
            PreloadSuggestionVideos.getPreloadSuggestionVideos(context, list.get(position+6));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        holder.bindVideo(model);
        holder.itemView.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, FunSuggestions.class);
            intent.putExtra("myVideo", myGson);
            intent.putExtra("position", 13 + position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageview);
        }

        void bindVideo(BattleModel_fun model_fun) {
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model_fun.getThumbnail())
                    .placeholder(R.color.black_semi_transparen3)
                    .centerCrop()
                    .into(img);
        }
    }
}

