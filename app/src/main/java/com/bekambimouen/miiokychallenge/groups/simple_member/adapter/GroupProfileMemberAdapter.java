package com.bekambimouen.miiokychallenge.groups.simple_member.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.display_insta.see_user_all_profile.SeeUserAllProfileOnMiioky;
import com.bekambimouen.miiokychallenge.groups.GroupUpdateMember;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCommentText;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCoverBackgroundProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCoverImage;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupImageDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupImageUne;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupRecyclerItem;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupResponseImageDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupResponseVideoDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupVideoDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupVideoUne;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedImageDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedImageUneViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedRecyclerItemViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedResponseImageDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedResponseVideoDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopCircleImageViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopImageDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopImageUneViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopRecyclerItemViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopResponseImageDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopResponseVideoDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopVideoDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedSingleTopVideoUneViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedUserProfileViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedVideoDoubleViewProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.SharedVideoUneViewProfile;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.see_member_profile.SeeGroupMemberAllBackgroundProfile;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "GroupProfileMemberAdapter";
    // load more item
    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    public static final int HEADER = 0;
    public static final int GROUP_COVER_IMAGE = 1;
    public static final int GROUP_COVER_BACK_PROFILE = 2;
    public static final int GROUP_RECYCLER_ITEM = 3;
    public static final int GROUP_IMAGE_UNE = 4;
    public static final int GROUP_IMAGE_UNE_BIG_IMG = 42;
    public static final int GROUP_VIDEO_UNE = 5;
    public static final int GROUP_VIDEO_UNE_BIG_IMG = 52;
    public static final int GROUP_IMAGE_DOUBLE = 6;
    public static final int GROUP_VIDEO_DOUBLE = 7;
    public static final int GROUP_RESPONSE_IMAGE_DOUBLE = 8;
    public static final int GROUP_RESPONSE_VIDEO_DOUBLE = 9;
    // share single bottom
    public static final int USER_PROFILE_SHARE = 10;
    public static final int RECYCLER_ITEM_SHARED = 11;
    public static final int IMAGE_UNE_SHARED = 12;
    public static final int IMAGE_UNE_SHARED_BIG_IMG = 122;
    public static final int VIDEO_UNE_SHARED = 13;
    public static final int VIDEO_UNE_SHARED_BIG_IMG = 132;
    public static final int IMAGE_DOUBLE_SHARED = 14;
    public static final int VIDEO_DOUBLE_SHARED = 15;
    public static final int REPONSE_IMG_DOUBLE_SHARED = 16;
    public static final int REPONSE_VID_DOUBLE_SHARED = 17;
    // share top bottom
    public static final int GROUP_SINGLE_TOP_CIRCLE_IMAGE = 18;
    public static final int GROUP_SINGLE_TOP_IMAGE_DOUBLE = 19;
    public static final int GROUP_SINGLE_TOP_IMAGE_UNE = 20;
    public static final int GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG = 202;
    public static final int GROUP_SINGLE_TOP_RECYCLER = 21;
    public static final int GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE = 22;
    public static final int GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE = 23;
    public static final int GROUP_SINGLE_TOP_VIDEO_DOUBLE = 24;
    public static final int GROUP_SINGLE_TOP_VIDEO_UNE = 25;
    public static final int GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG = 252;
    // comment text
    public static final int GROUP_COMMENT_TEXT = 30;

    // vars
    private final AppCompatActivity context;
    private final List<BattleModel> list;
    private final UserGroups user_groups;
    private final String userID;
    private final RelativeLayout relLayout_board;
    private final RelativeLayout relLayout_view_overlay;

    private CustomShareProgressView progressDialog;

    private void showLoading(){
        if (progressDialog == null)
            progressDialog = new CustomShareProgressView(context);
        progressDialog.show();
    }

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public GroupProfileMemberAdapter(AppCompatActivity context, List<BattleModel> list,
                                     UserGroups user_groups, String userID, RelativeLayout relLayout_board,
                                     OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.user_groups = user_groups;
        this.userID = userID;
        this.relLayout_board = relLayout_board;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_header_group_member_profile, parent, false);
            return new RecyclerViewHeader(relLayout_view_overlay, view);

        } else if (viewType == GROUP_COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_comment_text, parent, false);
            return new GroupCommentText(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_photo, parent, false);
            return new GroupCoverImage(context, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_BACK_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_photo, parent, false);
            return new GroupCoverBackgroundProfile(context, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_recycler, parent, false);
            return new GroupRecyclerItem(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imageune, parent, false);
            return new GroupImageUne(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imageune_big_img, parent, false);
            return new GroupImageUne(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videoune, parent, false);
            return new GroupVideoUne(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videoune_big_img, parent, false);
            return new GroupVideoUne(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imagedouble, parent, false);
            return new GroupImageDouble(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videodouble, parent, false);
            return new GroupVideoDouble(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_imagedouble, parent, false);
            return new GroupResponseImageDouble(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_videodouble, parent, false);
            return new GroupResponseVideoDouble(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        }

        // share
        else if (viewType == USER_PROFILE_SHARE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_user_profile, parent, false);
            return new SharedUserProfileViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == RECYCLER_ITEM_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_recycler, parent, false);
            return new SharedRecyclerItemViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune, parent, false);
            return new SharedImageUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune_big_img, parent, false);
            return new SharedImageUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune, parent, false);
            return new SharedVideoUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune_big_img, parent, false);
            return new SharedVideoUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imagedouble, parent, false);
            return new SharedImageDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videodouble_item, parent, false);
            return new SharedVideoDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_imagedouble, parent, false);
            return new SharedResponseImageDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_videodouble, parent, false);
            return new SharedResponseVideoDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        }


        else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_circle_image, parent, false);
            return new SharedSingleTopCircleImageViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imagedouble, parent, false);
            return new SharedSingleTopImageDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune, parent, false);
            return new SharedSingleTopImageUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune_big_img, parent, false);
            return new SharedSingleTopImageUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_recycler, parent, false);
            return new SharedSingleTopRecyclerItemViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_imagedouble, parent, false);
            return new SharedSingleTopResponseImageDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_videodouble, parent, false);
            return new SharedSingleTopResponseVideoDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videodouble_item, parent, false);
            return new SharedSingleTopVideoDoubleViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune, parent, false);
            return new SharedSingleTopVideoUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune_big_img, parent, false);
            return new SharedSingleTopVideoUneViewProfile(context, false, false, "from_profile_member", relLayout_view_overlay, view);

        } else
            return null;
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
        if (itemViewType == GROUP_COMMENT_TEXT) {
            GroupCommentText commentText = (GroupCommentText) holder;
            commentText.init(model);

            if (model.getUser_id().equals(user_id)) {
                commentText.profile.setEnabled(false);
                commentText.relLayout_username.setEnabled(false);
            }

            // delete post
            deletePostFromGroup(model, model.getPhoto_id(), commentText.menu_item, commentText);

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupCoverImage coverImage = (GroupCoverImage) holder;
            coverImage.init(model);

            if (model.getUser_id().equals(user_id)) {
                coverImage.relLayout_go_user_profile.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id(), coverImage.menu_item, coverImage);

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupCoverBackgroundProfile coverBackgroundProfile = (GroupCoverBackgroundProfile) holder;
            coverBackgroundProfile.init(model);

            if (model.getUser_id().equals(user_id)) {
                coverBackgroundProfile.relLayout_go_user_profile.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id(), coverBackgroundProfile.menu_item, coverBackgroundProfile);

        } else if (itemViewType == GROUP_RECYCLER_ITEM) {
            GroupRecyclerItem recyclerItem = (GroupRecyclerItem) holder;
            recyclerItem.init(model);

            if (model.getUser_id().equals(user_id)) {
                recyclerItem.profil_photo.setEnabled(false);
                recyclerItem.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhotoi_id(), recyclerItem.menu_item, recyclerItem);

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUne.profile.setEnabled(false);
                imageUne.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id(), imageUne.menu_item, imageUne);

        } else if (itemViewType == GROUP_IMAGE_UNE_BIG_IMG) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUne.profile.setEnabled(false);
                imageUne.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id(), imageUne.menu_item, imageUne);

        } else if (itemViewType == GROUP_VIDEO_UNE) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUne.profile.setEnabled(false);
                videoUne.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id(), videoUne.menu_item, videoUne);

        } else if (itemViewType == GROUP_VIDEO_UNE_BIG_IMG) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUne.profile.setEnabled(false);
                videoUne.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id(), videoUne.menu_item, videoUne);

        } else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDouble imageDouble = (GroupImageDouble) holder;
            imageDouble.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageDouble.profile.setEnabled(false);
                imageDouble.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id_un(), imageDouble.menu_item, imageDouble);

        }  else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDouble videoDouble = (GroupVideoDouble) holder;
            videoDouble.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoDouble.profile.setEnabled(false);
                videoDouble.relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroup(model, model.getPhoto_id_un(), videoDouble.menu_item, videoDouble);

        } else if (itemViewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            GroupResponseImageDouble groupResponseImageDoubleDisplay = (GroupResponseImageDouble) holder;
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

            if (model.getUser_id().equals(user_id)) {
                groupResponseImageDoubleDisplay.relLayout_go_user_profile_un.setEnabled(false);
                groupResponseImageDoubleDisplay.menu_item.setVisibility(View.VISIBLE);
            }

            // delete
            deletePostFromGroup(model, model.getReponse_photoID(), groupResponseImageDoubleDisplay.menu_item, groupResponseImageDoubleDisplay);

        } else if (itemViewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            GroupResponseVideoDouble groupResponseVideoDoubleDisplay = (GroupResponseVideoDouble) holder;
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

            if (model.getUser_id().equals(user_id)) {
                groupResponseVideoDoubleDisplay.relLayout_go_user_profile_un.setEnabled(false);
                groupResponseVideoDoubleDisplay.menu_item.setVisibility(View.VISIBLE);
            }

            // delete
            deletePostFromGroup(model, model.getReponse_photoID(), groupResponseVideoDoubleDisplay.menu_item, groupResponseVideoDoubleDisplay);
        }

        // share
        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileViewProfile userProfileDisplay = (SharedUserProfileViewProfile) holder;
            userProfileDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                userProfileDisplay.sharing_profile_photo.setEnabled(false);
                userProfileDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), userProfileDisplay.menu_item, userProfileDisplay);

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemViewProfile recyclerItemShared = (SharedRecyclerItemViewProfile) holder;
            recyclerItemShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                recyclerItemShared.sharing_profile_photo.setEnabled(false);
                recyclerItemShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), recyclerItemShared.menu_item, recyclerItemShared);

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUneShared.sharing_profile_photo.setEnabled(false);
                imageUneShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        } else if (itemViewType == IMAGE_UNE_SHARED_BIG_IMG) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageUneShared.sharing_profile_photo.setEnabled(false);
                imageUneShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        } else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUneShared.sharing_profile_photo.setEnabled(false);
                videoUneShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == VIDEO_UNE_SHARED_BIG_IMG) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoUneShared.sharing_profile_photo.setEnabled(false);
                videoUneShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleViewProfile imageDoubleShared = (SharedImageDoubleViewProfile) holder;
            imageDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                imageDoubleShared.sharing_profile_photo.setEnabled(false);
                imageDoubleShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), imageDoubleShared.menu_item, imageDoubleShared);

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleViewProfile videoDoubleShared = (SharedVideoDoubleViewProfile) holder;
            videoDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                videoDoubleShared.sharing_profile_photo.setEnabled(false);
                videoDoubleShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), videoDoubleShared.menu_item, videoDoubleShared);

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleViewProfile responseImageDoubleShared = (SharedResponseImageDoubleViewProfile) holder;
            responseImageDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                responseImageDoubleShared.sharing_profile_photo.setEnabled(false);
                responseImageDoubleShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), responseImageDoubleShared.menu_item, responseImageDoubleShared);

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleViewProfile responseVideoDoubleShared = (SharedResponseVideoDoubleViewProfile) holder;
            responseVideoDoubleShared.init(model);

            if (model.getUser_id().equals(user_id)) {
                responseVideoDoubleShared.sharing_profile_photo.setEnabled(false);
                responseVideoDoubleShared.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), responseVideoDoubleShared.menu_item, responseVideoDoubleShared);

        }

        else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageViewProfile singleTopCircleImageDisplay = (SharedSingleTopCircleImageViewProfile) holder;
            singleTopCircleImageDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopCircleImageDisplay.sharing_profile_photo.setEnabled(false);
                singleTopCircleImageDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopCircleImageDisplay.menu_item, singleTopCircleImageDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleViewProfile singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleViewProfile) holder;
            singleTopImageDoubleDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopImageDoubleDisplay.sharing_profile_photo.setEnabled(false);
                singleTopImageDoubleDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopImageDoubleDisplay.menu_item, singleTopImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopImageUneDisplay.sharing_profile_photo.setEnabled(false);
                singleTopImageUneDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopImageUneDisplay.sharing_profile_photo.setEnabled(false);
                singleTopImageUneDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemViewProfile singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemViewProfile) holder;
            singleTopRecyclerItemDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopRecyclerItemDisplay.sharing_profile_photo.setEnabled(false);
                singleTopRecyclerItemDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopRecyclerItemDisplay.menu_item, singleTopRecyclerItemDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleViewProfile singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleViewProfile) holder;
            singleTopResponseImageDoubleDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopResponseImageDoubleDisplay.sharing_profile_photo.setEnabled(false);
                singleTopResponseImageDoubleDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopResponseImageDoubleDisplay.menu_item, singleTopResponseImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleViewProfile singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleViewProfile) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopResponseVideoDoubleDisplay.sharing_profile_photo.setEnabled(false);
                singleTopResponseVideoDoubleDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopResponseVideoDoubleDisplay.menu_item, singleTopResponseVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleViewProfile singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleViewProfile) holder;
            singleTopVideoDoubleDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopVideoDoubleDisplay.sharing_profile_photo.setEnabled(false);
                singleTopVideoDoubleDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopVideoDoubleDisplay.menu_item, singleTopVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopVideoUneDisplay.sharing_profile_photo.setEnabled(false);
                singleTopVideoUneDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

            if (model.getUser_id().equals(user_id)) {
                singleTopVideoUneDisplay.sharing_profile_photo.setEnabled(false);
                singleTopVideoUneDisplay.sharing_relLayout_username.setEnabled(false);
            }

            // delete
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay);
        }

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) context;
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    // delete post from group
    private void deletePostFromGroup(BattleModel model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        if (model.getUser_id().equals(user_id)) {
            menu_item.setOnClickListener(view -> {
                // prevent double click
                Util.preventTwoClick(view);

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

                            showLoading();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", true);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_media))
                                    .child(photo_id)
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group))
                                    .child(model.getGroup_id())
                                    .child(photo_id)
                                    .updateChildren(map).addOnSuccessListener(unused1 -> {
                                        removeAt(holder.getLayoutPosition());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    });
                        });
                    }
                    return false;
                });
                //displaying the popup
                popup.show();
            });

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(model.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model.getGroup_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                        menu_item.setOnClickListener(view -> {
                            if (bottomSheet.isAdded())
                                return;
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user_groups);

                            Bundle args = new Bundle();
                            args.putParcelable("battle_model", model);
                            args.putString("user_groups", myJson);
                            args.putString("group", "group");
                            bottomSheet.setArguments(args);
                            bottomSheet.show(context.getSupportFragmentManager(),
                                    "TAG");
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // delete post from group share
    private void deletePostFromGroupShare(BattleModel model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        if (model.getUser_id().equals(user_id)) {

            menu_item.setOnClickListener(view -> {
                // prevent double click
                Util.preventTwoClick(view);

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

                            showLoading();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", true);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_media_share))
                                    .child(photo_id)
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group))
                                    .child(model.getGroup_id())
                                    .child(photo_id)
                                    .updateChildren(map).addOnSuccessListener(unused1 -> {
                                        removeAt(holder.getLayoutPosition());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    });
                        });
                    }
                    return false;
                });
                //displaying the popup
                popup.show();
            });

        } else {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(model.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model.getGroup_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                        menu_item.setOnClickListener(view -> {
                            if (bottomSheet.isAdded())
                                return;
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user_groups);

                            Bundle args = new Bundle();
                            args.putParcelable("battle_model", model);
                            args.putString("user_groups", myJson);
                            args.putString("group", "group");
                            bottomSheet.setArguments(args);
                            bottomSheet.show(context.getSupportFragmentManager(),
                                    "TAG");
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
        if (list.get(position).isRecycler_story())
            return HEADER;
        else if (list.get(position).isComment_text())
            return GROUP_COMMENT_TEXT;
        else if (list.get(position).isGroup_cover_profile_photo())
            return GROUP_COVER_IMAGE;
        else if (list.get(position).isGroup_cover_background_profile_photo())
            return GROUP_COVER_BACK_PROFILE;
        else if (list.get(position).isGroup_recyclerItem())
            return GROUP_RECYCLER_ITEM;
        else if (list.get(position).isGroup_imageUne() && !list.get(position).isBig_image())
            return GROUP_IMAGE_UNE;
        else if (list.get(position).isGroup_imageUne() && list.get(position).isBig_image())
            return GROUP_IMAGE_UNE_BIG_IMG;
        else if (list.get(position).isGroup_videoUne() && !list.get(position).isBig_image())
            return GROUP_VIDEO_UNE;
        else if (list.get(position).isGroup_videoUne() && list.get(position).isBig_image())
            return GROUP_VIDEO_UNE_BIG_IMG;
        else if (list.get(position).isGroup_imageDouble())
            return GROUP_IMAGE_DOUBLE;
        else if (list.get(position).isGroup_videoDouble())
            return GROUP_VIDEO_DOUBLE;
        else if (list.get(position).isGroup_response_imageDouble())
            return GROUP_RESPONSE_IMAGE_DOUBLE;
        else if (list.get(position).isGroup_response_videoDouble())
            return GROUP_RESPONSE_VIDEO_DOUBLE;

            // share
        else  if (list.get(position).isGroup_share_single_bottom_circle_image())
            return USER_PROFILE_SHARE;
        else  if (list.get(position).isGroup_share_single_bottom_image_double())
            return IMAGE_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_image_une() && !list.get(position).isBig_image())
            return IMAGE_UNE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_image_une() && list.get(position).isBig_image())
            return IMAGE_UNE_SHARED_BIG_IMG;
        else  if (list.get(position).isGroup_share_single_bottom_recycler())
            return RECYCLER_ITEM_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_response_image_double())
            return REPONSE_IMG_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_response_video_double())
            return REPONSE_VID_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_video_double())
            return VIDEO_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_video_une() && !list.get(position).isBig_image())
            return VIDEO_UNE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_video_une() && list.get(position).isBig_image())
            return VIDEO_UNE_SHARED_BIG_IMG;

        else  if (list.get(position).isGroup_share_top_bottom_circle_image())
            return GROUP_SINGLE_TOP_CIRCLE_IMAGE;
        else  if (list.get(position).isGroup_share_top_bottom_image_double())
            return GROUP_SINGLE_TOP_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_image_une() && !list.get(position).isBig_image())
            return GROUP_SINGLE_TOP_IMAGE_UNE;
        else  if (list.get(position).isGroup_share_top_bottom_image_une() && list.get(position).isBig_image())
            return GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG;
        else  if (list.get(position).isGroup_share_top_bottom_recycler())
            return GROUP_SINGLE_TOP_RECYCLER;
        else  if (list.get(position).isGroup_share_top_bottom_response_image_double())
            return GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_response_video_double())
            return GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_video_double())
            return GROUP_SINGLE_TOP_VIDEO_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_video_une() && !list.get(position).isBig_image())
            return GROUP_SINGLE_TOP_VIDEO_UNE;
        else  if (list.get(position).isGroup_share_top_bottom_video_une() && list.get(position).isBig_image())
            return GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG;
        else
            return -1;
    }

    public class RecyclerViewHeader extends RecyclerView.ViewHolder {
        private static final String TAG = "RecyclerViewHeader";
        // widgets
        private final ImageView profile_photo;
        private final CircleImageView user_profile_photo;
        private final TextView date_following, number_of_followers_and_friend, bio, username, no_yet_publication, txt_points;
        private final RelativeLayout relLayout_update, relLayoutDiscussion, relLayout_see_the_profile;
        private final RelativeLayout relLayout_not_member;
        private final RelativeLayout relLayout_see_not_member_profile;
        private final LinearLayout linLayout_about_me;
        private final LinearLayout linLayout_member, linLayout_points;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private int nber_of_followers = 0, nber_of_friends = 0;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public RecyclerViewHeader(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            relLayout_update = itemView.findViewById(R.id.relLayout_update);
            relLayoutDiscussion = itemView.findViewById(R.id.relLayoutDiscussion);
            relLayout_see_the_profile = itemView.findViewById(R.id.relLayout_see_the_profile);
            username = itemView.findViewById(R.id.username);
            user_profile_photo = itemView.findViewById(R.id.user_profile_photo);
            linLayout_points = itemView.findViewById(R.id.linLayout_points);
            txt_points = itemView.findViewById(R.id.txt_points);
            date_following = itemView.findViewById(R.id.date_following);
            number_of_followers_and_friend = itemView.findViewById(R.id.number_of_followers_and_friend);
            linLayout_about_me = itemView.findViewById(R.id.linLayout_about_me);
            bio = itemView.findViewById(R.id.bio);
            RelativeLayout relLayout_not_pub_yet = itemView.findViewById(R.id.relLayout_not_pub_yet);
            no_yet_publication = itemView.findViewById(R.id.no_yet_publication);
            relLayout_not_member = itemView.findViewById(R.id.relLayout_not_member);
            linLayout_member = itemView.findViewById(R.id.linLayout_member);
            LinearLayout parent = itemView.findViewById(R.id.parent);
            relLayout_see_not_member_profile = itemView.findViewById(R.id.relLayout_see_not_member_profile);
            String theme = user_groups.getGroup_theme();

            if (theme.equals(context.getString(R.string.theme_normal))) {

                parent.setBackgroundResource(R.drawable.toolbar_blue_grey_50);

            } else if (theme.equals(context.getString(R.string.theme_blue))) {

                parent.setBackgroundResource(R.drawable.toolbar_blue_clear_background);

            } else if (theme.equals(context.getString(R.string.theme_brown))) {

                parent.setBackgroundResource(R.drawable.toolbar_brown_clear_background);

            } else if (theme.equals(context.getString(R.string.theme_pink))) {

                parent.setBackgroundResource(R.drawable.toolbar_pink_clear_background);

            } else if (theme.equals(context.getString(R.string.theme_green))) {

                parent.setBackgroundResource(R.drawable.toolbar_green_clear_background);

            } else if (theme.equals(context.getString(R.string.theme_black))) {

                parent.setBackgroundResource(R.drawable.toolbar_black_clear_background);

            }

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            // verify is current user is suspended _____________________________________________________
            Query query0 = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(user_groups.getGroup_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);

            query0.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        GroupFollowersFollowing follower = new GroupFollowersFollowing();

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                        assert objectMap != null;
                        follower.setSuspended(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suspended))).toString())));

                        if (follower.isSuspended())
                            relLayout_board.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@org.jetbrains.annotations.NotNull DatabaseError error) {

                }
            });
            // _________________________________________________________________________________________

            if (userID.equals(user_id)) {
                relLayout_update.setVisibility(View.VISIBLE);
                relLayout_board.setVisibility(View.VISIBLE);
                relLayoutDiscussion.setVisibility(View.GONE);
            }

            if (list.size() <= 1) {
                relLayout_not_pub_yet.setVisibility(View.VISIBLE);
            }

            Query theQuery1 = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(userID);
            theQuery1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        no_yet_publication.setText(Html.fromHtml(user.getUsername()+" "+context.getString(R.string.no_yet_publication)+
                                " <b>"+user_groups.getGroup_name()+"</b>"));

                        relLayout_see_not_member_profile.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                intent = new Intent(context, Profile.class);

                            } else {
                                intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                            }
                            context.startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            getUserData();

            // number of followers on miioky
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_followers))
                    .child(userID);
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                        nber_of_followers++;
                    }

                    // number of friends on miioky
                    Query myQuery2 = myRef
                            .child(context.getString(R.string.dbname_friend_follower))
                            .child(userID);
                    myQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+ds.getKey());
                                nber_of_friends++;
                            }

                            // no followers and no friends
                            if (nber_of_followers == 0 && nber_of_friends == 0) {
                                number_of_followers_and_friend.setVisibility(View.GONE);
                            }

                            // followers but no friends
                            if (nber_of_followers != 0 && nber_of_friends == 0) {
                                if (nber_of_followers == 1)
                                    number_of_followers_and_friend.setText(Html.fromHtml("<b>"+ nber_of_followers +"</b> "+context.getString(R.string.follower_on_miioky)));
                                else
                                    number_of_followers_and_friend.setText(Html.fromHtml("<b>"+ nber_of_followers +"</b> "+context.getString(R.string.followers_on_miioky)));

                            }

                            // friends but no followers
                            if (nber_of_followers == 0 && nber_of_friends != 0) {
                                if (nber_of_friends == 1)
                                    number_of_followers_and_friend.setText(Html.fromHtml("<b>"+ nber_of_friends +"</b> "+context.getString(R.string.friend_on_miioky)));
                                else
                                    number_of_followers_and_friend.setText(Html.fromHtml("<b>"+ nber_of_friends +"</b> "+context.getString(R.string.friends_on_miioky)));
                            }

                            // followers and friends
                            if (nber_of_followers != 0 && nber_of_friends != 0) {
                                if (nber_of_followers == 1 && nber_of_friends == 1) {
                                    number_of_followers_and_friend.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.one_follower)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.one_friend_on_miioky)));
                                }

                                if (nber_of_followers > 1 && nber_of_friends == 1) {
                                    number_of_followers_and_friend.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.several_followers)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.one_friend_on_miioky)));
                                }

                                if (nber_of_followers == 1 && nber_of_friends > 1) {
                                    number_of_followers_and_friend.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.one_follower)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.several_friends_on_miioky)));
                                }

                                if (nber_of_followers > 1 && nber_of_friends > 1) {
                                    number_of_followers_and_friend.setText(Html.fromHtml(
                                            "<b>"+ nber_of_followers +"</b> "+context.getString(R.string.several_followers)
                                                    +" "+context.getString(R.string.and)
                                                    +" <b>"+ nber_of_friends +"</b> "+context.getString(R.string.several_friends_on_miioky)));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@org.jetbrains.annotations.NotNull DatabaseError error) {

                }
            });

            // get the admin master bio
            if (userID.equals(user_groups.getAdmin_master())) {
                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(user_groups.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            long tv_date = user_groups.getDate_created();
                            date_following.setText(Html.fromHtml(context.getString(R.string.member_of_group)+" <b>"
                                    +user_groups.getGroup_name()+"</b> "+context.getString(R.string.member_from)+" "+ TimeUtils.getTime(context, tv_date)));

                            // get the admin points
                            int post_points = Integer.parseInt(user_groups.getPost_points());
                            int comment_points = Integer.parseInt(user_groups.getComment_points());
                            int share_points = Integer.parseInt(user_groups.getShare_points());
                            int like_points = Integer.parseInt(user_groups.getLike_points());

                            int total_point = post_points + comment_points + share_points + like_points;
                            if (total_point >= 1) {
                                linLayout_points.setVisibility(View.VISIBLE);

                                DecimalFormat formatter = new DecimalFormat("#,###,###");
                                if (total_point == 1)
                                    txt_points.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                            " "+context.getString(R.string.point)));
                                else
                                    txt_points.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                            " "+context.getString(R.string.points)));
                            }

                            if (!TextUtils.isEmpty(user_groups.getAdmin_master_bio())) {
                                linLayout_about_me.setVisibility(View.VISIBLE);
                                bio.setText(user_groups.getAdmin_master_bio());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } // get the member bio
            else {
                Query query1 = myRef
                        .child(context.getString(R.string.dbname_group_following))
                        .child(userID)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());

                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            long tv_date = following.getDate_following();
                            date_following.setText(Html.fromHtml(context.getString(R.string.member_of_group)+" <b>"
                                    +user_groups.getGroup_name()+"</b> "+context.getString(R.string.member_from)+" "+ TimeUtils.getTime(context, tv_date)));

                            // get the member points
                            int post_points = Integer.parseInt(following.getPost_points());
                            int comment_points = Integer.parseInt(following.getComment_points());
                            int share_points = Integer.parseInt(following.getShare_points());
                            int like_points = Integer.parseInt(following.getLike_points());

                            int total_point = post_points + comment_points + share_points + like_points;
                            if (total_point >= 1) {
                                linLayout_points.setVisibility(View.VISIBLE);

                                DecimalFormat formatter = new DecimalFormat("#,###,###");
                                if (total_point == 1)
                                    txt_points.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                            " "+context.getString(R.string.point)));
                                else
                                    txt_points.setText(Html.fromHtml(formatter.format(total_point).replaceAll(",", " ")+
                                            " "+context.getString(R.string.points)));
                            }

                            if (!following.getMember_bio().isEmpty()) {
                                linLayout_about_me.setVisibility(View.VISIBLE);
                                bio.setText(following.getMember_bio());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            // to get user profile photo background
            Query theQuery = myRef
                    .child(context.getString(R.string.dbname_group))
                    .child(user_groups.getGroup_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(userID);

            theQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                        String background_profile_photo = model.getGroup_user_background_profile_url();
                        String full_profile_photo = model.getGroup_user_background_full_profile_url();
                        boolean isBackgroundProfile = model.isGroup_cover_background_profile_photo();

                        if (isBackgroundProfile) {
                            Glide.with(context.getApplicationContext())
                                    .asBitmap()
                                    .load(background_profile_photo)
                                    .into(profile_photo);

                            Glide.with(context.getApplicationContext())
                                    .load(full_profile_photo)
                                    .preload();

                            profile_photo.setOnClickListener(view -> {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, SeeGroupMemberAllBackgroundProfile.class);
                                intent.putExtra("group_id", user_groups.getGroup_id());
                                context.startActivity(intent);
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(userID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        String profileUrl = user.getProfileUrl();
                        String userName = user.getUsername();

                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(profileUrl)
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(user_profile_photo);

                        Glide.with(context.getApplicationContext())
                                .load(user.getFull_profileUrl())
                                .preload();

                        username.setText(userName);

                        user_profile_photo.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, SeeUserAllProfileOnMiioky.class);
                            intent.putExtra("userID", user.getUser_id());
                            context.startActivity(intent);
                        });

                        relLayout_see_the_profile.setOnClickListener(v -> {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    user.getUsername());

                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                intent = new Intent(context, Profile.class);

                            } else {
                                intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                            }
                            context.startActivity(intent);
                        });

                        relLayoutDiscussion.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("to_chat", myGson);
                            context.startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // to prevent apparition of relLayout_not_member when we are adamin of group
            if (!user_groups.getAdmin_master().equals(user_id))
                isFollowing(user_groups.getGroup_id());
        }

        private void setFollowing(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            relLayout_not_member.setVisibility(View.GONE);
            linLayout_member.setVisibility(View.VISIBLE);
        }

        private void setUnfollowing(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            relLayout_not_member.setVisibility(View.VISIBLE);
            linLayout_member.setVisibility(View.GONE);
        }

        private void isFollowing(String group_id){
            Log.d(TAG, "isFollowing: checking if following this users.");
            setUnfollowing();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                        setFollowing();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void getUserData() {
            String theme = user_groups.getGroup_theme();

            if (theme.equals(context.getString(R.string.theme_normal))) {
                relLayout_update.setBackgroundResource(R.drawable.button_blue);
                relLayoutDiscussion.setBackgroundResource(R.drawable.button_blue);
                relLayout_see_not_member_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));

            } else if (theme.equals(context.getString(R.string.theme_blue))) {
                relLayout_update.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                relLayoutDiscussion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                relLayout_see_not_member_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));

            } else if (theme.equals(context.getString(R.string.theme_brown))) {
                relLayout_update.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));
                relLayoutDiscussion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));
                relLayout_see_not_member_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));

            } else if (theme.equals(context.getString(R.string.theme_pink))) {
                relLayout_update.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));
                relLayoutDiscussion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));
                relLayout_see_not_member_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));

            } else if (theme.equals(context.getString(R.string.theme_green))) {
                relLayout_update.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                relLayoutDiscussion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                relLayout_see_not_member_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));

            } else if (theme.equals(context.getString(R.string.theme_black))) {
                relLayout_update.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));
                relLayoutDiscussion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));
                relLayout_see_not_member_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));

            }

            relLayout_update.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupUpdateMember.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);
            });
        }
    }
}

