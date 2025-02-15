package com.bekambimouen.miiokychallenge.views_count;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VisitProfileManager {

    private final AppCompatActivity context;
    private final DatabaseReference databaseReference;

    public VisitProfileManager(AppCompatActivity context) {
        this.context = context;

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void incrementViewCount(User user, User current_user) {
        DatabaseReference myRef = databaseReference
                .child(context.getString(R.string.dbname_visit_profile))
                .child(user.getUser_id())
                .child(current_user.getUser_id());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Date date=new Date();

                if (snapshot.exists()) {
                    Long currentViews = snapshot.child(context.getString(R.string.field_views)).getValue(Long.class);
                    if (currentViews == null) {
                        currentViews = 0L;
                    }
                    long newViews = currentViews + 1;

                    // Update the view count using a transaction
                    Map<String, Object> updates = new HashMap<>();

                    updates.put(context.getString(R.string.field_views), newViews);
                    updates.put(context.getString(R.string.field_date_last_profile_visit), date.getTime());

                    myRef.updateChildren(updates);

                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(context.getString(R.string.field_user_id), current_user.getUser_id());
                    map.put(context.getString(R.string.field_fullName), current_user.getFullName());
                    map.put(context.getString(R.string.field_username), current_user.getUsername());
                    map.put(context.getString(R.string.field_date_last_profile_visit), date.getTime());
                    map.put(context.getString(R.string.field_views), 1);

                    databaseReference.child(context.getString(R.string.dbname_visit_profile))
                            .child(user.getUser_id())
                            .child(current_user.getUser_id())
                            .updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
