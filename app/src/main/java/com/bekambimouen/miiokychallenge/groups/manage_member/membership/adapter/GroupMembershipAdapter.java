package com.bekambimouen.miiokychallenge.groups.manage_member.membership.adapter;

import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.friends.CommonFriends;
import com.bekambimouen.miiokychallenge.groups.manage_member.group_where_user_is_member.GroupWhereUserIsMember;
import com.bekambimouen.miiokychallenge.groups.model.GroupMembershipModel;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.User;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMembershipAdapter extends RecyclerView.Adapter<GroupMembershipAdapter.MyViewHolder> {
    private static final String TAG = "GroupMembershipAdapter";

    private final OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final List<GroupMembershipModel> list;
    private final ProgressBar progressBar;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupMembershipAdapter(AppCompatActivity context, List<GroupMembershipModel> list, OnLoadMoreItemsListener mOnLoadMoreItemsListener,
                                  ProgressBar progressBar, ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.progressBar = progressBar;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_membership_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupMembershipModel model = list.get(position);
        holder.bind(model);

        // refuse and remove item
        holder.relLayout_button_refuse.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_following))
                    .child(model.getUser_id())
                    .child(model.getGroup_id())
                    .removeValue();

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_follower))
                    .child(model.getGroup_id())
                    .child(model.getUser_id())
                    .removeValue().addOnSuccessListener(unused1 -> {
                        removeAt(holder.getLayoutPosition());
                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                    });

            // send notification
            Date date = new Date();
            String new_key = myRef.push().getKey();
            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                    false, false, false, false, false,
                    false,false, false, false,
                    false, false, false, false, false, false,
                    false,
                    false, false, false, false, false,
                    false, true,
                    false, false, false, false, false,
                    false, false,
                    false, "", false, false, false, false,
                    true,false, false,
                    model.getUser_id(), new_key, model.getUser_id(), model.getAdmin_master(),
                    "", model.getGroup_id(), date.getTime(),
                    false, false,
                    false, false, false, false, false, false, false, false,
                    false, "", "", "", false, "", "", "", false,
                    "", "", "", "", "", 0,
                    "", "", 0, "", "", "",
                    true, false, false, true,
                    false, false, false,
                    false, false);

            assert new_key != null;
            myRef.child(context.getString(R.string.dbname_notification))
                    .child(model.getUser_id())
                    .child(new_key)
                    .setValue(map_notification);
        });

        holder.relLayout_button_approve.setOnClickListener(view -> {
            // create the count to the user who asked_______________________________________________
            HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(model.getAdmin_master(),
                    model.getUser_id(), user_id, model.getGroup_id());

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_following))
                    .child(model.getUser_id())
                    .child(model.getGroup_id())
                    .setValue(map);

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(model.getGroup_id())
                    .child(model.getUser_id())
                    .setValue(map);
            // _____________________________________________________________________________________

            // remove membership request
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_following))
                    .child(model.getUser_id())
                    .child(model.getGroup_id())
                    .removeValue();

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_Membership_request_follower))
                    .child(model.getGroup_id())
                    .child(model.getUser_id())
                    .removeValue().addOnSuccessListener(unused1 -> {
                        removeAt(holder.getLayoutPosition());
                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                    });

            // send notification
            Date date = new Date();
            String new_key = myRef.push().getKey();
            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                    false, false, false, false, false,
                    false,false, false, false,
                    false, false, false, false, false, false,
                    false,
                    false, false, false, false, false,
                    false, true,
                    false, false, false, false, false,
                    false, false,
                    false, "", false, false, false, false,
                    true,false, false,
                    model.getUser_id(), new_key, model.getUser_id(), model.getAdmin_master(),
                    "", model.getGroup_id(), date.getTime(),
                    false, false,
                    false, false, false, false, false, false, false, false,
                    false, "", "", "", false, "", "", "", false,
                    "", "", "", "", "", 0,
                    "", "", 0, "", "", "",
                    true, false, true, false,
                    false, false, false,
                    false, false);

            assert new_key != null;
            myRef.child(context.getString(R.string.dbname_notification))
                    .child(model.getUser_id())
                    .child(new_key)
                    .setValue(map_notification);

            // add badge number
            HashMap<String, Object> map_number = new HashMap<>();
            map_number.put("user_id", model.getUser_id());

            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                    .child(model.getUser_id())
                    .child(new_key)
                    .setValue(map_number);
        });

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
            loading_progressBar.setVisibility(View.GONE);
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final CircleImageView profile_photo;
        private final View view_online;
        private final TextView username, from_time, total_followers_on_miioky, number_of_common_friends, total_groups_following,
                date_start_to_use_miioky;
        private final RelativeLayout relLayout_go_user_profile, relLayout_button_approve, relLayout_button_refuse,
                relLayout_total_followers_on_miioky, relLayout_number_of_common_friends, relLayout_total_groups_following;

        // vars
        private final List<String> current_user_friends_list;
        private final List<String> friend_user_friends_list;
        private int m = 0, k = 0, t = 0;
        private int nber_of_followers = 0, nber_of_friends = 0;

        // firebase
        private final DatabaseReference myRef;
        private final FirebaseDatabase database;

        public MyViewHolder(View itemView) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.profile_photo);
            view_online = itemView.findViewById(R.id.view_online);
            username = itemView.findViewById(R.id.username);
            from_time = itemView.findViewById(R.id.from_time);
            total_followers_on_miioky = itemView.findViewById(R.id.total_followers_on_miioky);
            number_of_common_friends = itemView.findViewById(R.id.number_of_common_friends);
            total_groups_following = itemView.findViewById(R.id.total_groups_following);
            date_start_to_use_miioky = itemView.findViewById(R.id.date_start_to_use_miioky);
            relLayout_button_approve = itemView.findViewById(R.id.relLayout_button_approve);
            relLayout_button_refuse = itemView.findViewById(R.id.relLayout_button_refuse);
            relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);
            relLayout_total_followers_on_miioky = itemView.findViewById(R.id.relLayout_total_followers_on_miioky);
            relLayout_number_of_common_friends = itemView.findViewById(R.id.relLayout_number_of_common_friends);
            relLayout_total_groups_following = itemView.findViewById(R.id.relLayout_total_groups_following);

            myRef = FirebaseDatabase.getInstance().getReference();
            database=FirebaseDatabase.getInstance();
            current_user_friends_list = new ArrayList<>();
            friend_user_friends_list = new ArrayList<>();
        }

        private void bind(GroupMembershipModel model) {

            // verify if user is online
            database.getReference()
                    .child(context.getString(R.string.dbname_presence))
                    .child(model.getUser_id())
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
                                        if (!model.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                            view_online.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            // get the groups user had created or following
            getGroupFollowing(model);
            getCommonFriends(model.getUser_id());

            // get date user asked adhesion
            long tv_date = model.getDate_created();
            from_time.setText(Html.fromHtml(context.getString(R.string.there_is)
                    +" "+ TimeUtils.getTime(context, tv_date)));

            // go the user profile photo
            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(model.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        username.setText(user.getUsername());

                        Glide.with(context.getApplicationContext())
                                .load(user.getProfileUrl())
                                .into(profile_photo);

                        long tv_date = user.getDate_created();
                        date_start_to_use_miioky.setText(Html.fromHtml(context.getString(R.string.joined_miioky_on)
                                +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

                        // go to the user profile
                        relLayout_go_user_profile.setOnClickListener(view -> {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    user.getUsername());

                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                intent = new Intent(context, Profile.class);

                            } else {
                                intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                            }
                            context.startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // number of followers on miioky
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_followers))
                    .child(model.getUser_id());
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                        nber_of_followers++;
                    }

                    // number of friends on miioky
                    Query myQuery2 = myRef
                            .child(context.getString(R.string.dbname_friend_follower))
                            .child(model.getUser_id());
                    myQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+ds.getKey());
                                nber_of_friends++;
                            }

                            // no followers and no friends
                            if (nber_of_followers == 0 && nber_of_friends == 0) {
                                relLayout_total_followers_on_miioky.setVisibility(View.GONE);
                            }

                            // followers but no friends
                            if (nber_of_followers != 0 && nber_of_friends == 0) {
                                if (nber_of_followers == 1)
                                    total_followers_on_miioky.setText(Html.fromHtml("<b>"+ nber_of_followers +"</b> "+context.getString(R.string.follower_on_miioky)));
                                else
                                    total_followers_on_miioky.setText(Html.fromHtml("<b>"+ nber_of_followers +"</b> "+context.getString(R.string.followers_on_miioky)));

                            }

                            // friends but no followers
                            if (nber_of_followers == 0 && nber_of_friends != 0) {
                                if (nber_of_friends == 1)
                                    total_followers_on_miioky.setText(Html.fromHtml("<b>"+ nber_of_friends +"</b> "+context.getString(R.string.friend_on_miioky)));
                                else
                                    total_followers_on_miioky.setText(Html.fromHtml("<b>"+ nber_of_friends +"</b> "+context.getString(R.string.friends_on_miioky)));
                            }

                            // followers and friends
                            if (nber_of_followers != 0 && nber_of_friends != 0) {
                                if (nber_of_followers == 1 && nber_of_friends == 1) {
                                    total_followers_on_miioky.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.one_follower)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.one_friend_on_miioky)));
                                }

                                if (nber_of_followers > 1 && nber_of_friends == 1) {
                                    total_followers_on_miioky.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.several_followers)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.one_friend_on_miioky)));
                                }

                                if (nber_of_followers == 1 && nber_of_friends > 1) {
                                    total_followers_on_miioky.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.one_follower)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.several_friends_on_miioky)));
                                }

                                if (nber_of_followers > 1 && nber_of_friends > 1) {
                                    total_followers_on_miioky.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.several_followers)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.several_friends_on_miioky)));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // common friends
            relLayout_number_of_common_friends.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CommonFriends.class);
                intent.putExtra("userID", model.getUser_id());
                context.startActivity(intent);
            });

            // group where user is member
            relLayout_total_groups_following.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupWhereUserIsMember.class);
                intent.putExtra("userID", model.getUser_id());
                context.startActivity(intent);
            });
        }

        private void getCommonFriends(String userID) {
            Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                    .child(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String keyID = dataSnapshot.getKey();

                        current_user_friends_list.add(keyID);
                    }

                    // get the friend user friend list
                    Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                            .child(userID);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String keyID = dataSnapshot.getKey();

                                friend_user_friends_list.add(keyID);
                            }

                            for (String ID: current_user_friends_list) {
                                for (int i = 0; i < friend_user_friends_list.size(); i++) {
                                    if (ID.equals(friend_user_friends_list.get(i))) {
                                        t++;
                                    }
                                }
                            }

                            if (t == 0)
                                relLayout_number_of_common_friends.setEnabled(false);
                            else
                                relLayout_number_of_common_friends.setVisibility(View.VISIBLE);

                            if (t == 1)
                                number_of_common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friend)));
                            else
                                number_of_common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friends)));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // get the groups user had created or following
        private void getGroupFollowing(GroupMembershipModel model) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(model.getUser_id())
                    .orderByChild(context.getString(R.string.field_admin_master))
                    .equalTo(model.getUser_id());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get the group user has created
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                        m++;
                    }

                    Query myQuery = myRef
                            .child(context.getString(R.string.dbname_group_following))
                            .child(model.getUser_id());

                    myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // first add current user id
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                                k++;
                            }

                            final int total = m + k;
                            if (total == 0)
                                relLayout_total_groups_following.setEnabled(false);
                            if (total == 1)
                                total_groups_following.setText(Html.fromHtml(context.getString(R.string.is_a_member_of)
                                        +" "+total+" "+context.getString(R.string.group)));
                            else
                                total_groups_following.setText(Html.fromHtml(context.getString(R.string.is_a_member_of)
                                        +" "+total+" "+context.getString(R.string.groups)));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

