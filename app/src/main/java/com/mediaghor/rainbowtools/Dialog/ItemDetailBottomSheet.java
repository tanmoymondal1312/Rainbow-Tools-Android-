package com.mediaghor.rainbowtools.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mediaghor.rainbowtools.R;
import java.io.File;
import com.github.barteksc.pdfviewer.PDFView;


public class ItemDetailBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_PDF_PATH = "pdf_path";
    private String pdfPath;

    public static ItemDetailBottomSheet newInstance(String pdfPath) {
        ItemDetailBottomSheet fragment = new ItemDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PDF_PATH, pdfPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_item_detail, container, false);
        PDFView pdfView = view.findViewById(R.id.pdfView);

        if (getArguments() != null) {
            pdfPath = getArguments().getString(ARG_PDF_PATH);
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                pdfView.fromFile(pdfFile)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .load();
            }
        }

        return view;
    }
}
