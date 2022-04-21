package com.example.explorejournal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Used to render recipes in the global list
public class GlobalRecipeAdapter extends RecyclerView.Adapter<GlobalRecipeAdapter.ViewHolder> implements Filterable {

    // Referenced from here: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

    private final LayoutInflater inflater;
    private final List<Recipe> data;
    private final List<Recipe> myList;
    private ItemClickListener clickListener;

    public GlobalRecipeAdapter(Context context, List<Recipe> data){
        this.inflater = LayoutInflater.from(context);
        this.myList = data;
        this.data = new ArrayList<>(data); //full list
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
        holder.recipeNameView.setText(myList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    public Recipe getItem(int position) {
        return myList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView recipeNameView;
        Button saveRecipeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameView = itemView.findViewById(R.id.RecipeName);
            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.RecipeName);
            saveRecipeButton = itemView.findViewById(R.id.SaveRecipe);
            saveRecipeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }
//
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private final Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(data);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Recipe r: data) {
                    if (r.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(r);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            myList.clear();
            myList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

}
