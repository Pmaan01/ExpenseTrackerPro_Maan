package com.android_assignments.expensetrackerpro_maan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.android_assignments.expensetrackerpro_maan.R;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delayed navigation to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, com.android_assignments.expensetrackerpro_maan.activities.MainActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_DELAY);
    }
}
