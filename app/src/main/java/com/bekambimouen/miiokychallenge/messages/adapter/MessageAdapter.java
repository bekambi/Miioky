package com.bekambimouen.miiokychallenge.messages.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.market_place.MarketAboutProduct;
import com.bekambimouen.miiokychallenge.messages.FullVideo;
import com.bekambimouen.miiokychallenge.messages.adapter.viewholders.MessageLeft;
import com.bekambimouen.miiokychallenge.messages.adapter.viewholders.MessageRight;
import com.bekambimouen.miiokychallenge.messages.model.Chat;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    // vars
    private final AppCompatActivity context;
    public List<Chat> mChat;
    public List<Chat> selected_usersList;
    private final RelativeLayout relLayout_view_overlay;

    private final int currentPlayingPosition;
    private int n = 0;

    // ______________ firebase
    private final DatabaseReference myRef;

    public MessageAdapter(AppCompatActivity context, List<Chat> mChat, List<Chat> selected_usersList,
                          RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.mChat = mChat;
        this.selected_usersList = selected_usersList;
        this.relLayout_view_overlay = relLayout_view_overlay;
        this.currentPlayingPosition = -1;

        this.myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.msg_item_left, parent, false);
            return new MessageLeft(view, context, currentPlayingPosition);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.msg_item_right, parent, false);
            return new MessageRight(view, context, currentPlayingPosition);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        final int itemviewType = getItemViewType(position);

        // retrieve the previous message, if exists
        Chat previousMessage = null;
        if (position > 0) {
            previousMessage = mChat.get(position - 1);
        }

        if (itemviewType == MSG_TYPE_LEFT) {
            MessageLeft msgLeft = (MessageLeft) holder;
            msgLeft.bindDate(previousMessage, chat, position);

            if (chat.getSuppressed().equals("yes")) {
                msgLeft.ll_listitem.setVisibility(View.GONE);
                msgLeft.deleted_comment.setVisibility(View.VISIBLE);
            } else {
                msgLeft.deleted_comment.setVisibility(View.GONE);
                msgLeft.ll_listitem.setVisibility(View.VISIBLE);
            }

            if (!chat.getMessage().isEmpty()) {
                msgLeft.linLayout.setVisibility(View.GONE);
                msgLeft.linLayout_simple_message.setVisibility(View.VISIBLE);
                msgLeft.linLayout2.setVisibility(View.GONE);

                msgLeft.show_message.setText(chat.getMessage());
                if (!TextUtils.isEmpty(chat.getStore_id())) {
                    msgLeft.linLayout_simple_message.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MarketAboutProduct.class);
                        intent.putExtra("store_id", chat.getStore_id());
                        intent.putExtra("photoi_id", chat.getPhotoi_id());
                        intent.putExtra("from_chat", "from_chat");
                        context.startActivity(intent);
                    });
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                String heure = sfd.format(new Date(chat.getTimeStamp()));
                msgLeft.hour.setText(heure);

            } else if (!chat.getVoiceMail().isEmpty()) {
                msgLeft.linLayout.setVisibility(View.GONE);
                msgLeft.linLayout_simple_message.setVisibility(View.GONE);

                // internet control
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    msgLeft.linLayout2.setVisibility(View.VISIBLE);

                    msgLeft.bind(chat, position);
                    // profile photo
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference.child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(chat.getSender());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Glide.with(context.getApplicationContext())
                                        .load(user.getProfileUrl())
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(msgLeft.profil_photo);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                    String heure = sfd.format(new Date(chat.getTimeStamp()));
                    msgLeft.voice_hour.setText(heure);
                }

            } else if (!chat.getMessagePhoto().isEmpty()) {
                msgLeft.linLayout.setVisibility(View.VISIBLE);
                msgLeft.linLayout_simple_message.setVisibility(View.GONE);
                msgLeft.linLayout2.setVisibility(View.GONE);

                msgLeft.relLayout_photo_simple.setVisibility(View.GONE);
                msgLeft.image.setVisibility(View.VISIBLE);
                msgLeft.showMessagePhoto.setVisibility(View.VISIBLE);
                msgLeft.hourPhoto.setVisibility(View.VISIBLE);

                msgLeft.showMessagePhoto.setText(chat.getMessagePhoto());
                if (!TextUtils.isEmpty(chat.getStore_id())) {
                    msgLeft.showMessagePhoto.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MarketAboutProduct.class);
                        intent.putExtra("store_id", chat.getStore_id());
                        intent.putExtra("photoi_id", chat.getPhotoi_id());
                        intent.putExtra("from_chat", "from_chat");
                        context.startActivity(intent);
                    });
                }

                if (chat.getVideo().equals("video")) {
                    msgLeft.img_play.setVisibility(View.VISIBLE);

                    Glide.with(context.getApplicationContext())
                            .load(chat.getThumbnail())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgLeft.image);

                } else {

                    Glide.with(context.getApplicationContext())
                            .load(chat.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgLeft.image);
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                String heure = sfd.format(new Date(chat.getTimeStamp()));
                msgLeft.hourPhoto.setText(heure);

            } else if (!chat.getPhotoSimple().isEmpty()){
                msgLeft.linLayout.setVisibility(View.VISIBLE);
                msgLeft.linLayout_simple_message.setVisibility(View.GONE);
                msgLeft.linLayout2.setVisibility(View.GONE);

                msgLeft.relLayout_photo_simple.setVisibility(View.VISIBLE);
                msgLeft.image.setVisibility(View.GONE);
                msgLeft.showMessagePhoto.setVisibility(View.GONE);
                msgLeft.hourPhoto.setVisibility(View.GONE);
                msgLeft.hourPhotoSimple.setVisibility(View.VISIBLE);

                if (chat.getVideo().equals("video")) {
                    msgLeft.img_play.setVisibility(View.VISIBLE);

                    Glide.with(context.getApplicationContext())
                            .load(chat.getThumbnail())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgLeft.imageSimple);

                } else {

                    Glide.with(context.getApplicationContext())
                            .load(chat.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgLeft.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgLeft.imageSimple);
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                String heure = sfd.format(new Date(chat.getTimeStamp()));
                msgLeft.hourPhotoSimple.setText(heure);

            } else {
                msgLeft.linLayout.setVisibility(View.GONE);
                msgLeft.linLayout_simple_message.setVisibility(View.GONE);
                msgLeft.linLayout2.setVisibility(View.GONE);
            }

            if (chat.getVideo().equals("video")) {
                msgLeft.image.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, FullVideo.class);
                    intent.putExtra("video_url", chat.getImageUrl());
                    context.startActivity(intent);
                });

                msgLeft.imageSimple.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, FullVideo.class);
                    intent.putExtra("video_url", chat.getImageUrl());
                    context.startActivity(intent);
                });

            } else {
                msgLeft.image.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                    intent.putExtra("img_url", chat.getImageUrl());
                    context.startActivity(intent);
                });

                msgLeft.imageSimple.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                    intent.putExtra("img_url", chat.getImageUrl());
                    context.startActivity(intent);
                });
            }

        } else {
            MessageRight msgRight = (MessageRight) holder;

            msgRight.bindDate(previousMessage, chat, position);

            if (chat.getSuppressed().equals("yes")) {
                msgRight.ll_listitem.setVisibility(View.GONE);
                msgRight.deleted_comment.setVisibility(View.VISIBLE);
            } else {
                msgRight.deleted_comment.setVisibility(View.GONE);
                msgRight.ll_listitem.setVisibility(View.VISIBLE);
            }

            // selected multiples items
            if(selected_usersList.contains(mChat.get(position)))
                msgRight.ll_listitem.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_selected_state));
            else
                msgRight.ll_listitem.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_normal_state));

            if (!chat.getMessage().isEmpty()) {
                msgRight.linLayout.setVisibility(View.GONE);
                msgRight.linLayout_simple_message.setVisibility(View.VISIBLE);
                msgRight.linLayout2.setVisibility(View.GONE);

                msgRight.show_message.setText(chat.getMessage());
                if (!TextUtils.isEmpty(chat.getStore_id())) {
                    msgRight.linLayout_simple_message.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MarketAboutProduct.class);
                        intent.putExtra("store_id", chat.getStore_id());
                        intent.putExtra("photoi_id", chat.getPhotoi_id());
                        intent.putExtra("from_chat", "from_chat");
                        context.startActivity(intent);
                    });
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                String heure = sfd.format(new Date(chat.getTimeStamp()));
                msgRight.hour.setText(heure);

                if (chat.isIsseen()) {
                    msgRight.done_msg_one.setImageResource(R.drawable.ic_baseline_done_all_24);
                } else {
                    msgRight.done_msg_one.setImageResource(R.drawable.done_white);

                    n++;
                    // add badge number
                    if (n == 1) {
                        String key = myRef.push().getKey();
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", chat.getReceiver());

                        assert key != null;
                        myRef.child(context.getString(R.string.dbname_badge_message_number))
                                .child(chat.getReceiver())
                                .child(key)
                                .setValue(map_number);
                    }
                }

            } else if (!chat.getVoiceMail().isEmpty()) {
                msgRight.linLayout.setVisibility(View.GONE);
                msgRight.linLayout_simple_message.setVisibility(View.GONE);

                // internet control
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    msgRight.linLayout2.setVisibility(View.VISIBLE);

                    msgRight.bind(chat, position);

                    // profile photo
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference.child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                Glide.with(context.getApplicationContext())
                                        .load(user.getProfileUrl())
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .into(msgRight.profil_photo);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                    String heure = sfd.format(new Date(chat.getTimeStamp()));
                    msgRight.voice_hour.setText(heure);

                    if (chat.isIsseen()) {
                        msgRight.done_voice_one.setImageResource(R.drawable.ic_baseline_done_all_24);
                    } else {
                        msgRight.done_voice_one.setImageResource(R.drawable.done_white);

                        n++;
                        // add badge number
                        if (n == 1) {
                            String key = myRef.push().getKey();
                            HashMap<String, Object> map_number = new HashMap<>();
                            map_number.put("user_id", chat.getReceiver());

                            assert key != null;
                            myRef.child(context.getString(R.string.dbname_badge_message_number))
                                    .child(chat.getReceiver())
                                    .child(key)
                                    .setValue(map_number);
                        }
                    }
                }

            } else if (!chat.getMessagePhoto().isEmpty()) {
                msgRight.linLayout.setVisibility(View.VISIBLE);
                msgRight.linLayout_simple_message.setVisibility(View.GONE);
                msgRight.linLayout2.setVisibility(View.GONE);

                msgRight.relLayout_photo_simple.setVisibility(View.GONE);
                msgRight.image.setVisibility(View.VISIBLE);
                msgRight.showMessagePhoto.setVisibility(View.VISIBLE);
                msgRight.lin_hourPhoto.setVisibility(View.VISIBLE);

                msgRight.showMessagePhoto.setText(chat.getMessagePhoto());
                if (!TextUtils.isEmpty(chat.getStore_id())) {
                    msgRight.showMessagePhoto.setOnClickListener(view -> {
                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MarketAboutProduct.class);
                        intent.putExtra("store_id", chat.getStore_id());
                        intent.putExtra("photoi_id", chat.getPhotoi_id());
                        intent.putExtra("from_chat", "from_chat");
                        context.startActivity(intent);
                    });
                }

                if (chat.getVideo().equals("video")) {
                    msgRight.img_play.setVisibility(View.VISIBLE);

                    Glide.with(context.getApplicationContext())
                            .load(chat.getThumbnail())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgRight.image);

                } else {

                    Glide.with(context.getApplicationContext())
                            .load(chat.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgRight.image);
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                String heure = sfd.format(new Date(chat.getTimeStamp()));
                msgRight.hourPhoto.setText(heure);

                if (chat.isIsseen()) {
                    msgRight.done_photo_one.setImageResource(R.drawable.ic_baseline_done_all_24);
                } else {
                    msgRight.done_photo_one.setImageResource(R.drawable.done_white);

                    n++;
                    // add badge number
                    if (n == 1) {
                        String key = myRef.push().getKey();
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", chat.getReceiver());

                        assert key != null;
                        myRef.child(context.getString(R.string.dbname_badge_message_number))
                                .child(chat.getReceiver())
                                .child(key)
                                .setValue(map_number);
                    }
                }

            } else if (!chat.getPhotoSimple().isEmpty()){
                msgRight.linLayout.setVisibility(View.VISIBLE);
                msgRight.linLayout_simple_message.setVisibility(View.GONE);
                msgRight.linLayout2.setVisibility(View.GONE);

                msgRight.relLayout_photo_simple.setVisibility(View.VISIBLE);
                msgRight.image.setVisibility(View.GONE);
                msgRight.showMessagePhoto.setVisibility(View.GONE);
                msgRight.lin_hourPhoto.setVisibility(View.GONE);
                msgRight.hourPhotoSimple.setVisibility(View.VISIBLE);

                if (chat.getVideo().equals("video")) {
                    msgRight.img_play.setVisibility(View.VISIBLE);

                    Glide.with(context.getApplicationContext())
                            .load(chat.getThumbnail())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgRight.imageSimple);

                } else {

                    Glide.with(context.getApplicationContext())
                            .load(chat.getImageUrl())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    msgRight.pbProgressAction.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .placeholder(R.color.white)
                            .into(msgRight.imageSimple);
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
                String heure = sfd.format(new Date(chat.getTimeStamp()));
                msgRight.hourPhotoSimple.setText(heure);

                if (chat.isIsseen()) {
                    msgRight.done_img_one.setImageResource(R.drawable.ic_baseline_done_all_24);
                } else {
                    msgRight.done_img_one.setImageResource(R.drawable.done_white);

                    n++;
                    // add badge number
                    if (n == 1) {
                        String key = myRef.push().getKey();
                        HashMap<String, Object> map_number = new HashMap<>();
                        map_number.put("user_id", chat.getReceiver());

                        assert key != null;
                        myRef.child(context.getString(R.string.dbname_badge_message_number))
                                .child(chat.getReceiver())
                                .child(key)
                                .setValue(map_number);
                    }
                }

            } else {
                msgRight.linLayout.setVisibility(View.GONE);
                msgRight.linLayout_simple_message.setVisibility(View.GONE);
                msgRight.linLayout2.setVisibility(View.GONE);
            }

            if (chat.getVideo().equals("video")) {
                msgRight.image.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, FullVideo.class);
                    intent.putExtra("video_url", chat.getImageUrl());
                    context.startActivity(intent);
                });

                msgRight.imageSimple.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, FullVideo.class);
                    intent.putExtra("video_url", chat.getImageUrl());
                    context.startActivity(intent);
                });

            } else {
                msgRight.image.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                    intent.putExtra("img_url", chat.getImageUrl());
                    context.startActivity(intent);
                });

                msgRight.imageSimple.setOnClickListener(view -> {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                    intent.putExtra("img_url", chat.getImageUrl());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MessageRight) {
            MessageRight msgRight = (MessageRight) holder;
            if (currentPlayingPosition == msgRight.getLayoutPosition()) {
                msgRight.updateNonPlayingView();
            }
        } else if (holder instanceof MessageLeft) {
            MessageLeft msgLeft = (MessageLeft) holder;
            if (currentPlayingPosition == msgLeft.getLayoutPosition()) {
                msgLeft.updateNonPlayingView();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fuser != null;
        if (mChat.get(position).getSender().equals(fuser.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        if(mChat==null) return 0;
        return mChat.size();
    }
}

