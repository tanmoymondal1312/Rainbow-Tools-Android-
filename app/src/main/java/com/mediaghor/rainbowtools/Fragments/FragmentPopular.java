package com.mediaghor.rainbowtools.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mediaghor.rainbowtools.R;
import android.graphics.drawable.Drawable;

import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import android.graphics.drawable.Drawable;


//import javax.sql.DataSource;


public class FragmentPopular extends Fragment {
    ImageView imageView;
    TextView UrlText;



    public FragmentPopular() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        String url = "http://192.168.0.105:8000/image-optimization/get_bg_removed_images/2.png";
        imageView = view.findViewById(R.id.imgTest);
        UrlText = view.findViewById(R.id.urlText);

// Set the URL text to the TextView
        UrlText.setText(url);

// Load the image into ImageView using Glide with logging
        Glide.with(view.getContext())
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("Glide", "Error loading image: " + (e != null ? e.getMessage() : "Unknown error"));
                        return false; // Let Glide handle the error
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Image loaded successfully");
                        return false; // Allow Glide to handle the resource
                    }
                })
                .into(imageView);

// Log the URL to ensure it's correct
        Log.d("Glide", "Loading image from URL: " + url);



        return view;
    }
}