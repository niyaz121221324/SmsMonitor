package com.example.smsmonitor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://cheaply-cute-ape.ngrok-free.app/";
    private static Retrofit client = null;

    public static Retrofit getClient() {
        if (client != null) {
            return  client;
        }

        client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return client;
    }
}
