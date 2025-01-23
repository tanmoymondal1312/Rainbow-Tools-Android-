package com.mediaghor.rainbowtools.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;

public class EnhanceImagesActivity extends AppCompatActivity {

    private ImageButton toolbarBackBtn,settingToolbarImgBtn;
    LottieAnimationView lottieAnimationSelectImages,downloadAllImagesLottie;
    TextView toolbarTitle;

    //ArrayList<Integer> beforeEnhanceImages = new ArrayList<>();
    private ArrayList<Uri>beforeEnhanceImages;
    private ArrayList<Uri>afterEnhanceImages = new ArrayList<>();



    RecyclerView recyclerViewImageEnhance;
    EnhanceImagesAdapter adapter;
    ButtonAnimationManager buttonAnimationManager;
    private ActivityResultLauncher<Intent> selectImagesLauncher;








    private void SetSelectedImagesInRecycler(){
        recyclerViewImageEnhance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EnhanceImagesAdapter(EnhanceImagesActivity.this,beforeEnhanceImages);
        recyclerViewImageEnhance.setAdapter(adapter);
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
        downloadAllImagesLottie = findViewById(R.id.lottie_anim_download_in_bottom_toolbar_bgrl);
        recyclerViewImageEnhance = findViewById(R.id.recycler_id_enhance_images);



        buttonAnimationManager = new ButtonAnimationManager(this);




        //Initial States
        buttonAnimationManager.SelectImageAnimation("loop_animation");
        buttonAnimationManager.GeneratingButtonAnimation("disable");
        buttonAnimationManager.DownloadAllImagesAnimation("disable");


        afterEnhanceImages.add(Uri.parse("https://picsum.photos/id/237/200/300"));
        afterEnhanceImages.add(Uri.parse("https://picsum.photos/seed/picsum/200/300"));
        afterEnhanceImages.add(Uri.parse("https://picsum.photos/200/300?grayscale"));


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
        settingToolbarImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.AddAfterEnhanceImagesToArray(afterEnhanceImages);
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