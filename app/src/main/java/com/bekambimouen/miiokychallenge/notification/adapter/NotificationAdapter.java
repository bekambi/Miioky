package com.bekambimouen.miiokychallenge.notification.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.child_recyclerview.comment.ChildRecyclerComment;
import com.bekambimouen.miiokychallenge.child_recyclerview.comment.ChildRecyclerResponseComment;
import com.bekambimouen.miiokychallenge.child_recyclerview_share.comment.ChildRecyclerCommentShare;
import com.bekambimouen.miiokychallenge.child_recyclerview_share.comment.ChildRecyclerResponseCommentShare;
import com.bekambimouen.miiokychallenge.comment.ViewComment;
import com.bekambimouen.miiokychallenge.comment.ViewResponseComment;
import com.bekambimouen.miiokychallenge.comment_fun.ViewCommentFun;
import com.bekambimouen.miiokychallenge.comment_fun.ViewResponseFunComment;
import com.bekambimouen.miiokychallenge.comment_share.ViewCommentShare;
import com.bekambimouen.miiokychallenge.comment_share.ViewResponseCommentShare;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview.comment.GroupChildRecyclerComment;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview.comment.GroupChildRecyclerResponseComment;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview_share.comment.GroupChildRecyclerCommentShare;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview_share.comment.GroupChildRecyclerResponseCommentShare;
import com.bekambimouen.miiokychallenge.groups.comment.GroupComment;
import com.bekambimouen.miiokychallenge.groups.comment.GroupResponseComment;
import com.bekambimouen.miiokychallenge.groups.comment_share.GroupCommentShare;
import com.bekambimouen.miiokychallenge.groups.comment_share.GroupResponseCommentShare;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPrivateViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.discover.GroupPublicViewProfileDiscover;
import com.bekambimouen.miiokychallenge.groups.manage_member.membership.GroupMembership;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.MarketAboutProduct;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel_fun;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private static final String TAG = "MarketExplorerAdapter";
    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final List<NotificationModel> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public NotificationAdapter(AppCompatActivity context, List<NotificationModel> list,
                               OnLoadMoreItemsListener mOnLoadMoreItemsListener, ProgressBar progressBar,
                               RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = list.get(position);

        // retrieve the previous notification, if exists
        NotificationModel previousNotification = null;
        if (position > 0) {
            previousNotification = list.get(position - 1);
        }
        holder.setDate(previousNotification, notification, position);
        // current notification
        holder.bind(notification);

        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

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
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final CircleImageView profile_photo;
        private final TextView message_text, date, day_date, invitation_accepted;
        private final ImageView group_icon, person_icon, market_icon;
        private final LinearLayout linLayout_buttons_join_group;
        private final RelativeLayout parent, relLayout_button_join_group, relLayout_button_delete_invitation;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public ViewHolder(View itemView) {
            super(itemView);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            message_text = itemView.findViewById(R.id.message_text);
            date = itemView.findViewById(R.id.date);
            day_date = itemView.findViewById(R.id.day_date);
            group_icon = itemView.findViewById(R.id.group_icon);
            person_icon = itemView.findViewById(R.id.person_icon);
            market_icon = itemView.findViewById(R.id.market_icon);
            parent = itemView.findViewById(R.id.parent);
            invitation_accepted = itemView.findViewById(R.id.invitation_accepted);
            linLayout_buttons_join_group = itemView.findViewById(R.id.linLayout_buttons_join_group);
            relLayout_button_join_group = itemView.findViewById(R.id.relLayout_button_join_group);
            relLayout_button_delete_invitation = itemView.findViewById(R.id.relLayout_button_delete_invitation);

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        }

        public void bind(NotificationModel notification) {
            long tv_date = notification.getDate_created();
            getTimestampDifference(tv_date, date);

            // item background if notification is already open _____________________________________
            if (notification.isAllReadyOpenItem())
                parent.setBackgroundColor(context.getColor(R.color.white));
            else
                parent.setBackgroundColor(context.getColor(R.color.blue_50));
            // _____________________________________________________________________________________

            // change profile icon
            if (notification.isGroup()) {
                group_icon.setVisibility(View.VISIBLE);
                person_icon.setVisibility(View.GONE);
                market_icon.setVisibility(View.GONE);

            } else if (notification.isMiioky()) {
                group_icon.setVisibility(View.GONE);
                person_icon.setVisibility(View.VISIBLE);
                market_icon.setVisibility(View.GONE);

            } else if (notification.isMarket()) {
                group_icon.setVisibility(View.GONE);
                person_icon.setVisibility(View.GONE);
                market_icon.setVisibility(View.VISIBLE);
            }

            if (notification.isMiioky()) {
                // concerning comments
                if (notification.isPost_share_comment_like()) {
                    manageMiiokyComments(notification);
                }

                // concerning friendship
                if (notification.isFriendship()) {
                    friendship(notification);
                }

                // concerning subscription request
                if (notification.isSubscription_request()) {
                    subscriptionRequest(notification);
                }

            } else if (notification.isMarket()) {
                // concerning market place
                announceInMarket(notification);

            } else if (notification.isGroup()){
                // concerning request to be member
                if (notification.isMembership()) {
                    membership_request(notification);
                }

                // concerning comments
                if (notification.isPost_share_comment_like()) {
                    manageGroupComments(notification);
                }

                // invitation in group
                if (notification.isInvitation_in_group()) {
                    getInvitationToBeMemberInGroup(notification);

                }

                // get the admin profile (invitation to be admin or moderator)
                if (notification.isEnter_invitation_admin() || notification.isEnter_moderation())
                    getTheAdminWhoInvitedProfile(notification);

                if (notification.isSuspension() || notification.isPublicationApprobation()
                        || notification.isActivityLimitation() || notification.isBanFromGroup()) {
                    // get the sanctioned user profile
                    Query query = myRef.child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(notification.getMember_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, dataSnapshot);

                                String userName = user.getUsername();

                                // group
                                Query query = myRef.child(context.getString(R.string.dbname_user_group))
                                        .child(notification.getAdmin_master())
                                        .orderByChild(context.getString(R.string.field_group_id))
                                        .equalTo(notification.getGroup_id());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()) {

                                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                            assert objectMap != null;
                                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                            // get value
                                            String groupName = user_groups.getGroup_name();

                                            Glide.with(context.getApplicationContext())
                                                    .load(user_groups.getProfile_photo())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(profile_photo);

                                            if (notification.isSuspension()) {
                                                if (notification.isSuspended()) {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "+context.getString(R.string.the_group)+
                                                            " <b>"+groupName+"</b> "+context.getString(R.string.suspends_you)));

                                                } else if (notification.isCancel_suspension()) {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "+context.getString(R.string.admin_canceled_your_suspension)+
                                                            " <b>"+groupName+"</b> "+context.getString(R.string.you_can_comment_and_publish_now)));

                                                } else {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "+context.getString(R.string.your_suspension_period_in_group)+
                                                            " <b>"+groupName+"</b> "+context.getString(R.string.has_ended)));
                                                }

                                                getGroup(parent, notification, user_groups);
                                            }

                                            // posts approval
                                            if (notification.isPublicationApprobation()) {
                                                if (notification.isNeed_post_approval()) {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                            +context.getString(R.string.during_a_period_of_30_days)+
                                                            " <b>"+groupName+"</b> "+context.getString(R.string.posts_will_be_submitted)));

                                                } else if (notification.isApproval()) {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                            +context.getString(R.string.your_post_has_been_approval)+
                                                            " <b>"+groupName+"</b>."));

                                                } else if (notification.isCancel_publicationApprobation()) {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                            +context.getString(R.string.admin_disable_approval_request)+
                                                            " <b>"+groupName+"</b>."));

                                                } else {
                                                    message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                            +context.getString(R.string.your_post_approval_period)
                                                            + " <b>"+groupName+"</b> "+context.getString(R.string.has_ended)));
                                                }

                                                getGroup(parent, notification, user_groups);
                                            }

                                            // limit activity in group
                                            if (notification.isActivityLimitation()) {

                                                if (notification.isPostsActivityLimitation()) {
                                                    if (notification.isPostsActivityLimited()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                                +context.getString(R.string.admin_limit_your_activities_in_group)+
                                                                " <b>"+groupName+"</b>."));

                                                    } else if (notification.isCancel_limit_activity()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                                +context.getString(R.string.admin_cancel_your_limit_activities_in_group)+
                                                                " <b>"+groupName+"</b>."));

                                                    } else {
                                                        message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                                +context.getString(R.string.the_period_of_the_post_activity_limit_in_the_activity)
                                                                + " <b>"+groupName+"</b> "+context.getString(R.string.has_ended)));
                                                    }
                                                }

                                                if (notification.isCommentsActivityLimitation()) {
                                                    if (notification.isCommentsActivityLimited()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                                +context.getString(R.string.admin_limit_your_activities_in_group)+
                                                                " <b>"+groupName+"</b>."));

                                                    } else if (notification.isCancel_limit_activity()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                                +context.getString(R.string.admin_cancel_your_limit_activities_in_group)+
                                                                " <b>"+groupName+"</b>."));

                                                    } else {
                                                        message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                                +context.getString(R.string.the_period_of_the_comment_activity_limit_in_the_activity)
                                                                + " <b>"+groupName+"</b> "+context.getString(R.string.has_ended)));
                                                    }
                                                }

                                                getGroup(parent, notification, user_groups);
                                            }

                                            // ban from group
                                            if (notification.isBanFromGroup()) {
                                                message_text.setText(Html.fromHtml(" <b>"+userName+"</b> "
                                                        +context.getString(R.string.admin_banned_you_from_group)+
                                                        " <b>"+groupName+"</b>. "+context.getString(R.string.you_are_permanently_excluded_from_this_group)));

                                                getBanMemberFromGroup(parent, notification, user, user_groups);
                                            }
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
            }
        }

        // concerning subscription request
        private void subscriptionRequest(NotificationModel notification) {
            Query query = myRef.child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(notification.getMember_id());

            if (notification.isAccept_subscription_request()) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            message_text.setText(Html.fromHtml(" <b>"+user.getUsername()+"</b> "
                                    +context.getString(R.string.accepted_your_subscription_request)));

                            Glide.with(context.getApplicationContext())
                                    .load(user.getProfileUrl())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            parent.setOnClickListener(view -> {
                                //  change notification background
                                if (!notification.isAllReadyOpenItem()) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("allReadyOpenItem", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(map);

                                    parent.setBackgroundColor(context.getColor(R.color.white));
                                }

                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                                context.startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }

        // concerning friendship
        private void friendship(NotificationModel notification) {
            Query query = myRef.child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(notification.getMember_id());

            if (notification.isAccept_friendship()) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            message_text.setText(Html.fromHtml(" <b>"+user.getUsername()+"</b> "
                                    +context.getString(R.string.accepted_your_friend_request)));

                            Glide.with(context.getApplicationContext())
                                    .load(user.getProfileUrl())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            parent.setOnClickListener(view -> {
                                //  change notification background
                                if (!notification.isAllReadyOpenItem()) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("allReadyOpenItem", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(map);

                                    parent.setBackgroundColor(context.getColor(R.color.white));
                                }

                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                                context.startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else if (notification.isRemove_friendship()) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            message_text.setText(Html.fromHtml(" <b>"+user.getUsername()+"</b> "
                                    +context.getString(R.string.has_unfriend_you)));

                            Glide.with(context.getApplicationContext())
                                    .load(user.getProfileUrl())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            parent.setOnClickListener(view -> {
                                //  change notification background
                                if (!notification.isAllReadyOpenItem()) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("allReadyOpenItem", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(map);

                                    parent.setBackgroundColor(context.getColor(R.color.white));
                                }

                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ViewProfile.class);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(user);
                                intent.putExtra("user", myJson);
                                context.startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        // concerning market
        /**
         * admin_master replace store_owner
         * group_id replace store_id
         */
        private void announceInMarket(NotificationModel notification) {
            // get store name and profile photo
            Query myQuery = myRef.child(context.getString(R.string.dbname_user_stores))
                    .child(notification.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(notification.getGroup_id());

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        // get value
                        String storeName = market_model.getStore_name();

                        Glide.with(context.getApplicationContext())
                                .load(market_model.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(profile_photo);

                        Query query = myRef.child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(notification.getMember_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                    assert objectMap != null;
                                    User member = Util_User.getUser(context, objectMap, dataSnapshot);

                                    message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                            +context.getString(R.string.the_shop)
                                            + " <b>"+storeName+"</b> "+context.getString(R.string.made_a_new_announcement)+"."));
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

            // click on item
            Query query = myRef
                    .child(context.getString(R.string.dbname_market))
                    .child(notification.getGroup_id()) // store_id
                    .orderByChild(context.getString(R.string.field_store_id))
                    .equalTo(notification.getGroup_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                        parent.setOnClickListener(view -> {
                            //  change notification background
                            if (!notification.isAllReadyOpenItem()) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("allReadyOpenItem", true);

                                myRef.child(context.getString(R.string.dbname_notification))
                                        .child(user_id)
                                        .child(notification.getNotification_id())
                                        .updateChildren(map);
                            }

                            if (!market_model.isSuppressed()) {
                                parent.setBackgroundColor(context.getColor(R.color.white));
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                String myJson = gson.toJson(market_model);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, MarketAboutProduct.class);
                                intent.putExtra("market_model", myJson);
                                context.startActivity(intent);

                            } else
                                Toast.makeText(context, context.getString(R.string.sold), Toast.LENGTH_LONG).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // concerning membership request
        private void membership_request(NotificationModel notification) {
            if (notification.isAsking_to_join_the_group()) {
                // send to all admins
                Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                        .child(notification.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(notification.getGroup_id());
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get value
                            String groupName = user_groups.getGroup_name();

                            Query query = myRef.child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(notification.getMember_id());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                        assert objectMap != null;
                                        User member = Util_User.getUser(context, objectMap, dataSnapshot);

                                        Glide.with(context.getApplicationContext())
                                                .load(member.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(profile_photo);

                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                +context.getString(R.string.asked_to_join_the_group)
                                                + " <b>"+groupName+"</b>."));

                                        parent.setOnClickListener(view -> {
                                            //  change notification background
                                            if (!notification.isAllReadyOpenItem()) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("allReadyOpenItem", true);

                                                myRef.child(context.getString(R.string.dbname_notification))
                                                        .child(user_id)
                                                        .child(notification.getNotification_id())
                                                        .updateChildren(map);

                                                parent.setBackgroundColor(context.getColor(R.color.white));
                                            }

                                            if (relLayout_view_overlay != null)
                                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                                            Gson gson = new Gson();
                                            String myGson = gson.toJson(user_groups);
                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                            Intent intent = new Intent(context, GroupMembership.class);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (notification.isRefused_membership()) {
                // send to the user
                Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                        .child(notification.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(notification.getGroup_id());
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get value
                            String groupName = user_groups.getGroup_name();
                            Glide.with(context.getApplicationContext())
                                    .load(user_groups.getProfile_photo())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            message_text.setText(Html.fromHtml(context.getString(R.string.the_group_admins)
                                    +" <b>"+groupName+"</b> "
                                    +context.getString(R.string.reject_your_membership_request)));

                            parent.setOnClickListener(view -> {
                                //  change notification background
                                if (!notification.isAllReadyOpenItem()) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("allReadyOpenItem", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(map);

                                    parent.setBackgroundColor(context.getColor(R.color.white));
                                }

                                if (!user_groups.isSuppressed()) {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                                    intent.putExtra("user_groups", myGson);
                                    context.startActivity(intent);

                                } else
                                    Toast.makeText(context, context.getString(R.string.this_group_has_been_deleted), Toast.LENGTH_LONG).show();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (notification.isApproved_membership()) {
                // send to the user
                Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                        .child(notification.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(notification.getGroup_id());
                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get value
                            String groupName = user_groups.getGroup_name();
                            Glide.with(context.getApplicationContext())
                                    .load(user_groups.getProfile_photo())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            message_text.setText(Html.fromHtml(context.getString(R.string.the_group_admins)
                                    +" <b>"+groupName+"</b> "
                                    +context.getString(R.string.have_accepted_your_membership_request)));

                            parent.setOnClickListener(view -> {
                                //  change notification background
                                if (!notification.isAllReadyOpenItem()) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("allReadyOpenItem", true);

                                    myRef.child(context.getString(R.string.dbname_notification))
                                            .child(user_id)
                                            .child(notification.getNotification_id())
                                            .updateChildren(map);

                                    parent.setBackgroundColor(context.getColor(R.color.white));
                                }

                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                // to know if current user is still member of group
                                Query query = myRef
                                        .child(context.getString(R.string.dbname_group_followers))
                                        .child(user_groups.getGroup_id())
                                        .orderByChild(context.getString(R.string.field_user_id))
                                        .equalTo(user_id);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!user_groups.isSuppressed()) {
                                            if (snapshot.exists()) {
                                                Gson gson = new Gson();
                                                String myGson = gson.toJson(user_groups);
                                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                Intent intent = new Intent(context, GroupViewProfile.class);
                                                intent.putExtra("user_groups", myGson);
                                                context.startActivity(intent);

                                            } else {
                                                if (user_groups.isGroup_private()) {
                                                    Gson gson = new Gson();
                                                    String myGson = gson.toJson(user_groups);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                                                    intent.putExtra("user_groups", myGson);
                                                    context.startActivity(intent);

                                                } else {
                                                    Gson gson = new Gson();
                                                    String myJson = gson.toJson(user_groups);
                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupPublicViewProfileDiscover.class);
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                }
                                            }

                                        } else
                                            Toast.makeText(context, context.getString(R.string.this_group_has_been_deleted), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        // concerning Miioky comments
        private void manageMiiokyComments(NotificationModel notification) {
            if (notification.isSimple_comment()) {
                Query query = myRef.child(context.getString(R.string.dbname_users))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(notification.getMember_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            User member = Util_User.getUser(context, objectMap, dataSnapshot);

                            Glide.with(context.getApplicationContext())
                                    .load(member.getProfileUrl())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                    +context.getString(R.string.commented_on_your_post)));

                            // the user his post
                            Query query = null;
                            // comment from battle fun
                            if (notification.isComment_from_battle_fun()) {
                                query = myRef
                                        .child(context.getString(R.string.dbname_videos))
                                        .orderByChild(notification.getPost_id_field())
                                        .equalTo(notification.getPost_id());

                            }

                            // comment from battle
                            if (notification.isComment_from_battle()) {
                                if (notification.isFrom_shared_post()) {
                                    query = myRef
                                            .child(context.getString(R.string.dbname_battle_media_share))
                                            .orderByChild(notification.getPost_id_field())
                                            .equalTo(notification.getPost_id());

                                } else {
                                    query = myRef
                                            .child(context.getString(R.string.dbname_battle_media))
                                            .orderByChild(notification.getPost_id_field())
                                            .equalTo(notification.getPost_id());
                                }

                            }

                            assert query != null;
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                                        parent.setOnClickListener(view -> {
                                            //  change notification background
                                            if (!notification.isAllReadyOpenItem()) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("allReadyOpenItem", true);

                                                myRef.child(context.getString(R.string.dbname_notification))
                                                        .child(user_id)
                                                        .child(notification.getNotification_id())
                                                        .updateChildren(map);

                                                parent.setBackgroundColor(context.getColor(R.color.white));
                                            }

                                            if (relLayout_view_overlay != null)
                                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                            Intent intent;
                                            // from Fun comment
                                            String myGson;
                                            if (notification.isComment_from_battle_fun()) {
                                                assert objectMap != null;
                                                BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, ds);

                                                if (!model.isSuppressed()) {
                                                    Gson gson = new Gson();
                                                    myGson = gson.toJson(model);
                                                    intent = new Intent(context, ViewCommentFun.class);
                                                    intent.putExtra(notification.getCategory_of_post(), myGson);
                                                    intent.putExtra(notification.getPost_type(), notification.getPost_type());
                                                    intent.putExtra("from_notification_comment", "from_notification_comment");
                                                    context.startActivity(intent);

                                                } else
                                                    Toast.makeText(context, context.getString(R.string.this_post_has_been_deleted), Toast.LENGTH_LONG).show();
                                            }
                                            // from Battle comment
                                            if (notification.isComment_from_battle()) {
                                                assert objectMap != null;
                                                BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                                if (!model.isSuppressed()) {
                                                    Gson gson = new Gson();
                                                    myGson = gson.toJson(model);
                                                    if (notification.isFrom_shared_post()) {
                                                        if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                            intent = new Intent(context, ChildRecyclerCommentShare.class);
                                                            intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                            intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                        } else {
                                                            intent = new Intent(context, ViewCommentShare.class);
                                                        }

                                                    } else if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                        intent = new Intent(context, ChildRecyclerComment.class);
                                                        intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                        intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                    } else {
                                                        intent = new Intent(context, ViewComment.class);

                                                    }
                                                    intent.putExtra(notification.getCategory_of_post(), myGson);
                                                    intent.putExtra(notification.getPost_type(), notification.getPost_type());
                                                    intent.putExtra("from_notification_comment", "from_notification_comment");
                                                    context.startActivity(intent);

                                                } else
                                                    Toast.makeText(context, context.getString(R.string.this_post_has_been_deleted), Toast.LENGTH_LONG).show();
                                            }
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
            } else if (notification.isComment_response()) {
                Query query = myRef.child(context.getString(R.string.dbname_users))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(notification.getMember_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            User member = Util_User.getUser(context, objectMap, dataSnapshot);

                            Glide.with(context.getApplicationContext())
                                    .load(member.getProfileUrl())
                                    .placeholder(R.drawable.ic_baseline_person_24)
                                    .into(profile_photo);

                            message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                    +context.getString(R.string.responded_to_your_comment)));

                            // the user his comment
                            Query query = null;
                            // comment from battle fun
                            if (notification.isComment_from_battle_fun()) {
                                query = myRef
                                        .child(context.getString(R.string.dbname_videos))
                                        .orderByChild(notification.getPost_id_field())
                                        .equalTo(notification.getPost_id());
                            }

                            // comment from battle
                            if (notification.isComment_from_battle()) {
                                if (notification.isFrom_shared_post()) {
                                    query = myRef
                                            .child(context.getString(R.string.dbname_battle_media_share))
                                            .orderByChild(notification.getPost_id_field())
                                            .equalTo(notification.getPost_id());
                                } else {
                                    query = myRef
                                            .child(context.getString(R.string.dbname_battle_media))
                                            .orderByChild(notification.getPost_id_field())
                                            .equalTo(notification.getPost_id());
                                }

                            }

                            assert query != null;
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                                        parent.setOnClickListener(view -> {
                                            //  change notification background
                                            if (!notification.isAllReadyOpenItem()) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("allReadyOpenItem", true);

                                                myRef.child(context.getString(R.string.dbname_notification))
                                                        .child(user_id)
                                                        .child(notification.getNotification_id())
                                                        .updateChildren(map);

                                                parent.setBackgroundColor(context.getColor(R.color.white));
                                            }

                                            if (relLayout_view_overlay != null)
                                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                            Intent intent = null;
                                            // from Fun comment
                                            String myGson = null;
                                            if (notification.isComment_from_battle_fun()) {
                                                assert objectMap != null;
                                                BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, ds);

                                                Gson gson = new Gson();
                                                myGson = gson.toJson(model);
                                                intent = new Intent(context, ViewResponseFunComment.class);
                                                intent.putExtra("comment_key", notification.getCommentKey());
                                                intent.putExtra("comment", notification.getComment());
                                                intent.putExtra("userID", notification.getUser_id());
                                                intent.putExtra("media_comment_url", notification.getUrl());
                                                intent.putExtra("media_comment_thumbnail", notification.getThumbnail());
                                                intent.putExtra("time", notification.getDate_comment_created());
                                                intent.putExtra(notification.getCategory_of_post(), myGson);
                                                intent.putExtra("post_id_field", notification.getPost_id_field());
                                                intent.putExtra("post_type", notification.getPost_type());
                                                intent.putExtra("post_id", notification.getPost_id());
                                                intent.putExtra("recyclerview_photo_id_upload", notification.getRecyclerview_photo_id());
                                                intent.putExtra("recyclerview_fieldLike_upload", notification.getRecyclerview_fieldLike());
                                                intent.putExtra("admin_master", notification.getAdmin_master());
                                                intent.putExtra("the_user_who_posted_id", notification.getUser_id());
                                                intent.putExtra("group_id", notification.getGroup_id());
                                                // response comment
                                                intent.putExtra("author_comment", notification.getAuthor_comment());
                                                intent.putExtra("author_commentKey", notification.getAuthor_commentKey());
                                                intent.putExtra("author_comment_date_create", notification.getAuthor_comment_date_create());
                                                intent.putExtra("author_thumbnail", notification.getAuthor_thumbnail());
                                                intent.putExtra("author_url", notification.getAuthor_url());
                                                intent.putExtra("author_id", notification.getAuthor_id());
                                                intent.putExtra("from_notification", "from_notification");
                                                intent.putExtra("from_notification_comment", "from_notification_comment");
                                                context.startActivity(intent);
                                            }
                                            // from Battle comment
                                            if (notification.isComment_from_battle()) {
                                                assert objectMap != null;
                                                BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                                if (!model.isSuppressed()) {
                                                    Gson gson = new Gson();
                                                    myGson = gson.toJson(model);
                                                    if (notification.isFrom_shared_post()) {
                                                        if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                            intent = new Intent(context, ChildRecyclerResponseCommentShare.class);
                                                            intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                            intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                        } else {
                                                            intent = new Intent(context, ViewResponseCommentShare.class);

                                                            if (model.isUser_profile_shared() || model.isGroup_share_single_top_circle_image()
                                                                    || ((model.isImageUne_shared() || model.isGroup_share_single_top_image_une())  && model.isBig_image())
                                                                    || ((model.isImageUne_shared() || model.isGroup_share_single_top_image_une())  && !model.isBig_image())
                                                                    || ((model.isVideoUne_shared() || model.isGroup_share_single_top_video_une())  && model.isBig_image())
                                                                    || ((model.isVideoUne_shared() || model.isGroup_share_single_top_video_une())  && !model.isBig_image())) {
                                                                intent.putExtra("comment_image_une", myGson);

                                                            } else if (model.isRecyclerItem_shared() || model.isGroup_share_single_top_recycler()) {
                                                                intent.putExtra("comment_recycler", myGson);

                                                            } else if (model.isImageDouble_shared() || model.isGroup_share_single_top_image_double()
                                                                    || model.isVideoDouble_shared() || model.isGroup_share_single_top_video_double()) {
                                                                intent.putExtra("comment_image_double", myGson);

                                                            } else if (model.isReponseImageDouble_shared() || model.isGroup_share_single_top_response_image_double()
                                                                    || model.isReponseVideoDouble_shared() || model.isGroup_share_single_top_response_video_double()) {
                                                                intent.putExtra("comment_reponse_image_double", myGson);

                                                            }
                                                        }

                                                    } else if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                        intent = new Intent(context, ChildRecyclerResponseComment.class);
                                                        intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                        intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                    } else {
                                                        intent = new Intent(context, ViewResponseComment.class);
                                                    }

                                                    intent.putExtra("comment_key", notification.getCommentKey());
                                                    intent.putExtra("comment", notification.getComment());
                                                    intent.putExtra("userID", notification.getUser_id());
                                                    intent.putExtra("media_comment_url", notification.getUrl());
                                                    intent.putExtra("media_comment_thumbnail", notification.getThumbnail());
                                                    intent.putExtra("time", notification.getDate_comment_created());
                                                    intent.putExtra(notification.getCategory_of_post(), myGson);
                                                    intent.putExtra("post_id_field", notification.getPost_id_field());
                                                    intent.putExtra("post_type", notification.getPost_type());
                                                    intent.putExtra("post_id", notification.getPost_id());
                                                    intent.putExtra("recyclerview_photo_id_upload", notification.getRecyclerview_photo_id());
                                                    intent.putExtra("recyclerview_fieldLike_upload", notification.getRecyclerview_fieldLike());
                                                    intent.putExtra("admin_master", notification.getAdmin_master());
                                                    intent.putExtra("the_user_who_posted_id", notification.getUser_id());
                                                    intent.putExtra("group_id", notification.getGroup_id());
                                                    // response comment
                                                    intent.putExtra("author_comment", notification.getAuthor_comment());
                                                    intent.putExtra("author_commentKey", notification.getAuthor_commentKey());
                                                    intent.putExtra("author_comment_date_create", notification.getAuthor_comment_date_create());
                                                    intent.putExtra("author_thumbnail", notification.getAuthor_thumbnail());
                                                    intent.putExtra("author_url", notification.getAuthor_url());
                                                    intent.putExtra("author_id", notification.getAuthor_id());
                                                    intent.putExtra("from_notification", "from_notification");
                                                    intent.putExtra("from_notification_comment", "from_notification_comment");
                                                    context.startActivity(intent);
                                                }
                                            }
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
        }

        // concerning group comments
        private void manageGroupComments(NotificationModel notification) {
            if (notification.isSimple_comment()) {
                // group
                Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                        .child(notification.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(notification.getGroup_id());

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get value
                            String groupName = user_groups.getGroup_name();

                            Query query = myRef.child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(notification.getMember_id());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                        assert objectMap != null;
                                        User member = Util_User.getUser(context, objectMap, dataSnapshot);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(member.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(profile_photo);
                                        }

                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                +context.getString(R.string.commented_on_your_post_in_the_group)
                                                + " <b>"+groupName+"</b>."));

                                        Query query = myRef
                                                .child(context.getString(R.string.dbname_group))
                                                .child(notification.getGroup_id())
                                                .orderByChild(notification.getPost_id_field())
                                                .equalTo(notification.getPost_id());

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds: snapshot.getChildren()) {

                                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                                                    assert objectMap != null;
                                                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                                    parent.setOnClickListener(view -> {
                                                        //  change notification background
                                                        if (!notification.isAllReadyOpenItem()) {
                                                            HashMap<String, Object> map = new HashMap<>();
                                                            map.put("allReadyOpenItem", true);

                                                            myRef.child(context.getString(R.string.dbname_notification))
                                                                    .child(user_id)
                                                                    .child(notification.getNotification_id())
                                                                    .updateChildren(map);

                                                            parent.setBackgroundColor(context.getColor(R.color.white));
                                                        }

                                                        if (relLayout_view_overlay != null)
                                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                        if (!model.isSuppressed()) {
                                                            Gson gson = new Gson();
                                                            String myGSon = gson.toJson(model);

                                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                            Intent intent;
                                                            if (notification.isFrom_shared_post()) {
                                                                if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                                    intent = new Intent(context, GroupChildRecyclerCommentShare.class);
                                                                    intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                                    intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                                } else {
                                                                    intent = new Intent(context, GroupCommentShare.class);
                                                                    intent.putExtra(notification.getPost_type(), notification.getPost_type());
                                                                }

                                                            } else if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                                intent = new Intent(context, GroupChildRecyclerComment.class);
                                                                intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                                intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                            } else {
                                                                intent = new Intent(context, GroupComment.class);
                                                                intent.putExtra(notification.getPost_type(), notification.getPost_type());
                                                                if (notification.isCircle_group_cover())
                                                                    intent.putExtra("from_group_cover", "from_group_cover");
                                                                if (notification.isCircle_group_background_profile())
                                                                    intent.putExtra("from_group_background_profile", "from_group_background_profile");

                                                            }

                                                            intent.putExtra(notification.getCategory_of_post(), myGSon);
                                                            intent.putExtra(notification.getPost_type(), notification.getPost_type());
                                                            intent.putExtra("from_notification_comment", "from_notification_comment");
                                                            context.startActivity(intent);
                                                        }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else if (notification.isComment_response()) {
                // group
                Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                        .child(notification.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(notification.getGroup_id());

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get value
                            String groupName = user_groups.getGroup_name();

                            Query query = myRef.child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(notification.getMember_id());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                        assert objectMap != null;
                                        User member = Util_User.getUser(context, objectMap, dataSnapshot);

                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(member.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(profile_photo);
                                        }

                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                +context.getString(R.string.responded_to_your_comment_in_the_group)
                                                + " <b>"+groupName+"</b>."));

                                        Query query = myRef
                                                .child(context.getString(R.string.dbname_group))
                                                .child(notification.getGroup_id())
                                                .orderByChild(notification.getPost_id_field())
                                                .equalTo(notification.getPost_id());

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds: snapshot.getChildren()) {
                                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                    assert objectMap != null;
                                                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                                                    parent.setOnClickListener(view -> {
                                                        //  change notification background
                                                        if (!notification.isAllReadyOpenItem()) {
                                                            HashMap<String, Object> map = new HashMap<>();
                                                            map.put("allReadyOpenItem", true);

                                                            myRef.child(context.getString(R.string.dbname_notification))
                                                                    .child(user_id)
                                                                    .child(notification.getNotification_id())
                                                                    .updateChildren(map);

                                                            parent.setBackgroundColor(context.getColor(R.color.white));
                                                        }

                                                        if (relLayout_view_overlay != null)
                                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                                        if (!model.isSuppressed()) {
                                                            Gson gson = new Gson();
                                                            String myGson = gson.toJson(model);

                                                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                            Intent intent;
                                                            if (notification.isFrom_shared_post()) {

                                                                if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                                    intent = new Intent(context, GroupChildRecyclerResponseCommentShare.class);
                                                                    intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                                    intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                                } else {
                                                                    intent = new Intent(context, GroupResponseCommentShare.class);
                                                                    intent.putExtra("comment_key", notification.getCommentKey());
                                                                    intent.putExtra("comment", notification.getComment());
                                                                    intent.putExtra("userID", notification.getUser_id());
                                                                    intent.putExtra("media_comment_url", notification.getUrl());
                                                                    intent.putExtra("media_comment_thumbnail", notification.getThumbnail());
                                                                    intent.putExtra("time", notification.getDate_comment_created());
                                                                    if (model.isGroup_share_top_bottom_circle_image()
                                                                            || model.isGroup_share_single_bottom_image_une() || model.isGroup_share_single_bottom_video_une()
                                                                            || model.isGroup_share_top_bottom_image_une() || model.isGroup_share_top_bottom_video_une()) {
                                                                        intent.putExtra("comment_image_une", myGson);

                                                                    } else if (model.isGroup_share_single_bottom_recycler() || model.isGroup_share_top_bottom_recycler()) {
                                                                        intent.putExtra("comment_recycler", myGson);

                                                                    } else if (model.isGroup_share_single_bottom_image_double() || model.isGroup_share_single_bottom_video_double()
                                                                            || model.isGroup_share_top_bottom_image_double() || model.isGroup_share_top_bottom_video_double()) {
                                                                        intent.putExtra("comment_image_double", myGson);

                                                                    } else if (model.isGroup_share_single_bottom_response_image_double() || model.isGroup_share_single_bottom_response_video_double()
                                                                            || model.isGroup_share_top_bottom_response_image_double() || model.isGroup_share_top_bottom_response_video_double()) {
                                                                        intent.putExtra("comment_reponse_image_double", myGson);

                                                                    }
                                                                }

                                                            } else if (!TextUtils.isEmpty(notification.getRecyclerview_photo_id())) {
                                                                intent = new Intent(context, GroupChildRecyclerResponseComment.class);
                                                                intent.putExtra("recyclerview_photo_id", notification.getRecyclerview_photo_id());
                                                                intent.putExtra("recyclerview_fieldLike", notification.getRecyclerview_fieldLike());

                                                            } else {
                                                                intent = new Intent(context, GroupResponseComment.class);
                                                                intent.putExtra("comment_key", notification.getCommentKey());
                                                                intent.putExtra("comment", notification.getComment());
                                                                intent.putExtra("userID", notification.getUser_id());
                                                                intent.putExtra("media_comment_url", notification.getUrl());
                                                                intent.putExtra("media_comment_thumbnail", notification.getThumbnail());
                                                                intent.putExtra("time", notification.getDate_comment_created());

                                                                // response comment
                                                            }
                                                            intent.putExtra("post_id_field", notification.getPost_id_field());
                                                            intent.putExtra(notification.getCategory_of_post(), myGson);
                                                            intent.putExtra("post_type", notification.getPost_type());
                                                            intent.putExtra("post_id", notification.getPost_id());
                                                            intent.putExtra("recyclerview_photo_id_upload", notification.getRecyclerview_photo_id());
                                                            intent.putExtra("recyclerview_fieldLike_upload", notification.getRecyclerview_fieldLike());
                                                            intent.putExtra("admin_master", notification.getAdmin_master());
                                                            intent.putExtra("the_user_who_posted_id", notification.getUser_id());
                                                            intent.putExtra("group_id", notification.getGroup_id());
                                                            intent.putExtra("author_comment", notification.getAuthor_comment());
                                                            intent.putExtra("author_commentKey", notification.getAuthor_commentKey());
                                                            intent.putExtra("author_comment_date_create", notification.getAuthor_comment_date_create());
                                                            intent.putExtra("author_thumbnail", notification.getAuthor_thumbnail());
                                                            intent.putExtra("author_url", notification.getAuthor_url());
                                                            intent.putExtra("author_id", notification.getAuthor_id());
                                                            intent.putExtra("from_notification", "from_notification");
                                                            context.startActivity(intent);
                                                        }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (notification.isHas_posted()) {
                // group
                Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                        .child(notification.getAdmin_master())
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(notification.getGroup_id());

                myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            // get value
                            String groupName = user_groups.getGroup_name();

                            Query query = myRef.child(context.getString(R.string.dbname_users))
                                    .orderByChild(context.getString(R.string.field_user_id))
                                    .equalTo(notification.getMember_id());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                        assert objectMap != null;
                                        User member = Util_User.getUser(context, objectMap, dataSnapshot);


                                        if (!context.isFinishing()) {
                                            Glide.with(context.getApplicationContext())
                                                    .load(member.getProfileUrl())
                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                    .into(profile_photo);
                                        }

                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                +context.getString(R.string.has_made_a_post_in_the_group)
                                                + " <b>"+groupName+"</b>."));

                                        parent.setOnClickListener(view -> {
                                            //  change notification background
                                            if (!notification.isAllReadyOpenItem()) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("allReadyOpenItem", true);

                                                myRef.child(context.getString(R.string.dbname_notification))
                                                        .child(user_id)
                                                        .child(notification.getNotification_id())
                                                        .updateChildren(map);

                                                parent.setBackgroundColor(context.getColor(R.color.white));
                                            }

                                            if (relLayout_view_overlay != null)
                                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                                            if (!user_groups.isSuppressed()) {
                                                Gson gson = new Gson();
                                                String myJson = gson.toJson(user_groups);

                                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                Intent intent;
                                                if (user_groups.getAdmin_master().equals(user_id)) {
                                                    intent = new Intent(context, GroupViewAdmin.class);

                                                } else {
                                                    intent = new Intent(context, GroupViewProfile.class);
                                                }
                                                intent.putExtra("user_groups", myJson);
                                                context.startActivity(intent);

                                            } else
                                                Toast.makeText(context, context.getString(R.string.this_group_has_been_deleted), Toast.LENGTH_LONG).show();

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
        }

        // invitation to be member in group
        private void getInvitationToBeMemberInGroup(NotificationModel notification) {
            // group
            Query myQuery = myRef.child(context.getString(R.string.dbname_user_group))
                    .child(notification.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(notification.getGroup_id());
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                        // get value
                        String groupName = user_groups.getGroup_name();

                        // notification to admin from member
                        Query query = myRef.child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(notification.getMember_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User member = Util_User.getUser(context, objectMap, ds);

                                    if (notification.isAccepted_invitation_to_join_the_group()) {
                                        Glide.with(context.getApplicationContext())
                                                .load(member.getProfileUrl())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(profile_photo);

                                        // when group is public because when is private you can't go in
                                        if (user_groups.isGroup_public())
                                            getGoToInvitedMemberProfile_public_group(parent, notification, user_groups);

                                        if (user_groups.isGroup_private())
                                            getGoToInvitedMemberProfile_private_group(parent, notification);

                                    } else {
                                        Glide.with(context.getApplicationContext())
                                                .load(user_groups.getProfile_photo())
                                                .placeholder(R.drawable.ic_baseline_person_24)
                                                .into(profile_photo);

                                        getGroup(parent, notification, user_groups);

                                    }
                                }

                                if (!snapshot.exists()) {
                                    Glide.with(context.getApplicationContext())
                                            .load(user_groups.getProfile_photo())
                                            .placeholder(R.drawable.ic_baseline_person_24)
                                            .into(profile_photo);

                                    getGroup(parent, notification, user_groups);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // get the invited moderator member
                        Query query3 = myRef.child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(notification.getMember_id());

                        query3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User member = Util_User.getUser(context, objectMap, ds);

                                    // invitation in group
                                    if (notification.isInvite_to_join_the_group()) {

                                        if (user_groups.isGroup_public()) {
                                            message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                    +context.getString(R.string.invite_you_to_join_the_group)+" "+context.getString(R.string.txt_public)+
                                                    " <b>"+groupName+"</b>."));

                                        } else if (user_groups.isGroup_private()) {
                                            message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                    +context.getString(R.string.invite_you_to_join_the_group)+" "+context.getString(R.string.txt_private)+
                                                    " <b>"+groupName+"</b>."));

                                        }


                                        if (!notification.isHide_buttons()) {
                                            // hide buttons
                                            linLayout_buttons_join_group.setVisibility(View.VISIBLE);

                                        } else {
                                            // show the text "invitation accepted" or "invitation refused"
                                            if (notification.isAccepted_invitation_to_join_the_group()) {
                                                invitation_accepted.setVisibility(View.VISIBLE);
                                                invitation_accepted.setText(context.getString(R.string.invitation_accepted));

                                            } else {
                                                invitation_accepted.setVisibility(View.VISIBLE);
                                                invitation_accepted.setText(context.getString(R.string.invitation_refused));
                                            }
                                        }
                                        // accepted
                                        relLayout_button_join_group.setOnClickListener(view -> {
                                            // verify if user has not already click accept or refuse in group activity

                                            if (!notification.isAccepted_invitation_to_join_the_group()
                                                    && !notification.isRefuse_invitation_to_join_the_group()) {
                                                linLayout_buttons_join_group.setVisibility(View.GONE);
                                                invitation_accepted.setVisibility(View.VISIBLE);
                                                invitation_accepted.setText(context.getString(R.string.invitation_accepted));

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

                                                // remove person who has been invited in the list
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                                        .child(user_groups.getGroup_id())
                                                        .child(user_id)
                                                        .removeValue();

                                            } else {
                                                linLayout_buttons_join_group.setVisibility(View.GONE);
                                            }
                                        });

                                        // refused
                                        relLayout_button_delete_invitation.setOnClickListener(view -> {
                                            // verify if user has not already click accept or refuse in group activity

                                            Query query = myRef.child(context.getString(R.string.dbname_notification))
                                                    .child(user_id)
                                                    .orderByChild(context.getString(R.string.field_user_id))
                                                    .equalTo(user_id);
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                                        assert objectMap != null;
                                                        NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, dataSnapshot);

                                                        if (!notification.isAccepted_invitation_to_join_the_group()
                                                                && !notification.isRefuse_invitation_to_join_the_group()) {

                                                            linLayout_buttons_join_group.setVisibility(View.GONE);
                                                            invitation_accepted.setVisibility(View.VISIBLE);
                                                            invitation_accepted.setText(context.getString(R.string.invitation_refused));

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

                                                            // remove person who has been invited in the list
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child(context.getString(R.string.dbname_group_list_of_person_invited_in_group))
                                                                    .child(user_groups.getGroup_id())
                                                                    .child(user_id)
                                                                    .removeValue();

                                                        } else {
                                                            linLayout_buttons_join_group.setVisibility(View.GONE);
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        });

                                    } else if (notification.isAccepted_invitation_to_join_the_group()) {
                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                +context.getString(R.string.accepted_you_invitation_to_become_member_of_group)+
                                                " <b>"+groupName+"</b>."));

                                    } else if (notification.isRefuse_invitation_to_join_the_group()) {
                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                +context.getString(R.string.refused_you_invitation_to_become_member_of_group)+
                                                " <b>"+groupName+"</b>."));

                                    }
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

        // go to invited member profile
        private void getGoToInvitedMemberProfile_public_group(View parent, NotificationModel notification, UserGroups user_groups) {
            parent.setOnClickListener(view -> {
                //  change notification background
                if (!notification.isAllReadyOpenItem()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("allReadyOpenItem", true);

                    myRef.child(context.getString(R.string.dbname_notification))
                            .child(user_id)
                            .child(notification.getNotification_id())
                            .updateChildren(map);

                    parent.setBackgroundColor(context.getColor(R.color.white));
                }

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(user_groups);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupProfileMember.class);
                intent.putExtra("user_groups", myGson);
                intent.putExtra("userID", notification.getMember_id());
                intent.putExtra("group_id", user_groups.getGroup_id());
                context.startActivity(intent);
            });
        }

        // go to invited member profile
        private void getGoToInvitedMemberProfile_private_group(View parent, NotificationModel notification) {
            parent.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Query query = myRef.child(context.getString(R.string.dbname_users))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(notification.getMember_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User member = Util_User.getUser(context, objectMap, ds);

                            //  change notification background
                            if (!notification.isAllReadyOpenItem()) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("allReadyOpenItem", true);

                                myRef.child(context.getString(R.string.dbname_notification))
                                        .child(user_id)
                                        .child(notification.getNotification_id())
                                        .updateChildren(map);

                                parent.setBackgroundColor(context.getColor(R.color.white));
                            }

                            Gson gson = new Gson();
                            String myGson = gson.toJson(member);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, ViewProfile.class);
                            intent.putExtra("user", myGson);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });
        }

        // invitation to be admin
        private void getTheAdminWhoInvitedProfile(NotificationModel notification) {
            // get the admin who sanctioned profile
            Query query2 = myRef.child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(notification.getAdmin_in_group());
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        String adminName = user.getUsername();

                        // group
                        Query query = myRef.child(context.getString(R.string.dbname_user_group))
                                .child(notification.getAdmin_master())
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(notification.getGroup_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                    assert objectMap != null;
                                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                    // get value
                                    String groupName = user_groups.getGroup_name();

                                    // invite like admin
                                    if (notification.isEnter_invitation_admin()) {

                                        if (notification.isAdminInvitation()) {
                                            message_text.setText(Html.fromHtml(" <b>"+adminName+"</b> "
                                                    +context.getString(R.string.invite_you)
                                                    +context.getString(R.string.to_become_an_admin_in_the_group)+" <b>"+groupName+"</b>."));

                                            // show the text "invitation accepted" or "invitation refused"
                                            if (notification.isAccept_admin_invitation()) {
                                                invitation_accepted.setVisibility(View.VISIBLE);
                                                invitation_accepted.setText(context.getString(R.string.invitation_accepted));

                                            } else if (notification.isRefuse_admin_invitation()) {
                                                invitation_accepted.setVisibility(View.VISIBLE);
                                                invitation_accepted.setText(context.getString(R.string.invitation_refused));
                                            }

                                        } else if (notification.isCancel_invitation_admin()) {
                                            message_text.setText(Html.fromHtml(" <b>"+adminName+"</b> "
                                                    +context.getString(R.string.has_canceled_your_invitation)
                                                    +context.getString(R.string.to_become_an_admin_in_the_group)+" <b>"+groupName+"</b>."));

                                        } else if (notification.isRemove_by_another_admin_like_admin()) {
                                            message_text.setText(Html.fromHtml(" <b>"+adminName+"</b> "
                                                    +context.getString(R.string.delete_you_like_admin_from_group)+" <b>"+groupName+"</b>."));

                                        }




                                        // notification to admin from member
                                        Query query = myRef.child(context.getString(R.string.dbname_users))
                                                .orderByChild(context.getString(R.string.field_user_id))
                                                .equalTo(notification.getMember_id());

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds: snapshot.getChildren()) {
                                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                    assert objectMap != null;
                                                    User member = Util_User.getUser(context, objectMap, ds);

                                                    if (notification.getAdmin_in_group().equals(user_id)) {
                                                        if (!context.isFinishing()) {
                                                            Glide.with(context.getApplicationContext())
                                                                    .load(member.getProfileUrl())
                                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                                    .into(profile_photo);
                                                        }
                                                    } else {
                                                        if (!context.isFinishing()) {
                                                            Glide.with(context.getApplicationContext())
                                                                    .load(user_groups.getProfile_photo())
                                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                                    .into(profile_photo);
                                                        }
                                                    }

                                                    if (notification.isRefuse_admin_invitation()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                                +context.getString(R.string.refuse_your_invitation_to_become_an_admin)+
                                                                " <b>"+groupName+"</b>."));

                                                    } else if (notification.isAccept_admin_invitation()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                                +context.getString(R.string.accept_your_invitation_to_become_an_admin)+
                                                                " <b>"+groupName+"</b>. "));

                                                    } else if (notification.isRemove_yourself_like_admin()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                                +context.getString(R.string.delete_his_self_like_admin_from_group)+" <b>"+groupName+"</b>."));

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        getGroupOrMemberProfile(parent, notification, user_groups);
                                    }

                                    // add like moderator
                                    if (notification.isEnter_moderation()) {

                                        if (notification.isAddModerator()) {
                                            message_text.setText(Html.fromHtml(" <b>"+adminName+"</b> "
                                                    +context.getString(R.string.has_add_you)+" "+
                                                    context.getString(R.string.ass_moderator_in_the_group)+" <b>"+groupName+"</b>."));

                                        } else if (notification.isCancel_adding_moderator()) {
                                            message_text.setText(Html.fromHtml(" <b>"+adminName+"</b> "
                                                    +context.getString(R.string.has_remove_to_you_the_oderator_rule)+" "+" <b>"+groupName+"</b>."));

                                        } else if (notification.isRemove_by_another_admin_like_moderator()) {
                                            message_text.setText(Html.fromHtml(" <b>"+adminName+"</b> "
                                                    +context.getString(R.string.delete_you_like_moderator_from_group)+" <b>"+groupName+"</b>."));

                                        }

                                        // get the invited moderator member
                                        Query query = myRef.child(context.getString(R.string.dbname_users))
                                                .orderByChild(context.getString(R.string.field_user_id))
                                                .equalTo(notification.getMember_id());
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds: snapshot.getChildren()) {
                                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                                    assert objectMap != null;
                                                    User member = Util_User.getUser(context, objectMap, ds);

                                                    if (notification.getAdmin_in_group().equals(user_id)) {
                                                        if (!context.isFinishing()) {
                                                            Glide.with(context.getApplicationContext())
                                                                    .load(member.getProfileUrl())
                                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                                    .into(profile_photo);
                                                        }
                                                    } else {
                                                        if (!context.isFinishing()) {
                                                            Glide.with(context.getApplicationContext())
                                                                    .load(user_groups.getProfile_photo())
                                                                    .placeholder(R.drawable.ic_baseline_person_24)
                                                                    .into(profile_photo);
                                                        }
                                                    }

                                                    if (notification.isRefuse_moderator_invitation()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                                +context.getString(R.string.refuse_your_invitation_to_become_a_moderator)+
                                                                " <b>"+groupName+"</b>."));

                                                    } else if (notification.isAccept_moderator_invitation()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                                +context.getString(R.string.accept_your_invitation_to_become_a_moderator)+
                                                                " <b>"+groupName+"</b>. "));

                                                    } else if (notification.isRemove_yourself_like_moderator()) {
                                                        message_text.setText(Html.fromHtml(" <b>"+member.getUsername()+"</b> "
                                                                +context.getString(R.string.delete_his_self_like_moderator_from_group)+" <b>"+groupName+"</b>."));

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        getGroupOrMemberProfile(parent, notification, user_groups);
                                    }
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

        // go to group activity when clicked on view
        private void getGroup(View parent, NotificationModel notification, UserGroups user_groups) {
            parent.setOnClickListener(view -> {
                //  change notification background
                if (!notification.isAllReadyOpenItem()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("allReadyOpenItem", true);

                    myRef.child(context.getString(R.string.dbname_notification))
                            .child(user_id)
                            .child(notification.getNotification_id())
                            .updateChildren(map);

                    parent.setBackgroundColor(context.getColor(R.color.white));
                }

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                if (!user_groups.isSuppressed()) {
                    if (user_groups.getAdmin_master().equals(user_id)) {
                        Gson gson = new Gson();
                        String myJson = gson.toJson(user_groups);
                        Intent intent = new Intent(context, GroupViewAdmin.class);
                        intent.putExtra("user_groups", myJson);
                        context.startActivity(intent);

                    } else {
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(user_groups.getGroup_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (following.isAdminInGroup() || following.isModerator()) {
                                        intent = new Intent(context, GroupViewAdmin.class);

                                    } else {
                                        intent = new Intent(context, GroupViewProfile.class);
                                    }
                                    intent.putExtra("user_groups", myJson);
                                    context.startActivity(intent);
                                }

                                if (!snapshot.exists()) {
                                    if (user_groups.isGroup_private()) {
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupPrivateViewProfileDiscover.class);
                                        intent.putExtra("user_groups", myGson);
                                        context.startActivity(intent);

                                    } else {
                                        Gson gson = new Gson();
                                        String myJson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewProfile.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else
                    Toast.makeText(context, context.getString(R.string.this_group_has_been_deleted), Toast.LENGTH_LONG).show();
            });
        }

        // go to group when clicked by a member
        // go to member profile when clicked by an admin
        private void getGroupOrMemberProfile(View parent, NotificationModel notification, UserGroups user_groups) {
            parent.setOnClickListener(view -> {
                //  change notification background
                if (!notification.isAllReadyOpenItem()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("allReadyOpenItem", true);

                    myRef.child(context.getString(R.string.dbname_notification))
                            .child(user_id)
                            .child(notification.getNotification_id())
                            .updateChildren(map);

                    parent.setBackgroundColor(context.getColor(R.color.white));
                }

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                if (!user_groups.isSuppressed()) {
                    // admin clicked
                    if (notification.getAdmin_in_group().equals(user_id)) {
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user_groups);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("user_groups", myGson);
                        intent.putExtra("userID", notification.getMember_id());
                        intent.putExtra("group_id", user_groups.getGroup_id());
                        context.startActivity(intent);

                    } // member clicked
                    else {
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(user_groups.getGroup_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (following.isAdminInGroup() || following.isModerator()) {
                                        intent = new Intent(context, GroupViewAdmin.class);

                                    } else {
                                        intent = new Intent(context, GroupViewProfile.class);
                                    }
                                    intent.putExtra("user_groups", myJson);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } else
                    Toast.makeText(context, context.getString(R.string.this_group_has_been_deleted), Toast.LENGTH_LONG).show();
            });
        }

        // go to group activity when clicked on view
        private void getBanMemberFromGroup(View parent, NotificationModel notification, User user, UserGroups user_groups) {
            parent.setOnClickListener(view -> {
                //  change notification background
                if (!notification.isAllReadyOpenItem()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("allReadyOpenItem", true);

                    myRef.child(context.getString(R.string.dbname_notification))
                            .child(user_id)
                            .child(notification.getNotification_id())
                            .updateChildren(map);

                    parent.setBackgroundColor(context.getColor(R.color.white));
                }

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

                dialog_title.setText(Html.fromHtml(context.getString(R.string.you_are_banned)));
                dialog_text.setText(Html.fromHtml(user.getUsername()+" "+context.getString(R.string.admin_has_notice_that_you_do_not_respect_the_group_operating_rules)
                        +" "+user_groups.getGroup_name()));

                negativeButton.setVisibility(View.GONE);
                positiveButton.setOnClickListener(view1 -> d.dismiss());
            });
        }

        // date of the day
        private void setDate(NotificationModel previousNotification, NotificationModel notification, int position) {
            Date previousNotificationDate = null;
            if (previousNotification != null) {
                previousNotificationDate = new Date(previousNotification.getDate_created());
            }

            Date currentNotificationDate = new Date(notification.getDate_created());
            // it's today. show the label "today"
            if (TimeUtils.isDateToday(notification.getDate_created())) {
                day_date.setText(itemView.getContext().getString(R.string.today));
            } else {
                // it's not today. shows the week of day label
                day_date.setText(TimeUtils.getFormattedTimestamp(itemView.getContext(), notification.getDate_created()));
            }

            // hides or shows the date label
            if (previousNotificationDate != null && position > 0) {
                // compare current date with previous date
                if (TimeUtils.getDayOfWeek(currentNotificationDate).equals(TimeUtils.getDayOfWeek(previousNotificationDate))) {
                    day_date.setVisibility(View.GONE);
                } else {
                    day_date.setVisibility(View.VISIBLE);
                }
            } else {
                day_date.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     */
    @SuppressLint("SetTextI18n")
    public void getTimestampDifference(long date, TextView tvDate){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        String tps;
        long time = (System.currentTimeMillis() - date);
        if (time >= 2*3600*24000) {
            tps = date_passe;
        } else if (isYesterday(date)) {
            tps = context.getString(R.string.yesterday);
        } else if (time >= 2*3600000) {
            tps = ((int)(time/(3600000)))+" "+context.getString(R.string.hours_ago);
        } else if (time >= 3600000) {
            tps = context.getString(R.string.an_hour_ago);
        } else if (time >= 120000) {
            tps = (int)(time/60000) + " "+context.getString(R.string.minutes_ago);
        } else {
            tps = context.getString(R.string.just_now);
        }
        tvDate.setText(tps);
    }

    public boolean isYesterday(long milliSeconds) {
        Calendar toCheck = Calendar.getInstance();
        toCheck.setTimeInMillis(milliSeconds); // your date

        Calendar yesterday = Calendar.getInstance(); // today
        yesterday.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        return yesterday.get(Calendar.YEAR) == toCheck.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == toCheck.get(Calendar.DAY_OF_YEAR);
    }
}

