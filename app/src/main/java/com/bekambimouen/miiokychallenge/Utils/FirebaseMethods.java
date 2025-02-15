package com.bekambimouen.miiokychallenge.Utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge.model.ModelInvite;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.model.UserAccountSettings;
import com.bekambimouen.miiokychallenge.model.UserSettings;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private final AppCompatActivity mContext;
    private final DatabaseReference myRef;
    private final StorageReference mStorageReference;
    private String userID;

    public FirebaseMethods(AppCompatActivity context) {
        mContext = context;

        //firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        if (auth.getCurrentUser() != null)
            userID = auth.getCurrentUser().getUid();
    }

    // pour faire varier les fichers dans le storage
    public int getImageCount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos_videos))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .getChildren()){
            count++;
        }
        return count;
    }

    public void uploadNewPhoto(final String imgUrl, Bitmap bm) {
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        FilePaths filePaths = new FilePaths();

        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

        //convert image url to bitmap
        String filePath;
        Uri uri;
        if (bm != null) {
            filePath = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    bm, "image 1", null);

            uri = Uri.parse(filePath);

        } else {
            uri = Uri.parse(imgUrl);
        }

        UploadTask uploadTask = storageReference.putFile(uri);
        Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();

                //insert into 'user_account_settings' node
                setProfilePhoto(firebaseUrl.toString());
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });

    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);

        // to upadate the profile photo if the user change it
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_invite_battle))
                .child(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String keyID = dataSnapshot.getKey();

                    ModelInvite model = new ModelInvite();
                    model.setInvite_profile_photo(url);
                    assert keyID != null;
                    myRef.child(mContext.getString(R.string.dbname_invite_battle))
                            .child(userID)
                            .child(keyID)
                            .child(mContext.getString(R.string.field_invite_profile_photo))
                            .setValue(url);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child(mContext.getString(R.string.dbname_complet_profile))
                .child(userID)
                .child(mContext.getString(R.string.field_profileUrl))
                .setValue(url);
        //____________________________________________________________________________________

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_profileUrl))
                .setValue(url).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mContext.finish();
                    }
                });
    }

    /**
     * Update 'user_account_settings' node for the current user
     */
    public void updateUserAccountSettings(String username, String fullname, String status, String bio){
        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if(username != null){
            HashMap<String, Object> map_username = new HashMap<>();
            map_username.put("username", username);

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .updateChildren(map_username);

            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .updateChildren(map_username);

            myRef.child(mContext.getString(R.string.dbname_complet_profile))
                    .child(userID)
                    .updateChildren(map_username);
        }

        if(fullname != null){
            HashMap<String, Object> map_fullName = new HashMap<>();
            map_fullName.put("fullName", fullname);

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .updateChildren(map_fullName);

            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .updateChildren(map_fullName);
        }

        if(status != null){
            HashMap<String, Object> map_status = new HashMap<>();
            map_status.put("status", status);

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .updateChildren(map_status);

            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .updateChildren(map_status);
        }

        if(bio != null) {
            HashMap<String, Object> map_bio = new HashMap<>();
            map_bio.put("bio", bio);

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .updateChildren(map_bio);

            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .updateChildren(map_bio);

            myRef.child(mContext.getString(R.string.dbname_complet_profile))
                    .child(userID)
                    .updateChildren(map_bio);
        }
    }

    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_acount_Settings node
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserSettings: retrieving user account settings from firebase.");

        UserAccountSettings settings  = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // user_account_settings node
            if(Objects.equals(ds.getKey(), mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserSettings: user account settings node datasnapshot: " + ds);

                try {
                    settings.setUser_id(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getUser_id()
                    );
                    settings.setUsername(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getUsername()
                    );
                    settings.setFullName(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getFullName()
                    );
                    settings.setProfileUrl(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getProfileUrl()
                    );
                    settings.setFull_profileUrl(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getFull_profileUrl()
                    );
                    settings.setPhoto_id(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getPhoto_id()
                    );
                    settings.setBio(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getBio()
                    );
                    settings.setStatus(
                            Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getStatus()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());


                } catch (NullPointerException e) {
                    Log.d(TAG, "getUserSettings: NullPointerException: "+e.getMessage());
                }
            }

            // users node
            /*Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
            if(Objects.equals(ds.getKey(), mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: users node datasnapshot: " + ds);

                user.setUser_id(
                        Objects.requireNonNull(ds.child(userID).getValue(User.class)).getUser_id()
                );
                user.setUsername(
                        Objects.requireNonNull(ds.child(userID).getValue(User.class)).getUsername()
                );
                user.setFullName(
                        Objects.requireNonNull(ds.child(userID).getValue(User.class)).getFullName()
                );
                user.setProfileUrl(
                        Objects.requireNonNull(ds.child(userID).getValue(User.class)).getProfileUrl()
                );
                user.setFull_profileUrl(
                        Objects.requireNonNull(ds.child(userID).getValue(User.class)).getFull_profileUrl()
                );
                user.setPhoto_id(
                        Objects.requireNonNull(ds.child(userID).getValue(User.class)).getPhoto_id()
                );

                Log.d(TAG, "getUserAccountSettings: retrieved users information: " + user.toString());
            }*/
        }
        return new UserSettings(settings);
    }
}

