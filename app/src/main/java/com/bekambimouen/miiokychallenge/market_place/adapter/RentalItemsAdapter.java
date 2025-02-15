package com.bekambimouen.miiokychallenge.market_place.adapter;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.MarketAboutProduct;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentalItemsAdapter extends RecyclerView.Adapter<RentalItemsAdapter.MyViewHolder> {
    private final AppCompatActivity context;
    private final List<MarketModel> list;
    private final ProgressBar progressBar_to_rent_out, progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public RentalItemsAdapter(AppCompatActivity context, List<MarketModel> list, ProgressBar progressBar,
                              ProgressBar progressBar_to_rent_out, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar_to_rent_out = progressBar_to_rent_out;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
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

        if (View.VISIBLE == progressBar_to_rent_out.getVisibility()) {
            progressBar.setVisibility(View.GONE);
            progressBar_to_rent_out.setVisibility(View.GONE);
        }
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

        public MyViewHolder(View itemView) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.profile_photo);
            store_name = itemView.findViewById(R.id.store_name);
            number_of_announce = itemView.findViewById(R.id.number_of_announce);
        }

        public void bind(MarketModel model) {
            store_name.setText(model.getProduct_name());

            Glide.with(context.getApplicationContext())
                    .load(model.getMain_photo())
                    .into(profile_photo);

            // preload all product images
            if (!TextUtils.isEmpty(model.getUrli()))
                Glide.with(context).load(model.getUrli()).preload();
            if (!TextUtils.isEmpty(model.getUrlii()))
                Glide.with(context).load(model.getUrlii()).preload();
            if (!TextUtils.isEmpty(model.getUrliii()))
                Glide.with(context).load(model.getUrliii()).preload();
            if (!TextUtils.isEmpty(model.getUrliv()))
                Glide.with(context).load(model.getUrliv()).preload();
            if (!TextUtils.isEmpty(model.getUrlv()))
                Glide.with(context).load(model.getUrlv()).preload();
            if (!TextUtils.isEmpty(model.getUrlvi()))
                Glide.with(context).load(model.getUrlvi()).preload();
            if (!TextUtils.isEmpty(model.getUrlvii()))
                Glide.with(context).load(model.getUrlvii()).preload();
            if (!TextUtils.isEmpty(model.getUrlviii()))
                Glide.with(context).load(model.getUrlviii()).preload();
            if (!TextUtils.isEmpty(model.getUrlix()))
                Glide.with(context).load(model.getUrlix()).preload();
            if (!TextUtils.isEmpty(model.getUrlx()))
                Glide.with(context).load(model.getUrlx()).preload();
            if (!TextUtils.isEmpty(model.getUrlxi()))
                Glide.with(context).load(model.getUrlxi()).preload();
            if (!TextUtils.isEmpty(model.getUrlxii()))
                Glide.with(context).load(model.getUrlxii()).preload();
            if (!TextUtils.isEmpty(model.getUrlxiii()))
                Glide.with(context).load(model.getUrlxiii()).preload();
            if (!TextUtils.isEmpty(model.getUrlxiv()))
                Glide.with(context).load(model.getUrlxiv()).preload();
            if (!TextUtils.isEmpty(model.getUrlxv()))
                Glide.with(context).load(model.getUrlxv()).preload();
            if (!TextUtils.isEmpty(model.getUrlxvi()))
                Glide.with(context).load(model.getUrlxvi()).preload();
            if (!TextUtils.isEmpty(model.getUrlxvii()))
                Glide.with(context).load(model.getUrlxvii()).preload();

            number_of_announce.setText(Html.fromHtml(model.getPrice()+" "+model.getDevise()));

            // number of announce
            itemView.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myJson = gson.toJson(model);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, MarketAboutProduct.class);
                intent.putExtra("market_model", myJson);
                context.startActivity(intent);
            });
        }
    }
}

