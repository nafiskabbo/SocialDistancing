package com.kabbo.covid19.socialdistancing.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabbo.covid19.socialdistancing.R;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //// checking Firebase User
                if (currentUser == null) {
                    Intent RegisterIntent = new Intent(SplashActivity.this, RegisterActivity.class);
                    startActivity(RegisterIntent);
                    finish();
                } else {
                    Intent MainIntent = new Intent(SplashActivity.this, InformationActivity.class);
                    startActivity(MainIntent);
                    finish();
                }
                //// checking Firebase User
            }
        }, 3000);

    }

}