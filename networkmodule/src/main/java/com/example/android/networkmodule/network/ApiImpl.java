package com.example.android.networkmodule.network;

import com.example.android.networkmodule.model.Recipe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Cristian on 3/11/2018.
 */

public class ApiImpl implements ApiInterface {

    /**
     * API URL
     */
    private static final String API_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    private static final long TIME_TO_CONNECT = 30;
    private final RecipesApi apiService;

    public ApiImpl() {
        Retrofit retrofit = buildRetrofit();
        apiService = retrofit.create(RecipesApi.class);
    }

    private Retrofit buildRetrofit() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_TO_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIME_TO_CONNECT, TimeUnit.SECONDS)
                .writeTimeout(TIME_TO_CONNECT, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build();
    }

    @Override
    public void getRecipes(CallbackInterface<List<Recipe>> callback) {
        Call<List<Recipe>> call = apiService.getRecipes();
        call.enqueue(new RestCallbackImpl<>(callback));
    }
}
