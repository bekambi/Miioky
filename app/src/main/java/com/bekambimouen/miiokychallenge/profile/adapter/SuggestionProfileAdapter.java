package com.bekambimouen.miiokychallenge.profile.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.friends.utils_map.Utils_Map_SubscriptionRequestModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestionProfileAdapter extends RecyclerView.Adapter<SuggestionProfileAdapter.MyViewHolder> {
    private static final String TAG = "SuggestionAdapter";

    // vars
    private final AppCompatActivity context;
    private final ArrayList<User> list;
    private final RelativeLayout relLayout_view_overlay;

    private final String user_id;

    public SuggestionProfileAdapter(AppCompatActivity context, ArrayList<User> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;

        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_profile_suggestion, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
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
        private final CircleImageView profile_photo;
        private final ImageView close;
        private final TextView username, new_user, button_follow, button_unfollow, button_sent;

        // firebase
        private final DatabaseReference myRef;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            new_user = itemView.findViewById(R.id.new_user);
            close = itemView.findViewById(R.id.close);
            username = itemView.findViewById(R.id.username);
            button_follow = itemView.findViewById(R.id.button_follow);
            button_unfollow = itemView.findViewById(R.id.button_unfollow);
            button_sent = itemView.findViewById(R.id.button_sent);

            myRef = FirebaseDatabase.getInstance().getReference();
        }

        void bindView(User user) {
            Glide.with(context.getApplicationContext())
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);

            username.setText(user.getUsername());

            username.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        user.getUsername());

                if (!user.getUser_id().equals(user_id)) {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, ViewProfile.class);
                    Gson gson = new Gson();
                    String myJson = gson.toJson(user);
                    intent.putExtra("user", myJson);
                    context.startActivity(intent);
                }
            });

            profile_photo.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        user.getUsername());

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

            long timecurrent = System.currentTimeMillis();

            if (user.getDate_created() +345600000L > timecurrent)
                new_user.setVisibility(View.VISIBLE);
            else
                new_user.setVisibility(View.GONE);

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

        // follow unfollow person ______________________________________________________________________
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
    }
}

