package com.bekambimouen.miiokychallenge.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.report_post.model.ReportReasonModel;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class BottomSheetSignaler extends BottomSheetDialogFragment {
    // vars
    private final AppCompatActivity context;
    private final BattleModel battleModel;
    private final String photo_id;

    // firebase
    private final DatabaseReference myRef;

    public BottomSheetSignaler(AppCompatActivity context, BattleModel battleModel, String photo_id) {
        this.context = context;
        this.battleModel = battleModel;
        this.photo_id = photo_id;

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onStart()
    {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Prevent BottomSheetDialogFragment covering navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet_signaler, null);

        TextView tv_sexual = view.findViewById(R.id.tv_sexual);
        TextView tv_violent = view.findViewById(R.id.tv_violent);
        TextView tv_fake = view.findViewById(R.id.tv_fake);
        TextView tv_hat_speech = view.findViewById(R.id.tv_hat_speech);
        TextView tv_others = view.findViewById(R.id.tv_others);

        tv_sexual.setOnClickListener(view1 -> {
            dismiss();
            if (battleModel != null)
                post(battleModel, photo_id, context.getString(R.string.sexual));
            Toast.makeText(context, context.getString(R.string.thanks_for_your_comment), Toast.LENGTH_LONG).show();
        });

        tv_violent.setOnClickListener(view1 -> {
            dismiss();
            if (battleModel != null)
                post(battleModel, photo_id, context.getString(R.string.violent));
            Toast.makeText(context, context.getString(R.string.thanks_for_your_comment), Toast.LENGTH_LONG).show();
        });

        tv_fake.setOnClickListener(view1 -> {
            dismiss();
            if (battleModel != null)
                post(battleModel, photo_id, context.getString(R.string.fake));
            Toast.makeText(context, context.getString(R.string.thanks_for_your_comment), Toast.LENGTH_LONG).show();
        });

        tv_hat_speech.setOnClickListener(view1 -> {
            dismiss();
            if (battleModel != null)
                post(battleModel, photo_id, context.getString(R.string.hat_speech));
            Toast.makeText(context, context.getString(R.string.thanks_for_your_comment), Toast.LENGTH_LONG).show();
        });

        tv_others.setOnClickListener(view1 -> {
            dismiss();
            if (battleModel != null)
                post(battleModel, photo_id, context.getString(R.string.others));
            Toast.makeText(context, context.getString(R.string.thanks_for_your_comment), Toast.LENGTH_LONG).show();
        });

        dialog.setContentView(view);
    }

    // concerning group. Approve member post
    private void post(BattleModel battleModel, String photo_id, String reason_of_report) {
        // try to prevent if we are not in the group
        Date date=new Date();

        BattleModel model = new BattleModel();

        model.setSuppressed(false);
        model.setRecyclerItem(battleModel.isRecyclerItem());
        model.setImageUne(battleModel.isImageUne());
        model.setVideoUne(battleModel.isVideoUne());
        model.setImageDouble(battleModel.isImageDouble());
        model.setVideoDouble(battleModel.isVideoDouble());
        model.setReponseImageDouble(battleModel.isReponseImageDouble());
        model.setReponseVideoDouble(battleModel.isReponseVideoDouble());
        // pour le grid profile
        model.setGridRecyclerImage(battleModel.isGridRecyclerImage());

        model.setReponse_deja(battleModel.isReponse_deja());

        // group
        model.setGroup(battleModel.isGroup());
        model.setGroup_private(battleModel.isGroup_private());
        model.setGroup_public(battleModel.isGroup_public());
        model.setGroup_cover_profile_photo(battleModel.isGroup_cover_profile_photo());
        model.setGroup_recyclerItem(battleModel.isGroup_recyclerItem());
        model.setGroup_imageUne(battleModel.isGroup_imageUne());
        model.setGroup_videoUne(battleModel.isGroup_videoUne());
        model.setGroup_cover_background_profile_photo(battleModel.isGroup_cover_background_profile_photo());
        model.setGroup_imageDouble(battleModel.isGroup_imageDouble());
        model.setGroup_videoDouble(battleModel.isGroup_videoDouble());
        model.setGroup_response_imageDouble(battleModel.isGroup_response_imageDouble());
        model.setGroup_response_videoDouble(battleModel.isGroup_response_videoDouble());

        model.setGroup_share_single_bottom_image_double(battleModel.isGroup_share_single_bottom_image_double());
        model.setGroup_share_single_bottom_image_une(battleModel.isGroup_share_single_bottom_image_une());
        model.setGroup_share_single_bottom_recycler(battleModel.isGroup_share_single_bottom_recycler());
        model.setGroup_share_single_bottom_response_image_double(battleModel.isGroup_share_single_bottom_response_image_double());
        model.setGroup_share_single_bottom_response_video_double(battleModel.isGroup_share_single_bottom_response_video_double());
        model.setGroup_share_single_bottom_video_double(battleModel.isGroup_share_single_bottom_video_double());
        model.setGroup_share_single_bottom_video_une(battleModel.isGroup_share_single_bottom_video_une());

        model.setGroup_share_single_top_image_double(battleModel.isGroup_share_single_top_image_double());
        model.setGroup_share_single_top_image_une(battleModel.isGroup_share_single_top_image_une());
        model.setGroup_share_single_top_recycler(battleModel.isGroup_share_single_top_recycler());
        model.setGroup_share_single_top_response_image_double(battleModel.isGroup_share_single_top_response_image_double());
        model.setGroup_share_single_top_response_video_double(battleModel.isGroup_share_single_top_response_video_double());
        model.setGroup_share_single_top_video_double(battleModel.isGroup_share_single_top_video_double());
        model.setGroup_share_single_top_video_une(battleModel.isGroup_share_single_top_video_une());

        model.setGroup_share_top_bottom_image_double(battleModel.isGroup_share_top_bottom_image_double());
        model.setGroup_share_top_bottom_image_une(battleModel.isGroup_share_top_bottom_image_une());
        model.setGroup_share_top_bottom_recycler(battleModel.isGroup_share_top_bottom_recycler());
        model.setGroup_share_top_bottom_response_image_double(battleModel.isGroup_share_top_bottom_response_image_double());
        model.setGroup_share_top_bottom_response_video_double(battleModel.isGroup_share_top_bottom_response_video_double());
        model.setGroup_share_top_bottom_video_double(battleModel.isGroup_share_top_bottom_video_double());
        model.setGroup_share_top_bottom_video_une(battleModel.isGroup_share_top_bottom_video_une());

        model.setUser_profile_shared(battleModel.isUser_profile_shared());
        model.setGroup_share_single_bottom_circle_image(battleModel.isGroup_share_single_bottom_circle_image());
        model.setGroup_share_single_top_circle_image(battleModel.isGroup_share_single_top_circle_image());
        model.setGroup_share_top_bottom_circle_image(battleModel.isGroup_share_top_bottom_circle_image());

        model.setUser_profile_photo(battleModel.getUser_profile_photo());
        model.setUser_full_profile_photo(battleModel.getUser_full_profile_photo());
        model.setDate_shared(battleModel.getDate_shared());

        model.setAdmin_master(battleModel.getAdmin_master());
        model.setGroup_id(battleModel.getGroup_id());
        model.setSharing_admin_master(battleModel.getSharing_admin_master());
        model.setShared_group_id(battleModel.getShared_group_id());
        model.setPublisher(battleModel.getPublisher());
        model.setGroup_profile_photo(battleModel.getGroup_profile_photo());
        model.setGroup_full_profile_photo(battleModel.getGroup_full_profile_photo());
        model.setGroup_user_background_profile_url(battleModel.getGroup_user_background_profile_url());
        model.setGroup_user_background_full_profile_url(battleModel.getGroup_user_background_full_profile_url());

        model.setComment_text(battleModel.isComment_text());
        model.setComment_subject(battleModel.getComment_subject());
        model.setComment_theme(battleModel.getComment_theme());
        model.setBig_image(battleModel.isBig_image());

        model.setCaption(battleModel.getCaption());
        model.setUrl(battleModel.getUrl());
        model.setDate_created(date.getTime());
        model.setPhoto_id(battleModel.getPhoto_id());
        model.setUser_id(battleModel.getUser_id());
        model.setTags(battleModel.getTags());

        model.setDetails(battleModel.getDetails());
        model.setCaptionUn(battleModel.getCaptionUn());
        model.setTagsUn(battleModel.getTagsUn());
        model.setTagsDeux(battleModel.getTagsDeux());
        model.setUrlUn(battleModel.getUrlUn());
        model.setUrlDeux(battleModel.getUrlDeux());
        model.setPhoto_id_un(battleModel.getPhoto_id_un());
        model.setPhoto_id_deux(battleModel.getPhoto_id_deux());

        model.setUrli(battleModel.getUrli());
        model.setUrlii(battleModel.getUrlii());
        model.setUrliii(battleModel.getUrliii());
        model.setUrliv(battleModel.getUrliv());
        model.setUrlv(battleModel.getUrlv());
        model.setUrlvi(battleModel.getUrlvi());
        model.setUrlvii(battleModel.getUrlvii());
        model.setUrlviii(battleModel.getUrlviii());
        model.setUrlix(battleModel.getUrlix());
        model.setUrlx(battleModel.getUrlx());
        model.setUrlxi(battleModel.getUrlxi());
        model.setUrlxii(battleModel.getUrlxii());
        model.setUrlxiii(battleModel.getUrlxiii());
        model.setUrlxiv(battleModel.getUrlxiv());
        model.setUrlxv(battleModel.getUrlxv());
        model.setUrlxvi(battleModel.getUrlxvi());
        model.setUrlxvii(battleModel.getUrlxvii());

        model.setPhotoi_id(battleModel.getPhotoi_id());
        model.setPhotoii_id(battleModel.getPhotoii_id());
        model.setPhotoiii_id(battleModel.getPhotoiii_id());
        model.setPhotoiv_id(battleModel.getPhotoiv_id());
        model.setPhotov_id(battleModel.getPhotov_id());
        model.setPhotovi_id(battleModel.getPhotovi_id());
        model.setPhotovii_id(battleModel.getPhotovii_id());
        model.setPhotoviii_id(battleModel.getPhotoviii_id());
        model.setPhotoix_id(battleModel.getPhotoix_id());
        model.setPhotox_id(battleModel.getPhotox_id());
        model.setPhotoxi_id(battleModel.getPhotoxi_id());
        model.setPhotoxii_id(battleModel.getPhotoxii_id());
        model.setPhotoxiii_id(battleModel.getPhotoxiii_id());
        model.setPhotoxiv_id(battleModel.getPhotoxiv_id());
        model.setPhotoxv_id(battleModel.getPhotoxv_id());
        model.setPhotoxvi_id(battleModel.getPhotoxvi_id());
        model.setPhotoxvii_id(battleModel.getPhotoxvii_id());

        model.setCaption_i(battleModel.getCaption_i());
        model.setCaption_ii(battleModel.getCaption_ii());
        model.setCaption_iii(battleModel.getCaption_iii());
        model.setCaption_iv(battleModel.getCaption_iv());
        model.setCaption_v(battleModel.getCaption_v());
        model.setCaption_vi(battleModel.getCaption_vi());
        model.setCaption_vii(battleModel.getCaption_vii());
        model.setCaption_viii(battleModel.getCaption_viii());
        model.setCaption_ix(battleModel.getCaption_ix());
        model.setCaption_x(battleModel.getCaption_x());
        model.setCaption_xi(battleModel.getCaption_xi());
        model.setCaption_xii(battleModel.getCaption_xii());
        model.setCaption_xiii(battleModel.getCaption_xiii());
        model.setCaption_xiv(battleModel.getCaption_xiv());
        model.setCaption_xv(battleModel.getCaption_xv());
        model.setCaption_xvi(battleModel.getCaption_xvi());
        model.setCaption_xvii(battleModel.getCaption_xvii());

        model.setThumbnail_un(battleModel.getThumbnail_un());
        model.setThumbnail_deux(battleModel.getThumbnail_deux());
        model.setThumbnail(battleModel.getThumbnail());
        model.setThumbnail_invite(battleModel.getThumbnail_invite());
        model.setThumbnail_response(battleModel.getThumbnail_response());

        // challenge
        model.setInvite_url(battleModel.getInvite_url());
        model.setInvite_photoID(battleModel.getInvite_photoID());
        model.setInvite_username(battleModel.getInvite_username());
        model.setInvite_userID(battleModel.getInvite_userID());
        model.setInvite_caption(battleModel.getInvite_caption());
        model.setInvite_tag(battleModel.getInvite_tag());
        model.setInvite_category(battleModel.getInvite_category());
        model.setInvite_profile_photo(battleModel.getInvite_profile_photo());

        model.setReponse_profile_photo(battleModel.getReponse_profile_photo());
        model.setReponse_username(battleModel.getReponse_username());
        model.setReponse_user_id(battleModel.getReponse_user_id());
        model.setReponse_url(battleModel.getReponse_url());
        model.setReponse_photoID(battleModel.getReponse_photoID());
        model.setReponse_caption(battleModel.getReponse_caption());
        model.setReponse_tag(battleModel.getReponse_tag());

        // challenge and flag
        model.setInvite_country_name(battleModel.getInvite_country_name());
        model.setInvite_countryID(battleModel.getInvite_countryID());
        model.setReponse_country_name(battleModel.getReponse_country_name());
        model.setReponse_countryID(battleModel.getReponse_countryID());
        model.setCountry_name(battleModel.getCountry_name());
        model.setCountryID(battleModel.getCountryID());
        model.setInvite_category_status(battleModel.getInvite_category_status());
        model.setSharing_caption(battleModel.getSharing_caption());

        // republication of publication
        model.setImageDouble_shared(battleModel.isImageDouble_shared());
        model.setImageUne_shared(battleModel.isImageUne_shared());
        model.setRecyclerItem_shared(battleModel.isRecyclerItem_shared());
        model.setReponseImageDouble_shared(battleModel.isReponseImageDouble_shared());
        model.setReponseVideoDouble_shared(battleModel.isReponseVideoDouble_shared());
        model.setVideoDouble_shared(battleModel.isVideoDouble_shared());
        model.setVideoUne_shared(battleModel.isVideoUne_shared());
        model.setUser_id_sharing(battleModel.getUser_id_sharing());
        // for saved
        model.setUsername("");
        model.setProfileUrl("");
        model.setAuth_user_id("");
        model.setUser_id_shared("");

        // post identity
        String post_key = myRef.push().getKey();
        model.setPost_identity(post_key);

        // first verify if there are not already report this post
        Query myQuery = null;
        if (!TextUtils.isEmpty(battleModel.getPhoto_id())) {
            myQuery = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_report_post))
                    .child(battleModel.getGroup_id())
                    .orderByChild(context.getString(R.string.field_photo_id))
                    .equalTo(battleModel.getPhoto_id());
        }
        if (!TextUtils.isEmpty(battleModel.getPhotoi_id())) {
            myQuery = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_report_post))
                    .child(battleModel.getGroup_id())
                    .orderByChild(context.getString(R.string.field_photoi_id))
                    .equalTo(battleModel.getPhotoi_id());
        }
        if (!TextUtils.isEmpty(battleModel.getPhoto_id_un())) {
            myQuery = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_report_post))
                    .child(battleModel.getGroup_id())
                    .orderByChild(context.getString(R.string.field_photo_id_un))
                    .equalTo(battleModel.getPhoto_id_un());
        }
        if (!TextUtils.isEmpty(battleModel.getReponse_photoID())) {
            myQuery = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.dbname_group_report_post))
                    .child(battleModel.getGroup_id())
                    .orderByChild(context.getString(R.string.field_reponse_photoID))
                    .equalTo(battleModel.getReponse_photoID());
        }

        assert myQuery != null;
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // download to database
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_report_post))
                            .child(battleModel.getGroup_id())
                            .child(photo_id)
                            .setValue(model);

                    // download number and reason
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("reason_of_report", reason_of_report);
                    map.put("number_of_report", "1");
                    map.put("photo_id", photo_id);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                            .child(photo_id)
                            .setValue(map);

                } // if the report already exists
                else {
                    Query query = null;
                    if (!TextUtils.isEmpty(battleModel.getPhoto_id())) {
                        query = myRef
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .orderByChild(context.getString(R.string.field_photo_id))
                                .equalTo(photo_id);
                    }
                    if (!TextUtils.isEmpty(battleModel.getPhotoi_id())) {
                        query = myRef
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .orderByChild(context.getString(R.string.field_photoi_id))
                                .equalTo(photo_id);
                    }
                    if (!TextUtils.isEmpty(battleModel.getPhoto_id_un())) {
                        query = myRef
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .orderByChild(context.getString(R.string.field_photo_id_un))
                                .equalTo(photo_id);
                    }
                    if (!TextUtils.isEmpty(battleModel.getReponse_photoID())) {
                        query = myRef
                                .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                .orderByChild(context.getString(R.string.field_reponse_photoID))
                                .equalTo(photo_id);
                    }

                    assert query != null;
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String number_of_report = Objects.requireNonNull(dataSnapshot.getValue(ReportReasonModel.class)).getNumber_of_report();

                                int convert = Integer.parseInt(number_of_report) + 1;

                                // update value
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("number_of_report", String.valueOf(convert));

                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_group_report_post_number_and_reason))
                                        .child(photo_id)
                                        .updateChildren(map);
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

    /**
     * Prevent BottomSheetDialogFragment covering navigation bar
     */
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}

