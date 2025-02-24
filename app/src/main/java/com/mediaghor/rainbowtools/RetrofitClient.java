package com.mediaghor.rainbowtools;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Get the IP from strings.xml dynamically
            String serverIp = context.getResources().getString(R.string.serverIp);
            String BASE_URL = "http://" + serverIp + ":8000/";  // ✅ Use dynamic IP

            // Configure OkHttpClient with a timeout of 60 seconds
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(500, TimeUnit.SECONDS)
                    .readTimeout(500, TimeUnit.SECONDS)
                    .writeTimeout(500, TimeUnit.SECONDS)
                    .build();

            // Build the Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Set the custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
