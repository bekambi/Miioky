package com.bekambimouen.miiokychallenge.comment_share.single_top.img_and_vid_double.bottomsheet_adapter;

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
import com.bekambimouen.miiokychallenge.comment_share.viewholder.CommentImageDoubleViewHolder;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CommentShareSingleTopImageDoubleBottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "GroupCommentShareSingleBottomImageDoubleAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final ArrayList<Comment> list;
    private final BattleModel battleModel;
    private final ItemImageUneClickListener itemImageUneClickListener;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;
    private final LayoutInflater inflater;

    // for notification
    private final String post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
            recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id;

    public CommentShareSingleTopImageDoubleBottomSheetAdapter(AppCompatActivity context, ArrayList<Comment> list, BattleModel battleModel,
                                                              ItemImageUneClickListener itemImageUneClickListener, ProgressBar loading_progressBar,
                                                              String post_id_field, String category_of_post, String post_type,
                                                              String post_id, String recyclerview_photo_id_upload,
                                                              String recyclerview_fieldLike_upload, String admin_master, String the_user_who_posted_id,
                                                              String group_id,
                                                              OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.battleModel = battleModel;
        this.itemImageUneClickListener = itemImageUneClickListener;
        this.loading_progressBar = loading_progressBar;
        this.post_id_field = post_id_field;
        this.category_of_post = category_of_post;
        this.post_type = post_type;
        this.post_id = post_id;
        this.recyclerview_photo_id_upload = recyclerview_photo_id_upload;
        this.recyclerview_fieldLike_upload = recyclerview_fieldLike_upload;
        this.admin_master = admin_master;
        this.the_user_who_posted_id = the_user_who_posted_id;
        this.group_id = group_id;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_comment, parent, false);
        return new CommentImageDoubleViewHolder(context, battleModel, itemImageUneClickListener,
                post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, "from_bottom_sheet", relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = list.get(position);

        CommentImageDoubleViewHolder commentImageDouble = (CommentImageDoubleViewHolder) holder;
        commentImageDouble.bindView(comment);

        if (battleModel.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            commentImageDouble.menu_item.setVisibility(View.VISIBLE);
            commentImageDouble.menu_item.setOnClickListener(view -> {
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
                                .child(battleModel.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(comment.getCommentKey())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_battle))
                                .child(battleModel.getUser_id())
                                .child(battleModel.getPhoto_id())
                                .child(context.getString(R.string.field_comment_share))
                                .child(comment.getCommentKey())
                                .updateChildren(map).addOnSuccessListener(unused1 -> {
                                    removeAt(holder.getLayoutPosition());
                                });
                    }
                });
            });
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

