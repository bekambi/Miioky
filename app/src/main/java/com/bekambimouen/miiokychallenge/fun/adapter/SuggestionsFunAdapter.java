package com.bekambimouen.miiokychallenge.fun.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.fun.adapter.viewholder.FunProfileViewHolder;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
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
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SuggestionsFunAdapter extends PaginatedAdapter<BattleModel_fun, FunProfileViewHolder> {
    private static final String TAG = "FunSuggestionsAdapter";

    // vars
    private final AppCompatActivity context;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    private BottomSheetMenuOneFile bottomSheet;
    private boolean isMute = false;
    private final HttpProxyCacheServer proxy;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public SuggestionsFunAdapter(AppCompatActivity context, RecyclerView recyclerView, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        proxy = App.getProxy(context);
    }

    @NonNull
    @Override
    public FunProfileViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_video_fun, parent, false);
        return new FunProfileViewHolder(context, relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull  FunProfileViewHolder holder, int position) {
        BattleModel_fun model = getItem(position);

        holder.bindVideo(model);

        //get the profile image and username
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    holder.username.setText(Html.fromHtml("#"+user.getUsername()));

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(holder.profil_image);

                    holder.username.setOnClickListener(v -> {
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
                        intent.putExtra("from_fun", "from_fun");
                        context.startActivity(intent);
                    });

                    holder.relLayout_profil.setOnClickListener(v -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getFullName());

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
                        intent.putExtra("from_fun", "from_fun");
                        context.startActivity(intent);
                    });

                    // follow unfollow person ______________________________________________________________
                    // public account
                    if (user.getScope().equals(context.getString(R.string.title_public))) {
                        isFollowing_public_account(user, holder);

                        HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
                        HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

                        holder.button_follow.setOnClickListener(view -> {
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
                            setFollowing_public_account(holder);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError databaseError) {

            }
        });

        holder.rel_vol.setOnClickListener(view -> {
            if (holder.player != null) {
                if (holder.player.getVolume() == 0) {
                    isMute = false;
                    holder.player.setVolume(1);
                    holder.img_vol.setImageResource(R.drawable.ic_unmute);

                } else {
                    isMute = true;
                    holder.player.setVolume(0);
                    holder.img_vol.setImageResource(R.drawable.ic_mute);
                }
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull  RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (holder.player != null) {
                        if (isMute) {
                            try {
                                holder.player.setVolume(0);
                                holder.img_vol.setImageResource(R.drawable.ic_mute);
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                            }

                        } else {
                            try {
                                holder.player.setVolume(1);
                                holder.img_vol.setImageResource(R.drawable.ic_unmute);
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onScrollStateChanged: "+e);
                            }
                        }
                    }
                }
            }
        });

        bottomSheet = new BottomSheetMenuOneFile(context);
        holder.menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);if (bottomSheet.isAdded())
                return;
            Bundle bundle = new Bundle();
            bundle.putParcelable("menu_item_fun", model);
            bottomSheet.setArguments(bundle);
            bottomSheet.show(context.getSupportFragmentManager(),
                    "TAG");
        });

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    // follow unfollow person __________________________________________________________________
    // public
    private void setFollowing_public_account(FunProfileViewHolder holder){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        holder.point.setVisibility(View.GONE);
        holder.relLayout_follow.setVisibility(View.GONE);
    }

    private void isFollowing_public_account(User user, FunProfileViewHolder holder){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing_public_account(holder);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
}
