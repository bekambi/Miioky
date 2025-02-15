package com.bekambimouen.miiokychallenge.groups.parameters.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.GroupInviteNewMembers;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
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

public class GroupInvitPersonsAdapter extends RecyclerView.Adapter<GroupInvitPersonsAdapter.MyViewHolder>
        implements Filterable {
    private static final String TAG = "GroupListSuggestionAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final List<UserGroups> groupNameList;
    public List<UserGroups> groupListFiltered;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private String string, string2;
    private final String user_id;

    public GroupInvitPersonsAdapter(AppCompatActivity context, List<UserGroups> groupNameList,
                                    ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.groupNameList = groupNameList;
        this.groupListFiltered = groupNameList;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_invit_persons_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserGroups user_groups = groupListFiltered.get(position);
        holder.bind(user_groups);

        // to know if current user is admin
        if (user_groups.getAdmin_master().equals(user_id)) {
            Gson gson = new Gson();
            String myJson = gson.toJson(user_groups);

            holder.parent.setOnClickListener(v -> {
                // prevent two clic
                Util.preventTwoClick(holder.parent);
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewAdmin.class);
                intent.putExtra("user_groups", myJson);
                context.startActivity(intent);
            });

        } else {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
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

                        holder.parent.setOnClickListener(v -> {
                            // prevent two clic
                            Util.preventTwoClick(holder.parent);

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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final ImageView profile_photo;
        private final TextView group_name, last_visit;
        private final RelativeLayout parent, relLayout_button_invite;

        // firebase
        private final DatabaseReference myRef;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            group_name = itemView.findViewById(R.id.group_name);
            last_visit = itemView.findViewById(R.id.last_visit);
            relLayout_button_invite = itemView.findViewById(R.id.relLayout_button_invite);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            parent = itemView.findViewById(R.id.parent);

            myRef = FirebaseDatabase.getInstance().getReference();
        }

        private void bind(UserGroups user_groups) {
            group_name.setText(user_groups.getGroup_name());

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(user_groups.getProfile_photo())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);

            // get the last visit from the group
            if (user_groups.getAdmin_master().equals(user_id)) {
                Query myQuery = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());

                myQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            long tv_date = user_groups.getLastSeenInGroup();
                            last_visit.setText(Html.fromHtml(context.getString(R.string.last_visit)+" "
                                    +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                Query query = myRef.child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                            assert objectMap != null;
                            GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            // get the last visite from the group
                            long tv_date = following.getLastSeenInGroup();
                            last_visit.setText(Html.fromHtml(context.getString(R.string.last_visit)+" "
                                    +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            relLayout_button_invite.setOnClickListener(view -> {
                closeKeyboard();
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupInviteNewMembers.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);
            });
        }
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

