package com.mediaghor.rainbowtools.Activities;

import android.annotation.SuppressLint;
import android.net.Uri;
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

import com.google.android.material.button.MaterialButton;
import com.mediaghor.rainbowtools.Adapter.BackgroundRemovedImageAdapter;
import com.mediaghor.rainbowtools.Adapter.PhotoPickerAdapter;
import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundRemoverActivity extends AppCompatActivity {
    //Variables
    ImageButton toolbarBackBtn,chooseFilesBtn;
    RecyclerView rv_pickImages,rv_bgRemovedImages;
    MaterialButton generateButton;
    ProgressBar progressIndicator;
    List<Uri> imageUrls = new ArrayList<>();
    BackgroundRemovedImageAdapter backgroundRemovedImageAdapter;





    //Select The Images
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5),uris ->{
                if(uris != null)
                {
                    Toast.makeText(this, String.valueOf(uris.size()), Toast.LENGTH_SHORT).show();
                    PhotoPickerAdapter adapter =  new PhotoPickerAdapter(uris);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    rv_pickImages.setLayoutManager(layoutManager);
                    rv_pickImages.setAdapter(adapter);
                    setButtonClickable();
                    uploadImagesToBackend(uris);
                }

            });









    private void uploadImagesToBackend(List<Uri> uris) {
        // Create an instance of ImageUploadHelper
        ImageUploadHelper imageUploadHelper = new ImageUploadHelper(this);

        // Call the uploadImages method
        imageUploadHelper.uploadImages(uris, new ImageUploadHelper.ImageUploadCallback() {
            @Override
            public void onImageUploadSuccess(List<Uri> imageUrls) {
                // Handle success: the imageUrls list contains the processed image URLs
                BackgroundRemoverActivity.this.imageUrls = imageUrls;
//                Log.d("post",imageUrls.toString());
                // Optionally, you can update the UI with the processed images
                Toast.makeText(BackgroundRemoverActivity.this, "Images processed successfully", Toast.LENGTH_SHORT).show();
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



    //Generate Button States
    // State 1: Disabled
    private void setButtonDisabled() {
        generateButton.setEnabled(false);
        generateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
        generateButton.setText("Generate");
        findViewById(R.id.progress_indicator).setVisibility(View.GONE); // Hide progress bar
    }

    // State 2: Clickable
    private void setButtonClickable() {
        generateButton.setEnabled(true);
        generateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_700));
        generateButton.setText("Generate");
        findViewById(R.id.progress_indicator).setVisibility(View.GONE); // Hide progress bar
    }

    // State 3: Generating
    private void setButtonGenerating() {
        generateButton.setEnabled(false);
        generateButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_700));
        generateButton.setText(""); // Clear text
        findViewById(R.id.progress_indicator).setVisibility(View.VISIBLE); // Show progress bar



    }

    private void AddingBgRemovedImages(){
        backgroundRemovedImageAdapter = new BackgroundRemovedImageAdapter(imageUrls);
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
        //Set status bar Colors and behaviour
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.darker_gray)); // Set to white background
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        toolbarBackBtn = findViewById(R.id.toolbar_back_btn);
        chooseFilesBtn = findViewById(R.id.choose_images);
        rv_pickImages = findViewById(R.id.recycler_for_selected_images);
        rv_bgRemovedImages = findViewById(R.id.recycler_for_processed_images);
        generateButton = findViewById(R.id.generate_button);
        progressIndicator = findViewById(R.id.progress_indicator);
        //Toolbar back button behaviour
        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Select images
       chooseFilesBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pickMultipleMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.
                       PickVisualMedia.ImageOnly.INSTANCE).build());
           }
       });
       //Button Generate
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonGenerating();
            }
        });
        //Added Some Manually
//        imageUrls.add(Uri.parse("http://192.168.0.105:8000/image-optimization/get_bg_removed_images/1.png"));
//        imageUrls.add(Uri.parse("https://fastly.picsum.photos/id/893/200/200.jpg?hmac=MKUqbcyRrvAYoTmgHo74fEI3o9V4CH2kBrvWfmHkr7U"));
//        imageUrls.add(Uri.parse("https://fastly.picsum.photos/id/670/200/300.jpg?grayscale&hmac=OfelnBKL5NEzJJiK2lCpkJww2xtIQZAsfyI5yWniBpo"));
//        imageUrls.add(Uri.parse("https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI"));
//        imageUrls.add(Uri.parse("http://192.168.0.105:8000/image-optimization/get_bg_removed_images/1.png"));


    }

}