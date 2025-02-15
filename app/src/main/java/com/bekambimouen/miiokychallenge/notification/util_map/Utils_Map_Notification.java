package com.bekambimouen.miiokychallenge.notification.util_map;

import java.util.HashMap;

public class Utils_Map_Notification {
    public static HashMap<Object, Object> setNotification(boolean manage_member_in_group, boolean allReadyOpenItem, boolean suspension, boolean suspended, boolean cancel_suspension,
                                                          boolean publicationApprobation, boolean need_post_approval, boolean approval, boolean cancel_publicationApprobation,
                                                          boolean activityLimitation, boolean postsActivityLimitation, boolean commentsActivityLimitation,
                                                          boolean postsActivityLimited, boolean commentsActivityLimited, boolean cancel_limit_activity,
                                                          boolean banFromGroup,
                                                          boolean enter_invitation_admin, boolean adminInvitation, boolean cancel_invitation_admin,
                                                          boolean accept_admin_invitation, boolean refuse_admin_invitation,
                                                          boolean remove_yourself_like_admin, boolean remove_by_another_admin_like_admin,
                                                          boolean enter_moderation, boolean addModerator, boolean cancel_adding_moderator,
                                                          boolean accept_moderator_invitation, boolean refuse_moderator_invitation,
                                                          boolean remove_yourself_like_moderator, boolean remove_by_another_admin_like_moderator,
                                                          boolean invitation_in_group, String adding_by, boolean invite_to_join_the_group,
                                                          boolean accepted_invitation_to_join_the_group, boolean refuse_invitation_to_join_the_group, boolean hide_buttons,
                                                          boolean isGroup, boolean isMarket, boolean isMiioky,
                                                          String user_id, String notification_id, String member_id, String admin_master,
                                                          String admin_in_group, String group_id, long date_created,
                                                          boolean comment_from_battle, boolean comment_from_battle_fun,
                                                          boolean post_share_comment_like, boolean from_shared_post, boolean has_posted, boolean has_shared, boolean has_comment, boolean has_like, boolean circle_group_cover, boolean circle_group_background_profile,
                                                          boolean simple_comment, String post_id_field, String category_of_post, String post_type, boolean comment_response, String post_id,
                                                          String thumbnail, String url, boolean userAnswer, String comment, String commentKey,
                                                          String answeringUsername, String recyclerview_photo_id, String recyclerview_fieldLike,
                                                          long date_comment_created,
                                                          String author_comment, String author_commentKey, long author_comment_date_create, String author_thumbnail, String author_url, String author_id,
                                                          boolean membership, boolean asking_to_join_the_group, boolean approved_membership, boolean refused_membership,
                                                          boolean friendship, boolean accept_friendship, boolean remove_friendship,
                                                          boolean subscription_request, boolean accept_subscription_request) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("allReadyOpenItem", allReadyOpenItem);
        map.put("suspension", suspension);
        map.put("suspended", suspended);
        map.put("cancel_suspension", cancel_suspension);
        map.put("publicationApprobation", publicationApprobation);
        map.put("need_post_approval", need_post_approval);
        map.put("approval", approval);
        map.put("cancel_publicationApprobation", cancel_publicationApprobation);
        map.put("activityLimitation", activityLimitation);
        map.put("postsActivityLimitation", postsActivityLimitation);
        map.put("commentsActivityLimitation", commentsActivityLimitation);
        map.put("postsActivityLimited", postsActivityLimited);
        map.put("commentsActivityLimited", commentsActivityLimited);
        map.put("cancel_limit_activity", cancel_limit_activity);
        map.put("banFromGroup", banFromGroup);
        map.put("enter_invitation_admin", enter_invitation_admin);
        map.put("adminInvitation", adminInvitation);
        map.put("cancel_invitation_admin", cancel_invitation_admin);
        map.put("enter_moderation", enter_moderation);
        map.put("addModerator", addModerator);
        map.put("cancel_adding_moderator", cancel_adding_moderator);
        map.put("accept_admin_invitation", accept_admin_invitation);
        map.put("refuse_admin_invitation", refuse_admin_invitation);
        map.put("remove_yourself_like_admin", remove_yourself_like_admin);
        map.put("remove_by_another_admin_like_admin", remove_by_another_admin_like_admin);
        map.put("accept_moderator_invitation", accept_moderator_invitation);
        map.put("refuse_moderator_invitation", refuse_moderator_invitation);
        map.put("remove_yourself_like_moderator", remove_yourself_like_moderator);
        map.put("remove_by_another_admin_like_moderator", remove_by_another_admin_like_moderator);
        map.put("invitation_in_group", invitation_in_group);
        map.put("adding_by", adding_by);
        map.put("invite_to_join_the_group", invite_to_join_the_group);
        map.put("accepted_invitation_to_join_the_group", accepted_invitation_to_join_the_group);
        map.put("refuse_invitation_to_join_the_group", refuse_invitation_to_join_the_group);
        map.put("hide_buttons", hide_buttons);
        map.put("manage_member_in_group", manage_member_in_group);
        map.put("comment_from_battle", comment_from_battle);
        map.put("comment_from_battle_fun", comment_from_battle_fun);
        map.put("post_share_comment_like", post_share_comment_like);
        map.put("from_shared_post", from_shared_post);
        map.put("circle_group_background_profile", circle_group_background_profile);
        map.put("circle_group_cover", circle_group_cover);
        map.put("has_posted", has_posted);
        map.put("has_shared", has_shared);
        map.put("has_comment", has_comment);
        map.put("has_like", has_like);
        map.put("isGroup", isGroup);
        map.put("isMarket", isMarket);
        map.put("isMiioky", isMiioky);
        map.put("user_id", user_id);
        map.put("notification_id", notification_id);
        map.put("admin_master", admin_master);
        map.put("member_id", member_id);
        map.put("admin_in_group", admin_in_group);
        map.put("group_id", group_id);
        map.put("date_created", date_created);
        // comment
        map.put("category_of_post", category_of_post);
        map.put("post_type", post_type);
        map.put("post_id_field", post_id_field);
        map.put("simple_comment", simple_comment);
        map.put("comment_response", comment_response);
        map.put("post_id", post_id);
        map.put("thumbnail", thumbnail);
        map.put("url", url);
        map.put("userAnswer", userAnswer);
        map.put("comment", comment);
        map.put("commentKey", commentKey);
        map.put("answeringUsername", answeringUsername);
        map.put("recyclerview_photo_id", recyclerview_photo_id);
        map.put("recyclerview_fieldLike", recyclerview_fieldLike);
        map.put("date_comment_created", date_comment_created);
        map.put("author_comment", author_comment);
        map.put("author_commentKey", author_commentKey);
        map.put("author_comment_date_create", author_comment_date_create);
        map.put("author_thumbnail", author_thumbnail);
        map.put("author_url", author_url);
        map.put("author_id", author_id);
        // membership
        map.put("membership", membership);
        map.put("asking_to_join_the_group", asking_to_join_the_group);
        map.put("approved_membership", approved_membership);
        map.put("refused_membership", refused_membership);
        // friendship
        map.put("friendship", friendship);
        map.put("accept_friendship", accept_friendship);
        map.put("remove_friendship", remove_friendship);
        // subscription request
        map.put("subscription_request", subscription_request);
        map.put("accept_subscription_request", accept_subscription_request);

        return map;
    }
}

