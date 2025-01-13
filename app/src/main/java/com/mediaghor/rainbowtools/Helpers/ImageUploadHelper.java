package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mediaghor.rainbowtools.Api;
import com.mediaghor.rainbowtools.Models.ImageUploadResponse;
import com.mediaghor.rainbowtools.RetrofitClient;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageUploadHelper {

    private Context context;
    private Call<ImageUploadResponse> currentUploadCall; // Track the current upload call

    public ImageUploadHelper(Context context) {
        this.context = context;
    }

    // Method to upload images
    public void uploadImages(List<Uri> uris, final ImageUploadCallback callback) {
        // Prepare the list of MultipartBody.Part for each image
        ArrayList<MultipartBody.Part> parts = new ArrayList<>();
        for (Uri uri : uris) {
            File file = new File(getRealPathFromURI(uri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
            parts.add(body);
        }

        // Create the Retrofit API service
        Api service = RetrofitClient.getRetrofitInstance().create(Api.class);
        currentUploadCall = service.uploadImages(parts); // Store the call reference

        // Make the network call
        currentUploadCall.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    // On success, get the image URLs from the response
                    ArrayList<String> imageNames = (ArrayList<String>) response.body().getImageNames();
                    ArrayList<Uri> imageUrls = new ArrayList<>();
                    for (String imageName : imageNames) {
                        imageUrls.add(Uri.parse("http://192.168.0.109:8000/image-optimization/get_bg_removed_images/" + imageName));  // Assuming media URL pattern
                    }
                    callback.onImageUploadSuccess(imageUrls);
                } else {
                    // Handle failure
                    callback.onImageUploadFailure("Failed to upload images");
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                // Handle network failure
                callback.onImageUploadFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Method to cancel the ongoing upload
    public void cancelUpload() {
        if (currentUploadCall != null && !currentUploadCall.isCanceled()) {
            currentUploadCall.cancel(); // Cancel the ongoing call
        } else {
        }
    }

    // Helper function to get file path from URI
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {android.provider.MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    // Callback interface for uploading images
    public interface ImageUploadCallback {
        void onImageUploadSuccess(ArrayList<Uri> imageUrls);
        void onImageUploadFailure(String errorMessage);
    }
}
