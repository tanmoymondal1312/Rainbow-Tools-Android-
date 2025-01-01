package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class ImagePermissionHandler {

    public static void requestImagePermission(Context context) {
        // Determine the permission based on the Android version
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            permission = android.Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            // Android 8 to 12
            permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        // Request the appropriate permission
        Dexter.withContext(context).withPermission(permission)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(context, "Permission Granted for Images!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showPermissionSettingsDialog(context);
                        } else {
                            Toast.makeText(context, "Permission Denied for Images", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(context, "You need to grant permission to access images.", Toast.LENGTH_LONG).show();
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> Toast.makeText(context, "Error occurred: " + error.toString(), Toast.LENGTH_SHORT).show())
                .check();
    }

    private static void showPermissionSettingsDialog(Context context) {
        Toast.makeText(context, "Permission Denied Permanently! Go to settings to enable.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
