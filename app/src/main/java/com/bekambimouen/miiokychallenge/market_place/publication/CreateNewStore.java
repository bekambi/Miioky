package com.bekambimouen.miiokychallenge.market_place.publication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.HRArrayAdapter;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.bottomsheet_gallery_folder.BottomSheetGalleryResponseChallengePhotosFolders;
import com.bekambimouen.miiokychallenge.crop_profile_image.CropImage;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateNewStore extends AppCompatActivity {
    private static final String TAG = "CreateNewShop";

    // widgets
    private RecyclerView recyclerView;
    private TextView nber_char_sequence;
    private ProgressBar progressbar;
    private MyEditText edit_store_name, edit_about_store;
    private AutoCompleteTextView edit_neighborhood;
    private RelativeLayout parent, relLayout_profile, relLayout_view_overlay, relLayout_parent;
    private RelativeLayout relLayout_store_name, relLayout_neighborhood;
    private RelativeLayout relLayout_pub_profile_photo;
    private LinearLayout linLayout_add_photo;
    private LinearLayout linLayout_nber_char_sequence;
    private ImageView camera_id;

    // vars
    private CreateNewStore context;
    private StoryGridImageAdapter adapter;
    private ArrayList<ImageModel> imageList;
    private ArrayList<String> name_list;
    private String mSelectedImage, originOfPhoto;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isScreenEnabled = true;
    private boolean isCameraVisible = true, isFirstTime = true;
    private String imgUrl, store_name, neighborhood_name, store_message;
    private String from_shop, from_restaurant, from_bakery;
    private boolean isFirstTimeStroke_neighborhood = false, isFirstTimeStroke_store_name = false;
    private boolean isStore = false, isRestaurant = false, isBakery = false;
    private int n = 0;

    private List<String> suggestion_list;
    // dialog message
    private SharedPreferences mSharedPreferences;
    private boolean isChecke = false;

    // firebase
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_create_new_store);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        imageList = new ArrayList<>();
        name_list = new ArrayList<>();
        suggestion_list = new ArrayList<>();

        try {
            from_shop = getIntent().getStringExtra("from_shop");
            from_restaurant = getIntent().getStringExtra("from_restaurant");
            from_bakery = getIntent().getStringExtra("from_bakery");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        // no show dialog box again
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        boolean pref = mSharedPreferences.getBoolean("isChecked", false);

        if (!pref) {
            dialogMessage();
        }

        init();

        relLayout_pub_profile_photo = findViewById(R.id.relLayout_pub_profile_photo);
        linLayout_add_photo = findViewById(R.id.linLayout_add_photo);
        progressbar = findViewById(R.id.progressBar);
        ImageView iv_photo = findViewById(R.id.iv_photo);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        // get all images from gallery
        getAllImages();

        iv_photo.setOnClickListener(view1 -> {
            Log.d(TAG, "onCreate: takePicture");
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            takePicture();
        });
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void dialogMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_create_store_msg, null);
        CheckBox checkBox = view1.findViewById(R.id.checkbox);
        TextView store_msg = view1.findViewById(R.id.store_msg);
        TextView compris = view1.findViewById(R.id.ok);

        builder.setView(view1);
        Dialog dialog = builder.create();
        dialog.show();

        if (from_shop != null)
            store_msg.setText(context.getString(R.string.create_store_msg));
        if (from_restaurant != null)
            store_msg.setText(context.getString(R.string.create_restaurant_msg));
        if (from_bakery != null)
            store_msg.setText(context.getString(R.string.create_bakery_msg));


        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                isChecke = true;
            }
        });

        compris.setOnClickListener(view -> {
            mSharedPreferences.edit().putBoolean("isChecked", isChecke).apply();
            dialog.dismiss();
        });
    }

    private void init() {
        parent = findViewById(R.id.parent);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout menu_item = findViewById(R.id.menu_item);
        relLayout_parent = findViewById(R.id.relLayout_parent);
        TextView toolBar_textview = findViewById(R.id.toolBar_textview);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        ImageView ivCloseShare = findViewById(R.id.ivCloseShare);
        linLayout_nber_char_sequence = findViewById(R.id.linLayout_nber_char_sequence);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        TextView title = findViewById(R.id.title);
        nber_char_sequence = findViewById(R.id.nber_char_sequence);
        relLayout_profile = findViewById(R.id.relLayout_profile);
        relLayout_store_name = findViewById(R.id.relLayout_store_name);
        relLayout_neighborhood = findViewById(R.id.relLayout_neighborhood);
        edit_store_name = findViewById(R.id.edit_store_name);
        edit_neighborhood = findViewById(R.id.edit_neighborhood);
        edit_about_store = findViewById(R.id.edit_about_store);
        camera_id = findViewById(R.id.camera_id);
        RelativeLayout relLayout_publish = findViewById(R.id.relLayout_publish);

        if (from_shop != null) {
            toolbar_title.setText(context.getString(R.string.create_a_shop));
            title.setText(context.getString(R.string.add_your_store_cover_photo));
            edit_store_name.setHint(context.getString(R.string.name_your_store));
            edit_about_store.setHint(context.getString(R.string.tell_a_little_about_your_store));
        }

        if (from_restaurant != null) {
            toolbar_title.setText(context.getString(R.string.create_a_restaurant));
            title.setText(context.getString(R.string.add_your_restaurant_cover_photo));
            edit_store_name.setHint(context.getString(R.string.name_your_restaurant));
            edit_about_store.setHint(context.getString(R.string.tell_a_little_about_your_restaurant));
        }

        if (from_bakery != null) {
            toolbar_title.setText(context.getString(R.string.create_a_bakery));
            title.setText(context.getString(R.string.add_your_bakery_cover_photo));
            edit_store_name.setHint(context.getString(R.string.name_your_bakery));
            edit_about_store.setHint(context.getString(R.string.tell_a_little_about_your_bakery));
        }

        edit_store_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTimeStroke_store_name) {
                    isFirstTimeStroke_store_name = false;
                    GradientDrawable drawable = (GradientDrawable) relLayout_store_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_neighborhood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTimeStroke_neighborhood) {
                    isFirstTimeStroke_neighborhood = false;
                    GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, getColor(R.color.black_semi_transparent2));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // autoCompleteTextView neighborhood
        final HRArrayAdapter<String> autoComplete = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        suggestion_list.clear();
                        autoComplete.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            String suggestion_neighborhood = dataSnapshot.child(context.getString(R.string.field_neighborhood_name)).getValue(String.class);

                            if (!suggestion_list.contains(suggestion_neighborhood))
                                suggestion_list.add(suggestion_neighborhood);
                        }

                        for (int i = 0; i < suggestion_list.size(); i++) {
                            autoComplete.add(suggestion_list.get(i));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edit_neighborhood.setAdapter(autoComplete);
        // number of typing charSequence before suggestion appear
        edit_neighborhood.setThreshold(1);


        edit_about_store.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int char_length = charSequence.length();
                nber_char_sequence.setText(String.valueOf(char_length));

                if (char_length == 0) {
                    linLayout_nber_char_sequence.setVisibility(View.GONE);

                } else if (char_length >= 1) {
                    linLayout_nber_char_sequence.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        relLayout_publish.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                store_name = Objects.requireNonNull(Objects.requireNonNull(edit_store_name.getText()).toString().trim());
                neighborhood_name = Objects.requireNonNull(edit_neighborhood.getText().toString().trim());
                store_message = Objects.requireNonNull(Objects.requireNonNull(edit_about_store.getText()).toString().trim());

                if (imgUrl == null) {
                    GradientDrawable drawable = (GradientDrawable) relLayout_profile.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                if (TextUtils.isEmpty(store_name)){
                    isFirstTimeStroke_store_name = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_store_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                if (TextUtils.isEmpty(neighborhood_name)){
                    isFirstTimeStroke_neighborhood = true;
                    GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood.getBackground();
                    drawable.mutate();
                    drawable.setStroke(3, Color.RED);
                    return;
                }

                // disable screen
                isScreenEnabled = false;
                progressbar.setVisibility(View.VISIBLE);

                // verify if the name already exists
                Query query = myRef
                        .child(context.getString(R.string.dbname_user_stores));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            String key = dataSnapshot.getKey();

                            assert key != null;
                            Query query1 = myRef
                                    .child(context.getString(R.string.dbname_user_stores))
                                    .child(key);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds: snapshot.getChildren()) {
                                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                        assert objectMap != null;
                                        MarketModel market_model = Util_MarketModel.getMarketModel(context, objectMap, ds);

                                        String name = market_model.getStore_name();

                                        name_list.add(name);
                                    }

                                    // verify if the name already exists
                                    if (isFirstTime) {
                                        isFirstTime = false;
                                        for (int i = 0; i < name_list.size(); i++) {
                                            n++;
                                            // to remove all caps
                                            if (name_list.get(i).equalsIgnoreCase(store_name)) {
                                                isFirstTimeStroke_store_name = true;
                                                GradientDrawable drawable = (GradientDrawable) relLayout_store_name.getBackground();
                                                drawable.mutate();
                                                drawable.setStroke(3, Color.RED);

                                                Toast.makeText(context, context.getString(R.string.name_already_exists), Toast.LENGTH_LONG).show();
                                                progressbar.setVisibility(View.GONE);
                                                return;
                                            }
                                        }

                                        if (n == name_list.size())
                                            uploadData();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        if (!snapshot.exists())
                            uploadData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        camera_id.setOnClickListener(view -> {
            if (isCameraVisible) {
                relLayout_parent.setVisibility(View.GONE);
                relLayout_pub_profile_photo.setVisibility(View.VISIBLE);
                isCameraVisible = false;
            }
        });

        menu_item.setOnClickListener(view -> {
            BottomSheetGalleryResponseChallengePhotosFolders bottomSheet2 = new BottomSheetGalleryResponseChallengePhotosFolders(context, adapter, imageList, toolBar_textview);
            // prevent two clicks
            Util.preventTwoClick(menu_item);
            try {
                if (bottomSheet2.isAdded())
                    return;
                bottomSheet2.show(context.getSupportFragmentManager(), TAG);

            } catch (NullPointerException e) {
                Log.d(TAG, "init: "+e.getMessage());
            }
        });

        ivCloseShare.setOnClickListener(v -> {
            if (!isCameraVisible) {
                relLayout_parent.setVisibility(View.VISIBLE);
                relLayout_pub_profile_photo.setVisibility(View.GONE);
                isCameraVisible = true;
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
                if (!isCameraVisible) {
                    relLayout_parent.setVisibility(View.VISIBLE);
                    relLayout_pub_profile_photo.setVisibility(View.GONE);
                    isCameraVisible = true;
                } else {
                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    finish();
                }
            }
        });
    }

    // get the lenght of image
    private String getImageSize(Uri uri){
        return Long.toString(new File(Objects.requireNonNull(uri.getPath())).length());
    }

    private void uploadData() {
        String size = getImageSize(Uri.parse(imgUrl));
        long bitmap_size = Long.parseLong(size);

        Uri uri = Uri.parse(imgUrl);

        if (originOfPhoto.equals("pickPhoto")) {
            if (bitmap_size <= 1000000)
                uploadPhotoWithoutCompress(uri);
            else
                uploadPhotoWithCompress(uri);

        } else {
            uploadPhotoWithCompress(uri);
        }
    }

    // start the image capture Intent
    public void takePicture(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Continue only if the File was successfully created;
        File photoFile = createImageFile();
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            mLauncher.launch(cameraIntent);
        }
    }

    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (mCurrentPhotoPath != null) {
                            addImage(mCurrentPhotoPath);
                        }

                    }
                }
            }
    );

    public File createImageFile() {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + dateTime + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d(TAG, "createImageFile: error: "+e.getMessage());
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                imgUrl = String.valueOf(resultUri);
                GradientDrawable drawable = (GradientDrawable) relLayout_profile.getBackground();
                drawable.mutate();
                drawable.setStroke(0, Color.WHITE);

                if (!isCameraVisible) {
                    relLayout_parent.setVisibility(View.VISIBLE);
                    relLayout_pub_profile_photo.setVisibility(View.GONE);
                    linLayout_add_photo.setVisibility(View.GONE);
                    isCameraVisible = true;

                    Glide.with(context.getApplicationContext())
                            .load(imgUrl)
                            .placeholder(R.drawable.ic_baseline_photo_camera_24)
                            .into(camera_id);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                android.util.Log.d(TAG, "onActivityResult: error: "+error);
            }
        }
    }

    // add image in selectedImageList and imageList
    @SuppressLint("NotifyDataSetChanged")
    public void addImage(String filePath) {
        if (filePath != null) {
            String path = filePath.replace("file:", "");

            ImageModel imageModel = new ImageModel();
            imageModel.setImage(path);
            imageModel.setSelected(true);
            imageList.add(0, imageModel);
            Collections.reverse(imageList);

            String takePicture = "takePicture";
            setupGridView(takePicture);
            adapter.notifyDataSetChanged();
        }
    }

    // get all images from external storage
    public void getAllImages(){
        imageList.clear();
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null,null, null);
        try {
            while (true) {
                assert cursor != null;
                if (!cursor.moveToNext()) break;
                @SuppressLint("Range") String absolutePathOfImage =
                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                ImageModel ImageModel = new ImageModel();
                ImageModel.setImage(absolutePathOfImage);
                imageList.add(ImageModel);
            }
            cursor.close();

        } catch (NullPointerException e) {
            Log.d(TAG, "getAllImages: error: "+e.getMessage());
        }
        String pickPhoto = "pickPhoto";
        setupGridView(pickPhoto);
    }

    private void setupGridView(String imageOrigine){
        //use the grid adapter to adapter the images to gridview
        Collections.reverse(imageList);
        adapter = new StoryGridImageAdapter(context, imageList, null);
        recyclerView.setAdapter(adapter);
        // the origine of photo
        originOfPhoto = imageOrigine;

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            mSelectedImage = imageList.get(0).getImage();
        } catch (ArrayIndexOutOfBoundsException e){
            android.util.Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "setupGridView: error: "+e.getMessage());
        }

        adapter.setOnItemClickListener((position, v) -> {
            android.util.Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mSelectedImage = imageList.get(position).getImage();

            Uri uri = Uri.fromFile(new File(mSelectedImage));
            CropImage.activity(uri).start(this);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    private void uploadPhotoWithoutCompress(Uri profileUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_STORE + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        UploadTask uploadTask = storageReference.putFile(Uri.parse("file://"+profileUri));
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String profileUrl = String.valueOf(firebaseUrl);

                downloadDataWithoutCompress(profileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadPhotoWithCompress(Uri profileUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_STORE + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://"+profileUri));
        } catch (IOException e) {
            Log.d(TAG, "uploadPhotoWithCompress: error: "+e.getMessage());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // here we can choose quality factor
        // in third parameter(ex. here it is 25)
        assert bmp != null;
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);

        byte[] fileInBytes = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(fileInBytes);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful())
                throw Objects.requireNonNull(task.getException());

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Uri firebaseUrl = task.getResult();
                String profileUrl = String.valueOf(firebaseUrl);

                downloadDataWithoutCompress(profileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadDataWithoutCompress(String profileUrl) {
        //get the profile town name
        Query query = myRef
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

                    String newStoreKey = myRef.push().getKey();
                    Date date=new Date();

                    // determine if it is store or restaurant
                    if (from_shop != null)
                        isStore = true;
                    if (from_restaurant != null)
                        isRestaurant = true;
                    if (from_bakery != null)
                        isBakery = true;

                    MarketModel model = new MarketModel();

                    model.setStore(isStore);
                    model.setStore_product(false);
                    model.setRestaurant(isRestaurant);
                    model.setBakery(isBakery);
                    model.setRecyclerItem(false);
                    model.setImageUne(false);
                    model.setSell(false);
                    model.setLocation(false);

                    model.setStore_id(newStoreKey);
                    model.setStore_name(store_name);
                    model.setStore_owner(user_id);
                    model.setStore_message(store_message);
                    model.setRestaurant_menu("");
                    model.setStore_profile_photo_id(newStoreKey);
                    model.setLive_country_name(GetCountryName());
                    model.setTown_name(user.getTown_name());
                    model.setNeighborhood_name(neighborhood_name);
                    model.setProfile_photo(profileUrl);
                    model.setFull_photo("");
                    model.setDontSuggestAnymore("");
                    model.setUser_id(user_id);
                    // market
                    model.setProduct_name("");
                    model.setProduct_category("");
                    model.setLocation_category("");
                    model.setOld_price("");
                    model.setPrice("");
                    model.setDevise("");
                    model.setTags("");
                    model.setProduct_state("");
                    model.setProduct_description("");
                    model.setLocation_period("");
                    model.setDate_created(date.getTime());
                    model.setViews(0);
                    model.setMain_photo("");
                    model.setMain_photo_id("");

                    model.setUrli("");
                    model.setUrlii("");
                    model.setUrliii("");
                    model.setUrliv("");
                    model.setUrlv("");
                    model.setUrlvi("");
                    model.setUrlvii("");
                    model.setUrlviii("");
                    model.setUrlix("");
                    model.setUrlx("");
                    model.setUrlxi("");
                    model.setUrlxii("");
                    model.setUrlxiii("");
                    model.setUrlxiv("");
                    model.setUrlxv("");
                    model.setUrlxvi("");
                    model.setUrlxvii("");

                    model.setPhotoi_id("");
                    model.setPhotoii_id("");
                    model.setPhotoiii_id("");
                    model.setPhotoiv_id("");
                    model.setPhotov_id("");
                    model.setPhotovi_id("");
                    model.setPhotovii_id("");
                    model.setPhotoviii_id("");
                    model.setPhotoix_id("");
                    model.setPhotox_id("");
                    model.setPhotoxi_id("");
                    model.setPhotoxii_id("");
                    model.setPhotoxiii_id("");
                    model.setPhotoxiv_id("");
                    model.setPhotoxv_id("");
                    model.setPhotoxvi_id("");
                    model.setPhotoxvii_id("");

                    // suppress product
                    model.setSuppressed(false);

                    assert newStoreKey != null;
                    myRef.child(context.getString(R.string.dbname_user_stores))
                            .child(user_id)
                            .child(newStoreKey)
                            .setValue(model);

                    myRef.child(context.getString(R.string.dbname_user_stores_media))
                            .child(newStoreKey)
                            .setValue(model).addOnCompleteListener(task -> {
                                context.getWindow().setExitTransition(new Slide(Gravity.END));
                                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("from_market", "from_market");
                                intent.putExtra("from_publish", "from_publish");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // country name corresponding to country name
    public String GetCountryName(){
        String CountryID;
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryNames);
        for (String s : rl) {
            String[] g = s.split(getString(R.string.coma));
            if (g[0].trim().equals(CountryID.trim())) {
                CountryZipCode = g[1];
                break;
            }
        }
        return CountryZipCode;
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