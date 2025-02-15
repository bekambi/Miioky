package com.bekambimouen.miiokychallenge.market_place.publication.adapter;

import android.content.Intent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.market_place.recycler_full_image.MarketRecyclerFullImage;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MarketAboutArticleAdapter extends RecyclerView.Adapter<MarketAboutArticleAdapter.ViewHolder> {
    private final AppCompatActivity context;
    private final ArrayList<String> list;
    private final MarketModel market_model;
    private final RelativeLayout relLayout_view_overlay;

    public MarketAboutArticleAdapter(AppCompatActivity context, ArrayList<String> list, MarketModel market_model,
                                     RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.market_model = market_model;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_market_product_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = list.get(position);
        holder.bind(url);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

        public void bind(String url) {
            Glide.with(context.getApplicationContext())
                    .load(url)
                    .into(image);

            itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketRecyclerFullImage.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(market_model);
                intent.putExtra("market_model", myGson);
                intent.putExtra("position", getLayoutPosition());
                context.startActivity(intent);
            });
        }
    }
}

