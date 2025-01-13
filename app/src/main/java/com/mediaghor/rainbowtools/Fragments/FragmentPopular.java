package com.mediaghor.rainbowtools.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mediaghor.rainbowtools.Activities.BackgroundRemoverActivity;
import com.mediaghor.rainbowtools.OthersClasses.CustomNetworkDialog;
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
    AppCompatButton btn;
    AppCompatButton cancel_processing;




    public FragmentPopular() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);
        btn = view.findViewById(R.id.click_in_popular);
        //cancel_processing = view.findViewById(R.id.id_btn_cancel_processing_bg_removed_layout);

        //cancel_processing.setVisibility(View.GONE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomNetworkDialog dialog = new CustomNetworkDialog(
                        getContext(), // Context (e.g., Activity)
                        R.drawable.no_internet, // Image resource
                        "Please Connect Your Internet !" // Dynamic message
                );
                dialog.show();

//                cancel_processing.setVisibility(View.VISIBLE);


            }

        });

        //processingMode("disable");

//        cancel_processing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancel_processing.setVisibility(View.GONE);
//
//                Toast.makeText(getContext(), "Just Wait A Little Longer", Toast.LENGTH_SHORT).show();
//
////                Log.d("Cancel","Clicked");
////                processingMode("disable");
//            }
//        });




        return view;
    }
}