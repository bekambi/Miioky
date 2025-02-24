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
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class SnippetPubInstaDownloadVideoBinding implements ViewBinding {
  @NonNull
  private final View rootView;

  @NonNull
  public final MyEditText editCaptionDownloadVideo;

  @NonNull
  public final ImageView imageviewDownloadVideo;

  @NonNull
  public final ProgressBar progressbarDownloadVideo;

  @NonNull
  public final RelativeLayout relLayoutArrowBackDownloadVideo;

  @NonNull
  public final RelativeLayout relLayoutImgPlayUn;

  @NonNull
  public final RelativeLayout relLayoutPublish;

  @NonNull
  public final Toolbar toolbarDownloadVideo;

  @NonNull
  public final TextView tvPublish;

  private SnippetPubInstaDownloadVideoBinding(@NonNull View rootView,
      @NonNull MyEditText editCaptionDownloadVideo, @NonNull ImageView imageviewDownloadVideo,
      @NonNull ProgressBar progressbarDownloadVideo,
      @NonNull RelativeLayout relLayoutArrowBackDownloadVideo,
      @NonNull RelativeLayout relLayoutImgPlayUn, @NonNull RelativeLayout relLayoutPublish,
      @NonNull Toolbar toolbarDownloadVideo, @NonNull TextView tvPublish) {
    this.rootView = rootView;
    this.editCaptionDownloadVideo = editCaptionDownloadVideo;
    this.imageviewDownloadVideo = imageviewDownloadVideo;
    this.progressbarDownloadVideo = progressbarDownloadVideo;
    this.relLayoutArrowBackDownloadVideo = relLayoutArrowBackDownloadVideo;
    this.relLayoutImgPlayUn = relLayoutImgPlayUn;
    this.relLayoutPublish = relLayoutPublish;
    this.toolbarDownloadVideo = toolbarDownloadVideo;
    this.tvPublish = tvPublish;
  }

  @Override
  @NonNull
  public View getRoot() {
    return rootView;
  }

  @NonNull
  public static SnippetPubInstaDownloadVideoBinding inflate(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    if (parent == null) {
      throw new NullPointerException("parent");
    }
    inflater.inflate(R.layout.snippet_pub_insta_download_video, parent);
    return bind(parent);
  }

  @NonNull
  public static SnippetPubInstaDownloadVideoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.edit_caption_download_video;
      MyEditText editCaptionDownloadVideo = ViewBindings.findChildViewById(rootView, id);
      if (editCaptionDownloadVideo == null) {
        break missingId;
      }

      id = R.id.imageview_download_video;
      ImageView imageviewDownloadVideo = ViewBindings.findChildViewById(rootView, id);
      if (imageviewDownloadVideo == null) {
        break missingId;
      }

      id = R.id.progressbar_download_video;
      ProgressBar progressbarDownloadVideo = ViewBindings.findChildViewById(rootView, id);
      if (progressbarDownloadVideo == null) {
        break missingId;
      }

      id = R.id.relLayout_arrowBack_download_video;
      RelativeLayout relLayoutArrowBackDownloadVideo = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutArrowBackDownloadVideo == null) {
        break missingId;
      }

      id = R.id.relLayout_img_play_un;
      RelativeLayout relLayoutImgPlayUn = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutImgPlayUn == null) {
        break missingId;
      }

      id = R.id.relLayout_publish;
      RelativeLayout relLayoutPublish = ViewBindings.findChildViewById(rootView, id);
      if (relLayoutPublish == null) {
        break missingId;
      }

      id = R.id.toolbar_download_video;
      Toolbar toolbarDownloadVideo = ViewBindings.findChildViewById(rootView, id);
      if (toolbarDownloadVideo == null) {
        break missingId;
      }

      id = R.id.tv_publish;
      TextView tvPublish = ViewBindings.findChildViewById(rootView, id);
      if (tvPublish == null) {
        break missingId;
      }

      return new SnippetPubInstaDownloadVideoBinding(rootView, editCaptionDownloadVideo,
          imageviewDownloadVideo, progressbarDownloadVideo, relLayoutArrowBackDownloadVideo,
          relLayoutImgPlayUn, relLayoutPublish, toolbarDownloadVideo, tvPublish);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
