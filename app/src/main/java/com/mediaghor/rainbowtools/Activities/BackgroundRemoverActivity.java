package com.mediaghor.rainbowtools.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;



import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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

import com.mediaghor.rainbowtools.Adapter.SelectedImageAdapter;
import com.mediaghor.rainbowtools.Adapter.SelectedImagesAdapterForBgr;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;

import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.ProcessingDialog;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundRemoverActivity extends AppCompatActivity {
    //Variables

    //Components
    Button click;
    private ImageButton toolbarBackBtn,chooseFilesBtn;
    private AppCompatButton downLoadAll;
    private RecyclerView RV_SelectedImages,RV_ProcessedImages;
    private MaterialButton generateButton;
    private ProgressBar progressIndicator;
    LottieAnimationView lottieAnimationSelectImages,lottieAnimationGenerateImages;

    //Data
    private ArrayList<Uri> imageUrls = new ArrayList<>();
    private ArrayList<Uri> selectedUris;
    //Manual Variables
    SelectedImagesAdapterForBgr adapter;
    BackgroundRemovedImageAdapter adapter2;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    ButtonAnimationManager buttonAnimationManager;
    ProcessingDialog processingDialog;






    private void uploadImagesToBackend(ArrayList<Uri> uris) {
        ExecutorService executor = Executors.newSingleThreadExecutor(); // Use a single background thread for processing
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()); // Handler for communicating with the UI thread

        executor.execute(() -> {
            try {
                ImageUploadHelper imageUploadHelper = new ImageUploadHelper(this);
                imageUploadHelper.uploadImages(uris, new ImageUploadHelper.ImageUploadCallback() {
                    @Override
                    public void onImageUploadSuccess(ArrayList<Uri> imageUrls) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            BackgroundRemoverActivity.this.imageUrls = imageUrls;
                            Toast.makeText(BackgroundRemoverActivity.this, "Images processed successfully", Toast.LENGTH_SHORT).show();
                            notifyImageUploadSuccess(); // Signal external components
                        });
                    }

                    @Override
                    public void onImageUploadFailure(String errorMessage) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            Toast.makeText(BackgroundRemoverActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } catch (Exception e) {
                // Handle unexpected exceptions
                mainThreadHandler.post(() ->
                        Toast.makeText(BackgroundRemoverActivity.this, "Unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } finally {
                executor.shutdown(); // Release resources
            }
        });
    }


    private void WorkWithFinalImages() {
        processingDialog.show();
        adapter.setUploadingStates(true);
        buttonAnimationManager.GeneratingButtonAnimation("animated");
        ArrayList<Uri> UriForPost = adapter.getFinalSelectedUris();
        uploadImagesToBackend(UriForPost);
    }

    // Optional method to signal external components
    private void notifyImageUploadSuccess() {
        processingDialog.dismiss();
        SetProcessImagesInRecycler();
        buttonAnimationManager.GeneratingButtonAnimation("enable");

        // Implement your signaling mechanism here, e.g., a callback, event bus, or a LiveData observer
        // Example: EventBus.getDefault().post(new ImageUploadSuccessEvent(imageUrls));
    }




    private void SetProcessImagesInRecycler(){
        adapter2 = new BackgroundRemovedImageAdapter(this,imageUrls);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RV_ProcessedImages.setLayoutManager(layoutManager2);
        RV_ProcessedImages.setAdapter(adapter2);

    }
    private void SetSelectedImagesInRecycler(){
        buttonAnimationManager.SelectImageAnimation("unloop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        adapter = new SelectedImagesAdapterForBgr(this, selectedUris);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RV_SelectedImages.setLayoutManager(layoutManager);
        RV_SelectedImages.setAdapter(adapter);
        //Data Loaded In Ui And pause the loop


    }

    private void GetImagesFromDevices(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 (API 33) and above
            // Check if READ_MEDIA_IMAGES permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for images
                ImagePermissionHandler.requestImagePermission(BackgroundRemoverActivity.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(BackgroundRemoverActivity.this, AllimagesActivity.class);
                selectImagesLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8 to Android 12 (API 26-32)
            // Check if READ_EXTERNAL_STORAGE permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for external storage
                ImagePermissionHandler.requestImagePermission(BackgroundRemoverActivity.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(BackgroundRemoverActivity.this, AllimagesActivity.class);
                selectImagesLauncher.launch(intent);
            }
        } else {
            // For devices below Android 8, no need to request permission
            Toast.makeText(this, "No permission needed for devices below Android 8", Toast.LENGTH_SHORT).show();
        }
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
        //Setting Status bar color and icon color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.darker_gray)); // Set status bar color to white
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //Finding The Is
        toolbarBackBtn = findViewById(R.id.toolbar_back_btn);
        lottieAnimationSelectImages = findViewById(R.id.animation_select_image_id_abgr);
        lottieAnimationGenerateImages = findViewById(R.id.generating_animation_id_bg_remover_layout);
        RV_SelectedImages = findViewById(R.id.rv_right_for_selected_image_bg_remover_activity);
        RV_ProcessedImages = findViewById(R.id.rv_left_for_bg_removed_images_bg_remover_activity);
        //Implementation Everything
        processingDialog = new ProcessingDialog(this);
        buttonAnimationManager = new ButtonAnimationManager(this);
        //On create animation and visibility components start
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");


        //Toolbar Back Button
        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Select Images Opening Image Chooser Activity (AllimagesActivity)
        lottieAnimationSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImagesFromDevices();
            }
        });
        lottieAnimationGenerateImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkWithFinalImages();
            }
        });











        //Get Result From (AllimagesActivity)
        selectImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedUris = result.getData()
                                .getParcelableArrayListExtra(AllimagesActivity.SELECTED_IMAGES_KEY);
                        if (selectedUris != null && !selectedUris.isEmpty()) {
                            SetSelectedImagesInRecycler();
                        }
                        else {

                        }
                    }
                }
        );






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