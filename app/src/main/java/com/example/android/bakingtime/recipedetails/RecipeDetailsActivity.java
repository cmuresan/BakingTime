package com.example.android.bakingtime.recipedetails;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ActivityRecipeDetailsBinding;
import com.example.android.networkmodule.model.Ingredient;
import com.example.android.networkmodule.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "RecipeDetailsActivity.EXTRA_RECIPE";
    private Recipe recipe;
    ActivityRecipeDetailsBinding binding;
    private StepsAdapter stepsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        binding = DataBindingUtil.setContentView(this,
                R.layout.activity_recipe_details);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE)) {
            recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        }

        if (recipe != null) {
            setTitle(recipe.getName());
            initRecyclerView();
            bindData();
        } else {
            Toast.makeText(this, getString(R.string.recipe_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setTitle(String name) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(name);
        }
    }

    private void bindData() {
        setIngredients();

        stepsAdapter.setSteps(recipe.getSteps());
    }

    private void setIngredients() {
        List<String> ingredients = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredients.add(ingredient.toString());
        }
        String finalString = TextUtils.join("\n", ingredients);
        binding.recipeDetails.ingredientsContent.setText(finalString);
    }

    private void initRecyclerView() {
        stepsAdapter = new StepsAdapter(this);

        binding.recipeDetails.stepsRecyclerView.setHasFixedSize(true);
        binding.recipeDetails.stepsRecyclerView.setAdapter(stepsAdapter);
    }
}
