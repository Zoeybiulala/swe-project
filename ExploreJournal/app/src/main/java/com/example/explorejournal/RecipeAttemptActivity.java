package com.example.explorejournal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecipeAttemptActivity extends BaseActivity {

    public static final int LAUNCH_NEW_ATTEMPT_LOG = 1;

    private ArrayList<Attempt> attemptsArrayList;
    private String recipe_id;
    private String google_uid;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe_each_view);

        // display recipe
        String name = getIntent().getStringExtra("name");
        TextView recipeName= findViewById(R.id.recipe_name);
        recipeName.setText(name);

        String url = getIntent().getStringExtra("url");
        TextView recipeUrl= findViewById(R.id.recipe_url);
        recipeUrl.setText(url);

        String description = getIntent().getStringExtra("description");
        TextView recipeDescription= findViewById(R.id.recipe_description);
        recipeDescription.setText(description);

        recipe_id = getIntent().getStringExtra("recipe_id");
        google_uid = getIntent().getStringExtra("google_uid");

        // display attempts
        listView = findViewById(R.id.attempt_list_view);

        populateList();

        AttemptAdapter attemptsAdapter = new AttemptAdapter(this,attemptsArrayList);
        listView.setAdapter(attemptsAdapter);
    }
    private void populateList(){

        JSONObject result = new ServerConnection("http://10.0.2.2:3000").get("userattempts?uid=" + google_uid + "&rid=" + recipe_id);


        try {
            if(result != null && result.getString("status").equals("success")){
                JSONArray data = result.getJSONArray("data");
                Log.v("recipe attempts", data.toString());
                attemptsArrayList = new ArrayList<>();
                for(int i=0; i<data.length(); i++){
                    JSONObject currentAttempt = data.getJSONObject(i);
                    String note = currentAttempt.getString("note");
                    String dateString = currentAttempt.getString("date");
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    Date attemptDate = dateFormatter.parse(dateString);
                    double attemptRating = currentAttempt.getDouble("rating");
                    attemptsArrayList.add(new Attempt(note, attemptDate, attemptRating));
                }
            } else {
                attemptsArrayList = new ArrayList<>();
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public void onNewAttemptButtonClick(View view){
        Log.v("attempts","new attempt button pressed");
        // take recipe id and login info, launch new activity
        Intent logNewAttempt= new Intent(this, LogNewAttemptActivity.class);

        logNewAttempt.putExtra("recipe_id", recipe_id);
        logNewAttempt.putExtra("google_uid", getIntent().getStringExtra("google_uid"));
        logNewAttempt.putExtra("recipe_name", getIntent().getStringExtra("name"));
        startActivityForResult(logNewAttempt, LAUNCH_NEW_ATTEMPT_LOG);
    }

    public void onBackButtonClick(View view){
        Log.v("attempts","back button pressed");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        populateList();

        AttemptAdapter attemptsAdapter = new AttemptAdapter(this,attemptsArrayList);
        listView.setAdapter(attemptsAdapter);
    }
}
