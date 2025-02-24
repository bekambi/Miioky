// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.ui.PlayerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutGroupVideoBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final FrameLayout frameLayout;

  @NonNull
  public final ImageView imgPlay;

  @NonNull
  public final ImageView imgVol;

  @NonNull
  public final PlayerView playerView;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final RelativeLayout relLayoutImgPlay;

  @NonNull
  public final RelativeLayout relVol;

  @NonNull
  public final ImageView thumbnail;

  private LayoutGroupVideoBinding(@NonNull RelativeLayout rootView,
      @NonNull FrameLayout frameLayout, @NonNull ImageView imgPlay, @NonNull ImageView imgVol,
      @NonNull PlayerView playerView, @NonNull ProgressBar progressBar,
      @NonNull RelativeLayout relLayoutImgPlay, @NonNull RelativeLayout relVol,
      @NonNull ImageView thumbnail) {
    this.rootView = rootView;
    this.frameLayout = frameLayout;
    this.imgPlay = imgPlay;
    this.imgVol = imgVol;
    this.playerView = playerView;
    this.progressBar = progressBar;
    this.relLayoutImgPlay = relLayoutImgPlay;
    this.relVol = relVol;
    this.thumbnail = thumbnail;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutGroupVideoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutGroupVideoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_group_video, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutGroupVideoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.frame_layout;
      FrameLayout frameLayout = ViewBindings.findChildViewById(rootView, id);
      if (frameLayout == null) {
        break missingId;
      }

      id = R.id.img_play;
      ImageView imgPlay = ViewBindings.findChildViewById(rootView, id);
      if (imgPlay == null) {
        break missingId;
      }

      id = R.id.img_vol;
      ImageView imgVol = ViewBindings.findChildViewById(rootView, id);
      if (imgVol == null) {
        break missingId;
      }

      id = R.id.playerView;
      PlayerView playerView = ViewBindings.findChildViewById(rootView, id);
      if (playerView == null) {
        break missingId;
      }

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.relLayout_img_play;
      RelativeLayout relLayoutImgPlay = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutImgPlay == null) {
        break missingId;
      }

      id = R.id.rel_vol;
      RelativeLayout relVol = ViewBindings.findChildViewById(rootView, id);
      if (relVol == null) {
        break missingId;
      }

      id = R.id.thumbnail;
      ImageView thumbnail = ViewBindings.findChildViewById(rootView, id);
      if (thumbnail == null) {
        break missingId;
      }

      return new LayoutGroupVideoBinding((RelativeLayout) rootView, frameLayout, imgPlay, imgVol,
          playerView, progressBar, relLayoutImgPlay, relVol, thumbnail);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
