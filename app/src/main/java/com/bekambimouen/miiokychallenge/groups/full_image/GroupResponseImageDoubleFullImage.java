package com.bekambimouen.miiokychallenge.groups.full_image;



import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
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
import com.bekambimouen.miiokychallenge.challenge_category.Util_BattleChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.comment.GroupComment;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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

public class GroupResponseImageDoubleFullImage extends AppCompatActivity {
    private static final String TAG = "GroupResponseImageDoubleFullImage";

    // widgets
    private ImageView menu_item;
    private TextView username;
    private TextView the_user;
    private TextView number_of_comments;
    private TextView number_of_share;
    private TextView tps_publication;
    private TextView category;
    private ViewMoreTextView caption;
    private TextView translate_comment;
    private RelativeLayout parent, relLayout_username, relLayout_view_overlay;
    private RelativeLayout relLayout_write_to;
    private LinearLayout linLayout_comment;
    private LinearLayout linLayout_share;

    // vars
    private GroupResponseImageDoubleFullImage context;
    private BattleModel model;
    private BottomSheetSharePublication bottomSheetShare;
    private List<String> url_list, photo_id_list, caption_list, id_list;
    private String from_comment;
    private boolean isFrom_approval_post;
    private int position;
    private int comments_count, shares_count;

    // firebase
    private DatabaseReference myRef;
    private String user_id;
    private FirebaseDatabase database;

    private void getBlackTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_group_response_image_double_full_image);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        url_list = new ArrayList<>();
        photo_id_list = new ArrayList<>();
        caption_list = new ArrayList<>();
        id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            model = gson.fromJson(getIntent().getStringExtra("battleModel_response_image_double"), BattleModel.class);

            position = getIntent().getIntExtra("position", 0);
            from_comment = getIntent().getStringExtra("from_comment");

            // data from approval post
            isFrom_approval_post = getIntent().getBooleanExtra("isFrom_approval_post", false);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        LinearLayout linLayout_all_data = findViewById(R.id.linLayout_all_data);
        menu_item = findViewById(R.id.menu_item);
        ImageView back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        the_user = findViewById(R.id.the_user);
        tps_publication = findViewById(R.id.tps_publication);
        caption = findViewById(R.id.caption);
        category = findViewById(R.id.category);
        translate_comment = findViewById(R.id.translate_comment);
        relLayout_username = findViewById(R.id.relLayout_username);

        number_of_comments = findViewById(R.id.number_of_comments);
        number_of_share = findViewById(R.id.number_of_share);
        relLayout_write_to = findViewById(R.id.relLayout_write_to);
        linLayout_comment = findViewById(R.id.linLayout_comment);
        linLayout_share = findViewById(R.id.linLayout_share);

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
            url_list.add(model.getReponse_url());
            url_list.add(model.getInvite_url());
            photo_id_list.add(model.getReponse_photoID());
            photo_id_list.add(model.getInvite_photoID());
            adapter.notifyDataSetChanged();

            // caption;
            caption_list.add(model.getReponse_caption());
            caption_list.add(model.getInvite_caption());
            // user id list
            id_list.add(model.getReponse_user_id());
            id_list.add(model.getInvite_userID());

            setComments(model.getReponse_photoID());
            setShare(photo_id_list.get(position));
            fullImage(position);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE){
                        int position = ((LinearLayoutManager) requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                        setComments(model.getReponse_photoID());
                        setShare(photo_id_list.get(position));
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
        if (id_list.get(position).equals(user_id))
            relLayout_write_to.setVisibility(View.GONE);
        else
            relLayout_write_to.setVisibility(View.VISIBLE);

        // category
        String set_category = Util_BattleChallengeCategory.getBattleChallengeCategory(context, model);
        category.setText(Html.fromHtml("#"+set_category));

        String invite_category_status = model.getInvite_category_status();
        category.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(model);
            intent.putExtra("battle_model", myGson);
            intent.putExtra("category_status", invite_category_status);
            context.startActivity(intent);
        });

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(id_list.get(position));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String userID = user.getUser_id();
                    String name = user.getUsername();

                    username.setText(name);
                    the_user.setText(Html.fromHtml(context.getString(R.string.write_to)+" "+name));

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

                                relLayout_username.setOnClickListener(view -> {
                                    if (relLayout_view_overlay != null)
                                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                                    getWindow().setExitTransition(new Slide(Gravity.END));
                                    getWindow().setEnterTransition(new Slide(Gravity.START));
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

        // caption
        if (!caption_list.get(position).isEmpty())
            caption.setVisibility(View.VISIBLE);
        caption.setCharText(caption_list.get(position));

        translate_comment.setVisibility(View.GONE);
        // Get the current language in device
        String language = Locale.getDefault().getLanguage();
        // detect text language
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(caption_list.get(position))
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
                            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        });

        translate_comment.setOnClickListener(view -> {
            translate_comment.setVisibility(View.GONE);
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.AUTO_DETECT,   // language from
                    language,         //language to
                    caption_list.get(position));

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

        linLayout_comment.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            if (from_comment != null) {
                finish();

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGSon = gson.toJson(model);
                Intent intent = new Intent(context, GroupComment.class);
                intent.putExtra("comment_reponse_image_double", myGSon);
                intent.putExtra("response_img_double", "response_img_double");
                intent.putExtra("from_full_image", "from_full_image");
                context.startActivity(intent);
            }
        });

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, null, id_list.get(position), url_list.get(position),
                photo_id_list.get(position), "from_full_image", "", false);
        linLayout_share.setOnClickListener(view -> {
            // prevent two clicks
            Util.preventTwoClick(linLayout_share);
            try {
                if (bottomSheetShare.isAdded())
                    return;
                Bundle bundle = new Bundle();
                bundle.putBoolean("group_share_single_bottom_circle_image", false);
                bundle.putBoolean("group_share_single_bottom_image_double", false);
                bundle.putBoolean("group_share_single_bottom_image_une", false);
                bundle.putBoolean("group_share_single_bottom_recycler", false);
                bundle.putBoolean("group_share_single_bottom_response_image_double", false);
                bundle.putBoolean("group_share_single_bottom_response_video_double", false);
                bundle.putBoolean("group_share_single_bottom_video_double", false);
                bundle.putBoolean("group_share_single_bottom_video_une", false);

                bundle.putBoolean("group_share_single_top_circle_image", false);
                bundle.putBoolean("group_share_single_top_image_double", false);
                bundle.putBoolean("group_share_single_top_image_une", false);
                bundle.putBoolean("group_share_single_top_recycler", false);
                bundle.putBoolean("group_share_single_top_response_image_double", false);
                bundle.putBoolean("group_share_single_top_response_video_double", false);
                bundle.putBoolean("group_share_single_top_video_double", false);
                bundle.putBoolean("group_share_single_top_video_une", false);

                bundle.putBoolean("group_share_top_bottom_circle_image", false);
                bundle.putBoolean("group_share_top_bottom_image_double", false);
                bundle.putBoolean("group_share_top_bottom_image_une", false);
                bundle.putBoolean("group_share_top_bottom_recycler", false);
                bundle.putBoolean("group_share_top_bottom_response_image_double", false);
                bundle.putBoolean("group_share_top_bottom_response_video_double", false);
                bundle.putBoolean("group_share_top_bottom_video_double", false);
                bundle.putBoolean("group_share_top_bottom_video_une", false);

                bundle.putBoolean("user_profile_shared", false);
                bundle.putBoolean("recyclerItem_shared", false);
                bundle.putBoolean("imageUne_shared", false);
                bundle.putBoolean("videoUne_shared", false);
                bundle.putBoolean("imageDouble_shared", false);
                bundle.putBoolean("videoDouble_shared", false);
                bundle.putBoolean("reponseImageDouble_shared", false);
                bundle.putBoolean("reponseVideoDouble_shared", false);
                bundle.putBoolean("user_profile", false);

                bundle.putBoolean("group_circle_image", false);
                bundle.putBoolean("group_image_double", false);
                bundle.putBoolean("group_image_une", false);
                bundle.putBoolean("group_recycler", false);
                bundle.putBoolean("group_response_image_double", true);
                bundle.putBoolean("group_response_video_double", false);
                bundle.putBoolean("group_video_double", false);
                bundle.putBoolean("group_video_une", false);

                bundle.putBoolean("circle_image", false);
                bundle.putBoolean("image_double", false);
                bundle.putBoolean("image_une", false);
                bundle.putBoolean("recycler", false);
                bundle.putBoolean("response_image_double", false);
                bundle.putBoolean("response_video_double", false);
                bundle.putBoolean("video_double", false);
                bundle.putBoolean("video_une", false);
                bottomSheetShare.setArguments(bundle);
                bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

            } catch (IllegalStateException e) {
                Log.d(TAG, "fullImage: "+e.getMessage());
            }
        });

        Query myQuery2 = myRef
                .child(context.getString(R.string.dbname_user_group))
                .child(model.getAdmin_master())
                .orderByChild(context.getString(R.string.field_group_id))
                .equalTo(model.getGroup_id());
        myQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                    // menu_item
                    BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
                    menu_item.setOnClickListener(view -> {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setComments(String photo_id) {
        comments_count = 0;
        number_of_comments.setVisibility(View.GONE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media))
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
                            .child(context.getString(R.string.dbname_group_media))
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