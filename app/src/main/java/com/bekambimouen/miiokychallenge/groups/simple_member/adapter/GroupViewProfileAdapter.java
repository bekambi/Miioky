package com.bekambimouen.miiokychallenge.groups.simple_member.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.groups.GroupAbout;
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
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.GroupInviteFromSameTown;
import com.bekambimouen.miiokychallenge.groups.manage_member.limit_member_actions.LimitActivityExplanation;
import com.bekambimouen.miiokychallenge.groups.manage_member.suspend.SuspensionExpalanation;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.GroupOnlyPhotoGradle;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.GroupOnlyVideoGradle;
import com.bekambimouen.miiokychallenge.groups.see_member_profile.SeeGroupAllProfile;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_NotificationModel;
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
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupViewProfileAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "GroupViewProfileAdapter";

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
    private final String admin_master;
    private final String group_id;
    private final UserGroups user_groups;
    private final ProgressBar progressBar;
    private CustomShareProgressView progressDialog;
    private final RelativeLayout relLayout_view_overlay;

    private void showLoading(){
        if (progressDialog == null)
            progressDialog = new CustomShareProgressView(context);
        progressDialog.show();
    }

    // firebase
    private final String user_id;

    public GroupViewProfileAdapter(AppCompatActivity context, List<BattleModel> list,
                                   String admin_master, String group_id, UserGroups user_groups,
                                   ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.admin_master = admin_master;
        this.group_id = group_id;
        this.user_groups = user_groups;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_header_group_view_profile, parent, false);
            return new RecyclerViewHeader(relLayout_view_overlay, view);

        } else if (viewType == GROUP_COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_comment_text, parent, false);
            return new GroupCommentText(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_photo, parent, false);
            return new GroupCoverImage(context, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_BACK_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_photo, parent, false);
            return new GroupCoverBackgroundProfile(context, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_recycler, parent, false);
            return new GroupRecyclerItem(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imageune, parent, false);
            return new GroupImageUne(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imageune_big_img, parent, false);
            return new GroupImageUne(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videoune, parent, false);
            return new GroupVideoUne(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videoune_big_img, parent, false);
            return new GroupVideoUne(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imagedouble, parent, false);
            return new GroupImageDouble(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videodouble, parent, false);
            return new GroupVideoDouble(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_imagedouble, parent, false);
            return new GroupResponseImageDouble(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_videodouble, parent, false);
            return new GroupResponseVideoDouble(context, false, false, null, relLayout_view_overlay, view);

        }

        // share
        else if (viewType == USER_PROFILE_SHARE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_user_profile, parent, false);
            return new SharedUserProfileViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == RECYCLER_ITEM_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_recycler, parent, false);
            return new SharedRecyclerItemViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune, parent, false);
            return new SharedImageUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune_big_img, parent, false);
            return new SharedImageUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune, parent, false);
            return new SharedVideoUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune_big_img, parent, false);
            return new SharedVideoUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imagedouble, parent, false);
            return new SharedImageDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videodouble_item, parent, false);
            return new SharedVideoDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_imagedouble, parent, false);
            return new SharedResponseImageDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_videodouble, parent, false);
            return new SharedResponseVideoDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        }

        else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_circle_image, parent, false);
            return new SharedSingleTopCircleImageViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imagedouble, parent, false);
            return new SharedSingleTopImageDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune, parent, false);
            return new SharedSingleTopImageUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune_big_img, parent, false);
            return new SharedSingleTopImageUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_recycler, parent, false);
            return new SharedSingleTopRecyclerItemViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_imagedouble, parent, false);
            return new SharedSingleTopResponseImageDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_videodouble, parent, false);
            return new SharedSingleTopResponseVideoDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videodouble_item, parent, false);
            return new SharedSingleTopVideoDoubleViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune, parent, false);
            return new SharedSingleTopVideoUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune_big_img, parent, false);
            return new SharedSingleTopVideoUneViewProfile(context, false, false, null, relLayout_view_overlay, view);

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
            GroupCommentText commentText = (GroupCommentText) holder;
            commentText.init(model);

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupCoverImage coverImage = (GroupCoverImage) holder;
            coverImage.init(model);

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupCoverBackgroundProfile coverBackgroundProfile = (GroupCoverBackgroundProfile) holder;
            coverBackgroundProfile.init(model);

        } else if (itemViewType == GROUP_RECYCLER_ITEM) {
            GroupRecyclerItem recyclerItem = (GroupRecyclerItem) holder;
            recyclerItem.init(model);

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

        } else if (itemViewType == GROUP_IMAGE_UNE_BIG_IMG) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

        } else if (itemViewType == GROUP_VIDEO_UNE) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

        } else if (itemViewType == GROUP_VIDEO_UNE_BIG_IMG) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

        } else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDouble imageDouble = (GroupImageDouble) holder;
            imageDouble.init(model);

        } else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDouble videoDouble = (GroupVideoDouble) holder;
            videoDouble.init(model);

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
        }

        // share
        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileViewProfile userProfileDisplay = (SharedUserProfileViewProfile) holder;
            userProfileDisplay.init(model);

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemViewProfile recyclerItemShared = (SharedRecyclerItemViewProfile) holder;
            recyclerItemShared.init(model);

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

        } else if (itemViewType == IMAGE_UNE_SHARED_BIG_IMG) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

        } else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

        } else if (itemViewType == VIDEO_UNE_SHARED_BIG_IMG) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleViewProfile imageDoubleShared = (SharedImageDoubleViewProfile) holder;
            imageDoubleShared.init(model);

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleViewProfile videoDoubleShared = (SharedVideoDoubleViewProfile) holder;
            videoDoubleShared.init(model);

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleViewProfile responseImageDoubleShared = (SharedResponseImageDoubleViewProfile) holder;
            responseImageDoubleShared.init(model);

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleViewProfile responseVideoDoubleShared = (SharedResponseVideoDoubleViewProfile) holder;
            responseVideoDoubleShared.init(model);

        }

        else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageViewProfile singleTopCircleImageDisplay = (SharedSingleTopCircleImageViewProfile) holder;
            singleTopCircleImageDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleViewProfile singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleViewProfile) holder;
            singleTopImageDoubleDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemViewProfile singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemViewProfile) holder;
            singleTopRecyclerItemDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleViewProfile singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleViewProfile) holder;
            singleTopResponseImageDoubleDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleViewProfile singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleViewProfile) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleViewProfile singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleViewProfile) holder;
            singleTopVideoDoubleDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

        }

        // hide progressbar
        if (View.VISIBLE == progressBar.getVisibility())
            progressBar.setVisibility(View.GONE);
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isRecycler_story())
            return HEADER;
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

            // share
        else  if (getItem(position).isGroup_share_single_bottom_circle_image())
            return USER_PROFILE_SHARE;
        else  if (getItem(position).isGroup_share_single_bottom_image_double())
            return IMAGE_DOUBLE_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_image_une() && !getItem(position).isBig_image())
            return IMAGE_UNE_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_image_une() && getItem(position).isBig_image())
            return IMAGE_UNE_SHARED_BIG_IMG;
        else  if (getItem(position).isGroup_share_single_bottom_recycler())
            return RECYCLER_ITEM_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_response_image_double())
            return REPONSE_IMG_DOUBLE_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_response_video_double())
            return REPONSE_VID_DOUBLE_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_video_double())
            return VIDEO_DOUBLE_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_video_une() && !getItem(position).isBig_image())
            return VIDEO_UNE_SHARED;
        else  if (getItem(position).isGroup_share_single_bottom_video_une() && getItem(position).isBig_image())
            return VIDEO_UNE_SHARED_BIG_IMG;

        else  if (getItem(position).isGroup_share_top_bottom_circle_image())
            return GROUP_SINGLE_TOP_CIRCLE_IMAGE;
        else  if (getItem(position).isGroup_share_top_bottom_image_double())
            return GROUP_SINGLE_TOP_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_image_une() && !getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_IMAGE_UNE;
        else  if (getItem(position).isGroup_share_top_bottom_image_une() && getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG;
        else  if (getItem(position).isGroup_share_top_bottom_recycler())
            return GROUP_SINGLE_TOP_RECYCLER;
        else  if (getItem(position).isGroup_share_top_bottom_response_image_double())
            return GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_response_video_double())
            return GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_video_double())
            return GROUP_SINGLE_TOP_VIDEO_DOUBLE;
        else  if (getItem(position).isGroup_share_top_bottom_video_une() && !getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_VIDEO_UNE;
        else  if (getItem(position).isGroup_share_top_bottom_video_une() && getItem(position).isBig_image())
            return GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG;
        else
            return -1;
    }

    public class RecyclerViewHeader extends RecyclerView.ViewHolder {
        private static final String TAG = "RecyclerViewHeader";
        // widgets
        private final ImageView profile_photo;
        private final CircleImageView profile_photo_you;
        private final TextView title;
        private final TextView private_public;
        private final TextView bouton_quitter;
        private final TextView bouton_rejoindre;
        private final TextView total_members;
        private final TextView suspension_message;
        private final TextView limit_activity_in_group_message;
        private final RelativeLayout relLayout_you, relLayout_go_about_class, relLayout_invite,
                relLayout_member_button, relLayout_limit_activity_in_group;
        private final RelativeLayout see_only_photos;
        private final RelativeLayout see_only_videos;
        private final LinearLayout linLayout_suspended_message, linLayout_buttons;
        // members profile photo
        private final CircleImageView member_i, member_ii, member_iii, member_iv, member_v, member_vi, member_vii,
                member_viii, member_ix;
        private final RelativeLayout relLayout_member_ii,
                relLayout_member_iii, relLayout_member_iv, relLayout_member_v, relLayout_member_vi,
                relLayout_member_vii, relLayout_member_viii, relLayout_member_ix, relLayout_member_x;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private final ArrayList<String> membersList;
        private int t = 0;

        // firebase
        private final DatabaseReference myRef;
        private final FirebaseMethods mFirebaseMethods;
        private final String user_id;

        public RecyclerViewHeader(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            bouton_quitter = itemView.findViewById(R.id.bouton_quitter);
            bouton_rejoindre = itemView.findViewById(R.id.bouton_rejoindre);
            total_members = itemView.findViewById(R.id.total_members);
            relLayout_invite = itemView.findViewById(R.id.relLayout_invite);
            relLayout_you = itemView.findViewById(R.id.relLayout_you);
            relLayout_member_button = itemView.findViewById(R.id.relLayout_member_button);
            relLayout_go_about_class = itemView.findViewById(R.id.relLayout_go_about_class);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            profile_photo_you = itemView.findViewById(R.id.profile_photo_you);
            title = itemView.findViewById(R.id.title);
            private_public = itemView.findViewById(R.id.private_public);
            see_only_photos = itemView.findViewById(R.id.see_only_photos);
            see_only_videos = itemView.findViewById(R.id.see_only_videos);
            linLayout_suspended_message = itemView.findViewById(R.id.linLayout_suspended_message);
            linLayout_buttons = itemView.findViewById(R.id.linLayout_buttons);
            suspension_message = itemView.findViewById(R.id.suspension_message);
            TextView see_more = itemView.findViewById(R.id.see_more);

            relLayout_limit_activity_in_group = itemView.findViewById(R.id.relLayout_limit_activity_in_group);
            limit_activity_in_group_message = itemView.findViewById(R.id.limit_activity_in_group_message);
            TextView see_more_limit_activity_in_group = itemView.findViewById(R.id.see_more_limit_activity_in_group);

            // profile phot members
            relLayout_member_ii = itemView.findViewById(R.id.relLayout_member_ii);
            relLayout_member_iii = itemView.findViewById(R.id.relLayout_member_iii);
            relLayout_member_iv = itemView.findViewById(R.id.relLayout_member_iv);
            relLayout_member_v = itemView.findViewById(R.id.relLayout_member_v);
            relLayout_member_vi = itemView.findViewById(R.id.relLayout_member_vi);
            relLayout_member_vii = itemView.findViewById(R.id.relLayout_member_vii);
            relLayout_member_viii = itemView.findViewById(R.id.relLayout_member_viii);
            relLayout_member_ix = itemView.findViewById(R.id.relLayout_member_ix);
            relLayout_member_x = itemView.findViewById(R.id.relLayout_member_x);
            member_i = itemView.findViewById(R.id.member_i);
            member_ii = itemView.findViewById(R.id.member_ii);
            member_iii = itemView.findViewById(R.id.member_iii);
            member_iv = itemView.findViewById(R.id.member_iv);
            member_v = itemView.findViewById(R.id.member_v);
            member_vi = itemView.findViewById(R.id.member_vi);
            member_vii = itemView.findViewById(R.id.member_vii);
            member_viii = itemView.findViewById(R.id.member_viii);
            member_ix = itemView.findViewById(R.id.member_ix);

            myRef = FirebaseDatabase.getInstance().getReference();
            mFirebaseMethods = new FirebaseMethods(context);
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            membersList = new ArrayList<>();

            inviteUserToBeMemberOfGroup();
            getTotalMembers();
            getUserData();
            setupFirebaseAuth();

            see_more.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SuspensionExpalanation.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);
            });

            see_more_limit_activity_in_group.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, LimitActivityExplanation.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);
            });
        }

        // invite user to be member of group
        private void inviteUserToBeMemberOfGroup() {
            Query query = myRef.child(context.getString(R.string.dbname_notification))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, dataSnapshot);

                        if (notification.isInvitation_in_group()) {
                            // show or hide dialog box
                            if (notification.isInvite_to_join_the_group()
                                    && !notification.isAccepted_invitation_to_join_the_group()
                                    && !notification.isRefuse_invitation_to_join_the_group()) {
                                // show dialog box
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                                TextView dialog_title = v.findViewById(R.id.dialog_title);
                                TextView dialog_text = v.findViewById(R.id.dialog_text);
                                TextView negativeButton = v.findViewById(R.id.tv_no);
                                TextView positiveButton = v.findViewById(R.id.tv_yes);
                                builder.setView(v);

                                Dialog d = builder.create();
                                d.show();

                                negativeButton.setText(context.getString(R.string.refuse));
                                positiveButton.setText(context.getString(R.string.accept));

                                // get the invited moderator member
                                Query query = myRef.child(context.getString(R.string.dbname_users))
                                        .orderByChild(context.getString(R.string.field_user_id))
                                        .equalTo(notification.getAdding_by());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()) {
                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                            assert objectMap != null;
                                            User user = Util_User.getUser(context, objectMap, ds);

                                            dialog_title.setText(context.getString(R.string.become_group_member_of_this_group));
                                            dialog_text.setText(Html.fromHtml(user.getUsername()+" "
                                                    +context.getString(R.string.invite_you_to_become_member_of_group)+" "
                                                    +user_groups.getGroup_name()+". "+context.getString(R.string.you_will_see_the_group_posts)));

                                            positiveButton.setOnClickListener(view -> {
                                                d.dismiss();

                                                HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(),
                                                        user_id, notification.getAdding_by(), user_groups.getGroup_id());

                                                // follow the group
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(context.getString(R.string.dbname_group_following))
                                                        .child(user_id)
                                                        .child(user_groups.getGroup_id())
                                                        .setValue(map);

                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(context.getString(R.string.dbname_group_followers))
                                                        .child(user_groups.getGroup_id())
                                                        .child(user_id)
                                                        .setValue(map);

                                                // update current user notification
                                                HashMap<String, Object> user_notification = new HashMap<>();
                                                user_notification.put("invite_to_join_the_group", true);
                                                user_notification.put("accepted_invitation_to_join_the_group", true);
                                                user_notification.put("hide_buttons", true);

                                                myRef.child(context.getString(R.string.dbname_notification))
                                                        .child(user_id)
                                                        .child(notification.getNotification_id())
                                                        .updateChildren(user_notification);

                                                // send notification to member who invited
                                                Date date = new Date();
                                                String new_key = myRef.push().getKey();
                                                HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                        true, false, false, false, false,
                                                        false,false, false, false,
                                                        false, false, false, false, false, false,
                                                        false,
                                                        false, false, false, false, false,
                                                        false, false,
                                                        false, false, false, false, false,
                                                        false, false,
                                                        true, "", false, true,
                                                        false, false,
                                                        true,false, false,
                                                        notification.getAdding_by(), new_key, user_id, user_groups.getAdmin_master(),
                                                        "", user_groups.getGroup_id(), date.getTime(),
                                                        false, false,
                                                        false, false, false, false, false, false, false, false,
                                                        false, "", "", "", false, "", "", "", false,
                                                        "", "", "", "", "", 0,
                                                        "", "", 0, "", "", "",
                                                        false, false, false, false,
                                                        false, false, false,
                                                        false, false);

                                                assert new_key != null;
                                                myRef.child(context.getString(R.string.dbname_notification))
                                                        .child(notification.getAdding_by())
                                                        .child(new_key)
                                                        .setValue(map_notification);

                                                // add badge number
                                                HashMap<String, Object> map_number = new HashMap<>();
                                                map_number.put("user_id", notification.getAdding_by());

                                                myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                        .child(notification.getAdding_by())
                                                        .child(new_key)
                                                        .setValue(map_number);

                                                // remove person who has been invited in the list
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                                        .child(user_groups.getGroup_id())
                                                        .child(user_id)
                                                        .removeValue();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                negativeButton.setOnClickListener(view -> {
                                    d.dismiss();

                                    // update current user notification
                                    HashMap<String, Object> user_notification = new HashMap<>();
                                    user_notification.put("invite_to_join_the_group", true);
                                    user_notification.put("accepted_invitation_to_join_the_group", false);
                                    user_notification.put("refuse_invitation_to_join_the_group", true);
                                    user_notification.put("hide_buttons", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(user_notification);

                                    // send notification to person who invited the user
                                    Date date = new Date();
                                    String new_key = myRef.push().getKey();
                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                            true, false, false, false, false,
                                            false,false, false, false,
                                            false, false, false, false, false, false,
                                            false,
                                            false, false, false, false, false,
                                            false, false,
                                            false, false, false, false, false,
                                            false, false,
                                            true, "", false, false,
                                            true, false,
                                            true,false, false,
                                            notification.getAdding_by(), new_key, user_id, user_groups.getAdmin_master(),
                                            "", user_groups.getGroup_id(), date.getTime(),
                                            false, false,
                                            false, false, false, false, false, false, false, false,
                                            false, "", "", "", false, "", "", "", false,
                                            "", "", "", "", "", 0,
                                            "", "", 0, "", "", "",
                                            false, false, false, false,
                                            false, false, false,
                                            false, false);

                                    assert new_key != null;
                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(notification.getAdding_by())
                                            .child(new_key)
                                            .setValue(map_notification);

                                    // add badge number
                                    HashMap<String, Object> map_number = new HashMap<>();
                                    map_number.put("user_id", notification.getAdding_by());

                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                            .child(notification.getAdding_by())
                                            .child(new_key)
                                            .setValue(map_number);

                                    // remove person who has been invited
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                            .child(user_groups.getGroup_id())
                                            .child(user_id)
                                            .removeValue();
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // sanction member or invite member to be an admin or a moderator
        private void getTotalMembers() {
            // get the follower status
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(group_id)
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        // get data
                        String suspension_duration = follower.getSuspension_duration();
                        long sanction_date = follower.getDate_admin_applied_sanction_in_group();
                        long currentTime = System.currentTimeMillis();

                        if (follower.isSuspended()) {
                            linLayout_buttons.setVisibility(View.GONE);
                            linLayout_suspended_message.setVisibility(View.VISIBLE);

                            switch (suspension_duration) {
                                case "12":
                                    if ((sanction_date + 12*3600000L) > currentTime) {
                                        suspension_message.setText(Html.fromHtml(context.getString(R.string.suspension_information) + " "
                                                +context.getString(R.string.open)+ "12" + context.getString(R.string.letter_h)+context.getString(R.string.close)));
                                    }
                                    break;
                                case "24":
                                    if ((sanction_date + 86400000L) > currentTime) {
                                        suspension_message.setText(Html.fromHtml(context.getString(R.string.suspension_information) + " "
                                                +context.getString(R.string.open)+ "24" + context.getString(R.string.letter_h)+context.getString(R.string.close)));
                                    }
                                    break;
                                case "3":
                                    if ((sanction_date + 3 * 86400000L) > currentTime) {
                                        suspension_message.setText(Html.fromHtml(context.getString(R.string.suspension_information) + " "
                                                +context.getString(R.string.open)+ "3" + context.getString(R.string.letter_j)+context.getString(R.string.close)));
                                    }
                                    break;
                                case "7":
                                    if ((sanction_date + 7 * 86400000L) > currentTime) {
                                        suspension_message.setText(Html.fromHtml(context.getString(R.string.suspension_information) + " "
                                                +context.getString(R.string.open)+ "7" + context.getString(R.string.letter_j)+context.getString(R.string.close)));
                                    }
                                    break;
                                case "14":
                                    if ((sanction_date + 14 * 86400000L) > currentTime) {
                                        suspension_message.setText(Html.fromHtml(context.getString(R.string.suspension_information) + " "
                                                +context.getString(R.string.open)+ "14" + context.getString(R.string.letter_j)+context.getString(R.string.close)));
                                    }
                                    break;
                                case "28":
                                    if ((sanction_date + 28 * 86400000L) > currentTime) {
                                        suspension_message.setText(Html.fromHtml(context.getString(R.string.suspension_information) + " "
                                                +context.getString(R.string.open)+ "28" + context.getString(R.string.letter_j)+context.getString(R.string.close)));
                                    }
                                    break;
                            }
                        }
                        // activity limited
                        if (follower.isActivityLimitation()) {
                            relLayout_limit_activity_in_group.setVisibility(View.VISIBLE);

                            // if activity posts has limited
                            if (follower.isPostsActivityLimited() || follower.isCommentsActivityLimited()) {
                                switch (follower.getNumber_posts_per_day_expiration()) {
                                    case "12":
                                        if ((sanction_date + 12*3600000L) > currentTime) {
                                            limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                        }
                                        break;
                                    case "24":
                                        if ((sanction_date + 86400000L) > currentTime) {
                                            limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                        }
                                        break;
                                    case "3":
                                        if ((sanction_date + 3*86400000L) > currentTime) {
                                            limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                        }
                                        break;
                                    case "7":
                                        if ((sanction_date + 7*86400000L) > currentTime) {
                                            limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                        }
                                        break;
                                    case "14":
                                        if ((sanction_date + 14*86400000L) > currentTime) {
                                            limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                        }
                                        break;
                                    case "28":
                                        if ((sanction_date + 28*86400000L) > currentTime) {
                                            limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                        }
                                        break;
                                }

                                // if activity comments has limited
                                if (follower.isCommentsActivityLimited()) {
                                    switch (follower.getNumber_comments_per_day_expiration()) {
                                        case "12":
                                            if ((sanction_date + 12 * 3600000L) > currentTime) {
                                                limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                            }
                                            break;
                                        case "24":
                                            if ((sanction_date + 86400000L) > currentTime) {
                                                limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                            }
                                            break;
                                        case "3":
                                            if ((sanction_date + 3 * 86400000L) > currentTime) {
                                                limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                            }
                                            break;
                                        case "7":
                                            if ((sanction_date + 7 * 86400000L) > currentTime) {
                                                limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                            }
                                            break;
                                        case "14":
                                            if ((sanction_date + 14 * 86400000L) > currentTime) {
                                                limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                            }
                                            break;
                                        case "28":
                                            if ((sanction_date + 28 * 86400000L) > currentTime) {
                                                limit_activity_in_group_message.setText(Html.fromHtml(context.getString(R.string.admin_has_temporarily_limited_your_activity_in_this_groupe)));
                                            }
                                            break;
                                    }
                                }
                            }
                        }

                        // invite to become admin
                        if (follower.isAdminInvitation()) {
                            // show dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                            TextView dialog_title = v.findViewById(R.id.dialog_title);
                            TextView dialog_text = v.findViewById(R.id.dialog_text);
                            TextView negativeButton = v.findViewById(R.id.tv_no);
                            TextView positiveButton = v.findViewById(R.id.tv_yes);
                            builder.setView(v);

                            Dialog d = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                            InsetDrawable inset = new InsetDrawable(back, 50);
                            d.getWindow().setBackgroundDrawable(inset);
                            d.show();

                            negativeButton.setText(context.getString(R.string.refuse));
                            positiveButton.setText(context.getString(R.string.accept));

                            dialog_title.setText(context.getString(R.string.accept_the_invitation_to_become_an_dmin));
                            dialog_text.setText(Html.fromHtml(context.getString(R.string.admin_rules)));

                            Query query1 = myRef
                                    .child(context.getString(R.string.dbname_notification))
                                    .child(user_id)
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user_id);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {

                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                                        negativeButton.setOnClickListener(view -> {
                                            // change the boolean value
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("adminInvitation", false);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(user_id)
                                                    .updateChildren(map);

                                            // send notification to admin
                                            String new_key = myRef.push().getKey();
                                            Date date1 = new Date();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    true, false, false, false, true,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    notification.getAdmin_in_group(), new_key, notification.getMember_id(), user_groups.getAdmin_master(),
                                                    notification.getAdmin_in_group(), user_groups.getGroup_id(), date1.getTime(),
                                                    false, false,
                                                    false, false, false, false, false, false, false, false,
                                                    false, "", "", "", false, "", "", "", false,
                                                    "", "", "", "", "", 0,
                                                    "", "", 0, "", "", "",
                                                    false, false, false, false,
                                                    false, false, false,
                                                    false, false);

                                            assert new_key != null;
                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            d.dismiss();
                                        });

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("adminInGroup", true);
                                            map.put("adminInvitation", false);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(user_id)
                                                    .updateChildren(map);

                                            // send notification to admin
                                            String new_key = myRef.push().getKey();
                                            Date date1 = new Date();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    true, false, false, true, false,
                                                    false, false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    notification.getAdmin_in_group(), new_key, notification.getMember_id(), user_groups.getAdmin_master(),
                                                    notification.getAdmin_in_group(), user_groups.getGroup_id(), date1.getTime(),
                                                    false, false,
                                                    false, false, false, false, false, false, false, false,
                                                    false, "", "", "", false, "", "", "", false,
                                                    "", "", "", "", "", 0,
                                                    "", "", 0, "", "", "",
                                                    false, false, false, false,
                                                    false, false, false,
                                                    false, false);

                                            assert new_key != null;
                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", notification.getAdmin_in_group());

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            // go to admin fragment
                                            if (relLayout_view_overlay != null)
                                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                                            Gson gson = new Gson();
                                            String myJson = gson.toJson(user_groups);
                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                            Intent intent = new Intent(context, GroupViewAdmin.class);
                                            intent.putExtra("user_groups", myJson);
                                            context.startActivity(intent);
                                            context.finish();
                                            d.dismiss();
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        // invite to become moderator
                        if (follower.isModeratorAdding()) {
                            // show dialog box
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                            TextView dialog_title = v.findViewById(R.id.dialog_title);
                            TextView dialog_text = v.findViewById(R.id.dialog_text);
                            TextView negativeButton = v.findViewById(R.id.tv_no);
                            TextView positiveButton = v.findViewById(R.id.tv_yes);
                            builder.setView(v);

                            Dialog d = builder.create();
                            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                            InsetDrawable inset = new InsetDrawable(back, 50);
                            d.getWindow().setBackgroundDrawable(inset);
                            d.show();

                            negativeButton.setText(context.getString(R.string.refuse));
                            positiveButton.setText(context.getString(R.string.accept));

                            dialog_title.setText(context.getString(R.string.accept_the_invitation_to_become_a_moderator));
                            dialog_text.setText(Html.fromHtml(context.getString(R.string.moderator_rules)));

                            Query query1 = myRef
                                    .child(context.getString(R.string.dbname_notification))
                                    .child(user_id)
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(user_id);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {

                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                                        negativeButton.setOnClickListener(view -> {
                                            // change the boolean value
                                            HashMap<String, Object> map1 = new HashMap<>();
                                            map1.put("moderatorAdding", false);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map1);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(user_id)
                                                    .updateChildren(map1);

                                            // send notification to admin
                                            String new_key = myRef.push().getKey();
                                            Date date1 = new Date();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    true, false, false, false, true,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    notification.getAdmin_in_group(), new_key, notification.getMember_id(), user_groups.getAdmin_master(),
                                                    notification.getAdmin_in_group(), user_groups.getGroup_id(), date1.getTime(),
                                                    false, false,
                                                    false, false, false, false, false, false, false, false,
                                                    false, "", "", "", false, "", "", "", false,
                                                    "", "", "", "", "", 0,
                                                    "", "", 0, "", "", "",
                                                    false, false, false, false,
                                                    false, false, false,
                                                    false, false);

                                            assert new_key != null;
                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", notification.getAdmin_in_group());

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            d.dismiss();
                                        });

                                        positiveButton.setOnClickListener(view3 -> {
                                            HashMap<String, Object> map1 = new HashMap<>();
                                            map1.put("moderator", true);
                                            map1.put("moderatorAdding", false);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_following))
                                                    .child(user_id)
                                                    .child(user_groups.getGroup_id())
                                                    .updateChildren(map1);

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(context.getString(R.string.dbname_group_followers))
                                                    .child(user_groups.getGroup_id())
                                                    .child(user_id)
                                                    .updateChildren(map1);

                                            // send notification to admin
                                            String new_key = myRef.push().getKey();
                                            Date date1 = new Date();
                                            HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                                    true, false, false, false, false,
                                                    false,false, false, false,
                                                    false, false, false, false, false, false,
                                                    false,
                                                    false, false, false, false, false,
                                                    false, false,
                                                    true, false, false, true, false,
                                                    false, false,
                                                    false, "", false, false, false, false,
                                                    true,false, false,
                                                    notification.getAdmin_in_group(), new_key, notification.getMember_id(), user_groups.getAdmin_master(),
                                                    notification.getAdmin_in_group(), user_groups.getGroup_id(), date1.getTime(),
                                                    false, false,
                                                    false, false, false, false, false, false, false, false,
                                                    false, "", "", "", false, "", "", "", false,
                                                    "", "", "", "", "", 0,
                                                    "", "", 0, "", "", "",
                                                    false, false, false, false,
                                                    false, false, false,
                                                    false, false);

                                            assert new_key != null;
                                            myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_notification);

                                            // add badge number
                                            HashMap<String, Object> map_number = new HashMap<>();
                                            map_number.put("user_id", notification.getAdmin_in_group());

                                            myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                                    .child(notification.getAdmin_in_group())
                                                    .child(new_key)
                                                    .setValue(map_number);

                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();

                                            // go to admin fragment
                                            if (relLayout_view_overlay != null)
                                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                                            Gson gson = new Gson();
                                            String myJson = gson.toJson(user_groups);
                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                            Intent intent = new Intent(context, GroupViewAdmin.class);
                                            intent.putExtra("user_groups", myJson);
                                            context.startActivity(intent);
                                            context.finish();

                                            d.dismiss();
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // all group followers to show their profile photo
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(group_id);
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        String member = follower.getUser_id();
                        if (!follower.isBanFromGroup())
                            membersList.add(member);

                        // total members
                        t++;
                    }

                    if (t == 0)
                        total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.member_minus)));
                    else
                        total_members.setText(Html.fromHtml("<b>"+ (t+1)+"</b> "+context.getString(R.string.members)));

                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(user_groups.getAdmin_master());

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);


                                Glide.with(context.getApplicationContext())
                                        .load(user.getProfileUrl())
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(member_i);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (!membersList.isEmpty()) {

                        if (membersList.size() == 1) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() == 2) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() == 3) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);
                            relLayout_member_iv.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(2));

                            query_iii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iv);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() == 4) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);
                            relLayout_member_iv.setVisibility(View.VISIBLE);
                            relLayout_member_v.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(2));

                            query_iii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iv);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iv = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(3));

                            query_iv.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_v);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() == 5) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);
                            relLayout_member_iv.setVisibility(View.VISIBLE);
                            relLayout_member_v.setVisibility(View.VISIBLE);
                            relLayout_member_vi.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(2));

                            query_iii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iv);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iv = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(3));

                            query_iv.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_v);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_v = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(4));

                            query_v.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vi);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() == 6) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);
                            relLayout_member_iv.setVisibility(View.VISIBLE);
                            relLayout_member_v.setVisibility(View.VISIBLE);
                            relLayout_member_vi.setVisibility(View.VISIBLE);
                            relLayout_member_vii.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(2));

                            query_iii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iv);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iv = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(3));

                            query_iv.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_v);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_v = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(4));

                            query_v.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vi);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_vi = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(5));

                            query_vi.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() == 7) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);
                            relLayout_member_iv.setVisibility(View.VISIBLE);
                            relLayout_member_v.setVisibility(View.VISIBLE);
                            relLayout_member_vi.setVisibility(View.VISIBLE);
                            relLayout_member_vii.setVisibility(View.VISIBLE);
                            relLayout_member_viii.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(2));

                            query_iii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iv);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iv = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(3));

                            query_iv.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_v);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_v = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(4));

                            query_v.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vi);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_vi = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(5));

                            query_vi.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_vii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(6));

                            query_vii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_viii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (membersList.size() >= 8) {
                            relLayout_member_ii.setVisibility(View.VISIBLE);
                            relLayout_member_iii.setVisibility(View.VISIBLE);
                            relLayout_member_iv.setVisibility(View.VISIBLE);
                            relLayout_member_v.setVisibility(View.VISIBLE);
                            relLayout_member_vi.setVisibility(View.VISIBLE);
                            relLayout_member_vii.setVisibility(View.VISIBLE);
                            relLayout_member_viii.setVisibility(View.VISIBLE);
                            relLayout_member_ix.setVisibility(View.VISIBLE);
                            relLayout_member_x.setVisibility(View.VISIBLE);

                            Query query_i = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(0));

                            query_i.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ii);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_ii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(1));

                            query_ii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(2));

                            query_iii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_iv);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_iv = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(3));

                            query_iv.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_v);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_v = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(4));

                            query_v.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vi);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_vi = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(5));

                            query_vi.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_vii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_vii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(6));

                            query_vii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_viii);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Query query_viii = myRef
                                    .child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(membersList.get(7));

                            query_viii.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        User user = Util_User.getUser(context, objectMap, ds);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(user.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(member_ix);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            see_only_photos.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupOnlyPhotoGradle.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);
            });

            see_only_videos.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupOnlyVideoGradle.class);
                intent.putExtra("user_groups", myGson);
                context.startActivity(intent);
            });
        }

        private void getUserData() {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(admin_master)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        String theme = user_groups.getGroup_theme();
                        title.setText(Html.fromHtml("<b>" + user_groups.getGroup_name() + "</b> >"));

                        if (theme.equals(context.getString(R.string.theme_normal))) {
                            relLayout_invite.setBackgroundResource(R.drawable.button_blue);
                            bouton_rejoindre.setBackgroundResource(R.drawable.button_blue);

                        } else if (theme.equals(context.getString(R.string.theme_blue))) {
                            relLayout_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                            bouton_rejoindre.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));

                        } else if (theme.equals(context.getString(R.string.theme_brown))) {
                            relLayout_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));
                            bouton_rejoindre.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));

                        } else if (theme.equals(context.getString(R.string.theme_pink))) {
                            relLayout_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));
                            bouton_rejoindre.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));

                        } else if (theme.equals(context.getString(R.string.theme_green))) {
                            relLayout_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                            bouton_rejoindre.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));

                        } else if (theme.equals(context.getString(R.string.theme_black))) {
                            relLayout_invite.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));
                            bouton_rejoindre.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));

                        }

                        if (user_groups.isGroup_public())
                            private_public.setText(context.getString(R.string.title_public));
                        else
                            private_public.setText(context.getString(R.string.title_private));

                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);

                        Glide.with(context.getApplicationContext())
                                .load(user_groups.getFull_photo())
                                .preload();

                        profile_photo.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(context, SeeGroupAllProfile.class);
                            intent.putExtra("group_id", user_groups.getGroup_id());
                            context.startActivity(intent);
                        });

                        relLayout_go_about_class.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user_groups);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupAbout.class);
                            intent.putExtra("user_groups", myGson);
                            context.startActivity(intent);
                        });

                        relLayout_invite.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user_groups);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupInviteFromSameTown.class);
                            intent.putExtra("user_groups", myGson);
                            context.startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            isFollowing(user_groups);

            HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(), user_id, "", user_groups.getGroup_id());

            bouton_rejoindre.setOnClickListener(view -> {
                // hide the button in notification "invite user to be member of group"
                Query query1 = myRef.child(context.getString(R.string.dbname_notification))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(user_groups.getGroup_id());
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, ds);

                            if (notification.isInvitation_in_group()) {
                                // show or hide dialog box
                                if (notification.isInvite_to_join_the_group()
                                        && !notification.isAccepted_invitation_to_join_the_group()
                                        && !notification.isRefuse_invitation_to_join_the_group()) {

                                    HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(user_groups.getAdmin_master(),
                                            user_id, notification.getAdding_by(), user_groups.getGroup_id());

                                    // follow the group
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_group_following))
                                            .child(user_id)
                                            .child(user_groups.getGroup_id())
                                            .setValue(map);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_group_followers))
                                            .child(user_groups.getGroup_id())
                                            .child(user_id)
                                            .setValue(map);

                                    setFollowing();

                                    // update current user notification
                                    HashMap<String, Object> user_notification = new HashMap<>();
                                    user_notification.put("invite_to_join_the_group", true);
                                    user_notification.put("accepted_invitation_to_join_the_group", true);
                                    user_notification.put("hide_buttons", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(user_notification);

                                    // send notification to member who invited
                                    Date date = new Date();
                                    String new_key = myRef.push().getKey();
                                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                                            true, false, false, false, false,
                                            false,false, false, false,
                                            false, false, false, false, false, false,
                                            false,
                                            false, false, false, false, false,
                                            false, false,
                                            false, false, false, false, false,
                                            false, false,
                                            true, "", false, true,
                                            false, false,
                                            true,false, false,
                                            notification.getAdding_by(), new_key, user_id, user_groups.getAdmin_master(),
                                            "", user_groups.getGroup_id(), date.getTime(),
                                            false, false,
                                            false, false, false, false, false, false, false, false,
                                            false, "", "", "", false, "", "", "", false,
                                            "", "", "", "", "", 0,
                                            "", "", 0, "", "", "",
                                            false, false, false, false,
                                            false, false, false,
                                            false, false);

                                    assert new_key != null;
                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(notification.getAdding_by())
                                            .child(new_key)
                                            .setValue(map_notification);

                                    // add badge number
                                    HashMap<String, Object> map_number = new HashMap<>();
                                    map_number.put("user_id", notification.getAdding_by());

                                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                                            .child(notification.getAdding_by())
                                            .child(new_key)
                                            .setValue(map_number);

                                    // remove person who has been invited in the list
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                            .child(user_groups.getGroup_id())
                                            .child(user_id)
                                            .removeValue();
                                }
                            } else {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_following))
                                        .child(user_id)
                                        .child(user_groups.getGroup_id())
                                        .setValue(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_followers))
                                        .child(user_groups.getGroup_id())
                                        .child(user_id)
                                        .setValue(map);
                                setFollowing();
                            }
                        }

                        if (!snapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user_id)
                                    .child(user_groups.getGroup_id())
                                    .setValue(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_followers))
                                    .child(user_groups.getGroup_id())
                                    .child(user_id)
                                    .setValue(map);
                            setFollowing();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

            bouton_quitter.setOnClickListener(v -> {
                // show dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = view.findViewById(R.id.dialog_title);
                TextView dialog_text = view.findViewById(R.id.dialog_text);
                TextView negativeButton = view.findViewById(R.id.tv_no);
                TextView positiveButton = view.findViewById(R.id.tv_yes);
                builder.setView(view);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.no));
                positiveButton.setText(context.getString(R.string.yes));

                dialog_title.setVisibility(View.GONE);
                dialog_text.setText(context.getString(R.string.do_you_want_to_leave_this_group));

                negativeButton.setOnClickListener(view1 -> d.dismiss());

                positiveButton.setOnClickListener(view1 -> {
                    d.dismiss();
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id)
                            .child(user_groups.getGroup_id())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(user_groups.getGroup_id())
                            .child(user_id)
                            .removeValue();
                    setUnfollowing();
                });
            });
        }

        private void setFollowing(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            bouton_rejoindre.setVisibility(View.GONE);
            relLayout_member_button.setVisibility(View.VISIBLE);
        }

        private void setUnfollowing(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            bouton_rejoindre.setVisibility(View.VISIBLE);
            relLayout_member_button.setVisibility(View.GONE);
        }

        private void isFollowing(UserGroups user_groups){
            Log.d(TAG, "isFollowing: checking if following this users.");
            setUnfollowing();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(user_groups.getGroup_id());
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

        private void setProfileWidgets(UserSettings userSettings) {
            UserAccountSettings settings = userSettings.getSettings();

            Glide.with(context.getApplicationContext())
                    .load(settings.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo_you);

            relLayout_you.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupProfileMember.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("userID", user_id);
                intent.putExtra("group_id", user_groups.getGroup_id());
                context.startActivity(intent);
            });
        }

        /**
         * Setup the firebase auth object
         */
        private void setupFirebaseAuth(){
            Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference();

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //retrieve user information from the database
                    setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}

