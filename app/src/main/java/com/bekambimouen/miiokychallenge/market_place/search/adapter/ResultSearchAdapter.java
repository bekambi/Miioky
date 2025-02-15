package com.bekambimouen.miiokychallenge.market_place.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.adapter.viewholder.MarketExplorerProductViewHolder;
import com.bekambimouen.miiokychallenge.market_place.adapter.viewholder.MarketExplorerViewHolder;
import com.bekambimouen.miiokychallenge.market_place.adapter.viewholder.StoreExplorerViewHolder;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;

import java.util.List;

public class ResultSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int STORE = 1;
    public static final int STORE_PRODUCT = 2;
    public static final int PRODUCT = 3;

    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public ResultSearchAdapter(AppCompatActivity context, List<MarketModel> list, ProgressBar progressBar,
                               RelativeLayout relLayout_view_overlay) {
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

        } else if (viewType == STORE_PRODUCT){
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_market_explorer_product,parent,false);
            return new MarketExplorerProductViewHolder(context, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MarketModel model = list.get(position);

        final int itemViewType = getItemViewType(position);
        if (itemViewType == STORE) {
            StoreExplorerViewHolder storeExplorerViewHolder = (StoreExplorerViewHolder) holder;
            storeExplorerViewHolder.bind(model);

        } else if (itemViewType == PRODUCT){
            MarketExplorerViewHolder marketExplorerViewHolder = (MarketExplorerViewHolder) holder;
            marketExplorerViewHolder.bind(model);

        } else if (itemViewType == STORE_PRODUCT){
            MarketExplorerProductViewHolder marketExplorerProduct = (MarketExplorerProductViewHolder) holder;
            marketExplorerProduct.bind(model);
        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (
                (list.get(position).isStore() && !list.get(position).isStore_product())
                        || (list.get(position).isRestaurant() && !list.get(position).isStore_product())
                        || (list.get(position).isBakery() && !list.get(position).isStore_product())
        )
            return STORE;
        else if (list.get(position).isSell() || list.get(position).isLocation())
            return PRODUCT;
        else if (list.get(position).isStore_product())
            return STORE_PRODUCT;
        else
            return -1;
    }
}

