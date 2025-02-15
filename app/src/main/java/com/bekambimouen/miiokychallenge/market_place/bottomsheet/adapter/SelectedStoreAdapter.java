package com.bekambimouen.miiokychallenge.market_place.bottomsheet.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.publication.MarketSellInStore;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedStoreAdapter extends RecyclerView.Adapter<SelectedStoreAdapter.MyViewHolder> {
    private static final String TAG = "SelectedStoreAdapter";

    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final Dialog dialog;
    private final ProgressBar progressBar;

    public SelectedStoreAdapter(AppCompatActivity context, List<MarketModel> list, Dialog dialog, ProgressBar progressBar) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bottomsheet_market_store_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MarketModel market_model = list.get(position);
        holder.bind(market_model);

        if (View.VISIBLE == progressBar.getVisibility())
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final CircleImageView profile_photo;
        private final TextView store_name;
        private final TextView number_of_announce;

        // vars
        private int t;

        public MyViewHolder(View itemView) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.profile_photo);
            store_name = itemView.findViewById(R.id.store_name);
            number_of_announce = itemView.findViewById(R.id.number_of_announce);
        }

        public void bind(MarketModel market_model) {
            store_name.setText(market_model.getStore_name());

            Glide.with(context.getApplicationContext())
                    .load(market_model.getProfile_photo())
                    .into(profile_photo);

            itemView.setOnClickListener(view -> {
                dialog.dismiss();
                Gson gson = new Gson();
                String myJson = gson.toJson(market_model);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketSellInStore.class);
                intent.putExtra("market_model", myJson);
                context.startActivity(intent);
            });

            // number of announce
            Query query2 = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_market))
                    .child(market_model.getStore_id()) // store_id
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(market_model.getStore_id());

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+ds.getKey());
                        t++;
                    }

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

