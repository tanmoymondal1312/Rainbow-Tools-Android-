package com.mediaghor.rainbowtools.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mediaghor.rainbowtools.Activities.MainActivity;
import com.mediaghor.rainbowtools.Adapter.CardItemAdapter;
import com.mediaghor.rainbowtools.Models.CardItemsModel;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentPDFOptimization extends Fragment {

    private View rootView;
    private View toolbar;
    View toolbarView;


    public FragmentPDFOptimization() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_p_d_f_optimization, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.use_now_recycler_view_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Preparing data for the adapter
        List<CardItemsModel> cardItemsList = new ArrayList<>();
        cardItemsList.add(new CardItemsModel(
                "PDF to DOCX",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.pdf_to_docx
        ));
        cardItemsList.add(new CardItemsModel(
                "Photo Optimizer",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.img_enhance_card_tmplt
        ));
        cardItemsList.add(new CardItemsModel(
                "Text Extractor From Images",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.txt_extraxt
        ));
        cardItemsList.add(new CardItemsModel(
                "Image Size Reducer",
                "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.card_img_size_reducer_
        ));

        // Setting adapter
        CardItemAdapter adapter = new CardItemAdapter(requireContext(), cardItemsList,2);
        recyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }
}