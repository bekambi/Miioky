package com.bekambimouen.miiokychallenge.groups.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPublicViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.publication.GroupCreateNewGroup;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class YourGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int FOLLOWING_GROUP = 1;

    // vars
    private final AppCompatActivity context;
    private final List<UserGroups> list;
    private final String userID;
    private final RelativeLayout relLayout_view_overlay;

    public YourGroupsAdapter(AppCompatActivity context, List<UserGroups> list, String userID, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.userID = userID;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_recyclerview_your_group, parent, false);
            return new YourGroups(view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_item, parent, false);
            return new FollowingGroups(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserGroups groups = list.get(position);

        final int itemViewType = getItemViewType(position);
        if (itemViewType == FOLLOWING_GROUP) {
            FollowingGroups followingGroups = (FollowingGroups) holder;
            followingGroups.bind(groups);
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isHeader())
            return HEADER;
        else
            return FOLLOWING_GROUP;
    }

    public class YourGroups extends RecyclerView.ViewHolder {
        // widgets
        private final TextView group_you_manage;
        private final RecyclerView recycler_your_group;
        private final RelativeLayout relLayout_title;

        // vars
        private GroupYouManageAdapter adapter;
        private List<UserGroups> list;
        public ArrayList<String> followingUserList;
        public ArrayList<String> group_id_List;
        public ArrayList<String> followingUserIDList;
        public ArrayList<String> userIDList;
        private final Handler handler;

        // firebase
        private final DatabaseReference myRef;

        public YourGroups(@NonNull View itemView) {
            super(itemView);
            myRef = FirebaseDatabase.getInstance().getReference();
            handler = new Handler(Looper.getMainLooper());

            group_you_manage = itemView.findViewById(R.id.group_you_manage);
            relLayout_title = itemView.findViewById(R.id.relLayout_title);
            RelativeLayout relLayout_create_group = itemView.findViewById(R.id.relLayout_create_group);
            recycler_your_group = itemView.findViewById(R.id.recycler_your_group);
            recycler_your_group.setLayoutManager(new LinearLayoutManager(context));

            getGroupFollowing();

            //get the profile image and username
            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(userID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            group_you_manage.setText(Html.fromHtml(context.getString(R.string.group_manage_by)+" <b>"
                                    +context.getString(R.string.you)+"</b>"));
                        }
                        else {
                            relLayout_create_group.setVisibility(View.GONE);
                            group_you_manage.setText(Html.fromHtml(context.getString(R.string.group_manage_by)+" <b>"
                                    +user.getUsername()+"</b>"));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            relLayout_create_group.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupCreateNewGroup.class);
                context.startActivity(intent);
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void clearAll(){
            if(list != null){
                list.clear();
            }
            if(followingUserList != null){
                followingUserList.clear();
            }
            if(group_id_List != null){
                group_id_List.clear();
            }

            if(followingUserIDList != null){
                followingUserIDList.clear();
            }
            if(userIDList != null){
                userIDList.clear();
            }

            if(adapter != null){
                adapter = null;
            }

            if(recycler_your_group != null){
                handler.post(() -> recycler_your_group.setAdapter(null));
            }
            list = new ArrayList<>();
            followingUserList = new ArrayList<>();
            followingUserIDList = new ArrayList<>();
            group_id_List = new ArrayList<>();
            userIDList = new ArrayList<>();
        }

        /**
         // * RÃ©cupÃ©rer tous les identifiants d'utilisateur que l'utilisateur actuel suit
         // */
        private void getGroupFollowing() {
            clearAll();

            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(userID);

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // first add current user id
                    userIDList.add(userID);

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        // get data
                        String admin_id = following.getAdmin_master();
                        if (!userIDList.contains(admin_id)) {
                            userIDList.add(admin_id);
                        }
                    }
                    getUserIdList(userIDList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // add all other following groups
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(userID);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        String followingId = following.getGroup_id();

                        // get the list of group where member is admin
                        if (following.isAdminInGroup() || following.isModerator())
                            group_id_List.add(followingId);
                    }

                    getGroups(group_id_List);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // get list of all followings
        private void getUserIdList(ArrayList<String> following_usersIDList) {
            followingUserIDList.addAll(following_usersIDList);
        }

        private void getGroups(ArrayList<String> group_id_List) {
            if (!followingUserIDList.isEmpty()) {
                for(int i = 0; i < followingUserIDList.size(); i++){
                    final int count_user_list = i;

                    Query query = myRef
                            .child(context.getString(R.string.dbname_user_group))
                            .child(followingUserIDList.get(i))
                            .orderByChild(context.getString(R.string.field_admin_master))
                            .equalTo(followingUserIDList.get(i));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                if (user_groups.getAdmin_master().equals(userID))
                                    if (!user_groups.isSuppressed())
                                        list.add(user_groups);

                                for (String groupKey : group_id_List) {
                                    if (user_groups.getGroup_id().equals(groupKey))
                                        if (!user_groups.isSuppressed())
                                            list.add(user_groups);
                                }
                            }

                            if(count_user_list >= followingUserIDList.size() -1){
                                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                        .compareTo(String.valueOf(o1.getDate_created())));

                                adapter = new GroupYouManageAdapter(context, list, relLayout_view_overlay);
                                recycler_your_group.setAdapter(adapter);

                                if (adapter.getItemCount() != 0)
                                    relLayout_title.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }
    }

    public class FollowingGroups extends RecyclerView.ViewHolder {
        private final ImageView group_profile_photo;
        private final TextView group_name, tv_update;
        private final RelativeLayout relLayout_go_inside_group;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public FollowingGroups(@NonNull View itemView) {
            super(itemView);
            group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
            group_name = itemView.findViewById(R.id.group_name);
            tv_update = itemView.findViewById(R.id.tv_update);
            relLayout_go_inside_group = itemView.findViewById(R.id.relLayout_go_inside_group);

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }

        public void bind(UserGroups user_groups) {
            //get the group profile image and group name
            group_name.setText(user_groups.getGroup_name());

            Glide.with(context.getApplicationContext())
                    .load(user_groups.getProfile_photo())
                    .into(group_profile_photo);

            relLayout_go_inside_group.setOnClickListener(view -> {
                // to know if current user is administrator
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

                } else {// to know if current user is still member of group and to prevent single field adding in firebase
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

            getPhotos(user_groups);
        }

        private void getPhotos(UserGroups groups) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_group))
                    .child(groups.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                        // get last username and last publication
                        Query theQuery1 = myRef
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(model.getPublisher());
                        theQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    long tv_date = model.getDate_created();
                                    tv_update.setText(Html.fromHtml(user.getUsername()+" "+context.getString(R.string.made_post_in_group)
                                            +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

