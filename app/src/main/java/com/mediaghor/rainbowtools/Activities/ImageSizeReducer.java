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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mediaghor.rainbowtools.Adapter.GeneratedTextItemAdapterEXT;
import com.mediaghor.rainbowtools.Adapter.ImageSizeReducerAdapter;
import com.mediaghor.rainbowtools.Adapter.SelectedImagesAdapterEXT;
import com.mediaghor.rainbowtools.Adapter.SelectedImagesAdapterForBgr;
import com.mediaghor.rainbowtools.Helpers.CheckConnection;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;
import com.mediaghor.rainbowtools.Helpers.ImageSizeUploadHelper;
import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.Models.ReduceImageUploadResponse;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomNetworkDialog;
import com.mediaghor.rainbowtools.OthersClasses.CustomToastManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSizeReducer extends AppCompatActivity {
    LottieAnimationView lottieAnimationSelectImages,generateButton,downloadAllImagesLottie,downloadingAllImagesLottie;
    ImageButton toolbarBackBtn,selectImagesFromBdy;
    TextView toolbarTitle;
    private ArrayList<Uri> ImagesFromDevice;
    RecyclerView RV_SelectedImages;
    LinearLayout homeLinearBdy;
    ImageView homeImgBdy;
    Button btnCancelProcessing;

    ImageSizeReducerAdapter adapter;
    ButtonAnimationManager buttonAnimationManager;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    private CheckConnection checkConnection;
    CustomToastManager customToastManager;
    ImageSizeUploadHelper imageSizeUploadHelper;






    private void UploadImageInServer(ArrayList<Uri> uris, ArrayList<String> sizes) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            imageSizeUploadHelper = new ImageSizeUploadHelper(this, new ImageSizeUploadHelper.ImageUploadCallback() {
                @Override
                public void onSuccess(ArrayList<String>imageNames,ArrayList<Uri> imageUrls) {
                    mainThreadHandler.post(() -> {
                        btnCancelProcessing.setVisibility(View.GONE);
                        buttonAnimationManager.ImageReducerGeneratingAnimation("stop");
                        adapter.setServerUrl(imageUrls);
                        buttonAnimationManager.GeneratingButtonAnimation("enable");
                        buttonAnimationManager.DownloadAllImagesAnimation("download");
                        customToastManager.showDownloadSuccessToast(R.drawable.icn_success,"Reduce Successful",4);

                    });
                }
                @Override
                public void onFailure(String error) {
                    mainThreadHandler.post(() -> {
                        btnCancelProcessing.setVisibility(View.GONE);
                        buttonAnimationManager.GeneratingButtonAnimation("enable");
                        buttonAnimationManager.ImageReducerGeneratingAnimation("stop");
                    });
                }
            });

            // Call upload function from helper
            imageSizeUploadHelper.uploadImages(uris, sizes);
        });
    }



    private void SetSelectedImagesInRecycler(){
        buttonAnimationManager.DownloadAllImagesAnimation("disable");
        homeImgBdy.setVisibility(View.VISIBLE);
        homeLinearBdy.setVisibility(View.GONE);
        buttonAnimationManager.SelectImageAnimation("unloop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("enable");
        adapter = new ImageSizeReducerAdapter(this, ImagesFromDevice, new ImageSizeReducerAdapter.OnImageListEmptyListener() {
            @Override
            public void onImageListEmpty() {
                RV_SelectedImages.setVisibility(View.GONE);
                homeImgBdy.setVisibility(View.GONE);
                homeLinearBdy.setVisibility(View.VISIBLE);
            }
        },RV_SelectedImages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RV_SelectedImages.setLayoutManager(layoutManager);
        RV_SelectedImages.setAdapter(adapter);
    }



    private void GetImagesFromDevices(){
        Log.d("GIFD","Get Images from device called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 (API 33) and above
            // Check if READ_MEDIA_IMAGES permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for images
                ImagePermissionHandler.requestImagePermission(ImageSizeReducer.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(ImageSizeReducer.this, AllimagesActivity.class);
                selectImagesLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8 to Android 12 (API 26-32)
            // Check if READ_EXTERNAL_STORAGE permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for external storage
                ImagePermissionHandler.requestImagePermission(ImageSizeReducer.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(ImageSizeReducer.this, AllimagesActivity.class);
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_size_reducer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.darker_gray));

        toolbarBackBtn = findViewById(R.id.toolbar_back_btn);
        toolbarTitle = findViewById(R.id.toolbar_in_tools_title_id);
        lottieAnimationSelectImages = findViewById(R.id.animation_select_image_id_abgr);
        generateButton = findViewById(R.id.generating_animation_id_bg_remover_layout);
        downloadAllImagesLottie = findViewById(R.id.lottie_anim_download_in_bottom_toolbar_bgrl);
        downloadingAllImagesLottie = findViewById(R.id.lottie_anim_downloading_in_bottom_toolbar_bgrl);
        btnCancelProcessing = findViewById(R.id.btn_cancel_processing_rbg_l);
        homeImgBdy = findViewById(R.id.neon_bg_2_reduce_img_lay);
        homeLinearBdy = findViewById(R.id.linear_home_rszimg);
        selectImagesFromBdy = findViewById(R.id.select_img_btn_in_bdy_enhance_img_layout);
        RV_SelectedImages = findViewById(R.id.recycler_view_id_reduce_img_size);
        buttonAnimationManager = new ButtonAnimationManager(this);
        checkConnection = new CheckConnection(this);
        customToastManager = new CustomToastManager(this);
        //Set Initial Thing
        homeImgBdy.setVisibility(View.GONE);
        homeLinearBdy.setVisibility(View.VISIBLE);
        toolbarTitle.setText("Size Reducer");
        btnCancelProcessing.setVisibility(View.GONE);
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");
        buttonAnimationManager.ImageReducerGeneratingAnimation("stop");

        //Onclick On Toolbar Back Button
        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectImagesFromBdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImagesFromDevices();
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
                if(adapter.getAllSizes() == null){
                    Log.d("WOWWWWW","problem In list");
                }else{
                    if (checkConnection.isInternetConnected()) {
                        // Start animations
                        buttonAnimationManager.ImageReducerGeneratingAnimation("start");
                        buttonAnimationManager.GeneratingButtonAnimation("animated");
                        btnCancelProcessing.setVisibility(View.VISIBLE);

                        // Upload image to server
                        UploadImageInServer(adapter.getFinalImageUri(), adapter.getAllSizes());
                        adapter.setUploadingStates(true);

                        checkConnection.checkServerConnection(new CheckConnection.ServerConnectionListener() {
                            @Override
                            public void onConnectionChecked(boolean isConnected) {
                                if (!isConnected) {
                                    Log.d("Called", "Connection Failed");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stop animations & show error
                                            buttonAnimationManager.ImageReducerGeneratingAnimation("stop");
                                            buttonAnimationManager.GeneratingButtonAnimation("enable");
                                            btnCancelProcessing.setVisibility(View.GONE);
                                            adapter.setUploadingStates(true);
                                            imageSizeUploadHelper.cancelUpload();

                                            CustomNetworkDialog dialog = new CustomNetworkDialog(
                                                    ImageSizeReducer.this,
                                                    R.drawable.server_error_2,
                                                    "Server Connection Failed, Please Try Again!"
                                            );
                                            dialog.show();
                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        // **Fix: Stop UI animations when there’s no internet**
                        buttonAnimationManager.ImageReducerGeneratingAnimation("stop");
                        buttonAnimationManager.GeneratingButtonAnimation("enable");
                        btnCancelProcessing.setVisibility(View.GONE);
                        adapter.setUploadingStates(true);

                        // Show "No Internet" dialog
                        CustomNetworkDialog dialog = new CustomNetworkDialog(
                                ImageSizeReducer.this,
                                R.drawable.no_internet,
                                "Please Connect Your Internet."
                        );
                        dialog.show();
                    }



                }
            }
        });
        btnCancelProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSizeUploadHelper.cancelUpload();
                adapter.setUploadingStates(false);
                buttonAnimationManager.GeneratingButtonAnimation("enable");
                buttonAnimationManager.ImageReducerGeneratingAnimation("stop");
                btnCancelProcessing.setVisibility(View.GONE);

            }
        });
        downloadAllImagesLottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.DownloadAllImages();
            }
        });
        downloadingAllImagesLottie.setOnClickListener(new View.OnClickListener() {
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
                        ImagesFromDevice = result.getData()
                                .getParcelableArrayListExtra(AllimagesActivity.SELECTED_IMAGES_KEY);

                        //afterEnhanceImages = new ArrayList<>(beforeEnhanceImages);

                        if (ImagesFromDevice != null && !ImagesFromDevice.isEmpty()) {
                            SetSelectedImagesInRecycler();
                            //onNewImagesSelected(ImagesFromDevice);
                        }
                        else {

                        }
                    }
                }
        );



    }
}