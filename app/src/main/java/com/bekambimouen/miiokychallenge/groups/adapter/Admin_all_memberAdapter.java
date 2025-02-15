package com.bekambimouen.miiokychallenge.groups.adapter;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;
import com.bekambimouen.miiokychallenge.groups.manage_member.bottomsheet.BottomSheetManageMemberInGroup;
import com.bekambimouen.miiokychallenge.groups.model.GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.groups.model.UserGroups;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_GroupFollowersFollowing;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_all_memberAdapter extends RecyclerView.Adapter<Admin_all_memberAdapter.ViewHolder> {
    private static final String TAG = "Admin_all_memberAdapter";

    // vars
    private final AppCompatActivity context;
    private BottomSheetManageMemberInGroup bottomSheetManageMemberInGroup;
    private final List<String> list;
    private final UserGroups user_groups;

    // firebase
    private final DatabaseReference myRef;

    public Admin_all_memberAdapter(AppCompatActivity context, List<String> list,
                                   UserGroups user_groups) {
        this.context = context;
        this.list = list;
        this.user_groups = user_groups;

        myRef = FirebaseDatabase.getInstance().getReference();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_admin_members_of_group,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String the_user_id = list.get(position);
        holder.bindView(the_user_id);

        Query query = myRef
                .child(context.getString(R.string.dbname_users))
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(the_user_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                    assert objectMap != null;
                    User user = Util_User.getUser(context, objectMap, ds);

                    holder.username.setText(user.getUsername());

                    if (!context.isFinishing()) {
                        Glide.with(context)
                                .asBitmap()
                                .load(user.getProfileUrl())
                                .placeholder(R.drawable.ic_baseline_person_24)
                                .into(holder.profile_photo);
                    }

                    bottomSheetManageMemberInGroup = new BottomSheetManageMemberInGroup(context, user_groups);
                    holder.view.setOnClickListener(view -> {
                        try {
                            if (bottomSheetManageMemberInGroup.isAdded())
                                return;
                            // hide keyboard
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                            Bundle bundle = new Bundle();
                            bundle.putString("the_user_id", the_user_id);
                            bundle.putString("admin_or_moderator_id", "member");
                            bottomSheetManageMemberInGroup.setArguments(bundle);
                            bottomSheetManageMemberInGroup.show(context.getSupportFragmentManager(), "TAG");

                        } catch (IllegalStateException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                    });

                    // date following
                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child(context.getString(R.string.dbname_group_following))
                            .child(user.getUser_id())
                            .orderByChild(context.getString(R.string.field_group_id))
                            .equalTo(user_groups.getGroup_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                GroupFollowersFollowing following = Util_GroupFollowersFollowing.getGroupFollowersFollowing(context, objectMap);

                                long date = following.getDate_following();

                                if (!TextUtils.isEmpty(following.getAdding_by())) {
                                    holder.linLayout_add_by.setVisibility(View.VISIBLE);

                                    Query query = myRef
                                            .child(context.getString(R.string.dbname_users))
                                            .orderByChild(context.getString(R.string.field_user_id))
                                            .equalTo(following.getAdding_by());

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                                Map<Object, Object> objectMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                                                assert objectMap != null;
                                                User user = Util_User.getUser(context, objectMap, dataSnapshot);

                                                long time = (System.currentTimeMillis() - date);
                                                if (time >= 2*3600*24000254025L)
                                                    holder.add_by.setText(Html.fromHtml(context.getString(R.string.adding_by)
                                                            +" "+ user.getUsername()
                                                            +" "+context.getString(R.string.the) +" "+ TimeUtils.getTime(context, date)));
                                                else
                                                    holder.add_by.setText(Html.fromHtml(context.getString(R.string.adding_by)
                                                            +" "+ user.getUsername()
                                                            +" "+context.getString(R.string.there_is) +" "+ TimeUtils.getTime(context, date)));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    holder.linLayout_add_by.setVisibility(View.VISIBLE);
                                    holder.add_by.setText(Html.fromHtml(context.getString(R.string.admin_master_member_from)
                                            +" "+ TimeUtils.getTime(context, date)));
                                }
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
    }

    @Override
    public int getItemCount() {
        return (list!= null && !list.isEmpty()) ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

        // widgets
        private final CircleImageView profile_photo;
        private final TextView username, add_by;
        private final LinearLayout linLayout_add_by;
        private final View view, view_online;

        // firebase
        private final FirebaseDatabase database;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            profile_photo = itemView.findViewById(R.id.profile_photo);
            username = itemView.findViewById(R.id.username);
            add_by = itemView.findViewById(R.id.add_by);
            linLayout_add_by = itemView.findViewById(R.id.linLayout_add_by);
            view_online = itemView.findViewById(R.id.view_online);
            LinearLayout linLayout_add_by = itemView.findViewById(R.id.linLayout_add_by);
            linLayout_add_by.setVisibility(View.VISIBLE);

            database=FirebaseDatabase.getInstance();
        }

        void bindView(String admin_user_id) {
            Log.d(TAG, "bindView: bindView");

            // verify if user is online
            database.getReference()
                    .child(context.getString(R.string.dbname_presence))
                    .child(admin_user_id)
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
                                        if (!admin_user_id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                                            view_online.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}

