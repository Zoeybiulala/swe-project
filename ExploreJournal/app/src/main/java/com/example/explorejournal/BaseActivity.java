package com.example.explorejournal;

import static com.example.explorejournal.RealmApp.app;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.mongodb.User;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miLogout) {
            Log.i("AUTH", "Logout");
            User user = app.currentUser();
            assert user != null;
            user.logOutAsync(result -> {
                if (result.isSuccess()) {
                    Log.v("AUTH", "Successfully logged out.");
                } else {
                    Log.e("AUTH", result.getError().toString());
                }
            });
            Intent loggedOutIntent = new Intent(this, MainActivity.class);
            startActivity(loggedOutIntent);

        }
        return super.onOptionsItemSelected(item);
    }
}
