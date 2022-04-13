package com.example.explorejournal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyRecipeViewActivity extends AppCompatActivity implements MyRecipeAdapter.ItemClickListener{
    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    List<Recipe> myRecipesList;
    MyRecipeAdapter adapter;
    String loggedInName;
    String loggedInGoogleUID;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe_view);

        loggedInName = getIntent().getStringExtra("name");
        loggedInGoogleUID = getIntent().getStringExtra("google_uid");
        TextView welcomeMessage = findViewById(R.id.WelcomeMessage);
        welcomeMessage.setText("Hello, " + loggedInName + "!\n(" + loggedInGoogleUID + ")");

        RecyclerView recyclerView = findViewById(R.id.ExampleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getMyRecipes();
        adapter = new MyRecipeAdapter(this, myRecipesList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    ((LinearLayoutManager)(recyclerView.getLayoutManager())).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        Button here = findViewById(R.id.nav_button_my_recipes);
        here.setBackgroundColor(getResources().getColor(R.color.purple_light));
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    // Fetch JSON array of recipes from server, convert it into a list of Recipe objects,
    // and store in the myRecipes field
    public void getMyRecipes(){

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {

                            URL url = new URL("http://10.0.2.2:3000/myrecipes?id=" + loggedInGoogleUID);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            // Handle response
                            int responsecode = conn.getResponseCode();
                            if(responsecode != 200){
                                throw new IllegalStateException();
                            }

                            List<Recipe> allRecipes = new ArrayList<>();

                            Scanner in = new Scanner(url.openStream());
                            while(in.hasNext()){
                                String line = in.nextLine();
                                JSONArray data = new JSONObject(line).getJSONArray("data");
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
                            }

                            myRecipesList = allRecipes;

                        }
                        catch (Exception e) {
                            Log.v("Express", e.toString());
                        }
                    }
            );

            executor.shutdown();
            boolean timeout = executor.awaitTermination(2, TimeUnit.SECONDS);
            if(timeout){
                Log.v("Timeout", "timeout in MyRecipeView");
            }
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
        }
    }

    public void onNavButtonClick(View view) {
        if(view.getId() == R.id.nav_button_global_recipes){
            Intent loggedInIntent = new Intent(this, GlobalRecipeViewActivity.class);
            loggedInIntent.putExtra("google_uid", loggedInGoogleUID);
            loggedInIntent.putExtra("name", loggedInName);
            startActivity(loggedInIntent);
        } else if(view.getId() == R.id.nav_button_global_recipes) {
            // Nothing
        }
    }


}