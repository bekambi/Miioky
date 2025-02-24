// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class LayoutGroupAddRulesBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final RelativeLayout delete;

  @NonNull
  public final TextView number;

  @NonNull
  public final TextView ruleDetails;

  @NonNull
  public final TextView ruleTitle;

  private LayoutGroupAddRulesBinding(@NonNull RelativeLayout rootView,
      @NonNull RelativeLayout delete, @NonNull TextView number, @NonNull TextView ruleDetails,
      @NonNull TextView ruleTitle) {
    this.rootView = rootView;
    this.delete = delete;
    this.number = number;
    this.ruleDetails = ruleDetails;
    this.ruleTitle = ruleTitle;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutGroupAddRulesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutGroupAddRulesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_group_add_rules, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutGroupAddRulesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.delete;
      RelativeLayout delete = ViewBindings.findChildViewById(rootView, id);
      if (delete == null) {
        break missingId;
      }

      id = R.id.number;
      TextView number = ViewBindings.findChildViewById(rootView, id);
      if (number == null) {
        break missingId;
      }

      id = R.id.rule_details;
      TextView ruleDetails = ViewBindings.findChildViewById(rootView, id);
      if (ruleDetails == null) {
        break missingId;
      }

      id = R.id.rule_title;
      TextView ruleTitle = ViewBindings.findChildViewById(rootView, id);
      if (ruleTitle == null) {
        break missingId;
      }

      return new LayoutGroupAddRulesBinding((RelativeLayout) rootView, delete, number, ruleDetails,
          ruleTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
