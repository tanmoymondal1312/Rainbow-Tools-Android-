package com.mediaghor.rainbowtools.Adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class EnhanceImagesAdapter extends RecyclerView.Adapter<EnhanceImagesAdapter.ImageViewHolder> {

    private ArrayList<Uri> beforeEnhanceImages;
    private ArrayList<Uri> afterEnhanceImages;
    private final Context context;
    private boolean iSEnhanceImagesGet = false;
    private boolean isUploading;
    private OnImageListEmptyListener imageListEmptyListener;
    ButtonAnimationManager buttonAnimationManager;

    // Constructor
    public EnhanceImagesAdapter(Context context, ArrayList<Uri> beforeEnhanceImages,OnImageListEmptyListener listener) {
        this.context = context;
        this.beforeEnhanceImages = beforeEnhanceImages;
        buttonAnimationManager = new ButtonAnimationManager(context);
        this.imageListEmptyListener = listener;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_enhance_images_item_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Uri uriBeforeEnhance = beforeEnhanceImages.get(position);

        loadBeforeEnhanceImage(holder,uriBeforeEnhance);
        //Hide Slider Line In Initial Sate
        sliderLineSliderHide(holder);

        //Enable The Remove Button In Initial Sate
        buttonAnimationManager.DownloadDeleteAnimationInItem("remove", holder.itemView);

        //Disable the Remove Button When Uploading In Server
        if (isUploading) {
            buttonAnimationManager.DownloadDeleteAnimationInItem("empty", holder.itemView);
        } else {
            holder.AnimationRemoveItem.setVisibility(View.VISIBLE);
        }


        //Setting On Remove Item Logic In Remove Button Clicking
        holder.AnimationRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition(); // Get updated position
                if (adapterPosition != RecyclerView.NO_POSITION) { // Ensure it's a valid position
                    removeItemFromList(adapterPosition);
                }
            }
        });

        // Successfully Get The Process Images , So Lest Work With Those.
        if(afterEnhanceImages != null && iSEnhanceImagesGet && position < afterEnhanceImages.size()){
            Uri uriAfterEnhance = afterEnhanceImages.get(position);
            buttonAnimationManager.DownloadDeleteAnimationInItem("download", holder.itemView);

            resetViewState(holder);
            loadImagesAfterEnhance(holder, uriAfterEnhance,position);
            //sliderLineSliderUnHide(holder);

        }







    }


    @Override
    public int getItemCount() {
        return beforeEnhanceImages.size();
    }

    // ViewHolder class
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ImageViewBeforeEnhance,ImageViewAfterEnhance;
        View sliderLine;
        SeekBar slider;
        LottieAnimationView AnimationRemoveItem,AnimationDownloadItem;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ImageViewBeforeEnhance = itemView.findViewById(R.id.img_id_recycler_before_enhance);
            ImageViewAfterEnhance = itemView.findViewById(R.id.img_id_recycler_after_enhance);
            sliderLine = itemView.findViewById(R.id.slider_line_view);
            slider = itemView.findViewById(R.id.slider);
            AnimationRemoveItem = itemView.findViewById(R.id.remove_item_from_recy_enhance_img_itm);
            AnimationDownloadItem = itemView.findViewById(R.id.download_item_from_recy_enhance_img_itm);

        }
    }

    // Helper Methods





    // All Classes To Manage States Of Selected Images ============================================
    /**
     * Loads an image into the ImageView using Glide.
     *
     * @param holder The ViewHolder containing the ImageView.
     * @param uri    The URI of the image to load.
     */
    private void loadBeforeEnhanceImage(ImageViewHolder holder, Uri uri) {
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.placeholder_image)
                .dontAnimate() // Optional: Skip animations for faster transitions
                .into(holder.ImageViewBeforeEnhance);

    }
    /**
     * Visible The Slider And The View/Slider Line
     *
     * @param holder The ViewHolder containing the ImageView.
     */
    private void sliderLineSliderHide(ImageViewHolder holder){
        holder.slider.setVisibility(View.GONE);
        holder.sliderLine.setVisibility(View.GONE);
    }
    /**
     * Remove an item from Selected Images List and Adding a Removed Visual Effect
     *
     * @param position The ViewHolder containing the ImageView.
     */
    private void removeItemFromList(int position) {
        if (position >= 0 && position < beforeEnhanceImages.size()) { // Check to avoid IndexOutOfBoundsException
            beforeEnhanceImages.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, beforeEnhanceImages.size()); // Optional but ensures smooth updates
        }
        if (beforeEnhanceImages.isEmpty() && imageListEmptyListener != null) {

            imageListEmptyListener.onImageListEmpty();
        }

    }
    /**
     * Returning The Final Images After Cut Or Add
     */
    public ArrayList<Uri> GetFinalSelectedImages(){
        return beforeEnhanceImages;
    }
    /**
     * Setting Uploading State Eg If Uploading Then Made Remove Animation Button Disable else Enable
     * @param state Set The state That if uploading or not
     */
    public void setUploadingState(boolean state) {
        isUploading = state;
        notifyDataSetChanged();
    }

    public interface OnImageListEmptyListener {
        void onImageListEmpty();
    }





    // All Classes To Manage States Of Processes/Enhance Images ============================================

    /**
     * Loads an image into the ImageView using Glide.
     *
     * @param holder The ViewHolder containing the ImageView.
     * @param uri    The URI of the processed image to load.
     */
    private void loadImagesAfterEnhance(ImageViewHolder holder, Uri uri, int position) {
        // Get the corresponding before-enhance image
        Uri beforeEnhanceUri = beforeEnhanceImages.get(position);

        // Load the before-enhance image with a blur effect
        Glide.with(context)
                .load(uri)
                .thumbnail(Glide.with(context)
                        .load(beforeEnhanceUri)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))) // Apply blur effect
                )
                .transition(DrawableTransitionOptions.withCrossFade(1000)) // Smooth cross-fade animation
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Apply initial clipping when the image is loaded
                        buttonAnimationManager.CongressCuttingPaperAnimation("stop");
                        holder.ImageViewAfterEnhance.post(() -> {
                            holder.slider.setProgress(50);
                            int initialProgress = holder.slider.getProgress();
                            handleSeekBarProgress(holder, initialProgress);
                        });

                        sliderLineSliderUnHide(holder);
                        animateSlider(holder);
                        setupSeekBar(holder);

                        return false;
                    }
                })
                .into(holder.ImageViewAfterEnhance);
    }


    /**
     * Updates the position of the slider line based on the SeekBar progress.
     *
     * @param afterEnhanceImages An array which contain the process iamges
     */
    public void  AddAfterEnhanceImagesToArray(ArrayList<Uri> afterEnhanceImages){
        this.afterEnhanceImages = afterEnhanceImages;
        this.iSEnhanceImagesGet = true;
        notifyDataSetChanged();
    }

    /**
     * Invisible The Slider And The View/Slider Line
     *
     * @param holder The ViewHolder containing the ImageView.
     */
    private void sliderLineSliderUnHide(ImageViewHolder holder){
        holder.slider.setVisibility(View.VISIBLE);
        holder.sliderLine.setVisibility(View.VISIBLE);
    }


    /**
     * Rest The All Components Such as slider sliderLine ImageView To Get Better Performance
     *
     * @param holder The ViewHolder containing the ImageView.
     */
    private void resetViewState(ImageViewHolder holder) {
        if (holder.ImageViewAfterEnhance != null) {
            // Remove any previous clipping
            holder.ImageViewAfterEnhance.setClipBounds(null);
            holder.ImageViewAfterEnhance.invalidate();
        }

        if (holder.sliderLine != null) {
            // Reset slider line position
            holder.sliderLine.setX(0);
        }

        if (holder.slider != null) {
            // Reset SeekBar progress
            holder.slider.setProgress(0);
        }
    }
    /**
     * Sets up the SeekBar listener and handles progress updates.
     *
     * @param holder The ViewHolder containing the SeekBar and associated views.
     */
    private void setupSeekBar(ImageViewHolder holder) {
        holder.slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                handleSeekBarProgress(holder, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Handle SeekBar touch start
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Handle SeekBar touch stop
            }
        });
    }

    /**
     * Handles SeekBar progress updates by clipping the image and moving the slider line.
     *
     * @param holder   The ViewHolder containing the views to update.
     * @param progress The current SeekBar progress.
     */
    private void handleSeekBarProgress(ImageViewHolder holder, int progress) {
        updateClipping(holder, progress);
        updateLinePosition(holder, progress);
    }

    /**
     * Updates the clipping of the image based on the SeekBar progress.
     *
     * @param holder   The ViewHolder containing the ImageView.
     * @param progress The current SeekBar progress.
     */
    private void updateClipping(ImageViewHolder holder, int progress) {
        if (holder.ImageViewAfterEnhance == null) return;

        // Ensure we get the actual scaled dimensions of the ImageView
        int width = holder.ImageViewAfterEnhance.getWidth();
        int height = holder.ImageViewAfterEnhance.getHeight();

        if (width > 0 && height > 0) {
            int clipWidth = (width * progress) / 100;

            // Clip only the width while keeping the full height
            holder.ImageViewAfterEnhance.setClipBounds(new Rect(0, 0, clipWidth, height));
            holder.ImageViewAfterEnhance.invalidate();
        }
    }

    /**
     * Updates the position of the slider line based on the SeekBar progress.
     *
     * @param holder   The ViewHolder containing the slider line.
     * @param progress The current SeekBar progress.
     */
    private void updateLinePosition(ImageViewHolder holder, int progress) {
        if (holder.sliderLine == null || holder.ImageViewAfterEnhance == null) return;

        float width = holder.ImageViewAfterEnhance.getWidth();
        float linePosition = (width * progress) / 100;

        holder.sliderLine.setX(linePosition - (holder.sliderLine.getWidth() / 2));
    }
    /**
     * Seeing An Animation On Slider & SliderLine On Initial State
     *
     * @param holder   The ViewHolder containing the slider line.
     */
    private void animateSlider(ImageViewHolder holder) {
        if (holder.slider == null) return;

        // Animate the SeekBar from 0 to 100 and back to 50
        ObjectAnimator animator = ObjectAnimator.ofInt(holder.slider, "progress", 0, 100, 50);
        animator.setDuration(3000); // 3 seconds
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.start();
    }






}
