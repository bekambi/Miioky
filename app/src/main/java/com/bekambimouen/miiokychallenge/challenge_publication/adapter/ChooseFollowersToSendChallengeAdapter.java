package com.bekambimouen.miiokychallenge.challenge_publication.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseFollowersToSendChallengeAdapter extends RecyclerView.Adapter<ChooseFollowersToSendChallengeAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "ChooseFollowersToSendChallengeAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    private static OnItemClickListener onItemClickListener;

    private final AppCompatActivity context;
    public List<User> userList;
    public List<Integer> userListFiltered;
    private final ProgressBar loading_progressBar;

    // vars
    private String string_username, string_fullName, string2;

    public ChooseFollowersToSendChallengeAdapter(AppCompatActivity context, List<User> userList,
                                                 ProgressBar loading_progressBar) {
        this.context = context;
        this.userList = userList;
        this.userListFiltered = new ArrayList<>();
        this.loading_progressBar = loading_progressBar;

        setDefaultIds(userListFiltered);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_single_choose_followers,parent,
                false);
        return new ViewHolder(view);
    }

    // if search text is empty add all original list values to filterlist
    private void setDefaultIds(List<Integer> dataList) {
        dataList.clear();
        for (int i = 0; i < userList.size(); i++) dataList.add(i);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User users = userList.get(userListFiltered.get(position));
        holder.bindView(users);

        if(reachedEndOfList(position)){
            loadMoreData();
        }
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
            loading_progressBar.setVisibility(View.GONE);
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        if(userListFiltered==null) return 0;
        return userListFiltered.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        ChooseFollowersToSendChallengeAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Integer> filterList = new ArrayList<>();
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

                //String charString = charSequence.toString();
                if (charString.isEmpty() || charSequence.length() == 0) {
                    // if search text is empty add all original list values to filterlist
                    setDefaultIds(filterList);

                } else {
                    // Perform search on whole original list
                    for (int i = 0; i < userList.size(); i++) {
                        string_username = Normalizer.normalize(userList.get(i).getUsername().toLowerCase(), Normalizer.Form.NFD);
                        string_username = string_username.replaceAll("[^\\p{ASCII}]", "");

                        string_fullName = Normalizer.normalize(userList.get(i).getFullName().toLowerCase(), Normalizer.Form.NFD);
                        string_fullName = string_fullName.replaceAll("[^\\p{ASCII}]", "");

                        if (string_username.toLowerCase().contains(charString) ||
                                string_fullName.toLowerCase().contains(charString.toLowerCase())) {
                            filterList.add(i);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filterList;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userListFiltered.clear();
                userListFiltered.addAll((List<Integer>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout parent;
        public TextView username, fullName;
        public CircleImageView profileimage;
        private final View view_online;
        private final CheckBox checkbox;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullName);
            profileimage = itemView.findViewById(R.id.profile_photo);
            view_online = itemView.findViewById(R.id.view_online);
            checkbox = itemView.findViewById(R.id.checkbox);

            database=FirebaseDatabase.getInstance();
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(userListFiltered.get(getLayoutPosition()), v));
        }

        public void bindView(User user) {
            // verify if user is online
            database.getReference()
                    .child(context.getString(R.string.dbname_presence))
                    .child(user.getUser_id())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String status = snapshot.child(context.getString(R.string.field_onLine)).getValue(String.class);

                                assert status != null;
                                if(!status.isEmpty()){
                                    if(status.equals(context.getString(R.string.field_offLine))){
                                        view_online.setVisibility(View.GONE);
                                    }else{
                                        if (!user.getUser_id().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                            view_online.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            username.setText(user.getUsername());
            fullName.setText(user.getFullName());
            checkbox.setChecked(userList.get(userListFiltered.get(getLayoutPosition())).isSelected());

            Glide.with(context.getApplicationContext())
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profileimage);
        }
    }
}

