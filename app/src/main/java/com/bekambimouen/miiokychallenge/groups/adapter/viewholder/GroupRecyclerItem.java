package com.bekambimouen.miiokychallenge.groups.adapter.viewholder;

import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Crushers;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.groups.bottomsheet.GroupBottomSheetAddComment;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.child_recyclerview.GroupViewRecycler;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.github.kshitij_jain.instalike.InstaLikeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRecyclerItem extends RecyclerView.ViewHolder {
    private static final String TAG = "RecyclerItemDisplay";

    // widgets
    private final ImageView conner_big_star_icon;
    private final InstaLikeView insta_star_view;
    private final SmallBangView like_star;
    private final ImageView unlike_star_image;
    private final SmallBangView like_heart;
    private final ImageView image;
    private final View view_online;
    public final ImageView menu_item;
    public final CircleImageView profil_photo;
    private final TextView nber_of_crush;
    private final TextView crush_msg;
    private final TextView tps_publication;
    private final TextView username;
    private final TextView translate_comment;
    public final RelativeLayout relLayout_username;
    private final RelativeLayout relLayout_tv_admin;
    private final ViewMoreTextView caption;
    private final FrameLayout frameLayout;
    public final LinearLayout linLayout_share_only;
    public final LinearLayout linLayout_possibility_action;

    private final TextView number_of_likes;
    private final TextView number_of_comments;
    private final TextView number_of_share;
    private final LinearLayout linLayout_like;
    private final LinearLayout linLayout_comment;
    private final LinearLayout linLayout_share;
    private final LinearLayout linLayout_count_like;

    // vars
    private final AppCompatActivity context;
    private BottomSheetSharePublication bottomSheetShare;
    private BattleModel mPhoto;
    private final RelativeLayout relLayout_view_overlay;
    private final String from_profile_member;
    private final ArrayList<String> liker_list, crusher_list;
    private final boolean isFrom_approval_post;
    private final boolean isReportPost;
    private boolean mLikedByCurrentUser;
    private boolean mCrushedByCurrentUser;
    private StringBuilder mUsers;
    private StringBuilder updateUsers;
    private StringBuilder mUsersCrush;
    private StringBuilder updateCrushUsers;
    private int crush_count = 0;
    private int likes_count = 0;
    private int comments_count = 0;
    private int shares_count = 0;
    private final ArrayList<String> listUrl;
    private User mCurrentUser;

    // firebase
    private final DatabaseReference myRef;
    private final FirebaseDatabase database;
    private final String user_id;

    @SuppressLint("ClickableViewAccessibility")
    public GroupRecyclerItem(AppCompatActivity context, boolean isFrom_approval_post,
                             boolean isReportPost, String from_profile_member, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.isFrom_approval_post = isFrom_approval_post;
        this.isReportPost = isReportPost;
        this.from_profile_member = from_profile_member;
        this.relLayout_view_overlay = relLayout_view_overlay;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        liker_list = new ArrayList<>();
        crusher_list = new ArrayList<>();

        frameLayout=itemView.findViewById(R.id.imgframe);

        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        username = itemView.findViewById(R.id.username);
        relLayout_username = itemView.findViewById(R.id.relLayout_username);
        profil_photo = itemView.findViewById(R.id.profile_photo);
        menu_item = itemView.findViewById(R.id.menu_item);
        view_online = itemView.findViewById(R.id.view_online);
        like_heart = itemView.findViewById(R.id.like_heart);
        image = itemView.findViewById(R.id.image);

        number_of_likes = itemView.findViewById(R.id.number_of_likes);
        number_of_comments = itemView.findViewById(R.id.number_of_comments);
        number_of_share = itemView.findViewById(R.id.number_of_share);
        linLayout_count_like = itemView.findViewById(R.id.linLayout_count_like);
        linLayout_like = itemView.findViewById(R.id.linLayout_like);
        linLayout_comment = itemView.findViewById(R.id.linLayout_comment);
        linLayout_share = itemView.findViewById(R.id.linLayout_share);
        // crush
        conner_big_star_icon = itemView.findViewById(R.id.conner_big_star_icon);
        insta_star_view = itemView.findViewById(R.id.insta_star_view);
        like_star = itemView.findViewById(R.id.like_star);
        unlike_star_image = itemView.findViewById(R.id.unlike_star_image);

        nber_of_crush = itemView.findViewById(R.id.nber_of_crush);
        crush_msg = itemView.findViewById(R.id.crush_msg);

        // group
        relLayout_tv_admin = itemView.findViewById(R.id.relLayout_tv_admin);
        linLayout_share_only = itemView.findViewById(R.id.linLayout_share_only);
        linLayout_possibility_action = itemView.findViewById(R.id.linLayout_possibility_action);

        listUrl = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    public void init(BattleModel model) {
        mPhoto = model;

        caption.setCharText("");
        caption.setVisibility(View.GONE);
        translate_comment.setVisibility(View.GONE);
        nber_of_crush.setText("0");
        number_of_likes.setText("0");
        number_of_comments.setText("0");
        number_of_share.setText("0");

        if(liker_list != null){
            liker_list.clear();
        }

        if(crusher_list != null){
            crusher_list.clear();
        }

        // verify if user is online
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getUser_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            if(status != null && !status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        setLikes();
        setComments();
        setShare();
        crushCount();
        getCurrentUser();
        getLikesString();
        updateLike();
        getCrushString();
        updateCrush();

        // verify is current user is suspended _____________________________________________________
        Query query = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(model.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    GroupFollowersFollowing follower = new GroupFollowersFollowing();

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                    assert objectMap != null;
                    follower.setSuspended(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suspended))).toString())));

                    if (follower.isSuspended()) {
                        linLayout_possibility_action.setVisibility(View.GONE);
                        linLayout_share_only.setVisibility(View.VISIBLE);

                    } else {
                        linLayout_possibility_action.setVisibility(View.VISIBLE);
                        linLayout_share_only.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // _________________________________________________________________________________________

        listUrl.clear();
        // add url to the arrayList
        if (!model.getUrli().isEmpty())
            listUrl.add(model.getUrli());

        if (!model.getUrlii().isEmpty())
            listUrl.add(model.getUrlii());

        if (!model.getUrliii().isEmpty())
            listUrl.add(model.getUrliii());

        if (!model.getUrliv().isEmpty())
            listUrl.add(model.getUrliv());

        if (!model.getUrlv().isEmpty())
            listUrl.add(model.getUrlv());

        if (!model.getUrlvi().isEmpty())
            listUrl.add(model.getUrlvi());

        if (!model.getUrlvii().isEmpty())
            listUrl.add(model.getUrlvii());

        if (!model.getUrlviii().isEmpty())
            listUrl.add(model.getUrlviii());

        if (!model.getUrlix().isEmpty())
            listUrl.add(model.getUrlix());

        if (!model.getUrlx().isEmpty())
            listUrl.add(model.getUrlx());

        if (!model.getUrlxi().isEmpty())
            listUrl.add(model.getUrlxi());

        if (!model.getUrlxii().isEmpty())
            listUrl.add(model.getUrlxii());

        if (!model.getUrlxiii().isEmpty())
            listUrl.add(model.getUrlxiii());

        if (!model.getUrlxiv().isEmpty())
            listUrl.add(model.getUrlxiv());

        if (!model.getUrlxv().isEmpty())
            listUrl.add(model.getUrlxv());

        if (!model.getUrlxvi().isEmpty())
            listUrl.add(model.getUrlxvi());

        if (!model.getUrlxvii().isEmpty())
            listUrl.add(model.getUrlxvii());

        gridImages(listUrl);

        //set the time it was posted
        long tv_date = mPhoto.getDate_created();
        long time = (System.currentTimeMillis() - tv_date);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(TimeUtils.getTime(context, tv_date));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

        // get group admin id
        getAdmin_id(model);

        // caption _________________________________________________________________
        String description = model.getCaption();

        if (!TextUtils.isEmpty(description)) {
            caption.setCharText(description);
            caption.setVisibility(View.VISIBLE);
        }

        // Get the current language in device
        String language = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(description)
                .addOnSuccessListener(
                        languageCode -> {
                            assert languageCode != null;
                            if (languageCode.equals("und")) {
                                Log.i(TAG, "Can't identify language.");
                            } else {
                                Log.i(TAG, "Language: " + languageCode);
                                if (!languageCode.equals(language))
                                    translate_comment.setVisibility(View.VISIBLE);
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldnâ€™t be loaded or other internal error.
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        translate_comment.setOnClickListener(view -> {
            translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language,         //language to
                    description);           //Query Text

            translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                @Override
                public void onSuccess(String translatedText) {
                    Log.d(TAG, "onSuccess: "+translatedText);
                    caption.setCharText(translatedText);
                }

                @Override
                public void onFailure(String ErrorText) {
                    Log.d(TAG, "onFailure: "+ErrorText);
                }
            });
        });

        //get the profile image and username
        Query query3 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());

        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Log.d(TAG, "onDataChange: found user: "+user.getUsername());

                    String userID = user.getUser_id();

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profil_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    String userName = user.getUsername();
                    username.setText(userName);

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

                                relLayout_username.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, GroupProfileMember.class);
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                profil_photo.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent = new Intent(context, GroupProfileMember.class);
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                // menu_item
                                blockedMenu(model, user_groups);

                                if (user_groups.isGroup_private()) {
                                    linLayout_share.setOnClickListener(view -> {
                                        // show dialog box
                                        privateDialogBox();
                                    });

                                    linLayout_share_only.setOnClickListener(view -> {
                                        // show dialog box
                                        privateDialogBox();
                                    });

                                } else {

                                    // share
                                    bottomSheetShare = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getUrli(),
                                            model.getPhotoi_id(), "from_recyclerView", "recycler", false);
                                    linLayout_share.setOnClickListener(view -> {
                                        Util.preventTwoClick(linLayout_share);
                                        try {
                                            if (bottomSheetShare.isAdded())
                                                return;
                                            Bundle bundle = new Bundle();
                                            bundle.putString("item_view", "group_recyclerview");
                                            bottomSheetShare.setArguments(bundle);
                                            bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

                                        } catch (IllegalStateException e) {
                                            Log.d(TAG, "onDataChange: "+e.getMessage());
                                        }
                                    });

                                    // when current user is suspended
                                    linLayout_share_only.setOnClickListener(view -> {
                                        Util.preventTwoClick(linLayout_share_only);
                                        try {
                                            if (bottomSheetShare.isAdded())
                                                return;
                                            Bundle bundle = new Bundle();
                                            bundle.putString("item_view", "group_recyclerview");
                                            bottomSheetShare.setArguments(bundle);
                                            bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

                                        } catch (IllegalStateException e) {
                                            Log.d(TAG, "onDataChange: "+e.getMessage());
                                        }
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        GroupBottomSheetAddComment mSheetFragment = new GroupBottomSheetAddComment(context,
                null, model, null, null, null,
                null, null, null, null, null, null,
                null, null, relLayout_view_overlay);
        linLayout_comment.setOnClickListener(view -> {
            if (mSheetFragment.isAdded())
                return;
            mSheetFragment.show(context.getSupportFragmentManager(), "TAG");
        });

        linLayout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                    image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeLike();
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
                            }
                        }
                    });
                }
            }
        });

        like_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_star.isSelected()) {
                    like_star.setSelected(false);
                    conner_big_star_icon.setVisibility(View.GONE);
                    unlike_star_image.setImageResource(R.drawable.unlike_star);
                    like_star.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeCrush();
                        }
                    });

                } else {
                    like_star.setSelected(true);
                    unlike_star_image.setImageResource(R.drawable.big_star_icon);
                    insta_star_view.start();
                    conner_big_star_icon.setVisibility(View.VISIBLE);
                    like_star.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mCrushedByCurrentUser) {
                                addNewCrush();
                            }
                        }
                    });
                }
            }
        });
    }

    // to permit admin to delete post
    private void blockedMenu(BattleModel model, UserGroups user_groups) {
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

                    if (!model.getAdmin_master().equals(user_id) && !follower.isAdminInGroup() && !follower.isModerator()) {
                        if (from_profile_member == null) {
                            if (!isReportPost) {
                                BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                                menu_item.setOnClickListener(view1 -> {
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
                    }
                }

                if (!snapshot.exists()) {
                    if (!model.getAdmin_master().equals(user_id)) {
                        if (from_profile_member == null) {
                            if (!isReportPost) {
                                BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                                menu_item.setOnClickListener(view1 -> {
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // dialog box if group is private
    private void privateDialogBox() {
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

        dialog_title.setText(context.getString(R.string.share));
        dialog_text.setText(Html.fromHtml(context.getString(R.string.this_group_is_private)
                +" "+context.getString(R.string.only_member_can_see_who_is_in_the_group_and_what_is_published)));

        positiveButton.setOnClickListener(view2 -> d.dismiss());
    }

    private void getAdmin_id(BattleModel model) {
        relLayout_tv_admin.setVisibility(View.GONE);
        // to know if is admin master
        Query query = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(model.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(model.getGroup_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    String admin_master = user_groups.getAdmin_master();

                    if (admin_master.equals(model.getPublisher()))
                        relLayout_tv_admin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // to know if publisher user is admin
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_group_followers))
                .child(model.getGroup_id())
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    follower.setAdminInGroup(Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_adminInGroup))).toString())));

                    if (follower.getUser_id().equals(model.getPublisher()) && follower.isAdminInGroup())
                        relLayout_tv_admin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gridImages(ArrayList<String> urlList) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int sst=width-20;
        Log.d("Width is",String.valueOf(sst));
        Log.d("Width is",String.valueOf(width));
        int i = urlList.size();
        frameLayout.removeAllViews();

        if(i==2)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(0))
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 800));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(1))
                    .centerCrop()
                    .into(imageView2);

            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 800));
            imageView2.setX((float) width/2);
            imageView2.setPadding(7,0,0,0);
            frameLayout.addView(imageView1);
            frameLayout.addView(imageView2);

            // click listener
            imageView1.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView2.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });
        }
        if(i==3)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(0))
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 800));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(1))
                    .centerCrop()
                    .into(imageView2);

            imageView2.setX((float) width /2);
            imageView2.setPadding(7,0,0,7);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 400));

            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(2))
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setY(400);
            imageView3.setPadding(7,0,0,0);
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2, 400));
            frameLayout.addView(imageView1);
            frameLayout.addView(imageView2);
            frameLayout.addView(imageView3);

            // click listener
            imageView1.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView2.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView3.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 2);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });
        }
        if(i==4)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(0))
                    .centerCrop()
                    .into(imageView1);

            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2,800));
            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            //x=200,y==0
            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(1))
                    .centerCrop()
                    .into(imageView2);

            imageView2.setX((float) width/2);
            imageView2.setPadding(7,0,0,7);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(2))
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setY((float) 800/3);
            imageView3.setPadding(7,0,0,7);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView4= new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(urlList.get(3))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(imageView4);

            imageView4.setX((float) width/2);
            imageView4.setY((float) (800/3+800/3));
            imageView4.setPadding(7,0,0,0);
            imageView4.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

            frameLayout.addView(imageView1);
            frameLayout.addView(imageView2);
            frameLayout.addView(imageView3);
            frameLayout.addView(imageView4);

            // click listener
            imageView1.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView2.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView3.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 2);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView4.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 3);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });
        }
        if(i==5)
        {
            //x=0,y=0
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(0))
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 400));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(1))
                    .centerCrop()
                    .into(imageView2);

            imageView2.setY(400);
            imageView2.setPadding(0,7,0,0);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 400));

            //x=200,y==0
            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(2))
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setPadding(7,0,0,7);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView4 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(3))
                    .centerCrop()
                    .into(imageView4);

            imageView4.setX((float) width/2);
            imageView4.setY((float) 800/3);
            imageView4.setPadding(7,0,0,7);
            imageView4.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView5= new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(4))
                    .centerCrop()
                    .into(imageView5);

            imageView5.setX((float) width/2);
            imageView5.setY((float) (800/3+800/3));
            imageView5.setPadding(7,0,0,0);
            imageView5.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView5.setScaleType(ImageView.ScaleType.FIT_XY);

            frameLayout.addView(imageView1);
            frameLayout.addView(imageView2);
            frameLayout.addView(imageView3);
            frameLayout.addView(imageView4);
            frameLayout.addView(imageView5);

            // click listener
            imageView1.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView2.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView3.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 2);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView4.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 3);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView5.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 4);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });
        }
        if(i>5)
        {
            ImageView imageView1 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(0))
                    .centerCrop()
                    .into(imageView1);

            imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView1.setLayoutParams(new FrameLayout.LayoutParams(width/2, 400));

            ImageView imageView2 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(1))
                    .centerCrop()
                    .into(imageView2);

            imageView2.setY(400);
            imageView2.setPadding(0,7,0,0);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setLayoutParams(new FrameLayout.LayoutParams(width/2, 400));

            //x=200,y==0
            ImageView imageView3 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(2))
                    .centerCrop()
                    .into(imageView3);

            imageView3.setX((float) width/2);
            imageView3.setPadding(7,0,0,7);
            imageView3.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView4 = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(3))
                    .centerCrop()
                    .into(imageView4);

            imageView4.setX((float) width/2);
            imageView4.setY((float) 800/3);
            imageView4.setPadding(7,0,0,7);
            imageView4.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView imageView5= new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(urlList.get(4))
                    .centerCrop()
                    .into(imageView5);

            imageView5.setX((float) width/2);
            imageView5.setY((float) (800/3+800/3));
            imageView5.setPadding(7,0,0,0);
            imageView5.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            imageView5.setScaleType(ImageView.ScaleType.FIT_XY);

            TextView textView=new TextView(context);
            String n = "+" + (i - 5);
            textView.setText(n);
            textView.setX((float) width/2);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setY((float) (800/3+800/3));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            textView.setLayoutParams(new FrameLayout.LayoutParams(width/2,800/3));
            frameLayout.addView(imageView1);
            frameLayout.addView(imageView2);
            frameLayout.addView(imageView3);
            frameLayout.addView(imageView4);
            frameLayout.addView(imageView5);
            frameLayout.addView(textView);

            // click listener
            imageView1.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView2.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView3.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 2);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView4.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 3);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });

            imageView5.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGson = gson.toJson(mPhoto);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GroupViewRecycler.class);
                intent.putExtra("battleModel", myGson);
                intent.putExtra("position", 4);
                intent.putExtra("view_header_group", "view_header_group");
                intent.putExtra("isFrom_approval_post", isFrom_approval_post);
                context.startActivity(intent);
            });
        }
    }

    private void removeLike() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
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
                        if (str.equals("0")) {
                            linLayout_count_like.setVisibility(View.GONE);
                            number_of_likes.setVisibility(View.GONE);
                        }
                        number_of_likes.setText(str);

                        // remove like points
                        removeLikePoints();

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_group_media))
                                .child(mPhoto.getPhotoi_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(mPhoto.getGroup_id())
                                .child(mPhoto.getPhotoi_id())
                                .child(context.getString(R.string.field_likes))
                                .child(keyID)
                                .removeValue();

                        getLikesString();
                        updateLike();

                    }
                    //case2: The user has not liked the photo
                    else if (!mLikedByCurrentUser){
                        // add new like
                        addNewLike();
                        break;
                    }
                }
                // s'il n'existe encore aucun like
                if(!snapshot.exists()){
                    //add new like
                    addNewLike();
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
        }
        if (linLayout_count_like.getVisibility() != View.VISIBLE)
            linLayout_count_like.setVisibility(View.VISIBLE);

        number_of_likes.setText(str);

        // add like points
        addLikePoints();

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_group))
                .child(mPhoto.getGroup_id())
                .child(mPhoto.getPhotoi_id())
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
                .child(mPhoto.getPhotoi_id())
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
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_likes));

        // on parcours tous les likes
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateUsers = new StringBuilder();

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

                                updateUsers.append(user.getUsername());
                                updateUsers.append(",");
                            }

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
                            mLikedByCurrentUser = updateUsers.toString().contains(mCurrentUser.getUsername() + ",");
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
            }

        } else {
            if (like_heart.isSelected()) {
                like_heart.setSelected(false);
                image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    private void removeCrush() {
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_crush));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mCrushedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update crush count
                        int count = Integer.parseInt(nber_of_crush.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nber_of_crush.setVisibility(View.GONE);
                        nber_of_crush.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_group_media))
                                .child(mPhoto.getPhotoi_id())
                                .child(context.getString(R.string.field_crush))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_group))
                                .child(mPhoto.getGroup_id())
                                .child(mPhoto.getPhotoi_id())
                                .child(context.getString(R.string.field_crush))
                                .child(keyID)
                                .removeValue();

                        getCrushString();
                        updateCrush();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewCrush(){
        Log.d(TAG, "addNewCrush: adding new crush");
        // update crush count
        nber_of_crush.setVisibility(View.VISIBLE);
        int count = Integer.parseInt(nber_of_crush.getText().toString());
        String str = String.valueOf(count+1);
        nber_of_crush.setText(str);

        String newCrushID = myRef.push().getKey();
        Crush crush = new Crush();
        crush.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newCrushID != null;
        myRef.child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_crush))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_group))
                .child(mPhoto.getGroup_id())
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_crush))
                .child(newCrushID)
                .setValue(crush);

        // affichage Ã  l'Ã©cran
        getCrushString();
        updateCrush();
    }

    private void getCrushString(){
        Log.d(TAG, "getCrushString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_crush));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersCrush = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on rÃ©cupÃ¨re l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsersCrush.append(user.getUsername());
                                mUsersCrush.append(",");
                            }

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
                            mCrushedByCurrentUser = mUsersCrush.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser = false;
                    setupCrushString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCrush(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_crush));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateCrushUsers = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found crush: " +user.getUsername());

                                updateCrushUsers.append(user.getUsername());
                                updateCrushUsers.append(",");
                            }

                            // vÃ©rifie si c'est l'utilistateur courant a likÃ©
                            mCrushedByCurrentUser = updateCrushUsers.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser = false;
                    setupCrushString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupCrushString() {
        if (mCrushedByCurrentUser) {
            if (!like_star.isSelected()) {
                like_star.setSelected(true);
                conner_big_star_icon.setVisibility(View.VISIBLE);
                unlike_star_image.setImageResource(R.drawable.big_star_icon);
            }

        } else {
            if (like_star.isSelected()) {
                like_star.setSelected(false);
                conner_big_star_icon.setVisibility(View.GONE);
                unlike_star_image.setImageResource(R.drawable.unlike_star);
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

    public void crushCount() {
        crush_count = 0;
        nber_of_crush.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_crush));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    crush_count++;
                }

                if (crush_count >= 1) {
                    nber_of_crush.setVisibility(View.VISIBLE);
                    nber_of_crush.setText(String.valueOf(crush_count));

                    if (crush_count >= 2)
                        crush_msg.setText(context.getString(R.string.several_crush));

                    nber_of_crush.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list);
                        context.startActivity(intent);
                    });

                    crush_msg.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLikes() {
        likes_count = 0;
        linLayout_count_like.setVisibility(View.INVISIBLE);
        number_of_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
                .child(context.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count++;
                }

                if (likes_count >= 1) {
                    linLayout_count_like.setVisibility(View.VISIBLE);
                    number_of_likes.setVisibility(View.VISIBLE);

                    double count;
                    if (likes_count >= 1000) {
                        count = (float)likes_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";

                        number_of_likes.setText(tv_count);

                    } else {
                        number_of_likes.setText(String.valueOf(likes_count));
                    }

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
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
                .child(mPhoto.getPhotoi_id())
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
                            .child(mPhoto.getPhotoi_id())
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
                                number_of_comments.setVisibility(View.VISIBLE);

                                double count;
                                if (comments_count >= 1000) {
                                    count = (float)comments_count/1000;

                                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                                    number_of_comments.setText(tv_count);

                                } else {
                                    number_of_comments.setText(String.valueOf(comments_count));
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

    private void setShare() {
        shares_count = 0;
        number_of_share.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(mPhoto.getPhotoi_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shares_count++;
                }


                if (shares_count >= 1) {
                    number_of_share.setVisibility(View.VISIBLE);

                    double count;
                    if (shares_count >= 1000) {
                        count = (float)shares_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        number_of_share.setText(tv_count);

                    } else {
                        number_of_share.setText(String.valueOf(shares_count));
                    }
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

