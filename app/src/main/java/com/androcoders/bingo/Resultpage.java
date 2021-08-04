package com.androcoders.bingo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Resultpage extends AppCompatActivity {
    private RecyclerView resultrecycler;
    private ResultAdapter resultAdapter;
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    private ArrayList<Player> list=new ArrayList<>();
    private int key=0;
    private Button playagain;
    private String playerid="",ownerid=" ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultpage);
        getSupportActionBar().hide();


        resultrecycler=findViewById(R.id.result_rec);
        resultAdapter=new ResultAdapter(getApplicationContext(),list);
        resultrecycler.setHasFixedSize(true);
        resultrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        resultrecycler.setAdapter(resultAdapter);
        key=getIntent().getIntExtra("roomkey",0);
        playagain=findViewById(R.id.ready);

        playerid= GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();
       firestore.collection("rooms").document(String.valueOf(key)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                ownerid=documentSnapshot.getString("owner");
                if(!playerid.contentEquals(ownerid))
                {
                   playagain.setVisibility(View.INVISIBLE);
                }

            }
        });

        playagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                firestore.collection("rooms").document(String.valueOf(key)).update("isMatchover",false,"isStarted",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),GameActivity.class);
                        intent.putExtra("room_key",key);
                        startActivity(intent);
                        finish();
                    }
                });


            }
        });

        firestore.collection("rooms").document(String.valueOf(key)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(!playerid.contentEquals(ownerid) && value.getBoolean("isStarted"))
                {
                    Intent intent=new Intent();
                    intent.setClass(getApplicationContext(),GameActivity.class);
                    intent.putExtra("room_key",0);
                    startActivity(intent);
                    finish();
                }


            }
        });





        firestore.collection("rooms").document(String.valueOf(key)).collection("players").orderBy("score", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    String playername,playerid ;
                    long bingo,score;

                    playername=documentSnapshot.getString("player_name");
                    playerid=documentSnapshot.getId();
                    bingo=(long)documentSnapshot.getLong("bingo");
                    score=(long)documentSnapshot.getLong("score");
                    list.add(new Player(playername,playerid,bingo,score));

                }
                resultAdapter.notifyDataSetChanged();

            }
        });

    }
}