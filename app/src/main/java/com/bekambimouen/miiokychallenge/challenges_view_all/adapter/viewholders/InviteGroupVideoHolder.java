package com.bekambimouen.miiokychallenge.challenges_view_all.adapter.viewholders;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.danikula_cache.CacheListener;
import com.bekambimouen.miiokychallenge.danikula_cache.HttpProxyCacheServer;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.toro.media.VolumeInfo;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.csguys.viewmoretextview.ViewMoreTextView;
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

import java.io.File;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bekambimouen.miiokychallenge.toro.ToroPlayer;
import com.bekambimouen.miiokychallenge.toro.ToroUtil;
import com.bekambimouen.miiokychallenge.toro.media.PlaybackInfo;
import com.bekambimouen.miiokychallenge.toro.widget.Container;

public class InviteGroupVideoHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, Player.Listener,
        CacheListener {
    private static final String TAG = "VideoViewHolder";

    // widgets
    private final View view_online;
    private final ImageView thumbnail;
    public final ImageView menu_item;
    private final CircleImageView profile;
    private final TextView username;
    private final TextView tps_publication;
    private final ViewMoreTextView caption;
    private final TextView translate_comment;
    private final TextView category;
    private final RelativeLayout relLayout_category;
    private final ProgressBar progressBar;

    private final TextView downloaded_count;
    private final TextView share_count;
    private final RelativeLayout relLayout_count_actions;
    private final RelativeLayout relLayout_download;
    private final RelativeLayout relLayout_share;
    private final LinearLayout linLayout_share;

    // group
    private final ImageView group_profile_photo;
    private final TextView group_name;
    private final RelativeLayout relLayout_group_name;
    private final RelativeLayout relLayout_username;
    private final TextView join_group;


    // vars
    private final AppCompatActivity context;
    private final View parent;
    private BottomSheetSharePublication bottomSheetShare;
    private ModelInvite video;
    private final RelativeLayout relLayout_view_overlay;
    private int saved_count = 0;
    private int shared_count = 0;

    public final RelativeLayout rel_vol;
    private final GestureDetector detector_video;
    private final RelativeLayout relLayout_img_play;
    private boolean isPlaying = true;
    public final ImageView img_vol;
    public Player player;
    private final PlayerView playerView;
    private Uri mediaUri;

    private final TextView positionTextView;
    private final TextView durationTextView;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;

    // firebase
    private final DatabaseReference myRef;
    private final FirebaseDatabase database;
    private final String user_id;

    @SuppressLint("ClickableViewAccessibility")
    public InviteGroupVideoHolder(AppCompatActivity context, RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
        super(itemView);
        parent = itemView;
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        playerView = itemView.findViewById(R.id.playerView);
        positionTextView = playerView.findViewById(R.id.exo_position);
        durationTextView = playerView.findViewById(R.id.exo_duration);
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        thumbnail = itemView.findViewById(R.id.thumbnail);
        rel_vol = itemView.findViewById(R.id.rel_vol);
        relLayout_img_play = itemView.findViewById(R.id.relLayout_img_play);
        img_vol = itemView.findViewById(R.id.img_vol);
        progressBar = itemView.findViewById(R.id.progressBar);

        view_online = itemView.findViewById(R.id.view_online);
        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        category = itemView.findViewById(R.id.category);
        relLayout_category = itemView.findViewById(R.id.relLayout_category);
        tps_publication = itemView.findViewById(R.id.tps_publication);
        username = itemView.findViewById(R.id.username);
        profile = itemView.findViewById(R.id.profile_photo);
        menu_item = itemView.findViewById(R.id.menu_item);

        downloaded_count = itemView.findViewById(R.id.downloaded_count);
        share_count = itemView.findViewById(R.id.share_count);
        relLayout_count_actions = itemView.findViewById(R.id.relLayout_count_actions);
        relLayout_download = itemView.findViewById(R.id.relLayout_download);
        relLayout_share = itemView.findViewById(R.id.relLayout_share);
        linLayout_share = itemView.findViewById(R.id.linLayout_share);

        // group
        group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
        group_name = itemView.findViewById(R.id.group_name);
        relLayout_username = itemView.findViewById(R.id.relLayout_username);
        relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);
        join_group = itemView.findViewById(R.id.join_group);

        // Play/Pause video
        detector_video = new GestureDetector(context, new GestureListenerPausePlay());
        playerView.setOnTouchListener((view, motionEvent) -> detector_video.onTouchEvent(motionEvent));
    }

    private final class GestureListenerPausePlay extends GestureDetector.SimpleOnGestureListener {
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
            if (isPlaying)  {
                isPlaying = false;
                if (player != null && player.isPlaying()) {
                    player.pause();
                }
                thumbnail.setVisibility(View.GONE);
                relLayout_img_play.setVisibility(View.VISIBLE);
            } else {
                isPlaying = true;
                if (player != null) {
                    player.play();
                }
                relLayout_img_play.setVisibility(View.GONE);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Log.d(TAG, "onDoubleTap: single tap detected.");

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
        }
    }

    public void bindVideo(ModelInvite model) {
        video = model;

        caption.setVisibility(View.GONE);
        translate_comment.setVisibility(View.GONE);

        // verify if user is online
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(model.getInvite_userID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                            assert status != null;
                            if(!status.isEmpty()){
                                if(status.equals(context.getString(R.string.field_offLine))){
                                    view_online.setVisibility(View.GONE);
                                }else{
                                    if (!model.getInvite_userID().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        setSavedVideosInGallery();
        setSharedVideo();

        //set the time it was posted
        long tv_date = video.getDate_created();
        long time = (System.currentTimeMillis() - tv_date);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(TimeUtils.getTime(context, tv_date));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

        // category
        String set_category = Util_InviteChallengeCategory.getInviteChallengeCategory(context, model);
        category.setText(Html.fromHtml("#"+set_category));

        String invite_category_status = model.getInvite_category_status();
        relLayout_category.setOnClickListener(view -> {
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

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, null, model.getInvite_userID(), model.getInvite_url(),
                model.getInvite_photoID(), "from_video", "", true);
        linLayout_share.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "bindVideo: "+e.getMessage());
            }
        });

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(video.getThumbnail_invite())
                .placeholder(R.drawable.black_person)
                .into(thumbnail);

        Glide.with(context.getApplicationContext())
                .load(video.getInvite_profile_photo())
                .placeholder(R.drawable.ic_baseline_person_24)
                .centerCrop()
                .into(profile);

        HttpProxyCacheServer proxy = App.getProxy(context);
        String proxyUrl = proxy.getProxyUrl(video.getInvite_url());

        Uri uri = Uri.parse(proxyUrl);
        if (uri != null) {
            mediaUri = uri;
        }

        String description = model.getInvite_caption();

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
                            // Model couldn’t be loaded or other internal error.
                            // ...
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        });

        if (!TextUtils.isEmpty(description)) {
            caption.setVisibility(View.VISIBLE);
            caption.setCharText(description);
        }

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

        username.setText(model.getInvite_username());

        //get the profile image and username
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query = reference1
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getInvite_userID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String userID = user.getUser_id();

                    profile.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);

                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Gson gson = new Gson();
                        String myGson = gson.toJson(model);
                        Intent intent = new Intent(context, GroupProfileMember.class);
                        intent.putExtra("groupBattleModel", myGson);
                        intent.putExtra("userID", userID);
                        intent.putExtra("group_id", model.getGroup_id());
                        context.startActivity(intent);
                    });

                    Query query4 = myRef
                            .child(context.getString(R.string.dbname_user_group))
                            .child(model.getAdmin_master())
                            .orderByChild(context.getString(R.string.field_group_id))
                            .equalTo(model.getGroup_id());

                    query4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                Glide.with(context.getApplicationContext())
                                        .load(user_groups.getProfile_photo())
                                        .transition(DrawableTransitionOptions.withCrossFade(500))
                                        .into(group_profile_photo);

                                group_name.setText(user_groups.getGroup_name());

                                relLayout_username.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);

                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    Intent intent;
                                    if (user_groups.getUser_id().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                profile.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);

                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                    Gson gson = new Gson();
                                    String myGson = gson.toJson(user_groups);
                                    Intent intent;
                                    if (user_groups.getUser_id().equals(user_id)) {
                                        intent = new Intent(context, GroupProfileAdmin.class);
                                    } else {
                                        intent = new Intent(context, GroupProfileMember.class);
                                    }
                                    intent.putExtra("user_groups", myGson);
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("group_id", user_groups.getGroup_id());
                                    context.startActivity(intent);
                                });

                                // to know if current user is admin
                                if (user_groups.getAdmin_master().equals(user_id)) {
                                    Gson gson = new Gson();
                                    String myJson = gson.toJson(user_groups);

                                    group_profile_photo.setOnClickListener(v -> {
                                        Log.d(TAG, "onClick: navigating to profile of: " +
                                                user.getUsername());
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);

                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent = new Intent(context, GroupViewAdmin.class);
                                        intent.putExtra("user_groups", myJson);
                                        context.startActivity(intent);
                                    });

                                    relLayout_group_name.setOnClickListener(v -> {
                                        Log.d(TAG, "onClick: navigating to profile of: " +
                                                user.getUsername());
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

                                                group_profile_photo.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());
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
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());
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

                                                group_profile_photo.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());
                                                    if (relLayout_view_overlay != null)
                                                        relLayout_view_overlay.setVisibility(View.VISIBLE);

                                                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                                                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                                    Intent intent = new Intent(context, GroupViewProfile.class);
                                                    intent.putExtra("user_groups", myJson);
                                                    context.startActivity(intent);
                                                });

                                                relLayout_group_name.setOnClickListener(v -> {
                                                    Log.d(TAG, "onClick: navigating to profile of: " +
                                                            user.getUsername());
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
            args.putParcelable("model_invite", model);
            args.putString("miioky", "miioky");
            bottomSheet.setArguments(args);
            bottomSheet.show(context.getSupportFragmentManager(),
                    "TAG");
        });

        isFollowing();

        // get the following model data
        HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(model.getAdmin_master(), user_id, "", model.getGroup_id());

        join_group.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .child(model.getGroup_id())
                    .setValue(map);

            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_followers))
                    .child(model.getGroup_id())
                    .child(user_id)
                    .setValue(map);
            setFollowing();
        });
    }

    private void setFollowing(){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        join_group.setVisibility(View.GONE);
    }

    private void setUnFollowing(){
        Log.d(TAG, "setUnFollowing: updating UI for following this user");

        if (video.getAdmin_master().equals(user_id))
            join_group.setVisibility(View.GONE);
        else
            join_group.setVisibility(View.VISIBLE);
    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnFollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(context.getString(R.string.dbname_group_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(video.getGroup_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return this.playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        if (player != null) {
            PlaybackInfo playbackInfo = new PlaybackInfo();
            playbackInfo.setResumePosition(player.getCurrentPosition());
            playbackInfo.setVolumeInfo(new VolumeInfo(player.getVolume() == 0f, player.getVolume()));
            return playbackInfo;
        } else {
            return new PlaybackInfo();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void initialize(@NonNull  Container container, @NonNull  PlaybackInfo playbackInfo) {
        if (player == null) {
            player = new ExoPlayer.Builder(context).build();

        }
        player.addListener(this);
        if (mediaUri != null) {
            player.clearMediaItems();
            MediaItem mediaItem = MediaItem.fromUri(mediaUri);
            player.setMediaItem(mediaItem);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    // Update the duration when the player is ready
                    long duration = player.getDuration();
                    durationTextView.setText(androidx.media3.common.util.Util.getStringForTime(formatBuilder, formatter, duration));
                }
            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
                updatePosition();
            }

            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {

                updatePosition();
            }
        });

        player.prepare();

        if (playerView != null) {
            playerView.setPlayer(player);
        }

        parent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull  View view) {

            }

            @Override
            public void onViewDetachedFromWindow(@NonNull  View view) {
                thumbnail.setVisibility(View.VISIBLE);
            }
        });
    }

    @UnstableApi
    private void updatePosition() {
        long position = player.getCurrentPosition();
        positionTextView.setText(androidx.media3.common.util.Util.getStringForTime(formatBuilder, formatter, position));
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            onBuffering();
        } else if (playbackState == Player.STATE_READY) {
            if (player.getPlayWhenReady()) {
                onPlaying();
            } else {
                onPaused();
            }
        } else if (playbackState == Player.STATE_ENDED) {
            onCompleted();
        }
    }

    @Override
    public void play() {
        if (player != null && !player.isPlaying()) {
            player.play();
        }
    }

    @Override
    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        relLayout_img_play.setVisibility(View.GONE);
        return player != null && player.isPlaying();
    }

    @Override
    public void release() {
        if (player != null) {
            player.removeListener(this);
            player.release();
            player = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.25;
    }

    @Override
    public int getPlayerOrder() {
        return getLayoutPosition();
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

    }

    @Override public void onFirstFrameRendered() {
    }

    @Override public void onBuffering() {
        thumbnail.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void onPlaying() {
        progressBar.setVisibility(View.GONE);
        thumbnail.setVisibility(View.GONE);
    }

    @Override public void onPaused() {
        // pour que la vignette n'apparaisse pas losqu'on met la video en pause
        thumbnail.setVisibility(View.VISIBLE);
    }

    @Override public void onCompleted() {
    }

    // count number of video downloaded
    public void setSavedVideosInGallery() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(video.getInvite_photoID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    saved_count++;
                }

                if (saved_count >= 1) {
                    relLayout_count_actions.setVisibility(View.VISIBLE);
                    relLayout_download.setVisibility(View.VISIBLE);

                    downloaded_count.setText(String.valueOf(saved_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // share video coubt
    private void setSharedVideo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(context.getString(R.string.dbname_share_video))
                .child(video.getInvite_photoID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shared_count++;
                }

                if (shared_count >= 1) {
                    relLayout_count_actions.setVisibility(View.VISIBLE);
                    relLayout_share.setVisibility(View.VISIBLE);

                    share_count.setText(String.valueOf(shared_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
