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
    private final View customView;
    private final TextView toastTitle;
    private final ImageView toastImage;
    private Toast currentToast;

    // Constructor to initialize the activity reference and custom toast layout
    public CustomToastManager(Context context) {
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        } else {
            throw new IllegalArgumentException("Context must be an instance of Activity");
        }

        // Inflate the custom layout for the toast
        LayoutInflater inflater = activity.getLayoutInflater();
        customView = inflater.inflate(R.layout.custom_toast, null);

        // Find the views in the custom layout
        toastTitle = customView.findViewById(R.id.title_custom_toast);
        toastImage = customView.findViewById(R.id.img_custom_toast);
    }

    // Show a custom toast with an image and title, ensuring only one instance exists
    public void showDownloadSuccessToast(int imageResId, String title, int duration) {
        if (currentToast != null) {
            currentToast.cancel();  // Cancel previous toast before showing a new one
        }

        toastTitle.setText(title);
        toastImage.setImageResource(imageResId);

        currentToast = new Toast(activity);
        currentToast.setDuration(duration);
        currentToast.setView(customView);
        currentToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 350);
        currentToast.show();
    }
}
