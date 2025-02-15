package com.bekambimouen.miiokychallenge.challenge.adapter.viewholder;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenges_view_all.ViewChallengeVideos;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.challenge_home.model.AcceptedChallengeModel;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.challenges_response.ChallengesResponse;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_AcceptedChallengeModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "VideoHolder";

    // widgets
    private final ViewMoreTextView caption;
    private final TextView translate_comment;
    private final ImageView menu_item;
    private final ImageView thumbnail;
    private final View view_online;
    private final CircleImageView profile_photo;
    private final TextView username;
    private final TextView tps_publication;
    private final TextView category;
    public final TextView button;
    private final TextView nber_accept_challenge;
    private final RelativeLayout relLayout_category;
    private final RelativeLayout relLayout_answered_challenge;
    private final RelativeLayout relLayout_go_user_profile;

    private final TextView downloaded_count;
    private final TextView share_count;
    private final RelativeLayout relLayout_count_actions;
    private final RelativeLayout relLayout_download;
    private final RelativeLayout relLayout_share;

    private final LinearLayout linLayout_share;

    // vars
    private BottomSheetSharePublication bottomSheetShare;
    private final AppCompatActivity context;
    private final GestureDetector detector_full_image;
    private ModelInvite modelInvite;
    private final String from_view_profile_user_id;
    private final RelativeLayout relLayout_view_overlay;
    private final ArrayList<String> invite_photo_id_List, user_id_List;
    private String description;
    private int saved_count;
    private int shared_count;
    private int k = 0;

    // firebase
    private final DatabaseReference myRef;
    private final FirebaseDatabase database;
    private final String user_id;

    @SuppressLint("ClickableViewAccessibility")
    public VideoHolder(AppCompatActivity context, String from_view_profile_user_id, RelativeLayout relLayout_view_overlay,
                       @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.from_view_profile_user_id = from_view_profile_user_id;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        invite_photo_id_List = new ArrayList<>();
        user_id_List = new ArrayList<>();

        button = itemView.findViewById(R.id.button);
        nber_accept_challenge = itemView.findViewById(R.id.nber_accept_challenge);
        relLayout_answered_challenge = itemView.findViewById(R.id.relLayout_answered_challenge);

        view_online = itemView.findViewById(R.id.view_online);
        profile_photo = itemView.findViewById(R.id.profile_photo);
        username = itemView.findViewById(R.id.username);
        menu_item = itemView.findViewById(R.id.menu_item);
        caption = itemView.findViewById(R.id.caption);
        translate_comment = itemView.findViewById(R.id.translate_comment);
        category = itemView.findViewById(R.id.category);
        relLayout_category = itemView.findViewById(R.id.relLayout_category);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);

        tps_publication = itemView.findViewById(R.id.tps_publication);

        downloaded_count = itemView.findViewById(R.id.downloaded_count);
        share_count = itemView.findViewById(R.id.share_count);
        relLayout_count_actions = itemView.findViewById(R.id.relLayout_count_actions);
        relLayout_download = itemView.findViewById(R.id.relLayout_download);
        relLayout_share = itemView.findViewById(R.id.relLayout_share);
        linLayout_share = itemView.findViewById(R.id.linLayout_share);

        detector_full_image = new GestureDetector(context, new GestureListener());
        thumbnail.setOnTouchListener((view, motionEvent) -> detector_full_image.onTouchEvent(motionEvent));
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
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
            Intent intent = new Intent(context, ViewChallengeVideos.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(modelInvite);
            intent.putExtra("modelInvite", myGson);
            if (from_view_profile_user_id != null)
                intent.putExtra("userID", from_view_profile_user_id);
            else
                intent.putExtra("userID", user_id);
            intent.putExtra("from_view_my_challenge", "from_view_my_challenge");
            context.startActivity(intent);
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

    @SuppressLint("SetTextI18n")
    public void bindView(ModelInvite model) {
        modelInvite = model;

        caption.setCharText("");
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

        setSavedImagesInGallery();
        setSharedImage();
        getChallengesNumberOfResponses();

        //set the time it was posted
        long tv_date = model.getDate_created();
        long time = (System.currentTimeMillis() - tv_date);
        if (time >= 2*3600*24000254025L)
            tps_publication.setText(TimeUtils.getTime(context, tv_date));
        else
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(model.getThumbnail_invite())
                .placeholder(R.drawable.black_person)
                .into(thumbnail);

        description = "";
        caption.setCharText("");
        caption.setVisibility(View.GONE);

        description = model.getInvite_caption();

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

        String set_category = Util_InviteChallengeCategory.getInviteChallengeCategory(context, model);
        category.setText(Html.fromHtml("#"+set_category));

        String invite_category_status = model.getInvite_category_status();
        relLayout_category.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, GridCategories.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("modelInvite", myGson);
            intent.putExtra("category_status", invite_category_status);
            context.startActivity(intent);
        });

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, null, model.getInvite_userID(), model.getInvite_url(),
                model.getInvite_photoID(), "from_video", "", false);
        linLayout_share.setOnClickListener(view -> {
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "bindView: "+e.getMessage());
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

        //get the profile image and username
        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getInvite_userID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username.setText("");
                username.setVisibility(View.GONE);

                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo);

                    String userName = user.getUsername();
                    if (!TextUtils.isEmpty(userName))
                        username.setVisibility(View.VISIBLE);
                    username.setText(userName);

                    relLayout_go_user_profile.setOnClickListener(view -> {
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the number of acceptation
    private void getChallengesNumberOfResponses() {
        Query myQuery = myRef
                .child(context.getString(R.string.dbname_battle_response_challenge))
                .child(modelInvite.getInvite_photoID());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                k = 0;
                nber_accept_challenge.setText("");
                nber_accept_challenge.setVisibility(View.GONE);
                relLayout_answered_challenge.setVisibility(View.GONE);
                user_id_List.clear();
                invite_photo_id_List.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) ds.getValue();
                    assert objectMap != null;
                    AcceptedChallengeModel challengeModel = Util_AcceptedChallengeModel.getAcceptedChallengeModel(context, objectMap);

                    k++;
                    user_id_List.add(challengeModel.getId());
                    invite_photo_id_List.add(challengeModel.getInvite_photoID());
                }

                if (k >= 1) {
                    relLayout_answered_challenge.setVisibility(View.VISIBLE);
                    nber_accept_challenge.setVisibility(View.VISIBLE);

                    if (k == 1)
                        nber_accept_challenge.setText(Html.fromHtml("<b>"+ k +"</b> "+
                                (context.getString(R.string.one_person_accept_your_challenge))));

                    if (k >= 2)
                        nber_accept_challenge.setText(Html.fromHtml("<b>"+ k +"</b> "+
                                (context.getString(R.string.several_person_accept_your_challenge))));
                }

                relLayout_answered_challenge.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(context, ChallengesResponse.class);
                    intent.putExtra("invite_photo_id_List", invite_photo_id_List);
                    intent.putExtra("id_List", user_id_List);
                    context.startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // count number of video downloaded
    public void setSavedImagesInGallery() {
        saved_count = 0;
        relLayout_count_actions.setVisibility(View.GONE);
        relLayout_download.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(modelInvite.getInvite_photoID());

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
    private void setSharedImage() {
        shared_count = 0;
        relLayout_count_actions.setVisibility(View.GONE);
        relLayout_share.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(modelInvite.getInvite_photoID());

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

