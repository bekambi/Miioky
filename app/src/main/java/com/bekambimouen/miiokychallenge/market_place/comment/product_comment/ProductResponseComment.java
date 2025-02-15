package com.bekambimouen.miiokychallenge.market_place.comment.product_comment;

import static java.util.Objects.requireNonNull;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.interfaces.ChildItemClickListener;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.comment.product_comment.coment_response.adapter.ProductCommentResponseAdapter;
import com.bekambimouen.miiokychallenge.market_place.comment.product_comment.publication.ProductCommentPublication;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.CommentResponse;
import com.bekambimouen.miiokychallenge.model.Like;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_CommentResponse;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductResponseComment extends AppCompatActivity implements OnLoadMoreItemsListener {
    private static final String TAG = "MarketResponseComment";

    private RecyclerView recyclerView;
    private MyEditText mComment;
    private TextView answering_username;
    private ProgressBar loading_progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private ProductResponseComment context;
    private ProductCommentResponseAdapter adapter;
    private ChildItemClickListener childItemClickListener;
    private MarketModel market_model;
    private Comment commentModel;
    private String comment_key;
    private String comment;
    private String mUsername;
    private String userID;
    private String media_comment_url;
    private String media_comment_thumbnail;
    private String userName;
    private String edittext_clear_focus;
    private LinearLayoutManager layoutManager;
    private long time;
    private Date date;
    private boolean isInResponseTo = false;
    private boolean isPosting = false;
    private int resultsCount = 0, list_size = 0;
    private Handler handler;

    private ArrayList<CommentResponse> commentsResponseList, pagination;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_product_response_comment);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
            commentModel = gson.fromJson(getIntent().getStringExtra("commentModel"), Comment.class);

            comment_key = getIntent().getStringExtra("comment_key");
            comment = getIntent().getStringExtra("comment");
            userID = getIntent().getStringExtra("userID");
            media_comment_url = getIntent().getStringExtra("media_comment_url");
            media_comment_thumbnail = getIntent().getStringExtra("media_comment_thumbnail");
            time= getIntent().getLongExtra("time", 0);
            userName = getIntent().getStringExtra("userName");
            edittext_clear_focus = getIntent().getStringExtra("edittext_clear_focus");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        date = new Date();
        init();

        getResponseComments();
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        RelativeLayout icone_photo = findViewById(R.id.icone_photo);
        loading_progressBar = findViewById(R.id.loading_progressBar);
        mComment = findViewById(R.id.EditText_commentaire);
        answering_username = findViewById(R.id.answering_username);
        if (edittext_clear_focus == null)
            showKeyboard();
        // widgets
        ImageView posterComment = findViewById(R.id.posterComment);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0 || s.length() != 0) {
                    icone_photo.setVisibility(View.GONE);
                } else {
                    icone_photo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        childItemClickListener = (childPosition, username) -> {
            isInResponseTo = true;
            mUsername = username;
            mComment.setHint(getString(R.string.in_response_to)+username);
            mComment.requestFocus();
            showKeyboard();
        };

        // answer user from view comment
        if (userName != null) {
            isInResponseTo = true;
            mUsername = userName;
            mComment.setHint(getString(R.string.in_response_to)+mUsername);
        }

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    answering_username.setText(Html.fromHtml(context.getString(R.string.answer_to)+" "+user.getUsername()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

        icone_photo.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            isPosting = true;
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(context, ProductCommentPublication.class);
            intent.putExtra("isInResponseTo", isInResponseTo);
            intent.putExtra("comment_key", comment_key);
            intent.putExtra("mUsername", mUsername);
            intent.putExtra("store_id", market_model.getStore_id());
            intent.putExtra("photo_id", market_model.getPhotoi_id());
            intent.putExtra("view_response_comment", "view_response_comment");

            Gson gson = new Gson();
            String myGson = gson.toJson(commentModel);
            intent.putExtra("commentModel", myGson);
            startActivity(intent);
        });

        posterComment.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                // add comment to database
                downloadComment();
            }
        });

        arrowBack.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        });
    }

    // add comment to database
    private void downloadComment() {
        String newComment = requireNonNull(mComment.getText()).toString().trim();
        if (!TextUtils.isEmpty(newComment)) {
            if (market_model != null) {
                if (isInResponseTo) {
                    Log.d(TAG, "onClick: attempting to submit new response comment.");
                    addNewChildResponseComment(market_model, market_model.getPhotoi_id(), requireNonNull(mComment.getText()).toString(), comment_key, mUsername);

                    mComment.setText("");
                    mComment.setHint(context.getString(R.string.add_comment));
                    closeKeyboard();

                    isInResponseTo = false;

                    RecyclerView.SmoothScroller smoothScroller = new
                            LinearSmoothScroller(context.getApplicationContext()) {
                                @Override protected int getVerticalSnapPreference() {
                                    return LinearSmoothScroller.SNAP_TO_START;
                                }
                            };

                    smoothScroller.setTargetPosition(0);
                    layoutManager.startSmoothScroll(smoothScroller);

                } else {
                    Log.d(TAG, "onClick: attempting to submit new response comment.");
                    addNewResponseComment(market_model, market_model.getPhotoi_id(), requireNonNull(mComment.getText()).toString(), comment_key);

                    mComment.setText("");
                    closeKeyboard();

                    RecyclerView.SmoothScroller smoothScroller = new
                            LinearSmoothScroller(context.getApplicationContext()) {
                                @Override protected int getVerticalSnapPreference() {
                                    return LinearSmoothScroller.SNAP_TO_START;
                                }
                            };

                    smoothScroller.setTargetPosition(0);
                    layoutManager.startSmoothScroll(smoothScroller);
                }
            }
        } else
            Toast.makeText(context, context.getString(R.string.you_must_write_comment), Toast.LENGTH_SHORT).show();
    }

    // get comment _____________________________________________________________________________
    private void getResponseComments() {
        clearAll();
        try {
            Query query1 = myRef.child(context.getString(R.string.dbname_market_media))
                    .child(market_model.getPhotoi_id())
                    .child(context.getString(R.string.field_comments))
                    .child(comment_key)
                    .child(context.getString(R.string.field_comments));

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        CommentResponse commentResponse = new CommentResponse();
                        Map<Object, Object> hashMap = (HashMap<Object, Object>) ds.getValue();

                        assert hashMap != null;
                        commentResponse.setUserAnswer(Boolean.parseBoolean(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_userAnswer))).toString()));
                        commentResponse.setUser_id(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_user_id))).toString());
                        commentResponse.setComment(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_comment))).toString());
                        commentResponse.setCommentParentKey(comment_key);
                        commentResponse.setCommentKey(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_commentKey))).toString());
                        commentResponse.setUrl(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_url))).toString());
                        commentResponse.setThumbnail(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_thumbnail))).toString());
                        try {
                            commentResponse.setAnsweringUsername(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_answeringUsername))).toString());
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                        commentResponse.setDate_created(Long.parseLong(Objects.requireNonNull(hashMap.get(context.getString(R.string.field_date_created))).toString()));

                        List<Like> likeResponseList = new ArrayList<>();
                        for (DataSnapshot data : ds
                                .child(context.getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(Objects.requireNonNull(data.getValue(Like.class)).getUser_id());
                            likeResponseList.add(like);
                        }
                        commentResponse.setLikes(likeResponseList);
                        commentsResponseList.add(commentResponse);
                    }

                    // adapter
                    int iterations = commentsResponseList.size();

                    if(iterations > 20){
                        iterations = 20;
                    }

                    resultsCount = 20;
                    for(int i = 0; i < iterations; i++){
                        pagination.add(commentsResponseList.get(i));
                    }

                    list_size = commentsResponseList.size();

                    getAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (NullPointerException e) {
            Log.d(TAG, "getResponseComments: "+e.getMessage());
        }
    }

    private void getAdapter() {
        adapter = new ProductCommentResponseAdapter(context, pagination, market_model,
                comment_key, childItemClickListener, commentModel, userID, comment, media_comment_url,
                media_comment_thumbnail, market_model.getPhotoi_id(), time, mComment, loading_progressBar,
                this, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);
    }

    private void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(commentsResponseList.size() > resultsCount && !commentsResponseList.isEmpty()){

                int iterations;
                if(commentsResponseList.size() > (resultsCount + 20)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 20;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = commentsResponseList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(commentsResponseList.get(i));
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

    private void clearAll() {
        if (commentsResponseList != null)
            commentsResponseList.clear();
        if (pagination != null)
            pagination.clear();

        if (adapter != null)
            adapter = null;

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        commentsResponseList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    // add new comment response image une _________________________________________________________
    private void addNewResponseComment(MarketModel marketModel, String photo_id, String newComment, String commentKey){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                false, newComment, commentID, "", "", date.getTime(),
                user_id);

        adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_market_media))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);


            //insert into user_photos node
            myRef.child(getString(R.string.dbname_market))
                    .child(marketModel.getStore_id())
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    private void addNewChildResponseComment(MarketModel marketModel, String photo_id, String newComment, String commentKey, String username){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);
        String commentID = myRef.push().getKey();

        CommentResponse comment = Utils_Map_CommentResponse.getCommentResponse("no", "", "",
                true, newComment, commentID, "", username, date.getTime(),
                user_id);

        adapter.addComment(comment);

        try {
            //insert into photos node
            assert commentID != null;
            myRef.child(getString(R.string.dbname_market_media))
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

            //insert into user_photos node
            myRef.child(getString(R.string.dbname_market))
                    .child(marketModel.getStore_id())
                    .child(photo_id)
                    .child(getString(R.string.field_comments))
                    .child(commentKey)
                    .child(getString(R.string.field_comments))
                    .child(commentID)
                    .setValue(comment);

        } catch (NullPointerException e) {
            Log.d(TAG, "addNewComment_recycler: NullPointerException"+e.getMessage());
        }
    }

    private void showKeyboard() {
        // to show keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        mComment.requestFocus();
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);

        if (isPosting) {
            isPosting = false;

            // to avoid crash when user post and repost photo
            if (!commentsResponseList.isEmpty()) {
                commentsResponseList.clear();
                adapter.notifyDataSetChanged();
            }

            mComment.setHint(context.getString(R.string.add_comment));
            getResponseComments();

            recyclerView.scrollToPosition(list_size - 1);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        closeKeyboard();
    }
}