package com.androcoders.bingo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity {

    private Button create;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        create=findViewById(R.id.create_btn);
        logout=findViewById(R.id.logout_btn);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent()
                startActivity(getIntent().setClass(getApplicationContext(),CreateRoom.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setClass(getApplicationContext(),Loginpage.class);
                intent.putExtra("auto",false);
                FirebaseAuth auth= FirebaseAuth.getInstance();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestId()
                        .requestProfile()
                        .build();

                GoogleSignInClient user = GoogleSignIn.getClient(getApplicationContext(),gso);
                user.signOut();
                startActivity(intent);
                finish();
            }
        });

        getSupportActionBar().hide();
        Toast.makeText(getApplicationContext(), ""+getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
    }
}