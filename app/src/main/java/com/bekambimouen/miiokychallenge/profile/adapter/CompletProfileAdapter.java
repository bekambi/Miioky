package com.bekambimouen.miiokychallenge.profile.adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FirebaseMethods;
import com.bekambimouen.miiokychallenge.editprofile.ChooseProfilePhoto;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.bekambimouen.miiokychallenge.profile.CompletBioAndUsername;
import com.bekambimouen.miiokychallenge.profile.model.CompletProfilModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompletProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CompletProfileAdapter";
    // constant
    private static final int ADD_PROFILE = 1;
    private static final int ADD_BIO = 2;
    private static final int UPDATE_USERNAME = 3;

    private final AppCompatActivity context;
    private final RelativeLayout relLayout_view_overlay;

    public CompletProfileAdapter(AppCompatActivity context, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ADD_PROFILE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_complet_profile_photo_item, parent, false);
            return new AddProfilePhoto(view);
        } else if (viewType == ADD_BIO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_complet_bio_item, parent, false);
            return new AddBio(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_complet_displayname_item, parent, false);
            return new UpdateUsername(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ADD_PROFILE;
        else if (position == 1)
            return ADD_BIO;
        else
            return UPDATE_USERNAME;
    }

    public class AddProfilePhoto extends RecyclerView.ViewHolder {
        RelativeLayout relLayout1;
        CircleImageView profile_photo;
        TextView text1;
        TextView tv_add_photo;

        // Permissions
        private final String[] CAMERA_PERMISSIONS = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        //firebase
        private final FirebaseMethods mFirebaseMethods;

        public AddProfilePhoto(@NonNull View itemView) {
            super(itemView);

            relLayout1 = itemView.findViewById(R.id.relLayout1);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            text1 = itemView.findViewById(R.id.text1);
            tv_add_photo = itemView.findViewById(R.id.tv_add_photo);

            tv_add_photo.setOnClickListener(view -> {
                if (!allPermissionsGrented()) {
                    int REQUEST_CODE_CAMERA = 101;
                    ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                    Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
                } else {
                    if (relLayout_view_overlay != null)
                        relLayout_view_overlay.setVisibility(View.VISIBLE);
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ChooseProfilePhoto.class);
                                Gson gson = new Gson();
                                String myGson = gson.toJson(user);
                                intent.putExtra("user", myGson);

                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "onCancelled: query cancelled.");
                        }
                    });
                }
            });

            mFirebaseMethods = new FirebaseMethods(context);
            setupFirebaseAuth();
        }

        private void setProfileWidgets(UserSettings userSettings) {
            UserAccountSettings settings = userSettings.getSettings();

            Glide.with(context.getApplicationContext())
                    .load(settings.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);
        }

        private boolean allPermissionsGrented() {
            for (String permission: CAMERA_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    /*
    ------------------------------------ Firebase ---------------------------------------------
     */
        /**
         * Setup the firebase auth object
         */
        private void setupFirebaseAuth(){
            Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference();

            assert user != null;
            Query query = myRef.child(context.getString(R.string.dbname_complet_profile))
                    .child(user.getUid());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String profilUrl = Objects.requireNonNull(snapshot.getValue(CompletProfilModel.class)).getProfileUrl();
                        if (!TextUtils.isEmpty(profilUrl) && profilUrl != null) {
                            relLayout1.setVisibility(View.VISIBLE);
                            text1.setText(context.getString(R.string.title_update_profile_photo));
                            tv_add_photo.setText(context.getString(R.string.update));
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
    }

    public class AddBio extends RecyclerView.ViewHolder {
        RelativeLayout relLayout1;
        TextView text1;
        TextView tv_add_bio;
        public AddBio(@NonNull View itemView) {
            super(itemView);
            relLayout1 = itemView.findViewById(R.id.relLayout1);
            text1 = itemView.findViewById(R.id.text1);
            tv_add_bio = itemView.findViewById(R.id.tv_add_bio);

            setupFirebaseAuth();

            tv_add_bio.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CompletBioAndUsername.class);
                intent.putExtra("bio", "bio");
                context.startActivity(intent);
            });
        }
    /*
    ------------------------------------ Firebase ---------------------------------------------
     */
        /**
         * Setup the firebase auth object
         */
        private void setupFirebaseAuth(){
            Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference();
            assert user != null;
            Query query = myRef.child(context.getString(R.string.dbname_complet_profile))
                    .child(user.getUid());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String bio = Objects.requireNonNull(snapshot.getValue(CompletProfilModel.class)).getBio();
                        if (!TextUtils.isEmpty(bio) && bio != null) {
                            relLayout1.setVisibility(View.VISIBLE);
                            text1.setText(context.getString(R.string.title_update_bio));
                            tv_add_bio.setText(context.getString(R.string.update));
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public class UpdateUsername extends RecyclerView.ViewHolder {
        RelativeLayout relLayout1;
        CircleImageView profile_photo;
        TextView tv_update_displayname;

        //firebase
        private final FirebaseMethods mFirebaseMethods;

        public UpdateUsername(@NonNull View itemView) {
            super(itemView);
            relLayout1 = itemView.findViewById(R.id.relLayout1);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            tv_update_displayname = itemView.findViewById(R.id.tv_update_displayname);

            mFirebaseMethods = new FirebaseMethods(context);
            setupFirebaseAuth();

            tv_update_displayname.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CompletBioAndUsername.class);
                intent.putExtra("username", "username");
                context.startActivity(intent);
            });
        }

        private void setProfileWidgets(UserSettings userSettings) {
            UserAccountSettings settings = userSettings.getSettings();

            Glide.with(context.getApplicationContext())
                    .load(settings.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);
        }
    /*
    ------------------------------------ Firebase ---------------------------------------------
     */
        /**
         * Setup the firebase auth object
         */
        private void setupFirebaseAuth(){
            Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference();

            assert user != null;
            Query query = myRef.child(context.getString(R.string.dbname_complet_profile))
                    .child(user.getUid());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String username = Objects.requireNonNull(snapshot.getValue(CompletProfilModel.class)).getUsername();
                        if (!TextUtils.isEmpty(username) && username != null) {
                            relLayout1.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
    }
}

