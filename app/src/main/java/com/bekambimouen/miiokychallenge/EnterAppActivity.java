package com.bekambimouen.miiokychallenge;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;

public class EnterAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen.installSplashScreen(this);

        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}