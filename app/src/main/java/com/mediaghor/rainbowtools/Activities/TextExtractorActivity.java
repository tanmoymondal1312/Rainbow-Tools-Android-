package com.mediaghor.rainbowtools.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.Adapter.CardItemAdapter;
import com.mediaghor.rainbowtools.Adapter.EnhanceImagesAdapter;
import com.mediaghor.rainbowtools.Adapter.GeneratedTextItemAdapterEXT;
import com.mediaghor.rainbowtools.Adapter.SelectedImagesAdapterEXT;
import com.mediaghor.rainbowtools.Helpers.CheckConnection;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;
import com.mediaghor.rainbowtools.Helpers.ImageUploadHelper;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomNetworkDialog;
import com.mediaghor.rainbowtools.OthersClasses.CustomToastManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextExtractorActivity extends AppCompatActivity {

    ImageButton toolbarBackBtn,selectImagesFromBdy;
    LottieAnimationView lottieAnimationSelectImages,generateButton,downloadAllImagesLottie,downloadingAllImagesLottie;
    RecyclerView selectedImagesRecycler,generatedTextRecycler;
    Button btnCancelProcessing;
    ImageView imgHomeBg;

    private ArrayList<Uri>ImagesFromDevice;
    private int originalWidth;
    public String serverIp;

    private ArrayList<Uri> TextsFilesUris = new ArrayList<>();
    public ArrayList<Uri> finalImageUri = new ArrayList<>();

    LinearLayout linearBdy,LinearRecuclerViewBdyctrlBg,bdyDownloadItems;
    TextView toolbarTitle;

    ButtonAnimationManager buttonAnimationManager;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    SelectedImagesAdapterEXT adapter;
    GeneratedTextItemAdapterEXT generatedTextItemAdapterEXT;
    private CheckConnection checkConnection;
    ImageUploadHelper imageUploadHelper;
    CustomToastManager customToastManager;



    private void setChildClickListeners(LinearLayout parentLayout) {
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            child.setOnClickListener(commonClickListener);
        }
    }

    private final View.OnClickListener commonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message;

            if (v.getId() == R.id.child1_download_as_txt) {
                generatedTextItemAdapterEXT.DownloadsAll("txt");
                message = "Downloaded all as txt";
            } else if (v.getId() == R.id.child2_download_as_pdf) {
                generatedTextItemAdapterEXT.DownloadsAll("pdf");
                message = "Downloaded all as pdf";
            } else if (v.getId() == R.id.child3_download_as_docx) {
                generatedTextItemAdapterEXT.DownloadsAll("docx");
                message = "Downloaded all as docx";
            } else {
                message = "Other Child Clicked";
            }
            buttonAnimationManager.DownloadAllImagesAnimation("downloading");
            animateSlideOut(bdyDownloadItems);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                customToastManager.showDownloadSuccessToast(R.drawable.download_success, message, 2);
            }, 2500);


        }
    };





    public void animateSlideIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = view.getMeasuredHeight(); // Get the actual height

        view.setTranslationY(height); // Start from below
        view.animate().translationY(0).setDuration(300).start(); // Animate upwards
    }

    public void animateSlideOut(View view) {
        int height = view.getHeight(); // Get the current height

        view.animate().translationY(height).setDuration(300)
                .withEndAction(() -> {
                    view.setVisibility(View.GONE);
                    view.setEnabled(false);
                }).start();
    }




    public void startExtractingUiComponents() {
        linearBdy.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_gray));

        if (buttonAnimationManager != null) {
            buttonAnimationManager.GeneratingButtonAnimation("animated");
            buttonAnimationManager.ExtractingAnim("play");
        }

        btnCancelProcessing.setVisibility(View.VISIBLE);
        btnCancelProcessing.setEnabled(true);
    }


    public void disabledExtractingUiComponents(){
        linearBdy.setBackground(null);
        if (buttonAnimationManager != null) {
            buttonAnimationManager.GeneratingButtonAnimation("disable");
            buttonAnimationManager.ExtractingAnim("stop");
        }

        btnCancelProcessing.setVisibility(View.GONE);
    }



    private void uploadImagesToBackend(ArrayList<Uri> uris) {
        ExecutorService executor = Executors.newSingleThreadExecutor(); // Use a single background thread for processing
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()); // Handler for communicating with the UI thread

        executor.execute(() -> {
            try {
                imageUploadHelper = new ImageUploadHelper(this,3);
                imageUploadHelper.uploadImages(uris, new ImageUploadHelper.ImageUploadCallback() {
                    @Override
                    public void onImageUploadSuccess(ArrayList<Uri> imageUrls) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            customToastManager.showDownloadSuccessToast(R.drawable.icn_success,"Extract Successful",2);

                            disabledExtractingUiComponents();
                            TextExtractorActivity.this.TextsFilesUris = imageUrls;
                            SetUpGeneratedTextRecycler();
                            Log.d("TXT","Response Accepted");
                            Log.d("TXT",TextsFilesUris.toString());

                        });
                    }

                    @Override
                    public void onImageUploadFailure(String errorMessage) {
                        // Switch back to the UI thread for UI updates
                        mainThreadHandler.post(() -> {
                            linearBdy.setBackground(null);
                            if (buttonAnimationManager != null) {
                                buttonAnimationManager.ExtractingAnim("stop");
                                buttonAnimationManager.GeneratingButtonAnimation("enable");
                                customToastManager.showDownloadSuccessToast(R.drawable.baseline_error_outline_24,"Something Wrong",2);
                                adapter.setCardsColorBlur(false);
                            }

                            btnCancelProcessing.setVisibility(View.GONE);
                            Log.d("TXT","POst Failed");
                        });
                    }
                });
            } catch (Exception e) {
                // Handle unexpected exceptions
                mainThreadHandler.post(() ->
                        Toast.makeText(TextExtractorActivity.this, "Unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } finally {
                executor.shutdown(); // Release resources
            }
        });
    }








    private void SetUpImagesLinearly(){
        // Set RecyclerView to Linear Layout

        adapter.setUploadingState(true);
        adapter.setCardsColorBlur(false);

        LinearLayoutManager linearLayoutManager__2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        selectedImagesRecycler.setLayoutManager(linearLayoutManager__2);
        // Set RecyclerView Width to 40dp
        ViewGroup.LayoutParams params = selectedImagesRecycler.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()); // Convert 40dp to px
        selectedImagesRecycler.setLayoutParams(params);
        // Move RecyclerView to Left Side of Parent
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
            marginParams.setMargins(0, marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);
            selectedImagesRecycler.setLayoutParams(marginParams);
        }

    }



    private void SetUpGeneratedTextRecycler(){
        SetUpImagesLinearly();
        buttonAnimationManager.DownloadAllImagesAnimation("download");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        generatedTextRecycler.setLayoutManager(linearLayoutManager);


        generatedTextRecycler.setHasFixedSize(true);
        generatedTextRecycler.setItemViewCacheSize(5);
        generatedTextRecycler.setDrawingCacheEnabled(true);
        generatedTextRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        generatedTextItemAdapterEXT = new GeneratedTextItemAdapterEXT(this,TextsFilesUris,finalImageUri);
        generatedTextRecycler.setAdapter(generatedTextItemAdapterEXT);

        ViewGroup.LayoutParams params__2 = generatedTextRecycler.getLayoutParams();
        params__2.width = ViewGroup.LayoutParams.MATCH_PARENT;
        generatedTextRecycler.setLayoutParams(params__2);





    }
    private void SetSelectedImagesInRecycler() {
        bdyDownloadItems.setVisibility(View.GONE);
        LinearRecuclerViewBdyctrlBg.setVisibility(View.GONE);
        imgHomeBg.setVisibility(View.VISIBLE);
        if (ImagesFromDevice.isEmpty()) {
            // Normally set everything according to XML
            buttonAnimationManager.SelectImageAnimation("unloop_animation");
            buttonAnimationManager.GeneratingButtonAnimation("enable");
            buttonAnimationManager.DownloadAllImagesAnimation("disable");

            originalWidth = selectedImagesRecycler.getLayoutParams().width;
            selectedImagesRecycler.setLayoutManager(new LinearLayoutManager(this));

            adapter = new SelectedImagesAdapterEXT(this, ImagesFromDevice);
            selectedImagesRecycler.setAdapter(adapter);

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            selectedImagesRecycler.setLayoutManager(layoutManager);
        } else {
            // Reset everything when reselecting images
            buttonAnimationManager.SelectImageAnimation("unloop_animation");
            buttonAnimationManager.GeneratingButtonAnimation("enable");
            buttonAnimationManager.DownloadAllImagesAnimation("disable");

            // Reset RecyclerView position
            ViewGroup.LayoutParams params = selectedImagesRecycler.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            selectedImagesRecycler.setLayoutParams(params);

            if (params instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
                marginParams.setMargins(0, marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);
                selectedImagesRecycler.setLayoutParams(marginParams);
            }

            // Set back to StaggeredGridLayout
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            selectedImagesRecycler.setLayoutManager(layoutManager);

            // Update adapter with new images
            adapter = new SelectedImagesAdapterEXT(this, ImagesFromDevice);
            selectedImagesRecycler.setAdapter(adapter);
        }
    }


    private void GetImagesFromDevices(){
        Log.d("GIFD","Get Images from device called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 (API 33) and above
            // Check if READ_MEDIA_IMAGES permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for images
                ImagePermissionHandler.requestImagePermission(TextExtractorActivity.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(TextExtractorActivity.this, AllimagesActivity.class);
                selectImagesLauncher.launch(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8 to Android 12 (API 26-32)
            // Check if READ_EXTERNAL_STORAGE permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permission for external storage
                ImagePermissionHandler.requestImagePermission(TextExtractorActivity.this);
            } else {
                // Permission already granted, proceed with accessing images
                Intent intent = new Intent(TextExtractorActivity.this, AllimagesActivity.class);
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
        setContentView(R.layout.activity_text_extractor);
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
        selectedImagesRecycler = findViewById(R.id.recycler_id_selected_images_text_extractor);
        generatedTextRecycler = findViewById(R.id.generatedTextRecycler);
        linearBdy = findViewById(R.id.l_layout_body_in_ext_layout);
        LinearRecuclerViewBdyctrlBg = findViewById(R.id.linear_lay_ext_lay_home_bdy);
        imgHomeBg = findViewById(R.id.item_neon_bg_ext_lay);
        btnCancelProcessing = findViewById(R.id.btn_cancel_processing_rbg_l);
        selectImagesFromBdy = findViewById(R.id.select_img_from_bdt_txt_ext_lay);
        bdyDownloadItems = findViewById(R.id.download_txt_item_btn_ext_rec_item);

        buttonAnimationManager = new ButtonAnimationManager(this);
        checkConnection = new CheckConnection(this);
        customToastManager = new CustomToastManager(this);

        //Set Initial Thing
        toolbarTitle.setText("Text Extractor");
        bdyDownloadItems.setVisibility(View.GONE);
        LinearRecuclerViewBdyctrlBg.setVisibility(View.VISIBLE);
        LinearRecuclerViewBdyctrlBg.setEnabled(true);
        selectImagesFromBdy.setEnabled(true);
        imgHomeBg.setVisibility(View.GONE);

        btnCancelProcessing.setVisibility(View.GONE);
        buttonAnimationManager.ExtractingAnim("stop");
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");
        serverIp = this.getResources().getString(R.string.serverIp);

        //Onclick On Toolbar Back Button
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
        selectImagesFromBdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImagesFromDevices();
            }
        });
        downloadAllImagesLottie.setOnClickListener(new View.OnClickListener() {
            boolean isVisible = false; // Track visibility state

            @Override
            public void onClick(View v) {
                if (isVisible) {
                    // Animate from visible to gone (top to bottom)
                    animateSlideOut(bdyDownloadItems);
                } else {
                    // Animate from gone to visible (bottom to top)
                    bdyDownloadItems.post(() -> animateSlideIn(bdyDownloadItems));
                    setChildClickListeners(bdyDownloadItems);

                }
                isVisible = !isVisible; // Toggle state
            }
        });
        downloadingAllImagesLottie.setOnClickListener(new View.OnClickListener() {
            boolean isVisible = false;
            @Override
            public void onClick(View v) {
                if (isVisible) {
                    // Animate from visible to gone (top to bottom)
                    animateSlideOut(bdyDownloadItems);
                } else {
                    // Animate from gone to visible (bottom to top)
                    bdyDownloadItems.post(() -> animateSlideIn(bdyDownloadItems));
                    setChildClickListeners(bdyDownloadItems);

                }
                isVisible = !isVisible; // Toggle state
            }
        });





        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalImageUri = adapter.getFinalSelectedUris();
                bdyDownloadItems.setVisibility(View.GONE);

                if(checkConnection.isInternetConnected()){
                    startExtractingUiComponents();
                    uploadImagesToBackend(finalImageUri);
                    adapter.setUploadingState(true);
                    adapter.setCardsColorBlur(true);
                    checkConnection.checkServerConnection(new CheckConnection.ServerConnectionListener() {
                        @Override
                        public void onConnectionChecked(boolean isConnected) {
                            if(isConnected){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TextExtractorActivity.this, "Just Wait A Little Longer", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setUploadingState(false);
                                        linearBdy.setBackground(null);
                                        buttonAnimationManager.GeneratingButtonAnimation("enable");
                                        if (buttonAnimationManager != null) {
                                            buttonAnimationManager.ExtractingAnim("stop");
                                        }

                                        btnCancelProcessing.setVisibility(View.GONE);
                                        imageUploadHelper.cancelUpload();
                                        CustomNetworkDialog dialog = new CustomNetworkDialog(
                                                TextExtractorActivity.this, // Context (e.g., Activity)
                                                R.drawable.server_error_2, // Image resource
                                                "Server Connection Failed, Please Try Again !" // Dynamic message
                                        );
                                        dialog.show();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapter.setUploadingState(false);
                            linearBdy.setBackground(null);
                            buttonAnimationManager.GeneratingButtonAnimation("enable");
                            if (buttonAnimationManager != null) {
                                buttonAnimationManager.ExtractingAnim("stop");
                            }

                            btnCancelProcessing.setVisibility(View.GONE);
                            CustomNetworkDialog dialog = new CustomNetworkDialog(
                                    TextExtractorActivity.this, // Context (e.g., Activity)
                                    R.drawable.no_internet, // Image resource
                                    "Please Connect Your Internet ." // Dynamic message
                            );
                            dialog.show();
                        }
                    });
                }

            }
        });
        btnCancelProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customToastManager.showDownloadSuccessToast(R.drawable.baseline_cancel_24,"Processing Cancelled",3);
                adapter.setUploadingState(false);
                disabledExtractingUiComponents();
                imageUploadHelper.cancelUpload();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 121 && resultCode == RESULT_OK) {
            String editedText = data.getStringExtra("editedText");
            int position = data.getIntExtra("position",-1);

            // Update UI with the returned text
            generatedTextItemAdapterEXT.updateText(position,editedText);
            Log.d("RE_EXT", "Updated text at position " + position + ": " + editedText);
            customToastManager.showDownloadSuccessToast(R.drawable.icn_success,"Edit Successful",2);

        }
    }

}