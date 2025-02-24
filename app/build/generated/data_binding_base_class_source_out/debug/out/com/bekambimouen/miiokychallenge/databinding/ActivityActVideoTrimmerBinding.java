// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.media3.ui.PlayerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityActVideoTrimmerBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView_;

  @NonNull
  public final ImageView imagePlayPause;

  @NonNull
  public final PlayerView playerViewLib;

  @NonNull
  public final ProgressBar progressCircular;

  @NonNull
  public final CoordinatorLayout rootView;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final TextView txtEndDuration;

  @NonNull
  public final TextView txtStartDuration;

  private ActivityActVideoTrimmerBinding(@NonNull CoordinatorLayout rootView_,
      @NonNull ImageView imagePlayPause, @NonNull PlayerView playerViewLib,
      @NonNull ProgressBar progressCircular, @NonNull CoordinatorLayout rootView,
      @NonNull Toolbar toolbar, @NonNull TextView txtEndDuration,
      @NonNull TextView txtStartDuration) {
    this.rootView_ = rootView_;
    this.imagePlayPause = imagePlayPause;
    this.playerViewLib = playerViewLib;
    this.progressCircular = progressCircular;
    this.rootView = rootView;
    this.toolbar = toolbar;
    this.txtEndDuration = txtEndDuration;
    this.txtStartDuration = txtStartDuration;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView_;
  }

  @NonNull
  public static ActivityActVideoTrimmerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityActVideoTrimmerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_act_video_trimmer, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityActVideoTrimmerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.image_play_pause;
      ImageView imagePlayPause = ViewBindings.findChildViewById(rootView, id);
      if (imagePlayPause == null) {
        break missingId;
      }

      id = R.id.player_view_lib;
      PlayerView playerViewLib = ViewBindings.findChildViewById(rootView, id);
      if (playerViewLib == null) {
        break missingId;
      }

      id = R.id.progress_circular;
      ProgressBar progressCircular = ViewBindings.findChildViewById(rootView, id);
      if (progressCircular == null) {
        break missingId;
      }

      CoordinatorLayout rootView_ = (CoordinatorLayout) rootView;

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.txt_end_duration;
      TextView txtEndDuration = ViewBindings.findChildViewById(rootView, id);
      if (txtEndDuration == null) {
        break missingId;
      }

      id = R.id.txt_start_duration;
      TextView txtStartDuration = ViewBindings.findChildViewById(rootView, id);
      if (txtStartDuration == null) {
        break missingId;
      }

      return new ActivityActVideoTrimmerBinding((CoordinatorLayout) rootView, imagePlayPause,
          playerViewLib, progressCircular, rootView_, toolbar, txtEndDuration, txtStartDuration);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
