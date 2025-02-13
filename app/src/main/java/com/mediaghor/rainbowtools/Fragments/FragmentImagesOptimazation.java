package com.mediaghor.rainbowtools.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
        // Preparing data for the adapter
        List<CardItemsModel> cardItemsList = new ArrayList<>();
        cardItemsList.add(new CardItemsModel(
                "Background Remover",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.bg_remover
        ));
        cardItemsList.add(new CardItemsModel(
                "Photo Optimizer",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.img_enhance_card_tmplt
        ));
        cardItemsList.add(new CardItemsModel(
                "Text Extractor",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.txt_extraxt
        ));
        cardItemsList.add(new CardItemsModel(
                "Background Remover",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.bg_remover
        ));

        // Setting adapter
        CardItemAdapter adapter = new CardItemAdapter(requireContext(), cardItemsList);
        recyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }
}
