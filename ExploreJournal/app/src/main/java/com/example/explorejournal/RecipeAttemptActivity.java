package com.example.explorejournal;

import android.os.Bundle;
import android.widget.TextView;

public class RecipeAttemptActivity extends BaseActivity {
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
    }
}
