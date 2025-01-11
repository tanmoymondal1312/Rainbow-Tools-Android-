package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.R;

public class ProcessingDialog {

    private Dialog dialog;
    private LottieAnimationView animationView;

    public ProcessingDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.processing_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        animationView = dialog.findViewById(R.id.animationView);
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
