package com.bekambimouen.miiokychallenge.messages.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.full_img_and_vid_simple.SimpleFullProfilPhoto;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.messages.MessageActivity;
import com.bekambimouen.miiokychallenge.messages.model.Chat;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>
        implements Filterable {
    private static final String TAG = "FriendsAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    // vars
    private final AppCompatActivity context;
    private final List<User> userList;
    public List<User> userListFiltered;
    private final ProgressBar progressBar;

    // vars
    private final boolean isChat;
    private final RelativeLayout relLayout_view_overlay;
    private int n;
    private Chat chat;

    private String string_username, string_fullName, string2;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;
    long tv_date;
    User user;

    public FriendsAdapter(AppCompatActivity context, List<User> userList, boolean isChat,
                          OnLoadMoreItemsListener mOnLoadMoreItemsListener, ProgressBar progressBar,
                          RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.userList = userList;
        this.userListFiltered = userList;
        this.isChat = isChat;
        this.mOnLoadMoreItemsListener = mOnLoadMoreItemsListener;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_single_layout,parent,
                false);
        return new ViewHolder(relLayout_view_overlay, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user = userListFiltered.get(position);
        holder.bindView(user);

        if (isChat){
            lastMessage(user.getUser_id(), holder.last_msg, holder);
        } else {
            holder.last_msg.setText(user.getStatus());
            holder.nb_nv_msgs.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
        }

        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

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
                    userListFiltered = userList;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : userList) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username,last_msg, nb_nv_msgs, date;
        public CircleImageView profile_photo;
        private final View view_online;

        // vars
        private final RelativeLayout relLayout_view_overlay;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(RelativeLayout relLayout_view_overlay, @NonNull View itemView) {
            super(itemView);
            this.relLayout_view_overlay = relLayout_view_overlay;

            username = itemView.findViewById(R.id.username);
            last_msg = itemView.findViewById(R.id.FriendSingle_lastMsg);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            nb_nv_msgs = itemView.findViewById(R.id.nb_nv_msgs);
            date = itemView.findViewById(R.id.date);
            view_online = itemView.findViewById(R.id.view_online);

            database=FirebaseDatabase.getInstance();
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

            Glide.with(context)
                    .load(user.getProfileUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(profile_photo);

            profile_photo.setOnClickListener(v -> {
                if (relLayout_view_overlay != null)
                    relLayout_view_overlay.setVisibility(View.VISIBLE);
                context.getWindow().setExitTransition(new Slide(Gravity.END));
                context.getWindow().setEnterTransition(new Slide(Gravity.START));
                Intent intent = new Intent(context, SimpleFullProfilPhoto.class);
                intent.putExtra("img_url", user.getFull_profileUrl());
                context.startActivity(intent);
            });

            itemView.setOnClickListener(v -> go_chat(user.getUser_id()));
        }

        private void go_chat(String userID) {
            Query query = myRef.child(context.getString(R.string.dbname_users))
                    .orderByChild(context.getString(R.string.field_user_id))
                    .equalTo(userID);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                        assert objectMap != null;
                        User user = Util_User.getUser(context, objectMap, ds);

                        if (relLayout_view_overlay != null)
                            relLayout_view_overlay.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        String myGson = gson.toJson(user);
                        context.getWindow().setExitTransition(new Slide(Gravity.END));
                        context.getWindow().setEnterTransition(new Slide(Gravity.START));
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("to_chat", myGson);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //check for last message
    private void lastMessage(final String userID, final TextView last_msg, ViewHolder holder){
        Query query = myRef
                .child(context.getString(R.string.dbname_chat));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                n = 0;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    chat = ds.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getReceiver().equals(user_id) && chat.getSender().equals(userID)
                            ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(user_id)) {

                        tv_date = chat.getTimeStamp();

                        // ___________________________________________________________________________________
                        // to show number of new messages on chat fragment
                        if (chat.getReceiver().equals(user_id) && chat.getSender().equals(userID)) {
                            if (!chat.isIsseen() && chat.getSuppressed().equals("no")) {
                                n++;
                            }

                        }

                        if (chat.getSuppressed().equals("no")) {
                            if (!chat.getMessage().isEmpty()) {
                                last_msg.setText(chat.getMessage());

                                holder.date.setVisibility(View.VISIBLE);
                                holder.date.setTextColor(context.getColor(R.color.white_semi_transparent));
                                getTimestampDifference(tv_date, holder.date);

                            } else if (!chat.getMessagePhoto().isEmpty()) {
                                last_msg.setText(chat.getMessagePhoto());

                                holder.date.setVisibility(View.VISIBLE);
                                holder.date.setTextColor(context.getColor(R.color.white_semi_transparent));
                                getTimestampDifference(tv_date, holder.date);

                            } else if (!chat.getVoiceMail().isEmpty()) {
                                last_msg.setText(context.getString(R.string.voice_message));

                                holder.date.setVisibility(View.VISIBLE);
                                holder.date.setTextColor(context.getColor(R.color.white_semi_transparent));
                                getTimestampDifference(tv_date, holder.date);

                            }
                        }
                    }
                }

                if (n == 0) {
                    holder.nb_nv_msgs.setVisibility(View.GONE);

                } else {
                    holder.nb_nv_msgs.setVisibility(View.VISIBLE);
                    holder.nb_nv_msgs.setText(String.valueOf(n));
                    holder.date.setTextColor(context.getColor(R.color.blue_600));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Returns a string representing the number of days ago the post was made
     */
    @SuppressLint("SetTextI18n")
    private void getTimestampDifference(long date, TextView tvDate){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        String tps;
        long time = (System.currentTimeMillis() - date);
        if (time >= 2*3600*24000) {
            tps = "le "+date_passe;
        } else if (time >= 24*3600000) {
            tps = "hier";
        } else if (time >= 2*3600000) {
            tps = "il ya "+((int)(time/(3600000)))+" h";
        } else if (time >= 3600000) {
            tps = "il ya une heure";
        } else if (time >= 120000) {
            tps = "il ya "+(int)(time/60000) + " minutes";
        } else {
            tps = "à l'instant";
        }
        tvDate.setText(tps);
    }
}

