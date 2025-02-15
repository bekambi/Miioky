package com.bekambimouen.miiokychallenge.groups.simple_member.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
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
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<String> admin_user_id_list;
    private final UserGroups user_groups;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;

    public AdminAdapter(AppCompatActivity context, List<String> admin_user_id_list,
                        UserGroups user_groups, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.admin_user_id_list = admin_user_id_list;
        this.user_groups = user_groups;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_members_of_group,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String admin_user_id = admin_user_id_list.get(position);
        holder.bindView(admin_user_id);

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(admin_user_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String userID = user.getUser_id();

                    holder.username.setText(user.getUsername());

                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(holder.profile_photo);

                    holder.view.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("user_groups", myGson);
                        intent.putExtra("userID", userID);
                        intent.putExtra("group_id", user_groups.getGroup_id());
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // admin master
        if (admin_user_id.equals(user_groups.getAdmin_master())) {
            holder.linLayout_administrators.setVisibility(View.VISIBLE);
            holder.icon_admin_or_moderator.setImageResource(R.drawable.ic_baseline_local_police_24);
            holder.tv_admin_or_moderator.setText(context.getString(R.string.admin));
        }

        // to know if current user is admin or moderator
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(user_groups.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(admin_user_id);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (follower.isAdminInGroup()) {
                        holder.linLayout_administrators.setVisibility(View.VISIBLE);
                        holder.icon_admin_or_moderator.setImageResource(R.drawable.ic_baseline_local_police_24);
                        holder.tv_admin_or_moderator.setText(context.getString(R.string.admin));
                    }

                    if (follower.isModerator()) {
                        holder.linLayout_administrators.setVisibility(View.VISIBLE);
                        holder.icon_admin_or_moderator.setImageResource(R.drawable.ic_baseline_beenhere_24);
                        holder.tv_admin_or_moderator.setText(context.getString(R.string.moderator));
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
        private final TextView username, tv_admin_or_moderator;
        private final View view, view_online;
        private final ImageView icon_admin_or_moderator;
        private final LinearLayout linLayout_administrators;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            database=FirebaseDatabase.getInstance();

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            tv_admin_or_moderator = itemView.findViewById(R.id.tv_admin_or_moderator);
            view_online = itemView.findViewById(R.id.view_online);
            icon_admin_or_moderator = itemView.findViewById(R.id.icon_admin_or_moderator);
            linLayout_administrators = itemView.findViewById(R.id.linLayout_administrators);
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