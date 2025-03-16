package com.mediaghor.rainbowtools.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.OthersClasses.CustomToastManager;
import com.mediaghor.rainbowtools.R;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class ImageSizeReducerAdapter extends RecyclerView.Adapter<ImageSizeReducerAdapter.ViewHolder>{

    private final ArrayList<Uri> selectedUris;
    private ArrayList<String> Sizes = new ArrayList<>();

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
        Uri uri = selectedUris.get(position);
        // Load the image using Glide for efficiency
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.placeholder_image) // Optional: Set a placeholder image
                .into(holder.imageView);
        // Get recommended image size and set it in EditText
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



        // Set click listener for the delete button
//        if(isUploading){
//            holder.deleteButton.setVisibility(View.GONE);
//        }
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
          LottieAnimationView deleteBtn;
          EditText SizeInKb;
          TextView actualImgSize,actualWidth,actualHeight,RecommendTxt;
          boolean isProgrammitically = false;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_id_recycler_before_enhance);
            deleteBtn = itemView.findViewById(R.id.remove_item_from_recy_enhance_img_itm);
            SizeInKb = itemView.findViewById(R.id.size_in_kb_input_box);

            actualImgSize = itemView.findViewById(R.id.r_img_size_actual_size_of_img);
            actualWidth = itemView.findViewById(R.id.orginal_width_r_img_size);
            actualHeight = itemView.findViewById(R.id.orginal_height_r_img_size);
            RecommendTxt = itemView.findViewById(R.id.recomended_txt_itm_v_i_reducer);

        }
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


}
