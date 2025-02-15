package com.bekambimouen.miiokychallenge.groups.explorer.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.manage_member.group_where_user_is_member.GroupWhereUserIsMember;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupHorizontalFollowingAdapter extends RecyclerView.Adapter<GroupHorizontalFollowingAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<UserGroups> list;
    private final int list_size;
    private final RelativeLayout relLayout_view_overlay;
    private final String user_id;

    // firebase
    private final DatabaseReference myRef;

    public GroupHorizontalFollowingAdapter(AppCompatActivity context, ArrayList<UserGroups> list, int list_size,
                                           RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.list_size = list_size;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_profil_photo,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserGroups user_groups = list.get(position);
        holder.bind(user_groups);

        if (position == 11) {
            if ((list_size-12 > 0) )
                holder.relLayout_nber_of_hide_views.setVisibility(View.VISIBLE);
            holder.nber_of_views.setText(String.valueOf(list_size-12));
        }

        holder.relLayout_nber_of_hide_views.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupWhereUserIsMember.class);
            intent.putExtra("userID", user_id);
            context.startActivity(intent);
        });

        // to know if current user is admin
        if (user_groups.getAdmin_master().equals(user_id)) {
            Gson gson = new Gson();
            String myJson = gson.toJson(user_groups);

            holder.view.setOnClickListener(v -> {
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

                        holder.view.setOnClickListener(v -> {
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

        // widgets
        private final ImageView profile_photo;
        private final TextView group_name, nber_of_views;
        private final View view;
        private final RelativeLayout relLayout_nber_of_hide_views;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            group_name = itemView.findViewById(R.id.group_name);
            nber_of_views = itemView.findViewById(R.id.nber_of_views);
            relLayout_nber_of_hide_views = itemView.findViewById(R.id.relLayout_nber_of_hide_views);
        }

        public void bind(UserGroups userGroups) {
            Log.d(TAG, "bindView: bindView");

            group_name.setText(userGroups.getGroup_name());

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(userGroups.getProfile_photo())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);
        }
    }
}

