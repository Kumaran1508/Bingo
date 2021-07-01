package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TimerTask task;
    Intent intent;
    private Timer time;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        time=new Timer();
        intent=new Intent();

    }

    @Override
    protected void onStart() {
        super.onStart();

        task=new TimerTask() {
            @Override
            public void run() {

                /*firebaseFirestore.collection("meta").document("data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getBoolean("is_maintenance"))
                        {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setMessage("The App is currently under maintenance.Come Again Later:)") .setTitle("OOPS!");
                            builder.setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            alert.show();


                        }
                    }
                });*/

          intent.setClass(getApplicationContext(),Loginpage.class);
          startActivity(intent);
          finish();
            }
        };
        time.schedule(task,2000);
        }
}