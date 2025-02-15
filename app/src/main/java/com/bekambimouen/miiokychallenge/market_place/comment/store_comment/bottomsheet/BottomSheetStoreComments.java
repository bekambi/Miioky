package com.bekambimouen.miiokychallenge.market_place.comment.store_comment.bottomsheet;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.interfaces.MarketCommentListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.market_place.comment.store_comment.StoreResponseComment;
import com.bekambimouen.miiokychallenge.market_place.comment.store_comment.adapter.StoreCommentAdapter;
import com.bekambimouen.miiokychallenge.market_place.comment.store_comment.publication.StoreCommentPublication;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Comment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BottomSheetStoreComments extends BottomSheetDialogFragment implements OnLoadMoreItemsListener {
    private static final String TAG = "BottomSheetComments";

    // widgets
    private RecyclerView recyclerView;
    private ImageView posterComment;
    private View line_view;
    private MyEditText mComment;
    private TextView number_of_comments;
    private RelativeLayout relLayout_first_comment, icon_photo;
    private ProgressBar loading_progressBar;

    // vars
    private final AppCompatActivity context;
    private StoreCommentAdapter adapter;
    private final MarketModel market_model;
    private final RelativeLayout relLayout_view_overlay;
    private Comment commentModel;
    private LinearLayoutManager layoutManager;
    private ArrayList<Comment> commentsList, pagination;
    private Date date;
    private MarketCommentListener marketCommentListener;

    private int resultsCount = 0, comments_count;
    private boolean isPosting = false;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public BottomSheetStoreComments(AppCompatActivity context, MarketModel market_model, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.market_model = market_model;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // to expand completely layout
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
            coordinatorLayout.getParent().requestLayout();
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onStart()
    {
        requireNonNull(requireNonNull(getDialog()).getWindow())
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomsheet_comment, null);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        number_of_comments = view.findViewById(R.id.number_of_comments);
        mComment = view.findViewById(R.id.EditText_commentaire);
        mComment.requestFocus();
        showKeyboard();
        icon_photo = view.findViewById(R.id.icon_photo);
        relLayout_first_comment = view.findViewById(R.id.relLayout_first_comment);
        loading_progressBar = view.findViewById(R.id.loading_progressBar);
        line_view = view.findViewById(R.id.line_view);
        posterComment = view.findViewById(R.id.posterComment);

        dialog.setContentView(view);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        new Handler().postDelayed(() -> {
            setComments();
            getResponseComment(market_model);

            date = new Date();
            init();
        }, 1000);
    }

    private void init() {
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0 || s.length() != 0) {
                    icon_photo.setVisibility(View.GONE);
                } else {
                    icon_photo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        icon_photo.setOnClickListener(view -> {
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, StoreCommentPublication.class);
            intent.putExtra("view_comment", "view_comment");

            Gson gson = new Gson();
            String myGson1 = gson.toJson(market_model);
            intent.putExtra("market_model", myGson1);

            String myGson = gson.toJson(commentModel);
            intent.putExtra("commentModel", myGson);
            intent.putExtra("from_bottom_sheet", "from_bottom_sheet");
            startActivity(intent);
        });

        posterComment.setOnClickListener(view -> {
            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();

            } else {
                // add comment to database
                downloadComment();
            }
        });

        marketCommentListener = (commentKey, comment, user_id, url, thumbnail, commentModel, time) -> {
            // for ccomment notification
            this.commentModel = commentModel;
            // __________________________________________________________________
            Gson gson = new Gson();
            String myGson = gson.toJson(market_model);
            String myGson2 = gson.toJson(commentModel);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, StoreResponseComment.class);
            intent.putExtra("comment_key", commentKey);
            intent.putExtra("comment", comment);
            intent.putExtra("userID", user_id);
            intent.putExtra("media_comment_url", url);
            intent.putExtra("media_comment_thumbnail", thumbnail);
            intent.putExtra("time", time);
            intent.putExtra("commentModel", myGson2);
            intent.putExtra("market_model", myGson);
            context.startActivity(intent);
        };
    }

    // add comment to database
    private void downloadComment() {
        if(!requireNonNull(mComment.getText()).toString().isEmpty()){
            Log.d(TAG, "onClick: attempting to submit new comment.");
            addNewComment(mComment.getText().toString());

            mComment.getText().clear();
            closeKeyboard();

        } else{
            Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupWidgets(MarketModel marketModel) {
        Collections.reverse(commentsList);

        int iterations = commentsList.size();

        if(iterations > 20){
            iterations = 20;
        }

        resultsCount = 20;
        for(int i = 0; i < iterations; i++){
            pagination.add(commentsList.get(i));
        }

        adapter = new StoreCommentAdapter(context, pagination, marketModel, marketCommentListener,
                loading_progressBar, this, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0)
            relLayout_first_comment.setVisibility(View.VISIBLE);
    }

    private void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(commentsList.size() > resultsCount && !commentsList.isEmpty()){

                int iterations;
                if(commentsList.size() > (resultsCount + 20)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = commentsList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(commentsList.get(i));
                }

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }

    @Override
    public void onLoadMoreItems() {
        loading_progressBar.setVisibility(View.VISIBLE);
        displayMorePhotos();
    }

    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        Comment comment = Utils_Map_Comment.getCommentResponse("no", "", "",
                newComment, commentID, date.getTime(), user_id);

        adapter.addComment(comment);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_user_stores_media))
                    .child(market_model.getStore_id())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_user_stores))
                    .child(market_model.getStore_owner())
                    .child(market_model.getStore_id())
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(context) {
                        @Override protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };

            smoothScroller.setTargetPosition(0);
            layoutManager.startSmoothScroll(smoothScroller);

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /**
     * Setup the firebase auth object
     */
    private void clearAll() {
        if (commentsList != null)
            commentsList.clear();
        if (pagination != null)
            pagination.clear();

        if (adapter != null)
            adapter = null;

        commentsList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getResponseComment(MarketModel marketModel) {
        Log.d(TAG, "onChildAdded: child added.");
        clearAll();

        Query query = myRef
                .child(getString(R.string.dbname_user_stores_media))
                .orderByChild(context.getString(R.string.field_store_id))
                .equalTo(marketModel.getStore_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot singleSnapshot :  snapshot.getChildren()){

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) singleSnapshot.getValue();

                    assert objectMap != null;
                    MarketModel model = Util_MarketModel.getMarketModel(context, objectMap, singleSnapshot);

                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()){
                        Comment comment = new Comment();
                        Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

                        assert objectHashMap != null;
                        comment.setUser_id(requireNonNull(objectHashMap.get(getString(R.string.field_user_id))).toString());
                        comment.setComment(requireNonNull(objectHashMap.get(getString(R.string.field_comment))).toString());
                        comment.setCommentKey(requireNonNull(objectHashMap.get(getString(R.string.field_commentKey))).toString());
                        comment.setDate_created(Long.parseLong(requireNonNull(objectHashMap.get(getString(R.string.field_date_created))).toString()));
                        comment.setUrl(requireNonNull(objectHashMap.get(getString(R.string.field_url))).toString());
                        comment.setThumbnail(requireNonNull(objectHashMap.get(getString(R.string.field_thumbnail))).toString());

                        List<Like> likeList = new ArrayList<>();
                        for (DataSnapshot data : dSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(requireNonNull(data.getValue(Like.class)).getUser_id());
                            likeList.add(like);
                        }
                        comment.setLikes(likeList);
                        commentsList.add(comment);
                    }

                    model.setComments(commentsList);
                    setupWidgets(marketModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (isPosting) {
            isPosting = false;

            // to avoid crash when user post and repost photo
            if (!commentsList.isEmpty()) {
                adapter.notifyDataSetChanged();
            }

            if (market_model != null) {
                getResponseComment(market_model);

                RecyclerView.SmoothScroller smoothScroller = new
                        LinearSmoothScroller(context) {
                            @Override protected int getVerticalSnapPreference() {
                                return LinearSmoothScroller.SNAP_TO_START;
                            }
                        };

                smoothScroller.setTargetPosition(0);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        }
    }

    private void setComments() {
        comments_count = 0;

        Query query = myRef
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .child(context.getString(R.string.field_comments));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String keyID = ds.getKey();
                    comments_count++;
                    assert keyID != null;
                    Query query1 = myRef
                            .child(context.getString(R.string.dbname_user_stores_media))
                            .child(market_model.getStore_id())
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
                                line_view.setVisibility(View.VISIBLE);
                                if (comments_count == 1)
                                    number_of_comments.setText(Html.fromHtml(" <font color=red>"+ comments_count +"</font> "
                                            +context.getString(R.string.comment_single)));
                                else
                                    number_of_comments.setText(Html.fromHtml(" <font color=red>"+ comments_count +"</font> "
                                            +context.getString(R.string.comments)));
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

