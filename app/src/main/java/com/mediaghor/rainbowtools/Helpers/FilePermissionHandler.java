package com.mediaghor.rainbowtools.Helpers;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class FilePermissionHandler {

    public interface PermissionCallback {
        void onPermissionGranted();
    }

    public static void requestFilePermission(Context context, PermissionCallback callback) {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = android.Manifest.permission.READ_MEDIA_IMAGES;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        } else {
            permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }

        Dexter.withContext(context).withPermission(permission)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(context, "Permission Granted for File Access!", Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showPermissionSettingsDialog(context);
                        } else {
                            Toast.makeText(context, "Permission Denied for File Access", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Toast.makeText(context, "You need to grant permission to access files.", Toast.LENGTH_LONG).show();
                        token.continuePermissionRequest();
                    }
                })
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
