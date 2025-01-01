package com.mediaghor.rainbowtools.Models;

public class CardItemsModel {
    private String text;      // Text description or title
    private String imageUri;  // URI or path of the video

    // Constructor
    public CardItemsModel(String text, String imageUri) {
        this.text = text;
        this.imageUri = imageUri;
    }

    // Getter and Setter for text
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter and Setter for imageUri
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String videoUri) {
        this.imageUri = videoUri;
    }
}
