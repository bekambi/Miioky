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
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.GroupViewOnlyVideos;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GridVideosAdapter extends RecyclerView.Adapter<GridVideosAdapter.ViewHolder> {
    private static final String TAG = "GridVideosAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final ArrayList<BattleModel> list;
    private final UserGroups user_groups;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public GridVideosAdapter(AppCompatActivity context, ArrayList<BattleModel> list, UserGroups user_groups,
                             ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.user_groups = user_groups;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_grid_video_fun, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BattleModel model = list.get(position);
        holder.bindVideo(model);
        holder.itemView.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            String myGson = gson.toJson(user_groups);
            String myGson2 = gson.toJson(model);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupViewOnlyVideos.class);
            intent.putExtra("user_groups", myGson);
            intent.putExtra("battle_model", myGson2);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageview);
        }

        void bindVideo(BattleModel model) {
            if (!context.isFinishing()) {

                Glide.with(context)
                        .asBitmap()
                        .load(model.getThumbnail())
                        .centerCrop()
                        .into(thumbnail);
            }
        }
    }
}

