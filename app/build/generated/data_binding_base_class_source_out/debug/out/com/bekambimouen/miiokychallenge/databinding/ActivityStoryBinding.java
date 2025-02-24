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
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public final class ActivityStoryBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView ActivityStoryImage;

  @NonNull
  public final RelativeLayout ActivityStoryRSeen;

  @NonNull
  public final TextView ActivityStorySeenNumber;

  @NonNull
  public final TextView ActivityStoryStoryUsername;

  @NonNull
  public final ImageView addStory;

  @NonNull
  public final TextView caption;

  @NonNull
  public final ImageView image;

  @NonNull
  public final LinearLayout linearlayoutViews;

  @NonNull
  public final RelativeLayout menuItem;

  @NonNull
  public final ImageView more;

  @NonNull
  public final RelativeLayout parent;

  @NonNull
  public final CircleImageView profilePhoto;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final RelativeLayout relLayoutAddStory;

  @NonNull
  public final RelativeLayout relLayoutSpotlight;

  @NonNull
  public final View reverse;

  @NonNull
  public final View skip;

  @NonNull
  public final ImageView spotLight;

  @NonNull
  public final StoriesProgressView stories;

  @NonNull
  public final TextView tpsPublication;

  private ActivityStoryBinding(@NonNull RelativeLayout rootView,
      @NonNull ImageView ActivityStoryImage, @NonNull RelativeLayout ActivityStoryRSeen,
      @NonNull TextView ActivityStorySeenNumber, @NonNull TextView ActivityStoryStoryUsername,
      @NonNull ImageView addStory, @NonNull TextView caption, @NonNull ImageView image,
      @NonNull LinearLayout linearlayoutViews, @NonNull RelativeLayout menuItem,
      @NonNull ImageView more, @NonNull RelativeLayout parent,
      @NonNull CircleImageView profilePhoto, @NonNull ProgressBar progressBar,
      @NonNull RelativeLayout relLayoutAddStory, @NonNull RelativeLayout relLayoutSpotlight,
      @NonNull View reverse, @NonNull View skip, @NonNull ImageView spotLight,
      @NonNull StoriesProgressView stories, @NonNull TextView tpsPublication) {
    this.rootView = rootView;
    this.ActivityStoryImage = ActivityStoryImage;
    this.ActivityStoryRSeen = ActivityStoryRSeen;
    this.ActivityStorySeenNumber = ActivityStorySeenNumber;
    this.ActivityStoryStoryUsername = ActivityStoryStoryUsername;
    this.addStory = addStory;
    this.caption = caption;
    this.image = image;
    this.linearlayoutViews = linearlayoutViews;
    this.menuItem = menuItem;
    this.more = more;
    this.parent = parent;
    this.profilePhoto = profilePhoto;
    this.progressBar = progressBar;
    this.relLayoutAddStory = relLayoutAddStory;
    this.relLayoutSpotlight = relLayoutSpotlight;
    this.reverse = reverse;
    this.skip = skip;
    this.spotLight = spotLight;
    this.stories = stories;
    this.tpsPublication = tpsPublication;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityStoryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityStoryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_story, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityStoryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ActivityStory_image;
      ImageView ActivityStoryImage = ViewBindings.findChildViewById(rootView, id);
      if (ActivityStoryImage == null) {
        break missingId;
      }

      id = R.id.ActivityStory_r_seen;
      RelativeLayout ActivityStoryRSeen = ViewBindings.findChildViewById(rootView, id);
      if (ActivityStoryRSeen == null) {
        break missingId;
      }

      id = R.id.ActivityStory_seen_number;
      TextView ActivityStorySeenNumber = ViewBindings.findChildViewById(rootView, id);
      if (ActivityStorySeenNumber == null) {
        break missingId;
      }

      id = R.id.ActivityStory_story_username;
      TextView ActivityStoryStoryUsername = ViewBindings.findChildViewById(rootView, id);
      if (ActivityStoryStoryUsername == null) {
        break missingId;
      }

      id = R.id.add_story;
      ImageView addStory = ViewBindings.findChildViewById(rootView, id);
      if (addStory == null) {
        break missingId;
      }

      id = R.id.caption;
      TextView caption = ViewBindings.findChildViewById(rootView, id);
      if (caption == null) {
        break missingId;
      }

      id = R.id.image;
      ImageView image = ViewBindings.findChildViewById(rootView, id);
      if (image == null) {
        break missingId;
      }

      id = R.id.linearlayout_views;
      LinearLayout linearlayoutViews = ViewBindings.findChildViewById(rootView, id);
      if (linearlayoutViews == null) {
        break missingId;
      }

      id = R.id.menu_item;
      RelativeLayout menuItem = ViewBindings.findChildViewById(rootView, id);
      if (menuItem == null) {
        break missingId;
      }

      id = R.id.more;
      ImageView more = ViewBindings.findChildViewById(rootView, id);
      if (more == null) {
        break missingId;
      }

      RelativeLayout parent = (RelativeLayout) rootView;

      id = R.id.profile_photo;
      CircleImageView profilePhoto = ViewBindings.findChildViewById(rootView, id);
      if (profilePhoto == null) {
        break missingId;
      }

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.relLayout_add_story;
      RelativeLayout relLayoutAddStory = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutAddStory == null) {
        break missingId;
      }

      id = R.id.relLayout_spotlight;
      RelativeLayout relLayoutSpotlight = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutSpotlight == null) {
        break missingId;
      }

      id = R.id.reverse;
      View reverse = ViewBindings.findChildViewById(rootView, id);
      if (reverse == null) {
        break missingId;
      }

      id = R.id.skip;
      View skip = ViewBindings.findChildViewById(rootView, id);
      if (skip == null) {
        break missingId;
      }

      id = R.id.spotLight;
      ImageView spotLight = ViewBindings.findChildViewById(rootView, id);
      if (spotLight == null) {
        break missingId;
      }

      id = R.id.stories;
      StoriesProgressView stories = ViewBindings.findChildViewById(rootView, id);
      if (stories == null) {
        break missingId;
      }

      id = R.id.tps_publication;
      TextView tpsPublication = ViewBindings.findChildViewById(rootView, id);
      if (tpsPublication == null) {
        break missingId;
      }

      return new ActivityStoryBinding((RelativeLayout) rootView, ActivityStoryImage,
          ActivityStoryRSeen, ActivityStorySeenNumber, ActivityStoryStoryUsername, addStory,
          caption, image, linearlayoutViews, menuItem, more, parent, profilePhoto, progressBar,
          relLayoutAddStory, relLayoutSpotlight, reverse, skip, spotLight, stories, tpsPublication);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
