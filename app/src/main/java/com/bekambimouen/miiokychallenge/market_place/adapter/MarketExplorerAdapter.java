package com.bekambimouen.miiokychallenge.market_place.adapter;

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
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.adapter.viewholder.MarketExplorerViewHolder;
import com.bekambimouen.miiokychallenge.market_place.adapter.viewholder.StoreExplorerViewHolder;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMarketImages;

import java.util.List;

public class MarketExplorerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MarketExplorerAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    public void setOnLoadMoreItemsListener(OnLoadMoreItemsListener listener) {
        this.mOnLoadMoreItemsListener = listener;
    }

    public static final int STORE = 1;
    public static final int PRODUCT = 2;

    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public MarketExplorerAdapter(AppCompatActivity context, List<MarketModel> list,
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
        if (viewType == STORE) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_store_explorer_item,parent,false);
            return new StoreExplorerViewHolder(context, relLayout_view_overlay, view);

        } else if (viewType == PRODUCT){
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_market_explorer_item,parent,false);
            return new MarketExplorerViewHolder(context, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MarketModel model = list.get(position);

        try {
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+1));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);
        if (itemViewType == STORE) {
            StoreExplorerViewHolder storeExplorerViewHolder = (StoreExplorerViewHolder) holder;
            storeExplorerViewHolder.bind(model);

        } else if (itemViewType == PRODUCT){
            MarketExplorerViewHolder marketExplorerViewHolder = (MarketExplorerViewHolder) holder;
            marketExplorerViewHolder.bind(model);
        }

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        if (reachedEndOfList(position))
            loadMoreData();
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData() {
        if (mOnLoadMoreItemsListener != null) {
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isStore() || list.get(position).isRestaurant() || list.get(position).isBakery())
            return STORE;
        else if (list.get(position).isSell() || list.get(position).isLocation())
            return PRODUCT;
        else
            return -1;
    }
}

