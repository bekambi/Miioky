package com.bekambimouen.miiokychallenge.challenges_view_all.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenges_view_all.ViewGridCategories;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GridCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "GridCategoriesAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    public void setOnLoadMoreItemsListener(OnLoadMoreItemsListener listener) {
        this.mOnLoadMoreItemsListener = listener;
    }

    private static final int IMAGE = 1;
    private static final int VIDEO = 2;
    private static final int GROUP_IMAGE = 3;
    private static final int GROUP_VIDEO = 4;

    private final AppCompatActivity context;
    private final ArrayList<ModelInvite> list;
    private final String category_status;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public GridCategoriesAdapter(AppCompatActivity context, ArrayList<ModelInvite> list,
                                 String category_status, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.category_status = category_status;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_imageune, parent, false);
            return new ImageItem(view);

        } else if (viewType == VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_videoune, parent, false);
            return new VideoItem(view);

        }if (viewType == GROUP_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_imageune, parent, false);
            return new ImageItem(view);

        } else  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_videoune, parent, false);
            return new VideoItem(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelInvite model = list.get(position);

        try {
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+1));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+2));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+3));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+4));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+5));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+6));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);
        if (itemViewType == IMAGE) {
            ImageItem imageItem = (ImageItem) holder;
            imageItem.bindImageUne(model);

            imageItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewGridCategories.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", category_status);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == VIDEO) {
            VideoItem videoItem = (VideoItem) holder;
            videoItem.bindImageUne(model);

            videoItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewGridCategories.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", category_status);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        }

        else if (itemViewType == GROUP_IMAGE) {
            ImageItem imageItem = (ImageItem) holder;
            imageItem.bindImageUne(model);

            imageItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewGridCategories.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", category_status);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_VIDEO) {
            VideoItem videoItem = (VideoItem) holder;
            videoItem.bindImageUne(model);

            videoItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewGridCategories.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", category_status);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        }

        // hide progressBar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData() {
        if (mOnLoadMoreItemsListener != null) {
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isImage())
            return IMAGE;
        else if (list.get(position).isVideo())
            return VIDEO;
        else if (list.get(position).isGroup_image())
            return GROUP_IMAGE;
        else
            return GROUP_VIDEO;
    }

    public class ImageItem extends RecyclerView.ViewHolder {
        ImageView img;
        public ImageItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(ModelInvite model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getInvite_url())
                    .placeholder(R.color.white)
                    .centerCrop()
                    .into(img);
        }
    }

    public class VideoItem extends RecyclerView.ViewHolder {
        ImageView img;
        public VideoItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.video_une);
        }

        void bindImageUne(ModelInvite model) {
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getThumbnail_invite())
                    .placeholder(R.color.white)
                    .centerCrop()
                    .into(img);
        }
    }
}

