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

import org.bson.Document;

import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.auth.GoogleAuthType;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

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

                        // TODO code to insert user into database!
                        User user = app.currentUser();
                        System.out.println(user.getId());
                        System.out.println("profile" + user.getProfile().getName());

                        MongoClient mongoClient =
                                user.getMongoClient("mongodb-atlas"); // service for MongoDB Atlas cluster containing custom user data
                        MongoDatabase mongoDatabase =
                                mongoClient.getDatabase("exploreJournalDb");
                        MongoCollection<Document> mongoCollection =
                                mongoDatabase.getCollection("users");

                        /*
                        ERROR
                        Unable to insert custom user data. Error: SERVICE_UNKNOWN(realm::app::ServiceError:-1):
                        no rule exists for namespace 'custom-user-data-database.custom-user-data-collection'
                         */
                        mongoCollection.insertOne(
                                new Document("user-id-field", user.getId()).append("name", user.getProfile().getName()).append("_partition", "partition"))
                                .getAsync(result -> {
                                    if (result.isSuccess()) {
                                        Log.v("EXAMPLE", "Inserted custom user data document. _id of inserted document: "
                                                + result.get().getInsertedId());
                                    } else {
                                        Log.e("EXAMPLE", "Unable to insert custom user data. Error: " + result.getError());
                                    }
                                });

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

                // TODO toast
            }
        } catch (ApiException e) {
            Log.w("AUTH", "Failed to log in with Google OAuth: " + e.getMessage());
            // TODO toast
        }
    }
}