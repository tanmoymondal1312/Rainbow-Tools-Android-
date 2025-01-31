package com.mediaghor.rainbowtools.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.Adapter.EnhanceImagesAdapter;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;
import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomNetworkDialog;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnhanceImagesActivity extends AppCompatActivity {

    private ImageButton toolbarBackBtn,settingToolbarImgBtn;
    LottieAnimationView lottieAnimationSelectImages,generateButton,downloadAllImagesLottie;
    TextView toolbarTitle;

    //ArrayList<Integer> beforeEnhanceImages = new ArrayList<>();
    private ArrayList<Uri>beforeEnhanceImages;
    private ArrayList<Uri>afterEnhanceImages = new ArrayList<>();



    RecyclerView recyclerViewImageEnhance;
    EnhanceImagesAdapter adapter;
    ButtonAnimationManager buttonAnimationManager;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    ImageUploadHelper imageUploadHelper;



    private void uploadImagesToBackend(ArrayList<Uri> uris) {
        ExecutorService executor = Executors.newSingleThreadExecutor(); // Use a single background thread for processing
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()); // Handler for communicating with the UI thread

        executor.execute(() -> {
            try {
                imageUploadHelper = new ImageUploadHelper(this,2);
                imageUploadHelper.uploadImages(uris, new ImageUploadHelper.ImageUploadCallback() {
                    @Override
                    public void onImageUploadSuccess(ArrayList<Uri> imageUrls) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            EnhanceImagesActivity.this.afterEnhanceImages = imageUrls;
                            EnhanceImagesAccepted();
                            Toast.makeText(EnhanceImagesActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        });
                    }

                    @Override
                    public void onImageUploadFailure(String errorMessage) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            //Hande If cancel or connection failed
                        });
                    }
                });
            } catch (Exception e) {
                // Handle unexpected exceptions
                mainThreadHandler.post(() ->
                        Toast.makeText(EnhanceImagesActivity.this, "Unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } finally {
                executor.shutdown(); // Release resources
            }
        });
    }





    private void SetSelectedImagesInRecycler(){
        buttonAnimationManager.SelectImageAnimation("unloop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        recyclerViewImageEnhance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EnhanceImagesAdapter(EnhanceImagesActivity.this,beforeEnhanceImages);
        recyclerViewImageEnhance.setAdapter(adapter);
    }
    private void WorkWithFinalImages() {
        // These lines will execute immediately
        buttonAnimationManager.GeneratingButtonAnimation("animated");
        adapter.setUploadingState(true);
        uploadImagesToBackend(adapter.GetFinalSelectedImages());

        // Wait 5 seconds before showing the custom dialog
    }
    private void EnhanceImagesAccepted(){
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        adapter.AddAfterEnhanceImagesToArray(afterEnhanceImages);
    }






    private void GetImagesFromDevices(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 (API 33) and above
            // Check if READ_MEDIA_IMAGES permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for images
                ImagePermissionHandler.requestImagePermission(EnhanceImagesActivity.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(EnhanceImagesActivity.this, AllimagesActivity.class);
                selectImagesLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8 to Android 12 (API 26-32)
            // Check if READ_EXTERNAL_STORAGE permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for external storage
                ImagePermissionHandler.requestImagePermission(EnhanceImagesActivity.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(EnhanceImagesActivity.this, AllimagesActivity.class);
                selectImagesLauncher.launch(intent);
            }
        } else {
            // For devices below Android 8, no need to request permission
            Toast.makeText(this, "No permission needed for devices below Android 8", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance_images);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.darker_gray)); // Set status bar color to white
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //Find The Id
        toolbarBackBtn = findViewById(R.id.toolbar_back_btn);
        settingToolbarImgBtn = findViewById(R.id.setting_img_btn_tools_toolbar);
        toolbarTitle = findViewById(R.id.toolbar_in_tools_title_id);

        lottieAnimationSelectImages = findViewById(R.id.animation_select_image_id_abgr);
        generateButton = findViewById(R.id.generating_animation_id_bg_remover_layout);
        downloadAllImagesLottie = findViewById(R.id.lottie_anim_download_in_bottom_toolbar_bgrl);
        recyclerViewImageEnhance = findViewById(R.id.recycler_id_enhance_images);



        buttonAnimationManager = new ButtonAnimationManager(this);




        //Initial States
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");


//        afterEnhanceImages.add(Uri.parse("https://picsum.photos/id/237/200/300"));
//        afterEnhanceImages.add(Uri.parse("https://picsum.photos/seed/picsum/200/300"));
//        afterEnhanceImages.add(Uri.parse("https://picsum.photos/200/300?grayscale"));


        //Set Listener
        toolbarTitle.setText("Images Enhance");

        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lottieAnimationSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImagesFromDevices();
            }
        });
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkWithFinalImages();
            }
        });

        settingToolbarImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                adapter.AddAfterEnhanceImagesToArray(afterEnhanceImages);
            }
        });




        //Get Result From (AllimagesActivity)
        selectImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        beforeEnhanceImages = result.getData()
                                .getParcelableArrayListExtra(AllimagesActivity.SELECTED_IMAGES_KEY);

                        //afterEnhanceImages = new ArrayList<>(beforeEnhanceImages);

                        if (beforeEnhanceImages != null && !beforeEnhanceImages.isEmpty()) {
                            SetSelectedImagesInRecycler();
                        }
                        else {

                        }
                    }
                }
        );

    }//On Create End ===========================================================
}