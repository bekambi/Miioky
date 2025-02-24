// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.instacropper.InstaCropperView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGroupCreateMemberBackgroundProfilePhotoBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final InstaCropperView instacropper;

  @NonNull
  public final ProgressBar mainProgressBar;

  @NonNull
  public final RelativeLayout parent;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final ProgressBar progressbar;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final RelativeLayout relLayout1;

  private ActivityGroupCreateMemberBackgroundProfilePhotoBinding(@NonNull RelativeLayout rootView,
      @NonNull InstaCropperView instacropper, @NonNull ProgressBar mainProgressBar,
      @NonNull RelativeLayout parent, @NonNull ProgressBar progressBar,
      @NonNull ProgressBar progressbar, @NonNull RecyclerView recyclerView,
      @NonNull RelativeLayout relLayout1) {
    this.rootView = rootView;
    this.instacropper = instacropper;
    this.mainProgressBar = mainProgressBar;
    this.parent = parent;
    this.progressBar = progressBar;
    this.progressbar = progressbar;
    this.recyclerView = recyclerView;
    this.relLayout1 = relLayout1;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGroupCreateMemberBackgroundProfilePhotoBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGroupCreateMemberBackgroundProfilePhotoBinding inflate(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_group_create_member_background_profile_photo, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGroupCreateMemberBackgroundProfilePhotoBinding bind(
      @NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.instacropper;
      InstaCropperView instacropper = ViewBindings.findChildViewById(rootView, id);
      if (instacropper == null) {
        break missingId;
      }

      id = R.id.main_progressBar;
      ProgressBar mainProgressBar = ViewBindings.findChildViewById(rootView, id);
      if (mainProgressBar == null) {
        break missingId;
      }

      RelativeLayout parent = (RelativeLayout) rootView;

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.progressbar;
      ProgressBar progressbar = ViewBindings.findChildViewById(rootView, id);
      if (progressbar == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.relLayout1;
      RelativeLayout relLayout1 = ViewBindings.findChildViewById(rootView, id);
      if (relLayout1 == null) {
        break missingId;
      }

      return new ActivityGroupCreateMemberBackgroundProfilePhotoBinding((RelativeLayout) rootView,
          instacropper, mainProgressBar, parent, progressBar, progressbar, recyclerView,
          relLayout1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
