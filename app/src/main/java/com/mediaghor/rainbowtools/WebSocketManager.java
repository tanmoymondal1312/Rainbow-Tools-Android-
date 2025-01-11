package com.mediaghor.rainbowtools;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.json.JSONObject;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static final String SERVER_URL = "ws://your-server-url/ws/image-processing/";
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private WebSocketCallback callback;

    private WebSocketManager() {
        // Private constructor for singleton
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect(WebSocketCallback callback) {
        this.callback = callback;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d(TAG, "WebSocket Connection Opened");
                if (callback != null) {
                    callback.onOpen();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message Received: " + text);
                if (callback != null) {
                    callback.onMessage(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Byte Message Received: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closing: " + reason);
                webSocket.close(1000, null);
                if (callback != null) {
                    callback.onClosing(code, reason);
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closed: " + reason);
                if (callback != null) {
                    callback.onClosed(code, reason);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e(TAG, "WebSocket Failure: " + t.getMessage(), t);
                if (callback != null) {
                    callback.onFailure(t);
                }
            }
        });

        client.dispatcher().executorService().shutdown();
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
            Log.d(TAG, "Message Sent: " + message);
        } else {
            Log.e(TAG, "WebSocket is not connected.");
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "User Disconnected");
            webSocket = null;
        }
    }

    public interface WebSocketCallback {
        void onOpen();

        void onMessage(String message);

        void onClosing(int code, String reason);

        void onClosed(int code, String reason);

        void onFailure(Throwable t);
    }
}
