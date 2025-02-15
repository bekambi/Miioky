package com.bekambimouen.miiokychallenge.groups.manage_member.report_post.adapter;

import android.app.Dialog;
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
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
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
import com.bekambimouen.miiokychallenge.groups.manage_member.report_post.model.ReportReasonModel;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_ReportReasonModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ReportPostAdapter";

    public static final int GROUP_COVER_IMAGE = 1;
    public static final int GROUP_COVER_BACK_PROFILE = 2;
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

    private boolean isFrom_approval_post = true;
    private CustomShareProgressView progressDialog;

    private void showLoading(){
        if (progressDialog == null)
            progressDialog = new CustomShareProgressView(context);
        progressDialog.show();
    }

    // firebase
    private final DatabaseReference myRef;

    public ReportPostAdapter(AppCompatActivity context, List<BattleModel> list,
                             UserGroups user_groups, OnLoadMoreItemsListener mOnLoadMoreItemsListener,
                             ProgressBar progressBar, ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.user_groups = user_groups;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.progressBar = progressBar;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == GROUP_COMMENT_TEXT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_comment_text, parent, false);
            return new GroupCommentText(context, false, false, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_photo, parent, false);
            return new GroupCoverImage(context, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_COVER_BACK_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_cover_photo, parent, false);
            return new GroupCoverBackgroundProfile(context, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RECYCLER_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_recycler, parent, false);
            return new GroupRecyclerItem(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imageune, parent, false);
            return new GroupImageUne(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videoune, parent, false);
            return new GroupVideoUne(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_imagedouble, parent, false);
            return new GroupImageDouble(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_videodouble, parent, false);
            return new GroupVideoDouble(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_imagedouble, parent, false);
            return new GroupResponseImageDouble(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_profile_reponse_videodouble, parent, false);
            return new GroupResponseVideoDouble(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        }

        // share
        else if (viewType == USER_PROFILE_SHARE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_user_profile, parent, false);
            return new SharedUserProfileViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == RECYCLER_ITEM_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_recycler, parent, false);
            return new SharedRecyclerItemViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imageune, parent, false);
            return new SharedImageUneViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_UNE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videoune, parent, false);
            return new SharedVideoUneViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == IMAGE_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_imagedouble, parent, false);
            return new SharedImageDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_videodouble_item, parent, false);
            return new SharedVideoDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_imagedouble, parent, false);
            return new SharedResponseImageDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE_SHARED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_shared_reponse_videodouble, parent, false);
            return new SharedResponseVideoDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        }

        else if (viewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_circle_image, parent, false);
            return new SharedSingleTopCircleImageViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imagedouble, parent, false);
            return new SharedSingleTopImageDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_IMAGE_UNE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_imageune, parent, false);
            return new SharedSingleTopImageUneViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RECYCLER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_recycler, parent, false);
            return new SharedSingleTopRecyclerItemViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_imagedouble, parent, false);
            return new SharedSingleTopResponseImageDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_reponse_videodouble, parent, false);
            return new SharedSingleTopResponseVideoDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videodouble_item, parent, false);
            return new SharedSingleTopVideoDoubleViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shared_single_top_videoune, parent, false);
            return new SharedSingleTopVideoUneViewProfile(context, isFrom_approval_post, true, null, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = list.get(position);

        final int itemViewType = getItemViewType(position);

        if (itemViewType == GROUP_COMMENT_TEXT) {
            GroupCommentText commentText = (GroupCommentText) holder;
            commentText.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id(), commentText.menu_item, commentText);

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupCoverImage coverImage = (GroupCoverImage) holder;
            coverImage.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id(), coverImage.menu_item, coverImage);

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupCoverBackgroundProfile coverBackgroundProfile = (GroupCoverBackgroundProfile) holder;
            coverBackgroundProfile.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id(), coverBackgroundProfile.menu_item, coverBackgroundProfile);

        } else if (itemViewType == GROUP_RECYCLER_ITEM) {
            GroupRecyclerItem recyclerItem = (GroupRecyclerItem) holder;
            recyclerItem.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhotoi_id(), recyclerItem.menu_item, recyclerItem);

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id(), imageUne.menu_item, imageUne);


        }  else if (itemViewType == GROUP_VIDEO_UNE) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id(), videoUne.menu_item, videoUne);

        }  else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDouble imageDouble = (GroupImageDouble) holder;
            imageDouble.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id_un(), imageDouble.menu_item, imageDouble);

        }  else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDouble videoDouble = (GroupVideoDouble) holder;
            videoDouble.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getPhoto_id_un(), videoDouble.menu_item, videoDouble);

        } else if (itemViewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            GroupResponseImageDouble groupResponseImageDouble = (GroupResponseImageDouble) holder;
            groupResponseImageDouble.init(model);

            groupResponseImageDouble.menu_item.setVisibility(View.VISIBLE);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getReponse_photoID(), groupResponseImageDouble.menu_item, groupResponseImageDouble);

        } else if (itemViewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            GroupResponseVideoDouble groupResponseVideoDouble = (GroupResponseVideoDouble) holder;
            groupResponseVideoDouble.init(model);

            groupResponseVideoDouble.menu_item.setVisibility(View.VISIBLE);

            // delete or ignore report post
            deleteOrIgnoreReportPost(model.getReponse_photoID(), groupResponseVideoDouble.menu_item, groupResponseVideoDouble);

        }

        // share
        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileViewProfile userProfileDisplay = (SharedUserProfileViewProfile) holder;
            userProfileDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id(),/*shared post*/ model.getPhoto_id(), userProfileDisplay.menu_item, userProfileDisplay);

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemViewProfile recyclerItemShared = (SharedRecyclerItemViewProfile) holder;
            recyclerItemShared.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhotoi_id(),/*shared post*/ model.getPhoto_id(), recyclerItemShared.menu_item, recyclerItemShared);

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id(),/*shared post*/ model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        }  else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id(),/*shared post*/ model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleViewProfile imageDoubleShared = (SharedImageDoubleViewProfile) holder;
            imageDoubleShared.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id_un(),/*shared post*/ model.getPhoto_id(), imageDoubleShared.menu_item, imageDoubleShared);

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleViewProfile videoDoubleShared = (SharedVideoDoubleViewProfile) holder;
            videoDoubleShared.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id_un(),/*shared post*/ model.getPhoto_id(), videoDoubleShared.menu_item, videoDoubleShared);

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleViewProfile responseImageDoubleShared = (SharedResponseImageDoubleViewProfile) holder;
            responseImageDoubleShared.init(model);

            responseImageDoubleShared.menu_item.setVisibility(View.VISIBLE);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getReponse_photoID(),/*shared post*/ model.getPhoto_id(), responseImageDoubleShared.menu_item, responseImageDoubleShared);

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleViewProfile responseVideoDoubleShared = (SharedResponseVideoDoubleViewProfile) holder;
            responseVideoDoubleShared.init(model);

            responseVideoDoubleShared.menu_item.setVisibility(View.VISIBLE);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getReponse_photoID(),/*shared post*/ model.getPhoto_id(), responseVideoDoubleShared.menu_item, responseVideoDoubleShared);

        }

        else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageViewProfile singleTopCircleImageDisplay = (SharedSingleTopCircleImageViewProfile) holder;
            singleTopCircleImageDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id(),/*shared post*/ model.getPhoto_id(), singleTopCircleImageDisplay.menu_item, singleTopCircleImageDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleViewProfile singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleViewProfile) holder;
            singleTopImageDoubleDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id_un(),/*shared post*/ model.getPhoto_id(), singleTopImageDoubleDisplay.menu_item, singleTopImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id(),/*shared post*/ model.getPhoto_id(), singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemViewProfile singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemViewProfile) holder;
            singleTopRecyclerItemDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhotoi_id(),/*shared post*/ model.getPhoto_id(), singleTopRecyclerItemDisplay.menu_item, singleTopRecyclerItemDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleViewProfile singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleViewProfile) holder;
            singleTopResponseImageDoubleDisplay.init(model);

            singleTopResponseImageDoubleDisplay.menu_item.setVisibility(View.VISIBLE);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getReponse_photoID(),/*shared post*/ model.getPhoto_id(), singleTopResponseImageDoubleDisplay.menu_item, singleTopResponseImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleViewProfile singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleViewProfile) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

            singleTopResponseVideoDoubleDisplay.menu_item.setVisibility(View.VISIBLE);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getReponse_photoID(),/*shared post*/ model.getPhoto_id(), singleTopResponseVideoDoubleDisplay.menu_item, singleTopResponseVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleViewProfile singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleViewProfile) holder;
            singleTopVideoDoubleDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id_un(),/*shared post*/ model.getPhoto_id(), singleTopVideoDoubleDisplay.menu_item, singleTopVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

            // delete or ignore report post
            deleteOrIgnoreReportSharedPost(model.getPhoto_id(),/*shared post*/ model.getPhoto_id(), singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay);

        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    // delete or approve post
    private void deleteOrIgnoreReportPost(String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_report_post);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_report_detail) {
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

                    negativeButton.setVisibility(View.GONE);
                    positiveButton.setText(context.getString(R.string.ok));

                    Query query = myRef
                            .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                            .orderByChild(context.getString(R.string.field_photo_id))
                            .equalTo(photo_id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                ReportReasonModel reasonModel = Util_ReportReasonModel.getReportReasonModel(context, objectMap);

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.details)));
                                dialog_text.setText(Html.fromHtml(
                                        context.getString(R.string.this_post_has_been_report)
                                                +" <b>"+reasonModel.getNumber_of_report() +"</b> "
                                                +context.getString(R.string.time)+" "+context.getString(R.string.for_the_following_reason)
                                                +" <b>"+reasonModel.getReason_of_report()+"</b>."));

                                positiveButton.setOnClickListener(view2 -> d.dismiss());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (item.getItemId() == R.id.menu_ignore) {
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

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.ignore_report)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_really_want_to_ignore_this_report)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        showLoading();

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .child(photo_id)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group_report_post))
                                .child(user_groups.getGroup_id())
                                .child(photo_id)
                                .removeValue().addOnSuccessListener(unused1 -> {
                                    removeAt(holder.getLayoutPosition());
                                    progressDialog.dismiss();
                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                });

                        d.dismiss();
                    });
                }
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
                    positiveButton.setText(context.getString(R.string.delete));

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_post_title)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_delete_this_post)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        d.dismiss();

                        showLoading();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("suppressed", true);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_media))
                                .child(photo_id)
                                .updateChildren(map);

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(user_groups.getGroup_id())
                                .child(photo_id)
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .child(photo_id)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group_report_post))
                                .child(user_groups.getGroup_id())
                                .child(photo_id)
                                .removeValue().addOnSuccessListener(unused1 -> {
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
    }

    // delete or approve shared post
    private void deleteOrIgnoreReportSharedPost(String photo_id, String photo_id_shared, ImageView menu_item, RecyclerView.ViewHolder holder) {
        menu_item.setOnClickListener(view -> {
            // prevent double click
            Util.preventTwoClick(view);

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, menu_item);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_report_post);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_report_detail) {
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

                    negativeButton.setVisibility(View.GONE);
                    positiveButton.setText(context.getString(R.string.ok));

                    Query query = myRef
                            .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                            .orderByChild(context.getString(R.string.field_photo_id))
                            .equalTo(photo_id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                ReportReasonModel reasonModel = Util_ReportReasonModel.getReportReasonModel(context, objectMap);

                                dialog_title.setText(Html.fromHtml(context.getString(R.string.details)));
                                dialog_text.setText(Html.fromHtml(
                                        context.getString(R.string.this_post_has_been_report)
                                                +" <b>"+reasonModel.getNumber_of_report() +"</b> "
                                                +context.getString(R.string.time)+" "+context.getString(R.string.for_the_following_reason)
                                                +" <b>"+reasonModel.getReason_of_report()+"</b>."));

                                positiveButton.setOnClickListener(view2 -> d.dismiss());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if (item.getItemId() == R.id.menu_ignore) {
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

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.ignore_report)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_really_want_to_ignore_this_report)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        showLoading();

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .child(photo_id)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group_report_post))
                                .child(user_groups.getGroup_id())
                                .child(photo_id)
                                .removeValue().addOnSuccessListener(unused1 -> {
                                    removeAt(holder.getLayoutPosition());
                                    progressDialog.dismiss();
                                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                });

                        d.dismiss();
                    });
                }
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
                    positiveButton.setText(context.getString(R.string.delete));

                    dialog_title.setText(Html.fromHtml(context.getString(R.string.delete_post_title)));
                    dialog_text.setText(Html.fromHtml(context.getString(R.string.you_are_about_to_delete_this_post)));

                    negativeButton.setOnClickListener(view2 -> d.dismiss());

                    positiveButton.setOnClickListener(view3 -> {
                        d.dismiss();

                        showLoading();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("suppressed", true);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_media_share))
                                .child(photo_id_shared)
                                .updateChildren(map);

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(user_groups.getGroup_id())
                                .child(photo_id_shared)
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .child(photo_id)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group_report_post))
                                .child(user_groups.getGroup_id())
                                .child(photo_id)
                                .removeValue().addOnSuccessListener(unused1 -> {
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
        else if (list.get(position).isGroup_cover_profile_photo())
            return GROUP_COVER_IMAGE;
        else if (list.get(position).isGroup_cover_background_profile_photo())
            return GROUP_COVER_BACK_PROFILE;
        else if (list.get(position).isGroup_recyclerItem())
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
        else if (list.get(position).isGroup_share_top_bottom_video_une())
            return GROUP_SINGLE_TOP_VIDEO_UNE;
        else
            return -1;
    }
}

