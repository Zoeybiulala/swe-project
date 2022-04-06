package com.example.explorejournal.simplelistexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.explorejournal.R;

public class RecyclerViewStringAdapter extends RecyclerView.Adapter<RecyclerViewStringAdapter.ViewHolder>{

    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    private LayoutInflater mInflater;

    public RecyclerViewStringAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.example_scroll_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // We would set the data here if it were not a fixed string
        return;
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
