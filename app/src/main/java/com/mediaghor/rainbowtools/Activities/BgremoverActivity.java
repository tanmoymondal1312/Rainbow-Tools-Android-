package com.mediaghor.rainbowtools.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mediaghor.rainbowtools.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BgremoverActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 1;

    private ImageView imageView;
    private Button uploadButton;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bgremover);

        // Initialize views
        imageView = findViewById(R.id.empty_img_id);
        uploadButton = findViewById(R.id.btn_generate_image);
        progressBar = findViewById(R.id.progress_bar_id);

        // Set image click listener
        imageView.setOnClickListener(v -> {
            // Check permissions
            requestStoragePermission();
        });
    }

    private void requestStoragePermission() {
        Dexter.withContext(this)
                .withPermission(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openImagePicker();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // Permission permanently denied, show settings dialog
                            showSettingsDialog();
                        } else {
                            // Permission denied without "Don't ask again"
                            Toast.makeText(BgremoverActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(BgremoverActivity.this, "Permission rationale needed", Toast.LENGTH_SHORT).show();
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                encodeImage(bitmap);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Toast.makeText(this, "Image encoded successfully", Toast.LENGTH_SHORT).show();
    }

    private void showSettingsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("You have permanently denied this permission. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
