package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.R;

public class ButtonAnimationManager {

    private Context context;
    private Activity activity;

    // Constructor to initialize with Activity context
    public ButtonAnimationManager(Context context) {
        if (context instanceof Activity) { // Ensure context is an Activity
            this.context = context;
            this.activity = (Activity) context; // Store the activity from context
        } else {
            throw new IllegalArgumentException("Context must be an instance of Activity.");
        }
    }
    public void SelectImageAnimation(String state) {
        if (context instanceof Activity) { // Ensure context is an Activity
            LottieAnimationView selectImageAnimation = activity.findViewById(R.id.animation_select_image_id_abgr);
            switch (state){
                case "loop_animation":
                    selectImageAnimation.loop(true);
                    selectImageAnimation.playAnimation();
                    break;
                case "unloop_animation":
                    selectImageAnimation.loop(false);
                    selectImageAnimation.playAnimation();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid state: " + state);
            }
        } else {
            throw new IllegalArgumentException("Context is not an Activity. Unable to find view.");
        }
    }
    public void GeneratingButtonAnimation(String state){
        if (context instanceof Activity) {
            ImageView imageView = activity.findViewById(R.id.generate_disable_image_id_bg_remover_layout);
            LottieAnimationView generatingAnimation = activity.findViewById(R.id.generating_animation_id_bg_remover_layout);
            switch (state){
                case "enable":
                    imageView.setVisibility(View.GONE);
                    generatingAnimation.setProgress(0f);
                    generatingAnimation.pauseAnimation();
                    generatingAnimation.setVisibility(View.VISIBLE);
                    generatingAnimation.setClickable(true);
                    break;
                case "disable":
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setClickable(false);
                    generatingAnimation.setClickable(false);
                    generatingAnimation.setVisibility(View.GONE);
                    break;
                case "animated":
                    imageView.setVisibility(View.GONE);
                    generatingAnimation.setVisibility(View.VISIBLE);
                    generatingAnimation.loop(true);
                    generatingAnimation.playAnimation();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid state: " + state);
            }
        }
        else {
            Log.e("GeneratingButtonAnimation", "Context is not an instance of Activity!");
        }
    }
}
