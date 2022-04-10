package com.example.explorejournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Used to render recipes in the global list
public class GlobalRecipeAdapter extends RecyclerView.Adapter<GlobalRecipeAdapter.ViewHolder>{

    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    private final LayoutInflater inflater;
    private final List<Recipe> data;
    private ItemClickListener clickListener;

    public GlobalRecipeAdapter(Context context, List<Recipe> data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public GlobalRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.global_recipe_view, parent, false);
        return new GlobalRecipeAdapter.ViewHolder(view);
    }

    @Override
    // This function executes when the holder is assigned to position 'position'
    // so we do any modifications necessary to give it the data for that spot
    public void onBindViewHolder(@NonNull GlobalRecipeAdapter.ViewHolder holder, int position) {
        holder.recipeNameView.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Recipe getItem(int position) {
        return data.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView recipeNameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameView = itemView.findViewById(R.id.RecipeName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
