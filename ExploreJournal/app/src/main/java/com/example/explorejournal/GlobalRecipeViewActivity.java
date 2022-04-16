package com.example.explorejournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GlobalRecipeViewActivity extends BaseActivity implements GlobalRecipeAdapter.ItemClickListener{
    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    List<Recipe> allRecipesList;
    GlobalRecipeAdapter adapter;
    String loggedInGoogleUID;
    String loggedInName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_recipe_view);

        loggedInName = getIntent().getStringExtra("name");
        loggedInGoogleUID = getIntent().getStringExtra("google_uid");
        TextView welcomeMessage = findViewById(R.id.WelcomeMessage);
        welcomeMessage.setText("Hello, " + loggedInName + "!\n(" + loggedInGoogleUID + ")");

        RecyclerView recyclerView = findViewById(R.id.ExampleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllRecipes();
        adapter = new GlobalRecipeAdapter(this, allRecipesList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    ((LinearLayoutManager)(recyclerView.getLayoutManager())).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        Button here = findViewById(R.id.nav_button_global_recipes);
        here.setBackgroundColor(getResources().getColor(R.color.purple_light));
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    // Fetch JSON array of recipes from server, convert it into a list of Recipe objects,
    // and store in the allRecipes field
    public void getAllRecipes(){

        JSONObject result = new ServerConnection("http://10.0.2.2:3000").get("api");
        try {
            if(result != null && result.getString("status").equals("success")){
                JSONArray data = result.getJSONArray("data");
                List<Recipe> allRecipes = new ArrayList<>();
                for(int i=0; i<data.length(); i++){
                    JSONObject jsonRecipe = data.getJSONObject(i);
                    String id = jsonRecipe.getString("_id");
                    String recipeUrl = jsonRecipe.getString("url");
                    String description = jsonRecipe.getString("description");
                    String name = jsonRecipe.getString("name");
                    JSONArray jsonTags = jsonRecipe.getJSONArray("tags");
                    JSONArray jsonUsers = jsonRecipe.getJSONArray("list_of_users");
                    // Parse json array of tags into list of strings
                    List<String> tags = new ArrayList<>();
                    List<String> users = new ArrayList<>();
                    for(int j=0; j<jsonTags.length(); j++){
                        tags.add(jsonTags.getString(j));
                    }
                    // Parse json array of user ids into list of strings
                    for(int j=0; j<jsonUsers.length(); j++){
                        users.add(jsonUsers.getString(j));
                    }
                    allRecipes.add(new Recipe(id, recipeUrl, description, name, tags, users));
                }
                allRecipesList = allRecipes;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public void refreshGlobalRecipeView(View view) {
        getAllRecipes();
        RecyclerView recyclerView = findViewById(R.id.ExampleRecyclerView);
        adapter = new GlobalRecipeAdapter(this, allRecipesList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void onNavButtonClick(View view) {
        if(view.getId() == R.id.nav_button_my_recipes) {
            finish();
        }
    }
}