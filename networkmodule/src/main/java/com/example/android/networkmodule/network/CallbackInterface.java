package com.example.android.networkmodule.network;

/**
 * Created by Cristian on 3/11/2018.
 */

public interface CallbackInterface<T> {
    void success(T response);
    void failure(String errorMessage, String errorCode);
}
