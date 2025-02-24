// Generated by view binder compiler. Do not edit!
package com.bekambimouen.miiokychallenge.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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

public final class LayoutVoiceRightMsgViewBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final RelativeLayout collectorLinearLayout;

  @NonNull
  public final ImageView imgPlay;

  @NonNull
  public final LinearLayout paddedLinearLayout;

  @NonNull
  public final CircleImageView profilPhoto;

  @NonNull
  public final RelativeLayout relLayout;

  @NonNull
  public final SeekBar seekBar;

  @NonNull
  public final TextView totalTxtTime;

  @NonNull
  public final TextView txtTime;

  private LayoutVoiceRightMsgViewBinding(@NonNull RelativeLayout rootView,
      @NonNull RelativeLayout collectorLinearLayout, @NonNull ImageView imgPlay,
      @NonNull LinearLayout paddedLinearLayout, @NonNull CircleImageView profilPhoto,
      @NonNull RelativeLayout relLayout, @NonNull SeekBar seekBar, @NonNull TextView totalTxtTime,
      @NonNull TextView txtTime) {
    this.rootView = rootView;
    this.collectorLinearLayout = collectorLinearLayout;
    this.imgPlay = imgPlay;
    this.paddedLinearLayout = paddedLinearLayout;
    this.profilPhoto = profilPhoto;
    this.relLayout = relLayout;
    this.seekBar = seekBar;
    this.totalTxtTime = totalTxtTime;
    this.txtTime = txtTime;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutVoiceRightMsgViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutVoiceRightMsgViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_voice_right_msg_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutVoiceRightMsgViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      RelativeLayout collectorLinearLayout = (RelativeLayout) rootView;

      id = R.id.imgPlay;
      ImageView imgPlay = ViewBindings.findChildViewById(rootView, id);
      if (imgPlay == null) {
        break missingId;
      }

      id = R.id.paddedLinearLayout;
      LinearLayout paddedLinearLayout = ViewBindings.findChildViewById(rootView, id);
      if (paddedLinearLayout == null) {
        break missingId;
      }

      id = R.id.profil_photo;
      CircleImageView profilPhoto = ViewBindings.findChildViewById(rootView, id);
      if (profilPhoto == null) {
        break missingId;
      }

      id = R.id.relLayout;
      RelativeLayout relLayout = ViewBindings.findChildViewById(rootView, id);
      if (relLayout == null) {
        break missingId;
      }

      id = R.id.seekBar;
      SeekBar seekBar = ViewBindings.findChildViewById(rootView, id);
      if (seekBar == null) {
        break missingId;
      }

      id = R.id.total_txtTime;
      TextView totalTxtTime = ViewBindings.findChildViewById(rootView, id);
      if (totalTxtTime == null) {
        break missingId;
      }

      id = R.id.txtTime;
      TextView txtTime = ViewBindings.findChildViewById(rootView, id);
      if (txtTime == null) {
        break missingId;
      }

      return new LayoutVoiceRightMsgViewBinding((RelativeLayout) rootView, collectorLinearLayout,
          imgPlay, paddedLinearLayout, profilPhoto, relLayout, seekBar, totalTxtTime, txtTime);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
