package com.example.android.networkmodule.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cristian on 3/14/2018.
 */

abstract class RestCallback<T> implements Callback<T> {

    protected abstract void success(T response);

    protected abstract void failure(String errorMessage, @Nullable String errorCode);

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            success(response.body());
        } else {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(response.errorBody().string());
                failure(jsonObject.getString("status_message"),
                        jsonObject.getString("status_code"));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                failure(response.message(), null);
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        failure(t.getMessage(), null);
    }
}
