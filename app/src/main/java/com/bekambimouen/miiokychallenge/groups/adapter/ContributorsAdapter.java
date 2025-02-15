package com.bekambimouen.miiokychallenge.groups.adapter;

import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContributorsAdapter extends RecyclerView.Adapter<ContributorsAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<GroupFollowersFollowing> contributors_user_list;
    private final UserGroups user_groups;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;

    public ContributorsAdapter(AppCompatActivity context, List<GroupFollowersFollowing> contributors_user_list,
                               UserGroups user_groups, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.contributors_user_list = contributors_user_list;
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
        GroupFollowersFollowing contributor = contributors_user_list.get(position);
        holder.bindView(contributor.getUser_id());

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(contributor.getUser_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    holder.username.setText(user.getUsername());

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.profile_photo);
                    }

                    holder.view.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("user_groups", myGson);
                        intent.putExtra("userID", user.getUser_id());
                        intent.putExtra("group_id", user_groups.getGroup_id());
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get admin master contribution points
        if (contributor.getUser_id().equals(user_groups.getAdmin_master())) {
            Query query4 = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_groups.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        // get the admin points
                        int post_points = Integer.parseInt(user_groups.getPost_points());
                        int comment_points = Integer.parseInt(user_groups.getComment_points());
                        int share_points = Integer.parseInt(user_groups.getShare_points());
                        int like_points = Integer.parseInt(user_groups.getLike_points());

                        int total_point = post_points + comment_points + share_points + like_points;
                        if (total_point >= 1) {
                            holder.linLayout_contributors.setVisibility(View.VISIBLE);

                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            if (total_point == 1)
                                holder.points_of_user_contribution.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                        " "+context.getString(R.string.point)));
                            else
                                holder.points_of_user_contribution.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                        " "+context.getString(R.string.points)));
                        } else
                            holder.points_of_user_contribution.setText(Html.fromHtml(context.getString(R.string.zero)+ " "+context.getString(R.string.point)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } // get the member contribution
        else {
            Query query1 = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(contributor.getUser_id())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());

            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        // get the member points
                        int post_points = Integer.parseInt(following.getPost_points());
                        int comment_points = Integer.parseInt(following.getComment_points());
                        int share_points = Integer.parseInt(following.getShare_points());
                        int like_points = Integer.parseInt(following.getLike_points());

                        int total_point = post_points + comment_points + share_points + like_points;
                        if (total_point >= 1) {
                            holder.linLayout_contributors.setVisibility(View.VISIBLE);

                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            if (total_point == 1)
                                holder.points_of_user_contribution.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                        " "+context.getString(R.string.point)));
                            else
                                holder.points_of_user_contribution.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                        " "+context.getString(R.string.points)));
                        } else
                            holder.points_of_user_contribution.setText(Html.fromHtml(context.getString(R.string.zero)+ " "+context.getString(R.string.point)));
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
        return (contributors_user_list!= null && !contributors_user_list.isEmpty()) ? contributors_user_list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

        // widgets
        private final CircleImageView profile_photo;
        private final TextView username, points_of_user_contribution;
        private final View view, view_online;
        private final LinearLayout linLayout_contributors;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            points_of_user_contribution = itemView.findViewById(R.id.points_of_user_contribution);
            view_online = itemView.findViewById(R.id.view_online);
            linLayout_contributors = itemView.findViewById(R.id.linLayout_contributors);

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

