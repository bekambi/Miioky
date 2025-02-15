package com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders;

import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetAddCommentShare;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_category.Util_BattleChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Crushers;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.full_challenge_video_share.FullChallengeVideoShare;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.notification.util_map.Utils_Map_Notification;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_NotificationModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SharedSingleTopResponseVideoDoubleDisplay extends RecyclerView.ViewHolder {
    private static final String TAG = "GroupSharedSingleTopResponseVideoDoubleDisplay";

    // widgets
    private final ImageView conner_big_star_icon_un;
    private final InstaLikeView insta_star_view_un;
    private final SmallBangView like_star_un;
    private final ImageView unlike_star_image_un;
    private final ImageView conner_big_star_icon_deux;
    private final InstaLikeView insta_star_view_deux;
    private final SmallBangView like_star_deux;
    private final ImageView unlike_star_image_deux;
    private final SmallBangView like_heart_un;
    private final ImageView image_un;
    private final SmallBangView like_heart_deux;
    private final ImageView image_deux;
    private final InstaLikeView mInstaLikeView;
    private final InstaLikeView mInstaLikeView2;
    private final ImageView thumbnail_un;
    private final ImageView thumbnail_deux;
    private final View view_online_response;
    private final View view_online_invite;
    private final CircleImageView profil_image_un;
    private final CircleImageView profil_image_deux;
    private final ImageView reponse_flag_icon;
    private final ImageView invite_flag_icon;
    private final TextView nber_of_crush_un;
    private final TextView crush_msg_un;
    private final TextView nber_of_crush_deux;
    private final TextView crush_msg_deux;
    private final TextView reponse_username;
    private final TextView invite_username;
    private final TextView category_un;
    private final TextView category_deux;
    private final TextView reponse_country_name;
    private final TextView invite_country_name;
    private final ViewMoreTextView caption_un;
    private final ViewMoreTextView caption_deux;
    private final TextView translate_comment_un;
    private final TextView translate_comment_deux;
    private final RelativeLayout relLayout_go_user_profile_un;
    private final RelativeLayout relLayout_go_user_profile_deux;
    private final RelativeLayout relLayout_share_only;
    private final LinearLayout linLayout_share_only_un;
    private final LinearLayout linLayout_share_only_deux;
    private final LinearLayout linLayout_possibility_action;
    private final LinearLayout linLayout_like_un, linLayout_like_deux;
    private final LinearLayout linLayout_count_like_un, linLayout_count_like_deux;

    private final LinearLayout linLayout_comment;
    private final TextView number_of_comments;
    private final TextView number_of_likes_un;
    private final TextView number_of_share_un;
    private final LinearLayout linLayout_share_un;

    private final TextView number_of_likes_deux;
    private final TextView number_of_share_deux;
    private final LinearLayout linLayout_share_deux;

    // top single
    private final CircleImageView sharing_profile_photo;
    private final View sharing_view_online;
    private final TextView sharing_username;
    private final TextView sharing_tps_publication;
    private final ViewMoreTextView sharing_caption;
    private final TextView sharing_translate_comment;
    public final RelativeLayout relLayout_go_user_profile;
    public final RelativeLayout relLayout_follow_sharing;

    // bottom group
    private final CardView cardView;
    private final ImageView group_profile_photo;
    public final ImageView menu_item;
    public final ImageView delete_suggestion;
    private final TextView group_name;
    public final TextView join_group;
    private final TextView tps_publication;
    private final RelativeLayout relLayout_group_name;

    // vars
    private final AppCompatActivity context;
    private final GestureDetector detector_like_un;
    private final GestureDetector detector_like_deux;
    private BottomSheetSharePublication bottomSheetShare_un;
    private BottomSheetSharePublication bottomSheetShare_deux;
    private BattleModel photo, mModel;
    private final RelativeLayout relLayout_view_overlay;
    private MyEditText mComment;
    private String from_full_video;
    private final ArrayList<String> liker_list_un, liker_list_deux, crusher_list_un, crusher_list_deux;
    private int comments_count;
    private int crush_count_un;
    private int crush_count_deux;
    private int likes_count_un;
    private int likes_count_deux;
    private int shares_count_un;
    private int shares_count_deux;
    private StringBuilder users_un;
    private StringBuilder updateUsers_un;
    private StringBuilder users_deux;
    private StringBuilder updateUsers_deux;
    private StringBuilder mUsersCrush_un;
    private StringBuilder updateCrushUsers_un;
    private StringBuilder mUsersCrush_deux;
    private StringBuilder updateCrushUsers_deux;
    private boolean likedByCurrentUser_un;
    private boolean likedByCurrentUser_deux;
    private boolean mCrushedByCurrentUser_un;
    private boolean mCrushedByCurrentUser_deux;

    private final DatabaseReference myRef;
    private final FirebaseDatabase database;
    private final DatabaseReference mReference;
    private User mCurrentUser;
    private final String user_id;

    @SuppressLint("ClickableViewAccessibility")
    public SharedSingleTopResponseVideoDoubleDisplay(AppCompatActivity context, BattleModel mModel,
                                                     MyEditText mComment,
                                                     String from_full_video, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        if (mModel != null) {
            this.mModel = mModel;
            this.photo = mModel;
            this.mComment = mComment;
            this.from_full_video = from_full_video;
        }

        // firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        database=FirebaseDatabase.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        liker_list_un = new ArrayList<>();
        liker_list_deux = new ArrayList<>();
        crusher_list_un = new ArrayList<>();
        crusher_list_deux = new ArrayList<>();

        view_online_response = itemView.findViewById(R.id.view_online_response);
        view_online_invite = itemView.findViewById(R.id.view_online_invite);
        thumbnail_un = itemView.findViewById(R.id.thumbnail_un);
        thumbnail_deux = itemView.findViewById(R.id.thumbnail_deux);

        profil_image_un = itemView.findViewById(R.id.response_profile);
        profil_image_deux = itemView.findViewById(R.id.invite_profile);
        caption_un = itemView.findViewById(R.id.caption_un);
        caption_deux = itemView.findViewById(R.id.caption_deux);
        translate_comment_un = itemView.findViewById(R.id.translate_comment_un);
        translate_comment_deux = itemView.findViewById(R.id.translate_comment_deux);
        reponse_username = itemView.findViewById(R.id.reponse_username);
        invite_username = itemView.findViewById(R.id.invite_username);
        category_un = itemView.findViewById(R.id.category_un);
        category_deux = itemView.findViewById(R.id.category_deux);
        reponse_country_name = itemView.findViewById(R.id.reponse_country_name);
        reponse_flag_icon = itemView.findViewById(R.id.reponse_flag_icon);
        invite_country_name = itemView.findViewById(R.id.invite_country_name);
        invite_flag_icon = itemView.findViewById(R.id.invite_flag_icon);
        relLayout_go_user_profile_un = itemView.findViewById(R.id.relLayout_go_user_profile_un);
        relLayout_go_user_profile_deux = itemView.findViewById(R.id.relLayout_go_user_profile_deux);
        RelativeLayout relLayout_main_view = itemView.findViewById(R.id.relLayout_main_view);

        image_un = itemView.findViewById(R.id.image_un);
        like_heart_un = itemView.findViewById(R.id.like_heart_un);
        image_deux = itemView.findViewById(R.id.image_deux);
        like_heart_deux = itemView.findViewById(R.id.like_heart_deux);
        mInstaLikeView = itemView.findViewById(R.id.insta_like_view);
        mInstaLikeView2 = itemView.findViewById(R.id.view);

        linLayout_comment = itemView.findViewById(R.id.linLayout_comment);
        number_of_comments = itemView.findViewById(R.id.number_of_comments);
        number_of_likes_un = itemView.findViewById(R.id.number_of_likes_un);
        number_of_share_un = itemView.findViewById(R.id.number_of_share_un);
        linLayout_share_un = itemView.findViewById(R.id.linLayout_share_un);

        number_of_likes_deux = itemView.findViewById(R.id.number_of_likes_deux);
        number_of_share_deux = itemView.findViewById(R.id.number_of_share_deux);
        linLayout_share_deux = itemView.findViewById(R.id.linLayout_share_deux);
        // crush
        conner_big_star_icon_un = itemView.findViewById(R.id.conner_big_star_icon_un);
        insta_star_view_un = itemView.findViewById(R.id.insta_star_view_un);
        like_star_un = itemView.findViewById(R.id.like_star_un);
        unlike_star_image_un = itemView.findViewById(R.id.unlike_star_image_un);

        nber_of_crush_un = itemView.findViewById(R.id.nber_of_crush_un);
        crush_msg_un = itemView.findViewById(R.id.crush_msg_un);

        conner_big_star_icon_deux = itemView.findViewById(R.id.conner_big_star_icon_deux);
        insta_star_view_deux = itemView.findViewById(R.id.insta_star_view_deux);
        like_star_deux = itemView.findViewById(R.id.like_star_deux);
        unlike_star_image_deux = itemView.findViewById(R.id.unlike_star_image_deux);

        nber_of_crush_deux = itemView.findViewById(R.id.nber_of_crush_deux);
        crush_msg_deux = itemView.findViewById(R.id.crush_msg_deux);

        // single top
        sharing_profile_photo = itemView.findViewById(R.id.sharing_profile_photo);
        sharing_view_online = itemView.findViewById(R.id.sharing_view_online);
        sharing_username = itemView.findViewById(R.id.sharing_username);
        sharing_caption = itemView.findViewById(R.id.sharing_caption);
        sharing_translate_comment = itemView.findViewById(R.id.sharing_translate_comment);
        sharing_tps_publication = itemView.findViewById(R.id.sharing_tps_publication);
        relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);
        relLayout_follow_sharing = itemView.findViewById(R.id.relLayout_follow_sharing);

        // group bottom
        cardView = itemView.findViewById(R.id.cardView);
        group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
        menu_item = itemView.findViewById(R.id.menu_item);
        delete_suggestion = itemView.findViewById(R.id.delete_suggestion);
        group_name = itemView.findViewById(R.id.group_name);
        join_group = itemView.findViewById(R.id.join_group);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);

        relLayout_share_only = itemView.findViewById(R.id.relLayout_share_only);
        linLayout_share_only_un = itemView.findViewById(R.id.linLayout_share_only_un);
        linLayout_share_only_deux = itemView.findViewById(R.id.linLayout_share_only_deux);
        linLayout_possibility_action = itemView.findViewById(R.id.linLayout_possibility_action);
        linLayout_like_un = itemView.findViewById(R.id.linLayout_like_un);
        linLayout_like_deux = itemView.findViewById(R.id.linLayout_like_deux);
        linLayout_count_like_un = itemView.findViewById(R.id.linLayout_count_like_un);
        linLayout_count_like_deux = itemView.findViewById(R.id.linLayout_count_like_deux);
        LinearLayout parent = itemView.findViewById(R.id.parent);

        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // double tap to like image
        detector_like_un = new GestureDetector(context, new GestureListenerLike_un());
        thumbnail_un.setOnTouchListener((view, motionEvent) -> detector_like_un.onTouchEvent(motionEvent));
        detector_like_deux = new GestureDetector(context, new GestureListenerLike_deux());
        thumbnail_deux.setOnTouchListener((view, motionEvent) -> detector_like_deux.onTouchEvent(motionEvent));

        // for comments
        if (mModel != null) {
            init(this.mModel);
            parent.setPadding(0,0,0,100);
            relLayout_main_view.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 500));
        }
    }

    @SuppressLint("SetTextI18n")
    public void init(BattleModel model) {
        photo = model;

        caption_un.setText("");
        caption_deux.setText("");
        sharing_caption.setVisibility(View.GONE);
        caption_un.setVisibility(View.GONE);
        caption_deux.setVisibility(View.GONE);
        translate_comment_un.setVisibility(View.GONE);
        translate_comment_deux.setVisibility(View.GONE);
        sharing_translate_comment.setVisibility(View.GONE);
        sharing_caption.setCharText("");
        nber_of_crush_un.setText("0");
        nber_of_crush_deux.setText("0");
        number_of_comments.setText("0");
        number_of_likes_un.setText("0");
        number_of_likes_deux.setText("0");
        number_of_share_un.setText("0");
        number_of_share_deux.setText("0");

        if(liker_list_un != null){
            liker_list_un.clear();
        }
        if(liker_list_deux != null){
            liker_list_deux.clear();
        }
        if(crusher_list_un != null){
            crusher_list_un.clear();
        }
        if(crusher_list_deux != null){
            crusher_list_deux.clear();
        }

        // verify if user is online /___top
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
                                    sharing_view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id().equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        sharing_view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // verify if user is online
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getReponse_user_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            if(status != null && !status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online_response.setVisibility(View.GONE);
                                }else{
                                    if (!model.getReponse_user_id().equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online_response.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getInvite_userID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            if(status != null && !status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online_invite.setVisibility(View.GONE);
                                }else{
                                    if (!model.getInvite_userID().equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online_invite.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        setComments();
        setLikes_un();
        setLikes_deux();
        setShare_un();
        setShare_deux();
        crushCount_un();
        crushCount_deux();
        getCurrentUser();
        getLikesString_un();
        updateLikes_un();
        getLikesString_deux();
        updateLikes_deux();
        getCrushString_un();
        updateCrush_un();
        getCrushString_deux();
        updateCrush_deux();

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
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                    if (follower.isSuspended()) {
                        linLayout_possibility_action.setVisibility(View.GONE);
                        relLayout_share_only.setVisibility(View.VISIBLE);

                    } else {
                        linLayout_possibility_action.setVisibility(View.VISIBLE);
                        relLayout_share_only.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // _________________________________________________________________________________________

        //set the time it was posted
        long date_shared = model.getDate_shared();
        long time = (System.currentTimeMillis() - date_shared);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(TimeUtils.getTime(context, date_shared));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date_shared)));

        long date_created = model.getDate_created();
        long time2 = (System.currentTimeMillis() - date_created);
        if (time2 >= 2*3600*24000254025L)
            sharing_tps_publication.setText(TimeUtils.getTime(context, date_created));
        else
            sharing_tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, date_created)));

        // country name and flag
        reponse_country_name.setText(model.getReponse_country_name());
        invite_country_name.setText(model.getInvite_country_name());
        Util.getCountryFlag(model.getReponse_countryID(), reponse_flag_icon);
        Util.getCountryFlag(model.getInvite_countryID(), invite_flag_icon);

        String set_category = Util_BattleChallengeCategory.getBattleChallengeCategory(context, model);
        category_un.setText(Html.fromHtml("#"+set_category));
        category_deux.setText(Html.fromHtml("#"+set_category));

        String invite_category_status = model.getInvite_category_status();
        category_un.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("battle_model", myGson);
            intent.putExtra("category_status", invite_category_status);
            context.startActivity(intent);
        });

        category_deux.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("battle_model", myGson);
            intent.putExtra("category_status", invite_category_status);
            context.startActivity(intent);
        });

        // l'image une
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(model.getThumbnail_response())
                .into(thumbnail_un);

        // l'image deux
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(model.getThumbnail_invite())
                .into(thumbnail_deux);

        // sharing caption _______________________________________
        String sharing_description = model.getSharing_caption();

        if (!TextUtils.isEmpty(sharing_description)) {
            sharing_caption.setCharText(sharing_description);
            sharing_caption.setVisibility(View.VISIBLE);
        }

        // Get the current language in device
        String language1 = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(sharing_description)
                .addOnSuccessListener(
                        languageCode -> {
                            assert languageCode != null;
                            if (languageCode.equals("und")) {
                                Log.i(TAG, "Can't identify language.");
                            } else {
                                Log.i(TAG, "Language: " + languageCode);
                                if (!languageCode.equals(language1))
                                    sharing_translate_comment.setVisibility(View.VISIBLE);
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldn’t be loaded or other internal error.
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        sharing_translate_comment.setOnClickListener(view -> {
            sharing_translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language1,         //language to
                    sharing_description);           //Query Text

            translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                @Override
                public void onSuccess(String translatedText) {
                    Log.d(TAG, "onSuccess: "+translatedText);
                    sharing_caption.setCharText(translatedText);
                }

                @Override
                public void onFailure(String ErrorText) {
                    Log.d(TAG, "onFailure: "+ErrorText);
                }
            });
        });
        // _______________________________________________________________________
        //get the profile image and username /___top
        Query query3 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id());

        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Log.d(TAG, "onDataChange: found user: "+user.getUsername());

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(sharing_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    String userName = user.getUsername();
                    sharing_username.setText(userName);

                    relLayout_go_user_profile.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            intent = new Intent(context, Profile.class);

                        } else {
                            intent = new Intent(context, ViewProfile.class);
                            Gson gson = new Gson();
                            String myJson = gson.toJson(user);
                            intent.putExtra("user", myJson);
                        }
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get the profile image and username /___bottom
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(model.getSharing_admin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(model.getGroup_id());

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    group_name.setText(user_groups.getGroup_name());

                    Glide.with(context.getApplicationContext())
                            .load(user_groups.getProfile_photo())
                            .into(group_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user_groups.getFull_photo())
                            .preload();

                    // to know if current user is admin
                    if (user_groups.getAdmin_master().equals(user_id)) {
                        Gson gson = new Gson();
                        String myJson = gson.toJson(user_groups);

                        cardView.setOnClickListener(v -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupViewAdmin.class);
                            intent.putExtra("user_groups", myJson);
                            context.startActivity(intent);
                        });

                        relLayout_group_name.setOnClickListener(v -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, GroupViewAdmin.class);
                            intent.putExtra("user_groups", myJson);
                            context.startActivity(intent);
                        });

                    } else {
                        Query query = myRef
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(user_groups.getGroup_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();

                                    assert objectMap != null;
                                    GroupFollowersFollowing follower = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);

                                    cardView.setOnClickListener(v -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent;
                                        if (follower.isModerator()|| follower.isAdminInGroup()) {
                                            intent = new Intent(context, GroupViewAdmin.class);
                                        } else {
                                            intent = new Intent(context, GroupViewProfile.class);
                                        }
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                    relLayout_group_name.setOnClickListener(v -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent;
                                        if (follower.isModerator() || follower.isAdminInGroup()) {
                                            intent = new Intent(context, GroupViewAdmin.class);
                                        } else {
                                            intent = new Intent(context, GroupViewProfile.class);
                                        }
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });
                                }

                                if (!snapshot.exists()) {
                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);

                                    cardView.setOnClickListener(v -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewProfile.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                    relLayout_group_name.setOnClickListener(v -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewProfile.class);
                                        intent.putExtra("user_groups", myJson);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get the (reponse) profile image and username
        Query query1 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(photo.getReponse_user_id());

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user_reponse = Util_User.getUser(context, objectMap, ds);

                    Log.d(TAG, "onDataChange: found user: "+user_reponse.getUsername());
                    String userID = user_reponse.getUser_id();
                    String userName_un = user_reponse.getUsername();
                    reponse_username.setText(userName_un);

                    String description = photo.getReponse_caption();

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
                                            if (!languageCode.equals(language)) {
                                                translate_comment_un.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    e -> {
                                        // Model couldn’t be loaded or other internal error.
                                        // ...
                                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                                    });

                    if (!TextUtils.isEmpty(description)) {
                        caption_un.setVisibility(View.VISIBLE);
                        caption_un.setCharText(description);
                    }

                    translate_comment_un.setOnClickListener(view -> {
                        translate_comment_un.setVisibility(View.GONE);
                        TranslateAPI translateAPI = new TranslateAPI(
                                Language.AUTO_DETECT,   // language from
                                language,         //language to
                                description);           //Query Text

                        translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                            @Override
                            public void onSuccess(String translatedText) {
                                Log.d(TAG, "onSuccess: "+translatedText);
                                caption_un.setCharText(translatedText);
                            }

                            @Override
                            public void onFailure(String ErrorText) {
                                Log.d(TAG, "onFailure: "+ErrorText);
                            }
                        });
                    });

                    Glide.with(context.getApplicationContext())
                            .load(user_reponse.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profil_image_un);

                    Glide.with(context.getApplicationContext())
                            .load(user_reponse.getFull_profileUrl())
                            .preload();

                    Query myQuery1 = myRef
                            .child(context.getString(R.string.dbname_user_group))
                            .child(model.getSharing_admin_master())
                            .orderByChild(context.getString(R.string.field_group_id))
                            .equalTo(model.getShared_group_id());
                    myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                relLayout_go_user_profile_un.setOnClickListener(v -> {
                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                            user_reponse.getUsername());
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (model.getReponse_user_id().equals(user_id) && user_groups.getAdmin_master().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query2 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(photo.getInvite_userID());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user_invite = Util_User.getUser(context, objectMap, ds);

                    String userID = user_invite.getUser_id();
                    String userName_deux = user_invite.getUsername();
                    invite_username.setText(userName_deux);

                    String description2 = photo.getInvite_caption();

                    // Get the current language in device
                    String language = Locale.getDefault().getLanguage();
                    // detect text language
                    LanguageIdentifier languageIdentifier =
                            LanguageIdentification.getClient();
                    languageIdentifier.identifyLanguage(description2)
                            .addOnSuccessListener(
                                    languageCode -> {
                                        assert languageCode != null;
                                        if (languageCode.equals("und")) {
                                            Log.i(TAG, "Can't identify language.");
                                        } else {
                                            Log.i(TAG, "Language: " + languageCode);
                                            if (!languageCode.equals(language)) {
                                                translate_comment_deux.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    e -> {
                                        // Model couldn’t be loaded or other internal error.
                                        // ...
                                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                                    });

                    if (!TextUtils.isEmpty(description2)) {
                        caption_deux.setVisibility(View.VISIBLE);
                        caption_deux.setCharText(description2);
                    }

                    translate_comment_deux.setOnClickListener(view -> {
                        translate_comment_deux.setVisibility(View.GONE);
                        TranslateAPI translateAPI = new TranslateAPI(
                                Language.AUTO_DETECT,   // language from
                                language,         //language to
                                description2);           //Query Text

                        translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                            @Override
                            public void onSuccess(String translatedText) {
                                Log.d(TAG, "onSuccess: "+translatedText);
                                caption_deux.setCharText(translatedText);
                            }

                            @Override
                            public void onFailure(String ErrorText) {
                                Log.d(TAG, "onFailure: "+ErrorText);
                            }
                        });
                    });

                    Glide.with(context.getApplicationContext())
                            .load(user_invite.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profil_image_deux);

                    Glide.with(context.getApplicationContext())
                            .load(user_invite.getFull_profileUrl())
                            .preload();

                    Query myQuery1 = myRef
                            .child(context.getString(R.string.dbname_user_group))
                            .child(model.getSharing_admin_master())
                            .orderByChild(context.getString(R.string.field_group_id))
                            .equalTo(model.getShared_group_id());
                    myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                relLayout_go_user_profile_deux.setOnClickListener(v -> {
                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                            user_invite.getUsername());
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Intent intent;
                                    if (model.getInvite_userID().equals(user_id) && user_groups.getAdmin_master().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
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

        // menu_item
        BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
        menu_item.setOnClickListener(view -> {
            if (bottomSheet.isAdded())
                return;

            Bundle args = new Bundle();
            args.putParcelable("battle_model", model);
            args.putString("miioky", "miioky");
            bottomSheet.setArguments(args);
            bottomSheet.show(context.getSupportFragmentManager(),
                    "TAG");
        });

        // aller à lactivité des commentaires
        if (mModel != null) {
            linLayout_comment.setOnClickListener(view -> {
                // to show keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                mComment.requestFocus();
            });
        } else {

            // add new comment
            BottomSheetAddCommentShare bottomSheetFragmentShare = new BottomSheetAddCommentShare(context,
                    null, null, null, model,
                    null, null, null, null,
                    null, null, null, null,
                    null, "response_vid_double_single_top", null, null,
                    null, null, null, null, relLayout_view_overlay);
            linLayout_comment.setOnClickListener(view -> {
                if (bottomSheetFragmentShare.isAdded())
                    return;
                bottomSheetFragmentShare.show(context.getSupportFragmentManager(), "TAG");
            });
        }

        // share_un
        bottomSheetShare_un = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getReponse_url(),
                model.getReponse_photoID(), "from_video", "", true);
        linLayout_share_un.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share_un);
            try {
                if (bottomSheetShare_un.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare_un.setArguments(bundle);
                bottomSheetShare_un.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        // when current user is suspended
        linLayout_share_only_un.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share_only_un);
            try {
                if (bottomSheetShare_un.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare_un.setArguments(bundle);
                bottomSheetShare_un.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        // share_deux
        bottomSheetShare_deux = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getInvite_url(),
                model.getInvite_photoID(), "from_video", "", true);
        linLayout_share_deux.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share_deux);
            try {
                if (bottomSheetShare_deux.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare_deux.setArguments(bundle);
                bottomSheetShare_deux.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        // when current user is suspended
        linLayout_share_only_deux.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share_only_deux);
            try {
                if (bottomSheetShare_deux.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare_deux.setArguments(bundle);
                bottomSheetShare_deux.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        linLayout_like_un.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart_un.isSelected()) {
                    like_heart_un.setSelected(false);
                    image_un.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    like_heart_un.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeLike_un();
                        }
                    });

                } else {
                    like_heart_un.setSelected(true);
                    image_un.setImageResource(R.drawable.ic_heart_red);
                    like_heart_un.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!likedByCurrentUser_un) {
                                addNewLike_un();

                                removeLike_deux();
                            }
                        }
                    });
                }
            }
        });

        linLayout_like_deux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_heart_deux.isSelected()) {
                    like_heart_deux.setSelected(false);
                    image_deux.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    like_heart_deux.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeLike_deux();
                        }
                    });

                } else {
                    like_heart_deux.setSelected(true);
                    image_deux.setImageResource(R.drawable.ic_heart_red);
                    like_heart_deux.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!likedByCurrentUser_deux) {
                                addNewLike_deux();

                                removeLike_un();
                            }
                        }
                    });
                }
            }
        });

        like_star_un.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_star_un.isSelected()) {
                    like_star_un.setSelected(false);
                    conner_big_star_icon_un.setVisibility(View.GONE);
                    unlike_star_image_un.setImageResource(R.drawable.unlike_star);
                    like_star_un.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeCrush_un();
                        }
                    });

                } else {
                    like_star_un.setSelected(true);
                    unlike_star_image_un.setImageResource(R.drawable.big_star_icon);
                    insta_star_view_un.start();
                    conner_big_star_icon_un.setVisibility(View.VISIBLE);
                    like_star_un.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mCrushedByCurrentUser_un) {
                                addNewCrush_un();

                                removeCrush_deux();
                            }
                        }
                    });
                }
            }
        });

        like_star_deux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent Two Click
                Util.preventTwoClick(v);

                if (like_star_deux.isSelected()) {
                    like_star_deux.setSelected(false);
                    conner_big_star_icon_deux.setVisibility(View.GONE);
                    unlike_star_image_deux.setImageResource(R.drawable.unlike_star);
                    like_star_deux.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeCrush_deux();
                        }
                    });

                } else {
                    like_star_deux.setSelected(true);
                    unlike_star_image_deux.setImageResource(R.drawable.big_star_icon);
                    insta_star_view_deux.start();
                    conner_big_star_icon_deux.setVisibility(View.VISIBLE);
                    like_star_deux.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mCrushedByCurrentUser_deux) {
                                addNewCrush_deux();

                                removeCrush_un();
                            }
                        }
                    });
                }
            }
        });

        // join group
        getJoin_group(model.getSharing_admin_master(), model.getGroup_id(), join_group);
    }

    private final class GestureListenerLike_un extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            if (mModel != null) {
                if (from_full_video != null) {
                    context.finish();
                } else {
                    Intent intent = new Intent(context, FullChallengeVideoShare.class);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(photo);
                    intent.putExtra("challengeRepGson", myGson);
                    intent.putExtra("position", 0);
                    intent.putExtra("response_vid_double_single_top", "response_vid_double_single_top");
                    intent.putExtra("from_comment", "from_comment");
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, FullChallengeVideoShare.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(photo);
                intent.putExtra("challengeRepGson", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("response_vid_double_single_top", "response_vid_double_single_top");
                context.startActivity(intent);
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Log.d(TAG, "onDoubleTap: single tap detected.");
            if (like_heart_un.isSelected()) {
                like_heart_un.setSelected(false);
                image_un.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                like_heart_un.likeAnimation(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removeLike_un();
                    }
                });

            } else {
                like_heart_un.setSelected(true);
                image_un.setImageResource(R.drawable.ic_heart_red);
                // screen like animation
                mInstaLikeView.start();
                like_heart_un.likeAnimation(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!likedByCurrentUser_un) {
                            addNewLike_un();

                            removeLike_deux();
                        }
                    }
                });
            }
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    }

    private final class GestureListenerLike_deux extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            if (mModel != null) {
                if (from_full_video != null) {
                    context.finish();
                } else {
                    Intent intent = new Intent(context, FullChallengeVideoShare.class);
                    Gson gson = new Gson();
                    String myGson = gson.toJson(photo);
                    intent.putExtra("challengeRepGson", myGson);
                    intent.putExtra("position", 1);
                    intent.putExtra("response_vid_double_single_top", "response_vid_double_single_top");
                    intent.putExtra("from_comment", "from_comment");
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, FullChallengeVideoShare.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(photo);
                intent.putExtra("challengeRepGson", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("response_vid_double_single_top", "response_vid_double_single_top");
                context.startActivity(intent);
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Log.d(TAG, "onDoubleTap: single tap detected.");
            if (like_heart_deux.isSelected()) {
                like_heart_deux.setSelected(false);
                image_deux.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                like_heart_deux.likeAnimation(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removeLike_deux();
                    }
                });

            } else {
                like_heart_deux.setSelected(true);
                image_deux.setImageResource(R.drawable.ic_heart_red);
                // screen like animation
                mInstaLikeView2.start();
                like_heart_deux.likeAnimation(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!likedByCurrentUser_deux) {
                            addNewLike_deux();

                            removeLike_un();
                        }
                    }
                });
            }
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    }

    private void removeLike_un() {
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    String keyID = ds.getKey();
                    String user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    //case1: Then user already liked the photo
                    if(likedByCurrentUser_un
                            && requireNonNull(ds.getValue(Like.class)).getUser_id()
                            .equals(user_id)
                    ){

                        // update like count
                        int count = Integer.parseInt(number_of_likes_un.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0")) {
                            number_of_likes_un.setVisibility(View.GONE);
                            linLayout_count_like_un.setVisibility(View.INVISIBLE);
                        }
                        number_of_likes_un.setText(str);

                        // remove like points
                        removeLikePoints();

                        assert keyID != null;
                        mReference.child(context.getString(R.string.dbname_battle_media_share))
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_un))
                                .child(keyID)
                                .removeValue();

                        mReference.child(context.getString(R.string.dbname_battle))
                                .child(photo.getUser_id())
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_un))
                                .child(keyID)
                                .removeValue();

                        getLikesString_un();
                        updateLikes_un();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeLike_deux() {
        Log.d(TAG, "onDoubleTap: double tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    String keyID = singleSnapshot.getKey();
                    String user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    //case1: Then user already liked the photo
                    if(likedByCurrentUser_deux
                            && requireNonNull(singleSnapshot.getValue(Like.class)).getUser_id()
                            .equals(user_id)
                    ){

                        // update like count
                        int count = Integer.parseInt(number_of_likes_deux.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0")) {
                            number_of_likes_deux.setVisibility(View.GONE);
                            linLayout_count_like_deux.setVisibility(View.INVISIBLE);
                        }
                        number_of_likes_deux.setText(str);

                        // remove like points
                        removeLikePoints();

                        assert keyID != null;
                        mReference.child(context.getString(R.string.dbname_battle_media_share))
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_deux))
                                .child(keyID)
                                .removeValue();

                        mReference.child(context.getString(R.string.dbname_battle))
                                .child(photo.getUser_id())
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_deux))
                                .child(keyID)
                                .removeValue();

                        getLikesString_deux();
                        updateLikes_deux();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewLike_un(){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(number_of_likes_un.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            number_of_likes_un.setVisibility(View.VISIBLE);

        if (linLayout_count_like_un.getVisibility() != View.VISIBLE) {
            linLayout_count_like_un.setVisibility(View.VISIBLE);
        }
        number_of_likes_un.setText(str);

        // add like points
        addLikePoints();

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        mReference.child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un))
                .child(newLikeID)
                .setValue(like);

        mReference.child(context.getString(R.string.dbname_battle))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un))
                .child(newLikeID)
                .setValue(like);

        getLikesString_un();
        updateLikes_un();
    }

    private void addNewLike_deux(){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(number_of_likes_deux.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            number_of_likes_deux.setVisibility(View.VISIBLE);

        if (linLayout_count_like_deux.getVisibility() != View.VISIBLE) {
            linLayout_count_like_deux.setVisibility(View.VISIBLE);
        }
        number_of_likes_deux.setText(str);

        // add like points
        addLikePoints();

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        mReference.child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux))
                .child(newLikeID)
                .setValue(like);

        mReference.child(context.getString(R.string.dbname_battle))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux))
                .child(newLikeID)
                .setValue(like);

        getLikesString_deux();
        updateLikes_deux();
    }

    private void getCurrentUser(){
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);

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

    private void getLikesString_un(){
        Log.d(TAG, "getLikesString: getting likes string");
        try{
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media_share))
                    .child(photo.getPhoto_id())
                    .child(context.getString(R.string.field_likes_share_un));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users_un = new StringBuilder();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(requireNonNull(singleSnapshot.getValue(Like.class)).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                    users_un.append(user.getUsername());
                                    users_un.append(",");
                                }

                                //mitch, mitchell.tabian
                                likedByCurrentUser_un = users_un.toString().contains(mCurrentUser.getUsername() + ",");
                                setupLikesString_un();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        likedByCurrentUser_un = false;
                        setupLikesString_un();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage() );
            likedByCurrentUser_un = false;
            setupLikesString_un();
        }
    }

    private void updateLikes_un(){
        Log.d(TAG, "getLikesString: getting likes string");
        try{
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media_share))
                    .child(photo.getPhoto_id())
                    .child(context.getString(R.string.field_likes_share_un));

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    updateUsers_un = new StringBuilder();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(requireNonNull(singleSnapshot.getValue(Like.class)).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                    updateUsers_un.append(user.getUsername());
                                    updateUsers_un.append(",");
                                }

                                //mitch, mitchell.tabian
                                likedByCurrentUser_un = updateUsers_un.toString().contains(mCurrentUser.getUsername() + ",");
                                setupLikesString_un();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        likedByCurrentUser_un = false;
                        setupLikesString_un();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage() );
            likedByCurrentUser_un = false;
            setupLikesString_un();
        }
    }

    private void getLikesString_deux(){
        Log.d(TAG, "getLikesString: getting likes string");
        try{
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media_share))
                    .child(photo.getPhoto_id())
                    .child(context.getString(R.string.field_likes_share_deux));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users_deux = new StringBuilder();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(requireNonNull(singleSnapshot.getValue(Like.class)).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                    users_deux.append(user.getUsername());
                                    users_deux.append(",");
                                }

                                //mitch, mitchell.tabian
                                likedByCurrentUser_deux = users_deux.toString().contains(mCurrentUser.getUsername() + ",");
                                setupLikesString_deux();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        likedByCurrentUser_deux = false;
                        setupLikesString_deux();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage() );
            likedByCurrentUser_deux = false;
            setupLikesString_deux();
        }
    }

    private void updateLikes_deux(){
        Log.d(TAG, "getLikesString: getting likes string");
        try{
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media_share))
                    .child(photo.getPhoto_id())
                    .child(context.getString(R.string.field_likes_share_deux));

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    updateUsers_deux = new StringBuilder();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(context.getString(R.string.dbname_users))
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(requireNonNull(singleSnapshot.getValue(Like.class)).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    User user = Util_User.getUser(context, objectMap, ds);

                                    Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                    updateUsers_deux.append(user.getUsername());
                                    updateUsers_deux.append(",");
                                }

                                //mitch, mitchell.tabian
                                likedByCurrentUser_deux = updateUsers_deux.toString().contains(mCurrentUser.getUsername() + ",");
                                setupLikesString_deux();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        likedByCurrentUser_deux = false;
                        setupLikesString_deux();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (NullPointerException e){
            likedByCurrentUser_deux = false;
            setupLikesString_deux();
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupLikesString_un() {
        if (likedByCurrentUser_un) {
            if (!like_heart_un.isSelected()) {
                like_heart_un.setSelected(true);
                image_un.setImageResource(R.drawable.ic_heart_red);
            }

        } else {
            if (like_heart_un.isSelected()) {
                like_heart_un.setSelected(false);
                image_un.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupLikesString_deux() {
        if (likedByCurrentUser_deux) {
            if (!like_heart_deux.isSelected()) {
                like_heart_deux.setSelected(true);
                image_deux.setImageResource(R.drawable.ic_heart_red);
            }

        } else {
            if (like_heart_deux.isSelected()) {
                like_heart_deux.setSelected(false);
                image_deux.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        }
    }

    private void removeCrush_un() {
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mCrushedByCurrentUser_un &&
                            requireNonNull(ds.getValue(Crush.class)).getUser_id()
                                    .equals(requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update crush count
                        int count = Integer.parseInt(nber_of_crush_un.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nber_of_crush_un.setVisibility(View.GONE);
                        nber_of_crush_un.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share_un))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(photo.getUser_id())
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share_un))
                                .child(keyID)
                                .removeValue();

                        getCrushString_un();
                        updateCrush_un();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewCrush_un(){
        Log.d(TAG, "addNewCrush: adding new crush");
        // update crush count
        nber_of_crush_un.setVisibility(View.VISIBLE);
        int count = Integer.parseInt(nber_of_crush_un.getText().toString());
        String str = String.valueOf(count+1);
        nber_of_crush_un.setText(str);

        String newCrushID = myRef.push().getKey();
        Crush crush = new Crush();
        crush.setUser_id(requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newCrushID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un))
                .child(newCrushID)
                .setValue(crush);

        // affichage à l'écran
        getCrushString_un();
        updateCrush_un();
    }

    private void getCrushString_un(){
        Log.d(TAG, "getCrushString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersCrush_un = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found crush: " +user.getUsername());

                                mUsersCrush_un.append(user.getUsername());
                                mUsersCrush_un.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mCrushedByCurrentUser_un = mUsersCrush_un.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString_un();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser_un = false;
                    setupCrushString_un();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCrush_un(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateCrushUsers_un = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found crush: " +user.getUsername());

                                updateCrushUsers_un.append(user.getUsername());
                                updateCrushUsers_un.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mCrushedByCurrentUser_un = updateCrushUsers_un.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString_un();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser_un = false;
                    setupCrushString_un();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeCrush_deux() {
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mCrushedByCurrentUser_deux &&
                            requireNonNull(ds.getValue(Crush.class)).getUser_id()
                                    .equals(requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update crush count
                        int count = Integer.parseInt(nber_of_crush_deux.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nber_of_crush_deux.setVisibility(View.GONE);
                        nber_of_crush_deux.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share_deux))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(photo.getUser_id())
                                .child(photo.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share_deux))
                                .child(keyID)
                                .removeValue();

                        getCrushString_deux();
                        updateCrush_deux();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewCrush_deux(){
        Log.d(TAG, "addNewCrush: adding new crush");
        // update crush count
        nber_of_crush_deux.setVisibility(View.VISIBLE);
        int count = Integer.parseInt(nber_of_crush_deux.getText().toString());
        String str = String.valueOf(count+1);
        nber_of_crush_deux.setText(str);

        String newCrushID = myRef.push().getKey();
        Crush crush = new Crush();
        crush.setUser_id(user_id);

        assert newCrushID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux))
                .child(newCrushID)
                .setValue(crush);

        // affichage à l'écran
        getCrushString_deux();
        updateCrush_deux();
    }

    private void getCrushString_deux(){
        Log.d(TAG, "getCrushString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersCrush_deux = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            // comparaison des ID
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found like: " +user.getUsername());

                                mUsersCrush_deux.append(user.getUsername());
                                mUsersCrush_deux.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mCrushedByCurrentUser_deux = mUsersCrush_deux.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString_deux();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser_deux = false;
                    setupCrushString_deux();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCrush_deux(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateCrushUsers_deux = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                    Query query1 = reference1
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(requireNonNull(ds.getValue(Crush.class)).getUser_id());

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Log.d(TAG, "onDataChange: found crush: " +user.getUsername());

                                updateCrushUsers_deux.append(user.getUsername());
                                updateCrushUsers_deux.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            mCrushedByCurrentUser_deux = updateCrushUsers_deux.toString().contains(mCurrentUser.getUsername() + ",");

                            setupCrushString_deux();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    mCrushedByCurrentUser_deux = false;
                    setupCrushString_deux();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setupCrushString_un() {
        if (mCrushedByCurrentUser_un) {
            if (!like_star_un.isSelected()) {
                like_star_un.setSelected(true);
                conner_big_star_icon_un.setVisibility(View.VISIBLE);
                unlike_star_image_un.setImageResource(R.drawable.big_star_icon);
            }

        } else {
            if (like_star_un.isSelected()) {
                like_star_un.setSelected(false);
                conner_big_star_icon_un.setVisibility(View.GONE);
                unlike_star_image_un.setImageResource(R.drawable.unlike_star);
            }
        }
    }

    private void setupCrushString_deux() {
        if (mCrushedByCurrentUser_deux) {
            if (!like_star_deux.isSelected()) {
                like_star_deux.setSelected(true);
                conner_big_star_icon_deux.setVisibility(View.VISIBLE);
                unlike_star_image_deux.setImageResource(R.drawable.big_star_icon);
            }

        } else {
            if (like_star_deux.isSelected()) {
                like_star_deux.setSelected(false);
                conner_big_star_icon_deux.setVisibility(View.GONE);
                unlike_star_image_deux.setImageResource(R.drawable.unlike_star);
            }
        }
    }

    public void crushCount_un() {
        crush_count_un = 0;
        nber_of_crush_un.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list_un.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    crush_count_un++;
                }

                if (crush_count_un >= 1) {
                    nber_of_crush_un.setVisibility(View.VISIBLE);
                    nber_of_crush_un.setText(String.valueOf(crush_count_un));

                    if (crush_count_un >= 2)
                        crush_msg_un.setText(context.getString(R.string.several_crush));

                    nber_of_crush_un.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list_un);
                        context.startActivity(intent);
                    });

                    crush_msg_un.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list_un);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void crushCount_deux() {
        crush_count_deux = 0;
        nber_of_crush_deux.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list_deux.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    crush_count_deux++;
                }

                if (crush_count_deux >= 1) {
                    nber_of_crush_deux.setVisibility(View.VISIBLE);
                    nber_of_crush_deux.setText(String.valueOf(crush_count_deux));

                    if (crush_count_deux >= 2)
                        crush_msg_deux.setText(context.getString(R.string.several_crush));

                    nber_of_crush_deux.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list_deux);
                        context.startActivity(intent);
                    });

                    crush_msg_deux.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Crushers.class);
                        intent.putStringArrayListExtra("crusher_list", crusher_list_deux);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLikes_un() {
        likes_count_un = 0;
        number_of_likes_un.setVisibility(View.GONE);
        linLayout_count_like_un.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list_un.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count_un++;
                }

                if (likes_count_un >= 1) {
                    linLayout_count_like_un.setVisibility(View.VISIBLE);
                    number_of_likes_un.setVisibility(View.VISIBLE);
                    number_of_likes_un.setText(String.valueOf(likes_count_un));

                    linLayout_count_like_un.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Likers.class);
                        intent.putStringArrayListExtra("liker_list", liker_list_un);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLikes_deux() {
        likes_count_deux = 0;
        number_of_likes_deux.setVisibility(View.GONE);
        linLayout_count_like_deux.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list_deux.add(requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
                    likes_count_deux++;
                }

                if (likes_count_deux >= 1) {
                    linLayout_count_like_deux.setVisibility(View.VISIBLE);
                    number_of_likes_deux.setVisibility(View.VISIBLE);
                    number_of_likes_deux.setText(String.valueOf(likes_count_deux));

                    linLayout_count_like_deux.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, Likers.class);
                        intent.putStringArrayListExtra("liker_list", liker_list_deux);
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
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(photo.getPhoto_id())
                .child(context.getString(R.string.field_comment_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_battle_media_share))
                            .child(photo.getPhoto_id())
                            .child(context.getString(R.string.field_comment_share))
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

    private void setShare_un() {
        shares_count_un = 0;
        number_of_share_un.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(photo.getReponse_photoID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shares_count_un++;
                }

                if (shares_count_un >= 1) {
                    number_of_share_un.setVisibility(View.VISIBLE);
                    number_of_share_un.setText(String.valueOf(shares_count_un));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setShare_deux() {
        shares_count_deux = 0;
        number_of_share_deux.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(photo.getInvite_photoID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shares_count_deux++;
                }

                if (shares_count_deux >= 1) {
                    number_of_share_deux.setVisibility(View.VISIBLE);
                    number_of_share_deux.setText(String.valueOf(shares_count_deux));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // add post points
    private void addLikePoints() {
        if (photo.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(photo.getGroup_id());

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
                    .equalTo(photo.getGroup_id());

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
                                .child(photo.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(photo.getGroup_id())
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
        if (photo.getAdmin_master().equals(user_id)) {
            Query query = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(photo.getGroup_id());

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
                    .equalTo(photo.getGroup_id());

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
                                .child(photo.getGroup_id())
                                .updateChildren(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(photo.getGroup_id())
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

    // join group __________________________________________________________________________________
    private void getJoin_group(String admin_master, String group_id, TextView join_group) {
        isJoinedGroup(admin_master, group_id, join_group);

        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(admin_master,
                user_id, "", group_id);

        join_group.setOnClickListener(view -> {
            // hide the button in notification "invite user to be member of group"
            Query query2 = myRef.child(context.getString(R.string.dbname_notification))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(group_id);
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(admin_master,
                                        user_id, notification.getAdding_by(), group_id);

                                // follow the group
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_following))
                                        .child(user_id)
                                        .child(group_id)
                                        .setValue(map);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_followers))
                                        .child(group_id)
                                        .child(user_id)
                                        .setValue(map);

                                setJoinedGroup(join_group);

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
                                        notification.getAdding_by(), new_key, user_id, admin_master,
                                        "", group_id, date.getTime(),
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
                                        .child(group_id)
                                        .child(user_id)
                                        .removeValue();
                            }
                        } else {
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_following))
                                    .child(user_id)
                                    .child(group_id)
                                    .setValue(map);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_group_followers))
                                    .child(group_id)
                                    .child(user_id)
                                    .setValue(map);
                            setJoinedGroup(join_group);
                        }
                    }

                    if (!snapshot.exists()) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_following))
                                .child(user_id)
                                .child(group_id)
                                .setValue(map);

                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_group_followers))
                                .child(group_id)
                                .child(user_id)
                                .setValue(map);
                        setJoinedGroup(join_group);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void setJoinedGroup(TextView join_group){
        Log.d(TAG, "setFollowing: updating UI for following this group");
        join_group.setVisibility(View.GONE);
    }

    private void setUnFollowing(String admin_master, TextView join_group){
        Log.d(TAG, "setUnFollowing: updating UI for following this user");

        if (admin_master.equals(user_id))
            join_group.setVisibility(View.GONE);
        else
            join_group.setVisibility(View.VISIBLE);
    }

    private void isJoinedGroup(String admin_master, String group_id, TextView join_group){
        Log.d(TAG, "isFollowing: checking if following this group.");
        setUnFollowing(admin_master, join_group);

        Query query = myRef.child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(group_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setJoinedGroup(join_group);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

