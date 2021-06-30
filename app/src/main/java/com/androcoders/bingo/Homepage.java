package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(), ""+getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
    }
}