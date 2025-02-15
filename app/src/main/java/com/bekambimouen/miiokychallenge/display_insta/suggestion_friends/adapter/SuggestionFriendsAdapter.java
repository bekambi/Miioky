package com.bekambimouen.miiokychallenge.display_insta.suggestion_friends.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.friends.CommonFriends;
import com.bekambimouen.miiokychallenge.friends.utils_map.Utils_Map_SubscriptionRequestModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadSuggestionFriends;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestionFriendsAdapter extends RecyclerView.Adapter<SuggestionFriendsAdapter.MyViewHolder> {
    private static final String TAG = "SuggestionFriendsAdapter";

    // vars
    private final AppCompatActivity context;
    private final ArrayList<User> list;
    private final RelativeLayout relLayout_view_overlay;
    private final String user_id;

    public SuggestionFriendsAdapter(AppCompatActivity context, ArrayList<User> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_friends_suggestion, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);

        try {
            PreloadSuggestionFriends.getPreloadSuggestionFriends(context, list.get(position+1).getUser_id());
            PreloadSuggestionFriends.getPreloadSuggestionFriends(context, list.get(position+2).getUser_id());
            PreloadSuggestionFriends.getPreloadSuggestionFriends(context, list.get(position+3).getUser_id());
            PreloadSuggestionFriends.getPreloadSuggestionFriends(context, list.get(position+4).getUser_id());
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        holder.bindView(user);

        holder.close.setOnClickListener(view -> {
            // show dialog box
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

            TextView dialog_title = v.findViewById(R.id.dialog_title);
            TextView dialog_text = v.findViewById(R.id.dialog_text);
            TextView negativeButton = v.findViewById(R.id.tv_no);
            TextView positiveButton = v.findViewById(R.id.tv_yes);
            builder.setView(v);

            Dialog d = builder.create();
            d.show();

            negativeButton.setText(context.getString(R.string.cancel));
            positiveButton.setText(context.getString(R.string.hide));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.hide_suggestion)));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_will_definitely_remove_this_suggestion_for_you)));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            positiveButton.setOnClickListener(view3 -> {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                params.width = 0;
                holder.itemView.setLayoutParams(params);

                String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                HashMap<String, Object> map_account = new HashMap<>();
                map_account.put("user_id", user.getUser_id());
                assert newKey != null;
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_remove_Suggestion))
                        .child(user_id)
                        .child(newKey)
                        .setValue(map_account);
                d.dismiss();
            });
        });
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "PublicGroupDisplayViewHolder";

        // widgets
        private final ImageView profile_photo, close;
        private final TextView username, number_of_common_friends;
        private final TextView button_follow, button_sent, button_unfollow;
        private final CircleImageView member_i, member_ii;
        private final RelativeLayout relLayout_profile_friends, relLayout_member_ii;

        // vars
        private final List<String> current_user_friends_list;
        private final List<String> friend_user_friends_list;
        private final List<String> friends_profile_url_list, common_friends_profile_url;
        private int t = 0;

        // firebase
        private final DatabaseReference myRef;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.profile_photo);
            close = itemView.findViewById(R.id.close);
            username = itemView.findViewById(R.id.username);
            number_of_common_friends = itemView.findViewById(R.id.number_of_common_friends);
            button_follow = itemView.findViewById(R.id.button_follow);
            button_unfollow = itemView.findViewById(R.id.button_unfollow);
            button_sent = itemView.findViewById(R.id.button_sent);
            member_i = itemView.findViewById(R.id.member_i);
            member_ii = itemView.findViewById(R.id.member_ii);
            relLayout_member_ii = itemView.findViewById(R.id.relLayout_member_ii);
            relLayout_profile_friends = itemView.findViewById(R.id.relLayout_profile_friends);

            myRef = FirebaseDatabase.getInstance().getReference();
            current_user_friends_list = new ArrayList<>();
            friend_user_friends_list = new ArrayList<>();
            friends_profile_url_list = new ArrayList<>();
            common_friends_profile_url = new ArrayList<>();
        }

        private void bindView(User user) {

            username.setText(user.getUsername());

            Glide.with(context.getApplicationContext())
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);

            // get the common friends ___________________________________________________
            getCommonFriends(user);

            profile_photo.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        user.getUsername());

                closeKeyboard();

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewProfile.class);
                Gson gson = new Gson();
                String myJson = gson.toJson(user);
                intent.putExtra("user", myJson);
                context.startActivity(intent);
            });

            // common friends
            relLayout_profile_friends.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CommonFriends.class);
                intent.putExtra("userID", user.getUser_id());
                context.startActivity(intent);
            });
        }

        // get the common friends ___________________________________________________
        private void getCommonFriends(User user) {
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
                            .child(user.getUser_id());

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
                                        common_friends_profile_url.add(friend_user_friends_list.get(i));
                                    }
                                }
                            }

                            if (t != 0)
                                if (!user.getUser_id().equals(user_id))
                                    relLayout_profile_friends.setVisibility(View.VISIBLE);

                            if (t == 1)
                                number_of_common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friend)));
                            else
                                number_of_common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friends)));


                            // get the profile url
                            for (int i = 0; i < common_friends_profile_url.size(); i++) {
                                if (i < 2) {
                                    Query query = myRef.child(context.getString(R.string.dbname_users))
                                            .orderByChild(context.getString(R.string.field_user_id))
                                            .equalTo(common_friends_profile_url.get(i));

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()) {

                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                assert objectMap != null;
                                                User user = Util_User.getUser(context, objectMap, ds);
                                                // get the profile url
                                                if (!user.getUser_id().equals(user_id))
                                                    friends_profile_url_list.add(user.getProfileUrl());
                                            }

                                            // show profile photo
                                            if (friends_profile_url_list.size() == 1) {
                                                Glide.with(context.getApplicationContext())
                                                        .load(friends_profile_url_list.get(0))
                                                        .placeholder(R.drawable.ic_baseline_person_24)
                                                        .into(member_i);
                                            }
                                            if (friends_profile_url_list.size() == 2) {
                                                relLayout_member_ii.setVisibility(View.VISIBLE);

                                                Glide.with(context.getApplicationContext())
                                                        .load(friends_profile_url_list.get(0))
                                                        .placeholder(R.drawable.ic_baseline_person_24)
                                                        .into(member_i);

                                                Glide.with(context.getApplicationContext())
                                                        .load(friends_profile_url_list.get(1))
                                                        .placeholder(R.drawable.ic_baseline_person_24)
                                                        .into(member_ii);
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
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // follow unfollow person ______________________________________________________________
            // public account
            if (user.getScope().equals(context.getString(R.string.title_public))) {
                isFollowing_public_account(user);

                HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
                HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

                button_follow.setOnClickListener(view -> {
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_following))
                            .child(user_id)
                            .child(user.getUser_id())
                            .setValue(map_other_user);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_followers))
                            .child(user.getUser_id())
                            .child(user_id)
                            .setValue(map_current_user);
                    setFollowing_public_account();
                });

                button_unfollow.setOnClickListener(v -> {
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_following))
                            .child(user_id)
                            .child(user.getUser_id())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_followers))
                            .child(user.getUser_id())
                            .child(user_id)
                            .removeValue();
                    setUnfollowing_public_account();
                });
            }
            // private account
            if (user.getScope().equals(context.getString(R.string.title_private))) {
                // verify if user is already following
                Query query1 = myRef.child(context.getString(R.string.dbname_following))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(user.getUser_id());

                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: "+ds.getValue());

                            isFollowing_public_account(user);

                            button_follow.setOnClickListener(view -> {
                                HashMap<Object, Object> map_current_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user_id);
                                HashMap<Object, Object> map_other_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user.getUser_id());

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_subscription_request_following))
                                        .child(user_id)
                                        .child(user.getUser_id())
                                        .setValue(map_other_user);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_subscription_request_follower))
                                        .child(user.getUser_id())
                                        .child(user_id)
                                        .setValue(map_current_user);
                                setFollowing_subscription_request();

                                // add badge number
                                String key = myRef.push().getKey();
                                HashMap<String, Object> map_number = new HashMap<>();
                                map_number.put("user_id", user.getUser_id());

                                assert key != null;
                                myRef.child(context.getString(R.string.dbname_badge_follow_approbation_number))
                                        .child(user.getUser_id())
                                        .child(key)
                                        .setValue(map_number);

                                // for the global
                                myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                        .child(user.getUser_id())
                                        .child(key)
                                        .setValue(map_number);
                            });

                            button_unfollow.setOnClickListener(v -> {
                                // show dialog box
                                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                TextView dialog_title = view.findViewById(R.id.dialog_title);
                                TextView dialog_text = view.findViewById(R.id.dialog_text);
                                TextView negativeButton = view.findViewById(R.id.tv_no);
                                TextView positiveButton = view.findViewById(R.id.tv_yes);
                                builder.setView(view);

                                Dialog d = builder.create();
                                d.show();

                                negativeButton.setText(context.getString(R.string.cancel));
                                positiveButton.setText(context.getString(R.string.unsubscribe));

                                dialog_title.setVisibility(View.GONE);
                                dialog_text.setText(Html.fromHtml(context.getString(R.string.would_you_like_to_stop_following)+" "
                                        +user.getUsername()+" ?"));

                                negativeButton.setOnClickListener(view2 -> d.dismiss());

                                positiveButton.setOnClickListener(view1 -> {
                                    d.dismiss();
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_following))
                                            .child(user_id)
                                            .child(user.getUser_id())
                                            .removeValue();

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_followers))
                                            .child(user.getUser_id())
                                            .child(user_id)
                                            .removeValue();
                                    setUnfollowing_subscription_request();
                                });
                            });
                        }

                        if (!snapshot.exists()) {
                            isFollowing_subscription_request(user);

                            HashMap<Object, Object> map_current_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user_id);
                            HashMap<Object, Object> map_other_user = Utils_Map_SubscriptionRequestModel.setSubscriptionRequestModel(user.getUser_id());

                            button_follow.setOnClickListener(view -> {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_subscription_request_following))
                                        .child(user_id)
                                        .child(user.getUser_id())
                                        .setValue(map_other_user);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_subscription_request_follower))
                                        .child(user.getUser_id())
                                        .child(user_id)
                                        .setValue(map_current_user);
                                setFollowing_subscription_request();

                                // add badge number
                                String key = myRef.push().getKey();
                                HashMap<String, Object> map_number = new HashMap<>();
                                map_number.put("user_id", user.getUser_id());

                                assert key != null;
                                myRef.child(context.getString(R.string.dbname_badge_follow_approbation_number))
                                        .child(user.getUser_id())
                                        .child(key)
                                        .setValue(map_number);

                                // for the global
                                myRef.child(context.getString(R.string.dbname_badge_follow_number))
                                        .child(user.getUser_id())
                                        .child(key)
                                        .setValue(map_number);
                            });

                            button_sent.setOnClickListener(v -> {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_subscription_request_following))
                                        .child(user_id)
                                        .child(user.getUser_id())
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_subscription_request_follower))
                                        .child(user.getUser_id())
                                        .child(user_id)
                                        .removeValue();
                                setUnfollowing_subscription_request();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        // follow unfollow person __________________________________________________________________
        // public
        private void setFollowing_public_account(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            button_follow.setVisibility(View.GONE);
            button_unfollow.setVisibility(View.VISIBLE);
            button_sent.setVisibility(View.GONE);
        }

        private void setUnfollowing_public_account(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            button_follow.setVisibility(View.VISIBLE);
            button_unfollow.setVisibility(View.GONE);
            button_sent.setVisibility(View.GONE);
        }

        private void isFollowing_public_account(User user){
            Log.d(TAG, "isFollowing: checking if following this users.");

            Query query = myRef.child(context.getString(R.string.dbname_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUser_id());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                        setFollowing_public_account();
                    }

                    if (!snapshot.exists()) {
                        setUnfollowing_public_account();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // private
        private void setFollowing_subscription_request(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            button_follow.setVisibility(View.GONE);
            button_unfollow.setVisibility(View.GONE);
            button_sent.setVisibility(View.VISIBLE);
        }

        private void setUnfollowing_subscription_request(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            button_follow.setVisibility(View.VISIBLE);
            button_unfollow.setVisibility(View.GONE);
            button_sent.setVisibility(View.GONE);
        }

        private void isFollowing_subscription_request(User user){
            Log.d(TAG, "isFollowing: checking if following this users.");

            Query query = myRef.child(context.getString(R.string.dbname_subscription_request_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user.getUser_id());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                        setFollowing_subscription_request();
                    }

                    if (!snapshot.exists()) {
                        setUnfollowing_subscription_request();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void closeKeyboard(){
            View view = context.getCurrentFocus();
            if(view != null){
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}

