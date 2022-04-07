package com.example.explorejournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.explorejournal.simplelistexample.RecyclerViewStringListAdapter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class GlobalRecipeView extends AppCompatActivity implements GlobalRecipeAdapter.ItemClickListener{
    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    List<Recipe> allRecipesList;
    GlobalRecipeAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_recipe_view);

        String name = getIntent().getStringExtra("name");
        String google_uid = getIntent().getStringExtra("google_uid");
        TextView welcomeMessage = findViewById(R.id.WelcomeMessage);
        welcomeMessage.setText("Hello, " + name + "!\n(" + google_uid + ")");

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
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    // Fetch JSON array of recipes from server, convert it into a list of Recipe objects,
    // and store in the allRecipes field
    public void getAllRecipes(){

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {

                            URL url = new URL("http://10.0.2.2:3000/api");

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
                                JSONArray data = new JSONArray(line);
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

                            allRecipesList = allRecipes;

                        }
                        catch (Exception e) {
                            Log.v("Express", e.toString());
                        }
                    }
            );

            executor.shutdown();
            boolean timeout = executor.awaitTermination(2, TimeUnit.SECONDS);
            if(timeout){
                Log.v("Timeout", "timeout in GlobalRecipeView");
            }
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
        }
    }

    public void refreshGlobalRecipeView(View view) {
        getAllRecipes();
        List<Recipe> allRecipes = allRecipesList;
        RecyclerView recyclerView = findViewById(R.id.ExampleRecyclerView);
        adapter = new GlobalRecipeAdapter(this, allRecipesList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
}