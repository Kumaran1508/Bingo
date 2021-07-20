package com.androcoders.bingo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean isReady = false;
    private String current_turn;
    private ArrayList<String> players = new ArrayList<>();
    private FirebaseFirestore firestore =FirebaseFirestore.getInstance();
    private int roomkey;
    private String playerid;
    private TextView playerturn;
    private ArrayList<Button> nums = new ArrayList<>();
    private ArrayList<View> crosses = new ArrayList<>();
    private int bingo = 0;


    private int numbers[]= {R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,R.id.btn10,R.id.btn11,R.id.btn12,
            R.id.btn13,R.id.btn14,R.id.btn15,R.id.btn16,R.id.btn17,R.id.btn18,R.id.btn19,R.id.btn20,R.id.btn21,R.id.btn22,R.id.btn23,R.id.btn24,R.id.btn25};
    private int lines[]={R.id.row1,R.id.row2,R.id.row3,R.id.row4,R.id.row5,R.id.column1,R.id.column2,R.id.column3,R.id.column4,R.id.column5,R.id.diagonal1,R.id.diagonal2};


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
            nums.add(number);
        }

        for (int i=0;i<lines.length;i++){
            View line=findViewById(lines[i]);
            line.setVisibility(View.INVISIBLE);
            crosses.add(line);
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
                    if (striked_number.contentEquals(button.getText().toString()) && isReady) {
                        button.setBackground(getDrawable(R.drawable.gradient2));
                        try {
                            button.setEnabled(false);
                            checkBingo();
                        } catch (Exception e) {
                            Log.d("Enable fail", e.getMessage());
                        }
                    }
                }

                if (value.getString("filled_count").contentEquals(value.getString("total_players")))
                    isReady = true;

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
            if(val>25){
                firestore.collection("rooms").document(String.valueOf(roomkey)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String count = documentSnapshot.getString("filled_count");
                        updateFilledPlayers(count);
                        isFilled=true;
                    }
                });
            }

        }
        else if (isFilled && isReady){
            if (current_turn.contentEquals(playerid)){
                number.setBackground(getDrawable(R.drawable.gradient2));
                number.setEnabled(false);
                checkBingo();

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

    void updateFilledPlayers(String count){
        int val = Integer.valueOf(count);
        firestore.collection("rooms").document(String.valueOf(roomkey)).update("filled_count",String.valueOf(val+1));
    }


    void checkBingo(){
        if (isReady){
            if (!nums.get(0).isEnabled() && !nums.get(1).isEnabled() && !nums.get(2).isEnabled()
                    && !nums.get(3).isEnabled() && !nums.get(4).isEnabled()) {
                if (crosses.get(0).getVisibility() == View.INVISIBLE) {
                    crosses.get(0).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(5).isEnabled() && !nums.get(6).isEnabled() && !nums.get(7).isEnabled()
                    && !nums.get(8).isEnabled() && !nums.get(9).isEnabled()) {
                if (crosses.get(1).getVisibility() == View.INVISIBLE) {
                    crosses.get(1).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(10).isEnabled() && !nums.get(11).isEnabled() && !nums.get(12).isEnabled()
                    && !nums.get(13).isEnabled() && !nums.get(14).isEnabled()) {
                if (crosses.get(2).getVisibility() == View.INVISIBLE) {
                    crosses.get(2).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(15).isEnabled() && !nums.get(16).isEnabled() && !nums.get(17).isEnabled()
                    && !nums.get(18).isEnabled() && !nums.get(19).isEnabled()) {
                if (crosses.get(3).getVisibility() == View.INVISIBLE) {
                    crosses.get(3).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(20).isEnabled() && !nums.get(21).isEnabled() && !nums.get(22).isEnabled()
                    && !nums.get(23).isEnabled() && !nums.get(24).isEnabled()) {
                if (crosses.get(4).getVisibility() == View.INVISIBLE) {
                    crosses.get(4).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(0).isEnabled() && !nums.get(5).isEnabled() && !nums.get(10).isEnabled()
                    && !nums.get(15).isEnabled() && !nums.get(20).isEnabled()) {
                if (crosses.get(5).getVisibility() == View.INVISIBLE) {
                    crosses.get(5).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(1).isEnabled() && !nums.get(6).isEnabled() && !nums.get(11).isEnabled()
                    && !nums.get(16).isEnabled() && !nums.get(21).isEnabled()) {
                if (crosses.get(6).getVisibility() == View.INVISIBLE) {
                    crosses.get(6).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(2).isEnabled() && !nums.get(7).isEnabled() && !nums.get(12).isEnabled()
                    && !nums.get(17).isEnabled() && !nums.get(27).isEnabled()) {
                if (crosses.get(7).getVisibility() == View.INVISIBLE) {
                    crosses.get(7).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(3).isEnabled() && !nums.get(8).isEnabled() && !nums.get(13).isEnabled()
                    && !nums.get(18).isEnabled() && !nums.get(28).isEnabled()) {
                if (crosses.get(8).getVisibility() == View.INVISIBLE) {
                    crosses.get(8).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(4).isEnabled() && !nums.get(9).isEnabled() && !nums.get(14).isEnabled()
                    && !nums.get(19).isEnabled() && !nums.get(24).isEnabled()) {
                if (crosses.get(9).getVisibility() == View.INVISIBLE) {
                    crosses.get(9).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(0).isEnabled() && !nums.get(6).isEnabled() && !nums.get(12).isEnabled()
                    && !nums.get(18).isEnabled() && !nums.get(24).isEnabled()) {
                if (crosses.get(10).getVisibility() == View.INVISIBLE) {
                    crosses.get(10).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (!nums.get(4).isEnabled() && !nums.get(8).isEnabled() && !nums.get(12).isEnabled()
                    && !nums.get(16).isEnabled() && !nums.get(20).isEnabled()) {
                if (crosses.get(11).getVisibility() == View.INVISIBLE) {
                    crosses.get(11).setVisibility(View.VISIBLE);
                    bingo++;
                }
            }

            if (bingo>=5)
                Toast.makeText(this, "BINGO", Toast.LENGTH_LONG).show();
        }
    }
}