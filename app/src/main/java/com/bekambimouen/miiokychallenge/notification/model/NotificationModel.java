package com.bekambimouen.miiokychallenge.notification.model;

public class NotificationModel {
    private boolean allReadyOpenItem;

    private boolean manage_member_in_group;
    private boolean suspension;
    private boolean suspended;
    private boolean cancel_suspension;

    private boolean publicationApprobation;
    private boolean need_post_approval;
    private boolean approval;
    private boolean cancel_publicationApprobation;

    private boolean activityLimitation;
    private boolean postsActivityLimitation;
    private boolean commentsActivityLimitation;
    private boolean postsActivityLimited;
    private boolean commentsActivityLimited;
    private boolean cancel_limit_activity;

    private boolean banFromGroup;

    private boolean enter_invitation_admin;
    private boolean adminInvitation;
    private boolean cancel_invitation_admin;

    private boolean enter_moderation;
    private boolean addModerator;
    private boolean cancel_adding_moderator;

    private boolean invitation_in_group;
    private boolean invite_to_join_the_group;
    private boolean accepted_invitation_to_join_the_group;
    private boolean refuse_invitation_to_join_the_group;
    private boolean hide_buttons;
    private boolean isGroup;
    private boolean isMarket;
    private boolean isMiioky;

    // accept or refuse invitation to become admin or moderator
    private boolean accept_admin_invitation;
    private boolean refuse_admin_invitation;
    private boolean accept_moderator_invitation;
    private boolean refuse_moderator_invitation;
    private boolean remove_yourself_like_admin;
    private boolean remove_by_another_admin_like_admin;
    private boolean remove_yourself_like_moderator;
    private boolean remove_by_another_admin_like_moderator;

    private boolean post_share_comment_like;
    private boolean has_posted;
    private boolean has_shared;
    private boolean has_comment;
    private boolean has_like;
    private boolean from_shared_post;
    private boolean circle_group_background_profile;
    private boolean circle_group_cover;

    // comment
    // difference between the origin of comment
    private boolean comment_from_battle;
    private boolean comment_from_battle_fun;
    private String category_of_post;
    private String post_type;
    private boolean comment_response;
    private boolean simple_comment;
    private String post_id;
    private String post_id_field;
    private String thumbnail;
    private String url;
    private boolean userAnswer;
    private String comment;
    private String commentKey;
    private String answeringUsername;
    private String user_id;
    private String recyclerview_photo_id;
    private String recyclerview_fieldLike;
    private long date_comment_created;
    private long date_created;

    // the answer of user his comment
    private String author_comment;
    private String author_commentKey;
    private long author_comment_date_create;
    private String author_thumbnail;
    private String author_url;
    private String author_id;

    private String adding_by;
    private String notification_id;
    private String member_id;
    private String admin_master;
    private String admin_in_group;
    private String group_id;

    // memebership
    private boolean membership;
    private boolean asking_to_join_the_group;
    private boolean approved_membership;
    private boolean refused_membership;

    // friendship
    private boolean friendship;
    private boolean accept_friendship;
    private boolean remove_friendship;

    // subscriber request
    private boolean subscription_request;
    private boolean accept_subscription_request;

    public NotificationModel() {
    }

    public NotificationModel(boolean allReadyOpenItem, boolean suspension, boolean suspended, boolean cancel_suspension,
                             boolean publicationApprobation, boolean need_post_approval, boolean approval, boolean cancel_publicationApprobation,
                             boolean activityLimitation, boolean postsActivityLimitation, boolean commentsActivityLimitation,
                             boolean postsActivityLimited, boolean commentsActivityLimited,
                             boolean cancel_limit_activity, boolean banFromGroup,
                             boolean enter_invitation_admin, boolean adminInvitation, boolean cancel_invitation_admin,
                             boolean enter_moderation, boolean addModerator, boolean cancel_adding_moderator,
                             boolean accept_admin_invitation, boolean refuse_admin_invitation, boolean accept_moderator_invitation, boolean refuse_moderator_invitation,
                             boolean remove_yourself_like_admin, boolean remove_by_another_admin_like_admin, boolean remove_yourself_like_moderator, boolean remove_by_another_admin_like_moderator,
                             boolean invitation_in_group, boolean invite_to_join_the_group,
                             boolean accepted_invitation_to_join_the_group, boolean refuse_invitation_to_join_the_group, boolean hide_buttons,
                             boolean isGroup, boolean isMarket, boolean isMiioky, String user_id, String notification_id, String member_id, String admin_master, String admin_in_group,
                             String group_id, String adding_by, long date_created, boolean manage_member_in_group, boolean post_share_comment_like, boolean has_posted, boolean has_shared, boolean has_comment,
                             boolean has_like, boolean from_shared_post, boolean circle_group_cover, boolean circle_group_background_profile,
                             boolean comment_from_battle, boolean comment_from_battle_fun,
                             String post_id, String post_id_field, String category_of_post, String post_type, boolean comment_response, boolean simple_comment, String thumbnail, String url,
                             boolean userAnswer, String comment, String commentKey, String answeringUsername, String recyclerview_photo_id,
                             String recyclerview_fieldLike, String author_comment, String author_commentKey, long author_comment_date_create, String author_thumbnail,
                             String author_url, String author_id,
                             long date_comment_created, boolean membership, boolean asking_to_join_the_group, boolean approved_membership,
                             boolean refused_membership, boolean friendship, boolean accept_friendship, boolean remove_friendship,
                             boolean subscription_request, boolean accept_subscription_request) {
        this.allReadyOpenItem = allReadyOpenItem;
        this.suspension = suspension;
        this.suspended = suspended;
        this.cancel_suspension = cancel_suspension;
        this.approval = approval;
        this.publicationApprobation = publicationApprobation;
        this.need_post_approval = need_post_approval;
        this.cancel_publicationApprobation = cancel_publicationApprobation;
        this.activityLimitation = activityLimitation;
        this.postsActivityLimitation = postsActivityLimitation;
        this.commentsActivityLimitation = commentsActivityLimitation;
        this.postsActivityLimited = postsActivityLimited;
        this.commentsActivityLimited = commentsActivityLimited;
        this.cancel_limit_activity = cancel_limit_activity;
        this.banFromGroup = banFromGroup;
        this.enter_invitation_admin = enter_invitation_admin;
        this.adminInvitation = adminInvitation;
        this.cancel_invitation_admin = cancel_invitation_admin;
        this.enter_moderation = enter_moderation;
        this.addModerator = addModerator;
        this.cancel_adding_moderator = cancel_adding_moderator;
        this.accept_admin_invitation = accept_admin_invitation;
        this.refuse_admin_invitation = refuse_admin_invitation;
        this.accept_moderator_invitation = accept_moderator_invitation;
        this.refuse_moderator_invitation = refuse_moderator_invitation;
        this.remove_yourself_like_admin = remove_yourself_like_admin;
        this.remove_by_another_admin_like_admin = remove_by_another_admin_like_admin;
        this.remove_yourself_like_moderator = remove_yourself_like_moderator;
        this.remove_by_another_admin_like_moderator = remove_by_another_admin_like_moderator;
        this.invitation_in_group = invitation_in_group;
        this.invite_to_join_the_group = invite_to_join_the_group;
        this.accepted_invitation_to_join_the_group = accepted_invitation_to_join_the_group;
        this.refuse_invitation_to_join_the_group = refuse_invitation_to_join_the_group;
        this.hide_buttons = hide_buttons;
        this.isGroup = isGroup;
        this.isMarket = isMarket;
        this.isMiioky = isMiioky;
        this.user_id = user_id;
        this.notification_id = notification_id;
        this.member_id = member_id;
        this.admin_master = admin_master;
        this.admin_in_group = admin_in_group;
        this.group_id = group_id;
        this.adding_by = adding_by;
        this.date_created = date_created;
        this.manage_member_in_group = manage_member_in_group;
        this.post_share_comment_like = post_share_comment_like;
        this.has_posted = has_posted;
        this.has_shared = has_shared;
        this.has_comment = has_comment;
        this.has_like = has_like;
        this.from_shared_post = from_shared_post;
        this.circle_group_cover = circle_group_cover;
        this.circle_group_background_profile = circle_group_background_profile;
        this.comment_from_battle = comment_from_battle;
        this.comment_from_battle_fun = comment_from_battle_fun;
        this.post_id = post_id;
        this.post_id_field = post_id_field;
        this.category_of_post = category_of_post;
        this.post_type = post_type;
        this.simple_comment = simple_comment;
        this.comment_response = comment_response;
        this.thumbnail = thumbnail;
        this.url = url;
        this.userAnswer = userAnswer;
        this.comment = comment;
        this.commentKey = commentKey;
        this.answeringUsername = answeringUsername;
        this.recyclerview_fieldLike = recyclerview_fieldLike;
        this.recyclerview_photo_id = recyclerview_photo_id;
        this.author_comment = author_comment;
        this.author_commentKey = author_commentKey;
        this.author_comment_date_create = author_comment_date_create;
        this.author_thumbnail = author_thumbnail;
        this.author_url = author_url;
        this.author_id = author_id;
        this.date_comment_created = date_comment_created;
        this.membership = membership;
        this.asking_to_join_the_group = asking_to_join_the_group;
        this.approved_membership = approved_membership;
        this.refused_membership = refused_membership;
        this.friendship = friendship;
        this.accept_friendship = accept_friendship;
        this.remove_friendship = remove_friendship;
        this.subscription_request = subscription_request;
        this.accept_subscription_request = accept_subscription_request;
    }

    public boolean isAllReadyOpenItem() {
        return allReadyOpenItem;
    }

    public void setAllReadyOpenItem(boolean allReadyOpenItem) {
        this.allReadyOpenItem = allReadyOpenItem;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isCancel_suspension() {
        return cancel_suspension;
    }

    public void setCancel_suspension(boolean cancel_suspension) {
        this.cancel_suspension = cancel_suspension;
    }

    public boolean isPublicationApprobation() {
        return publicationApprobation;
    }

    public void setPublicationApprobation(boolean publicationApprobation) {
        this.publicationApprobation = publicationApprobation;
    }

    public boolean isNeed_post_approval() {
        return need_post_approval;
    }

    public void setNeed_post_approval(boolean need_post_approval) {
        this.need_post_approval = need_post_approval;
    }

    public boolean isCancel_publicationApprobation() {
        return cancel_publicationApprobation;
    }

    public void setCancel_publicationApprobation(boolean cancel_publicationApprobation) {
        this.cancel_publicationApprobation = cancel_publicationApprobation;
    }

    public boolean isPostsActivityLimited() {
        return postsActivityLimited;
    }

    public void setPostsActivityLimited(boolean postsActivityLimited) {
        this.postsActivityLimited = postsActivityLimited;
    }

    public boolean isCommentsActivityLimited() {
        return commentsActivityLimited;
    }

    public void setCommentsActivityLimited(boolean commentsActivityLimited) {
        this.commentsActivityLimited = commentsActivityLimited;
    }

    public boolean isCancel_limit_activity() {
        return cancel_limit_activity;
    }

    public void setCancel_limit_activity(boolean cancel_limit_activity) {
        this.cancel_limit_activity = cancel_limit_activity;
    }

    public boolean isBanFromGroup() {
        return banFromGroup;
    }

    public void setBanFromGroup(boolean banFromGroup) {
        this.banFromGroup = banFromGroup;
    }

    public boolean isAdminInvitation() {
        return adminInvitation;
    }

    public void setAdminInvitation(boolean adminInvitation) {
        this.adminInvitation = adminInvitation;
    }

    public boolean isCancel_invitation_admin() {
        return cancel_invitation_admin;
    }

    public void setCancel_invitation_admin(boolean cancel_invitation_admin) {
        this.cancel_invitation_admin = cancel_invitation_admin;
    }

    public boolean isAddModerator() {
        return addModerator;
    }

    public void setAddModerator(boolean addModerator) {
        this.addModerator = addModerator;
    }

    public boolean isCancel_adding_moderator() {
        return cancel_adding_moderator;
    }

    public void setCancel_adding_moderator(boolean cancel_adding_moderator) {
        this.cancel_adding_moderator = cancel_adding_moderator;
    }

    public boolean isAccept_admin_invitation() {
        return accept_admin_invitation;
    }

    public void setAccept_admin_invitation(boolean accept_admin_invitation) {
        this.accept_admin_invitation = accept_admin_invitation;
    }

    public boolean isAccept_moderator_invitation() {
        return accept_moderator_invitation;
    }

    public void setAccept_moderator_invitation(boolean accept_moderator_invitation) {
        this.accept_moderator_invitation = accept_moderator_invitation;
    }

    public boolean isRefuse_admin_invitation() {
        return refuse_admin_invitation;
    }

    public void setRefuse_admin_invitation(boolean refuse_admin_invitation) {
        this.refuse_admin_invitation = refuse_admin_invitation;
    }

    public boolean isRefuse_moderator_invitation() {
        return refuse_moderator_invitation;
    }

    public void setRefuse_moderator_invitation(boolean refuse_moderator_invitation) {
        this.refuse_moderator_invitation = refuse_moderator_invitation;
    }

    public boolean isRemove_yourself_like_admin() {
        return remove_yourself_like_admin;
    }

    public void setRemove_yourself_like_admin(boolean remove_yourself_like_admin) {
        this.remove_yourself_like_admin = remove_yourself_like_admin;
    }

    public boolean isRemove_by_another_admin_like_admin() {
        return remove_by_another_admin_like_admin;
    }

    public void setRemove_by_another_admin_like_admin(boolean remove_by_another_admin_like_admin) {
        this.remove_by_another_admin_like_admin = remove_by_another_admin_like_admin;
    }

    public boolean isRemove_yourself_like_moderator() {
        return remove_yourself_like_moderator;
    }

    public void setRemove_yourself_like_moderator(boolean remove_yourself_like_moderator) {
        this.remove_yourself_like_moderator = remove_yourself_like_moderator;
    }

    public boolean isRemove_by_another_admin_like_moderator() {
        return remove_by_another_admin_like_moderator;
    }

    public void setRemove_by_another_admin_like_moderator(boolean remove_by_another_admin_like_moderator) {
        this.remove_by_another_admin_like_moderator = remove_by_another_admin_like_moderator;
    }

    public boolean isInvitation_in_group() {
        return invitation_in_group;
    }

    public void setInvitation_in_group(boolean invitation_in_group) {
        this.invitation_in_group = invitation_in_group;
    }

    public boolean isInvite_to_join_the_group() {
        return invite_to_join_the_group;
    }

    public void setInvite_to_join_the_group(boolean invite_to_join_the_group) {
        this.invite_to_join_the_group = invite_to_join_the_group;
    }

    public boolean isAccepted_invitation_to_join_the_group() {
        return accepted_invitation_to_join_the_group;
    }

    public void setAccepted_invitation_to_join_the_group(boolean accepted_invitation_to_join_the_group) {
        this.accepted_invitation_to_join_the_group = accepted_invitation_to_join_the_group;
    }

    public boolean isRefuse_invitation_to_join_the_group() {
        return refuse_invitation_to_join_the_group;
    }

    public void setRefuse_invitation_to_join_the_group(boolean refuse_invitation_to_join_the_group) {
        this.refuse_invitation_to_join_the_group = refuse_invitation_to_join_the_group;
    }

    public boolean isHide_buttons() {
        return hide_buttons;
    }

    public void setHide_buttons(boolean hide_buttons) {
        this.hide_buttons = hide_buttons;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isMarket() {
        return isMarket;
    }

    public void setMarket(boolean market) {
        isMarket = market;
    }

    public boolean isMiioky() {
        return isMiioky;
    }

    public void setMiioky(boolean miioky) {
        isMiioky = miioky;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getAdmin_master() {
        return admin_master;
    }

    public void setAdmin_master(String admin_master) {
        this.admin_master = admin_master;
    }

    public String getAdmin_in_group() {
        return admin_in_group;
    }

    public void setAdmin_in_group(String admin_in_group) {
        this.admin_in_group = admin_in_group;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getAdding_by() {
        return adding_by;
    }

    public void setAdding_by(String adding_by) {
        this.adding_by = adding_by;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }

    public boolean isSuspension() {
        return suspension;
    }

    public void setSuspension(boolean suspension) {
        this.suspension = suspension;
    }

    public boolean isApproval() {
        return approval;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    public boolean isActivityLimitation() {
        return activityLimitation;
    }

    public void setActivityLimitation(boolean activityLimitation) {
        this.activityLimitation = activityLimitation;
    }

    public boolean isPostsActivityLimitation() {
        return postsActivityLimitation;
    }

    public void setPostsActivityLimitation(boolean postsActivityLimitation) {
        this.postsActivityLimitation = postsActivityLimitation;
    }

    public boolean isCommentsActivityLimitation() {
        return commentsActivityLimitation;
    }

    public void setCommentsActivityLimitation(boolean commentsActivityLimitation) {
        this.commentsActivityLimitation = commentsActivityLimitation;
    }

    public boolean isEnter_invitation_admin() {
        return enter_invitation_admin;
    }

    public void setEnter_invitation_admin(boolean enter_invitation_admin) {
        this.enter_invitation_admin = enter_invitation_admin;
    }

    public boolean isEnter_moderation() {
        return enter_moderation;
    }

    public void setEnter_moderation(boolean enter_moderation) {
        this.enter_moderation = enter_moderation;
    }

    public boolean isHas_posted() {
        return has_posted;
    }

    public void setHas_posted(boolean has_posted) {
        this.has_posted = has_posted;
    }

    public boolean isHas_shared() {
        return has_shared;
    }

    public void setHas_shared(boolean has_shared) {
        this.has_shared = has_shared;
    }

    public boolean isHas_comment() {
        return has_comment;
    }

    public void setHas_comment(boolean has_comment) {
        this.has_comment = has_comment;
    }

    public boolean isHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public boolean isFrom_shared_post() {
        return from_shared_post;
    }

    public void setFrom_shared_post(boolean from_shared_post) {
        this.from_shared_post = from_shared_post;
    }

    public boolean isCircle_group_background_profile() {
        return circle_group_background_profile;
    }

    public void setCircle_group_background_profile(boolean circle_group_background_profile) {
        this.circle_group_background_profile = circle_group_background_profile;
    }

    public boolean isCircle_group_cover() {
        return circle_group_cover;
    }

    public void setCircle_group_cover(boolean circle_group_cover) {
        this.circle_group_cover = circle_group_cover;
    }

    public boolean isManage_member_in_group() {
        return manage_member_in_group;
    }

    public boolean isPost_share_comment_like() {
        return post_share_comment_like;
    }

    public void setPost_share_comment_like(boolean post_share_comment_like) {
        this.post_share_comment_like = post_share_comment_like;
    }

    public void setManage_member_in_group(boolean manage_member_in_group) {
        this.manage_member_in_group = manage_member_in_group;
    }

    public boolean isComment_from_battle() {
        return comment_from_battle;
    }

    public void setComment_from_battle(boolean comment_from_battle) {
        this.comment_from_battle = comment_from_battle;
    }

    public boolean isComment_from_battle_fun() {
        return comment_from_battle_fun;
    }

    public void setComment_from_battle_fun(boolean comment_from_battle_fun) {
        this.comment_from_battle_fun = comment_from_battle_fun;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCategory_of_post() {
        return category_of_post;
    }

    public void setCategory_of_post(String category_of_post) {
        this.category_of_post = category_of_post;
    }

    public String getPost_id_field() {
        return post_id_field;
    }

    public void setPost_id_field(String post_id_field) {
        this.post_id_field = post_id_field;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public boolean isSimple_comment() {
        return simple_comment;
    }

    public void setSimple_comment(boolean simple_comment) {
        this.simple_comment = simple_comment;
    }

    public boolean isComment_response() {
        return comment_response;
    }

    public void setComment_response(boolean comment_response) {
        this.comment_response = comment_response;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(boolean userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    public String getAnsweringUsername() {
        return answeringUsername;
    }

    public void setAnsweringUsername(String answeringUsername) {
        this.answeringUsername = answeringUsername;
    }

    public String getRecyclerview_photo_id() {
        return recyclerview_photo_id;
    }

    public void setRecyclerview_photo_id(String recyclerview_photo_id) {
        this.recyclerview_photo_id = recyclerview_photo_id;
    }

    public String getRecyclerview_fieldLike() {
        return recyclerview_fieldLike;
    }

    public void setRecyclerview_fieldLike(String recyclerview_fieldLike) {
        this.recyclerview_fieldLike = recyclerview_fieldLike;
    }

    public String getAuthor_comment() {
        return author_comment;
    }

    public void setAuthor_comment(String author_comment) {
        this.author_comment = author_comment;
    }

    public String getAuthor_commentKey() {
        return author_commentKey;
    }

    public void setAuthor_commentKey(String author_commentKey) {
        this.author_commentKey = author_commentKey;
    }

    public long getAuthor_comment_date_create() {
        return author_comment_date_create;
    }

    public void setAuthor_comment_date_create(long author_comment_date_create) {
        this.author_comment_date_create = author_comment_date_create;
    }

    public String getAuthor_thumbnail() {
        return author_thumbnail;
    }

    public void setAuthor_thumbnail(String author_thumbnail) {
        this.author_thumbnail = author_thumbnail;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public long getDate_comment_created() {
        return date_comment_created;
    }

    public void setDate_comment_created(long date_comment_created) {
        this.date_comment_created = date_comment_created;
    }

    public boolean isMembership() {
        return membership;
    }

    public void setMembership(boolean membership) {
        this.membership = membership;
    }

    public boolean isAsking_to_join_the_group() {
        return asking_to_join_the_group;
    }

    public void setAsking_to_join_the_group(boolean asking_to_join_the_group) {
        this.asking_to_join_the_group = asking_to_join_the_group;
    }

    public boolean isApproved_membership() {
        return approved_membership;
    }

    public void setApproved_membership(boolean approved_membership) {
        this.approved_membership = approved_membership;
    }

    public boolean isRefused_membership() {
        return refused_membership;
    }

    public void setRefused_membership(boolean refused_membership) {
        this.refused_membership = refused_membership;
    }

    public boolean isSubscription_request() {
        return subscription_request;
    }

    public void setSubscription_request(boolean subscription_request) {
        this.subscription_request = subscription_request;
    }

    public boolean isAccept_subscription_request() {
        return accept_subscription_request;
    }

    public void setAccept_subscription_request(boolean accept_subscription_request) {
        this.accept_subscription_request = accept_subscription_request;
    }

    public boolean isFriendship() {
        return friendship;
    }

    public void setFriendship(boolean friendship) {
        this.friendship = friendship;
    }

    public boolean isAccept_friendship() {
        return accept_friendship;
    }

    public void setAccept_friendship(boolean accept_friendship) {
        this.accept_friendship = accept_friendship;
    }

    public boolean isRemove_friendship() {
        return remove_friendship;
    }

    public void setRemove_friendship(boolean remove_friendship) {
        this.remove_friendship = remove_friendship;
    }

}

