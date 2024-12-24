package com.mediaghor.rainbowtools.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mediaghor.rainbowtools.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class BackgroundRemovedImageAdapter extends RecyclerView.Adapter<BackgroundRemovedImageAdapter.ViewHolder> {
    private final List<Uri> imageUrls;
    private final Context context; // For accessing resources and storage

    public BackgroundRemovedImageAdapter(List<Uri> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public BackgroundRemovedImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_for_bg_removed_images, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BackgroundRemovedImageAdapter.ViewHolder holder, int position) {
        Uri imageUrl = imageUrls.get(position);

        // Show loading animation
        holder.progressBar.setVisibility(View.VISIBLE);

        // Load the image
        Glide.with(holder.imv_listMedia.getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar when loaded

                        // Calculate height based on a fixed width
                        int fixedWidth = holder.imv_listMedia.getWidth();
                        int dynamicHeight = (int) ((float) resource.getHeight() / resource.getWidth() * fixedWidth);

                        // Set the calculated dimensions
                        ViewGroup.LayoutParams layoutParams = holder.imv_listMedia.getLayoutParams();
                        layoutParams.height = dynamicHeight;
                        holder.imv_listMedia.setLayoutParams(layoutParams);

                        // Set the image into the ImageView
                        holder.imv_listMedia.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        holder.progressBar.setVisibility(View.GONE); // Ensure progress bar hides
                    }
                });

        // Handle image download on button click
        holder.btn.setOnClickListener(v -> downloadImage(imageUrl, holder));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imv_listMedia;
        ProgressBar progressBar;
        AppCompatButton btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_listMedia = itemView.findViewById(R.id.rv_bg_removed_image);
            progressBar = itemView.findViewById(R.id.image_loading_progress); // Add a ProgressBar in your layout
            btn = itemView.findViewById(R.id.btn_download);
        }
    }

    // Method to download the image
    private void downloadImage(Uri imageUrl, ViewHolder holder) {
        Glide.with(holder.imv_listMedia.getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            // Save the image to local storage
                            String fileName = "Image_" + System.currentTimeMillis() + ".png";
                            OutputStream outputStream;

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppImages");

                                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                                if (uri != null) {
                                    outputStream = context.getContentResolver().openOutputStream(uri);
                                } else {
                                    Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
                                if (!dir.exists()) dir.mkdirs();
                                File file = new File(dir, fileName);
                                outputStream = new FileOutputStream(file);
                            }

                            resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();

                            Toast.makeText(context, "Image downloaded to Pictures/MyAppImages", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(context, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder logic if needed
                    }
                });
    }
}
