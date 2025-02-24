// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TouchDetectableScrollView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGroupCreateNewGroupBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView about;

  @NonNull
  public final RelativeLayout arrowBack;

  @NonNull
  public final CardView blackTheme;

  @NonNull
  public final CardView blueTheme;

  @NonNull
  public final CardView brownTheme;

  @NonNull
  public final ImageView cameraId;

  @NonNull
  public final TextView chooseConfidentiality;

  @NonNull
  public final ImageView close;

  @NonNull
  public final TextView confidentiality;

  @NonNull
  public final MyEditText editAboutGroup;

  @NonNull
  public final MyEditText editGroupName;

  @NonNull
  public final CardView greenTheme;

  @NonNull
  public final LinearLayout linLayout;

  @NonNull
  public final LinearLayout linLayoutAddPhoto;

  @NonNull
  public final LinearLayout linLayoutNberCharSequence;

  @NonNull
  public final LinearLayout linLayoutParent;

  @NonNull
  public final LinearLayout linLayoutUpdatePhoto;

  @NonNull
  public final TextView name;

  @NonNull
  public final TextView nberCharSequence;

  @NonNull
  public final TouchDetectableScrollView nestedScrollView;

  @NonNull
  public final RelativeLayout normalTheme;

  @NonNull
  public final RelativeLayout parent;

  @NonNull
  public final ImageView penStart;

  @NonNull
  public final CardView pinkTheme;

  @NonNull
  public final ProgressBar progressbar;

  @NonNull
  public final RelativeLayout relLayoutAbout;

  @NonNull
  public final RelativeLayout relLayoutChooseConfidentiality;

  @NonNull
  public final RelativeLayout relLayoutGroupName;

  @NonNull
  public final RelativeLayout relLayoutProfile;

  @NonNull
  public final RelativeLayout relLayoutPubProfilePhoto;

  @NonNull
  public final RelativeLayout relLayoutUpdate;

  @NonNull
  public final RelativeLayout relLayoutViewOverlay;

  @NonNull
  public final TextView title;

  @NonNull
  public final Toolbar toolBar;

  @NonNull
  public final TextView toolbarTitle;

  private ActivityGroupCreateNewGroupBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView about, @NonNull RelativeLayout arrowBack, @NonNull CardView blackTheme,
      @NonNull CardView blueTheme, @NonNull CardView brownTheme, @NonNull ImageView cameraId,
      @NonNull TextView chooseConfidentiality, @NonNull ImageView close,
      @NonNull TextView confidentiality, @NonNull MyEditText editAboutGroup,
      @NonNull MyEditText editGroupName, @NonNull CardView greenTheme,
      @NonNull LinearLayout linLayout, @NonNull LinearLayout linLayoutAddPhoto,
      @NonNull LinearLayout linLayoutNberCharSequence, @NonNull LinearLayout linLayoutParent,
      @NonNull LinearLayout linLayoutUpdatePhoto, @NonNull TextView name,
      @NonNull TextView nberCharSequence, @NonNull TouchDetectableScrollView nestedScrollView,
      @NonNull RelativeLayout normalTheme, @NonNull RelativeLayout parent,
      @NonNull ImageView penStart, @NonNull CardView pinkTheme, @NonNull ProgressBar progressbar,
      @NonNull RelativeLayout relLayoutAbout,
      @NonNull RelativeLayout relLayoutChooseConfidentiality,
      @NonNull RelativeLayout relLayoutGroupName, @NonNull RelativeLayout relLayoutProfile,
      @NonNull RelativeLayout relLayoutPubProfilePhoto, @NonNull RelativeLayout relLayoutUpdate,
      @NonNull RelativeLayout relLayoutViewOverlay, @NonNull TextView title,
      @NonNull Toolbar toolBar, @NonNull TextView toolbarTitle) {
    this.rootView = rootView;
    this.about = about;
    this.arrowBack = arrowBack;
    this.blackTheme = blackTheme;
    this.blueTheme = blueTheme;
    this.brownTheme = brownTheme;
    this.cameraId = cameraId;
    this.chooseConfidentiality = chooseConfidentiality;
    this.close = close;
    this.confidentiality = confidentiality;
    this.editAboutGroup = editAboutGroup;
    this.editGroupName = editGroupName;
    this.greenTheme = greenTheme;
    this.linLayout = linLayout;
    this.linLayoutAddPhoto = linLayoutAddPhoto;
    this.linLayoutNberCharSequence = linLayoutNberCharSequence;
    this.linLayoutParent = linLayoutParent;
    this.linLayoutUpdatePhoto = linLayoutUpdatePhoto;
    this.name = name;
    this.nberCharSequence = nberCharSequence;
    this.nestedScrollView = nestedScrollView;
    this.normalTheme = normalTheme;
    this.parent = parent;
    this.penStart = penStart;
    this.pinkTheme = pinkTheme;
    this.progressbar = progressbar;
    this.relLayoutAbout = relLayoutAbout;
    this.relLayoutChooseConfidentiality = relLayoutChooseConfidentiality;
    this.relLayoutGroupName = relLayoutGroupName;
    this.relLayoutProfile = relLayoutProfile;
    this.relLayoutPubProfilePhoto = relLayoutPubProfilePhoto;
    this.relLayoutUpdate = relLayoutUpdate;
    this.relLayoutViewOverlay = relLayoutViewOverlay;
    this.title = title;
    this.toolBar = toolBar;
    this.toolbarTitle = toolbarTitle;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGroupCreateNewGroupBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGroupCreateNewGroupBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_group_create_new_group, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGroupCreateNewGroupBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.about;
      TextView about = ViewBindings.findChildViewById(rootView, id);
      if (about == null) {
        break missingId;
      }

      id = R.id.arrowBack;
      RelativeLayout arrowBack = ViewBindings.findChildViewById(rootView, id);
      if (arrowBack == null) {
        break missingId;
      }

      id = R.id.black_theme;
      CardView blackTheme = ViewBindings.findChildViewById(rootView, id);
      if (blackTheme == null) {
        break missingId;
      }

      id = R.id.blue_theme;
      CardView blueTheme = ViewBindings.findChildViewById(rootView, id);
      if (blueTheme == null) {
        break missingId;
      }

      id = R.id.brown_theme;
      CardView brownTheme = ViewBindings.findChildViewById(rootView, id);
      if (brownTheme == null) {
        break missingId;
      }

      id = R.id.camera_id;
      ImageView cameraId = ViewBindings.findChildViewById(rootView, id);
      if (cameraId == null) {
        break missingId;
      }

      id = R.id.choose_confidentiality;
      TextView chooseConfidentiality = ViewBindings.findChildViewById(rootView, id);
      if (chooseConfidentiality == null) {
        break missingId;
      }

      id = R.id.close;
      ImageView close = ViewBindings.findChildViewById(rootView, id);
      if (close == null) {
        break missingId;
      }

      id = R.id.confidentiality;
      TextView confidentiality = ViewBindings.findChildViewById(rootView, id);
      if (confidentiality == null) {
        break missingId;
      }

      id = R.id.edit_about_group;
      MyEditText editAboutGroup = ViewBindings.findChildViewById(rootView, id);
      if (editAboutGroup == null) {
        break missingId;
      }

      id = R.id.edit_group_name;
      MyEditText editGroupName = ViewBindings.findChildViewById(rootView, id);
      if (editGroupName == null) {
        break missingId;
      }

      id = R.id.green_theme;
      CardView greenTheme = ViewBindings.findChildViewById(rootView, id);
      if (greenTheme == null) {
        break missingId;
      }

      id = R.id.linLayout;
      LinearLayout linLayout = ViewBindings.findChildViewById(rootView, id);
      if (linLayout == null) {
        break missingId;
      }

      id = R.id.linLayout_add_photo;
      LinearLayout linLayoutAddPhoto = ViewBindings.findChildViewById(rootView, id);
      if (linLayoutAddPhoto == null) {
        break missingId;
      }

      id = R.id.linLayout_nber_char_sequence;
      LinearLayout linLayoutNberCharSequence = ViewBindings.findChildViewById(rootView, id);
      if (linLayoutNberCharSequence == null) {
        break missingId;
      }

      id = R.id.linLayout_parent;
      LinearLayout linLayoutParent = ViewBindings.findChildViewById(rootView, id);
      if (linLayoutParent == null) {
        break missingId;
      }

      id = R.id.linLayout_update_photo;
      LinearLayout linLayoutUpdatePhoto = ViewBindings.findChildViewById(rootView, id);
      if (linLayoutUpdatePhoto == null) {
        break missingId;
      }

      id = R.id.name;
      TextView name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      id = R.id.nber_char_sequence;
      TextView nberCharSequence = ViewBindings.findChildViewById(rootView, id);
      if (nberCharSequence == null) {
        break missingId;
      }

      id = R.id.nestedScrollView;
      TouchDetectableScrollView nestedScrollView = ViewBindings.findChildViewById(rootView, id);
      if (nestedScrollView == null) {
        break missingId;
      }

      id = R.id.normal_theme;
      RelativeLayout normalTheme = ViewBindings.findChildViewById(rootView, id);
      if (normalTheme == null) {
        break missingId;
      }

      RelativeLayout parent = (RelativeLayout) rootView;

      id = R.id.penStart;
      ImageView penStart = ViewBindings.findChildViewById(rootView, id);
      if (penStart == null) {
        break missingId;
      }

      id = R.id.pink_theme;
      CardView pinkTheme = ViewBindings.findChildViewById(rootView, id);
      if (pinkTheme == null) {
        break missingId;
      }

      id = R.id.progressbar;
      ProgressBar progressbar = ViewBindings.findChildViewById(rootView, id);
      if (progressbar == null) {
        break missingId;
      }

      id = R.id.relLayout_about;
      RelativeLayout relLayoutAbout = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutAbout == null) {
        break missingId;
      }

      id = R.id.relLayout_choose_confidentiality;
      RelativeLayout relLayoutChooseConfidentiality = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutChooseConfidentiality == null) {
        break missingId;
      }

      id = R.id.relLayout_group_name;
      RelativeLayout relLayoutGroupName = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutGroupName == null) {
        break missingId;
      }

      id = R.id.relLayout_profile;
      RelativeLayout relLayoutProfile = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutProfile == null) {
        break missingId;
      }

      id = R.id.relLayout_pub_profile_photo;
      RelativeLayout relLayoutPubProfilePhoto = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutPubProfilePhoto == null) {
        break missingId;
      }

      id = R.id.relLayout_update;
      RelativeLayout relLayoutUpdate = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutUpdate == null) {
        break missingId;
      }

      id = R.id.relLayout_view_overlay;
      RelativeLayout relLayoutViewOverlay = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutViewOverlay == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      id = R.id.toolBar;
      Toolbar toolBar = ViewBindings.findChildViewById(rootView, id);
      if (toolBar == null) {
        break missingId;
      }

      id = R.id.toolbar_title;
      TextView toolbarTitle = ViewBindings.findChildViewById(rootView, id);
      if (toolbarTitle == null) {
        break missingId;
      }

      return new ActivityGroupCreateNewGroupBinding((RelativeLayout) rootView, about, arrowBack,
          blackTheme, blueTheme, brownTheme, cameraId, chooseConfidentiality, close,
          confidentiality, editAboutGroup, editGroupName, greenTheme, linLayout, linLayoutAddPhoto,
          linLayoutNberCharSequence, linLayoutParent, linLayoutUpdatePhoto, name, nberCharSequence,
          nestedScrollView, normalTheme, parent, penStart, pinkTheme, progressbar, relLayoutAbout,
          relLayoutChooseConfidentiality, relLayoutGroupName, relLayoutProfile,
          relLayoutPubProfilePhoto, relLayoutUpdate, relLayoutViewOverlay, title, toolBar,
          toolbarTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
