package com.androcoders.bingo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateRoom extends AppCompatActivity {

    private TextView room_id;
    private RecyclerView players;
    private Button start;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private boolean uniquekey = false;
    private int roomkey = 0;
    private ArrayList<Player> players_list=new ArrayList<>();
    private PlayerAdaptor adaptor;
    private boolean isOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        room_id=findViewById(R.id.room_id);
        players=findViewById(R.id.players_recycler);
        start=findViewById(R.id.start_btn);

        Toast.makeText(this, ""+getIntent().getStringExtra("id"), Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getBooleanExtra("create",true))
        {
            generateroom();
            isOwner=true;
        }
        else
        {
            roomkey=getIntent().getIntExtra("room_key",0);
            addPlayersListener();
            room_id.setText("Room Id\n"+roomkey);
            isOwner=false;
            start.setVisibility(View.GONE);
        }


        adaptor = new PlayerAdaptor(getApplicationContext(),players_list,isOwner);
        players.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        players.setAdapter(adaptor);
        players.setHasFixedSize(true);

    }

    void addPlayersListener(){
        firebaseFirestore.collection("rooms").document(String.valueOf(roomkey)).collection("players").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                players_list.clear();
                for (DocumentSnapshot document: queryDocumentSnapshots) {
                    Player player= new Player(document.getString("player_name"),document.getId());
                    players_list.add(player);
                }
                adaptor.notifyDataSetChanged();
            }
        });

        firebaseFirestore.collection("rooms").document(String.valueOf(roomkey)).collection("players").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                players_list.clear();
                for (DocumentSnapshot document: value) {
                    Player player= new Player(document.getString("player_name"),document.getId());
                    players_list.add(player);
                }
                adaptor.notifyDataSetChanged();
            }
        });
    }

    void createRoom(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        HashMap<String,Object> room = new HashMap<>();
        room.put("isStarted",false);
        room.put("owner",account.getId());
        room.put("current_turn",account.getId());

        firebaseFirestore.collection("rooms").document(String.valueOf(roomkey)).set(room);

        HashMap<String,Object> owner=new HashMap<>();
        owner.put("bingo",0);
        owner.put("player_name",account.getDisplayName());
        owner.put("score",0);

        firebaseFirestore.collection("rooms").document(String.valueOf(roomkey)).collection("players").document(account.getId()).set(owner).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addPlayersListener();
            }
        });


    }

    int generatekey() {
        Random random= new Random();
        int min= 10000;
        int max= 99999;
        int key=random.nextInt((max - min) + 1) + min;
        return key;

    }
    void generateroom() {
        firebaseFirestore.collection("rooms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                roomkey = generatekey();
                while(!uniquekey) {
                    int pos = 0;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        int id = Integer.valueOf(document.getId());
                        if (id == roomkey) {
                            roomkey = generatekey();
                            break;
                        }
                        if (pos==queryDocumentSnapshots.size()-1){
                            uniquekey=true;
                            room_id.setText(room_id.getText().toString()+"\n"+String.valueOf(roomkey));
                            createRoom();
                        }
                        pos++;
                    }

                }
            }
        });
    }
}