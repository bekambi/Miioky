package com.bekambimouen.miiokychallenge.display_insta.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.editprofile.ChooseProfilePhoto;
import com.bekambimouen.miiokychallenge.editprofile.EditProfile;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.challenge.ViewMyChallenges;
import com.bekambimouen.miiokychallenge.challenges_view_all.GridCategories;
import com.bekambimouen.miiokychallenge.fun.FunSuggestions;
import com.bekambimouen.miiokychallenge.groups.explorer.GroupExplorer;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.publication_saved.ViewSavedPublications;
import com.bekambimouen.miiokychallenge.publication_insta.PubInstagPhoto;
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
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParametersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParametersFragment extends Fragment {
    private static final String TAG = "ParametersFragment";

    // request code et permissions pour appareil photo invite
    private static final int REQUEST_CODE_CAMERA = 102;
    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private CircleImageView profile_photo, profile_photo2;
    private TextView username;
    private RelativeLayout relLayout_view_overlay;

    // vars
    private MainActivity context;

    //firebase
    private DatabaseReference myRef;
    private String user_id;

    public ParametersFragment() {
        // Required empty public constructor
    }

    public static ParametersFragment newInstance() {
        ParametersFragment fragment = new ParametersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parameters, container, false);
        context = (MainActivity) getActivity();

        myRef=FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        init(view);

        setProfileWidgets();
        return view;
    }

    private void init(View view) {
        profile_photo = view.findViewById(R.id.profile_photo);
        profile_photo2 = view.findViewById(R.id.profile_photo2);
        username = view.findViewById(R.id.username);
        relLayout_view_overlay = view.findViewById(R.id.relLayout_view_overlay);
        RelativeLayout relayout_profile = view.findViewById(R.id.relayout_profile);
        LinearLayout linLayout_group = view.findViewById(R.id.linLayout_group);
        LinearLayout linLayout_watch_videos = view.findViewById(R.id.linLayout_watch_videos);
        LinearLayout linLayout_create_a_challenge = view.findViewById(R.id.linLayout_create_a_challenge);
        LinearLayout linLayout_make_post = view.findViewById(R.id.linLayout_make_post);
        LinearLayout linLayout_for_you = view.findViewById(R.id.linLayout_for_you);
        LinearLayout linLayout_update_profile = view.findViewById(R.id.linLayout_update_profile);
        LinearLayout linLayout_registration = view.findViewById(R.id.linLayout_registration);
        LinearLayout linLayout_see_profile = view.findViewById(R.id.linLayout_see_profile);

        // post challenge
        linLayout_create_a_challenge.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                ActivityCompat
                        .requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewMyChallenges.class);
                context.startActivity(intent);
            }
        });

        // groups
        linLayout_group.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            startActivity(new Intent(context, GroupExplorer.class));
        });

        // watch videos
        linLayout_watch_videos.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, FunSuggestions.class);
            startActivity(intent);
        });

        // post on miioky
        linLayout_make_post.setOnClickListener(view1 -> {
            if (!allPermissionsGranted()) {
                ActivityCompat
                        .requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubInstagPhoto.class);
                startActivity(intent);
            }
        });

        // for you
        linLayout_for_you.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GridCategories.class);
            startActivity(intent);
        });

        // update profile
        linLayout_update_profile.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, EditProfile.class);
            startActivity(intent);
        });

        // registration
        linLayout_registration.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, ViewSavedPublications.class);
            startActivity(intent);
        });

        // see profile
        linLayout_see_profile.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, Profile.class);
            context.startActivity(intent);
        });

        relayout_profile.setOnClickListener(view1 -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, Profile.class);
            context.startActivity(intent);
        });
    }

    public boolean allPermissionsGranted() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void setProfileWidgets() {
        Query query = myRef
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, dataSnapshot);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo);

                    Glide.with(context.getApplicationContext())
                            .load(user.getProfileUrl())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(profile_photo2);

                    username.setText(user.getUsername());

                    profile_photo.setOnClickListener(view -> {
                        if (!user.getProfileUrl().isEmpty()) {
                            if (relLayout_view_overlay != null)
                                relLayout_view_overlay.setVisibility(View.VISIBLE);
                            context.getWindow().setExitTransition(new Slide(Gravity.END));
                            context.getWindow().setEnterTransition(new Slide(Gravity.START));
                            Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                            intent.putExtra("img_url", user.getProfileUrl());
                            context.startActivity(intent);

                        } else {
                            Log.d(TAG, "onClick: changing profile photo");
                            if (allPermissionsGrented()) {
                                int REQUEST_CODE_CAMERA = 101;
                                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (relLayout_view_overlay != null)
                                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, ChooseProfilePhoto.class);
                                context.startActivity(intent);
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

    private boolean allPermissionsGrented() {
        for (String permission: CAMERA_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);
    }
}