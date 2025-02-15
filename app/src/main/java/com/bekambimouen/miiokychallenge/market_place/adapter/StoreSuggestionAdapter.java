package com.bekambimouen.miiokychallenge.market_place.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.MarketStore;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMarketImages;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Utils_Map_StoreFollowingModel;
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

public class StoreSuggestionAdapter extends RecyclerView.Adapter<StoreSuggestionAdapter.MyViewHolder> {
    private static final String TAG = "StoreSuggestionAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    public void setOnLoadMoreItemsListener(OnLoadMoreItemsListener listener) {
        this.mOnLoadMoreItemsListener = listener;
    }
    // vars
    private final AppCompatActivity context;
    private final ArrayList<MarketModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public StoreSuggestionAdapter(AppCompatActivity context, ArrayList<MarketModel> list, ProgressBar progressBar,
                                  RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_store_suggestion_item, parent, false);
        return new MyViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MarketModel market_model = list.get(position);

        try {
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+1));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        holder.bind(market_model);

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "PublicGroupViewHolder";

        private final ImageView profile_photo;
        private final TextView store_name, nber_of_members;
        private final TextView bouton_voir, bouton_rejoindre;
        public final RelativeLayout close;

        // vars
        private final AppCompatActivity context;
        private int t = 0;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public MyViewHolder(AppCompatActivity context, View itemView) {
            super(itemView);
            this.context = context;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            close = itemView.findViewById(R.id.close);
            nber_of_members = itemView.findViewById(R.id.nber_of_members);
            store_name = itemView.findViewById(R.id.store_name);
            bouton_voir = itemView.findViewById(R.id.bouton_voir);
            bouton_rejoindre = itemView.findViewById(R.id.bouton_rejoindre);

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }

        public void bind(MarketModel market_model) {
            store_name.setText(market_model.getStore_name());

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(market_model.getProfile_photo())
                    .into(profile_photo);

            // get the store followers
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_store_followers))
                    .child(market_model.getStore_id());
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                        t++;
                    }

                    if (t == 0) {
                        nber_of_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.subscriber)));

                    } else {
                        nber_of_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.subscribers)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            close.setOnClickListener(view -> {
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
                    itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    itemView.setLayoutParams(params);

                    String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                    HashMap<String, Object> map_account = new HashMap<>();
                    map_account.put("user_id", market_model.getStore_id());
                    assert newKey != null;
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_remove_Suggestion))
                            .child(user_id)
                            .child(newKey)
                            .setValue(map_account);

                    d.dismiss();
                });
            });

            bouton_voir.setOnClickListener(v1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myJson = gson.toJson(market_model);
                Intent intent = new Intent(context, MarketStore.class);
                intent.putExtra("market_model", myJson);
                context.startActivity(intent);
            });

            profile_photo.setOnClickListener(v1 -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myJson = gson.toJson(market_model);
                Intent intent = new Intent(context, MarketStore.class);
                intent.putExtra("market_model", myJson);
                context.startActivity(intent);
            });

            // follow or unfollow the store
            isFollowing(market_model);

            // get the following model data
            HashMap<Object, Object> map = Utils_Map_StoreFollowingModel.storeFollowingModel(market_model.getStore_owner(), user_id, market_model.getStore_id());

            bouton_rejoindre.setOnClickListener(view -> {
                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_store_following))
                        .child(user_id)
                        .child(market_model.getStore_id())
                        .setValue(map);

                FirebaseDatabase.getInstance().getReference()
                        .child(context.getString(R.string.dbname_store_followers))
                        .child(market_model.getStore_id())
                        .child(user_id)
                        .setValue(map);
                setFollowing();
            });
        }

        private void setFollowing(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            bouton_rejoindre.setVisibility(View.GONE);
            bouton_voir.setVisibility(View.VISIBLE);
        }

        private void setUnfollowing(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            bouton_rejoindre.setVisibility(View.VISIBLE);
            bouton_voir.setVisibility(View.GONE);
        }

        private void isFollowing(MarketModel market_model){
            Log.d(TAG, "isFollowing: checking if following this users.");
            setUnfollowing();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(context.getString(R.string.dbname_store_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(market_model.getStore_id());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                        setFollowing();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

