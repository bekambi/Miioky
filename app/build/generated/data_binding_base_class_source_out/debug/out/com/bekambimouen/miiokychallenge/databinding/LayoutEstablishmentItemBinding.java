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
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutEstablishmentItemBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView establishmentName;

  @NonNull
  public final ImageView imageview;

  @NonNull
  public final ImageView menuItem;

  private LayoutEstablishmentItemBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView establishmentName, @NonNull ImageView imageview,
      @NonNull ImageView menuItem) {
    this.rootView = rootView;
    this.establishmentName = establishmentName;
    this.imageview = imageview;
    this.menuItem = menuItem;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutEstablishmentItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutEstablishmentItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_establishment_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutEstablishmentItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.establishment_name;
      TextView establishmentName = ViewBindings.findChildViewById(rootView, id);
      if (establishmentName == null) {
        break missingId;
      }

      id = R.id.imageview;
      ImageView imageview = ViewBindings.findChildViewById(rootView, id);
      if (imageview == null) {
        break missingId;
      }

      id = R.id.menu_item;
      ImageView menuItem = ViewBindings.findChildViewById(rootView, id);
      if (menuItem == null) {
        break missingId;
      }

      return new LayoutEstablishmentItemBinding((RelativeLayout) rootView, establishmentName,
          imageview, menuItem);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
