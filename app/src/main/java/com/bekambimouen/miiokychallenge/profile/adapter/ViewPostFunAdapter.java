package com.bekambimouen.miiokychallenge.profile.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.fun.adapter.viewholder.FunProfileViewHolder;
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
import java.util.Map;
import java.util.Objects;

public class ViewPostFunAdapter extends RecyclerView.Adapter<FunProfileViewHolder> {
    private static final String TAG = "ProfileViewPostFunAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    // vars
    private final AppCompatActivity context;
    private final ArrayList<BattleModel_fun> list;
    private final RecyclerView recyclerView;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    private CustomShareProgressView progresDialog;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.show();
    }

    private boolean isMute = false;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public ViewPostFunAdapter(AppCompatActivity context, ArrayList<BattleModel_fun> list,
                              OnLoadMoreItemsListener mOnLoadMoreItemsListener,
                              RecyclerView recyclerView, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

    @NonNull
    @Override
    public FunProfileViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_fun, parent, false);
        return new FunProfileViewHolder(context, relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull  FunProfileViewHolder holder, int position) {
        BattleModel_fun model = list.get(position);

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

                    Log.d(TAG, "onDataChange: found user: "+user.getUsername());

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
                        intent.putExtra("intent_fun", "intent_fun");
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
                        intent.putExtra("intent_fun", "intent_fun");
                        context.startActivity(intent);
                    });
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
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
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

        if (model.getUser_id().equals(user_id))
            holder.menu_item.setVisibility(View.VISIBLE);

        // delete post
        deletePost(model, model.getPhoto_id(), holder.menu_item, holder);

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    // delete post from group
    private void deletePost(BattleModel_fun model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            if (model.getUser_id().equals(user_id)) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, menu_item);
                //inflating menu from xml resource
                popup.inflate(R.menu.more_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        // show dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                        TextView negativeButton = v.findViewById(R.id.tv_no);
                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                        builder.setView(v);

                        Dialog d = builder.create();
                        d.show();

                        negativeButton.setText(context.getString(R.string.no));
                        positiveButton.setText(context.getString(R.string.delete));

                        dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)+" ?"));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                        negativeButton.setOnClickListener(view1 -> d.dismiss());
                        positiveButton.setOnClickListener(view2 -> {
                            d.dismiss();
                            // internet control
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                            if (!isConnected) {
                                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

                            } else {
                                showLoading();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("suppressed", true);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_videos))
                                        .child(photo_id)
                                        .updateChildren(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_fun))
                                        .child(model.getUser_id())
                                        .child(photo_id)
                                        .updateChildren(map).addOnSuccessListener(unused1 -> {
                                            removeAt(holder.getLayoutPosition());
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });
                            }
                        });
                    }
                    return false;
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) context;
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    // this will reduce blinking effect after data change
    @Override
    public long getItemId(int position) {
        BattleModel_fun model = list.get(position);
        return model.getDate_created();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }
}
