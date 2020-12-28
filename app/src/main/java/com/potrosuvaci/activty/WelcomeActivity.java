package com.potrosuvaci.activty;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.potrosuvaci.R;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    /* TODO change this to 5000*/
    private static final int SCREEN_ACTIVE_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // your code to start second activity. Will wait for 3 seconds before calling
                // this method
                finish();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        }, SCREEN_ACTIVE_TIME);
    }


}