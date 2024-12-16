package com.mediaghor.rainbowtools.Models;

public class CardItemsModel {
    private String text;      // Text description or title
    private String videoUri;  // URI or path of the video

    // Constructor
    public CardItemsModel(String text, String videoUri) {
        this.text = text;
        this.videoUri = videoUri;
    }

    // Getter and Setter for text
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter and Setter for videoUri
    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }
}
