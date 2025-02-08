package com.mediaghor.rainbowtools.Activities;

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
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.Adapter.EnhanceImagesAdapter;
import com.mediaghor.rainbowtools.Helpers.CheckConnection;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;
import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomNetworkDialog;
import com.mediaghor.rainbowtools.OthersClasses.CustomToastManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnhanceImagesActivity extends AppCompatActivity {

    ImageButton selectImagesFromBdy,toolbarBackBtn;
    LottieAnimationView lottieAnimationSelectImages,generateButton,downloadAllImagesLottie,congressCuttingPaper;
    TextView toolbarTitle,InstructionText;
    Button btnCancelProcessing;

    //ArrayList<Integer> beforeEnhanceImages = new ArrayList<>();
    private ArrayList<Uri>beforeEnhanceImages;
    private ArrayList<Uri>afterEnhanceImages = new ArrayList<>();



    RecyclerView recyclerViewImageEnhance;
    EnhanceImagesAdapter adapter;
    ButtonAnimationManager buttonAnimationManager;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    ImageUploadHelper imageUploadHelper;
    CheckConnection checkConnection;
    CustomToastManager customToastManager;




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
                            buttonAnimationManager.ButterflyProgressing("disable");
                            btnCancelProcessing.setVisibility(View.GONE);
                            EnhanceImagesActivity.this.afterEnhanceImages = imageUrls;
                            EnhanceImagesAccepted();
                            buttonAnimationManager.DownloadAllImagesAnimation("download");
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


    private void CancelProcessing(){
        imageUploadHelper.cancelUpload();
        btnCancelProcessing.setVisibility(View.GONE);
        buttonAnimationManager.ButterflyProgressing("disable");
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        customToastManager.showDownloadSuccessToast(R.drawable.baseline_cancel_24,"Processing Cancelled",3);


    }



    private void SetSelectedImagesInRecycler(){
        recyclerViewImageEnhance.setVisibility(View.VISIBLE);
        selectImagesFromBdy.setVisibility(View.GONE);
        InstructionText.setVisibility(View.GONE);
        buttonAnimationManager.SelectImageAnimation("unloop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");

        recyclerViewImageEnhance.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EnhanceImagesAdapter(this, beforeEnhanceImages, new EnhanceImagesAdapter.OnImageListEmptyListener() {
            @Override
            public void onImageListEmpty() {
                recyclerViewImageEnhance.setVisibility(View.GONE);
                selectImagesFromBdy.setVisibility(View.VISIBLE);
                InstructionText.setVisibility(View.VISIBLE);


                // Play animations
                buttonAnimationManager.SelectImageAnimation("loop_animation");
                buttonAnimationManager.GeneratingButtonAnimation("disable");
            }
        });
        recyclerViewImageEnhance.setAdapter(adapter);


    }
    private void WorkWithFinalImages() {
        // These lines will execute immediately
        btnCancelProcessing.setVisibility(View.VISIBLE);
        buttonAnimationManager.GeneratingButtonAnimation("animated");
        buttonAnimationManager.ButterflyProgressing("progressing");
        adapter.setUploadingState(true);
        uploadImagesToBackend(adapter.GetFinalSelectedImages());

        // Wait 5 seconds before showing the custom dialog
    }
    private void EnhanceImagesAccepted(){
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        adapter.AddAfterEnhanceImagesToArray(afterEnhanceImages);
        buttonAnimationManager.CongressCuttingPaperAnimation("start");

    }






    private void GetImagesFromDevices(){
        Log.d("GIFD","Get Images from device called");
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
        toolbarTitle = findViewById(R.id.toolbar_in_tools_title_id);

        lottieAnimationSelectImages = findViewById(R.id.animation_select_image_id_abgr);
        congressCuttingPaper = findViewById(R.id.lottie_cutting_paper_congress_in_img_inhnc_layout);
        generateButton = findViewById(R.id.generating_animation_id_bg_remover_layout);
        downloadAllImagesLottie = findViewById(R.id.lottie_anim_download_in_bottom_toolbar_bgrl);
        recyclerViewImageEnhance = findViewById(R.id.recycler_id_enhance_images);

        selectImagesFromBdy = findViewById(R.id.select_img_btn_in_bdy_enhance_img_layout);
        InstructionText = findViewById(R.id.notyTextView_in_bdy_inhnce_img_layout);

        btnCancelProcessing = findViewById(R.id.btn_cancel_processing_rbg_l);




        buttonAnimationManager = new ButtonAnimationManager(this);
        checkConnection = new CheckConnection(this);
        customToastManager = new CustomToastManager(this);





        //Initial States
        btnCancelProcessing.setVisibility(View.GONE);
        congressCuttingPaper.setVisibility(View.GONE);
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");



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
                if (checkConnection.isInternetConnected()) {
                    // If the device is connected to the internet, check server connection
                    WorkWithFinalImages();
                    checkConnection.checkServerConnection(new CheckConnection.ServerConnectionListener() {
                        @Override
                        public void onConnectionChecked(boolean isConnected) {
                            // Handle the result based on server connection status
                            Log.d("Called","Called Function");
                            if (isConnected) {
                                Log.d("Called","Connected");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EnhanceImagesActivity.this, "Just Wait A Little Longer", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.d("Called","Connection Failed");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CancelProcessing();
                                        CustomNetworkDialog dialog = new CustomNetworkDialog(
                                                EnhanceImagesActivity.this, // Context (e.g., Activity)
                                                R.drawable.server_error_2, // Image resource
                                                "Server Connection Failed, Please Try Again !" // Dynamic message
                                        );
                                        dialog.show();
                                    }
                                });

                            }
                        }
                    });
                } else {
                    CustomNetworkDialog dialog = new CustomNetworkDialog(
                            EnhanceImagesActivity.this, // Context (e.g., Activity)
                            R.drawable.no_internet, // Image resource
                            "Please Connect Your Internet ." // Dynamic message
                    );
                    dialog.show();
                }
            }
        });
        selectImagesFromBdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImagesFromDevices();

            }
        });
        btnCancelProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelProcessing();
            }
        });


        downloadAllImagesLottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.DownloadAllImages();
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

    @Override
    public void onBackPressed() {
        new Thread(() -> {
            Glide.get(getApplicationContext()).clearDiskCache(); // Clears disk cache
        }).start();

        Glide.get(getApplicationContext()).clearMemory(); // Clears memory cache
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        new Thread(() -> {
            Glide.get(getApplicationContext()).clearDiskCache(); // Clears disk cache
        }).start();

        Glide.get(getApplicationContext()).clearMemory(); // Clears memory cache
        super.onDestroy();
    }



}