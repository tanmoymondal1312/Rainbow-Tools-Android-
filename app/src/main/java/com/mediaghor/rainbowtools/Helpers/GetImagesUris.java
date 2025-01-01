package com.mediaghor.rainbowtools.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class GetImagesUris {

    /**
     * Fetches all image URIs from the device in recent order.
     *
     * @param context The application context.
     * @return A list of image URIs in recent order.
     */
    public static List<Uri> getAllImagesUris(Context context) {
        // List to store the URIs of images
        List<Uri> imageUris = new ArrayList<>();

        // URI of the external content storage for images
        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Define the columns we want to retrieve
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED // For sorting by recent
        };

        // Define the sorting order (most recent first)
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Query the content resolver to fetch image details
        try (Cursor cursor = context.getContentResolver().query(
                externalContentUri, // URI to query
                projection,         // Columns to retrieve
                null,               // Selection (null for all rows)
                null,               // Selection arguments
                sortOrder           // Order by recent
        )) {
            if (cursor != null) {
                // Get the column index for the image ID
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                // Iterate through the cursor to fetch image URIs
                while (cursor.moveToNext()) {
                    // Get the image ID
                    long id = cursor.getLong(idColumn);

                    // Construct the URI for the image
                    Uri imageUri = Uri.withAppendedPath(externalContentUri, String.valueOf(id));

                    // Add the URI to the list
                    imageUris.add(imageUri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        }

        // Return the list of image URIs
        return imageUris;
    }
}
