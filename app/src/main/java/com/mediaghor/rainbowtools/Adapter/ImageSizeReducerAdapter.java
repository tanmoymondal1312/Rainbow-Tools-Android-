package com.mediaghor.rainbowtools.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomToastManager;
import com.mediaghor.rainbowtools.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageSizeReducerAdapter extends RecyclerView.Adapter<ImageSizeReducerAdapter.ViewHolder>{

    private final ArrayList<Uri> selectedUris;
    private ArrayList<String> Sizes = new ArrayList<>();
    private ArrayList<Uri> serverUrl = new ArrayList<>();


    private final Context context;
    ButtonAnimationManager buttonAnimationManager;
    private boolean isUploading;
    private ImageSizeReducerAdapter.OnImageListEmptyListener imageListEmptyListener;
    CustomToastManager customToastManager;
    RecyclerView recyclerView;



    public ImageSizeReducerAdapter(Context context, ArrayList<Uri> selectedUris, ImageSizeReducerAdapter.OnImageListEmptyListener listener, RecyclerView recyclerView) {
        this.context = context;
        this.selectedUris = selectedUris;
        buttonAnimationManager = new ButtonAnimationManager(context);
        this.imageListEmptyListener = listener;
        this.recyclerView = recyclerView;  // Assign RecyclerView reference
        customToastManager = new CustomToastManager(context);

        for (int i = 0; i < this.selectedUris.size(); i++) {
            Sizes.add(String.valueOf(getRecommendedSize(selectedUris.get(i))));
        }


    }

    @NonNull
    @Override
    public ImageSizeReducerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_reduce_img_size_item_layout, parent, false);
        return new ImageSizeReducerAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ImageSizeReducerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.loadingImagesLottie.setVisibility(View.GONE); // Hide progress when loaded
        holder.disabledTxtLine.setVisibility(View.GONE);
        holder.newSize.setVisibility(View.GONE);
        holder.downloadBtn.setVisibility(View.GONE);


        // Load the image using Glide for efficiency
        try {
            // Ensure position is valid
            if (position < 0 || position >= selectedUris.size()) {
                Log.e("Adapter", "Invalid position: " + position + ", selectedUris size: " + selectedUris.size());
                return;
            }

            Uri uri = selectedUris.get(position);
            Log.d("Adapter", "Loading image from URI: " + uri.toString());
            if (serverUrl == null || serverUrl.isEmpty() || position >= serverUrl.size()) {
                // Load local image
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.placeholder_image) // Optional: Set a placeholder image
                        .into(holder.imageView);
                new Thread(() -> {
                    int recommendedSize = getRecommendedSize(uri);
                    String actualImageSize = "Size " + getImageSizeFormatted(uri);
                    String actualWidth = "Width:"+getImageWidth(uri)+" PX";
                    String actualHeight = "Height:"+getImageHeight(uri)+" PX";
                    ((Activity) context).runOnUiThread(() -> {
                        holder.isProgrammitically = true;
                        holder.SizeInKb.setText(String.valueOf(recommendedSize));
                        holder.isProgrammitically = false;
                        holder.actualImgSize.setText(actualImageSize);
                        holder.actualWidth.setText(actualWidth);
                        holder.actualHeight.setText(actualHeight);
                    });
                }).start();
                holder.SizeInKb.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().isEmpty()) {
                            try {
                                int newSize = Integer.parseInt(s.toString().replaceFirst("^0+(?!$)", ""));
                                Sizes.set(position, String.valueOf(newSize)); // Update Sizes list
                                if(holder.isProgrammitically){
                                    return;
                                }
                                holder.RecommendTxt.setText("User Define");
                                holder.RecommendTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                            } catch (NumberFormatException e) {
                                e.printStackTrace(); // Handle invalid input gracefully
                            }
                        }else {
                            Sizes.set(position, "0");
                            holder.RecommendTxt.setText("Size Required");
                            holder.RecommendTxt.setTextColor(ContextCompat.getColor(context, R.color.hard_red));
                        }
                    }
                });





            } else {
                // Load from server with progress
                holder.loadingImagesLottie.setVisibility(View.VISIBLE);
                Log.d("Adapter", "Loading image from server URL: " + serverUrl.get(position));

                Glide.with(context)
                        .load(serverUrl.get(position))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.loadingImagesLottie.setVisibility(View.GONE); // Hide progress if failed
                                Log.e("Glide", "Image load failed for position: " + position, e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.loadingImagesLottie.setVisibility(View.GONE); // Hide progress when loaded

                                new Thread(() -> {
                                    long size = getFileSizeFromUrl(String.valueOf(serverUrl.get(position)));
                                    ((Activity) context).runOnUiThread(() -> {
                                        holder.imgData.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                                        holder.RecommendTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                                        holder.disabledTxtLine.setVisibility(View.VISIBLE);
                                        holder.newSize.setVisibility(View.VISIBLE);
                                        holder.newSize.setText("Size: " + size + " KB");
                                        holder.deleteBtn.setVisibility(View.GONE);
                                        holder.downloadBtn.setVisibility(View.VISIBLE);
                                        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                downloadLoadedImage(serverUrl.get(position));
                                                customToastManager.showDownloadSuccessToast(R.drawable.download_success,"Download Successful",3);
                                            }
                                        });
                                    });
                                }).start();

                                Log.d("Glide", "Image successfully loaded for position: " + position);
                                return false;
                            }
                        })
                        .into(holder.imageView);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Adapter", "IndexOutOfBoundsException at position: " + position, e);
        } catch (Exception e) {
            Log.e("Adapter", "Unexpected error in onBindViewHolder at position: " + position, e);
        }

        if(isUploading){
            holder.deleteBtn.setVisibility(View.GONE);
        }else {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }
        holder.deleteBtn.setOnClickListener(v -> {
            selectedUris.remove(position);
            if(selectedUris.size() == 0){
                imageListEmptyListener.onImageListEmpty();
                buttonAnimationManager.SelectImageAnimation("loop_animation");
                buttonAnimationManager.GeneratingButtonAnimation("disable");
            }
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, selectedUris.size());
        });



    }

    @Override
    public int getItemCount() {
        return selectedUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
          ImageView imageView;
          LottieAnimationView deleteBtn,downloadBtn,loadingImagesLottie;
          EditText SizeInKb;
          TextView actualImgSize,actualWidth,actualHeight,RecommendTxt,newSize;
          View disabledTxtLine;
          boolean isProgrammitically = false;
          LinearLayout imgData;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_id_recycler_before_enhance);
            deleteBtn = itemView.findViewById(R.id.remove_item_from_recy_enhance_img_itm);
            downloadBtn = itemView.findViewById(R.id.download_item_from_recy_enhance_img_itm);

            SizeInKb = itemView.findViewById(R.id.size_in_kb_input_box);

            actualImgSize = itemView.findViewById(R.id.r_img_size_actual_size_of_img);
            actualWidth = itemView.findViewById(R.id.orginal_width_r_img_size);
            actualHeight = itemView.findViewById(R.id.orginal_height_r_img_size);
            RecommendTxt = itemView.findViewById(R.id.recomended_txt_itm_v_i_reducer);
            loadingImagesLottie = itemView.findViewById(R.id.lottie_progressing_loading_img_rec_red_img);
            newSize = itemView.findViewById(R.id.r_img_size_actual_new_size_of_img);
            disabledTxtLine = itemView.findViewById(R.id.view_disabled_txt_rec_redu_img);
            imgData = itemView.findViewById(R.id.img_data_r_red_img);

        }
    }

    public void setServerUrl(ArrayList<Uri>serverUrl){
        this.serverUrl = serverUrl;
        notifyDataSetChanged();
    }

    public void setUploadingStates(boolean uploading) {
        isUploading = uploading;
        notifyDataSetChanged();
    }

    public interface OnImageListEmptyListener {
        void onImageListEmpty();
    }

    public ArrayList<Uri> getFinalImageUri(){
        return selectedUris;
    }

    public ArrayList<String> getAllSizes() {
        for (int i = 0; i < Sizes.size(); i++) {
            if (Sizes.get(i).trim().matches("^0+$")) { // Matches "0", "00", "000", etc.
                customToastManager.showDownloadSuccessToast(R.drawable.cross_icon, "Enter A Valid Size", 2);

                // Scroll to the item with "0" value
                recyclerView.smoothScrollToPosition(i);

                // Store the index in a final variable
                final int position = i;

                // Try to get the ViewHolder for the current position
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    // If ViewHolder is visible, update UI directly
                    TextView recommendTxt = viewHolder.itemView.findViewById(R.id.recomended_txt_itm_v_i_reducer);
                    recommendTxt.setText("Enter Valid Size");
                    recommendTxt.setTextColor(ContextCompat.getColor(context, R.color.hard_red));
                } else {
                    // If not visible, delay update using a Handler
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(position);
                        if (vh != null) {
                            TextView recommendTxt = vh.itemView.findViewById(R.id.recomended_txt_itm_v_i_reducer);
                            recommendTxt.setText("Enter Valid Size");
                            recommendTxt.setTextColor(ContextCompat.getColor(context, R.color.hard_red));
                        }
                    }, 300); // Small delay to allow RecyclerView to scroll
                }

                return null; // Stop further execution
            }
        }
        return Sizes; // Return the list if no invalid size is found
    }















    private int getRecommendedSize(Uri uri) {
        try {
            // Get file size in KB correctly
            long fileSizeKb = getFileSizeFromUri(uri) / 1024; // Convert bytes to KB

            // Generate a random percentage between 5.35% - 5.40%
            double percentage = getRandomPercentage(5.35, 5.40);

            // Calculate recommended size
            int recommendedSize = (int) Math.round(fileSizeKb * (percentage / 100));

            return Math.max(recommendedSize, 50); // Ensure minimum 50KB
        } catch (Exception e) {
            e.printStackTrace();
            return 100; // Default fallback if error occurs
        }
    }

    // ✅ Correct method to get file size from Uri
    private long getFileSizeFromUri(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            long size = cursor.getLong(sizeIndex);
            cursor.close();
            return size;
        }
        return 0;
    }
    private long getFileSizeFromUrl(String url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .head() // Send HEAD request to get metadata only (faster)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Long.parseLong(response.header("Content-Length", "0")) / 1024; // Convert to KB
            }
        } catch (Exception e) {
            Log.e("FileSize", "Error getting file size from URL: " + url, e);
        }
        return 0; // Return 0 if error
    }

    // Helper method to return a random percentage between 5.35% and 5.40%
    private double getRandomPercentage(double min, double max) {
        return min + (new Random().nextDouble() * (max - min));
    }




    /**
     * Returns the actual image size in KB or MB.
     * If size < 1MB, returns in KB (e.g., "500 KB").
     * If size >= 1MB, returns in MB (e.g., "2.5 MB").
     */
    private String getImageSizeFormatted(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return "0 KB";

            int fileSizeBytes = inputStream.available(); // Get file size in bytes
            inputStream.close();

            double fileSizeKB = fileSizeBytes / 1024.0; // Convert bytes to KB
            double fileSizeMB = fileSizeKB / 1024.0; // Convert KB to MB

            // Return formatted size
            return fileSizeKB < 1024
                    ? String.format("%.2f KB", fileSizeKB)
                    : String.format("%.2f MB", fileSizeMB);

        } catch (Exception e) {
            e.printStackTrace();
            return "0 KB";
        }
    }


    /**
     * Returns the image width in pixels.
     */
    private int getImageWidth(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return 0;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Decode only size
            BitmapFactory.decodeStream(inputStream, null, options);

            inputStream.close();
            return options.outWidth;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Returns the image height in pixels.
     */
    private int getImageHeight(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return 0;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Decode only size
            BitmapFactory.decodeStream(inputStream, null, options);

            inputStream.close();
            return options.outHeight;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
    /**
     * Scan The File And Notify To Media
     *
     * @param file The file retrieved from Glide's cache
     */
    private void scanFileForGallery(File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> Log.d("MediaScanner", "Scanned " + path + " -> URI: " + uri));
    }

    /**
     * Download All Enhance Images To Device
     */
    public void DownloadAllImages() {
        buttonAnimationManager.DownloadAllImagesAnimation("downloading");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Code to execute after 5 seconds
                // For example, you can show a Toast or update the UI
                customToastManager.showDownloadSuccessToast(R.drawable.download_success,"All Images Downloaded Successful",3);
            }
        }, 5000); // 5000 milliseconds = 5 seconds

        for (Uri imageUrl : serverUrl) {
            // Download from the cached file

            // Download from Glide
            downloadLoadedImage(imageUrl);
        }
    }


}
