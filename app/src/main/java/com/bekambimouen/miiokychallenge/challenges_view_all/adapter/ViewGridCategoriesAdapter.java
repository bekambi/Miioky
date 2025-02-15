package com.bekambimouen.miiokychallenge.challenges_view_all.adapter;

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
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders.GroupImageItem;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders.GroupVideoItem;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders.ImageItem;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders.VideoItem;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.google.firebase.auth.FirebaseAuth;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.Objects;

public class ViewGridCategoriesAdapter extends PaginatedAdapter<ModelInvite, RecyclerView.ViewHolder> {
    private  final String TAG = "ViewGridCategoryAdapter";
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
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    private final String user_id;

    public ViewGridCategoriesAdapter(AppCompatActivity context, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_category_image, parent, false);
            return new ImageItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_category_image_big_img, parent, false);
            return new ImageItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_category_video, parent, false);
            return new VideoItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_category_video_big_img, parent, false);
            return new VideoItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_view_category_image, parent, false);
            return new GroupImageItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_view_category_image_big_img, parent, false);
            return new GroupImageItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_view_category_video, parent, false);
            return new GroupVideoItem(context, user_id, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_view_category_video_big_img, parent, false);
            return new GroupVideoItem(context, user_id, relLayout_view_overlay, view);

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

        final int itemViewType = getItemViewType(position);
        if (itemViewType == IMAGE) {
            ImageItem imageItem = (ImageItem) holder;
            imageItem.bindView(model);

        } else if (itemViewType == IMAGE_BIG_IMG) {
            ImageItem imageItem = (ImageItem) holder;
            imageItem.bindView(model);

        }  else if (itemViewType == VIDEO) {
            VideoItem videoItem = (VideoItem) holder;
            videoItem.bindView(model);

        }  else if (itemViewType == VIDEO_BIG_IMG) {
            VideoItem videoItem = (VideoItem) holder;
            videoItem.bindView(model);

        } else if (itemViewType == GROUP_IMAGE) {
            GroupImageItem groupImageItem = (GroupImageItem) holder;
            groupImageItem.bindView(model);

        } else if (itemViewType == GROUP_IMAGE_BIG_IMG) {
            GroupImageItem groupImageItem = (GroupImageItem) holder;
            groupImageItem.bindView(model);

        } else if (itemViewType == GROUP_VIDEO) {
            GroupVideoItem groupVideoItem = (GroupVideoItem) holder;
            groupVideoItem.bindView(model);

        } else if (itemViewType == GROUP_VIDEO_BIG_IMG) {
            GroupVideoItem groupVideoItem = (GroupVideoItem) holder;
            groupVideoItem.bindView(model);
        }

        // hide progressbar
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
