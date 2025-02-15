package com.bekambimouen.miiokychallenge.profile.adapter;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.CommentTextDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.RecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedRecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopCircleImageDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopRecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedSingleTopVideoUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedUserProfileDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.SharedVideoUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.UserProfileDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.VideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.VideoUneDisplay;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewPostBattleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ViewPostBattleAdapter";

    // vars
    public static final int RECYCLER_ITEM = 1;
    public static final int IMAGE_UNE = 2;
    public static final int IMAGE_UNE_BIG_IMAGE = 3;
    public static final int VIDEO_UNE = 4;
    public static final int VIDEO_UNE_BIG_IMAGE = 5;
    public static final int IMAGE_DOUBLE = 6;
    public static final int VIDEO_DOUBLE = 7;
    public static final int REPONSE_IMG_DOUBLE = 8;
    public static final int REPONSE_VID_DOUBLE = 9;
    // shared
    public static final int USER_PROFILE_SHARE = 10;
    public static final int RECYCLER_ITEM_SHARED = 11;
    public static final int IMAGE_UNE_SHARED = 12;
    public static final int IMAGE_UNE_SHARED_BIG_IMG = 13;
    public static final int VIDEO_UNE_SHARED = 14;
    public static final int VIDEO_UNE_SHARED_BIG_IMG = 15;
    public static final int IMAGE_DOUBLE_SHARED = 16;
    public static final int VIDEO_DOUBLE_SHARED = 17;
    public static final int REPONSE_IMG_DOUBLE_SHARED = 18;
    public static final int REPONSE_VID_DOUBLE_SHARED = 19;

    public static final int USER_PROFILE = 20;
    public static final int COMMENT_TEXT = 21;

    public static final int GROUP_SINGLE_TOP_CIRCLE_IMAGE = 22;
    public static final int GROUP_SINGLE_TOP_IMAGE_DOUBLE = 23;
    public static final int GROUP_SINGLE_TOP_IMAGE_UNE = 24;
    public static final int GROUP_SINGLE_TOP_RECYCLER = 25;
    public static final int GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE = 26;
    public static final int GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE = 27;
    public static final int GROUP_SINGLE_TOP_VIDEO_DOUBLE = 28;
    public static final int GROUP_SINGLE_TOP_VIDEO_UNE = 29;

    private final AppCompatActivity context;
    private final ArrayList<BattleModel> list;
    private final ProgressBar progressBar;
    private final String user_id;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private CustomShareProgressView progresDialog;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.show();
    }

    private final BottomSheetMenuOneFile bottomSheet;

    public ViewPostBattleAdapter(AppCompatActivity context, ArrayList<BattleModel> list,
                                 ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        bottomSheet = new BottomSheetMenuOneFile(context);
        this.user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == USER_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_user_profile, parent, false);
            return new UserProfileDisplay(context, relLayout_view_overlay, view);

        } else if (viewType == COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_comment_text, parent, false);
            return new CommentTextDisplay(context, null, null, relLayout_view_overlay, view);

        } else if (viewType == RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_recycler_item, parent, false);
            return new RecyclerItemDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imageune_item, parent, false);
            return new ImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_BIG_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imageune_big_img, parent, false);
            return new ImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videoune_item, parent, false);
            return new VideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_BIG_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videoune_big_img, parent, false);
            return new VideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imagedouble_item, parent, false);
            return new ImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videodouble_item, parent, false);
            return new VideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_reponse_imagedouble, parent, false);
            return new ResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_reponse_videodouble, parent, false);
            return new ResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        }

        else if (viewType == USER_PROFILE_SHARE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_user_profile, parent, false);
            return new SharedUserProfileDisplay(context, relLayout_view_overlay, view);

        } else if (viewType == RECYCLER_ITEM_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_recycler, parent, false);
            return new SharedRecyclerItemDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune, parent, false);
            return new SharedImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune_big_img, parent, false);
            return new SharedImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune, parent, false);
            return new SharedVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune_big_img, parent, false);
            return new SharedVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imagedouble, parent, false);
            return new SharedImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videodouble_item, parent, false);
            return new SharedVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_imagedouble, parent, false);
            return new SharedResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_videodouble, parent, false);
            return new SharedResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        }  else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_circle_image, parent, false);
            return new SharedSingleTopCircleImageDisplay(context, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imagedouble, parent, false);
            return new SharedSingleTopImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune, parent, false);
            return new SharedSingleTopImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_recycler, parent, false);
            return new SharedSingleTopRecyclerItemDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_imagedouble, parent, false);
            return new SharedSingleTopResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_videodouble, parent, false);
            return new SharedSingleTopResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videodouble_item, parent, false);
            return new SharedSingleTopVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune, parent, false);
            return new SharedSingleTopVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = list.get(position);

        try {
            PreloadMainFragment.getPreloadImages(context, list.get(position+1));
            PreloadMainFragment.getPreloadImages(context, list.get(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);
        if (itemViewType == USER_PROFILE) {
            UserProfileDisplay userProfileDisplay = (UserProfileDisplay) holder;
            userProfileDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                userProfileDisplay.relLayout_go_user_profile.setEnabled(false);
            }
            // show views
            /*userProfileDisplay.views.setVisibility(View.VISIBLE);
            userProfileDisplay.views.setText(Html.fromHtml(context.getString(R.string.views)+": "+model.getViews()));*/

            // delete post
            deletePost(model, model.getPhoto_id(), userProfileDisplay.menu_item, userProfileDisplay);

        } else if (itemViewType == COMMENT_TEXT) {
            CommentTextDisplay commentTextDisplay = (CommentTextDisplay) holder;
            commentTextDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                commentTextDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id(), commentTextDisplay.menu_item, commentTextDisplay);

        } else if (itemViewType == RECYCLER_ITEM) {
            RecyclerItemDisplay recyclerItem = (RecyclerItemDisplay) holder;
            recyclerItem.init(model);

            if (model.getUser_id().equals(user_id)) {
                recyclerItem.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhotoi_id(), recyclerItem.menu_item, recyclerItem);

        } else if (itemViewType == IMAGE_UNE) {
            ImageUneDisplay imageUne = (ImageUneDisplay) holder;
            imageUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUne.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id(), imageUne.menu_item, imageUne);

        } else if (itemViewType == IMAGE_UNE_BIG_IMAGE) {
            ImageUneDisplay imageUne = (ImageUneDisplay) holder;
            imageUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUne.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id(), imageUne.menu_item, imageUne);

        } else if (itemViewType == VIDEO_UNE) {
            VideoUneDisplay videoUne = (VideoUneDisplay) holder;
            videoUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUne.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id(), videoUne.menu_item, videoUne);

        } else if (itemViewType == VIDEO_UNE_BIG_IMAGE) {
            VideoUneDisplay videoUne = (VideoUneDisplay) holder;
            videoUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUne.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id(), videoUne.menu_item, videoUne);

        } else if (itemViewType == IMAGE_DOUBLE){
            ImageDoubleDisplay imageDouble = (ImageDoubleDisplay) holder;
            imageDouble.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageDouble.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id_un(), imageDouble.menu_item, imageDouble);

        } else if (itemViewType == VIDEO_DOUBLE) {
            VideoDoubleDisplay videoDouble = (VideoDoubleDisplay) holder;
            videoDouble.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoDouble.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePost(model, model.getPhoto_id_un(), videoDouble.menu_item, videoDouble);

        } else if (itemViewType == REPONSE_IMG_DOUBLE) {
            ResponseImageDoubleDisplay responseImageDouble = (ResponseImageDoubleDisplay) holder;
            responseImageDouble.init(model);

            if (model.getReponse_user_id().equals(user_id)) {
                responseImageDouble.relLayout_go_user_profile_un.setEnabled(false);
            }

            if (model.getInvite_userID().equals(user_id)) {
                responseImageDouble.relLayout_go_user_profile_deux.setEnabled(false);
            }

            if (model.isEveryone_can_answer_this_challenge())
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, null, model.getInvite_url(),
                    true, false, false, false);
            responseImageDouble.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(responseImageDouble.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

            responseImageDouble.menu_item.setVisibility(View.VISIBLE);

            // delete post
            deletePost(model, model.getReponse_photoID(), responseImageDouble.menu_item, responseImageDouble);

        } else if (itemViewType == REPONSE_VID_DOUBLE) {
            ResponseVideoDoubleDisplay responseVideoDouble = (ResponseVideoDoubleDisplay) holder;
            responseVideoDouble.init(model);

            if (model.getReponse_user_id().equals(user_id)) {
                responseVideoDouble.relLayout_go_user_profile_un.setEnabled(false);
            }

            if (model.getInvite_userID().equals(user_id)) {
                responseVideoDouble.relLayout_go_user_profile_deux.setEnabled(false);
            }

            if (model.isEveryone_can_answer_this_challenge())
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, "video", model.getThumbnail_invite(),
                    false, true, false, false);
            responseVideoDouble.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(responseVideoDouble.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

            responseVideoDouble.menu_item.setVisibility(View.VISIBLE);

            // delete post
            deletePost(model, model.getReponse_photoID(), responseVideoDouble.menu_item, responseVideoDouble);

        } // share
        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileDisplay userProfileDisplay = (SharedUserProfileDisplay) holder;
            userProfileDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                userProfileDisplay.sharing_relLayout_username.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                userProfileDisplay.relLayout_username_shared.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), userProfileDisplay.menu_item, userProfileDisplay);

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemDisplay recyclerItemShared = (SharedRecyclerItemDisplay) holder;
            recyclerItemShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                recyclerItemShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                recyclerItemShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), recyclerItemShared.menu_item, recyclerItemShared);

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneDisplay imageUneShared = (SharedImageUneDisplay) holder;
            imageUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUneShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                imageUneShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        } else if (itemViewType == IMAGE_UNE_SHARED_BIG_IMG) {
            SharedImageUneDisplay imageUneShared = (SharedImageUneDisplay) holder;
            imageUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUneShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                imageUneShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        } else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneDisplay videoUneShared = (SharedVideoUneDisplay) holder;
            videoUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUneShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                videoUneShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == VIDEO_UNE_SHARED_BIG_IMG) {
            SharedVideoUneDisplay videoUneShared = (SharedVideoUneDisplay) holder;
            videoUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUneShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                videoUneShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleDisplay imageDoubleShared = (SharedImageDoubleDisplay) holder;
            imageDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageDoubleShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                imageDoubleShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), imageDoubleShared.menu_item, imageDoubleShared);

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleDisplay videoDoubleShared = (SharedVideoDoubleDisplay) holder;
            videoDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoDoubleShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getUser_id_sharing().equals(user_id)) {
                videoDoubleShared.relLayout_go_user_share_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), videoDoubleShared.menu_item, videoDoubleShared);

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleDisplay responseImageDoubleShared = (SharedResponseImageDoubleDisplay) holder;
            responseImageDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                responseImageDoubleShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getInvite_userID().equals(user_id)) {
                responseImageDoubleShared.relLayout_go_user_profile_un.setEnabled(false);
            }

            if (model.getReponse_user_id().equals(user_id)) {
                responseImageDoubleShared.relLayout_go_user_profile_deux.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), responseImageDoubleShared.menu_item, responseImageDoubleShared);

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleDisplay responseVideoDoubleShared = (SharedResponseVideoDoubleDisplay) holder;
            responseVideoDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                responseVideoDoubleShared.relLayout_go_user_profile.setEnabled(false);
            }

            if (model.getInvite_userID().equals(user_id)) {
                responseVideoDoubleShared.relLayout_go_user_profile_un.setEnabled(false);
            }

            if (model.getReponse_user_id().equals(user_id)) {
                responseVideoDoubleShared.relLayout_go_user_profile_deux.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), responseVideoDoubleShared.menu_item, responseVideoDoubleShared);

        } else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageDisplay singleTopCircleImageDisplay = (SharedSingleTopCircleImageDisplay) holder;
            singleTopCircleImageDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopCircleImageDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopCircleImageDisplay.menu_item, singleTopCircleImageDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleDisplay singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleDisplay) holder;
            singleTopImageDoubleDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopImageDoubleDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopImageDoubleDisplay.menu_item, singleTopImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneDisplay singleTopImageUneDisplay = (SharedSingleTopImageUneDisplay) holder;
            singleTopImageUneDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopImageUneDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemDisplay singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemDisplay) holder;
            singleTopRecyclerItemDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopRecyclerItemDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopRecyclerItemDisplay.menu_item, singleTopRecyclerItemDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleDisplay singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleDisplay) holder;
            singleTopResponseImageDoubleDisplay.init(model);

            if (model.getInvite_userID().equals(user_id)) {
                singleTopResponseImageDoubleDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopResponseImageDoubleDisplay.menu_item, singleTopResponseImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleDisplay singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleDisplay) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

            if (model.getInvite_userID().equals(user_id)) {
                singleTopResponseVideoDoubleDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopResponseVideoDoubleDisplay.menu_item, singleTopResponseVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleDisplay singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleDisplay) holder;
            singleTopVideoDoubleDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopVideoDoubleDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopVideoDoubleDisplay.menu_item, singleTopVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneDisplay singleTopVideoUneDisplay = (SharedSingleTopVideoUneDisplay) holder;
            singleTopVideoUneDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopVideoUneDisplay.relLayout_go_user_profile.setEnabled(false);
            }

            // delete post
            deletePostShare(model, model.getPhoto_id(), singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay);
        }

        // hide progressbar
        progressBar.setVisibility(View.GONE);
    }

    // delete post from group
    private void deletePost(BattleModel model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            if (model.getUser_id().equals(user_id)) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, menu_item);
                //inflating menu from xml resource
                popup.inflate(R.menu.more_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        // show dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                        TextView negativeButton = v.findViewById(R.id.tv_no);
                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                        builder.setView(v);

                        Dialog d = builder.create();
                        d.show();

                        negativeButton.setText(context.getString(R.string.no));
                        positiveButton.setText(context.getString(R.string.delete));

                        dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)+" ?"));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                        negativeButton.setOnClickListener(view1 -> d.dismiss());
                        positiveButton.setOnClickListener(view2 -> {
                            d.dismiss();
                            // internet control
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                            if (!isConnected) {
                                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

                            } else {
                                showLoading();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("suppressed", true);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_battle_media))
                                        .child(photo_id)
                                        .updateChildren(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_battle))
                                        .child(model.getUser_id())
                                        .child(photo_id)
                                        .updateChildren(map).addOnSuccessListener(unused1 -> {
                                            removeAt(holder.getLayoutPosition());
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });
                            }
                        });
                    }
                    return false;
                });
                //displaying the popup
                popup.show();

            } else {
                if (bottomSheet.isAdded())
                    return;

                Bundle args = new Bundle();
                args.putParcelable("battle_model", model);
                args.putString("miioky", "miioky");
                bottomSheet.setArguments(args);
                bottomSheet.show(context.getSupportFragmentManager(),
                        "TAG");

            }
        });
    }

    // delete post from group share
    private void deletePostShare(BattleModel model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            if (model.getUser_id().equals(user_id)) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, menu_item);
                //inflating menu from xml resource
                popup.inflate(R.menu.more_delete);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_delete) {
                        // show dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                        TextView dialog_title = v.findViewById(R.id.dialog_title);
                        TextView dialog_text = v.findViewById(R.id.dialog_text);
                        TextView negativeButton = v.findViewById(R.id.tv_no);
                        TextView positiveButton = v.findViewById(R.id.tv_yes);
                        builder.setView(v);

                        Dialog d = builder.create();
                        d.show();

                        negativeButton.setText(context.getString(R.string.no));
                        positiveButton.setText(context.getString(R.string.delete));

                        dialog_title.setText(Html.fromHtml(context.getString(R.string.delete)+" ?"));
                        dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                        negativeButton.setOnClickListener(view1 -> d.dismiss());
                        positiveButton.setOnClickListener(view2 -> {
                            d.dismiss();
                            // internet control
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                            if (!isConnected) {
                                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

                            } else {
                                showLoading();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("suppressed", true);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_battle_media_share))
                                        .child(photo_id)
                                        .updateChildren(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_battle))
                                        .child(model.getUser_id())
                                        .child(photo_id)
                                        .updateChildren(map).addOnSuccessListener(unused1 -> {
                                            removeAt(holder.getLayoutPosition());
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });
                            }
                        });
                    }
                    return false;
                });
                //displaying the popup
                popup.show();

            } else {
                if (bottomSheet.isAdded())
                    return;

                Bundle args = new Bundle();
                args.putParcelable("battle_model", model);
                args.putString("miioky", "miioky");
                bottomSheet.setArguments(args);
                bottomSheet.show(context.getSupportFragmentManager(),
                        "TAG");

            }
        });
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isUser_profile())
            return USER_PROFILE;
        else if (list.get(position).isComment_text())
            return COMMENT_TEXT;
        else if (list.get(position).isRecyclerItem())
            return RECYCLER_ITEM;
        else if (list.get(position).isImageUne() && !list.get(position).isBig_image())
            return IMAGE_UNE;
        else if (list.get(position).isImageUne() && list.get(position).isBig_image())
            return IMAGE_UNE_BIG_IMAGE;
        else if (list.get(position).isVideoUne() && !list.get(position).isBig_image())
            return VIDEO_UNE;
        else if (list.get(position).isVideoUne() && list.get(position).isBig_image())
            return VIDEO_UNE_BIG_IMAGE;
        else if (list.get(position).isImageDouble())
            return IMAGE_DOUBLE;
        else if (list.get(position).isVideoDouble())
            return VIDEO_DOUBLE;
        else if (list.get(position).isReponseImageDouble())
            return REPONSE_IMG_DOUBLE;
        else if (list.get(position).isReponseVideoDouble())
            return REPONSE_VID_DOUBLE;

        else if (list.get(position).isUser_profile_shared())
            return USER_PROFILE_SHARE;
        else if (list.get(position).isRecyclerItem_shared())
            return RECYCLER_ITEM_SHARED;
        else if (list.get(position).isImageUne_shared() && !list.get(position).isBig_image())
            return IMAGE_UNE_SHARED;
        else if (list.get(position).isImageUne_shared() && list.get(position).isBig_image())
            return IMAGE_UNE_SHARED_BIG_IMG;
        else if (list.get(position).isVideoUne_shared() && !list.get(position).isBig_image())
            return VIDEO_UNE_SHARED;
        else if (list.get(position).isVideoUne_shared() && list.get(position).isBig_image())
            return VIDEO_UNE_SHARED_BIG_IMG;
        else if (list.get(position).isImageDouble_shared())
            return IMAGE_DOUBLE_SHARED;
        else if (list.get(position).isVideoDouble_shared())
            return VIDEO_DOUBLE_SHARED;
        else if (list.get(position).isReponseImageDouble_shared())
            return REPONSE_IMG_DOUBLE_SHARED;
        else if (list.get(position).isReponseVideoDouble_shared())
            return REPONSE_VID_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_top_circle_image())
            return GROUP_SINGLE_TOP_CIRCLE_IMAGE;
        else  if (list.get(position).isGroup_share_single_top_image_double())
            return GROUP_SINGLE_TOP_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_single_top_image_une())
            return GROUP_SINGLE_TOP_IMAGE_UNE;
        else  if (list.get(position).isGroup_share_single_top_recycler())
            return GROUP_SINGLE_TOP_RECYCLER;
        else  if (list.get(position).isGroup_share_single_top_response_image_double())
            return GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_single_top_response_video_double())
            return GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE;
        else  if (list.get(position).isGroup_share_single_top_video_double())
            return GROUP_SINGLE_TOP_VIDEO_DOUBLE;
        else
            return GROUP_SINGLE_TOP_VIDEO_UNE;
    }
}
