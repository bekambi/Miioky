package com.bekambimouen.miiokychallenge.publication_saved.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.RecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.VideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.VideoUneDisplay;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;

import java.util.ArrayList;

public class ViewSavedPublicationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ViewSavedPublicationsAdapter";

    // vars
    public static final int RECYCLER_ITEM = 1;
    public static final int IMAGE_UNE = 2;
    public static final int IMAGE_UNE_BIG_IMAGE = 22;
    public static final int VIDEO_UNE = 3;
    public static final int VIDEO_UNE_BIG_IMAGE = 33;
    public static final int IMAGE_DOUBLE = 4;
    public static final int VIDEO_DOUBLE = 5;

    private final AppCompatActivity context;
    private final ArrayList<BattleModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public ViewSavedPublicationsAdapter(AppCompatActivity context, ArrayList<BattleModel> list,
                                        ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_recycler_item, parent, false);
            return new RecyclerItemDisplay(context,  null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imageune_item, parent, false);
            return new ImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_BIG_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imageune_big_img, parent, false);
            return new ImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videoune_item, parent, false);
            return new VideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_BIG_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videoune_big_img, parent, false);
            return new VideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imagedouble_item, parent, false);
            return new ImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videodouble_item, parent, false);
            return new VideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = list.get(position);

        try {
            PreloadMainFragment.getPreloadImages(context, list.get(position+1));
            PreloadMainFragment.getPreloadImages(context, list.get(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);
        if (itemViewType == RECYCLER_ITEM) {
            RecyclerItemDisplay recyclerItem = (RecyclerItemDisplay) holder;
            recyclerItem.init(model);
            recyclerItem.menu_item.setVisibility(View.GONE);

        } else if (itemViewType == IMAGE_UNE) {
            ImageUneDisplay imageUne = (ImageUneDisplay) holder;
            imageUne.init(model);
            imageUne.menu_item.setVisibility(View.GONE);

        } else if (itemViewType == IMAGE_UNE_BIG_IMAGE) {
            ImageUneDisplay imageUne = (ImageUneDisplay) holder;
            imageUne.init(model);
            imageUne.menu_item.setVisibility(View.GONE);

        } else if (itemViewType == VIDEO_UNE) {
            VideoUneDisplay videoUne = (VideoUneDisplay) holder;
            videoUne.init(model);
            videoUne.menu_item.setVisibility(View.GONE);

        } else if (itemViewType == VIDEO_UNE_BIG_IMAGE) {
            VideoUneDisplay videoUne = (VideoUneDisplay) holder;
            videoUne.init(model);
            videoUne.menu_item.setVisibility(View.GONE);

        } else if (itemViewType == IMAGE_DOUBLE){
            ImageDoubleDisplay imageDouble = (ImageDoubleDisplay) holder;
            imageDouble.init(model);
            imageDouble.menu_item.setVisibility(View.GONE);

        } else if (itemViewType == VIDEO_DOUBLE) {
            VideoDoubleDisplay videoDouble = (VideoDoubleDisplay) holder;
            videoDouble.init(model);
            videoDouble.menu_item.setVisibility(View.GONE);

        }

        // hide progressbar
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isRecyclerItem())
            return RECYCLER_ITEM;
        else if (list.get(position).isImageUne() && !list.get(position).isBig_image())
            return IMAGE_UNE;
        else if (list.get(position).isImageUne() && list.get(position).isBig_image())
            return IMAGE_UNE_BIG_IMAGE;
        else if (list.get(position).isVideoUne() && !list.get(position).isBig_image())
            return VIDEO_UNE;
        else if (list.get(position).isVideoUne() && list.get(position).isBig_image())
            return VIDEO_UNE_BIG_IMAGE;
        else if (list.get(position).isImageDouble())
            return IMAGE_DOUBLE;
        else if (list.get(position).isVideoDouble())
            return VIDEO_DOUBLE;
        else
            return -1;
    }
}

