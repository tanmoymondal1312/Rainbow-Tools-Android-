package com.mediaghor.rainbowtools;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudmersiveApiService {
    @Multipart
    @POST("convert/pdf/to/docx")
    Call<ResponseBody> convertPdfToDocx(
            @Header("Apikey") String apiKey,
            @Part MultipartBody.Part file
    );
}
