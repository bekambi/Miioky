package com.bekambimouen.miiokychallenge.groups.child_recyclerview_share.comment.comment_response.adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.groups.comment_share.comment_response.GroupViewHolderRecyclerChildFirstCommentShareHeader;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview_share.comment.comment_response.GroupViewHolderShareResponse_recycler_child;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
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

public class GroupCommentShareResponseRecyclerChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "GroupCommentResponseRecyclerChildAdapter";
    // constants
    private static final int FIRST_COMMENT = 1;
    private static final int COMMENT = 2;

    private final AppCompatActivity context;
    private final ArrayList<CommentResponse> list;
    private final LayoutInflater inflater;
    private final BattleModel model;
    private final String recyclerview_photo_id;
    private final String recyclerview_fieldLike;
    private final String comment_key;
    private final Comment commentModel;
    private final String userID;
    private final ChildItemClickListener mChildItemClickListener;
    private final MyEditText mComment;
    private final String comment;
    private final long date_created;
    private final String url;
    private final String thumbnail;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupCommentShareResponseRecyclerChildAdapter(AppCompatActivity context, ArrayList<CommentResponse> list,
                                                         BattleModel model, String recyclerview_photo_id, String recyclerview_fieldLike,
                                                         String comment_key, Comment commentModel, String userID, ChildItemClickListener mChildItemClickListener,
                                                         MyEditText mComment, String comment, long date_created, String url,
                                                         String thumbnail, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.model = model;
        this.recyclerview_photo_id = recyclerview_photo_id;
        this.recyclerview_fieldLike = recyclerview_fieldLike;
        this.comment_key = comment_key;
        this.commentModel = commentModel;
        this.userID = userID;
        this.mChildItemClickListener = mChildItemClickListener;
        this.mComment = mComment;
        this.comment = comment;
        this.date_created = date_created;
        this.url = url;
        this.thumbnail = thumbnail;
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
            return new GroupViewHolderRecyclerChildFirstCommentShareHeader(context, model, commentModel, comment_key, userID,
                    recyclerview_photo_id, recyclerview_fieldLike, mComment, comment, date_created, url, thumbnail, relLayout_view_overlay, view);

        } else {
            view = inflater.inflate(R.layout.layout_comment_response, parent, false);
            return new GroupViewHolderShareResponse_recycler_child(context, model, recyclerview_photo_id, recyclerview_fieldLike,
                    comment_key, mChildItemClickListener, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentResponse comment = list.get(position);
        int view_type = getItemViewType(position);

        if (view_type == COMMENT) {
            GroupViewHolderShareResponse_recycler_child response_recycler_child = (GroupViewHolderShareResponse_recycler_child) holder;
            response_recycler_child.bindView(comment);

            // delete comment by the owner comment or admin and moderator
            deleteComment(comment, response_recycler_child.menu_item, response_recycler_child);
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
                            .child(model.getPhoto_id())
                            .child(context.getString(R.string.field_child_comments_share))
                            .child(recyclerview_photo_id)
                            .child(context.getString(R.string.field_comments))
                            .child(comment_key)
                            .child(context.getString(R.string.field_comments))
                            .child(comment.getCommentKey())
                            .updateChildren(map);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group))
                            .child(model.getGroup_id())
                            .child(model.getPhoto_id())
                            .child(context.getString(R.string.field_child_comments_share))
                            .child(recyclerview_photo_id)
                            .child(context.getString(R.string.field_comments))
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

