package com.bekambimouen.miiokychallenge.search.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.GridSpacingItemDecoration;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bekambimouen.miiokychallenge.search.adapter.ProfileGridViewBattleAdapter;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
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

public class BattleGridProfileFragment extends Fragment {
    private static final String TAG = "BattleGridProfileFragment";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout parent, relLayout_private_account, relLayout, relLayout_view_overlay;

    // vars
    private ViewProfile context;
    private ProfileGridViewBattleAdapter adapter;
    private User user, mUser;
    private List<BattleModel> list;
    private Handler handler;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public BattleGridProfileFragment() {
        // Required empty public constructor
    }

    public static BattleGridProfileFragment newInstance() {
        BattleGridProfileFragment fragment = new BattleGridProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_battle_grid_profile, container, false);
        context = (ViewProfile) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        try {
            user = context.getUser();
            relLayout_view_overlay = context.getRelLayout_view_overlay();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: "+e.getMessage());
        }

        init(view);

        if (user != null) {
            mUser = user;
            getAccountScope(mUser);
            new Thread(this::getPost).start();
        }

        return view;
    }

    private void init(View view) {
        relLayout = view.findViewById(R.id.relLayout);
        parent = view.findViewById(R.id.parent);
        relLayout_private_account = view.findViewById(R.id.relLayout_private_account);
        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 1, false));
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
                .child(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
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

                adapter = new ProfileGridViewBattleAdapter(context, list, mUser, relLayout_view_overlay);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

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

    // verify if account is private _______________________________________
    private void getAccountScope(User user) {
        if (user.getScope().equals(context.getString(R.string.title_private))) {
            // verify is users are friends
            Query myQuery = myRef
                    .child(context.getString(R.string.dbname_friend_follower))
                    .child(user.getUser_id())
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(user_id);

            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+ds.getKey());
                        // users are  friend
                        relLayout_private_account.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    if (!snapshot.exists()) {
                        // verify if user is follower
                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.dbname_followers))
                                .child(user.getUser_id())
                                .orderByChild(context.getString(R.string.field_user_id))
                                .equalTo(user_id);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: "+ds.getKey());
                                    // users is followed
                                    relLayout_private_account.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }

                                if (!snapshot.exists()) {
                                    relLayout_private_account.setVisibility(View.VISIBLE);
                                    relLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (user.getScope().equals(context.getString(R.string.title_public))) {
            // public account
            relLayout_private_account.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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