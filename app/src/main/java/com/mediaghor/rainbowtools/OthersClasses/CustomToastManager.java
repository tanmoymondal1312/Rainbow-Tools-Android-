package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mediaghor.rainbowtools.R;

public class CustomToastManager {

    private final Activity activity;
    private TextView toastTitle;
    private ImageView toastImage;
    private TextView toastDescription;
    private View customView;

    // Constructor to initialize the activity reference and custom toast layout
    public CustomToastManager(Context context) {
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        } else {
            throw new IllegalArgumentException("Context must be an instance of Activity");
        }
        initializeCustomToast();
    }

    // Initialize the custom toast layout and views
    private void initializeCustomToast() {
        // Inflate the custom layout for the toast
        LayoutInflater inflater = activity.getLayoutInflater();
        customView = inflater.inflate(R.layout.custom_toast, null);

        // Find the views in the custom layout
        toastTitle = customView.findViewById(R.id.title_custom_toast);
        toastImage = customView.findViewById(R.id.img_custom_toast);
        toastDescription = customView.findViewById(R.id.description_custom_toast);
    }
    // Method to show a custom toast with the provided image, title, description, and duration


    // Example method for a success toast
    public void showDownloadSuccessToast(int imageResId, String title, int duration) {
        toastDescription.setVisibility(View.GONE);
        toastTitle.setText(title);
        toastImage.setImageResource(imageResId);
        Toast toast = new Toast(activity);
        toast.setDuration(duration);
        toast.setView(customView);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 350);
        toast.show();
    }

    // Example method for a failure toast
//    public void showDownloadFailureToast(int imageResId, String title, String description, int duration) {
//        toastTitle.setText(title);
//        toastImage.setImageResource(imageResId);
//        toastDescription.setText(description);
//
//        // Create and show the Toast
//        Toast toast = new Toast(activity);
//        toast.setDuration(duration);
//        toast.setView(customView);
//        toast.show();
//    }

    // You can add more methods for different toast types if needed, e.g., success, error, info, etc.
}
