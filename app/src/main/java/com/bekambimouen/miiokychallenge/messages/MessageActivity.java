package com.bekambimouen.miiokychallenge.messages;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.messages.adapter.MessageAdapter;
import com.bekambimouen.miiokychallenge.messages.model.Chat;
import com.bekambimouen.miiokychallenge.messages.publication.PubPhotosChat;
import com.bekambimouen.miiokychallenge.messages.util.RecyclerItemClickListener;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.util_map.Utils_Map_Chat;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";

    // widgets
    private CircleImageView profile_photo;
    private TextView username;
    private TextView vue_a;
    private TextView receiversStatus;
    private MyEditText text_send;
    private TextView selection_count;
    private RelativeLayout relLayout_delete;
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_message, backArrow, relLayout_view_overlay;
    private RelativeLayout relLayout_first_comment;

    // firebase
    private FirebaseUser fuser;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    // vars
    private MessageActivity context;
    private MessageAdapter adapter;
    private User user;
    private List<Chat> mchat, multiselect_list;
    private MarketModel market_model;
    private String userid;
    boolean notify = false;
    boolean isSeen = false; // pour Ã©viter la prolifÃ©ration des items en fonction des msg non lus
    private boolean isMultiSelect = false;

    private String fileName;
    private MediaRecorder recorder;
    private RecordView record_view;
    private RecordButton recordButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_message);
        context = this;

        mchat = new ArrayList<>();
        multiselect_list = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        selection_count = findViewById(R.id.selection_count);
        ImageView arrowBack_delete = findViewById(R.id.arrowBack_delete);
        ImageView delete = findViewById(R.id.delete);
        relLayout_delete = findViewById(R.id.relLayout_delete);
        relLayout_first_comment = findViewById(R.id.relLayout_first_comment);
        relLayout_message = findViewById(R.id.relLayout_message);
        backArrow = findViewById(R.id.arrowBack);
        RelativeLayout parent_layout = findViewById(R.id.parent_layout);
        RelativeLayout relLayout = findViewById(R.id.relLayout);
        record_view = findViewById(R.id.record_view);
        progressBar = findViewById(R.id.progressBar);
        username = findViewById(R.id.username);
        receiversStatus = findViewById(R.id.receiversStatus);
        vue_a = findViewById(R.id.vue_a);
        text_send = findViewById(R.id.MessageActivity_text_send);
        profile_photo = findViewById(R.id.profile_photo);
        ImageView btn_send = findViewById(R.id.MessageActivity_btn_send);
        ImageView icone_photo = findViewById(R.id.icone_photo);
        ImageView phone_call = findViewById(R.id.phone_call);
        ImageView camera_video = findViewById(R.id.camera_video);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Gson gson = new Gson();
        user = gson.fromJson(getIntent().getStringExtra("to_chat"), User.class);

        // on recois ici l'ID de l'autre from friend fragment or ChatActivty
        userid = user.getUser_id();
        String userName = user.getUsername();
        receiversStatus.setText(userName);

        myRef.child(getString(R.string.dbname_presence))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            try {
                                String status = snapshot.child(getString(R.string.field_onLine)).getValue(String.class);
                                long date = snapshot.child("timeStamp").getValue(Long.class);

                                assert status != null;
                                if(!status.isEmpty()){
                                    if(status.equals(getString(R.string.field_offLine))){
                                        receiversStatus.setVisibility(View.GONE);
                                        if (date != 0) {
                                            vue_a.setVisibility(View.VISIBLE);
                                            getTimestampDifference(date);
                                        }
                                    }else{
                                        vue_a.setVisibility(View.GONE);
                                        receiversStatus.setText(status);
                                        receiversStatus.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    username.setText(user.getUsername());
                    Glide.with(context)
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo);
                    readMesagges(fuser.getUid(), userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // record__________________________________________________________________________________
        recording();
        // record__________________________________________________________________________________

        final Handler handler= new Handler();
        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0 || s.length() != 0) {
                    icone_photo.setVisibility(View.GONE);
                    parent_layout.setVisibility(View.GONE);
                    relLayout.setVisibility(View.VISIBLE);
                } else {
                    icone_photo.setVisibility(View.VISIBLE);
                    parent_layout.setVisibility(View.VISIBLE);
                    relLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.field_onLine), getString(R.string.writing));
                myRef.child(getString(R.string.dbname_presence))
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .setValue(hashMap);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }

            final Runnable userStoppedTyping= new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(getString(R.string.field_onLine), getString(R.string.in_line));
                    myRef.child(getString(R.string.dbname_presence))
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .setValue(hashMap);
                }
            };
        });

        icone_photo.setOnClickListener(v -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent= new Intent(MessageActivity.this, PubPhotosChat.class);
            intent.putExtra("userid", userid);
            startActivity(intent);
        });

        btn_send.setOnClickListener(view1 -> {
            notify = true;
            String msg = Objects.requireNonNull(text_send.getText()).toString();
            if (!TextUtils.isEmpty(msg)){
                sendMessage(fuser.getUid(), userid, msg);
            } else {
                Toast.makeText(MessageActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
            text_send.getText().clear();
        });

        username.setOnClickListener(v -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewProfile.class);
            String myJson = gson.toJson(user);
            intent.putExtra("user", myJson);
            context.startActivity(intent);
        });

        profile_photo.setOnClickListener(v -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewProfile.class);
            String myJson = gson.toJson(user);
            intent.putExtra("user", myJson);
            context.startActivity(intent);
        });

        backArrow.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isMultiSelect) {
                    isMultiSelect = false;
                    relLayout_delete.setVisibility(View.GONE);
                    backArrow.setEnabled(true);
                    multiselect_list = new ArrayList<>();
                    refreshAdapter();

                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });

        // delete items
        arrowBack_delete.setOnClickListener(view -> {
            if (isMultiSelect) {
                isMultiSelect = false;
                relLayout_delete.setVisibility(View.GONE);
                backArrow.setEnabled(true);
                multiselect_list = new ArrayList<>();
                refreshAdapter();

            }
        });
        delete.setOnClickListener(view -> {
            delete_items();
        });

        phone_call.setOnClickListener(view1 -> Toast.makeText(context, getString(R.string.coming_soon), Toast.LENGTH_LONG).show());
        camera_video.setOnClickListener(view1 -> Toast.makeText(context, getString(R.string.coming_soon), Toast.LENGTH_LONG).show());
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void recording() {
        RecordView recordView = findViewById(R.id.record_view);
        recordButton = findViewById(R.id.record_button);

        //IMPORTANT
        recordButton.setRecordView(recordView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                relLayout_message.setVisibility(View.GONE);
                record_view.setVisibility(View.VISIBLE);
                startRecording();
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");
                relLayout_message.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> record_view.setVisibility(View.GONE), 1500);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //limitReached to determine if the Record was finished when time limit reached.
                //String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");
                relLayout_message.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> record_view.setVisibility(View.GONE), 1500);

                progressBar.setVisibility(View.VISIBLE);
                recordButton.setEnabled(false);
                stopRecording();
                // send voice message
                startUploadVoiceMessage(fuser.getUid(), userid, fileName);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        // disable Sounds
        recordView.setSoundEnabled(false);
        // change the small mic icon color
        recordView.setSmallMicColor(Color.parseColor("#000000"));
        recordView.setSlideToCancelText("Supprimer");
        // change slide To Cancel Text Color
        recordView.setSlideToCancelTextColor(Color.parseColor("#000000"));
        // auto cancelling recording after timeLimit (In millis)
        recordView.setTimeLimit(60000*5);//05 mins
        // change the delete icon color
        recordView.setTrashIconColor(Color.parseColor("#000000"));

        //Handling Permissions(Optional)
        recordView.setRecordPermissionHandler(() -> {
            boolean recordPermissionAvailable = ContextCompat
                    .checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
            if (recordPermissionAvailable) {
                return true;
            }

            ActivityCompat.
                    requestPermissions(context,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            0);

            return false;

        });
    }

    private void startRecording() {
        String uuid = UUID.randomUUID().toString();
        fileName = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath() + "/" + uuid + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.d(TAG, "startRecording: "+e.getMessage());
        }

        try {
            recorder.start();
        } catch (IllegalStateException e) {
            Log.d(TAG, "startRecording: "+e.getMessage());
        }
    }
    private void stopRecording() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void sendMessage(String sender, final String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Date date=new Date();

        String chat_key = myRef.push().getKey();

        String store_id, photoi_id;
        if (market_model != null) {
            store_id = market_model.getStore_id();
            photoi_id = market_model.getPhotoi_id();
        } else {
            store_id = "";
            photoi_id = "";
        }

        HashMap<String, Object> hashMap = Utils_Map_Chat.getChat(chat_key,"", sender, receiver, message,
                "", "", false, "", "", "", date.getTime(),
                store_id, photoi_id, "no");

        // sender
        assert chat_key != null;
        reference.child(getString(R.string.dbname_chat))
                .child(chat_key)
                .setValue(hashMap);

        if (relLayout_first_comment.getVisibility() == View.VISIBLE)
            relLayout_first_comment.setVisibility(View.GONE);
    }

    private void startUploadVoiceMessage(String sender, final String receiver, String fileName){
        FilePaths filePaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Calendar calendar = Calendar.getInstance();
        Uri uri = Uri.fromFile(new File(fileName));
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_CHAT_IMAGE_STORAGE + "/"
                        + user_id + "/voice/"+ calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String url = task.getResult().toString();
                //add the new photo to 'photos' node and 'user_photos' node
                sendVoiceMessage(sender, receiver, url);
            }
        }).addOnFailureListener(e -> android.util.Log.d(TAG, "uploadNewPhotos: error: "+e.getMessage()));
    }

    private void sendVoiceMessage(String sender, final String receiver, String fileName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Date date=new Date();

        String chat_key = myRef.push().getKey();

        String store_id, photoi_id;
        if (market_model != null) {
            store_id = market_model.getStore_id();
            photoi_id = market_model.getPhotoi_id();
        } else {
            store_id = "";
            photoi_id = "";
        }

        HashMap<String, Object> hashMap = Utils_Map_Chat.getChat(chat_key,"", sender, receiver, "",
                "", fileName, false, "", "", "", date.getTime(),
                store_id, photoi_id, "no");

        assert chat_key != null;
        reference.child(getString(R.string.dbname_chat))
                .child(chat_key)
                .setValue(hashMap)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    recordButton.setEnabled(true);
                });
    }

    private void readMesagges(final String myid, final String userid){
        mchat = new ArrayList<>();

        DatabaseReference reference = myRef
                .child(getString(R.string.dbname_chat));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){

                        mchat.add(chat);
                    }

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) {
                        if (!chat.isIsseen() && !isSeen) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }

                adapter = new MessageAdapter(context, mchat, multiselect_list, relLayout_view_overlay);
                recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() == 0)
                    relLayout_first_comment.setVisibility(View.VISIBLE);

                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mchat.get(position).getSuppressed().equals("no")
                                && mchat.get(position).getSender().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                && !mchat.get(position).getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            if (isMultiSelect)
                                multi_select(position);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        if (mchat.get(position).getSuppressed().equals("no")
                                && mchat.get(position).getSender().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                && !mchat.get(position).getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            if (!isMultiSelect) {
                                multiselect_list = new ArrayList<>();
                                isMultiSelect = true;
                                backArrow.setEnabled(false);

                                if (!(relLayout_delete.getVisibility() == View.VISIBLE))
                                    relLayout_delete.setVisibility(View.VISIBLE);
                            }

                            multi_select(position);
                        }
                    }
                }));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void multi_select(int position) {
        if (relLayout_delete.getVisibility() == View.VISIBLE) {
            if (multiselect_list.contains(mchat.get(position)))
                multiselect_list.remove(mchat.get(position));
            else
                multiselect_list.add(mchat.get(position));

            if (!multiselect_list.isEmpty())
                selection_count.setText(String.valueOf(multiselect_list.size()));
            else
                selection_count.setText("");

            if (multiselect_list.isEmpty()) {
                if (relLayout_delete.getVisibility() == View.VISIBLE)  {
                    relLayout_delete.setVisibility(View.GONE);
                    backArrow.setEnabled(true);
                    isMultiSelect = false;
                }
            }

            refreshAdapter();


        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void refreshAdapter()
    {
        adapter.selected_usersList=multiselect_list;
        adapter.mChat=mchat;
        adapter.notifyDataSetChanged();
    }

    private void delete_items() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view2 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_delete_msg_chat, null);
        TextView delete_count = view2.findViewById(R.id.delete_count);
        TextView text2 = view2.findViewById(R.id.text2);
        TextView negativeButton = view2.findViewById(R.id.cancel);
        TextView positiveButton = view2.findViewById(R.id.tv_yes);
        builder.setView(view2);

        builder.setView(view2);
        Dialog dialog1 = builder.create();
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawable(inset);
        dialog1.show();

        String str = "" + multiselect_list.size();
        delete_count.setText(str);

        if (multiselect_list.size() == 1)
            text2.setText(getString(R.string.message_question1));
        else
            text2.setText(getString(R.string.message_question2));

        negativeButton.setOnClickListener(view3 -> {
            dialog1.dismiss();

            if (relLayout_delete.getVisibility() == View.VISIBLE) {
                if (isMultiSelect) {
                    isMultiSelect = false;
                    relLayout_delete.setVisibility(View.GONE);
                    backArrow.setEnabled(true);
                    multiselect_list = new ArrayList<>();
                    refreshAdapter();

                }
            }
        });

        positiveButton.setOnClickListener(view3 -> {
            deleteItems();
            dialog1.dismiss();

            if (isMultiSelect) {
                isMultiSelect = false;
                relLayout_delete.setVisibility(View.GONE);
                backArrow.setEnabled(true);
                multiselect_list = new ArrayList<>();
                refreshAdapter();

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteItems() {
        if(!multiselect_list.isEmpty())
        {
            for(int i=0;i<multiselect_list.size();i++) {
                mchat.remove(multiselect_list.get(i));
            }

            for (int i = 0; i < multiselect_list.size(); i++) {
                Query query = myRef
                        .child(getString(R.string.dbname_chat))
                        .orderByChild(context.getString(R.string.field_chat_key))
                        .equalTo(multiselect_list.get(i).getChat_key());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("suppressed", "yes");

                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_chat))
                                    .child(Objects.requireNonNull(ds.getKey()))
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            if (isMultiSelect) {
                isMultiSelect = false;
                relLayout_delete.setVisibility(View.GONE);
                backArrow.setEnabled(true);
                multiselect_list = new ArrayList<>();
                refreshAdapter();

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isSeen = true;
        if (recorder != null)
            try {
                recorder.stop();
            } catch (IllegalStateException e) {
                Log.d(TAG, "onStop: "+e.getMessage());
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

    /**
     * Returns a string representing the number of days ago the post was made
     */
    @SuppressLint("SetTextI18n")
    private void getTimestampDifference(long date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_h = new SimpleDateFormat("HH:mm");
        String heure = sfd_h.format(new Date(date));

        if (date < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            date *= 1000;
        }

        long now = System.currentTimeMillis();
        if (date > now || date <= 0) {
            return;
        }

        String tps;

        long time = (System.currentTimeMillis() - date);
        if (time >= 3*3600*24000) {
            tps = context.getString(R.string.seen_the)+" "+date_passe;
        } else if (time >= 2*3600*24000) {
            tps = context.getString(R.string.seen_there)+" "+((int)(time/(3600*24000)))+" "+context.getString(R.string.letter_j);
        } else if (time >= 24*3600000) {
            tps = context.getString(R.string.seen_yesterday_at)+" "+heure;
        } else if (time >= 2*3600000) {
            tps = context.getString(R.string.seen_there)+" "+((int)(time/(3600000)))+" "+context.getString(R.string.letter_h);
        } else if (time >= 3600000) {
            tps = context.getString(R.string.seen_an_hour_ago);
        } else if (time >= 120000) {
            tps = context.getString(R.string.seen_there)+(int)(time/60000) + " "+context.getString(R.string.minutes_ago);
        } else {
            tps = context.getString(R.string.seen_just_now);
        }
        vue_a.setText(tps);
    }
}