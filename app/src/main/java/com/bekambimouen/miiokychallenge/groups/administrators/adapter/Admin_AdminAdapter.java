package com.bekambimouen.miiokychallenge.groups.administrators.adapter;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_AdminAdapter extends RecyclerView.Adapter<Admin_AdminAdapter.ViewHolder> {
    private static final String TAG = "Admin_AdminAdapter";

    // vars
    private final AppCompatActivity context;
    private final List<String> admin_user_id_list;
    private final UserGroups user_groups;

    private BottomSheetManageMemberInGroup bottomSheetManageMemberInGroup;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public Admin_AdminAdapter(AppCompatActivity context, List<String> admin_user_id_list,
                              UserGroups user_groups) {
        this.context = context;
        this.admin_user_id_list = admin_user_id_list;
        this.user_groups = user_groups;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_admin_members_of_group,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String admin_or_moderator_id = admin_user_id_list.get(position);
        holder.bindView(admin_or_moderator_id);

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(admin_or_moderator_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String profileUrl = user.getProfileUrl();
                    String userName = user.getUsername();

                    holder.username.setText(userName);

                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(profileUrl)
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(holder.profile_photo);

                    bottomSheetManageMemberInGroup = new BottomSheetManageMemberInGroup(context, user_groups);
                    holder.view.setOnClickListener(view -> {
                        try {
                            if (bottomSheetManageMemberInGroup.isAdded())
                                return;
                            Bundle bundle = new Bundle();
                            bundle.putString("the_user_id", admin_or_moderator_id);
                            bundle.putString("admin_or_moderator_id", admin_or_moderator_id);
                            bottomSheetManageMemberInGroup.setArguments(bundle);
                            bottomSheetManageMemberInGroup.show(context.getSupportFragmentManager(), "TAG");

                        } catch (IllegalStateException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                    });

                    holder.menu_item.setOnClickListener(view -> {
                        try {
                            if (bottomSheetManageMemberInGroup.isAdded())
                                return;
                            Bundle bundle = new Bundle();
                            bundle.putString("admin_or_moderator_id", admin_or_moderator_id);
                            bundle.putString("the_user_id", admin_or_moderator_id);
                            bottomSheetManageMemberInGroup.setArguments(bundle);
                            bottomSheetManageMemberInGroup.show(context.getSupportFragmentManager(), "TAG");
                        } catch (IllegalStateException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                    });

                    if (user.getUser_id().equals(user_id) || user.getUser_id().equals(user_groups.getAdmin_master())) {
                        long date = user_groups.getDate_created();

                        holder.date_of_following.setText(Html.fromHtml(context.getString(R.string.admin_master_member_from)
                                +" "+ TimeUtils.getTime(context, date)));

                    } else {
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
                                    long time = (System.currentTimeMillis() - date);
                                    if (time >= 2*3600*24000254025L)
                                        holder.date_of_following.setText(Html.fromHtml(context.getString(R.string.joined)
                                                +" "+context.getString(R.string.the)+" "+ TimeUtils.getTime(context, date)));
                                    else
                                        holder.date_of_following.setText(Html.fromHtml(context.getString(R.string.joined)
                                                +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
        return (admin_user_id_list!= null && !admin_user_id_list.isEmpty()) ? admin_user_id_list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

        // widgets
        private final CircleImageView profile_photo;
        private final TextView username, date_of_following;
        private final ImageView menu_item;
        private final View view, view_online;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            date_of_following = itemView.findViewById(R.id.date_of_following);
            menu_item = itemView.findViewById(R.id.menu_item);
            view_online = itemView.findViewById(R.id.view_online);
            LinearLayout linLayout_administrators = itemView.findViewById(R.id.linLayout_administrators);
            linLayout_administrators.setVisibility(View.VISIBLE);

            database=FirebaseDatabase.getInstance();
        }

        void bindView(String admin_user_id) {
            Log.d(TAG, "bindView: bindView");

            // verify if user is online
            database.getReference()
                    .child(context.getString(R.string.dbname_presence))
                    .child(admin_user_id)
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
                                        if (!admin_user_id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
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

