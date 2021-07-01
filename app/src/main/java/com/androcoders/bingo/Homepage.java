package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Homepage extends AppCompatActivity {

    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        create=findViewById(R.id.create_btn);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(getApplicationContext(),CreateRoom.class));
            }
        });

        getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(), ""+getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
    }
}