package com.bekambimouen.miiokychallenge.full_img_and_vid_simple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetMenuOneFile;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.csguys.viewmoretextview.ViewMoreTextView;
import com.github.chrisbanes.photoview.PhotoView;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SimpleFullImage extends AppCompatActivity {
    private static final String TAG = "SimpleFullImage";

    // widgets
    private PhotoView photoView;
    private ImageView menu_item;
    private TextView username;
    private TextView tps_publication;
    private TextView translate_comment;
    private TextView the_user;
    private ViewMoreTextView caption;
    private LinearLayout linLayout_all_moving_views;
    private RelativeLayout parent, relLayout_username, relLayout_view_overlay;
    private RelativeLayout relLayout_write_to;

    // vars
    private SimpleFullImage context;
    private ModelInvite modelInvite;
    private Comment comment;
    private CommentResponse comment_response;
    private boolean isShown = true;
    private String from_bottom_sheet;

    // firebase
    private String user_id;

    private void getBlackTheme() {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getColor(R.color.black));

        // changer a couleur des icons en blanc
        View decor = context.getWindow().getDecorView();
        decor.setSystemUiVisibility(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.black));
        setContentView(R.layout.activity_simple_full_image);
        this.context = this;
        getBlackTheme();

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        try {
            from_bottom_sheet = getIntent().getStringExtra("from_bottom_sheet");
            Gson gson = new Gson();
            modelInvite = gson.fromJson(getIntent().getStringExtra("modelInvite"), ModelInvite.class);
            comment = gson.fromJson(getIntent().getStringExtra("comment"), Comment.class);
            comment_response = gson.fromJson(getIntent().getStringExtra("comment_response"), CommentResponse.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        closeKeyboard();
        init();
        fullImage();
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        ImageView back = findViewById(R.id.back);
        // pour agrandir et rÃ©duire l'image (import graddle)
        photoView = findViewById(R.id.iv_photo);
        menu_item = findViewById(R.id.menu_item);
        caption = findViewById(R.id.caption);
        tps_publication = findViewById(R.id.tps_publication);
        translate_comment = findViewById(R.id.translate_comment);
        username = findViewById(R.id.username);
        linLayout_all_moving_views = findViewById(R.id.linLayout_all_moving_views);
        relLayout_username = findViewById(R.id.relLayout_username);
        the_user = findViewById(R.id.the_user);
        relLayout_write_to = findViewById(R.id.relLayout_write_to);

        back.setOnClickListener(v -> finish());
    }

    private void fullImage() {
        if (comment != null) {
            linLayout_all_moving_views.setVisibility(View.GONE);

            if (!context.isFinishing()) {
                Glide.with(this)
                        .load(comment.getUrl())
                        .placeholder(R.color.black)
                        .into(photoView);
            }

        } else if (comment_response != null) {
            linLayout_all_moving_views.setVisibility(View.GONE);

            if (!context.isFinishing()) {
                Glide.with(this)
                        .load(comment_response.getUrl())
                        .placeholder(R.color.black)
                        .into(photoView);
            }

        } else {

            if (modelInvite.getInvite_userID().equals(user_id))
                relLayout_write_to.setVisibility(View.GONE);

            if (!context.isFinishing()) {
                Glide.with(this)
                        .load(modelInvite.getInvite_url())
                        .placeholder(R.color.black)
                        .into(photoView);
            }

            long tv_date = modelInvite.getDate_created();
            tps_publication.setText(Html.fromHtml(context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));

            String description = modelInvite.getInvite_caption();
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
                                // Model couldnâ€™t be loaded or other internal error.
                                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                            });

            if (!modelInvite.getInvite_caption().isEmpty())
                caption.setVisibility(View.VISIBLE);

            caption.setCharText(description);

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

            String name = modelInvite.getInvite_username();
            username.setText(name);
            the_user.setText(name);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(modelInvite.getInvite_userID());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        relLayout_write_to.setOnClickListener(view -> {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);

                            Gson gson = new Gson();
                            String myGson = gson.toJson(user);
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, MessageActivity.class);
                            intent.putExtra("to_chat", myGson);
                            context.startActivity(intent);
                        });

                        relLayout_username.setOnClickListener(view -> {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    user.getUsername());
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);

                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
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

            photoView.setOnClickListener(view -> {
                if (isShown) {
                    linLayout_all_moving_views.setVisibility(View.GONE);
                    menu_item.setVisibility(View.GONE);
                    isShown = false;
                } else {
                    linLayout_all_moving_views.setVisibility(View.VISIBLE);
                    menu_item.setVisibility(View.VISIBLE);
                    isShown = true;
                }
            });

            // menu_item
            BottomSheetMenuOneFile bottomSheet = new BottomSheetMenuOneFile(context);
            menu_item.setOnClickListener(view -> {
                if (bottomSheet.isAdded())
                    return;

                Bundle args = new Bundle();
                args.putParcelable("model_invite", modelInvite);
                args.putString("miioky", "miioky");
                bottomSheet.setArguments(args);
                bottomSheet.show(context.getSupportFragmentManager(),
                        "TAG");
            });
        }
    }

    private void closeKeyboard(){
        if (from_bottom_sheet != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        } else {
            View view = context.getCurrentFocus();
            if(view != null){
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
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