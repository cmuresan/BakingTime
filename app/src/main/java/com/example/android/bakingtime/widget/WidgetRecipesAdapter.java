package com.example.android.bakingtime.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ItemRecipeWidgetBinding;
import com.example.android.networkmodule.model.Recipe;

import java.util.List;

class WidgetRecipesAdapter extends RecyclerView.Adapter<WidgetRecipesAdapter.RecipeViewHolder> {
    private final Context context;
    private List<Recipe> recipes;
    private OnRecipeItemClickListener onRecipeItemClickListener;

    WidgetRecipesAdapter(Context context) {
        this.context = context;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public void setClickListener(OnRecipeItemClickListener onRecipeItemClickListener) {
        this.onRecipeItemClickListener = onRecipeItemClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_widget, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeViewHolder holder, int position) {
        if (holder.binding != null) {
            holder.binding.recipeName.setText(recipes.get(position).getName());

            holder.binding.recipeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecipeItemClickListener != null) {
                        onRecipeItemClickListener.onItemClick(holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeWidgetBinding binding;

        RecipeViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
