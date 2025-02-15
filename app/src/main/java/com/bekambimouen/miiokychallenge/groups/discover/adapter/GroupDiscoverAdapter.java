package com.bekambimouen.miiokychallenge.groups.discover.adapter;

import android.app.Dialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.discover.adapter.viewholder.PrivateGroupViewHolder;
import com.bekambimouen.miiokychallenge.groups.discover.adapter.viewholder.PublicGroupViewHolder;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.preload_image.PreloadSuggestionGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GroupDiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "GroupDiscoverAdapter";
    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final int PUBLIC_GROUP = 1;
    private static final int PRIVATE_GROUP = 2;

    // vars
    private final AppCompatActivity context;
    private final ArrayList<UserGroups> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final String user_id;

    public GroupDiscoverAdapter(AppCompatActivity context, ArrayList<UserGroups> list, ProgressBar progressBar,
                                OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PRIVATE_GROUP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_private_suggestion_discover, parent, false);
            return new PrivateGroupViewHolder(context, relLayout_view_overlay, view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_public_suggestion_discover, parent, false);
            return new PublicGroupViewHolder(context, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserGroups user_groups = list.get(position);

        try {
            PreloadSuggestionGroups.getPreloadSuggestionGroups(context, list.get(position+1));
            PreloadSuggestionGroups.getPreloadSuggestionGroups(context, list.get(position+2));
            PreloadSuggestionGroups.getPreloadSuggestionGroups(context, list.get(position+3));
            PreloadSuggestionGroups.getPreloadSuggestionGroups(context, list.get(position+4));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int view_type = getItemViewType(position);
        if (view_type == PRIVATE_GROUP) {
            PrivateGroupViewHolder privateGroupViewHolder = (PrivateGroupViewHolder) holder;
            privateGroupViewHolder.bind(user_groups);

            privateGroupViewHolder.close.setOnClickListener(view -> {
                // show dialog box
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = v.findViewById(R.id.dialog_title);
                TextView dialog_text = v.findViewById(R.id.dialog_text);
                TextView negativeButton = v.findViewById(R.id.tv_no);
                TextView positiveButton = v.findViewById(R.id.tv_yes);
                builder.setView(v);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.cancel));
                positiveButton.setText(context.getString(R.string.hide));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.hide_suggestion)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_will_definitely_remove_this_suggestion_for_you)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());

                positiveButton.setOnClickListener(view3 -> {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);


                    String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                    HashMap<String, Object> map_account = new HashMap<>();
                    map_account.put("user_id", user_groups.getGroup_id());
                    assert newKey != null;
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_remove_Suggestion))
                            .child(user_id)
                            .child(newKey)
                            .setValue(map_account);

                    d.dismiss();
                });
            });

        } else {
            PublicGroupViewHolder publicGroupViewHolder = (PublicGroupViewHolder) holder;
            publicGroupViewHolder.bind(user_groups);

            publicGroupViewHolder.close.setOnClickListener(view -> {
                // show dialog box
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = v.findViewById(R.id.dialog_title);
                TextView dialog_text = v.findViewById(R.id.dialog_text);
                TextView negativeButton = v.findViewById(R.id.tv_no);
                TextView positiveButton = v.findViewById(R.id.tv_yes);
                builder.setView(v);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.cancel));
                positiveButton.setText(context.getString(R.string.hide));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.hide_suggestion)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_will_definitely_remove_this_suggestion_for_you)));

                negativeButton.setOnClickListener(view2 -> d.dismiss());

                positiveButton.setOnClickListener(view3 -> {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);


                    String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                    HashMap<String, Object> map_account = new HashMap<>();
                    map_account.put("user_id", user_groups.getGroup_id());
                    assert newKey != null;
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_remove_Suggestion))
                            .child(user_id)
                            .child(newKey)
                            .setValue(map_account);
                    d.dismiss();
                });
            });
        }
        // hide the progressBar
        if (progressBar.getVisibility() == View.VISIBLE)
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
    public int getItemViewType(int position) {
        if (list.get(position).isGroup_private())
            return PRIVATE_GROUP;
        else
            return PUBLIC_GROUP;
    }
}

