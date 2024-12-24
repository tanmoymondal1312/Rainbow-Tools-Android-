package com.mediaghor.rainbowtools.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ImageUploadResponse {

    @SerializedName("image_names")
    private List<String> imageNames;

    public List<String> getImageNames() {
        return imageNames;
    }

    public void setImageNames(List<String> imageNames) {
        this.imageNames = imageNames;
    }
}
