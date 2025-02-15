package com.bekambimouen.miiokychallenge.profile.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileGridBattleAdapter;
import com.bekambimouen.miiokychallenge.publication_insta.PubInstagPhoto;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BattleGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BattleGridFragment extends Fragment {

    // Permissions
    private static final String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // widgets
    private RelativeLayout parent, relLayout, relLayout_view_overlay;
    private RecyclerView recyclerView;

    // vars
    private Profile context;
    private ProfileGridBattleAdapter adapter;
    private List<BattleModel> list;
    private List<String> preload_url_list;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public BattleGridFragment() {
        // Required empty public constructor
    }

    public static BattleGridFragment newInstance() {
        BattleGridFragment fragment = new BattleGridFragment();
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
        View view = inflater.inflate(R.layout.fragment_battle_grid, container, false);
        context = (Profile) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());
        preload_url_list = new ArrayList<>();

        relLayout_view_overlay = context.getRelLayout_view_overlay();

        init(view);
        new Thread(this::getPost).start();
        return view;
    }

    private void init(View v) {
        parent = v.findViewById(R.id.parent);
        relLayout = v.findViewById(R.id.relLayout);
        recyclerView = v.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));

        TextView text = v.findViewById(R.id.text3);

        text.setOnClickListener(view -> {
            if (allPermissionsGranted()) {
                int REQUEST_CODE_CAMERA = 101;
                ActivityCompat.requestPermissions(context, CAMERA_PERMISSIONS, REQUEST_CODE_CAMERA);
                Toast.makeText(context, "permissions denied!", Toast.LENGTH_SHORT).show();
            } else {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, PubInstagPhoto.class);
                intent.putExtra("from_battle_fragment", "from_battle_fragment");
                startActivity(intent);
            }
        });
    }

    private void clearAll(){
        if(list != null){
            list.clear();
        }
        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }
        list = new ArrayList<>();
    }

    private void getPost() {
        clearAll();
        Query query = myRef
                .child(getString(R.string.dbname_battle))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel model = Util_BattleModel.getBattleModel(context, objectMap, ds);

                    if (!model.isSuppressed())
                        list.add(model);
                }

                //sort for newest to oldest
                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                // preload image
                for(int i = 0; i < list.size(); i++){
                    // get the 20 first url to preload
                    if (!TextUtils.isEmpty(list.get(i).getUrl())){
                        preload_url_list.add(list.get(i).getUrl());

                    } else if (!TextUtils.isEmpty(list.get(i).getUrlUn())) {
                        preload_url_list.add(list.get(i).getUrlUn());
                        preload_url_list.add(list.get(i).getUrlDeux());

                    } else if (!TextUtils.isEmpty(list.get(i).getReponse_url())) {
                        preload_url_list.add(list.get(i).getReponse_url());
                        preload_url_list.add(list.get(i).getInvite_url());

                    } else if (!TextUtils.isEmpty(list.get(i).getUrli())) {
                        preload_url_list.add(list.get(i).getUrli());
                        preload_url_list.add(list.get(i).getUrlii());
                        if (!TextUtils.isEmpty(list.get(i).getUrliii()))
                            preload_url_list.add(list.get(i).getUrliii());
                        if (!TextUtils.isEmpty(list.get(i).getUrliv()))
                            preload_url_list.add(list.get(i).getUrliv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlv()))
                            preload_url_list.add(list.get(i).getUrlv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlvi()))
                            preload_url_list.add(list.get(i).getUrlvi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlvii()))
                            preload_url_list.add(list.get(i).getUrlvii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlviii()))
                            preload_url_list.add(list.get(i).getUrlviii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlix()))
                            preload_url_list.add(list.get(i).getUrlix());
                        if (!TextUtils.isEmpty(list.get(i).getUrlx()))
                            preload_url_list.add(list.get(i).getUrlx());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxi()))
                            preload_url_list.add(list.get(i).getUrlxi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxii()))
                            preload_url_list.add(list.get(i).getUrlxii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxiii()))
                            preload_url_list.add(list.get(i).getUrlxiii());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxiv()))
                            preload_url_list.add(list.get(i).getUrlxiv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxv()))
                            preload_url_list.add(list.get(i).getUrlxv());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxvi()))
                            preload_url_list.add(list.get(i).getUrlxvi());
                        if (!TextUtils.isEmpty(list.get(i).getUrlxvii()))
                            preload_url_list.add(list.get(i).getUrlxvii());

                    } else if (!TextUtils.isEmpty(list.get(i).getUser_profile_photo())) {
                        preload_url_list.add(list.get(i).getUser_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_profile_photo())) {
                        preload_url_list.add(list.get(i).getGroup_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_full_profile_photo())) {
                        preload_url_list.add(list.get(i).getGroup_full_profile_photo());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_user_background_profile_url())) {
                        preload_url_list.add(list.get(i).getGroup_user_background_profile_url());

                    } else if (!TextUtils.isEmpty(list.get(i).getGroup_user_background_full_profile_url())) {
                        preload_url_list.add(list.get(i).getGroup_user_background_full_profile_url());
                    }
                }

                // preload image
                preloadAllImage(preload_url_list);

                adapter = new ProfileGridBattleAdapter(context, list, relLayout_view_overlay);
                recyclerView.setAdapter(adapter);

                context.runOnUiThread(() -> {
                    if (adapter.getItemCount() == 0) {
                        relLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void preloadAllImage(List<String> list) {
        for (String url: list) {
            preloadImages(url);
        }
    }

    private void preloadImages(String url) {
        Glide.with(context.getApplicationContext())
                .downloadOnly()
                .load(url)
                .submit(500, 500);
    }

    private boolean allPermissionsGranted() {
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

        // to make fragment wrap view
        parent.requestLayout();
    }
}