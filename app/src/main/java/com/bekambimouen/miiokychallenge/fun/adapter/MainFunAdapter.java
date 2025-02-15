package com.bekambimouen.miiokychallenge.fun.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.fun.adapter.viewholder.FunProfileViewHolder;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainFunAdapter extends RecyclerView.Adapter<FunProfileViewHolder> {
    private static final String TAG = "MainFunAdapter";

    private final OnLoadMoreItemsListener mOnLoadMoreItemsListener;

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

    public MainFunAdapter(AppCompatActivity context, ArrayList<BattleModel_fun> list,
                          RecyclerView recyclerView, ProgressBar progressBar,
                          OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        holder.menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            if (model.getUser_id().equals(user_id)) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.menu_item);
                //inflating menu from xml resource
                popup.inflate(R.menu.more_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_delete_one_publication, null);
                        TextView negativeButton = view1.findViewById(R.id.tv_no);
                        TextView positiveButton = view1.findViewById(R.id.tv_yes);
                        builder.setView(view1);

                        Dialog dialog = builder.create();
                        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                        InsetDrawable inset = new InsetDrawable(back, 50);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(inset);
                        dialog.show();

                        negativeButton.setOnClickListener(view2 -> dialog.dismiss());

                        positiveButton.setOnClickListener(view2 -> {
                            dialog.dismiss();

                            showLoading();
                            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(model.getUrl());

                            photoRef.delete().addOnSuccessListener(unused -> {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_fun))
                                        .child(model.getUser_id())
                                        .child(model.getPhoto_id())
                                        .removeValue().addOnSuccessListener(unused1 -> {
                                            removeAt(holder.getLayoutPosition());
                                            progresDialog.dismiss();

                                            holder.itemView.setVisibility(View.GONE);
                                            holder.release();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_videos))
                                        .child(model.getPhoto_id())
                                        .removeValue();

                            }).addOnFailureListener(e -> Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show());
                        });
                    }
                    return false;
                });
                //displaying the popup
                popup.show();

            }
        });

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        if (reachedEndOfList(position))
            loadMoreData();
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData() {
        if (mOnLoadMoreItemsListener != null) {
            mOnLoadMoreItemsListener.onLoadMoreItems();
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
