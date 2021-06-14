package com.agnet.leteApp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


import com.agnet.leteApp.R;

import com.agnet.leteApp.helpers.DatabaseHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SpashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    private Gson _gson;
    private DatabaseHandler _dbHandler;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _preferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        _gson = gsonBuilder.create();

        _dbHandler = new DatabaseHandler(this);

        new Handler().postDelayed(new Runnable() {


        /*  Showing splash screen with a timer. This will be useful when you
         want to show case your app logo / company*/


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SpashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_TIME_OUT);

    }



}
