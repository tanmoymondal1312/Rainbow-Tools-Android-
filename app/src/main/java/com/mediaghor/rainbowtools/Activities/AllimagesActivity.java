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
import androidx.recyclerview.widget.RecyclerView;

import com.mediaghor.rainbowtools.Adapter.ImageAdapter;
import com.mediaghor.rainbowtools.Helpers.GetImagesUris;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class AllimagesActivity extends AppCompatActivity {

    public static final String SELECTED_IMAGES_KEY = "selected_images";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG2", "AllimagesActivity onCreate called"); // Log when onCreate is called
        try {
            // Try setting the layout
            setContentView(R.layout.activity_all_images);
            Log.d("TAG2", "Layout set successfully");
            Log.d("TAG2", "View padding applied"); // Log after padding applied

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            Button btnAdd = findViewById(R.id.btnAdd);

            // Check if RecyclerView is null
            if (recyclerView == null) {
                Log.e("TAG2", "RecyclerView not found!");
            } else {
                Log.d("TAG2", "RecyclerView found");
            }

            // Check if Button is null
            if (btnAdd == null) {
                Log.e("TAG2", "Button Add not found!");
            } else {
                Log.d("TAG2", "Button Add found");
            }

        } catch (Exception e) {
            // Log any exception that happens during layout inflation
            Log.e("TAG2", "Error setting layout: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d("TAG2", "View padding applied"); // Log after padding applied

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button btnAdd = findViewById(R.id.btnAdd);

        // Fetch all image URIs
        Log.d("TAG2", "Fetching all image URIs");
        List<Uri> imageUris = GetImagesUris.getAllImagesUris(this);
        Log.d("TAG2", "Fetched " + imageUris.size() + " images"); // Log the number of images fetched

        // Set up RecyclerView
        ImageAdapter adapter = new ImageAdapter(this, imageUris);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns
        recyclerView.setAdapter(adapter);
        Log.d("TAG2", "RecyclerView set up"); // Log after RecyclerView setup

        // Handle Add button click
        btnAdd.setOnClickListener(v -> {
            Log.d("TAG2", "Add button clicked"); // Log when Add button is clicked
            List<Uri> selectedUris = adapter.getSelectedUris();
            if (selectedUris.isEmpty()) {
                Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
                Log.d("TAG2", "No images selected"); // Log if no images are selected
            } else {
                // Pass selected URIs back to the calling activity
                Log.d("TAG2", "Selected images: " + selectedUris.size()); // Log the number of selected images
                Intent resultIntent = new Intent();
                ArrayList<String> uriStrings = new ArrayList<>();
                for (Uri uri : selectedUris) {
                    uriStrings.add(uri.toString());
                    Log.d("TAG2", "Selected URI: " + uri.toString()); // Log each selected URI
                }
                resultIntent.putStringArrayListExtra(SELECTED_IMAGES_KEY, uriStrings);
                setResult(RESULT_OK, resultIntent);
                finish();
                Log.d("TAG2", "Result sent back to calling activity and activity finished"); // Log after finishing activity
            }
        });
    }
}
