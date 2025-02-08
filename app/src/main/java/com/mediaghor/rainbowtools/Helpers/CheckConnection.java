package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckConnection {

    private Context context;

    // Constructor to initialize the context
    public CheckConnection(Context context) {
        this.context = context;
    }

    // Method to check the internet connection status
    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (networkCapabilities != null) {
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            } else {
                // For devices running below Android 6.0
                android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }

    // Function to check server connection status
    public void checkServerConnection(final ServerConnectionListener listener) {
        // Use ExecutorService to run network request on a background thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // Configure OkHttpClient with a 5-second timeout
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS) // Timeout for establishing a connection
                        .readTimeout(3, TimeUnit.SECONDS)    // Timeout for reading data
                        .writeTimeout(3, TimeUnit.SECONDS)   // Timeout for sending data
                        .build();

                Request request = new Request.Builder()
                        .url("http://192.168.0.101:8000/connection-status/") // Django backend URL
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful() && response.code() == 200) {
                        // Successful response (status code 200)
                        if (listener != null) {
                            listener.onConnectionChecked(true);
                        }
                    } else {
                        // If the server responds with an error code
                        if (listener != null) {
                            listener.onConnectionChecked(false);
                        }
                    }
                } catch (IOException e) {
                    Log.e("ServerConnection", "Connection failed: " + e.getMessage());
                    if (listener != null) {
                        listener.onConnectionChecked(false);
                    }
                }
            }
        });
    }


    // Interface to communicate the result of the connection check
    public interface ServerConnectionListener {
        void onConnectionChecked(boolean isConnected);
    }
}
