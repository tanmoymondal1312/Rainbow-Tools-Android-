package com.mediaghor.rainbowtools.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.mediaghor.rainbowtools.Adapter.SelectedImagesAdapterEXT;
import com.mediaghor.rainbowtools.Helpers.ImagePermissionHandler;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class TextExtractorActivity extends AppCompatActivity {

    ImageButton toolbarBackBtn;
    LottieAnimationView lottieAnimationSelectImages,generateButton,downloadAllImagesLottie;
    RecyclerView selectedImagesRecycler;

    private ArrayList<Uri>ImagesFromDevice;
    private int originalWidth;


    TextView toolbarTitle;

    ButtonAnimationManager buttonAnimationManager;

    private ActivityResultLauncher<Intent> selectImagesLauncher;




    private void SetSelectedImagesInRecycler() {
        if (ImagesFromDevice.isEmpty()) {
            // Normally set everything according to the XML code
            buttonAnimationManager.SelectImageAnimation("unloop_animation");
            buttonAnimationManager.GeneratingButtonAnimation("enable");
            buttonAnimationManager.DownloadAllImagesAnimation("disable");

            originalWidth = selectedImagesRecycler.getLayoutParams().width;
            selectedImagesRecycler.setLayoutManager(new LinearLayoutManager(this));

            SelectedImagesAdapterEXT adapter = new SelectedImagesAdapterEXT(this, ImagesFromDevice);
            selectedImagesRecycler.setAdapter(adapter);

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            selectedImagesRecycler.setLayoutManager(layoutManager);
        } else {
            // Reset everything when reselecting images
            buttonAnimationManager.SelectImageAnimation("unloop_animation");
            buttonAnimationManager.GeneratingButtonAnimation("enable");
            buttonAnimationManager.DownloadAllImagesAnimation("disable");

            // Reset RecyclerView to full width
            ViewGroup.LayoutParams params = selectedImagesRecycler.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            selectedImagesRecycler.setLayoutParams(params);

            // Reset RecyclerView position
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
                marginParams.setMargins(0, marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);
                selectedImagesRecycler.setLayoutParams(marginParams);
            }

            // Set back to StaggeredGridLayout
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            selectedImagesRecycler.setLayoutManager(layoutManager);

            // Update adapter with new images
            SelectedImagesAdapterEXT adapter = new SelectedImagesAdapterEXT(this, ImagesFromDevice);
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



        buttonAnimationManager = new ButtonAnimationManager(this);

        //Set Initial Thing
        toolbarTitle.setText("Text Extractor");
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");

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
                // Set RecyclerView to Linear Layout
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
                selectedImagesRecycler.setLayoutManager(linearLayoutManager);

                // Set RecyclerView Width to 40dp
                ViewGroup.LayoutParams params = selectedImagesRecycler.getLayoutParams();
                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, v.getResources().getDisplayMetrics()); // Convert 40dp to px
                selectedImagesRecycler.setLayoutParams(params);

                // Move RecyclerView to Left Side of Parent
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
                    marginParams.setMargins(0, marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);
                    selectedImagesRecycler.setLayoutParams(marginParams);
                }
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