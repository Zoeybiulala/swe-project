package com.example.explorejournal;

import static com.example.explorejournal.RealmApp.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.auth.GoogleAuthType;

public class MainActivity extends AppCompatActivity {

    private  GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> resultLauncher;
    private String loggedInUser = null;
    private boolean serverPingResultAtLaunch = false;

    @SuppressLint("SetTextI18n")
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

        // Check if server is running
        if(ping()){
            TextView serverRunning = findViewById(R.id.server_status);
            serverRunning.setText("Server running at launch");
        }
    }

    private void signIn() {
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
                        assert user != null;
                        System.out.println(user.getId());
                        System.out.println("profile" + user.getProfile().getName());
                        loggedInUser = user.getId();

                        Intent loggedInIntent = new Intent(this, GlobalRecipeView.class);
                        loggedInIntent.putExtra("google_uid", loggedInUser);
                        loggedInIntent.putExtra("name", user.getProfile().getName());
                        startActivity(loggedInIntent);
                        Log.v("AUTH",
                                "Successfully logged in to MongoDB Realm using Google OAuth.");
                    } else {
                        Log.e("AUTH",
                                "Failed to log in to MongoDB Realm: ", it.getError());
                    }
                });
            } else {
                Log.e("AUTH", "Google Auth failed: "
                        + (completedTask.getException() == null? "null exception" : completedTask.getException().toString()));

                // TODO toast
            }
        } catch (ApiException e) {
            Log.w("AUTH", "Failed to log in with Google OAuth: " + e.getMessage());
            // TODO toast
        }
    }

    private boolean ping() {

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            URL url = new URL("http://10.0.2.2:3000/ping");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();
                            serverPingResultAtLaunch = true;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            serverPingResultAtLaunch = false;
                        }
                    }
            );

            // Returns true if, after telling executor to shutdown,
            // all tasks are done in 2 seconds, AND connection didn't throw error
            executor.shutdown();
            boolean completed = (executor.awaitTermination(2, TimeUnit.SECONDS) && serverPingResultAtLaunch);
            Log.v("ping", Boolean.toString(completed));
            return (completed);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
        }
        return false;
    }
}