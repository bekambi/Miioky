package com.bekambimouen.miiokychallenge.groups.comment_share.top_bottom.resp_img_and_vid.bottomsheet_adapter;

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
import com.bekambimouen.miiokychallenge.groups.comment_share.viewholder.GroupViewHolderCommentShareRespImgDouble;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.interfaces.ItemImageUneClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Comment;
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

public class GroupCommentShareTopBottomRespVidDoubleBottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    private static final String TAG = "CommentListAdapter";

    private final AppCompatActivity context;
    private final ArrayList<Comment> list;
    private final LayoutInflater inflater;
    private final BattleModel battleModel;
    private final ItemImageUneClickListener itemImageUneClickListener;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // for notification
    private final String post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
            recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupCommentShareTopBottomRespVidDoubleBottomSheetAdapter(AppCompatActivity context, ArrayList<Comment> list, BattleModel battleModel,
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
        this.myRef = FirebaseDatabase.getInstance().getReference();
        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_comment, parent, false);
        return new GroupViewHolderCommentShareRespImgDouble(context, battleModel, itemImageUneClickListener,
                post_id_field, category_of_post, post_type, post_id, recyclerview_photo_id_upload,
                recyclerview_fieldLike_upload, admin_master, the_user_who_posted_id, group_id, "from_bottom_sheet", relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = list.get(position);

        GroupViewHolderCommentShareRespImgDouble respImgDouble = (GroupViewHolderCommentShareRespImgDouble) holder;
        respImgDouble.bindView(comment);

        // delete comment by the owner comment or admin and moderator
        deleteComment(comment, respImgDouble.menu_item, respImgDouble);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    // delete comment by the owner comment or admin and moderator
    private void deleteComment(Comment comment, ImageView menu_item , RecyclerView.ViewHolder holder) {
        if (battleModel.getUser_id().equals(user_id)
                || battleModel.getAdmin_master().equals(user_id)) {
            commentToDelete(comment, menu_item, holder);

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(battleModel.getGroup_id())
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

    private void commentToDelete(Comment comment, ImageView menu_item , RecyclerView.ViewHolder holder) {
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
                            .child(battleModel.getPhoto_id())
                            .child(context.getString(R.string.field_comment_share))
                            .child(comment.getCommentKey())
                            .updateChildren(map);

                    //insert into user_photos node
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group))
                            .child(battleModel.getGroup_id())
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

