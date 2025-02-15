package com.bekambimouen.miiokychallenge.views_count;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ViewsMarketStoreManager {

    private final AppCompatActivity context;
    private final DatabaseReference databaseReference;

    public ViewsMarketStoreManager(AppCompatActivity context) {
        this.context = context;

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void incrementViewCount(MarketModel marketModel, String publicationId) {
        DatabaseReference publicationRef1 = databaseReference
                .child(context.getString(R.string.dbname_user_stores))
                .child(marketModel.getStore_owner())
                .child(publicationId);

        publicationRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long currentViews = snapshot.child(context.getString(R.string.field_views)).getValue(Long.class);
                    if (currentViews == null) {
                        currentViews = 0L;
                    }
                    long newViews = currentViews + 1;

                    // Update the view count using a transaction
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(context.getString(R.string.field_views), newViews);
                    publicationRef1.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference publicationRef2 = databaseReference
                .child(context.getString(R.string.dbname_user_stores_media))
                .child(publicationId);

        publicationRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long currentViews = snapshot.child(context.getString(R.string.field_views)).getValue(Long.class);
                    if (currentViews == null) {
                        currentViews = 0L;
                    }
                    long newViews = currentViews + 1;

                    // Update the view count using a transaction
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(context.getString(R.string.field_views), newViews);
                    publicationRef2.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
