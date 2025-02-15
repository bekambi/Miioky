package com.bekambimouen.miiokychallenge.view_videos.adapter;

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
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder.GroupVideoUneViewHolder;
import com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder.GroupViewHolderSingleBottomVideoUne;
import com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder.GroupViewHolderTopBottomVideoUne;
import com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder.GroupViewSharedSingleTopVideoUne;
import com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder.VideoViewHolder;
import com.bekambimouen.miiokychallenge.view_videos.adapter.viewholder.ViewShareOnMiiokyVideoUne;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

public class ViewVideoUneShareAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "ViewVideoUneShareAdapter";

    // constant
    public static final int SIMPLE_VIDEO = 1;
    public static final int GROUP_VIDEO = 2;
    public static final int SHARING_VIDEO = 3;
    public static final int SINGLE_BOTTOM_VIDEO = 4;
    public static final int SINGLE_TOP_VIDEO = 5;
    public static final int TOP_BOTTOM_VIDEO = 6;

    private final AppCompatActivity context;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private boolean isMute = false;
    private final HttpProxyCacheServer proxy;

    public ViewVideoUneShareAdapter(AppCompatActivity context, RecyclerView recyclerView, ProgressBar progressBar,
                                    RelativeLayout relLayout_view_overlay) {
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
        if (viewType == SIMPLE_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_video_une, parent, false);
            return new VideoViewHolder(context, relLayout_view_overlay, view);

        } else if (viewType == SHARING_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_video_une_share, parent, false);
            return new ViewShareOnMiiokyVideoUne(context, relLayout_view_overlay, view);

        } if (viewType == GROUP_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_view_video_une, parent, false);
            return new GroupVideoUneViewHolder(context, relLayout_view_overlay, view);

        } else if (viewType == SINGLE_BOTTOM_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_share_single_bottom_view_video_une, parent, false);
            return new GroupViewHolderSingleBottomVideoUne(context, relLayout_view_overlay, view);

        } else if (viewType == SINGLE_TOP_VIDEO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_share_single_top_view_video_une, parent, false);
            return new GroupViewSharedSingleTopVideoUne(context, relLayout_view_overlay, view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_share_top_bottom_view_video_une, parent, false);
            return new GroupViewHolderTopBottomVideoUne(context, relLayout_view_overlay, view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = getItem(position);
        final int itemViewType = getItemViewType(position);

        if (itemViewType == SIMPLE_VIDEO) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.bindVideo(model);



            videoViewHolder.rel_vol.setOnClickListener(view -> {
                if (videoViewHolder.player != null) {
                    if (videoViewHolder.player.getVolume() == 0) {
                        isMute = false;
                        videoViewHolder.player.setVolume(1);
                        videoViewHolder.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        videoViewHolder.player.setVolume(0);
                        videoViewHolder.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (videoViewHolder.player != null) {
                            if (isMute) {
                                try {
                                    videoViewHolder.player.setVolume(0);
                                    videoViewHolder.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    videoViewHolder.player.setVolume(1);
                                    videoViewHolder.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });

        } else if (itemViewType == GROUP_VIDEO) {
            GroupVideoUneViewHolder groupVideoUneViewHolder = (GroupVideoUneViewHolder) holder;
            groupVideoUneViewHolder.bindVideo(model);

            groupVideoUneViewHolder.rel_vol.setOnClickListener(view -> {
                if (groupVideoUneViewHolder.player != null) {
                    if (groupVideoUneViewHolder.player.getVolume() == 0) {
                        isMute = false;
                        groupVideoUneViewHolder.player.setVolume(1);
                        groupVideoUneViewHolder.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        groupVideoUneViewHolder.player.setVolume(0);
                        groupVideoUneViewHolder.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (groupVideoUneViewHolder.player != null) {
                            if (isMute) {
                                try {
                                    groupVideoUneViewHolder.player.setVolume(0);
                                    groupVideoUneViewHolder.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    groupVideoUneViewHolder.player.setVolume(1);
                                    groupVideoUneViewHolder.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });

        } else if (itemViewType == SHARING_VIDEO) {
            ViewShareOnMiiokyVideoUne viewShareOnMiiokyVideoUne = (ViewShareOnMiiokyVideoUne) holder;
            viewShareOnMiiokyVideoUne.bindVideo(model);

            viewShareOnMiiokyVideoUne.rel_vol.setOnClickListener(view -> {
                if (viewShareOnMiiokyVideoUne.player != null) {
                    if (viewShareOnMiiokyVideoUne.player.getVolume() == 0) {
                        isMute = false;
                        viewShareOnMiiokyVideoUne.player.setVolume(1);
                        viewShareOnMiiokyVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        viewShareOnMiiokyVideoUne.player.setVolume(0);
                        viewShareOnMiiokyVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (viewShareOnMiiokyVideoUne.player != null) {
                            if (isMute) {
                                try {
                                    viewShareOnMiiokyVideoUne.player.setVolume(0);
                                    viewShareOnMiiokyVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    viewShareOnMiiokyVideoUne.player.setVolume(1);
                                    viewShareOnMiiokyVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });

        } else if (itemViewType == SINGLE_BOTTOM_VIDEO) {
            GroupViewHolderSingleBottomVideoUne singleBottomVideoUne = (GroupViewHolderSingleBottomVideoUne) holder;
            singleBottomVideoUne.bindVideo(model);

            singleBottomVideoUne.rel_vol.setOnClickListener(view -> {
                if (singleBottomVideoUne.player != null) {
                    if (singleBottomVideoUne.player.getVolume() == 0) {
                        isMute = false;
                        singleBottomVideoUne.player.setVolume(1);
                        singleBottomVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        singleBottomVideoUne.player.setVolume(0);
                        singleBottomVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (singleBottomVideoUne.player != null) {
                            if (isMute) {
                                try {
                                    singleBottomVideoUne.player.setVolume(0);
                                    singleBottomVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    singleBottomVideoUne.player.setVolume(1);
                                    singleBottomVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });

        } else if (itemViewType == SINGLE_TOP_VIDEO) {
            GroupViewSharedSingleTopVideoUne singleTopVideoUne = (GroupViewSharedSingleTopVideoUne) holder;
            singleTopVideoUne.bindVideo(model);

            singleTopVideoUne.rel_vol.setOnClickListener(view -> {
                if (singleTopVideoUne.player != null) {
                    if (singleTopVideoUne.player.getVolume() == 0) {
                        isMute = false;
                        singleTopVideoUne.player.setVolume(1);
                        singleTopVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        singleTopVideoUne.player.setVolume(0);
                        singleTopVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (singleTopVideoUne.player != null) {
                            if (isMute) {
                                try {
                                    singleTopVideoUne.player.setVolume(0);
                                    singleTopVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    singleTopVideoUne.player.setVolume(1);
                                    singleTopVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });

        } else if (itemViewType == TOP_BOTTOM_VIDEO) {
            GroupViewHolderTopBottomVideoUne topBottomVideoUne = (GroupViewHolderTopBottomVideoUne) holder;
            topBottomVideoUne.bindVideo(model);

            topBottomVideoUne.rel_vol.setOnClickListener(view -> {
                if (topBottomVideoUne.player != null) {
                    if (topBottomVideoUne.player.getVolume() == 0) {
                        isMute = false;
                        topBottomVideoUne.player.setVolume(1);
                        topBottomVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);

                    } else {
                        isMute = true;
                        topBottomVideoUne.player.setVolume(0);
                        topBottomVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                    }
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (topBottomVideoUne.player != null) {
                            if (isMute) {
                                try {
                                    topBottomVideoUne.player.setVolume(0);
                                    topBottomVideoUne.img_vol.setImageResource(R.drawable.ic_mute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                }

                            } else {
                                try {
                                    topBottomVideoUne.player.setVolume(1);
                                    topBottomVideoUne.img_vol.setImageResource(R.drawable.ic_unmute);
                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onScrollStateChanged: "+e);
                                }
                            }
                        }
                    }
                }
            });
        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isVideoUne_shared())
            return SHARING_VIDEO;
        else if (getItem(position).isVideoUne())
            return SIMPLE_VIDEO;
        else if (getItem(position).isGroup_videoUne())
            return GROUP_VIDEO;
        else if (getItem(position).isGroup_share_single_bottom_video_une())
            return SINGLE_BOTTOM_VIDEO;
        else if (getItem(position).isGroup_share_single_top_video_une())
            return SINGLE_TOP_VIDEO;
        else
            return TOP_BOTTOM_VIDEO;
    }
}

