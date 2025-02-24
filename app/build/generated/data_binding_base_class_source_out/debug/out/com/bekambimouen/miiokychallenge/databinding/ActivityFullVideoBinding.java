// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityFullVideoBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView arrowBack;

  @NonNull
  public final RelativeLayout parent;

  @NonNull
  public final Container recyclerView;

  @NonNull
  public final Toolbar toolBar;

  private ActivityFullVideoBinding(@NonNull RelativeLayout rootView, @NonNull ImageView arrowBack,
      @NonNull RelativeLayout parent, @NonNull Container recyclerView, @NonNull Toolbar toolBar) {
    this.rootView = rootView;
    this.arrowBack = arrowBack;
    this.parent = parent;
    this.recyclerView = recyclerView;
    this.toolBar = toolBar;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityFullVideoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityFullVideoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_full_video, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityFullVideoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.arrowBack;
      ImageView arrowBack = ViewBindings.findChildViewById(rootView, id);
      if (arrowBack == null) {
        break missingId;
      }

      RelativeLayout parent = (RelativeLayout) rootView;

      id = R.id.recyclerView;
      Container recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.toolBar;
      Toolbar toolBar = ViewBindings.findChildViewById(rootView, id);
      if (toolBar == null) {
        break missingId;
      }

      return new ActivityFullVideoBinding((RelativeLayout) rootView, arrowBack, parent,
          recyclerView, toolBar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
