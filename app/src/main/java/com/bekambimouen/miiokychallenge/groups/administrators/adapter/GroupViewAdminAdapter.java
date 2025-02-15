package com.bekambimouen.miiokychallenge.groups.administrators.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
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
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.groups.GroupAbout;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCommentText;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupManege;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupResponseImageDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupResponseVideoDouble;
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
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.invite_user_in_group.GroupInviteFromSameTown;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupImageDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupVideoDouble;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCoverBackgroundProfile;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupCoverImage;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupImageUne;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupRecyclerItem;
import com.bekambimouen.miiokychallenge.groups.adapter.viewholder.GroupVideoUne;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.GroupOnlyPhotoGradle;
import com.bekambimouen.miiokychallenge.groups.photos_videos_only.GroupOnlyVideoGradle;
import com.bekambimouen.miiokychallenge.groups.publication.GroupCreateNewGroup;
import com.bekambimouen.miiokychallenge.groups.see_member_profile.SeeGroupAllProfile;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
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
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupViewAdminAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "GroupViewAdminAdapter";

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

    public GroupViewAdminAdapter(AppCompatActivity context, List<BattleModel> list,
                                 String admin_master, String group_id, UserGroups user_groups, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.admin_master = admin_master;
        this.group_id = group_id;
        this.user_groups = user_groups;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        this.user_id = Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_header_group_view_admin_profile, parent, false);
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
        if (holder.getItemViewType() == HEADER) {
            RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader) holder;

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(admin_master)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        UserGroups user_groups = new UserGroups();

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        user_groups.setGroup_theme(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_theme))).toString());

                        String theme = user_groups.getGroup_theme();

                        if (theme.equals(context.getString(R.string.theme_normal)))
                            recyclerViewHeader.parent.setBackgroundResource(R.drawable.toolbar_blue_clear_background);

                        else if (theme.equals(context.getString(R.string.theme_blue)))
                            recyclerViewHeader.parent.setBackgroundResource(R.drawable.toolbar_blue_clear_background);

                        else if (theme.equals(context.getString(R.string.theme_brown)))
                            recyclerViewHeader.parent.setBackgroundResource(R.drawable.toolbar_brown_clear_background);

                        else if (theme.equals(context.getString(R.string.theme_pink)))
                            recyclerViewHeader.parent.setBackgroundResource(R.drawable.toolbar_pink_clear_background);

                        else if (theme.equals(context.getString(R.string.theme_green)))
                            recyclerViewHeader.parent.setBackgroundResource(R.drawable.toolbar_green_clear_background);

                        else if (theme.equals(context.getString(R.string.theme_black)))
                            recyclerViewHeader.parent.setBackgroundResource(R.drawable.toolbar_black_clear_background);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (itemViewType == GROUP_COMMENT_TEXT) {
            GroupCommentText commentText = (GroupCommentText) holder;
            commentText.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), commentText.menu_item, commentText);

        } else if (itemViewType == GROUP_COVER_IMAGE) {
            GroupCoverImage coverImage = (GroupCoverImage) holder;
            coverImage.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), coverImage.menu_item, coverImage);

        } else if (itemViewType == GROUP_COVER_BACK_PROFILE) {
            GroupCoverBackgroundProfile coverBackgroundProfile = (GroupCoverBackgroundProfile) holder;
            coverBackgroundProfile.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), coverBackgroundProfile.menu_item, coverBackgroundProfile);

        } else if (itemViewType == GROUP_RECYCLER_ITEM) {
            GroupRecyclerItem recyclerItem = (GroupRecyclerItem) holder;
            recyclerItem.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhotoi_id(), recyclerItem.menu_item, recyclerItem);

        } else if (itemViewType == GROUP_IMAGE_UNE) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), imageUne.menu_item, imageUne);

        } else if (itemViewType == GROUP_IMAGE_UNE_BIG_IMG) {
            GroupImageUne imageUne = (GroupImageUne) holder;
            imageUne.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), imageUne.menu_item, imageUne);

        } else if (itemViewType == GROUP_VIDEO_UNE) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), videoUne.menu_item, videoUne);

        } else if (itemViewType == GROUP_VIDEO_UNE_BIG_IMG) {
            GroupVideoUne videoUne = (GroupVideoUne) holder;
            videoUne.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id(), videoUne.menu_item, videoUne);

        } else if (itemViewType == GROUP_IMAGE_DOUBLE) {
            GroupImageDouble imageDouble = (GroupImageDouble) holder;
            imageDouble.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getPhoto_id_un(), imageDouble.menu_item, imageDouble);

        } else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDouble videoDouble = (GroupVideoDouble) holder;
            videoDouble.init(model);

            // delete post from group share if current user is administrator or moderator
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

            // delete post from group share if current user is administrator or moderator
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

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroup(model, model.getReponse_photoID(), groupResponseVideoDoubleDisplay.menu_item, groupResponseVideoDoubleDisplay);

        }

        // share
        else if (itemViewType == USER_PROFILE_SHARE) {
            SharedUserProfileViewProfile userProfileDisplay = (SharedUserProfileViewProfile) holder;
            userProfileDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), userProfileDisplay.menu_item, userProfileDisplay);

        } else if (itemViewType == RECYCLER_ITEM_SHARED) {
            SharedRecyclerItemViewProfile recyclerItemShared = (SharedRecyclerItemViewProfile) holder;
            recyclerItemShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), recyclerItemShared.menu_item, recyclerItemShared);

        } else if (itemViewType == IMAGE_UNE_SHARED) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        } else if (itemViewType == IMAGE_UNE_SHARED_BIG_IMG) {
            SharedImageUneViewProfile imageUneShared = (SharedImageUneViewProfile) holder;
            imageUneShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), imageUneShared.menu_item, imageUneShared);

        } else if (itemViewType == VIDEO_UNE_SHARED) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == VIDEO_UNE_SHARED_BIG_IMG) {
            SharedVideoUneViewProfile videoUneShared = (SharedVideoUneViewProfile) holder;
            videoUneShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), videoUneShared.menu_item, videoUneShared);

        } else if (itemViewType == IMAGE_DOUBLE_SHARED){
            SharedImageDoubleViewProfile imageDoubleShared = (SharedImageDoubleViewProfile) holder;
            imageDoubleShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), imageDoubleShared.menu_item, imageDoubleShared);

        } else if (itemViewType == VIDEO_DOUBLE_SHARED) {
            SharedVideoDoubleViewProfile videoDoubleShared = (SharedVideoDoubleViewProfile) holder;
            videoDoubleShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), videoDoubleShared.menu_item, videoDoubleShared);

        } else if (itemViewType == REPONSE_IMG_DOUBLE_SHARED) {
            SharedResponseImageDoubleViewProfile responseImageDoubleShared = (SharedResponseImageDoubleViewProfile) holder;
            responseImageDoubleShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), responseImageDoubleShared.menu_item, responseImageDoubleShared);

        } else if (itemViewType == REPONSE_VID_DOUBLE_SHARED) {
            SharedResponseVideoDoubleViewProfile responseVideoDoubleShared = (SharedResponseVideoDoubleViewProfile) holder;
            responseVideoDoubleShared.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), responseVideoDoubleShared.menu_item, responseVideoDoubleShared);

        }

        else if (itemViewType == GROUP_SINGLE_TOP_CIRCLE_IMAGE) {
            SharedSingleTopCircleImageViewProfile singleTopCircleImageDisplay = (SharedSingleTopCircleImageViewProfile) holder;
            singleTopCircleImageDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopCircleImageDisplay.menu_item, singleTopCircleImageDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_DOUBLE) {
            SharedSingleTopImageDoubleViewProfile singleTopImageDoubleDisplay = (SharedSingleTopImageDoubleViewProfile) holder;
            singleTopImageDoubleDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopImageDoubleDisplay.menu_item, singleTopImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_IMAGE_UNE_BIG_IMG) {
            SharedSingleTopImageUneViewProfile singleTopImageUneDisplay = (SharedSingleTopImageUneViewProfile) holder;
            singleTopImageUneDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopImageUneDisplay.menu_item, singleTopImageUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RECYCLER) {
            SharedSingleTopRecyclerItemViewProfile singleTopRecyclerItemDisplay = (SharedSingleTopRecyclerItemViewProfile) holder;
            singleTopRecyclerItemDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopRecyclerItemDisplay.menu_item, singleTopRecyclerItemDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_IMAGE_DOUBLE) {
            SharedSingleTopResponseImageDoubleViewProfile singleTopResponseImageDoubleDisplay = (SharedSingleTopResponseImageDoubleViewProfile) holder;
            singleTopResponseImageDoubleDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopResponseImageDoubleDisplay.menu_item, singleTopResponseImageDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_RESPONSE_VIDEO_DOUBLE) {
            SharedSingleTopResponseVideoDoubleViewProfile singleTopResponseVideoDoubleDisplay = (SharedSingleTopResponseVideoDoubleViewProfile) holder;
            singleTopResponseVideoDoubleDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopResponseVideoDoubleDisplay.menu_item, singleTopResponseVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_DOUBLE) {
            SharedSingleTopVideoDoubleViewProfile singleTopVideoDoubleDisplay = (SharedSingleTopVideoDoubleViewProfile) holder;
            singleTopVideoDoubleDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopVideoDoubleDisplay.menu_item, singleTopVideoDoubleDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay);

        } else if (itemViewType == GROUP_SINGLE_TOP_VIDEO_UNE_BIG_IMG) {
            SharedSingleTopVideoUneViewProfile singleTopVideoUneDisplay = (SharedSingleTopVideoUneViewProfile) holder;
            singleTopVideoUneDisplay.init(model);

            // delete post from group share if current user is administrator or moderator
            deletePostFromGroupShare(model, model.getPhoto_id(), singleTopVideoUneDisplay.menu_item, singleTopVideoUneDisplay);

        }
    }

    // delete post from group if current user is administrator or moderator
    private void deletePostFromGroup(BattleModel model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        // delete post if current user is administrator or moderator
        if (model.getAdmin_master().equals(user_id)) {
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
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(model.getGroup_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        if (follower.isAdminInGroup() || follower.isModerator()) {
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

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // delete post from group share if current user is administrator or moderator
    private void deletePostFromGroupShare(BattleModel model, String photo_id, ImageView menu_item, RecyclerView.ViewHolder holder) {
        if (model.getAdmin_master().equals(user_id)) {
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
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(model.getGroup_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        if (follower.isAdminInGroup() || follower.isModerator()) {
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

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
        return -1;
    }

    public class RecyclerViewHeader extends RecyclerView.ViewHolder {
        private final static String TAG = "RecyclerViewHeader";

        // widgets
        private final RelativeLayout parent;
        private final ImageView profile_photo;
        private final TextView title, private_public, total_members;
        private final CircleImageView profile_photo_you;
        private final RelativeLayout relLayout_you, relLayout_go_about_class, relLayout_invite,
                relLayout_manage, see_only_photos, see_only_videos;
        private final LinearLayout linLayout_update_photo;
        // memebes profile photo
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

            parent = itemView.findViewById(R.id.parent);
            linLayout_update_photo = itemView.findViewById(R.id.linLayout_update_photo);
            relLayout_manage = itemView.findViewById(R.id.relLayout_manage);
            total_members = itemView.findViewById(R.id.total_members);
            relLayout_invite = itemView.findViewById(R.id.relLayout_invite);
            relLayout_you = itemView.findViewById(R.id.relLayout_you);
            relLayout_go_about_class = itemView.findViewById(R.id.relLayout_go_about_class);
            see_only_photos = itemView.findViewById(R.id.see_only_photos);
            see_only_videos = itemView.findViewById(R.id.see_only_videos);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            profile_photo_you = itemView.findViewById(R.id.profile_photo_you);
            title = itemView.findViewById(R.id.title);
            private_public = itemView.findViewById(R.id.private_public);

            // profile photo members
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

            getTotalMembers();
            getUserData();
            setupFirebaseAuth();
        }

        private void getTotalMembers() {
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

                                if (!context.isFinishing()) {
                                    Glide.with(context.getApplicationContext())
                                            .load(user.getProfileUrl())
                                            .placeholder(R.drawable.ic_baseline_person_24)
                                            .into(member_i);
                                }

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
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        String theme = user_groups.getGroup_theme();
                        title.setText(Html.fromHtml("<b>"+user_groups.getGroup_name()+"</b> >"));

                        if (theme.equals(context.getString(R.string.theme_normal)))
                            relLayout_manage.setBackgroundResource(R.drawable.button_blue);
                        else if (theme.equals(context.getString(R.string.theme_blue)))
                            relLayout_manage.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
                        else if (theme.equals(context.getString(R.string.theme_brown)))
                            relLayout_manage.setBackground(ContextCompat.getDrawable(context, R.drawable.button_brown));
                        else if (theme.equals(context.getString(R.string.theme_pink)))
                            relLayout_manage.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pink));
                        else if (theme.equals(context.getString(R.string.theme_green)))
                            relLayout_manage.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
                        else if (theme.equals(context.getString(R.string.theme_black)))
                            relLayout_manage.setBackground(ContextCompat.getDrawable(context, R.drawable.button_black));

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
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
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
                            intent.putExtra("from_admin", "from_admin");
                            context.startActivity(intent);
                        });

                        // user_id = admin_user_id, is current user id
                        linLayout_update_photo.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupCreateNewGroup.class);

                            Gson gson = new Gson();
                            String myGson = gson.toJson(user_groups);
                            intent.putExtra("user_groups", myGson);
                            context.startActivity(intent);
                        });

                        relLayout_manage.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String myGson = gson.toJson(user_groups);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupManege.class);
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
                Intent intent = new Intent(context, GroupProfileAdmin.class);
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

