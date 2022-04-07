package com.example.explorejournal.simplelistexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.explorejournal.R;

import java.util.List;

public class RecyclerViewStringListAdapter extends RecyclerView.Adapter<RecyclerViewStringListAdapter.ViewHolder>{

    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    private final LayoutInflater inflater;
    private final List<String> data;

    public RecyclerViewStringListAdapter(Context context, List<String> data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.example_scroll_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // This function executes when the holder is assigned to position 'position'
    // so we do any modifications necessary to give it the data for that spot
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.innerTextView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView innerTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView is the view generated from example_scroll_row
            innerTextView = itemView.findViewById(R.id.FixedTextView);
        }
    }
}
