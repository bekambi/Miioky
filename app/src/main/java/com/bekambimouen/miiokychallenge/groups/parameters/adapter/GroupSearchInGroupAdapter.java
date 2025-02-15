package com.bekambimouen.miiokychallenge.groups.parameters.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPublicViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.parameters.adapter.viewholder.PrivateSearchInGroupViewHolder;
import com.bekambimouen.miiokychallenge.groups.parameters.adapter.viewholder.PublicSearchInGroupViewHolder;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupSearchInGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {
    private static final String TAG = "GroupListSuggestionAdapter";

    private static final int PRIVATE_GROUP = 1;
    private static final int PUBLIC_GROUP = 2;

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final List<UserGroups> groupNameList;
    public List<UserGroups> groupListFiltered;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private String string, string2;
    private final String user_id;

    // firebase
    private final DatabaseReference myRef;

    public GroupSearchInGroupAdapter(AppCompatActivity context, List<UserGroups> groupNameList,
                                     ProgressBar loading_progressBar, OnLoadMoreItemsListener mOnLoadMoreItemsListener,
                                     RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.groupNameList = groupNameList;
        this.groupListFiltered = groupNameList;
        this.loading_progressBar = loading_progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PRIVATE_GROUP) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_private_search_in_group, parent, false);
            return new PrivateSearchInGroupViewHolder(context, relLayout_view_overlay, view);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_public_search_in_group, parent, false);
            return new PublicSearchInGroupViewHolder(context, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserGroups user_groups = groupListFiltered.get(position);
        int view_type = getItemViewType(position);

        if (view_type == PRIVATE_GROUP) {
            PrivateSearchInGroupViewHolder privateSearchInGroupViewHolder = (PrivateSearchInGroupViewHolder) holder;
            privateSearchInGroupViewHolder.bind(user_groups);

            // if current user is admin master
            if (user_groups.getAdmin_master().equals(user_id)) {
                Gson gson = new Gson();
                String myJson = gson.toJson(user_groups);

                privateSearchInGroupViewHolder.relLayout_group_name.setOnClickListener(v -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupViewAdmin.class);
                    intent.putExtra("user_groups", myJson);
                    context.startActivity(intent);
                });

            } else {
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            Gson gson = new Gson();
                            String myJson = gson.toJson(user_groups);

                            // if user already following the group
                            privateSearchInGroupViewHolder.relLayout_group_name.setOnClickListener(v -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent;
                                if (follower.isModerator() || follower.isAdminInGroup()) {
                                    intent = new Intent(context, GroupViewAdmin.class);
                                } else {
                                    intent = new Intent(context, GroupViewProfile.class);
                                }
                                intent.putExtra("user_groups", myJson);
                                context.startActivity(intent);
                            });
                        }

                        // if user not following the group
                        if (!snapshot.exists()) {
                            privateSearchInGroupViewHolder.relLayout_group_name.setOnClickListener(view -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                String myGson = gson.toJson(user_groups);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                                intent.putExtra("user_groups", myGson);
                                context.startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            PublicSearchInGroupViewHolder publicSearchInGroupViewHolder = (PublicSearchInGroupViewHolder) holder;
            publicSearchInGroupViewHolder.bind(user_groups);

            // to know if current user is admin
            if (user_groups.getAdmin_master().equals(user_id)) {
                Gson gson = new Gson();
                String myJson = gson.toJson(user_groups);

                publicSearchInGroupViewHolder.bouton_voir.setOnClickListener(v -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupViewAdmin.class);
                    intent.putExtra("user_groups", myJson);
                    context.startActivity(intent);
                });

                publicSearchInGroupViewHolder.relLayout_group_name.setOnClickListener(v -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupViewAdmin.class);
                    intent.putExtra("user_groups", myJson);
                    context.startActivity(intent);
                });

            } else {
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            Gson gson = new Gson();
                            String myJson = gson.toJson(user_groups);

                            publicSearchInGroupViewHolder.bouton_voir.setOnClickListener(v -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent;
                                if (follower.isModerator()|| follower.isAdminInGroup()) {
                                    intent = new Intent(context, GroupViewAdmin.class);
                                } else {
                                    intent = new Intent(context, GroupViewProfile.class);
                                }
                                intent.putExtra("user_groups", myJson);
                                context.startActivity(intent);
                            });

                            publicSearchInGroupViewHolder.relLayout_group_name.setOnClickListener(v -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent;
                                if (follower.isModerator() || follower.isAdminInGroup()) {
                                    intent = new Intent(context, GroupViewAdmin.class);
                                } else {
                                    intent = new Intent(context, GroupViewProfile.class);
                                }
                                intent.putExtra("user_groups", myJson);
                                context.startActivity(intent);
                            });
                        }

                        // when user is not member of group
                        if (!snapshot.exists()) {
                            publicSearchInGroupViewHolder.bouton_voir.setOnClickListener(v -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user_groups);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, GroupPublicViewProfileDiscover.class);
                                intent.putExtra("user_groups", myJson);
                                context.startActivity(intent);
                            });

                            publicSearchInGroupViewHolder.relLayout_group_name.setOnClickListener(v -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user_groups);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, GroupPublicViewProfileDiscover.class);
                                intent.putExtra("user_groups", myJson);
                                context.startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == groupListFiltered.size() - 1;
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

    @Override
    public int getItemCount() {
        if(groupListFiltered==null) return 0;
        return groupListFiltered.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (groupNameList.get(position).isGroup_private())
            return PRIVATE_GROUP;
        else
            return PUBLIC_GROUP;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter
                //String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    groupListFiltered = groupNameList;
                } else {
                    List<UserGroups> filteredList = new ArrayList<>();
                    for (UserGroups row : groupNameList) {
                        string = Normalizer.normalize(row.getGroup_name().toLowerCase(), Normalizer.Form.NFD);
                        string = string.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

                        if (string.toLowerCase().contains(charString)) {
                            filteredList.add(row);
                        }
                    }

                    groupListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupListFiltered;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupListFiltered = (ArrayList<UserGroups>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

