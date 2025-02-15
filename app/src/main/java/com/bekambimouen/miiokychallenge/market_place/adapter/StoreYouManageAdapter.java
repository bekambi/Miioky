package com.bekambimouen.miiokychallenge.market_place.adapter;

import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.MarketStore;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreYouManageAdapter extends RecyclerView.Adapter<StoreYouManageAdapter.MyViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final RelativeLayout relLayout_view_overlay;

    public StoreYouManageAdapter(AppCompatActivity context, List<MarketModel> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_bottomsheet_market_store_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MarketModel marketModel = list.get(position);
        holder.bind(marketModel);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profile_photo;
        private final TextView store_name;
        private final TextView number_of_announce;

        // vars
        private int t;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.profile_photo);
            store_name = itemView.findViewById(R.id.store_name);
            number_of_announce = itemView.findViewById(R.id.number_of_announce);
        }

        public void bind(MarketModel marketModel) {
            store_name.setText(marketModel.getStore_name());

            Glide.with(context.getApplicationContext())
                    .load(marketModel.getProfile_photo())
                    .into(profile_photo);

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
        }
    }
}

