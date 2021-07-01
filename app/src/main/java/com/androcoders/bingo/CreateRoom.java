package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateRoom extends AppCompatActivity {

    private TextView room_id;
    private RecyclerView players;
    private Button start;
    private FirebaseAuth auth= FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        room_id=findViewById(R.id.room_id);
        players=findViewById(R.id.players_recycler);
        start=findViewById(R.id.start_btn);

        Toast.makeText(this, ""+getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();


    }
}