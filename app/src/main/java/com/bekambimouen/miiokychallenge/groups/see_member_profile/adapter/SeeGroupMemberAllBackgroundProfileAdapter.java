package com.bekambimouen.miiokychallenge.groups.see_member_profile.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.groups.comment.GroupComment;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SeeGroupMemberAllBackgroundProfileAdapter extends RecyclerView.Adapter<SeeGroupMemberAllBackgroundProfileAdapter.ViewHolder> {
    private static final String TAG = "FullImageAdapter";

    // vars
    private final AppCompatActivity context;
    private final List<BattleModel> list;
    private final RelativeLayout relLayout_view_overlay;

    public SeeGroupMemberAllBackgroundProfileAdapter(AppCompatActivity context, List<BattleModel> list, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_full_image_user_profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BattleModel url = list.get(position);
        holder.bind(url);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final SmallBangView like_heart;
        private final ImageView image;
        private final TextView tv_like;
        private final PhotoView iv_photo;
        private final ImageView menu_item;
        private final TextView username;
        private final TextView tps_publication;
        private final TextView the_user;
        private final TextView number_of_likes;
        private final TextView number_of_comments;
        private final RelativeLayout relLayout_username;
        private final RelativeLayout relLayout_write_to;
        private final RelativeLayout relLayout_count_actions;
        private final LinearLayout linLayout_all_data;
        private final LinearLayout linLayout_count_like;
        private final LinearLayout linLayout_count_comment;
        private final LinearLayout linLayout_like;
        private final LinearLayout linLayout_comment;
        private final LinearLayout linLayout_share;

        // vars
        private BattleModel mPhoto;
        private User mCurrentUser;
        private StringBuilder mUsers;
        private StringBuilder updateLikeUsers;
        private boolean isShown = true;
        private boolean mLikedByCurrentUser;
        private final ArrayList<String> liker_list;
        private int likes_count;
        private int comments_count;
        private int shares_count;

        // firebase
        private final DatabaseReference myRef;
        private final String user_id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            like_heart = itemView.findViewById(R.id.like_heart);
            image = itemView.findViewById(R.id.image);
            tv_like = itemView.findViewById(R.id.tv_like);
            menu_item = itemView.findViewById(R.id.menu_item);
            username = itemView.findViewById(R.id.username);
            tps_publication = itemView.findViewById(R.id.tps_publication);
            the_user = itemView.findViewById(R.id.the_user);
            number_of_likes = itemView.findViewById(R.id.number_of_likes);
            number_of_comments = itemView.findViewById(R.id.number_of_comments);
            relLayout_username = itemView.findViewById(R.id.relLayout_username);
            relLayout_write_to = itemView.findViewById(R.id.relLayout_write_to);
            relLayout_count_actions = itemView.findViewById(R.id.relLayout_count_actions);
            linLayout_all_data = itemView.findViewById(R.id.linLayout_all_data);
            linLayout_count_like = itemView.findViewById(R.id.linLayout_count_like);
            linLayout_count_comment = itemView.findViewById(R.id.linLayout_count_comment);
            linLayout_like = itemView.findViewById(R.id.linLayout_like);
            linLayout_comment = itemView.findViewById(R.id.linLayout_comment);
            linLayout_share = itemView.findViewById(R.id.linLayout_share);

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            liker_list = new ArrayList<>();
        }

        private void bind(BattleModel model) {
            mPhoto = model;

            username.setText("");
            tps_publication.setText("");
            number_of_likes.setText("0");
            number_of_comments.setText("0");

            if(liker_list != null){
                liker_list.clear();
            }

            if (model.getUser_id().equals(user_id))
                relLayout_write_to.setVisibility(View.GONE);

            Glide.with(context.getApplicationContext())
                    .load(model.getGroup_user_background_full_profile_url())
                    .placeholder(R.color.black)
                    .into(iv_photo);

            iv_photo.setOnClickListener(view -> {
                if (isShown) {
                    linLayout_all_data.setVisibility(View.GONE);
                    menu_item.setVisibility(View.GONE);
                    isShown = false;
                } else {
                    linLayout_all_data.setVisibility(View.VISIBLE);
                    menu_item.setVisibility(View.VISIBLE);
                    isShown = true;
                }
            });

            setLikes();
            setComments();
            getCurrentUser();
            getLikesString();
            updateLike();

            long tv_date = model.getDate_created();
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

            //get the profile image and username
            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(model.getUser_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        String name = user.getUsername();

                        username.setText(name);
                        the_user.setText(name);

                        relLayout_write_to.setOnClickListener(view -> {
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

                        Query query4 = myRef
                                .child(context.getString(R.string.dbname_user_group))
                                .child(mPhoto.getAdmin_master())
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(mPhoto.getGroup_id());
                        query4.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                    relLayout_username.setOnClickListener(v -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupProfileMember.class);
                                        intent.putExtra("user_groups", myGson);
                                        intent.putExtra("userID", user.getUser_id());
                                        intent.putExtra("group_id", user_groups.getGroup_id());
                                        context.startActivity(intent);
                                    });

                                    // menu_item
                                    BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                                    menu_item.setOnClickListener(view -> {
                                        if (bottomSheet.isAdded())
                                            return;
                                        Gson gson = new Gson();
                                        String myJson = gson.toJson(user_groups);

                                        Bundle args = new Bundle();
                                        args.putParcelable("battle_model", mPhoto);
                                        args.putString("user_groups", myJson);
                                        args.putString("group", "group");
                                        bottomSheet.setArguments(args);
                                        bottomSheet.show(context.getSupportFragmentManager(),
                                                "TAG");
                                    });

                                    // share
                                    BottomSheetSharePublication bottomSheetShare = new BottomSheetSharePublication(context, mPhoto, mPhoto.getUser_id(),
                                            mPhoto.getGroup_full_profile_photo(), mPhoto.getPhoto_id(),
                                            "", "image_une", false);
                                    linLayout_share.setOnClickListener(view -> {
                                        // prevent two clicks
                                        Util.preventTwoClick(linLayout_share);
                                        try {
                                            if (bottomSheetShare.isAdded())
                                                return;
                                            Bundle bundle = new Bundle();
                                            bundle.putString("item_view", "group_cover");
                                            bottomSheetShare.setArguments(bundle);
                                            bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

                                        } catch (IllegalStateException e) {
                                            Log.d(TAG, "onDataChange: "+e.getMessage());
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
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            linLayout_comment.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGSon = gson.toJson(model);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupComment.class);
                intent.putExtra("comment_image_une", myGSon);
                intent.putExtra("image_une", "image_une");
                intent.putExtra("from_group_background_profile", "from_group_background_profile");
                intent.putExtra("from_full_image", "from_full_image");
                context.startActivity(intent);
            });

            linLayout_count_comment.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGSon = gson.toJson(model);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupComment.class);
                intent.putExtra("comment_image_une", myGSon);
                intent.putExtra("image_une", "image_une");
                intent.putExtra("from_group_background_profile", "from_group_background_profile");
                intent.putExtra("from_full_image", "from_full_image");
                context.startActivity(intent);
            });

            linLayout_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Prevent Two Click
                    Util.preventTwoClick(v);

                    if (like_heart.isSelected()) {
                        like_heart.setSelected(false);
                        image.setImageResource(R.drawable.ic_heart_white);
                        like_heart.likeAnimation(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                removeLike();
                                tv_like.setTextColor(context.getColor(R.color.white));
                            }
                        });

                    } else {
                        like_heart.setSelected(true);
                        image.setImageResource(R.drawable.ic_heart_red);
                        like_heart.likeAnimation(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (!mLikedByCurrentUser) {
                                    addNewLike();
                                    tv_like.setTextColor(context.getColor(R.color.red_600));
                                }
                            }
                        });
                    }
                }
            });
        }

        private void removeLike() {
            Log.d(TAG, "onDoubleTap: single tap detected.");
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_media))
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {

                        String keyID = ds.getKey();

                        //case1: Then user already liked the photo
                        if (mLikedByCurrentUser &&
                                Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                        .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                                .getCurrentUser()).getUid())) {

                            // update like count
                            int count = Integer.parseInt(number_of_likes.getText().toString());
                            String str = String.valueOf(count-1);
                            if (str.equals("0"))
                                number_of_likes.setVisibility(View.GONE);
                            number_of_likes.setText(str);

                            // remove like points
                            removeLikePoints();

                            assert keyID != null;
                            myRef.child(context.getString(R.string.dbname_group_media))
                                    .child(mPhoto.getPhoto_id())
                                    .child(context.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            myRef.child(context.getString(R.string.dbname_group))
                                    .child(mPhoto.getGroup_id())
                                    .child(mPhoto.getPhoto_id())
                                    .child(context.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            getLikesString();
                            updateLike();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void addNewLike(){
            Log.d(TAG, "addNewLike: adding new like");
            // update like count
            int count = Integer.parseInt(number_of_likes.getText().toString());
            String str = String.valueOf(count+1);
            if (!str.equals("0")) {
                number_of_likes.setVisibility(View.VISIBLE);
                linLayout_count_like.setVisibility(View.VISIBLE);
            }
            if (relLayout_count_actions.getVisibility() != View.VISIBLE)
                relLayout_count_actions.setVisibility(View.VISIBLE);
            number_of_likes.setText(str);

            // add like points
            addLikePoints();

            // add new like
            String newLikeID = myRef.push().getKey();
            Like like = new Like();
            like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            assert newLikeID != null;
            myRef.child(context.getString(R.string.dbname_group_media))
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_likes))
                    .child(newLikeID)
                    .setValue(like);

            myRef.child(context.getString(R.string.dbname_group))
                    .child(mPhoto.getGroup_id())
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_likes))
                    .child(newLikeID)
                    .setValue(like);

            // affichage Ã  l'Ã©cran
            getLikesString();
            updateLike();
        }

        private void getLikesString(){
            Log.d(TAG, "getLikesString: getting likes string");
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_media))
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_likes));

            // on parcours tous les likes
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers = new StringBuilder();

                    for (DataSnapshot ds: snapshot.getChildren()) {

                        // ici on rÃ©cupÃ¨re l'identifiant du likeur et le like
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                        Query query1 = reference1
                                .child(context.getString(R.string.dbname_users))
                                // comparaison des ID
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                    mUsers.append(user.getUsername());
                                    mUsers.append(",");
                                }

                                // vÃ©rifie si c'est l'utilistateur courant a likÃ©
                                mLikedByCurrentUser = mUsers.toString().contains(mCurrentUser.getUsername() + ",");
                                setupLikeString();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(!snapshot.exists()){
                        mLikedByCurrentUser = false;
                        setupLikeString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void updateLike(){
            Log.d(TAG, "getLikesString: getting likes string");
            Query query = myRef
                    .child(context.getString(R.string.dbname_group_media))
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_likes));

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    updateLikeUsers = new StringBuilder();

                    for (DataSnapshot ds: snapshot.getChildren()) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                        Query query1 = reference1
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(Objects.requireNonNull(ds.getValue(Like.class)).getUser_id());

                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                    updateLikeUsers.append(user.getUsername());
                                    updateLikeUsers.append(",");
                                }

                                // vÃ©rifie si c'est l'utilistateur courant a likÃ©
                                mLikedByCurrentUser = updateLikeUsers.toString().contains(mCurrentUser.getUsername() + ",");
                                setupLikeString();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(!snapshot.exists()){
                        mLikedByCurrentUser = false;
                        setupLikeString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        private void setupLikeString() {
            if (mLikedByCurrentUser) {
                if (!like_heart.isSelected()) {
                    like_heart.setSelected(true);
                    image.setImageResource(R.drawable.ic_heart_red);
                    tv_like.setTextColor(context.getColor(R.color.red_600));
                }

            } else {
                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                    image.setImageResource(R.drawable.ic_heart_white);
                    tv_like.setTextColor(context.getColor(R.color.white));
                }
            }
        }

        private void getCurrentUser(){
            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        mCurrentUser = Util_User.getUser(context, objectMap, ds);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });
        }

        public void setLikes() {
            likes_count = 0;
            relLayout_count_actions.setVisibility(View.GONE);
            linLayout_count_like.setVisibility(View.INVISIBLE);

            Query query = myRef
                    .child(context.getString(R.string.dbname_group_media))
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        // add user id to the list
                        liker_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                        likes_count++;
                    }

                    if (likes_count >= 1) {
                        relLayout_count_actions.setVisibility(View.VISIBLE);
                        linLayout_count_like.setVisibility(View.VISIBLE);
                        number_of_likes.setText(String.valueOf(likes_count));

                        linLayout_count_like.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, Likers.class);
                            intent.putStringArrayListExtra("liker_list", liker_list);
                            context.startActivity(intent);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void setComments() {
            comments_count = 0;
            relLayout_count_actions.setVisibility(View.GONE);
            linLayout_count_comment.setVisibility(View.INVISIBLE);

            Query query = myRef
                    .child(context.getString(R.string.dbname_group_media))
                    .child(mPhoto.getPhoto_id())
                    .child(context.getString(R.string.field_comments));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        String keyID = ds.getKey();
                        comments_count++;
                        assert keyID != null;
                        Query query1 = myRef
                                .child(context.getString(R.string.dbname_group_media))
                                .child(mPhoto.getPhoto_id())
                                .child(context.getString(R.string.field_comments))
                                .child(keyID)
                                .child(context.getString(R.string.field_comments));

                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data: snapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: data: "+data);
                                    comments_count++;
                                }

                                if (comments_count >= 1) {
                                    relLayout_count_actions.setVisibility(View.VISIBLE);
                                    linLayout_count_comment.setVisibility(View.VISIBLE);
                                    number_of_comments.setText(String.valueOf(comments_count));
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

        // add post points
        private void addLikePoints() {
            if (mPhoto.getAdmin_master().equals(user_id)) {
                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(mPhoto.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            HashMap<String, Object> map = new HashMap<>();
                            int number_of_like_points = Integer.parseInt(user_groups.getLike_points());
                            map.put("like_points", number_of_like_points+1);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_user_group))
                                    .child(user_groups.getAdmin_master())
                                    .child(user_groups.getGroup_id())
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(mPhoto.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            HashMap<String, Object> map = new HashMap<>();

                            int number_of_like_points = Integer.parseInt(following.getLike_points());
                            map.put("like_points", number_of_like_points+1);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user_id)
                                    .child(mPhoto.getGroup_id())
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_followers))
                                    .child(mPhoto.getGroup_id())
                                    .child(user_id)
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        // remove post points
        private void removeLikePoints() {
            if (mPhoto.getAdmin_master().equals(user_id)) {
                Query query = myRef
                        .child(context.getString(R.string.dbname_user_group))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(mPhoto.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                            HashMap<String, Object> map = new HashMap<>();
                            int number_of_like_points = Integer.parseInt(user_groups.getLike_points());
                            map.put("like_points", number_of_like_points-1);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_user_group))
                                    .child(user_groups.getAdmin_master())
                                    .child(user_groups.getGroup_id())
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                Query query = myRef
                        .child(context.getString(R.string.dbname_group_following))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(mPhoto.getGroup_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                            assert objectMap != null;
                            GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                            HashMap<String, Object> map = new HashMap<>();

                            int number_of_like_points = Integer.parseInt(following.getLike_points());
                            map.put("like_points", number_of_like_points-1);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user_id)
                                    .child(mPhoto.getGroup_id())
                                    .updateChildren(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_followers))
                                    .child(mPhoto.getGroup_id())
                                    .child(user_id)
                                    .updateChildren(map);
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

