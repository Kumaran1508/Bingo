package com.androcoders.bingo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class JoinRoomActivity extends AppCompatActivity {

    EditText code;
    Button join;
    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
         code=findViewById(R.id.roomcode);
         join=findViewById(R.id.join);
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        join.setEnabled(false);
        getSupportActionBar().hide();


        join.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 HashMap<String,Object> player=new HashMap<>();
                 player.put("bingo",0);
                 player.put("player_name",account.getDisplayName());
                 player.put("score",0);

                 firebaseFirestore.collection("rooms").document(code.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     @Override
                     public void onSuccess(DocumentSnapshot documentSnapshot) {
                         try {
                             if(!documentSnapshot.getBoolean("isStarted")) {
                                 firebaseFirestore.collection("rooms").document(code.getText().toString()).collection("players").document(account.getId()).set(player);
                                 Intent intent = new Intent();
                                 intent.putExtra("create",false);
                                 intent.putExtra("room_key",Integer.valueOf(code.getText().toString()));
                                 intent.setClass(getApplicationContext(),CreateRoom.class);
                                 startActivity(intent);
                             }
                             else{
                                 Toast.makeText(JoinRoomActivity.this, "You can't join now, The game has already started ", Toast.LENGTH_LONG).show();
                             }
                         }
                         catch(Exception e){
                             Toast.makeText(JoinRoomActivity.this, "Room does not exist", Toast.LENGTH_SHORT).show();
                         }

                     }
                 });


             }
        });

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 5) {
                    join.setEnabled(true);
                } else {
                    join.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}