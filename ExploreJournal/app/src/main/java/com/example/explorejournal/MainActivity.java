package com.example.explorejournal;

import static com.example.explorejournal.RealmApp.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.explorejournal.expressexample.ExpressExample;
import com.example.explorejournal.simplelistexample.ScrollList;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
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

    private void signIn() {
        System.out.println("here");
        Intent signInIntent = googleSignInClient.getSignInIntent();

        resultLauncher.launch(signInIntent);

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            if (completedTask.isSuccessful()) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                String token = account.getIdToken();
                Credentials googleCredentials =
                        Credentials.google(token, GoogleAuthType.ID_TOKEN);
                app.loginAsync(googleCredentials, it -> {
                    if (it.isSuccess()) {

                        // TODO: update UI method, follow https://www.youtube.com/watch?v=k0TUwjxr8LE

                        User user = app.currentUser();
                        System.out.println(user.getId());
                        System.out.println("profile" + user.getProfile().getName());

                        // TODO: Now that the android app has the user's id, it can use it to try to
                        // access the user's data, which will trigger the express app to create a new entry
                        // if that user does not already have one.

                        startActivity(new Intent(this, GlobalRecipeView.class));
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

                // TODO toast
            }
        } catch (ApiException e) {
            Log.w("AUTH", "Failed to log in with Google OAuth: " + e.getMessage());
            // TODO toast
        }
    }
}