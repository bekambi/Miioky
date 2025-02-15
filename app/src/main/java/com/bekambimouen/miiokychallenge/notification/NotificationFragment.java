package com.bekambimouen.miiokychallenge.notification;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.notification.adapter.NotificationAdapter;
import com.bekambimouen.miiokychallenge.notification.model.NotificationModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_NotificationModel;
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

public class NotificationFragment extends Fragment implements OnLoadMoreItemsListener {
    private static final String TAG = "NotificationFragment";

    // widgets
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private MainActivity context;
    private NotificationAdapter adapter;
    private List<NotificationModel> list, pagination;
    private Handler handler;
    private int resultsCount = 0;
    private boolean isFirstLoaded = true;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        relLayout_view_overlay = view.findViewById(R.id.relLayout_view_overlay);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(list != null){
            list.clear();
        }

        if(pagination != null){
            pagination.clear();
        }

        if(adapter != null){
            adapter = null;
        }

        if(recyclerView != null){
            handler.post(() -> recyclerView.setAdapter(null));
        }

        list = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getNotification() {
        clearAll();
        Query query = myRef.child(context.getString(R.string.dbname_notification))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    assert objectMap != null;
                    NotificationModel notification = Util_NotificationModel.getNotificationModel(context, objectMap, dataSnapshot);

                    list.add(notification);
                }

                display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void display() {
        list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                .compareTo(String.valueOf(o1.getDate_created())));

        int iterations = list.size();

        if(iterations > 10){
            iterations = 10;
        }

        resultsCount = 10;
        for(int i = 0; i < iterations; i++){
            pagination.add(list.get(i));
        }

        adapter = new NotificationAdapter(context, pagination, this, progressBar, relLayout_view_overlay);
        recyclerView.setAdapter(adapter);
    }

    public void displayMore() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(list.size() > resultsCount && !list.isEmpty()){

                int iterations;
                if(list.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = list.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(list.get(i));
                }

                resultsCount = resultsCount + iterations;
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }

    @Override
    public void onLoadMoreItems() {
        displayMore();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            getNotification();
        }
    }
}