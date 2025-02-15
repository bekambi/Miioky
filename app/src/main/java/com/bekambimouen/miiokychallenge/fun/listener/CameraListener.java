package com.bekambimouen.miiokychallenge.fun.listener;

import android.view.View;
import android.widget.ImageView;

public interface CameraListener {
    void changeCamera(View view);
    void retoucheBouton(View view);
    void closeBackToCamera(View view);
    void submitClick(View view);
    void flashOff(View view, ImageView imageView);
    void playPause(View view, ImageView imageView);
}
