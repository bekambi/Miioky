package com.bekambimouen.miiokychallenge.market_place.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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

public class MarketExplorerProductViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "MarketExplorerViewHolder";

    // widgets
    public final ImageView product_image;
    private final TextView product_name;
    private final TextView price;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_view_overlay;
    private final GestureDetector mDetectorLike;
    private MarketModel marketModel;

    @SuppressLint("ClickableViewAccessibility")
    public MarketExplorerProductViewHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        product_image = itemView.findViewById(R.id.product_image);
        product_name = itemView.findViewById(R.id.product_name);
        price = itemView.findViewById(R.id.price);

        // for double tap to like
        mDetectorLike = new GestureDetector(context, new GestureListenerLike());
        product_image.setOnTouchListener((view, motionEvent) -> mDetectorLike.onTouchEvent(motionEvent));
    }

    public void bind(MarketModel model) {
        marketModel = model;


        Glide.with(context.getApplicationContext())
                .load(marketModel.getMain_photo())
                .into(product_image);

        Glide.with(context).load(model.getUrli()).preload();

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

        product_name.setText(model.getProduct_name());

        if (model.isLocation())
            price.setText(Html.fromHtml(model.getPrice().replace(",", " ")
                    + " " + model.getDevise() + "/" + model.getLocation_period()));
        else
            price.setText(Html.fromHtml(model.getPrice().replace(",", " ")+" "+model.getDevise()));
    }

    private final class GestureListenerLike extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String myJson = gson.toJson(marketModel);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, MarketAboutProduct.class);
            intent.putExtra("market_model", myJson);
            context.startActivity(intent);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Log.d(TAG, "onDoubleTap: single tap detected.");

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    }
}

