package com.androcoders.bingo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private int val=1;
    private boolean isFilled = false;
    private String current_turn;
    private ArrayList<String> players = new ArrayList<>();
    private FirebaseFirestore firestore =FirebaseFirestore.getInstance();
    private int roomkey;
    private String playerid;
    private TextView playerturn;
    private int numbers[]= {R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,R.id.btn10,R.id.btn11,R.id.btn12,
            R.id.btn13,R.id.btn14,R.id.btn15,R.id.btn16,R.id.btn17,R.id.btn18,R.id.btn19,R.id.btn20,R.id.btn21,R.id.btn22,R.id.btn23,R.id.btn24,R.id.btn25};
    private int lines[]={R.id.diagonal1,R.id.diagonal2,R.id.row1,R.id.row2,R.id.row3,R.id.row4,R.id.row5,R.id.column1,R.id.column2,R.id.column3,R.id.column4,R.id.column5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();

        roomkey = getIntent().getIntExtra("room_key",0);
        playerid = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();

        playerturn = findViewById(R.id.player_turn);

        for (int i=0;i<numbers.length;i++){
            Button number = findViewById(numbers[i]);
            number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberClick(number);
                }
            });
        }

        for (int i=0;i<lines.length;i++){
            View line=findViewById(lines[i]);
            line.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        firestore.collection("rooms").document(String.valueOf(roomkey)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                current_turn = value.getString("current_turn");
                playerturn.setText(current_turn);

                String striked_number = value.getString("striked_number");
                for (int i=0;i<numbers.length;i++){
                    Button button = findViewById(numbers[i]);
                    if (striked_number.contentEquals(button.getText().toString()))
                        button.setBackground(getDrawable(R.drawable.gradient2));
                }
            }
        });

        firestore.collection("rooms").document(String.valueOf(roomkey)).collection("players").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    players.add(documentSnapshot.getId());
                }
            }
        });

    }

    void onNumberClick(Button number){
        if (!isFilled && number.getText().toString().contentEquals("")){
            number.setText(""+val);
            number.setTextColor(getColor(R.color.white));
            val++;
            if(val>25) isFilled=true;

        }
        else if (isFilled){
            if (current_turn.contentEquals(playerid)){
                number.setBackground(getDrawable(R.drawable.gradient2));

                firestore.collection("rooms").document(String.valueOf(roomkey)).update("striked_number",number.getText().toString());

                String next_player;

                if (players.indexOf(playerid)==players.size()-1)
                    next_player = players.get(0);
                else
                    next_player = players.get(players.indexOf(playerid)+1);


                firestore.collection("rooms").document(String.valueOf(roomkey)).update("current_turn",next_player);
            }

        }
    }


}