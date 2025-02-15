package com.bekambimouen.miiokychallenge.challenge.adapter;

import android.app.Dialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.GroupImageHolder;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.GroupVideoHolder;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.ImageHolder;
import com.bekambimouen.miiokychallenge.challenge.adapter.viewholder.VideoHolder;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ViewMyChallengesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ViewMyChallengesAdapter";

    private CustomShareProgressView progresDialog;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.setCancelable(false);
        progresDialog.show();
    }

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
    private final List<ModelInvite> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;
    private final String user_id;

    public ViewMyChallengesAdapter(AppCompatActivity context, List<ModelInvite> list,
                                   ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
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
                    .inflate(R.layout.layout_vue_user_image_battle_item, parent, false);
            return new ImageHolder(context, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_image_battle_big_img, parent, false);
            return new ImageHolder(context, relLayout_view_overlay, view);

        } else if (viewType == VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_video_battle_item, parent, false);
            return new VideoHolder(context, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_user_video_battle_big_img, parent, false);
            return new VideoHolder(context, null, relLayout_view_overlay, view);

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
            return new GroupVideoHolder(context, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_user_video_battle_big_img, parent, false);
            return new GroupVideoHolder(context, null, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelInvite model = list.get(position);

        try {
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+1));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        int itemView = getItemViewType(position);

        if (itemView == IMAGE) {
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.bindView(model);

            // delete challenge
            deleteChallengeOnMiioky(model, imageHolder.button, holder);

        } else if (itemView == IMAGE_BIG_IMG) {
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.bindView(model);

            // delete challenge
            deleteChallengeOnMiioky(model, imageHolder.button, holder);

        } else if (itemView == VIDEO) {
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.bindView(model);

            // delete challenge
            deleteChallengeOnMiioky(model, videoHolder.button, holder);

        } else if (itemView == VIDEO_BIG_IMG) {
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.bindView(model);

            // delete challenge
            deleteChallengeOnMiioky(model, videoHolder.button, holder);

        } else if (itemView == GROUP_IMAGE) {
            GroupImageHolder groupImageHolder = (GroupImageHolder) holder;
            groupImageHolder.bindView(model);

            // delete challenge
            deleteChallengeInGroup(model, groupImageHolder.button, holder);

        } else if (itemView == GROUP_IMAGE_BIG_IMG) {
            GroupImageHolder groupImageHolder = (GroupImageHolder) holder;
            groupImageHolder.bindView(model);

            // delete challenge
            deleteChallengeInGroup(model, groupImageHolder.button, holder);

        } else if (itemView == GROUP_VIDEO) {
            GroupVideoHolder groupVideoHolder = (GroupVideoHolder) holder;
            groupVideoHolder.bindView(model);

            // delete challenge
            deleteChallengeInGroup(model, groupVideoHolder.button, holder);

        } else if (itemView == GROUP_VIDEO_BIG_IMG) {
            GroupVideoHolder groupVideoHolder = (GroupVideoHolder) holder;
            groupVideoHolder.bindView(model);

            // delete challenge
            deleteChallengeInGroup(model, groupVideoHolder.button, holder);
        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    // delete challenge
    private void deleteChallengeOnMiioky(ModelInvite model, TextView button, RecyclerView.ViewHolder holder) {
        button.setOnClickListener(view -> {
            // show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = v.findViewById(R.id.dialog_title);
            TextView dialog_text = v.findViewById(R.id.dialog_text);
            TextView negativeButton = v.findViewById(R.id.tv_no);
            TextView positiveButton = v.findViewById(R.id.tv_yes);
            builder.setView(v);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.no));
            positiveButton.setText(context.getString(R.string.delete));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)+" ?"));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_challenge)));

            negativeButton.setOnClickListener(view1 -> d.dismiss());
            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();

                showLoading();
                HashMap<String, Object> map = new HashMap<>();
                map.put("suppressed", true);

                // delete images
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_invite_battle))
                        .child(user_id)
                        .child(model.getInvite_photoID())
                        .updateChildren(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .child(model.getInvite_photoID())
                        .updateChildren(map).addOnSuccessListener(unused1 -> {
                            removeAt(holder.getLayoutPosition());
                            progresDialog.dismiss();
                        });
            });
        });
    }

    // delete challenge
    private void deleteChallengeInGroup(ModelInvite model, TextView button, RecyclerView.ViewHolder holder) {

        button.setOnClickListener(view -> {
            // show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = v.findViewById(R.id.dialog_title);
            TextView dialog_text = v.findViewById(R.id.dialog_text);
            TextView negativeButton = v.findViewById(R.id.tv_no);
            TextView positiveButton = v.findViewById(R.id.tv_yes);
            builder.setView(v);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.no));
            positiveButton.setText(context.getString(R.string.delete));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)+" ?"));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_challenge)));

            negativeButton.setOnClickListener(view1 -> d.dismiss());
            positiveButton.setOnClickListener(view2 -> {
                d.dismiss();

                showLoading();
                HashMap<String, Object> map = new HashMap<>();
                map.put("suppressed", true);

                // delete images
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_invitation_challenge))
                        .child(model.getGroup_id())
                        .child(model.getInvite_photoID())
                        .updateChildren(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_invite_battle_media))
                        .child(model.getInvite_photoID())
                        .updateChildren(map).addOnSuccessListener(unused1 -> {
                            removeAt(holder.getLayoutPosition());
                            progresDialog.dismiss();
                        });
            });
        });
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isImage() && !list.get(position).isBig_image())
            return IMAGE;
        else if (list.get(position).isImage() && list.get(position).isBig_image())
            return IMAGE_BIG_IMG;
        else if (list.get(position).isVideo() && !list.get(position).isBig_image())
            return VIDEO;
        else if (list.get(position).isVideo() && list.get(position).isBig_image())
            return VIDEO_BIG_IMG;
        else if (list.get(position).isGroup_image() && !list.get(position).isBig_image())
            return GROUP_IMAGE;
        else if (list.get(position).isGroup_image() && list.get(position).isBig_image())
            return GROUP_IMAGE_BIG_IMG;
        else if (list.get(position).isGroup_video() && !list.get(position).isBig_image())
            return GROUP_VIDEO;
        else if (list.get(position).isGroup_video() && list.get(position).isBig_image())
            return GROUP_VIDEO_BIG_IMG;
        else
            return -1;
    }
}

