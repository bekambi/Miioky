// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import com.csguys.viewmoretextview.ViewMoreTextView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutGroupNonRescpectedRulesBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final CheckBox checkBox;

  @NonNull
  public final TextView number;

  @NonNull
  public final ViewMoreTextView ruleDetails;

  @NonNull
  public final TextView ruleTitle;

  private LayoutGroupNonRescpectedRulesBinding(@NonNull RelativeLayout rootView,
      @NonNull CheckBox checkBox, @NonNull TextView number, @NonNull ViewMoreTextView ruleDetails,
      @NonNull TextView ruleTitle) {
    this.rootView = rootView;
    this.checkBox = checkBox;
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
  public static LayoutGroupNonRescpectedRulesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutGroupNonRescpectedRulesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_group_non_rescpected_rules, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutGroupNonRescpectedRulesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.checkBox;
      CheckBox checkBox = ViewBindings.findChildViewById(rootView, id);
      if (checkBox == null) {
        break missingId;
      }

      id = R.id.number;
      TextView number = ViewBindings.findChildViewById(rootView, id);
      if (number == null) {
        break missingId;
      }

      id = R.id.rule_details;
      ViewMoreTextView ruleDetails = ViewBindings.findChildViewById(rootView, id);
      if (ruleDetails == null) {
        break missingId;
      }

      id = R.id.rule_title;
      TextView ruleTitle = ViewBindings.findChildViewById(rootView, id);
      if (ruleTitle == null) {
        break missingId;
      }

      return new LayoutGroupNonRescpectedRulesBinding((RelativeLayout) rootView, checkBox, number,
          ruleDetails, ruleTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
