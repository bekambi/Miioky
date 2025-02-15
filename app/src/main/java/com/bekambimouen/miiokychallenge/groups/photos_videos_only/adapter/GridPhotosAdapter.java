package com.bekambimouen.miiokychallenge.groups.photos_videos_only.adapter;

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
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.GroupViewOnlyPhotos;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GridPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ExploreGridAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final int GROUP_RECYCLER_ITEM = 1;
    private static final int GROUP_IMAGE_UNE = 2;
    private static final int GROUP_COVER_IMAGE = 3;
    private static final int GROUP_COVER_BACK_PROFILE = 4;
    private static final int GROUP_IMAGE_DOUBLE = 5;
    private static final int GROUP_RESPONSE_IMG_DOUBLE = 6;

    private final AppCompatActivity context;
    private final ArrayList<BattleModel> list;
    private final UserGroups user_groups;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public GridPhotosAdapter(AppCompatActivity context, ArrayList<BattleModel> list, UserGroups user_groups,
                             ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.user_groups = user_groups;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == GROUP_COVER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new GroupGridCoverImage(view);

        } else if (viewType == GROUP_COVER_BACK_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new GroupGridCoverImage(view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_recycler_img, parent, false);
            return new RecyclerItem(view);

        } else if (viewType == GROUP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new ImageUneItem(view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new GroupImageDoubleItem(view);

        } else if (viewType == GROUP_RESPONSE_IMG_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new GroupResponseImageDoubleItem(view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = list.get(position);

        final int itemViewType = getItemViewType(position);
        if (itemViewType == GROUP_RECYCLER_ITEM) {
            RecyclerItem recyclerItem = (RecyclerItem) holder;
            recyclerItem.bindRecyclerView(model);

            recyclerItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                String myGson2 = gson.toJson(model);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewOnlyPhotos.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("battle_model", myGson2);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            ImageUneItem imageUneItem = (ImageUneItem) holder;
            imageUneItem.bindImageUne(model);

            imageUneItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                String myGson2 = gson.toJson(model);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewOnlyPhotos.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("battle_model", myGson2);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupGridCoverImage groupGridCoverImage = (GroupGridCoverImage) holder;
            groupGridCoverImage.bindImageUne(model);

            groupGridCoverImage.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                String myGson2 = gson.toJson(model);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewOnlyPhotos.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("battle_model", myGson2);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupGridCoverImage gridCoverBackgroundProfile = (GroupGridCoverImage) holder;
            gridCoverBackgroundProfile.bindImageUne(model);

            gridCoverBackgroundProfile.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                String myGson2 = gson.toJson(model);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewOnlyPhotos.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("battle_model", myGson2);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDoubleItem groupImageDoubleItem = (GroupImageDoubleItem) holder;
            groupImageDoubleItem.bindImageDouble(model);

            groupImageDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                String myGson2 = gson.toJson(model);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewOnlyPhotos.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("battle_model", myGson2);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_RESPONSE_IMG_DOUBLE) {
            GroupResponseImageDoubleItem responseImageDoubleItem = (GroupResponseImageDoubleItem) holder;
            responseImageDoubleItem.bindImageDouble(model);

            responseImageDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                String myGson2 = gson.toJson(model);

                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewOnlyPhotos.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("battle_model", myGson2);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        }

        // hide progressbar
        progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) context;
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
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
        if (list.get(position).isGroup_cover_profile_photo())
            return GROUP_COVER_IMAGE;
        else if (list.get(position).isGroup_cover_background_profile_photo())
            return GROUP_COVER_BACK_PROFILE;
        else if (list.get(position).isGroup_recyclerItem())
            return GROUP_RECYCLER_ITEM;
        else if (list.get(position).isGroup_imageUne())
            return GROUP_IMAGE_UNE;
        else if (list.get(position).isGroup_imageDouble())
            return GROUP_IMAGE_DOUBLE;
        else if (list.get(position).isGroup_response_imageDouble())
            return GROUP_RESPONSE_IMG_DOUBLE;
        else
            return -1;
    }

    public class RecyclerItem extends RecyclerView.ViewHolder {
        ImageView img;
        public RecyclerItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_recyclerview);
        }

        void bindRecyclerView(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(img);
        }
    }

    public class ImageUneItem extends RecyclerView.ViewHolder {
        ImageView img;
        public ImageUneItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUrl())
                    .into(img);
        }
    }

    public class GroupGridCoverImage extends RecyclerView.ViewHolder {
        ImageView img;
        public GroupGridCoverImage(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(BattleModel model) {
            if (!model.getGroup_user_background_profile_url().isEmpty()) {
                // to user background prodile
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(model.getGroup_user_background_full_profile_url())
                        .into(img);

            } else {
                // to group profile
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(model.getGroup_full_profile_photo())
                        .into(img);
            }
        }
    }

    public class GroupImageDoubleItem extends RecyclerView.ViewHolder {
        ImageView img1, img2;
        public GroupImageDoubleItem(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.imagedouble_grid_un);
            img2 = itemView.findViewById(R.id.imagedouble_grid_deux);
        }



        void bindImageDouble(BattleModel model) {
            Glide.with(context.getApplicationContext()).load(model.getUrlUn())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(img1);

            Glide.with(context.getApplicationContext()).load(model.getUrlDeux())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(img2);
        }
    }

    public class GroupResponseImageDoubleItem extends RecyclerView.ViewHolder {
        ImageView img1, img2;
        public GroupResponseImageDoubleItem(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.imagedouble_grid_un);
            img2 = itemView.findViewById(R.id.imagedouble_grid_deux);
        }

        void bindImageDouble(BattleModel model) {
            Glide.with(context.getApplicationContext()).load(model.getReponse_url())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(img1);

            Glide.with(context.getApplicationContext()).load(model.getInvite_url())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(img2);
        }
    }
}

