package com.bekambimouen.miiokychallenge.full_image_share;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.Utils.adapter.FullImageAdapter;
import com.bekambimouen.miiokychallenge.child_recyclerview_share.comment.ChildRecyclerCommentShare;
import com.bekambimouen.miiokychallenge.full_image_share.bottomSheet_add_recycler_description.BottomSheetAddRecyclerImageDescriptionShare;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RecyclerFullImageShare extends AppCompatActivity {
    private static final String TAG = "RecyclerFullImageShare";

    // widgets
    private ImageView menu_item;
    private ImageView imageview;
    private ImageView image;
    private SmallBangView like_heart;
    private TextView username;
    private TextView the_user;
    private TextView number_of_likes;
    private TextView number_of_comments;
    private TextView number_of_share;
    private TextView tps_publication;
    private TextView add_description;
    private ViewMoreTextView caption;
    private TextView translate_comment;
    private RelativeLayout parent, relLayout_username, relLayout_view_overlay;
    private LinearLayout linLayout_add_description;
    private RelativeLayout relLayout_write_to;
    private LinearLayout linLayout_like;
    private LinearLayout linLayout_comment;
    private LinearLayout linLayout_share;

    // vars
    private RecyclerFullImageShare context;
    private BattleModel model;
    private boolean isImageEnabled;
    private int position, recycler_list_size;
    private List<String> item_description_List, photo_id_list, fieldLike_list, field_caption;
    private BottomSheetAddRecyclerImageDescriptionShare bottomSheetAddRecyclerImageDescription;
    private BottomSheetSharePublication bottomSheetShare;
    private User mCurrentUser;
    private List<String> url_list;

    private String description, from_comment;
    private StringBuilder mUsers, updateLikeUsers;
    private int likes_count, comments_count, shares_count;
    private boolean mLikedByCurrentUser;

    // firebase
    private DatabaseReference myRef;
    private String user_id;
    private FirebaseDatabase database;

    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_recycler_full_image_share);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        url_list = new ArrayList<>();
        item_description_List = new ArrayList<>();
        fieldLike_list = new ArrayList<>();
        field_caption = new ArrayList<>();
        photo_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            model = gson.fromJson(getIntent().getStringExtra("battleModel_recyclerview"), BattleModel.class);

            position = getIntent().getIntExtra("position", 0);
            recycler_list_size = getIntent().getIntExtra("recycler_list_size", 0);
            isImageEnabled = getIntent().getBooleanExtra("isImageEnabled", true);
            from_comment = getIntent().getStringExtra("from_comment");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        LinearLayout linLayout_all_data = findViewById(R.id.linLayout_all_data);
        menu_item = findViewById(R.id.menu_item);
        imageview = findViewById(R.id.imageview);
        ImageView back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        the_user = findViewById(R.id.the_user);
        tps_publication = findViewById(R.id.tps_publication);
        add_description = findViewById(R.id.add_description);
        caption = findViewById(R.id.caption);
        translate_comment = findViewById(R.id.translate_comment);
        relLayout_username = findViewById(R.id.relLayout_username);

        linLayout_add_description = findViewById(R.id.linLayout_add_description);
        image = findViewById(R.id.image);
        like_heart = findViewById(R.id.like_heart);
        number_of_likes = findViewById(R.id.number_of_likes);
        number_of_comments = findViewById(R.id.number_of_comments);
        number_of_share = findViewById(R.id.number_of_share);
        relLayout_write_to = findViewById(R.id.relLayout_write_to);
        linLayout_like = findViewById(R.id.linLayout_like);
        linLayout_comment = findViewById(R.id.linLayout_comment);
        linLayout_share = findViewById(R.id.linLayout_share);

        // hide lineatLayout like when one user liked
        if (!isImageEnabled)
            linLayout_like.setVisibility(View.GONE);

        back.setOnClickListener(v -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(position);

        FullImageAdapter adapter = new FullImageAdapter(context, url_list, linLayout_all_data, menu_item);
        recyclerView.setAdapter(adapter);

        if (model != null) {
            if (recycler_list_size == 1) {
                url_list.add(model.getUrli());

                photo_id_list.add(model.getPhotoi_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));

                field_caption.add(context.getString(R.string.field_caption_i));

                item_description_List.add(model.getCaption_i());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 2) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 3) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 4) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 5) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 6) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 7) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 8) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 9) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 10) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 11) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 12) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());
                url_list.add(model.getUrlxii());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());
                photo_id_list.add(model.getPhotoxii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));
                fieldLike_list.add(context.getString(R.string.field_likes_xii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));
                field_caption.add(context.getString(R.string.field_caption_xii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                item_description_List.add(model.getCaption_xii());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 13) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());
                url_list.add(model.getUrlxii());
                url_list.add(model.getUrlxiii());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());
                photo_id_list.add(model.getPhotoxii_id());
                photo_id_list.add(model.getPhotoxiii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));
                fieldLike_list.add(context.getString(R.string.field_likes_xii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));
                field_caption.add(context.getString(R.string.field_caption_xii));
                field_caption.add(context.getString(R.string.field_caption_xiii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                item_description_List.add(model.getCaption_xii());
                item_description_List.add(model.getCaption_xiii());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 14) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());
                url_list.add(model.getUrlxii());
                url_list.add(model.getUrlxiii());
                url_list.add(model.getUrlxiv());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());
                photo_id_list.add(model.getPhotoxii_id());
                photo_id_list.add(model.getPhotoxiii_id());
                photo_id_list.add(model.getPhotoxiv_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));
                fieldLike_list.add(context.getString(R.string.field_likes_xii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiv));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));
                field_caption.add(context.getString(R.string.field_caption_xii));
                field_caption.add(context.getString(R.string.field_caption_xiii));
                field_caption.add(context.getString(R.string.field_caption_xiv));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                item_description_List.add(model.getCaption_xii());
                item_description_List.add(model.getCaption_xiii());
                item_description_List.add(model.getCaption_xiv());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 15) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());
                url_list.add(model.getUrlxii());
                url_list.add(model.getUrlxiii());
                url_list.add(model.getUrlxiv());
                url_list.add(model.getUrlxv());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());
                photo_id_list.add(model.getPhotoxii_id());
                photo_id_list.add(model.getPhotoxiii_id());
                photo_id_list.add(model.getPhotoxiv_id());
                photo_id_list.add(model.getPhotoxv_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));
                fieldLike_list.add(context.getString(R.string.field_likes_xii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiv));
                fieldLike_list.add(context.getString(R.string.field_likes_xv));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));
                field_caption.add(context.getString(R.string.field_caption_xii));
                field_caption.add(context.getString(R.string.field_caption_xiii));
                field_caption.add(context.getString(R.string.field_caption_xiv));
                field_caption.add(context.getString(R.string.field_caption_xv));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                item_description_List.add(model.getCaption_xii());
                item_description_List.add(model.getCaption_xiii());
                item_description_List.add(model.getCaption_xiv());
                item_description_List.add(model.getCaption_xv());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 16) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());
                url_list.add(model.getUrlxii());
                url_list.add(model.getUrlxiii());
                url_list.add(model.getUrlxiv());
                url_list.add(model.getUrlxv());
                url_list.add(model.getUrlxvi());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());
                photo_id_list.add(model.getPhotoxii_id());
                photo_id_list.add(model.getPhotoxiii_id());
                photo_id_list.add(model.getPhotoxiv_id());
                photo_id_list.add(model.getPhotoxv_id());
                photo_id_list.add(model.getPhotoxvi_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));
                fieldLike_list.add(context.getString(R.string.field_likes_xii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiv));
                fieldLike_list.add(context.getString(R.string.field_likes_xv));
                fieldLike_list.add(context.getString(R.string.field_likes_xvi));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));
                field_caption.add(context.getString(R.string.field_caption_xii));
                field_caption.add(context.getString(R.string.field_caption_xiii));
                field_caption.add(context.getString(R.string.field_caption_xiv));
                field_caption.add(context.getString(R.string.field_caption_xv));
                field_caption.add(context.getString(R.string.field_caption_xvi));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                item_description_List.add(model.getCaption_xii());
                item_description_List.add(model.getCaption_xiii());
                item_description_List.add(model.getCaption_xiv());
                item_description_List.add(model.getCaption_xv());
                item_description_List.add(model.getCaption_xvi());
                adapter.notifyDataSetChanged();

            } else if (recycler_list_size == 17) {
                url_list.add(model.getUrli());
                url_list.add(model.getUrlii());
                url_list.add(model.getUrliii());
                url_list.add(model.getUrliv());
                url_list.add(model.getUrlv());
                url_list.add(model.getUrlvi());
                url_list.add(model.getUrlvii());
                url_list.add(model.getUrlviii());
                url_list.add(model.getUrlix());
                url_list.add(model.getUrlx());
                url_list.add(model.getUrlxi());
                url_list.add(model.getUrlxii());
                url_list.add(model.getUrlxiii());
                url_list.add(model.getUrlxiv());
                url_list.add(model.getUrlxv());
                url_list.add(model.getUrlxvi());
                url_list.add(model.getUrlxvii());

                photo_id_list.add(model.getPhotoi_id());
                photo_id_list.add(model.getPhotoii_id());
                photo_id_list.add(model.getPhotoiii_id());
                photo_id_list.add(model.getPhotoiv_id());
                photo_id_list.add(model.getPhotov_id());
                photo_id_list.add(model.getPhotovi_id());
                photo_id_list.add(model.getPhotovii_id());
                photo_id_list.add(model.getPhotoviii_id());
                photo_id_list.add(model.getPhotoix_id());
                photo_id_list.add(model.getPhotox_id());
                photo_id_list.add(model.getPhotoxi_id());
                photo_id_list.add(model.getPhotoxii_id());
                photo_id_list.add(model.getPhotoxiii_id());
                photo_id_list.add(model.getPhotoxiv_id());
                photo_id_list.add(model.getPhotoxv_id());
                photo_id_list.add(model.getPhotoxvi_id());
                photo_id_list.add(model.getPhotoxvii_id());

                fieldLike_list.add(context.getString(R.string.field_likes_i));
                fieldLike_list.add(context.getString(R.string.field_likes_ii));
                fieldLike_list.add(context.getString(R.string.field_likes_iii));
                fieldLike_list.add(context.getString(R.string.field_likes_iv));
                fieldLike_list.add(context.getString(R.string.field_likes_v));
                fieldLike_list.add(context.getString(R.string.field_likes_vi));
                fieldLike_list.add(context.getString(R.string.field_likes_vii));
                fieldLike_list.add(context.getString(R.string.field_likes_viii));
                fieldLike_list.add(context.getString(R.string.field_likes_ix));
                fieldLike_list.add(context.getString(R.string.field_likes_x));
                fieldLike_list.add(context.getString(R.string.field_likes_xi));
                fieldLike_list.add(context.getString(R.string.field_likes_xii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiii));
                fieldLike_list.add(context.getString(R.string.field_likes_xiv));
                fieldLike_list.add(context.getString(R.string.field_likes_xv));
                fieldLike_list.add(context.getString(R.string.field_likes_xvi));
                fieldLike_list.add(context.getString(R.string.field_likes_xvii));

                field_caption.add(context.getString(R.string.field_caption_i));
                field_caption.add(context.getString(R.string.field_caption_ii));
                field_caption.add(context.getString(R.string.field_caption_iii));
                field_caption.add(context.getString(R.string.field_caption_iv));
                field_caption.add(context.getString(R.string.field_caption_v));
                field_caption.add(context.getString(R.string.field_caption_vi));
                field_caption.add(context.getString(R.string.field_caption_vii));
                field_caption.add(context.getString(R.string.field_caption_viii));
                field_caption.add(context.getString(R.string.field_caption_ix));
                field_caption.add(context.getString(R.string.field_caption_x));
                field_caption.add(context.getString(R.string.field_caption_xi));
                field_caption.add(context.getString(R.string.field_caption_xii));
                field_caption.add(context.getString(R.string.field_caption_xiii));
                field_caption.add(context.getString(R.string.field_caption_xiv));
                field_caption.add(context.getString(R.string.field_caption_xv));
                field_caption.add(context.getString(R.string.field_caption_xvi));
                field_caption.add(context.getString(R.string.field_caption_xvii));

                item_description_List.add(model.getCaption_i());
                item_description_List.add(model.getCaption_ii());
                item_description_List.add(model.getCaption_iii());
                item_description_List.add(model.getCaption_iv());
                item_description_List.add(model.getCaption_v());
                item_description_List.add(model.getCaption_vi());
                item_description_List.add(model.getCaption_vii());
                item_description_List.add(model.getCaption_viii());
                item_description_List.add(model.getCaption_ix());
                item_description_List.add(model.getCaption_x());
                item_description_List.add(model.getCaption_xi());
                item_description_List.add(model.getCaption_xii());
                item_description_List.add(model.getCaption_xiii());
                item_description_List.add(model.getCaption_xiv());
                item_description_List.add(model.getCaption_xv());
                item_description_List.add(model.getCaption_xvi());
                item_description_List.add(model.getCaption_xvii());
                adapter.notifyDataSetChanged();

            }
            setComments(model, photo_id_list.get(position));
            setShare(photo_id_list.get(position));
            setLikes(model, photo_id_list.get(position), fieldLike_list.get(position));
            getLikesString(model, photo_id_list.get(position), fieldLike_list.get(position));
            updateLike(model, photo_id_list.get(position), fieldLike_list.get(position));

            if (!item_description_List.get(position).isEmpty())
                add_description.setText(context.getString(R.string.update_description));

            getCurrentUser();
            fullImage(position);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE){
                        int position = ((LinearLayoutManager) requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                        setComments(model, photo_id_list.get(position));
                        setShare(photo_id_list.get(position));
                        setLikes(model, photo_id_list.get(position), fieldLike_list.get(position));

                        if (!item_description_List.get(position).isEmpty())
                            add_description.setText(context.getString(R.string.update_description));
                        else
                            add_description.setText(context.getString(R.string.add_description_to_recycler_image));

                        fullImage(position);
                    }
                }
            });
        }
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void fullImage(int position) {
        caption.setCharText("");

        if (model.getUser_id_sharing().equals(user_id)) {
            relLayout_write_to.setVisibility(View.GONE);
            linLayout_add_description.setVisibility(View.VISIBLE);

            bottomSheetAddRecyclerImageDescription =
                    new BottomSheetAddRecyclerImageDescriptionShare(context, model,
                            url_list.get(position), field_caption.get(position), linLayout_add_description, caption);

            linLayout_add_description.setOnClickListener(view -> {
                if (bottomSheetAddRecyclerImageDescription.isAdded())
                    return;
                bottomSheetAddRecyclerImageDescription.show(context.getSupportFragmentManager(), TAG);
            });
        }

        Glide.with(context.getApplicationContext())
                .load(url_list.get(position))
                .placeholder(R.color.black)
                .into(imageview);

        // get likes
        getLikesString(model, photo_id_list.get(position), fieldLike_list.get(position));
        updateLike(model, photo_id_list.get(position), fieldLike_list.get(position));

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(model.getUser_id_sharing());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String name = user.getUsername();

                    username.setText(name);
                    the_user.setText(name);

                    relLayout_username.setOnClickListener(view -> {
                        Log.d(TAG, "onClick: navigating to profile of: " +
                                user.getUsername());

                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
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

                    relLayout_write_to.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        getWindow().setExitTransition(new Slide(Gravity.END));
                        getWindow().setEnterTransition(new Slide(Gravity.START));
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // caption
        description = model.getCaption();

        if (!item_description_List.get(position).isEmpty()) {
            caption.setVisibility(View.VISIBLE);
            caption.setCharText(item_description_List.get(position));
        } else {
            if (!TextUtils.isEmpty(description)) {
                caption.setVisibility(View.VISIBLE);
                caption.setCharText(description);
            }
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
                            // Model couldn’t be loaded or other internal error.
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

        long tv_date;
        tv_date = model.getDate_created();
        tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

        // like
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

                            removeLike(model, photo_id_list.get(position), fieldLike_list.get(position));
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
                                addNewLike(model, photo_id_list.get(position), fieldLike_list.get(position));
                            }
                        }
                    });
                }
            }
        });

        linLayout_comment.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            if (from_comment != null) {
                finish();

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, ChildRecyclerCommentShare.class);
                Gson gson = new Gson();
                String myGSon = gson.toJson(model);
                intent.putExtra("comment_recycler_child", myGSon);
                intent.putExtra("recyclerview_photo_id", photo_id_list.get(position));
                intent.putExtra("recyclerview_fieldLike", fieldLike_list.get(position));
                intent.putExtra("from_full_image", "from_full_image");
                context.startActivity(intent);
            }
        });

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, null, model.getUser_id(), url_list.get(position),
                photo_id_list.get(position), "from_full_image", "", true);
        linLayout_share.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("item_view", "");
                bottomSheetShare.setArguments(bundle);
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "fullImage: error: "+e.getMessage());
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

    private void removeLike(BattleModel model, String photo_id, String fieldLike) {
        Log.d(TAG, "onDoubleTap: single tap detected.");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_child_likes_share))
                .child(photo_id)
                .child(fieldLike);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    String keyID = ds.getKey();

                    //case1: Then user already liked the photo
                    if (mLikedByCurrentUser &&
                            Objects.requireNonNull(ds.getValue(Like.class)).getUser_id()
                                    .equals(user_id)) {

                        // update like count
                        int count = Integer.parseInt(number_of_likes.getText().toString());
                        String str = String.valueOf(count-1);
                        if (str.equals("0"))
                            number_of_likes.setVisibility(View.GONE);
                        number_of_likes.setText(str);

                        assert keyID != null;
                        myRef.child(context.getString(R.string.dbname_battle_media_share))
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_child_likes_share))
                                .child(photo_id)
                                .child(fieldLike)
                                .child(keyID)
                                .removeValue();

                        myRef.child(context.getString(R.string.dbname_battle))
                                .child(model.getUser_id())
                                .child(model.getPhoto_id())
                                .child(context.getString(R.string.field_child_likes_share))
                                .child(photo_id)
                                .child(fieldLike)
                                .child(keyID)
                                .removeValue();

                        getLikesString(model, photo_id, fieldLike);
                        updateLike(model, photo_id, fieldLike);

                    }
                    //case2: The user has not liked the photo
                    else if (!mLikedByCurrentUser){
                        // add new like
                        addNewLike(model, photo_id, fieldLike);
                        break;
                    }
                }
                // s'il n'existe encore aucun like
                if(!snapshot.exists()){
                    //add new like
                    addNewLike(model, photo_id, fieldLike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewLike(BattleModel model, String photo_id, String fieldLike){
        Log.d(TAG, "addNewLike: adding new like");
        // update like count
        int count = Integer.parseInt(number_of_likes.getText().toString());
        String str = String.valueOf(count+1);
        if (!str.equals("0"))
            number_of_likes.setVisibility(View.VISIBLE);
        number_of_likes.setText(str);

        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(user_id);

        assert newLikeID != null;
        myRef.child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_child_likes_share))
                .child(photo_id)
                .child(fieldLike)
                .child(newLikeID)
                .setValue(like);

        myRef.child(context.getString(R.string.dbname_battle))
                .child(model.getUser_id())
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_child_likes_share))
                .child(photo_id)
                .child(fieldLike)
                .child(newLikeID)
                .setValue(like);

        // affichage à l'écran
        getLikesString(model, photo_id, fieldLike);
        updateLike(model, photo_id, fieldLike);
    }

    private void getLikesString(BattleModel model, String photo_id, String like_id){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_child_likes_share))
                .child(photo_id)
                .child(like_id);

        // on parcours tous les likes
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new StringBuilder();

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

                                mUsers.append(user.getUsername());
                                mUsers.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
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

    private void updateLike(BattleModel model, String photo_id, String like_id){
        Log.d(TAG, "getLikesString: getting likes string");
        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_child_likes_share))
                .child(photo_id)
                .child(like_id);

        // on parcours tous les likes
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateLikeUsers = new StringBuilder();

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

                                updateLikeUsers.append(user.getUsername());
                                updateLikeUsers.append(",");
                            }

                            // vérifie si c'est l'utilistateur courant a liké
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
            }

        } else {
            if (like_heart.isSelected()) {
                like_heart.setSelected(false);
                image.setImageResource(R.drawable.ic_heart_white);
            }
        }
    }
    public void setLikes(BattleModel model, String photo_id, String fieldLike) {
        likes_count = 0;
        number_of_likes.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(model.getPhoto_id())
                .child(context.getString(R.string.field_child_likes_share))
                .child(photo_id)
                .child(fieldLike);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    // add user id to the list
                    likes_count++;
                }

                if (likes_count >= 1) {
                    number_of_likes.setVisibility(View.VISIBLE);

                    double count;
                    if (likes_count >= 1000) {
                        count = (float)likes_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";

                        number_of_likes.setText(tv_count);

                    } else {
                        number_of_likes.setText(String.valueOf(likes_count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setComments(BattleModel battleModel, String photo_id) {
        comments_count = 0;
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_battle_media_share))
                .child(battleModel.getPhoto_id())
                .child(context.getString(R.string.field_child_comments_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comments));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_battle_media_share))
                            .child(battleModel.getPhoto_id())
                            .child(context.getString(R.string.field_child_comments_share))
                            .child(photo_id)
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

    private void setShare(String photo_id) {
        shares_count = 0;
        number_of_share.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(photo_id);

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

    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}