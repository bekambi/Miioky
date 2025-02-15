package com.bekambimouen.miiokychallenge.market_place.comment.product_comment.adapter;

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
import com.bekambimouen.miiokychallenge.interfaces.MarketCommentListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.comment.product_comment.adapter.viewholder.ProductViewHolderComment;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.Comment;

import java.util.ArrayList;

public class ProductCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MarketCommentAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    // vars
    private final AppCompatActivity context;
    private final ArrayList<Comment> list;
    private final LayoutInflater inflater;
    private final MarketModel marketModel;
    private final MarketCommentListener marketCommentListener;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public ProductCommentAdapter(AppCompatActivity context, ArrayList<Comment> list, MarketModel marketModel,
                                 MarketCommentListener marketCommentListener, ProgressBar loading_progressBar,
                                 OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.marketModel = marketModel;
        this.marketCommentListener = marketCommentListener;
        this.loading_progressBar = loading_progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_comment, parent, false);
        return new ProductViewHolderComment(context, marketModel, marketCommentListener, "from_bottom_sheet", relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = list.get(position);

        ProductViewHolderComment productViewHolderComment = (ProductViewHolderComment) holder;
        productViewHolderComment.bindView(comment);

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
            loading_progressBar.setVisibility(View.GONE);
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    public void addComment(Comment comment) {
        this.list.add(0, comment);
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }
}

