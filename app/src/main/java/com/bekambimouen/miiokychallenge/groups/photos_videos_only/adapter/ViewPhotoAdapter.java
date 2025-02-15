package com.bekambimouen.miiokychallenge.groups.photos_videos_only.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CirclePagerIndicatorBlackLayoutDecoration;
import com.bekambimouen.miiokychallenge.groups.adapter.PhotosRecyclerViewAdapter;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bumptech.glide.Glide;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPhotoAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {

    private static final int GROUP_RECYCLER_ITEM = 1;
    private static final int GROUP_IMAGE_UNE = 2;
    private static final int GROUP_COVER_IMAGE = 3;
    private static final int GROUP_COVER_BACK_PROFILE = 4;
    private static final int GROUP_IMAGE_DOUBLE = 5;
    private static final int GROUP_RESPONSE_IMG_DOUBLE = 7;

    private final AppCompatActivity context;
    private final ProgressBar progressBar;

    public ViewPhotoAdapter(AppCompatActivity context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == GROUP_COVER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_photo, parent, false);
            return new GroupGridCoverImage(view);

        } else if (viewType == GROUP_COVER_BACK_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_photo, parent, false);
            return new GroupGridCoverImage(view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_image_recycler, parent, false);
            return new RecyclerItem(view);

        } else if (viewType == GROUP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_photo, parent, false);
            return new ImageUneItem(view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_image_recycler, parent, false);
            return new GroupImageDouble(view);

        } else if (viewType == GROUP_RESPONSE_IMG_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_image_recycler, parent, false);
            return new GroupResponseImageDouble(view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = getItem(position);
        final int itemViewType = getItemViewType(position);
        if (itemViewType == GROUP_RECYCLER_ITEM) {
            RecyclerItem recyclerItem = (RecyclerItem) holder;
            recyclerItem.bindRecyclerView(model);

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            ImageUneItem imageUneItem = (ImageUneItem) holder;
            imageUneItem.bindImageUne(model);

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupGridCoverImage groupGridCoverImage = (GroupGridCoverImage) holder;
            groupGridCoverImage.bindImageUne(model);

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupGridCoverImage gridCoverBackgroundProfile = (GroupGridCoverImage) holder;
            gridCoverBackgroundProfile.bindImageUne(model);

        } else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDouble groupImageDouble = (GroupImageDouble) holder;
            groupImageDouble.bindRecyclerView(model);

        } else if (itemViewType == GROUP_RESPONSE_IMG_DOUBLE) {
            GroupResponseImageDouble groupResponseImageDouble = (GroupResponseImageDouble) holder;
            groupResponseImageDouble.bindRecyclerView(model);

        }

        // hide progressbar
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isGroup_cover_profile_photo())
            return GROUP_COVER_IMAGE;
        else if (getItem(position).isGroup_cover_background_profile_photo())
            return GROUP_COVER_BACK_PROFILE;
        else if (getItem(position).isGroup_recyclerItem())
            return GROUP_RECYCLER_ITEM;
        else if (getItem(position).isGroup_imageUne())
            return GROUP_IMAGE_UNE;
        else if (getItem(position).isGroup_imageDouble())
            return GROUP_IMAGE_DOUBLE;
        else if (getItem(position).isGroup_response_imageDouble())
            return GROUP_RESPONSE_IMG_DOUBLE;
        else
            return -1;
    }

    public class RecyclerItem extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView numerateur, denominateur;

        // vars
        private final LinearLayoutManager manager;
        private final List<String> modelList;

        public RecyclerItem(@NonNull View itemView) {
            super(itemView);
            numerateur = itemView.findViewById(R.id.numerateur);
            denominateur = itemView.findViewById(R.id.denominateur);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            modelList = new ArrayList<>();

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
            manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(manager);
        }

        void bindRecyclerView(BattleModel model) {
            modelList.clear();
            if (!model.getUrli().isEmpty()) {
                modelList.add(model.getUrli());

                if (!model.getUrlii().isEmpty()) {
                    modelList.add(model.getUrlii());

                    if (!model.getUrliii().isEmpty()) {
                        modelList.add(model.getUrliii());

                        if (!model.getUrliv().isEmpty()) {
                            modelList.add(model.getUrliv());

                            if (!model.getUrlv().isEmpty()) {
                                modelList.add(model.getUrlv());

                                if (!model.getUrlvi().isEmpty()) {
                                    modelList.add(model.getUrlvi());

                                    if (!model.getUrlvii().isEmpty()) {
                                        modelList.add(model.getUrlvii());

                                        if (!model.getUrlviii().isEmpty()) {
                                            modelList.add(model.getUrlviii());

                                            if (!model.getUrlix().isEmpty()) {
                                                modelList.add(model.getUrlix());

                                                if (!model.getUrlx().isEmpty()) {
                                                    modelList.add(model.getUrlx());

                                                    if (!model.getUrlxi().isEmpty()) {
                                                        modelList.add(model.getUrlxi());

                                                        if (!model.getUrlxii().isEmpty()) {
                                                            modelList.add(model.getUrlxii());

                                                            if (!model.getUrlxiii().isEmpty()) {
                                                                modelList.add(model.getUrlxiii());

                                                                if (!model.getUrlxiv().isEmpty()) {
                                                                    modelList.add(model.getUrlxiv());

                                                                    if (!model.getUrlxv().isEmpty()) {
                                                                        modelList.add(model.getUrlxv());

                                                                        if (!model.getUrlxvi().isEmpty()) {
                                                                            modelList.add(model.getUrlxvi());

                                                                            if (!model.getUrlxvii().isEmpty()) {
                                                                                modelList.add(model.getUrlxvii());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
            }

            PhotosRecyclerViewAdapter adapter = new PhotosRecyclerViewAdapter(context, modelList);
            recyclerView.setAdapter(adapter);
            // le slidding: les pointillÃ©s du bas
            recyclerView.addItemDecoration(new CirclePagerIndicatorBlackLayoutDecoration());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int position = manager.findFirstVisibleItemPosition();
                        numerateur.setText(String.valueOf((position + 1)));
                    }
                }
            });

            denominateur.setText(String.valueOf(modelList.size()));

        }
    }

    public class ImageUneItem extends RecyclerView.ViewHolder {
        ImageView img;
        public ImageUneItem(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.main_image_une);
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
            img = itemView.findViewById(R.id.main_image_une);
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

    public class GroupImageDouble extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView numerateur, denominateur;

        // vars
        private final LinearLayoutManager manager;
        private final List<String> modelList;

        public GroupImageDouble(@NonNull View itemView) {
            super(itemView);
            numerateur = itemView.findViewById(R.id.numerateur);
            denominateur = itemView.findViewById(R.id.denominateur);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            modelList = new ArrayList<>();

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
            manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(manager);
        }

        void bindRecyclerView(BattleModel model) {
            modelList.add(model.getUrlUn());
            modelList.add(model.getUrlDeux());

            PhotosRecyclerViewAdapter adapter = new PhotosRecyclerViewAdapter(context, modelList);
            recyclerView.setAdapter(adapter);
            // le slidding: les pointillÃ©s du bas
            recyclerView.addItemDecoration(new CirclePagerIndicatorBlackLayoutDecoration());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int position = manager.findFirstVisibleItemPosition();
                        numerateur.setText(String.valueOf((position + 1)));
                    }
                }
            });

            denominateur.setText(String.valueOf(modelList.size()));

        }
    }

    public class GroupResponseImageDouble extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView numerateur, denominateur;

        // vars
        private final LinearLayoutManager manager;
        private final List<String> modelList;

        public GroupResponseImageDouble(@NonNull View itemView) {
            super(itemView);
            numerateur = itemView.findViewById(R.id.numerateur);
            denominateur = itemView.findViewById(R.id.denominateur);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            modelList = new ArrayList<>();

            manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(manager);

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
        }

        void bindRecyclerView(BattleModel model) {
            modelList.add(model.getReponse_url());
            modelList.add(model.getInvite_url());

            PhotosRecyclerViewAdapter adapter = new PhotosRecyclerViewAdapter(context, modelList);
            recyclerView.setAdapter(adapter);
            // le slidding: les pointillÃ©s du bas
            recyclerView.addItemDecoration(new CirclePagerIndicatorBlackLayoutDecoration());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int position = manager.findFirstVisibleItemPosition();
                        numerateur.setText(String.valueOf((position + 1)));
                    }
                }
            });

            denominateur.setText(String.valueOf(modelList.size()));
        }
    }
}

