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

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders.InviteGroupVideoHolder;
import com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders.InviteVideoHolder;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

public class ViewInviteVideoAdapter extends PaginatedAdapter<ModelInvite, RecyclerView.ViewHolder> {
    private static final String TAG = "ViewInviteVideoAdapter";
    // constant
    private static final int VIDEO = 1;
    private static final int GROUP_VIDEO = 2;

    private final AppCompatActivity context;
    private final Container recyclerView;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private boolean isMute = false;
    private final HttpProxyCacheServer proxy;

    public ViewInviteVideoAdapter(AppCompatActivity context, Container recyclerView, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
        proxy = App.getProxy(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == GROUP_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_challenge_invite_group_video, parent, false);
            return new InviteGroupVideoHolder(context, relLayout_view_overlay, view);

        } else {view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_challenge_invite_video, parent, false);
            return new InviteVideoHolder(context, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelInvite model = getItem(position);

        int view_type = getItemViewType(position);
        if (view_type == GROUP_VIDEO) {
            InviteGroupVideoHolder groupVideoHolder = (InviteGroupVideoHolder) holder;

            groupVideoHolder.bindVideo(model);

            groupVideoHolder.rel_vol.setOnClickListener(view -> {
                if (groupVideoHolder.player != null) {
                    if (groupVideoHolder.player.getVolume() == 0) {
                        isMute = false;
                        groupVideoHolder.player.setVolume(1);
                        groupVideoHolder.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        groupVideoHolder.player.setVolume(0);
                        groupVideoHolder.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }

            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (groupVideoHolder.player != null) {
                            if (isMute) {
                                try {
                                    groupVideoHolder.player.setVolume(0);
                                    groupVideoHolder.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    groupVideoHolder.player.setVolume(1);
                                    groupVideoHolder.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });

        } else {
            InviteVideoHolder videoHolder = (InviteVideoHolder) holder;

            videoHolder.bindVideo(model);

            videoHolder.rel_vol.setOnClickListener(view -> {
                if (videoHolder.player != null) {
                    if (videoHolder.player.getVolume() == 0) {
                        isMute = false;
                        videoHolder.player.setVolume(1);
                        videoHolder.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        videoHolder.player.setVolume(0);
                        videoHolder.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }

            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (videoHolder.player != null) {
                            if (isMute) {
                                try {
                                    videoHolder.player.setVolume(0);
                                    videoHolder.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    videoHolder.player.setVolume(1);
                                    videoHolder.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }
                            }
                        }
                    }
                }
            });
        }

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isGroup_video())
            return GROUP_VIDEO;
        else
            return VIDEO;
    }
}
