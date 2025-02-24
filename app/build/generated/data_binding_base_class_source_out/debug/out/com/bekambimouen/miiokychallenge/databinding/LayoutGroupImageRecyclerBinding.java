// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutGroupImageRecyclerBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView denominateur;

  @NonNull
  public final View diviseur;

  @NonNull
  public final TextView numerateur;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final RelativeLayout relLayout;

  private LayoutGroupImageRecyclerBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView denominateur, @NonNull View diviseur, @NonNull TextView numerateur,
      @NonNull RecyclerView recyclerView, @NonNull RelativeLayout relLayout) {
    this.rootView = rootView;
    this.denominateur = denominateur;
    this.diviseur = diviseur;
    this.numerateur = numerateur;
    this.recyclerView = recyclerView;
    this.relLayout = relLayout;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutGroupImageRecyclerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutGroupImageRecyclerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_group_image_recycler, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutGroupImageRecyclerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.denominateur;
      TextView denominateur = ViewBindings.findChildViewById(rootView, id);
      if (denominateur == null) {
        break missingId;
      }

      id = R.id.diviseur;
      View diviseur = ViewBindings.findChildViewById(rootView, id);
      if (diviseur == null) {
        break missingId;
      }

      id = R.id.numerateur;
      TextView numerateur = ViewBindings.findChildViewById(rootView, id);
      if (numerateur == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.relLayout;
      RelativeLayout relLayout = ViewBindings.findChildViewById(rootView, id);
      if (relLayout == null) {
        break missingId;
      }

      return new LayoutGroupImageRecyclerBinding((RelativeLayout) rootView, denominateur, diviseur,
          numerateur, recyclerView, relLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
