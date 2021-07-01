package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TimerTask task;
    Intent intent;
    private Timer time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        time=new Timer();
        intent=new Intent();

    }

    @Override
    protected void onStart() {
        super.onStart();
        task=new TimerTask() {
            @Override
            public void run() {
          intent.setClass(getApplicationContext(),Loginpage.class);
          startActivity(intent);
          finish();
            }
        };
        time.schedule(task,2000);
        }
}