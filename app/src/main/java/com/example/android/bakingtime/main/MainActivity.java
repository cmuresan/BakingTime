package com.example.android.bakingtime.main;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ActivityMainBinding;
import com.example.android.networkmodule.model.Recipe;
import com.example.android.networkmodule.network.ApiImpl;
import com.example.android.networkmodule.network.ApiInterface;
import com.example.android.networkmodule.network.CallbackInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String RECIPES_PREFERENCE_NAME = "MainActivity.RECIPES_PREFERENCE_NAME";
    public static final String RECIPES_PREFERENCE_KEY = "MainActivity.RECIPES_PREFERENCE_KEY";
    private static final String RV_CURRENT_VISIBLE_POSITION = "MainActivity.RV_CURRENT_VISIBLE_POSITION";
    private ActivityMainBinding binding;
    private RecipesAdapter recipesAdapter;
    private long currentVisiblePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (savedInstanceState != null) {
            currentVisiblePosition = savedInstanceState.getLong(RV_CURRENT_VISIBLE_POSITION);
        }

        initRecyclerView();
        getRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.recipesRecyclerView.getLayoutManager()
                .scrollToPosition((int) currentVisiblePosition);
        currentVisiblePosition = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCurrentVisiblePosition();
    }

    private void saveCurrentVisiblePosition() {
        currentVisiblePosition = ((LinearLayoutManager) binding.recipesRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
    }

    private void initRecyclerView() {
        recipesAdapter = new RecipesAdapter(this);

        binding.recipesRecyclerView.setHasFixedSize(true);
        binding.recipesRecyclerView.setAdapter(recipesAdapter);
        getLocallySavedRecipes();
    }

    private void getLocallySavedRecipes() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.RECIPES_PREFERENCE_NAME, MODE_PRIVATE);
        String recipeJson = prefs.getString(MainActivity.RECIPES_PREFERENCE_KEY, null);

        if (recipeJson != null) {
            Type type = new TypeToken<List<Recipe>>() {
            }.getType();
            List<Recipe> recipes = new Gson().fromJson(recipeJson, type);
            recipesAdapter.setRecipes(recipes);
        }
    }

    private void getRecipes() {
        ApiInterface apiInterface = new ApiImpl();
        apiInterface.getRecipes(recipesApiCallbackInterface);
    }

    private final CallbackInterface<List<Recipe>> recipesApiCallbackInterface = new CallbackInterface<List<Recipe>>() {
        @Override
        public void success(List<Recipe> response) {
            recipesAdapter.setRecipes(response);
            saveRecipes(response);
        }

        @Override
        public void failure(String errorMessage, String errorCode) {
            Toast.makeText(MainActivity.this, R.string.failure, Toast.LENGTH_SHORT).show();
        }
    };

    private void saveRecipes(List<Recipe> response) {
        SharedPreferences.Editor prefs = getSharedPreferences(RECIPES_PREFERENCE_NAME, MODE_PRIVATE).edit();
        prefs.putString(RECIPES_PREFERENCE_KEY, new Gson().toJson(response));
        prefs.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCurrentVisiblePosition();
        outState.putLong(RV_CURRENT_VISIBLE_POSITION, currentVisiblePosition);
    }
}
