package com.mediaghor.rainbowtools.OthersClasses;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class SliderMovementSystem {
    private final ImageView imageView;
    private final View sliderLine;
    private float dX; // To store the difference between touch point and view's position

    public SliderMovementSystem(ImageView imageView, View sliderLine) {
        this.imageView = imageView;
        this.sliderLine = sliderLine;
    }

    /**
     * Sets up the movement system for the slider and ImageView.
     */
    public void setup() {
        // Set initial clipping boundary after the view is laid out
        imageView.post(() -> {
            int halfWidth = imageView.getWidth() / 2; // Calculate half the width
            Rect rect = new Rect(0, 0, halfWidth, imageView.getHeight()); // Define the clipping rectangle
            imageView.setClipBounds(rect); // Apply the clipping boundary
        });

        // Set touch listener for sliderLine
        sliderLine.setOnTouchListener(this::handleTouch);
    }

    /**
     * Handles touch events for the slider line.
     *
     * @param v     The view being touched.
     * @param event The motion event.
     * @return true if the event is handled.
     */
    private boolean handleTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Capture the offset between touch and view's position
                dX = v.getX() - event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                // Update the position with offset considered
                v.setX(event.getRawX() + dX);

                // Adjust the clipping boundary of the image view
                imageView.post(() -> {
                    Rect rect = new Rect(0, 0, (int) v.getX(), imageView.getHeight());
                    imageView.setClipBounds(rect);
                });
                break;
        }
        return true; // Indicate that the event is handled.
    }
}
