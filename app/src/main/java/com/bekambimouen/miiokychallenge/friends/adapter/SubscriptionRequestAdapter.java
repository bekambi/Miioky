package com.bekambimouen.miiokychallenge.friends.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.followersfollowings.model.FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_FollowerFollowingModel;
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
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubscriptionRequestAdapter extends RecyclerView.Adapter<SubscriptionRequestAdapter.MyViewHolder> {
    private static final String TAG = "SuggestionAdapter";
    // vars
    private final AppCompatActivity context;
    private final ArrayList<User> list;
    private final RelativeLayout relLayout_confirmation;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public SubscriptionRequestAdapter(AppCompatActivity context, ArrayList<User> list,
                                      RelativeLayout relLayout_confirmation, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_confirmation = relLayout_confirmation;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_accept_subscription_request, parent, false);
        return new MyViewHolder(relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.bindView(user);

        // accept invitation to be friends
        holder.relLayout_button_confirm.setOnClickListener(view -> {
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
            positiveButton.setText(context.getString(R.string.accept));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.accept_subscription_request)));
            dialog_text.setText(Html.fromHtml(user.getUsername()+" "+context.getString(R.string.will_now_be_able_to_see_your_publications)));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            positiveButton.setOnClickListener(view3 -> {
                // delete the request from firebase
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_subscription_request_following))
                        .child(user.getUser_id())
                        .child(user_id)
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_subscription_request_follower))
                        .child(user_id)
                        .child(user.getUser_id())
                        .removeValue();

                // add follower ____________________________________________________________
                HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
                HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());


                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_following))
                        .child(user.getUser_id())
                        .child(user_id)
                        .setValue(map_current_user);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_followers))
                        .child(user_id)
                        .child(user.getUser_id())
                        .setValue(map_other_user).addOnSuccessListener(unused -> {
                            d.dismiss();
                            removeAt(holder.getLayoutPosition());

                            Date date = new Date();
                            String new_key = myRef.push().getKey();
                            assert new_key != null;
                            // send notification to the requester
                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                    false, false, false, false, false,
                                    false, false, false, false,
                                    false, false, false, false, false, false,
                                    false,
                                    false, false, false, false, false,
                                    false, false,
                                    false, false, false, false, false,
                                    false, false,
                                    false, "", false, false, false, false,
                                    false,false, true,
                                    user.getUser_id(), new_key, user_id, "",
                                    "", "", date.getTime(),
                                    false, false,
                                    false, false, false, false, false, false, false, false,
                                    false, "", "", "", false, "", "", "", false,
                                    "", "", "", "", "", 0,
                                    "", "", 0, "", "", "",
                                    false, false, false, false,
                                    false, false, false,
                                    true, true);

                            myRef.child(context.getString(R.string.dbname_notification))
                                    .child(user.getUser_id())
                                    .child(new_key)
                                    .setValue(map_notification);

                            // add badge number
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", user.getUser_id());

                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                    .child(user.getUser_id())
                                    .child(new_key)
                                    .setValue(map_number);

                            if (relLayout_confirmation != null && list.isEmpty())
                                relLayout_confirmation.setVisibility(View.GONE);
                        });
            });
        });

        // delete invitation to be friends
        holder.relLayout_button_delete.setOnClickListener(view -> {
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
            positiveButton.setText(context.getString(R.string.delete));

            dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_the_request)));
            dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_really_want_to_delet_this_subscription_request)));

            negativeButton.setOnClickListener(view2 -> d.dismiss());

            positiveButton.setOnClickListener(view3 -> {
                // invert the place of user id to cncel i,vitation to be friend
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_subscription_request_following))
                        .child(user.getUser_id())
                        .child(user_id)
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_subscription_request_follower))
                        .child(user_id)
                        .child(user.getUser_id())
                        .removeValue().addOnSuccessListener(unused -> {
                            d.dismiss();
                            removeAt(holder.getLayoutPosition());

                            if (relLayout_confirmation != null && list.isEmpty())
                                relLayout_confirmation.setVisibility(View.GONE);
                        });
            });
        });

        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profile_photo;
        private final TextView username, full_name, txt_date;
        private final RelativeLayout relLayout_button_confirm, relLayout_button_delete;
        private final LinearLayout linLayout_username;

        // vars
        private final RelativeLayout relLayout_view_overlay;

        // firebase
        private final DatabaseReference myRef;

        public MyViewHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            full_name = itemView.findViewById(R.id.full_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            linLayout_username = itemView.findViewById(R.id.linLayout_username);
            relLayout_button_confirm = itemView.findViewById(R.id.relLayout_button_confirm);
            relLayout_button_delete = itemView.findViewById(R.id.relLayout_button_delete);

            myRef = FirebaseDatabase.getInstance().getReference();
        }

        void bindView(User user) {
            Glide.with(context.getApplicationContext())
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);

            username.setText(user.getUsername());
            full_name.setText(user.getFullName());

            Query query1 = myRef.child(context.getString(R.string.dbname_subscription_request_following))
                    .child(user.getUser_id());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        FollowerFollowingModel follower = Util_FollowerFollowingModel.getUtil_FollowerFollowingModel(context, objectMap);

                        long tv_date = follower.getDate_created();
                        txt_date.setText(Html.fromHtml(TimeUtils.getTime(context, tv_date)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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

            linLayout_username.setOnClickListener(v -> {
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
        }
    }
}

