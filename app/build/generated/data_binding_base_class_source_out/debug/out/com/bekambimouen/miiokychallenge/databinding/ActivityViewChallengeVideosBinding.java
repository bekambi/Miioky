// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public final class ActivityViewChallengeVideosBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView category;

  @NonNull
  public final ImageView imgPlay;

  @NonNull
  public final ImageView imgViewfun;

  @NonNull
  public final RelativeLayout parent;

  @NonNull
  public final Toolbar profileToolBar;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final Container recyclerView;

  @NonNull
  public final RelativeLayout relLayout;

  @NonNull
  public final RelativeLayout relLayoutIconHome;

  @NonNull
  public final RelativeLayout relLayoutImgPlay;

  @NonNull
  public final RelativeLayout relLayoutImgPlayRed;

  @NonNull
  public final RelativeLayout relLayoutMenu;

  @NonNull
  public final RelativeLayout relLayoutViewOverlay;

  @NonNull
  public final View view;

  @NonNull
  public final View view2;

  private ActivityViewChallengeVideosBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView category, @NonNull ImageView imgPlay, @NonNull ImageView imgViewfun,
      @NonNull RelativeLayout parent, @NonNull Toolbar profileToolBar,
      @NonNull ProgressBar progressBar, @NonNull Container recyclerView,
      @NonNull RelativeLayout relLayout, @NonNull RelativeLayout relLayoutIconHome,
      @NonNull RelativeLayout relLayoutImgPlay, @NonNull RelativeLayout relLayoutImgPlayRed,
      @NonNull RelativeLayout relLayoutMenu, @NonNull RelativeLayout relLayoutViewOverlay,
      @NonNull View view, @NonNull View view2) {
    this.rootView = rootView;
    this.category = category;
    this.imgPlay = imgPlay;
    this.imgViewfun = imgViewfun;
    this.parent = parent;
    this.profileToolBar = profileToolBar;
    this.progressBar = progressBar;
    this.recyclerView = recyclerView;
    this.relLayout = relLayout;
    this.relLayoutIconHome = relLayoutIconHome;
    this.relLayoutImgPlay = relLayoutImgPlay;
    this.relLayoutImgPlayRed = relLayoutImgPlayRed;
    this.relLayoutMenu = relLayoutMenu;
    this.relLayoutViewOverlay = relLayoutViewOverlay;
    this.view = view;
    this.view2 = view2;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityViewChallengeVideosBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityViewChallengeVideosBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_view_challenge_videos, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityViewChallengeVideosBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.category;
      TextView category = ViewBindings.findChildViewById(rootView, id);
      if (category == null) {
        break missingId;
      }

      id = R.id.img_play;
      ImageView imgPlay = ViewBindings.findChildViewById(rootView, id);
      if (imgPlay == null) {
        break missingId;
      }

      id = R.id.img_viewfun;
      ImageView imgViewfun = ViewBindings.findChildViewById(rootView, id);
      if (imgViewfun == null) {
        break missingId;
      }

      RelativeLayout parent = (RelativeLayout) rootView;

      id = R.id.profileToolBar;
      Toolbar profileToolBar = ViewBindings.findChildViewById(rootView, id);
      if (profileToolBar == null) {
        break missingId;
      }

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      Container recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.relLayout;
      RelativeLayout relLayout = ViewBindings.findChildViewById(rootView, id);
      if (relLayout == null) {
        break missingId;
      }

      id = R.id.relLayout_icon_home;
      RelativeLayout relLayoutIconHome = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutIconHome == null) {
        break missingId;
      }

      id = R.id.relLayout_img_play;
      RelativeLayout relLayoutImgPlay = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutImgPlay == null) {
        break missingId;
      }

      id = R.id.relLayout_img_play_red;
      RelativeLayout relLayoutImgPlayRed = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutImgPlayRed == null) {
        break missingId;
      }

      id = R.id.relLayout_menu;
      RelativeLayout relLayoutMenu = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutMenu == null) {
        break missingId;
      }

      id = R.id.relLayout_view_overlay;
      RelativeLayout relLayoutViewOverlay = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutViewOverlay == null) {
        break missingId;
      }

      id = R.id.view;
      View view = ViewBindings.findChildViewById(rootView, id);
      if (view == null) {
        break missingId;
      }

      id = R.id.view_2;
      View view2 = ViewBindings.findChildViewById(rootView, id);
      if (view2 == null) {
        break missingId;
      }

      return new ActivityViewChallengeVideosBinding((RelativeLayout) rootView, category, imgPlay,
          imgViewfun, parent, profileToolBar, progressBar, recyclerView, relLayout,
          relLayoutIconHome, relLayoutImgPlay, relLayoutImgPlayRed, relLayoutMenu,
          relLayoutViewOverlay, view, view2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
