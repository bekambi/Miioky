package com.bekambimouen.miiokychallenge.display_insta.suggestion_groups.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.suggestion_groups.adapter.viewholder.PrivateGroupDisplayViewHolder;
import com.bekambimouen.miiokychallenge.display_insta.suggestion_groups.adapter.viewholder.PublicGroupDisplayViewHolder;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.bekambimouen.miiokychallenge.preload_image.PreloadSuggestionGroups;

import java.util.ArrayList;

public class SuggestionGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SuggestionGroupAdapter";

    private static final int PUBLIC_GROUP = 1;
    private static final int PRIVATE_GROUP = 2;

    // vars
    private final AppCompatActivity context;
    private final ArrayList<UserGroups> list;
    private final RelativeLayout relLayout_view_overlay;

    public SuggestionGroupAdapter(AppCompatActivity context, ArrayList<UserGroups> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PRIVATE_GROUP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_private_suggestion_item, parent, false);
            return new PrivateGroupDisplayViewHolder(context, relLayout_view_overlay, view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_public_suggestion_item, parent, false);
            return new PublicGroupDisplayViewHolder(context, view);
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

        int view_type = getItemViewType(position);
        if (view_type == PRIVATE_GROUP) {
            PrivateGroupDisplayViewHolder privateGroupDisplayViewHolder = (PrivateGroupDisplayViewHolder) holder;
            privateGroupDisplayViewHolder.bind(user_groups);

        } else {
            PublicGroupDisplayViewHolder publicGroupDisplayViewHolder = (PublicGroupDisplayViewHolder) holder;
            publicGroupDisplayViewHolder.bind(user_groups);
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

