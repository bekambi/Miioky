// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutGroupShareInPubItemBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final TextView groupName;

  @NonNull
  public final ImageView groupProfilePhoto;

  private LayoutGroupShareInPubItemBinding(@NonNull RelativeLayout rootView,
      @NonNull CardView cardView, @NonNull TextView groupName,
      @NonNull ImageView groupProfilePhoto) {
    this.rootView = rootView;
    this.cardView = cardView;
    this.groupName = groupName;
    this.groupProfilePhoto = groupProfilePhoto;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutGroupShareInPubItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutGroupShareInPubItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_group_share_in_pub_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutGroupShareInPubItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.group_name;
      TextView groupName = ViewBindings.findChildViewById(rootView, id);
      if (groupName == null) {
        break missingId;
      }

      id = R.id.group_profile_photo;
      ImageView groupProfilePhoto = ViewBindings.findChildViewById(rootView, id);
      if (groupProfilePhoto == null) {
        break missingId;
      }

      return new LayoutGroupShareInPubItemBinding((RelativeLayout) rootView, cardView, groupName,
          groupProfilePhoto);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
