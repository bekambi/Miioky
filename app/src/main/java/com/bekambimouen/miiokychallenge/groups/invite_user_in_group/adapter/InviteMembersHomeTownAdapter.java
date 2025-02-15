package com.bekambimouen.miiokychallenge.groups.invite_user_in_group.adapter;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.User;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteMembersHomeTownAdapter extends RecyclerView.Adapter<InviteMembersHomeTownAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "InviteMembersAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    public List<User> userList;
    public List<User> userListFiltered;
    private final UserGroups user_groups;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private String string_username, string_fullName, string2;

    // firebase
    private final DatabaseReference myRef;

    public InviteMembersHomeTownAdapter(AppCompatActivity context, List<User> userList,
                                        UserGroups user_groups, ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.userList = userList;
        this.userListFiltered = userList;
        this.user_groups = user_groups;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_invite_members,parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User users = userListFiltered.get(position);
        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(user_groups.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserGroups user_groups = new UserGroups();

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    user_groups.setGroup_theme(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_theme))).toString());

                    String theme = user_groups.getGroup_theme();

                    if (theme.equals(context.getString(R.string.theme_normal)))
                        holder.button_invite.setBackgroundResource(R.drawable.button_blue);
                    else if (theme.equals(context.getString(R.string.theme_blue)))
                        holder.button_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                    else if (theme.equals(context.getString(R.string.theme_brown)))
                        holder.button_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));
                    else if (theme.equals(context.getString(R.string.theme_pink)))
                        holder.button_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));
                    else if (theme.equals(context.getString(R.string.theme_green)))
                        holder.button_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                    else if (theme.equals(context.getString(R.string.theme_black)))
                        holder.button_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.bindView(users);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == userListFiltered.size() - 1;
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
        if(userListFiltered==null) return 0;
        return userListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

                if (charString.isEmpty()) {
                    userListFiltered = userList;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : userList) {
                        string_username = Normalizer.normalize(row.getUsername().toLowerCase(), Normalizer.Form.NFD);
                        string_username = string_username.replaceAll("[^\\p{ASCII}]", "");

                        string_fullName = Normalizer.normalize(row.getFullName().toLowerCase(), Normalizer.Form.NFD);
                        string_fullName = string_fullName.replaceAll("[^\\p{ASCII}]", "");

                        if (string_username.toLowerCase().contains(charString) ||
                                string_fullName.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    userListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userListFiltered;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userListFiltered = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relLayout_go_user_profile;
        public TextView username, fullName;
        public CircleImageView profileimage;
        private final View view_online;
        private final TextView button_invite;

        // firebase
        private final FirebaseDatabase database;
        private final String user_id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullName);
            profileimage = itemView.findViewById(R.id.profile_photo);
            view_online = itemView.findViewById(R.id.view_online);
            button_invite = itemView.findViewById(R.id.button_invite);

            database=FirebaseDatabase.getInstance();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }

        public void bindView(User user) {
            username.setText(user.getUsername());
            fullName.setText(user.getFullName());

            Glide.with(context)
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profileimage);

            // verify if user is online
            database.getReference()
                    .child(context.getString(R.string.dbname_presence))
                    .child(user.getUser_id())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                                assert status != null;
                                if(!status.isEmpty()){
                                    if(status.equals(context.getString(R.string.field_offLine))){
                                        view_online.setVisibility(View.GONE);
                                    }else{
                                        if (!user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                            view_online.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            relLayout_go_user_profile.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        user.getUsername());

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewProfile.class);
                Gson gson = new Gson();
                String myJson = gson.toJson(user);
                intent.putExtra("user", myJson);
                context.startActivity(intent);
            });

            // invitation in group
            isInvitation(user);

            // invitation in group
            button_invite.setOnClickListener(view1 -> {
                // send notification
                Date date = new Date();
                String new_key = myRef.push().getKey();
                HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                        true, false, false, false, false,
                        false,false, false, false,
                        false, false, false, false, false, false,
                        false,
                        false, false, false, false, false,
                        false, false,
                        false, false, false, false, false,
                        false, false,
                        true, user_id, true, false,
                        false, false,
                        true,false, false,
                        user.getUser_id(), new_key, user_id, user_groups.getAdmin_master(),
                        "", user_groups.getGroup_id(), date.getTime(),
                        false, false,
                        false, false, false, false, false, false, false, false,
                        false, "", "", "", false, "", "", "", false,
                        "", "", "", "", "", 0,
                        "", "", 0, "", "", "",
                        false, false, false, false,
                        false, false, false,
                        false, false);

                assert new_key != null;
                myRef.child(context.getString(R.string.dbname_notification))
                        .child(user.getUser_id())
                        .child(new_key)
                        .setValue(map_notification);

                // add badge number
                HashMap<String, Object> map_number = new HashMap<>();
                map_number.put("user_id", user.getUser_id());

                myRef.child(context.getString(R.string.dbname_badge_notification_number))
                        .child(user.getUser_id())
                        .child(new_key)
                        .setValue(map_number);

                setInvitation();

                // create the list of person who has been invited
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                        .child(user_groups.getGroup_id())
                        .child(user.getUser_id())
                        .child(context.getString(R.string.field_user_id))
                        .setValue(user.getUser_id());
            });
        }

        private void setInvitation(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            button_invite.setEnabled(false);
            button_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_white));
            button_invite.setText(context.getString(R.string.sent));
            button_invite.setTextColor(context.getColor(R.color.black_semi_transparent));
        }

        private void setUnInvite(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            button_invite.setVisibility(View.VISIBLE);
        }

        private void isInvitation(User user){
            Log.d(TAG, "isFollowing: checking if following this users.");
            setUnInvite();

            Query query = myRef.child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                    .child(user_groups.getGroup_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUser_id());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+ds.getKey());
                        setInvitation();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

