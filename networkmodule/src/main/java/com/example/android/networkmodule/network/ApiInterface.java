package com.example.android.networkmodule.network;

import com.example.android.networkmodule.model.Recipe;

import java.util.List;

/**
 * Created by Cristian on 3/6/2018.
 */

public interface ApiInterface {
    void getRecipes(CallbackInterface<List<Recipe>> result);
}
