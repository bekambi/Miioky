package com.bekambimouen.miiokychallenge.market_place.comment.store_comment.coment_response.adapter;

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
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.comment.store_comment.coment_response.StoreViewHolderFirstCommentHeader;
import com.bekambimouen.miiokychallenge.market_place.comment.store_comment.coment_response.StoreViewHolderResponse;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;

import java.util.ArrayList;

public class StoreCommentResponseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MarketCommentResponseAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    // constants
    private static final int FIRST_COMMENT = 1;
    private static final int COMMENT = 2;

    private final AppCompatActivity context;
    private final ArrayList<CommentResponse> list;
    private final LayoutInflater inflater;
    private final MarketModel marketModel;
    private final String comment_key;
    private final ChildItemClickListener mChildItemClickListener;

    private final Comment commentModel;
    private final String userID;
    private final String comment;
    private final String media_comment_url;
    private final String media_comment_thumbnail;
    private final long time;
    private final MyEditText mComment;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public StoreCommentResponseAdapter(AppCompatActivity context, ArrayList<CommentResponse> list, MarketModel marketModel,
                                       String comment_key, ChildItemClickListener mChildItemClickListener,
                                       Comment commentModel, String userID, String comment, String media_comment_url, String media_comment_thumbnail,
                                       long time, MyEditText mComment, ProgressBar loading_progressBar,
                                       OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.marketModel = marketModel;
        this.comment_key = comment_key;
        this.mChildItemClickListener = mChildItemClickListener;
        this.commentModel = commentModel;
        this.userID = userID;
        this.comment = comment;
        this.media_comment_url = media_comment_url;
        this.media_comment_thumbnail = media_comment_thumbnail;
        this.time = time;
        this.mComment = mComment;
        this.loading_progressBar = loading_progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        inflater = LayoutInflater.from(context);

        this.list.remove(null);
        this.list.add(0, null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == FIRST_COMMENT) {
            view = inflater.inflate(R.layout.layout_comment_first_comment, parent, false);
            return new StoreViewHolderFirstCommentHeader(context, marketModel, commentModel, userID, comment,
                    media_comment_url, media_comment_thumbnail, comment_key, time, mComment, relLayout_view_overlay, view);

        } else {
            view = inflater.inflate(R.layout.layout_comment_response, parent, false);
            return new StoreViewHolderResponse(context, marketModel, comment_key, mChildItemClickListener, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentResponse comment = list.get(position);
        int view_type = getItemViewType(position);

        if (view_type == COMMENT) {
            StoreViewHolderResponse storeViewHolderResponse = (StoreViewHolderResponse) holder;
            storeViewHolderResponse.bindView(comment);
        }

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

    public void addComment(CommentResponse comment) {
        this.list.add(1, comment);
        notifyItemInserted(1);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return FIRST_COMMENT;
        else
            return COMMENT;
    }
}

