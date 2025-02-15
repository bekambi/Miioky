package com.bekambimouen.miiokychallenge.search.adapter;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.bekambimouen.miiokychallenge.search.ViewExplore;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

// appelé par BattleProfileFragment
public class ExploreGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ExploreGridAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    public static final int USER_PROFILE = 0;
    public static final int COMMENT_TEXT = 1;
    private static final int RECYCLER_IMAGE = 2;
    private static final int IMAGE_UNE = 3;
    private static final int VIDEO_UNE = 4;
    private static final int IMAGE_DOUBLE = 5;
    private static final int VIDEO_DOUBLE = 6;
    private static final int REPONSE_IMG_DOUBLE = 7;
    private static final int REPONSE_VID_DOUBLE = 8;
    // shared
    private static final int IMAGE_UNE_SHARED = 9;
    private static final int VIDEO_UNE_SHARED = 10;
    private static final int IMAGE_DOUBLE_SHARED = 11;
    private static final int VIDEO_DOUBLE_SHARED = 12;
    private static final int REPONSE_IMG_DOUBLE_SHARED = 13;
    private static final int REPONSE_VID_DOUBLE_SHARED = 14;

    public static final int USER_PROFILE_SHARE = 15;
    public static final int GROUP_SINGLE_TOP_CIRCLE_IMAGE = 16;
    public static final int GROUP_SINGLE_TOP_IMAGE_DOUBLE = 17;
    public static final int GROUP_SINGLE_TOP_IMAGE_UNE = 18;
    public static final int GROUP_SINGLE_TOP_RECYCLER = 19;
    public static final int GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE = 20;
    public static final int GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE = 21;
    public static final int GROUP_SINGLE_TOP_VIDEO_DOUBLE = 22;
    public static final int GROUP_SINGLE_TOP_VIDEO_UNE = 23;

    private final AppCompatActivity context;
    private final ArrayList<BattleModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public ExploreGridAdapter(AppCompatActivity context, ArrayList<BattleModel> list, ProgressBar progressBar,
                              OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == USER_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new UserProfileItem(view);

        } else if (viewType == COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_comment_text, parent, false);
            return new CommentText(view);

        } else if (viewType == RECYCLER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_recycler_img, parent, false);
            return new RecyclerItem(view);

        } else if (viewType == IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new ImageUneItem(view);

        } else if (viewType == VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videoune, parent, false);
            return new VideoUneItem(view);

        } else if (viewType == IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new ImageDoubleItem(view);

        } else if (viewType == VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videodouble, parent, false);
            return new VideoDoubleItem(view);

        } else if (viewType == REPONSE_IMG_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new ResponseImageDoubleItem(view);

        } else if (viewType == REPONSE_VID_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videodouble, parent, false);
            return new ResponseVideoDoubleItem(view);

        } else if (viewType == USER_PROFILE_SHARE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new SharedUserProfileItem(view);

        } else if (viewType == IMAGE_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new ImageUneItem(view);

        } else if (viewType == VIDEO_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videoune, parent, false);
            return new VideoUneItem(view);

        } else if (viewType == IMAGE_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new ImageDoubleItem(view);

        } else if (viewType == VIDEO_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videodouble, parent, false);
            return new VideoDoubleItem(view);

        } else if (viewType == REPONSE_IMG_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new ResponseImageDoubleItem(view);

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videodouble, parent, false);
            return new ResponseVideoDoubleItem(view);

        } // shared from from group
        else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new SharedSingleTopCircleImageProfileItem(view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new SharedSingleTopImageDoubleProfileItem(view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imageune, parent, false);
            return new SharedSingleTopImageUneProfileItem(view);

        } else if (viewType == GROUP_SINGLE_TOP_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_recycler_img, parent, false);
            return new SharedSingleTopRecyclerProfileItem(view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_imagedouble, parent, false);
            return new SharedSingleTopResponseImageDoubleProfileItem(view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videodouble, parent, false);
            return new SharedSingleTopResponseVideoDoubleProfile(view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videodouble, parent, false);
            return new SharedSingleTopVideoDoubleProfile(view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_grid_explore_videoune, parent, false);
            return new SharedSingleTopVideoUneProfile(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = list.get(position);

        try {
            PreloadMainFragment.getPreloadImages(context, list.get(position+1));
            PreloadMainFragment.getPreloadImages(context, list.get(position+2));
            PreloadMainFragment.getPreloadImages(context, list.get(position+3));
            PreloadMainFragment.getPreloadImages(context, list.get(position+4));
            PreloadMainFragment.getPreloadImages(context, list.get(position+5));
            PreloadMainFragment.getPreloadImages(context, list.get(position+6));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);
        if (itemViewType == USER_PROFILE) {
            UserProfileItem userProfileItem = (UserProfileItem) holder;
            userProfileItem.bindImageUne(model);

            userProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == COMMENT_TEXT) {
            CommentText commentText = (CommentText) holder;
            commentText.bindImageUne(model);

            commentText.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == RECYCLER_IMAGE) {
            RecyclerItem recyclerItem = (RecyclerItem) holder;
            recyclerItem.bindRecyclerView(model);

            recyclerItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == IMAGE_UNE) {
            ImageUneItem imageUneItem = (ImageUneItem) holder;
            imageUneItem.bindImageUne(model);

            imageUneItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == VIDEO_UNE) {
            VideoUneItem videoUneItem = (VideoUneItem) holder;
            videoUneItem.bindVideoUne(model);

            videoUneItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == IMAGE_DOUBLE){
            ImageDoubleItem imageDoubleItem = (ImageDoubleItem) holder;
            imageDoubleItem.bindImageDouble(model);

            imageDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == VIDEO_DOUBLE) {
            VideoDoubleItem videoDoubleItem = (VideoDoubleItem) holder;
            videoDoubleItem.bindVideoDouble(model);

            videoDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == REPONSE_IMG_DOUBLE){
            ResponseImageDoubleItem reponseImageDoubleItem = (ResponseImageDoubleItem) holder;
            reponseImageDoubleItem.bindImageDouble(model);

            reponseImageDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });
        } else if (itemViewType == REPONSE_VID_DOUBLE) {
            ResponseVideoDoubleItem reponseVideoDoubleItem = (ResponseVideoDoubleItem) holder;
            reponseVideoDoubleItem.bindVideoDouble(model);

            reponseVideoDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileItem sharedUserProfileItem = (SharedUserProfileItem) holder;
            sharedUserProfileItem.bindImageUne(model);

            sharedUserProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            ImageUneItem imageUneItem = (ImageUneItem) holder;
            imageUneItem.bindImageUne(model);

            imageUneItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == VIDEO_UNE_SHARED) {
            VideoUneItem videoUneItem = (VideoUneItem) holder;
            videoUneItem.bindVideoUne(model);

            videoUneItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            ImageDoubleItem imageDoubleItem = (ImageDoubleItem) holder;
            imageDoubleItem.bindImageDouble(model);

            imageDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            VideoDoubleItem videoDoubleItem = (VideoDoubleItem) holder;
            videoDoubleItem.bindVideoDouble(model);

            videoDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED){
            ResponseImageDoubleItem reponseImageDoubleItem = (ResponseImageDoubleItem) holder;
            reponseImageDoubleItem.bindImageDouble(model);

            reponseImageDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });
        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            ResponseVideoDoubleItem reponseVideoDoubleItem = (ResponseVideoDoubleItem) holder;
            reponseVideoDoubleItem.bindVideoDouble(model);

            reponseVideoDoubleItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageProfileItem singleTopCircleImageProfileItem = (SharedSingleTopCircleImageProfileItem) holder;
            singleTopCircleImageProfileItem.bindImageUne(model);

            singleTopCircleImageProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            SharedSingleTopImageDoubleProfileItem singleTopImageDoubleProfileItem = (SharedSingleTopImageDoubleProfileItem) holder;
            singleTopImageDoubleProfileItem.bindImageDouble(model);

            singleTopImageDoubleProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneProfileItem singleTopImageUneProfileItem = (SharedSingleTopImageUneProfileItem) holder;
            singleTopImageUneProfileItem.bindImageUne(model);

            singleTopImageUneProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerProfileItem singleTopRecyclerProfileItem = (SharedSingleTopRecyclerProfileItem) holder;
            singleTopRecyclerProfileItem.bindRecyclerView(model);

            singleTopRecyclerProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            SharedSingleTopResponseImageDoubleProfileItem singleTopResponseImageDoubleProfileItem = (SharedSingleTopResponseImageDoubleProfileItem) holder;
            singleTopResponseImageDoubleProfileItem.bindImageDouble(model);

            singleTopResponseImageDoubleProfileItem.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });
        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleProfile singleTopResponseVideoDoubleProfile = (SharedSingleTopResponseVideoDoubleProfile) holder;
            singleTopResponseVideoDoubleProfile.bindVideoDouble(model);

            singleTopResponseVideoDoubleProfile.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });
        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleProfile singleTopVideoDoubleProfile = (SharedSingleTopVideoDoubleProfile) holder;
            singleTopVideoDoubleProfile.bindVideoDouble(model);

            singleTopVideoDoubleProfile.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneProfile singleTopVideoUneProfile = (SharedSingleTopVideoUneProfile) holder;
            singleTopVideoUneProfile.bindVideoUne(model);

            singleTopVideoUneProfile.itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewExplore.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("battle_model", myGson);
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
        if (list.get(position).isComment_text())
            return COMMENT_TEXT;
        else if (list.get(position).isGridRecyclerImage())
            return RECYCLER_IMAGE;
        else if (list.get(position).isImageUne())
            return IMAGE_UNE;
        else if (list.get(position).isVideoUne())
            return VIDEO_UNE;
        else if (list.get(position).isImageDouble())
            return IMAGE_DOUBLE;
        else if (list.get(position).isVideoDouble())
            return VIDEO_DOUBLE;
        else if (list.get(position).isReponseImageDouble())
            return REPONSE_IMG_DOUBLE;
        else if (list.get(position).isReponseVideoDouble())
            return REPONSE_VID_DOUBLE;


        else if (list.get(position).isUser_profile_shared())
            return USER_PROFILE_SHARE;
        else if (list.get(position).isImageUne_shared())
            return IMAGE_UNE_SHARED;
        else if (list.get(position).isVideoUne_shared())
            return VIDEO_UNE_SHARED;
        else if (list.get(position).isImageDouble_shared())
            return IMAGE_DOUBLE_SHARED;
        else if (list.get(position).isVideoDouble_shared())
            return VIDEO_DOUBLE_SHARED;
        else if (list.get(position).isReponseImageDouble_shared())
            return REPONSE_IMG_DOUBLE_SHARED;
        else if (list.get(position).isReponseVideoDouble_shared())
            return REPONSE_VID_DOUBLE_SHARED;
        else if (list.get(position).isUser_profile())
            return USER_PROFILE;
        else  if (list.get(position).isGroup_share_single_top_circle_image())
            return GROUP_SINGLE_TOP_CIRCLE_IMAGE;
        else  if (list.get(position).isGroup_share_single_top_image_double())
            return GROUP_SINGLE_TOP_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_single_top_image_une())
            return GROUP_SINGLE_TOP_IMAGE_UNE;
        else  if (list.get(position).isGroup_share_single_top_recycler())
            return GROUP_SINGLE_TOP_RECYCLER;
        else  if (list.get(position).isGroup_share_single_top_response_image_double())
            return GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_single_top_response_video_double())
            return GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE;
        else  if (list.get(position).isGroup_share_single_top_video_double())
            return GROUP_SINGLE_TOP_VIDEO_DOUBLE;
        else
            return GROUP_SINGLE_TOP_VIDEO_UNE;
    }

    public class CommentText extends RecyclerView.ViewHolder {
        TextView comment_text;
        public CommentText(@NonNull View itemView) {
            super(itemView);
            comment_text = itemView.findViewById(R.id.comment_text);
        }

        void bindImageUne(BattleModel model) {
            comment_text.setText(model.getComment_subject());
            getTheComment(model);
        }

        // get the comment text
        private void getTheComment(BattleModel model) {
            String theme = model.getComment_theme();
            if (theme.equals(context.getString(R.string.gradient_blue)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_blue));
            if (theme.equals(context.getString(R.string.gradient_red)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_red));
            if (theme.equals(context.getString(R.string.gradient_brown)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_brown));
            if (theme.equals(context.getString(R.string.gradient_yellow_yellow_and_pink)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_yellow_yellow_and_pink));
            if (theme.equals(context.getString(R.string.gradient_darkred_black_blue_shinning)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_darkred_black_blue_shinning));
            if (theme.equals(context.getString(R.string.gradient_black)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_black));
            if (theme.equals(context.getString(R.string.gradient_shinning_blue_darkred_chinning_blue)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_shinning_blue_darkred_chinning_blue));
            if (theme.equals(context.getString(R.string.gradient_shinning_blue_darkred_pink)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_shinning_blue_darkred_pink));
            if (theme.equals(context.getString(R.string.gradient_yellow_pink_dark_violet)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_yellow_pink_dark_violet));
            if (theme.equals(context.getString(R.string.gradient_pink)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_pink));
            if (theme.equals(context.getString(R.string.gradient_vertwhatsapp)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_vertwhatsapp));
            if (theme.equals(context.getString(R.string.gradient_dark_violet)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_dark_violet));
            if (theme.equals(context.getString(R.string.gradient_blue_green)))
                comment_text.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_blue_green));
        }
    }

    public class UserProfileItem extends RecyclerView.ViewHolder {
        ImageView img;
        public UserProfileItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUser_profile_photo())
                    .centerCrop()
                    .into(img);
        }
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
                    .centerCrop()
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
                    .centerCrop()
                    .into(img);
        }
    }

    public class VideoUneItem extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public VideoUneItem(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_une);
        }

        void bindVideoUne(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail())
                    .centerCrop()
                    .into(thumbnail);
        }
    }

    public class ImageDoubleItem extends RecyclerView.ViewHolder {
        ImageView img1, img2;
        public ImageDoubleItem(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.imagedouble_grid_un);
            img2 = itemView.findViewById(R.id.imagedouble_grid_deux);
        }



        void bindImageDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlUn())
                    .centerCrop()
                    .into(img1);

            Glide.with(context.getApplicationContext())
                    .load(model.getUrlDeux())
                    .centerCrop()
                    .into(img2);
        }
    }

    public class VideoDoubleItem extends RecyclerView.ViewHolder {
        ImageView thumbnail1, thumbnail2;

        public VideoDoubleItem(@NonNull View itemView) {
            super(itemView);
            thumbnail1 = itemView.findViewById(R.id.videodouble_grid_un);
            thumbnail2 = itemView.findViewById(R.id.videodouble_grid_deux);
        }

        void bindVideoDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_un())
                    .centerCrop()
                    .into(thumbnail1);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_deux())
                    .centerCrop()
                    .into(thumbnail2);
        }
    }

    public class ResponseImageDoubleItem extends RecyclerView.ViewHolder {
        ImageView img1, img2;
        public ResponseImageDoubleItem(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.imagedouble_grid_un);
            img2 = itemView.findViewById(R.id.imagedouble_grid_deux);
        }

        void bindImageDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getReponse_url())
                    .centerCrop()
                    .into(img1);

            Glide.with(context.getApplicationContext())
                    .load(model.getInvite_url())
                    .centerCrop()
                    .into(img2);
        }
    }

    public class ResponseVideoDoubleItem extends RecyclerView.ViewHolder {
        ImageView thumbnail1, thumbnail2;

        public ResponseVideoDoubleItem(@NonNull View itemView) {
            super(itemView);
            thumbnail1 = itemView.findViewById(R.id.videodouble_grid_un);
            thumbnail2 = itemView.findViewById(R.id.videodouble_grid_deux);
        }

        void bindVideoDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_response())
                    .centerCrop()
                    .into(thumbnail1);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_invite())
                    .centerCrop()
                    .into(thumbnail2);
        }
    }

    public class SharedUserProfileItem extends RecyclerView.ViewHolder {
        ImageView img;
        public SharedUserProfileItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUser_profile_photo())
                    .centerCrop()
                    .into(img);
        }
    }

    public class SharedSingleTopCircleImageProfileItem extends RecyclerView.ViewHolder {
        ImageView img;
        public SharedSingleTopCircleImageProfileItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(BattleModel model) {
            if (!model.getGroup_user_background_profile_url().isEmpty()) {
                Glide.with(context.getApplicationContext())
                        .load(model.getGroup_user_background_profile_url())
                        .centerCrop()
                        .into(img);

            } else if (!model.getGroup_profile_photo().isEmpty()) {
                Glide.with(context.getApplicationContext())
                        .load(model.getGroup_profile_photo())
                        .centerCrop()
                        .into(img);
            }
        }
    }

    public class SharedSingleTopImageDoubleProfileItem extends RecyclerView.ViewHolder {
        ImageView img1, img2;
        public SharedSingleTopImageDoubleProfileItem(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.imagedouble_grid_un);
            img2 = itemView.findViewById(R.id.imagedouble_grid_deux);
        }

        void bindImageDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUrlUn())
                    .centerCrop()
                    .into(img1);

            Glide.with(context.getApplicationContext())
                    .load(model.getUrlDeux())
                    .centerCrop()
                    .into(img2);
        }
    }

    public class SharedSingleTopImageUneProfileItem extends RecyclerView.ViewHolder {
        ImageView img;
        public SharedSingleTopImageUneProfileItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageune_grid);
        }

        void bindImageUne(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUrl())
                    .centerCrop()
                    .into(img);
        }
    }

    public class SharedSingleTopRecyclerProfileItem extends RecyclerView.ViewHolder {
        ImageView img;
        public SharedSingleTopRecyclerProfileItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_recyclerview);
        }

        void bindRecyclerView(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getUrli())
                    .centerCrop()
                    .into(img);
        }
    }

    public class SharedSingleTopResponseImageDoubleProfileItem extends RecyclerView.ViewHolder {
        ImageView img1, img2;
        public SharedSingleTopResponseImageDoubleProfileItem(@NonNull View itemView) {
            super(itemView);
            img1 = itemView.findViewById(R.id.imagedouble_grid_un);
            img2 = itemView.findViewById(R.id.imagedouble_grid_deux);
        }

        void bindImageDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getReponse_url())
                    .centerCrop()
                    .into(img1);

            Glide.with(context.getApplicationContext())
                    .load(model.getInvite_url())
                    .centerCrop()
                    .into(img2);
        }
    }

    public class SharedSingleTopResponseVideoDoubleProfile extends RecyclerView.ViewHolder {
        ImageView thumbnail1, thumbnail2;

        public SharedSingleTopResponseVideoDoubleProfile(@NonNull View itemView) {
            super(itemView);
            thumbnail1 = itemView.findViewById(R.id.videodouble_grid_un);
            thumbnail2 = itemView.findViewById(R.id.videodouble_grid_deux);
        }

        void bindVideoDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_response())
                    .centerCrop()
                    .into(thumbnail1);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_invite())
                    .centerCrop()
                    .into(thumbnail2);
        }
    }

    public class SharedSingleTopVideoDoubleProfile extends RecyclerView.ViewHolder {
        ImageView thumbnail1, thumbnail2;

        public SharedSingleTopVideoDoubleProfile(@NonNull View itemView) {
            super(itemView);
            thumbnail1 = itemView.findViewById(R.id.videodouble_grid_un);
            thumbnail2 = itemView.findViewById(R.id.videodouble_grid_deux);
        }

        void bindVideoDouble(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_un())
                    .centerCrop()
                    .into(thumbnail1);

            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail_deux())
                    .centerCrop()
                    .into(thumbnail2);
        }
    }

    public class SharedSingleTopVideoUneProfile extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public SharedSingleTopVideoUneProfile(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_une);
        }

        void bindVideoUne(BattleModel model) {
            Glide.with(context.getApplicationContext())
                    .load(model.getThumbnail())
                    .centerCrop()
                    .into(thumbnail);
        }
    }
}

