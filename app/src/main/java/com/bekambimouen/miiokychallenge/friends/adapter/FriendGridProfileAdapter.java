package com.bekambimouen.miiokychallenge.friends.adapter;

import android.content.Intent;
import android.text.Html;
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
import com.bekambimouen.miiokychallenge.friends.model.FriendsFollowerFollowing;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FriendGridProfileAdapter extends RecyclerView.Adapter<FriendGridProfileAdapter.MyViewHolder> {
    private static final String TAG = "SuggestionAdapter";
    // vars
    private final AppCompatActivity context;
    private final ArrayList<FriendsFollowerFollowing> list;
    private final String userID;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final String user_id;

    public FriendGridProfileAdapter(AppCompatActivity context, ArrayList<FriendsFollowerFollowing> list,
                                    String userID, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.userID = userID;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_friends_grid_item, parent, false);
        return new MyViewHolder(relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FriendsFollowerFollowing followerFollowing = list.get(position);
        holder.bindView(followerFollowing);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final ImageView profile_photo;
        private final TextView username, common_friends;
        private final LinearLayout parent;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private final List<String> current_user_friends_list;
        private final List<String> friend_user_friends_list;
        private int t = 0;

        // firebase
        private final DatabaseReference myRef;

        public MyViewHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            parent = itemView.findViewById(R.id.parent);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            common_friends = itemView.findViewById(R.id.common_friends);

            myRef = FirebaseDatabase.getInstance().getReference();
            current_user_friends_list = new ArrayList<>();
            friend_user_friends_list = new ArrayList<>();
        }

        void bindView(FriendsFollowerFollowing follower) {

            Query query1 = myRef.child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(follower.getUser_id());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        username.setText(user.getUsername());

                        Glide.with(context.getApplicationContext())
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);

                        parent.setOnClickListener(v -> {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    user.getUsername());

                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (user.getUser_id().equals(user_id)) {
                                intent = new Intent(context, Profile.class);

                            } else {
                                intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                            }
                            context.startActivity(intent);
                        });

                        // get the common friends ___________________________________________________
                        getCommonFriends(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // get the common friends ___________________________________________________
        private void getCommonFriends(User user) {
            Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                    .child(user.getUser_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String keyID = dataSnapshot.getKey();

                        assert keyID != null;
                        if (!keyID.equals(userID))
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

                                assert keyID != null;
                                if (!keyID.equals(user.getUser_id()))
                                    friend_user_friends_list.add(keyID);
                            }

                            for (String ID: current_user_friends_list) {
                                for (int i = 0; i < friend_user_friends_list.size(); i++) {
                                    if (ID.equals(friend_user_friends_list.get(i))) {
                                        t++;
                                    }
                                }
                            }

                            if (t != 0)
                                common_friends.setVisibility(View.VISIBLE);

                            if (t == 1)
                                common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friend)));
                            else
                                common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friends)));
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

