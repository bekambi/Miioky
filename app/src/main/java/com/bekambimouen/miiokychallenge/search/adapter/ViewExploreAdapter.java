package com.bekambimouen.miiokychallenge.search.adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
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
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewExploreAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "ViewExploreAdapter";

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
    private static final int GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG = 25;
    public static final int GROUP_SINGLE_TOP_RECYCLER = 26;
    public static final int GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE = 27;
    public static final int GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE = 28;
    public static final int GROUP_SINGLE_TOP_VIDEO_DOUBLE = 29;
    public static final int GROUP_SINGLE_TOP_VIDEO_UNE = 30;
    private static final int GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG = 31;

    private final AppCompatActivity context;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public ViewExploreAdapter(AppCompatActivity context, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_videodouble, parent, false);
            return new SharedResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        }
        else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
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

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune_big_img, parent, false);
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

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune, parent, false);
            return new SharedSingleTopVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune_big_img, parent, false);
            return new SharedSingleTopVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = getItem(position);

        try {
            PreloadMainFragment.getPreloadImages(context, getItem(position+1));
            PreloadMainFragment.getPreloadImages(context, getItem(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);
        if (itemViewType == USER_PROFILE) {
            UserProfileDisplay userProfileDisplay = (UserProfileDisplay) holder;
            userProfileDisplay.init(model);

            if (!model.getUser_id().equals(user_id)) {
                userProfileDisplay.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), userProfileDisplay.relLayout_follow, userProfileDisplay.menu_item, userProfileDisplay.delete_suggestion);

            } else
                userProfileDisplay.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            userProfileDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(userProfileDisplay, model.getUser_id()));

        } else if (itemViewType == COMMENT_TEXT) {
            CommentTextDisplay commentTextDisplay = (CommentTextDisplay) holder;
            commentTextDisplay.init(model);

            if (!model.getUser_id().equals(user_id)) {
                commentTextDisplay.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), commentTextDisplay.relLayout_follow, commentTextDisplay.menu_item, commentTextDisplay.delete_suggestion);

            } else
                commentTextDisplay.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            commentTextDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(commentTextDisplay, model.getUser_id()));

        } else if (itemViewType == RECYCLER_ITEM) {
            RecyclerItemDisplay recyclerItem = (RecyclerItemDisplay) holder;
            recyclerItem.init(model);

            if (!model.getUser_id().equals(user_id)) {
                recyclerItem.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), recyclerItem.relLayout_follow, recyclerItem.menu_item, recyclerItem.delete_suggestion);

            } else
                recyclerItem.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            recyclerItem.delete_suggestion.setOnClickListener(view -> deleteSuggestion(recyclerItem, model.getUser_id()));

        } else if (itemViewType == IMAGE_UNE) {
            ImageUneDisplay imageUne = (ImageUneDisplay) holder;
            imageUne.init(model);

            if (!model.getUser_id().equals(user_id)) {
                imageUne.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), imageUne.relLayout_follow, imageUne.menu_item, imageUne.delete_suggestion);

            } else {
                imageUne.relLayout_follow.setVisibility(View.GONE);
            }

            // delete suggestin
            imageUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageUne, model.getUser_id()));

        } else if (itemViewType == IMAGE_UNE_BIG_IMAGE) {
            ImageUneDisplay imageUne = (ImageUneDisplay) holder;
            imageUne.init(model);

            if (!model.getUser_id().equals(user_id)) {
                imageUne.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), imageUne.relLayout_follow, imageUne.menu_item, imageUne.delete_suggestion);

            } else {
                imageUne.relLayout_follow.setVisibility(View.GONE);
            }

            // delete suggestin
            imageUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageUne, model.getUser_id()));

        }  else if (itemViewType == VIDEO_UNE) {
            VideoUneDisplay videoUne = (VideoUneDisplay) holder;
            videoUne.init(model);

            if (!model.getUser_id().equals(user_id)) {
                videoUne.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), videoUne.relLayout_follow, videoUne.menu_item, videoUne.delete_suggestion);

            } else
                videoUne.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            videoUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoUne, model.getUser_id()));

        }  else if (itemViewType == VIDEO_UNE_BIG_IMAGE) {
            VideoUneDisplay videoUne = (VideoUneDisplay) holder;
            videoUne.init(model);

            if (!model.getUser_id().equals(user_id)) {
                videoUne.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), videoUne.relLayout_follow, videoUne.menu_item, videoUne.delete_suggestion);

            } else
                videoUne.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            videoUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoUne, model.getUser_id()));

        } else if (itemViewType == IMAGE_DOUBLE){
            ImageDoubleDisplay imageDouble = (ImageDoubleDisplay) holder;
            imageDouble.init(model);

            if (!model.getUser_id().equals(user_id)) {
                imageDouble.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), imageDouble.relLayout_follow, imageDouble.menu_item, imageDouble.delete_suggestion);

            } else
                imageDouble.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            imageDouble.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageDouble, model.getUser_id()));

        } else if (itemViewType == VIDEO_DOUBLE) {
            VideoDoubleDisplay videoDouble = (VideoDoubleDisplay) holder;
            videoDouble.init(model);

            if (!model.getUser_id().equals(user_id)) {
                videoDouble.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), videoDouble.relLayout_follow, videoDouble.menu_item, videoDouble.delete_suggestion);

            } else
                videoDouble.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            videoDouble.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoDouble, model.getUser_id()));

        } else if (itemViewType == REPONSE_IMG_DOUBLE) {
            ResponseImageDoubleDisplay responseImageDouble = (ResponseImageDoubleDisplay) holder;
            responseImageDouble.init(model);

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

        } else if (itemViewType == REPONSE_VID_DOUBLE) {
            ResponseVideoDoubleDisplay responseVideoDouble = (ResponseVideoDoubleDisplay) holder;
            responseVideoDouble.init(model);

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
        }

        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileDisplay userProfileDisplay = (SharedUserProfileDisplay) holder;
            userProfileDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                userProfileDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), userProfileDisplay.relLayout_follow_sharing, userProfileDisplay.menu_item, userProfileDisplay.delete_suggestion);

            } else
                userProfileDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                userProfileDisplay.relLayout_follow.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), userProfileDisplay.relLayout_follow, null, null);

            } else
                userProfileDisplay.relLayout_follow.setVisibility(View.GONE);

            // delete suggestin
            userProfileDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(userProfileDisplay, model.getUser_id()));

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemDisplay recyclerItemShared = (SharedRecyclerItemDisplay) holder;
            recyclerItemShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                recyclerItemShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), recyclerItemShared.relLayout_follow_sharing, recyclerItemShared.menu_item, recyclerItemShared.delete_suggestion);

            } else
                recyclerItemShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                recyclerItemShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), recyclerItemShared.relLayout_follow_shared, null, null);

            } else
                recyclerItemShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            recyclerItemShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(recyclerItemShared, model.getUser_id()));

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneDisplay imageUneShared = (SharedImageUneDisplay) holder;
            imageUneShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                imageUneShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), imageUneShared.relLayout_follow_sharing, imageUneShared.menu_item, imageUneShared.delete_suggestion);

            } else
                imageUneShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                imageUneShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), imageUneShared.relLayout_follow_shared, null, null);

            } else
                imageUneShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            imageUneShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageUneShared, model.getUser_id()));

        } else if (itemViewType == IMAGE_UNE_SHARED_BIG_IMG) {
            SharedImageUneDisplay imageUneShared = (SharedImageUneDisplay) holder;
            imageUneShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                imageUneShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), imageUneShared.relLayout_follow_sharing, imageUneShared.menu_item, imageUneShared.delete_suggestion);

            } else
                imageUneShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                imageUneShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), imageUneShared.relLayout_follow_shared, null, null);

            } else
                imageUneShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            imageUneShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageUneShared, model.getUser_id()));

        } else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneDisplay videoUneShared = (SharedVideoUneDisplay) holder;
            videoUneShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                videoUneShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), videoUneShared.relLayout_follow_sharing, videoUneShared.menu_item, videoUneShared.delete_suggestion);

            } else
                videoUneShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                videoUneShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), videoUneShared.relLayout_follow_shared, null, null);

            } else
                videoUneShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            videoUneShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoUneShared, model.getUser_id()));

        } else if (itemViewType == VIDEO_UNE_SHARED_BIG_IMG) {
            SharedVideoUneDisplay videoUneShared = (SharedVideoUneDisplay) holder;
            videoUneShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                videoUneShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), videoUneShared.relLayout_follow_sharing, videoUneShared.menu_item, videoUneShared.delete_suggestion);

            } else
                videoUneShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                videoUneShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), videoUneShared.relLayout_follow_shared, null, null);

            } else
                videoUneShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            videoUneShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoUneShared, model.getUser_id()));

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleDisplay imageDoubleShared = (SharedImageDoubleDisplay) holder;
            imageDoubleShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                imageDoubleShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), imageDoubleShared.relLayout_follow_sharing, imageDoubleShared.menu_item, imageDoubleShared.delete_suggestion);

            } else
                imageDoubleShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                imageDoubleShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), imageDoubleShared.relLayout_follow_shared, null, null);

            } else
                imageDoubleShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            imageDoubleShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageDoubleShared, model.getUser_id()));

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleDisplay videoDoubleShared = (SharedVideoDoubleDisplay) holder;
            videoDoubleShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                videoDoubleShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), videoDoubleShared.relLayout_follow_sharing, videoDoubleShared.menu_item, videoDoubleShared.delete_suggestion);

            } else
                videoDoubleShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // bottom
            if (!model.getUser_id_sharing().equals(user_id)) {
                videoDoubleShared.relLayout_follow_shared.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id_sharing(), videoDoubleShared.relLayout_follow_shared, null, null);

            } else
                videoDoubleShared.relLayout_follow_shared.setVisibility(View.GONE);

            // delete suggestin
            videoDoubleShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoDoubleShared, model.getUser_id()));

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleDisplay responseImageDoubleShared = (SharedResponseImageDoubleDisplay) holder;
            responseImageDoubleShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                responseImageDoubleShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), responseImageDoubleShared.relLayout_follow_sharing, responseImageDoubleShared.menu_item, responseImageDoubleShared.delete_suggestion);

            } else
                responseImageDoubleShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestin
            responseImageDoubleShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(responseImageDoubleShared, model.getUser_id()));

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleDisplay responseVideoDoubleShared = (SharedResponseVideoDoubleDisplay) holder;
            responseVideoDoubleShared.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                responseVideoDoubleShared.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), responseVideoDoubleShared.relLayout_follow_sharing, responseVideoDoubleShared.menu_item, responseVideoDoubleShared.delete_suggestion);

            } else
                responseVideoDoubleShared.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            responseVideoDoubleShared.delete_suggestion.setOnClickListener(view -> deleteSuggestion(responseVideoDoubleShared, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageDisplay singleTopCircleImageDisplay = (SharedSingleTopCircleImageDisplay) holder;
            singleTopCircleImageDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopCircleImageDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopCircleImageDisplay.relLayout_follow_sharing, singleTopCircleImageDisplay.menu_item, singleTopCircleImageDisplay.delete_suggestion);

            } else
                singleTopCircleImageDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopCircleImageDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopCircleImageDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleDisplay singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleDisplay) holder;
            singleTopImageDoubleDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopImageDoubleDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopImageDoubleDisplay.relLayout_follow_sharing, singleTopImageDoubleDisplay.menu_item, singleTopImageDoubleDisplay.delete_suggestion);

            } else
                singleTopImageDoubleDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopImageDoubleDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneDisplay singleTopImageUneDisplay = (SharedSingleTopImageUneDisplay) holder;
            singleTopImageUneDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopImageUneDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopImageUneDisplay.relLayout_follow_sharing, singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay.delete_suggestion);

            } else
                singleTopImageUneDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopImageUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopImageUneDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG) {
            SharedSingleTopImageUneDisplay singleTopImageUneDisplay = (SharedSingleTopImageUneDisplay) holder;
            singleTopImageUneDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopImageUneDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopImageUneDisplay.relLayout_follow_sharing, singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay.delete_suggestion);

            } else
                singleTopImageUneDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopImageUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopImageUneDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemDisplay singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemDisplay) holder;
            singleTopRecyclerItemDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopRecyclerItemDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopRecyclerItemDisplay.relLayout_follow_sharing, singleTopRecyclerItemDisplay.menu_item, singleTopRecyclerItemDisplay.delete_suggestion);

            } else
                singleTopRecyclerItemDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopRecyclerItemDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopRecyclerItemDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleDisplay singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleDisplay) holder;
            singleTopResponseImageDoubleDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopResponseImageDoubleDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopResponseImageDoubleDisplay.relLayout_follow_sharing, singleTopResponseImageDoubleDisplay.menu_item, singleTopResponseImageDoubleDisplay.delete_suggestion);

            } else
                singleTopResponseImageDoubleDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopResponseImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopResponseImageDoubleDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleDisplay singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleDisplay) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopResponseVideoDoubleDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopResponseVideoDoubleDisplay.relLayout_follow_sharing, singleTopResponseVideoDoubleDisplay.menu_item, singleTopResponseVideoDoubleDisplay.delete_suggestion);

            } else
                singleTopResponseVideoDoubleDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopResponseVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopResponseVideoDoubleDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleDisplay singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleDisplay) holder;
            singleTopVideoDoubleDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopVideoDoubleDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopVideoDoubleDisplay.relLayout_follow_sharing, singleTopVideoDoubleDisplay.menu_item, singleTopVideoDoubleDisplay.delete_suggestion);

            } else
                singleTopVideoDoubleDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopVideoDoubleDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneDisplay singleTopVideoUneDisplay = (SharedSingleTopVideoUneDisplay) holder;
            singleTopVideoUneDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopVideoUneDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopVideoUneDisplay.relLayout_follow_sharing, singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay.delete_suggestion);

            } else
                singleTopVideoUneDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopVideoUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopVideoUneDisplay, model.getUser_id()));

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG) {
            SharedSingleTopVideoUneDisplay singleTopVideoUneDisplay = (SharedSingleTopVideoUneDisplay) holder;
            singleTopVideoUneDisplay.init(model);

            // top
            if (!model.getUser_id().equals(user_id)) {
                singleTopVideoUneDisplay.relLayout_follow_sharing.setVisibility(View.VISIBLE);
                // follow
                getFollow(model.getUser_id(), singleTopVideoUneDisplay.relLayout_follow_sharing, singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay.delete_suggestion);

            } else
                singleTopVideoUneDisplay.relLayout_follow_sharing.setVisibility(View.GONE);

            // delete suggestion
            singleTopVideoUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleTopVideoUneDisplay, model.getUser_id()));

        }

        // hide progressbar
        progressBar.setVisibility(View.GONE);
    }

    // follow ______________________________________________________________________________________
    private void getFollow(String userID, RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion) {
        Query myQuery = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());

                    relLayout_follow.setVisibility(View.GONE);
                }

                if (!snapshot.exists()) {
                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(userID);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                isFollowing(user, relLayout_follow, menu_item, delete_suggestion);

                                HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
                                HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

                                relLayout_follow.setOnClickListener(view -> {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_following))
                                            .child(user_id)
                                            .child(user.getUser_id())
                                            .setValue(map_other_user);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_followers))
                                            .child(user.getUser_id())
                                            .child(user_id)
                                            .setValue(map_current_user);
                                    setFollowing(relLayout_follow, menu_item, delete_suggestion);
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFollowing(RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_follow.setVisibility(View.GONE);
        try {
            menu_item.setVisibility(View.VISIBLE);
            delete_suggestion.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.d(TAG, "setFollowing: "+e.getMessage());
        }
    }

    private void setUnFollowing(RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_follow.setVisibility(View.VISIBLE);
        try {
            menu_item.setVisibility(View.GONE);
            delete_suggestion.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.d(TAG, "setUnFollowing: "+e.getMessage());
        }
    }

    private void isFollowing(User user, RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion){
        Log.d(TAG, "isFollowing: checking if following this users.");
        try {
            setUnFollowing(relLayout_follow, menu_item, delete_suggestion);
        } catch (NullPointerException e) {
            Log.d(TAG, "isFollowing: "+e.getMessage());
        }

        Query query = myRef.child(context.getString(R.string.dbname_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing(relLayout_follow, menu_item, delete_suggestion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // delete suggestion
    private void deleteSuggestion(RecyclerView.ViewHolder holder, String id) {
        // show dialog box
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

        TextView dialog_title = v.findViewById(R.id.dialog_title);
        TextView dialog_text = v.findViewById(R.id.dialog_text);
        TextView negativeButton = v.findViewById(R.id.tv_no);
        TextView positiveButton = v.findViewById(R.id.tv_yes);
        builder.setView(v);

        Dialog d = builder.create();
        d.show();

        negativeButton.setText(context.getString(R.string.cancel));
        positiveButton.setText(context.getString(R.string.hide));

        dialog_title.setText(Html.fromHtml(context.getString(R.string.hide_suggestion)));
        dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_will_definitely_remove_this_suggestion_for_you)));

        negativeButton.setOnClickListener(view2 -> d.dismiss());

        positiveButton.setOnClickListener(view3 -> {
            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();
                d.dismiss();

            } else {
                Query query = myRef.child(context.getString(R.string.dbname_remove_Suggestion))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            RemoveSuggestionModel suggestionModel = Util_RemoveSuggestionModel.getRemoveSuggestionModel(context, objectMap);

                            if (!suggestionModel.getUser_id().equals(id)) {
                                holder.itemView.setVisibility(View.GONE);
                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height = 0;
                                params.width = 0;
                                holder.itemView.setLayoutParams(params);

                                String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                HashMap<String, Object> map_account = new HashMap<>();
                                map_account.put("user_id", id);
                                assert newKey != null;
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_remove_Suggestion))
                                        .child(user_id)
                                        .child(newKey)
                                        .setValue(map_account);
                                d.dismiss();
                            } else {
                                d.dismiss();
                                Toast.makeText(context, context.getResources().getString(R.string.your_request_is_in_progress), Toast.LENGTH_LONG).show();
                            }
                        }

                        if (!snapshot.exists()) {
                            holder.itemView.setVisibility(View.GONE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            params.width = 0;
                            holder.itemView.setLayoutParams(params);

                            String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                            HashMap<String, Object> map_account = new HashMap<>();
                            map_account.put("user_id", id);
                            assert newKey != null;
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_remove_Suggestion))
                                    .child(user_id)
                                    .child(newKey)
                                    .setValue(map_account);
                            d.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (!getItem(position).isGroup() && getItem(position).isComment_text())
            return COMMENT_TEXT;
        else if (getItem(position).isUser_profile())
            return USER_PROFILE;
        else if (getItem(position).isRecyclerItem())
            return RECYCLER_ITEM;
        else if (getItem(position).isImageUne() && !getItem(position).isBig_image())
            return IMAGE_UNE;
        else if (getItem(position).isImageUne() && getItem(position).isBig_image())
            return IMAGE_UNE_BIG_IMAGE;
        else if (getItem(position).isVideoUne() && !getItem(position).isBig_image())
            return VIDEO_UNE;
        else if (getItem(position).isVideoUne() && getItem(position).isBig_image())
            return VIDEO_UNE_BIG_IMAGE;
        else if (getItem(position).isImageDouble())
            return IMAGE_DOUBLE;
        else if (getItem(position).isVideoDouble())
            return VIDEO_DOUBLE;
        else if (getItem(position).isReponseImageDouble())
            return REPONSE_IMG_DOUBLE;
        else if (getItem(position).isReponseVideoDouble())
            return REPONSE_VID_DOUBLE;
            // share
        else if (getItem(position).isUser_profile_shared())
            return USER_PROFILE_SHARE;
        else if (getItem(position).isRecyclerItem_shared())
            return RECYCLER_ITEM_SHARED;
        else if (getItem(position).isImageUne_shared() && !getItem(position).isBig_image())
            return IMAGE_UNE_SHARED;
        else if (getItem(position).isImageUne_shared() && getItem(position).isBig_image())
            return IMAGE_UNE_SHARED_BIG_IMG;
        else if (getItem(position).isVideoUne_shared() && !getItem(position).isBig_image())
            return VIDEO_UNE_SHARED;
        else if (getItem(position).isVideoUne_shared() && getItem(position).isBig_image())
            return VIDEO_UNE_SHARED_BIG_IMG;
        else if (getItem(position).isImageDouble_shared())
            return IMAGE_DOUBLE_SHARED;
        else if (getItem(position).isVideoDouble_shared())
            return VIDEO_DOUBLE_SHARED;
        else if (getItem(position).isReponseImageDouble_shared())
            return REPONSE_IMG_DOUBLE_SHARED;
        else if (getItem(position).isReponseVideoDouble_shared())
            return REPONSE_VID_DOUBLE_SHARED;

        else  if (getItem(position).isGroup_share_single_top_circle_image())
            return GROUP_SINGLE_TOP_CIRCLE_IMAGE;
        else  if (getItem(position).isGroup_share_single_top_image_double())
            return GROUP_SINGLE_TOP_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_single_top_image_une() && !getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_IMAGE_UNE;
        else  if (getItem(position).isGroup_share_single_top_image_une() && getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG;
        else  if (getItem(position).isGroup_share_single_top_recycler())
            return GROUP_SINGLE_TOP_RECYCLER;
        else  if (getItem(position).isGroup_share_single_top_response_image_double())
            return GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_single_top_response_video_double())
            return GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_single_top_video_double())
            return GROUP_SINGLE_TOP_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_single_top_video_une() && !getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_VIDEO_UNE;
        else  if (getItem(position).isGroup_share_single_top_video_une() && getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG;

        else
            return -1;
    }
}

