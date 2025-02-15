package com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetAddCommentShare;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_category.Util_BattleChallengeCategory;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Crushers;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.full_challenge_video_share.FullChallengeVideoShare;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SharedVideoDoubleDisplay extends RecyclerView.ViewHolder {
    private static final String TAG = "VideoDoubleDisplay";

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
    public final ImageView menu_item;
    public final ImageView delete_suggestion;
    private final ImageView flag_icon;
    private final TextView nber_of_crush_un;
    private final TextView crush_msg_un;
    private final TextView nber_of_crush_deux;
    private final TextView crush_msg_deux;
    private final TextView category_un;
    private final TextView category_deux;
    private final TextView country_name;
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
    public final RelativeLayout relLayout_follow_sharing;
    public final RelativeLayout relLayout_follow_shared;

    // top single
    private final CircleImageView sharing_profile_photo;
    private final View sharing_view_online;
    private final TextView sharing_username;
    private final TextView sharing_tps_publication;
    private final ViewMoreTextView sharing_caption;
    private final TextView sharing_translate_comment;
    public final RelativeLayout relLayout_go_user_profile;

    // bottom single
    private final CircleImageView profile_photo_shared;
    private final View view_online_shared;
    private final TextView username_shared;
    private final TextView tps_publication;
    private final ViewMoreTextView caption;
    private final TextView translate_comment;
    public final RelativeLayout relLayout_go_user_share_profile;

    // vars
    private final AppCompatActivity context;
    private final GestureDetector detector_like_un;
    private final GestureDetector detector_like_deux;
    private BattleModel mVideo, mModel;
    private final RelativeLayout relLayout_view_overlay;
    private MyEditText mComment;
    private String from_full_video;
    private BottomSheetSharePublication bottomSheetShare_un;
    private BottomSheetSharePublication bottomSheetShare_deux;
    private User mCurrentUser;
    private final ArrayList<String> liker_list_un, liker_list_deux, crusher_list_un, crusher_list_deux;
    private boolean likedByCurrentUser_un;
    private boolean likedByCurrentUser_deux;
    private boolean mCrushedByCurrentUser_un;
    private boolean mCrushedByCurrentUser_deux;
    private int comments_count;
    private int crush_count_un;
    private int crush_count_deux;
    private int likes_count_un;
    private int likes_count_deux;
    private int shares_count_un;
    private int shares_count_deux;
    private StringBuilder mUsers_un;
    private StringBuilder updateUsers_un;
    private StringBuilder mUsers_deux;
    private StringBuilder updateUsers_deux;
    private StringBuilder mUsersCrush_un;
    private StringBuilder updateCrushUsers_un;
    private StringBuilder mUsersCrush_deux;
    private StringBuilder updateCrushUsers_deux;

    // firebase
    private final DatabaseReference myRef;
    private final FirebaseDatabase database;

    @SuppressLint("ClickableViewAccessibility")
    public SharedVideoDoubleDisplay(AppCompatActivity context, BattleModel mModel,
                                    MyEditText mComment,
                                    String from_full_video, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        if (mModel != null) {
            this.mModel = mModel;
            this.mVideo = mModel;
            this.mComment = mComment;
            this.from_full_video = from_full_video;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        database=FirebaseDatabase.getInstance();

        liker_list_un = new ArrayList<>();
        liker_list_deux = new ArrayList<>();
        crusher_list_un = new ArrayList<>();
        crusher_list_deux = new ArrayList<>();

        thumbnail_un = itemView.findViewById(R.id.thumbnail_un);
        thumbnail_deux = itemView.findViewById(R.id.thumbnail_deux);
        category_un = itemView.findViewById(R.id.category_un);
        category_deux = itemView.findViewById(R.id.category_deux);
        menu_item = itemView.findViewById(R.id.menu_item);
        delete_suggestion = itemView.findViewById(R.id.delete_suggestion);
        RelativeLayout relLayout_main_view = itemView.findViewById(R.id.relLayout_main_view);

        linLayout_comment = itemView.findViewById(R.id.linLayout_comment);
        number_of_comments = itemView.findViewById(R.id.number_of_comments);
        number_of_likes_un = itemView.findViewById(R.id.number_of_likes_un);
        number_of_share_un = itemView.findViewById(R.id.number_of_share_un);
        linLayout_share_un = itemView.findViewById(R.id.linLayout_share_un);

        number_of_likes_deux = itemView.findViewById(R.id.number_of_likes_deux);
        number_of_share_deux = itemView.findViewById(R.id.number_of_share_deux);
        linLayout_share_deux = itemView.findViewById(R.id.linLayout_share_deux);
        relLayout_follow_sharing = itemView.findViewById(R.id.relLayout_follow_sharing);
        relLayout_follow_shared = itemView.findViewById(R.id.relLayout_follow_shared);
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
        linLayout_like_un = itemView.findViewById(R.id.linLayout_like_un);
        linLayout_like_deux = itemView.findViewById(R.id.linLayout_like_deux);
        linLayout_count_like_un = itemView.findViewById(R.id.linLayout_count_like_un);
        linLayout_count_like_deux = itemView.findViewById(R.id.linLayout_count_like_deux);

        image_un = itemView.findViewById(R.id.image_un);
        like_heart_un = itemView.findViewById(R.id.like_heart_un);
        image_deux = itemView.findViewById(R.id.image_deux);
        like_heart_deux = itemView.findViewById(R.id.like_heart_deux);
        mInstaLikeView = itemView.findViewById(R.id.insta_like_view);
        mInstaLikeView2 = itemView.findViewById(R.id.view);
        country_name = itemView.findViewById(R.id.country_name);
        flag_icon = itemView.findViewById(R.id.flag_icon);

        // single top
        sharing_profile_photo = itemView.findViewById(R.id.sharing_profile_photo);
        sharing_view_online = itemView.findViewById(R.id.sharing_view_online);
        sharing_username = itemView.findViewById(R.id.sharing_username);
        sharing_caption = itemView.findViewById(R.id.sharing_caption);
        sharing_translate_comment = itemView.findViewById(R.id.sharing_translate_comment);
        sharing_tps_publication = itemView.findViewById(R.id.sharing_tps_publication);
        relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);

        // single bottom
        profile_photo_shared = itemView.findViewById(R.id.profile_photo_shared);
        view_online_shared = itemView.findViewById(R.id.view_online_shared);
        username_shared = itemView.findViewById(R.id.username_shared);
        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        relLayout_go_user_share_profile = itemView.findViewById(R.id.relLayout_go_user_share_profile);
        LinearLayout parent = itemView.findViewById(R.id.parent);

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

    public void init(BattleModel model) {
        mVideo = model;

        sharing_caption.setCharText("");
        caption.setCharText("");
        sharing_caption.setVisibility(View.GONE);
        caption.setVisibility(View.GONE);
        translate_comment.setVisibility(View.GONE);
        sharing_translate_comment.setVisibility(View.GONE);
        number_of_comments.setText("0");
        number_of_likes_un.setText("0");
        number_of_likes_deux.setText("0");
        nber_of_crush_un.setText("0");
        nber_of_crush_deux.setText("0");
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

        // verify if user is online /___top. Inverser les ID
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
                                    if (!model.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        sharing_view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // verify if user is online /___bottom
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getUser_id_sharing())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            if(status != null && !status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online_shared.setVisibility(View.GONE);
                                }else{
                                    if (!model.getUser_id_sharing().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online_shared.setVisibility(View.VISIBLE);
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
                .load(model.getThumbnail_un())
                .into(thumbnail_un);

        // l'image deux
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(model.getThumbnail_deux())
                .into(thumbnail_deux);

        // country name and flag
        country_name.setText(model.getCountry_name());
        String country_id = model.getCountryID();
        Util.getCountryFlag(country_id, flag_icon);

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

        // caption __________________________________________________________________
        String description = model.getCaption();

        if (!TextUtils.isEmpty(description)) {
            caption.setCharText(description);
            caption.setVisibility(View.VISIBLE);
        }

        // Get the current language in device
        String language2 = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier2 =
                LanguageIdentification.getClient();
        languageIdentifier2.identifyLanguage(description)
                .addOnSuccessListener(
                        languageCode -> {
                            assert languageCode != null;
                            if (languageCode.equals("und")) {
                                Log.i(TAG, "Can't identify language.");
                            } else {
                                Log.i(TAG, "Language: " + languageCode);
                                if (!languageCode.equals(language2))
                                    translate_comment.setVisibility(View.VISIBLE);
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Model couldn’t be loaded or other internal error.
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        translate_comment.setOnClickListener(view -> {
            translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language2,         //language to
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

        //get the profile image and username /___top
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

                    Log.d(TAG, "onDataChange: found user: "+user.getUsername());

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(sharing_profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    String userName = Objects.requireNonNull(user.getUsername());
                    sharing_username.setText(userName);

                    relLayout_go_user_profile.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
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
        // _________________________________________________________________
        //get the profile image and username /___bottom
        Query query1 = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id_sharing());

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            .into(profile_photo_shared);

                    Glide.with(context.getApplicationContext())
                            .load(user.getFull_profileUrl())
                            .preload();

                    String userName = user.getUsername();
                    username_shared.setText(userName);

                    relLayout_go_user_share_profile.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent;
                        if (user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
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
                    null, null, null, null,
                    null, null, model, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, "video_double_on_miioky", null, null, relLayout_view_overlay);
            linLayout_comment.setOnClickListener(view -> {
                if (bottomSheetFragmentShare.isAdded())
                    return;
                bottomSheetFragmentShare.show(context.getSupportFragmentManager(), "TAG");
            });
        }

        // share_un
        bottomSheetShare_un = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getUrlUn(),
                model.getPhoto_id_un(), "from_video", "", true);
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

        // share_deux
        bottomSheetShare_deux = new BottomSheetSharePublication(context, model, model.getUser_id(), model.getUrlDeux(),
                model.getPhoto_id_deux(), "from_video", "", true);
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
                    String myGson = gson.toJson(mVideo);
                    intent.putExtra("challengeGson", myGson);
                    intent.putExtra("position", 0);
                    intent.putExtra("video_double_single_top_bottom", "video_double_single_top_bottom");
                    intent.putExtra("from_comment", "from_comment");
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, FullChallengeVideoShare.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(mVideo);
                intent.putExtra("challengeGson", myGson);
                intent.putExtra("position", 0);
                intent.putExtra("video_double_single_top_bottom", "video_double_single_top_bottom");
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
                    String myGson = gson.toJson(mVideo);
                    intent.putExtra("challengeGson", myGson);
                    intent.putExtra("position", 1);
                    intent.putExtra("video_double_single_top_bottom", "video_double_single_top_bottom");
                    intent.putExtra("from_comment", "from_comment");
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, FullChallengeVideoShare.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(mVideo);
                intent.putExtra("challengeGson", myGson);
                intent.putExtra("position", 1);
                intent.putExtra("video_double_single_top_bottom", "video_double_single_top_bottom");
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
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (likedByCurrentUser_un &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update like count
                        int count = Integer.parseInt(number_of_likes_un.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0")) {
                            number_of_likes_un.setVisibility(View.GONE);
                            linLayout_count_like_un.setVisibility(View.INVISIBLE);
                        }
                        number_of_likes_un.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(mVideo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_un))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(mVideo.getUser_id())
                                .child(mVideo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_un))
                                .child(keyID)
                                .removeValue();

                        // enlever la photo de profil

                        getLikesString_un();
                        updateLikes_un();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeLike_deux() {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (likedByCurrentUser_deux &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update like count
                        int count = Integer.parseInt(number_of_likes_deux.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0")) {
                            number_of_likes_deux.setVisibility(View.GONE);
                            linLayout_count_like_deux.setVisibility(View.INVISIBLE);
                        }
                        number_of_likes_deux.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(mVideo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_deux))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(mVideo.getUser_id())
                                .child(mVideo.getPhoto_id())
                                .child(context.getString(R.string.field_likes_share_deux))
                                .child(keyID)
                                .removeValue();

                        getLikesString_deux();
                        updateLikes_deux();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike_un(){
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

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(mVideo.getUser_id())
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString_un();
        updateLikes_un();
    }

    public void addNewLike_deux(){
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

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux))
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(mVideo.getUser_id())
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux))
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString_deux();
        updateLikes_deux();
    }

    private void getLikesString_un(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers_un = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
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

                                mUsers_un.append(user.getUsername());
                                mUsers_un.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            likedByCurrentUser_un = mUsers_un.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikesString_un();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    likedByCurrentUser_un = false;
                    setupLikesString_un();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikesString_deux(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux));

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers_deux = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
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

                                mUsers_deux.append(user.getUsername());
                                mUsers_deux.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            likedByCurrentUser_deux = mUsers_deux.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikesString_deux();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    likedByCurrentUser_deux = false;
                    setupLikesString_deux();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLikes_un(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un));

        // on parcours tous les likes
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateUsers_un = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
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

                                updateUsers_un.append(user.getUsername());
                                updateUsers_un.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            likedByCurrentUser_un = updateUsers_un.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikesString_un();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    likedByCurrentUser_un = false;
                    setupLikesString_un();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLikes_deux(){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux));

        // on parcours tous les likes
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateUsers_deux = new StringBuilder();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    // ici on récupère l'identifiant du likeur et le like
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

                                updateUsers_deux.append(user.getUsername());
                                updateUsers_deux.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
                            likedByCurrentUser_deux = updateUsers_deux.toString().contains(mCurrentUser.getUsername() + ",");
                            setupLikesString_deux();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(!snapshot.exists()){
                    likedByCurrentUser_deux = false;
                    setupLikesString_deux();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mCrushedByCurrentUser_un &&
                            Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update crush count
                        int count = Integer.parseInt(nber_of_crush_un.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nber_of_crush_un.setVisibility(View.GONE);
                        nber_of_crush_un.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(mVideo.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share_un))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(mVideo.getUser_id())
                                .child(mVideo.getPhoto_id())
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
        crush.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newCrushID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(mVideo.getUser_id())
                .child(mVideo.getPhoto_id())
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
                .child(mVideo.getPhoto_id())
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
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

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
                .child(mVideo.getPhoto_id())
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
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

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
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mCrushedByCurrentUser_deux &&
                            Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id()
                                    .equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid())) {

                        // update crush count
                        int count = Integer.parseInt(nber_of_crush_deux.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            nber_of_crush_deux.setVisibility(View.GONE);
                        nber_of_crush_deux.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(mVideo.getPhoto_id())
                                .child(context.getString(R.string.field_crush_share_deux))
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(mVideo.getUser_id())
                                .child(mVideo.getPhoto_id())
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
        crush.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        assert newCrushID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux))
                .child(newCrushID)
                .setValue(crush);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(mVideo.getUser_id())
                .child(mVideo.getPhoto_id())
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
                .child(mVideo.getPhoto_id())
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
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

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
                .child(mVideo.getPhoto_id())
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
                            .equalTo(Objects.requireNonNull(ds.getValue(Crush.class)).getUser_id());

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
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list_un.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_crush_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    crusher_list_deux.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_un));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list_un.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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
                .child(mVideo.getPhoto_id())
                .child(context.getString(R.string.field_likes_share_deux));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // add user id to the list
                    liker_list_deux.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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
                .child(mVideo.getPhoto_id())
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
                            .child(mVideo.getPhoto_id())
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
                .child(mVideo.getPhoto_id_un());

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
                .child(mVideo.getPhoto_id_deux());

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
}

