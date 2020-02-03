package com.volive.whitecab.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.volive.whitecab.R;
import com.volive.whitecab.util.GPSTracker;

public class SplashActivity extends AppCompatActivity {

    private int SPLASH_TIME_OUT=3000;
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        gpsTracker=new GPSTracker(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();

            }
        },SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
