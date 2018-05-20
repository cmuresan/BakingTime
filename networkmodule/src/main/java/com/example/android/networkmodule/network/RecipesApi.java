package com.example.android.networkmodule.network;

import com.example.android.networkmodule.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Cristian on 3/11/2018.
 */

interface RecipesApi {
    @GET("baking.json")
    Call<List<Recipe>> getRecipes();
}
