package com.example.explorejournal;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

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

        attemptsArrayList = populateList();

        AttemptAdapter attemptsAdapter = new AttemptAdapter(this,attemptsArrayList);
        listView.setAdapter(attemptsAdapter);
    }
    private ArrayList<Attempt> populateList(){

        ArrayList<Attempt> list = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            Attempt attempt = new Attempt("good", Calendar.getInstance().getTime(), 1000);
            list.add(attempt);
        }

        return list;
    }
    
}
