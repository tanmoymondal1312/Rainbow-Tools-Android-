package com.mediaghor.rainbowtools.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.mediaghor.rainbowtools.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BackgroundRemovedImageAdapter extends RecyclerView.Adapter<BackgroundRemovedImageAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Uri> imageUrls;
    private final Map<Uri, File> loadedImagesCache = new HashMap<>();

    public BackgroundRemovedImageAdapter(Context context, ArrayList<Uri> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_for_bg_removed_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUrl = imageUrls.get(position);

        // Show the ProgressBar initially
        holder.progressBar.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(imageUrl.toString())                 // Load the image URL
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache images for faster reloads
                .placeholder(R.drawable.emtyimgbgrmv)     // Placeholder while loading
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            Log.e("GlideError", "Image load failed", e);
                        }
                        holder.progressBar.setVisibility(View.GONE);
                        holder.imageView.setImageResource(R.drawable.server_error);
                        holder.singleImageDownload.setVisibility(View.GONE);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.singleImageDownload.setVisibility(View.VISIBLE);

                        // Cache the image as a File
                        cacheLoadedImage(imageUrl);
                        return false;
                    }
                })
                .into(holder.imageView);

        holder.singleImageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadedImagesCache.containsKey(imageUrl)) {
                    // Download from the cached file
                    saveFileToDownloads(loadedImagesCache.get(imageUrl));
                    Log.d("Download", "Downloaded from cache: " + imageUrl);
                } else {
                    // Download from Glide
                    downloadLoadedImage(imageUrl);
                    Log.d("Download", "Downloaded from URL: " + imageUrl);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
        AppCompatButton singleImageDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_bg_removed_image);
            progressBar = itemView.findViewById(R.id.progressbar_bg_removed_image_bg_remover_layout);
            singleImageDownload = itemView.findViewById(R.id.btn_single_img_download_layout_bg_remover);
        }
    }

    /**
     * Cache a loaded image as a File
     *
     * @param imageUrl The URL of the loaded image
     */
    private void cacheLoadedImage(Uri imageUrl) {
        Glide.with(context)
                .asFile()
                .load(imageUrl)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        loadedImagesCache.put(imageUrl, resource);
                        Log.d("Cache", "Image cached: " + imageUrl.toString());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d("Cache", "Cache cleared for: " + imageUrl.toString());
                    }
                });
    }

    /**
     * Download a loaded image directly
     *
     * @param imageUrl The URL of the image to download
     */
    private void downloadLoadedImage(Uri imageUrl) {
        Glide.with(context)
                .asFile()
                .load(imageUrl)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        saveFileToDownloads(resource);
                        Log.d("Download", "File downloaded successfully from URL: " + imageUrl.toString());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d("Download", "Download target cleared.");
                    }
                });
    }

    /**
     * Save a cached file to the Downloads directory
     *
     * @param sourceFile The file retrieved from Glide's cache
     */
    private void saveFileToDownloads(File sourceFile) {
        try {
            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // Create a destination file with a unique name
            File destFile = new File(downloadsDir, "Rainbow_image_" + System.currentTimeMillis() + ".png");

            // Copy the file from the cache to the Downloads directory
            try (InputStream inputStream = new FileInputStream(sourceFile);
                 OutputStream outputStream = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            // Notify the media scanner to index the new file
            scanFileForGallery(destFile);

            // Log the file's location for debugging
            Log.d("Download", "File saved to " + destFile.getAbsolutePath());
        } catch (IOException e) {
            // Handle any errors during the file save process
            Log.e("Download", "Failed to save file", e);
        }
    }
    private void scanFileForGallery(File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> Log.d("MediaScanner", "Scanned " + path + " -> URI: " + uri));
    }

}
