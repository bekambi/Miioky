package com.bekambimouen.miiokychallenge.story.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SawStoriesAdapter extends RecyclerView.Adapter<SawStoriesAdapter.MyViewHolder> {
    private static final String TAG = "SuggestionAdapter";
    // vars
    private final AppCompatActivity context;
    private final ArrayList<User> list;

    public SawStoriesAdapter(AppCompatActivity context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_suggestion_toutvoir, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.bindView(user);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final CircleImageView profile_photo;
        private final TextView username, full_name;
        private final RelativeLayout relLayout_go_user_profile;

        // firebase
        private final String user_id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            full_name = itemView.findViewById(R.id.full_name);
            relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);
        }

        void bindView(User user) {
            username.setText(user.getUsername());
            full_name.setText(user.getFullName());

            Glide.with(context.getApplicationContext())
                    .load(user.getProfileUrl())
                    .into(profile_photo);


            relLayout_go_user_profile.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        user.getUsername());

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
        }
    }
}

