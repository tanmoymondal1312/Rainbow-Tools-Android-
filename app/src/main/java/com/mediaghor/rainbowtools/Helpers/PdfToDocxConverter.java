package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mediaghor.rainbowtools.ApiClient;
import com.mediaghor.rainbowtools.CloudmersiveApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PdfToDocxConverter {
    private static final String API_KEY = "9f2bd5b3-05d4-4234-89c8-b669614c2f6e";

    private CloudmersiveApiService apiService;
    private Handler handler;

    public PdfToDocxConverter() {
        apiService = ApiClient.getClient().create(CloudmersiveApiService.class);
        handler = new Handler(Looper.getMainLooper());
    }

    public void convertPdf(final Context context, final File pdfFile, final String outputFilePath, final int delay, final PdfConversionCallback callback) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file", pdfFile.getName(),
                RequestBody.create(MediaType.parse("application/pdf"), pdfFile)
        );

        Call<ResponseBody> call = apiService.convertPdfToDocx(API_KEY, filePart);
        handler.postDelayed(() -> call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File docxFile = new File(outputFilePath);
                        InputStream inputStream = response.body().byteStream();
                        FileOutputStream outputStream = new FileOutputStream(docxFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();
                        callback.onSuccess(docxFile);
                    } catch (Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    callback.onFailure("Conversion failed! Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }), delay);
    }

    public interface PdfConversionCallback {
        void onSuccess(File docxFile);
        void onFailure(String errorMessage);
    }
}
