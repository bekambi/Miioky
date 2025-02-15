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
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.fun.publication.CameraVideoFun;
import com.bekambimouen.miiokychallenge.profile.Profile;
import com.bekambimouen.miiokychallenge.profile.adapter.ProfileGridFunAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel_fun;
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
 * Use the {@link FunGridCurrentUserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunGridCurrentUserProfileFragment extends Fragment {

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
    private ProfileGridFunAdapter adapter;
    private List<BattleModel_fun> list;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public FunGridCurrentUserProfileFragment() {
        // Required empty public constructor
    }

    public static FunGridCurrentUserProfileFragment newInstance() {
        FunGridCurrentUserProfileFragment fragment = new FunGridCurrentUserProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_fun_grid_current_user_profile, container, false);
        context = (Profile) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        relLayout_view_overlay = context.getRelLayout_view_overlay();
        init(view);
        new Thread(this::getPost).start();
        return view;
    }

    private void init(View v) {
        parent = v.findViewById(R.id.parent);
        relLayout = v.findViewById(R.id.relLayout);
        TextView text = v.findViewById(R.id.text3);
        recyclerView = v.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));

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
                Intent intent = new Intent(context, CameraVideoFun.class);
                intent.putExtra("from_fun_fragment", "from_fun_fragment");
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
                .child(getString(R.string.dbname_fun))
                .child(user_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    BattleModel_fun model = Util_BattleModel_fun.getBattleModel_fun(context, objectMap, ds);

                    if (!model.isSuppressed())
                        list.add(model);
                }
                //sort for newest to oldest
                list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                        .compareTo(String.valueOf(o1.getDate_created())));

                adapter = new ProfileGridFunAdapter(context, list, relLayout_view_overlay);
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