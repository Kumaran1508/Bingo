package com.androcoders.bingo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
    private boolean gameover = false;
    private String current_turn;
    private ArrayList<Player> players = new ArrayList<>();
    private FirebaseFirestore firestore =FirebaseFirestore.getInstance();
    private int roomkey;
    private String playerid="",owner;
    private TextView playerturn,filling;
    private ArrayList<Button> nums = new ArrayList<>();
    private ArrayList<View> crosses = new ArrayList<>();
    private int bingo = 0;
    private Button B,I,N,G,O;




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
        filling = findViewById(R.id.filling);

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

        B = findViewById(R.id.b_btn);
        I = findViewById(R.id.i_btn);
        N = findViewById(R.id.n_btn);
        G = findViewById(R.id.g_btn);
        O = findViewById(R.id.o_btn);

        firestore.collection("rooms").document(String.valueOf(roomkey)).get().addOnSuccessListener(GameActivity.this,new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                owner = documentSnapshot.getString("owner");

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        firestore.collection("rooms").document(String.valueOf(roomkey)).addSnapshotListener(GameActivity.this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                current_turn = value.getString("current_turn");
                for (Player player : players){
                    if(player.getPlayerid().contentEquals(current_turn))
                        playerturn.setText(player.getPlayername()+ " 's turn");
                }


                String striked_number = value.getString("striked_number");
                for (int i=0;i<numbers.length;i++){
                    Button button = findViewById(numbers[i]);
                    try {
                        if (striked_number.contentEquals(button.getText().toString()) && isReady) {
                        button.setBackground(getDrawable(R.drawable.gradient2));

                            button.setEnabled(false);
                            checkBingo();
                        }
                    } catch (Exception e) {
                        Log.d("Enable fail", e.getMessage());
                    }
                }

                try{
                    if (value.getString("filled_count").contentEquals(value.getString("total_players"))) {
                    isReady = true;
                    filling.setVisibility(View.INVISIBLE);
                    }
                }
                catch (Exception e){}

                try{
                    if(!value.getBoolean("isStarted"))
                    {
                        finishgame();
                    }
                }catch (Exception e){ }

                try {
                    if(value.getBoolean("isMatchover"))
                    {
                        Intent intent= new Intent();
                        intent.putExtra("roomkey",roomkey);
                        intent.setClass(getApplicationContext(),Resultpage.class);
                        startActivity(intent);
                        finishActivity(0);
                        finish();
                    }
                }
                catch (Exception e){}


            }

        });

        firestore.collection("rooms").document(String.valueOf(roomkey)).collection("players").get().addOnSuccessListener(GameActivity.this,new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    players.add(new Player(documentSnapshot.getString("player_name"),documentSnapshot.getId()));
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
                firestore.collection("rooms").document(String.valueOf(roomkey)).get().addOnSuccessListener(GameActivity.this,new OnSuccessListener<DocumentSnapshot>() {
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

                String next_player_id=playerid;

                for (Player player : players){
                    if (player.getPlayerid().contentEquals(playerid)){
                        if (players.indexOf(player)==players.size()-1)
                            next_player_id = players.get(0).getPlayerid();
                        else
                            next_player_id = players.get(players.indexOf(player)+1).getPlayerid();

                    }
                }


                firestore.collection("rooms").document(String.valueOf(roomkey)).update("current_turn",next_player_id);
            }

        }
    }

    void updateFilledPlayers(String count){
        int val = Integer.valueOf(count);
        firestore.collection("rooms").document(String.valueOf(roomkey)).update("filled_count",String.valueOf(val+1));
    }


    void checkBingo(){
        if (isReady){
            try {
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
                        && !nums.get(17).isEnabled() && !nums.get(22).isEnabled()) {
                    if (crosses.get(7).getVisibility() == View.INVISIBLE) {
                        crosses.get(7).setVisibility(View.VISIBLE);
                        bingo++;
                    }
                }

                if (!nums.get(3).isEnabled() && !nums.get(8).isEnabled() && !nums.get(13).isEnabled()
                        && !nums.get(18).isEnabled() && !nums.get(23).isEnabled()) {
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

                strikeBingo();

                if (bingo>=5)
                {
                    firestore.collection("rooms").document("" + roomkey).update("isStarted",false);

                }





            }catch (Exception e){
                Toast.makeText(this, "check bingo error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void strikeBingo(){
        if (bingo>0) B.setBackground(getDrawable(R.drawable.gradient_orange));
        if (bingo>1) I.setBackground(getDrawable(R.drawable.gradient_orange));
        if (bingo>2) N.setBackground(getDrawable(R.drawable.gradient_orange));
        if (bingo>3) G.setBackground(getDrawable(R.drawable.gradient_orange));
        if (bingo>4) O.setBackground(getDrawable(R.drawable.gradient_orange));

        firestore.collection("rooms").document(String.valueOf(roomkey))
                .collection("players").document(playerid).update("bingo",bingo);
    }
    private void finishgame() {

        if(owner.contentEquals(playerid) && !gameover)
        {
            gameover=true;
            firestore.collection("rooms").document(""+roomkey).update("filled_count","0","striked_number","0");

            firestore.collection("rooms").document(""+roomkey).collection("players").get().addOnSuccessListener(GameActivity.this,new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                    {
                        long score1=documentSnapshot.getLong("score");
                        long bingoscore=documentSnapshot.getLong("bingo");
                        if(bingoscore>=5)
                        {
                            score1++;
                            firestore.collection("rooms")
                                    .document(""+roomkey)
                                    .collection("players")
                                    .document(documentSnapshot.getId())
                                    .update("score",score1);
                        }


                    }

                    firestore.collection("rooms").document(""+roomkey).update("isMatchover",true);




                }
            });
        }

    }
}
