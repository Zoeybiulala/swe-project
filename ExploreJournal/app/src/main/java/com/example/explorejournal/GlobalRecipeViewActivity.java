package com.example.explorejournal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        if (view.getId() != R.id.SaveRecipe) {
            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        } else {
            // recipe
            Recipe recipe = adapter.getItem(position);

            JSONObject result = new ServerConnection("http://10.0.2.2:3000").get("user_add_recipe?uid=" + loggedInGoogleUID + "&rid=" + recipe.id);

            try {

                if (result != null && result.getString("status").equals("success")) {
                    Toast.makeText(this, "Recipe \"" + recipe.getName() + "\" has been added to your library", Toast.LENGTH_SHORT).show();
                } else if (result != null && result.getString("status").equals("already added")) {
                    Toast.makeText(this, "Recipe \"" + recipe.getName() + "\" is already in your library!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
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

    /***
     * for search functionality *
                                ***/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_with_search,menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

}