package com.bekambimouen.miiokychallenge.comment_share.comment_response.adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.comment_share.comment_response.ViewHolderShareFirstCommentHeader;
import com.bekambimouen.miiokychallenge.comment_share.comment_response.ViewHolderShareResponse_image_une;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentShareResponseImageUneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CommentShareResponseImageUneAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    // constants
    private static final int FIRST_COMMENT = 1;
    private static final int COMMENT = 2;

    private final AppCompatActivity context;
    private final ArrayList<CommentResponse> list;
    private final LayoutInflater inflater;
    private final BattleModel model;
    private final String comment_key;
    private final ChildItemClickListener mChildItemClickListener;

    private final BattleModel comment_recycler_child;
    private final BattleModel comment_reponse_image_double;
    private final Comment commentModel;
    private final String userID;
    private final String comment;
    private final String media_comment_url;
    private final String media_comment_thumbnail;
    private final String recyclerview_photo_id;
    private final String recyclerview_fieldLike;
    private final String photo_id;
    private final long time;
    private final MyEditText mComment;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public CommentShareResponseImageUneAdapter(AppCompatActivity context, ArrayList<CommentResponse> list,
                                               BattleModel model, String comment_key, ChildItemClickListener mChildItemClickListener,
                                               BattleModel comment_recycler_child,
                                               BattleModel comment_reponse_image_double,
                                               Comment commentModel, String userID, String comment, String media_comment_url, String media_comment_thumbnail,
                                               String recyclerview_photo_id, String recyclerview_fieldLike, String photo_id,
                                               long time, MyEditText mComment, ProgressBar loading_progressBar,
                                               OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.model = model;
        this.comment_key = comment_key;
        this.mChildItemClickListener = mChildItemClickListener;
        this.comment_recycler_child = comment_recycler_child;
        this.comment_reponse_image_double = comment_reponse_image_double;
        this.commentModel = commentModel;
        this.userID = userID;
        this.comment = comment;
        this.media_comment_url = media_comment_url;
        this.media_comment_thumbnail = media_comment_thumbnail;
        this.recyclerview_photo_id = recyclerview_photo_id;
        this.recyclerview_fieldLike = recyclerview_fieldLike;
        this.photo_id = photo_id;
        this.time = time;
        this.mComment = mComment;
        this.loading_progressBar = loading_progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        this.list.remove(null);
        this.list.add(0, null);

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == FIRST_COMMENT) {
            view = inflater.inflate(R.layout.layout_comment_first_comment, parent, false);
            return new ViewHolderShareFirstCommentHeader(context, model, comment_recycler_child, comment_reponse_image_double,
                    commentModel, userID, comment, media_comment_url,
                    media_comment_thumbnail, comment_key, recyclerview_photo_id, recyclerview_fieldLike, photo_id,
                    time, mComment, relLayout_view_overlay, view);

        } else {
            view = inflater.inflate(R.layout.layout_comment_response, parent, false);
            return new ViewHolderShareResponse_image_une(context, model, comment_key, mChildItemClickListener, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentResponse comment = list.get(position);
        int view_type = getItemViewType(position);

        if (view_type == COMMENT) {
            ViewHolderShareResponse_image_une image_une = (ViewHolderShareResponse_image_une) holder;
            image_une.bindView(comment);

            if (model.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                image_une.menu_item.setVisibility(View.VISIBLE);
                image_une.menu_item.setOnClickListener(view -> {
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
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_want_to_delete_this_comment)));

                    negativeButton.setOnClickListener(view1 -> d.dismiss());
                    positiveButton.setOnClickListener(view1 -> {
                        d.dismiss();
                        // internet control
                        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                        if (!isConnected) {
                            Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

                        } else {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", "yes");

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_battle_media_share))
                                    .child(photo_id)
                                    .child(context.getString(R.string.field_comment_share))
                                    .child(comment_key)
                                    .child(context.getString(R.string.field_comments))
                                    .child(comment.getCommentKey())
                                    .updateChildren(map);


                            //insert into user_photos node
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_battle))
                                    .child(model.getUser_id())
                                    .child(photo_id)
                                    .child(context.getString(R.string.field_comment_share))
                                    .child(comment_key)
                                    .child(context.getString(R.string.field_comments))
                                    .child(comment.getCommentKey())
                                    .updateChildren(map).addOnSuccessListener(unused1 -> {
                                        removeAt(holder.getLayoutPosition());
                                    });
                        }
                    });
                });
            }
        }

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
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

