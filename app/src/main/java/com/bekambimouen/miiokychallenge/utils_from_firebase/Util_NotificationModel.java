package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util_NotificationModel {
    public static NotificationModel getNotificationModel(AppCompatActivity context, Map<Object, Object> objectMap, DataSnapshot ds) {
        NotificationModel notification = new NotificationModel();

        notification.setAllReadyOpenItem(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_allReadyOpenItem))).toString()));
        notification.setSuspension(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suspension))).toString()));
        notification.setSuspended(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suspended))).toString()));
        notification.setCancel_suspension(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_cancel_suspension))).toString()));
        notification.setPublicationApprobation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_publicationApprobation))).toString()));
        notification.setNeed_post_approval(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_need_post_approval))).toString()));
        notification.setApproval(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_approval))).toString()));
        notification.setCancel_publicationApprobation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_cancel_publicationApprobation))).toString()));
        notification.setActivityLimitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_activityLimitation))).toString()));
        notification.setPostsActivityLimitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_postsActivityLimitation))).toString()));
        notification.setCommentsActivityLimitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_commentsActivityLimitation))).toString()));
        notification.setPostsActivityLimited(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_postsActivityLimited))).toString()));
        notification.setCommentsActivityLimited(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_commentsActivityLimited))).toString()));
        notification.setCancel_limit_activity(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_cancel_limit_activity))).toString()));
        notification.setBanFromGroup(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_banFromGroup))).toString()));
        notification.setEnter_invitation_admin(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_enter_invitation_admin))).toString()));
        notification.setAdminInvitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_adminInvitation))).toString()));
        notification.setCancel_invitation_admin(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_cancel_invitation_admin))).toString()));
        notification.setEnter_moderation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_enter_moderation))).toString()));
        notification.setAddModerator(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_addModerator))).toString()));
        notification.setCancel_adding_moderator(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_cancel_adding_moderator))).toString()));
        notification.setGroup(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_isGroup))).toString()));
        notification.setMarket(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_isMarket))).toString()));
        notification.setMiioky(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_isMiioky))).toString()));
        notification.setMembership(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_membership))).toString()));
        notification.setAsking_to_join_the_group(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_asking_to_join_the_group))).toString()));
        notification.setApproved_membership(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_approved_membership))).toString()));
        notification.setRefused_membership(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_refused_membership))).toString()));
        notification.setAccept_admin_invitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_accept_admin_invitation))).toString()));
        notification.setRefuse_admin_invitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_refuse_admin_invitation))).toString()));
        notification.setAccept_moderator_invitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_accept_moderator_invitation))).toString()));
        notification.setRefuse_moderator_invitation(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_refuse_moderator_invitation))).toString()));
        notification.setRemove_yourself_like_admin(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_remove_yourself_like_admin))).toString()));
        notification.setRemove_by_another_admin_like_admin(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_remove_by_another_admin_like_admin))).toString()));
        notification.setRemove_yourself_like_moderator(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_remove_yourself_like_moderator))).toString()));
        notification.setRemove_by_another_admin_like_moderator(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_remove_by_another_admin_like_moderator))).toString()));
        notification.setInvitation_in_group(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invitation_in_group))).toString()));
        notification.setInvite_to_join_the_group(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_to_join_the_group))).toString()));
        notification.setAccepted_invitation_to_join_the_group(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_accepted_invitation_to_join_the_group))).toString()));
        notification.setRefuse_invitation_to_join_the_group(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_refuse_invitation_to_join_the_group))).toString()));
        notification.setHide_buttons(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_hide_buttons))).toString()));

        notification.setAdding_by(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_adding_by))).toString()));
        notification.setNotification_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_notification_id))).toString());
        notification.setMember_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_member_id))).toString());
        notification.setAdmin_master(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_master))).toString());
        notification.setAdmin_in_group(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_admin_in_group))).toString());
        notification.setGroup_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_id))).toString());

        // comments
        notification.setComment_from_battle(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_from_battle))).toString()));
        notification.setComment_from_battle_fun(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_from_battle_fun))).toString()));
        notification.setManage_member_in_group(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_manage_member_in_group))).toString()));
        notification.setCategory_of_post(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_category_of_post))).toString());
        notification.setPost_type(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_type))).toString());
        notification.setSimple_comment(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_simple_comment))).toString()));
        notification.setComment_response(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment_response))).toString()));
        notification.setPost_id_field(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_id_field))).toString());
        notification.setPost_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_id))).toString());
        notification.setThumbnail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail))).toString());
        notification.setUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_url))).toString());
        notification.setUserAnswer(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_userAnswer))).toString()));
        notification.setComment(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_comment))).toString()));
        notification.setCommentKey(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_commentKey))).toString()));
        notification.setAnsweringUsername(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_answeringUsername))).toString()));
        notification.setRecyclerview_fieldLike(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_recyclerview_fieldLike))).toString());
        notification.setRecyclerview_photo_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_recyclerview_photo_id))).toString());
        notification.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        notification.setDate_comment_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_comment_created))).toString()));
        notification.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        notification.setPost_share_comment_like(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_post_share_comment_like))).toString()));
        notification.setHas_posted(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_has_posted))).toString()));
        notification.setHas_shared(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_has_shared))).toString()));
        notification.setHas_comment(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_has_comment))).toString()));
        notification.setHas_like(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_has_like))).toString()));
        notification.setFrom_shared_post(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_from_shared_post))).toString()));
        notification.setCircle_group_background_profile(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_circle_group_background_profile))).toString()));
        notification.setCircle_group_cover(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_circle_group_cover))).toString()));
        // response to comment
        notification.setAuthor_comment(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_author_comment))).toString());
        notification.setAuthor_commentKey(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_author_commentKey))).toString());
        notification.setAuthor_comment_date_create(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_author_comment_date_create))).toString()));
        notification.setAuthor_thumbnail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_author_thumbnail))).toString());
        notification.setAuthor_url(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_author_url))).toString());
        notification.setAuthor_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_author_id))).toString());

        // friendship
        notification.setFriendship(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_friendship))).toString()));
        notification.setAccept_friendship(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_accept_friendship))).toString()));
        notification.setRemove_friendship(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_remove_friendship))).toString()));

        // subscription request
        notification.setSubscription_request(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_subscription_request))).toString()));
        notification.setAccept_subscription_request(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_accept_subscription_request))).toString()));

        return notification;
    }
}

