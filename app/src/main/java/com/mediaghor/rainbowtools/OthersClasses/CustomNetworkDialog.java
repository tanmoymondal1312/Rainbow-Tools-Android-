package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.mediaghor.rainbowtools.R;

public class CustomNetworkDialog {

    private final Dialog dialog;

    public CustomNetworkDialog(Context context, @DrawableRes int imageResId, String message) {
        // Check if context is an instance of Activity
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("Context must be an instance of Activity.");
        }

        Activity activity = (Activity) context;

        // Initialize the dialog
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_network_status); // Reference to your layout XML file
//        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);


        // Set the dynamic image
        ImageView imageView = dialog.findViewById(R.id.img_id_in_dialog_network_status);
        if (imageView != null) {
            imageView.setImageResource(imageResId);
        }

        // Set the dynamic text
        TextView textView = dialog.findViewById(R.id.text_id_in_dialog_network_status);
        if (textView != null) {
            textView.setText(message);
        }

        // Set the button click listener
        Button button = dialog.findViewById(R.id.btnOk);
        if (button != null) {
            button.setOnClickListener(view -> dialog.dismiss());
        }
    }

    // Show the dialog
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    // Dismiss the dialog
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
