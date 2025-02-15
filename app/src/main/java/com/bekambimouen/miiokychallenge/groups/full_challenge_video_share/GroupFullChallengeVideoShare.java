package com.bekambimouen.miiokychallenge.groups.full_challenge_video_share;

import static com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFullChallenge.preDownloadVideo;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CirclePagerIndicatorBlackLayoutDecoration;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.challenge_category.Util_BattleChallengeCategory;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.groups.administrators.GroupProfileAdmin;
import com.bekambimouen.miiokychallenge.groups.comment_share.GroupCommentShare;
import com.bekambimouen.miiokychallenge.groups.full_challenge_video.adapter.GroupFullChallengeVideoAdapter;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupProfileMember;
import com.bekambimouen.miiokychallenge.interfaces.HelperPlayerViewListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.ChallengeModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_video.PrepareNextVideoFullChallenge;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.toro.widget.Container;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_UserGroups;
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
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupFullChallengeVideoShare extends AppCompatActivity {
    private static final String TAG = "GroupFullChallengeVideoShare";

    // widgets
    private Container recyclerView;
    private TextView numerateur, denominateur;
    private ProgressBar progressBar;
    private RelativeLayout relLayout_view_overlay;

    // scroller data
    private ImageView flag_icon;
    private ImageView iv_download;
    private ImageView ivEnveloppe;
    private ImageView share;
    private ImageView iv_image;
    private ImageView img_vol;
    private View view_online;
    private CircleImageView profil_photo;
    private RelativeLayout relLayout_view_save;
    private RelativeLayout rel_vol;
    private TextView country_name;
    private TextView username;
    private RelativeLayout parent, relLayout_go_user_profile;
    private TextView category;
    private ViewMoreTextView caption;
    private TextView translate_comment;
    private TextView tv_download;
    private TextView nb_commentaires;
    private TextView tv_share;

    // vars
    private GroupFullChallengeVideoShare context;
    private GroupFullChallengeVideoAdapter adapter;
    private BottomSheetSharePublication bottomSheetShare;
    private BattleModel battleModel;
    private HelperPlayerViewListener playerViewListener;
    private Player player;
    private BattleModel challengeGson, challengeRepGson;
    private List<ChallengeModel> challengeList;
    private List<String> response_user_id_list, photo_id_list;
    private String from_comment, photo_id;
    private int position;
    private double comments_count;
    private double saved_count;
    private double shared_count;
    private Handler handler;
    private Runnable preDownloadRunnable;

    private boolean isFrom_approval_post;
    private boolean isMute = false;

    // single bottom
    private String video_double_single_bottom;
    private String response_video_double_single_bottom;
    // single bottom
    private String video_double_top_bottom;
    private String response_video_double_top_bottom;

    private CustomShareProgressView progresDialog;

    private void showLoading(){
        if (progresDialog == null)
            progresDialog = new CustomShareProgressView(context);
        progresDialog.show();
    }

    // firebase
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String user_id;

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
        setContentView(R.layout.activity_group_full_challenge_video_share);
        context = this;
        getBlackTheme();

        myRef = FirebaseDatabase.getInstance().getReference();
        database=FirebaseDatabase.getInstance();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());
        challengeList = new ArrayList<>();
        response_user_id_list = new ArrayList<>();
        photo_id_list = new ArrayList<>();

        try {
            position = getIntent().getIntExtra("position", 0);
            Gson gson = new Gson();
            challengeGson = gson.fromJson(getIntent().getStringExtra("challengeGson"), BattleModel.class);
            challengeRepGson = gson.fromJson(getIntent().getStringExtra("challengeRepGson"), BattleModel.class);

            from_comment = getIntent().getStringExtra("from_comment");

            // single bottom
            video_double_single_bottom = getIntent().getStringExtra("video_double_single_bottom");
            response_video_double_single_bottom = getIntent().getStringExtra("response_video_double_single_bottom");
            // single bottom
            video_double_top_bottom = getIntent().getStringExtra("video_double_top_bottom");
            response_video_double_top_bottom = getIntent().getStringExtra("response_video_double_top_bottom");

            // data from approval post
            isFrom_approval_post = getIntent().getBooleanExtra("isFrom_approval_post", false);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        if (challengeGson != null) {
            battleModel = challengeGson;
            photo_id = challengeGson.getPhoto_id();
            photo_id_list.add(challengeGson.getPhoto_id_un());
            photo_id_list.add(challengeGson.getPhoto_id_deux());
            getMaVideoDouble(challengeGson);
        } else {
            battleModel = challengeRepGson;
            photo_id = challengeRepGson.getPhoto_id();
            photo_id_list.add(challengeRepGson.getReponse_photoID());
            photo_id_list.add(challengeRepGson.getInvite_photoID());
            response_user_id_list.add(challengeRepGson.getReponse_user_id());
            response_user_id_list.add(challengeRepGson.getInvite_userID());
            getRepVideoDouble(challengeRepGson);
        }

        progressBar = findViewById(R.id.progressBar);
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        view_online = findViewById(R.id.view_online);
        profil_photo = findViewById(R.id.profile_photo);
        username = findViewById(R.id.username);
        relLayout_go_user_profile = findViewById(R.id.relLayout_go_user_profile);
        category = findViewById(R.id.category);
        caption = findViewById(R.id.caption);
        translate_comment = findViewById(R.id.translate_comment);
        iv_image = findViewById(R.id.iv_image);
        img_vol = findViewById(R.id.img_vol);
        rel_vol = findViewById(R.id.rel_vol);
        relLayout_view_save = findViewById(R.id.relLayout_view_save);
        ivEnveloppe = findViewById(R.id.message_video);
        share = findViewById(R.id.iv_share);
        nb_commentaires = findViewById(R.id.nb_commentaires);
        tv_share = findViewById(R.id.tv_share);
        tv_download = findViewById(R.id.tv_download);
        iv_download = findViewById(R.id.iv_download);
        flag_icon = findViewById(R.id.flag_icon);
        country_name = findViewById(R.id.country_name);
        LinearLayout optionCameraVideo = findViewById(R.id.optionCameraVideo);

        // hide view when is from approval post
        if (isFrom_approval_post)
            optionCameraVideo.setVisibility(View.GONE);

        numerateur = findViewById(R.id.numerateur);
        denominateur = findViewById(R.id.denominateur);
        recyclerView = findViewById(R.id.container);
        recyclerView.setHasFixedSize(true);

        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        manager.scrollToPositionWithOffset(position, 0);
        recyclerView.setLayoutManager(manager);

        playerViewListener = myHelper -> player = myHelper;
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    // for video_double
    private void getMaVideoDouble(BattleModel model) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(model.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String profil = user.getProfileUrl();
                    String name = user.getUsername();

                    challengeList.add(new ChallengeModel(profil, model.getUrlUn(), model.getThumbnail_un(), model.getPhoto_id_un(),
                            model.getPhoto_id_un(), name, model.getCaption(), model.getInvite_category(),
                            model.getUser_id(), user, model.getDate_created(), model.getCountry_name(), model.getCountryID()));

                    challengeList.add(new ChallengeModel(profil, model.getUrlDeux(), model.getThumbnail_deux(), model.getPhoto_id_deux(),
                            model.getPhoto_id_un(), name, model.getCaption(), model.getInvite_category(),
                            model.getUser_id(), user, model.getDate_created(), model.getCountry_name(), model.getCountryID()));

                    // pour que les prmières valeurs soient visible
                    if (position == 0) {
                        numerateur.setText(String.valueOf((1)));
                    } else {
                        numerateur.setText(String.valueOf((2)));
                    }
                    denominateur.setText(String.valueOf(challengeList.size()));

                    GroupFullChallengeVideoAdapter adapter = new GroupFullChallengeVideoAdapter(context,
                            challengeList, playerViewListener, progressBar);
                    recyclerView.setAdapter(adapter);

                    // preload video
                    if (position == 0) {
                        // first download the second video _____________________________________________________
                        ChallengeModel nextVideoItem = challengeList.get(1);

                        // Create a PreloadListener
                        PrepareNextVideoFullChallenge.PreloadListener preloadListener = new PrepareNextVideoFullChallenge.PreloadListener() {
                            @Override
                            public void onPreloadProgress(ChallengeModel videoItem, int progress) {
                                // Handle progress updates (e.g., update a progress bar)
                                Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                            }

                            @Override
                            public void onPreloadComplete(ChallengeModel videoItem) {
                                // Handle completion (e.g., update UI)
                                Log.d("Preload", "Complete for " + videoItem.getUrl());
                            }

                            @Override
                            public void onPreloadError(ChallengeModel videoItem, Exception e) {
                                // Handle errors (e.g., show an error message)
                                Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                            }
                        };

                        // Call preDownloadVideo with the listener
                        preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
                    }

                    if (position == 1) {
                        // first download the second video _____________________________________________________
                        ChallengeModel previousVideoItem = challengeList.get(0);

                        // Create a PreloadListener
                        PrepareNextVideoFullChallenge.PreloadListener preloadListener = new PrepareNextVideoFullChallenge.PreloadListener() {
                            @Override
                            public void onPreloadProgress(ChallengeModel videoItem, int progress) {
                                // Handle progress updates (e.g., update a progress bar)
                                Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                            }

                            @Override
                            public void onPreloadComplete(ChallengeModel videoItem) {
                                // Handle completion (e.g., update UI)
                                Log.d("Preload", "Complete for " + videoItem.getUrl());
                            }

                            @Override
                            public void onPreloadError(ChallengeModel videoItem, Exception e) {
                                // Handle errors (e.g., show an error message)
                                Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                            }
                        };

                        // Call preDownloadVideo with the listener
                        preDownloadVideo(context.getApplicationContext(), previousVideoItem, preloadListener);
                    }

                    // le slidding: les pointillés du bas
                    recyclerView.addItemDecoration(new CirclePagerIndicatorBlackLayoutDecoration());
                    // all data
                    getData(position);

                    // volume manager
                    rel_vol.setOnClickListener(view -> {
                        if (player != null) {
                            if (player.getVolume() == 0) {
                                isMute = false;
                                player.setVolume(1);
                                img_vol.setImageResource(R.drawable.ic_unmute);

                            } else {
                                isMute = true;
                                player.setVolume(0);
                                img_vol.setImageResource(R.drawable.ic_mute);
                            }
                        }

                    });

                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                int position = ((LinearLayoutManager) requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                                numerateur.setText(String.valueOf((position + 1)));
                                denominateur.setText(String.valueOf(challengeList.size()));

                                if (player != null) {
                                    if (isMute) {
                                        try {
                                            player.setVolume(0);
                                            img_vol.setImageResource(R.drawable.ic_mute);
                                        } catch (NullPointerException e) {
                                            Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                        }

                                    } else {
                                        player.setVolume(1);
                                        img_vol.setImageResource(R.drawable.ic_unmute);
                                    }
                                }

                                // all data
                                getData(position);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // for response video_double
    private void getRepVideoDouble(BattleModel challengeRepGson) {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(challengeRepGson.getReponse_user_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    String reponse_userID = user.getUser_id();
                    String reponse_profil = user.getProfileUrl();
                    String reponse_name = user.getUsername();

                    challengeList.add(new ChallengeModel(reponse_profil, challengeRepGson.getReponse_url(), challengeRepGson.getThumbnail_response(),
                            challengeRepGson.getReponse_photoID(), challengeRepGson.getReponse_photoID(),
                            reponse_name, challengeRepGson.getReponse_caption(), challengeRepGson.getInvite_category(),
                            reponse_userID, user, challengeRepGson.getDate_created(), challengeRepGson.getReponse_country_name(),
                            challengeRepGson.getReponse_countryID()));

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference.child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(challengeRepGson.getInvite_userID());


                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                String invite_userID = user.getUser_id();
                                String invite_profil = user.getProfileUrl();
                                String invite_name = user.getUsername();

                                challengeList.add(new ChallengeModel(invite_profil, challengeRepGson.getInvite_url(), challengeRepGson.getThumbnail_invite(),
                                        challengeRepGson.getInvite_photoID(), challengeRepGson.getReponse_photoID(),
                                        invite_name, challengeRepGson.getInvite_caption(), challengeRepGson.getInvite_category(),
                                        invite_userID, user, challengeRepGson.getDate_created(),
                                        challengeRepGson.getInvite_country_name(), challengeRepGson.getInvite_countryID()));
                            }

                            adapter = new GroupFullChallengeVideoAdapter(context,
                                    challengeList, playerViewListener, progressBar);
                            recyclerView.setAdapter(adapter);

                            // preload video
                            if (position == 0) {
                                // first download the second video _____________________________________________________
                                ChallengeModel nextVideoItem = challengeList.get(1);

                                // Create a PreloadListener
                                PrepareNextVideoFullChallenge.PreloadListener preloadListener = new PrepareNextVideoFullChallenge.PreloadListener() {
                                    @Override
                                    public void onPreloadProgress(ChallengeModel videoItem, int progress) {
                                        // Handle progress updates (e.g., update a progress bar)
                                        Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                                    }

                                    @Override
                                    public void onPreloadComplete(ChallengeModel videoItem) {
                                        // Handle completion (e.g., update UI)
                                        Log.d("Preload", "Complete for " + videoItem.getUrl());
                                    }

                                    @Override
                                    public void onPreloadError(ChallengeModel videoItem, Exception e) {
                                        // Handle errors (e.g., show an error message)
                                        Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                                    }
                                };

                                // Call preDownloadVideo with the listener
                                preDownloadVideo(context.getApplicationContext(), nextVideoItem, preloadListener);
                            }

                            if (position == 1) {
                                // first download the second video _____________________________________________________
                                ChallengeModel previousVideoItem = challengeList.get(0);

                                // Create a PreloadListener
                                PrepareNextVideoFullChallenge.PreloadListener preloadListener = new PrepareNextVideoFullChallenge.PreloadListener() {
                                    @Override
                                    public void onPreloadProgress(ChallengeModel videoItem, int progress) {
                                        // Handle progress updates (e.g., update a progress bar)
                                        Log.d("Preload", "Progress for " + videoItem.getUrl() + ": " + progress + "%");
                                    }

                                    @Override
                                    public void onPreloadComplete(ChallengeModel videoItem) {
                                        // Handle completion (e.g., update UI)
                                        Log.d("Preload", "Complete for " + videoItem.getUrl());
                                    }

                                    @Override
                                    public void onPreloadError(ChallengeModel videoItem, Exception e) {
                                        // Handle errors (e.g., show an error message)
                                        Log.e("Preload", "Error for " + videoItem.getUrl(), e);
                                    }
                                };

                                // Call preDownloadVideo with the listener
                                preDownloadVideo(context.getApplicationContext(), previousVideoItem, preloadListener);
                            }

                            // le slidding: les pointillés du bas
                            recyclerView.addItemDecoration(new CirclePagerIndicatorBlackLayoutDecoration());
                            // all data
                            getData(position);

                            // volume manager
                            rel_vol.setOnClickListener(view -> {
                                if (player != null) {
                                    if (player.getVolume() == 0) {
                                        isMute = false;
                                        player.setVolume(1);
                                        img_vol.setImageResource(R.drawable.ic_unmute);

                                    } else {
                                        isMute = true;
                                        player.setVolume(0);
                                        img_vol.setImageResource(R.drawable.ic_mute);
                                    }
                                }

                            });

                            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);

                                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                        int position = ((LinearLayoutManager) requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                                        numerateur.setText(String.valueOf((position + 1)));
                                        denominateur.setText(String.valueOf(challengeList.size()));

                                        if (player != null) {
                                            if (isMute) {
                                                try {
                                                    player.setVolume(0);
                                                    img_vol.setImageResource(R.drawable.ic_mute);
                                                } catch (NullPointerException e) {
                                                    Log.d(TAG, "onScrollStateChanged: "+e.getMessage());
                                                }

                                            } else {
                                                player.setVolume(1);
                                                img_vol.setImageResource(R.drawable.ic_unmute);
                                            }
                                        }

                                        // all data
                                        getData(position);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // pour que les prmières valeurs soient visible
                    if (position == 0) {
                        numerateur.setText(String.valueOf((1)));
                    } else {
                        numerateur.setText(String.valueOf((2)));
                    }
                    denominateur.setText(String.valueOf(2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getData(int position) {
        database.getReference()
                .child(context.getString(R.string.dbname_presence))
                .child(challengeList.get(position).getUser_id())
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
                                    if (!challengeList.get(position).getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                        view_online.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        setComments();
        setSavedVideosInGallery(challengeList.get(position));
        setSharedVideo(challengeList.get(position));

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(challengeList.get(position).getThumbnail())
                .placeholder(R.color.black_semi_transparen3)
                .centerCrop()
                .into(iv_image);

        // category
        String set_category = Util_BattleChallengeCategory.getBattleChallengeCategory(context, battleModel);
        category.setText(Html.fromHtml("#"+set_category));

        String invite_category_status = battleModel.getInvite_category_status();
        category.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            Gson gson = new Gson();
            String myGson = gson.toJson(battleModel);
            intent.putExtra("battle_model", myGson);
            intent.putExtra("category_status", invite_category_status);
            context.startActivity(intent);
        });

        // country name and flag
        country_name.setText(challengeList.get(position).getCountry_name());
        Util.getCountryFlag(challengeList.get(position).getCountryID(), flag_icon);

        String description = challengeList.get(position).getCaption();

        // Get the current language in device
        String language = Locale.getDefault().getLanguage();

        if (!TextUtils.isEmpty(description)) {
            caption.setCharText(description);
            caption.setVisibility(View.VISIBLE);
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

        //get the profile image and username
        Query myQuery;
        if (challengeGson != null) {
            myQuery = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(battleModel.getUser_id_sharing());

        } else {
            myQuery = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(response_user_id_list.get(position));
        }

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    //get the profile image and username
                    username.setText(user.getUsername());

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profil_photo);

                    // single bottom
                    if (battleModel.isGroup_share_single_bottom_response_video_double()) {
                        relLayout_go_user_profile.setOnClickListener(view -> {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    user.getUsername());

                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent;
                            if (user.getUser_id().equals(user_id)) {
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

                    // top bottom
                    if (battleModel.isGroup_share_top_bottom_response_video_double()) {
                        Query myQuery1 = myRef
                                .child(context.getString(R.string.dbname_user_group))
                                .child(battleModel.getSharing_admin_master())
                                .orderByChild(context.getString(R.string.field_group_id))
                                .equalTo(battleModel.getShared_group_id());
                        myQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                    assert objectMap != null;
                                    UserGroups user_groups = Util_UserGroups.getUserGroups(context, objectMap);

                                    relLayout_go_user_profile.setOnClickListener(v -> {
                                        if (relLayout_view_overlay != null)
                                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                                        Gson gson = new Gson();
                                        String myGson = gson.toJson(user_groups);
                                        getWindow().setExitTransition(new Slide(Gravity.END));
                                        getWindow().setEnterTransition(new Slide(Gravity.START));
                                        Intent intent;
                                        if (user_groups.getUser_id().equals(user_id)) {
                                            intent = new Intent(context, GroupProfileAdmin.class);
                                        } else {
                                            intent = new Intent(context, GroupProfileMember.class);
                                        }
                                        intent.putExtra("user_groups", myGson);
                                        intent.putExtra("userID", response_user_id_list.get(position));
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ivEnveloppe.setOnClickListener(view -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            if (from_comment != null) {
                finish();

            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                String myGSon = gson.toJson(battleModel);
                Intent intent = new Intent(context, GroupCommentShare.class);
                if (video_double_single_bottom != null) {
                    intent.putExtra("comment_image_double_single_bottom", myGSon);
                    // to show image une publication
                    intent.putExtra("video_double_single_bottom", "video_double_single_bottom");

                }else if (video_double_top_bottom != null) {
                    intent.putExtra("comment_image_double_top_bottom", myGSon);
                    // to show image une publication
                    intent.putExtra("video_double_top_bottom", "video_double_top_bottom");

                } else if (response_video_double_single_bottom != null) {
                    intent.putExtra("comment_reponse_image_double_single_bottom", myGSon);
                    // to show image une publication
                    intent.putExtra("response_vid_double_single_bottom", "response_vid_double_single_bottom");

                } else if (response_video_double_top_bottom != null) {
                    intent.putExtra("comment_reponse_image_double_top_bottom", myGSon);
                    // to show image une publication
                    intent.putExtra("response_vid_double_top_bottom", "response_vid_double_top_bottom");

                }
                intent.putExtra("from_full_video", "from_full_video");
                context.startActivity(intent);
            }
        });

        // share
        bottomSheetShare = new BottomSheetSharePublication(context, null, battleModel.getUser_id(), challengeList.get(position).getUrl(),
                photo_id_list.get(position), "from_video", "", true);
        share.setOnClickListener(view -> {
            Util.preventTwoClick(share);
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
                bundle.putBoolean("group_response_image_double", false);
                bundle.putBoolean("group_response_video_double", true);
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
                Log.d(TAG, "getData: "+e.getMessage());
            }
        });

        iv_download.setOnClickListener(view -> {
            showLoading();
            DownloadVideoFile videoFile = new DownloadVideoFile();
            videoFile.execute(challengeList.get(position).getUrl());

            String newKey = myRef.push().getKey();
            assert newKey != null;
            myRef.child(context.getString(R.string.dbname_save_file_in_gallery))
                    .child(photo_id_list.get(position))
                    .child(newKey)
                    .setValue(user_id);
        });
    }

    public void setComments() {
        comments_count = 0;
        nb_commentaires.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_group_media_share))
                .child(photo_id)
                .child(context.getString(R.string.field_comment_share));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_group_media_share))
                            .child(photo_id)
                            .child(context.getString(R.string.field_comment_share))
                            .child(keyID)
                            .child(context.getString(R.string.field_comments));

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: "+data.getKey());
                                comments_count++;
                            }

                            if (comments_count >= 1) {
                                nb_commentaires.setVisibility(View.VISIBLE);

                                double count;
                                if (comments_count >= 1000) {
                                    count = comments_count/1000;

                                    String tv_count = new DecimalFormat("##.##").format(count)+"k";
                                    nb_commentaires.setText(tv_count);

                                } else {
                                    nb_commentaires.setText(String.valueOf((int) comments_count));
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

    // count number of video downloaded
    public void setSavedVideosInGallery(ChallengeModel model) {
        saved_count = 0;
        tv_download.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_save_file_in_gallery))
                .child(model.getPhoto_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: ds: "+ds);
                    saved_count++;
                }

                if (saved_count >= 1) {
                    tv_download.setVisibility(View.VISIBLE);

                    double count;
                    if (saved_count >= 1000) {
                        count = saved_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        tv_download.setText(tv_count);

                    } else {
                        tv_download.setText(String.valueOf((int) saved_count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // share video coubt
    private void setSharedVideo(ChallengeModel model) {
        shared_count = 0;
        tv_share.setVisibility(View.INVISIBLE);

        Query query = myRef
                .child(context.getString(R.string.dbname_share_video))
                .child(model.getPhoto_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());
                    shared_count++;
                }

                if (shared_count >= 1) {
                    tv_share.setVisibility(View.VISIBLE);
                    double count;
                    if (shared_count >= 1000) {
                        count = shared_count/1000;

                        String tv_count = new DecimalFormat("##.##").format(count)+"k";
                        tv_share.setText(tv_count);

                    } else {
                        tv_share.setText(String.valueOf((int) shared_count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // download video from URL
    @SuppressLint("StaticFieldLeak")
    class DownloadVideoFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... videoURL) {
            int count;
            try {
                URL url = new URL(videoURL[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), challengeList.get(position).getDate_created()+".mp4");
                MediaScannerConnection.scanFile(context, new String[] { file.getPath() }, new String[] { "video/mp4" }, null);
                OutputStream output = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    output = Files.newOutputStream(file.toPath());
                }

                byte[] data = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    assert output != null;
                    output.write(data, 0, count);
                }

                assert output != null;
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                progresDialog.dismiss();
                context.runOnUiThread(() -> Toast.makeText(context, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progresDialog.dismiss();
            Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);

            if (relLayout_view_save.getVisibility() == View.GONE) {
                relLayout_view_save.setVisibility(View.VISIBLE);
                relLayout_view_save.startAnimation(slideUp);
            }

            new Handler().postDelayed(() -> {
                TranslateAnimation animate1 = new TranslateAnimation(0,0,0,relLayout_view_save.getHeight());
                animate1.setDuration(500);
                animate1.setFillAfter(true);
                relLayout_view_save.startAnimation(animate1);
                relLayout_view_save.setVisibility(View.GONE);
            }, 2000);
        }
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