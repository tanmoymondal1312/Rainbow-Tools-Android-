package com.mediaghor.rainbowtools.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.Adapter.BackgroundRemovedImageAdapter;

import com.mediaghor.rainbowtools.Adapter.SelectedImagesAdapterForBgr;
import com.mediaghor.rainbowtools.Helpers.CheckConnection;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;

import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomNetworkDialog;
import com.mediaghor.rainbowtools.OthersClasses.CustomToastManager;
import com.mediaghor.rainbowtools.OthersClasses.ProcessingDialog;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundRemoverActivity extends AppCompatActivity {
    //Variables

    //Components
    private ImageButton toolbarBackBtn;
    AppCompatButton cancel_processing;
    private RecyclerView RV_SelectedImages,RV_ProcessedImages;
    LottieAnimationView lottieAnimationSelectImages,lottieAnimationGenerateImages,download,downloading,lottieGeneratingProgressBar;
    View ProgressingIncluded;
    TextView textView_1,textView_2;




    //Data
    private ArrayList<Uri> imageUrls = new ArrayList<>();
    private ArrayList<Uri> selectedUris;
    private HashMap<String, Integer> buttonIdMap = new HashMap<>();

    //Manual Variables
    SelectedImagesAdapterForBgr adapter;
    BackgroundRemovedImageAdapter adapter2;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    ButtonAnimationManager buttonAnimationManager;
    CustomToastManager customToastManager;
    private CheckConnection checkConnection;
    ImageUploadHelper imageUploadHelper;








    private void showLoading() {
        ProgressingIncluded.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        ProgressingIncluded.setVisibility(View.GONE);
    }


    private void uploadImagesToBackend(ArrayList<Uri> uris) {
        ExecutorService executor = Executors.newSingleThreadExecutor(); // Use a single background thread for processing
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()); // Handler for communicating with the UI thread

        executor.execute(() -> {
            try {
                imageUploadHelper = new ImageUploadHelper(this,1);
                imageUploadHelper.uploadImages(uris, new ImageUploadHelper.ImageUploadCallback() {
                    @Override
                    public void onImageUploadSuccess(ArrayList<Uri> imageUrls) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            BackgroundRemoverActivity.this.imageUrls = imageUrls;
                            notifyImageUploadSuccess(); // Signal external components
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
                        Toast.makeText(BackgroundRemoverActivity.this, "Unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } finally {
                executor.shutdown(); // Release resources
            }
        });
    }


    private void WorkWithFinalImages() {
        showLoading();
        cancel_processing.setVisibility(View.VISIBLE);
        adapter.setUploadingStates(true);
        buttonAnimationManager.GeneratingButtonAnimation("animated");
        ArrayList<Uri> UriForPost = adapter.getFinalSelectedUris();
        uploadImagesToBackend(UriForPost);
    }

    // Optional method to signal external components
    private void notifyImageUploadSuccess() {
        hideLoading();
        cancel_processing.setVisibility(View.GONE);
        SetProcessImagesInRecycler();
        buttonAnimationManager.GeneratingButtonAnimation("enable");
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
        download = findViewById(R.id.lottie_anim_download_in_bottom_toolbar_bgrl);
        downloading = findViewById(R.id.lottie_anim_downloading_in_bottom_toolbar_bgrl);
        RV_SelectedImages = findViewById(R.id.rv_right_for_selected_image_bg_remover_activity);
        RV_ProcessedImages = findViewById(R.id.rv_left_for_bg_removed_images_bg_remover_activity);
        cancel_processing = findViewById(R.id.btn_cancel_processing_rbg_l);
        ProgressingIncluded = findViewById(R.id.lottie_generating_in_bgr_l);
        lottieGeneratingProgressBar = findViewById(R.id.lottieAnimation_generating);
        textView_1 = findViewById(R.id.txt_view_1_in_bg_remover_bdy_layout);
        //Implementation Everything
        buttonAnimationManager = new ButtonAnimationManager(this);
        customToastManager = new CustomToastManager(this);
        checkConnection = new CheckConnection(this);
        //On create animation and visibility components start



        buttonAnimationManager.applyMovingRainbowGradient(textView_1);
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");
        cancel_processing.setVisibility(View.GONE);
        hideLoading();



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
        //Generate And Generating Animation Behaviour
        lottieAnimationGenerateImages.setOnClickListener(new View.OnClickListener() {
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
                                        Toast.makeText(BackgroundRemoverActivity.this, "Just Wait A Little Longer", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.d("Called","Connection Failed");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoading();
                                        cancel_processing.setVisibility(View.GONE);
                                        buttonAnimationManager.GeneratingButtonAnimation("enable");


                                        CustomNetworkDialog dialog = new CustomNetworkDialog(
                                                BackgroundRemoverActivity.this, // Context (e.g., Activity)
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
                            BackgroundRemoverActivity.this, // Context (e.g., Activity)
                            R.drawable.no_internet, // Image resource
                            "Please Connect Your Internet ." // Dynamic message
                    );
                    dialog.show();
                }
            }
        });
        //Download All Downloading Behaviour
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter2.DownloadAllImages();
            }
        });
        downloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter2.DownloadAllImages();
            }
        });

        cancel_processing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideLoading();
                imageUploadHelper.cancelUpload();
                buttonAnimationManager.GeneratingButtonAnimation("enable");
                cancel_processing.setVisibility(View.GONE);
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
    }

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