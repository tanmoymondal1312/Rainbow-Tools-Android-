package com.mediaghor.rainbowtools;
import com.mediaghor.rainbowtools.Models.ImageUploadResponse;
import com.mediaghor.rainbowtools.Models.ReduceImageUploadResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import java.util.List;



public interface Api{
    @Multipart
    @POST("image-optimization/remove-bg/")
    Call<ImageUploadResponse> uploadImages(
            @Part List<MultipartBody.Part> images
    );
    @Multipart
    @POST("image-optimization/enhance-images/")
    Call<ImageUploadResponse> uploadImagesForEnhanceImages(
            @Part List<MultipartBody.Part> images
    );
    @Multipart
    @POST("image-optimization/extract-texts/")
    Call<ImageUploadResponse> uploadImagesForTextExtract(
            @Part List<MultipartBody.Part> images
    );
    @Multipart
    @POST("image-optimization/reduce-images-size/")
    Call<ReduceImageUploadResponse> uploadImagesForReduceSize(
            @Part List<MultipartBody.Part> images,
            @Part("target_size") List<String> targetSizes
    );


}
