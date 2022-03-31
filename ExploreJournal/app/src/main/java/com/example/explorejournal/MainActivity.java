package com.example.explorejournal;

import static com.example.explorejournal.RealmApp.app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import io.realm.mongodb.Credentials;
import io.realm.mongodb.auth.GoogleAuthType;

public class MainActivity extends AppCompatActivity {

    private  GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        resultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            handleSignInResult(task);
                        });

        findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener)(it -> MainActivity.this.signIn()));
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        System.out.println("here1.5");
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == 100) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

    private void signIn() {
        System.out.println("here");
        Intent signInIntent = googleSignInClient.getSignInIntent();

        resultLauncher.launch(signInIntent);

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        System.out.println("here2");
        try {
            if (completedTask.isSuccessful()) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                String token = account.getIdToken();
                System.out.println("here3");
                Credentials googleCredentials =
                        Credentials.google(token, GoogleAuthType.ID_TOKEN);
                System.out.println("here4");
                app.loginAsync(googleCredentials, it -> {
                    if (it.isSuccess()) {
                        startActivity(new Intent(this, SampleResult.class));
                        Log.v("AUTH",
                                "Successfully logged in to MongoDB Realm using Google OAuth.");
                    } else {
                        Log.e("AUTH",
                                "Failed to log in to MongoDB Realm: ", it.getError());
                    }
                });
            } else {
                Log.e("AUTH", "Google Auth failed: "
                        + completedTask.getException().toString());
            }
        } catch (ApiException e) {
            Log.w("AUTH", "Failed to log in with Google OAuth: " + e.getMessage());
        }
    }
}