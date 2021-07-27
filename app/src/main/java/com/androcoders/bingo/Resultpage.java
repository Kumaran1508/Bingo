package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Resultpage extends AppCompatActivity {
    private RecyclerView resultrecycler;
    private ResultAdapter resultAdapter;
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    private ArrayList<Player> list=new ArrayList<>();
    private String key="42714";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultpage);


        resultrecycler=findViewById(R.id.result_rec);
        resultAdapter=new ResultAdapter(getApplicationContext(),list);
        resultrecycler.setHasFixedSize(true);
        resultrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        resultrecycler.setAdapter(resultAdapter);

        firestore.collection("rooms").document(key).collection("players").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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