package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.R;

public class ProcessingDialog {

    private Dialog dialog;
    private LottieAnimationView animationView;

    public ProcessingDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);  // Prevent dismiss by default
        dialog.setContentView(R.layout.processing_dialog_layout);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            // Ensure dialog does not block touch events
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        animationView = dialog.findViewById(R.id.animationView);

        // Make the root view pass touch events
        View rootView = dialog.findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.setOnTouchListener((v, event) -> {
                // Pass all touch events to the underlying UI
                activity.dispatchTouchEvent(event);
                return true;
            });
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            animationView.playAnimation();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            animationView.cancelAnimation();
            dialog.dismiss();
        }
    }
}
