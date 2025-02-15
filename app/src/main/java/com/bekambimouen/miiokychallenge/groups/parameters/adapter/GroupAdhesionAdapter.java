package com.bekambimouen.miiokychallenge.groups.parameters.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.groups.simple_member.GroupViewProfile;
import com.bekambimouen.miiokychallenge.groups.utils.Utils_Map_GroupFollowingModel;
import com.bekambimouen.miiokychallenge.interfaces.OnLoadMoreItemsListener;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupAdhesionAdapter extends RecyclerView.Adapter<GroupAdhesionAdapter.MyViewHolder>
        implements Filterable {
    private static final String TAG = "GroupListSuggestionAdapter";

    private OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private final AppCompatActivity context;
    private final List<UserGroups> groupNameList;
    public List<UserGroups> groupListFiltered;
    private final ProgressBar loading_progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // vars
    private String string, string2;
    private final String user_id;

    public GroupAdhesionAdapter(AppCompatActivity context, List<UserGroups> groupNameList,
                                ProgressBar loading_progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.groupNameList = groupNameList;
        this.groupListFiltered = groupNameList;
        this.loading_progressBar = loading_progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_adhesion_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserGroups user_groups = groupListFiltered.get(position);
        holder.bind(user_groups);

        Gson gson = new Gson();
        String myJson = gson.toJson(user_groups);

        holder.parent.setOnClickListener(v -> {
            // prevent two clic
            Util.preventTwoClick(holder.parent);

            if (relLayout_view_overlay != null)
                relLayout_view_overlay.setVisibility(View.VISIBLE);
            context.getWindow().setExitTransition(new Slide(Gravity.END));
            context.getWindow().setEnterTransition(new Slide(Gravity.START));
            Intent intent = new Intent(context, GroupViewProfile.class);
            intent.putExtra("user_groups", myJson);
            context.startActivity(intent);
        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }
    }

    private boolean reachedEndOfList(int position){
        return position == groupListFiltered.size() - 1;
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
        if(groupListFiltered==null) return 0;
        return groupListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                string2 = Normalizer.normalize(charSequence.toString().toLowerCase(), Normalizer.Form.NFD);
                String charString = string2.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter
                //String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    groupListFiltered = groupNameList;
                } else {
                    List<UserGroups> filteredList = new ArrayList<>();
                    for (UserGroups row : groupNameList) {
                        string = Normalizer.normalize(row.getGroup_name().toLowerCase(), Normalizer.Form.NFD);
                        string = string.replaceAll("[^\\p{ASCII}]", ""); // to remove accents on letter

                        if (string.toLowerCase().contains(charString)) {
                            filteredList.add(row);
                        }
                    }

                    groupListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupListFiltered;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupListFiltered = (ArrayList<UserGroups>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "MyViewHolder";
        // widgets
        private final ImageView profile_photo;
        private final TextView group_name, last_visit;
        private final RelativeLayout parent, relLayout_button_unJoin, relLayout_button_join;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            group_name = itemView.findViewById(R.id.group_name);
            last_visit = itemView.findViewById(R.id.last_visit);
            relLayout_button_unJoin = itemView.findViewById(R.id.relLayout_button_unJoin);
            relLayout_button_join = itemView.findViewById(R.id.relLayout_button_join);
            profile_photo = itemView.findViewById(R.id.profile_photo);
            parent = itemView.findViewById(R.id.parent);
        }

        private void bind(UserGroups userGroups) {
            group_name.setText(userGroups.getGroup_name());

            if (!context.isFinishing()) {
                Glide.with(context)
                        .asBitmap()
                        .load(userGroups.getProfile_photo())
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(profile_photo);
            }

            isFollowing(userGroups);

            relLayout_button_join.setOnClickListener(view -> {
                closeKeyboard();
                // show dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                TextView negativeButton = view1.findViewById(R.id.tv_no);
                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                builder.setView(view1);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.cancel));
                positiveButton.setText(context.getString(R.string.join_group));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.join)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.you_will_join)
                        +" <b> " +userGroups.getGroup_name()+"</b>  "));

                negativeButton.setOnClickListener(view2 -> d.dismiss());

                // get the following model data
                HashMap<Object, Object> map = Utils_Map_GroupFollowingModel.groupFollowingModel(userGroups.getAdmin_master(), user_id, "", userGroups.getGroup_id());

                positiveButton.setOnClickListener(view2 -> {
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id)
                            .child(userGroups.getGroup_id())
                            .setValue(map);

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(userGroups.getGroup_id())
                            .child(user_id)
                            .setValue(map);

                    d.dismiss();
                    setJoin();
                });
            });

            relLayout_button_unJoin.setOnClickListener(v -> {
                closeKeyboard();
                // show dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

                TextView dialog_title = view1.findViewById(R.id.dialog_title);
                TextView dialog_text = view1.findViewById(R.id.dialog_text);
                TextView negativeButton = view1.findViewById(R.id.tv_no);
                TextView positiveButton = view1.findViewById(R.id.tv_yes);
                builder.setView(view1);

                Dialog d = builder.create();
                d.show();

                negativeButton.setText(context.getString(R.string.cancel));
                positiveButton.setText(context.getString(R.string.quit));

                dialog_title.setText(Html.fromHtml(context.getString(R.string.leave_the_group_title)));
                dialog_text.setText(Html.fromHtml(context.getString(R.string.do_you_really_want_to_leave_group)
                        +" <b> " +userGroups.getGroup_name()+"?</b>  "));

                negativeButton.setOnClickListener(view2 -> d.dismiss());

                positiveButton.setOnClickListener(view2 -> {
                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user_id)
                            .child(userGroups.getGroup_id())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_followers))
                            .child(userGroups.getGroup_id())
                            .child(user_id)
                            .removeValue();

                    d.dismiss();
                    setUnJoin();
                });
            });
        }

        private void setJoin(){
            Log.d(TAG, "setFollowing: updating UI for following this user");
            relLayout_button_join.setVisibility(View.GONE);
            relLayout_button_unJoin.setVisibility(View.VISIBLE);
        }

        private void setUnJoin(){
            Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
            relLayout_button_join.setVisibility(View.VISIBLE);
            relLayout_button_unJoin.setVisibility(View.GONE);
        }

        private void isFollowing(UserGroups userGroups){
            Log.d(TAG, "isFollowing: checking if following this users.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(context.getString(R.string.dbname_group_following))
                    .child(user_id)
                    .orderByChild(context.getString(R.string.field_group_id))
                    .equalTo(userGroups.getGroup_id());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                        Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();

                        assert objectMap != null;
                        GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                        setJoin();

                        long tv_date = following.getLastSeenInGroup();
                        last_visit.setText(Html.fromHtml(context.getString(R.string.last_visit)+" "
                                +" "+context.getString(R.string.there_is)+" "+ TimeUtils.getTime(context, tv_date)));
                    }

                    if (!snapshot.exists()) {
                        setUnJoin();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // date of the day
    private String setDate(long date) {
        String mDate;

        // it's today. show the label "today"
        if (TimeUtils.isDateToday(date)) {
            mDate = context.getString(R.string.today);
        } else {
            // it's not today. shows the week of day label
            mDate = TimeUtils.getFormattedTimestamp(context, date);
        }
        return mDate;
    }
}

