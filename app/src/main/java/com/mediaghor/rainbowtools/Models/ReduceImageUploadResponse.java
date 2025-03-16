package com.mediaghor.rainbowtools.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReduceImageUploadResponse {

    @SerializedName("reduced_images")
    private List<ReducedImage> reducedImages;

    public List<ReducedImage> getReducedImages() {
        return reducedImages;
    }

    public void setReducedImages(List<ReducedImage> reducedImages) {
        this.reducedImages = reducedImages;
    }

    public static class ReducedImage {
        @SerializedName("image")
        private String image;

        @SerializedName("url")
        private String url;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
