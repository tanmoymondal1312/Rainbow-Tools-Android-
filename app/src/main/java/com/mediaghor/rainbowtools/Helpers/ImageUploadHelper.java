package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mediaghor.rainbowtools.Api;
import com.mediaghor.rainbowtools.Models.ImageUploadResponse;
import com.mediaghor.rainbowtools.R;
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


import android.database.Cursor;
import android.provider.OpenableColumns;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUploadHelper {
    public int division;
    private Context context;
    private Call<ImageUploadResponse> currentUploadCall; // Track the current upload call
    String serverIp;

    public ImageUploadHelper(Context context,int division) {
        this.context = context;
        this.division = division;
        this.serverIp = context.getResources().getString(R.string.serverIp);
    }

    // Method to upload images
    public void uploadImages(List<Uri> uris, final ImageUploadCallback callback) {
        // Prepare the list of MultipartBody.Part for each image
        ArrayList<MultipartBody.Part> parts = new ArrayList<>();
        for (Uri uri : uris) {
            File file = getFileFromUri(uri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
            parts.add(body);
        }

        // Create the Retrofit API service
        Api service = RetrofitClient.getRetrofitInstance(context).create(Api.class);
        if (division == 1){
            currentUploadCall = service.uploadImages(parts); // Store the call reference
        } else if (division == 2) {
            currentUploadCall = service.uploadImagesForEnhanceImages(parts); // Store the call reference
        }


        // Make the network call
        currentUploadCall.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    // On success, get the image URLs from the response
                    ArrayList<String> imageNames = (ArrayList<String>) response.body().getImageNames();
                    ArrayList<Uri> imageUrls = new ArrayList<>();
                    if(division == 1){
                        for (String imageName : imageNames) {
                            imageUrls.add(Uri.parse("http://"+serverIp+":8000/image-optimization/get_bg_removed_images/" + imageName));  // Assuming media URL pattern
                        }
                    } else if (division == 2) {
                        for (String imageName : imageNames) {
                            imageUrls.add(Uri.parse("http://"+serverIp+":8000/image-optimization/get_enhance_images/" + imageName));  // Assuming media URL pattern
                        }
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
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("ImageUploadHelper", "InputStream is null for URI: " + uri.toString());
                return null;
            }

            // Generate a unique filename for each image
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File tempFile = new File(context.getCacheDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            Log.d("ImageUploadHelper", "File created successfully: " + tempFile.getAbsolutePath());
            return tempFile;
        } catch (Exception e) {
            Log.e("ImageUploadHelper", "Error getting file from URI: " + uri.toString(), e);
            return null;
        }
    }



    // Callback interface for uploading images
    public interface ImageUploadCallback {
        void onImageUploadSuccess(ArrayList<Uri> imageUrls);
        void onImageUploadFailure(String errorMessage);
    }
}
