package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.mediaghor.rainbowtools.Api;
import com.mediaghor.rainbowtools.Models.ReduceImageUploadResponse;
import com.mediaghor.rainbowtools.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageSizeUploadHelper {

    private Call<ReduceImageUploadResponse> uploadCall;
    private final ImageUploadCallback callback;
    private final Context context;

    public ImageSizeUploadHelper(Context context, ImageUploadCallback callback) {
        this.context = context;
        this.callback = callback;
    }


    public void uploadImages(ArrayList<Uri> imageUris, ArrayList<String> targetSizes) {
        Api apiService = RetrofitClient.getRetrofitInstance(context).create(Api.class);

        List<MultipartBody.Part> images = new ArrayList<>();
        List<String> sizes = new ArrayList<>();

        Log.d("UPLOAD_DEBUG", "Received targetSizes: " + targetSizes.toString()); // ✅ Log received sizes

        for (int i = 0; i < imageUris.size(); i++) {
            Uri uri = imageUris.get(i);
            String targetSizeStr = targetSizes.get(i);

            // Log each target size before adding to list
            Log.d("UPLOAD_DEBUG", "Original targetSizeStr: " + targetSizeStr);

            // Remove extra quotes and spaces if present
            targetSizeStr = targetSizeStr.replace("\"", "").trim();
            Log.d("UPLOAD_DEBUG", "Processed targetSizeStr: " + targetSizeStr);

            File file = getFileFromUri(uri);
            if (file == null || !file.exists()) {
                Log.e("UPLOAD_DEBUG", "Failed to get file from URI: " + uri.toString());
                callback.onFailure("Failed to get file from URI: " + uri.toString());
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            images.add(body);

            sizes.add(String.valueOf(targetSizeStr));
        }

        Log.d("UPLOAD_DEBUG", "Final sizes list before API call: " + sizes.toString()); // ✅ Log final sizes list

        uploadCall = apiService.uploadImagesForReduceSize(images, sizes);
        uploadCall.enqueue(new Callback<ReduceImageUploadResponse>() {
            @Override
            public void onResponse(Call<ReduceImageUploadResponse> call, Response<ReduceImageUploadResponse> response) {
                ArrayList<String> imageNames = new ArrayList<>();
                ArrayList<Uri> imageUrls = new ArrayList<>();

                if (response.body() != null && response.body().getReducedImages() != null) {
                    for (ReduceImageUploadResponse.ReducedImage image : response.body().getReducedImages()) {
                        imageNames.add(image.getImage());
                        imageUrls.add(Uri.parse(image.getUrl()));
                    }
                    callback.onSuccess(imageNames,imageUrls);

                } else {
                    Log.e("UPLOAD_DEBUG", "API Failed: " + response.message());
                    callback.onFailure("Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReduceImageUploadResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.e("UPLOAD_DEBUG", "API Call Failure: " + t.getMessage());
                    callback.onFailure(t.getMessage());
                }
            }
        });
    }




    public void cancelUpload() {
        if (uploadCall != null && !uploadCall.isCanceled()) {
            uploadCall.cancel();
            Log.d("ImageUploadHelper", "Upload canceled.");
            callback.onFailure("Upload was canceled.");
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            String fileName = getFileName(uri);
            File file = new File(context.getCacheDir(), fileName);

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = "temp_file.jpg"; // Default name

        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uri.getLastPathSegment();
    }

    public interface ImageUploadCallback {
        void onSuccess( ArrayList<String>imageNames,ArrayList<Uri> imageUrls);
        void onFailure(String error);
    }
}
