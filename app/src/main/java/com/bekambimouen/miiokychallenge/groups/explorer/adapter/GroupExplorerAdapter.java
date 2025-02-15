package com.bekambimouen.miiokychallenge.groups.explorer.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupCommentTextDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupCoverBackProfileDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupCoverDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupRecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomCircleImageDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomRecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedSingleBottomVideoUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomCircleImageDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomImageUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomRecyclerItemDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupSharedTopBottomVideoUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupVideoUneDisplay;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupExplorerAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "GroupExplorerAdapter";

    public static final int FOLLOWING_GROUP = 1;
    public static final int GROUP_COVER_IMAGE = 2;
    public static final int GROUP_COVER_BACK_PROFILE = 3;
    public static final int GROUP_RECYCLER_ITEM = 4;
    public static final int GROUP_IMAGE_UNE = 5;
    public static final int GROUP_IMAGE_UNE_BIG_IMG = 52;
    public static final int GROUP_VIDEO_UNE = 6;
    public static final int GROUP_VIDEO_UNE_BIG_IMG = 62;
    public static final int GROUP_IMAGE_DOUBLE = 7;
    public static final int GROUP_VIDEO_DOUBLE = 8;
    public static final int GROUP_RESPONSE_IMAGE_DOUBLE = 9;
    public static final int GROUP_RESPONSE_VIDEO_DOUBLE = 10;
    // share single bottom
    public static final int GROUP_SINGLE_BOTTOM_CIRCLE_IMAGE = 11;
    public static final int GROUP_SINGLE_BOTTOM_IMAGE_DOUBLE = 12;
    public static final int GROUP_SINGLE_BOTTOM_IMAGE_UNE = 13;
    public static final int GROUP_SINGLE_BOTTOM_IMAGE_UNE_BIG_IMG = 132;
    public static final int GROUP_SINGLE_BOTTOM_RECYCLER = 14;
    public static final int GROUP_SINGLE_BOTTOM_RESPONSE_IMAGE_DOUBLE = 15;
    public static final int GROUP_SINGLE_BOTTOM_RESPONSE_VIDEO_DOUBLE = 16;
    public static final int GROUP_SINGLE_BOTTOM_VIDEO_DOUBLE = 17;
    public static final int GROUP_SINGLE_BOTTOM_VIDEO_UNE = 18;
    public static final int GROUP_SINGLE_BOTTOM_VIDEO_UNE_BIG_IMG = 182;
    // share top bottom
    public static final int GROUP_TOP_BOTTOM_CIRCLE_IMAGE = 19;
    public static final int GROUP_TOP_BOTTOM_IMAGE_DOUBLE = 20;
    public static final int GROUP_TOP_BOTTOM_IMAGE_UNE = 21;
    public static final int GROUP_TOP_BOTTOM_IMAGE_UNE_BIG_IMG = 212;
    public static final int GROUP_TOP_BOTTOM_RECYCLER = 22;
    public static final int GROUP_TOP_BOTTOM_RESPONSE_IMAGE_DOUBLE = 23;
    public static final int GROUP_TOP_BOTTOM_RESPONSE_VIDEO_DOUBLE = 24;
    public static final int GROUP_TOP_BOTTOM_VIDEO_DOUBLE = 25;
    public static final int GROUP_TOP_BOTTOM_VIDEO_UNE = 26;
    public static final int GROUP_TOP_BOTTOM_VIDEO_UNE_BIG_IMG = 262;

    // comment text
    public static final int GROUP_COMMENT_TEXT = 30;
    // vars
    private final AppCompatActivity context;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupExplorerAdapter(AppCompatActivity context, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
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
        if (viewType == FOLLOWING_GROUP) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_group_following_recyclerview,parent,false);
            return new FollowingGroups(view);

        } else if (viewType == GROUP_COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_comment_text, parent, false);
            return new GroupCommentTextDisplay(context, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_item, parent, false);
            return new GroupCoverDisplay(context, relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_BACK_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_item, parent, false);
            return new GroupCoverBackProfileDisplay(context, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_recycler_item, parent, false);
            return new GroupRecyclerItemDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_imageune_item, parent, false);
            return new GroupImageUneDisplay(context, null, null, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_imageune_big_img, parent, false);
            return new GroupImageUneDisplay(context, null, null, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_videoune_item, parent, false);
            return new GroupVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_videoune_big_img, parent, false);
            return new GroupVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_imagedouble_item, parent, false);
            return new GroupImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_videodouble_item, parent, false);
            return new GroupVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_reponse_imagedouble_item, parent, false);
            return new GroupResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_reponse_videodouble_item, parent, false);
            return new GroupResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_circle_image, parent, false);
            return new GroupSharedSingleBottomCircleImageDisplay(context, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_imagedouble, parent, false);
            return new GroupSharedSingleBottomImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_imageune, parent, false);
            return new GroupSharedSingleBottomImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_IMAGE_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_imageune_big_img, parent, false);
            return new GroupSharedSingleBottomImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_recycler, parent, false);
            return new GroupSharedSingleBottomRecyclerItemDisplay(context, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_reponse_imagedouble, parent, false);
            return new GroupSharedSingleBottomResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_reponse_videodouble, parent, false);
            return new GroupSharedSingleBottomResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_videodouble, parent, false);
            return new GroupSharedSingleBottomVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_VIDEO_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_videoune, parent, false);
            return new GroupSharedSingleBottomVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_BOTTOM_VIDEO_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_single_bottom_videoune_big_img, parent, false);
            return new GroupSharedSingleBottomVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_circle_image, parent, false);
            return new GroupSharedTopBottomCircleImageDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_imagedouble, parent, false);
            return new GroupSharedTopBottomImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_imageune, parent, false);
            return new GroupSharedTopBottomImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_IMAGE_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_imageune_big_img, parent, false);
            return new GroupSharedTopBottomImageUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_recycler, parent, false);
            return new GroupSharedTopBottomRecyclerItemDisplay(context, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_reponse_imagedouble, parent, false);
            return new GroupSharedTopBottomResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_reponse_videodouble, parent, false);
            return new GroupSharedTopBottomResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_videodouble_item, parent, false);
            return new GroupSharedTopBottomVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_videoune, parent, false);
            return new GroupSharedTopBottomVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_TOP_BOTTOM_VIDEO_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_shared_top_bottom_videoune_big_img, parent, false);
            return new GroupSharedTopBottomVideoUneDisplay(context, null, null, null, relLayout_view_overlay, view);

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
        if (itemViewType == GROUP_COMMENT_TEXT) {
            GroupCommentTextDisplay groupCommentText = (GroupCommentTextDisplay) holder;
            groupCommentText.init(model);

            // delete suggestion
            groupCommentText.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupCommentText, model.getGroup_id()));

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupCoverDisplay groupCover = (GroupCoverDisplay) holder;
            groupCover.init(model);

            // delete suggestion
            groupCover.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupCover, model.getGroup_id()));

        } else if (itemViewType == GROUP_RECYCLER_ITEM) {
            GroupRecyclerItemDisplay groupRecyclerItem = (GroupRecyclerItemDisplay) holder;
            groupRecyclerItem.init(model);

            // delete suggestion
            groupRecyclerItem.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupRecyclerItem, model.getGroup_id()));

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupCoverBackProfileDisplay groupCoverBackProfile = (GroupCoverBackProfileDisplay) holder;
            groupCoverBackProfile.init(model);

            // delete suggestion
            groupCoverBackProfile.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupCoverBackProfile, model.getGroup_id()));

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            GroupImageUneDisplay group_imageUne = (GroupImageUneDisplay) holder;
            group_imageUne.init(model);

            // delete suggestion
            group_imageUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(group_imageUne, model.getGroup_id()));

        } else if (itemViewType == GROUP_IMAGE_UNE_BIG_IMG) {
            GroupImageUneDisplay group_imageUne = (GroupImageUneDisplay) holder;
            group_imageUne.init(model);

            // delete suggestion
            group_imageUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(group_imageUne, model.getGroup_id()));

        } else if (itemViewType == GROUP_VIDEO_UNE) {
            GroupVideoUneDisplay groupVideoUne = (GroupVideoUneDisplay) holder;
            groupVideoUne.init(model);

            // delete suggestion
            groupVideoUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupVideoUne, model.getGroup_id()));

        } else if (itemViewType == GROUP_VIDEO_UNE_BIG_IMG) {
            GroupVideoUneDisplay groupVideoUne = (GroupVideoUneDisplay) holder;
            groupVideoUne.init(model);

            // delete suggestion
            groupVideoUne.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupVideoUne, model.getGroup_id()));

        } else if (itemViewType == GROUP_IMAGE_DOUBLE){
            GroupImageDoubleDisplay imageDoubleGroupExplorer = (GroupImageDoubleDisplay) holder;
            imageDoubleGroupExplorer.init(model);

            // delete suggestion
            imageDoubleGroupExplorer.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageDoubleGroupExplorer, model.getGroup_id()));

        } else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDoubleDisplay videoDoubleGroupExplorer = (GroupVideoDoubleDisplay) holder;
            videoDoubleGroupExplorer.init(model);

            // delete suggestion
            videoDoubleGroupExplorer.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoDoubleGroupExplorer, model.getGroup_id()));

        } else if (itemViewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            GroupResponseImageDoubleDisplay groupResponseImageDoubleDisplay = (GroupResponseImageDoubleDisplay) holder;
            groupResponseImageDoubleDisplay.init(model);

            if (model.isEveryone_can_answer_this_challenge())
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, null, model.getInvite_url(),
                    false, false, true, false);
            groupResponseImageDoubleDisplay.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(groupResponseImageDoubleDisplay.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

            // delete suggestion
            groupResponseImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupResponseImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            GroupResponseVideoDoubleDisplay groupResponseVideoDoubleDisplay = (GroupResponseVideoDoubleDisplay) holder;
            groupResponseVideoDoubleDisplay.init(model);

            if (model.isEveryone_can_answer_this_challenge())
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, "video", model.getThumbnail_invite(),
                    false, false, false, true);
            groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(groupResponseVideoDoubleDisplay.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

            // delete suggestion
            groupResponseVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupResponseVideoDoubleDisplay, model.getGroup_id()));

        }

        // share
        else if (itemViewType == GROUP_SINGLE_BOTTOM_CIRCLE_IMAGE) {
            GroupSharedSingleBottomCircleImageDisplay singleBottomCircleImageDisplay = (GroupSharedSingleBottomCircleImageDisplay) holder;
            singleBottomCircleImageDisplay.init(model);

            // delete suggestion
            singleBottomCircleImageDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomCircleImageDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_IMAGE_DOUBLE) {
            GroupSharedSingleBottomImageDoubleDisplay singleBottomImageDoubleDisplay = (GroupSharedSingleBottomImageDoubleDisplay) holder;
            singleBottomImageDoubleDisplay.init(model);

            // delete suggestion
            singleBottomImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_IMAGE_UNE) {
            GroupSharedSingleBottomImageUneDisplay singleBottomImageUneDisplay = (GroupSharedSingleBottomImageUneDisplay) holder;
            singleBottomImageUneDisplay.init(model);

            // delete suggestion
            singleBottomImageUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomImageUneDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_IMAGE_UNE_BIG_IMG) {
            GroupSharedSingleBottomImageUneDisplay singleBottomImageUneDisplay = (GroupSharedSingleBottomImageUneDisplay) holder;
            singleBottomImageUneDisplay.init(model);

            // delete suggestion
            singleBottomImageUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomImageUneDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_RECYCLER) {
            GroupSharedSingleBottomRecyclerItemDisplay singleBottomRecyclerItemDisplay = (GroupSharedSingleBottomRecyclerItemDisplay) holder;
            singleBottomRecyclerItemDisplay.init(model);

            // delete suggestion
            singleBottomRecyclerItemDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomRecyclerItemDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_RESPONSE_IMAGE_DOUBLE) {
            GroupSharedSingleBottomResponseImageDoubleDisplay singleBottomResponseImageDoubleDisplay = (GroupSharedSingleBottomResponseImageDoubleDisplay) holder;
            singleBottomResponseImageDoubleDisplay.init(model);

            // delete suggestion
            singleBottomResponseImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomResponseImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_RESPONSE_VIDEO_DOUBLE) {
            GroupSharedSingleBottomResponseVideoDoubleDisplay singleBottomResponseVideoDoubleDisplay = (GroupSharedSingleBottomResponseVideoDoubleDisplay) holder;
            singleBottomResponseVideoDoubleDisplay.init(model);

            // delete suggestion
            singleBottomResponseVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomResponseVideoDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_VIDEO_DOUBLE) {
            GroupSharedSingleBottomVideoDoubleDisplay singleBottomVideoDoubleDisplay = (GroupSharedSingleBottomVideoDoubleDisplay) holder;
            singleBottomVideoDoubleDisplay.init(model);

            // delete suggestion
            singleBottomVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomVideoDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_VIDEO_UNE) {
            GroupSharedSingleBottomVideoUneDisplay singleBottomVideoUneDisplay = (GroupSharedSingleBottomVideoUneDisplay) holder;
            singleBottomVideoUneDisplay.init(model);

            // delete suggestion
            singleBottomVideoUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomVideoUneDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_SINGLE_BOTTOM_VIDEO_UNE_BIG_IMG) {
            GroupSharedSingleBottomVideoUneDisplay singleBottomVideoUneDisplay = (GroupSharedSingleBottomVideoUneDisplay) holder;
            singleBottomVideoUneDisplay.init(model);

            // delete suggestion
            singleBottomVideoUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(singleBottomVideoUneDisplay, model.getGroup_id()));

        }

        else if (itemViewType == GROUP_TOP_BOTTOM_CIRCLE_IMAGE) {
            GroupSharedTopBottomCircleImageDisplay topBottomCircleImageDisplay = (GroupSharedTopBottomCircleImageDisplay) holder;
            topBottomCircleImageDisplay.init(model);

            // delete suggestion
            topBottomCircleImageDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomCircleImageDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_IMAGE_DOUBLE) {
            GroupSharedTopBottomImageDoubleDisplay topBottomImageDoubleDisplay = (GroupSharedTopBottomImageDoubleDisplay) holder;
            topBottomImageDoubleDisplay.init(model);

            // delete suggestion
            topBottomImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_IMAGE_UNE) {
            GroupSharedTopBottomImageUneDisplay topBottomImageUneDisplay = (GroupSharedTopBottomImageUneDisplay) holder;
            topBottomImageUneDisplay.init(model);

            // delete suggestion
            topBottomImageUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomImageUneDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_IMAGE_UNE_BIG_IMG) {
            GroupSharedTopBottomImageUneDisplay topBottomImageUneDisplay = (GroupSharedTopBottomImageUneDisplay) holder;
            topBottomImageUneDisplay.init(model);

            // delete suggestion
            topBottomImageUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomImageUneDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_RECYCLER) {
            GroupSharedTopBottomRecyclerItemDisplay topBottomRecyclerItemDisplay = (GroupSharedTopBottomRecyclerItemDisplay) holder;
            topBottomRecyclerItemDisplay.init(model);

            // delete suggestion
            topBottomRecyclerItemDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomRecyclerItemDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_RESPONSE_IMAGE_DOUBLE) {
            GroupSharedTopBottomResponseImageDoubleDisplay topBottomResponseImageDoubleDisplay = (GroupSharedTopBottomResponseImageDoubleDisplay) holder;
            topBottomResponseImageDoubleDisplay.init(model);

            // delete suggestion
            topBottomResponseImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomResponseImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_RESPONSE_VIDEO_DOUBLE) {
            GroupSharedTopBottomResponseVideoDoubleDisplay topBottomResponseVideoDoubleDisplay = (GroupSharedTopBottomResponseVideoDoubleDisplay) holder;
            topBottomResponseVideoDoubleDisplay.init(model);

            // delete suggestion
            topBottomResponseVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomResponseVideoDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_VIDEO_DOUBLE) {
            GroupSharedTopBottomVideoDoubleDisplay topBottomVideoDoubleDisplay = (GroupSharedTopBottomVideoDoubleDisplay) holder;
            topBottomVideoDoubleDisplay.init(model);

            // delete suggestion
            topBottomVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomVideoDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_VIDEO_UNE) {
            GroupSharedTopBottomVideoUneDisplay topBottomVideoUneDisplay = (GroupSharedTopBottomVideoUneDisplay) holder;
            topBottomVideoUneDisplay.init(model);

            // delete suggestion
            topBottomVideoUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomVideoUneDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_TOP_BOTTOM_VIDEO_UNE_BIG_IMG) {
            GroupSharedTopBottomVideoUneDisplay topBottomVideoUneDisplay = (GroupSharedTopBottomVideoUneDisplay) holder;
            topBottomVideoUneDisplay.init(model);

            // delete suggestion
            topBottomVideoUneDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(topBottomVideoUneDisplay, model.getGroup_id()));

        }
        // hide the progressBar
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
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
        if (getItem(position).isRecycler_story())
            return FOLLOWING_GROUP;
        else if (getItem(position).isComment_text())
            return GROUP_COMMENT_TEXT;
        else if (getItem(position).isGroup_cover_profile_photo())
            return GROUP_COVER_IMAGE;
        else if (getItem(position).isGroup_cover_background_profile_photo())
            return GROUP_COVER_BACK_PROFILE;
        else if (getItem(position).isGroup_recyclerItem())
            return GROUP_RECYCLER_ITEM;
        else if (getItem(position).isGroup_imageUne() && !getItem(position).isBig_image())
            return GROUP_IMAGE_UNE;
        else if (getItem(position).isGroup_imageUne() && getItem(position).isBig_image())
            return GROUP_IMAGE_UNE_BIG_IMG;
        else if (getItem(position).isGroup_videoUne() && !getItem(position).isBig_image())
            return GROUP_VIDEO_UNE;
        else if (getItem(position).isGroup_videoUne() && getItem(position).isBig_image())
            return GROUP_VIDEO_UNE_BIG_IMG;
        else if (getItem(position).isGroup_imageDouble())
            return GROUP_IMAGE_DOUBLE;
        else if (getItem(position).isGroup_videoDouble())
            return GROUP_VIDEO_DOUBLE;
        else if (getItem(position).isGroup_response_imageDouble())
            return GROUP_RESPONSE_IMAGE_DOUBLE;
        else if (getItem(position).isGroup_response_videoDouble())
            return GROUP_RESPONSE_VIDEO_DOUBLE;

        else  if (getItem(position).isGroup_share_single_bottom_circle_image())
            return GROUP_SINGLE_BOTTOM_CIRCLE_IMAGE;
        else  if (getItem(position).isGroup_share_single_bottom_image_double())
            return GROUP_SINGLE_BOTTOM_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_single_bottom_image_une() && !getItem(position).isBig_image())
            return GROUP_SINGLE_BOTTOM_IMAGE_UNE;
        else  if (getItem(position).isGroup_share_single_bottom_image_une() && getItem(position).isBig_image())
            return GROUP_SINGLE_BOTTOM_IMAGE_UNE_BIG_IMG;
        else  if (getItem(position).isGroup_share_single_bottom_recycler())
            return GROUP_SINGLE_BOTTOM_RECYCLER;
        else  if (getItem(position).isGroup_share_single_bottom_response_image_double())
            return GROUP_SINGLE_BOTTOM_RESPONSE_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_single_bottom_response_video_double())
            return GROUP_SINGLE_BOTTOM_RESPONSE_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_single_bottom_video_double())
            return GROUP_SINGLE_BOTTOM_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_single_bottom_video_une() && !getItem(position).isBig_image())
            return GROUP_SINGLE_BOTTOM_VIDEO_UNE;
        else  if (getItem(position).isGroup_share_single_bottom_video_une() && getItem(position).isBig_image())
            return GROUP_SINGLE_BOTTOM_VIDEO_UNE_BIG_IMG;

        else  if (getItem(position).isGroup_share_top_bottom_circle_image())
            return GROUP_TOP_BOTTOM_CIRCLE_IMAGE;
        else  if (getItem(position).isGroup_share_top_bottom_image_double())
            return GROUP_TOP_BOTTOM_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_image_une() && !getItem(position).isBig_image())
            return GROUP_TOP_BOTTOM_IMAGE_UNE;
        else  if (getItem(position).isGroup_share_top_bottom_image_une() && getItem(position).isBig_image())
            return GROUP_TOP_BOTTOM_IMAGE_UNE_BIG_IMG;
        else  if (getItem(position).isGroup_share_top_bottom_recycler())
            return GROUP_TOP_BOTTOM_RECYCLER;
        else  if (getItem(position).isGroup_share_top_bottom_response_image_double())
            return GROUP_TOP_BOTTOM_RESPONSE_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_response_video_double())
            return GROUP_TOP_BOTTOM_RESPONSE_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_video_double())
            return GROUP_TOP_BOTTOM_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_video_une() && !getItem(position).isBig_image())
            return GROUP_TOP_BOTTOM_VIDEO_UNE;
        else  if (getItem(position).isGroup_share_top_bottom_video_une() && getItem(position).isBig_image())
            return GROUP_TOP_BOTTOM_VIDEO_UNE_BIG_IMG;
        return -1;
    }

    public class FollowingGroups extends RecyclerView.ViewHolder {

        // widgets
        private final RecyclerView recyclerView_horizontal;

        // vars
        private GroupHorizontalFollowingAdapter adapter;
        private ArrayList<UserGroups> list, finalList;
        public ArrayList<String> followingUserList;
        public ArrayList<String> group_id_List;
        public ArrayList<String> followingUserIDList;
        public ArrayList<String> userIDList;
        private final Handler handler;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public FollowingGroups(@NonNull View itemView) {
            super(itemView);
            recyclerView_horizontal = itemView.findViewById(R.id.recyclerView_following);
            recyclerView_horizontal.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_horizontal.setItemAnimator(new DefaultItemAnimator());

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            handler = new Handler(Looper.getMainLooper());
            getGroupFollowing();
        }

        @SuppressLint("NotifyDataSetChanged")
        private void clearAll(){
            if(list != null){
                list.clear();
            }
            if(followingUserList != null){
                followingUserList.clear();
            }
            if(group_id_List != null){
                group_id_List.clear();
            }
            if(followingUserIDList != null){
                followingUserIDList.clear();
            }
            if(userIDList != null){
                userIDList.clear();
            }

            if(adapter != null){
                adapter = null;
            }

            if(recyclerView_horizontal != null){
                handler.post(() -> recyclerView_horizontal.setAdapter(null));
            }

            list = new ArrayList<>();
            finalList = new ArrayList<>();
            followingUserList = new ArrayList<>();
            followingUserIDList = new ArrayList<>();
            group_id_List = new ArrayList<>();
            userIDList = new ArrayList<>();
        }

        /**
         // * RÃ©cupÃ©rer tous les identifiants d'utilisateur que l'utilisateur actuel suit
         // */
        private void getGroupFollowing() {
            clearAll();

            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id);

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // first add current user id
                    userIDList.add(user_id);

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        String admin_id = following.getAdmin_master();
                        if (!userIDList.contains(admin_id))
                            userIDList.add(admin_id);
                    }

                    getUserIdList(userIDList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            Query myQuery1 = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id);

            myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // get all current user groups
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        String groupID = ds.getKey();
                        group_id_List.add(groupID);
                    }

                    // add all other following groups
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                // get key
                                String followingId = ds.getKey();
                                if (!following.isBanFromGroup())
                                    group_id_List.add(followingId);
                            }

                            getGroups(group_id_List);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // get list of all followings
        private void getUserIdList(ArrayList<String> following_usersIDList) {
            followingUserIDList.addAll(following_usersIDList);
        }

        private void getGroups(ArrayList<String> group_id_List) {
            if (!followingUserIDList.isEmpty()) {
                for(int i = 0; i < followingUserIDList.size(); i++){
                    final int count_user_list = i;

                    Query query = myRef
                            .child(context.getString(R.string.dbname_user_group))
                            .child(followingUserIDList.get(i))
                            .orderByChild(context.getString(R.string.field_admin_master))
                            .equalTo(followingUserIDList.get(i));

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                // get data
                                for (String groupKey : group_id_List) {
                                    if (user_groups.getGroup_id().equals(groupKey))
                                        list.add(user_groups);
                                }
                            }

                            if(count_user_list >= followingUserIDList.size() -1){

                                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                        .compareTo(String.valueOf(o1.getDate_created())));

                                if (list.size()  >= 12) {
                                    for (int i = 0; i < 12; i++) {
                                        finalList.add(list.get(i));
                                    }

                                } else {
                                    finalList.addAll(list);
                                }

                                adapter = new GroupHorizontalFollowingAdapter(context, finalList, list.size(), relLayout_view_overlay);
                                recyclerView_horizontal.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }
    }
}

