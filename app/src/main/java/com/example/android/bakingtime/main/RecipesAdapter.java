package com.example.android.bakingtime.main;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ItemRecipeBinding;
import com.example.android.bakingtime.details.RecipeDetailsActivity;
import com.example.android.networkmodule.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private final Context context;
    private List<Recipe> recipes;

    public RecipesAdapter(Context context) {
        this.context = context;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, final int position) {
        holder.bindData(recipes.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recipeDetailsIntent = new Intent(context, RecipeDetailsActivity.class);
                recipeDetailsIntent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE, recipes.get(position));
                context.startActivity(recipeDetailsIntent);
            }
        });
        if (position == recipes.size() - 1) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.bottomMargin = (int) context.getResources().getDimension(R.dimen.margin_default);
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ItemRecipeBinding binding;
        private static final String PIE = "pie";
        private static final String BROWNIE = "brownie";
        private static final String CHEESE = "cheese";
        private static final String CAKE = "cake";

        RecipeViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindData(Recipe recipe) {
            binding.recipeTitle.setText(recipe.getName());
            binding.recipeServings.setText(String.format(context.getString(R.string.servings),
                    recipe.getServings()));

            if (!TextUtils.isEmpty(recipe.getImage())) {
                Picasso.with(context)
                        .load(recipe.getImage())
                        .placeholder(R.drawable.recipe_placeholder)
                        .error(R.string.recipe_image_error)
                        .into(binding.recipeImage);
            } else {
                bindLocalImage(recipe.getName());
            }
        }

        private void bindLocalImage(String name) {
            if (name.toLowerCase().contains(PIE)) {
                binding.recipeImage.setImageResource(R.drawable.nutella_pie);
            } else if (name.toLowerCase().contains(BROWNIE)) {
                binding.recipeImage.setImageResource(R.drawable.brownies);
            } else if (name.toLowerCase().contains(CHEESE)) {
                binding.recipeImage.setImageResource(R.drawable.cheesecake);
            } else if (name.toLowerCase().contains(CAKE)) {
                binding.recipeImage.setImageResource(R.drawable.yellow_cake);
            } else {
                binding.recipeImage.setImageResource(R.drawable.recipe_placeholder);
            }
        }
    }
}
