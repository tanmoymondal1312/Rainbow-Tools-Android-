package com.mediaghor.rainbowtools.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediaghor.rainbowtools.Adapter.CardItemAdapter;
import com.mediaghor.rainbowtools.Models.CardItemsModel;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentImagesOptimazation extends Fragment {

    public FragmentImagesOptimazation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_images_optimazation, container, false);

        // Setting up RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.use_now_recycler_view_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Log.d("jjj","Niceeee");
        // Preparing data for the adapter
        List<CardItemsModel> cardItemsList = new ArrayList<>();
        cardItemsList.add(new CardItemsModel(
                "Background Remover",
                "android.resource://" + requireContext().getPackageName() + "/" + R.raw.card1bgremover
        ));
        cardItemsList.add(new CardItemsModel(
                "Photo Optimizer",
                "android.resource://" + requireContext().getPackageName() + "/" + R.raw.imageresulationincreser
        ));
        cardItemsList.add(new CardItemsModel(
                "Image Resizer",
                "android.resource://" + requireContext().getPackageName() + "/" + R.raw.card3imageresizer
        ));

        // Setting adapter
        CardItemAdapter adapter = new CardItemAdapter(requireContext(), cardItemsList);
        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        return rootView;
    }
}
