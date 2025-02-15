package com.bekambimouen.miiokychallenge.story.adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.bottomsheet.BottomSheetUpdateProfileOrStory;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.bekambimouen.miiokychallenge.story.StoryActivity;
import com.bekambimouen.miiokychallenge.story.model.Story;
import com.bekambimouen.miiokychallenge.story.publication.PublicationStories;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "StoryAdapter";
    // constants
    private static final int ICON_PHOTO = 1;
    private static final int STORY_ITEM = 2;

    // vars
    private final AppCompatActivity context;
    private final List<Story> list;

    public StoryAdapter(AppCompatActivity context, List<Story> storyList) {
        this.context = context;
        this.list = storyList;

        this.list.remove(null);
        this.list.add(0, null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ICON_PHOTO) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_main_recycler_story_icon,parent,false);
            return new PhotoIcon(view);

        } else if (viewType == STORY_ITEM) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_story_item,parent,false);
            return new StoryFeeds(view);
        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Story story = list.get(position);
        final int itemViewType = getItemViewType(position);
        if (itemViewType == ICON_PHOTO) {
            PhotoIcon photoIcon = (PhotoIcon) holder;
            photoIcon.itemView.setOnClickListener(view -> photoIcon.myStory());

        } else {
            StoryFeeds storyFeeds = (StoryFeeds) holder;
            if (story.getUser_id() != null) {
                storyFeeds.seenStory(story);
                storyFeeds.bindDate(story);

                storyFeeds.itemView.setOnClickListener(view -> {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, StoryActivity.class);
                    intent.putExtra("userid", story.getUser_id());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ICON_PHOTO;
        else
            return STORY_ITEM;
    }



    public class PhotoIcon extends RecyclerView.ViewHolder {
        // item invite challenge
        private static final int REQUEST_CODE_CAMERA = 102;
        private final String[] CAMERA_PERMISSIONS = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // widgets
        private final ImageView profile_photo;

        // firebase
        private final FirebaseMethods mFirebaseMethods;
        private final String user_id;

        public PhotoIcon(@NonNull View itemView) {
            super(itemView);
            RelativeLayout relLayout = itemView.findViewById(R.id.relLayout);
            profile_photo = itemView.findViewById(R.id.profile_photo);

            mFirebaseMethods = new FirebaseMethods(context);
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            setupFirebaseAuth();

            relLayout.setOnClickListener(v -> {
                if (!allPermissionsGranted()) {
                    ActivityCompat
                            .requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                } else {
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(user_id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                if (TextUtils.isEmpty(user.getProfileUrl())) {
                                    BottomSheetUpdateProfileOrStory bottomSheet = new BottomSheetUpdateProfileOrStory(context);
                                    bottomSheet.show(context.getSupportFragmentManager(), TAG);
                                } else {
                                    myStory();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }

        private void myStory(){
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference(context.getString(R.string.dbname_story))
                    .child(user_id);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    long timecurrent = System.currentTimeMillis();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Story story = snapshot.getValue(Story.class);
                        assert story != null;
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                            count++;
                        }
                    }

                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent;
                    if (count > 0) {
                        intent = new Intent(context, StoryActivity.class);
                        intent.putExtra("userid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                    } else {
                        intent = new Intent(context, PublicationStories.class);
                    }
                    context.startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void setProfileWidgets(UserSettings userSettings) {
            UserAccountSettings settings = userSettings.getSettings();
            Glide.with(context.getApplicationContext())
                    .load(settings.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);
        }

        private void setupFirebaseAuth() {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //retrieve user information from the database
                    setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private boolean allPermissionsGranted() {
            for (String permission: CAMERA_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    }

    public class StoryFeeds extends RecyclerView.ViewHolder {
        private final CircleImageView profile_photo;
        private final ImageView imageview;
        private final TextView username, nber_of_stories;
        private final View view_online;

        // vars
        private int n = 0;

        // firebase
        private final DatabaseReference myRef;
        private final FirebaseDatabase database;

        public StoryFeeds(@NonNull View itemView) {
            super(itemView);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            imageview = itemView.findViewById(R.id.imageview);
            username = itemView.findViewById(R.id.username);
            nber_of_stories = itemView.findViewById(R.id.nber_of_stories);
            view_online = itemView.findViewById(R.id.view_online);

            myRef = FirebaseDatabase.getInstance().getReference();
            database=FirebaseDatabase.getInstance();
        }

        public void bindDate(final Story story) {
            // verify if user is online
            database.getReference()
                    .child(context.getString(R.string.dbname_presence))
                    .child(story.getUser_id())
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
                                        if (!story.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                            view_online.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        void seenStory(Story story){
            // username and profile photo
            Glide.with(context.getApplicationContext()).
                    load(story.getMediaUrl())
                    .into(imageview);

            Query query = myRef
                    .child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(story.getUser_id());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        username.setText(user.getUsername());
                        Glide.with(context.getApplicationContext()).
                                load(user.getProfileUrl())
                                .into(profile_photo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference reference  = FirebaseDatabase.getInstance()
                    .getReference(context.getString(R.string.dbname_story))
                    .child(story.getUser_id());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long timecurrent = System.currentTimeMillis();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        Story story = ds.getValue(Story.class);
                        assert story != null;
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend())
                            if (!story.isSuppressed())
                                n++;

                        if (!snapshot.child("views")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .exists()) {
                            System.currentTimeMillis();
                            Objects.requireNonNull(snapshot.getValue(Story.class));
                        }
                    }

                    if (n > 1) {
                        nber_of_stories.setVisibility(View.VISIBLE);
                        nber_of_stories.setText(String.valueOf(n));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}

