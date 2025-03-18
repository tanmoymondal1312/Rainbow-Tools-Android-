package com.mediaghor.rainbowtools.OthersClasses;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.R;

public class ButtonAnimationManager {

    private Context context;
    private Activity activity;
    private ValueAnimator animator;

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

    // 🌈 Method to apply Rainbow Gradient to a TextView
    // 🌈 Method to Apply & Animate Rainbow Gradient on TextView
    public void applyMovingRainbowGradient(TextView textView) {
        if (textView == null) return;


        //Rainbow color but there added 5. So sorry to the programme for the 6 and 7 th color
        int[] rainbowColors = {
                ContextCompat.getColor(context, R.color.rainbow_color_1),
                ContextCompat.getColor(context, R.color.rainbow_color_2),
                ContextCompat.getColor(context, R.color.rainbow_color_3),
                ContextCompat.getColor(context, R.color.rainbow_color_4),
                ContextCompat.getColor(context, R.color.rainbow_color_5),
        };




        float textWidth = textView.getPaint().measureText(textView.getText().toString());
        float textSize = textView.getTextSize();

        // Create an animator to shift the gradient position
        animator = ValueAnimator.ofFloat(0, textWidth * 2); // Loop across text width
        animator.setDuration(4000); // 4 seconds
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);

        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();

            Shader textShader = new LinearGradient(
                    animatedValue, 0, animatedValue + textWidth, textSize,
                    rainbowColors, null, Shader.TileMode.MIRROR
            );

            textView.getPaint().setShader(textShader);
            textView.invalidate(); // Refresh the view
        });

        animator.start(); // Start animation

        // **Add Stroke (Outline) for Readability**
        textView.setShadowLayer(5, 2, 2, Color.BLACK);
        textView.setTextColor(Color.WHITE);
        textView.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        textView.getPaint().setStrokeWidth(3);
    }



    public void RemoveCrossButtonEXT(String state, View itemview){
        ImageView imageView;
        imageView = itemview.findViewById(R.id.btn_cancel_from_selected_images);

        switch (state){
            case"disable":
                imageView.setVisibility(View.GONE);
                break;

            case"enable":
                imageView.setVisibility(View.VISIBLE);
                imageView.setClickable(true);
                imageView.setEnabled(true);
        }
    }
    public void AnimCopyTextsManager(String state, View itemView) {
        LottieAnimationView copyAnim = itemView.findViewById(R.id.anim_copy_txt_in_ext);

        switch (state) {
            case "initial":
                Log.d("LottieDebug", "In Initial");

                copyAnim.addLottieOnCompositionLoadedListener(composition -> {
                    copyAnim.setProgress(0.75f);  // Move to 75%
                    copyAnim.pauseAnimation();    // Pause animation
                });
                break;

            case "play":
                Log.d("LottieDebug", "Playing animation from 75%");

                copyAnim.setMinProgress(0.75f);  // Start from 75%
                copyAnim.setMaxProgress(1.0f);   // Play until 100%
                copyAnim.playAnimation();        // Start playing

                copyAnim.addAnimatorUpdateListener(animation -> {
                    if (copyAnim.getProgress() >= 1.0f) {  // If animation reaches the end
                        copyAnim.setProgress(0.75f);  // Move back to 75%
                        copyAnim.pauseAnimation();    // Pause animation
                    }
                });
                break;
        }
    }


    public void ExtractingAnim(String state){
        if (context instanceof Activity) {
            LottieAnimationView generatingAnimation = activity.findViewById(R.id.extracting_anim_in_ext_lay);
            switch (state){
                case "play":
                    generatingAnimation.setVisibility(View.VISIBLE);
                    generatingAnimation.setEnabled(true);
                    generatingAnimation.loop(true);
                    generatingAnimation.setSpeed(0.5f);
                    generatingAnimation.playAnimation();
                    break;
                case "stop":
                    generatingAnimation.setVisibility(View.GONE);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid state: " + state);
            }
        }
        else {
            Log.e("GeneratingButtonAnimation", "Context is not an instance of Activity!");
        }
    }

    public void ImageReducerGeneratingAnimation(String state){
        if (context instanceof Activity) {
            LinearLayout generetingReducingAnimLay,LinearRecycleContained;
            generetingReducingAnimLay = activity.findViewById(R.id.reducing_img_size_in_r_img_lay);
            LinearRecycleContained = activity.findViewById(R.id.linear_content_rszimg);

            switch (state){
                case "start":
                    generetingReducingAnimLay.setVisibility(View.VISIBLE);
                    LinearRecycleContained.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.transparent_gray)));


                    break;
                case "stop":
                    generetingReducingAnimLay.setVisibility(View.GONE);
                    LinearRecycleContained.setForeground(null);
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
