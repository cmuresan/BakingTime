package com.example.android.bakingtime.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.ActivityMainBinding;
import com.example.android.networkmodule.model.Recipe;
import com.example.android.networkmodule.network.ApiImpl;
import com.example.android.networkmodule.network.ApiInterface;
import com.example.android.networkmodule.network.CallbackInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecipesAdapter recipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initRecyclerView();
        getRecipes();
    }

    private void initRecyclerView() {
        recipesAdapter = new RecipesAdapter(this);

//        binding.recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recipesRecyclerView.setHasFixedSize(true);
        binding.recipesRecyclerView.setAdapter(recipesAdapter);
    }

    private void getRecipes() {
        ApiInterface apiInterface = new ApiImpl();
        apiInterface.getRecipes(recipesApiCallbackInterface);
    }

    private final CallbackInterface<List<Recipe>> recipesApiCallbackInterface = new CallbackInterface<List<Recipe>>() {
        @Override
        public void success(List<Recipe> response) {
            recipesAdapter.setRecipes(response);
        }

        @Override
        public void failure(String errorMessage, String errorCode) {
            Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
        }
    };
}
