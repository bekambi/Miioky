package com.bekambimouen.miiokychallenge.groups.shared_in_group.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.share_publication.SharePublicationInGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ShareInGroupAdapter extends RecyclerView.Adapter<ShareInGroupAdapter.MyViewHolder>
        implements Filterable {
    private static final String TAG = "ShareInGroupAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    // vars
    private final AppCompatActivity context;
    private final List<UserGroups> list;
    private List<UserGroups> groupListFiltered;
    private final BattleModel model;
    private final String value;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // share on group
    private final boolean group_share_single_bottom_circle_image;
    private final boolean group_share_single_bottom_image_double;
    private final boolean group_share_single_bottom_image_une;
    private final boolean group_share_single_bottom_recycler;
    private final boolean group_share_single_bottom_response_image_double;
    private final boolean group_share_single_bottom_response_video_double;
    private final boolean group_share_single_bottom_video_double;
    private final boolean group_share_single_bottom_video_une;

    private final boolean group_share_top_bottom_circle_image;
    private final boolean group_share_top_bottom_image_double;
    private final boolean group_share_top_bottom_image_une;
    private final boolean group_share_top_bottom_recycler;
    private final boolean group_share_top_bottom_response_image_double;
    private final boolean group_share_top_bottom_response_video_double;
    private final boolean group_share_top_bottom_video_double;
    private final boolean group_share_top_bottom_video_une;

    private final boolean group_share_single_top_circle_image;
    private final boolean group_share_single_top_image_double;
    private final boolean group_share_single_top_image_une;
    private final boolean group_share_single_top_recycler;
    private final boolean group_share_single_top_response_image_double;
    private final boolean group_share_single_top_response_video_double;
    private final boolean group_share_single_top_video_double;
    private final boolean group_share_single_top_video_une;

    private final boolean group_circle_image;
    private final boolean group_image_double;
    private final boolean group_image_une;
    private final boolean group_recycler;
    private final boolean group_response_image_double;
    private final boolean group_response_video_double;
    private final boolean group_video_double;
    private final boolean group_video_une;

    // share on Miioky
    private final boolean user_profile_shared;
    private final boolean recyclerItem_shared;
    private final boolean imageUne_shared;
    private final boolean videoUne_shared;
    private final boolean imageDouble_shared;
    private final boolean videoDouble_shared;
    private final boolean reponseImageDouble_shared;
    private final boolean reponseVideoDouble_shared;

    private final boolean circle_image;
    private final boolean image_double;
    private final boolean image_une;
    private final boolean recycler;
    private final boolean response_image_double;
    private final boolean response_video_double;
    private final boolean video_double;
    private final boolean video_une;

    // vars
    private String string_groupName, string2;

    public ShareInGroupAdapter(AppCompatActivity context, List<UserGroups> list, BattleModel model,
                               String value,
                               boolean group_share_single_bottom_circle_image, boolean group_share_single_bottom_image_double,
                               boolean group_share_single_bottom_image_une, boolean group_share_single_bottom_recycler,
                               boolean group_share_single_bottom_response_image_double, boolean group_share_single_bottom_response_video_double,
                               boolean group_share_single_bottom_video_double, boolean group_share_single_bottom_video_une,

                               boolean group_share_single_top_circle_image, boolean group_share_single_top_image_double,
                               boolean group_share_single_top_image_une, boolean group_share_single_top_recycler,
                               boolean group_share_single_top_response_image_double, boolean group_share_single_top_response_video_double,
                               boolean group_share_single_top_video_double, boolean group_share_single_top_video_une,

                               boolean group_share_top_bottom_circle_image, boolean group_share_top_bottom_image_double,
                               boolean group_share_top_bottom_image_une, boolean group_share_top_bottom_recycler,
                               boolean group_share_top_bottom_response_image_double, boolean group_share_top_bottom_response_video_double,
                               boolean group_share_top_bottom_video_double, boolean group_share_top_bottom_video_une,

                               boolean user_profile_shared, boolean imageUne_shared, boolean imageDouble_shared, boolean recyclerItem_shared,
                               boolean reponseImageDouble_shared, boolean reponseVideoDouble_shared, boolean videoDouble_shared,
                               boolean videoUne_shared,

                               boolean group_circle_image, boolean group_image_double, boolean group_image_une, boolean group_recycler,
                               boolean group_response_image_double, boolean group_response_video_double, boolean group_video_double,
                               boolean group_video_une, boolean circle_image, boolean image_double, boolean image_une, boolean recycler,
                               boolean response_image_double, boolean response_video_double, boolean video_double, boolean video_une,

                               ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.groupListFiltered = list;
        this.model = model;
        this.value = value;
        this.group_share_single_bottom_circle_image = group_share_single_bottom_circle_image;
        this.group_share_single_bottom_image_double = group_share_single_bottom_image_double;
        this.group_share_single_bottom_image_une = group_share_single_bottom_image_une;
        this.group_share_single_bottom_recycler = group_share_single_bottom_recycler;
        this.group_share_single_bottom_response_image_double = group_share_single_bottom_response_image_double;
        this.group_share_single_bottom_response_video_double = group_share_single_bottom_response_video_double;
        this.group_share_single_bottom_video_double = group_share_single_bottom_video_double;
        this.group_share_single_bottom_video_une = group_share_single_bottom_video_une;

        this.group_share_single_top_circle_image = group_share_single_top_circle_image;
        this.group_share_single_top_image_double = group_share_single_top_image_double;
        this.group_share_single_top_image_une = group_share_single_top_image_une;
        this.group_share_single_top_recycler = group_share_single_top_recycler;
        this.group_share_single_top_response_image_double = group_share_single_top_response_image_double;
        this.group_share_single_top_response_video_double = group_share_single_top_response_video_double;
        this.group_share_single_top_video_double = group_share_single_top_video_double;
        this.group_share_single_top_video_une = group_share_single_top_video_une;

        this.group_share_top_bottom_circle_image = group_share_top_bottom_circle_image;
        this.group_share_top_bottom_image_double = group_share_top_bottom_image_double;
        this.group_share_top_bottom_image_une = group_share_top_bottom_image_une;
        this.group_share_top_bottom_recycler = group_share_top_bottom_recycler;
        this.group_share_top_bottom_response_image_double = group_share_top_bottom_response_image_double;
        this.group_share_top_bottom_response_video_double = group_share_top_bottom_response_video_double;
        this.group_share_top_bottom_video_double = group_share_top_bottom_video_double;
        this.group_share_top_bottom_video_une = group_share_top_bottom_video_une;

        this.user_profile_shared = user_profile_shared;
        this.imageDouble_shared = imageDouble_shared;
        this.imageUne_shared = imageUne_shared;
        this.recyclerItem_shared = recyclerItem_shared;
        this.reponseImageDouble_shared = reponseImageDouble_shared;
        this.reponseVideoDouble_shared = reponseVideoDouble_shared;
        this.videoDouble_shared = videoDouble_shared;
        this.videoUne_shared = videoUne_shared;

        this.group_circle_image = group_circle_image;
        this.group_image_double = group_image_double;
        this.group_image_une = group_image_une;
        this.group_recycler = group_recycler;
        this.group_response_image_double = group_response_image_double;
        this.group_response_video_double = group_response_video_double;
        this.group_video_double = group_video_double;
        this.group_video_une = group_video_une;

        this.circle_image = circle_image;
        this.image_double = image_double;
        this.image_une = image_une;
        this.recycler = recycler;
        this.response_image_double = response_image_double;
        this.response_video_double = response_video_double;
        this.video_double = video_double;
        this.video_une = video_une;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_group_share_in_pub_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserGroups groups = groupListFiltered.get(position);
        holder.bind(groups);

        holder.itemView.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, SharePublicationInGroup.class);
            Gson gson = new Gson();
            String gsonModel = gson.toJson(model);
            String gsonGroup = gson.toJson(groups);
            intent.putExtra("model", gsonModel);
            intent.putExtra("userGroup", gsonGroup);
            intent.putExtra("value", value);

            intent.putExtra("group_share_single_bottom_circle_image", group_share_single_bottom_circle_image);
            intent.putExtra("group_share_single_bottom_image_double", group_share_single_bottom_image_double);
            intent.putExtra("group_share_single_bottom_image_une", group_share_single_bottom_image_une);
            intent.putExtra("group_share_single_bottom_recycler", group_share_single_bottom_recycler);
            intent.putExtra("group_share_single_bottom_response_image_double", group_share_single_bottom_response_image_double);
            intent.putExtra("group_share_single_bottom_response_video_double", group_share_single_bottom_response_video_double);
            intent.putExtra("group_share_single_bottom_video_double", group_share_single_bottom_video_double);
            intent.putExtra("group_share_single_bottom_video_une", group_share_single_bottom_video_une);

            intent.putExtra("group_share_single_top_circle_image", group_share_single_top_circle_image);
            intent.putExtra("group_share_single_top_image_double", group_share_single_top_image_double);
            intent.putExtra("group_share_single_top_image_une", group_share_single_top_image_une);
            intent.putExtra("group_share_single_top_recycler", group_share_single_top_recycler);
            intent.putExtra("group_share_single_top_response_image_double", group_share_single_top_response_image_double);
            intent.putExtra("group_share_single_top_response_video_double", group_share_single_top_response_video_double);
            intent.putExtra("group_share_single_top_video_double", group_share_single_top_video_double);
            intent.putExtra("group_share_single_top_video_une", group_share_single_top_video_une);

            intent.putExtra("group_share_top_bottom_circle_image", group_share_top_bottom_circle_image);
            intent.putExtra("group_share_top_bottom_image_double", group_share_top_bottom_image_double);
            intent.putExtra("group_share_top_bottom_image_une", group_share_top_bottom_image_une);
            intent.putExtra("group_share_top_bottom_recycler", group_share_top_bottom_recycler);
            intent.putExtra("group_share_top_bottom_response_image_double", group_share_top_bottom_response_image_double);
            intent.putExtra("group_share_top_bottom_response_video_double", group_share_top_bottom_response_video_double);
            intent.putExtra("group_share_top_bottom_video_double", group_share_top_bottom_video_double);
            intent.putExtra("group_share_top_bottom_video_une", group_share_top_bottom_video_une);

            intent.putExtra("user_profile_shared", user_profile_shared);
            intent.putExtra("imageDouble_shared", imageDouble_shared);
            intent.putExtra("imageUne_shared", imageUne_shared);
            intent.putExtra("recyclerItem_shared", recyclerItem_shared);
            intent.putExtra("reponseImageDouble_shared", reponseImageDouble_shared);
            intent.putExtra("reponseVideoDouble_shared", reponseVideoDouble_shared);
            intent.putExtra("videoDouble_shared", videoDouble_shared);
            intent.putExtra("videoUne_shared", videoUne_shared);

            intent.putExtra("group_circle_image", group_circle_image);
            intent.putExtra("group_image_double", group_image_double);
            intent.putExtra("group_image_une", group_image_une);
            intent.putExtra("group_recycler", group_recycler);
            intent.putExtra("group_response_image_double", group_response_image_double);
            intent.putExtra("group_response_video_double", group_response_video_double);
            intent.putExtra("group_video_double", group_video_double);
            intent.putExtra("group_video_une", group_video_une);

            intent.putExtra("circle_image", circle_image);
            intent.putExtra("image_double", image_double);
            intent.putExtra("image_une", image_une);
            intent.putExtra("recycler", recycler);
            intent.putExtra("response_image_double", response_image_double);
            intent.putExtra("response_video_double", response_video_double);
            intent.putExtra("video_double", video_double);
            intent.putExtra("video_une", video_une);
            context.startActivity(intent);

        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == groupListFiltered.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) context;
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
            loading_progressBar.setVisibility(View.GONE);
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        if(groupListFiltered==null) return 0;
        return groupListFiltered.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

                if (charString.isEmpty()) {
                    groupListFiltered = list;
                } else {
                    List<UserGroups> filteredList = new ArrayList<>();
                    for (UserGroups row : list) {
                        string_groupName = Normalizer.normalize(row.getGroup_name().toLowerCase(), Normalizer.Form.NFD);
                        string_groupName = string_groupName.replaceAll("[^\\p{ASCII}]", "");

                        if (string_groupName.toLowerCase().contains(charString)) {
                            filteredList.add(row);
                        }
                    }

                    groupListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupListFiltered;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupListFiltered = (ArrayList<UserGroups>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView group_profile_photo;
        private final TextView group_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
            group_name = itemView.findViewById(R.id.group_name);
        }

        public void bind(UserGroups groups) {
            //get the group profile image and group name
            group_name.setText(groups.getGroup_name());

            if (!context.isFinishing()) {
                Glide.with(context.getApplicationContext())
                        .load(groups.getProfile_photo())
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(group_profile_photo);
            }
        }
    }
}

