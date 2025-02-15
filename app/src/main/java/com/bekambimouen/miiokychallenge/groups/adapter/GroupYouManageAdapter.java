package com.bekambimouen.miiokychallenge.groups.adapter;

import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
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
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPublicViewProfileDiscover;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupYouManageAdapter extends RecyclerView.Adapter<GroupYouManageAdapter.MyViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<UserGroups> list;
    private final RelativeLayout relLayout_view_overlay;

    public GroupYouManageAdapter(AppCompatActivity context, List<UserGroups> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserGroups groups = list.get(position);
        holder.bind(groups);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView group_profile_photo;
        private final TextView group_name, tv_update;
        private final RelativeLayout relLayout_go_create_group;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
            group_name = itemView.findViewById(R.id.group_name);
            tv_update = itemView.findViewById(R.id.tv_update);
            relLayout_go_create_group = itemView.findViewById(R.id.relLayout_go_inside_group);
        }

        public void bind(UserGroups user_groups) {
            //get the group profile image and group name
            group_name.setText(user_groups.getGroup_name());
            long tv_date = user_groups.getDate_created();
            tv_update.setText(Html.fromHtml(context.getString(R.string.update_day)
                    +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

            Glide.with(context.getApplicationContext())
                    .load(user_groups.getProfile_photo())
                    .into(group_profile_photo);

            relLayout_go_create_group.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);

                if (user_groups.getAdmin_master().equals(user_id)) {
                    Gson gson = new Gson();
                    String myJson = gson.toJson(user_groups);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, GroupViewAdmin.class);
                    intent.putExtra("user_groups", myJson);
                    context.startActivity(intent);

                } else {
                    // to know if current user is still member of group and to prevent single field adding in firebase
                    Query query = myRef
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(user_groups.getGroup_id())
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(user_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                if (user_groups.isGroup_private()) {
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                                    intent.putExtra("user_groups", myGson);
                                    context.startActivity(intent);

                                } else {
                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, GroupPublicViewProfileDiscover.class);
                                    intent.putExtra("user_groups", myJson);
                                    context.startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
    }
}

