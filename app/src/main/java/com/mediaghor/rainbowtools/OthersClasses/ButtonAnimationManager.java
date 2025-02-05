package com.mediaghor.rainbowtools.OthersClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    public void DownloadAllImagesAnimation(String state){
        if (context instanceof Activity) {
            LottieAnimationView download,downloading;
            download = activity.findViewById(R.id.lottie_anim_download_in_bottom_toolbar_bgrl);
            downloading = activity.findViewById(R.id.lottie_anim_downloading_in_bottom_toolbar_bgrl);

            switch (state){
                case "download":
                    downloading.setVisibility(View.GONE);
                    downloading.setClickable(false);
                    downloading.setEnabled(false);

                    download.setVisibility(View.VISIBLE);
                    download.setClickable(true);
                    download.setEnabled(true);
                    download.loop(true);
                    download.setAlpha(1f);
                    download.playAnimation();
                    break;
                case "downloading":
                    download.setVisibility(View.GONE);
                    download.setClickable(false);
                    download.setEnabled(false);

                    downloading.setVisibility(View.VISIBLE);
                    downloading.setClickable(true);
                    downloading.setEnabled(true);
                    downloading.loop(false);
                    downloading.playAnimation();
                    break;
                case "disable":
                    downloading.setVisibility(View.GONE);
                    downloading.setClickable(false);
                    downloading.setEnabled(false);

                    download.setVisibility(View.VISIBLE);
                    download.setClickable(false);
                    download.setEnabled(false);
                    download.setAlpha(0.5f);
                    download.loop(false);
                    break;

            }
        }
        else {
            Log.e("GeneratingButtonAnimation", "Context is not an instance of Activity!");
        }
    }
    public void DownloadDeleteAnimationInItem(String state, View itemView) {
        LottieAnimationView Remove, Download;
        Remove = itemView.findViewById(R.id.remove_item_from_recy_enhance_img_itm);
        Download = itemView.findViewById(R.id.download_item_from_recy_enhance_img_itm);

        if (Remove == null || Download == null) {
            Log.e("DownloadDeleteAnimation", "Views not found in itemView");
            return; // Prevent null pointer exceptions
        }

        switch (state) {
            case "remove":
                Download.setVisibility(View.GONE);
                Remove.setVisibility(View.VISIBLE);
                Remove.setEnabled(true);
                Remove.setClickable(true);
                Remove.loop(true);
                Remove.playAnimation();
                break;
            case "download":
                Remove.setVisibility(View.GONE);
                Download.setVisibility(View.VISIBLE);
                Download.setEnabled(true);
                Download.setClickable(true);
                Download.loop(true);
                Download.playAnimation();
                break;
            case "empty":
                Remove.setVisibility(View.GONE);
                Download.setVisibility(View.GONE);
                break;
            default:
                Log.e("DownloadDeleteAnimation", "Invalid state: " + state);
                break;
        }
    }

    public void ButterflyProgressing(String state) {
        if (context instanceof Activity) {
            LottieAnimationView butterFlyLoading = activity.findViewById(R.id.butterfly_progressing_id_img_enhance_laout);

            switch (state) {
                case "progressing":
                    butterFlyLoading.setVisibility(View.VISIBLE);
                    butterFlyLoading.loop(true);
                    butterFlyLoading.playAnimation();
                    break;
                case "disable":
                    butterFlyLoading.setVisibility(View.GONE);
                    break;
            }


        }
    }

    public void CongressCuttingPaperAnimation(String state){
        if (context instanceof Activity) {
            LottieAnimationView cuttingPaperAnimation = activity.findViewById(R.id.lottie_cutting_paper_congress_in_img_inhnc_layout);

            switch (state) {
                case "start":
                    cuttingPaperAnimation.setVisibility(View.VISIBLE);
                    cuttingPaperAnimation.loop(true);
                    cuttingPaperAnimation.playAnimation();
                    break;
                case "stop":
                    cuttingPaperAnimation.setVisibility(View.GONE);
                    break;
            }


        }
    }





}
