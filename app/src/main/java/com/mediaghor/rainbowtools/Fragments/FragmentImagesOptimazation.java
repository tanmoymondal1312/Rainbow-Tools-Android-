package com.mediaghor.rainbowtools.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.mediaghor.rainbowtools.R;

public class FragmentImagesOptimazation extends Fragment {

    public FragmentImagesOptimazation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_images_optimazation, container, false);

        // Find the VideoView by its ID
        VideoView videoView = rootView.findViewById(R.id.video_view);

        // Path to the video file in the raw folder
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.card1bgremover);

        // Set the video URI
        videoView.setVideoURI(videoUri);

        // Start the video
        videoView.start();

        return rootView;
    }
}
