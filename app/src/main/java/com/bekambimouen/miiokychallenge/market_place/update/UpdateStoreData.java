package com.bekambimouen.miiokychallenge.market_place.update;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.FilePaths;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.Utils.HRArrayAdapter;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.crop_profile_image.CropImage;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.model.ImageModel;
import com.bekambimouen.miiokychallenge.story.publication.adapter.StoryGridImageAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_MarketModel;
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
import com.google.gson.Gson;

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

public class UpdateStoreData extends AppCompatActivity {
    private static final String TAG = "UpdateStoreData";

    // widgets
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private MyEditText edit_store_name, edit_about_store;
    private AutoCompleteTextView edit_neighborhood;
    private RelativeLayout parent, relLayout_profile;
    private RelativeLayout relLayout_store_name, relLayout_neighborhood;
    private RelativeLayout relLayout_pub_profile_photo;
    private LinearLayout linLayout_update_photo;
    private ImageView camera_id;

    // vars
    private UpdateStoreData context;
    private StoryGridImageAdapter adapter;
    private MarketModel market_model;
    private ArrayList<ImageModel> imageList;
    private ArrayList<String> name_list;
    private String mSelectedImage, originOfPhoto, imgUrl;
    private final String[] projection = { MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
    private String mCurrentPhotoPath;
    private File image;
    private boolean isCameraVisible = true, isFirstTime = true;
    private boolean isFirstTime_stroke_store_name = false, isFirstTime_stroke_neighborhood = false;
    private String store_name, neighborhood_name, store_message;
    private int n = 0;

    private List<String> suggestion_list;
    private List<String> product_photos_id_list;

    // firebase
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_update_store_data);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        imageList = new ArrayList<>();
        name_list = new ArrayList<>();
        suggestion_list = new ArrayList<>();
        product_photos_id_list = new ArrayList<>();

        try {
            Gson gson = new Gson();
            market_model = gson.fromJson(getIntent().getStringExtra("market_model"), MarketModel.class);
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: error: "+e.getMessage());
        }

        init();
        relLayout_pub_profile_photo = findViewById(R.id.relLayout_pub_profile_photo);
        linLayout_update_photo = findViewById(R.id.linLayout_update_photo);
        progressbar = findViewById(R.id.progressbar);
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

    private void init() {
        parent = findViewById(R.id.parent);
        ImageView ivCloseShare = findViewById(R.id.ivCloseShare);
        ImageView close = findViewById(R.id.close);
        relLayout_profile = findViewById(R.id.relLayout_profile);
        relLayout_store_name = findViewById(R.id.relLayout_store_name);
        relLayout_neighborhood = findViewById(R.id.relLayout_neighborhood);
        RelativeLayout relLayout_update = findViewById(R.id.relLayout_update);
        edit_store_name = findViewById(R.id.edit_store_name);
        edit_neighborhood = findViewById(R.id.edit_neighborhood);
        edit_about_store = findViewById(R.id.edit_about_store);
        camera_id = findViewById(R.id.camera_id);

        camera_id.setOnClickListener(view -> {
            if (isCameraVisible) {
                relLayout_pub_profile_photo.setVisibility(View.VISIBLE);
                isCameraVisible = false;
            }
        });

        if (market_model != null) {
            edit_store_name.setText(market_model.getStore_name());
            edit_neighborhood.setText(market_model.getNeighborhood_name());
            edit_about_store.setText(market_model.getStore_message());

            Glide.with(context.getApplicationContext())
                    .load(market_model.getProfile_photo())
                    .into(camera_id);
        }

        // autoCompleteTextView neighborhood
        final HRArrayAdapter<String> autoComplete = new HRArrayAdapter<>(context, R.layout.custom_arrayadaper_layout, R.id.suggestion_item);

        myRef.child(context.getString(R.string.dbname_users))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Basically, this say "for each DataSnapshot *Data* in dataSnapshot, do what's inside the method
                        suggestion_list.clear();
                        autoComplete.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            // get the suggestion by childing the key of the string you want to get.
                            String suggestion_neighborhood = dataSnapshot.child(context.getString(R.string.field_neighborhood_name)).getValue(String.class);
                            // add the retrieving String to the list
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

        edit_store_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFirstTime_stroke_store_name) {
                    isFirstTime_stroke_store_name = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_store_name.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
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
                if (isFirstTime_stroke_neighborhood) {
                    isFirstTime_stroke_neighborhood = false;

                    GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        relLayout_update.setOnClickListener(view -> {
            // internet control
            boolean isConnected = CheckInternetStatus.internetIsConnected(context, parent);

            if (!isConnected) {
                CheckInternetStatus.internetIsConnected(context, parent);

            } else {
                progressbar.setVisibility(View.VISIBLE);
                if (market_model != null) {

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
                                                    isFirstTime_stroke_store_name = true;
                                                    GradientDrawable drawable = (GradientDrawable) relLayout_store_name.getBackground();
                                                    drawable.mutate();
                                                    drawable.setStroke(3, Color.RED);

                                                    Toast.makeText(context, context.getString(R.string.name_already_exists), Toast.LENGTH_LONG).show();
                                                    progressbar.setVisibility(View.GONE);
                                                    return;
                                                }
                                            }

                                            if (n == name_list.size())
                                                updateData();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            if (!snapshot.exists())
                                updateData();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        ivCloseShare.setOnClickListener(view -> {
            if (!isCameraVisible) {
                relLayout_pub_profile_photo.setVisibility(View.GONE);
                linLayout_update_photo.setVisibility(View.VISIBLE);
                isCameraVisible = true;
            }
        });

        close.setOnClickListener(view -> {
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isCameraVisible) {
                    relLayout_pub_profile_photo.setVisibility(View.GONE);
                    linLayout_update_photo.setVisibility(View.VISIBLE);
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

    private void updateData() {
        closeKeyboard();
        store_name = Objects.requireNonNull(edit_store_name.getText()).toString().trim();
        neighborhood_name = Objects.requireNonNull(edit_neighborhood.getText().toString().trim());
        store_message = Objects.requireNonNull(edit_about_store.getText()).toString();

        if (TextUtils.isEmpty(store_name)){
            isFirstTime_stroke_store_name = true;
            GradientDrawable drawable = (GradientDrawable) relLayout_store_name.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            return;
        }

        if (TextUtils.isEmpty(neighborhood_name)){
            isFirstTime_stroke_neighborhood = true;
            GradientDrawable drawable = (GradientDrawable) relLayout_neighborhood.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            return;
        }

        if (TextUtils.isEmpty(mSelectedImage)) {
            updateWithoutPhoto();
        } else {
            String size = getImageSize(Uri.parse(imgUrl));
            long bitmap_size = Long.parseLong(size);

            Uri uri = Uri.parse(imgUrl);

            if (originOfPhoto.equals("pickPhoto")) {
                if (bitmap_size <= 1000000)
                    updatePhotoWithoutCompress(uri);
                else
                    updatePhotoWithCompress(uri);

            } else {
                updatePhotoWithCompress(uri);
            }
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
        Cursor cursor = context.getContentResolver()
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
        mSelectedImage = "";

        adapter.setOnItemClickListener((position, v) -> {
            android.util.Log.d(TAG, "onItemClick: selected an image: " + imageList.get(position).getImage());
            originOfPhoto = "pickPhoto";
            mSelectedImage = imageList.get(position).getImage();

            Uri uri = Uri.fromFile(new File(mSelectedImage));
            CropImage.activity(uri).start(context);
        });
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
                    relLayout_pub_profile_photo.setVisibility(View.GONE);
                    linLayout_update_photo.setVisibility(View.VISIBLE);
                    isCameraVisible = true;

                    Glide.with(context.getApplicationContext())
                            .load(imgUrl)
                            .into(camera_id);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                android.util.Log.d(TAG, "onActivityResult: error: "+error);
            }
        }
    }

    private void updatePhotoWithoutCompress(Uri profileUri) {
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

                updateProfileUrlWithoutCompress(profileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void updatePhotoWithCompress(Uri profileUri) {
        FilePaths filePaths = new FilePaths();
        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE_STORE + "/" + user_id  + "/profile/" + "/photo"+ calendar.getTimeInMillis());

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://"+profileUri));
        } catch (IOException e) {
            Log.d(TAG, "updatePhotoWithCompress: error: "+e.getMessage());
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

                updateProfileUrlWithCompress(profileUrl);

            } else {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            android.util.Log.d(TAG, "onFailure: Photo upload failed.");
            Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileUrlWithoutCompress(String profileUrl) {
        Query query = myRef
                .child(context.getString(R.string.dbname_market))
                .child(market_model.getStore_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String key = ds.getKey();
                    product_photos_id_list.add(key);
                }

                // update the product neighborhood_name
                HashMap<String, Object> map_product_store = new HashMap<>();
                map_product_store.put("neighborhood_name", neighborhood_name);
                for (int i = 0; i < product_photos_id_list.size(); i++) {

                    myRef.child(getString(R.string.dbname_market))
                            .child(market_model.getStore_id())
                            .child(product_photos_id_list.get(i))
                            .updateChildren(map_product_store);

                    myRef.child(getString(R.string.dbname_market_media))
                            .child(product_photos_id_list.get(i))
                            .updateChildren(map_product_store);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HashMap<String, Object> map = new HashMap<>();
        if (!profileUrl.equals(market_model.getProfile_photo()))
            map.put("profile_photo", profileUrl);
        map.put("neighborhood_name", neighborhood_name);
        if (!store_name.equals(market_model.getStore_name()))
            map.put("store_name", store_name);
        if (!store_message.equals(market_model.getStore_message()))
            map.put("store_message", store_message);

        myRef.child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .updateChildren(map);

        myRef.child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .child(market_model.getStore_id())
                .updateChildren(map).addOnSuccessListener(unused -> {

                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from_market", "from_market");
                    startActivity(intent);
                    finish();
                });
    }

    private void updateProfileUrlWithCompress(String profileUrl) {
        Query query = myRef
                .child(context.getString(R.string.dbname_market))
                .child(market_model.getStore_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String key = ds.getKey();
                    product_photos_id_list.add(key);
                }

                // update the product neighborhood_name
                HashMap<String, Object> map_product_store = new HashMap<>();
                map_product_store.put("neighborhood_name", neighborhood_name);
                for (int i = 0; i < product_photos_id_list.size(); i++) {

                    myRef.child(getString(R.string.dbname_market))
                            .child(market_model.getStore_id())
                            .child(product_photos_id_list.get(i))
                            .updateChildren(map_product_store);

                    myRef.child(getString(R.string.dbname_market_media))
                            .child(product_photos_id_list.get(i))
                            .updateChildren(map_product_store);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HashMap<String, Object> map = new HashMap<>();
        if (!profileUrl.equals(market_model.getProfile_photo()))
            map.put("profile_photo", profileUrl);
        map.put("neighborhood_name", neighborhood_name);
        if (!store_name.equals(market_model.getStore_name()))
            map.put("store_name", store_name);
        if (!store_message.equals(market_model.getStore_message()))
            map.put("store_message", store_message);

        myRef.child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .updateChildren(map);

        myRef.child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .child(market_model.getStore_id())
                .updateChildren(map).addOnSuccessListener(unused -> {

                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from_market", "from_market");
                    startActivity(intent);
                    finish();
                });
    }

    private void updateWithoutPhoto() {
        Query query = myRef
                .child(context.getString(R.string.dbname_market))
                        .child(market_model.getStore_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String key = ds.getKey();
                    product_photos_id_list.add(key);
                }

                // update the product neighborhood_name
                HashMap<String, Object> map_product_store = new HashMap<>();
                map_product_store.put("neighborhood_name", neighborhood_name);
                for (int i = 0; i < product_photos_id_list.size(); i++) {

                    myRef.child(getString(R.string.dbname_market))
                            .child(market_model.getStore_id())
                            .child(product_photos_id_list.get(i))
                            .updateChildren(map_product_store);

                    myRef.child(getString(R.string.dbname_market_media))
                            .child(product_photos_id_list.get(i))
                            .updateChildren(map_product_store);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("neighborhood_name", neighborhood_name);
        if (!store_name.equals(market_model.getStore_name()))
            map.put("store_name", store_name);
        if (!store_message.equals(market_model.getStore_message()))
            map.put("store_message", store_message);

        myRef.child(context.getString(R.string.dbname_user_stores_media))
                .child(market_model.getStore_id())
                .updateChildren(map);

        myRef.child(context.getString(R.string.dbname_user_stores))
                .child(market_model.getStore_owner())
                .child(market_model.getStore_id())
                .updateChildren(map).addOnSuccessListener(unused -> {

                    context.getWindow().setExitTransition(new Slide(Gravity.END));
                    context.getWindow().setEnterTransition(new Slide(Gravity.START));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from_market", "from_market");
                    startActivity(intent);
                    finish();
                });
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}