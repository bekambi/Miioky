package com.bekambimouen.miiokychallenge.challenge.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.challenge.GoResponseChallengePhoto;
import com.bekambimouen.miiokychallenge.challenges_view_all.ViewChallengeVideos;
import com.bekambimouen.miiokychallenge.challenge_category.Util_InviteChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.fun.publication.CameraVideoFun;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupViewAdmin;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMyChallengeImages;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewOthersUsersChallengesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ViewOthersUsersChallengesAdapter";

    // constants
    private static final int IMAGE = 1;
    private static final int IMAGE_BIG_IMG = 11;
    private static final int VIDEO = 2;
    private static final int VIDEO_BIG_IMG = 22;
    private static final int GROUP_IMAGE = 3;
    private static final int GROUP_IMAGE_BIG_IMG = 33;
    private static final int GROUP_VIDEO = 4;
    private static final int GROUP_VIDEO_BIG_IMG = 44;

    private final AppCompatActivity context;
    private final List<ModelInvite> list;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public ViewOthersUsersChallengesAdapter(AppCompatActivity context, List<ModelInvite> list,
                                            ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_image_battle_item, parent, false);
            return new ImageHolder(relLayout_view_overlay, view);

        } else if (viewType == IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_image_battle_big_img, parent, false);
            return new ImageHolder(relLayout_view_overlay, view);

        } else if (viewType == VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_video_battle_item, parent, false);
            return new VideoHolder(relLayout_view_overlay, view);

        } else if (viewType == VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_vue_video_battle_big_img, parent, false);
            return new VideoHolder(relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_image_battle_item, parent, false);
            return new GroupImageHolder(relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_BIG_IMG) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_image_battle_big_img, parent, false);
            return new GroupImageHolder(relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_video_battle_item, parent, false);
            return new GroupVideoHolder(relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_BIG_IMG)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_vue_video_battle_big_img, parent, false);
            return new GroupVideoHolder(relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelInvite model = list.get(position);

        try {
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+1));
            PreloadMyChallengeImages.getPreloadMyChallengeImages(context, list.get(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        int itemView = getItemViewType(position);
        if (itemView == IMAGE) {
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.bindView(model);

        } else if (itemView == IMAGE_BIG_IMG) {
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.bindView(model);

        } else if (itemView == VIDEO) {
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.bindView(model);

        } else if (itemView == VIDEO_BIG_IMG) {
            VideoHolder videoHolder = (VideoHolder) holder;
            videoHolder.bindView(model);

        } else if (itemView == GROUP_IMAGE) {
            GroupImageHolder groupImageHolder = (GroupImageHolder) holder;
            groupImageHolder.bindView(model);

        } else if (itemView == GROUP_IMAGE_BIG_IMG) {
            GroupImageHolder groupImageHolder = (GroupImageHolder) holder;
            groupImageHolder.bindView(model);

        } else if (itemView == GROUP_VIDEO) {
            GroupVideoHolder groupVideoHolder = (GroupVideoHolder) holder;
            groupVideoHolder.bindView(model);

        } else if (itemView == GROUP_VIDEO_BIG_IMG) {
            GroupVideoHolder groupVideoHolder = (GroupVideoHolder) holder;
            groupVideoHolder.bindView(model);
        }

        // hide progressbar
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isImage() && !list.get(position).isBig_image())
            return IMAGE;
        else if (list.get(position).isImage() && list.get(position).isBig_image())
            return IMAGE_BIG_IMG;
        else if (list.get(position).isVideo() && !list.get(position).isBig_image())
            return VIDEO;
        else if (list.get(position).isVideo() && list.get(position).isBig_image())
            return VIDEO_BIG_IMG;
        else if (list.get(position).isGroup_image() && !list.get(position).isBig_image())
            return GROUP_IMAGE;
        else if (list.get(position).isGroup_image() && list.get(position).isBig_image())
            return GROUP_IMAGE_BIG_IMG;
        else if (list.get(position).isGroup_video() && !list.get(position).isBig_image())
            return GROUP_VIDEO;
        else if (list.get(position).isGroup_video() && list.get(position).isBig_image())
            return GROUP_VIDEO_BIG_IMG;
        else
            return -1;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ImageHolder";

        // widgets
        private final ViewMoreTextView caption;
        private final TextView translate_comment;
        private final ImageView menu_item;
        private final ImageView flag_icon;
        private final ImageView image_invite;
        private final View view_online;
        private final CircleImageView profile_photo;
        private final TextView username;
        private final TextView country_name;
        private final TextView tps_publication;
        private final TextView category;
        private final TextView button;
        private final RelativeLayout relLayout_category;
        private final RelativeLayout relLayout_go_user_profile;

        private final TextView downloaded_count;
        private final TextView share_count;
        private final RelativeLayout relLayout_count_actions;
        private final RelativeLayout relLayout_download;
        private final RelativeLayout relLayout_share;

        private final LinearLayout linLayout_share;

        // vars
        private BottomSheetSharePublication bottomSheetShare;
        private final RelativeLayout relLayout_view_overlay;
        private final GestureDetector detector_full_image;
        private ModelInvite modelInvite;
        private int saved_count;
        private int shared_count;

        // firebase
        private final FirebaseDatabase database;

        @SuppressLint("ClickableViewAccessibility")
        public ImageHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            database=FirebaseDatabase.getInstance();

            view_online = itemView.findViewById(R.id.view_online);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            menu_item = itemView.findViewById(R.id.menu_item);
            caption = itemView.findViewById(R.id.caption);
            translate_comment = itemView.findViewById(R.id.translate_comment);
            category = itemView.findViewById(R.id.category);
            relLayout_category = itemView.findViewById(R.id.relLayout_category);
            button = itemView.findViewById(R.id.button);
            image_invite = itemView.findViewById(R.id.image_invite);
            relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);

            tps_publication = itemView.findViewById(R.id.tps_publication);
            country_name = itemView.findViewById(R.id.country_name);
            flag_icon = itemView.findViewById(R.id.flag_icon);

            downloaded_count = itemView.findViewById(R.id.downloaded_count);
            share_count = itemView.findViewById(R.id.share_count);
            relLayout_count_actions = itemView.findViewById(R.id.relLayout_count_actions);
            relLayout_download = itemView.findViewById(R.id.relLayout_download);
            relLayout_share = itemView.findViewById(R.id.relLayout_share);
            linLayout_share = itemView.findViewById(R.id.linLayout_share);

            detector_full_image = new GestureDetector(context, new GestureListener());
            image_invite.setOnTouchListener((view, motionEvent) -> detector_full_image.onTouchEvent(motionEvent));
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
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                intent.putExtra("img_url", modelInvite.getInvite_url());
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
        void bindView(ModelInvite model) {
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
            //set the time it was posted
            long tv_date = model.getDate_created();

            // it's today. show the label "today"
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if (TimeUtils.isDateToday(tv_date)) {
                tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                        +sdf.format(tv_date)));
            } else {
                // it's not today. shows the week of day label
                tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                        +sdf.format(tv_date)));
            }

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getInvite_url())
                    .placeholder(R.drawable.black_person)
                    .into(image_invite);

            Glide.with(context.getApplicationContext())
                    .load(model.getInvite_profile_photo())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .centerCrop()
                    .into(profile_photo);

            caption.setCharText("");
            caption.setVisibility(View.GONE);

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

            String userName;
            userName = model.getInvite_username();
            username.setText("");
            username.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(userName))
                username.setVisibility(View.VISIBLE);
            username.setText(userName);

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
                intent.putExtra("modelInvite", myGson);
                intent.putExtra("category_status", invite_category_status);
                context.startActivity(intent);
            });

            // share
            bottomSheetShare = new BottomSheetSharePublication(context, null, model.getInvite_userID(), model.getInvite_url(),
                    model.getInvite_photoID(), "", "", false);
            linLayout_share.setOnClickListener(view -> {
                // prevent two clicks
                Util.preventTwoClick(linLayout_share);
                try {
                    if (bottomSheetShare.isAdded())
                        return;
                    bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

                } catch (IllegalStateException e) {
                    Log.d(TAG, "bindView: "+e.getMessage());
                }
            });

            // country name and flag
            country_name.setText(model.getInvite_country_name());
            Util.getCountryFlag(model.getInvite_countryID(), flag_icon);

            //get the profile image and username
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
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

                        relLayout_go_user_profile.setOnClickListener(view -> {
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

            button.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GoResponseChallengePhoto.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", invite_category_status);
                context.startActivity(intent);
            });
        }

        // count number of video downloaded
        public void setSavedImagesInGallery() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
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
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
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

    public class VideoHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "VideoHolder";

        // widgets
        private final ViewMoreTextView caption;
        private final TextView translate_comment;
        private final ImageView menu_item;
        private final ImageView flag_icon;
        private final ImageView thumbnail;
        private final View view_online;
        private final CircleImageView profile_photo;
        private final TextView username;
        private final TextView country_name;
        private final TextView tps_publication;
        private final TextView category;
        private final TextView button;
        private final RelativeLayout relLayout_category;
        private final RelativeLayout relLayout_go_user_profile;

        private final TextView downloaded_count;
        private final TextView share_count;
        private final RelativeLayout relLayout_count_actions;
        private final RelativeLayout relLayout_download;
        private final RelativeLayout relLayout_share;

        private final LinearLayout linLayout_share;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private BottomSheetSharePublication bottomSheetShare;
        private final GestureDetector detector_full_image;
        private ModelInvite modelInvite;
        private int saved_count;
        private int shared_count;

        // firebase
        private final DatabaseReference myRef;
        private final FirebaseDatabase database;

        @SuppressLint("ClickableViewAccessibility")
        public VideoHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            myRef = FirebaseDatabase.getInstance().getReference();
            database=FirebaseDatabase.getInstance();

            view_online = itemView.findViewById(R.id.view_online);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            menu_item = itemView.findViewById(R.id.menu_item);
            caption = itemView.findViewById(R.id.caption);
            translate_comment = itemView.findViewById(R.id.translate_comment);
            category = itemView.findViewById(R.id.category);
            relLayout_category = itemView.findViewById(R.id.relLayout_category);
            button = itemView.findViewById(R.id.button);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            relLayout_go_user_profile = itemView.findViewById(R.id.relLayout_go_user_profile);

            tps_publication = itemView.findViewById(R.id.tps_publication);
            country_name = itemView.findViewById(R.id.country_name);
            flag_icon = itemView.findViewById(R.id.flag_icon);

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
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewChallengeVideos.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(modelInvite);
                intent.putExtra("modelInvite", myGson);
                intent.putExtra("userID", modelInvite.getInvite_userID());
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
        void bindView(ModelInvite model) {
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
            //set the time it was posted
            long tv_date = model.getDate_created();

            // it's today. show the label "today"
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if (TimeUtils.isDateToday(tv_date)) {
                tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                        +sdf.format(tv_date)));
            } else {
                // it's not today. shows the week of day label
                tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                        +sdf.format(tv_date)));
            }

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getThumbnail_invite())
                    .placeholder(R.drawable.black_person)
                    .into(thumbnail);

            Glide.with(context.getApplicationContext())
                    .load(model.getInvite_profile_photo())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .centerCrop()
                    .into(profile_photo);

            caption.setCharText("");
            caption.setVisibility(View.GONE);

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

            String userName;
            userName = model.getInvite_username();
            username.setText("");
            username.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(userName))
                username.setVisibility(View.VISIBLE);
            username.setText(userName);

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

            // country name and flag
            country_name.setText(model.getInvite_country_name());
            Util.getCountryFlag(model.getInvite_countryID(), flag_icon);

            //get the profile image and username
            Query query = myRef
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

                        relLayout_go_user_profile.setOnClickListener(view -> {
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

            button.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CameraVideoFun.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", invite_category_status);
                intent.putExtra("from_challenge", "from_challenge");
                context.startActivity(intent);

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

    public class GroupImageHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ImageHolder";

        // widgets
        private final ViewMoreTextView caption;
        private final TextView translate_comment;
        private final ImageView menu_item;
        private final ImageView flag_icon;
        private final ImageView image_invite;
        private final View view_online;
        private final CircleImageView profile_photo;
        private final TextView username;
        private final TextView country_name;
        private final TextView tps_publication;
        private final TextView category;
        private final TextView button;
        private final RelativeLayout relLayout_category;

        // group
        private final ImageView group_profile_photo;
        private final TextView group_name;
        private final RelativeLayout relLayout_username;
        private final RelativeLayout relLayout_group_name;

        private final TextView downloaded_count;
        private final TextView share_count;
        private final RelativeLayout relLayout_count_actions;
        private final RelativeLayout relLayout_download;
        private final RelativeLayout relLayout_share;

        private final LinearLayout linLayout_share;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private BottomSheetSharePublication bottomSheetShare;
        private final GestureDetector detector_full_image;
        private ModelInvite modelInvite;
        private int saved_count;
        private int shared_count;

        // firebase
        private final DatabaseReference myRef;
        private final FirebaseDatabase database;
        private final String user_id;

        @SuppressLint("ClickableViewAccessibility")
        public GroupImageHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            myRef = FirebaseDatabase.getInstance().getReference();
            database=FirebaseDatabase.getInstance();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            view_online = itemView.findViewById(R.id.view_online);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            menu_item = itemView.findViewById(R.id.menu_item);
            caption = itemView.findViewById(R.id.caption);
            translate_comment = itemView.findViewById(R.id.translate_comment);
            category = itemView.findViewById(R.id.category);
            relLayout_category = itemView.findViewById(R.id.relLayout_category);
            button = itemView.findViewById(R.id.button);
            image_invite = itemView.findViewById(R.id.image_invite);

            // group
            group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
            group_name = itemView.findViewById(R.id.group_name);
            relLayout_username = itemView.findViewById(R.id.relLayout_username);
            relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);

            tps_publication = itemView.findViewById(R.id.tps_publication);
            country_name = itemView.findViewById(R.id.country_name);
            flag_icon = itemView.findViewById(R.id.flag_icon);

            downloaded_count = itemView.findViewById(R.id.downloaded_count);
            share_count = itemView.findViewById(R.id.share_count);
            relLayout_count_actions = itemView.findViewById(R.id.relLayout_count_actions);
            relLayout_download = itemView.findViewById(R.id.relLayout_download);
            relLayout_share = itemView.findViewById(R.id.relLayout_share);
            linLayout_share = itemView.findViewById(R.id.linLayout_share);

            detector_full_image = new GestureDetector(context, new GestureListener());
            image_invite.setOnTouchListener((view, motionEvent) -> detector_full_image.onTouchEvent(motionEvent));
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
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                intent.putExtra("img_url", modelInvite.getInvite_url());
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
        void bindView(ModelInvite model) {
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
            //set the time it was posted
            long tv_date = model.getDate_created();

            // it's today. show the label "today"
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if (TimeUtils.isDateToday(tv_date)) {
                tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                        +sdf.format(tv_date)));
            } else {
                // it's not today. shows the week of day label
                tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                        +sdf.format(tv_date)));
            }

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getInvite_url())
                    .placeholder(R.drawable.black_person)
                    .into(image_invite);

            caption.setCharText("");
            caption.setVisibility(View.GONE);

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
                intent.putExtra("modelInvite", myGson);
                intent.putExtra("category_status", invite_category_status);
                context.startActivity(intent);
            });

            // share
            bottomSheetShare = new BottomSheetSharePublication(context, null, model.getInvite_userID(), model.getInvite_url(),
                    model.getInvite_photoID(), "", "", false);
            linLayout_share.setOnClickListener(view -> {
                // prevent two clicks
                Util.preventTwoClick(linLayout_share);
                try {
                    if (bottomSheetShare.isAdded())
                        return;
                    bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

                } catch (IllegalStateException e) {
                    Log.d(TAG, "bindView: "+e.getMessage());
                }
            });

            // country name and flag
            country_name.setText(model.getInvite_country_name());
            Util.getCountryFlag(model.getInvite_countryID(), flag_icon);

            // get group name and group profile photo
            Query query2 = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(model.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model.getGroup_id());

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    group_name.setText("");
                    group_name.setVisibility(View.GONE);

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        UserGroups user_groups = new UserGroups();

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        user_groups.setGroup_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_name))).toString());
                        user_groups.setProfile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profile_photo))).toString());

                        String groupName = user_groups.getGroup_name();
                        if (!TextUtils.isEmpty(groupName))
                            group_name.setVisibility(View.VISIBLE);
                        group_name.setText(groupName);

                        Glide.with(context.getApplicationContext())
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(group_profile_photo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //get the profile image and username
            Query query3 = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(model.getInvite_userID());

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
                                .into(profile_photo);

                        String userName = user.getUsername();
                        username.setText(userName);

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
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
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
                public void onCancelled(@NonNull DatabaseError databaseError) {

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

            button.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, GoResponseChallengePhoto.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", invite_category_status);
                intent.putExtra("from_group_challenge", "from_group_challenge");
                context.startActivity(intent);
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

    public class GroupVideoHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "VideoHolder";

        // widgets
        private final ViewMoreTextView caption;
        private final TextView translate_comment;
        private final ImageView menu_item;
        private final ImageView flag_icon;
        private final ImageView thumbnail;
        private final View view_online;
        private final CircleImageView profile_photo;
        private final TextView username;
        private final TextView country_name;
        private final TextView tps_publication;
        private final TextView category;
        private final TextView button;
        private final RelativeLayout relLayout_category;

        private final TextView downloaded_count;
        private final TextView share_count;
        private final RelativeLayout relLayout_count_actions;
        private final RelativeLayout relLayout_download;
        private final RelativeLayout relLayout_share;

        private final LinearLayout linLayout_share;

        // group
        private final ImageView group_profile_photo;
        private final TextView group_name;
        private final RelativeLayout relLayout_username;
        private final RelativeLayout relLayout_group_name;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private BottomSheetSharePublication bottomSheetShare;
        private final GestureDetector detector_full_image;
        private ModelInvite modelInvite;
        private int saved_count;
        private int shared_count;

        // firebase
        private final DatabaseReference myRef;
        private final FirebaseDatabase database;
        private final String user_id;

        @SuppressLint("ClickableViewAccessibility")
        public GroupVideoHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            myRef = FirebaseDatabase.getInstance().getReference();
            database=FirebaseDatabase.getInstance();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            view_online = itemView.findViewById(R.id.view_online);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            menu_item = itemView.findViewById(R.id.menu_item);
            caption = itemView.findViewById(R.id.caption);
            translate_comment = itemView.findViewById(R.id.translate_comment);
            category = itemView.findViewById(R.id.category);
            relLayout_category = itemView.findViewById(R.id.relLayout_category);
            button = itemView.findViewById(R.id.button);
            thumbnail = itemView.findViewById(R.id.thumbnail);

            tps_publication = itemView.findViewById(R.id.tps_publication);
            country_name = itemView.findViewById(R.id.country_name);
            flag_icon = itemView.findViewById(R.id.flag_icon);

            // group
            group_profile_photo = itemView.findViewById(R.id.group_profile_photo);
            group_name = itemView.findViewById(R.id.group_name);
            relLayout_username = itemView.findViewById(R.id.relLayout_username);
            relLayout_group_name = itemView.findViewById(R.id.relLayout_group_name);

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
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewChallengeVideos.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(modelInvite);
                intent.putExtra("modelInvite", myGson);
                intent.putExtra("userID", modelInvite.getGroup_id());
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
        void bindView(ModelInvite model) {
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
            //set the time it was posted
            long tv_date = model.getDate_created();

            // it's today. show the label "today"
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if (TimeUtils.isDateToday(tv_date)) {
                tps_publication.setText(Html.fromHtml(context.getString(R.string.today)+", "
                        +sdf.format(tv_date)));
            } else {
                // it's not today. shows the week of day label
                tps_publication.setText(Html.fromHtml(context.getString(R.string.yesterday)+", "
                        +sdf.format(tv_date)));
            }

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(model.getThumbnail_invite())
                    .placeholder(R.drawable.black_person)
                    .into(thumbnail);

            caption.setCharText("");
            caption.setVisibility(View.GONE);

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

            // country name and flag
            country_name.setText(model.getInvite_country_name());
            Util.getCountryFlag(model.getInvite_countryID(), flag_icon);

            // get group name and group profile photo
            Query query2 = myRef
                    .child(context.getString(R.string.dbname_user_group))
                    .child(model.getAdmin_master())
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(model.getGroup_id());

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    group_name.setText("");
                    group_name.setVisibility(View.GONE);

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        UserGroups user_groups = new UserGroups();

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                        assert objectMap != null;
                        user_groups.setGroup_name(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_group_name))).toString());
                        user_groups.setProfile_photo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_profile_photo))).toString());

                        String groupName = user_groups.getGroup_name();
                        if (!TextUtils.isEmpty(groupName))
                            group_name.setVisibility(View.VISIBLE);
                        group_name.setText(groupName);

                        Glide.with(context.getApplicationContext())
                                .load(user_groups.getProfile_photo())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(group_profile_photo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //get the profile image and username
            Query query3 = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(model.getInvite_userID());

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
                                .into(profile_photo);

                        String userName = user.getUsername();
                        username.setText(userName);

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
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
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
                public void onCancelled(@NonNull DatabaseError databaseError) {

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

            button.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CameraVideoFun.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(model);
                intent.putExtra("model_invite", myGson);
                intent.putExtra("category_status", invite_category_status);
                intent.putExtra("from_challenge", "from_challenge"); // necessary
                intent.putExtra("from_group_challenge", "from_group_challenge"); // necessary
                context.startActivity(intent);

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
}

