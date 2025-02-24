// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutBottomsheetSignalerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView tvFake;

  @NonNull
  public final TextView tvHatSpeech;

  @NonNull
  public final TextView tvOthers;

  @NonNull
  public final TextView tvSexual;

  @NonNull
  public final TextView tvViolent;

  private LayoutBottomsheetSignalerBinding(@NonNull LinearLayout rootView, @NonNull TextView tvFake,
      @NonNull TextView tvHatSpeech, @NonNull TextView tvOthers, @NonNull TextView tvSexual,
      @NonNull TextView tvViolent) {
    this.rootView = rootView;
    this.tvFake = tvFake;
    this.tvHatSpeech = tvHatSpeech;
    this.tvOthers = tvOthers;
    this.tvSexual = tvSexual;
    this.tvViolent = tvViolent;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutBottomsheetSignalerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutBottomsheetSignalerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_bottomsheet_signaler, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutBottomsheetSignalerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.tv_fake;
      TextView tvFake = ViewBindings.findChildViewById(rootView, id);
      if (tvFake == null) {
        break missingId;
      }

      id = R.id.tv_hat_speech;
      TextView tvHatSpeech = ViewBindings.findChildViewById(rootView, id);
      if (tvHatSpeech == null) {
        break missingId;
      }

      id = R.id.tv_others;
      TextView tvOthers = ViewBindings.findChildViewById(rootView, id);
      if (tvOthers == null) {
        break missingId;
      }

      id = R.id.tv_sexual;
      TextView tvSexual = ViewBindings.findChildViewById(rootView, id);
      if (tvSexual == null) {
        break missingId;
      }

      id = R.id.tv_violent;
      TextView tvViolent = ViewBindings.findChildViewById(rootView, id);
      if (tvViolent == null) {
        break missingId;
      }

      return new LayoutBottomsheetSignalerBinding((LinearLayout) rootView, tvFake, tvHatSpeech,
          tvOthers, tvSexual, tvViolent);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
