package com.mediaghor.rainbowtools.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextExtractorActivity extends AppCompatActivity {

    ImageButton toolbarBackBtn;
    LottieAnimationView lottieAnimationSelectImages,generateButton,downloadAllImagesLottie;
    RecyclerView selectedImagesRecycler,generatedTextRecycler;

    private ArrayList<Uri>ImagesFromDevice;
    private int originalWidth;
    public String serverIp;

    private ArrayList<Uri> TextsFilesUris = new ArrayList<>();


    TextView toolbarTitle;

    ButtonAnimationManager buttonAnimationManager;
    private ActivityResultLauncher<Intent> selectImagesLauncher;
    SelectedImagesAdapterEXT adapter;
    GeneratedTextItemAdapterEXT generatedTextItemAdapterEXT;
    private CheckConnection checkConnection;
    ImageUploadHelper imageUploadHelper;




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
                            //Hande If cancel or connection failed
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

//        TextsFilesUris.add(Uri.parse("http://"+serverIp+":8000/image-optimization/get_extracted_texts/20250215180328_cdf237ef.txt"));
//        TextsFilesUris.add(Uri.parse("http://"+serverIp+":8000/image-optimization/get_extracted_texts/20250216152152_75429cab.txt"));




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        generatedTextRecycler.setLayoutManager(linearLayoutManager);


        generatedTextRecycler.setHasFixedSize(true);
        generatedTextRecycler.setItemViewCacheSize(5);
        generatedTextRecycler.setDrawingCacheEnabled(true);
        generatedTextRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        generatedTextItemAdapterEXT = new GeneratedTextItemAdapterEXT(this,TextsFilesUris);
        generatedTextRecycler.setAdapter(generatedTextItemAdapterEXT);

        ViewGroup.LayoutParams params__2 = generatedTextRecycler.getLayoutParams();
        params__2.width = ViewGroup.LayoutParams.MATCH_PARENT;
        generatedTextRecycler.setLayoutParams(params__2);





    }
    private void SetSelectedImagesInRecycler() {

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
        selectedImagesRecycler = findViewById(R.id.recycler_id_selected_images_text_extractor);
        generatedTextRecycler = findViewById(R.id.generatedTextRecycler);

        buttonAnimationManager = new ButtonAnimationManager(this);

        //Set Initial Thing
        toolbarTitle.setText("Text Extractor");
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


        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Uri> S = adapter.getFinalSelectedUris();
                //SetUpGeneratedTextRecycler();
                 uploadImagesToBackend(S);
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
            Toast.makeText(this, "Updated Text: " + position +editedText, Toast.LENGTH_SHORT).show();
        }
    }

}