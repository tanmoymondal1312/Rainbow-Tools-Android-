package com.mediaghor.rainbowtools.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.mediaghor.rainbowtools.R;

public class FragmentPopular extends Fragment {

    private ImageView ImageAfterEnhance, ImageBeforeEnhance; // Foreground and background images
    private SeekBar slider;       // Slider to control clipping
    private View sliderLine;      // Line to represent the slider's position
    private FrameLayout frameLayout;

    public FragmentPopular() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        // Initialize views
//        ImageBeforeEnhance = view.findViewById(R.id.img_id_recycler_before_enhance);
//        ImageAfterEnhance = view.findViewById(R.id.img_id_recycler_after_enhance);
        //slider = view.findViewById(R.id.slider);
//        sliderLine = view.findViewById(R.id.slider_line);


        // Wait for layout setup
//        ImageAfterEnhance.post(() -> {
//            slider.setProgress(50);
//            updateClipping(slider.getProgress());
//            updateLinePosition(slider.getProgress());
//        });
//
//        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                updateClipping(progress);
//                updateLinePosition(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
        return view;
    }

    // Method to update clipping using Canvas (custom draw)
    private void updateClipping(int progress) {
        if (ImageAfterEnhance == null) return;

        // Calculate clipping width based on progress
        int width = ImageAfterEnhance.getWidth();
        int clipWidth = (width * progress) / 100;

        // Apply clipping
        ImageAfterEnhance.setClipBounds(new android.graphics.Rect(0, 0, clipWidth, ImageAfterEnhance.getHeight()));
        ImageAfterEnhance.invalidate(); // Redraw the view
    }

    // Update the position of the slider line
    private void updateLinePosition(int progress) {
        if (sliderLine == null || ImageAfterEnhance == null) return;

        // Calculate line position based on ImageAfterEnhance width
        float width = ImageAfterEnhance.getWidth();
        float linePosition = (width * progress) / 100;

        // Set the X position of the line
        sliderLine.setX(linePosition - (sliderLine.getWidth() / 2));
    }
}
