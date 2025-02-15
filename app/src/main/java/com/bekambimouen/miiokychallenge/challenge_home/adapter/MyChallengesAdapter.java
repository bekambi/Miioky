package com.bekambimouen.miiokychallenge.challenge_home.adapter;

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
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.GroupImageHolder;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.GroupVideoHolder;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.ImageHolder;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.VideoHolder;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

public class MyChallengesAdapter extends PaginatedAdapter<ModelInvite, RecyclerView.ViewHolder> {
    private static final String TAG = "MyChallengesAdapter";
    // constants
    private static final int IMAGE = 1;
    private static final int IMAGE_BIG_IMG = 11;
    private static final int VIDEO = 2;
    private static final int VIDEO_BIG_IMG = 22;
    private static final int GROUP_IMAGE = 3;
    private static final int GROUP_IMAGE_BIG_IMG = 33;
    private static final int GROUP_VIDEO = 4;
    private static final int GROUP_VIDEO_BIG_IMG = 44;

    private final AppCompatActivity context;
    private final String from_view_profile_user_id;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public MyChallengesAdapter(AppCompatActivity context, String from_view_profile_user_id, ProgressBar progressBar,
                               RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.from_view_profile_user_id = from_view_profile_user_id;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_image_battle_item, parent, false);
            return new ImageHolder(context, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_image_battle_big_img, parent, false);
            return new ImageHolder(context, relLayout_view_overlay, view);

        } else if (viewType == VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_video_battle_item, parent, false);
            return new VideoHolder(context, from_view_profile_user_id, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_video_battle_big_img, parent, false);
            return new VideoHolder(context, from_view_profile_user_id, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_user_image_battle_item, parent, false);
            return new GroupImageHolder(context, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_user_image_battle_big_img, parent, false);
            return new GroupImageHolder(context, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_user_video_battle_item, parent, false);
            return new GroupVideoHolder(context, from_view_profile_user_id, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_user_video_battle_big_img, parent, false);
            return new GroupVideoHolder(context, from_view_profile_user_id, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelInvite model = getItem(position);

        try {
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, getItem(position+1));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, getItem(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        int itemView = getItemViewType(position);

        if (itemView == IMAGE) {
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.bindView(model);
            imageHolder.button.setVisibility(View.GONE);

        } else if (itemView == IMAGE_BIG_IMG) {
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.bindView(model);
            imageHolder.button.setVisibility(View.GONE);

        } else if (itemView == VIDEO) {
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.bindView(model);
            videoHolder.button.setVisibility(View.GONE);

        } else if (itemView == VIDEO_BIG_IMG) {
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.bindView(model);
            videoHolder.button.setVisibility(View.GONE);

        } else if (itemView == GROUP_IMAGE) {
            GroupImageHolder groupImageHolder = (GroupImageHolder) holder;
            groupImageHolder.bindView(model);
            groupImageHolder.button.setVisibility(View.GONE);

        } else if (itemView == GROUP_IMAGE_BIG_IMG) {
            GroupImageHolder groupImageHolder = (GroupImageHolder) holder;
            groupImageHolder.bindView(model);
            groupImageHolder.button.setVisibility(View.GONE);

        } else if (itemView == GROUP_VIDEO) {
            GroupVideoHolder groupVideoHolder = (GroupVideoHolder) holder;
            groupVideoHolder.bindView(model);
            groupVideoHolder.button.setVisibility(View.GONE);

        } else if (itemView == GROUP_VIDEO_BIG_IMG) {
            GroupVideoHolder groupVideoHolder = (GroupVideoHolder) holder;
            groupVideoHolder.bindView(model);
            groupVideoHolder.button.setVisibility(View.GONE);
        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isImage() && !getItem(position).isBig_image())
            return IMAGE;
        else if (getItem(position).isImage() && getItem(position).isBig_image())
            return IMAGE_BIG_IMG;
        else if (getItem(position).isVideo() && !getItem(position).isBig_image())
            return VIDEO;
        else if (getItem(position).isVideo() && getItem(position).isBig_image())
            return VIDEO_BIG_IMG;
        else if (getItem(position).isGroup_image() && !getItem(position).isBig_image())
            return GROUP_IMAGE;
        else if (getItem(position).isGroup_image() && getItem(position).isBig_image())
            return GROUP_IMAGE_BIG_IMG;
        else if (getItem(position).isGroup_video() && !getItem(position).isBig_image())
            return GROUP_VIDEO;
        else if (getItem(position).isGroup_video() && getItem(position).isBig_image())
            return GROUP_VIDEO_BIG_IMG;
        else
            return -1;
    }
}
