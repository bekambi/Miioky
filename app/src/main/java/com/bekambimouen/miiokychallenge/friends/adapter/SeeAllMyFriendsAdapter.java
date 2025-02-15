package com.bekambimouen.miiokychallenge.friends.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.friends.CommonFriends;
import com.bekambimouen.miiokychallenge.friends.bottomsheet.BottomSheetManegeFriend;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.search.ViewProfile;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeeAllMyFriendsAdapter extends RecyclerView.Adapter<SeeAllMyFriendsAdapter.MyViewHolder>
        implements Filterable {
    private static final String TAG = "SuggestionAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    // vars
    private final AppCompatActivity context;
    private final ArrayList<User> list;
    public List<User> userListFiltered;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    private String string_username, string_fullName, string2;

    public SeeAllMyFriendsAdapter(AppCompatActivity context, ArrayList<User> list, ProgressBar progressBar,
                                  OnLoadMoreItemsListener mOnLoadMoreItemsListener, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.list = list;
        this.userListFiltered = list;
        this.progressBar = progressBar;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.relLayout_view_overlay = relLayout_view_overlay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_friend_see_all_my_friends_item, parent, false);
        return new MyViewHolder(relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userListFiltered.get(position);
        holder.bindView(user);

        if (View.VISIBLE == progressBar.getVisibility())
            progressBar.setVisibility(View.GONE);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    public void removeAt(int position) {
        userListFiltered.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userListFiltered.size());
    }

    private boolean reachedEndOfList(int position){
        return position == userListFiltered.size() - 1;
    }

    private void loadMoreData(){
        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) context;
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        if(userListFiltered==null) return 0;
        return userListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

                if (charString.isEmpty()) {
                    userListFiltered = list;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : list) {
                        string_username = Normalizer.normalize(row.getUsername().toLowerCase(), Normalizer.Form.NFD);
                        string_username = string_username.replaceAll("[^\\p{ASCII}]", "");

                        string_fullName = Normalizer.normalize(row.getFullName().toLowerCase(), Normalizer.Form.NFD);
                        string_fullName = string_fullName.replaceAll("[^\\p{ASCII}]", "");

                        if (string_username.toLowerCase().contains(charString) ||
                                string_fullName.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    userListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userListFiltered;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userListFiltered = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // widgets
        private final RelativeLayout parent;
        private final ImageView menu_item;
        private final CircleImageView profile_photo;
        private final TextView username, full_name, common_friends;

        // vars
        private final RelativeLayout relLayout_view_overlay;
        private final List<String> current_user_friends_list;
        private final List<String> friend_user_friends_list;
        private int t = 0;

        // firebase
        private final DatabaseReference myRef;
        private User mUser;
        private final String user_id;

        public MyViewHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            parent = itemView.findViewById(R.id.parent);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            full_name = itemView.findViewById(R.id.full_name);
            common_friends = itemView.findViewById(R.id.common_friends);
            menu_item = itemView.findViewById(R.id.menu_item);

            myRef = FirebaseDatabase.getInstance().getReference();
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            current_user_friends_list = new ArrayList<>();
            friend_user_friends_list = new ArrayList<>();

            // get the common friends
            getCommonFriends();
        }

        void bindView(User user) {
            mUser = user;
            Glide.with(context.getApplicationContext())
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);

            username.setText(user.getUsername());
            full_name.setText(user.getFullName());

            parent.setOnClickListener(v -> {
                Log.d(TAG, "onClick: navigating to profile of: " +
                        user.getUsername());

                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, ViewProfile.class);
                Gson gson = new Gson();
                String myJson = gson.toJson(user);
                intent.putExtra("user", myJson);
                context.startActivity(intent);
            });

            // common friends
            common_friends.setOnClickListener(view -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, CommonFriends.class);
                intent.putExtra("userID", user.getUser_id());
                context.startActivity(intent);
            });

            menu_item.setOnClickListener(view -> {
                BottomSheetManegeFriend bottomSheetManegeFriend = new BottomSheetManegeFriend(context, user.getUser_id(),
                        null, null, null, null);
                if (bottomSheetManegeFriend.isAdded())
                    return;
                bottomSheetManegeFriend.show(context.getSupportFragmentManager(), "TAG");
            });
        }

        // get common friends
        private void getCommonFriends() {
            Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                    .child(user_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String keyID = dataSnapshot.getKey();

                        current_user_friends_list.add(keyID);
                    }

                    // get the friend user friend list
                    Query query = myRef.child(context.getString(R.string.dbname_friend_following))
                            .child(mUser.getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                String keyID = dataSnapshot.getKey();

                                friend_user_friends_list.add(keyID);
                            }

                            for (String ID: current_user_friends_list) {
                                for (int i = 0; i < friend_user_friends_list.size(); i++) {
                                    if (ID.equals(friend_user_friends_list.get(i))) {
                                        t++;
                                    }
                                }
                            }

                            if (t != 0)
                                common_friends.setVisibility(View.VISIBLE);

                            if (t == 1)
                                common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friend)));
                            else
                                common_friends.setText(Html.fromHtml(t+" "+context.getString(R.string.mutual_friends)));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

