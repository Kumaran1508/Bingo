package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Random;

public class CreateRoom extends AppCompatActivity {

    private TextView room_id;
    private RecyclerView players;
    private Button start;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        room_id=findViewById(R.id.room_id);
        players=findViewById(R.id.players_recycler);
        start=findViewById(R.id.start_btn);

        Toast.makeText(this, ""+getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();

        int roomkey = generateroom();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        HashMap<String,Object> room = new HashMap<>();
        room.put("isStarted",false);
        room.put("owner",);


        firebaseFirestore.collection("rooms").document(String.valueOf(roomkey)).set()

    }
    int generatekey() {
        Random random= new Random();
        int min= 10000;
        int max= 99999;
        int roomkey=random.nextInt((max - min) + 1) + min;
        return roomkey;

    }
    int generateroom() {
        int roomkey=generatekey();
        final boolean[] uniquekey = {false};

        firebaseFirestore.collection("rooms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots){
                    int id= Integer.valueOf(document.getId());
                    if (id==roomkey){
                        return;
                    }
                }
                uniquekey[0] = true;
            }
        });
        if (uniquekey[0])
            return roomkey;
        return generateroom();
    }
}