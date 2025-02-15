package com.bekambimouen.miiokychallenge.video_trimmer.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.bekambimouen.miiokychallenge.R;


public class CustomProgress extends ProgressBar {

    public CustomProgress(Context context) {
        super(context);
        setTintColor(context);
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTintColor(context);
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTintColor(context);
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTintColor(context);
    }

    private void setTintColor(Context context) {
        this.getIndeterminateDrawable().setColorFilter(TrimmerUtils.getColor(
                context, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
    }
}
