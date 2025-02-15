package com.bekambimouen.miiokychallenge.market_place.adapter;

import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
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
import com.bekambimouen.miiokychallenge.interfaces.BooleanInfoListener;
import com.bekambimouen.miiokychallenge.market_place.MarketAboutProduct;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMarketImages;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

public class MarketGridAdapter extends RecyclerView.Adapter<MarketGridAdapter.MyViewHolder> {
    private static final String TAG = "MarketGridAdapter";

    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final BooleanInfoListener booleanInfoListener;
    private final RelativeLayout relLayout_view_overlay;

    public MarketGridAdapter(AppCompatActivity context, List<MarketModel> list, BooleanInfoListener booleanInfoListener,
                             RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.booleanInfoListener = booleanInfoListener;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_market_grid_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MarketModel market_model = list.get(position);

        try {
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+1));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+2));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+3));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+4));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+5));
            PreloadMarketImages.getPreloadMarketImages(context, list.get(position+6));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }
        holder.bind(market_model);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageview;
        private final TextView price, product_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageview);
            price = itemView.findViewById(R.id.price);
            product_name = itemView.findViewById(R.id.product_name);
        }

        public void bind(MarketModel market_model) {
            price.setText(Html.fromHtml(market_model.getPrice().replace(",", " ")+" "+market_model.getDevise()));
            product_name.setText(market_model.getProduct_name());

            Glide.with(context.getApplicationContext())
                    .load(market_model.getMain_photo())
                    .into(imageview);

            itemView.setOnClickListener(view -> {
                booleanInfoListener.setInfoBoolean(true);
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myJson = gson.toJson(market_model);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketAboutProduct.class);
                intent.putExtra("market_model", myJson);
                intent.putExtra("from_seller_store", "from_seller_store");
                context.startActivity(intent);
            });
        }
    }
}

