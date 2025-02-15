package com.bekambimouen.miiokychallenge.groups.simple_member.adapter;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoMembersProfileAdapter extends RecyclerView.Adapter<GoMembersProfileAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "InviteMembersAdapter";

    private final AppCompatActivity context;
    private final List<User> userList;
    private final UserGroups user_groups;
    public List<User> userListFiltered;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private String string_username, string_fullName, string2;

    public GoMembersProfileAdapter(AppCompatActivity context, List<User> userList,
                                   UserGroups user_groups, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.userList = userList;
        this.userListFiltered = userList;
        this.user_groups = user_groups;
        this.relLayout_view_overlay = relLayout_view_overlay;
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
        holder.bindView(users);
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

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullName);
            profileimage = itemView.findViewById(R.id.profile_photo);
            view_online = itemView.findViewById(R.id.view_online);
            TextView button_invite = itemView.findViewById(R.id.button_invite);
            button_invite.setVisibility(View.GONE);

            database=FirebaseDatabase.getInstance();
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
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupProfileMember.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("userID", user.getUser_id());
                context.startActivity(intent);
            });
        }
    }
}

