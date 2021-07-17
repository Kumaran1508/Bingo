package com.androcoders.bingo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Loginpage extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "Sign In";
    private static final String accessToken = "1058282051885-grphss423vighe3cr5pfeu1c7roefgk0.apps.googleusercontent.com";
    private static   boolean AUTO_SIGNIN = true;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        getSupportActionBar().hide();
        signInButton=findViewById(R.id.google_sign_in);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        getSupportActionBar().hide();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent signin=mGoogleSignInClient.getSignInIntent();
                  startActivityForResult(signin,RC_SIGN_IN);


            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
            AUTO_SIGNIN=getIntent().getBooleanExtra("auto",true);
        }
        catch (Exception e){

        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null && AUTO_SIGNIN){
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getId(),accessToken);
            firebaseAuth.signInWithCredential(credential);

            String user_name = account.getDisplayName();
            String prof_url;


            try {
                prof_url = account.getPhotoUrl().toString();
            }
            catch (Exception e)
            {
                prof_url = "";
            }

            String Id = account.getId();
            Intent intent = new Intent();
            intent.putExtra("name",user_name);
            intent.putExtra("url",prof_url);
            intent.putExtra("id",Id);
            intent.setClass(getApplicationContext(),Homepage.class);

            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getId(),accessToken);
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                }
                            }
                        });

                String user_name = account.getDisplayName();
                String prof_url;

                try {
                    prof_url = account.getPhotoUrl().toString();
                }
                catch (Exception e)
                {
                    prof_url = "";
                }

                String Id = account.getId();
                Intent intent = new Intent();
                intent.putExtra("name",user_name);
                intent.putExtra("url",prof_url);
                intent.putExtra("id",Id);
                intent.setClass(getApplicationContext(),Homepage.class);

                startActivity(intent);
                finish();

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this, "Sign In Failed. Try Again! \n"+e.getLocalizedMessage() , Toast.LENGTH_SHORT).show();
            }
        }
    }
}