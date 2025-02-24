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

public final class LayoutDialogDeleteOnePublicationBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView ivWarning;

  @NonNull
  public final TextView tvNo;

  @NonNull
  public final TextView tvYes;

  private LayoutDialogDeleteOnePublicationBinding(@NonNull RelativeLayout rootView,
      @NonNull ImageView ivWarning, @NonNull TextView tvNo, @NonNull TextView tvYes) {
    this.rootView = rootView;
    this.ivWarning = ivWarning;
    this.tvNo = tvNo;
    this.tvYes = tvYes;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutDialogDeleteOnePublicationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutDialogDeleteOnePublicationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_dialog_delete_one_publication, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutDialogDeleteOnePublicationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_warning;
      ImageView ivWarning = ViewBindings.findChildViewById(rootView, id);
      if (ivWarning == null) {
        break missingId;
      }

      id = R.id.tv_no;
      TextView tvNo = ViewBindings.findChildViewById(rootView, id);
      if (tvNo == null) {
        break missingId;
      }

      id = R.id.tv_yes;
      TextView tvYes = ViewBindings.findChildViewById(rootView, id);
      if (tvYes == null) {
        break missingId;
      }

      return new LayoutDialogDeleteOnePublicationBinding((RelativeLayout) rootView, ivWarning, tvNo,
          tvYes);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
