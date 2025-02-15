package com.bekambimouen.miiokychallenge.market_place.adapter;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.MarketStore;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
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

public class YourStoresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int FOLLOWING_STORES = 1;

    // vars
    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public YourStoresAdapter(AppCompatActivity context, List<MarketModel> list, ProgressBar progressBar,
                             RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        this.list.remove(null);
        this.list.add(0, null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_market_recyclerview_your_store, parent, false);
            return new YourStores(view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_bottomsheet_market_store_item, parent, false);
            return new FollowingStores(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MarketModel marketModel = list.get(position);

        final int itemViewType = getItemViewType(position);
        if (itemViewType == FOLLOWING_STORES) {
            FollowingStores followingStores = (FollowingStores) holder;
            followingStores.bind(marketModel);
        }

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
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
        if (position == 0)
            return HEADER;
        else
            return FOLLOWING_STORES;
    }

    public class YourStores extends RecyclerView.ViewHolder {
        // widgets
        private final TextView items_you_are_rented, stores_you_manage;
        private final RecyclerView recycler_your_stores, recyclerView_to_rent_out;
        private final ProgressBar progressBar_to_rent_out;
        private final RelativeLayout relLayout_to_rent_out;

        // vars
        private StoreYouManageAdapter adapter;
        private RentalItemsAdapter adapter_rented;
        private List<MarketModel> stores_list;
        private List<MarketModel> list_to_rent_out;
        public ArrayList<String> followingUserList;
        public ArrayList<String> store_id_List;
        public ArrayList<String> userIDList;
        private final Handler handler;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public YourStores(@NonNull View itemView) {
            super(itemView);
            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            handler = new Handler(Looper.getMainLooper());

            TextView stores_you_follow = itemView.findViewById(R.id.stores_you_follow);
            // hide stores you follow isf list size is equal zero
            if (list.size() == 1)
                stores_you_follow.setVisibility(View.GONE);
            recycler_your_stores = itemView.findViewById(R.id.recycler_your_stores);
            recycler_your_stores.setLayoutManager(new LinearLayoutManager(context));

            // to_rent_out objects
            relLayout_to_rent_out = itemView.findViewById(R.id.relLayout_to_rent_out);
            progressBar_to_rent_out = itemView.findViewById(R.id.progressBar_to_rent_out);
            items_you_are_rented = itemView.findViewById(R.id.items_you_are_rented);
            stores_you_manage = itemView.findViewById(R.id.stores_you_manage);
            recyclerView_to_rent_out = itemView.findViewById(R.id.recyclerView_to_rent_out);
            recyclerView_to_rent_out.setLayoutManager(new LinearLayoutManager(context));

            getStores();
            otherto_rent_outObjects();
        }

        @SuppressLint("NotifyDataSetChanged")
        private void clearAll(){
            if(stores_list != null){
                stores_list.clear();
            }
            if(followingUserList != null){
                followingUserList.clear();
            }
            if(store_id_List != null){
                store_id_List.clear();
            }

            if(userIDList != null){
                userIDList.clear();
            }

            if(adapter != null){
                adapter = null;
            }

            if(recycler_your_stores != null){
                handler.post(() -> recycler_your_stores.setAdapter(null));
            }
            stores_list = new ArrayList<>();
            followingUserList = new ArrayList<>();
            store_id_List = new ArrayList<>();
            userIDList = new ArrayList<>();
        }

        // get list of user's stores
        private void getStores() {
            clearAll();
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_stores))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_store_owner))
                    .equalTo(user_id);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, dataSnapshot);

                        if (market_model.getStore_owner().equals(user_id))
                            if (!market_model.isSuppressed())
                                stores_list.add(market_model);
                    }

                    stores_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                            .compareTo(String.valueOf(o1.getDate_created())));

                    adapter = new StoreYouManageAdapter(context, stores_list, relLayout_view_overlay);
                    recycler_your_stores.setAdapter(adapter);
                    if (adapter.getItemCount() == 0)
                        stores_you_manage.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void clearAllRented(){
            if(list_to_rent_out != null){
                list_to_rent_out.clear();
            }
            if(adapter_rented != null){
                adapter_rented = null;
            }

            if(recyclerView_to_rent_out != null){
                handler.post(() -> recyclerView_to_rent_out.setAdapter(null));
            }
            list_to_rent_out = new ArrayList<>();
        }

        // other stores of the same seller
        private void otherto_rent_outObjects() {
            clearAllRented();
            Query query = myRef
                    .child(context.getString(R.string.dbname_market))
                    .child(user_id) // user_id
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        if (!market_model.isSuppressed())
                            list_to_rent_out.add(market_model);
                    }

                    Collections.reverse(list_to_rent_out);

                    adapter_rented = new RentalItemsAdapter(context, list_to_rent_out, progressBar, progressBar_to_rent_out, relLayout_view_overlay);
                    recyclerView_to_rent_out.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView_to_rent_out.setAdapter(adapter_rented);

                    if (adapter_rented.getItemCount() == 0) {
                        relLayout_to_rent_out.setVisibility(View.GONE);
                        items_you_are_rented.setVisibility(View.GONE);
                        progressBar_to_rent_out.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public class FollowingStores extends RecyclerView.ViewHolder {
        // widgets
        private final ImageView profile_photo;
        private final TextView store_name, number_of_announce;

        // vars
        private int t;

        public FollowingStores(@NonNull View itemView) {
            super(itemView);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            store_name = itemView.findViewById(R.id.store_name);
            number_of_announce = itemView.findViewById(R.id.number_of_announce);
        }

        public void bind(MarketModel marketModel) {
            //get stores profile image and group name
            store_name.setText(marketModel.getStore_name());

            Glide.with(context.getApplicationContext())
                    .load(marketModel.getProfile_photo())
                    .into(profile_photo);

            // number of announce
            Query query2 = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_market))
                    .child(marketModel.getStore_id()) // store_id
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(marketModel.getStore_id());

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        if (!market_model.isSuppressed())
                            t++;
                    }

                    if (t == 1)
                        number_of_announce.setText(Html.fromHtml("<b> "+t+"+</b> "
                                +context.getString(R.string.announce)+"."));
                    else
                        number_of_announce.setText(Html.fromHtml("<b> "+t+"+</b> "
                                +context.getString(R.string.announces)+"."));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myJson = gson.toJson(marketModel);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketStore.class);
                intent.putExtra("market_model", myJson);
                context.startActivity(intent);
            });
        }
    }
}

