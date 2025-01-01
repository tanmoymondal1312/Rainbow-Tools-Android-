package com.mediaghor.rainbowtools.Activities;

import static com.mediaghor.rainbowtools.R.drawable.*;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.mediaghor.rainbowtools.Adapter.BackgroundRemovedImageAdapter;

import com.mediaghor.rainbowtools.Adapter.PhotoPickerAdapter;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;
import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import android.Manifest;

import com.mediaghor.rainbowtools.OthersClasses.ProcessingDialog;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundRemoverActivity extends AppCompatActivity {
    //Variables

    //Components
    Button click;
    private ImageButton toolbarBackBtn,chooseFilesBtn;
    private AppCompatButton downLoadAll;
    private RecyclerView rv_pickImages,rv_bgRemovedImages;
    private MaterialButton generateButton;
    private ProgressBar progressIndicator;
    LottieAnimationView lottieAnimationView ;

    //Data
    private List<Uri> imageUrls = new ArrayList<>();
    public List<Uri> selectedUris;

    //Manual Variables
    BackgroundRemovedImageAdapter backgroundRemovedImageAdapter;
//    ProcessingDialog processingDialog;





    //Select The Images and saw in adapter and store in a public variable
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5),uris ->{
                if(uris != null)
                {
                    selectedUris = uris;
                    Toast.makeText(this, uris.size() + " images selected", Toast.LENGTH_SHORT).show();
                    PhotoPickerAdapter adapter =  new PhotoPickerAdapter(uris);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    rv_pickImages.setLayoutManager(layoutManager);
                    rv_pickImages.setAdapter(adapter);
                    setButtonClickable();
                }

            });

    private void uploadImagesToBackend(List<Uri> uris) {
        ImageUploadHelper imageUploadHelper = new ImageUploadHelper(this);
        imageUploadHelper.uploadImages(uris, new ImageUploadHelper.ImageUploadCallback() {
            @Override
            public void onImageUploadSuccess(List<Uri> imageUrls) {
                // Handle success: the imageUrls list contains the processed image URLs
                BackgroundRemoverActivity.this.imageUrls = imageUrls;
                Toast.makeText(BackgroundRemoverActivity.this, "Images processed successfully", Toast.LENGTH_SHORT).show();
                setAllDownloadButtonVisible();
                AddingBgRemovedImages();
                setButtonDisabled();
            }


            @Override
            public void onImageUploadFailure(String errorMessage) {
                // Handle failure
                Toast.makeText(BackgroundRemoverActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                setButtonDisabled();
            }
        });
    }


    //Button States
    void setAllDownloadButtonInvisible() {
        downLoadAll.setVisibility(View.INVISIBLE); // Hide the button
        lottieAnimationView.setVisibility(View.GONE); // Hide animation if visible
    }
    void setAllDownloadButtonVisible() {
        downLoadAll.setVisibility(View.VISIBLE); // Show the button
//        downLoadAll.setText("Download All"); // Reset the text if modified earlier
//        lottieAnimationView.setVisibility(View.GONE); // Ensure animation is hidden
    }
    void setAnimationVisible() {
        downLoadAll.setVisibility(View.INVISIBLE);
        lottieAnimationView.setVisibility(View.VISIBLE); // Show animation
        lottieAnimationView.playAnimation(); // Start the animation
    }
    // State 1: Disabled
    private void setButtonDisabled() {
        generateButton.setEnabled(false);
        generateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
        generateButton.setText("Generate");
        progressIndicator.setVisibility(View.GONE); // Hide progress bar
    }
    // State 2: Clickable
    private void setButtonClickable() {
        generateButton.setEnabled(true);
        generateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_700));
        generateButton.setText("Generate");
        progressIndicator.setVisibility(View.GONE); // Hide progress bar
    }
    // State 3: Generating
    private void setButtonGenerating() {
        generateButton.setEnabled(false);
        generateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_700));
        generateButton.setText(""); // Clear text
        progressIndicator.setVisibility(View.VISIBLE); // Show progress bar
    }
    //Function To Add The Images From Response to adapter
    private void AddingBgRemovedImages(){
//        processingDialog.stopProcessingDialog();
        backgroundRemovedImageAdapter = new BackgroundRemovedImageAdapter(imageUrls, this);
        LinearLayoutManager layoutManager_2 = new LinearLayoutManager(this);
        rv_bgRemovedImages.setLayoutManager(layoutManager_2);
        rv_bgRemovedImages.setAdapter(backgroundRemovedImageAdapter);
    }


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_background_remover);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.darker_gray)); // Set status bar color to white
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        toolbarBackBtn = findViewById(R.id.toolbar_back_btn);
        click = findViewById(R.id.click);

        ActivityResultLauncher<Intent> selectImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        List<String> selectedImages = result.getData().getStringArrayListExtra(AllimagesActivity.SELECTED_IMAGES_KEY);
                        if (selectedImages != null && !selectedImages.isEmpty()) {
                            Toast.makeText(this, "Selected Images: " + selectedImages, Toast.LENGTH_LONG).show();
                            // Handle selected images here
                        }
                    }
                }
        );

        // Handle click event
        click.setOnClickListener(v -> {
            // Request image permissions
            //Log.d("TAG2", "Request Done");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 (API 33) and above
                // Check if READ_MEDIA_IMAGES permission is granted
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission for images
                    ImagePermissionHandler.requestImagePermission(BackgroundRemoverActivity.this);
                } else {
                    // Permission already granted, proceed with accessing images
                    Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    // Launch AllimagesActivity
                    Intent intent = new Intent(BackgroundRemoverActivity.this, AllimagesActivity.class);
                    Log.d("TAG2", "Intent set");
                    selectImagesLauncher.launch(intent);
                    Log.d("TAG2", "Intent launched");
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8 to Android 12 (API 26-32)
                // Check if READ_EXTERNAL_STORAGE permission is granted
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission for external storage
                    ImagePermissionHandler.requestImagePermission(BackgroundRemoverActivity.this);
                } else {
                    // Permission already granted, proceed with accessing images
                    Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    // Launch AllimagesActivity
                    Intent intent = new Intent(BackgroundRemoverActivity.this, AllimagesActivity.class);
                    Log.d("TAG2", "Intent set");
                    selectImagesLauncher.launch(intent);
                    Log.d("TAG2", "Intent launched");
                }
            } else {
                // For devices below Android 8, no need to request permission
                Toast.makeText(this, "No permission needed for devices below Android 8", Toast.LENGTH_SHORT).show();
            }
        });

        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        chooseFilesBtn = findViewById(R.id.choose_images);
//        rv_pickImages = findViewById(R.id.recycler_for_selected_images);
//        rv_bgRemovedImages = findViewById(R.id.recycler_for_processed_images);
//        generateButton = findViewById(R.id.generate_button);
//        progressIndicator = findViewById(R.id.progress_indicator);
//        downLoadAll = findViewById(R.id.btn_download_all_bg_removed_images);
//        lottieAnimationView = findViewById(R.id.lottie_animation_view_id);
        //Toolbar back button behaviour

//        //Select images
//        chooseFilesBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.
//                        PickVisualMedia.ImageOnly.INSTANCE).build());
//            }
//        });
//        //Button Generate
//        generateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                processingDialog = new ProcessingDialog(BackgroundRemoverActivity.this);
////                processingDialog.startProcessingDialog();
//                uploadImagesToBackend(selectedUris);
//                setButtonGenerating();
//            }
//        });
//        //Button Download All Bg Removed Images
//        downLoadAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setAnimationVisible();
//                backgroundRemovedImageAdapter.downloadAllImages();
//            }
//        });
//        //Set Animation And All Download Button Invisible
//        setAllDownloadButtonInvisible();
    }

}