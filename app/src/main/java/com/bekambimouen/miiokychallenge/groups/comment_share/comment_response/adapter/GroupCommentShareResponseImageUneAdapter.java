package com.bekambimouen.miiokychallenge.groups.comment_share.comment_response.adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.bekambimouen.miiokychallenge.groups.comment_share.comment_response.GroupViewHolderFirstCommentShareHeader;
import com.bekambimouen.miiokychallenge.groups.comment_share.comment_response.GroupViewHolderShareResponse_image_une;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupCommentShareResponseImageUneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CommentResponseImageUneAdapter";

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

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupCommentShareResponseImageUneAdapter(AppCompatActivity context, ArrayList<CommentResponse> list,
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
        this.myRef = FirebaseDatabase.getInstance().getReference();
        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == FIRST_COMMENT) {
            view = inflater.inflate(R.layout.layout_comment_first_comment, parent, false);
            return new GroupViewHolderFirstCommentShareHeader(context, model, comment_recycler_child, comment_reponse_image_double,
                    commentModel, userID, comment, media_comment_url,
                    media_comment_thumbnail, comment_key, recyclerview_photo_id, recyclerview_fieldLike, photo_id,
                    time, mComment, relLayout_view_overlay, view);

        } else {
            view = inflater.inflate(R.layout.layout_comment_response, parent, false);
            return new GroupViewHolderShareResponse_image_une(context, model, comment_key, mChildItemClickListener, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentResponse comment = list.get(position);
        int view_type = getItemViewType(position);

        if (view_type == COMMENT) {
            GroupViewHolderShareResponse_image_une image_une = (GroupViewHolderShareResponse_image_une) holder;
            image_une.bindView(comment);

            // delete comment by the owner comment or admin and moderator
            deleteComment(comment, image_une.menu_item, image_une);
        }

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    // delete comment by the owner comment or admin and moderator
    private void deleteComment(CommentResponse comment, ImageView menu_item , RecyclerView.ViewHolder holder) {
        if (model.getUser_id().equals(user_id)
                || model.getAdmin_master().equals(user_id)) {
            commentToDelete(comment, menu_item, holder);

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(model.getGroup_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        if (follower.isAdminInGroup() || follower.isModerator()) {
                            commentToDelete(comment, menu_item, holder);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void commentToDelete(CommentResponse comment, ImageView menu_item , RecyclerView.ViewHolder holder) {
        menu_item.setVisibility(View.VISIBLE);
        menu_item.setOnClickListener(view -> {
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
                            .child(context.getString(R.string.dbname_group_media_share))
                            .child(photo_id)
                            .child(context.getString(R.string.field_comment_share))
                            .child(comment_key)
                            .child(context.getString(R.string.field_comments))
                            .child(comment.getCommentKey())
                            .updateChildren(map);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group))
                            .child(model.getGroup_id())
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

