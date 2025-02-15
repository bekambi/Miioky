package com.bekambimouen.miiokychallenge.groups.adapter;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.manage_member.bottomsheet.BottomSheetManageMemberInGroup;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoManageMembersAdapter extends RecyclerView.Adapter<GoManageMembersAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "InviteMembersAdapter";

    private final AppCompatActivity context;
    private BottomSheetManageMemberInGroup bottomSheetManageMemberInGroup;
    private final List<User> userList;
    private final UserGroups user_groups;
    public List<User> userListFiltered;

    // vars
    private String string_username, string2;

    // firebase
    private final DatabaseReference myRef;

    public GoManageMembersAdapter(AppCompatActivity context, List<User> userList,
                                  UserGroups user_groups) {
        this.context = context;
        this.userList = userList;
        this.userListFiltered = userList;
        this.user_groups = user_groups;

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group_admin_members_of_group,parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userListFiltered.get(position);
        holder.bindView(user);

        holder.username.setText(user.getUsername());

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(user.getProfileUrl())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(holder.profile_photo);

        bottomSheetManageMemberInGroup = new BottomSheetManageMemberInGroup(context, user_groups);
        holder.view.setOnClickListener(view -> {
            try {
                if (bottomSheetManageMemberInGroup.isAdded())
                    return;
                // hide keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Bundle bundle = new Bundle();
                bundle.putString("the_user_id", user.getUser_id());
                bundle.putString("admin_or_moderator_id", "member");
                bottomSheetManageMemberInGroup.setArguments(bundle);
                bottomSheetManageMemberInGroup.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "onBindViewHolder: error: "+e.getMessage());
            }
        });

        // date following
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_following))
                .child(user.getUser_id())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(user_groups.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    long date = following.getDate_following();

                    if (!TextUtils.isEmpty(following.getAdding_by())) {
                        holder.linLayout_add_by.setVisibility(View.VISIBLE);

                        Query query = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(following.getAdding_by());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                                    holder.add_by.setText(Html.fromHtml(context.getString(R.string.adding_by)
                                            +" "+ user.getUsername()
                                            +" "+context.getString(R.string.there_is) +" "+ TimeUtils.getTime(context, date)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        holder.linLayout_add_by.setVisibility(View.VISIBLE);
                        holder.add_by.setText(Html.fromHtml(context.getString(R.string.admin_master_member_from)
                                +" "+ TimeUtils.getTime(context, date)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

                        if (string_username.toLowerCase().contains(charString)) {
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
        // widgets
        private final CircleImageView profile_photo;
        private final TextView username, add_by;
        private final View view, view_online;
        private final LinearLayout linLayout_add_by;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            add_by = itemView.findViewById(R.id.add_by);
            view_online = itemView.findViewById(R.id.view_online);
            linLayout_add_by = itemView.findViewById(R.id.linLayout_add_by);
            linLayout_add_by.setVisibility(View.VISIBLE);

            database=FirebaseDatabase.getInstance();
        }

        public void bindView(User user) {
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
        }
    }
}

