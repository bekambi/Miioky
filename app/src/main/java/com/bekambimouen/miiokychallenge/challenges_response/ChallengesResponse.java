package com.bekambimouen.miiokychallenge.challenges_response;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenges_response.adapter.ChallengesResponseAdapter;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_BattleModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChallengesResponse extends AppCompatActivity {
    private static final String TAG = "HasRespondedChallenge";

    // widgets
    private TextView toolbar_msg;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout parent, relLayout_view_overlay;

    // vars
    private ChallengesResponse context;
    private ChallengesResponseAdapter adapter;
    private ArrayList<BattleModel> list, final_battle_list;
    private ArrayList<String> id_List, invite_photo_id_List, response_id_list;
    private HashSet<BattleModel> tampon_photo_id_List;
    private String from_group;
    private Handler handler;

    // pagination constants
    int counter = 10;

    // firebase
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        // adjustpan
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_challenges_response);
        context = this;

        myRef = FirebaseDatabase.getInstance().getReference();
        handler = new Handler(Looper.getMainLooper());

        try {
            from_group = getIntent().getStringExtra("from_group");
            id_List = getIntent().getStringArrayListExtra("id_List");
            invite_photo_id_List = getIntent().getStringArrayListExtra("invite_photo_id_List");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getMiiokyChallenges();
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
        toolbar_msg = findViewById(R.id.toolbar_msg);
        relLayout_view_overlay = findViewById(R.id.relLayout_view_overlay);
        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrowBack.setOnClickListener(view -> finish());
    }

    private void clearAll() {
        if (list != null)
            list.clear();

        if (tampon_photo_id_List != null)
            tampon_photo_id_List.clear();

        if (final_battle_list != null)
            final_battle_list.clear();

        if (response_id_list != null) {
            response_id_list.clear();
        }

        if (adapter != null)
            adapter = null;

        if(recyclerView != null)
            handler.post(() -> recyclerView.setAdapter(null));

        list = new ArrayList<>();
        final_battle_list = new ArrayList<>();
        tampon_photo_id_List = new HashSet<>();
        response_id_list = new ArrayList<>();
    }

    private void getMiiokyChallenges() {
        clearAll();
        for(int i = 0; i < id_List.size(); i++){
            final int count = i;

            Query query;
            if (from_group != null) {
                query = myRef
                        .child(context.getString(R.string.dbname_group))
                        .child(id_List.get(i)) // group_id
                        .orderByChild(context.getString(R.string.field_group_id))
                        .equalTo(id_List.get(i));

            } else {
                query = myRef
                        .child(context.getString(R.string.dbname_battle))
                        .child(id_List.get(i)) // user_id
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(id_List.get(i));
            }

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

                    if(count >= id_List.size() -1){
                        for (String invite_photo_id: invite_photo_id_List) {
                            for(int i = 0; i < list.size(); i++){
                                if (list.get(i).getInvite_photoID().equals(invite_photo_id)) {
                                    // to prevent duplicate of response challenge
                                    if (!response_id_list.contains(list.get(i).getReponse_photoID())) {
                                        tampon_photo_id_List.add(list.get(i));
                                        response_id_list.add(list.get(i).getReponse_photoID());
                                    }
                                }
                            }
                        }

                        final_battle_list.addAll(tampon_photo_id_List);
                        final_battle_list.sort((o1, o2) -> String.valueOf(o2.getDate_created())
                                .compareTo(String.valueOf(o1.getDate_created())));

                        if (final_battle_list.size() == 1)
                            toolbar_msg.setText(Html.fromHtml(" <b>"+final_battle_list.size()+"</b> "
                                    +context.getString(R.string.challenge_accepted)));
                        else
                            toolbar_msg.setText(Html.fromHtml(" <b>"+final_battle_list.size()+"</b> "
                                    +context.getString(R.string.challenges_accepted)));

                        adapter = new ChallengesResponseAdapter(context, progressBar, relLayout_view_overlay);
                        adapter.setDefaultRecyclerView(context, R.id.recyclerView);

                        adapter.setOnPaginationListener(new PaginatedAdapter.OnPaginationListener() {
                            @Override
                            public void onCurrentPage(int page) {
                                //Toast.makeText(context, "Page " + page + " loaded!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNextPage(int page) {
                                getNewItems(page);
                            }

                            @Override
                            public void onFinish() {

                            }
                        });


                        getNewItems(adapter.getStartPage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    android.util.Log.d(TAG, "onCancelled: query cancelled.");
                }
            });
        }
    }

    public void onGetDate(List<BattleModel> models) {
        adapter.submitItems(models);
    }

    private void getNewItems(final int page) {
        List<BattleModel> models = new ArrayList<>();
        int start = page * counter - counter;
        int end = page * counter;
        for (int i = start; i < end; i++) {
            if (i < final_battle_list.size()) {
                models.add(final_battle_list.get(i));
            }
        }
        onGetDate(models);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeKeyboard();
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
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // check internet connection
        CheckInternetStatus.internetIsConnected(context, parent);
    }
}