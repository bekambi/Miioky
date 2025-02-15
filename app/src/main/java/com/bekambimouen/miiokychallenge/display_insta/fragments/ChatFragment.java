package com.bekambimouen.miiokychallenge.display_insta.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bekambimouen.miiokychallenge.App;
import com.bekambimouen.miiokychallenge.MainActivity;
import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyEditText;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.messages.ChatContacts;
import com.bekambimouen.miiokychallenge.messages.adapter.FriendsAdapter;
import com.bekambimouen.miiokychallenge.messages.model.Chat;
import com.bekambimouen.miiokychallenge.model.StringAboutTimeModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements OnLoadMoreItemsListener {
    private static final String TAG = "ChatFragment";

    // constants
    private static final String URL = "https://miiko-d1323-default-rtdb.europe-west1.firebasedatabase.app";

    // widgets
    private MyEditText edit_search;
    private ImageView erase;
    private RecyclerView recyclerView;
    private RelativeLayout relLayout_view_overlay;
    private ProgressBar progressBar;

    // vars
    private MainActivity context;
    private FriendsAdapter adapter;
    private List<User> userList, pagination;
    private List<StringAboutTimeModel> chatcheurs_list;
    private List<String> filter_list;
    private Handler handler;
    private int resultsCount = 0;
    private boolean isFirstLoaded = true;

    // firebase
    private DatabaseReference myRef;
    private String user_id;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        context = (MainActivity) getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        handler = new Handler(Looper.getMainLooper());

        init(view);

        return view;
    }

    private void init(View v) {
        RelativeLayout relLayout_contacts = v.findViewById(R.id.relLayout_contacts);
        progressBar = v.findViewById(R.id.progressBar);
        relLayout_view_overlay = v.findViewById(R.id.relLayout_view_overlay);
        recyclerView = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setInitialPrefetchItemCount(10);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        erase = v.findViewById(R.id.erase);
        edit_search = v.findViewById(R.id.edit_search);

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int size = charSequence.length();
                if (size != 0)
                    erase.setVisibility(View.VISIBLE);
                else
                    erase.setVisibility(View.GONE);
                // filter recycler view when text is changed
                try {
                    adapter.getFilter().filter(charSequence.toString().toLowerCase());
                } catch (NullPointerException e) {
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        erase.setOnClickListener(view -> {
            Objects.requireNonNull(edit_search.getText()).clear();
            edit_search.requestFocus();
        });

        relLayout_contacts.setOnClickListener(view -> {
            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            startActivity(new Intent(context, ChatContacts.class));
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAll(){
        if(userList != null){
            userList.clear();
        }
        if(chatcheurs_list != null){
            chatcheurs_list.clear();
        }
        if (filter_list != null) {
            filter_list.clear();
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

        chatcheurs_list = new ArrayList<>();
        filter_list = new ArrayList<>();
        userList = new ArrayList<>();
        pagination = new ArrayList<>();
    }

    private void getUsers() {
        clearAll();
        Query query = myRef
                .child(context.getString(R.string.dbname_chat));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getSender().equals(user_id)) {
                        chatcheurs_list.add(new StringAboutTimeModel(chat.getReceiver(), chat.getTimeStamp()));
                    }

                    if (chat.getReceiver().equals(user_id)) {
                        chatcheurs_list.add(new StringAboutTimeModel(chat.getSender(), chat.getTimeStamp()));
                    }
                }

                // contrairement à Hashmap, une clé ne peut pas être assossiée à deux valeurs dans HashSet
                Set<StringAboutTimeModel> hashSet = new HashSet<>(chatcheurs_list);
                chatcheurs_list.clear();
                chatcheurs_list.addAll(hashSet);

                chatcheurs_list.sort((o1, o2) -> String.valueOf(o2.getTimeStamp())
                        .compareTo(String.valueOf(o1.getTimeStamp())));

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        if (!chatcheurs_list.isEmpty()) {
            for (int i = 0; i < chatcheurs_list.size(); i++) {
                final int count = i;

                Query query = myRef
                        .child(context.getString(R.string.dbname_users))
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(chatcheurs_list.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            User user = Util_User.getUser(context, objectMap, ds);

                            if (!filter_list.contains(user.getUser_id())) {
                                userList.add(user);
                                filter_list.add(user.getUser_id());
                            }
                        }

                        if(count >= chatcheurs_list.size() -1) {
                            int iterations = userList.size();

                            if(iterations > 1){
                                iterations = 1;
                            }

                            resultsCount = 1;
                            for(int i = 0; i < iterations; i++){
                                pagination.add(userList.get(i));
                            }

                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    public void displayMore() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{
            if(userList.size() > resultsCount && !userList.isEmpty()){

                int iterations;
                if(userList.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = userList.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    pagination.add(userList.get(i));
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

    /**
     * fetches json by making http calls
     */
    private void fetchUsers() {
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(URL,
                response -> {
                    if (response == null) {
                        Toast.makeText(context, "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<User> items = new Gson().fromJson(response.toString(), new TypeToken<List<User>>() {
                    }.getType());

                    // adding contacts to contacts list
                    userList.clear();
                    userList.addAll(items);

                    // refreshing recycler view
                    adapter.notifyDataSetChanged();
                }, error -> {
            // error in getting json
            Log.d(TAG, "fetchUsers: "+ error.getMessage());
        });

        App.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relLayout_view_overlay != null && relLayout_view_overlay.getVisibility() == View.VISIBLE)
            relLayout_view_overlay.setVisibility(View.GONE);

        // to prevent data to upload to another fragment
        if (isFirstLoaded) {
            isFirstLoaded = false;
            getUsers();
            fetchUsers();

            adapter = new FriendsAdapter(context, pagination, true, this, progressBar, relLayout_view_overlay);

            if (adapter.getItemCount() == 0)
                progressBar.setVisibility(View.GONE);
        }
    }
}