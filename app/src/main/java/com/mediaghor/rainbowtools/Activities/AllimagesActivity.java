package com.mediaghor.rainbowtools.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mediaghor.rainbowtools.Adapter.ImageAdapter;
import com.mediaghor.rainbowtools.Adapter.SelectedImageAdapter;
import com.mediaghor.rainbowtools.Helpers.GetImagesUris;
import com.mediaghor.rainbowtools.Helpers.OnItemClickListener;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllimagesActivity extends AppCompatActivity implements OnItemClickListener {

    public static final String SELECTED_IMAGES_KEY = "selected_images";
    List<Uri> selectedUris = new ArrayList<>();
    RecyclerView recyclerView_2;
    SelectedImageAdapter selectedImageAdapter;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_images);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView_2 = findViewById(R.id.recyclerView_2);
        Button btnAdd = findViewById(R.id.btnAdd);
        // Fetch all image URIs
        List<Uri> imageUris = GetImagesUris.getAllImagesUris(this);

        // Set up RecyclerView

        ImageAdapter adapter = new ImageAdapter(this, imageUris,5);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns
        recyclerView.setAdapter(adapter);


        btnAdd.setOnClickListener(v -> {
            if (selectedUris.isEmpty()) {

            }
            else {
                // Pass selected URIs back to the calling activity
                Intent resultIntent = new Intent();
                ArrayList<Uri> MyUri = new ArrayList<>(selectedUris);
                resultIntent.putParcelableArrayListExtra(SELECTED_IMAGES_KEY, MyUri);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
    @Override
    public void onItemClick(Uri uri,int signal) {
        if (signal == 1){
            selectedUris.add(0,uri);
        }else {
            selectedUris.remove(uri);
        }
        if (selectedImageAdapter == null) {
            // Initialize adapter only once
            selectedImageAdapter = new SelectedImageAdapter(this, this.selectedUris);
            recyclerView_2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_2.setAdapter(selectedImageAdapter);
        } else {
            selectedImageAdapter.notifyDataSetChanged();

        }
    }

}


