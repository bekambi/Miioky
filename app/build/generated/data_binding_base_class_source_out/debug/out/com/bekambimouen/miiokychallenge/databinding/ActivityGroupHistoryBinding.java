// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGroupHistoryBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final RelativeLayout arrowBack;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final ImageView close;

  @NonNull
  public final TextView dateGroupCreation;

  @NonNull
  public final ImageView iconsTime;

  @NonNull
  public final LinearLayout parent;

  @NonNull
  public final TextView privatePublic;

  @NonNull
  public final ImageView profilePhotoToolbar;

  @NonNull
  public final Toolbar toolBar;

  @NonNull
  public final TextView toolBarGroupName;

  @NonNull
  public final TextView toolBarTextview;

  private ActivityGroupHistoryBinding(@NonNull LinearLayout rootView,
      @NonNull RelativeLayout arrowBack, @NonNull CardView cardView, @NonNull ImageView close,
      @NonNull TextView dateGroupCreation, @NonNull ImageView iconsTime,
      @NonNull LinearLayout parent, @NonNull TextView privatePublic,
      @NonNull ImageView profilePhotoToolbar, @NonNull Toolbar toolBar,
      @NonNull TextView toolBarGroupName, @NonNull TextView toolBarTextview) {
    this.rootView = rootView;
    this.arrowBack = arrowBack;
    this.cardView = cardView;
    this.close = close;
    this.dateGroupCreation = dateGroupCreation;
    this.iconsTime = iconsTime;
    this.parent = parent;
    this.privatePublic = privatePublic;
    this.profilePhotoToolbar = profilePhotoToolbar;
    this.toolBar = toolBar;
    this.toolBarGroupName = toolBarGroupName;
    this.toolBarTextview = toolBarTextview;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGroupHistoryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGroupHistoryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_group_history, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGroupHistoryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.arrowBack;
      RelativeLayout arrowBack = ViewBindings.findChildViewById(rootView, id);
      if (arrowBack == null) {
        break missingId;
      }

      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.close;
      ImageView close = ViewBindings.findChildViewById(rootView, id);
      if (close == null) {
        break missingId;
      }

      id = R.id.date_group_creation;
      TextView dateGroupCreation = ViewBindings.findChildViewById(rootView, id);
      if (dateGroupCreation == null) {
        break missingId;
      }

      id = R.id.icons_time;
      ImageView iconsTime = ViewBindings.findChildViewById(rootView, id);
      if (iconsTime == null) {
        break missingId;
      }

      LinearLayout parent = (LinearLayout) rootView;

      id = R.id.private_public;
      TextView privatePublic = ViewBindings.findChildViewById(rootView, id);
      if (privatePublic == null) {
        break missingId;
      }

      id = R.id.profile_photo_toolbar;
      ImageView profilePhotoToolbar = ViewBindings.findChildViewById(rootView, id);
      if (profilePhotoToolbar == null) {
        break missingId;
      }

      id = R.id.toolBar;
      Toolbar toolBar = ViewBindings.findChildViewById(rootView, id);
      if (toolBar == null) {
        break missingId;
      }

      id = R.id.toolBar_group_name;
      TextView toolBarGroupName = ViewBindings.findChildViewById(rootView, id);
      if (toolBarGroupName == null) {
        break missingId;
      }

      id = R.id.toolBar_textview;
      TextView toolBarTextview = ViewBindings.findChildViewById(rootView, id);
      if (toolBarTextview == null) {
        break missingId;
      }

      return new ActivityGroupHistoryBinding((LinearLayout) rootView, arrowBack, cardView, close,
          dateGroupCreation, iconsTime, parent, privatePublic, profilePhotoToolbar, toolBar,
          toolBarGroupName, toolBarTextview);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
