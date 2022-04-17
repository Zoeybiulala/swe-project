package com.example.explorejournal;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecipeAttemptActivity extends BaseActivity {
    private ArrayList<Attempt> attemptsArrayList;
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

        // display attempts
        listView = findViewById(R.id.attempt_list_view);

        populateList();

        AttemptAdapter attemptsAdapter = new AttemptAdapter(this,attemptsArrayList);
        listView.setAdapter(attemptsAdapter);
    }
    private void populateList(){

        JSONObject result = new ServerConnection("http://10.0.2.2:3000").get("users?google_uid=" + "example");


        try {
            if(result != null && result.getString("status").equals("success")){
                JSONArray data = result.getJSONArray("data");
                ArrayList<Attempt> allAttempts = new ArrayList<>();
//                for(int i=0; i<data.length(); i++){
////                    JSONObject savedRecipes = data.getJSONObject(i);
////                    JSONObject attempts= savedRecipes.getString("_id");
//
////                    allAttempts.add(new Attempt(note, date, rating));
//                }
                attemptsArrayList = allAttempts;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
    
}
