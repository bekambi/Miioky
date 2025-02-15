package com.bekambimouen.miiokychallenge.internet_status;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bekambimouen.miiokychallenge.R;
import com.google.android.material.snackbar.Snackbar;

public class CheckInternetStatus {

    public static boolean internetIsConnected(AppCompatActivity context, View layout) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Snackbar snackbar = Snackbar.make(layout, context.getResources().getString(R.string.no_connexion), Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.red_600));
            snackbar.setTextColor(ContextCompat.getColor(context, R.color.white));
            snackbar.show();
        }

        return isConnected;
    }
}

