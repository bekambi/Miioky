package com.bekambimouen.miiokychallenge.groups.manage_member.post_approval.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Html;
import android.text.TextUtils;
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
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCommentText;
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
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WaitingApprovalPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ApprovalPostAdapter";

    public static final int GROUP_RECYCLER_ITEM = 3;
    public static final int GROUP_IMAGE_UNE = 4;
    public static final int GROUP_VIDEO_UNE = 5;
    public static final int GROUP_IMAGE_DOUBLE = 6;
    public static final int GROUP_VIDEO_DOUBLE = 7;
    public static final int GROUP_RESPONSE_IMAGE_DOUBLE = 8;
    public static final int GROUP_RESPONSE_VIDEO_DOUBLE = 9;
    // share single bottom
    public static final int USER_PROFILE_SHARE = 10;
    public static final int RECYCLER_ITEM_SHARED = 11;
    public static final int IMAGE_UNE_SHARED = 12;
    public static final int VIDEO_UNE_SHARED = 13;
    public static final int IMAGE_DOUBLE_SHARED = 14;
    public static final int VIDEO_DOUBLE_SHARED = 15;
    public static final int REPONSE_IMG_DOUBLE_SHARED = 16;
    public static final int REPONSE_VID_DOUBLE_SHARED = 17;
    // share top bottom
    public static final int GROUP_SINGLE_TOP_CIRCLE_IMAGE = 18;
    public static final int GROUP_SINGLE_TOP_IMAGE_DOUBLE = 19;
    public static final int GROUP_SINGLE_TOP_IMAGE_UNE = 20;
    public static final int GROUP_SINGLE_TOP_RECYCLER = 21;
    public static final int GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE = 22;
    public static final int GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE = 23;
    public static final int GROUP_SINGLE_TOP_VIDEO_DOUBLE = 24;
    public static final int GROUP_SINGLE_TOP_VIDEO_UNE = 25;
    // comment text
    public static final int GROUP_COMMENT_TEXT = 30;

    // vars
    private final AppCompatActivity context;
    private final List<BattleModel> list;
    private final UserGroups user_groups;
    private final OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    private final ProgressBar progressBar, loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;
    private final List<String> urlList;

    private CustomShareProgressView progresDialog;

    // firebase
    private final FirebaseStorage firebaseStorage;
    private StorageReference finalRef;
    private final DatabaseReference myRef;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.setCancelable(false);
        progresDialog.show();
    }

    public WaitingApprovalPostAdapter(AppCompatActivity context, List<BattleModel> list,
                                      UserGroups user_groups, OnLoadMoreItemsListener mOnLoadMoreItemsListener,
                                      ProgressBar progressBar, ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.user_groups = user_groups;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.progressBar = progressBar;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        firebaseStorage = FirebaseStorage.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        urlList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        boolean isFrom_approval_post = true;
        if (viewType == GROUP_COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_comment_text, parent, false);
            return new GroupCommentText(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_recycler, parent, false);
            return new GroupRecyclerItem(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imageune, parent, false);
            return new GroupImageUne(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videoune, parent, false);
            return new GroupVideoUne(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imagedouble, parent, false);
            return new GroupImageDouble(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videodouble, parent, false);
            return new GroupVideoDouble(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_imagedouble, parent, false);
            return new GroupResponseImageDouble(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_videodouble, parent, false);
            return new GroupResponseVideoDouble(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        }

        // share
        else if (viewType == USER_PROFILE_SHARE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_user_profile, parent, false);
            return new SharedUserProfileViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == RECYCLER_ITEM_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_recycler, parent, false);
            return new SharedRecyclerItemViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune, parent, false);
            return new SharedImageUneViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune, parent, false);
            return new SharedVideoUneViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imagedouble, parent, false);
            return new SharedImageDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videodouble_item, parent, false);
            return new SharedVideoDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_imagedouble, parent, false);
            return new SharedResponseImageDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_videodouble, parent, false);
            return new SharedResponseVideoDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        }


        else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_circle_image, parent, false);
            return new SharedSingleTopCircleImageViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imagedouble, parent, false);
            return new SharedSingleTopImageDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune, parent, false);
            return new SharedSingleTopImageUneViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_recycler, parent, false);
            return new SharedSingleTopRecyclerItemViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_imagedouble, parent, false);
            return new SharedSingleTopResponseImageDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_videodouble, parent, false);
            return new SharedSingleTopResponseVideoDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videodouble_item, parent, false);
            return new SharedSingleTopVideoDoubleViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune, parent, false);
            return new SharedSingleTopVideoUneViewProfile(context, isFrom_approval_post, false, null, relLayout_view_overlay, view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = list.get(position);

        final int itemViewType = getItemViewType(position);

        if (itemViewType == GROUP_COMMENT_TEXT) {
            GroupCommentText commentText = (GroupCommentText) holder;
            commentText.init(model);

        } else if (itemViewType == GROUP_RECYCLER_ITEM) {
            GroupRecyclerItem recyclerItem = (GroupRecyclerItem) holder;
            recyclerItem.init(model);

            recyclerItem.linLayout_possibility_action.setVisibility(View.GONE);
            recyclerItem.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveRecyclerImage(recyclerItem.menu_item, model, recyclerItem.getLayoutPosition());

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

            imageUne.linLayout_possibility_action.setVisibility(View.GONE);
            imageUne.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImageUne(imageUne.menu_item, model, imageUne.getLayoutPosition());


        }  else if (itemViewType == GROUP_VIDEO_UNE) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

            videoUne.linLayout_possibility_action.setVisibility(View.GONE);
            videoUne.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImageUne(videoUne.menu_item, model, videoUne.getLayoutPosition());

        }  else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDouble imageDouble = (GroupImageDouble) holder;
            imageDouble.init(model);

            imageDouble.linLayout_possibility_action.setVisibility(View.GONE);
            imageDouble.relLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImagedouble(imageDouble.menu_item, model, imageDouble.getLayoutPosition());

        }  else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDouble videoDouble = (GroupVideoDouble) holder;
            videoDouble.init(model);

            videoDouble.linLayout_possibility_action.setVisibility(View.GONE);
            videoDouble.relLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImagedouble(videoDouble.menu_item, model, videoDouble.getLayoutPosition());

        } else if (itemViewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            GroupResponseImageDouble groupResponseImageDouble = (GroupResponseImageDouble) holder;
            groupResponseImageDouble.init(model);

            groupResponseImageDouble.linLayout_possibility_action.setVisibility(View.GONE);
            groupResponseImageDouble.relLayout_share_only.setVisibility(View.VISIBLE);

            groupResponseImageDouble.menu_item.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveResponseImagedouble(groupResponseImageDouble.menu_item, model, groupResponseImageDouble.getLayoutPosition());

        } else if (itemViewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            GroupResponseVideoDouble groupResponseVideoDouble = (GroupResponseVideoDouble) holder;
            groupResponseVideoDouble.init(model);

            groupResponseVideoDouble.linLayout_possibility_action.setVisibility(View.GONE);
            groupResponseVideoDouble.relLayout_share_only.setVisibility(View.VISIBLE);

            groupResponseVideoDouble.menu_item.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveResponseImagedouble(groupResponseVideoDouble.menu_item, model, groupResponseVideoDouble.getLayoutPosition());

        }

        // share
        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileViewProfile userProfileDisplay = (SharedUserProfileViewProfile) holder;
            userProfileDisplay.init(model);

            userProfileDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            userProfileDisplay.linLayout_share_only.setVisibility(View.VISIBLE);

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemViewProfile recyclerItemShared = (SharedRecyclerItemViewProfile) holder;
            recyclerItemShared.init(model);

            recyclerItemShared.linLayout_possibility_action.setVisibility(View.GONE);
            recyclerItemShared.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveRecyclerImage(recyclerItemShared.menu_item, model, recyclerItemShared.getLayoutPosition());

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

            imageUneShared.linLayout_possibility_action.setVisibility(View.GONE);
            imageUneShared.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImageUne(imageUneShared.menu_item, model, imageUneShared.getLayoutPosition());

        }  else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

            videoUneShared.linLayout_possibility_action.setVisibility(View.GONE);
            videoUneShared.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImageUne(videoUneShared.menu_item, model, videoUneShared.getLayoutPosition());

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleViewProfile imageDoubleShared = (SharedImageDoubleViewProfile) holder;
            imageDoubleShared.init(model);

            imageDoubleShared.linLayout_possibility_action.setVisibility(View.GONE);
            imageDoubleShared.relLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImagedouble(imageDoubleShared.menu_item, model, imageDoubleShared.getLayoutPosition());

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleViewProfile videoDoubleShared = (SharedVideoDoubleViewProfile) holder;
            videoDoubleShared.init(model);

            videoDoubleShared.linLayout_possibility_action.setVisibility(View.GONE);
            videoDoubleShared.relLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImagedouble(videoDoubleShared.menu_item, model, videoDoubleShared.getLayoutPosition());

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleViewProfile responseImageDoubleShared = (SharedResponseImageDoubleViewProfile) holder;
            responseImageDoubleShared.init(model);

            responseImageDoubleShared.linLayout_possibility_action.setVisibility(View.GONE);
            responseImageDoubleShared.relLayout_share_only.setVisibility(View.VISIBLE);

            responseImageDoubleShared.menu_item.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveResponseImagedouble(responseImageDoubleShared.menu_item, model, responseImageDoubleShared.getLayoutPosition());

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleViewProfile responseVideoDoubleShared = (SharedResponseVideoDoubleViewProfile) holder;
            responseVideoDoubleShared.init(model);

            responseVideoDoubleShared.linLayout_possibility_action.setVisibility(View.GONE);
            responseVideoDoubleShared.relLayout_share_only.setVisibility(View.VISIBLE);

            responseVideoDoubleShared.menu_item.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveResponseImagedouble(responseVideoDoubleShared.menu_item, model, responseVideoDoubleShared.getLayoutPosition());

        }

        else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageViewProfile singleTopCircleImageDisplay = (SharedSingleTopCircleImageViewProfile) holder;
            singleTopCircleImageDisplay.init(model);

            singleTopCircleImageDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopCircleImageDisplay.linLayout_share_only.setVisibility(View.VISIBLE);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleViewProfile singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleViewProfile) holder;
            singleTopImageDoubleDisplay.init(model);

            singleTopImageDoubleDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopImageDoubleDisplay.relLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImagedouble(singleTopImageDoubleDisplay.menu_item, model, singleTopImageDoubleDisplay.getLayoutPosition());

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

            singleTopImageUneDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopImageUneDisplay.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImageUne(singleTopImageUneDisplay.menu_item, model, singleTopImageUneDisplay.getLayoutPosition());

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemViewProfile singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemViewProfile) holder;
            singleTopRecyclerItemDisplay.init(model);

            singleTopRecyclerItemDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopRecyclerItemDisplay.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveRecyclerImage(singleTopRecyclerItemDisplay.menu_item, model, singleTopRecyclerItemDisplay.getLayoutPosition());

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleViewProfile singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleViewProfile) holder;
            singleTopResponseImageDoubleDisplay.init(model);

            singleTopResponseImageDoubleDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopResponseImageDoubleDisplay.relLayout_share_only.setVisibility(View.VISIBLE);

            singleTopResponseImageDoubleDisplay.menu_item.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveResponseImagedouble(singleTopResponseImageDoubleDisplay.menu_item, model, singleTopResponseImageDoubleDisplay.getLayoutPosition());

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleViewProfile singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleViewProfile) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

            singleTopResponseVideoDoubleDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopResponseVideoDoubleDisplay.relLayout_share_only.setVisibility(View.VISIBLE);

            singleTopResponseVideoDoubleDisplay.menu_item.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveResponseImagedouble(singleTopResponseVideoDoubleDisplay.menu_item, model, singleTopResponseVideoDoubleDisplay.getLayoutPosition());

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleViewProfile singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleViewProfile) holder;
            singleTopVideoDoubleDisplay.init(model);

            singleTopVideoDoubleDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopVideoDoubleDisplay.relLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImagedouble(singleTopVideoDoubleDisplay.menu_item, model, singleTopVideoDoubleDisplay.getLayoutPosition());

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

            singleTopVideoUneDisplay.linLayout_possibility_action.setVisibility(View.GONE);
            singleTopVideoUneDisplay.linLayout_share_only.setVisibility(View.VISIBLE);

            // delete or approve post
            deleteOrApproveImageUne(singleTopVideoUneDisplay.menu_item, model, singleTopVideoUneDisplay.getLayoutPosition());

        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    // delete or approve image recycler
    private void deleteOrApproveRecyclerImage(ImageView menu_item, BattleModel model, int position) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_approval_post);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
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
                    Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(inset);
                    d.show();

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        if (user_groups != null) {
                            //showLoading();
                            if (!TextUtils.isEmpty(model.getUrli()))
                                urlList.add(model.getUrli());
                            if (!TextUtils.isEmpty(model.getUrlii()))
                                urlList.add(model.getUrlii());
                            if (!TextUtils.isEmpty(model.getUrliii()))
                                urlList.add(model.getUrliii());
                            if (!TextUtils.isEmpty(model.getUrliv()))
                                urlList.add(model.getUrliv());
                            if (!TextUtils.isEmpty(model.getUrlv()))
                                urlList.add(model.getUrlv());
                            if (!TextUtils.isEmpty(model.getUrlvi()))
                                urlList.add(model.getUrlvi());
                            if (!TextUtils.isEmpty(model.getUrlvii()))
                                urlList.add(model.getUrlvii());
                            if (!TextUtils.isEmpty(model.getUrlviii()))
                                urlList.add(model.getUrlviii());
                            if (!TextUtils.isEmpty(model.getUrlix()))
                                urlList.add(model.getUrlix());
                            if (!TextUtils.isEmpty(model.getUrlx()))
                                urlList.add(model.getUrlx());
                            if (!TextUtils.isEmpty(model.getUrlxi()))
                                urlList.add(model.getUrlxi());
                            if (!TextUtils.isEmpty(model.getUrlxii()))
                                urlList.add(model.getUrlxii());
                            if (!TextUtils.isEmpty(model.getUrlxiii()))
                                urlList.add(model.getUrlxiii());
                            if (!TextUtils.isEmpty(model.getUrlxiv()))
                                urlList.add(model.getUrlxiv());
                            if (!TextUtils.isEmpty(model.getUrlxv()))
                                urlList.add(model.getUrlxv());
                            if (!TextUtils.isEmpty(model.getUrlxvi()))
                                urlList.add(model.getUrlxvi());
                            if (!TextUtils.isEmpty(model.getUrlxvii()))
                                urlList.add(model.getUrlxvii());


                            if (urlList.size() == 1) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                finalRef = photoRef1;

                            } else if (urlList.size() == 2) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());

                                photoRef1.delete();
                                finalRef = photoRef2;

                            } else if (urlList.size() == 3) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());

                                photoRef1.delete();
                                photoRef2.delete();
                                finalRef = photoRef3;

                            } else if (urlList.size() == 4) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                finalRef = photoRef4;

                            } else if (urlList.size() == 5) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                finalRef = photoRef5;

                            } else if (urlList.size() == 6) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                finalRef = photoRef6;

                            } else if (urlList.size() == 7) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                finalRef = photoRef7;

                            } else if (urlList.size() == 8) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                finalRef = photoRef8;

                            } else if (urlList.size() == 9) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                finalRef = photoRef9;

                            } else if (urlList.size() == 10) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                finalRef = photoRef10;

                            } else if (urlList.size() == 11) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                finalRef = photoRef11;

                            } else if (urlList.size() == 12) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());
                                StorageReference photoRef12 = firebaseStorage.getReferenceFromUrl(model.getUrlxii());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                photoRef11.delete();
                                finalRef = photoRef12;

                            } else if (urlList.size() == 13) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());
                                StorageReference photoRef12 = firebaseStorage.getReferenceFromUrl(model.getUrlxii());
                                StorageReference photoRef13 = firebaseStorage.getReferenceFromUrl(model.getUrlxiii());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                photoRef11.delete();
                                photoRef12.delete();
                                finalRef = photoRef13;

                            } else if (urlList.size() == 14) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());
                                StorageReference photoRef12 = firebaseStorage.getReferenceFromUrl(model.getUrlxii());
                                StorageReference photoRef13 = firebaseStorage.getReferenceFromUrl(model.getUrlxiii());
                                StorageReference photoRef14 = firebaseStorage.getReferenceFromUrl(model.getUrlxiv());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                photoRef11.delete();
                                photoRef12.delete();
                                photoRef13.delete();
                                finalRef = photoRef14;

                            } else if (urlList.size() == 15) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());
                                StorageReference photoRef12 = firebaseStorage.getReferenceFromUrl(model.getUrlxii());
                                StorageReference photoRef13 = firebaseStorage.getReferenceFromUrl(model.getUrlxiii());
                                StorageReference photoRef14 = firebaseStorage.getReferenceFromUrl(model.getUrlxiv());
                                StorageReference photoRef15 = firebaseStorage.getReferenceFromUrl(model.getUrlxv());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                photoRef11.delete();
                                photoRef12.delete();
                                photoRef13.delete();
                                photoRef14.delete();
                                finalRef = photoRef15;

                            } else if (urlList.size() == 16) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());
                                StorageReference photoRef12 = firebaseStorage.getReferenceFromUrl(model.getUrlxii());
                                StorageReference photoRef13 = firebaseStorage.getReferenceFromUrl(model.getUrlxiii());
                                StorageReference photoRef14 = firebaseStorage.getReferenceFromUrl(model.getUrlxiv());
                                StorageReference photoRef15 = firebaseStorage.getReferenceFromUrl(model.getUrlxv());
                                StorageReference photoRef16 = firebaseStorage.getReferenceFromUrl(model.getUrlxvi());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                photoRef11.delete();
                                photoRef12.delete();
                                photoRef13.delete();
                                photoRef14.delete();
                                photoRef15.delete();
                                finalRef = photoRef16;

                            } else if (urlList.size() == 17) {
                                StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrli());
                                StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlii());
                                StorageReference photoRef3 = firebaseStorage.getReferenceFromUrl(model.getUrliii());
                                StorageReference photoRef4 = firebaseStorage.getReferenceFromUrl(model.getUrliv());
                                StorageReference photoRef5 = firebaseStorage.getReferenceFromUrl(model.getUrlv());
                                StorageReference photoRef6 = firebaseStorage.getReferenceFromUrl(model.getUrlvi());
                                StorageReference photoRef7 = firebaseStorage.getReferenceFromUrl(model.getUrlvii());
                                StorageReference photoRef8 = firebaseStorage.getReferenceFromUrl(model.getUrlviii());
                                StorageReference photoRef9 = firebaseStorage.getReferenceFromUrl(model.getUrlix());
                                StorageReference photoRef10 = firebaseStorage.getReferenceFromUrl(model.getUrlx());
                                StorageReference photoRef11 = firebaseStorage.getReferenceFromUrl(model.getUrlxi());
                                StorageReference photoRef12 = firebaseStorage.getReferenceFromUrl(model.getUrlxii());
                                StorageReference photoRef13 = firebaseStorage.getReferenceFromUrl(model.getUrlxiii());
                                StorageReference photoRef14 = firebaseStorage.getReferenceFromUrl(model.getUrlxiv());
                                StorageReference photoRef15 = firebaseStorage.getReferenceFromUrl(model.getUrlxv());
                                StorageReference photoRef16 = firebaseStorage.getReferenceFromUrl(model.getUrlxvi());
                                StorageReference photoRef17 = firebaseStorage.getReferenceFromUrl(model.getUrlxvii());

                                photoRef1.delete();
                                photoRef2.delete();
                                photoRef3.delete();
                                photoRef4.delete();
                                photoRef5.delete();
                                photoRef6.delete();
                                photoRef7.delete();
                                photoRef8.delete();
                                photoRef9.delete();
                                photoRef10.delete();
                                photoRef11.delete();
                                photoRef12.delete();
                                photoRef13.delete();
                                photoRef14.delete();
                                photoRef15.delete();
                                photoRef16.delete();
                                finalRef = photoRef17;
                            }

                            finalRef.delete().addOnSuccessListener(aVoid -> {
                                myRef.child(context.getString(R.string.dbname_group_waiting_for_approval))
                                        .child(user_groups.getGroup_id())
                                        .child(model.getPhotoi_id())
                                        .removeValue();

                                myRef.child(context.getString(R.string.dbname_group_Media_waiting_for_approval))
                                        .child(model.getPhotoi_id())
                                        .removeValue().addOnSuccessListener(unused1 -> {
                                            removeAt(position);
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        }).addOnFailureListener(e -> {
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_LONG).show();
                                        });

                            }).addOnFailureListener(e -> {
                                progresDialog.dismiss();
                                Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_LONG).show();
                            });
                        }
                        d.dismiss();
                    });
                }
                if (item.getItemId() == R.id.menu_approval_post) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_approval_post)+
                            " <b>" +user_groups.getGroup_name()+"</b>"));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        showLoading();

                        post( model, model.getPhotoi_id(), position);

                        d.dismiss();
                    });
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    // delete or approve image une
    private void deleteOrApproveImageUne(ImageView menu_item, BattleModel model, int position) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_approval_post);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        if (user_groups != null) {
                            showLoading();
                            StorageReference photoRef = firebaseStorage.getReferenceFromUrl(model.getUrl());

                            photoRef.delete().addOnSuccessListener(aVoid -> {
                                myRef.child(context.getString(R.string.dbname_group_waiting_for_approval))
                                        .child(user_groups.getGroup_id())
                                        .child(model.getPhoto_id())
                                        .removeValue();

                                myRef.child(context.getString(R.string.dbname_group_Media_waiting_for_approval))
                                        .child(model.getPhoto_id())
                                        .removeValue().addOnSuccessListener(unused1 -> {
                                            removeAt(position);
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });
                            });
                        }
                        d.dismiss();
                    });
                }
                if (item.getItemId() == R.id.menu_approval_post) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_approval_post)+
                            " <b>" +user_groups.getGroup_name()+"</b>"));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        showLoading();

                        post( model, model.getPhoto_id(), position);

                        d.dismiss();
                    });
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    // delete or approve image double
    private void deleteOrApproveImagedouble(ImageView menu_item, BattleModel model, int position) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_approval_post);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        if (user_groups != null) {
                            showLoading();
                            StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getUrlUn());
                            StorageReference photoRef2 = firebaseStorage.getReferenceFromUrl(model.getUrlDeux());

                            photoRef2.delete();
                            photoRef1.delete().addOnSuccessListener(aVoid -> {
                                myRef.child(context.getString(R.string.dbname_group_waiting_for_approval))
                                        .child(user_groups.getGroup_id())
                                        .child(model.getPhoto_id_un())
                                        .removeValue();

                                myRef.child(context.getString(R.string.dbname_group_Media_waiting_for_approval))
                                        .child(model.getPhoto_id_un())
                                        .removeValue().addOnSuccessListener(unused1 -> {
                                            removeAt(position);
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });
                            });
                        }
                        d.dismiss();
                    });
                }
                if (item.getItemId() == R.id.menu_approval_post) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_approval_post)+
                            " <b>" +user_groups.getGroup_name()+"</b>"));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        showLoading();

                        post( model, model.getPhoto_id_un(), position);

                        d.dismiss();
                    });
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    // delete or approve image response double
    private void deleteOrApproveResponseImagedouble(ImageView menu_item, BattleModel model, int position) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_approval_post);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.really_want_delete_one_publication)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        if (user_groups != null) {
                            showLoading();
                            StorageReference photoRef1 = firebaseStorage.getReferenceFromUrl(model.getReponse_url());

                            photoRef1.delete().addOnSuccessListener(aVoid -> {
                                myRef.child(context.getString(R.string.dbname_group_waiting_for_approval))
                                        .child(user_groups.getGroup_id())
                                        .child(model.getReponse_photoID())
                                        .removeValue();

                                myRef.child(context.getString(R.string.dbname_group_Media_waiting_for_approval))
                                        .child(model.getReponse_photoID())
                                        .removeValue().addOnSuccessListener(unused1 -> {
                                            removeAt(position);
                                            progresDialog.dismiss();
                                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                        });
                            });
                        }
                        d.dismiss();
                    });
                }
                if (item.getItemId() == R.id.menu_approval_post) {
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

                    negativeButton.setText(context.getString(R.string.cancel));
                    positiveButton.setText(context.getString(R.string.ok));

                    dialog_title.setVisibility(View.GONE);
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_approval_post)+
                            " <b>" +user_groups.getGroup_name()+"</b>"));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        showLoading();

                        post( model, model.getReponse_photoID(), position);

                        d.dismiss();
                    });
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });
    }

    // approve member post
    private void post(BattleModel battleModel, String photo_id, int position) {
        Date date=new Date();

        BattleModel model = new BattleModel();

        model.setSuppressed(false);
        model.setRecycler_story(false);
        model.setFriends_suggestion_one(false);
        model.setFriends_suggestion_two(false);
        model.setFriends_suggestion_three(false);
        model.setFriends_suggestion_four(false);
        model.setFriends_suggestion_five(false);
        model.setGroups_suggestion_one(false);
        model.setGroups_suggestion_two(false);
        model.setGroups_suggestion_three(false);
        model.setGroups_suggestion_four(false);
        model.setGroups_suggestion_five(false);
        model.setVideos_suggestion_one(false);
        model.setVideos_suggestion_two(false);
        model.setVideos_suggestion_three(false);
        model.setVideos_suggestion_four(false);
        model.setVideos_suggestion_five(false);
        model.setEveryone_can_answer_this_challenge(false);
        model.setDetails("");

        model.setRecyclerItem(battleModel.isRecyclerItem());
        model.setImageUne(battleModel.isImageUne());
        model.setVideoUne(battleModel.isVideoUne());
        model.setImageDouble(battleModel.isImageDouble());
        model.setVideoDouble(battleModel.isVideoDouble());
        model.setReponseImageDouble(battleModel.isReponseImageDouble());
        model.setReponseVideoDouble(battleModel.isReponseVideoDouble());
        // pour le grid profile
        model.setGridRecyclerImage(battleModel.isGridRecyclerImage());

        model.setReponse_deja(battleModel.isReponse_deja());

        // group
        model.setGroup(battleModel.isGroup());
        model.setGroup_private(battleModel.isGroup_private());
        model.setGroup_public(battleModel.isGroup_public());
        model.setGroup_cover_profile_photo(battleModel.isGroup_cover_profile_photo());
        model.setGroup_recyclerItem(battleModel.isGroup_recyclerItem());
        model.setGroup_imageUne(battleModel.isGroup_imageUne());
        model.setGroup_videoUne(battleModel.isGroup_videoUne());
        model.setGroup_cover_background_profile_photo(battleModel.isGroup_cover_background_profile_photo());
        model.setGroup_imageDouble(battleModel.isGroup_imageDouble());
        model.setGroup_videoDouble(battleModel.isGroup_videoDouble());
        model.setGroup_response_imageDouble(battleModel.isGroup_response_imageDouble());
        model.setGroup_response_videoDouble(battleModel.isGroup_response_videoDouble());

        model.setGroup_share_single_bottom_image_double(battleModel.isGroup_share_single_bottom_image_double());
        model.setGroup_share_single_bottom_image_une(battleModel.isGroup_share_single_bottom_image_une());
        model.setGroup_share_single_bottom_recycler(battleModel.isGroup_share_single_bottom_recycler());
        model.setGroup_share_single_bottom_response_image_double(battleModel.isGroup_share_single_bottom_response_image_double());
        model.setGroup_share_single_bottom_response_video_double(battleModel.isGroup_share_single_bottom_response_video_double());
        model.setGroup_share_single_bottom_video_double(battleModel.isGroup_share_single_bottom_video_double());
        model.setGroup_share_single_bottom_video_une(battleModel.isGroup_share_single_bottom_video_une());

        model.setGroup_share_single_top_image_double(battleModel.isGroup_share_single_top_image_double());
        model.setGroup_share_single_top_image_une(battleModel.isGroup_share_single_top_image_une());
        model.setGroup_share_single_top_recycler(battleModel.isGroup_share_single_top_recycler());
        model.setGroup_share_single_top_response_image_double(battleModel.isGroup_share_single_top_response_image_double());
        model.setGroup_share_single_top_response_video_double(battleModel.isGroup_share_single_top_response_video_double());
        model.setGroup_share_single_top_video_double(battleModel.isGroup_share_single_top_video_double());
        model.setGroup_share_single_top_video_une(battleModel.isGroup_share_single_top_video_une());

        model.setGroup_share_top_bottom_image_double(battleModel.isGroup_share_top_bottom_image_double());
        model.setGroup_share_top_bottom_image_une(battleModel.isGroup_share_top_bottom_image_une());
        model.setGroup_share_top_bottom_recycler(battleModel.isGroup_share_top_bottom_recycler());
        model.setGroup_share_top_bottom_response_image_double(battleModel.isGroup_share_top_bottom_response_image_double());
        model.setGroup_share_top_bottom_response_video_double(battleModel.isGroup_share_top_bottom_response_video_double());
        model.setGroup_share_top_bottom_video_double(battleModel.isGroup_share_top_bottom_video_double());
        model.setGroup_share_top_bottom_video_une(battleModel.isGroup_share_top_bottom_video_une());

        model.setUser_profile_shared(battleModel.isUser_profile_shared());
        model.setGroup_share_single_bottom_circle_image(battleModel.isGroup_share_single_bottom_circle_image());
        model.setGroup_share_single_top_circle_image(battleModel.isGroup_share_single_top_circle_image());
        model.setGroup_share_top_bottom_circle_image(battleModel.isGroup_share_top_bottom_circle_image());

        model.setUser_profile_photo(battleModel.getUser_profile_photo());
        model.setUser_full_profile_photo(battleModel.getUser_full_profile_photo());
        model.setDate_shared(battleModel.getDate_shared());

        model.setAdmin_master(battleModel.getAdmin_master());
        model.setGroup_id(battleModel.getGroup_id());
        model.setSharing_admin_master(battleModel.getSharing_admin_master());
        model.setShared_group_id(battleModel.getShared_group_id());
        model.setPublisher(battleModel.getPublisher());
        model.setGroup_profile_photo(battleModel.getGroup_profile_photo());
        model.setGroup_full_profile_photo(battleModel.getGroup_full_profile_photo());
        model.setGroup_user_background_profile_url(battleModel.getGroup_user_background_profile_url());
        model.setGroup_user_background_full_profile_url(battleModel.getGroup_user_background_full_profile_url());

        model.setComment_text(battleModel.isComment_text());
        model.setComment_subject(battleModel.getComment_subject());
        model.setComment_theme(battleModel.getComment_theme());
        model.setBig_image(battleModel.isBig_image());

        model.setCaption(battleModel.getCaption());
        model.setUrl(battleModel.getUrl());
        model.setDate_created(date.getTime());
        model.setPhoto_id(battleModel.getPhoto_id());
        model.setUser_id(battleModel.getUser_id());
        model.setTags(battleModel.getTags());

        model.setCaptionUn(battleModel.getCaptionUn());
        model.setTagsUn(battleModel.getTagsUn());
        model.setTagsDeux(battleModel.getTagsDeux());
        model.setUrlUn(battleModel.getUrlUn());
        model.setUrlDeux(battleModel.getUrlDeux());
        model.setPhoto_id_un(battleModel.getPhoto_id_un());
        model.setPhoto_id_deux(battleModel.getPhoto_id_deux());

        model.setUrli(battleModel.getUrli());
        model.setUrlii(battleModel.getUrlii());
        model.setUrliii(battleModel.getUrliii());
        model.setUrliv(battleModel.getUrliv());
        model.setUrlv(battleModel.getUrlv());
        model.setUrlvi(battleModel.getUrlvi());
        model.setUrlvii(battleModel.getUrlvii());
        model.setUrlviii(battleModel.getUrlviii());
        model.setUrlix(battleModel.getUrlix());
        model.setUrlx(battleModel.getUrlx());
        model.setUrlxi(battleModel.getUrlxi());
        model.setUrlxii(battleModel.getUrlxii());
        model.setUrlxiii(battleModel.getUrlxiii());
        model.setUrlxiv(battleModel.getUrlxiv());
        model.setUrlxv(battleModel.getUrlxv());
        model.setUrlxvi(battleModel.getUrlxvi());
        model.setUrlxvii(battleModel.getUrlxvii());

        model.setPhotoi_id(battleModel.getPhotoi_id());
        model.setPhotoii_id(battleModel.getPhotoii_id());
        model.setPhotoiii_id(battleModel.getPhotoiii_id());
        model.setPhotoiv_id(battleModel.getPhotoiv_id());
        model.setPhotov_id(battleModel.getPhotov_id());
        model.setPhotovi_id(battleModel.getPhotovi_id());
        model.setPhotovii_id(battleModel.getPhotovii_id());
        model.setPhotoviii_id(battleModel.getPhotoviii_id());
        model.setPhotoix_id(battleModel.getPhotoix_id());
        model.setPhotox_id(battleModel.getPhotox_id());
        model.setPhotoxi_id(battleModel.getPhotoxi_id());
        model.setPhotoxii_id(battleModel.getPhotoxii_id());
        model.setPhotoxiii_id(battleModel.getPhotoxiii_id());
        model.setPhotoxiv_id(battleModel.getPhotoxiv_id());
        model.setPhotoxv_id(battleModel.getPhotoxv_id());
        model.setPhotoxvi_id(battleModel.getPhotoxvi_id());
        model.setPhotoxvii_id(battleModel.getPhotoxvii_id());

        model.setCaption_i(battleModel.getCaption_i());
        model.setCaption_ii(battleModel.getCaption_ii());
        model.setCaption_iii(battleModel.getCaption_iii());
        model.setCaption_iv(battleModel.getCaption_iv());
        model.setCaption_v(battleModel.getCaption_v());
        model.setCaption_vi(battleModel.getCaption_vi());
        model.setCaption_vii(battleModel.getCaption_vii());
        model.setCaption_viii(battleModel.getCaption_viii());
        model.setCaption_ix(battleModel.getCaption_ix());
        model.setCaption_x(battleModel.getCaption_x());
        model.setCaption_xi(battleModel.getCaption_xi());
        model.setCaption_xii(battleModel.getCaption_xii());
        model.setCaption_xiii(battleModel.getCaption_xiii());
        model.setCaption_xiv(battleModel.getCaption_xiv());
        model.setCaption_xv(battleModel.getCaption_xv());
        model.setCaption_xvi(battleModel.getCaption_xvi());
        model.setCaption_xvii(battleModel.getCaption_xvii());

        model.setThumbnail_un(battleModel.getThumbnail_un());
        model.setThumbnail_deux(battleModel.getThumbnail_deux());
        model.setThumbnail(battleModel.getThumbnail());
        model.setThumbnail_invite(battleModel.getThumbnail_invite());
        model.setThumbnail_response(battleModel.getThumbnail_response());

        // challenge
        model.setInvite_url(battleModel.getInvite_url());
        model.setInvite_photoID(battleModel.getInvite_photoID());
        model.setInvite_username(battleModel.getInvite_username());
        model.setInvite_userID(battleModel.getInvite_userID());
        model.setInvite_caption(battleModel.getInvite_caption());
        model.setInvite_tag(battleModel.getInvite_tag());
        model.setInvite_category(battleModel.getInvite_category());
        model.setInvite_profile_photo(battleModel.getInvite_profile_photo());

        model.setReponse_profile_photo(battleModel.getReponse_profile_photo());
        model.setReponse_username(battleModel.getReponse_username());
        model.setReponse_user_id(battleModel.getReponse_user_id());
        model.setReponse_url(battleModel.getReponse_url());
        model.setReponse_photoID(battleModel.getReponse_photoID());
        model.setReponse_caption(battleModel.getReponse_caption());
        model.setReponse_tag(battleModel.getReponse_tag());

        // challenge and flag
        model.setInvite_country_name(battleModel.getInvite_country_name());
        model.setInvite_countryID(battleModel.getInvite_countryID());
        model.setReponse_country_name(battleModel.getReponse_country_name());
        model.setReponse_countryID(battleModel.getReponse_countryID());
        model.setCountry_name(battleModel.getCountry_name());
        model.setCountryID(battleModel.getCountryID());
        model.setInvite_category_status(battleModel.getInvite_category_status());
        model.setSharing_caption(battleModel.getSharing_caption());

        // republication of publication
        model.setImageDouble_shared(battleModel.isImageDouble_shared());
        model.setImageUne_shared(battleModel.isImageUne_shared());
        model.setRecyclerItem_shared(battleModel.isRecyclerItem_shared());
        model.setReponseImageDouble_shared(battleModel.isReponseImageDouble_shared());
        model.setReponseVideoDouble_shared(battleModel.isReponseVideoDouble_shared());
        model.setVideoDouble_shared(battleModel.isVideoDouble_shared());
        model.setVideoUne_shared(battleModel.isVideoUne_shared());
        model.setUser_id_sharing(battleModel.getUser_id_sharing());

        // for saved
        model.setUsername("");
        model.setProfileUrl("");
        model.setAuth_user_id("");
        model.setUser_id_shared("");

        // post identity
        String post_key = myRef.push().getKey();
        model.setPost_identity(post_key);

        // download to database
        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group))
                .child(battleModel.getGroup_id())
                .child(photo_id)
                .setValue(model);

        FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_group_media))
                .child(photo_id)
                .setValue(model).addOnCompleteListener(task -> {

                    // send notifaication to member
                    String new_key = myRef.push().getKey();
                    Date date1 = new Date();
                    HashMap<Object, Object> map_notification = Utils_Map_Notification.setNotification(
                            true, false, false, false, false,
                            true,false, true, false,
                            false, false, false, false, false, false,
                            false,
                            false, false, false, false, false,
                            false, false,
                            false, false, false, false, false,
                            false, false,
                            false, "", false, false, false, false,
                            true,false, false,
                            battleModel.getUser_id(), new_key, battleModel.getUser_id(), user_groups.getAdmin_master(), "", user_groups.getGroup_id(), date1.getTime(),
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
                            .child(battleModel.getUser_id())
                            .child(new_key)
                            .setValue(map_notification);
                    // add badge number
                    HashMap<String, Object> map_number = new HashMap<>();
                    map_number.put("user_id", battleModel.getUser_id());

                    myRef.child(context.getString(R.string.dbname_badge_notification_number))
                            .child(battleModel.getUser_id())
                            .child(new_key)
                            .setValue(map_number);
                    // ____________________________________________________________________________________

                    // first delete post from waiting approval fragment
                    myRef.child(context.getString(R.string.dbname_group_waiting_for_approval))
                            .child(user_groups.getGroup_id())
                            .child(photo_id)
                            .removeValue();

                    myRef.child(context.getString(R.string.dbname_group_Media_waiting_for_approval))
                            .child(photo_id)
                            .removeValue().addOnSuccessListener(unused1 -> {

                                removeAt(position);
                                progresDialog.dismiss();
                                Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                            });
                });
    }

    private boolean reachedEndOfList(int position){
        return position == list.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
            loading_progressBar.setVisibility(View.GONE);
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
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
        if (list.get(position).isComment_text())
            return GROUP_COMMENT_TEXT;
        if (list.get(position).isGroup_recyclerItem())
            return GROUP_RECYCLER_ITEM;
        else if (list.get(position).isGroup_imageUne())
            return GROUP_IMAGE_UNE;
        else if (list.get(position).isGroup_videoUne())
            return GROUP_VIDEO_UNE;
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
        else  if (list.get(position).isGroup_share_single_bottom_image_une())
            return IMAGE_UNE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_recycler())
            return RECYCLER_ITEM_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_response_image_double())
            return REPONSE_IMG_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_response_video_double())
            return REPONSE_VID_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_video_double())
            return VIDEO_DOUBLE_SHARED;
        else  if (list.get(position).isGroup_share_single_bottom_video_une())
            return VIDEO_UNE_SHARED;

        else  if (list.get(position).isGroup_share_top_bottom_circle_image())
            return GROUP_SINGLE_TOP_CIRCLE_IMAGE;
        else  if (list.get(position).isGroup_share_top_bottom_image_double())
            return GROUP_SINGLE_TOP_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_image_une())
            return GROUP_SINGLE_TOP_IMAGE_UNE;
        else  if (list.get(position).isGroup_share_top_bottom_recycler())
            return GROUP_SINGLE_TOP_RECYCLER;
        else  if (list.get(position).isGroup_share_top_bottom_response_image_double())
            return GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_response_video_double())
            return GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE;
        else  if (list.get(position).isGroup_share_top_bottom_video_double())
            return GROUP_SINGLE_TOP_VIDEO_DOUBLE;
        else
            return GROUP_SINGLE_TOP_VIDEO_UNE;
    }
}

