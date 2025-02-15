package com.bekambimouen.miiokychallenge.child_recyclerview.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetSharePublication;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.child_recyclerview.comment.ChildRecyclerComment;
import com.bekambimouen.miiokychallenge.crushers_and_likers.Likers;
import com.bekambimouen.miiokychallenge.full_image.RecyclerFullImage;
import com.bekambimouen.miiokychallenge.like_button_animation.SmallBangView;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    // vars
    private final AppCompatActivity context;
    private final List<String> list;
    private final BattleModel battleModel;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    public RecyclerViewAdapter(AppCompatActivity context, List<String> list, BattleModel battleModel,
                               ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.battleModel = battleModel;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_view_recyclerview, parent, false);
        return new RecyclerViewHolder(relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        String url = list.get(position);
        holder.bind(url, position);

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final SmallBangView like_heart;
        private final InstaLikeView mInstaLikeView;
        private final ImageView image;
        private final ImageView imageview;
        private final TextView number_of_likes, number_of_comments, number_of_share;
        private final ViewMoreTextView caption;
        private final TextView translate_comment;
        private final LinearLayout linLayout_like;
        private final LinearLayout linLayout_comment;
        private final LinearLayout linLayout_share;
        private final LinearLayout linLayout_count_like;

        // vars
        private BottomSheetSharePublication bottomSheetShare;
        private final GestureDetector mDetectorLike;
        private final RelativeLayout relLayout_view_overlay;
        private boolean mLikedByCurrentUser;
        private StringBuilder mUsers;
        private StringBuilder updateUsers;
        private final ArrayList<String> liker_list;
        private String photo_id;
        private String fieldLike;
        private String description;
        private int position;
        private int likes_count = 0;
        private int comments_count = 0;
        private int shares_count = 0;

        // firebase
        private final DatabaseReference myRef;
        private User mCurrentUser;

        @SuppressLint("ClickableViewAccessibility")
        public RecyclerViewHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            myRef = firebaseDatabase.getReference();
            liker_list = new ArrayList<>();

            imageview = itemView.findViewById(R.id.imageview);
            number_of_likes = itemView.findViewById(R.id.number_of_likes);
            number_of_comments = itemView.findViewById(R.id.number_of_comments);
            number_of_share = itemView.findViewById(R.id.number_of_share);
            caption = itemView.findViewById(R.id.caption);
            translate_comment = itemView.findViewById(R.id.translate_comment);
            linLayout_like = itemView.findViewById(R.id.linLayout_like);
            linLayout_count_like = itemView.findViewById(R.id.linLayout_count_like);
            linLayout_comment = itemView.findViewById(R.id.linLayout_comment);
            linLayout_share = itemView.findViewById(R.id.linLayout_share);
            like_heart = itemView.findViewById(R.id.like_heart);
            mInstaLikeView = itemView.findViewById(R.id.insta_like_view);
            image = itemView.findViewById(R.id.image);

            // for double tap to like
            mDetectorLike = new GestureDetector(context, new GestureListenerLike());
            imageview.setOnTouchListener((view, motionEvent) -> mDetectorLike.onTouchEvent(motionEvent));
        }

        public void bind(String url, int position) {
            this.position = position;
            getCurrentUser();

            caption.setCharText("");
            caption.setVisibility(View.GONE);
            translate_comment.setVisibility(View.GONE);
            number_of_likes.setText("0");
            number_of_comments.setText("0");
            number_of_share.setText("0");

            if(liker_list != null){
                liker_list.clear();
            }
            // add photo_id
            if (position == 0) {
                photo_id = battleModel.getPhotoi_id();
                fieldLike = context.getString(R.string.field_likes_i);
            }

            if (position == 1) {
                photo_id = battleModel.getPhotoii_id();
                fieldLike = context.getString(R.string.field_likes_ii);
            }

            if (position == 2) {
                photo_id = battleModel.getPhotoiii_id();
                fieldLike = context.getString(R.string.field_likes_iii);
            }

            if (position == 3) {
                photo_id = battleModel.getPhotoiv_id();
                fieldLike = context.getString(R.string.field_likes_iv);
            }

            if (position == 4) {
                photo_id = battleModel.getPhotov_id();
                fieldLike = context.getString(R.string.field_likes_v);
            }

            if (position == 5) {
                photo_id = battleModel.getPhotovi_id();
                fieldLike = context.getString(R.string.field_likes_vi);
            }

            if (position == 6) {
                photo_id = battleModel.getPhotovii_id();
                fieldLike = context.getString(R.string.field_likes_vii);
            }

            if (position == 7) {
                photo_id = battleModel.getPhotoviii_id();
                fieldLike = context.getString(R.string.field_likes_viii);
            }

            if (position == 8) {
                photo_id = battleModel.getPhotoix_id();
                fieldLike = context.getString(R.string.field_likes_ix);
            }

            if (position == 9) {
                photo_id = battleModel.getPhotox_id();
                fieldLike = context.getString(R.string.field_likes_x);
            }

            if (position == 10) {
                photo_id = battleModel.getPhotoxi_id();
                fieldLike = context.getString(R.string.field_likes_xi);
            }

            if (position == 11) {
                photo_id = battleModel.getPhotoxii_id();
                fieldLike = context.getString(R.string.field_likes_xii);
            }

            if (position == 12) {
                photo_id = battleModel.getPhotoxiii_id();
                fieldLike = context.getString(R.string.field_likes_xiii);
            }

            if (position == 13) {
                photo_id = battleModel.getPhotoxiv_id();
                fieldLike = context.getString(R.string.field_likes_xiv);
            }

            if (position == 14) {
                photo_id = battleModel.getPhotoxv_id();
                fieldLike = context.getString(R.string.field_likes_xv);
            }

            if (position == 15) {
                photo_id = battleModel.getPhotoxvi_id();
                fieldLike = context.getString(R.string.field_likes_xvi);
            }

            if (position == 16) {
                photo_id = battleModel.getPhotoxvii_id();
                fieldLike = context.getString(R.string.field_likes_xvii);
            }

            Query query = myRef.child(context.getString(R.string.dbname_battle))
                    .child(battleModel.getUser_id())
                    .orderByChild(context.getString(R.string.field_photoi_id))
                    .equalTo(battleModel.getPhotoi_id());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                        if (position == 0)
                            description = model.getCaption_i();
                        if (position == 1)
                            description = model.getCaption_ii();
                        if (position == 2)
                            description = model.getCaption_iii();
                        if (position == 3)
                            description = model.getCaption_iv();
                        if (position == 4)
                            description = model.getCaption_v();
                        if (position == 5)
                            description = model.getCaption_vi();
                        if (position == 6)
                            description = model.getCaption_vii();
                        if (position == 7)
                            description = model.getCaption_viii();
                        if (position == 8)
                            description = model.getCaption_ix();
                        if (position == 9)
                            description = model.getCaption_x();
                        if (position == 10)
                            description = model.getCaption_xi();
                        if (position == 11)
                            description = model.getCaption_xii();
                        if (position == 12)
                            description = model.getCaption_xiii();
                        if (position == 13)
                            description = model.getCaption_xiv();
                        if (position == 14)
                            description = model.getCaption_xv();
                        if (position == 15)
                            description = model.getCaption_xvi();
                        if (position == 16)
                            description = model.getCaption_xvii();


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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            getLikesString(battleModel, photo_id, fieldLike);
            updateLike(battleModel, photo_id, fieldLike);
            setLikes(battleModel, photo_id, fieldLike);
            setComments(photo_id);
            setShare(photo_id);

            Glide.with(context.getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.black_person)
                    .into(imageview);

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
                                // add photo_id
                                if (position == 0) {
                                    photo_id = battleModel.getPhotoi_id();
                                    fieldLike = context.getString(R.string.field_likes_i);
                                }

                                if (position == 1) {
                                    photo_id = battleModel.getPhotoii_id();
                                    fieldLike = context.getString(R.string.field_likes_ii);
                                }

                                if (position == 2) {
                                    photo_id = battleModel.getPhotoiii_id();
                                    fieldLike = context.getString(R.string.field_likes_iii);
                                }

                                if (position == 3) {
                                    photo_id = battleModel.getPhotoiv_id();
                                    fieldLike = context.getString(R.string.field_likes_iv);
                                }

                                if (position == 4) {
                                    photo_id = battleModel.getPhotov_id();
                                    fieldLike = context.getString(R.string.field_likes_v);
                                }

                                if (position == 5) {
                                    photo_id = battleModel.getPhotovi_id();
                                    fieldLike = context.getString(R.string.field_likes_vi);
                                }

                                if (position == 6) {
                                    photo_id = battleModel.getPhotovii_id();
                                    fieldLike = context.getString(R.string.field_likes_vii);
                                }

                                if (position == 7) {
                                    photo_id = battleModel.getPhotoviii_id();
                                    fieldLike = context.getString(R.string.field_likes_viii);
                                }

                                if (position == 8) {
                                    photo_id = battleModel.getPhotoix_id();
                                    fieldLike = context.getString(R.string.field_likes_ix);
                                }

                                if (position == 9) {
                                    photo_id = battleModel.getPhotox_id();
                                    fieldLike = context.getString(R.string.field_likes_x);
                                }

                                if (position == 10) {
                                    photo_id = battleModel.getPhotoxi_id();
                                    fieldLike = context.getString(R.string.field_likes_xi);
                                }

                                if (position == 11) {
                                    photo_id = battleModel.getPhotoxii_id();
                                    fieldLike = context.getString(R.string.field_likes_xii);
                                }

                                if (position == 12) {
                                    photo_id = battleModel.getPhotoxiii_id();
                                    fieldLike = context.getString(R.string.field_likes_xiii);
                                }

                                if (position == 13) {
                                    photo_id = battleModel.getPhotoxiv_id();
                                    fieldLike = context.getString(R.string.field_likes_xiv);
                                }

                                if (position == 14) {
                                    photo_id = battleModel.getPhotoxv_id();
                                    fieldLike = context.getString(R.string.field_likes_xv);
                                }

                                if (position == 15) {
                                    photo_id = battleModel.getPhotoxvi_id();
                                    fieldLike = context.getString(R.string.field_likes_xvi);
                                }

                                if (position == 16) {
                                    photo_id = battleModel.getPhotoxvii_id();
                                    fieldLike = context.getString(R.string.field_likes_xvii);
                                }

                                removeLike(battleModel, photo_id, fieldLike);
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
                                    // add photo_id
                                    if (position == 0) {
                                        photo_id = battleModel.getPhotoi_id();
                                        fieldLike = context.getString(R.string.field_likes_i);
                                    }

                                    if (position == 1) {
                                        photo_id = battleModel.getPhotoii_id();
                                        fieldLike = context.getString(R.string.field_likes_ii);
                                    }

                                    if (position == 2) {
                                        photo_id = battleModel.getPhotoiii_id();
                                        fieldLike = context.getString(R.string.field_likes_iii);
                                    }

                                    if (position == 3) {
                                        photo_id = battleModel.getPhotoiv_id();
                                        fieldLike = context.getString(R.string.field_likes_iv);
                                    }

                                    if (position == 4) {
                                        photo_id = battleModel.getPhotov_id();
                                        fieldLike = context.getString(R.string.field_likes_v);
                                    }

                                    if (position == 5) {
                                        photo_id = battleModel.getPhotovi_id();
                                        fieldLike = context.getString(R.string.field_likes_vi);
                                    }

                                    if (position == 6) {
                                        photo_id = battleModel.getPhotovii_id();
                                        fieldLike = context.getString(R.string.field_likes_vii);
                                    }

                                    if (position == 7) {
                                        photo_id = battleModel.getPhotoviii_id();
                                        fieldLike = context.getString(R.string.field_likes_viii);
                                    }

                                    if (position == 8) {
                                        photo_id = battleModel.getPhotoix_id();
                                        fieldLike = context.getString(R.string.field_likes_ix);
                                    }

                                    if (position == 9) {
                                        photo_id = battleModel.getPhotox_id();
                                        fieldLike = context.getString(R.string.field_likes_x);
                                    }

                                    if (position == 10) {
                                        photo_id = battleModel.getPhotoxi_id();
                                        fieldLike = context.getString(R.string.field_likes_xi);
                                    }

                                    if (position == 11) {
                                        photo_id = battleModel.getPhotoxii_id();
                                        fieldLike = context.getString(R.string.field_likes_xii);
                                    }

                                    if (position == 12) {
                                        photo_id = battleModel.getPhotoxiii_id();
                                        fieldLike = context.getString(R.string.field_likes_xiii);
                                    }

                                    if (position == 13) {
                                        photo_id = battleModel.getPhotoxiv_id();
                                        fieldLike = context.getString(R.string.field_likes_xiv);
                                    }

                                    if (position == 14) {
                                        photo_id = battleModel.getPhotoxv_id();
                                        fieldLike = context.getString(R.string.field_likes_xv);
                                    }

                                    if (position == 15) {
                                        photo_id = battleModel.getPhotoxvi_id();
                                        fieldLike = context.getString(R.string.field_likes_xvi);
                                    }

                                    if (position == 16) {
                                        photo_id = battleModel.getPhotoxvii_id();
                                        fieldLike = context.getString(R.string.field_likes_xvii);
                                    }

                                    addNewLike(battleModel, photo_id,fieldLike);
                                }
                            }
                        });
                    }
                }
            });

            linLayout_comment.setOnClickListener(view -> {
                // add photo_id
                if (position == 0) {
                    photo_id = battleModel.getPhotoi_id();
                    fieldLike = context.getString(R.string.field_likes_i);
                }

                if (position == 1) {
                    photo_id = battleModel.getPhotoii_id();
                    fieldLike = context.getString(R.string.field_likes_ii);
                }

                if (position == 2) {
                    photo_id = battleModel.getPhotoiii_id();
                    fieldLike = context.getString(R.string.field_likes_iii);
                }

                if (position == 3) {
                    photo_id = battleModel.getPhotoiv_id();
                    fieldLike = context.getString(R.string.field_likes_iv);
                }

                if (position == 4) {
                    photo_id = battleModel.getPhotov_id();
                    fieldLike = context.getString(R.string.field_likes_v);
                }

                if (position == 5) {
                    photo_id = battleModel.getPhotovi_id();
                    fieldLike = context.getString(R.string.field_likes_vi);
                }

                if (position == 6) {
                    photo_id = battleModel.getPhotovii_id();
                    fieldLike = context.getString(R.string.field_likes_vii);
                }

                if (position == 7) {
                    photo_id = battleModel.getPhotoviii_id();
                    fieldLike = context.getString(R.string.field_likes_viii);
                }

                if (position == 8) {
                    photo_id = battleModel.getPhotoix_id();
                    fieldLike = context.getString(R.string.field_likes_ix);
                }

                if (position == 9) {
                    photo_id = battleModel.getPhotox_id();
                    fieldLike = context.getString(R.string.field_likes_x);
                }

                if (position == 10) {
                    photo_id = battleModel.getPhotoxi_id();
                    fieldLike = context.getString(R.string.field_likes_xi);
                }

                if (position == 11) {
                    photo_id = battleModel.getPhotoxii_id();
                    fieldLike = context.getString(R.string.field_likes_xii);
                }

                if (position == 12) {
                    photo_id = battleModel.getPhotoxiii_id();
                    fieldLike = context.getString(R.string.field_likes_xiii);
                }

                if (position == 13) {
                    photo_id = battleModel.getPhotoxiv_id();
                    fieldLike = context.getString(R.string.field_likes_xiv);
                }

                if (position == 14) {
                    photo_id = battleModel.getPhotoxv_id();
                    fieldLike = context.getString(R.string.field_likes_xv);
                }

                if (position == 15) {
                    photo_id = battleModel.getPhotoxvi_id();
                    fieldLike = context.getString(R.string.field_likes_xvi);
                }

                if (position == 16) {
                    photo_id = battleModel.getPhotoxvii_id();
                    fieldLike = context.getString(R.string.field_likes_xvii);
                }

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ChildRecyclerComment.class);
                Gson gson = new Gson();
                String myGSon = gson.toJson(battleModel);
                intent.putExtra("comment_recycler_child", myGSon);
                intent.putExtra("recyclerview_photo_id", photo_id);
                intent.putExtra("recyclerview_fieldLike", fieldLike);
                context.startActivity(intent);
            });

            // share
            bottomSheetShare = new BottomSheetSharePublication(context, null, battleModel.getUser_id(), url,
                    photo_id, "from_recyclerView_item", "", false);
            linLayout_share.setOnClickListener(view -> {
                try {
                    if (bottomSheetShare.isAdded())
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("item_view", "");
                    bottomSheetShare.setArguments(bundle);
                    bottomSheetShare.show(context.getSupportFragmentManager(), "TAG");

                } catch (IllegalStateException e) {
                    Log.d(TAG, "bind: error: "+e.getMessage());
                }
            });
        }

        private final class GestureListenerLike extends GestureDetector.SimpleOnGestureListener {
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
                Intent intent = new Intent(context, RecyclerFullImage.class);
                Gson gson = new Gson();
                String myGson = gson.toJson(battleModel);
                intent.putExtra("battleModel_recyclerview", myGson);
                intent.putExtra("position", getLayoutPosition());
                intent.putExtra("item_description", description);
                intent.putExtra("recycler_list_size", list.size());
                intent.putExtra("position", getLayoutPosition());
                context.startActivity(intent);
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                Log.d(TAG, "onDoubleTap: single tap detected.");

                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                    image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            // add photo_id
                            if (position == 0) {
                                photo_id = battleModel.getPhotoi_id();
                                fieldLike = context.getString(R.string.field_likes_i);
                            }

                            if (position == 1) {
                                photo_id = battleModel.getPhotoii_id();
                                fieldLike = context.getString(R.string.field_likes_ii);
                            }

                            if (position == 2) {
                                photo_id = battleModel.getPhotoiii_id();
                                fieldLike = context.getString(R.string.field_likes_iii);
                            }

                            if (position == 3) {
                                photo_id = battleModel.getPhotoiv_id();
                                fieldLike = context.getString(R.string.field_likes_iv);
                            }

                            if (position == 4) {
                                photo_id = battleModel.getPhotov_id();
                                fieldLike = context.getString(R.string.field_likes_v);
                            }

                            if (position == 5) {
                                photo_id = battleModel.getPhotovi_id();
                                fieldLike = context.getString(R.string.field_likes_vi);
                            }

                            if (position == 6) {
                                photo_id = battleModel.getPhotovii_id();
                                fieldLike = context.getString(R.string.field_likes_vii);
                            }

                            if (position == 7) {
                                photo_id = battleModel.getPhotoviii_id();
                                fieldLike = context.getString(R.string.field_likes_viii);
                            }

                            if (position == 8) {
                                photo_id = battleModel.getPhotoix_id();
                                fieldLike = context.getString(R.string.field_likes_ix);
                            }

                            if (position == 9) {
                                photo_id = battleModel.getPhotox_id();
                                fieldLike = context.getString(R.string.field_likes_x);
                            }

                            if (position == 10) {
                                photo_id = battleModel.getPhotoxi_id();
                                fieldLike = context.getString(R.string.field_likes_xi);
                            }

                            if (position == 11) {
                                photo_id = battleModel.getPhotoxii_id();
                                fieldLike = context.getString(R.string.field_likes_xii);
                            }

                            if (position == 12) {
                                photo_id = battleModel.getPhotoxiii_id();
                                fieldLike = context.getString(R.string.field_likes_xiii);
                            }

                            if (position == 13) {
                                photo_id = battleModel.getPhotoxiv_id();
                                fieldLike = context.getString(R.string.field_likes_xiv);
                            }

                            if (position == 14) {
                                photo_id = battleModel.getPhotoxv_id();
                                fieldLike = context.getString(R.string.field_likes_xv);
                            }

                            if (position == 15) {
                                photo_id = battleModel.getPhotoxvi_id();
                                fieldLike = context.getString(R.string.field_likes_xvi);
                            }

                            if (position == 16) {
                                photo_id = battleModel.getPhotoxvii_id();
                                fieldLike = context.getString(R.string.field_likes_xvii);
                            }

                            removeLike(battleModel, photo_id, fieldLike);
                        }
                    });

                } else {
                    like_heart.setSelected(true);
                    image.setImageResource(R.drawable.ic_heart_red);
                    // screen like animation
                    mInstaLikeView.start();
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!mLikedByCurrentUser) {
                                // add photo_id
                                if (position == 0) {
                                    photo_id = battleModel.getPhotoi_id();
                                    fieldLike = context.getString(R.string.field_likes_i);
                                }

                                if (position == 1) {
                                    photo_id = battleModel.getPhotoii_id();
                                    fieldLike = context.getString(R.string.field_likes_ii);
                                }

                                if (position == 2) {
                                    photo_id = battleModel.getPhotoiii_id();
                                    fieldLike = context.getString(R.string.field_likes_iii);
                                }

                                if (position == 3) {
                                    photo_id = battleModel.getPhotoiv_id();
                                    fieldLike = context.getString(R.string.field_likes_iv);
                                }

                                if (position == 4) {
                                    photo_id = battleModel.getPhotov_id();
                                    fieldLike = context.getString(R.string.field_likes_v);
                                }

                                if (position == 5) {
                                    photo_id = battleModel.getPhotovi_id();
                                    fieldLike = context.getString(R.string.field_likes_vi);
                                }

                                if (position == 6) {
                                    photo_id = battleModel.getPhotovii_id();
                                    fieldLike = context.getString(R.string.field_likes_vii);
                                }

                                if (position == 7) {
                                    photo_id = battleModel.getPhotoviii_id();
                                    fieldLike = context.getString(R.string.field_likes_viii);
                                }

                                if (position == 8) {
                                    photo_id = battleModel.getPhotoix_id();
                                    fieldLike = context.getString(R.string.field_likes_ix);
                                }

                                if (position == 9) {
                                    photo_id = battleModel.getPhotox_id();
                                    fieldLike = context.getString(R.string.field_likes_x);
                                }

                                if (position == 10) {
                                    photo_id = battleModel.getPhotoxi_id();
                                    fieldLike = context.getString(R.string.field_likes_xi);
                                }

                                if (position == 11) {
                                    photo_id = battleModel.getPhotoxii_id();
                                    fieldLike = context.getString(R.string.field_likes_xii);
                                }

                                if (position == 12) {
                                    photo_id = battleModel.getPhotoxiii_id();
                                    fieldLike = context.getString(R.string.field_likes_xiii);
                                }

                                if (position == 13) {
                                    photo_id = battleModel.getPhotoxiv_id();
                                    fieldLike = context.getString(R.string.field_likes_xiv);
                                }

                                if (position == 14) {
                                    photo_id = battleModel.getPhotoxv_id();
                                    fieldLike = context.getString(R.string.field_likes_xv);
                                }

                                if (position == 15) {
                                    photo_id = battleModel.getPhotoxvi_id();
                                    fieldLike = context.getString(R.string.field_likes_xvi);
                                }

                                if (position == 16) {
                                    photo_id = battleModel.getPhotoxvii_id();
                                    fieldLike = context.getString(R.string.field_likes_xvii);
                                }

                                addNewLike(battleModel, photo_id,fieldLike);
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

        private void removeLike(BattleModel model, String photo_id, String fieldLike) {
            Log.d(TAG, "onDoubleTap: single tap detected.");
            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media))
                    .child(model.getPhotoi_id())
                    .child(context.getString(R.string.field_child_likes))
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

                            assert keyID != null;
                            myRef.child(context.getString(R.string.dbname_battle_media))
                                    .child(model.getPhotoi_id())
                                    .child(context.getString(R.string.field_child_likes))
                                    .child(photo_id)
                                    .child(fieldLike)
                                    .child(keyID)
                                    .removeValue();

                            myRef.child(context.getString(R.string.dbname_battle))
                                    .child(model.getUser_id())
                                    .child(model.getPhotoi_id())
                                    .child(context.getString(R.string.field_child_likes))
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
            if (!str.equals("0")) {
                number_of_likes.setVisibility(View.VISIBLE);
            }
            if (linLayout_count_like.getVisibility() != View.VISIBLE)
                linLayout_count_like.setVisibility(View.VISIBLE);

            number_of_likes.setText(str);

            String newLikeID = myRef.push().getKey();
            Like like = new Like();
            like.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            assert newLikeID != null;
            myRef.child(context.getString(R.string.dbname_battle_media))
                    .child(model.getPhotoi_id())
                    .child(context.getString(R.string.field_child_likes))
                    .child(photo_id)
                    .child(fieldLike)
                    .child(newLikeID)
                    .setValue(like);

            myRef.child(context.getString(R.string.dbname_battle))
                    .child(model.getUser_id())
                    .child(model.getPhotoi_id())
                    .child(context.getString(R.string.field_child_likes))
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
                    .child(context.getString(R.string.dbname_battle_media))
                    .child(model.getPhotoi_id())
                    .child(context.getString(R.string.field_child_likes))
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
                    .child(context.getString(R.string.dbname_battle_media))
                    .child(model.getPhotoi_id())
                    .child(context.getString(R.string.field_child_likes))
                    .child(photo_id)
                    .child(like_id);

            // on parcours tous les likes
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    updateUsers = new StringBuilder();

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

                                    updateUsers.append(user.getUsername());
                                    updateUsers.append(",");
                                }

                                // vérifie si c'est l'utilistateur courant a liké
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

        public void setLikes(BattleModel model, String photo_id, String fieldLike) {
            likes_count = 0;
            linLayout_count_like.setVisibility(View.GONE);
            number_of_likes.setVisibility(View.GONE);

            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media))
                    .child(model.getPhotoi_id())
                    .child(context.getString(R.string.field_child_likes))
                    .child(photo_id)
                    .child(fieldLike);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        // add user id to the list
                        liker_list.add(Objects.requireNonNull(ds.child(context.getString(R.string.field_user_id)).getValue()).toString());
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

        public void setComments(String photo_id) {
            comments_count = 0;
            number_of_comments.setVisibility(View.GONE);

            Query query = myRef
                    .child(context.getString(R.string.dbname_battle_media))
                    .child(battleModel.getPhotoi_id())
                    .child(context.getString(R.string.field_child_comments))
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
                                .child(context.getString(R.string.dbname_battle_media))
                                .child(battleModel.getPhotoi_id())
                                .child(context.getString(R.string.field_child_comments))
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
    }
}

