package com.example.explorejournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.explorejournal.R;
import com.example.explorejournal.simplelistexample.RecyclerViewStringAdapter;
import com.example.explorejournal.simplelistexample.RecyclerViewStringListAdapter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class GlobalRecipeView extends AppCompatActivity {
    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    List<Recipe> allRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_list);

        RecyclerView recyclerView = findViewById(R.id.ExampleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Recipe> allRecipes = getAllRecipes();
        List<String> allRecipeStrings = new ArrayList<String>();

        if (allRecipes != null) {
            for(int i=0; i<allRecipes.size(); i++){
                allRecipeStrings.add(allRecipes.get(i).toString());
            }
        }
        recyclerView.setAdapter(new RecyclerViewStringListAdapter(this, allRecipeStrings));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager)(recyclerView.getLayoutManager())).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    // Fetch JSON array of recipes from server, and convert it into a list of Recipe objects
    public List<Recipe> getAllRecipes(){

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

                            List<Recipe> allRecipes = new ArrayList<Recipe>();

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
                                    List<String> tags = new ArrayList<String>();
                                    List<String> users = new ArrayList<String>();
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

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
        }
        return allRecipesList;
    }
}