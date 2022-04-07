package com.example.explorejournal.simplelistexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.example.explorejournal.R;
import java.util.LinkedList;

public class ScrollList extends AppCompatActivity {

    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_list);

        RecyclerView recyclerView = findViewById(R.id.ExampleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create fixed text
        // recyclerView.setAdapter(new RecyclerViewStringAdapter(this));

        LinkedList<String> exampleData = new LinkedList<>();
        for(int i=0; i<100; i++){
            exampleData.add(Integer.toString(i));
        }
        recyclerView.setAdapter(new RecyclerViewStringListAdapter(this, exampleData));

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    ((LinearLayoutManager)(recyclerView.getLayoutManager())).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
        }




    }

}