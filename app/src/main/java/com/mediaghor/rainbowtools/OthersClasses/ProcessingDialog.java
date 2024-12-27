package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.R;

public class ProcessingDialog {
    private Dialog dialog;

    public ProcessingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_processing);
        dialog.setCancelable(false); // Prevent closing the dialog by tapping outside
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // Start showing the processing dialog
    public void startProcessingDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    // Stop and dismiss the processing dialog
    public void stopProcessingDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
