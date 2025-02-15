package com.bekambimouen.miiokychallenge.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bekambimouen.miiokychallenge.R;


@SuppressWarnings("ConstantConditions")
public class CustomShareProgressView extends Dialog {

    @SuppressLint("InflateParams")
    public CustomShareProgressView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.share_progress_dialog, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        this.setCancelable(false);
        this.setContentView(view);
    }
}
